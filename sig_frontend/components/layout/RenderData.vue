<template>
  <div>
    <b-field v-if="data && data.length === 0 && !loading">
      <b-message type="is-warning">{{ alertMessage }}</b-message>
    </b-field>
    <div v-else>
      <slot></slot>
    </div>
  </div>
</template>

<script>
export default {
  props: ['data', 'alertMessage', 'loading'],
}
</script>