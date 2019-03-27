<template>
    <div class="toolbar-component row">
        <div class="col-2 order-last">
            {{ name }}
        </div>
        <div class="col"></div>
    </div>
</template>
<script>
export default{
  data () {
    return {
      isLoading: false
    }
  },
  computed: {
    name () {
      return this.$store.getters.userName
    }
  }
}
</script>
<style>

</style>
