<template>
  <div class="w-100" style="max-height: 100%">
    <Table
      @newSettingsType="showModal = true"
      @editSettingsType="onEditSettingsType"
    />
    <b-modal
      :active.sync="showModal"
      has-modal-card
      @close="settingsTypeToEdit = null"
    >
      <NewSettingsType
        @saved="showModal = false; settingsTypeToEdit = null"
        :settingsTypeToEdit="settingsTypeToEdit"
      />
    </b-modal>
  </div>
</template>

<script>
import Table from '~/components/dashboard/settingsType/Table'
import NewSettingsType from '~/components/dashboard/settingsType/NewSettingsType'
import pageTitle from '~/mixins/page-title'

export default {
  layout: 'dashboard',
  validate({ params, query, store }) {
    return (
      store.state.profile.roles.includes('ROLE_ADMIN') ||
      store.state.profile.roles.includes('SETTINGS_READ_AUTHORITY')
    )
  },
  mixins: [pageTitle],
  data() {
    return {
      showModal: false,
      settingsTypeToEdit: null,
      isFullPage: true,
      page: {
        title: 'Gestion des types de paramètres',
        hid: '',
        name: '',
        description: '',
      },
    }
  },
  components: { Table, NewSettingsType },
  methods: {
    onEditSettingsType(_id) {
      this.settingsTypeToEdit =
        this.$store.state.settingsType.settingsType.find(({ id }) => id === _id)
      this.showModal = true
    },
  },
}
</script>

<style></style>
