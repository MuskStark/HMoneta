import { createStore } from 'vuex'

const UserInfo = "UserInfo"
export default createStore({

  state: {
    User: window.sessionStorage.getItem(UserInfo) || {}
  },
  getters: {
  },
  mutations: {
    setUserInfo (state, data){
      state.User = data;
      window.sessionStorage.setItem("UserInfo", data);
    }
  },
  actions: {
  },
  modules: {
  }
})
