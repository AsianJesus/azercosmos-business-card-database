// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import VueAxios from 'vue-axios'
import axios from 'axios'
import VueBootstrap from 'bootstrap-vue'
import VueCookie from 'vue-cookie'
import Vuex from 'vuex'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'

const serverURL = 'http://localhost:8000/'
axios.defaults.baseURL = serverURL

Vue.use(VueAxios, axios)
Vue.use(Vuex)
Vue.use(VueCookie)
Vue.use(VueBootstrap)


const store = new Vuex.Store({
  state: {
    serverURL: serverURL,
    user: null
  },
  getters: {
    userId: state => {
      return 1
    },
    userName: state => {
      return state.user ? state.user.name : null
    }
  },
  mutations: {
    updateUserInfo (state, userInfo) {
      state.user = userInfo
    }
  }
})

Vue.config.productionTip = false

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  components: { App },
  template: '<App/>'
})
