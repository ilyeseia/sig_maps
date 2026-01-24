<template>
  <div>
    <div
      v-if="isShortcut && !isMobile"
      class="d-flex nearby-filter__close justify-content-end mb-3"
    >
      <button
        id="close"
        type="button"
        class="button is-primary is-pulled-left"
        @click="close"
        style="background: #ff3860 !important"
      >
        <b-icon pack="fas" icon="times"></b-icon>
      </button>
    </div>
    <section
      id="nearby"
      :style="{ position: draw ? 'static' : 'abosolute' }"
      class="nearby"
    >
      <b-tabs v-model="activeTab">
        <b-tab-item>
          <form @submit.prevent="onSubmit">
            <div class="nearby-filter">
              <div class="nearby-filter__fields">
                <div v-if="draw" class="d-flex mb-3">
                  <b-tooltip label="Cercle" type="is-dark" position="is-bottom">
                    <b-button
                      @click="startDraw('circle')"
                      :class="{
                        'mr-4': true,
                        active: selectedShape === 'circle' ? true : false,
                      }"
                    >
                      <b-icon pack="fas" icon="circle" />
                    </b-button>
                  </b-tooltip>
                  <b-tooltip
                    label="Rectangle"
                    type="is-dark"
                    position="is-bottom"
                  >
                    <b-button
                      @click="startDraw('square')"
                      :class="{
                        'mr-4': true,
                        active: selectedShape === 'square' ? true : false,
                      }"
                    >
                      <b-icon pack="fas" icon="square" />
                    </b-button>
                  </b-tooltip>
                  <b-tooltip
                    label="Polygone"
                    type="is-dark"
                    position="is-bottom"
                  >
                    <b-button
                      @click="startDraw('polygon')"
                      :class="{
                        'mr-4': true,
                        active: selectedShape === 'polygon' ? true : false,
                      }"
                    >
                      <b-icon pack="fas" icon="draw-polygon" />
                    </b-button>
                  </b-tooltip>
                  <b-tooltip label="Line" type="is-dark" position="is-bottom">
                    <b-button
                      @click="startDraw('lineString')"
                      :class="{
                        active: selectedShape === 'lineString' ? true : false,
                      }"
                    >
                      <b-icon pack="fas" icon="grip-lines" />
                    </b-button>
                  </b-tooltip>
                </div>
                <b-field label="Trouvez">
                  <b-select
                    v-model="selectedLayer"
                    placeholder="Veuillez sélectionner une couche"
                    required
                    expanded
                    validation-message="Ce champ est requis"
                  >
                    <option
                      v-for="layer in mapLayers"
                      :value="layer"
                      :key="layer.id"
                      :id="layer.id"
                    >
                      {{
                        layer.name.includes('____')
                          ? layer.name.split('____')[1]
                          : layer.name
                      }}
                    </option>
                  </b-select>
                </b-field>
                <template v-if="!draw">
                  <b-field label="Qui sont">
                    <b-select v-model="operation" expanded>
                      <option value="DWITHIN">Plus proches que</option>
                      <option value="BEYOND">Plus loin que</option>
                    </b-select>
                  </b-field>
                  <div class="d-flex" style="justify-content: space-between">
                    <b-field label="Unité">
                      <b-select name="unit" v-model="unit">
                        <option value="meters">Mettre</option>
                        <option value="feet">Pieds</option>
                        <option value="kilometers">Kilomètre</option>
                        <option value="miles">Milles</option>
                      </b-select>
                    </b-field>
                    <b-field
                      style="flex-grow: 1; margin-left: 15px"
                      :label="`${getLabel}(s) de`"
                    >
                      <b-input
                        :min="0"
                        name="perimeter"
                        v-model="perimeter"
                        type="number"
                      ></b-input>
                    </b-field>
                  </div>
                </template>
                <div v-else>
                  <b-field label="Operation Algèbrique">
                    <b-select name="unit" v-model="algebraOperation" expanded>
                      <option value="INTERSECTION">Intersection</option>
                      <option value="COVER">Couverture</option>
                    </b-select>
                  </b-field>
                </div>
              </div>
              <div class="nearby-filter__actions">
                <button
                  v-if="displayFilter"
                  :disabled="getDisabled"
                  :class="['button', 'is-primary']"
                  id=" filter-layer"
                  type="submit"
                  style="background: $color-primary !important"
                >
                  <b-icon class="mr-1" pack="fas" icon="filter" />Filtrer
                </button>
                <button
                  :style="{ display: displayFilter ? 'none' : 'inline-block' }"
                  @click="showFilter"
                  id="resetAll"
                  type="button"
                  class="button"
                >
                  <b-icon class="mr-1" pack="fas" icon="sliders-h" />Changer
                </button>
                <button
                  @click="resetAll"
                  id="resetAll"
                  type="button"
                  class="button is-success"
                >
                  <b-icon
                    class="mr-1"
                    pack="fas"
                    icon="sync-alt"
                  />Réinitialiser
                </button>
              </div>
              <div class="nearby-filter__results">
                <b-loading
                  :active="isLoading"
                  :is-full-page="false"
                ></b-loading>
                <div v-if="!displayFilter">
                  <render-data
                    :data="results"
                    alertMessage="Il n'y a aucun résultat à afficher ?"
                    :loading="isLoading"
                  >
                    <h2 class="nearby-filter__title">
                      Résultats ({{ total }}) :
                    </h2>
                    <div
                      @click="showFeature(r.featureJson)"
                      v-for="r in results"
                      :key="r.id"
                      class="result"
                    >
                      <div class="result__heading">
                        <h2>
                          {{ getValue(r.featureJson.properties) }}
                        </h2>
                      </div>
                      <div class="result_actions"></div>
                    </div>
                  </render-data>
                  <infinite-loading @infinite="performApi">
                    <div slot="spinner">Chargement...</div>
                    <div slot="no-more">Plus de résultat</div>
                    <div slot="no-results"></div>
                  </infinite-loading>
                </div>
              </div>
            </div>
          </form>
        </b-tab-item>
        <b-tab-item v-if="selectedLayer">
          <button
            id="toggle-display"
            type="button"
            class="back-button"
            @click="back"
          >
            <b-icon pack="fas" icon="arrow-left" />
          </button>
          <feature-fields
            :fields="selectedLayer.fields"
            :newLayer="getProperties"
          ></feature-fields>
        </b-tab-item>
      </b-tabs>
    </section>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { backApi } from '~/methods/serverApi'
