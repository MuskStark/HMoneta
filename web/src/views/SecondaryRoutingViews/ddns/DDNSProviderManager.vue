<script setup>
import {computed, onMounted, onUnmounted, ref,} from "vue";
import axios from "axios";
import {message} from "ant-design-vue";
import {encrypt, responseIsSuccess} from "@/utils/common.js";

const providerList = ref([])
const providerTable = ref([])
const queryDDNSProvider = async () => {
  await axios.get('ddns/provider/query').then((response) => {
    if (responseIsSuccess(response)) {
      dnsCardList.value = response.data.data;
    } else {
      message.error(response.data.message)
    }
  });
}
//查询DNS信息
const DnsRecordInfo = ref({})
const queryDNSRecordInfo = async (providerName="all") =>{
  const response = await axios.get('ddns/record', {
    params: {
      providerName: providerName
    }
  })
  if(responseIsSuccess(response)){
    if(providerName === 'all'){
      return response.data.data

    }else {
      return {providerName: response.data.data}
    }
  }else {
    message.error(response.data.message)

    }
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
      queryDDNSProvider()
    } else {
      message.error(response.data.message)
    }
  })
}

// card渲染相关代码
const dnsCardList = ref([])
const handleCardClick = (card) => {
  let resultMap = queryDNSRecordInfo(card.providerName);
}
const openDDNSInsert = ref(false)
const DDNSInsertModalData = ref({
  providerName: '',
  domain: '',
  subdomain: '',
})
const onInsert = (card) => {
  openDDNSInsert.value = true
  DDNSInsertModalData.value.providerName = card.providerName
}
const submitInsert = () => {
  console.log(DDNSInsertModalData.value)
}
// card元素中table相关属性及函数
const cardTableColumns = [
  {
    title: '主域名',
    dataIndex: 'domain',
    key:'domain'
  },
  {
    title: '二级域名',
    dataIndex: 'subDomain',
  },
  {
    title: '解析地址',
    dataIndex: 'ip',
  },
  {
    title: '解析状态',
    dataIndex: 'status',
  },


]

const queryCardDataSource = async (card) =>{
  await axios.get("ddns/record", {params: {providerName: card.providerName}}).then((response) => {
    const json = response.data
    if(json.code === 200){
      card.dataSource = json.data
    }else {
      message.warn(json.message)
    }
  })
}
onMounted(async () => {
  await queryDDNSProvider()
  getProviderSelectList()
  getPublicKey()
  for (const card of dnsCardList.value) {
    await queryCardDataSource(card)
  }

})

</script>

<template>
  <a-button class="provider-btn" @click="modifyDDNSProvider">新增供应商</a-button>
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
  <a-modal v-model:open="openDDNSInsert" :title="DDNSInsertModalData.providerName" @ok="submitInsert">
    <a-form
        :model="DDNSInsertModalData"
        name="basic"
        autocomplete="off"
    >
      <a-form-item label="域名">
        <a-input v-model:value="DDNSInsertModalData.domain" placeholder="请输入域名"></a-input>
      </a-form-item>
      <a-form-item label="子域名">
        <a-input v-model:value="DDNSInsertModalData.subdomain" placeholder="请输入子域名"></a-input>
      </a-form-item>
    </a-form>
  </a-modal>
  <a-flex>
    <a-card v-for="(card, index) in dnsCardList" :key="index" :title="card.label" @click="handleCardClick(card)">
      <template #extra><a-button type="text" @click="onInsert(card)">新增DNS记录</a-button></template>
        <a-table
            bordered
            :dataSource="card.dataSource"
            :columns="cardTableColumns"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'status'">
              <template v-if="record.status === 1">
                <a-tag :bordered="false" color="success">同步成功</a-tag>
              </template>
              <template v-else>
                <a-tag :bordered="false" color="error">同步失败</a-tag>
              </template>
            </template>
          </template>
        </a-table>
    </a-card>
  </a-flex>
</template>

<style scoped>
.provider-btn{
  margin-right: 10px;
  margin-top: 10px;
  margin-bottom: 10px;
}
</style>
