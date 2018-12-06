<script src="../../../../../azercosmos-intranet/frontend/src/main.js"></script>
<template>
    <div class="business-cards-page">
        <div class="bcards-header">

        </div>
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
                        <div class="row bcard-show-name-holder">
                            <span class="col-12 bcard-show-name">
                                {{ cardToShow.name }} {{ cardToShow.surname }}
                            </span>
                        </div>
                        <div class="row bcard-show-company-holder">
                            <span class="col-12 bcard-show-company">
                                {{ cardToShow.company_name }}
                            </span>
                        </div>
                        <div class="row bcard-show-positon-holder">
                            <span class="col-12 bcard-show-position">
                                {{ cardToShow.position }}
                            </span>
                        </div>
                        <div class="row bcard-show-icons-holder">
                            <div class="col-4 bcard-show-icon-holder">
                                Address
                            </div>
                            <div class="col-4 bcard-show-icon-holder">
                                Phone
                            </div>
                            <div class="col-4 bcard-show-icon-holder">
                                Website
                            </div>
                        </div>
                        <div class="row bcard-show-additional-info-holder">
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
                        <div class="row bcard-show-bottom-line">

                        </div>
                    </div>
                </div>
            </transition>
            <transition name="bcard-edit">
                <div class="bcard-show-holder" v-if="cardToEdit" @click="cancelEditing">
                    <div class="bcard-edit-holder row" @click="$event.stopPropagation()">
                        <div class="col-12">
                            <h2>Edit card #{{cardToEdit.id}}</h2>
                        </div>
                        <div class="col-3">Name</div>
                        <div class="col-6 offset-1">
                            <input type="text" v-model="cardToEdit.name">
                        </div>
                        <div class="col-3">Surname</div>
                        <div class="col-6 offset-1" >
                            <input type="text" v-model="cardToEdit.surname">
                        </div>
                        <div class="col-3">Company</div>
                        <div class="col-6 offset-1" >
                            <input type="text" v-model="cardToEdit.company_name">
                        </div>
                        <div class="col-3">Position</div>
                        <div class="col-6 offset-1">
                            <input type="text" v-model="cardToEdit.position">
                        </div>
                        <div class="col-3">Email</div>
                        <div class="col-6 offset-1" >
                            <input type="email" v-model="cardToEdit.email">
                        </div>
                        <div class="col-3">Phone</div>
                        <div class="col-6 offset-1">
                            <input type="tel" v-model="cardToEdit.mobile">
                        </div>
                        <div class="col-3">Website</div>
                        <div class="col-6 offset-1" >
                            <input type="text" v-model="cardToEdit.website">
                        </div>
                        <div class="col-12">
                          <table class="table">
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
                          <user-selector @select="addUserPermission($event)"></user-selector>
                        </div>
                        <div class="col-3">
                            <button @click="saveChanges">Save</button>
                        </div>
                        <div class="col-3">

                        </div>
                        <div class="col-3">
                            <button @click="cancelEditing">Cancel</button>
                        </div>
                    </div>
                </div>
            </transition>
            <transition name="bcard-new">
                <div class="bcard-show-holder" v-if="createNewCard" @click="createNewCard = false">
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
            <div>
                <button @click="showFilter ^= true">Filter</button>
                <button @click="showColumns ^= true">Columns</button>
                <button @click="createNewCard = true">&#65291;</button>
                <button @click="showMore" v-if="showCardsCount < businessCards.length">Show more</button>
            </div>
            <div class="bcards-filer" v-if="showFilter">
                <input type="text" v-model="filters.name" placeholder="Name">
                <input type="text" v-model="filters.surname" placeholder="Surname">
                <input type="text" v-model="filters.company_name" placeholder="Company">
                <input type="text" v-model="filters.position" placeholder="Position">
                <input type="text" v-model="filters.email" placeholder="Email">
                <input type="text" v-model="filters.mobile" placeholder="Phone">
                <input type="text" v-model="filters.address" placeholder="Address">
                <input type="text" v-model="filters.website" placeholder="Website">
                <button @click="filterCards">Apply</button>
            </div>
            <columns-list v-model="columnsToShow" @input="saveConfig" v-if="showColumns">

            </columns-list>
            <div class="bcards-table-holder">
                <table class="table bcards-table">
                    <tr class="thead-dark">
                        <th @click="sortBy('id')" v-if="columnsToShow.id">ID</th>
                        <th scope="col" @click="sortBy('name')" v-if="columnsToShow.name">Name</th>
                        <th @click="sortBy('surname')" v-if="columnsToShow.surname">Surname</th>
                        <th @click="sortBy('company_name')" v-if="columnsToShow.company_name">Company</th>
                        <th @click="sortBy('position')" v-if="columnsToShow.position">Position</th>
                        <th @click="sortBy('email')" v-if="columnsToShow.email">Email</th>
                        <th @click="sortBy('mobile')" v-if="columnsToShow.mobile">Phone</th>
                        <th @click="sortBy('address')" v-if="columnsToShow.address">Address</th>
                        <th @click="sortBy('website')" v-if="columnsToShow.website">Website</th>
                        <th colspan="4">Controls</th>
                    </tr>
                    <tr  v-for="(bcard, index) in businessCards.slice(0, showCardsCount)" v-bind:key="index">
                        <td v-if="columnsToShow.id"> {{ bcard.id }}</td>
                        <td v-if="columnsToShow.name">{{ bcard.name }}</td>
                        <td v-if="columnsToShow.surname">{{ bcard.surname}}</td>
                        <td v-if="columnsToShow.company_name">{{ bcard.company_name }}</td>
                        <td v-if="columnsToShow.position">{{ bcard.position }}</td>
                        <td v-if="columnsToShow.email">{{ bcard.email }}</td>
                        <td v-if="columnsToShow.mobile">{{ bcard.mobile }}</td>
                        <td v-if="columnsToShow.address">{{ bcard.address }}</td>
                        <td v-if="columnsToShow.website">{{ bcard.website }}</td>
                        <td>
                            <button @click="showCard(index)">
                                View
                            </button>
                        </td>
                        <td>
                          <button @click="showSourceImage(index)" v-if="bcard.image_path">
                              Source
                          </button>
                        </td>
                        <td>
                            <button @click="editCard(index)" v-if="editable(index)">
                                Edit
                            </button>
                        </td>
                        <td>
                            <button @click="deleteCard(bcard.id)" v-if="deletable(index)">
                                Del
                            </button>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</template>
