<template>
  <div class="w-100" style="max-height:100%">

  </div>
</template>

<script>


export default {
  validate({ params, query, store }) {
    return store.state.profile.roles.includes('ROLE_ADMIN') || store.state.profile.roles.includes('MAP_READ_AUTHORITY')
  },
  data() {
    return {

    }
  },
  components: { },
  methods: {

  }
}
</script>

<style>
</style>
