<template>
    <div class="business-cards-page">
        <!--<div v-if="isLoading">
            <h2>
                Content is loading..
                Please be patient
            </h2>
        </div>-->
        <div>
            <transition name="bcard-show">
                <div class="bcard-show-holder" v-if="cardToShow" @click="hideCard">
                    <div class="bcard-show-info-holder"
                         @click="$event.stopPropagation()">
                        <div class="bcard-show-form-save">
                            <span class="icon"
                                  @click="saveAsForm"
                            >
                                Save
                            </span>
                        </div>
                        <div class="bcard-show-form"
                             ref="showForm"
                        >
                            <div class="row bcard-show-name-holder"
                                 style="margin: auto 0 !important;"
                            >
                            <span class="col-12 bcard-show-name">
                                {{ cardToShow.name }}
                            </span>
                            </div>
                            <div class="row bcard-show-company-holder"
                                 style="margin: auto 0 !important;"
                            >
                            <span class="col-12 bcard-show-company">
                                {{ cardToShow.company_name }}
                            </span>
                            </div>
                            <div class="row bcard-show-positon-holder"
                                 style="margin: auto 0 !important;"
                            >
                            <span class="col-12 bcard-show-position">
                                {{ cardToShow.position }}
                            </span>
                            </div>
                            <div class="row bcard-show-icons-holder"
                                 style="margin: auto 0 !important;"
                            >
                                <div class="col-4 bcard-show-icon-holder">
                                    <img src='@/assets/icons/location.png' width="42px">
                                </div>
                                <div class="col-4 bcard-show-icon-holder">
                                    <img src='@/assets/icons/phone.png' width="42px">
                                </div>
                                <div class="col-4 bcard-show-icon-holder">
                                    <img src='@/assets/icons/web.png' width="42px">
                                </div>
                            </div>
                            <div class="row bcard-show-additional-info-holder"
                                 style="margin: auto 0 !important;"
                            >
                                <div class="col-4 bcard-show-address">
                                    {{ cardToShow.address }}
                                </div>
                                <div class="col-4 bcard-show-mobile">
                                    {{ cardToShow.mobile }}
                                </div>
                                <div class="col-4 bcard-show-website">
                                    {{ cardToShow.website }}
                                </div>
                            </div>
                            <!--                            <img src="@/assets/icons/bottom line.png" class="row bcard-show-bottom-line"-->
                            <!--                                 style="margin: auto 0">-->
                        </div>
                        <div class="bcard-note-header"
                             v-if="cardToShow.note">
                            Note
                        </div>
                        <div class="bcard-note-body"
                             v-if="cardToShow.note">
                            {{ cardToShow.note }}
                        </div>
                    </div>
                </div>
            </transition>
            <transition name="bcard-edit">
                <div class="bcard-show-holder"
                     v-if="cardToEdit"
                     @click="cancelEditing(false)">
                    <div class="bcard-edit-holder row" @click="$event.stopPropagation()">
                        <div class="col-12">
                            <h2>Edit card #{{cardToEdit.id}}</h2>
                        </div>
                        <div class="col-6">
                            <b-form-input type="text" class="bcard-edit-card-input" v-model="cardToEdit.name"
                                          placeholder="Name"></b-form-input>
                            <b-form-input type="text" class="bcard-edit-card-input" v-model="cardToEdit.company_name"
                                          placeholder="Company"></b-form-input>
                            <b-form-input type="text" class="bcard-edit-card-input" v-model="cardToEdit.position"
                                          placeholder="Position"></b-form-input>
                            <b-form-input type="text" class="bcard-edit-card-input" v-model="cardToEdit.address"
                                          placeholder="Address"></b-form-input>
                            <b-form-input type="tel" class="bcard-edit-card-input" v-model="cardToEdit.mobile"
                                          placeholder="Phone"></b-form-input>
                            <b-form-input type="email" class="bcard-edit-card-input" v-model="cardToEdit.email"
                                          placeholder="Email"></b-form-input>
                            <b-form-input type="text" class="bcard-edit-card-input" v-model="cardToEdit.website"
                                          placeholder="Website"></b-form-input>
                            <b-form-input type="text" class="bcard-edit-card-input" v-model="cardToEdit.note"
                                          placeholder="Note"></b-form-input>
                            <b-form-select v-model="cardToEdit.private"
                                           :options="privacyOptions">

                            </b-form-select>
                        </div>
                        <div class="col-6">
                            <h4>
                                Permissions
                            </h4>
                            <user-selector placeholder="User" class="bcard-edit-search"
                                           @select="addUserPermission($event)">

                            </user-selector>
                            <table class="table" v-if="cardToEdit.permissions && cardToEdit.permissions.length">
                                <tr>
                                    <th style="border-top: 0;">User</th>
                                    <th style="border-top: 0;">Read</th>
                                    <th style="border-top: 0;">Edit</th>
                                    <th style="border-top: 0;">Delete</th>
                                    <th style="border-top: 0;"></th>
                                </tr>
                                <tr v-for="(per, index) in groupPermissions(cardToEdit.permissions)" v-bind:key="index">
                                    <th>{{ per.user ? per.user.NAME : per.user_id }}</th>
                                    <th>
                                        <input type="checkbox" :checked="per[1]" v-if="!isUpdatingPermissions"
                                               @click="per[1] ? deletePermission(cardToEdit.id, per[1])
                                                    : addPermission(cardToEdit.id, per.user.ID, 1)">
                                    </th>
                                    <th>
                                        <input type="checkbox" :checked="per[2]" v-if="!isUpdatingPermissions"
                                               @change="per[2] ? deletePermission(cardToEdit.id, per[2])
                                                : addPermission(cardToEdit.id, per.user.ID, 2)">
                                    </th>
                                    <th>
                                        <input type="checkbox" :checked="per[3]" v-if="!isUpdatingPermissions"
                                               @change="per[3] ? deletePermission(cardToEdit.id, per[3])
                                               : addPermission(cardToEdit.id, per.user.ID, 3)">
                                    </th>
                                    <th>
                                        <i @click="deleteUserPermission(cardToEdit.id, per.user.ID)"
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
                        <div class="bcards-edit-buttons col-12">
                            <b-btn @click="saveChanges"
                                   class="bcards-icon-button g-wide-button"
                                   variant="success">
                                <font-awesome-icon :icon="saveIcon"/>
                            </b-btn>
                            <b-btn @click="cancelEditing(true)"
                                   class="bcards-icon-button g-standard-button"
                                   variant="danger">
                                <font-awesome-icon :icon="banIcon"/>
                            </b-btn>
                        </div>
                    </div>
                </div>
            </transition>
            <transition name="bcard-new">
                <div class="bcard-show-holder"
                     v-if="createNewCard"
                     @click="cancelCreating()">
                    <div class="bcard-new-card-holder" @click="$event.stopPropagation()">
                        <new-business-card @newCard="addCard">

                        </new-business-card>
                    </div>
                </div>
            </transition>
            <transition name="bcard-source-image">
                <div class="bcard-show-holder"
                     v-if="cardToShowSourceImage"
                     @click="cardToShowSourceImage = null">
                    <div class="bcard-source-image-holder" >
                        <div class="bcard-show-info-holder" @click="$event.stopPropagation()">
                            <div class="bcard-show-form-save">
                            <span class="icon"
                                  @click="saveImage($store.state.serverURL + '/' + cardToShowSourceImage.image_path)"
                            >
                                Save
                            </span>
                            </div>
                            <img :src="$store.state.serverURL + '/' + cardToShowSourceImage.image_path"
                                 class="bcard-source-image">
                            <div class="bcard-note-header"
                                 v-if="cardToShowSourceImage.note">
                                Note
                            </div>
                            <div class="bcard-note-body"
                                 v-if="cardToShowSourceImage.note">
                                {{ cardToShowSourceImage.note }}
                            </div>
                        </div>
                    </div>
                </div>
            </transition>
            <div class="bcards-filter row no-gutters">
                <div class="bcards-filter-input col-6">
                    <b-btn :size="'sm'"
                           class="bcards-icon-button bcards-filter-search-button"
                           :variant="'secondary'">
                        <!--
                                                <img @click="filterCards" src="@/assets/icons/search.png" width="16px" class="bcards-filter-search">
                        -->
                        <font-awesome-icon :icon="searchIcon"/>
                    </b-btn>
                    <input type="text"
                           v-model="filters.name"
                           @input="delayedFilter"
                           v-if="selectedFilter === 1">
                    <input type="text"
                           v-model="filters.company_name"
                           @input="delayedFilter"
                           v-if="selectedFilter === 2">
                    <input type="text"
                           v-model="filters.position"
                           @input="delayedFilter"
                           v-if="selectedFilter === 3">
                    <input type="text"
                           v-model="filters.email"
                           @input="delayedFilter"
                           v-if="selectedFilter === 4">
                    <input type="text"
                           v-model="filters.mobile"
                           @input="delayedFilter"
                           v-if="selectedFilter === 5">
                    <input type="text"
                           v-model="filters.address"
                           @input="delayedFilter"
                           v-if="selectedFilter === 6">
                    <input type="text"
                           v-model="filters.website"
                           @input="delayedFilter"
                           v-if="selectedFilter === 7">
                    <input type="text"
                           v-model="filters.note"
                           @input="delayedFilter"
                           v-if="selectedFilter === 8">
                    <select v-model="selectedFilter" style="height: 100%;">
                        <option :value="1">
                            Name
                        </option>
                        <option :value="2">
                            Company
                        </option>
                        <option :value="3">
                            Position
                        </option>
                        <option :value="4">
                            Email
                        </option>
                        <option :value="5">
                            Phone
                        </option>
                        <option :value="6">
                            Address
                        </option>
                        <option :value="7">
                            Website
                        </option>
                        <option :value="8">
                            Note
                        </option>
                    </select>
                </div>
                <div class="col" style="text-align: right;">
                    <b-btn @click="showColumns ^= true"
                           class="bcards-icon-button g-standard-button"
                           variant="primary">
                        <font-awesome-icon :icon="filterIcon"/>
                    </b-btn>
                    <transition name="columns">
                        <div class="columns-holder" v-if="showColumns">
                            <columns-list v-model="columnsToShow" @input="saveConfig"/>
                        </div>
                    </transition>
                    <b-btn @click="redirectToNewCard"
                           class="bcards-icon-button g-wide-button"
                           variant="success">
                        <font-awesome-icon :icon="plusIcon"/>
                    </b-btn>
                </div>
                <div class="col-12">
                    <div v-for="(value, key) in filters" v-bind:key="key" v-if="value"
                         class="bcard-filter-selected-item" @click="$set(filters, key, null); delayedFilter()">
                        {{ formatFilterName(key) }} - {{ value }}
                        <img src="@/assets/icons/delete_filter.png"
                             class="bcards-icon-button"
                             width="10px">
                    </div>
                </div>
            </div>
            <div class="bcards-table-holder">
                <table class="table table-bordered bcards-table">
                    <tr class="bcards-table-header">
                        <th @click="sortBy('id')" class="bcards-info-header" v-if="columnsToShow.id">
                            ID
                            <font-awesome-icon v-if="sorting.by === 'id' && sorting.asc"
                                               :icon="arrowUp"/>
                            <font-awesome-icon v-if="sorting.by === 'id' && !sorting.asc"
                                               :icon="arrowDown"/>
                        </th>
                        <th scope="col"
                            class="bcards-info-header"
                            @click="sortBy('name')"
                            v-if="columnsToShow.name">
                            Name
                            <font-awesome-icon v-if="sorting.by === 'name' && sorting.asc"
                                               :icon="arrowUp"/>
                            <font-awesome-icon v-if="sorting.by === 'name' && !sorting.asc"
                                               :icon="arrowDown"/>
                        </th>
                        <th @click="sortBy('company_name')"
                            class="bcards-info-header"
                            v-if="columnsToShow.company_name">
                            Company
                            <font-awesome-icon v-if="sorting.by === 'company_name' && sorting.asc"
                                               :icon="arrowUp"/>
                            <font-awesome-icon v-if="sorting.by === 'company_name' && !sorting.asc"
                                               :icon="arrowDown"/>
                        </th>
                        <th @click="sortBy('position')"
                            class="bcards-info-header"
                            v-if="columnsToShow.position">
                            Position
                            <font-awesome-icon v-if="sorting.by === 'position' && sorting.asc"
                                               :icon="arrowUp"/>
                            <font-awesome-icon v-if="sorting.by === 'position' && !sorting.asc"
                                               :icon="arrowDown"/>
                        </th>
                        <th @click="sortBy('email')"
                            class="bcards-info-header"
                            v-if="columnsToShow.email">
                            Email
                            <font-awesome-icon v-if="sorting.by === 'email' && sorting.asc"
                                               :icon="arrowUp"/>
                            <font-awesome-icon v-if="sorting.by === 'email' && !sorting.asc"
                                               :icon="arrowDown"/>
                        </th>
                        <th @click="sortBy('mobile')"
                            class="bcards-info-header"
                            v-if="columnsToShow.mobile">
                            Phone
                            <font-awesome-icon v-if="sorting.by === 'mobile' && sorting.asc"
                                               :icon="arrowUp"/>
                            <font-awesome-icon v-if="sorting.by === 'mobile' && !sorting.asc"
                                               :icon="arrowDown"/>
                        </th>
                        <th @click="sortBy('address')"
                            class="bcards-info-header"
                            v-if="columnsToShow.address">
                            Address
                            <font-awesome-icon v-if="sorting.by === 'address' && sorting.asc"
                                               :icon="arrowUp"/>
                            <font-awesome-icon v-if="sorting.by === 'address' && !sorting.asc"
                                               :icon="arrowDown"/>
                        </th>
                        <th @click="sortBy('website')"
                            class="bcards-info-header"
                            v-if="columnsToShow.website">
                            Website
                            <font-awesome-icon v-if="sorting.by === 'website' && sorting.asc"
                                               :icon="arrowUp"/>
                            <font-awesome-icon v-if="sorting.by === 'website' && !sorting.asc"
                                               :icon="arrowDown"/>
                        </th>
                        <th>Controls</th>
                    </tr>
                    <tr v-for="(bcard, index) in businessCardsToShow"
                        v-bind:key="index"
                        class="bcards-table-element"
                        @click="showCard(index)">
                        <td v-if="columnsToShow.id"> {{ bcard.id }}</td>
                        <td v-if="columnsToShow.name">{{ bcard.name }}</td>
                        <td v-if="columnsToShow.company_name">{{ bcard.company_name }}</td>
                        <td v-if="columnsToShow.position">{{ bcard.position }}</td>
                        <td v-if="columnsToShow.email">{{ bcard.email }}</td>
                        <td v-if="columnsToShow.mobile">{{ bcard.mobile }}</td>
                        <td v-if="columnsToShow.address">{{ bcard.address }}</td>
                        <td v-if="columnsToShow.website">{{ bcard.website }}</td>
                        <td class="bcards-table-actions"
                            @click="$event.stopPropagation()">
                            <router-link :to="{name: 'EditBusinessCard', params: {id: bcard.id}}">
                                <b-btn
                                        class="bcards-table-button g-edit-button bcards-icon-button"
                                >
                                    <font-awesome-icon :icon="editIcon"/>
                                </b-btn>

                            </router-link>
                            <b-btn @click="deleteCard(bcard.id)"
                                   class="bcards-table-button bcards-icon-button g-delete-button"
                                   v-if="deletable(index)"
                                   variant="danger">
                                <font-awesome-icon :icon="trashIcon"/>
                                <!--<i class="tooltiptext">
                                    Delete
                                </i>-->
                            </b-btn>
                        </td>
                    </tr>
                </table>
            </div>
            <b-btn @click="showMore"
                   v-if="showCardsCount < businessCardsFiltered.length"
                   class="bcards-show-more-button g-wide-button" variant="outline-primary">
                <v-icon name="arrow-down"/>
            </b-btn>
            <div style="display: block;border-radius:0 !important;float: right;margin-top: 10px; text-align:right;">

                <b-btn  variant="primary"
                        class=" g-wide-button"
                        @click="openPasswordPrompt">
                    App password
                </b-btn>
                <b-btn @click="exportedExcel"
                       class=" g-wide-button"
                       variant="success"
                >
                    Excel
                </b-btn>
            </div>
            <!-- This link is required to download images and dom objects as jpeg files -->
            <a ref="imageToDownload"
               href="#"
               style="display: none;"
               target="_blank"
               download="true"
            />
        </div>
    </div>
