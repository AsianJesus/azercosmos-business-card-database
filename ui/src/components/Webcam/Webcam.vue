<template>
    <div class="webcam-component">
        <canvas ref="canvas"
                style="display: none;" />
        <video ref="video"
               v-bind:srcObject="stream"
               style="display: none;" />
    </div>
</template>
<script>
export default{
  props: {
    width: {
      type: Number,
      default: 720
    },
    height: {
      type: Number,
      default: 960
    },
    value: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      imageUrl: '',
      streaming: false,
      stream: null,
      video: null,
      canvas: null,
      photo: null,
      startbutton: null
    }
  },
  destroyed () {
    this.stopVideo()
  },
  methods: {
    startVideo (callback) {
      if (this.streaming) return
      navigator.mediaDevices.getUserMedia({ video: {}}).then(stream => {
        this.streaming = true
        this.stream = stream
        this.$refs.video.srcObject = stream
        this.$refs.video.play()
        this.$emit('input', this.streaming)
        callback(stream)
      }).catch(err => {
        this.streaming = false
        this.$emit('input', this.streaming)
      })
    },
    stopVideo () {
      console.log(this.stream)
      if (!this.stream) return
      var tracks = this.stream.getTracks()
      for (let i  = 0; i < tracks.length; i++) {
        tracks[i].stop()
      }
      this.streaming = false
      this.$emit('input', this.streaming)
    },
    takePicture () {
      if (!this.streaming) {
        this.startVideo()
      }
      let context = this.$refs.canvas.getContext('2d')
      this.$refs.canvas.width = 750
      this.$refs.canvas.height = 520
      context.drawImage(this.$refs.video, 0, 0, this.width, this.height)
      this.imageUrl = this.$refs.canvas.toDataURL('image/jpeg')
      return this.imageUrl
    }
  }
}
</script>
<style>

</style>
