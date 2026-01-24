<template>
  <form @submit.prevent="onSubmit">
    <div
      id="globalFliter"
      class="
        d-flex
        flex-column
        position-absolute
        border border-light
        global-filter
      "
    >
      <div class="p-2 bg-white text-dark">
        <b-field>
          <b-input
            v-model="searchText"
            placeholder="Taper ce que vous voulez dans les données"
          ></b-input>
        </b-field>
      </div>
      <div
        class="bg-white text-dark"
        v-if="showDatatable"
        style="max-width: 640px; overflow-y: auto; overflow-x: hidden"
      >
        <b-table
          :current-page="page"
          :data="dataTable"
          paginated
          backend-pagination
          :per-page="perPage"
          :total="totalRecords"
          @page-change="onPageChange"
          :show-detail-icon="true"
          backend-sorting
          :default-sort-direction="defaultSortOrder"
          :default-sort="[sortField, sortOrder]"
          @sort="onSort"
        >
          <template slot-scope="props">
            <b-table-column field="id" :visible="false" label="Id" sortable>{{
              props.row.id
            }}</b-table-column>
            <b-table-column field="identifiant" label="Identifiant" sortable>{{
              props.row.identifiant
            }}</b-table-column>
            <b-table-column field="layerSlug" label="Couche" sortable>{{
              props.row.layerSlug.includes('____')
                ? props.row.layerSlug.split('____')[1]
                : props.row.layerSlug
            }}</b-table-column>
            <b-table-column field="value" label="Valeur" sortable>{{
              props.row.value
            }}</b-table-column>
            <b-table-column label="Visualiser" centered class="p-1 m-0">
              <b-tooltip label="Aller à" type="is-dark" position="is-bottom">
                <Button
                  icon="pi pi-map-marker"
                  class="
                    p-button-rounded p-button-warning p-mr-2
                    show-feature-btn
                  "
                  @click="showFeature(props.row.id)"
                />
              </b-tooltip>
            </b-table-column>
          </template>
          <template slot="empty"> Aucun resultat trouvé </template>
        </b-table>
        <p
          v-if="totalRecords > 0"
          style="margin-left: 10px; margin-top: -25px; margin-bottom: 10px"
        >
          <i>Total</i> : <strong>{{ totalRecords }}</strong>
        </p>
      </div>

      <div class="p-2 bg-white text-dark">
        <button
          :class="['button', 'is-primary', { 'is-loading': isFilterLoading }]"
          id=" filter-layer"
          type="submit"
        >
          <b-icon class="mr-1" pack="fas" icon="filter" />Filtrer
        </button>

        <button
          id="resetAll"
          type="button"
          class="button is-success"
          style="margin-right: 60px"
          @click="resetAll"
        >
          <b-icon class="mr-1" pack="fas" icon="sync-alt" />Réinitialiser
        </button>
      </div>
    </div>
  </form>
</template>
<script>
import Vue from 'vue'
import { mapGetters, mapState } from 'vuex'

import { backApi } from '~/methods/serverApi'
import RestApi from '../../methods/api'
import { SecuredPublicWMSURL } from '../../constants'
import { QueryBuilderPlugin } from '@syncfusion/ej2-vue-querybuilder'
import { ButtonPlugin } from '@syncfusion/ej2-vue-buttons'
import { DropDownListPlugin } from '@syncfusion/ej2-vue-dropdowns'
import { GridPlugin } from '@syncfusion/ej2-vue-grids'
import { DateTimePickerPlugin } from '@syncfusion/ej2-vue-calendars'
import { CheckBoxPlugin } from '@syncfusion/ej2-vue-buttons'
import { enableRipple } from '@syncfusion/ej2-base'
import Button from 'primevue/button'

enableRipple(true)
Vue.use(CheckBoxPlugin)
Vue.use(DateTimePickerPlugin)
Vue.use(GridPlugin)
Vue.use(DropDownListPlugin)
Vue.use(ButtonPlugin)
Vue.use(QueryBuilderPlugin)

