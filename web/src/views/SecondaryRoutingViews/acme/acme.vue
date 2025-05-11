<script setup>

// 查询全部DNS信息
import axios from "axios";
import {message} from "ant-design-vue";
import {getDownloadFileFromBase64} from "@/utils/common.js";

const test = () => {
  axios.get("/acme/getCert?domain=test7.summer.fan").then((resp) => {
    const json = resp.data
    if (json.status) {
      getDownloadFileFromBase64(json.data, 'test7.summer.fan.zip', 'application/zip')
    } else {
      message.error(json.message)
    }
  })

}

</script>

<template>

  <a-button @click="test">Test</a-button>
</template>

<style scoped>

</style>