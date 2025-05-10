<script setup>

// 查询全部DNS信息
import axios from "axios";
import {message} from "ant-design-vue";

const test = () => {
  axios.get("/acme/getCert?domain=test7.summer.fan").then((resp) => {
    const json = resp.data
    if (json.status) {
      // 将 Base64 字符串转换为二进制数据
      const zipBytesBase64 = json.data;
      const byteCharacters = atob(zipBytesBase64); // 解码 Base64
      const byteNumbers = new Array(byteCharacters.length);

      for (let i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i);
      }

      // 将字节数组转换为 Uint8Array
      const byteArray = new Uint8Array(byteNumbers);

      const blob = new Blob([byteArray], {type: 'application/zip'});
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'test7.summer.fan.zip')
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url)

    } else {
      message.error(json.message)
    }
  })

}

</script>

<template>
  <h1>证书申请界面</h1>
  <a-button @click="test">Test</a-button>
</template>

<style scoped>

</style>