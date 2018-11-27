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
            <div class="">
                <router-link :to="{name: 'NewBusinessCard'}">
                    <button>&#65291;</button>
                </router-link>
            </div>
            <table class="table business-cards-table">
                <thead class="thead-dark">
                    <th scope="col">Name</th>
                    <th>Surname</th>
                    <th>Company</th>
                    <th>Position</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Address</th>
                    <th>Website</th>
                    <th>Controls</th>
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
                        <button @click="editCard(index)" v-if="editable(index)">
                            Edit
                        </button>
                        <button @click="deleteCard(bcard.id)" v-if="deletable(index)">
                            Del
                        </button>
                    </td>
                <td>
                    {{ isEditing(bcard.id)}}
                </td>
                </tbody>
                <tbody v-else>
                <td>
                    <input type="text" placeholder="Name" class="bcard-edit bcard-edit-name" v-model="editingCards[bcard.id].name">
                </td>
                <td>
                    <input type="text" placeholder="Surname" class="bcard-edit bcard-edit-surname" v-model="editingCards[bcard.id].surname">
                </td>
                <td>
                    <input type="text" placeholder="Company" class="bcard-edit bcard-edit-company" v-model="editingCards[bcard.id].company_name">
                </td>
                <td>
                    <input type="text" placeholder="Position" class="bcard-edit bcard-edit-position" v-model="editingCards[bcard.id].position">
                </td>
                <td>
                    <input type="text" placeholder="Email" class="bcard-edit bcard-edit-email" v-model="editingCards[bcard.id].email">
                </td>
                <td>
                    <input type="text" placeholder="Phone" class="bcard-edit bcard-edit-phone" v-model="editingCards[bcard.id].mobile">
                </td>
                <td>
                    <input type="text" placeholder="Address" class="bcard-edit bcard-edit-address" v-model="editingCards[bcard.id].address">
                </td>
                <td>
                    <input type="text" placeholder="Website" class="bcard-edit bcard-edit-website" v-model="editingCards[bcard.id].website">
                </td>
                <td>
                    <button @click="saveChanges(bcard.id)">Save</button>
                    <button @click="cancelEditing(bcard.id)">Cancel</button>
                </td>
                </tbody>
            </table>
        </div>
    </div>
</template>
<script>
export default{
  data () {
    return {
      businessCards: [],
      isLoading: false,
      editingCards: {}
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
      //this.businessCards = [{"id":2,"name":"Enrico","surname":"Jenkins","company_name":"Powlowski-Dare","position":"Sports Book Writer","address":"4486 Lueilwitz Trail Apt. 478\nWittingchester, WI 15948-3166","mobile":"(707) 843-8181 x295","email":"earlene.ward@beer.biz","website":"lubowitz.biz","private":0,"created_by":3,"deleted_by":null,"created_at":"2018-11-27 05:09:03","updated_at":"2018-11-27 05:09:03"},{"id":3,"name":"Germaine","surname":"Herzog","company_name":"Fadel-Nitzsche","position":"Paste-Up Worker","address":"6531 Vandervort Tunnel Suite 037\nEast Loniefort, MN 17124-6601","mobile":"+1-904-386-2196","email":"tatyana42@yahoo.com","website":"jacobs.com","private":0,"created_by":2,"deleted_by":null,"created_at":"2018-11-27 05:09:03","updated_at":"2018-11-27 05:09:03"},{"id":4,"name":"Aurelia","surname":"Turcotte","company_name":"Wehner-Reynolds","position":"Sociologist","address":"99351 Harvey Gateway Suite 203\nMaryseborough, FL 97511-1147","mobile":"240-418-9531 x103","email":"vida.morar@mckenzie.com","website":"langosh.org","private":0,"created_by":1,"deleted_by":null,"created_at":"2018-11-27 05:09:03","updated_at":"2018-11-27 05:09:03"},{"id":5,"name":"Ilene","surname":"McCullough","company_name":"Stiedemann-Ratke","position":"Fitness Trainer","address":"76604 Nienow Trafficway Apt. 127\nNew Marlinport, NV 34708","mobile":"1-918-248-4805 x5674","email":"predovic.emmanuelle@yahoo.com","website":"abbott.com","private":0,"created_by":3,"deleted_by":null,"created_at":"2018-11-27 05:09:04","updated_at":"2018-11-27 05:09:04"},{"id":6,"name":"Kelly","surname":"Ziemann","company_name":"Kohler Ltd","position":"Eligibility Interviewer","address":"5698 Kunde Wall\nBuckridgeland, NE 88564-7689","mobile":"652.708.0178","email":"bechtelar.concepcion@hotmail.com","website":"hauck.net","private":1,"created_by":2,"deleted_by":null,"created_at":"2018-11-27 05:09:04","updated_at":"2018-11-27 05:09:04"},{"id":7,"name":"Ralph","surname":"Powlowski","company_name":"Murazik, Kutch and McKenzie","position":"Designer","address":"942 Myrl Heights\nNorth Agnes, SC 21739","mobile":"+18972735803","email":"carlee.hudson@yahoo.com","website":"hills.com","private":1,"created_by":1,"deleted_by":null,"created_at":"2018-11-27 05:09:04","updated_at":"2018-11-27 05:09:04"},{"id":8,"name":"Theodore","surname":"O'Keefe","company_name":"Blanda Group","position":"Rotary Drill Operator","address":"328 Arch Mission Suite 428\nJedtown, IN 09095","mobile":"764-710-7702","email":"karolann13@hotmail.com","website":"mohr.com","private":0,"created_by":3,"deleted_by":null,"created_at":"2018-11-27 05:09:04","updated_at":"2018-11-27 05:09:04"},{"id":9,"name":"Clifford","surname":"Quitzon","company_name":"Rohan LLC","position":"Heaters","address":"53598 Kerluke Mill Apt. 311\nBaumbachtown, MI 71693-9081","mobile":"+1 (503) 425-4138","email":"graham.raleigh@miller.info","website":"gutkowski.org","private":1,"created_by":2,"deleted_by":null,"created_at":"2018-11-27 05:09:04","updated_at":"2018-11-27 05:09:04"},{"id":10,"name":"Hester","surname":"Hauck","company_name":"Kassulke Group","position":"Furniture Finisher","address":"8345 Emard Path\nLake Rogers, AR 47141","mobile":"1-212-452-6008 x54560","email":"heller.glennie@wilkinson.com","website":"ebert.com","private":0,"created_by":1,"deleted_by":null,"created_at":"2018-11-27 05:09:04","updated_at":"2018-11-27 05:09:04"},{"id":11,"name":"Elvin","surname":"Bayramov","company_name":"Azercosmos","position":"Junior Programer","address":"MCS","mobile":" 994513887341","email":null,"website":null,"private":0,"created_by":1,"deleted_by":null,"created_at":"2018-11-27 05:16:20","updated_at":"2018-11-27 05:17:47"}]
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
    }
  }
}
</script>
<style>
.bcard-edit{
    width: 100%;
}
</style>