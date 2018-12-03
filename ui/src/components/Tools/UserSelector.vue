<template>
    <div class="user-selector-component">
        <div class="user-selector-holder">
            <input type="text" v-model="name" @input="delayedSearch(name)" class="user-selector-input">
        </div>
        <div class="user-selector-suggestions" v-if="suggestions">
            <ul class="user-suggestions-list">
                <li v-for="(user, index) in suggestionsToShow" v-bind:key="index"
                    @click="selectUser(index)" class="user-suggestion-item">
                    {{ user.name }}
                </li>
            </ul>
        </div>
    </div>
</template>
<script>
import _ from 'lodash'
export default{
  data () {
    return {
      name: '',
      lastTemplate: '',
      suggestions: null
    }
  },
  computed: {
    suggestionsToShow () {
      if (this.name && this.suggestions) {
        return this.suggestions.filter(u => u.name.toLowerCase().includes(this.name.toLowerCase()))
      } else {
        return null
      }
    }
  },
  methods: {
    sendRequest (template) {
      this.axios.get(this.$store.state.serverUrl + '/users/by-name/' + template).then(response => {
        this.suggestions = response.data
      })
    },
    search: _.debounce(function (template) {
      this.sendRequest(template)
    }, 400, 800),
    delayedSearch (template) {
      if (!template) return
      if (!(this.lastTemplate && template.includes(this.lastTemplate))){
        this.search(template)
        this.lastTemplate = template
      }
    },
    selectUser (index) {
      this.$emit('select', this.suggestionsToShow[index])
    }
  }
}
</script>
<style>
.user-selector-input{
    border-top-left-radius: 10px;
    border-top-right-radius: 10px;
    width: 100%;
}
.user-selector-suggestions{
    position: absolute;
    background-color: #abdde5;
    width: 90%;
    left: 5%;
    z-index: 1001;
}
</style>