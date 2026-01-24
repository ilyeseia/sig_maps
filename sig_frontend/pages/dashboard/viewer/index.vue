<template>
  <section class="h-100 d-flex flex-column">
    <div
      id="statusBar"
      :style="`
        position: fixed;
        left: 0;
        top: ${profile.authenticated ? '5rem' : 0};
        width: 100%;
        z-index: 399;
        height: fit-content;`"
      class="bg-light"
    >
      <div
        v-if="(mapIsReady || showDataView) && !isMobile && !isTablet"
        class="
          d-flex
          justify-content-between
          align-items-center
          border border-info
          p-1
          map-header
        "
      >
        <div class="align-self-center map-name">
          <b-tooltip label="Accueil" type="is-dark" position="is-right">
            <nuxt-link
              style="text-decoration: none"
              v-if="!getVisibility"
              to="/"
            >
              <b-icon
                pack="fas"
                icon="home"
                style="margin: 0 5px; font-size: 1rem"
              />
            </nuxt-link>
          </b-tooltip>
          <span
            :class="{
              'map-badge': true,
              private: currentMap.privacy === 'PRIVATE' ? true : false,
              public: currentMap.privacy === 'PUBLIC' ? true : false,
              'public-with-link':
                currentMap.privacy === 'PUBLIC_WITH_LINK' ? true : false,
              archived: currentMap.privacy === 'ARCHIVED' ? true : false,
            }"
          ></span>
          <span
            class="h4 text-muted"
            style="margin-left: 10px; color: $color-primary !important"
            >{{ mapMode == 'map' ? '' : 'Nom de couche :' }}</span
          >
          <span
            class="h4 text-muted"
            style="margin-left: 10px; color: $color-primary !important"
            >{{ mapMode == 'map' ? '' : 'Nom de couche :' }}</span
          >
          <span :tooltip="currentMap.name"  class="map-title text-info" style="vertical-align: bottom">
            {{ currentMap.name.slice(0, 40) }}
            <span v-if="currentMap.name.length > 40">
              ..
          </span>
          </span>
          <strong :tooltip="defaultMapTheme && defaultMapTheme.name" class="map-title" v-if="profile.authenticated">
             /{{ defaultMapTheme ? defaultMapTheme.name : '' }} 
          </strong>
        </div>
        <div>
          <button
            class="btn-table"
            :class="['button', 'is-secondary']"
            id="view-map"
            type="button"
            @click="viewMap"
          >
            <b-icon class="mr-1" pack="fas" icon="map" />{{
              mapMode == 'map' ? 'Map' : 'Layer'
            }}
          </button>
          <button
            class="btn-table"
            :class="['button', 'is-secondary']"
            id="view-table"
            type="button"
            @click="viewTable"
          >
            <b-icon class="mr-1" pack="fas" icon="table" />Table
          </button>
        </div>
        <div id="scale-position" class="scale-position">
          <div class="leaflet-control-scale leaflet-control">
            <div id="scaleKM" class="leaflet-control-scale-line"></div>
            <div id="scaleML" class="leaflet-control-scale-line"></div>
          </div>
          <div style="width: 250px" class="d-flex flex-row">
            <div
              id="positionId"
              class="align-self-center mr-3 ml-3"
              style="color: $color-primary"
            ></div>
            <div class="leaflet-control-scale leaflet-control">
              <div id="latCoords" class="leaflet-control-scale-line"></div>
              <div id="lngCoords" class="leaflet-control-scale-line"></div>
            </div>
          </div>
        </div>
      </div>
      <div v-else-if="mapIsReady || showDataView">
        <b-tabs
          v-model="mobileSwitcher"
          position="is-centered"
          expanded
          class="switcher"
        >
          <b-tab-item @click="viewMap" label="Map" icon="map"></b-tab-item>
          <b-tab-item
            @click="viewTable"
            label="Table"
            icon="table"
          ></b-tab-item>
          <div id="scale-position" class="scale-position">
            <div class="leaflet-control-scale leaflet-control">
              <div id="scaleKM" class="leaflet-control-scale-line"></div>
              <div id="scaleML" class="leaflet-control-scale-line"></div>
            </div>
            <div style="width: 250px" class="d-flex flex-row">
              <div
                id="positionId"
                class="align-self-center mr-3 ml-3"
                style="color: $color-primary"
              ></div>
              <div class="leaflet-control-scale leaflet-control">
                <div id="latCoords" class="leaflet-control-scale-line"></div>
                <div id="lngCoords" class="leaflet-control-scale-line"></div>
              </div>
            </div>
          </div>
        </b-tabs>
      </div>
    </div>
    <div class="wrapper-content" v-bind:style="{ position: 'relative' }">
      <ViewTable
        v-if="showDataView"
        @editFeatureInfo="editFeatureInfo"
        @opened="opened"
        @closed="closed"
        @onSave="onSave"
        @goTo="goToElement"
      />
      <Map
        v-if="showMap"
        @editFeatureInfo="editFeatureInfo"
        :mapSlug="currentMap.slug"
        :drawing="maps.drawing"
        @drawStart="canShowFeatureInfo = false"
        @drawEnd="canShowFeatureInfo = true"
        @newLayer="onNewLayer"
        @edit="onEdit"
        @toggleItems="toggleItems"
        @showBaseLayers="showComponent('baseLayers')"
        @showLayers="showComponent('layers')"
        @filterLayers="showComponent('advancedFilter')"
        @globalFilter="showComponent('globalFliter')"
        @showGetCurrentPosition="showComponent('getCurrPos')"
        @showProximityTool="showComponent('proximityTool')"
        @showGeoProccessingTools="showComponent('geoProcessing')"
        @setGoToWithDirection="setGoToWithDirection"
        @searchNearbiestPoint="searchNearbiestPoint"
      />

      <!-- Start Tool Box -->

      <!-- Start GetCurrent Positionn -->
      <ProductVersion v-if="showMap" />
      <LimiteBounds />
      <div v-if="!isMobile">
        <FeatureSettings
          v-if="showFeatureSettings"
          :loadingIndicator="loadingIndicator"
          @save="onSave"
          @cancel="showFeatureSettings = false"
          :newLayer="newLayer"
          :features="features"
          :mode="mode"
          @opened="opened"
          @closed="closed"
          @showDirection="showDirection"
          @setGoTo="setGoTo"
          @goToElement="goToElement"
          injectedIn="map"
        />
        <Layers ref="layers" @hide="toggledItems.showLayers = false" />
        <BaseLayers ref="baseLayers" @hide="toggledItems.baseLayers = false" />
        <div id="proximityTool">
          <div class="content-wrapper">
            <nearby
              :key="nearbyKey"
              :geometry="geometry"
              :newLayer="newLayer"
              :draw="allowDraw"
              :isShortcut="isShortcut"
              @hide="toggleItems"
              @goToElement="goToElement"
            ></nearby>
          </div>
        </div>
        <FilterLayers
          ref="filterLayers"
          @hide="toggleItems"
          @editFeatureInfo="editFeatureInfo"
          @goToElement="goToElement"
        />
        <GlobalFilter
          @hide="toggleItems"
          @editFeatureInfo="editFeatureInfo"
          @goToElement="goToElement"
        />
        <GeoProcessing @hide="toggledItems.geoProcessing = false" />
      </div>
    </div>
    <div
      class="bottom-sheet"
      v-if="isMobile && !showDataView"
      :key="bottomSheetKey"
    >
      <vue-bottom-sheet max-height="calc(100vh - 5.4rem)" ref="myBottomSheet">
        <div
          v-if="
            toggledItems.selected && toggledItems.selected !== 'actionsShortcut'
          "
        >
          <button
            id="close"
            type="button"
            class="button ml-3 bottom-sheet__return-btn back-btn"
            @click="showComponent('actionsShortcut', true)"
          >
            <b-icon pack="fas" icon="arrow-left"></b-icon>
          </button>
          <div class="bottom-sheet__separator"></div>
        </div>
        <ActionsShortcut
          v-if="toggledItems.selected === 'actionsShortcut'"
          @newGeometry="newGeometry"
          @measureDistance="measureDistance"
          @showLayers="showComponent('layers')"
          @filterLayers="showComponent('advancedFilter')"
          @globalFilter="showComponent('globalFliter')"
          @showGetCurrentPosition="showComponent('getCurrPos')"
          @showProximityTool="showComponent('proximityTool')"
          @showGeoProccessingTools="showComponent('geoProcessing')"
        />
        <FeatureSettings
          v-if="
            showFeatureSettings && toggledItems.selected === 'featureSettings'
          "
          ref="featureSettings"
          :loadingIndicator="loadingIndicator"
          @save="onSave"
          @cancel="showFeatureSettings = false"
          :newLayer="newLayer"
          :features="features"
          :mode="mode"
          @opened="opened"
          @closed="closed"
          @showDirection="showDirection"
          @setGoTo="setGoTo"
          @goToElement="goToElement"
          injectedIn="mobilemap"
        />
        <div
          id="itinerary"
          v-else-if="toggledItems.selected === 'itinerary'"
        ></div>
        <Layers
          v-if="toggledItems.selected === 'layers'"
          ref="layers"
          @hide="toggledItems.showLayers = false"
        />
        <div
          v-else-if="toggledItems.selected === 'proximityTool'"
          id="proximityTool"
        >
          <div class="content-wrapper">
            <nearby
              :key="nearbyKey"
              :geometry="geometry"
              :newLayer="newLayer"
              :draw="allowDraw"
              :isShortcut="isShortcut"
              @hide="toggleItems"
              @goToElement="goToElement"
            ></nearby>
          </div>
        </div>
        <FilterLayers
          v-else-if="toggledItems.selected === 'advancedFilter'"
          ref="filterLayers"
          @hide="toggleItems"
          @editFeatureInfo="editFeatureInfo"
          @goToElement="goToElement"
        />
        <GlobalFilter
          v-else-if="toggledItems.selected === 'globalFliter'"
          @hide="toggleItems"
          @editFeatureInfo="editFeatureInfo"
          @goToElement="goToElement"
        />
        <GeoProcessing
          v-else-if="toggledItems.selected === 'geoProcessing'"
          @hide="toggledItems.geoProcessing = false"
        />
      </vue-bottom-sheet>
    </div>
    <!-- End Tool Box  -->
    <!-- Start Show Map tools for Mobile -->
    <div
      v-if="!showDataView"
      @click="showComponent('actionsShortcut')"
      class="map-tools-trigger"
    >
      <h2>
        <i class="fa fa-chevron-up"></i>
        Appuyer pour afficher les actions rapides
      </h2>
    </div>
    <!-- End Show Map tools for Mobile -->
  </section>
</template>
<script>
import Map from '~/components/map'
import BaseLayers from '~/components/dashboard/maps/BaseLayers'
import { backApi } from '@/methods/serverApi'
import FeatureSettings from '~/components/viewer/FeatureSettings'
import FilterLayers from '~/components/viewer/FilterLayers'
import GlobalFilter from '~/components/viewer/GlobalFilter'
import GeoProcessing from '~/components/viewer/GeoProcessing'
import LimiteBounds from '~/components/LimiteBounds.vue'
import ActionsShortcut from '~/components/dashboard/maps/ActionsShortcut'
//import GetCurrentPosition from "~/components/viewer/GetCurrentPosition";
import Layers from '~/components/viewer/Layers'
import { mapState, mapGetters } from 'vuex'
import ViewTable from '~/components/dashboard/maps/ViewTable.vue'
import ProductVersion from '~/components/ProductVersion.vue'
import { frontend, osrm_server } from '../../../constants'
import nearby from '../../../components/viewer/Nearby.vue'
import 'leaflet-routing-machine/dist/leaflet-routing-machine.js'
import 'leaflet-routing-machine/dist/leaflet-routing-machine.css'
import 'leaflet-routing-machine/examples/Control.Geocoder.js'
import VueBottomSheet from '@webzlodimir/vue-bottom-sheet'
export default {
  /* validate({ params, query, store }) {
    return (
      store.state.profile.roles.includes('ROLE_ADMIN') ||
      store.state.profile.roles.includes('MAP_READ_AUTHORITY')
    )
  }, */
  /*  asyncData({ isDev, route, store, env, params, query, req, res, redirect, error }) {
    console.log("route:", route);
  }, */
  data() {
    return {
      isEditing: false,
      arr: [],
      zoomLevel: null,
      currentMap: this.$store.state.maps.currentMap,
      toggledItems: {
        selected: null,
        prec: null,
        globalFliter: false,
        advancedFilter: false,
        layers: false,
        showLayers: false,
        itinerary: false,
        proximityTool: false,
        geoProcessing: false,
        baseLayers: false
      },
      showGetCurrentPosition: true,
      showFeatureSettings: false,
      showDataView: false,
      showMap: true,
      newLayer: {},
      mapMode: this.$store.state.maps.mode,
      mode: 'create',
      canShowFeatureInfo: true,
      features: null,
      count: 1,
      geom: null,
      loadingIndicator: false,
      goTo: [],
      waypoints: [],
      allowDraw: true,
      geometry: {
        type: 'Point',
        coordinates: []
      },
      isShortcut: false,
      iterenaryLoaded: false,
      selectedProfile: 'car',
      nearbyKey: 1,
      bottomSheetKey: 1,
      mobileSwitcher: 0
    }
  },
  computed: {
    ...mapState(['profile', 'maps']),
    ...mapGetters({
      mapIsReady: 'maps/getMapIsReady',
      mapLayers: 'maps/mapLayers',
      defaultMapTheme: 'maps/getDefaultMapTheme',
      isMobile: 'app/getIsMobile',
      isTablet: 'app/getIsTablet',
      baseLayers: 'app/getBaseLayers'
    }),
    getVisibility() {
      return this.profile.authenticated
        ? true
        : !this.$route.matched[0].path.indexOf('viewer') > 0 ||
            this.currentMapPrivacy === 'PRIVATE'
    }
  },
  components: {
    Map,
    FeatureSettings,
    FilterLayers,
    GlobalFilter,
    ProductVersion,
    Layers,
    ViewTable,
    LimiteBounds,
    GeoProcessing,
    nearby,
    VueBottomSheet,
    ActionsShortcut,
    BaseLayers
  },
  watch: {
    mobileSwitcher(val) {
      if (val === 0) {
        this.viewMap()
      } else {
        this.viewTable()
      }
    },
    showDataView(newVal, oldVal) {
      let searchBar = document.querySelector('.navbar-end #mapSearchWrapper')
      if (newVal) {
        document
          .getElementsByTagName('html')[0]
          .classList.remove('hidden-scrollbar')
        document.getElementById('__nuxt').style.height = 'auto'
        document.querySelector('.wrapper-content').style.height = 'fit-content'
        this.toggleItems()
        this.$store.commit('maps/setMapIsReady', false)
        this.mobileSwitcher = 1
        if (searchBar) searchBar.style.visibility = 'hidden'
      } else if (!newVal && newVal !== oldVal) {
        if (searchBar) searchBar.style.visibility = 'visible'
        this.mobileSwitcher = 0
        document.querySelector('.wrapper-content').style.height =
          'calc(100vh - 54px)'
        this.toggledItems.selected = null
        document
          .getElementsByTagName('html')[0]
          .classList.add('hidden-scrollbar')
        document.getElementById('__nuxt').style.height = '100%'
      }
    },
    mapIsReady(newVal) {
      if (newVal) {
        this.setup()
        this.$store.dispatch(
          'app/setBaseLayers',
          document.querySelector('.leaflet-control-layers-base')
        )
      }
      // const BORDER_SIZE = 4
      // const panel = document.querySelector('.bottom-sheet__pan')

      // let m_pos
      // function resize(e) {
      //   const dx = m_pos - e.x
      //   m_pos = e.x
      //   panel.style.width =
      //     parseInt(getComputedStyle(panel, '').width) + dx + 'px'
      // }

      // panel.addEventListener(
      //   'mousedown',
      //   function (e) {
      //     console.log("here")
      //     if (e.offsetX < BORDER_SIZE) {
      //       m_pos = e.x
      //       document.addEventListener('mousemove', resize, false)
      //     }
      //   },
      //   false
      // )

      // document.addEventListener(
      //   'mouseup',
      //   function () {
      //     console.log("mouup")
      //     document.removeEventListener('mousemove', resize, false)
      //   },
      //   false
      // )
    }
  },
  methods: {
    newGeometry(geom) {
      this.$store.dispatch('maps/newGeom', geom)
      this.closeBottomSheet()
    },
    measureDistance() {
      document.querySelector(
        '.leaflet-left > div:nth-child(5)'
      ).style.visibility = 'visible'
      this.$store.dispatch('maps/calculateDistance')
      this.closeBottomSheet()
    },
    openBottomSheet() {
      this.$refs.myBottomSheet.open()
    },
    closeBottomSheet() {
      this.$refs.myBottomSheet.close()
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
        iconSize: [20, 20]
      })
    },
    goToElement(payload) {
      let coords = payload.coords
      let topo = payload.topo
      !this.showMap && this.viewMap()
      setTimeout(() => {
        if (this.$layerGroups['customMarker']) {
          this.$map.removeLayer(this.$layerGroups['customMarker'])
        }
        if (topo == 'MultiPolygon') {
          let multipolygone = coords[0]
          let array1 = []
          let array2 = []
          let array3 = []
          for (let i = 0; i < multipolygone[0].length; i++) {
            let x = []
            x[0] = multipolygone[0][i][1]
            x[1] = multipolygone[0][i][0]
            array1.push(x)
          }

          array2.push(array1)
          array3.push(array2)

          this.$layerGroups['customMarker'] = L.polygon(array3, {
            color: 'yellow'
          }).addTo(this.$map)
        }
        if (topo == 'Polygon') {
          let polygon = coords[0]
          let array1 = []
          let array2 = []
          for (let i = 0; i < polygon.length; i++) {
            let x = []
            x[0] = polygon[i][1]
            x[1] = polygon[i][0]
            array1.push(x)
          }

          array2.push(array1)
          this.$layerGroups['customMarker'] = L.polygon(array2, {
            color: 'yellow'
          }).addTo(this.$map)
        }
        if (topo == 'LineString') {
          let lineStrings = []
          for (let i = 0; i < coords.length; i++) {
            let x = []
            x[0] = coords[i][1]
            x[1] = coords[i][0]
            lineStrings.push(x)
          }
          this.$layerGroups['customMarker'] = L.polyline(lineStrings, {
            color: 'yellow',
            weight: 10,
            opacity: 0.8,
            smoothFactor: 1
          }).addTo(this.$map)
        }
        if (topo == 'Point') {
          let pulsatingIcon = this.generatePulsatingMarker(20, 'yellow')

          let x = coords[0]
          let y = coords[1]

          this.$layerGroups['customMarker'] = L.marker([y, x], {
            icon: pulsatingIcon
          }).addTo(this.$map)
          this.$map.flyTo([y, x], 15)
        }
        if (topo != 'Point')
          this.$map.fitBounds(this.$layerGroups['customMarker'].getBounds())
      }, 650)
    },
    searchNearbiestPoint(coords) {
      this.geometry.coordinates = coords
      this.allowDraw = false
      this.isShortcut = true
      this.nearbyKey++
    },
    setGoTo(coords) {
      this.goTo = coords
    },
    setGoToWithDirection(payload) {
      this.goTo = payload.coords
      this.showDirection(true, payload.direction)
    },
    closeIterenary() {
      //remove etiniraire
      if (this.$layerGroups['itinerary'] != null) {
        let path = document.querySelector('.leaflet-routing-container')
        if (path)
          setTimeout(() => {
            path.style.marginRight = '-443'
          }, 300)
        this.closed()
        this.$map.removeControl(this.$layerGroups['itinerary'])
        this.$layerGroups['itinerary'] = null
        this.iterenaryLoaded = false
      }
    },
    //show etiniraire
    showDirection(allPoint, direction) {
      //Direction : false === reversed
      let refThis = this
      let coords = []

      let feature = {}
      if (allPoint) {
        feature = {
          geometry: {
            type: 'Point',
            coordinates: this.goTo
          }
        }
        this.goTo = []
      } else {
        feature = this.features[0].feature
      }

      var geoPlan = L.Routing.Plan.extend({
        createGeocoders: function() {
          var container = L.Routing.Plan.prototype.createGeocoders.call(this),
            // Create a reverse waypoints button
            reverseButton = refThis.createReverseButton(
              '<i class="fas fa-exchange-alt fa-sm" ></i>',
              container
            ),
            // Create a reverse waypoints button
            closeButton = refThis.createCloseButton(
              '<i class="fas fa-times fa-sm" ></i>',
              container
            ),
            //http://gis.stackexchange.com/questions/193235/leaflet-routing-machine-how-to-dinamically-change-router-settings

            // Create a button for driving routes
            carButton = refThis.createCarButton(
              '<i class="fas fa-car fa-sm" ></i>',
              container
            ),
            // Create a button for biking routes
            bikeButton = refThis.createBikeButton(
              '<i class="fas fa-biking fa-sm" ></i>',
              container
            ),
            // Create a button for walking routes
            walkButton = refThis.createWalkButton(
              '<i class="fas fa-walking fa-sm" ></i>',
              container
            )

          // Event to close direction popup
          if (!refThis.isMobile) {
            L.DomEvent.on(
              closeButton,
              'click',
              function() {
                this.closeIterenary()
              },
              refThis
            )
          }

          // Event to reverse the waypoints
          L.DomEvent.on(
            reverseButton,
            'click',
            function() {
              var waypoints = this.getWaypoints()
              this.setWaypoints(waypoints.reverse())
            },
            this
          )

          // Event to generate walking routes
          L.DomEvent.on(
            walkButton,
            'click',
            function() {
              refThis.selectedProfile = 'foot'
              refThis.$layerGroups['itinerary'].getRouter().options.profile =
                'foot'
              refThis.$layerGroups['itinerary'].route()
              refThis.$layerGroups['itinerary'].setWaypoints(
                refThis.$layerGroups['itinerary'].getWaypoints()
              )
              document
                .querySelector('.leaflet-routing-geocoders  button.selected')
                .classList.remove('selected')
              console.log('Walking route')
              document.querySelector('.walk-button').classList.add('selected')
            },
            this
          )

          // Event to generate biking routes
          L.DomEvent.on(
            bikeButton,
            'click',
            function() {
              refThis.selectedProfile = 'bike'
              refThis.$layerGroups['itinerary'].getRouter().options.profile =
                'bike'
              refThis.$layerGroups['itinerary'].route()
              refThis.$layerGroups['itinerary'].setWaypoints(
                refThis.$layerGroups['itinerary'].getWaypoints()
              )
              document
                .querySelector('.leaflet-routing-geocoders  button.selected')
                .classList.remove('selected')
              document.querySelector('.bike-button').classList.add('selected')
              console.log('Biking route')
            },
            this
          )

          // Event to generate driving routes
          L.DomEvent.on(
            carButton,
            'click',
            function() {
              refThis.selectedProfile = 'car'
              refThis.$layerGroups['itinerary'].getRouter().options.profile =
                'car'
              refThis.$layerGroups['itinerary'].route()
              refThis.$layerGroups['itinerary'].setWaypoints(
                refThis.$layerGroups['itinerary'].getWaypoints()
              )
              document
                .querySelector('.leaflet-routing-geocoders  button.selected')
                .classList.remove('selected')
              document.querySelector('.car-button').classList.add('selected')

              console.log('Driving route')
            },
            refThis
          )

          return container
        }
      })

      // Create a plan for the routing
      var plan = new geoPlan([], {
        // Default geocoder
        geocoder: new L.Control.Geocoder.Nominatim(),
        // Create routes while dragging markers
        routeWhileDragging: true,

        createMarker: function(i, wp, nWps) {
          if (i === 0) {
            const myStartIcon = L.divIcon({
              html:
                '<i class="fa fa-3x fa-map-marker-alt" style="color:#1a73e8; backgtound:transparent"></i>',
              className: 'myDivIcon',
              iconSize: [10, 10],
              iconAnchor: [15, 35],
              popupAnchor: [1, -32]
            })
            return L.marker(wp.latLng, {
              icon: myStartIcon,
              draggable: true
            })
          } else if (i === nWps - 1) {
            var myEndIcon = L.divIcon({
              html:
                '<i class="fa fa-3x fa-map-marker-alt" style="color:#5BB058;"></i>',
              className: 'myDivIcon',
              iconSize: [10, 10],
              iconAnchor: [15, 35]
            })
            return L.marker(wp.latLng, { icon: myEndIcon, draggable: true })
          } else {
            var myMiddleIcon = L.divIcon({
              html:
                '<i class="fa fa-2x fa-map-marker-alt" style="color:#000;"></i>',
              className: 'myDivIcon',
              iconSize: [10, 10],
              iconAnchor: [10, 15]
            })
            return L.marker(wp.latLng, { icon: myMiddleIcon, draggable: true })
          }
        }
      })

      if (feature.geometry && feature.geometry.type == 'Point') {
        coords = feature.geometry.coordinates
        //add loader brefore set the route
        // this.$nextTick(() => {
        //   this.$nuxt.$loading.start()
        //   setTimeout(() => this.$nuxt.$loading.finish(), 900)
        // })

        //remove the last routing if is exist
        if (refThis.$layerGroups['itinerary'] != null) {
          this.$map.removeControl(refThis.$layerGroups['itinerary'])
          refThis.$layerGroups['itinerary'] = null
        }

        refThis.$layerGroups['itinerary'] = L.Routing.control({
          waypoints: [],

          // Change these.. Offline GH routing
          router: L.Routing.osrmv1({
            serviceUrl: `${osrm_server}`,
            language: 'fr',
            profile: refThis.selectedProfile
          }),

          // Use the created plan for GH routing
          plan: plan,

          // Show the routing icon on a reloaded window
          show: true,

          // Enable the box to be collapsed
          collapsible: false,

          showAlternatives: refThis.isMobile ? false : true,
          lineOptions: {
            addWaypoints: true,

            styles: [
              {
                color: '#669df6',
                opacity: 0.9,
                weight: 10
              }
            ]
          },
          altLineOptions: {
            styles: [
              {
                color: '#aaa',
                opacity: 0.9,
                weight: 10
              }
            ]
          },
          // Collapse button which opens the routing icon (mouse over)
          // Fix this so the routing box closes when mouse leaves the routing window rather than the window "X"
          collapseBtn: function(itinerary) {
            var collapseBtn = L.DomUtil.create(
              'span',
              itinerary.options.collapseBtnClass
            )
            L.DomEvent.on(collapseBtn, 'click', itinerary._toggle, itinerary)
            itinerary._container.insertBefore(
              collapseBtn,
              itinerary._container.firstChild
            )
          }
        })
          .on('routingstart', () => {
            !this.iterenaryLoaded && this.$nuxt.$loading.start()
            this.iterenaryLoaded = true
          })
          .on('routesfound routingerror routeselected', () => {
            this.$nuxt.$loading.finish()
          })

        if (this.goTo.length > 0) {
          let c = [
            L.latLng(this.goTo[1], this.goTo[0]),
            L.latLng(coords[1], coords[0])
          ]

          plan.setWaypoints(c)
          refThis.$layerGroups['itinerary'].addTo(this.$map)
          let path = document.querySelector('.leaflet-routing-container')
          path.setAttribute('id', 'itenerary')
          if (!this.isMobile) {
            this.opened(path.clientWidth, true)
            if (path) {
              path.style.marginRight = '-893px'
              path.style.display = 'none'
            }
            setTimeout(() => {
              path.style.marginRight = '-443px'
              path.style.right = 0
              path.style.display = 'flex'
            }, 300)
          } else {
            this.toggledItems.prec = this.toggledItems.selected
            this.toggledItems.selected = 'itinerary'
            setTimeout(() => {
              document.querySelector('#itinerary').appendChild(path)
            }, 0)
            this.openBottomSheet()
          }
        } else if ('geolocation' in navigator) {
          navigator.geolocation.getCurrentPosition(
            position => {
              let c = []
              if (direction != null && !direction) {
                c = [
                  L.latLng(coords[1], coords[0]),
                  L.latLng(position.coords.latitude, position.coords.longitude)
                ]
              } else {
                c = [
                  L.latLng(position.coords.latitude, position.coords.longitude),
                  L.latLng(coords[1], coords[0])
                ]
              }
              plan.setWaypoints(c)
              refThis.$layerGroups['itinerary'].addTo(this.$map)
              let path = document.querySelector('.leaflet-routing-container')
              path.setAttribute('id', 'itenerary')
              if (!this.isMobile) {
                this.opened(path.clientWidth, true)
                if (path) {
                  path.style.marginRight = '-893px'
                  path.style.display = 'none'
                }
                setTimeout(() => {
                  path.style.marginRight = '-443px'
                  path.style.right = 0
                  path.style.display = 'flex'
                }, 300)
              } else {
                this.toggledItems.prec = this.toggledItems.selected
                this.toggledItems.selected = 'itinerary'
                setTimeout(() => {
                  document.querySelector('#itinerary').appendChild(path)
                }, 0)
                this.openBottomSheet()
              }
            },
            e => {
              this.$notification.error({
                message: 'Veuillez activer le service de localisation !'
              })
            }
          )
        }

        // refThis.$map.on('contextmenu', function (e) {
        //   if (refThis.$layerGroups['itinerary'] != null) {
        //      var template = `
        //   <ul class="map-tooltip-list">
        //        <li id="btnCoords">
        //        "here"
        //        </li>
        //         <li id="directionFromHere">
        //          Itérinaire vers ce point
        //        </li>
        //         <li id="directionToHere">
        //          Itérinaire apartir de ce point
        //        </li>
        //         <li id="nearbiestPoints">
        //          Recherche à proximité
        //        </li>
        //         <li id="newPoint">
        //          Nouveau Point
        //        </li>
        //  </ul>`

        //     // L.DomEvent.on(startBtn, 'click', function () {
        //     //   refThis.$layerGroups['itinerary'].spliceWaypoints(0, 1, e.latlng)
        //     //   refThis.$map.closePopup()
        //     // })

        //     // L.DomEvent.on(destBtn, 'click', function () {
        //     //   refThis.$layerGroups['itinerary'].spliceWaypoints(
        //     //     refThis.$layerGroups['itinerary'].getWaypoints().length - 1,
        //     //     1,
        //     //     e.latlng
        //     //   )
        //     //   refThis.$map.closePopup()
        //     // })
        //     L.popup()
        //       .setContent(template)
        //       .setLatLng(e.latlng)
        //       .openOn(refThis.$map)
        //   }
        // })
      }
    },
    createButton(label, container) {
      var btn = L.DomUtil.create('button', '', container)
      btn.setAttribute('type', 'button')
      btn.setAttribute('class', 'btn btn-info btn-sm mr-2')
      // btn.setAttribute('style', 'float:right, marign :5px;')
      btn.innerHTML = label
      btn.title = 'Start route location'
      return btn
    },
    // GraphHopper foot/walk button
    createWalkButton(label, container) {
      var btn = L.DomUtil.create('button', '', container)
      btn.setAttribute('type', 'button')
      btn.classList.add('walk-button')
      this.selectedProfile === 'foot' && btn.classList.add('selected')
      btn.innerHTML = label
      btn.title = 'marche à pied'
      return btn
    },

    // GraphHopper bike button
    createBikeButton(label, container) {
      var btn = L.DomUtil.create('button', '', container)
      btn.setAttribute('type', 'button')
      btn.innerHTML = label
      btn.classList.add('bike-button')
      this.selectedProfile === 'bike' && btn.classList.add('selected')
      btn.title = 'Cyclisme'
      return btn
    },
    //  GraphHopper car button
    createCarButton(label, container) {
      var btn = L.DomUtil.create('button', '', container)
      btn.setAttribute('type', 'button')
      btn.innerHTML = label
      btn.classList.add('car-button')
      this.selectedProfile === 'car' && btn.classList.add('selected')
      btn.title = 'Conduite'
      return btn
    },
    // Create a Leaflet buttons for GraphHopper
    // Reserve waypoints button
    createReverseButton(label, container) {
      var btn = L.DomUtil.create('button', '', container)
      btn.setAttribute('type', 'button')
      btn.classList.add('reverse-button')
      btn.innerHTML = label
      btn.title = 'Inverser le point de départ et la destination'
      return btn
    },
    // Create a Leaflet buttons for GraphHopper
    // Close button
    createCloseButton(label, container) {
      if (!this.isMobile) {
        var btn = L.DomUtil.create('button', '', container)
        btn.setAttribute('type', 'button')
        btn.classList.add('close-button')
        btn.innerHTML = label
        btn.title = "Fermer l'itinéraire"
        return btn
      }
    },
    toggleItems() {
      const icons = document.querySelector('#toggle-button i')
      const elem = document.getElementById(this.toggledItems.selected)
      if (
        elem &&
        this.toggledItems[this.toggledItems.selected] === false &&
        !this.showDataView
      ) {
        if (elem) elem.style.right = 0
        this.toggledItems[this.toggledItems.selected] = true
        icons.classList.add('fa-chevron-right')
        icons.classList.remove('fa-chevron-left')
        this.opened(elem.clientWidth)
      } else if (elem) {
        if (this.toggledItems.selected === 'proximityTool')
          this.$DrawLayer.clearLayers()
        this.toggledItems[this.toggledItems.selected] = false
        elem.style.transition = 'all .3s ease-out'
        if (elem) elem.style.right = `-${elem.clientWidth + 10}px`
        icons.classList.add('fa-chevron-left')
        icons.classList.remove('fa-chevron-right')
        this.closed()
      }
      this.isShortcut = false
      this.allowDraw = true
    },
    showComponent(args, init) {
      if (window.innerWidth < 600) {
        if (
          (args !== 'actionsShortcut' &&
            this.toggledItems.selected === 'actionsShortcut') ||
          this.toggledItems.selected === null ||
          (this.toggledItems === 'actionsShortcut' && this.toggledItems.prec) ||
          init
        ) {
          if (this.toggledItems.prec) {
            this.toggledItems.selected = this.toggledItems.prec
            this.toggledItems.prec = null
            this.loadingIndicator = true
            //TODO To reload permission
            setTimeout(() => {
              this.loadingIndicator = false
            }, 0)
          } else {
            this.toggledItems.selected = args
          }
        }
        if (args !== 'actionsShortcut' || init) {
          this.bottomSheetKey++
        }

        setTimeout(() => {
          if (args === 'layers' && this.$refs['layers']) {
            this.$refs['layers'].getPermissions()
          }
          this.openBottomSheet()
        }, 0)
        if (
          this.toggledItems.selected === 'actionsShortcut' &&
          !this.toggleItems.prec
        ) {
          //Clear all
          if (this.$layerGroups['customMarker'] != null) {
            this.$map.removeLayer(this.$layerGroups['customMarker'])
          }
          if (this.$refs.featureSettings) {
            this.$refs.featureSettings.onCancel()
          }
          if (this.$layerGroups['marker']) {
            this.$map.removeLayer(this.$layerGroups['marker'])
          }
          if (this.$layerGroups['featureInfo'])
            this.$map.removeLayer(this.$layerGroups['featureInfo'])
          this.$DrawLayer.clearLayers()
          if (this.$layerGroups['itinerary']) {
            this.closeIterenary()
          }
          this.isShortcut = true
        }
      } else {
        const elem = document.getElementById(args)
        if (args === 'layers') {
          this.$refs['layers'].getPermissions()
        }
        if (
          this.toggledItems.selected !== null &&
          this.toggledItems[this.toggledItems.selected]
        ) {
          elem.style.transition = 'none'
        } else {
          elem.style.transition = 'all .3s ease-out'
        }
        this.toggledItems.selected = args
        Object.keys(this.toggledItems).map(key => {
          if (this.toggledItems[key]) {
            const element = document.getElementById(key)
            if (element) {
              element.style.transition = 'none'
              element.style.right = `-${element.clientWidth + 10}px`
            }
          }
          this.toggledItems[key] =
            key == args
              ? true
              : key !== 'selected'
                ? false
                : this.toggledItems['selected']
          if (key === args) {
            if (elem) {
              elem.style.right = 0
            }
          }
        })
        let resizeObserver = new ResizeObserver(() => {
          if (
            Object.keys(this.toggledItems)
              .filter(k => k !== 'selected')
              .some(k => this.toggledItems[k])
          )
            this.opened(elem.clientWidth)
        })
        resizeObserver.observe(elem)
        const icons = document.querySelector('#toggle-button i')
        icons.classList.add('fa-chevron-right')
        icons.classList.remove('fa-chevron-left')
        if (args !== 'itenerary' && this.$layerGroups['itinerary']) {
          this.closeIterenary()
        }
        if (args !== 'itenerary') {
          this.showFeatureSettings = false
          if (this.$layerGroups['featureInfo'])
            this.$map.removeLayer(this.$layerGroups['featureInfo'])

          if (this.newLayer) this.$DrawLayer.removeLayer(this.newLayer)
        }
      }
    },
    opened(width, itinerary) {
      if (itinerary) {
        this.showComponent('itenerary')
      }
      const leafletControl = document.querySelector('.leaflet-right')
      if (leafletControl) leafletControl.style.right = `${width - 5}px`
    },
    closed() {
      const leafletControl = document.querySelector('.leaflet-right')
      if (leafletControl) leafletControl.style.right = 0
      this.$layerGroups['marker'] &&
        this.$map.removeLayer(this.$layerGroups['marker'])
      this.isShortcut = false
    },
    viewMap() {
      this.showMap = true
      this.showDataView = false
      this.showGetCurrentPosition = true
      this.mobileSwitcher = 0
    },
    viewTable() {
      this.showMap = false
      this.showFeatureSettings = false
      this.showGetCurrentPosition = false
      this.showDataView = true
      this.mobileSwitcher = 1
    },
    openFeatureSetting() {
      if (this.$refs.featureSettings) {
        this.$refs.featureSettings.isAuthorizedLayer()
        this.loadingIndicator = true
        setTimeout(() => {
          this.loadingIndicator = false
        }, 0)
      }
      this.showFeatureSettings = true
      if (this.isMobile) {
        this.showComponent('featureSettings', true)
        //TODO to reload permissions
      }
    },
    onNewLayer(layer) {
      this.openFeatureSetting()
      this.newLayer = layer
      this.mode = 'create'
      // this.loadingIndicator = true
      // this.layersSharedWithUser = this.mapLayers.map(l => {
      //       return {
      //         ...l,
      //         name:
      //           l.name && l.name.includes('____')
      //             ? l.name.split('____')[1]
      //             : l.name
      //       }
      //     })
      this.$store.commit('maps/setActiveLayer', {})
      // backApi
      //   .get(`layers/sharedWithOthersInMap/${this.currentMap.id}`)
      //   .then(({ data }) => {
      //     this.layersSharedWithUser = data.content.map(l => {
      //       return {
      //         ...l,
      //         name:
      //           l.name && l.name.includes('____')
      //             ? l.name.split('____')[1]
      //             : l.name
      //       }
      //     })
      //     this.loadingIndicator = false
      //     this.$store.commit('maps/setActiveLayer', {})
      //   })
      //   .catch(() => {
      //     if (!this.newLayer.feature.id)
      //       this.$DrawLayer.removeLayer(this.newLayer)
      //     this.loadingIndicator = false
      //     this.showFeatureSettings = false
      //     this.newLayer = {}
      //     // reset layer on edit mode

      //     this.$emit('cancel')
      //     this.$notification.error({
      //       message: 'Error!',
      //       description: "Une erreur inattendue s'est produite !"
      //     })
      //   })
    },
    editFeatureInfo({ features, current }) {
      if (this.canShowFeatureInfo) {
        this.mode = 'edit'
        this.features = features
        this.newLayer = features[current]

        //load the active layer
        if (
          this.newLayer.feature.layer.name != this.maps.activeLayer.name ||
          this.showFeatureSettings == false
        ) {
          let layerId = this.newLayer.feature.layer.id
          this.openFeatureSetting()
          this.loadingIndicator = true
          let url = this.profile.authenticated
            ? `layers/withFieldsAndResource/maps/${
                this.currentMap.slug
              }/feature/${layerId}`
            : `layers/public/withFieldsAndResource/maps/${
                this.currentMap.slug
              }/feature/${layerId}`

          backApi
            .get(url)
            .then(({ data }) => {
              this.$store.commit('maps/setActiveLayer', data)
              this.loadingIndicator = false
            })
            .catch(() => {
              this.loadingIndicator = false
              this.showFeatureSettings = false
              this.$notification.error({
                message: 'Error!',
                description: "Une erreur inattendue s'est produite !"
              })
            })
        } else this.openFeatureSetting()
      }
    },
    onEdit(layer) {
      this.openFeatureSetting()
      this.mode = 'edit'

      if (
        this.newLayer &&
        this.newLayer.feature &&
        this.newLayer.feature.layer &&
        this.newLayer.feature.layer.typeLimit === 'LAYER'
      ) {
        if (this.newLayer.editEnabled()) this.newLayer.disableEdit()
      }

      this.newLayer = layer

      if (this.newLayer.feature.layer.typeLimit === 'LAYER') {
        this.newLayer.enableEdit()
      }
    },
    onSave() {
      if (this.isMobile) {
        this.closeBottomSheet()
        this.toggledItems.selected = 'actionsShortcut'
      }
      this.showFeatureSettings = false
    },
    setup() {
      document.querySelector('.leaflet-right').style.WebkitTransition =
        'right .35s'
      const conrds = localStorage.getItem('getSharedFeature')
      if (conrds != null) {
        let topo = JSON.parse(conrds)[0]
        let lat = JSON.parse(conrds)[1]
        let lang = JSON.parse(conrds)[2]
        if (topo === 'Point') {
          //console.log("topo, lat, lang", lang, lat, topo);
          this.$map.flyTo([lat, lang], 12, {
            animate: true,
            duration: 2 // in seconds
          })
          var marker = L.marker([lat, lang]).addTo(this.$map)
          marker
            .bindPopup(
              `<b>
              L'emplacement qui a été partagé est</b><br>
              latitude : ${lang} et longitude :${lat} `
            )
            .openPopup()
        } else if (topo === 'LineString') {
          let x = lat.toString()
          const latX = x.split(',')
          let y = lang.toString()
          const langY = y.split(',')
          this.$map.fitBounds([[latX[1], latX[0]], [langY[1], langY[0]]])
          /*   var polyline = L.polyline([
          [latX[1], latX[0]],
          [langY[1], langY[0]],
        ]).addTo(this.$map); */

          /*      polyline.setStyle({
          color: "blue",
        }); */
        } else if (topo == 'Polygon') {
          let f = lang.split(',')
          const rsl = f.reduce(function(result, value, index, array) {
            if (index % 2 === 0) result.push(array.slice(index - 1, index + 1))
            return result
          }, [])
          let rslt = []
          for (let i = 1; i < rsl.length; i++) {
            rslt.push(rsl[i])
          }
          var poly = L.polygon(rslt)
          this.$map.fitBounds(poly.getBounds())
        }

        setTimeout(() => {
          localStorage.removeItem('getSharedFeature')
        }, 500)
      }
      this.$nextTick(() => {
        this.$nuxt.$loading.start()
        setTimeout(() => this.$nuxt.$loading.finish(), 900)
      })
    }
  },

  beforeMount() {
    if (
      this.currentMap.privacy.toLowerCase() === 'PRIVATE'.toLowerCase() &&
      !this.profile.authenticated
    ) {
      //this.$router.push("/auth");
      window.open(`${frontend}/auth`, '_self')
    }
    // Hide the scroll bar
    if (!this.showDataView) {
      document.getElementById('__nuxt').style.height = '100%'
      document.getElementsByTagName('html')[0].classList.add('hidden-scrollbar')
    }
  },
  beforeDestroy() {
    this.$store.commit('maps/setMapIsReady', false)
    window.removeEventListener('resize', this.checkWindowWidth)
    // Display the scroll bar
    if (!this.showDataView) {
      document
        .getElementsByTagName('html')[0]
        .classList.remove('hidden-scrollbar')
    }
  }
}
</script>

