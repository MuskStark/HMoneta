<script setup>
import {onMounted, onUnmounted, ref,} from "vue";
import axios from "axios";
import {message} from "ant-design-vue";
import {encrypt} from "@/utils/common.js";

// DDNS供应商维护
const ddnsProvider = ref({
  providerName: '',
  accessKeyId: '',
  accessKeySecret: '',
})
// DDNS 供应商系统查询
const publicKey = ref();
const getPublicKey = () => {
  axios.get('/hm/ddns/publicKey').then(res => {
    if (res.data.code === 200) {
      publicKey.value = res.data.data
    } else {
      message.error(res.data.message)
    }
  })
}
const addDdnsProvider = async (publicKey) => {
  ddnsProvider.value.accessKeySecret = encrypt(ddnsProvider.value.accessKeySecret, publicKey)
  await axios.post('/hm/ddns/addDdnsProvider', ddnsProvider.value).then(res => {
    if (res.data.code === 200) {
      message.success(res.data.message)
    } else {
      message.error(res.data.message)
    }
  })
}
</script>

<template>
  <a-button @click="open = true">测试按钮</a-button>
  <a-modal v-model:open="open" :title="title" @ok="handleOk">
    <a-form
        :model="ddnsProvider"
        name="basic"
        autocomplete="off"
    >
      <a-form-item
          label="地址池名称"
          name="providerName"
      >
        <a-input v-model:value="ddnsProvider.providerName"/>
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
</template>

<style scoped>

</style>
