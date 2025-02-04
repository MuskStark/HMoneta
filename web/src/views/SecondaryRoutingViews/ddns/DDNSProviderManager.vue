<script setup>
import {onMounted, reactive, ref,} from "vue";
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
const queryDNSRecordInfo = async (providerName = "all") => {
  const response = await axios.get('ddns/record', {
    params: {
      providerName: providerName
    }
  })
  if (responseIsSuccess(response)) {
    if (providerName === 'all') {
      return response.data.data

    } else {
      return {providerName: response.data.data}
    }
  } else {
    message.error(response.data.message)

  }
}

const getProviderSelectList = () => {
  axios.get('ddns/provider/selector').then((response) => {
    if (responseIsSuccess(response)) {
      providerList.value = response.data.data
    } else {
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
const open = ref(false)
const title = ref('DDNS供应商维护')
const modifyLoading = ref(false)

const
    getPublicKey = () => {
      axios.get('ddns/publicKey').then(response => {
        if (responseIsSuccess(response)) {
          publicKey.value = response.data.data
        } else {
          message.error(response.data.message)
        }
      })
    }
const handleOk = () => {
  addDdnsProvider(publicKey.value)
  open.value = false
}
const closeProviderDraw = () => {
  open.value = false
}
const modifyDDNSProvider = () => {
  open.value = true
}
const addDdnsProvider = async (publicKey) => {
  ddnsProvider.value.accessKeySecret = encrypt(ddnsProvider.value.accessKeySecret, publicKey)
  modifyLoading.value = true
  await axios.post('ddns/provider/add', ddnsProvider.value).then(response => {
    if (responseIsSuccess(response)) {
      message.success(response.data.message)
      modifyLoading.value = false
      ddnsProvider.value.providerName = null
      ddnsProvider.value.accessKeyId = null
      ddnsProvider.value.accessKeySecret = null
      closeProviderDraw()
      queryDDNSProvider()
    } else {
      message.error(response.data.message)
      ddnsProvider.value.providerName = null
      ddnsProvider.value.accessKeyId = null
      ddnsProvider.value.accessKeySecret = null
      closeProviderDraw()
      modifyLoading.value = false
    }
  })
}

// card渲染相关代码
const dnsCardList = ref([])
// const handleCardClick = (card) => {
//   let resultMap = queryDNSRecordInfo(card.providerName);
// }
const openDDNSInsert = ref(false)
const DDNSInsertModalData = ref({
  providerName: '',
  domain: '',
  subDomain: '',
})
const confirmLoading = ref(false)
const onInsert = (card) => {
  openDDNSInsert.value = true
  DDNSInsertModalData.value.providerName = card.providerName
}
const submitInsert = () => {
  confirmLoading.value = true
  axios.post("/ddns/record/modify", DDNSInsertModalData.value).then((resp) => {
    if (responseIsSuccess(resp)) {
      message.success(resp.data.message)
      confirmLoading.value = false
      openDDNSInsert.value = false
      for (const card of dnsCardList.value) {
        queryCardDataSource(card)
      }
    } else {
      message.error(resp.data.message)
      confirmLoading.value = false
      openDDNSInsert.value = false
      for (const card of dnsCardList.value) {
        queryCardDataSource(card)
      }
    }

  })
}
// card元素中table相关属性及函数
const cardTableColumns = [
  {
    title: '主域名',
    dataIndex: 'domain',
    key: 'domain',
    algin: 'center'
  },
  {
    title: '二级域名',
    dataIndex: 'subDomain',
    algin: 'center'
  },
  {
    title: '解析地址',
    dataIndex: 'ip',
    algin: 'center'
  },
  {
    title: '解析状态',
    dataIndex: 'status',
    algin: 'center'
  },
  {
    title: '操作',
    dataIndex: 'operation',
    algin: 'center'
  },


]

const queryCardDataSource = async (card) => {
  await axios.get("ddns/record", {params: {providerName: card.providerName}}).then((response) => {
    const json = response.data
    if (json.code === 200) {
      card.dataSource = json.data
    } else {
      message.warn(json.message)
    }
  })
}
// 修改DDNS记录
const reqData = reactive({
  subDomain: null,
  domain: null,
  providerName: null,
  recorderId: null
})
const recorderModifySwitch = ref(false)
const closeRecorderModifyDraw = () => {
  recorderModifySwitch.value = false
}
const changeRecordInfo = (record) => {
  reqData.subDomain = record.subDomain
  reqData.domain = record.domain
  reqData.providerName = record.providerName
  reqData.recorderId = record.recorderId
  recorderModifySwitch.value = true
}
const submitRecorderModify = () => {
  axios.post("ddns/record/modify", reqData).then((resp) => {
    const json = resp.data;
    if (json.code === 200) {
      message.success(json.message)
      for (const card of dnsCardList.value) {
        queryCardDataSource(card)
      }
    } else {
      message.error(json.message)
      for (const card of dnsCardList.value) {
        queryCardDataSource(card)
      }
    }
  })
}
// 删除DDNS记录
const submitRecorderDelete = (record) => {
  axios.get("ddns/record/delete", {
    params: {
      recorderId: record.recorderId
    }
  }).then((resp) => {
    const json = resp.data;
    if (json.code === 200) {
      message.success(json.message)
      for (const card of dnsCardList.value) {
        queryCardDataSource(card)
      }
    } else {
      message.error(json.message)
      for (const card of dnsCardList.value) {
        queryCardDataSource(card)
      }
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
  <!--  新增供应商组件 -->
  <a-drawer
      :title="title"
      :open="open"
      @close="closeProviderDraw"
  >
    <template #extra>
      <a-flex gap="middle">
        <a-button type="primary" @click="handleOk" :loading="modifyLoading">提交</a-button>
        <a-button type="ghost" @click="closeProviderDraw">取消</a-button>
      </a-flex>
    </template>
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
  </a-drawer>
  <!--  新增DDNS记录组件 -->
  <a-modal
      v-model:open="openDDNSInsert"
      :title="DDNSInsertModalData.providerName"
      ok-text="新增"
      cancel-text="取消"
      @ok="submitInsert"
      :confirm-loading="confirmLoading"
  >
    <a-form
        :model="DDNSInsertModalData"
        name="basic"
        autocomplete="off"
    >
      <a-form-item label="域名">
        <a-input v-model:value="DDNSInsertModalData.domain" placeholder="请输入域名"></a-input>
      </a-form-item>
      <a-form-item label="子域名">
        <a-input v-model:value="DDNSInsertModalData.subDomain" placeholder="请输入子域名"></a-input>
      </a-form-item>
    </a-form>
  </a-modal>
  <a-flex>
    <a-card v-for="(card, index) in dnsCardList" :key="index" :title="card.label">
      <template #extra>
        <a-button type="text" @click="onInsert(card)">新增DNS记录</a-button>
      </template>
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
          <template v-if="column.dataIndex === 'operation'">
            <a-flex gap="small">
              <a-button type="text" @click="changeRecordInfo(record)">修改</a-button>
              <a-popconfirm
                  title="是否移除该DNS解析记录"
                  ok-text="删除"
                  cancel-text="取消"
                  @confirm="submitRecorderDelete(record)"
              >
                <a href="#">删除</a>
              </a-popconfirm>
            </a-flex>

          </template>
        </template>
      </a-table>
    </a-card>
  </a-flex>
  <!--  修改DDNS记录组件-->
  <a-drawer
      title="DNS记录修改"
      :open="recorderModifySwitch"
      @close="closeRecorderModifyDraw"
  >
    <template #extra>
      <a-button type="primary" @click="submitRecorderModify">提交</a-button>
    </template>
    <a-form>
      <a-form-item label="供应商名称">
        <a-input disabled v-model:value="reqData.providerName"/>
      </a-form-item>
      <a-form-item label="主域名">
        <a-input disabled v-model:value="reqData.domain"/>
      </a-form-item>
      <a-form-item label="二级域名">
        <a-input v-model:value="reqData.subDomain"/>
      </a-form-item>
    </a-form>

  </a-drawer>
</template>

<style scoped>
.provider-btn {
  margin-right: 10px;
  margin-top: 10px;
  margin-bottom: 10px;
}

</style>