<style lang="scss">
.wrapper-content {
  margin-top: 58px;
  height: calc(100vh - 58px);
  @include respond('tab-land') {
    margin-top: 40px;
  }
  @include respond('tab-port') {
    margin-top: 33.4px;
  }
  @include respond('phone') {
    margin-top: 31.3px;
  }
}
#statusBar {
  position: relative;
  background: #fff !important;
  padding: 5px;
  .map-header {
    .map-name {
      .map-title:nth-cild(2) {
        line-height: 1.5;
        text-align: left;
        max-width: 250px;
        width: fit-content;
        white-space: nowrap;
        text-overflow: ellipsis;
        overflow: hidden;
        display: inline-block;
        vertical-align: middle;
      }
    }
    > div:nth-child(1),
    > div:nth-child(3) {
      flex: 0.45;
    }
    > div:nth-child(2) {
      width: 0.15;
    }
    div:nth-child(2) {
      text-align: center;
    }
    div:nth-child(3) {
      display: flex;
      justify-content: flex-end;
    }
  }
  @include respond('tab-port') {
    padding: 0;
  }
}

.btn-table {
  background: $color-primary;
  color: white;
  padding: 12px;
  border: 1px solid white;
}
.btn-table:hover {
  background: rgba($color-primary, 0.85);
}
.map-name {
  font-size: 15px;
  padding: 5px;
  position: relative;
  @include respond('tab-port') {
    width: 50%;
  }
}
.map-name span {
  font-size: 1.3rem;
  &:last-child {
    @include respond('tab-port') {
      width: 100%;
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow: hidden;
    }
  }
}
.scale-position {
  @include respond('tab-port') {
    display: none;
  }
}
#scaleKM {
  border: 1px solid #fff;
  background: #fff;
  border-top: none;
  color: $color-primary;
}
#scaleML {
  border: 1px solid #fff;
  background: #fff;
  border-bottom: none;
  color: $color-primary;
}
#latCoords {
  border: 1px solid #fff;
  background: #fff;
  border-top: none;
  color: $color-primary;
}
#lngCoords {
  border: 1px solid #fff;
  background: #fff;
  border-bottom: none;
  color: $color-primary;
}
.map-badge {
  width: 8px;
  height: 60%;
  position: absolute;
  top: 20%;
  &.public {
    background-color: rgb(0, 136, 0);
  }
  &.public-with-link {
    background-color: orange;
  }
  &.private {
    background-color: red;
  }
  &.archived {
    background-color: rgb(128, 128, 128);
  }
}
.walk-button,
.reverse-button,
.bike-button,
.car-button,
.close-button,
.leaflet-routing-add-waypoint {
  border: none !important;
  background-color: transparent !important;
  float: left !important;
  i {
    color: #ccc;
    font-size: 1.35rem !important;
  }
}
.leaflet-routing-container .leaflet-routing-geocoders button {
  width: 35px !important;
  height: 35px !important;
}

