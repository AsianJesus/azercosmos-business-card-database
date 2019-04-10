<template>
    <div class="new-business-card-page row">
        <div class="col-12 new-business-card-header">
            Edit Business Card
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
                            <font-awesome-icon :icon="cameraIcon"/>
                            <!--<i class="tooltiptext">
                                Webcam
                            </i>-->
                            Capture
                        </b-btn>
                        <b-btn variant="primary"
                               v-else
                               class="bcards-icon-button file-icon-button"
                               @click="streaming = false">
                            <font-awesome-icon :icon="fileImageIcon"/>
                            Upload file
                        </b-btn>
                    </div>
                    <div>
                        <b-form-input class="new-bcard-info-cell"
                                      v-model="form.name"
                                      id="name"
                                      placeholder="Name"
                                      type="text"/>
                        <b-form-input class="new-bcard-info-cell"
                                      type="text"
                                      placeholder="Company"
                                      v-model="form.company_name"/>
                        <b-form-input class="new-bcard-info-cell"
                                      type="text"
                                      placeholder="Position"
                                      v-model="form.position"/>
                        <b-form-input class="new-bcard-info-cell"
                                      type="text"
                                      placeholder="Address"
                                      v-model="form.address"/>
                        <b-form-input class="new-bcard-info-cell"
                                      type="tel"
                                      placeholder="Phone"
                                      v-model="form.mobile"/>
                        <b-form-input class="new-bcard-info-cell"
                                      type="email"
                                      placeholder="Email"
                                      v-model="form.email"/>
                        <b-form-input class="new-bcard-info-cell"
                                      type="text"
                                      placeholder="Website"
                                      v-model="form.website"/>
                        <b-form-select class="new-bcard-info-cell"
                                       v-model="form.private"
                                       :options="privacyOptions"/>
                    </div>
                    <div>
                        <b-form-textarea class="new-bcard-info-cell"
                                         placeholder="Note"
                                         v-model="form.note"/>
                    </div>
                    <div>
                        <h4>
                            Permissions
                        </h4>
                        <!--                        <user-selector placeholder="User" class="bcard-edit-search"-->
                        <!--                                       @select="addUserPermission($event)">-->

                        <!--                        </user-selector>-->
                        <label class="">Tagging</label>
                        <multiselect v-model="value" tag-placeholder="Add this as new tag"
                                     placeholder="Search user" label="name" :clear-on-select="false"
                                     :close-on-select="false"
                                     track-by="ID" :options="options" :multiple="true" :taggable="true"
                                     @remove="removeTag"
                                     @select="selectedTags"></multiselect>


                        <table class="table" v-if="form.permissions && form.permissions.length">
                            <tr>
                                <th style="border-top: 0;">User</th>
                                <th style="border-top: 0;">Read</th>
                                <th style="border-top: 0;">Edit</th>
                                <th style="border-top: 0;">Delete</th>
                                <th style="border-top: 0;"></th>
                            </tr>
                            <tr v-for="(per, index) in groupPermissions(form.permissions)" v-bind:key="index">
                                <th>{{ per.user ? per.user.NAME : per.user_id }}</th>
                                <th>
                                    <input type="checkbox" :checked="per[1]" v-if="!isUpdatingPermissions"
                                           @click="per[1] ? deletePermission(form.id, per[1])
                                                    : addPermission(form.id, per.user.ID, 1)">
                                </th>
                                <th>
                                    <input type="checkbox" :checked="per[2]" v-if="!isUpdatingPermissions"
                                           @change="per[2] ? deletePermission(form.id, per[2])
                                                : addPermission(form.id, per.user.ID, 2)">
                                </th>
                                <th>
                                    <input type="checkbox" :checked="per[3]" v-if="!isUpdatingPermissions"
                                           @change="per[3] ? deletePermission(form.id, per[3])
                                               : addPermission(form.id, per.user.ID, 3)">
                                </th>
                                <th>
                                    <i @click="deleteUserPermission(form.id, per.user.ID)"
                                       class="bcards-table-button bcards-icon-button"
                                       variant="danger">
                                        <font-awesome-icon :icon="trashIcon"/>
                                        <i class="tooltiptext">
                                            Delete
                                        </i>
                                    </i>
                                </th>
                            </tr>
                        </table>
                    </div>
                    <div class="new-bcard-submit">
                        <b-btn class="business-card-submit" variant="success" @click="send">
                            <font-awesome-icon :icon="plusIcon"/>
                            Edit
                        </b-btn>
                    </div>
                </div>
                <div class="col-6">
                    <!--                    <div>-->
                    <!--                        {{ recognizing.recognizedText }}-->
                    <!--                    </div>-->
                    <clipper-basic ref="clipper" class="my-clipper" :src="cropUrl">
                        <div class="placeholder" slot="placeholder">No image</div>
                    </clipper-basic>

                    <webcam ref="webcam"
                            :height="500"
                            :width="750"
                            v-model="streaming"/>
                    <video ref="webcam_video" v-bind:style="{display: streaming ? 'inline' : 'none'}"
                           class="new-bcard-webcam-video">

                    </video>

                    <!--                    <div class="" v-bind:style="{display: !streaming ? 'inline' : 'none'}">-->
                    <!--&lt;!&ndash;                        <img style="cursor: pointer;" @click="showCrop = true" v-if="!imageUrl" src="@/assets/image.png"&ndash;&gt;-->
                    <!--&lt;!&ndash;                             height="400px" id="default-image" alt="">&ndash;&gt;-->
                    <!--                    </div>-->
                    <div style="margin-top: 10px;" class="row">
                        <div class=" bcard-centerizer"
                             style="text-align: right;">
                            <b-btn @click="selectWebcam"
                                   v-if="streaming"
                                   class="bcards-icon-button"
                                   variant="success">
                                <font-awesome-icon :icon="cameraIcon"/>
                                Capture
                            </b-btn>
                            <b-btn variant="success" class="bcards-icon-button" v-if="cropUrl" @click="getResult">Crop
                            </b-btn>

                            <!--                            <b-btn variant="primary" v-else-->
                            <!--                                   class="bcards-icon-button file-icon-button"-->
                            <!--                                   @click="upload($event)">-->
                            <!--                                <font-awesome-icon :icon="fileImageIcon"/>-->
                            <!--                                Upload file-->
                            <!--                            </b-btn>-->

                        </div>
                    </div>
                    <div style="margin-top: 10px;">
                        <img :src="imageUrl"
                             alt="Card image"
                             class="new-bcard-image" v-if="imageUrl">

                    </div>

                    <picture-input
                            ref="pictureInput"
                            width="500"
                            height="250"
                            margin="16"
                            accept="image/jpeg,image/png"
                            size="10"
                            v-if="!streaming"
                            button-class="btn"
                            :custom-strings="{
                            upload: '<h1>Bummer!</h1>',
                            drag: 'Drag a image or click to upload'
                          }"
                            @change="onChange">
                    </picture-input>

                    <div style="margin-top: 10px;" class="row">
                        <div class="bcard-centerizer">
                            <b-form-select :options="langOptions" v-model="selectedLang" @change="rerunRecognizing"/>
                        </div>
                    </div>

                    <div v-if="recognizing.progress" class="recognizing-progress">
                        <h5>
                            {{ recognizing.progress.status }}
                        </h5>
                        <b-progress :value="recognizing.progress.progress"
                                    :max="1"
                                    label="Recognizing"
                                    show-progress/>
                    </div>

                    <!--                    <img id="getImageData"-->
                    <!--                         :src="'http://localhost/azercosmos-business-card-database/api/public/'+form.image_path" alt="">-->


                </div>
            </div>
        </div>
    </div>
