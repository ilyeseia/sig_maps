<template>
  <div class="w-100" style="max-height: 100%">
    <Table
      @newLayer="showModal = true; layerToEdit = null"
      @uploadLayer="showImportationModal = true"
      @editLayer="onEditLayer"
      @tagLayer="onTagLayer"
      @shareLayers="onShareLayers"
      @cloneLayer="onCloneLayer"
    />
    <b-modal
      :active.sync="showModal"
      has-modal-card
      :can-cancel="false"
      @close="layerToEdit = null"
    >
      <NewLayer
        @saved="
          showModal = false
          layerToEdit = null
        "
        :layerToEdit="layerToEdit"
      />
    </b-modal>
    <b-modal
      :can-cancel="false"
      :active.sync="showImportationModal"
      has-modal-card
    >
      <Importation @imported="showImportationModal = false" />
    </b-modal>
    <b-modal
      :active.sync="showShareModal"
      has-modal-card
      @close="layerToShare = null"
    >
      <Share @saved="showShareModal = false" :layerToShare="layerToShare" />
    </b-modal>
    <b-modal
      :can-cancel="false"
      :active.sync="showCloneModal"
      has-modal-card
      @close="layerToEdit = null"
    >
      <CloneLayer
        @saved="showCloneModal = false"
        :layerToClone="layerToEdit"
      ></CloneLayer>
    </b-modal>
  </div>
</template>

<script>
import Table from '~/components/dashboard/layers/Table'
import NewLayer from '~/components/dashboard/layers/NewLayer'
import CloneLayer from '~/components/dashboard/layers/CloneLayer'
import Importation from '~/components/dashboard/layers/Importation'
import Share from '~/components/dashboard/layers/Share.vue'
import { backApi } from '~/methods/serverApi'

import pageTitle from '~/mixins/page-title'

export default {
  layout: 'dashboard',
  mixins: [pageTitle],
  validate({ store }) {
    return (
      store.state.profile.roles.includes('ROLE_ADMIN') ||
      store.state.profile.roles.includes('LAYER_READ_AUTHORITY')
    )
  },
  data() {
    return {
      showModal: false,
      showImportationModal: false,
      showShareModal: false,
      showCloneModal: false,
      layerToEdit: null,
      layerToShare: null,
      page: {
        title: 'Gestion des couches',
        hid: '',
        name: '',
        description: '',
      },
    }
  },
  components: { Table, Share, NewLayer, CloneLayer, Importation },
  methods: {
    onTagLayer() {},
    onEditLayer(_id) {
      backApi
        .get(`layers/withFieldsAndResource/edit/${_id}`)
        .then(({ data }) => {
          this.$store.commit('maps/setActiveLayer', data)
          this.layerToEdit = data
          this.showModal = true
        })
    },
    onShareLayers(_id) {
      backApi
        .get(`layers/layerSharedWithOthers/all/${_id}`)
        .then(({ data }) => {
          this.layerToShare = data
          this.showShareModal = true
        })
    },
    onCloneLayer(layer) {
      this.showCloneModal = true
      this.layerToEdit = layer
    },
  }
}
</script>
