<template>
  <div style="overflow: hidden auto">
    <b-loading :active="loaderIndicator" :is-full-page="false"></b-loading>
    <slot></slot>
  </div>
</template>

<script>
export default {
  props: ['loaderIndicator'],
}
</script>