</template>
<script>
    import Webcam from '@/components/Webcam/Webcam.vue'
    import VueImageCrop from 'vue-image-crop-upload'
    import Tesseract from 'tesseract.js'
    import {recognize} from '@/assets/js/parsingFunctions.js'
    import {
        faCamera, faFileImage, faImage, faPlus, faTrashAlt
    } from '@fortawesome/free-solid-svg-icons'
    import {clipperBasic, clipperPreview} from 'vuejs-clipper'
    import PictureInput from 'vue-picture-input'
    import UserSelector from '@/components/Tools/UserSelector.vue'
    import Multiselect from 'vue-multiselect'

    export default {
        components: {
            Webcam,
            VueImageCrop,
            clipperBasic,
            clipperPreview,
            UserSelector,
            'picture-input': PictureInput,
            Multiselect
        },
        data() {
            return {
                form: null,
                $Tesseract: null,
                privacyOptions: [
                    {
                        text: 'Please, select privacy level',
                        value: null
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
                cropUrl: '',
                isAdding: false,
                streaming: false,
                showCrop: false,
                recognizing: {
                    progress: null,
                    recognizedText: null
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
                $keyListener: null,
                isUpdatingPermissions: false,
                options: [],
                value: [],


            }
        },
        computed: {
            isMain() {
                return this.$route.name === 'EditBusinessCard'
            },
            cameraIcon: () => faCamera,
            fileImageIcon: () => faFileImage,
            imageIcon: () => faImage,
            plusIcon: () => faPlus,
            trashIcon: () => faTrashAlt,
        },
        mounted() {
            // this.startWebcam()
            this.$Tesseract = Tesseract.create({
                workerPath: this.$store.state.serverURL + 'tesseract/worker.js',
                langPath: this.$store.state.serverURL + 'tesseract/langs/',
                corePath: this.$store.state.serverURL + 'tesseract/index.js'
            })
        },
        created() {
            this.loadMe()

            // this.options
            this.axios.get('/users').then(response => {
                this.options = response.data
                this.options.forEach(function (element) {
                    element.name = element.NAME

                }.bind(this))
                console.log("sd")
                console.log(this.options)

            })
            // this.getDataUri('http://localhost/azercosmos-business-card-database/api/public/images/1554720141.png', function (dataUri) {
            //     console.log(dataUri)
            // });
            // let base64 = this.getBase64Image(document.getElementById("getImageData"));
            // console.log(base64)

        },
        methods: {
            getDataUri(url, callback) {
                var image = new Image();

                image.onload = function () {
                    var canvas = document.createElement('canvas');
                    canvas.width = this.naturalWidth; // or 'width' if you want a special/scaled size
                    canvas.height = this.naturalHeight; // or 'height' if you want a special/scaled size

                    canvas.getContext('2d').drawImage(this, 0, 0);

                    // Get raw image data
                    callback(canvas.toDataURL('image/png').replace(/^data:image\/(png|jpg);base64,/, ''));

                    // ... or get as Data URI
                    callback(canvas.toDataURL('image/png'));
                };

                image.src = url;
            },
            getBase64Image(img) {
                var canvas = document.createElement("canvas");
                canvas.width = img.width;
                canvas.height = img.height;
                var ctx = canvas.getContext("2d");
                ctx.drawImage(img, 0, 0);
                var dataURL = canvas.toDataURL("image/png");
                return dataURL.replace(/^data:image\/(png|jpg);base64,/, "");
            },
            selectedTags(user) {
                console.log(user)
                this.form.permissions.push({
                    user: user,
                    permission_id: 0,
                    business_card_id: this.form.id,
                    user_id: user.ID
                })

            },
            removeTag(user) {
                let cardId = this.form.id
                console.log(user)
                this.isUpdatingPermissions = true
                this.axios.delete(`/business-cards/${cardId}/permissions`, {
                    params: {
                        user_id: user.ID
                    }
                }).then(response => {
                    this.isUpdatingPermissions = false
                    if (this.form && this.form.id === cardId) {
                        this.form.permissions = this.form.permissions.filter(p => p.user_id !== user.ID)
                    }
                    for (let i = 0; i < this.businessCardsAll.length; i++) {
                        if (this.businessCardsAll[i].id === cardId) {
                            this.businessCardsAll[i].permissions = this.businessCardsAll[i].permissions.filter(p => p.user_id !== user.ID)
                        }
                    }
                })

            },

            loadMe() {
                let uri = `business-cards-one/${this.$route.params.id}`;

                this.axios.get(uri).then(response => {
                    console.log(response)
                    response.data[0].permissions = response.data[0].permissions ? response.data[0].permissions : []
                    response.data[0].note = response.data[0].notes.length ? response.data[0].notes[0].note : ''
                    this.form = JSON.parse(JSON.stringify(response.data[0]))

                    console.log(this.form)

                    let image = this.form.image_path

                    this.axios.get(image, {
                        responseType: 'arraybuffer'
                    })
                        .then(response => {
                            image = new Buffer(response.data, 'binary').toString('base64')
                            console.log(image)
                            this.cropUrl = "data:image/png;base64," + image
                            this.imageUrl = "data:image/png;base64," + image
                        })

                    // this.getDataUri('http://localhost/azercosmos-business-card-database/api/public/images/1554720141.png', function (dataUri) {
                    //     this.imageUrl = dataUri
                    // });

                }).catch(err => {
                    console.log(err)
                    this.isLoading = false
                })

            },
            getResult: function () {
                const canvas = this.$refs.clipper.clip();//call component's clip method
                this.imageUrl = canvas.toDataURL("image/jpg", 1);//canvas->image
                console.log(this.imageUrl)
                fetch(this.imageUrl).then(photo => photo.blob()).then(file => {
                    this.form.photo = file
                    this.recognizeImage(file)
                })

            },
            groupPermissions(permissions) {
                let result = {}
                for (let i = 0; i < permissions.length; i++) {
                    if (!result[permissions[i].user_id]) {
                        result[permissions[i].user_id] = {user: permissions[i].user}
                    }
                    result[permissions[i].user_id][permissions[i].permission_id] = permissions[i].id
                }
                return result
            },
            addUserPermission(user) {
                this.form.permissions.push({
                    user: user,
                    permission_id: 0,
                    business_card_id: this.form.id,
                    user_id: user.ID
                })
            },
            addPermission(cardId, userId, permissionId) {
                this.isUpdatingPermissions = true
                this.axios.post('/user-permissions', {
                    business_card_id: cardId,
                    user_id: userId,
                    permission_id: permissionId
                }).then(response => {
                    this.isUpdatingPermissions = false
                    if (this.form && this.form.id === cardId) {
                        this.form.permissions.push(response.data)
                    }
                    for (let i = 0; i < this.businessCardsAll.length; i++) {
                        if (this.businessCardsAll[i].id === cardId) {
                            this.businessCardsAll[i].permissions.push(response.data)
                        }
                    }
                }).catch(err => {
                    this.isUpdatingPermissions = false
                })
            },
            deleteUserPermission(cardId, userID) {
                this.isUpdatingPermissions = true
                this.axios.delete(`/business-cards/${cardId}/permissions`, {
                    params: {
                        user_id: userID
                    }
                }).then(response => {
                    this.isUpdatingPermissions = false
                    if (this.form && this.form.id === cardId) {
                        this.form.permissions = this.form.permissions.filter(p => p.user_id !== userID)
                    }
                    for (let i = 0; i < this.businessCardsAll.length; i++) {
                        if (this.businessCardsAll[i].id === cardId) {
                            this.businessCardsAll[i].permissions = this.businessCardsAll[i].permissions.filter(p => p.user_id !== userID)
                        }
                    }
                })
            },
            deletePermission(cardId, permissionId) {
                this.isUpdatingPermissions = true
                this.axios.delete('/user-permissions/' + permissionId).then(response => {
                    // for (let i = 0; i < this.businessCardsAll.length; i++) {
                    //     if (this.businessCardsAll[i].id === cardId) {
                    //         this.businessCardsAll[i].permissions = this.businessCardsAll[i].permissions.filter(x => x.id === permissionId)
                    //     }
                    // }
                    if (this.form && this.form.id === cardId) {
                        this.form.permissions = this.form.permissions.filter(x => x.id !== permissionId)
                    }
                    this.isUpdatingPermissions = false
                }).catch(err => {
                    console.log(err)
                    this.isUpdatingPermissions = false
                })
            },

            upload: function (e) {
                if (e.target.files.length !== 0) {
                    if (this.cropUrl) URL.revokeObjectURL(this.cropUrl)
                    this.cropUrl = window.URL.createObjectURL(e.target.files[0]);
                }
            },
            onChange(image) {
                console.log('New picture selected!')
                if (image) {
                    console.log('Picture loaded.')
                    this.cropUrl = image
                } else {
                    console.log('FileReader API not supported: use the <form>, Luke!')
                }
                fetch(this.cropUrl).then(photo => photo.blob()).then(file => {
                    this.form.photo = file
                    this.recognizeImage(file)
                })

            },
            selectWebcam() {
                let image = this.$refs.webcam.takePicture()
                fetch(image).then(photo => photo.blob()).then(photo => {
                    console.log(photo)
                    this.$refs.imageCrop.setSourceImg(photo)

                })
                this.cropUrl = image
                // this.stopWebcam()
            },
            startWebcam() {
                this.streaming = true
                this.$refs.webcam.startVideo(stream => {
                    this.$refs.webcam_video.srcObject = stream
                    this.$refs.webcam_video.play()
                })
                if (!this.$keyListener) {
                    document.addEventListener('keydown', this.takePhotoAtSpacebar)
                }
            },
            stopWebcam() {
                this.$refs.webcam.stopVideo()
                document.removeEventListener('keydown', this.$keyListener)
                this.$keyListener = null
            },
            takePhotoAtSpacebar(event) {
                if (this.streaming && event.keyCode === 32) {
                    this.selectWebcam()
                }
            },
            uploadFile(url) {
                this.imageUrl = url
                this.stopWebcam()
                fetch(this.imageUrl).then(photo => photo.blob()).then(file => {
                    this.form.photo = file
                    this.recognizeImage(file)
                })
            },
            rerunRecognizing() {
                if (this.form.photo) {
                    this.recognizeImage(this.form.photo)
                }
            },
            send() {
                if (this.isAdding) return
                this.isAdding = true
                let form = new FormData(this)
                for (let key in this.form) {
                    form.set(key, this.form[key])
                }
                form.append('photo', this.form.photo)
                console.log(form)
                for (let p of form) {
                    console.log(p);
                }

                console.log(this.form.photo)
                this.axios.post('/business-cards/' + this.form.id, form).then(response => {
                    console.log(response.data)
                    this.isAdding = false
                    if (this.isMain) {
                        this.$router.push({name: 'BusinessCardsWithOption', params: {option: 'mycards'}})
                    }
                    this.$emit('newCard', response.data)
                }).catch(err => {
                    console.log(err)
                    this.isAdding = false
                    alert('Error!')
                })
                console.log(this.form)
            },
            recognizeImage(image) {
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
            autoFillFields() {
                this.form.name = recognize(this.recognizing.recognizedText, 'name', this.selectedLang)
                this.form.mobile = recognize(this.recognizing.recognizedText, 'phone', this.selectedLang)
                this.form.email = recognize(this.recognizing.recognizedText, 'email', this.selectedLang)
                this.form.website = recognize(this.recognizing.recognizedText, 'website', this.selectedLang)
            }
        }
    }
</script>
<style>
    .placeholder {
        text-align: center;
        padding: 20px;
        background-color: lightgray;
    }

    .my-clipper {
        width: 100%;
        max-width: 700px;
    }

    .new-business-card-page {
        width: 100%;
        padding: 1rem;
    }

    .new-business-card-header {
        font-size: 1.9rem;
        font-family: Segoe UI, Frutiger, Frutiger Linotype, Dejavu Sans, Helvetica Neue, Arial, sans-serif;
        font-weight: bolder;
    }

    .new-bcard-webcam-video {
        width: 100%;
        border-radius: 1rem;
        /*height: 380px;*/
        margin-top: 15px;
    }

    .new-bcard-image {
        max-width: 100%;
        width: 480px;
        height: auto;
        border-radius: 10px;
    }

    .new-bcard-submit {
        margin: 1rem auto;
    }

    .new-bcard-webcam-buttons {
        margin: .5rem auto;
        text-align: left;
    }

    .new-bcard-info-cell {
        margin: .3rem auto;
    }

    .recognizing-progress {
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

    .bcards-icon-button:hover .tooltiptext {
        visibility: visible;
    }

    .bcard-centerizer {
        text-align: center;
        display: table;
        margin: 0 auto;
    }
</style>