</template>
<script>
    import ColumnsList from '@/components/Tools/ColumnsList.vue'
    import NewBusinessCard from '@/components/Business cards/NewBusinessCard.vue'
    import UserSelector from '@/components/Tools/UserSelector.vue'
    import lodash from 'lodash'
    import domToImage from 'dom-to-image'
    import exportFromJSON from 'export-from-json'

    import {
        faEdit,
        faTrashAlt,
        faCaretDown,
        faCaretUp,
        faFilter,
        faPlus,
        faSearch,
        faSave,
        faBan,
        faFileExcel
    } from '@fortawesome/free-solid-svg-icons'

    const cardsOnPage = 15
    const availableLoadOptions = {
        'mycards': {
            'created_by': 'my_id'
        },
        'public': {
            'private': false
        },
        'private': {
            'private': true
        }
    }
    export default {
        components: {
            ColumnsList,
            NewBusinessCard,
            UserSelector
        },
        data() {
            return {
                businessCardsAll: [],
                sorting: {
                    by: 'id',
                    asc: false
                },
                filters: {},
                selectedFilter: 1,
                columnsToShow: {
                    id: true,
                    name: true,
                    company_name: true,
                    position: true,
                    mobile: true,
                    address: false,
                    email: true,
                    website: false
                },
                customNames: {
                  mobile: 'Phone'
                },
                privacyOptions: [
                    {
                        text: 'Public',
                        value: 0
                    },
                    {
                        text: 'Private',
                        value: 1
                    },
                ],
                showCardsCount: cardsOnPage,
                isLoading: false,
                isUpdatingPermissions: false,
                cardToEdit: null,
                cardToShow: null,
                showColumns: false,
                createNewCard: false,
                cardToShowSourceImage: null,
                appPassword: {
                    key: null,
                    loaded: false
                }
            }
        },
        watch: {
            columnsToShow() {
                this.saveConfig()
            },
            option() {
                this.load()
            }
        },
        computed: {
            userID() {
                return this.$store.getters.userId
            },
            loadOptions() {
                let options = availableLoadOptions[this.option] || null
                if (options && options['created_by'] && options['created_by'] === 'my_id') {
                    options['created_by'] = this.userId
                }
                return options
            },
            option() {
                return this.$route.params.option
            },
            businessCardsToShow() {
                return this.businessCardsFiltered.slice(0, this.showCardsCount)
            },
            businessCardsFiltered() {
                return this.filterCards().sort((a, b) =>
                    (this.sorting.asc ? 1 : -1) * (a[this.sorting.by] ? ((typeof a[this.sorting.by] === 'string') ?
                    a[this.sorting.by].localeCompare(b[this.sorting.by]) : a[this.sorting.by] - b[this.sorting.by])
                    : 0));
            },
            editIcon: () => faEdit,
            trashIcon: () => faTrashAlt,
            arrowDown: () => faCaretDown,
            arrowUp: () => faCaretUp,
            filterIcon: () => faFilter,
            plusIcon: () => faPlus,
            searchIcon: () => faSearch,
            saveIcon: () => faSave,
            banIcon: () => faBan,
            excelIcon: () => faFileExcel,
        },
        created() {
            if (!this.$route.params.option) {
                this.$router.push({name: 'BusinessCardsWithOption', params: {option: 'mycards'}})
            }

        },
        mounted() {
            this.load()
            this.getConfig()
        },
        methods: {
            formatFilterName(name) {
                // return name.replace(/[-_]/g, ' ')
                return (this.customNames[name] || name).replace(/[-_]/g, ' ')
            },
            saveAsForm () {
                console.log(this.$refs.showForm)
                domToImage.toJpeg(this.$refs.showForm, {
                    quality: .96
                }).then(dataUrl => {
                    this.saveImage(dataUrl)
                })
            },
            saveImage (url) {
                this.$refs.imageToDownload.href = url
                this.$refs.imageToDownload.click()
            },
            exportedExcel() {
                this.axios.get('business-cards-excel', {
                    params: {
                        filters: this.loadOptions
                    }
                }).then(response => {
                        let data = response.data;
                        let fileName = 'download'
                        let exportType = 'csv'
                        delete data.notes
                        data.forEach(function (entry) {
                            entry.note = entry.notes.length ? entry.notes[0].note : ''
                            delete entry.notes
                            delete entry.permissions
                            delete entry.deleted_by
                            delete entry.deleted_at
                            delete entry.updated_at
                            delete entry.image_path
                        });

                        for (var f in data) {
                            let check = this.columnsToShow
                            Object.keys(data[f]).forEach(function (key) {
                                if (check.hasOwnProperty(key) === false || check[key] !== true) {
                                    // console.log(key)
                                    delete data[f][key]
                                }
                            });
                        }

                        exportFromJSON({data, fileName, exportType})

                        // this.isLoading = false
                    }
                ).catch(err => {
                    console.log(err)
                    this.isLoading = false
                })

            },
            load() {
                this.isLoading = true
                this.axios.get('business-cards', {
                    params: {
                        filters: this.loadOptions
                    }
                }).then(response => {
                    this.businessCardsAll = response.data.map(b => {
                        b.note = b.notes.length ? b.notes[0].note : ''
                        return b
                    })
                    this.isLoading = false
                }).catch(err => {
                    console.log(err)
                    this.isLoading = false
                })
            },
            editable(index) {
                return this.businessCardsToShow[index].created_by.toString() === this.$store.getters.userId.toString() ||
                    this.businessCardsToShow[index].permissions.filter(x => x.permission_id === 2).length > 0
            },
            deletable(index) {
                return this.businessCardsToShow[index].created_by.toString() === this.$store.getters.userId.toString() ||
                    this.businessCardsToShow[index].permissions.filter(x => x.permission_id === 3).length > 0
            },
            editCard(index) {
                this.cardToEdit = JSON.parse(JSON.stringify(this.businessCardsToShow[index]))
            },
            deleteCard(id) {
                if (!confirm('Are you sure?')) return
                this.axios.delete('/business-cards/' + id).then(response => {
                    if (response.data) {
                        this.businessCardsAll = this.businessCardsAll.filter(x => x.id !== id)
                    } else {
                        alert('Failed to delete')
                    }
                }).catch(err => {
                    console.log(err)
                    alert('Couldn\'t delete due to server trouble')
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
                this.cardToEdit.permissions.push({
                    user: user,
                    permission_id: 0,
                    business_card_id: this.cardToEdit.id,
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
                    if (this.cardToEdit && this.cardToEdit.id === cardId) {
                        this.cardToEdit.permissions.push(response.data)
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
                    if (this.cardToEdit && this.cardToEdit.id === cardId) {
                        this.cardToEdit.permissions = this.cardToEdit.permissions.filter(p => p.user_id !== userID)
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
                    for (let i = 0; i < this.businessCardsAll.length; i++) {
                        if (this.businessCardsAll[i].id === cardId) {
                            this.businessCardsAll[i].permissions = this.businessCardsAll[i].permissions.filter(x => x.id === permissionId)
                        }
                    }
                    if (this.cardToEdit && this.cardToEdit.id === cardId) {
                        this.cardToEdit.permissions = this.cardToEdit.permissions.filter(x => x.id !== permissionId)
                    }
                    this.isUpdatingPermissions = false
                }).catch(err => {
                    console.log(err)
                    this.isUpdatingPermissions = false
                })
            },
            cancelEditing(force = false) {
                if (force || confirm('Are you sure?')) {
                    this.cardToEdit = null
                }
            },
            saveChanges() {

                if (this.cardToEdit.notes.length > 0)
                    this.cardToEdit.notes[0].note = this.cardToEdit.note;
                console.log(this.cardToEdit)
                this.axios.put('/business-cards/' + this.cardToEdit.id, this.cardToEdit).then(response => {
                    for (let i = 0; i < this.businessCardsAll.length; i++) {
                        if (this.businessCardsAll[i].id === response.data.id) {
                            for (let key in response.data) {
                                this.$set(this.businessCardsAll[i], key, response.data[key])
                            }
                        }
                    }
                    this.load();

                }).catch(err => {
                    console.log(err)
                })
                this.cardToEdit = null
            },
            showCard(index) {
                if (this.businessCardsToShow[index].image_path) {
                    this.showSourceImage(index)
                } else {
                    this.cardToShow = this.businessCardsToShow[index]
                }
            },
            hideCard() {
                this.cardToShow = null
            },
            sortBy(field) {
                if (field) {
                    if (this.sorting.by === field) {
                        this.sorting.asc ^= true
                    } else {
                        this.sorting.by = field
                        this.sorting.asc = true
                    }
                }
            },
            filterCards() {
                return this.businessCardsAll.filter(card => {
                    for (let param in this.filters) {
                        if (this.filters.hasOwnProperty(param) && this.filters[param]) {
                            if (!card[param]
                                    || !card[param].toString().toLowerCase().includes(this.filters[param].toString().toLowerCase())) {
                                return false
                            }
                        }
                    }
                    return true
                })
            },
            delayedFilter: lodash.debounce(function () {
                this.filterCards()
            }, 500, 1500),
            cancelCreating(force = false) {
                if (force || confirm('Are you sure?')) {
                    this.createNewCard = false
                }
            },
            addCard(card) {
                console.log(card)
                card.permissions = []
                this.businessCardsAll.push(card)
                this.filterCards()
                this.sortBy()
                this.createNewCard = false
            },
            saveConfig() {
                this.$cookie.set('columns-config', JSON.stringify(this.columnsToShow), 30)
            },
            getConfig() {
                let saved = this.$cookie.get('columns-config')
                if (saved) {
                    this.columnsToShow = JSON.parse(saved)
                }
            },
            showSourceImage(index) {
                this.cardToShowSourceImage = this.businessCardsToShow[index]
            },
            showMore() {
                this.showCardsCount += cardsOnPage
            },
            redirectToNewCard() {
                this.$router.push({name: 'NewBusinessCard'})
            },
            openPasswordPrompt () {
              if (!this.appPassword.loaded) {
                  this.axios.get('/user/passwords').then(response => {
                      this.appPassword.loaded = true
                      this.appPassword.key = response.data || null
                      this.showPrompt()
                  })
              } else {
                  this.showPrompt()
              }
            },
            showPrompt (){
                let text = this.appPassword.key !== null ? `Current password: ${this.appPassword.key}` : 'Password is not set'
                let pass = prompt(text)
                if (pass) {
                     this.savePassword(pass)
                }
            },
            savePassword (password) {
                return this.axios.post('/passwords', {
                  password: password
                }).then(response => {
                  this.appPassword.key = response.data.password
                })
            }
        }
    }
</script>
<style>
    .business-cards-page {
        margin-bottom: 3rem;
    }

    .arrow-buttons {
        cursor: pointer;
    }

    .bcard-edit {
        width: 100%;
    }

    .bcard-show-holder {
        position: fixed;
        left: 0;
        right: 0;
        top: 0;
        z-index: 5;
        /*width: 1000px;*/
        height: 100%;
        /*overflow: auto;*/
    }

    .bcard-edit-card-input {
        margin: .4rem auto;
    }

    .bcard-edit-holder {
        background-color: #fff;
        margin: 5rem 10rem 0;
        border-radius: 5px;
        border: 1px solid #c8c8c8;
        padding: 1rem;
        position: relative;
        font-style: italic;
        overflow-y: auto;
        width: 70%;
        /* text-align: left; */
        box-shadow: 0 1px 2px 2px #00000030;
        margin-left: 15% !important;
    }

    .bcard-edit-holder input {
        width: 100%;
        text-align: left;
        border-radius: 10px;
    }

    .bcards-edit-buttons {
        margin: .4rem auto;
        text-align: left;
    }

    .bcards-info-header {
        cursor: pointer;
    }

    .
    .bcard-new-card-holder {
        background-color: white;
        margin: 5rem 4rem;
        overflow-y: auto;
        border-radius: 1rem;
        box-shadow: 0 0 4px 8px #43434343;
    }

    .bcards-table-actions {
        text-align: right;
        white-space: nowrap;
    }

    .bcard-source-image-holder {
        border-radius: 1rem;
        overflow-y: hidden;
        max-height: 100%;
    }

    .bcard-source-image {
        border-radius: inherit;
        width: 100%;
        overflow-y: auto;
    }

    .bcard-show-form {
        background-color: rgb(240, 240, 240);
        margin: 0 auto 1rem auto;
        width: 100%;
        border: 1px solid #00000030;
        border-radius: 3px;
        text-align: center;
    }
    .bcard-show-form-save {
        text-align: right;
        font-style: italic;
        color: #000000aa;
    }

    .bcard-show-name {
        font-weight: bold;
        font-size: 28px;
    }

    .bcard-show-company {
        font-size: 25px;
    }

    .bcard-show-position {
        font-size: 20px;
        margin-bottom: .4rem;
    }

    .bcard-show-positon-holder {
        margin-bottom: 1.5rem;
    }

    .bcard-show-icons-holder {
        background-color: #212121;
        padding: .3rem 0;
        margin: 0;
        width: 100%;
        color: white;
    }

    .bcard-show-additional-info-holder {
        margin-top: 1rem;
        margin-bottom: 1rem;
        min-height: 72px;
    }

    .bcard-show-info-holder {
        width: 40%;
        text-align: left;
        background-color: white;
        box-shadow: 0 1px 2px 2px #00000030;
        border-radius: 10px;
        margin: calc(10% - 1rem) auto;
        padding: 1rem 1rem;
    }

    .bcard-note-header {
        border-bottom: 1px solid #00000020;
        font-size: 1.1rem;
        font-style: italic;
    }

    .bcard-show-bottom-line {
        background-color: lightblue;
        height: 14px;
        margin: 0;
        padding: 0;
        width: 100%;
    }

    .bcards-filter {
        margin: 1rem 0;
    }

    .bcards-filter-search {
        transition: .2s linear;
    }

    .bcards-filter-search:hover {
        transform: scale(1.2);
    }

    .bcards-filter-search-button {
        padding: 3px 6px 4px 6px;
    }

    .bcards-filter-input {
        text-align: left;
    }

    .bcards-filter-input input {
    }

    .bcard-filter-selected-item {
        display: inline;
        background-color: #f1f1f1;
        padding: .4rem .6rem;
        margin: 0 .3rem;
        border-radius: 5px;
        text-transform: capitalize;
    }

    .bcards-table-holder {
        overflow-x: auto;
        text-align: left;
    }

    .bcards-table-header {
        color: #626262;
        font-weight: bold;
        border-bottom: 4px solid #9e9e9e
    }

    .bcards-table-header th {
        white-space: nowrap;
    }

    .bcards-table {
        max-width: 100%;
        font-family: Segoe UI, Frutiger, Frutiger Linotype, Dejavu Sans, Helvetica Neue, Arial, sans-serif;
        margin: 0;
        border-top: 0 !important;
    }

    .table td {
        padding: .3rem;
        font-size: 15px;
    }

    .bcards-table tr:nth-child(even) {
        background-color: rgba(0, 0, 0, 0.05);
    }

    .bcards-table tr:hover {
        background-color: rgba(0, 0, 0, 0.1)
    }

    .bcards-table tr:first-of-type:hover {
        background-color: transparent !important;
    }

    .bcards-table-button {
        position: relative;
    }

    .bcards-table-button .tooltiptext {
        visibility: hidden;
        background-color: black;
        color: #fff;
        text-align: center;
        padding: 5px 6px;
        border-radius: 6px;
        bottom: 75%;
        right: 0;
        /* Position the tooltip text - see examples below! */
        position: absolute;
        z-index: 1;
    }

    .bcards-table-button:hover .tooltiptext {
        visibility: visible;
    }

    .bcards-show-more-button {
        margin: 1rem auto;
        border-radius: 100% !important;
    }

    .bcards-icon-button {
        cursor: pointer;
    }

    .bcards-table-element {
        cursor: pointer;
    }

    .columns-holder {
        position: absolute;
        background-color: white;
        right: 0;
        border: 1px solid #30303030;
        box-shadow: 0 3px 5px 5px #40404040;
        z-index: 2;
    }

    @keyframes popup {
        0% {
            transform: scale(0);
        }
        100% {
            transform: scale(1);
        }
    }

    @keyframes showing-popup {
        0% {
            opacity: 0;
            transform: scale(0);
        }
        25% {
            opacity: .1;
            transform: scale(.25);
        }
        100% {
            opacity: 1;
            transform: scale(1);
        }
    }

    @keyframes showing {
        0% {
            opacity: 0;
        }
        100% {
            opacity: 1;
        }
    }

    @keyframes growup {
        0% {
            transform: translateY(-50%) scaleY(0);
        }
        100% {
            transform: translateY(0) scaleY(1);

        }
    }

    .bcard-edit-enter-active {
        animation: popup .5s ease;
    }

    .bcard-edit-leave-active {
        animation: popup .3s ease reverse;
    }

    .bcard-show-enter-active {
        animation: popup .3s ease;
    }

    .bcard-show-leave-active {
        animation: popup .3s ease reverse;
    }

    .bcard-new-enter-active {
        animation: showing .3s linear;
    }

    .bcard-new-leave-active {
        animation: showing .5s linear reverse;
    }

    .bcard-source-image-enter-active {
        animation: showing-popup .4s ease;
    }

    .bcard-source-image-leave-active {
        animation: showing-popup .4s ease reverse;
    }

    .columns-enter-active {
        animation: growup .2s ease;
    }

    .columns-leave-active {
        animation: growup .2s ease reverse;
    }

</style>
