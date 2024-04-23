<script setup>
import store from "@/store";
import {onMounted, ref} from "vue";
import {dateToString, encrypt, roleIdToName} from "@/utils/common";
import axios from "axios";
import {notification} from "ant-design-vue";
import router from "@/router";

const userInfo = ref({
  userId: "",
  userName: "",
  roleName: "",
  email: "",
  createTime: "",
})
const getUserInfo = () => {
  userInfo.value.userId = store.state.User.userId;
  userInfo.value.roleName = roleIdToName(store.state.User.roleId);
  userInfo.value.userName = store.state.User.userName;
  userInfo.value.email = store.state.User.email
  userInfo.value.createTime = dateToString(store.state.User.createTime, "yyyy-MM-dd");
}

// model窗口
const open = ref(false);
const oldData = ref({
  userName: "",
  email: "",
})
const showDrawer = () => {
  formState.value.userId = userInfo.value.userId;
  formState.value.userName = userInfo.value.userName;
  formState.value.email = userInfo.value.email;

  oldData.value.userName = userInfo.value.userName;
  oldData.value.email = userInfo.value.email;
  open.value = true;
}

// 用户信息修改表单属性
const formState = ref({
  userId: "",
  userName: "",
  email: "",
  oldPassword: "",
  newPassword: "",
  needChangePassword: false
})

// 更新信息方法
const updateUserInfo = () => {
  if (formState.value.newPassword === "") {
    formState.value.needChangePassword = false;
  } else {
    formState.value.needChangePassword = true;
    // 使用公钥加密
    formState.value.newPassword = encrypt(formState.value.newPassword, store.state.User.publicKey);
    formState.value.oldPassword = encrypt(formState.value.oldPassword, store.state.User.publicKey);
  }
  axios.post("/user/update", formState.value).then((response) => {
    const json = response.data;
    if (json.status) {
      notification.success({
        message: "更新成功, 请重新登录",
      })
      //TODO:在判断更改非必要信息时不强制登出系统
      if (formState.value.needChangePassword === false || formState.value.userName === oldData.value.userName) {
        userInfo.value.email = json.data.email;
        // 刷新缓存
        windows.sessionStorage.removeItem("setUserInfo");
        store.commit("setUserInfo", json.data);
      } else {
        // 置空浏览器本地存储
        windows.sessionStorage.removeItem("setUserInfo");
        router.push("/login")
      }
    } else {
      notification.error({
        message: json.message,
      })
    }
  });
}

onMounted(() => {
  getUserInfo();
})

</script>

<template>
  <a-descriptions class="userInfo" title="用户信息" bordered>
    <a-descriptions-item label="用户名称">{{ userInfo.userName }}</a-descriptions-item>
    <a-descriptions-item label="用户ID">{{ userInfo.userId }}</a-descriptions-item>
    <a-descriptions-item label="会员等级">{{ userInfo.roleName }}</a-descriptions-item>
    <a-descriptions-item label="入站时间">{{ userInfo.createTime }}</a-descriptions-item>
    <a-descriptions-item label="邮件地址" :span="2">{{ userInfo.email }}</a-descriptions-item>
  </a-descriptions>
  <a-button @click="showDrawer">修改</a-button>

  <a-modal v-model:open="open" title="信息修改" @ok="updateUserInfo">
    <a-form
        :model="formState"
        name="basic"
        :label-col="{ span: 8 }"
        :wrapper-col="{ span: 16 }"
        autocomplete="off"
    >
      <a-form-item
          label="用户名"
          name="username"
      >
        <a-input v-model:value="formState.userName"/>
      </a-form-item>
      <a-form-item
          label="邮箱"
          name="email"
      >
        <a-input v-model:value="formState.email"/>
      </a-form-item>

      <a-form-item
          label="老密码"
          name="password"
      >
        <a-input-password v-model:value="formState.oldPassword"/>
      </a-form-item>
      <a-form-item
          label="新密码"
          name="newpassword"
      >
        <a-input-password v-model:value="formState.newPassword"/>
      </a-form-item>
    </a-form>
  </a-modal>

</template>

<style scoped>

.userInfo {
  margin-bottom: 12px;
}

</style>