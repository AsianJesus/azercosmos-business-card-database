// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import VueAxios from 'vue-axios'
import axios from 'axios'
import VueBootstrap from 'bootstrap-vue'
import VueCookie from 'vue-cookie'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import 'vue-awesome/icons'
import './global.css'
import Icon from 'vue-awesome/components/Icon'
import { serverURL } from './config'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import store from './store'

axios.defaults.baseURL = serverURL

Vue.use(VueAxios, axios)
Vue.use(VueCookie)
Vue.use(VueBootstrap)
Vue.component('v-icon', Icon)
Vue.component('font-awesome-icon', FontAwesomeIcon)

Vue.config.productionTip = false

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  components: { App },
  template: '<App/>'
})
