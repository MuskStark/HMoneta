<script setup>

// 查询全部DNS信息
import axios from "axios";
import {message} from "ant-design-vue";
import {responseIsSuccess} from "@/utils/common.js";
import {onMounted, reactive, ref} from "vue";

// Dns供应商选择器
const dnsList = ref([])
const queryDDNSProvider = async () => {
  await axios.get('ddns/provider/query').then((response) => {
    if (responseIsSuccess(response)) {
      dnsList.value = []
      response.data.data.forEach(item => {
        dnsList.value.push({value: item.providerName, label: item.providerName})
      })
    } else {
      message.error(response.data.message)
    }
  });
}
// Modal框组件
const open = ref(false)
const openModal = () => {
  open.value = true

}
const handelOk = () => {
  if (formObject) {
    axios.post("/acme/apply", formObject).then((resp) => {
      if (responseIsSuccess(resp)) {
        const json = resp.data.data;
        message.info("已提交证书申请，详情请查看日志")
      } else {
        message.error(resp.data.message)
      }
    })
    // axios.get("/acme/getCert?domain=test7.summer.fan").then((resp) => {
    //   const json = resp.data
    //   if (json.status) {
    //     getDownloadFileFromBase64(json.data, 'test7.summer.fan.zip', 'application/zip')
    //   } else {
    //     message.error(json.message)
    //   }
    // })
  } else {
    message.error("表单为空请重新填写")
  }

}


// 请求Form对象
const formObject = reactive(
    {
      domain: null,
      dnsProvider: null
    }
)


onMounted(() => {
  queryDDNSProvider();
})

</script>

<template>
  <!--  新增证书申请Modal组件-->
  <a-modal
      v-model:open="open"
      @ok="handelOk"
  >
    <a-form>
      <a-form-item label="请输入域名">
        <a-input v-model:value(v-model)="formObject.domain"/>
      </a-form-item>
      <a-form-item label="选择域名供应商">
        <a-select
            v-model:value="formObject.dnsProvider"
            style="width: 120px"
            :options="dnsList"></a-select>
      </a-form-item>

    </a-form>
  </a-modal>
  <a-button @click="openModal">申请证书</a-button>
</template>

<style scoped>

</style>