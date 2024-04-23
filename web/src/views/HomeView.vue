<template>
  <a-layout class="layout-container">
    <header-bar-view></header-bar-view>
    <a-layout>
      <side-bar-view></side-bar-view>
      <a-layout class="content-layout">
        <a-layout-content class="router-view-container">
          <router-view></router-view>
        </a-layout-content>
        <a-layout-footer class="footer">
          HomeLab ©2024 Created by PhoebeJ
        </a-layout-footer>
      </a-layout>
    </a-layout>
  </a-layout>
</template>
<script>
import { defineComponent, ref } from 'vue';
import SideBarView from "@/components/SiderComponents.vue";
import HeaderBarView from "@/components/HearderComponents.vue";
export default defineComponent({
  components: {
    HeaderBarView,
    SideBarView,
  },
  setup() {
    return {
      selectedKeys1: ref(['2']),
      selectedKeys2: ref(['1']),
      collapsed: ref(false),
      openKeys: ref(['sub1']),
    };
  },
});
</script>
<style>
body {
  margin: 0;
  height: 100vh;
}

.layout-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.content-layout {
  background: white;
  display: flex;
  flex-direction: column;
  flex: 1 0 auto;
}
.router-view-container{
  flex: 1 0 auto; /* 新增这行 */
  overflow: auto; /* 新增这行 */
}
.footer {
  margin-top: auto;
  text-align: center;
}

#components-layout-demo-top-side-2 .logo {
  float: left;
  width: 120px;
  height: 31px;
  margin: 16px 24px 16px 0;
  background: rgba(255, 255, 255, 0.3);
}

.ant-row-rtl #components-layout-demo-top-side-2 .logo {
  float: right;
  margin: 16px 0 16px 24px;
}

.site-layout-background {
  background: #fff;
}
</style>