<script>
import ColumnsList from '@/components/Tools/ColumnsList.vue'
import NewBusinessCard from '@/components/Business cards/NewBusinessCard.vue'
import UserSelector from '@/components/Tools/UserSelector.vue'
const cardsOnPage = 3
export default{
  components: {
    ColumnsList,
    NewBusinessCard,
    UserSelector
  },
  data () {
    return {
      businessCards: [],
      businessCardsAll: [],
      sorting: {
        by: 'id',
        asc: false
      },
      filters: {

      },
      columnsToShow: {
        id: true,
        name: true,
        surname: true,
        company_name: true,
        position: true,
        mobile: true,
        address: false,
        email: true,
        website: false
      },
      showCardsCount: cardsOnPage,
      isLoading: false,
      isUpdatingPermissions: false,
      cardToEdit: null,
      cardToShow: null,
      showFilter: false,
      showColumns: false,
      imageToShow: false,
      createNewCard: false
    }
  },
  mounted () {
    this.load()
    this.getConfig()
  },
  methods: {
    load () {
      this.isLoading = true
      this.axios.get('business-cards/user/' + this.$store.getters.userId).then(response => {
        this.businessCards = response.data
        this.businessCardsAll = response.data
        this.isLoading = false
      }).catch(err => {
        console.log(err)
        this.isLoading = false
      })
    },
    editable (index) {
      return this.businessCards[index].created_by.toString() === this.$store.getters.userId.toString() ||
          this.businessCards[index].permissions.filter(x => x.permission_id === 2).length > 0
    },
    deletable (index) {
        return this.businessCards[index].created_by.toString() === this.$store.getters.userId.toString() ||
            this.businessCards[index].permissions.filter(x => x.permission_id === 3).length > 0
    },
    editCard (index) {
      this.cardToEdit = JSON.parse(JSON.stringify(this.businessCards[index]))
    },
    deleteCard (id) {
      if(!confirm('Are you sure?') || confirm('Do you lie?')) return
      this.axios.delete('/business-cards/' + id, {
        params: {
          user_id: this.$store.getters.userId
        }
      }).then(response => {
        if(response.data) {
          this.businessCards = this.businessCards.filter(x => x.id !== id)
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
      this.axios.post('/user-permissions/', {
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
    cancelEditing (id) {
      this.cardToEdit = null
    },
    saveChanges () {
      this.axios.put('/business-cards/' + this.cardToEdit.id, this.cardToEdit).then(response => {
        for (let i = 0; i < this.businessCards.length; i++) {
          if (this.businessCards[i].id === response.data.id) {
            for (let key in response.data){
                this.$set(this.businessCards[i], key, response.data[key])
            }
          }
        }
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
      this.cardToShow = this.businessCards[index]
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
      this.businessCards.sort((a,b) =>
          (this.sorting.asc ? 1 : -1) * (a[this.sorting.by] ? ((typeof a[this.sorting.by] === 'string') ?
                  a[this.sorting.by].localeCompare(b[this.sorting.by]) : a[this.sorting.by] - b[this.sorting.by])
                    : 0 ));
    },
    filterCards () {
      this.businessCards = this.businessCardsAll.filter(card => {
        for (let param in this.filters) {
          if (this.filters.hasOwnProperty(param) && this.filters[param]) {
            if(!card[param].toString().toLowerCase().includes(this.filters[param].toString().toLowerCase())) {
              return false
            }
          }
        }
        return true
      })
      this.sortBy()
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
      this.imageToShow = this.businessCards[index].image_path
    },
    showMore () {
      this.showCardsCount += cardsOnPage
    }
  }
}
</script>
<style>

.bcards-header{
    background-color: lime;
    width: 100%;
    height: 1.5rem;
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
}
.bcard-edit-holder{
    background-color: #FFFFFF;
    max-height: 80%;
    margin: 5rem 5rem;
    border-radius: 2rem;
    border: 3px double rgb(200,200,200);
    padding: 1rem;
    font-style: italic;
}
.bcard-edit-holder input{
    width: 100%;
    text-align: center;
    border-radius: 10px;
}
.bcard-new-card-holder{
    background-color: rgb(220,220,220);
    max-height: calc(100% - 5rem);
    margin: 2rem 1rem;
    overflow-y: auto;
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
    margin-top: 10%;
    margin-left: 20%;
    margin-right: 20%;
    box-shadow: 0 0 10px 10px rgb(150,150,150);
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
    background-color: #2c3e50;
    padding: 0;
    min-height: 48px;
    margin: 0;
    width: 100%;
    color: white;
}
.bcard-show-additional-info-holder {
    margin-top: 1rem;
    margin-bottom: 1rem;
}
.bcard-show-bottom-line{
    background-color: lightblue;
    height: 10px;
    margin: 0;
    width: 100%;
}
.bcards-filter div{
    display: inline;
}
.bcards-table-holder{
    overflow-x: auto;
}
.bcards-table{
    max-width: 100%;
    margin: 0;
}
.bcards-table tr:nth-child(even){
    background-color: rgb(205,205,205);
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
</style>