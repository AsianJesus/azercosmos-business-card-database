<script src="../../../../../azercosmos-intranet/frontend/src/main.js"></script>
<template>
    <div class="business-cards-page">
        <h1>
            Welcome to business cards page
        </h1>
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
                <router-link :to="{name: 'NewBusinessCard'}">
                    <button>&#65291;</button>
                </router-link>
            </div>
            <div>
                <table class="table table-striped table-hover bcards-table">
                    <thead class="thead-dark">
                        <th scope="col">Name</th>
                        <th>Surname</th>
                        <th>Company</th>
                        <th>Position</th>
                        <th>Email</th>
                        <th>Phone</th>
                        <th>Address</th>
                        <th>Website</th>
                        <th colspan="3">Controls</th>
                    </thead>
                    <tbody v-for="(bcard, index) in businessCards" v-bind:key="index" v-if="!isEditing(bcard.id)">
                        <td>{{ bcard.name }}</td>
                        <td>{{ bcard.surname}}</td>
                        <td>{{ bcard.company_name }}</td>
                        <td>{{ bcard.position }}</td>
                        <td>{{ bcard.email }}</td>
                        <td>{{ bcard.mobile }}</td>
                        <td>{{ bcard.address }}</td>
                        <td>{{ bcard.website }}</td>
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
                    </tbody>
                    <tbody v-else>
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
                            <button @click="cancelEditing(bcard.id)">Cancel</button>
                        </td>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</template>
<script>
export default{
  data () {
    return {
      businessCards: [],
      isLoading: false,
      editingCards: {},
      cardToShow: null
    }
  },
  mounted () {
    this.load()
  },
  methods: {
    load () {
      this.isLoading = true
      this.axios.get(this.$store.state.serverUrl + 'b-cards/user/' + this.$store.getters.userId).then(response => {
        this.businessCards = response.data
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
    }
  }
}
</script>
<style>
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
.bcards-table tbody:nth-child(even){
    background-color: rgb(205,205,205);
}
</style>