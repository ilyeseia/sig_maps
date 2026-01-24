<template>
  <form @submit.prevent="onSubmit">
    <div class="modal-card">
      <header class="modal-card-head">
        <h1 class="modal-card-title">Chargement de Couches</h1>
      </header>
      <section
        class="modal-card-body"
        style="height: 75vh; overflow: hidden auto"
      >
        <b-input
          v-model="searchName"
          ref="searchNameRef"
          placeholder="Rechercher ici.."
          type="search"
          icon="magnify"
          style="margin-bottom: 1rem"
        />
        <render-data
          :data="layers"
          alertMessage="Il n'y a pas de couche à afficher ?"
          :loading="loadingData"
        >
          <b-table
            :data="layers"
            :checked-rows.sync="checkedLayers"
            paginated
            backend-pagination
            :per-page="perPage"
            :total="totalRowsCount"
            @page-change="onPageChange"
            backend-sorting
            :default-sort-direction="defaultSortOrder"
            :default-sort="[sortField, sortOrder]"
            @sort="onSort"
            checkable
          >
            <template slot-scope="props">
              <b-table-column field="name" label="Nom">
                {{ props.row.name }}
              </b-table-column>
            </template>
          </b-table>
        </render-data>
      </section>

      <footer class="right-align modal-card-foot">
        <button
          id="close-layer-form"
          class="button"
          type="button"
          @click="$parent.close()"
        >
          Fermer
        </button>
        <button
          v-if="layers && layers.length > 0"
          id="save-layer"
          type="submit"
          :class="['button', 'is-primary', { 'is-loading': isLoading }]"
        >
          Enregistrer
        </button>
      </footer>
    </div>
  </form>
</template>

<script>
import { mapState, mapGetters } from 'vuex'
import axios from 'axios'
import { backApi } from '~/methods/serverApi'
import RestApi from '../../methods/api'
import { SecuredWMSURL } from '../../constants'
import RenderData from '~/components/layout/RenderData.vue'

