<template>
  <div class="w-100" style="max-height: 100%">
    <Table @newSetting="showModal = true" @editSetting="onEditSetting" />
    <b-modal
      :active.sync="showModal"
      has-modal-card
      @close="settingToEdit = null"
    >
      <NewSetting @saved="showModal = false; settingToEdit = null" :settingToEdit="settingToEdit" />
    </b-modal>
  </div>
</template>

<script>
import Table from '~/components/dashboard/settings/Table'
import NewSetting from '~/components/dashboard/settings/NewSetting'
import pageTitle from '~/mixins/page-title'

export default {
  layout: 'dashboard',
  mixins: [pageTitle],
  validate({ params, query, store }) {
    return (
      store.state.profile.roles.includes('ROLE_ADMIN') ||
      store.state.profile.roles.includes('SETTINGS_READ_AUTHORITY')
    )
  },
  data() {
    return {
      showModal: false,
      settingToEdit: null,
      isFullPage: true,
      page: {
        title: 'Gestion des paramètres',
        hid: '',
        name: '',
        description: '',
      },
    }
  },
  components: { Table, NewSetting },
  methods: {
    onEditSetting(_id) {
      this.settingToEdit = this.$store.state.settings.settings.find(
        ({ id }) => id === _id
      )
      this.showModal = true
    },
  },
}
</script>

<style></style>