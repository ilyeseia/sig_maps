<template>
  <form @submit.prevent="onSubmit">
    <div
      id="advancedFilter"
      class="
        d-flex
        flex-column
        position-absolute
        border border-light
        global-filter
      "
    >
      <div class="modal-card">
        <section
          style="overflow-y: auto; overflow-x: hidden"
          class="modal-card-body"
        >
          <div class="p-2 bg-white text-dark">
            <b-select
              placeholder="Veuillez sélectionner l'opération spatiale"
              name="spatialOperation"
              v-model="selectedSlugLayer"
              required
              v-if="mode == 'map'"
              validation-message="Ce champ est obligatoire"
              expanded
            >
              <option v-for="op in layersSlug" :key="op.id" :value="op.slug">
                {{ op.name }}
              </option>
            </b-select>
          </div>
          <b-tabs class="global-filter__tab" v-model="activeTab">
            <b-tab-item>
              <div class="d-flex flex-column">
                <div class="p-2 bg-white text-dark">
                  <ejs-checkbox
                    id="spatialId"
                    label="Filtre Spatial"
                    v-model="showSpatial"
                    :checked="showSpatial"
                  ></ejs-checkbox>
                </div>
                <div class="p-2 bg-white text-dark" v-if="showSpatial">
                  <ejs-dropdownlist
                    id="regionsId"
                    :dataSource="slugLayerLimitAdmin"
                    placeholder="Sélectionnez une région"
                    v-on:change="loadAllRegions"
                    allowFiltering="{true}"
                    v-model="selectedRegion"
                  ></ejs-dropdownlist>
                </div>
              </div>
              <div class="global-filter__fields">
                <ejs-querybuilder
                  ref="querybuilder"
                  :ruleChange="updateRule"
                  v-if="selectedSlugLayer"
                  :showButtons="showButtons"
                >
                  <e-columns>
                    <e-column
                      field="spatial"
                      label="Spatial"
                      type="string"
                      :template="spatialTemplate"
                      :operators="spatialOperators"
                    />
                    <e-column
                      v-for="field in copyLayer.fields"
                      :field="field.slug"
                      :type="getFieldType(field.type)"
                      :label="field.name"
                      :key="field.id"
                      :operators="getFieldOperators(field.type)"
                      format="yyyy-MM-dd HH:mm:ss"
                      :template="getFieldTemplate(field.type)"
                    />
                    <e-column
                      field="createDate"
                      label="Date de création"
                      type="date"
                      :template="dateTemplate"
                      :operators="dateOperators"
                      format="yyyy-MM-dd HH:mm:ss"
                    />
                    <e-column
                      field="lastModifiedDate"
                      label="Derniere modification"
                      type="date"
                      :template="dateTemplate"
                      :operators="dateOperators"
                      format="yyyy-MM-dd HH:mm:ss"
                    />
                  </e-columns>
                </ejs-querybuilder>
              </div>
            </b-tab-item>
            <b-tab-item>
              <div class="global-filter__actions">
                <button
                  id="toggle-display"
                  type="button"
                  class="back-button"
                  @click="
                    () => {
                      activeTab = 0
                      showDatatable = false
                    }
                  "
                >
                  <b-icon pack="fas" icon="arrow-left" />
                </button>
                <div
                  class="p-2 bg-white"
                  v-if="
                    showDatatable &&
                    dataTable.length > 0 &&
                    exportEntityElemeentPermission[selectedSlugLayer]
                  "
                >
                  <b-dropdown aria-role="list">
                    <button
                      :class="[
                        'button',
                        'is-primary',
                        { 'is-loading': isExportLoading },
                      ]"
                      slot="trigger"
                    >
                      <span>Exporter</span>
                      <b-icon icon="menu-down"></b-icon>
                    </button>
                    <b-dropdown-item
                      @click="exportFeatures('geojson', 'geojson')"
                      aria-role="listitem"
                      >Exporter vers GeoJson</b-dropdown-item
                    >
                    <b-dropdown-item @click="exportFeatures('shp', 'zip')"
                      >Exporter vers Shape</b-dropdown-item
                    >
                    <b-dropdown-item @click="exportFeatures('csv', 'csv')"
                      >Exporter vers CSV</b-dropdown-item
                    >
                    <b-dropdown-item @click="exportFeatures('excel', 'xlsx')"
                      >Exporter vers Excel</b-dropdown-item
                    >
                  </b-dropdown>
                </div>
              </div>
              <div class="bg-white text-dark global-filter__results">
                <b-table
                  :data="dataTable"
                  paginated
                  backend-pagination
                  :per-page="perPage"
                  :total="totalRowsCount"
                  @page-change="onPageChange"
                  :show-detail-icon="true"
                  backend-sorting
                  :default-sort-direction="defaultSortOrder"
                  :default-sort="[sortField, sortOrder]"
                  @sort="onSort"
                >
                  <template slot-scope="props">
                    <b-table-column
                      field="id"
                      :visible="false"
                      label="Id"
                      sortable
                      >{{ props.row.id }}</b-table-column
                    >
                    <b-table-column
                      field="identifiant"
                      label="Identifiant"
                      sortable
                      >{{ props.row.identifiant }}</b-table-column
                    >
                    <b-table-column field="value" label="Valeur" sortable>{{
                      props.row.value
                    }}</b-table-column>
                    <b-table-column label="Visualiser" centered>
                      <b-tooltip
                        label="Aller à"
                        type="is-dark"
                        position="is-bottom"
                      >
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
                  v-if="totalRowsCount > 0"
                  style="margin-top: -25px; margin-bottom: 5px"
                >
                  <i>Total</i> : <strong>{{ totalRowsCount }}</strong>
                </p>
              </div>
            </b-tab-item>
          </b-tabs>
        </section>
        <footer v-if="!showDatatable" class="left-align modal-card-foot">
          <div class="p-2 text-dark">
            <button
              :class="[
                'button',
                'is-primary',
                { 'is-loading': isFilterLoading },
              ]"
              id=" filter-layer"
              type="submit"
              style="background: $color-primary !important"
              v-if="!showDatatable"
            >
              <b-icon class="mr-1" pack="fas" icon="filter" />Filtrer
            </button>

            <button
              id="resetAll"
              type="button"
              class="button is-success"
              @click="resetAll"
              v-if="!showDatatable"
            >
              <b-icon class="mr-1" pack="fas" icon="sync-alt" />Réinitialiser
            </button>
            <!-- <button id="cancel" class="button is-dark" @click="cancel">
              Fermer
            </button> -->
          </div>
        </footer>
      </div>
    </div>
  </form>
