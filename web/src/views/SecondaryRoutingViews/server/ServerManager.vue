<script setup>
import {onMounted, onUnmounted, ref,} from "vue";
import axios from "axios";
import {message} from "ant-design-vue";
// model框
// 属性
const title = ref();
const open = ref(false);
const formState = ref({
  id: '',
  poolId: '',
  serverName: '',
  serverMacAddr: '',
  serverIpAddr: '',
  serverPort: '',
  create: false
});
const isSysIssueIp = ref(false)
const isRandom = ref(false)
// 方法
const switchChange = () => {
  if (isSysIssueIp.value) {
    issueIp();
  }
}
const handleOk = () => {
  isSysIssueIp.value = false
  isRandom.value = false
  open.value = false
  axios.post('/server/modify', formState.value).then(res => {
    if (res.data.status) {
      message.success('添加成功！');
      // 将formState所有值变成空
      Object.keys(formState.value).forEach(key => {
        formState.value[key] = ''
      })
      getServerData()
    } else {
      message.error(res.data.message);
    }
  })


}
const insert = () => {
  title.value = "新增服务器";
  formState.value.create = true;
  open.value = true;
}
// Table
// 属性
const ipPoolSelect = ref([])
const serverData = ref([])
const columns = [
  {
    title: '服务器名称',
    dataIndex: 'serverName',
    key: 'serverName',
  },
  {
    title: '地址池',
    dataIndex: 'poolName',
  },
  {
    title: '服务器IP地址',
    dataIndex: 'serverIpAddr',
    key: 'serverIpAddr',
  },
  {
    title: '服务器端口',
    dataIndex: 'serverPort',
    key: 'serverPort',
  },
  {
    title: '服务器MAC地址',
    dataIndex: 'serverMacAddr',
    key: 'serverMacAddr',
  },
  {
    title: '服务器状态',
    dataIndex: 'isAlive',
    key: 'isAlive',
  },
  {
    title: 'Agent状态',
    dataIndex: 'isOnline',
    key: 'isOnline',
  },
  {
    title: '配置文件下发状态',
    dataIndex: 'isIssueConfig',
    key: 'isIssueConfig',
  },
  {
    title: '是否上报报告',
    dataIndex: 'isReceivedReport',
    key: 'isReceivedReport',
  },
  {
    title: '操作',
    dataIndex: 'operation',
    key: 'operation'
  }
]
// 方法
const editInfo = (row) => {
  title.value = "修改" + row.serverName + "服务器信息";
  formState.value.create = false;
  formState.value.id = row.id;
  formState.value.poolId = row.poolId;
  formState.value.serverName = row.serverName;
  formState.value.serverIpAddr = row.serverIpAddr;
  formState.value.serverMacAddr = row.serverMacAddr;
  formState.value.serverPort = row.port;
  open.value = true;
}
const onDelete = (serverName) => {

  axios.delete(`/server/delete`, {params: {serverName: serverName}}).then(res => {
    if (res.data.status) {
      message.success("删除成功")
      getServerData()
    }else {
      message.error(res.data.message)
      getServerData()
    }
  })
}
// 服务器详情
const SystemInfo = ref({
  agentId:'',
  cpuName:'',
  cpuCoreNum:'',
  cupAvgLoad:'',
  totalMemory:'',
  freeMemory:'',
  totalDisk:'',
  freeDisk:'',
})
const infoModelSwitch = ref(false)
const infoHandleOk = () => {
  infoModelSwitch.value = false
}
const getSystemInfo = async (serviceId) => {
  await axios.get('/master/get-report', {params: {serverId: serviceId}}).then(res => {
    const json = res.data;
    if (json.status) {
      SystemInfo.value = json.data
      infoModelSwitch.value = true
    }
  })
}
// Common
// 属性
// 方法
const getIpPoolSelect = async () => {
  await axios.get('/pool/selectValue').then(res => {
        const json = res.data;
        if (json.status) {
          ipPoolSelect.value = json.data
        }
      })
}
const issueIp = () => {
  if(formState.value.poolId === ''){
    message.error('请选择IP池！');
  }else {
    axios.get("/pool/generate", {params: {
      poolId: formState.value.poolId, isRandom: isRandom.value
    }}).then(res => {
      if (res.data.status) {
        formState.value.serverIpAddr = res.data.data
      }
    })
  }

}
const getServerData = async () => {
  await axios.get('/server/info').then(res => {
    const json = res.data;
    if (json.status) {
      // 清空列表d
      serverData.value.length = 0;
      const ipPoolSelectMap = new Map();
      ipPoolSelect.value.forEach(item => {
        ipPoolSelectMap.set(item.value, item.label);
      });
      json.data.forEach(item => {
        item["poolName"] = ipPoolSelectMap.get(item.poolId)
        serverData.value.push(item);
      })
    }
    console.log(serverData.value)
  })
}
// 定时刷新界面
let interval

