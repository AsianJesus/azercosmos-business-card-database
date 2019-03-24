<template>
  <div id="app" class="container-fluid">
    <div class="bcards-header row">
      <!--<div class="col bcards-header-holder">
        <router-link :to="{name: 'BusinessCards'}" class="bcards-header-nav">Main page</router-link>
      </div>
      <div class="col bcards-header-holder">
        <router-link :to="{name: 'BusinessCardsWithOption', params: {option: 'private'}}" class="bcards-header-nav">
          Private
        </router-link>
      </div>-->
      <router-link :to="{name: 'BusinessCardsWithOption', params: {option: 'public'}}"
                   class="bcards-header-nav col bcards-header-holder">
        Public
      </router-link>
      <router-link :to="{name: 'BusinessCardsWithOption', params: {option: 'mycards'}}"
                   class="bcards-header-nav col bcards-header-holder">
          My cards
      </router-link>
      <!--<div class="col bcards-header-holder">
        <router-link :to="{name: 'NewBusinessCard'}" class="bcards-header-nav">
          New card
        </router-link>
      </div>-->
    </div>
    <div class="row">
      <div class="col-12">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<script>
import Toolbar from './components/Toolbar/ToolbarComponent.vue'
export default {
  name: 'App',
  components: {
    Toolbar
  },
  created () {
    this.loadUserInfo()
  },
  methods: {
    loadUserInfo () {
      this.axios.get('/user').then(response => {
        this.$store.commit('updateUserInfo', response.data)
      })
    }
  }
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

.bcards-header{
  background-color: #17a2b8;
}
.bcards-header-nav{
  color: white !important;
  text-decoration: none;
}
.bcards-header-nav:hover{
  text-decoration: none !important;
}
.bcards-header-holder{
  font-size: 1.1rem;
  padding-top: .2rem;
  padding-bottom: .2rem;
  transition: .2s ease;
}
.bcards-header-holder:hover{
  background: #30303030;

}
.bcards-icon-button{
  padding: 3px 8px;
}
</style>
