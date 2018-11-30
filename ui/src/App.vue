<template>
  <div id="app" class="container-fluid">
    <div class="row toolbar-layout">
      <div class="col-12">
        <toolbar></toolbar>
      </div>
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
.toolbar-layout{
  height: 2rem;
  background-color: #2c3e50;
  color: #FFFFFF;
}

</style>
