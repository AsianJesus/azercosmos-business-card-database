<template>
    <div class="user-selector-component">
        <div class="user-selector-holder">
            <input type="text" v-model="name" :placeholder="placeholder"
                   @input="delayedSearch(name)" class="user-selector-input">
        </div>
        <div class="user-selector-suggestions"
             v-if="suggestions && suggestions.length && name">
            <div class="user-suggestions-list">
                <div v-for="(user, index) in suggestionsToShow" v-bind:key="index"
                    @click="selectUser(index)" class="user-suggestion-item">
                    {{ user.NAME }}
                </div>
            </div>
        </div>
    </div>
</template>
<script>
import _ from 'lodash'
export default{
  props: {
    placeholder: {
      type: String,
      default: ''
    }
  },
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
        return this.suggestions.filter(u => u.NAME.toLowerCase().includes(this.name.toLowerCase()))
      } else {
        return null
      }
    }
  },
  methods: {
    sendRequest (template) {
      this.axios.get('/users/by-name/' + template).then(response => {
        this.suggestions = response.data
      })
    },
    search: _.debounce(function (template) {
      this.sendRequest(template)
    }, 400, 800),
    delayedSearch (template) {
      if (!template) return
      if (!(this.lastTemplate && template.includes(this.lastTemplate))){
        if(template.length >= 2){
          this.search(template)
          this.lastTemplate = template
        }
      }
    },
    selectUser (index) {
      this.$emit('select', this.suggestionsToShow[index])
      this.name = ''
    }
  }
}
</script>
<style>
.user-selector-input{
    border: 0;
    width: 100%;
}
.user-selector-suggestions{
    position: absolute;
    background-color: #f1f1f1;
    box-shadow: 0 2px 2px 2px #30303060;
    width: 100%;
    z-index: 1001;
    text-align: left;
    padding-left: .2rem;
}
.user-suggestion-item{
    cursor: pointer;
    margin: .2rem auto;
    transition: .2s;
}
.user-suggestion-item:hover{
    background-color: #eeeeee;
}
</style>