import RenderData from '~/components/layout/RenderData'
import FeatureFields from './FeatureFields'
import InfiniteLoading from 'vue-infinite-loading'

export default {
  props: ['featureId', 'geometry', 'draw', 'newLayer', 'isShortcut'],
  components: {
    RenderData,
    FeatureFields,
    InfiniteLoading,
  },
  data() {
    return {
      isLoading: false,
      displayFilter: true,
      selectedLayer: null,
      operation: 'DWITHIN',
      perimeter: 5,
      unit: 'kilometers',
      results: null,
      page: 0,
      size: 20,
      total: 0,
      activeTab: 0,
      selectedItem: null,
      algebraOperation: 'INTERSECTION',
      layer: null,
      geom: null,
      selectedShape: null,
      showHint: false,
      currentMap: this.$store.state.maps.currentMap,
      lastScrollHeight: 0,
    }
  },
  computed: {
    ...mapGetters({
      mapLayers: 'maps/mapLayers',
      isMobile: 'app/getIsMobile',
    }),
    getLabel() {
      switch (this.unit) {
        case 'kilometers':
          return 'Kilomètre'
        case 'meters':
          return 'Mettre'
        case 'feet':
          return 'Pied'
        case 'miles':
          return 'Milles'
      }
    },
    getProperties() {
      return {
        feature: {
          id: this.selectedItem && this.selectedItem.id,
          properties: { ...this.selectedItem },
          layer: this.selectedLayer,
        },
      }
    },
    getDisabled() {
      return (
        this.isLoading ||
        (this.draw && !this.geom && !this.isShortcut) ||
        (this.geom && this.geom.coordinates && this.geom.coordinates.length === 0 && !this.isShortcut) ||
        !this.selectedLayer
      )
    },
  },
  watch: {
    displayFilter(newVal, oldVal) {
      let filterSection = document.querySelector('.nearby-filter__fields')
      if (newVal != oldVal) {
        if (newVal === true) {
          setTimeout(() => {
            filterSection.style.marginTop = 0
          }, 300)
        } else if (newVal === false) {
          setTimeout(() => {
            filterSection.style.marginTop = this.draw
              ? `-${filterSection.clientHeight + 20}px`
              : `-${filterSection.clientHeight + 20}px`
          }, 300)
        }
      }
    },
    isShortcut(value) {
      if (!value) {
        this.resetAll()
      }
    },
  },
  methods: {
    back() {
      this.activeTab = 0
      if (this.isShortcut || this.draw) {
        document.querySelector('#proximityTool .content-wrapper').scrollTo(0, this.lastScrollHeight)
      } else {
        document.querySelector('.feature .tab-content').scrollTo(0, this.lastScrollHeight)
      }
    },
    close() {
      this.$map.removeLayer(this.$layerGroups['marker'])
      this.resetAll()
      this.$emit('hide')
    },
    getValue(r) {
      if (
        this.selectedLayer.fields.filter(
          (f) => f.slug === this.selectedLayer['identifiant']
        )[0] &&
        this.selectedLayer.fields.filter(
          (f) => f.slug === this.selectedLayer['identifiant']
        )[0].type === 'SELECT'
      ) {
        return  r[this.selectedLayer['identifiant']] != null && r[this.selectedLayer['identifiant']].split(':')[1] != ''
          ? r[this.selectedLayer['identifiant']].split(':')[1]
          : ''
      }
      return r[this.selectedLayer['identifiant']]
    },
    startDraw(shape) {
      this.selectedShape = shape
      this.$DrawLayer.clearLayers()
      this.$store.commit('maps/setDrawing', true)
      let doc
      switch (shape) {
        case 'lineString':
          doc = document.querySelector('.leaflet-draw-draw-polyline')
          break
        case 'polygon':
          doc = document.querySelector('.leaflet-draw-draw-polygon')
          break
        case 'circle':
          doc = document.querySelector('.leaflet-draw-draw-circle')
          break
        case 'square':
          doc = document.querySelector('.leaflet-draw-draw-rectangle')
          break
      }
      if (doc) doc.click()
      let toolbar = document.querySelector('.leaflet-draw-actions')
      if (toolbar) toolbar.style.display = 'none'
      this.$map.on('draw:created', (e) => {
        if (e.layerType == 'circle') {
          if (this.$layerGroups['bufferLayer'] != null)
            this.$map.removeLayer(this.$layerGroups['bufferLayer'])

          //if (this.theMarker != null) this.$map.removeLayer(this.theMarker)

          var centerPt = e.layer.getLatLng()
          var center = [centerPt.lng, centerPt.lat]
          var radius = e.layer.getRadius() / 100000
          let buffer = {
            center: center,
            radius: radius,
          }

          backApi.post('entityelements/buffer', buffer).then(({ data }) => {
            if (data) {
              this.geom = data
            }
          })
        } else {
          this.geom = e.layer.feature.geometry
        }
        setTimeout(() => {
          this.$store.commit('maps/setDrawing', false)
        }, 500)
      })
    },
    resetAll() {
      this.result = []
      this.displayFilter = true
      this.selectedLayer = null
      this.operation = 'DWITHIN'
      this.perimeter = 1
      this.unit = 'kilometers'
      this.page = 0
      this.$DrawLayer.clearLayers()
      if (this.draw) {
        this.geom = null
      }
      this.selectedShape = null
      this.$store.commit('maps/setDrawing', false)
      this.resetView()
    },
    onSubmit() {
      this.doFilter()
    },
    showFilter(_feature) {
      this.page = 0
      this.total = 0
      this.displayFilter = true
      if (this.isMobile) {
        let feature = document.querySelector('.feature')
        if (feature) feature.classList.remove('fixed-height')
      }
    },
    goTo(coords, topo) {
      this.$emit('goToElement', { coords, topo })
    },
    showFeature(_feature) {
      let viewList = null
      if (this.isShortcut || this.draw) {
        viewList = document.querySelector('#proximityTool .content-wrapper')
      } else {
        viewList = document.querySelector('.feature .tab-content')
      }
      this.lastScrollHeight = viewList.scrollTop
      viewList.scrollTo(0, 0)
      if (this.$layerGroups['itinerary'] != null) {
        this.$emit('closed')
        this.$map.removeControl(this.$layerGroups['itinerary'])
        this.$layerGroups['itinerary'] = null
      }
      this.activeTab = 1
      this.selectedItem = {
        ..._feature.properties,
        id: _feature.id
      }
      let doc = document.querySelector('#feature-type-list > .tab-content')
      if (doc) doc.scrollTop = 0
      let topo = _feature.geometry.type

      let coords = _feature.geometry.coordinates
      this.$emit('setGoTo', coords)

      this.goTo(coords, topo)
    },
    doFilter() {
      this.results = []
      this.performApi()
    },
    getGeometry() {
      if (this.geom) {
        switch (this.geom.type) {
          case 'LineString':
            return `${this.geom.coordinates.map((m) => m.join(' ')).join(', ')}`
          case 'Point':
            return this.geom.coordinates.join(' ')
          case 'Polygon':
            return `(${this.geom.coordinates
              .map((m) => m.map((e) => e.join(' ')))
              .join('')})`
          case 'MultiPolygon':
            return `(${this.geom.coordinates
              .map((m) => m.map((e) => e.map((c) => c.join(' '))))
              .join('')})`
        }
      }
    },
    performApi($state) {
      if (
        (this.page === 0 || this.total / this.size > this.page) &&
        this.geom
      ) {
        this.isLoading = true
        backApi
          .get('entityelements/public/nearbiest', {
            params: {
              selectedEntityElement: this.featureId
                ? this.featureId
                : '6407abe2-b427-4ed0-8b91-4e3234b7ee24',
              targetLayer: this.selectedLayer.id,
              targetLayerSlug: this.selectedLayer.slug,
              mapSlug: this.currentMap.slug ? this.currentMap.slug : 'null',
              perimeter: this.perimeter,
              unit: this.unit,
              geometry:
                typeof this.geom === 'string'
                  ? `SRID=4326;${this.geom}`
                  : `SRID=4326;${
                      this.geom.type === 'MultiPolygon'
                        ? 'Polygon'
                        : this.geom.type
                    }(${this.getGeometry()})`,
              operation: this.operation,
              intersection: this.draw,
              algebraOperation: this.algebraOperation,
              page: this.page,
              limit: this.size,
            },
          })
          .then(({ data }) => {
            this.total = data.totalElements
            this.displayFilter = false
            this.results.unshift(
              ...data.content.map((r) => ({
                ...r,
                featureJson: JSON.parse(r.featureJson),
              }))
            )
            this.isLoading = false
            if (this.isMobile && this.total > 0 && this.page === 0) {
              let feature = document.querySelector('.feature')
              if (feature) feature.classList.add('fixed-height')
            }
            this.page++
            $state && this.page > 0 && $state.loaded()
            if (this.selectedLayer.identifiant === null && !this.showHint) {
              this.$notification.warning({
                message: 'Remarque!',
                description: `S'il vous plaît définir un identifiant pour la couche ${this.selectedLayer.name} !`,
              })
              this.showHint = true
            }
          })
          .catch((e) => {
            this.isLoading = false
            this.page > 0 && $state.loaded()
          })
      } else {
        this.page > 0 && $state.complete()
      }
    },
    generateCqlFilter() {
      return `${
        this.operation === 'dwithin' ? 'DWITHIN' : 'BEYOND'
      }(geom, Point(${this.long} ${this.lat}),${this.perimeter} ,${this.unit})`
    },
    resetView() {
      if (this.isMobile) {
        let feature = document.querySelector('.feature')
        if (feature) feature.classList.remove('fixed-height')
      }
      if (this.$layerGroups && this.$layerGroups['customMarker']) {
        this.$map.removeLayer(this.$layerGroups['customMarker'])
      }
      this.$emit('setGoTo', [])
      this.$store.commit('maps/setDrawing', false)
    },
  },
  mounted() {
    this.geom = this.geometry
  },
  beforeDestroy() {
    this.resetView()
  },
}
</script> 

