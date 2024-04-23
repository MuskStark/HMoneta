<template>
  <a-layout-header class="header">
    <div class="logo-image"/>
    <a-button class="userButton" type="link" @click="showDrawer">用户信息</a-button>
    <a-drawer
        v-model:open="open"
        class="custom-class"
        root-class-name="root-class-name"
        :root-style="{ color: 'blue' }"
        style="color: red"
        title="登录信息"
        placement="right"
    >
      <div class="info">
        <a-descriptions :column="1">
          <a-descriptions-item label="用户名">{{ userInfo.userName }}</a-descriptions-item>
          <a-descriptions-item label="邮箱">{{ userInfo.email }}</a-descriptions-item>
        </a-descriptions>
      </div>
      <div class="logoutButton">
        <a-button type="primary" @click="logout">退出</a-button>
      </div>
    </a-drawer>

  </a-layout-header>
</template>

<script setup>
import {defineComponent, ref} from 'vue';
import store from "@/store";
import router from "@/router";
import {roleIdToName} from "@/utils/common";

const userInfo = ref({
  userName: "",
  email: ""
});

// 配置用户信息
userInfo.value.userName = store.state.User.userName;
userInfo.value.email = store.state.User.email;

const open = ref(false);
const showDrawer = () => {
  open.value = true;
}

const logout = () => {
  window.sessionStorage.clear();
  router.push("/login");

}
</script>
<style scoped>
.logo-image {
  margin-top: 5px;
  margin-right: 12px;
  float: left;
  background: url("@/assets/long-logo.png") no-repeat scroll center;
  background-size: 100% 100%;
  width: 200px;
  height: 50px;
  display: inline-block;
}

.userButton {
  float: right;
  margin-right: 10px;
  margin-top: 15px;
}

.logoutButton {
  position: absolute;
  display: inline-block;
  margin-left: 135px;
  bottom: 15px;
}

.info {
  margin-top: 12px;
}

</style>
