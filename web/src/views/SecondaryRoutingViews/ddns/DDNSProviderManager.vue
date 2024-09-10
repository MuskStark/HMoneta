<script setup>
import {onMounted, onUnmounted, ref,} from "vue";
import axios from "axios";
import {message} from "ant-design-vue";
import {encrypt, responseIsSuccess} from "@/utils/common.js";

const providerList = ref([])
const providerTable = ref([])
const queryDDNSProvider = () => {
  axios.get('ddns/provider/query').then((response) => {
    if (responseIsSuccess(response)) {
      providerTable.value = response.data.data;
    } else {
      message.success(response.data.message)
    }
  });
}
const getProviderSelectList = () => {
  axios.get('ddns/provider/selector').then((response) => {
    if(responseIsSuccess(response)){
      providerList.value = response.data.data
    }else {
      message.error(response.data.message)
    }
  });
}
// DDNS供应商维护
const ddnsProvider = ref({
  providerName: '',
  accessKeyId: '',
  accessKeySecret: '',
})
// DDNS 供应商系统查询
const publicKey = ref();
const getPublicKey = () => {
  axios.get('ddns/publicKey').then(response => {
    if (responseIsSuccess(response)) {
      publicKey.value = response.data.data
    } else {
      message.error(response.data.message)
    }
  })
}
const open = ref(false)
const handleOk = () => {
  addDdnsProvider(publicKey.value)
  open.value = false
}
const title = ref('DDNS供应商维护')
const modifyDDNSProvider = () => {
  open.value = true
}
const addDdnsProvider = async (publicKey) => {
  ddnsProvider.value.accessKeySecret = encrypt(ddnsProvider.value.accessKeySecret, publicKey)
  await axios.post('ddns/provider/add', ddnsProvider.value).then(response => {
    if (responseIsSuccess(response)) {
      message.success(response.data.message)
    } else {
      message.error(response.data.message)
    }
  })
}
// Table
const columns = [
  {
    title: 'DNS供应商名称',
    dataIndex: 'providerName',
    key: 'providerName',
  },
  {
    title: '授权ID',
    dataIndex: 'accessKeyId',
    key: 'accessKeyId',
  }]
onMounted(() => {
  queryDDNSProvider()
  getProviderSelectList()
  getPublicKey()
})
</script>

<template>
  <a-button class="provider-btn" @click="modifyDDNSProvider">新增/修改DDNS供应商</a-button>
  <a-modal v-model:open="open" :title="title" @ok="handleOk">
    <a-form
        :model="ddnsProvider"
        name="basic"
        autocomplete="off"
    >
      <a-form-item
          label="DNS供应商名称"
          name="providerName"
      >
        <a-select
            ref="select"
            v-model:value="ddnsProvider.providerName"
            style="width: 120px"
            :options="providerList"
        ></a-select>
      </a-form-item>
      <a-form-item
          label="授权ID"
          name="accessKeyId"
      >
        <a-input v-model:value="ddnsProvider.accessKeyId"/>
      </a-form-item>
      <a-form-item
          label="授权密钥"
          name="accessKeySecret"
      >
        <a-input v-model:value="ddnsProvider.accessKeySecret"/>
      </a-form-item>
    </a-form>
  </a-modal>
  <a-table :columns="columns" :dataSource="providerTable"/>
</template>

<style scoped>
.provider-btn{
  margin-right: 10px;
  margin-top: 10px;
  margin-bottom: 10px;
}
</style>