<style lang="scss">
.nearby-filter {
  flex-direction: column;
  display: flex;
  height: 100%;
  @include respond('phone') {
    margin-bottom: 0.5rem;
  }
  &__fields {
    transition: all 0.3s ease-in-out;
    height: fit-content;
    z-index: 0;
    button.active {
      border: 2px solid $color-primary;
    }
  }
  &__actions {
    margin-top: 20px;
  }
  &__results {
    margin-top: 15px;
    width: 100%;
    height: 100%;
  }
  &__title {
    font-weight: bold;
    margin: 10px 0 0 0;
  }
  &__close {
    height: fit-content;
    background-color: #ffffff;
    width: 100%;
    z-index: 10;
    position: relative;
    &::before {
      content: '';
      position: absolute;
      width: 100%;
      height: 20px;
      background-color: #ffffff;
      top: -20px;
    }
  }
  @include respond('phone') {
    .button {
      span {
        margin-top: 1px;
        margin-left: 1px;
      }
    }
  }
}
#nearby {
  min-height: 94%;
  height: fit-content !important;
  form {
    height: 100%;
  }
  .tabs {
    display: none;
  }
  .tab-content {
    margin-top: 0;
    padding: 0 !important;
    overflow: visible !important;
  }
  .back-button {
    margin: 0;
  }
}
.result {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 5px;
  border-radius: 5px;
  &:not(:last-child) {
    border-bottom: 1px solid #f4f4f4;
  }
  &:hover {
    background-color: rgba(204, 204, 204, 0.233);
    cursor: pointer;
  }
}
</style>