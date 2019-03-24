<template>
    <div class="new-business-card-page row">
        <div class="col-12 new-business-card-header">
            New Business Card
        </div>
        <div v-if="isAdding" class="col-12">
            <h2>
                Please wait for a second...
            </h2>
        </div>
        <div class="business-card-form col-12" v-else>
            <div class="row">
                <div class="col" @keydown="takePhotoAtSpacebar">
                    <div class="new-bcard-webcam-buttons">
                        <b-btn @click="streaming ? stopWebcam() : startWebcam()"
                               class="bcards-icon-button"
                               :variant="streaming ? 'danger' : 'warning'">
                            <v-icon name="camera">
                            </v-icon>
                        </b-btn>
                        <b-btn variant="primary"
                               class="bcards-icon-button"
                               @click="showCrop = true">
                            <v-icon name="file-image">

                            </v-icon>
                        </b-btn>
                        <b-btn @click="selectWebcam" v-if="streaming" variant="">
                            <v-icon name="image">

                            </v-icon>
                        </b-btn>
                    </div>
                    <div>
                        <b-form-input class="new-bcard-info-cell" v-model="form.name" id="name" placeholder="Name" type="text">

                        </b-form-input>
                        <b-form-input class="new-bcard-info-cell" type="text" placeholder="Company" v-model="form.company_name">

                        </b-form-input>
                        <b-form-input class="new-bcard-info-cell" type="text" placeholder="Position" v-model="form.position">

                        </b-form-input>
                        <b-form-input class="new-bcard-info-cell" type="text" placeholder="Address" v-model="form.address">

                        </b-form-input>
                        <b-form-input class="new-bcard-info-cell" type="tel" placeholder="Phone" v-model="form.mobile">

                        </b-form-input>
                        <b-form-input class="new-bcard-info-cell" type="email" placeholder="Email" v-model="form.email">

                        </b-form-input>
                        <b-form-input class="new-bcard-info-cell" type="text" placeholder="Website" v-model="form.website">

                        </b-form-input>
                        <b-form-select class="new-bcard-info-cell" v-model="form.private" :options="privacyOptions">

                        </b-form-select>
                    </div>
                    <div class="new-bcard-submit">
                        <b-btn class="business-card-submit" variant="success" @click="send">
                            Create
                            <v-icon name="plus">

                            </v-icon>
                        </b-btn>
                    </div>
                </div>
                <div class="col-6">
                    <webcam ref="webcam" v-model="streaming" :width="675" :height="400"></webcam>
                    <video ref="webcam_video" v-bind:style="{display: streaming ? 'inline' : 'none'}"
                           class="new-bcard-webcam-video">

                    </video>
                    <b-form-select :options="langOptions" v-model="selectedLang" @change="rerunRecognizing">

                    </b-form-select>
                    <div v-if="recognizing.progress" class="recognizing-progress">
                        <h5>
                          {{ recognizing.progress.status }}
                        </h5>
                        <b-progress :value="recognizing.progress.progress" :max="1" label="Recognizing" show-progress>
                        </b-progress>
                    </div>
                    <div @click="showCrop = true">
                        <img :src="imageUrl"
                             alt="Card image"
                             class="new-bcard-image" v-if="imageUrl">

                    </div>
                    <vue-image-crop v-model="showCrop" noCircle :width="675" :height="400" langType="en"
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
import Tesseract from 'tesseract.js'
import { recognize } from '@/assets/js/parsingFunctions.js'
export default{
  components: {
    Webcam,
    VueImageCrop
  },
  data () {
    return {
      form: {
        name: '',
        company_name: '',
        position: '',
        mobile: '',
        email: '',
        webste: '',
        address: '',
        photo: null,
        private: true
      },
      $Tesseract: null,
      privacyOptions: [
        {
          text: 'Please, select privacy level',
          value: 1
        },
        {
          text: 'Private',
          value: 1
        },
        {
          text: 'Public',
          value: 0
        }
      ],
      imageUrl: '',
      isAdding: false,
      streaming: false,
      showCrop: false,
      recognizing: {
        progress: null,
        recognizedText:  null
      },
      langOptions: [
        {
          text: 'Please, select language',
          value: null
        },
        {
          text: 'English',
          value: 'eng'
        },
        {
          text: 'Русский',
          value: 'rus'
        },
        {
          text: 'Azərbaycan',
          value: 'aze'
        }
      ],
      selectedLang: null
    }
  },
  mounted () {
    this.$Tesseract = Tesseract.create({
      workerPath: this.$store.state.serverURL + '/tesseract/worker.js',
      langPath:   this.$store.state.serverURL + '/tesseract/langs/',
      corePath:   this.$store.state.serverURL + '/tesseract/index.js'
    })
  },
  methods: {
    selectWebcam () {
      this.uploadFile(this.$refs.webcam.takePicture())
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
    takePhotoAtSpacebar (event) {
      if(this.streaming && event.keyCode === 32) {
        this.selectWebcam()
      }
    },
    uploadFile (url) {
      this.imageUrl = url  
      fetch(this.imageUrl).then(photo => photo.blob()).then(file => {
        this.form.photo = file
        this.recognizeImage(file)
      })
    },
    rerunRecognizing () {
      if (this.form.photo) {
        this.recognizeImage(this.form.photo)
      }
    },
    send () {
      if (this.isAdding) return
      this.isAdding = true
      var form = new FormData()
      for (let key in this.form) {
        form.set(key, this.form[key])
      }
      form.append('photo', this.form.photo)
      this.axios.post('/business-cards', form).then(response => {
        console.log(response.data)
        this.isAdding = false
        this.$emit('newCard', response.data)
      }).catch(err => {
        console.log(err)
        this.isAdding = false
        alert('Error!')
      })
      console.log(this.form)
    },
    recognizeImage (image) {
      this.$Tesseract.recognize(image, {
        lang: this.selectedLang
      }).progress(p => {
        this.recognizing.progress = p
      }).then(result => {
        this.recognizing.recognizedText = result.text
        this.autoFillFields()
      }).catch(err => {
        console.log(err)
      })
    },
    autoFillFields () {
      this.form.name = recognize(this.recognizing.recognizedText, 'name', this.selectedLang)
      this.form.mobile = recognize(this.recognizing.recognizedText, 'phone', this.selectedLang)
      this.form.email = recognize(this.recognizing.recognizedText, 'email', this.selectedLang)
      this.form.website = recognize(this.recognizing.recognizedText, 'website', this.selectedLang)
    }
  }
}
</script>
<style>
.new-business-card-page{
    width: 100%;
    padding: 1rem;
}
.new-business-card-header{
    font-size: 1.9rem;
    font-family: Segoe UI,Frutiger,Frutiger Linotype,Dejavu Sans,Helvetica Neue,Arial,sans-serif;
    font-weight: bolder;
}
.new-bcard-webcam-video{
    width: 100%;
    border-radius: 1rem;
    height: auto;
}
.new-bcard-image{
    max-width: 100%;
    width: 480px;
    height: auto;
    border-radius: 10px;
}
.new-bcard-submit{
    margin: 1rem auto;
}
.new-bcard-webcam-buttons{
    margin: .5rem auto;
}
.new-bcard-info-cell{
    margin: .3rem auto;
}
.recognizing-progress{
    text-transform: capitalize;
    font-family: serif;
    margin: .3rem auto;
    border: 1px solid black;
    padding: .2rem auto;
}
</style>
