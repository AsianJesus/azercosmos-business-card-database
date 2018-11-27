<template>
  <div id="app" class="container-fluid">
    <div class="row">
      <div class="col-12">
        <router-link :to="{name: 'BusinessCards'}">
          <img src="@/assets/logo.png">
        </router-link>
      </div>
      <div class="col-12">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
