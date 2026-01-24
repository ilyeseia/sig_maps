<template></template>

<script>
import { LayerSymbolizer } from '../style'
export default {
  props: ['layerToEdit', 'label'],
  data() {
    return {
      labelingEnabled: null,
    }
  },
  methods: {
    saveHeatMapSymbology() {
      let style = {
        slug: this.layerToEdit.slug,
        topo: this.layerToEdit.topo,
      }
      return LayerSymbolizer.generateHeatMap(style)
    },
    async onSubmit() {
      let heatMapStyle = {
        symbologyType: 'Heatmap',
        transformation: {
          name: 'Heatmap'
        },
        rules: [
       {
         icon: {
           name: "dfdf"
         }
       }
        ],
      }
      this.$emit('saveStyle', heatMapStyle)
    },
  },
}
</script>

<style scoped>
</style>