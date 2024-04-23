<script setup>
import {onMounted, reactive, ref} from "vue";
import axios from "axios";
import {JSEncrypt} from "jsencrypt";
import {message} from "ant-design-vue";
import store from "@/store";
import router from "@/router";
import {dateToString} from "@/utils/common";

const formData = reactive({
  userName: '',
  password: '',
  agree: true,
});
const rules = {
  userName: [
    {required: true, message: '请输入用户名', trigger: 'blur'},
  ],
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'},
  ],
  agree: [
    {required: true, message: '请勾选同意用户使用协议', trigger: 'change'},
  ],
};

const userContract = () => {
  console.log('点击用户协议');
}
const login = async () => {
  if (formData.agree) {
    let loginData = {
      userName: formData.userName,
      password: formData.password,
    }
    let base64PublicKey = undefined;
    const tmpDate = {
      userName: formData.userName,
    }
    await axios.get("/publicKey").then((response) => {
      const json = response.data;
      if (json.status) {
        base64PublicKey = json.data;
        const encryptor = new JSEncrypt();
        encryptor.setPublicKey(base64PublicKey);
        try {
          loginData.password = encryptor.encrypt(formData.password);
        } catch (e) {
          message.error('添加授权异常')
        }
      } else {
        message.error('未获得授权')
      }
    })

    await axios.post("/login", loginData).then((response) => {
      const json = response.data;
      if (json.status) {
        store.commit("setUserInfo", json.data);
        message.success('欢迎' + json.data.userName + '！')
        // 界面跳转
        router.push("/");
      } else {
        message.error(json.message)
      }
    });
  } else {
    message.warning("请先同意用户协议！");
  }
}

</script>

<template>
  <a-layout class="a-layout">
    <a-layout-header class="header">
      <div class="header-image"/>
    </a-layout-header>
  <a-layout-content class="content">
  <a-card hoverable  class="card">
    <template #cover>
      <img
          src="@/assets/login-image.jpeg"
       alt="login"/>
    </template>
    <a-form
        class="login"
        :model="formData"
        name="basic"
        autocomplete="off"
        :rules="rules"
    >
      <div class="logo-image"></div>
      <a-form-item
          label="用户名"
          name="userName"
      >
        <a-input v-model:value="formData.userName"/>
      </a-form-item>

      <a-form-item
          label="密码"
          name="password"
      >
        <a-input-password v-model:value="formData.password"/>
      </a-form-item>

      <a-form-item name="remember">
        <a-checkbox v-model:checked="formData.agree">
          同意用户使用协议
        </a-checkbox>
        <a v-on:click="userContract" >查看协议</a>
      </a-form-item>
    </a-form>
    <template #actions>

      <a-tooltip placement="bottom">
        <template #title>
          <span>注册</span>
        </template>
      <UserAddOutlined @click="register"/>
      </a-tooltip>
      <a-tooltip placement="bottom">
        <template #title>
          <span>登陆</span>
        </template>
        <LoginOutlined @click="login"/>
      </a-tooltip>
    </template>
  </a-card>
  </a-layout-content>
  <a-layout-footer class="footer">HomeLab ©2024 Created by PhoebeJ</a-layout-footer>
  </a-layout>
</template>

<style scoped>
.header-image {
  margin-top: 5px;
  margin-right: 12px;
  margin-left: 12px;
  float: left;
  background: url("@/assets/long-logo.png") no-repeat scroll center;
  background-size: 100% 100%;
  width: 200px;
  height: 50px;
  display: inline-block;
}

.sub-title {
  margin-top: 40px;
  margin-right: 12px;
  display: inline-block;
}
.card{
  width: 300px;
}
body {
  margin: 0;
  height: 100vh; /* 视口高度100% */
}

.a-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
}
.content{
  background-image: url("@/assets/background.png");
  background-size: 100% 100%;
  display: flex;
  justify-content: flex-end; /* 内容靠右对齐 */
  align-items: center; /* 垂直居中 */
  height: 100vh;
  padding-right: 10%; /* 右侧预留15%间距 */
  box-sizing: border-box; /* 包括padding在内的总宽度 */
}

.footer {
  margin-top: auto;
  text-align: center;
}

</style>