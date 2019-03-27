<template>
    <div class="column-list-component">
        <div class="column-list-header">
            <span>Fields</span>
        </div>
        <div>
            <div v-for="(v, k) in value" class="column-list-item">
                <!-- label :for="'column_' + k">{{ k }}</label>
                <input :id="'column_' + k" type="checkbox" :checked="v" @change="changeColumn(k, $event.target.checked)" -->
                <b-form-checkbox v-model="value[k]" @input="saveConfig" :unchecked-value="false">
                    {{ formatString(k) }}
                </b-form-checkbox>
            </div>
        </div>
    </div>
</template>
<script>
export default{
  props: {
    value: {
      type: Object,
      default: function () {
        return {}
      }
    }
  },
  methods: {
    saveConfig () {
      this.$emit('input', this.value)
    },
    formatString (string) {
      return string.replace('_', ' ')
    }
  }
}
</script>
<style>
.column-list-header{
    text-align: center;
    font-weight: bold;
    font-size: 1.2rem;
    border-bottom: 1px solid #30303080;
    margin-bottom: .3rem;
}
.column-list-component{
    text-align: left;
    font-family: Segoe UI,Frutiger,Frutiger Linotype,Dejavu Sans,Helvetica Neue,Arial,sans-serif;
}
.column-list-item{
    margin: .3rem .4rem;
    text-transform: capitalize;
}
</style>