.reverse-button {
  position: absolute;
  top: 10px;
  right: 10px;
  transform: rotate(90deg);
}
.leaflet-bar button.selected {
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.2) !important;
  padding: 14px;
  display: flex;
  justify-content: center;
  align-items: center;
  i {
    margin: 5px;
    color: white !important;
  }
}
.leaflet-bar button.walk-button i,
.leaflet-bar button.car-button i,
.leaflet-bar button.bike-button i,
.leaflet-bar button.reverse-button i {
  font-size: 1.35rem !important;
}
.leaflet-bar button.close-button i {
  margin-top: 5px;
  font-size: 1.6rem !important;
}
.close-button {
  position: absolute;
  right: 0;
  i {
    font-size: 1.6rem !important;
    color: white !important;
  }
}
.leaflet-routing-add-waypoint {
  position: absolute;
  right: 40px;
  border: 2px solid white !important;
  border-radius: 50% !important;
  @include respond('phone') {
    right: 5px;
    &::after {
      position: relative;
    }
  }
  &::after {
    color: white;
    font-family: 'Font Awesome 5 Free';
    font-weight: 900;
    content: '\f067';
    font-size: 1.2rem;
    line-height: 1.5;
  }
}
.leaflet-routing-geocoder {
  width: 85%;
  @include respond('phone') {
    width: 97%;
  }
}
#proximityTool {
  position: absolute;
  background: white;
  top: 11px;
  height: 97%;
  width: 360px;
  bottom: 0.75rem;
  right: -360px;
  z-index: 399;
  border-radius: 5px;
  display: flex;
  flex-direction: column;
  @include mapModalForMobile;
  @include respond('phone') {
    max-height: 60vh !important;
  }
  .content-wrapper {
    height: 100%;
    padding: 1rem 1rem;
    overflow: hidden auto;
    @include respond('phone') {
      padding: 1rem 1rem 0 1rem;
    }
  }
}
.map-tools-trigger {
  display: none;
  border-top: 1px solid $color-grey-light-2;
  h2 {
    white-space: nowrap;
    font-size: 1rem;
    margin: 0 auto;
    color: $color-grey-dark-2;
    i {
      margin-right: 1rem;
    }
  }
  @include respond('phone') {
    max-height: 40px;
    display: flex;
    padding: 1rem 1.5rem;
    background-color: #fff;
  }
}
.bottom-sheet {
  &__separator {
    margin: 0 auto;
    margin-top: 1rem;
    width: 95%;
    height: 1px;
    background-color: #ccc;
  }
}
.switcher {
  li.is-active {
    color: $color-primary;
  }
  .tab-content {
    display: none;
  }
}
</style>
