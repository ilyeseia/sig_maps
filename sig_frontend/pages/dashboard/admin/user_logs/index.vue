<template>
  <div class="w-100" style="max-height: 100%">
    <Table />
  </div>
</template>
<script>
import Table from '../../../../components/dashboard/admin/user_logs/Table'
import pageTitle from '~/mixins/page-title'
export default {
  layout: 'dashboard',
  mixins: [pageTitle],
  data() {
    return {
      page: {
        title: 'Journal',
        hid: '',
        name: '',
        description: '',
      },
    }
  },
  validate({ params, query, store }) {
    return store.state.profile.roles.includes('ROLE_ADMIN')
  },
  components: { Table },
}
</script>
