import { createRouter, createWebHistory } from 'vue-router'
import store from "@/store";
import {message} from "ant-design-vue";

const routes = [{
  path: '/',
  name: 'main',
  component: () => import('../views/HomeView.vue'),
  meta:{
    loginRequire: true
  },
  children: [
    {
      path: '/server',
      name: 'server',
      component: () => import('@/views/SecondaryRoutingViews/server/ServerManager.vue')
    },
      {
        path: '/ip',
        name: 'ip',
        component: () => import('@/views/SecondaryRoutingViews/ipResource/ipResource.vue')
      }
  ]

},
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/LoginView.vue')
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})
// 路由登录拦截,验证用户是否登录
router.beforeEach((to, from, next) => {
  // 要不要对meta.loginRequire属性做监控拦截
  if (to.matched.some(function (item) {
    if(import.meta.env.VITE_NODE_ENV !== 'production') {
      console.log(item, "是否需要登录校验：", item.meta.loginRequire || false);
    }
    return item.meta.loginRequire
  })) {
    const _userInfo = store.state.User;
    if(import.meta.env.VITE_NODE_ENV !== 'production') {
      console.log("页面登录校验开始：", _userInfo);
    }
    if (!_userInfo.token) {
      if(import.meta.env.VITE_NODE_ENV !== 'production') {
        console.log("用户未登录或登录超时！");
      }
      message.error("未登录或登录超时");
      next('/login');
    } else {
      next();
    }
  } else {
    next();
  }
});

export default router

