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
            <div class="bcard-show-holder" v-if="cardToShow" @click="hideCard">
                <div class="hcard-show-form" @click="$event.stopPropagation()">
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
            <div class="">
                <button @click="showFilter ^= true">Filter</button>
                <button @click="showColumns ^= true">Columns</button>
                <router-link :to="{name: 'NewBusinessCard'}">
                    <button>&#65291;</button>
                </router-link>
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
            <columns-list v-model="columnsToShow" v-if="showColumns">

            </columns-list>
            {{ columnsToShow }}
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
                        <th colspan="3">Controls</th>
                    </tr>
                    <tr  v-for="(bcard, index) in businessCards" v-bind:key="index" v-if="!isEditing(bcard.id)">
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
                    <tbody v-else>
                        <td>
                            {{ bcard.id }}
                        </td>
                        <td>
                            <input type="text" placeholder="Name" class="bcard-edit bcard-edit-name"
                                   v-model="editingCards[bcard.id].name">
                        </td>
                        <td>
                            <input type="text" placeholder="Surname" class="bcard-edit bcard-edit-surname"
                                   v-model="editingCards[bcard.id].surname">
                        </td>
                        <td>
                            <input type="text" placeholder="Company" class="bcard-edit bcard-edit-company"
                                   v-model="editingCards[bcard.id].company_name">
                        </td>
                        <td>
                            <input type="text" placeholder="Position" class="bcard-edit bcard-edit-position"
                                   v-model="editingCards[bcard.id].position">
                        </td>
                        <td>
                            <input type="text" placeholder="Email" class="bcard-edit bcard-edit-email"
                                   v-model="editingCards[bcard.id].email">
                        </td>
                        <td>
                            <input type="text" placeholder="Phone" class="bcard-edit bcard-edit-phone"
                                   v-model="editingCards[bcard.id].mobile">
                        </td>
                        <td>
                            <input type="text" placeholder="Address" class="bcard-edit bcard-edit-address"
                                   v-model="editingCards[bcard.id].address">
                        </td>
                        <td>
                            <input type="text" placeholder="Website" class="bcard-edit bcard-edit-website"
                                   v-model="editingCards[bcard.id].website">
                        </td>
                        <td>
                            <button @click="saveChanges(bcard.id)">Save</button>
                        </td>
                        <td>
                            <button @click="cancelEditing(bcard.id)">Cancel</button>
                        </td>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</template>
<script>
import ColumnsList from '@/components/Tools/ColumnsList.vue'
export default{
  components: {
    ColumnsList
  },
  data () {
    return {
      businessCards: [],
      businessCardsAll: [],
      sorting: {
        by: '',
        asc: 1
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
      isLoading: false,
      editingCards: {},
      cardToShow: null,
      showFilter: false,
      showColumns: false
    }
  },
  mounted () {
    this.load()
  },
  methods: {
    load () {
      this.isLoading = true
      this.axios.get(this.$store.state.serverUrl + 'b-cards/user/' + this.$store.getters.userId, {
        params: {
          user_id: this.$store.getters.userId
        }
      }).then(response => {
        this.businessCards = response.data
        this.businessCardsAll = response.data
        this.isLoading = false
      }).catch(err => {
        console.log(err)
        this.isLoading = false
      })
    },
    editable (index) {
      return this.businessCards[index].created_by === this.$store.getters.userId ||
          this.businessCards[index].permissions.filter(x => x.permission.name.toLowerCase().includes('edit')).length > 0
    },
    deletable (index) {
      return this.businessCards[index].created_by === this.$store.getters.userId ||
                this.businessCards[index].permissions.filter(x => x.permission.name.toLowerCase().includes('delete')).length > 0
    },
    editCard (index) {
      this.$set(this.editingCards,this.businessCards[index].id,JSON.parse(JSON.stringify(this.businessCards[index])))
    },
    deleteCard (id) {
      if(!confirm('Are you sure?') || confirm('Do you lie?')) return
      this.axios.delete(this.$store.state.serverUrl + '/b-cards/' + id, {
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
    isEditing (id) {
      return this.editingCards[id]
    },
    cancelEditing (id) {
      this.$set(this.editingCards, id, null)
    },
    saveChanges (id) {
      console.log(this.editingCards[id])
      this.axios.put(this.$store.state.serverUrl + '/b-cards/' + id, this.editingCards[id]).then(response => {
        for (let i = 0; i < this.businessCards.length; i++) {
          if (this.businessCards[i].id === id) {
            for (let key in response.data){
                this.$set(this.businessCards[i], key, response.data[key])
            }
          }
        }
        for (let i = 0; i < this.businessCardsAll.length; i++) {
          if (this.businessCardsAll[i].id === id) {
            for (let key in response.data){
                this.$set(this.businessCardsAll[i], key, response.data[key])
            }
          }
        }
      }).catch(err => {
        console.log(err)
      })
      this.$set(this.editingCards, id, null)
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
.hcard-show-form{
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
.bcards-table tbody:nth-child(even){
    background-color: rgb(205,205,205);
}
</style>