onMounted(()=>{
  getIpPoolSelect().then(()=>{
    getServerData()
  })

  interval = setInterval(()=>{
    getServerData()
  },300000)
})
onUnmounted(() => {
  clearInterval(interval) // 在组件卸载时清除定时器
})
</script>

<template>
  <a-button type="primary" @click="insert">添加服务器</a-button>
  <a-modal v-model:open="open" :title="title" @ok="handleOk">
    <a-form
        :model="formState"
        name="basic"
        autocomplete="off"
    >
      <a-form-item
          label="IP地址池"
          name="poolName"
      >
        <a-select
            ref="select"
            v-model:value="formState.poolId"
            style="width: 120px"
            :options="ipPoolSelect"
        ></a-select>
      </a-form-item>
      <a-form-item
          label="服务器名称"
          name="serverName"
      >
        <a-input v-model:value="formState.serverName" />
      </a-form-item>
      <a-form-item
          label="服务器MAC地址"
          name="serverMacAddr"
      >
        <a-input v-model:value="formState.serverMacAddr" />
      </a-form-item>
      <a-form-item
          label="系统生成IP地址"
          name="sysIssueIp"
      >
        <a-switch v-model:checked="isSysIssueIp" @change="switchChange" checked-children="开" un-checked-children="关" />
      </a-form-item>
      <a-form-item
          label="随机分配IP"
          name="random"
      >
        <a-switch :disabled="!isSysIssueIp" v-model:checked="isRandom" checked-children="是" un-checked-children="否" />
      </a-form-item>
      <a-form-item
          label="serverIpAddr"
          name="serverIpAddr"
      >
        <a-flex wrap="wrap" gap="small">

          <a-input :disabled="isSysIssueIp" v-model:value="formState.serverIpAddr" />
          <a-button type="link" :disabled="!isSysIssueIp" @click="issueIp">重新生成</a-button>
        </a-flex>

      </a-form-item>
      <a-form-item
          label="serverPort"
          name="serverPort"
      >
        <a-input v-model:value="formState.serverPort" />
      </a-form-item>

    </a-form>
  </a-modal>
  <!--服务器详情-->
  <a-modal v-model:open="infoModelSwitch" title="服务器详情" @ok="infoHandleOk">
    <a-descriptions class="SystemInfo"  bordered>
      <a-descriptions-item label="CPU名称">{{ SystemInfo.cpuName }}</a-descriptions-item>
      <a-descriptions-item label="核心数">{{ SystemInfo.cpuCoreNum }}</a-descriptions-item>
      <a-descriptions-item label="负载">{{ SystemInfo.cupAvgLoad }}</a-descriptions-item>
      <a-descriptions-item label="总内存">{{ SystemInfo.totalMemory }}</a-descriptions-item>
      <a-descriptions-item label="空闲内存">{{ SystemInfo.freeMemory }}</a-descriptions-item>
    </a-descriptions>
  </a-modal>
  <a-table bordered :data-source="serverData" :columns="columns">
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'isAlive'">
        <template v-if="record.isAlive">
          <a-tag :bordered="false" color="success" @click="getSystemInfo(record.id)">在线</a-tag>
        </template>
        <template v-else>
          <a-tag :bordered="false" color="error">离线</a-tag>
        </template>
      </template>

      <template v-if="column.dataIndex === 'isOnline'">
        <template v-if="record.isOnline">
          <a-tag :bordered="false" color="success">在线</a-tag>
        </template>
        <template v-else>
          <a-tag :bordered="false" color="error">离线</a-tag>
        </template>
      </template>

      <template v-if="column.dataIndex === 'isIssueConfig'">
        <template v-if="record.isIssueConfig">
          <a-tag :bordered="false" color="success">成功下发配置</a-tag>
        </template>
        <template v-else>
          <a-tag :bordered="false" color="error">未下发配置</a-tag>
        </template>
      </template>

      <template v-if="column.dataIndex === 'isReceivedReport'">
        <template v-if="record.isReceivedReport">
          <a-tag :bordered="false" color="success">已接收报告</a-tag>
        </template>
        <template v-else>
          <a-tag :bordered="false" color="error">未接收报告</a-tag>
        </template>
      </template>

      <template v-else-if="column.dataIndex === 'operation'">
        <a-flex gap="middle">
          <a-button type="primary" @click="editInfo(record)" > Edit</a-button>
          <a-popconfirm
              v-if="serverData.length"
              title="Sure to delete?"
              @confirm="onDelete(record.serverName)"
          >
            <a-button danger>Delete</a-button>
          </a-popconfirm>
        </a-flex>
      </template>
    </template>

  </a-table>
</template>

<style scoped>

</style>