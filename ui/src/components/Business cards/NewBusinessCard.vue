<template>
    <div class="new-business-card-page row">
        <h1 class="col-12">
            New Business Card
        </h1>
        <div v-if="isAdding" class="col-12">
            <h2>
                Please wait for a second...
            </h2>
        </div>
        <div class="business-card-form col-12" v-else>
            <div class="row">
                <div class="col">
                    <div class="row">
                        <label for="name" class="col-3 offset-2">Name</label>
                        <input type="text" id="name" v-model="form.name" class="col new-bcard-info">
                    </div>
                    <div class="row">
                        <label for="surname" class="col-3 offset-2">Surname</label>
                        <input type="text" id="surname" v-model="form.surname" class="col new-bcard-info">
                    </div>
                    <div class="row">
                        <label for="company_name" class="col-3 offset-2">Company</label>
                        <input type="text" id="company_name" v-model="form.company_name" class="col new-bcard-info">
                    </div>
                    <div class="row">
                        <label for="position" class="col-3 offset-2">Your position</label>
                        <input type="text" id="position" v-model="form.position" class="col new-bcard-info">
                    </div>
                    <div class="row">
                        <label for="address" class="col-3 offset-2">Address</label>
                        <input type="text" id="address" v-model="form.address" class="col new-bcard-info">
                    </div>
                    <div class="row">
                        <label for="mobile" class="col-3 offset-2">Mobile phone</label>
                        <input type="text" id="mobile" v-model="form.mobile" class="col new-bcard-info">
                    </div>
                    <div class="row">
                        <label for="email" class="col-3 offset-2">Email</label>
                        <input type="email" id="email" v-model="form.email" class="col new-bcard-info">
                    </div>
                    <div class="row">
                        <label for="website" class="col-3 offset-2">Website</label>
                        <input type="text" id="website" v-model="form.website" class="col new-bcard-info">
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
            </div>
            <div class="col-2">
                <button @click="streaming ? stopWebcam() : startWebcam()"> {{ streaming ? 'Stop' : 'Webcam' }}</button>
                <button @click="selectWebcam" v-if="streaming">Take picture</button>
            </div>
            <div class="col-6">
                <webcam ref="webcam" v-model="streaming" :width="1024" :height="720"></webcam>
                <video ref="webcam_video" v-bind:style="{display: streaming ? 'inline' : 'none'}" class="new-bcard-webcam-video">

                </video>
                <img :src="imageUrl ? imageUrl : null" alt="Card image" class="new-bcard-image" @click="showCrop = true">
                <vue-image-crop v-model="showCrop" noCircle imgFormat="jpg" url="" :width="640" :height="640" langType="en"
                                @crop-success="uploadFile">

                </vue-image-crop>
            </div>
            </div>
        </div>
    </div>
</template>
<script>
import Webcam from '@/components/Webcam/Webcam.vue'
import VueImageCrop from 'vue-image-crop-upload'
export default{
  components: {
    Webcam,
    VueImageCrop
  },
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
        photo: null,
        private: true
      },
      imageUrl: '',
      isAdding: false,
      streaming: false,
      showCrop: false
    }
  },
  methods: {
    selectWebcam () {
      this.imageUrl = this.$refs.webcam.takePicture()
      fetch(this.imageUrl).then(photo => photo.blob()).then(file => {
        this.form.photo = file
      })
    },
    startWebcam () {
      this.$refs.webcam.startVideo(stream => {
        this.$refs.webcam_video.srcObject = stream
        this.$refs.webcam_video.play()
      })
    },
    stopWebcam () {
      this.$refs.webcam.stopVideo()
    },
    uploadFile (url) {
      this.imageUrl = url
      fetch(this.imageUrl).then(photo => photo.blob()).then(file => {
        this.form.photo = file
      })
    },
    send () {
      if (this.isAdding) return
      this.isAdding = true
      var form = new FormData()
      for (let key in this.form) {
        form.set(key, this.form[key])
      }
      form.append('photo', this.form.photo)
      this.axios.post(this.$store.state.serverUrl + '/b-cards/', form).then(response => {
        console.log(response.data)
        this.isAdding = false
        this.$emit('newCard', response.data)
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
.new-bcard-webcam-video{
    width: 100%;
    height: auto;
}
.new-bcard-image{
    width: 100%;
    height: auto;
}
</style>
