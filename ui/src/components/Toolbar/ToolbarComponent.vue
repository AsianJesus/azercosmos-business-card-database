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
  },
  mounted () {
    this.load()
  },
  methods: {
    load () {
      if (this.isLoading) return
      this.isLoading = true
      this.axios.get('/users/' + this.$store.getters.userId).then(response => {
        this.isLoading = false
        this.$store.commit('updateUserInfo', response.data)
      }).catch(err => {
        console.log(err)
        this.isLoading = false
      })
    }
  }
}
</script>
<style>

</style>