import mark from '~/mixins/style/mark'
import fill from '~/mixins/style/fill'
import stroke from '~/mixins/style/stroke'
export default {
  mixins: [mark, fill, stroke],
  components: { RenderData },
  data() {
    return {
      currentMap: this.$store.state.maps.currentMap,
      layers: [],
      isLoading: false,
      fields: [],
      sortField: 'name',
      searchName: '',
      sortOrder: 'asc',
      defaultSortOrder: 'asc',
      perPage: 10,
      page: 0,
      totalRowsCount: null,
      checkedLayers: [],
      loadingData: false,
    }
  },
  watch: {
    searchName(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.searchNameRef.value) {
          this.filterChanged()
        }
      }, 500)
    },
  },
  computed: {
    ...mapState(['maps']),
    ...mapGetters({
      mapThemes: 'maps/mapThemes',
    }),
  },
  methods: {
    filterChanged() {
      let payload = {
        condition: 'and',
        rules: [
          {
            label: 'name',
            field: 'name',
            operator: 'ilike',
            type: 'string',
            value: this.searchName,
          },
        ],
      }
      RestApi.filterAllLayersInMap(
        `layers/search/sharedWithOthers/${this.currentMap.id}`,
        payload,
        {
          page: this.page,
          limit: this.perPage,
          sort: this.sortField,
          dir: this.sortOrder,
        },
        this.$store,
        this.$router,
        this.$notification
      ).then(({ content, totalElements }) => {
        this.layers = content.map((l) => {
          return {
            ...l,
            name:
              l.name && l.name.includes('____')
                ? l.name.split('____')[1]
                : l.name,
          }
        })
        this.totalRowsCount = totalElements
        document.querySelector('.modal-card-body').scrollTo(0, 0)
      })
    },
    editLayer({ relatedTarget }) {
      this.$emit('edit', relatedTarget)
    },
    deleteFeatureFromDB(feature) {
      backApi
        .delete(`entityelements/${feature.id}`)
        .then(() => {
          this.$notification.success({
            message: 'Success!',
            description: 'Deleted from database',
          })
        })
        .catch((e) => {
          this.$notification.error({
            message: 'Error: deleting from database',
            description: e.message,
          })
        })
    },
    loadLayers() {
      let mapLayers = this.currentMap.layers
        .filter((l) => l.visible)
        .map((layer) => layer)

      let myLayers = mapLayers.sort((a, b) => a.order - b.order)
      let wmsLayers = myLayers
        .map((layer) => `limite_admin:${layer.slug}`)
        .join(',')
      let wmsStyles = myLayers
        .map((layer) =>
          layer.layerType === 'RASTER' ? '' : layer.style && layer.style.name
        )
        .join(',')

      const token = localStorage.getItem('sigToken')
      this.$layerGroups[`wmsLayer`] = L.tileLayer
        .wms(`${SecuredWMSURL}`, {
          layers: wmsLayers,
          format: 'image/png',
          transparent: true,
          styles: wmsStyles,
          token,
        })
        .setZIndex(1000)
        .addTo(this.$map)

      // this.getFeatureInfo()
    },
    getFeatureInfo() {
      var geojson = null
      let vm = this.$map
      let thisRef = this
      this.$map.on('popupclose', function (e) {
        if (geojson) {
          vm.removeLayer(geojson)
        }
      })

      var url = `${SecuredWMSURL}`
      this.$map.on('click', function (e) {
        var _layers = this._layers,
          versions = []
        for (var x in _layers) {
          var _layer = _layers[x]
          if (_layer.wmsParams) {
            versions.push(_layer.wmsParams.version)
          }
        }

        let myLayers = thisRef.currentMap.layers
          .filter((l) => l.visible)
          .map((layer) => layer)
          .sort((a, b) => b.order - a.order)
        let wmsLayers = myLayers
          .map((layer) => `limite_admin:${layer.slug}`)
          .join(',')

        var loc = e.latlng,
          xy = e.containerPoint, // xy = this.latLngToContainerPoint(loc,this.getZoom())
          size = this.getSize(),
          bounds = this.getBounds(),
          crs = this.options.crs,
          sw = crs.project(bounds.getSouthWest()),
          ne = crs.project(bounds.getNorthEast()),
          obj = {
            service: 'WMS', // WMS (default)
            request: 'GetFeatureInfo',
            version: versions[0],
            layers: wmsLayers,
            buffer: 16,
            // bbox: bounds.toBBoxString(), // works only with EPSG4326, but not with EPSG3857
            bbox: sw.x + ',' + sw.y + ',' + ne.x + ',' + ne.y, // works with both EPSG4326, EPSG3857
            width: size.x,
            height: size.y,
            query_layers: wmsLayers,
            info_format: 'application/json', // text/plain (default), application/json for JSON (CORS enabled servers), text/javascript for JSONP (JSONP enabled servers)
            feature_count: 50,
            //exceptions: 'application/json', // application/vnd.ogc.se_xml (default)
            // format_options: 'callback: parseResponse' // callback: parseResponse (default), use only with JSONP enabled servers, when you want to change the callback name
          }
        if (parseFloat(obj.version) >= 1.3) {
          obj.crs = crs.code
          obj.i = Math.round(xy.x)
          obj.j = Math.round(xy.y)
        } else {
          obj.srs = crs.code
          obj.x = Math.round(xy.x)
          obj.y = Math.round(xy.y)
        }

        //var html = 'You Clicked @ ' + loc + '<br/>'
        var html = ''
        const token = localStorage.getItem('sigToken')
        axios({
          params: {
            token,
          },
          url: url + L.Util.getParamString(obj, url, true),
          /*  withCredentials: true,
          auth: { username: `${GeoServerUser}`, password: `${GeoServerPassword}` }, */
        })
          .then(({ data }) => {
            if (geojson) {
              map.removeLayer(geojson)
            }

            if (data.features) {
              var features = data.features

              if (features.length) {
                let newLayers = []
                for (var i in features) {
                  var feature = features[i]
                  var newLayer = { feature: { properties: {} } }

                  newLayer.feature.geometry = feature.geometry
                  var properties = feature.properties

                  var layerId = properties['layer_entity_element']
                  var layer = thisRef.currentMap.layers.find(
                    (layer) => layer.id === layerId
                  )

                  newLayer.feature = feature
                  newLayer.feature.layer = layer
                  newLayer.feature.id = properties['id']

                  newLayer.featureType = layer.slug
                  newLayers.push(newLayer)
                }

                thisRef.addFeatureInfoToMap(newLayers)

                thisRef.$emit('editFeatureInfo', {
                  features: newLayers,
                  current: 0,
                })
              } else {
              }
            } else {
            }
          })
          .catch((error) => {
            console.log(error)
            if (geojson) {
              vm.removeLayer(geojson)
            }

            //vm.openPopup(html, loc)
          })
      })
    },
    addFeatureInfoToMap(features) {
      let feature = features[0].feature

      if (this.$layerGroups['featureInfo'])
        this.$map.removeLayer(this.$layerGroups['featureInfo'])

      let iconUrl = feature.layer.customIcon ? feature.layer.iconUrl : null

      let marker = (latlng) => {
        if (iconUrl) return L.marker(latlng, null)
        else return L.circleMarker(latlng, null)
      }

      this.$layerGroups['featureInfo'] = L.geoJSON(feature, {
        pointToLayer: (feature, latlng) => {
          return marker(latlng)
        },
      }).addTo(this.$map)
    },
    onSort(field, order) {
      this.sortField = field
      this.sortOrder = order
      this.filterChanged()
    },
    onPageChange(page) {
      this.page = page - 1
      this.filterChanged()
    },
    async onSubmit(e) {
      let defaultTheme = this.mapThemes.find((t) => t.isDefault)
      if (!defaultTheme) {
        this.$notification.error({
          message:
            'Veuillez sélectionner le thème par défaut sur lequel vous travaillez !',
        })
        this.$emit('close')
      } else {
        this.$nextTick(() => {
          this.$nuxt.$loading.start()
        })
        if (this.$layerGroups['wmsLayer'])
          this.$map.removeLayer(this.$layerGroups['wmsLayer'])

        let mapLayers = []

        let order = this.currentMap.layers.length
        const uniqueCheckedLayers = [
          ...new Map(
            this.checkedLayers.map((item) => [item['id'], item])
          ).values(),
        ]
        uniqueCheckedLayers.forEach((layer) => {
          let mapLayer = {}
          order += 1
          mapLayer.map = {
            id: this.currentMap.id,
            privacy: this.currentMap.privacy,
          }
          mapLayer.order = order
          mapLayer.layer = {
            id: layer.id,
            name: layer.name,
            topo: layer.topo,
          }
          mapLayer.mapManipulation = 'ATTACH'
          mapLayer.layerStyle = {
            isDefault: true,
            order,
            styleConfig: {
              symbologyType: 'Simple',
              rules: [
                {
                  fill: this.fill,
                  mark: this.mark,
                  stroke: this.stroke,
                },
              ],
            },
            layer: {
              id: layer.id,
              topo: layer.topo,
            },
            mapId: this.currentMap.id,
          }
          mapLayer.targetTheme = {
            id: defaultTheme.id,
          }
          mapLayers.push(mapLayer)
        })

        backApi
          .post('maps/attach', mapLayers)
          .then(({ data }) => {
            if (data && data.length === mapLayers.length) {
              this.$nuxt.$loading.finish()
              this.$notification.success({
                message: 'La couche a été attachée avec succés ',
              })
              this.$store.commit('maps/attach', data)
              this.loadLayers()
            }else{
              this.$emit('setDefaultMapTheme', defaultTheme.id)
            }
            this.$emit('close')
          })
          .catch((error) => {
            this.$notification.error({
              message:
                error.response && error.response.data
                  ? error.response.data.message
                  : "Erreur lors de l'attachement de la couche!",
            })
            this.$emit('close')
          })
      }
    },
  },
  beforeMount() {
    this.filterChanged()
  },
}
</script>
<style lang="scss" >
.right-align {
  display: flex;
  flex-direction: row;
  justify-content: flex-end;
}
</style>
