<template>
  <a-layout-sider width="200" style="background: #fff">
    <a-menu
        v-model:selectedKeys="selectedKeys"
        mode="inline"
        :style="{ height: '100%', borderRight: 0 }"
    >
      <a-sub-menu key="ip">
        <template #title>
          <span>
            地址池管理
          </span>
        </template>

        <a-menu-item key="/ip">
          <router-link to="/ip">
            <GlobalOutlined/> &nbsp; 地址池信息
          </router-link>
        </a-menu-item>

      </a-sub-menu>
      <a-sub-menu key="server">
        <template #title>
          <span>
            服务器管理
          </span>
        </template>

        <a-menu-item key="/server">
          <router-link to="/server">
            <DesktopOutlined/> &nbsp; 新增服务器
          </router-link>
        </a-menu-item>

      </a-sub-menu>
      <a-sub-menu key="network">
        <template #title>
          <span>
            网络服务
          </span>
        </template>
        <a-menu-item key="/dns">
          <router-link to="/dns">
            &nbsp; DNS供应商管理
          </router-link>
        </a-menu-item>
        <a-menu-item key="/acme">
          <router-link to="/acme">
            &nbsp; 域名证书申请
          </router-link>
        </a-menu-item>
      </a-sub-menu>
      <a-sub-menu key="setting">
        <template #title>
          <span>
            系统设置
          </span>
        </template>

        <a-menu-item key="/user">
          <router-link to="/user">
            <UserOutlined/> &nbsp; 用户管理
          </router-link>
        </a-menu-item>

      </a-sub-menu>

    </a-menu>
  </a-layout-sider>

</template>

<script setup>
import {ref, watch} from 'vue';
import {DesktopOutlined, GlobalOutlined} from "@ant-design/icons-vue";
import router from "@/router";
import store from "@/store";

const displayMenu = () => {
  if (store.state.User.roleId === 1 || store.state.User.roleId === 2) {
    return true;
  } else {
    return false;
  }
}
// 使菜单两边选中的功能保持一致
const selectedKeys = ref([]);
watch(() => router.currentRoute.value.path, (newValue) => {
  selectedKeys.value = [];
  selectedKeys.value.push(newValue);
}, {immediate: true});

</script>

<style scoped>

</style>
