<script setup>

import axios from "axios";
import {message} from "ant-design-vue";
import {onMounted, onUnmounted, ref} from "vue";
// InsetButton
const insert = () => {
  title.value = '添加地址池'
  open.value = true;
  ipPoolInfo.value.create = true;
}
// model
// 属性
const title = ref('添加地址池')
const open = ref(false)
const ipPoolInfo = ref({
  poolId: '',
  poolName: '',
  networkAddress: '',
  mask: '',
  startAddr: '',
  endAddr: '',
  create: '',
})
const selectValue = ref([
  {
    value: '/24',
    label: '/24'
  },
])
// 方法
const handleOk = () => {
  axios.post("/pool/modify", ipPoolInfo.value).then((res) => {
    if (res.data.status) {
      // success
      Object.keys(ipPoolInfo.value).forEach(key => {
        ipPoolInfo.value[key] = ''
      })
      open.value = false;
      queryInfo();
    } else {
      message.error(res.data.message)
    }
  })
}
// Table
// 属性
const ipPoolTable = ref([])
const columns = [
  {
    title: 'Ip池名称',
    dataIndex: 'poolName',
    key: 'poolName',
  },
  {
    title: '网络地址',
    dataIndex: 'networkAddress',
    key: 'networkAddress',
  },
  {
    title: '掩码',
    dataIndex: 'mask',
    key: 'mask',
  },
  {
    title: '起始地址',
    dataIndex: 'startAddr',
    key: 'startAddr',
  },
  {
    title: '结束地址',
    dataIndex: 'endAddr',
    key: 'endAddr',
  },
  {
    title: '可用Ip地址总数',
    dataIndex: 'available',
    key: 'available',
  },
  {
    title: '已使用Ip地址数',
    dataIndex: 'inUse',
    key: 'inUse',
  },
  {
    title: '剩余Ip地址数',
    dataIndex: 'remain',
    key: 'remain',
  },
  {
    title: '操作',
    dataIndex: 'operation',
    key: 'operation'
  },
]
// 方法
const onEdit = (record) => {
  title.value = '修改地址池'
  Object.keys(ipPoolInfo.value).forEach(key => {
    ipPoolInfo.value[key] = record[key]
  })
  ipPoolInfo.value.create = false;
  console.log(ipPoolInfo.value)
  open.value = true;

}
const onDelete = (record) => {
  axios.delete("/pool/delete/" + record.poolId).then((res) => {
    if (res.data.status) {
      message.success('{}删除成功', record.poolName)
      queryInfo();
    } else {
      message.error(res.data.message)
    }
  })
}
// Common
// 属性
let interval;
// 方法
const queryInfo = () => {
  axios.get("/pool/info").then((res) => {
    if (res.data.status) {
      ipPoolTable.value = res.data.data;
    } else {
      message.error(res.data.message)
    }
  })
}
onMounted(()=>{
  queryInfo();
  interval = setInterval(()=>{
    queryInfo()
  },300000)
})
onUnmounted(()=>{
  clearInterval(interval)
})
</script>

<template>
  <a-button type="primary" @click="insert">添加地址池</a-button>
  <a-modal v-model:open="open" :title="title" @ok="handleOk">
    <a-form
        :model="ipPoolInfo"
        name="basic"
        autocomplete="off"
    >
      <a-form-item
          label="地址池名称"
          name="poolName"
      >
        <a-input v-model:value="ipPoolInfo.poolName"/>
      </a-form-item>
      <a-form-item
          label="网络地址"
          name="networkAddress"
      >
        <a-input v-model:value="ipPoolInfo.networkAddress"/>
      </a-form-item>
      <a-form-item
          label="掩码"
          name="mask"
      >
        <a-select :options="selectValue" v-model:value="ipPoolInfo.mask"/>
      </a-form-item>
      <a-form-item
          label="起始地址"
          name="startAddr"
      >
        <a-input v-model:value="ipPoolInfo.startAddr"/>
      </a-form-item>
      <a-form-item
          label="结束地址"
          name="endAddr"
      >
        <a-input v-model:value="ipPoolInfo.endAddr"/>
      </a-form-item>

    </a-form>
  </a-modal>
  <a-table bordered :data-source="ipPoolTable" :columns="columns">
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'operation'">
        <a-flex gap="middle">
          <a-button type="primary" @click="onEdit(record)" > Edit</a-button>
          <a-popconfirm
              v-if="ipPoolTable.length"
              title="Sure to delete?"
              @confirm="onDelete(record)"
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