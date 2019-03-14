<template>
    <div class="business-cards-page">
        <div v-if="isLoading">
            <h2>
                Content is loading..
                Please be patient
            </h2>
        </div>
        <div v-else>
            <transition name="bcard-show">
                <div class="bcard-show-holder" v-if="cardToShow" @click="hideCard">
                    <div class="bcard-show-form" @click="$event.stopPropagation()">
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
                        <img src="@/assets/icons/bottom line.png" class="row bcard-show-bottom-line" style="margin: auto 0">
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
                            <b-form-input type="text" class="bcard-edit-card-input" v-model="cardToEdit.name" placeholder="Name"></b-form-input>
                            <b-form-input type="text" class="bcard-edit-card-input" v-model="cardToEdit.company_name" placeholder="Company"></b-form-input>
                            <b-form-input type="text" class="bcard-edit-card-input" v-model="cardToEdit.position" placeholder="Position"></b-form-input>
                            <b-form-input type="text" class="bcard-edit-card-input" v-model="cardToEdit.address" placeholder="Address"></b-form-input>
                            <b-form-input type="tel" class="bcard-edit-card-input" v-model="cardToEdit.mobile" placeholder="Phone"></b-form-input>
                            <b-form-input type="email" class="bcard-edit-card-input" v-model="cardToEdit.email" placeholder="Email"></b-form-input>
                            <b-form-input type="text" class="bcard-edit-card-input" v-model="cardToEdit.website" placeholder="Website"></b-form-input>
                            <b-form-select v-model="cardToEdit.private" :options="privacyOptions">

                            </b-form-select>
                        </div>
                        <div class="col-6">
                            <user-selector placeholder="User" class="bcard-edit-search"
                                           @select="addUserPermission($event)">

                            </user-selector>
                            <table class="table" v-if="cardToEdit.permissions && cardToEdit.permissions.length">
                                <tr>
                                    <th>User</th>
                                    <th>Read</th>
                                    <th>Edit</th>
                                    <th>Delete</th>
                                </tr>
                                <tr v-for="(per, index) in groupPermissions(cardToEdit.permissions)" v-bind:key="index">
                                    <th>{{ per.user ? per.user.name : per.user_id }}</th>
                                    <th>
                                        <input type="checkbox" :checked="per[1]" v-if="!isUpdatingPermissions"
                                               @click="per[1] ? deletePermission(cardToEdit.id, per[1]) : addPermission(cardToEdit.id, per.user.id, 1)">
                                    </th>
                                    <th>
                                        <input type="checkbox" :checked="per[2]" v-if="!isUpdatingPermissions"
                                               @change="per[2] ? deletePermission(cardToEdit.id, per[2]) : addPermission(cardToEdit.id, per.user.id, 2)">
                                    </th>
                                    <th>
                                        <input type="checkbox" :checked="per[3]" v-if="!isUpdatingPermissions"
                                               @change="per[3] ? deletePermission(cardToEdit.id, per[3]) : addPermission(cardToEdit.id, per.user.id, 3)">
                                    </th>
                                </tr>
                            </table>
                        </div>
                        <div class="bcards-edit-buttons col-12">
                            <b-btn @click="saveChanges"
                                   class="bcards-icon-button"
                                   variant="success">
                                <v-icon name="save">

                                </v-icon>
                            </b-btn>
                            <b-btn @click="cancelEditing(true)"
                                   class="bcards-icon-button"
                                   variant="danger">
                                <v-icon name="ban">

                                </v-icon>
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
                <div class="bcard-show-holder" v-if="imageToShow" @click="imageToShow = null">
                  <div class="bcard-source-image-holder">
                    <img :src="$store.state.serverURL + '/' + imageToShow" class="bcard-source-image" @click="$event.stopPropagation()">
                  </div>
                </div>
            </transition>
            <div class="bcards-filter row no-gutters">
                <div class="bcards-filter-input col-6">
                    <b-btn :size="'sm'"
                           class="bcards-icon-button"
                           :variant="'secondary'">
                        <img @click="filterCards" src="@/assets/icons/search.png" width="16px" class="bcards-filter-search">
                    </b-btn>
                    <input type="text" v-model="filters.name" @input="delayedFilter" v-if="selectedFilter === 1">
                    <input type="text" v-model="filters.company_name" @input="delayedFilter" v-if="selectedFilter === 2">
                    <input type="text" v-model="filters.position" @input="delayedFilter" v-if="selectedFilter === 3">
                    <input type="text" v-model="filters.email" @input="delayedFilter" v-if="selectedFilter === 4">
                    <input type="text" v-model="filters.mobile" @input="delayedFilter" v-if="selectedFilter === 5">
                    <input type="text" v-model="filters.address" @input="delayedFilter" v-if="selectedFilter === 6">
                    <input type="text" v-model="filters.website" @input="delayedFilter" v-if="selectedFilter === 7">
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
                    </select>
                </div>
                <div class="col" style="text-align: right;">
                    <b-btn @click="showColumns ^= true"
                           class="bcards-icon-button"
                           variant="primary">
                        <v-icon name="filter">

                        </v-icon>
                    </b-btn>
                    <transition name="columns">
                        <div class="columns-holder" v-if="showColumns">
                            <columns-list v-model="columnsToShow" @input="saveConfig">

                            </columns-list>
                        </div>
                    </transition>
                    <b-btn @click="createNewCard = true"
                           class="bcards-icon-button"
                           variant="success">
                        <v-icon name="plus-square">

                        </v-icon>
                    </b-btn>
                </div>
                <div class="col-12">
                  <div v-for="(value, key) in filters" v-bind:key="key" v-if="value"
                       class="bcard-filter-selected-item" @click="$set(filters, key, null); delayedFilter()" >
                    {{ key }} - {{ value }}
                    <img src="@/assets/icons/delete_filter.png" width="10px" >
                  </div>
                </div>
            </div>
            <div class="bcards-table-holder">
                <table class="table bcards-table">
                    <tr class="bcards-table-header">
                        <th @click="sortBy('id')" class="bcards-info-header" v-if="columnsToShow.id">
                            ID
                        </th>
                        <th scope="col" class="bcards-info-header" @click="sortBy('name')" v-if="columnsToShow.name">Name</th>
                        <th @click="sortBy('company_name')" class="bcards-info-header" v-if="columnsToShow.company_name">Company</th>
                        <th @click="sortBy('position')" class="bcards-info-header" v-if="columnsToShow.position">Position</th>
                        <th @click="sortBy('email')" class="bcards-info-header" v-if="columnsToShow.email">Email</th>
                        <th @click="sortBy('mobile')" class="bcards-info-header" v-if="columnsToShow.mobile">Phone</th>
                        <th @click="sortBy('address')" class="bcards-info-header" v-if="columnsToShow.address">Address</th>
                        <th @click="sortBy('website')" class="bcards-info-header" v-if="columnsToShow.website">Website</th>
                        <th>Controls</th>
                    </tr>
                    <tr  v-for="(bcard, index) in businessCardsToShow" v-bind:key="index">
                        <td v-if="columnsToShow.id"> {{ bcard.id }}</td>
                        <td v-if="columnsToShow.name">{{ bcard.name }}</td>
                        <td v-if="columnsToShow.company_name">{{ bcard.company_name }}</td>
                        <td v-if="columnsToShow.position">{{ bcard.position }}</td>
                        <td v-if="columnsToShow.email">{{ bcard.email }}</td>
                        <td v-if="columnsToShow.mobile">{{ bcard.mobile }}</td>
                        <td v-if="columnsToShow.address">{{ bcard.address }}</td>
                        <td v-if="columnsToShow.website">{{ bcard.website }}</td>
                        <td>
                            <b-btn @click="showCard(index)"
                                   class="bcards-table-button bcards-icon-button"
                                   variant="primary">
                                <v-icon name="info">


                                </v-icon>
                                <i class="tooltiptext">
                                    Show card
                                </i>
                            </b-btn>
                          <b-btn @click="showSourceImage(index)"
                                 class="bcards-table-button bcards-icon-button"
                                 variant="warning"
                                 v-if="bcard.image_path">
                              <v-icon name="address-card">

                              </v-icon>
                              <i class="tooltiptext">
                                  Show original
                              </i>
                          </b-btn>
                            <b-btn @click="editCard(index)"
                                   class="bcards-table-button bcards-icon-button"
                                   v-if="editable(index)">
                                <v-icon name="pen">

                                </v-icon>
                                <i class="tooltiptext">
                                    Edit
                                </i>
                            </b-btn>
                            <b-btn @click="deleteCard(bcard.id)"
                                   class="bcards-table-button bcards-icon-button"
                                   v-if="deletable(index)"
                                   variant="danger">
                                <v-icon name="trash">

                                </v-icon>
                                <i class="tooltiptext">
                                    Delete
                                </i>
                            </b-btn>
                        </td>
                    </tr>
                </table>
            </div>
            <b-btn @click="showMore"
                   v-if="showCardsCount < businessCardsFiltered.length"
                   class="bcards-show-more-button" variant="success">
                Show more
                <v-icon name="arrow-down">

                </v-icon>
            </b-btn>
        </div>
    </div>
