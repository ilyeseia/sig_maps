<template>
  <div class="w-100" style="max-height: 100%">
    <GroupTable
      @newGroup="showGroupModal = true"
      @editGroup="onEditGroup"
      @viewGroup="OnViewGroup"
    />
    <b-modal :active.sync="showGroupModal" has-modal-card @close="groupToEdit = null">
      <NewGroup @saved="showGroupModal = false; groupToEdit = null" :groupToEdit="groupToEdit" />
    </b-modal>
    <b-modal :active.sync="viewGroupModal" has-modal-card @close="groupToEdit = null">
      <ViewGroup :groupToEdit="groupToEdit" />
    </b-modal>
  </div>
</template>
<script>
import { backApi } from '~/methods/serverApi'
import GroupTable from '../../../../components/dashboard/admin/GroupTable'
import NewGroup from '../../../../components/dashboard/admin/NewGroup'
import ViewGroup from '../../../../components/dashboard/admin/ViewGroup'
import pageTitle from '~/mixins/page-title'

export default {
  layout: 'dashboard',
  mixins: [pageTitle],
  validate({ params, query, store }) {
    return (
      store.state.profile.roles.includes('ROLE_ADMIN') ||
      store.state.profile.roles.includes('GROUP_READ_AUTHORITY')
    )
  },
  data() {
    return {
      showGroupModal: false,
      viewGroupModal: false,
      groupToEdit: null,
      page: {
        title: "Gestion des groupes",
        hid: '',
        name: '',
        description: '',
      },
    }
  },
  components: { GroupTable, NewGroup, ViewGroup },
  methods: {
    onEditGroup(_id) {
      backApi
        .get(`groups/groupSharedWithOthers/all/${_id}`)
        .then(({ data }) => {
          this.groupToEdit = data
          this.showGroupModal = true
        })
    },
    OnViewGroup(_id) {
      this.groupToEdit = this.$store.state.groups.groups.find(
        ({ id }) => id === _id
      )
      this.viewGroupModal = true
    },
    
  }
}
</script>
