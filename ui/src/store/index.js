import Vue from 'vue'
import Vuex from 'vuex'
import { serverURL } from '../config'

Vue.use(Vuex)

const state = {
  serverURL: serverURL,
  user: {
    id: null,
    name: null
  }
}
const mutations = {
  updateUserInfo (state, userInfo) {
    Object.keys(userInfo).forEach(field => {
      state.user[field.toLowerCase()] = userInfo[field]
    })
  }
}
const getters = {
  userId: state => state.user.id,
  userName: state => state.user.name
}

export default new Vuex.Store({
  state: state,
  getters: getters,
  mutations: mutations
})
