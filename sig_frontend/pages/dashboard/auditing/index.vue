<template>
  <div class="w-100" style="max-height: 100%">
    <Table @viewInfo="viewInfo" />
    <b-modal
      :active.sync="viewInfoModal"
      has-modal-card
      @close="close"
    >
      <Info 
        :action='currentRow'
      />
    </b-modal>
  </div>
</template>
<script>

import Table from '~/components/dashboard/auditing/Table'
import Info from '~/components/dashboard/auditing/Info'
import pageTitle from '~/mixins/page-title'

export default {
  layout: 'dashboard',
  mixins: [pageTitle],
   validate({ params, query, store }) {
    return (
      store.state.profile.roles.includes('ROLE_ADMIN') ||
      store.state.profile.roles.includes('AUDITING')
    )
  },
  data() {
    return {
      currentRow: null,
      viewInfoModal: false,
      page: {
        title: 'Traçabilité',
        hid: '',
        name: '',
        description: '',
      },
    }
  },
  components: {
    Table,
    Info,
  },
  methods: {
    viewInfo(action) {
      this.currentRow = action
      this.viewInfoModal = true
    },
    close(){
      this.viewInfoModal = false
    }
  },
}
</script>