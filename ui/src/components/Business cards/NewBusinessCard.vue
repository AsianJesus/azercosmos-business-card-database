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
                <div class="col">
                    <div class="new-bcard-webcam-buttons">
                        <b-btn @click="startWebcam"
                               v-if="!streaming"
                               class="bcards-icon-button g-standard-button"
                               variant="warning">
                            <font-awesome-icon :icon="cameraIcon" />
                            <!--<i class="tooltiptext">
                                Webcam
                            </i>-->
                            Capture
                        </b-btn>
                        <b-btn variant="primary"
                               class="bcards-icon-button file-icon-button"
                               @click="showCrop = true">
                            <font-awesome-icon :icon="fileImageIcon" />
                            Upload file
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
                            <font-awesome-icon :icon="plusIcon" />
                            Create
                        </b-btn>
                    </div>
                </div>
                <div class="col-6">
                    <div class="row">
                        <div class="col-6">
                            <b-form-select :options="langOptions" v-model="selectedLang" @change="rerunRecognizing" />
                        </div>
                        <div class="col-6"
                             style="text-align: right;" >
                            <b-btn @click="selectWebcam"
                                   v-if="streaming"
                                   class="bcards-icon-button"
                                   variant="success">
                                <font-awesome-icon :icon="cameraIcon" />
                                Capture
                            </b-btn>
                        </div>
                    </div>
                    <div>
                        {{ recognizing.recognizedText }}
                    </div>
                    <webcam ref="webcam"
                            :height="500"
                            :width="750"
                            v-model="streaming" />
                    <video ref="webcam_video" v-bind:style="{display: streaming ? 'inline' : 'none'}"
                           class="new-bcard-webcam-video">

                    </video>
                    <div v-if="recognizing.progress" class="recognizing-progress">
                        <h5>
                          {{ recognizing.progress.status }}
                        </h5>
                        <b-progress :value="recognizing.progress.progress"
                                    :max="1"
                                    label="Recognizing"
                                    show-progress />
                    </div>
                    <div @click="showCrop = true">
                        <img :src="imageUrl"
                             alt="Card image"
                             class="new-bcard-image" v-if="imageUrl">

                    </div>
                    <vue-image-crop v-model="showCrop"
                                    noCircle
                                    :width="500"
                                    :height="300"
                                    langType="en"
                                    ref="imageCrop"
                                    @crop-success="uploadFile" />
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
import { faCamera, faFileImage, faImage, faPlus } from '@fortawesome/free-solid-svg-icons'
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
        private: 1
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
          text: 'Language',
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
      selectedLang: null,
      $keyListener: null
    }
  },
  computed: {
    isMain () {
      return this.$route.name === 'NewBusinessCard'
    },
    cameraIcon: () => faCamera,
    fileImageIcon: () => faFileImage,
    imageIcon: () => faImage,
    plusIcon: () => faPlus,
  },
  mounted () {
    this.startWebcam()
    this.$Tesseract = Tesseract.create({
      workerPath: this.$store.state.serverURL + 'tesseract/worker.js',
      langPath:   this.$store.state.serverURL + 'tesseract/langs/',
      corePath:   this.$store.state.serverURL + 'tesseract/index.js'
    })
  },
  methods: {
    selectWebcam () {
      let image = this.$refs.webcam.takePicture()
      fetch(image).then(photo => photo.blob()).then(photo => {
          console.log(photo)
          this.$refs.imageCrop.setSourceImg(photo)
      })
      this.showCrop = true
      // this.stopWebcam()
    },
    startWebcam () {
      this.$refs.webcam.startVideo(stream => {
        this.$refs.webcam_video.srcObject = stream
        this.$refs.webcam_video.play()
      })
      if (!this.$keyListener) {
        document.addEventListener('keydown', this.takePhotoAtSpacebar)
      }
    },
    stopWebcam () {
      this.$refs.webcam.stopVideo()
      document.removeEventListener('keydown', this.$keyListener)
      this.$keyListener = null
    },
    takePhotoAtSpacebar (event) {
      if(this.streaming && event.keyCode === 32) {
        this.selectWebcam()
      }
    },
    uploadFile (url) {
      this.imageUrl = url
      this.stopWebcam()
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
        if (this.isMain) {
            this.$router.push({ name: 'BusinessCardsWithOption', params: {option: 'mycards'} })
        }
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
    text-align: left;
}
.new-bcard-info-cell{
    margin: .3rem auto;
}
.recognizing-progress{
    text-transform: capitalize;
    font-family: serif;
    margin: .3rem auto;
    padding: .2rem auto;
}
.file-icon-button {
    padding: 3px 9px;
}
.bcards-icon-button {
    position: relative;
}
.bcards-icon-button:hover .tooltiptext{
    visibility: visible;
}
</style>