</template>
<script>
import ColumnsList from '@/components/Tools/ColumnsList.vue'
import NewBusinessCard from '@/components/Business cards/NewBusinessCard.vue'
import UserSelector from '@/components/Tools/UserSelector.vue'
import lodash from 'lodash'
const cardsOnPage = 5
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
export default{
  components: {
    ColumnsList,
    NewBusinessCard,
    UserSelector
  },
  data () {
    return {
      businessCardsAll: [],
      sorting: {
        by: 'id',
        asc: false
      },
      filters: {

      },
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
      privacyOptions: [
        {
          text: 'Public',
          value: false
        },
        {
          text: 'Private',
          value: true
        },
      ],
      showCardsCount: cardsOnPage,
      isLoading: false,
      isUpdatingPermissions: false,
      cardToEdit: null,
      cardToShow: null,
      showColumns: false,
      imageToShow: false,
      createNewCard: false
    }
  },
  watch: {
    columnsToShow () {
      this.saveConfig()
    },
    option () {
      this.load()
    }
  },
  computed: {
    userID () {
      return this.$store.getters.userId
    },
    loadOptions () {
      let options = availableLoadOptions[this.option] || null
      if (options && options['created_by'] && options['created_by'] === 'my_id') {
        options['created_by'] = this.userID
      }
      return options
    },
    option () {
      return this.$route.params.option
    },
    businessCardsToShow () {
      return this.businessCardsFiltered.slice(0, this.showCardsCount)
    },
    businessCardsFiltered () {
      return this.filterCards().sort((a,b) =>
      (this.sorting.asc ? 1 : -1) * (a[this.sorting.by] ? ((typeof a[this.sorting.by] === 'string') ?
          a[this.sorting.by].localeCompare(b[this.sorting.by]) : a[this.sorting.by] - b[this.sorting.by])
          : 0 ));
    }
  },
  mounted () {
    this.load()
    this.getConfig()
  },
  methods: {
    load () {
      this.isLoading = true
      this.axios.get('business-cards',{
        params: {
          user_id: this.userID,
          filters: this.loadOptions
        }
      }).then(response => {
        this.businessCardsAll = response.data
        this.isLoading = false
      }).catch(err => {
        console.log(err)
        this.isLoading = false
      })
    },
    editable (index) {
      return this.businessCardsToShow[index].created_by.toString() === this.$store.getters.userId.toString() ||
          this.businessCardsToShow[index].permissions.filter(x => x.permission_id === 2).length > 0
    },
    deletable (index) {
        return this.businessCardsToShow[index].created_by.toString() === this.$store.getters.userId.toString() ||
            this.businessCardsToShow[index].permissions.filter(x => x.permission_id === 3).length > 0
    },
    editCard (index) {
      this.cardToEdit = JSON.parse(JSON.stringify(this.businessCardsToShow[index]))
    },
    deleteCard (id) {
      if(!confirm('Are you sure?') || confirm('Do you lie?')) return
      this.axios.delete('/business-cards/' + id, {
        params: {
          user_id: this.userID
        }
      }).then(response => {
        if(response.data) {
          this.businessCardsAll = this.businessCardsAll.filter(x => x.id !== id)
        }  else {
          alert('Failed to delete')
        }
      }).catch(err => {
        console.log(err)
        alert('Couldn\'t delete due to server trouble')
      })
    },
    groupPermissions (permissions) {
      let result = {}
      for (let i = 0; i < permissions.length; i++) {
        if (!result[permissions[i].user_id]) {
          result[permissions[i].user_id] = {user: permissions[i].user }
        }
        result[permissions[i].user_id][permissions[i].permission_id] = permissions[i].id
      }
      return result
    },
    addUserPermission (user) {
      this.cardToEdit.permissions.push({
        user: user,
        permission_id: 0,
        business_card_id: this.cardToEdit.id,
        user_id: user.id
      })
    },
    addPermission (cardId, userId, permissionId) {
      this.isUpdatingPermissions = true
      this.axios.post('/user-permissions', {
        business_card_id: cardId,
        user_id: userId,
        permission_id: permissionId
      }).then(response => {
        this.isUpdatingPermissions = false
        if(this.cardToEdit && this.cardToEdit.id === cardId) {
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
    deletePermission (cardId, permissionId) {
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
    cancelEditing (force = false) {
      if (force || confirm('Are you sure?')) {
        this.cardToEdit = null
      }
    },
    saveChanges () {
      this.axios.put('/business-cards/' + this.cardToEdit.id, this.cardToEdit).then(response => {
        for (let i = 0; i < this.businessCardsAll.length; i++) {
          if (this.businessCardsAll[i].id === response.data.id) {
            for (let key in response.data){
                this.$set(this.businessCardsAll[i], key, response.data[key])
            }
          }
        }
      }).catch(err => {
        console.log(err)
      })
      this.cardToEdit = null
    },
    showCard (index) {
      this.cardToShow = this.businessCardsToShow[index]
    },
    hideCard () {
      this.cardToShow = null
    },
    sortBy (field) {
      if (field) {
        if (this.sorting.by === field) {
          this.sorting.asc ^= true
        } else {
          this.sorting.by = field
          this.sorting.asc = true
        }
      }
    },
    filterCards () {
      return this.businessCardsAll.filter(card => {
        for (let param in this.filters) {
          if (this.filters.hasOwnProperty(param) && this.filters[param]) {
            if(!card[param].toString().toLowerCase().includes(this.filters[param].toString().toLowerCase())) {
              return false
            }
          }
        }
        return true
      })
    },
    delayedFilter: lodash.debounce(function () { this.filterCards() }, 500, 1500),
    cancelCreating (force = false) {
      if (force || confirm('Are you sure?')) {
        this.createNewCard = false
      }
    },
    addCard (card) {
      console.log(card)
      card.permissions = []
      this.businessCardsAll.push(card)
      this.filterCards()
      this.sortBy()
      this.createNewCard = false
    },
    saveConfig () {
      this.$cookie.set('columns-config', JSON.stringify(this.columnsToShow), 30)
    },
    getConfig () {
      let saved = this.$cookie.get('columns-config')
      if (saved) {
        this.columnsToShow = JSON.parse(saved)
      }
    },
    showSourceImage (index) {
      this.imageToShow = this.businessCardsToShow[index].image_path
    },
    showMore () {
      this.showCardsCount += cardsOnPage
    }
  }
}
</script>
<style>
.business-cards-page{
    margin-bottom: 3rem;
}
.arrow-buttons{
    cursor: pointer;
}
.bcard-edit{
    width: 100%;
}
.bcard-show-holder{
    position: fixed;
    left: 0;
    top: 0;
    z-index: 5;
    width: 100%;
    height: 100%;
    overflow: auto;
}
.bcard-edit-card-input{
    margin: .4rem auto;
}
.bcard-edit-holder{
    background-color: #FFFFFF;
    max-height: 60%;
    margin: 5rem 10rem;
    border-radius: 2rem;
    border: 3px double rgb(200,200,200);
    box-shadow: 0 0 3px 4px #15151540   ;
    padding: 1rem;
    font-style: italic;
    overflow-y: auto;
}
.bcard-edit-holder input{
    width: 100%;
    text-align: left;
    border-radius: 10px;
}
.bcards-edit-buttons{
    margin: .4rem auto;
}
.bcards-info-header{
    cursor: pointer;
}
.bcard-new-card-holder{
    background-color: white;
    margin: 5rem 4rem;
    overflow-y: auto;
    border-radius: 1rem;
    box-shadow: 0 0 4px 8px #43434343;
}
.bcard-source-image-holder {
  margin: 3rem 2rem;
  max-height: calc(100vh - 6rem);
  border-radius: 1rem;
  overflow-y: auto;
}
.bcard-source-image{
  border-radius: inherit;
  overflow-y: auto;
}
.bcard-show-form{
   background-color: rgb(240,240,240);
    margin: 10% auto;
    width: 45%;
    box-shadow: 0 0 30px 10px #212529B3;
}
.bcard-show-name{
    font-weight: bold;
    font-size: 2.4rem;
}
.bcard-show-company{
    font-size: 1.6rem;
}
.bcard-show-position{
    font-size: 1.1rem;
}
.bcard-show-positon-holder{
    margin-bottom: 1.5rem;
}
.bcard-show-icons-holder{
    background-color: #212121;
    padding: .3rem 0;
    margin:0;
    width: 100%;
    color: white;
}
.bcard-show-additional-info-holder {
    margin-top: 1rem;
    margin-bottom: 1rem;
    height: 72px;
}
.bcard-show-bottom-line{
    background-color: lightblue;
    height: 14px;
    margin: 0;
    padding: 0;
    width: 100%;
}
.bcards-filter {
    margin: 1rem 0;
}
.bcards-filter-search{
    transition: .2s linear;
}
.bcards-filter-search:hover{
    transform: scale(1.2);
}
.bcards-filter-input{
    margin: .5rem;
    margin-top: 0;
}
.bcards-filter-input input{
}
.bcard-filter-selected-item {
    display: inline;
    background-color: #f1f1f1;
    padding: .4rem .6rem;
    margin: 0 .3rem;
    border-radius: 5px;
  text-transform: capitalize;
}
.bcards-table-holder{
    overflow-x: auto;
}
.bcards-table-header{
    color: #626262;
    font-weight: bold;
    border-bottom: 4px solid #9e9e9e
}
.bcards-table{
    max-width: 100%;
    font-family: Segoe UI,Frutiger,Frutiger Linotype,Dejavu Sans,Helvetica Neue,Arial,sans-serif;
    margin: 0;
    border-top: 0 !important;
}
.bcards-table tr:nth-child(even){
    background-color: #f2f2f2;
}
.bcards-table-button{
    position: relative;
}
.bcards-table-button .tooltiptext{
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
.bcards-table-button:hover .tooltiptext{
    visibility: visible;
}
.bcards-show-more-button{
    margin: 1rem auto;
}
.columns-holder{
    position: absolute;
    background-color: white;
    right: 0;
    border: 1px solid #30303030;
    box-shadow: 0 3px 5px 5px #40404040;
    z-index:2;
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
@keyframes growup{
    0% {
        transform: translateY(-50%) scaleY(0);
    }
    100% {
        transform: translateY(0) scaleY(1);

    }
}
.bcard-edit-enter-active{
    animation: popup .5s ease;
}
.bcard-edit-leave-active{
    animation: popup .3s ease reverse;
}
.bcard-show-enter-active{
    animation: popup .3s ease ;
}
.bcard-show-leave-active{
    animation: popup .3s ease reverse;
}
.bcard-new-enter-active{
    animation: showing .3s linear;
}
.bcard-new-leave-active{
    animation: showing .5s linear reverse;
}
.bcard-source-image-enter-active{
    animation: showing-popup .4s ease;
}
.bcard-source-image-leave-active{
    animation: showing-popup .4s ease reverse;
}
.columns-enter-active{
    animation: growup .2s ease;
}
.columns-leave-active{
    animation: growup .2s ease reverse;
}
</style>
