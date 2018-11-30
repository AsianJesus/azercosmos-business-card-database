<template>
    <div class="column-list-component">
        <h1>Column selector</h1>
        <span v-for="(v, k) in value">
            <label :for="'column_' + k">{{ k }}</label>
            <input :id="'column_' + k" type="checkbox" :checked="v" @change="changeColumn(k, $event.target.checked)">
        </span>
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
    changeColumn (field, value) {
      let result = this.value
      result[field] = value
      this.$emit('input', result)
    }
  }
}
</script>
<style>

</style>
