import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Antd, {message} from 'ant-design-vue';
import 'ant-design-vue/dist/reset.css';
import * as Icons from '@ant-design/icons-vue'
import axios from "axios";
import "./assets/js/enums/custodian-enums"
import "./assets/js/tool"

const app = createApp(App);
app.use(Antd).use(store).use(router).mount('#app');

// 将Antd的图标以组件的形式全局加入到页面中
const icons = Icons;
for(const i in icons) {
    app.component(i, icons[i])
}


/**
 * axios拦截器
 */
axios.interceptors.request.use(function (config) {
    if(import.meta.env.VITE_NODE_ENV !== 'production') {
        console.log('请求参数：', config);
    }
    // 为所有的请求的headers带上token
    const _token = store.state.User.token;
    if (_token) {
        config.headers.token = _token;
        if(import.meta.env.VITE_NODE_ENV !== 'production') {
            console.log("请求headers增加token:", _token);
        }
    }
    return config;
}, error => {
    return Promise.reject(error);
});
axios.interceptors.response.use(function (response) {
    // 生产环境不打印控制台
    if(import.meta.env.VITE_NODE_ENV !== 'production') {
        console.log('返回结果：', response);
    }
    return response;
}, error => {
    if(import.meta.env.VITE_NODE_ENV !== 'production') {
        console.log('返回错误：', error);
    }
    const response = error.response;
    const status = response.status;
    if (status === 401) {
        // 判断状态码是401 跳转到登录页
        // console.log("未登录或登录超时，跳到登录页");
        store.commit("setMember", {});
        message.error("未登录或登录超时");
        router.push('/login');
    }
    return Promise.reject(error);
});
// 设置axios默认访问地址
axios.defaults.baseURL = import.meta.env.VITE_APP_SERVER
// 打印当前环境信息
if(import.meta.env.VITE_NODE_ENV !== 'production') {
    console.log(import.meta.env);
    console.log('环境：', import.meta.env.VITE_NODE_ENV);
    console.log('服务端：', import.meta.env.VITE_APP_SERVER);
}