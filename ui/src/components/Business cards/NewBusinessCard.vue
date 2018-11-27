<template>
    <div class="new-business-card-page">
        <h1>
            New Business Card
        </h1>
        <div v-if="isAdding">
            <h2>
                Please wait for a second...
            </h2>
        </div>
        <div class="business-card-form" v-else>
            <div class="row">
                <label for="name" class="col-3 offset-2">Name</label>
                <input type="text" id="name" v-model="form.name" class="col-4 new-bcard-info">
            </div>
            <div class="row">
                <label for="surname" class="col-3 offset-2">Surname</label>
                <input type="text" id="surname" v-model="form.surname" class="col-4 new-bcard-info">
            </div>
            <div class="row">
                <label for="company_name" class="col-3 offset-2">Company</label>
                <input type="text" id="company_name" v-model="form.company_name" class="col-4 new-bcard-info">
            </div>
            <div class="row">
                <label for="position" class="col-3 offset-2">Your position</label>
                <input type="text" id="position" v-model="form.position" class="col-4 new-bcard-info">
            </div>
            <div class="row">
                <label for="address" class="col-3 offset-2">Address</label>
                <input type="text" id="address" v-model="form.address" class="col-4 new-bcard-info">
            </div>
            <div class="row">
                <label for="mobile" class="col-3 offset-2">Mobile phone</label>
                <input type="text" id="mobile" v-model="form.mobile" class="col-4 new-bcard-info">
            </div>
            <div class="row">
                <label for="email" class="col-3 offset-2">Email</label>
                <input type="email" id="email" v-model="form.email" class="col-4 new-bcard-info">
            </div>
            <div class="row">
                <label for="website" class="col-3 offset-2">Website</label>
                <input type="text" id="website" v-model="form.website" class="col-4 new-bcard-info">
            </div>
            <div>
                <select name="" id="" v-model="form.private">
                    <option :value="false">Public</option>
                    <option :value="true">Private</option>
                </select>
            </div>
            <div>
                <button class="business-card-submit" @click="send">Submit</button>
            </div>
            {{ form }}
        </div>
    </div>
</template>
<script>
export default{
  data () {
    return {
      form: {
        name: '',
        surname: '',
        company_name: '',
        position: '',
        mobile: '',
        email: '',
        webste: '',
        address: '',
        created_by: this.$store.getters.userId,
        private: true
      },
      isAdding: false
    }
  },
  methods: {
    send () {
      if (this.isAdding) return
      this.isAdding = true
      this.axios.post(this.$store.state.serverUrl + '/b-cards/', this.form).then(response => {
        console.log(response.data)
        this.isAdding = false
        this.$router.push({name: 'BusinessCards'})
      }).catch(err => {
        console.log(err)
        this.isAdding = false
        alert('Error!')
      })
      console.log(this.form)
    }
  }
}
</script>
<style>
.new-bcard-info{
    border-radius: 4rem;
    outline: 0;
}
</style>