</template>
<script>
import Vue from 'vue'
import { backApi } from '~/methods/serverApi'
import {  SecuredWMSURL } from '../../constants'

import { mapState, mapGetters } from 'vuex'
import RestApi from '../../methods/api'
import advancedFilter from '~/mixins/advancedFilter'

import { QueryBuilderPlugin } from '@syncfusion/ej2-vue-querybuilder'
import { ButtonPlugin } from '@syncfusion/ej2-vue-buttons'
import { DropDownListPlugin } from '@syncfusion/ej2-vue-dropdowns'
import { GridPlugin } from '@syncfusion/ej2-vue-grids'
import { DropDownList } from '@syncfusion/ej2-dropdowns'
import { createElement, getComponent } from '@syncfusion/ej2-base'
import { DateTimePickerPlugin } from '@syncfusion/ej2-vue-calendars'
import { TimePickerPlugin } from '@syncfusion/ej2-vue-calendars'
import { CheckBoxPlugin } from '@syncfusion/ej2-vue-buttons'
import { enableRipple } from '@syncfusion/ej2-base'
import Button from 'primevue/button'
enableRipple(true)
Vue.use(CheckBoxPlugin)
Vue.use(TimePickerPlugin)
Vue.use(DateTimePickerPlugin)
Vue.use(GridPlugin)
Vue.use(DropDownListPlugin)
Vue.use(ButtonPlugin)
Vue.use(QueryBuilderPlugin)

