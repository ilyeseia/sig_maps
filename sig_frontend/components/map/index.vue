<template>
  <leaflet></leaflet>
</template>

<script>
import { backApi as axios } from '~/methods/serverApi'
import { mapState, mapGetters } from 'vuex'
import { backend, SecuredPublicWMSURL, SecuredWMSURL } from '../../constants'
import RestApi from '../../methods/api'

export default {
  props: ['mapSlug', 'drawing'],
  data() {
    return {
      canDrawFeatureInfo: true,
      authorized: false,
      theMarker: null,
      mapMode: this.$store.state.maps.mode,
    }
  },
  computed: {
    ...mapState(['profile', 'features']),
    ...mapGetters({
      mapIsReady: 'maps/getMapIsReady',
      canGetFeatureInfo: 'maps/getCanFeatureInfo',
      currentMap: 'maps/getCurrentMap',
      mapVersion: 'maps/getMapVersion',
      layersFilter: 'maps/getLayersFilter',
    }),
  },
  watch: {
    mapIsReady(newVal) {
      newVal && this.setup()
    },
    mapVersion() {
      this.clearLayers()
      this.addLayersToMap(this.currentMap.layers)
    },
  },
  methods: {
    getPopUp(feature) {
      /* generate popup content for each layer */
      let popup = ``

      const props = feature.properties
      const fields = feature.layer.fields

      for (let i = 0; i < fields.length; i++) {
        const prop = fields[i].name

        if (prop == 'image' || prop == '_id') continue
        if (prop == 'liaison_fo') {
          popup += `<h5><b>${prop}: </b></h5>`
          for (const el of props[prop]) {
            popup += `<h6><b>${el.trans || 'Tr'}: </b>${
              el.distanceKM || null
            } km</h6>`
          }
          continue
        }

        if (fields[i].type === 'IMAGE') {
          popup += `<h5><b>${prop}</b>: <img src="${backend}/download/${props[prop]}"/></h5>`
          continue
        }
        if (prop == 'createDate') {
          popup += `<h5><b>${prop}</b>: ${new Date(
            props[prop]
          ).toLocaleDateString()}</h5>`
          continue
        }

        if (prop == 'modifiedDate') {
          popup += `<h5><b>${prop}</b>: ${new Date(
            props[prop]
          ).toLocaleDateString()}</h5>`
          continue
        }

        popup += `<h5><b>${prop}</b>: ${props[prop]}</h5>`
      }
      return popup
    },
    editLayer({ relatedTarget }) {
      /* emit an edit event to the viewer edit menu click */
      this.$emit('edit', relatedTarget)
    },
    deleteFeatureFromDB(feature) {
      axios
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

    getFeatureInfo() {
      var geojson = null
      let vm = this.$map
      let thisRef = this
      this.$map.on('popupclose', function (e) {
        if (geojson) {
          vm.removeLayer(geojson)
        }
      })

      const url = thisRef.profile.authenticated
        ? SecuredWMSURL
        : SecuredPublicWMSURL
      this.$map.on('click', function (e) {
        if (thisRef.canGetFeatureInfo) {
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
              map: thisRef.mapSlug,
              buffer: 24,
              bbox: bounds.toBBoxString(), // works only with EPSG4326, but not with EPSG3857
              //bbox: sw.x + ',' + sw.y + ',' + ne.x + ',' + ne.y, // works with both EPSG4326, EPSG3857
              width: size.x,
              height: size.y,
              query_layers: wmsLayers,
              info_format: 'application/json', // text/plain (default), application/json for JSON (CORS enabled servers), text/javascript for JSONP (JSONP enabled servers)
              feature_count: 50,
              //exceptions: 'application/json', // application/vnd.ogc.se_xml (default)
              // format_options: 'callback: parseResponse' // callback: parseResponse (default), use only with JSONP enabled servers, when you want to change the callback name
            }
          if (parseFloat(obj.version) >= 1.3) {
            obj.crs = 'EPSG:4326'
            obj.i = Math.round(xy.x)
            obj.j = Math.round(xy.y)
          } else {
            obj.srs = 'EPSG:4326'
            obj.x = Math.round(xy.x)
            obj.y = Math.round(xy.y)
          }

          const storedToken = localStorage.getItem('sigToken')

          let axiosConfig = { url: url + L.Util.getParamString(obj, url, true) }
          if (thisRef.profile.authenticated)
            axiosConfig.params = { token: storedToken }

          axios(axiosConfig)
            .then(({ data }) => {
              if (geojson) {
                map.removeLayer(geojson)
              }
              if (data.features) {
                var features = data.features
                if (features.length) {
                  let newLayers = []
                  let allowAdd = false
                  for (var i in features) {
                    var feature = features[i]
                    var newLayer = { feature: { properties: {} } }

                    newLayer.feature.geometry = feature.geometry
                    var properties = feature.properties

                    var layerId = properties['layer_entity_element']

                    var layer = thisRef.currentMap.layers.find(
                      (layer) => layer.id === layerId
                    )

                    //Return feature if condition
                    if (
                      thisRef.layersFilter &&
                      Object.keys(thisRef.layersFilter).length > 0 &&
                      Object.keys(thisRef.layersFilter).includes(layer.slug)
                    ) {
                      for (let [layerSlug, fields] of Object.entries(
                        thisRef.layersFilter
                      )) {
                        if (
                          layer.slug === layerSlug &&
                          fields[Object.keys(fields)[0]].some(v =>
                            v == features[i].properties[Object.keys(fields)[0]]
                          )
                        ) {
                          allowAdd = true
                        }
                      }
                    } else {
                      allowAdd = true
                    }

                    if (allowAdd) {
                      newLayer.feature = feature
                      newLayer.feature.layer = layer
                      newLayer.feature.id = properties['id']

                      newLayer.featureType = layer.slug
                      newLayers.push(newLayer)
                    }
                  }
                  if (newLayers.length > 0) {
                    thisRef.addFeatureInfoToMap(newLayers)
                    thisRef.$emit('editFeatureInfo', {
                      features: newLayers,
                      current: 0,
                    })
                  }
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
        }
      })
    },
    clearLayers() {
      if (this.$layerGroups[`wmsLayer`]) {
        this.$map.removeLayer(this.$layerGroups['wmsLayer'])
      }
      if (this.$layerGroups[`wmsProperies`])
        this.$map.removeLayer(this.$layerGroups['wmsProperies'])

      this.currentMap.layers.forEach((layer) => {
        if (this.$layerGroups[`${layer.slug}`])
          this.$map.removeLayer(this.$layerGroups[`${layer.slug}`])
      })
    },
    addFeatureInfoToMap(features) {
      let feature = features[0].feature

      if (this.$layerGroups['featureInfo'])
        this.$map.removeLayer(this.$layerGroups['featureInfo'])

      let iconUrl = feature.layer.customIcon ? feature.layer.iconUrl : null

      let marker = (latlng) => {
        if (iconUrl) return L.marker(latlng, null)
        else
          return L.circleMarker(latlng, {
            fillOpacity: 0.5,
            radius: 20,
          })
      }

      this.$layerGroups['featureInfo'] = L.geoJSON(feature, {
        pointToLayer: (feature, latlng) => {
          return marker(latlng)
        },
      }).addTo(this.$map)
    },
    addLayersToMap(layers) {
      let myLayers = layers
        .filter((l) => l.visible)
        .map((layer) => layer)
        .sort((a, b) => a.order - b.order)
      let wmsLayers = myLayers
        .map((layer) => `limite_admin:${layer.slug}`)
        .join(',')
      let wmsStyles = myLayers
        .map((layer) =>
          layer.layerType === 'RASTER' ? '' : layer.style && layer.style.name
        )
        .join(',')
      const token = localStorage.getItem('sigToken')
      let wmsUrl = this.profile.authenticated
        ? SecuredWMSURL
        : SecuredPublicWMSURL
      const config = {
        layers: wmsLayers,
        format: 'image/png',
        transparent: true,
        styles: wmsStyles,
      }
      if (this.profile.authenticated) config.token = token
      this.$layerGroups[`wmsLayer`] = L.tileLayer.wms(wmsUrl, config)

      this.$layerGroups[`wmsLayer`].setZIndex(1000).addTo(this.$map)

      this.getFeatureInfo()

      // this.$layerGroups[`wmsLayer`].on('tileerror', (e, tile) => {
      //   this.$store.dispatch("profile/refresh");
      //   const token = localStorage.getItem("sigToken");
      //   if (this.currentMap.privacy === "PRIVATE")
      //     this.$layerGroups[`wmsLayer`].setParams({ token });
      // })
    },
    getScale() {
      var scale = L.control.scale()

      var metres = scale._getRoundNum(
        this.$map
          .containerPointToLatLng([0, this.$map.getSize().y / 2])
          .distanceTo(
            this.$map.containerPointToLatLng([
              scale.options.maxWidth,
              this.$map.getSize().y / 2,
            ])
          )
      )
      var km = metres < 1000 ? metres + ' m' : metres / 1000 + ' km'
      var ml =
        metres < 1000
          ? Math.round(metres) + ' m'
          : Math.round(metres / 1000) + ' km'

      let scaleKM = document.getElementById('scaleKM')
      let scaleML = document.getElementById('scaleKM')
      if (scaleKM) scaleKM.innerHTML = km
      if (scaleML) scaleML.innerHTML = ml
    },
    getBuffer(filter) {
      this.$layerGroups['bufferLayer'] = L.tileLayer
        .wms(`${SecuredPublicWMSURL}`, {
          layers: `limite_admin:etab_jeunesse`,
          format: 'image/png',
          styles: 'default_style_point',
          transparent: true,
          cql_filter: filter,
        })
        .setZIndex(9999)
        .addTo(this.$map)
    },
    showCoordinates($event) {
      const lat = $event.latlng.lat
      const lng = $event.latlng.lng
      return `${lat.toFixed(4)}, ${lng.toFixed(4)}`
    },
    copyCoordinates(e) {
      const el = document.createElement('textarea')
      el.value = `${e.latlng.lat}, ${e.latlng.lng}`
      el.setAttribute('readonly', '')
      el.style.position = 'absolute'
      el.style.left = '-9999px'
      document.body.appendChild(el)
      el.select()
      document.execCommand('copy')
      document.body.removeChild(el)
      this.$map.closePopup()
      this.$buefy.snackbar.open({
        message: 'Copié dans le presse-papier.',
        position: 'is-bottom',
        type: 'is-warning',
        actionText: null,
      })
      this.$map.closePopup()
    },
    directionFromHere(e) {
      this.getIterenary(e, true)
    },
    directionToHere(e) {
      this.getIterenary(e, false)
    },
    getIterenary(e, direction) {
      let coords = []
      coords.push(e.latlng.lng)
      coords.push(e.latlng.lat)
      this.$emit('setGoToWithDirection', { coords, direction })
      this.$map.closePopup()
    },
    newPoint() {
      this.$store.dispatch('maps/newGeom')
      this.$map.closePopup()
    },
    getNearbiestPoints(e) {
      this.$layerGroups['marker'] &&
        this.$map.removeLayer(this.$layerGroups['marker'])
      let coords = []
      coords.push(e.latlng.lng)
      coords.push(e.latlng.lat)
      this.$emit('searchNearbiestPoint', coords)
      document.getElementById('proximity-tool-button').click()
      this.$layerGroups[`marker`] = new L.marker(e.latlng).addTo(this.$map)
      this.$map.closePopup()
    },
    measureDistance() {
      this.$store.dispatch('maps/calculateDistance')
      this.$map.closePopup()
    },
    startFromDestination(e) {
      this.$layerGroups['itinerary'].spliceWaypoints(0, 1, e.latlng)
      this.$map.closePopup()
    },
    goToDestination(e) {
      this.$layerGroups['itinerary'].spliceWaypoints(
        this.$layerGroups['itinerary'].getWaypoints().length - 1,
        1,
        e.latlng
      )
      this.$map.closePopup()
    },
    setup() {
      let leafletControlLayer = document.querySelector(
        '.leaflet-control-layers'
      )
      leafletControlLayer.addEventListener('mouseenter', () => {
        leafletControlLayer.classList.remove('leaflet-control-layers-expanded')
      })
      document
        .querySelector('.leaflet-control-layers-toggle')
        .addEventListener('click', () => {
          leafletControlLayer.classList.remove(
            'leaflet-control-layers-expanded'
          )
          this.$emit('showBaseLayers')
        })
      this.getScale()

      let layers = this.currentMap.layers
      // create a layer group for each layer

      this.addLayersToMap(layers)
      let ref = this
      this.$map.on('zoomend', function () {
        ref.getScale()
      })

      this.$map.on('mousemove', function (event) {
        var lat = 'Lat : ' + event.latlng.lat.toString()
        var lng = 'Lng : ' + event.latlng.lng.toString()

        if (lat !== '' && lng !== '') {
          document.getElementById('positionId').innerHTML = 'Position'
          let latCoords = document.getElementById('latCoords')
          let lngCoords = document.getElementById('lngCoords')
          if (latCoords) latCoords.innerHTML = lat
          if (lngCoords) lngCoords.innerHTML = lng
        }
      })

      this.$map.on('draw:created', ({ layer, layerType }) => {
        if (layerType == 'circle') {
          if (this.$layerGroups['bufferLayer'] != null)
            this.$map.removeLayer(this.$layerGroups['bufferLayer'])

          //if (this.theMarker != null) this.$map.removeLayer(this.theMarker)

          var centerPt = layer.getLatLng()
          var center = [centerPt.lng, centerPt.lat]
          var radius = layer.getRadius() / 100000
          let buffer = {
            center: center,
            radius: radius,
          }

          axios.post('entityelements/buffer', buffer).then(({ data }) => {
            if (data) {
              let filter = 'INTERSECTS (geom, ' + data + ')'
              this.getBuffer(filter)
            }
          })
        }

        // add feature type and GeoJSON data to each layer on the map for ease of access
        if (!('feature' in layer)) {
          layer.featureType = ''
          layer.feature = layer.toGeoJSON()
        }

        //this.theMarker = this.$DrawLayer.addLayer(layer)
        // emit an event to the viewer so it can take th necessary actions.
        /*if (layerType != 'circle') {
        this.$emit('newLayer', layer)
      }*/

        //Clear al the previous layers
        this.$DrawLayer.eachLayer((l) => {
          this.$DrawLayer.removeLayer(l)
        })
        if (!this.profile.roles.includes('ROLE_ADMIN')) {
          RestApi.isAuthorizedArea(layer.feature.geometry)
            .then(() => {
              this.$DrawLayer.addLayer(layer)
              if (this.drawing === false) this.$emit('newLayer', layer)
            })
            .catch((error) => {
              this.$notification.warning({
                message: 'Attention !',
                description:
                  "Vous n'êtes pas autorisé à ajouter un point ici !!",
              })
            })
        } else {
          this.$DrawLayer.addLayer(layer)
          if (this.drawing === false) this.$emit('newLayer', layer)
        }
      })

      this.$map.on('draw:drawstart', (e) => {
        this.$store.dispatch('maps/updateCanGetFeatureInfo', false)
        this.canDrawFeatureInfo = false
        this.$emit('drawStart')
      })

      this.$map.on('draw:drawstop', (e) => {
        this.$store.dispatch('maps/updateCanGetFeatureInfo', true)
        this.$emit('drawEnd')
      })

      this.$map.on(
        'contextmenu.show' /* on mouse right-click on the map*/,
        ({ contextmenu }) => {
          if (contextmenu._items.length > 2 /* show only 2 menu items */)
            this.$map.contextmenu.hide()
        }
      )

      this.$map.on('contextmenu', (e) => {
        //  <button type="button" id="btnCoords" >Afficher les coordonées</button>
        let template = null
        if (this.$layerGroups['itinerary'] == null) {
          template = `
          <ul class="map-tooltip-list">
               <li id="btnCoords">
                ${this.showCoordinates(e)}
               </li>
                <li id="directionFromHere">
                 Itinéraire vers ce point
               </li>
                <li id="directionToHere">
                 Itinéraire apartir de ce point
               </li>
                <li id="nearbiestPoints">
                 Recherche à proximité
               </li>
          `
        } else {
          template = `<ul class="map-tooltip-list">
               <li id="btnCoords">
                ${this.showCoordinates(e)}
               </li>
                 <li id="startDestination">
                 Partir de cet endroit
               </li>
                 <li id="goToDestination">
                 Aller à cet endroit
               </li>
          `
        }
        if (this.profile.authenticated) {
          template += `
          <li id="newPoint">
                 Nouveau Point
               </li>
        `
        }
        template += `
          <li id="measureDistance">
                 Mesurer la distance
               </li>
          </ul>
        `
        L.popup().setLatLng(e.latlng).setContent(template).openOn(this.$map)

        //CopyCoordinate function
        var btn = document.getElementById('btnCoords')
        btn.addEventListener('click', () => {
          this.copyCoordinates(e)
        })

        //DirectionFromHere
        if (document.getElementById('directionFromHere')) {
          var getDirectionFromHereBtn =
            document.getElementById('directionFromHere')
          getDirectionFromHereBtn.addEventListener('click', () => {
            this.directionFromHere(e)
          })
        }

        //DirectionToHere
        if (document.getElementById('directionToHere')) {
          var getDirectionToHereBtn = document.getElementById('directionToHere')
          getDirectionToHereBtn.addEventListener('click', () => {
            this.directionToHere(e)
          })
        }

        //Get the nearbiest points
        if (document.getElementById('nearbiestPoints')) {
          var nearbiestPointsBtn = document.getElementById('nearbiestPoints')
          nearbiestPointsBtn.addEventListener('click', () => {
            this.getNearbiestPoints(e)
          })
        }

        //New Point Here
        if (document.getElementById('newPoint')) {
          var newPointBtn = document.getElementById('newPoint')
          newPointBtn.addEventListener('click', () => {
            this.newPoint()
          })
        }

        //StartDestination
        if (document.getElementById('startDestination')) {
          var startFromDestinationBtn =
            document.getElementById('startDestination')
          startFromDestinationBtn.addEventListener('click', () => {
            this.startFromDestination(e)
          })
        }

        //GoToDestination
        if (document.getElementById('goToDestination')) {
          var goToDestinationBtn = document.getElementById('goToDestination')
          goToDestinationBtn.addEventListener('click', () => {
            this.goToDestination(e)
          })
        }

        //MeasureDistance
        var measureDistancetBtn = document.getElementById('measureDistance')
        measureDistancetBtn.addEventListener('click', () => {
          this.measureDistance()
        })
      })

      // filter-layer button click
      this.$map.addControl(this.$globalFilterButton)
      document
        .getElementById('global-filter-button')
        .addEventListener('click', (e) => {
          e.stopPropagation()
          this.$emit('globalFilter')
        })

      if (this.mapMode === 'map') {
        if (
          this.profile.roles.includes('ROLE_ADMIN') ||
          this.profile.roles.includes('MAP_READ_AUTHORITY')
        ) {
          this.$map.addControl(this.$filterButton)

          document
            .getElementById('filter-button')
            .addEventListener('click', (e) => {
              e.stopPropagation()
              this.$emit('filterLayers')
            })

          if (
            this.profile.roles.includes('ROLE_ADMIN') ||
            this.profile.roles.includes('GEOPROCESSING')
          ) {
            //Add geo processing tool Button
            this.$map.addControl(this.$geoProcessingButton)
            document
              .getElementById('geo-processing-button')
              .addEventListener('click', (e) => {
                e.stopPropagation()
                this.$emit('showGeoProccessingTools')
              })
          }
        }
        //Add proximity tool Button
        this.$map.addControl(this.$proximityToolButton)
        document
          .getElementById('proximity-tool-button')
          .addEventListener('click', (e) => {
            e.stopPropagation()
            this.$emit('showProximityTool')
          })
        // show layers button
        this.$map.addControl(this.$showLayersButton)
        document
          .getElementById('show-layers-button')
          .addEventListener('click', (e) => {
            e.stopPropagation()
            this.$emit('showLayers')
          })

        if (this.profile.authenticated && this.currentMap.privacy != 'ARCHIVED')
          this.$map.addControl(this.$drawControl)

        //Add toggle Button
        this.$map.addControl(this.$toggleButton)
        document
          .getElementById('toggle-button')
          .addEventListener('click', (e) => {
            this.$emit('toggleItems')
            e.stopPropagation()
          })
      } else {
        if (
          this.profile.roles.includes('ROLE_ADMIN') ||
          this.profile.roles.includes('LAYER_READ_AUTHORITY')
        ) {
          this.$map.addControl(this.$filterButton)

          document
            .getElementById('filter-button')
            .addEventListener('click', (e) => {
              e.stopPropagation()
              this.$emit('filterLayers')
            })
        }
      }
      this.$nextTick(() => {
        this.$nuxt.$loading.start()
        setTimeout(() => this.$nuxt.$loading.finish(), 900)
      })
    },
  },
}
</script>

<style lang="scss">
.leaflet-control-geosearch.bar {
  display: flex;
  justify-content: center;
  width: 100%;
  left: 0;
  right: 0;
  z-index: 500;
  form {
    width: 35%;
  }
}
.leaflet-popup-content-wrapper {
  border-radius: 0;
  box-shadow: 0 1px 6px #3c404347;
  border: none;
  border-radius: 4px;
  min-width: 185px;
  max-width: 300px;
  padding: 8px 0;
  color: #3c4043;
  overflow: hidden;
  padding: 10px 0 !important;
  @include respond('phone') {
    padding: 5px 0 !important;
  }
}
.leaflet-measure-resultpopup {
  > div {
    padding: 8px !important;
  }
}
.leaflet-marker-draggable {
  border: 4px dashed rgba(243, 66, 75, 0.89);
  border-radius: 5px;
  padding: 4px;
}
.leaflet-popup-content {
  min-width: 185px;
  margin: 0;
}
.leaflet-popup-close-button {
  display: none;
}
.leaflet-popup-tip-container {
  display: none;
}
.map-tooltip-list {
  white-space: nowrap;
  font-size: 1rem;
  font-weight: 400;
  li {
    transition: all 0.1s ease-out;
    padding: 8px 18px;
    cursor: pointer;
    @include respond('phone') {
      padding: 6px 12px;
    }
    &:hover {
      background-color: #ccc;
    }
  }
}
.snackbar.is-warning.is-bottom {
  width: 235px !important;
}
</style>