export default {
  props: ['toggleKey'],
  components:  {
    Button
  },
  data: function () {
    return {
      searchText: "",
      isFilterLoading: false,
      sortField: 'createDate',
      sortOrder: 'asc',
      defaultSortOrder: 'asc',
      perPage: 10,
      page: 1,
      totalRecords: 0,
      centroPoint: [],
      dataTable: [],
      showDatatable: false,
    }
  },
  computed: {
    ...mapState(['profile']),
    ...mapGetters({
      currentMap: 'maps/getCurrentMap',
    }),
  },
  methods: {
    onSubmit(e) {
      this.page = 1
      this.doFilter()
    },
    addLayerToMap(filters) {
      let uniqueSlug = [...new Set(filters.map((item) => item.slug))]

      this.clearLayer()

      uniqueSlug.forEach((slug) => {
        let uniqueLayers = filters.filter((layer) => layer.slug == slug)

        let custom_filter = this.addCqlFilter(uniqueLayers.map((l) => l.id))
        let style =
          'filter_style_' +
          (uniqueLayers[0].topo !== null
            ? uniqueLayers[0].topo.toLowerCase()
            : '')

        this.$layerGroups[`${slug}_filter`] = L.tileLayer
          .wms(`${SecuredPublicWMSURL}`, {
            layers: `limite_admin:${slug}`,
            format: 'image/png',
            styles: style,
            transparent: true,
            cql_filter: custom_filter,
          })
          .setZIndex(1)
          .addTo(this.$map)
      })

      /* let ids = []
      uniqueLayers.forEach(l => {
        ids.push(l.id)
      })*/

      // let layers = this.currentMap.layers

      /* uniqueLayers.forEach(layer => {
        let style = 'filter_style_' + layer.topo.toLowerCase()

        this.$layerGroups[`${slug}_filter`] = L.tileLayer
          .wms(`${SecuredPublicWMSURL}`, {
            layers: `limite_admin:${slug}`,
            format: 'image/png',
            styles: style,
            transparent: true,
            cql_filter: custom_filter
          })
          .setZIndex(9999)
          .addTo(this.$map)
      })*/
    },
    addCqlFilter(params) {
      let filter = ''
      for (let i = 0; i < params.length; i++) {
        if (i == params.length - 1) {
          filter += "id ='"
          filter += params[i]
          filter += "'"
        } else {
          filter += "id ='"
          filter += params[i]
          filter += "' or "
        }
      }
      return filter
    },
    calculateBbox(features) {
      var lats = []
      var lngs = []

      features.forEach((feature) => {
        let featureJSon = JSON.parse(feature['featureJson'])
        var coords = featureJSon.geometry
          ? featureJSon.geometry.coordinates
          : null
        if (coords) {
          if (featureJSon.geometry.type === 'MultiPolygon') {
            let multipolygone = coords[0]

            for (let i = 0; i < multipolygone[0].length; i++) {
              lats.push(multipolygone[0][i][1])
              lngs.push(multipolygone[0][i][0])
            }
          }
          if (featureJSon.geometry.type === 'Polygon') {
            /*  this.getCentroId(feature.id).then(res => {
            lats.push(parseFloat(res[1]))
            lngs.push(parseFloat(res[0]))
          })*/
            let polygone = coords[0]

            for (let i = 0; i < polygone.length; i++) {
              lats.push(polygone[i][1])
              lngs.push(polygone[i][0])
            }
          }
          if (featureJSon.geometry.type === 'LineString') {
            let lineString = coords
            for (let i = 0; i < lineString.length; i++) {
              lats.push(lineString[i][1])
              lngs.push(lineString[i][0])
            }
          }
          if (featureJSon.geometry.type === 'Point') {
            lats.push(coords[1])
            lngs.push(coords[0])
          }
        }
      })

      // calc the min and max lng and lat
      var minlat = Math.min.apply(null, lats),
        maxlat = Math.max.apply(null, lats)
      var minlng = Math.min.apply(null, lngs),
        maxlng = Math.max.apply(null, lngs)

      // create a bounding rectangle that can be used in leaflet
      var bbox = [
        [minlat, minlng],
        [maxlat, maxlng],
      ]

      return bbox
    },
    // getCentroIdJS(shape) {
    //   var arr = shape[0]
    //   var center = null
    //   var minX, maxX, minY, maxY

    //   for (var i = 0; i < arr.length; i++) {
    //     minX = arr[i][0] < minX || minX == null ? arr[i][0] : minX
    //     maxX = arr[i][0] > maxX || maxX == null ? arr[i][0] : maxX
    //     minY = arr[i][1] < minY || minY == null ? arr[i][1] : minY
    //     maxY = arr[i][1] > maxY || maxY == null ? arr[i][1] : maxY
    //   }
    //   center = [(minX + maxX) / 2, (minY + maxY) / 2]

    //   return center
    // },
    // getCentroId(_id) {
    //   let point = null
    //   backApi
    //     .post(`entityelements/centroid/${_id}`)
    //     .then(({ data }) => {
    //       var str = data.split(/[()]/)
    //       point = str[1].split(' ')
    //       this.goTo(point[1], point[0])
    //     })

    //     .catch((error) => {})
    // },
    doFilter() {
      const _layers = this.currentMap.layers

      let globalFilter = {
        searchText: this.searchText.trim(),
        layersSlug: [],
        fieldsSlug: [],
        layerIds: `(${_layers.map((l) => `'${l.id}'`).join(', ')})`,
      }

      if (_layers) {
        _layers.forEach((layer) => {
          globalFilter.layersSlug.push(layer.slug)
          layer.fields.forEach((field) => {
            globalFilter.fieldsSlug.push(field.slug)
          })
        })
      }

      this.isFilterLoading = true
      let url = this.profile.authenticated
        ? `entityelements/maps/${this.currentMap.slug}/globalSearch`
        : `entityelements/public/globalSearch`
      backApi
        .post(url, globalFilter, {
          params: {
            page: this.page - 1,
            limit: this.perPage,
            sort: this.sortField,
            dir: this.sortOrder,
            useFieldSlug: true,
          },
        })
        .then(({ data }) => {
          if (data != null) {
            this.isFilterLoading = false

            this.dataFeatures = data.content
            this.totalRecords = data.totalElements

            this.dataTable = []
            let filters = []

            this.dataFeatures.forEach((feature) => {
              let featureJson = JSON.parse(feature['featureJson'])
              let filter = new Object()
              filter.id = feature.id
              filter.slug = feature.layerSlug
              filter.topo = featureJson.geometry
                ? featureJson.geometry.type
                : null

              filters.push(filter)
              const layer = this.currentMap.layers.find(
                (layer) => layer.slug == feature.layerSlug
              )

              var object = new Object()

              object.id = feature.id
              object.layerSlug = layer.name

              let identifiant = layer.fields.filter(
                (field) => field.slug === feature.layerIdentifiant
              )

              if (identifiant[0].type === 'SELECT') {
                object.identifiant = identifiant[0].name
                object.value = RestApi.extractSelectedRV(
                  featureJson.properties[feature.layerIdentifiant]
                )
              } else {
                object.identifiant = identifiant[0].name
                object.value = featureJson.properties[feature.layerIdentifiant]
              }

              this.dataTable.push(object)
            })

            this.addLayerToMap(filters)

            this.showDatatable = true

            this.$map.fitBounds(this.calculateBbox(this.dataFeatures))
          }
        })
        .catch((error) => {
          this.dataTable = []
          this.totalRecords = 0
          this.isFilterLoading = false
          this.showDatatable = true
          this.loadAllLayers()
        })
    },
    onSort(field, order) {
      this.sortField = field
      this.sortOrder = order
      this.doFilter()
      if (this.$layerGroups['customMarker'] != null) {
        this.$map.removeLayer(this.$layerGroups['customMarker'])
      }
    },
    onPageChange(page) {
      this.page = page
      this.doFilter()
      if (this.$layerGroups['customMarker'] != null) {
        this.$map.removeLayer(this.$layerGroups['customMarker'])
      }
    },
    showFeature(_id) {
      let _feature = this.dataFeatures.find((feature) => feature.id === _id)
      if (JSON.parse(_feature['featureJson']).geometry) {
        let topo = JSON.parse(_feature['featureJson']).geometry.type
        let coords = JSON.parse(_feature['featureJson']).geometry.coordinates
        this.$emit('goToElement', { coords, topo })
      }
    },
    clearLayer() {
      this.currentMap.layers.forEach((layer) => {
        if (this.$layerGroups[`${layer.slug}_filter`] != null) {
          this.$map.removeLayer(this.$layerGroups[`${layer.slug}_filter`])
        }
      })

      if (this.$layerGroups['customMarker'] != null)
        this.$map.removeLayer(this.$layerGroups['customMarker'])
    },
    loadAllLayers() {
      this.clearLayer()
      this.$map.setView([31.5, 3], 5)
      if (this.$layerGroups['customMarker'] != null) {
        this.$map.removeLayer(this.$layerGroups['customMarker'])
      }
    },
    cancel() {
      this.resetAll()
      this.$emit('hide')
    },
    resetAll() {
      this.searchText = ''

      this.loadAllLayers()

      this.showDatatable = false

      this.dataTable = []

      this.showSpatial = false

      this.page = 1
    },
    generatePulsatingMarker(radius, color) {
      const cssStyle = `
    width: ${radius}px;
    height: ${radius}px;
    background: ${color};
    color: ${color};
    box-shadow: 20px 20px 20px ${color};
  `
      return L.divIcon({
        html: `<span style="${cssStyle}" class="pulse"/>`,
        className: '',
        iconSize: [20, 20],
      })
    },
  },
  mounted() {
    // const filter = document.querySelector('.global-filter')
    // const leafletControl = document.querySelector('.leaflet-right')
    // setTimeout(() => {
    //   filter.classList.add("opened")
    // }, 200)
    // let resizeObserver = new ResizeObserver(() => {
    //   this.$emit('opened', filter.clientWidth)
    // })
    // resizeObserver.observe(filter)
  },
}
</script>
<style>
.b-table .level:not(.top) {
  padding: 0 !important;
}
table tr {
  line-height: 10px;
  vertical-align: middle;
}

.table td {
  line-height: 1.5;
  vertical-align: middle;
}
.pulse {
  display: block;
  border-radius: 70%;
  cursor: pointer;
  animation: pulse 1s infinite;
}
@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0;
  }
  70% {
    box-shadow: 0 0 0 10px rgba(0, 0, 0, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(0, 0, 0, 0);
  }
}
</style>
<style>
@import '@syncfusion/ej2-vue-dropdowns/styles/material.css';
@import '@syncfusion/ej2-base/styles/material.css';
@import '@syncfusion/ej2-buttons/styles/material.css';
@import '@syncfusion/ej2-splitbuttons/styles/material.css';
@import '@syncfusion/ej2-dropdowns/styles/material.css';
@import '@syncfusion/ej2-inputs/styles/material.css';
@import '@syncfusion/ej2-lists/styles/material.css';
@import '@syncfusion/ej2-popups/styles/material.css';
@import '@syncfusion/ej2-calendars/styles/material.css';
@import '@syncfusion/ej2-vue-querybuilder/styles/material.css';
@import '@syncfusion/ej2-vue-grids/styles/material.css';
@import '@syncfusion/ej2-vue-calendars/styles/material.css';
</style>