export default {
  mixins: [advancedFilter],
  components: {
    Button,
  },
  data: function () {
    return {
      activeTab: 0,
      mode: this.$store.state.maps.mode,
      isFilterLoading: false,
      isExportLoading: false,
      sortField: 'createDate',
      sortOrder: 'asc',
      defaultSortOrder: 'asc',
      perPage: 10,
      page: 1,
      totalRowsCount: 0,
      currentMap: this.$store.state.maps.currentMap,
      features: this.$store.state.features.features,
      // showButtons: { groupInsert: false, groupDelete: true, ruleDelete: true },
      dataFeatures: [],
      objectLimitAdmin: [],
      currentLayer: {},
      copyLayer: {},
      layerFields: { value: 'slug', text: 'name' },
      selectedSlugLayer: null,
      showSpatial: false,
      selectedRegion: '',
      centroPoint: [],
      spatialOperators: [
        { value: 'ST_contains', key: 'Contient' },
        { value: 'ST_intersects', key: 'Intersecte' },
      ],
      dataTable: [],
      showDatatable: false,
      spatialTemplate: {
        create: () => {
          return createElement('input', { attrs: { type: 'text' } })
        },
        destroy: (args) => {
          let dropdownlist = getComponent(
            document.getElementById(args.elementId),
            'dropdownlist'
          )
          if (dropdownlist) dropdownlist.destroy()
        },
        write: (args) => {
          let ds = this.regionList
          let dropDownObj = new DropDownList({
            dataSource: ds,
            allowFiltering: true,
            fields: { text: 'text', value: 'value' },
            value: args.values,
            change: (e) => {
              this.$refs.querybuilder.ej2Instances.notifyChange(
                e.itemData.value,
                e.element
              )
            },
          })
          dropDownObj.appendTo('#' + args.elements.id)
        },
      },
      exportEntityElemeentPermission: {},
    }
  },
  watch: {
    selectedSlugLayer(val) {
      if (val) this.loadFields()
    },
  },
  computed: {
    ...mapState(['profile', 'regions']),
    ...mapGetters({
      layersSlug: 'maps/layersSlug',
      slugLayerLimitAdmin: 'maps/slugLayerLimitAdmin',
    }),
    regionList() {
      return this.regions.regions
    },
  },
  beforeMount() {
    this.$store.commit('regions/clear')
    if (this.profile.authenticated) this.getPermissions()
    if (this.mode == 'layer') {
      this.selectedSlugLayer = this.currentMap.layers[0].slug
      this.loadFields()
    }
  },
  methods: {
    onSubmit(e) {
      if (this.selectedSlugLayer === '') {
        this.$buefy.dialog.alert({
          title: 'Warnning message',
          message: "Sélectionnez une couche s'il vous plaît !!",
          confirmText: 'ok',
        })
      } else if (this.searchCriteria == '') {
        this.$buefy.dialog.alert({
          title: 'Warnning message',
          message: "Choisissez une condition s'il vous plaît",
          confirmText: 'ok',
        })
      } else if (this.showSpatial === true && this.selectedRegion === '') {
        this.$buefy.dialog.alert({
          title: 'Warnning message',
          message: "Sélectionnez une couche limite s'il vous plaît!",
          confirmText: 'ok',
        })
      } else {
        this.doFilter(this.searchCriteria)
      }
    },
    getPermissions() {
      if (this.profile.authenticated) {
        for (let l of this.layersSlug) {
          if (this.profile.roles.includes('ROLE_ADMIN')) {
            this.exportEntityElemeentPermission[l.slug] = true
          } else {
            backApi
              .get(
                `layers/check-write-permission/${l.slug}/ENTITY_ELEMENT_MULTI_EXPORT_AUTHORITY`
              )
              .then(() => {
                this.exportEntityElemeentPermission[l.slug] = true
              })
              .catch(() => {
                this.exportEntityElemeentPermission[l.slug] = false
              })
          }
        }
      }
    },
    addLayerToMap(custom_filter) {
      let _layer = this.currentMap.layers.find(
        (layer) => layer.slug == this.selectedSlugLayer
      )

      let style = null
      switch (_layer.topo) {
        case 'Polygon':
          style = 'filter_style_polygon'
          break
        case 'MultiPolygon':
          style = 'filter_style_multipolygon'
          break
        case 'LineString':
          style = 'filter_style_linestring'
          break
        case 'Point':
          style = 'filter_style_point'
          break
      }
      this.clearLayer()
      const token = localStorage.getItem('sigToken')

      this.$layerGroups['filterLayer'] = L.tileLayer
        .wms(`${SecuredWMSURL}`, {
          layers: `limite_admin:${this.selectedSlugLayer}`,
          format: 'image/png',
          styles: style,
          transparent: true,
          cql_filter: custom_filter,
          token,
        })
        .setZIndex(9999) /* ???? */
        .addTo(this.$map)
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
        var coords = featureJSon.geometry.coordinates

        if (featureJSon.geometry.type === 'MultiPolygon') {
          let multipolygone = coords[0]

          for (let i = 0; i < multipolygone[0].length; i++) {
            lats.push(multipolygone[0][i][1])
            lngs.push(multipolygone[0][i][0])
          }
        }
        if (featureJSon.geometry.type === 'Polygon') {
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
    /* getCentroId(_id) {
      const storedToken = localStorage.getItem('sigToken')

      axios({
        method: 'POST',
        headers: {
          Authorization: `Bearer ${storedToken}`
        },
        url: `${backend}/entityelements/centroid/${_id}`,
        data: {}
      })
        .then(({ data }) => {
          var str = data.split(/[()]/)
          var points = str[1].split(' ')
          this.goTo(points)
        })

        .catch(error => {})
    },*/
    doFilter(query) {
      const _layer = this.currentMap.layers.find(
        (layer) => layer.slug == this.selectedSlugLayer
      )
      query.rules = query.rules.map((r) => {
        if(r.operator === 'empty'){
          return {...r, operator: '=', value: ''}
        }else return r
      })
      this.isFilterLoading = true
      backApi
        .post(
          `entityelements/public/maps/${this.currentMap.slug}/search/${this.selectedSlugLayer}`,
          query,
          {
            params: {
              page: this.page - 1,
              limit: this.perPage,
              sort: this.sortField,
              dir: this.sortOrder,
              useFieldSlug: true,
            },
          }
        )
        .then(({ data }) => {
          if (data) {
            this.totalRowsCount = data.totalElements
            this.dataFeatures = data.content

            this.isFilterLoading = false
            this.dataTable = []
            let featuresIds = []
            let newLayers = []
            let showWarning = false
            this.dataFeatures.forEach((feature) => {
              let featureJson = JSON.parse(feature['featureJson'])

              feature.layer = _layer

              featuresIds.push(feature.id)

              var object = new Object()

              object.id = feature.id
              let identifiant = feature.layer.fields.filter(
                (field) => field.slug === feature.layerIdentifiant
              )
              if (
                feature.layerIdentifiant !== '' &&
                identifiant &&
                identifiant.length > 0 &&
                identifiant[0].type === 'SELECT'
              ) {
                object.identifiant = identifiant[0].name
                object.value = RestApi.extractSelectedRV(
                  featureJson.properties[feature.layerIdentifiant]
                )
              } else if (
                feature.layerIdentifiant !== '' &&
                identifiant &&
                identifiant.length > 0
              ) {
                object.identifiant = identifiant[0].name
                object.value = featureJson.properties[feature.layerIdentifiant]
              } else {
                showWarning = true
              }
              this.dataTable.push(object)
            })
            if (showWarning) {
              this.$notification.warning({
                message: 'Remarque!',
                description: `S'il vous plaît définir un identifiant pour la couche ${this.selectedSlugLayer} !`,
              })
            }
            /*this.$store.commit('features/clear')
            this.$store.commit('features/set', newLayers)*/

            this.addLayerToMap(this.addCqlFilter(featuresIds))

            this.showDatatable = true

            this.activeTab = 1

            this.$map.fitBounds(this.calculateBbox(this.dataFeatures))
          }
        })
        .catch((error) => {
          console.log(error)
          this.isFilterLoading = false
        })
    },
    onSort(field, order) {
      this.sortField = field
      this.sortOrder = order
      this.doFilter(this.searchCriteria)
      if (this.$layerGroups['customMarker']) {
        this.$map.removeLayer(this.$layerGroups['customMarker'])
      }
    },
    onPageChange(page) {
      this.page = page
      this.doFilter(this.searchCriteria)
      if (this.$layerGroups['customMarker']) {
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
    async exportFeatures(fileType, extension) {
      this.isExportLoading = true
      RestApi.exportData(
        this.selectedSlugLayer,
        fileType,
        extension,
        this.searchCriteria,
        this.$notification
      )
        .then(() => {
          this.isExportLoading = false
        })
        .catch((error) => {
          this.$notification.error({
            message: 'Erreur !',
            description:
              error.response && error.response.data
                ? error.response.data.message
                : "Une erreur inattendue s'est produite",
          })
          this.isExportLoading = false
        })
    },
    clearLayer() {
      if (this.$layerGroups && this.$layerGroups['filterLayer'] != null)
        this.$map.removeLayer(this.$layerGroups['filterLayer'])
      if (this.$layerGroups && this.$layerGroups['customMarker'])
        this.$map.removeLayer(this.$layerGroups['customMarker'])
    },
    loadAllLayers() {
      this.clearLayer()
      this.$map && this.$map.setView([31.5, 3], 5)
      if (this.$layerGroups && this.$layerGroups['customMarker']) {
        this.$map.removeLayer(this.$layerGroups['customMarker'])
      }
    },
    loadAllRegions() {
      this.$store.commit('regions/clear')
      if (this.selectedRegion) {
        let selectedLayer = this.currentMap.layers.find(
          (layer) => layer.slug == this.selectedRegion
        )
        if (selectedLayer) {
          backApi
            .get(
              `entityelements/${this.selectedRegion}/${selectedLayer.identifiant}`
            )
            .then(({ data }) => {
              if (data) {
                this.$store.commit('regions/set', data)
              }
            })
            .catch((error) => {})
        }
      }
    },
    updateRule: function (args) {
      this.searchCriteria = args.rule

      if (this.searchCriteria.rules.length === 0) {
        this.showDatatable = false
        this.loadAllLayers()
      }
    },
    loadFields() {
      this.resetAll()
      this.currentLayer = this.currentMap.layers.find(
        ({ slug }) => slug === this.selectedSlugLayer
      )
      this.copyLayer = JSON.parse(JSON.stringify(this.currentLayer))

      /* this.copyLayer.fields.forEach(function(field) {
        //field.name = field.name.toLowerCase()
        if (field.type !== 'DATE' && field.type !== 'NUMBER') {
          field.type = 'string'
        } else field.type = field.type.toLowerCase()
      })*/

      this.copyLayer.fields.push(
        { id: 'createdBy', name: 'Créer par', type: 'string' },
        { id: 'modifiedBy', name: 'Modifier par', type: 'string' }
      )
    },
    cancel() {
      this.resetAll()
      this.$emit('hide')
    },
    resetAll() {
      if (document.querySelectorAll('.e-rule-container').length != 0)
        document
          .querySelectorAll('.e-rule-container')
          .forEach((el) => el.remove())

      this.loadAllLayers()

      this.showDatatable = false

      this.dataTable = []

      this.showSpatial = false

      this.page = 1

      this.activeTab = 0
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
}
</script>
<style  lang="scss">
.global-filter,
.base-layers {
  position: relative;
  border-radius: 5px;
  background-color: white;
  z-index: 399;
  top: 11px;
  right: -370px;
  height: 'fit-content';
  overflow: hidden;
  min-width: 360px;
  max-height: 97%;
  .b-table {
    padding: 0;
  }
  @include respond('phone') {
    .b-table {
      padding: 0 0.5rem;
    }
  }
  &__actions {
    display: flex;
  }
  &__fields {
    overflow: hidden auto;
    height: 30%;
    max-height: 30%;
    @include hideScroll;
  }
  &__tab {
    position: relative;
    height: 100% !important;
    overflow: hidden !important;
    background-color: white;
    .tabs {
      display: none;
    }
    .tab-item {
      overflow: hidden;
    }
    .tab-content {
      position: static !important;
      padding: 1rem 0;
      @include respond('phone') {
        padding: 1rem;
      }
    }
  }
  .modal-card {
    width: fit-content !important;
  }
  .modal-card-foot {
    padding: 5px !important;
    min-width: 360px;
    justify-content: flex-start !important;
  }
  .modal-card-body {
    @include hideScroll;
    padding: 5px !important;
    min-width: 360px;
    @include respond('phone') {
      padding: 0 !important;
    }
  }
  @include respond('phone') {
    position: static !important;
    top: unset !important;
    right: unset !important;
    width: 100%;
    height: fit-content !important;
    max-height: 60vh !important;
    .modal-card {
      width: 100vw !important;
      max-width: 100vw !important;
    }
  }
}
.opened {
  right: 0 !important;
}
.back-button {
  border: none;
  height: fit-content;
  height: 32px;
  border-radius: 3px;
  margin: 0.5rem;
  @include respond('phone') {
    height: 27px;
  }
}
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
