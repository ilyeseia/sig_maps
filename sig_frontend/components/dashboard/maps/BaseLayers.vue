<template>
  <div
    id="baseLayers"
    class="d-flex flex-column position-absolute border border-light base-layers"
  >
    <div id="base-layers-wrapper"></div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  watch: {
    baseLayers(val) {
      val && this.setBaseLayers()
    },
  },
  computed: {
    ...mapGetters({
      baseLayers: 'app/getBaseLayers',
    }),
  },
  methods: {
    setBaseLayers() {
      let actionsShortcut = document.querySelector('#base-layers-wrapper')
      let precElement = document.querySelector("#base-layers-wrapper .leaflet-control-layers-base");
      if(precElement != null){
        precElement.remove()
      }
      if (actionsShortcut && this.baseLayers) {
        actionsShortcut.appendChild(this.baseLayers)
      }
    },
  },
}
</script>
<style lang="scss">
.base-layers {
  overflow: hidden !important;
  min-width: 320px !important;
  width: 330px !important;
  padding-top: 0.8rem;
}
#base-layers-wrapper {
  width: 100%;
  border: none !important;
  .leaflet-control-layers-base {
    label > div {
      padding-top: 3px !important;
    }
  }
}
</style>