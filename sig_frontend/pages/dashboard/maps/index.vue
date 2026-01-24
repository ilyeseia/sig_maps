<template>
  <div class="w-100" style="max-height: 90%">
    <Table
      @shareMap="onShareMap"
      @newMap="showModal = true"
      @editMap="onEditMap"
    />
    <b-modal :active.sync="showModal" has-modal-card @close="mapToEdit = null">
      <NewMap @saved="showModal = false; mapToEdit = null" :mapToEdit="mapToEdit" />
    </b-modal>
    <b-modal
      :active.sync="showUsersModal"
      has-modal-card
      @close="mapToEdit = null"
    >
      <Users
        @saved="showUsersModal = false"
        :mapToShare="mapToShare"
        :usersInMap="usersInMap"
        :groupsInMap="groupsInMap"
      />
    </b-modal>
  </div>
</template>

<script>
import Table from '~/components/dashboard/maps/Table'
import NewMap from '~/components/dashboard/maps/NewMap'
import Users from '~/components/dashboard/maps/Users'
import { backApi } from '~/methods/serverApi'
import pageTitle from '~/mixins/page-title'

export default {
  validate({ params, query, store }) {
    return (
      store.state.profile.roles.includes('ROLE_ADMIN') ||
      store.state.profile.roles.includes('MAP_READ_AUTHORITY')
    )
  },
  layout: 'default',
  mixins: [pageTitle],
  data() {
    return {
      showModal: false,
      showUsersModal: false,
      mapToEdit: null,
      mapToShare: null,
      usersInMap: null,
      groupsInMap: null,
      page: {
        title: "Cartes",
        hid: '',
        name: '',
        description: '',
      },
    }
  },
  components: { Table, NewMap, Users },
  methods: {
    onEditMap(map) {
      this.mapToEdit = this.$store.state.maps.maps.find(
        ({ id }) => id === map.id
      )
      this.showModal = true
    },
    onShareMap(map) {
      backApi
        .get(`maps/mapSharedWithOthers/all/${map.id}`, {
          params: {
            page: 0,
            limit: 10,
            sort: 'createDate',
            dir: 'asc',
          },
        })
        .then(({ data }) => {
          this.mapToShare = data
          this.showUsersModal = true
        })
    },
  },
}
</script>

<style>
</style>
