<template>
  <div class="w-100" style="max-height: 100%">
    <Table />
  </div>
</template>
<script>
import Table from '../../../../components/dashboard/admin/sessions/Table'
import pageTitle from '~/mixins/page-title'

export default {
  layout: 'dashboard',
  mixins: [pageTitle],
  validate({ params, query, store }) {
    return (
      store.state.profile.roles.includes('ROLE_ADMIN') ||
      store.state.profile.roles.includes('USER_READ_AUTHORITY')
    )
  },
  data() {
    return {
      page: {
        title: 'Gestion des sessions',
        hid: '',
        name: '',
        description: '',
      },
    }
  },
  components: { Table },
  methods: {},
}
</script>
