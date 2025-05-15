<script setup>

// 查询全部DNS信息
import axios from "axios";
import {message} from "ant-design-vue";
import {getDownloadFileFromBase64, responseIsSuccess} from "@/utils/common.js";
import {onMounted, reactive, ref} from "vue";

const queryCertApplyStatus = (taskId) => {
  axios.get("/acme/apply/status?taskId=" + taskId).then((resp) => {
    if (responseIsSuccess(resp)) {
      formSource.value = resp.data.data;
    }
  })
}
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
        queryCertApplyStatus(json)
      } else {
        message.error(resp.data.message)
      }
    })
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
const formSource = ref()
const columns = [
  {
    title: '域名',
    dataIndex: 'domain',
    key: 'domain',
    align: 'center'
  },
  {
    title: '状态',
    dataIndex: 'statusInfo',
    key: 'statusInfo',
    align: 'center'
  },
  {
    title: '操作',
    dataIndex: 'operation',
    key: 'operation',
    align: 'center'
  },
]
// Table组件相关方法

// 下载相关功能
const downLoadCertFile = (domain) => {
  axios.get("/acme/getCert?domain=" + domain).then((resp) => {
    const json = resp.data
    if (json.status) {
      if (json.data) {
        message.success("开始下载[" + domain + "]证书文件")
        getDownloadFileFromBase64(json.data, domain + '.zip', 'application/zip')
      }
    } else {
      message.error(json.message)
    }
  })
}


onMounted(() => {
  queryDDNSProvider();
  queryCertApplyStatus("0");
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
        <a-input v-model:value="formObject.domain"/>
      </a-form-item>
      <a-form-item label="选择域名供应商">
        <a-select
            v-model:value="formObject.dnsProvider"
            style="width: 120px"
            :options="dnsList"></a-select>
      </a-form-item>
    </a-form>
  </a-modal>
  <a-flex gap="middle" vertical style="margin-top: 12px">
    <a-button style="width: 10%" @click="openModal">申请证书</a-button>
    <!-- 证书申请展示Table -->
    <a-table :data-source="formSource" :columns="columns">
      <template #bodyCell="{column, record}">
        <template v-if="column.dataIndex === 'statusInfo'">
          <a-flex justify="center">
            <a-tag color="green" v-if="record.statusInfo === '1'">申请成功</a-tag>
            <a-tag color="red" v-else-if="record.statusInfo === '-1'">申请失败</a-tag>
          </a-flex>
        </template>
        <template v-if="column.dataIndex === 'operation'">
          <a-flex gap="middle" justify="center">
            <a-button>重新申请</a-button>
            <a-button>查看日志</a-button>
            <a-button @click="downLoadCertFile(record.domain)">下载证书</a-button>
          </a-flex>
        </template>
      </template>
    </a-table>
  </a-flex>
</template>

<style scoped>

</style>