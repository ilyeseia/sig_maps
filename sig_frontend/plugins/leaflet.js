import Vue from 'vue'
import L from 'leaflet'
import 'leaflet-draw'
import 'leaflet-contextmenu'
import './leaflet-editable'
import 'leaflet.markercluster'
import 'leaflet.markercluster.layersupport'
import { GeoSearchControl, OpenStreetMapProvider } from 'leaflet-geosearch'
import 'leaflet-measure'
import 'leaflet-measure/dist/leaflet-measure.fr.js'
import 'leaflet.browser.print/dist/leaflet.browser.print.min.js'
import 'leaflet-fullscreen/dist/Leaflet.fullscreen.js'
import 'leaflet-fullscreen/dist/leaflet.fullscreen.css'
//import 'leaflet-basemaps/L.Control.Basemaps.js'
//import 'leaflet-basemaps/L.Control.Basemaps.css'
import 'leaflet.locatecontrol/dist/L.Control.Locate.min.js'
import 'leaflet-easybutton/src/easy-button.js'
import 'leaflet-easybutton/src/easy-button.css'

//import 'leaflet-routing-machine/dist/leaflet-routing-machine.js'
//import 'leaflet-routing-machine/dist/leaflet-routing-machine.css'

//styles

import 'leaflet.locatecontrol/dist/L.Control.Locate.min.css'
import 'leaflet/dist/leaflet.css'
import 'leaflet-draw/dist/leaflet.draw.css'
import 'leaflet-contextmenu/dist/leaflet.contextmenu.css'
import 'leaflet.markercluster/dist/MarkerCluster.css'
import 'leaflet.markercluster/dist/MarkerCluster.Default.css'
import 'leaflet-geosearch/dist/style.css'
import 'leaflet-geosearch/assets/css/leaflet.css'
import 'leaflet-measure/dist/leaflet-measure.css'
import 'leaflet.browser.print/dist/leaflet.browser.print.min.js'

import drawLocales from 'leaflet-draw-locales'
import axios from 'axios';
import { backend } from '../constants'
// Automatically defines Leaflet.draw to the specified language
drawLocales('fr')

delete L.Icon.Default.prototype._getIconUrl
// fix: default layer icon doesn't show up
L.Icon.Default.mergeOptions({
  iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
  iconUrl: require('leaflet/dist/images/marker-icon.png'),
  shadowUrl: require('leaflet/dist/images/marker-shadow.png')
})

// layer filter button
L.Control.ToggleButton = L.Control.extend({
  options: {
    position: 'topright'
  },
  container: null,
  onAdd(map) {
    this.container = L.DomUtil.create(
      'button',
      'btn btn-default leaflet-icons text-secondary btn-leaflet-toggle'
    )
    this.container.innerHTML = '<i class="fas fa-chevron-left"></i>'

    this.container.style.border = '2px solid rgba(0,0,0,0.2)'
    this.container.title = 'Basculer le menu'
    this.container.id = 'toggle-button'

    return this.container
  }
})
// layer filter button
L.Control.FilterButton = L.Control.extend({
  options: {
    position: 'topright'
  },
  container: null,
  onAdd(map) {
    this.container = L.DomUtil.create(
      'button',
      'btn btn-default leaflet-icons text-secondary'
    )
    this.container.innerHTML = '<i class="fas fa-filter text-black"></i>'

    this.container.style.border = '2px solid rgba(0,0,0,0.2)'

    this.container.id = 'filter-button'
    this.container.title = 'Filtre avancé'

    return this.container
  }
})

// Get Current Location

L.Control.GetCurrentLocation = L.Control.extend({
  options: {
    position: 'topright'
  },
  container: null,
  onAdd(map) {
    this.container = L.DomUtil.create(
      'button',
      'btn btn-default leaflet-icons text-secondary'
    )
    this.container.innerHTML = '<i class="fas fa-home text-black"></i>'
    this.container.style.border = '2px solid rgba(0,0,0,0.2)'
    this.container.id = 'filter-button'
    this.container.title = 'Obtenir votre emplacement actuel'

    return this.container
  }
})

// layer filter global
L.Control.GlobalFilterButton = L.Control.extend({
  options: {
    position: 'topright'
  },
  container: null,
  onAdd(map) {
    this.container = L.DomUtil.create(
      'button',
      'btn btn-default leaflet-icons text-secondary'
    )
    this.container.innerHTML =
      '<i class="fas fa-search"></i>'
    this.container.style.border = '2px solid rgba(0,0,0,0.2)'
    this.container.id = 'global-filter-button'
    this.container.title = 'Filtre Globale'

    return this.container
  }
})

// Geo processing tools
L.Control.GeoProcessingButton = L.Control.extend({
  options: {
    position: 'topright'
  },
  container: null,
  onAdd(map) {
    this.container = L.DomUtil.create(
      'button',
      'btn btn-default leaflet-icons text-secondary'
    )
    this.container.innerHTML =
      '<i class="fas fa-shapes"></i>'
    this.container.style.border = '2px solid rgba(0,0,0,0.2)'
    this.container.id = 'geo-processing-button'
    this.container.title = 'Outils de géotraitement'

    return this.container
  }
})

//show layers button
L.Control.ShowLayersButton = L.Control.extend({
  options: {
    position: 'topright'
  },
  container: null,
  onAdd(map) {
    this.container = L.DomUtil.create(
      'button',
      'btn btn-default leaflet-icons text-secondary'
    )
    this.container.innerHTML =
      '<i class="fas fa-layer-group"></i>'
    this.container.id = 'show-layers-button'
    this.container.title = 'Liste des couches'
    this.container.style.zIndex = '9999 !important'

    return this.container
  }
})

//show layers button
L.Control.ProximityToolButton = L.Control.extend({
  options: {
    position: 'topright'
  },
  container: null,
  onAdd(map) {
    this.container = L.DomUtil.create(
      'button',
      'btn btn-default leaflet-icons text-secondary'
    )
    this.container.innerHTML =
      '<i class="fas fa-street-view"></i>'
    this.container.id = 'proximity-tool-button'
    this.container.title = 'Outil de proximité'
    this.container.style.zIndex = '9999 !important'
    this.container.style.display = localStorage.getItem('sigToken') ? 'block' : 'none'

    return this.container
  }
})

L.control.toggleButton = function () {
  return new L.Control.ToggleButton()
}
L.control.filterButton = function () {
  return new L.Control.FilterButton()
}
L.control.globalFilterButton = function () {
  return new L.Control.GlobalFilterButton()
}
L.control.geoProcessingButton = function () {
  return new L.Control.GeoProcessingButton()
}
/* Get Cuurent Location for user  */
L.control.getCurrentLocation = function () {
  return new L.Control.GetCurrentLocation()
}
L.control.showLayersButton = function () {
  return new L.Control.ShowLayersButton()
}
L.control.proximityToolButton = function () {
  return new L.Control.ProximityToolButton()
}

// use leaflet as a Vuejs plugin so it can be accessible as a global variable (this.$map)
const leaflet = {
  install(V, options) {
    V.component('leaflet', {
      /* create the map component */
      render(h) {
        return (
          <div
            id="map"
            style="height:100%; width: 100%; position: relative; top:0; left:0"
          >
            {this.$slots.default}
          </div>
        )
      },
      mounted() {
        var defaultBaseLayer;
        var that = this;
        function setup() {
          var providers = that.$store.state.settings.settings

          const objects = providers.reduce(
            (key, value) => ({ ...key, [value.code]: value.code }),
            {}
          )

          var default_provider = providers.find(p => p.default_value == true)

          var code_provider =
            default_provider == undefined ? 'OSM HOT' : default_provider.code

          //OSM HOT tiles attribution and URL
          var osmHotLink = '<a href="https://www.openstreetmap.fr/"> &copy; OpenStreetMap France</a>'
          var osmHotURL = 'https://{s}.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png'
          var osmHotAttrib = osmHotLink

          var OSM_HOT_MAP = L.tileLayer(osmHotURL, {
          })

          //OSM FRANCE tiles attribution and URL
          var osmFrLink = '<a href="https://www.openstreetmap.fr/"> &copy; OpenStreetMap France</a>'
          var osmFrURL = 'https://{s}.tile.openstreetmap.fr/osmfr/{z}/{x}/{y}.png'


          var OSM_FR_MAP = L.tileLayer(osmFrURL, {
          })

          //OSM OpenTopoMap tiles attribution and URL
          var OpenTopoLink = '<a href="https://opentopomap.org">Leaflet | &copy; OpenStreetMap contributors</a>'
          var OpenTopoURL = 'https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png'
          var OpenTopoAttrib = OpenTopoLink

          var OPEN_TOPO_MAP = L.tileLayer(OpenTopoURL, {
          })

          //GOOGLE SATELITE tiles attribution and URL
          var googleSateliteURL =
            'http://{s}.google.com/vt/lyrs=s&x={x}&y={y}&z={z}'

          var GOOGLE_SAT_MAP = L.tileLayer(googleSateliteURL, {
            maxZoom: 21,
            subdomains: ['mt0', 'mt1', 'mt2', 'mt3']
          })

          //GOOGLE STREET tiles attribution and URL
          var googleStreetURL =
            'http://{s}.google.com/vt/lyrs=m&x={x}&y={y}&z={z}'

          var GOOGLE_STR_MAP = L.tileLayer(googleStreetURL, {
            maxZoom: 21,
            subdomains: ['mt0', 'mt1', 'mt2', 'mt3'],
            crs: L.CRS.EPSG4326
          })

          //L.control.layers(WMS).addTo(that.$map);

          //var default_bl = baseLayers[code_provider]

          // Base Map  still Need Editing
          var basemaps = [
            L.tileLayer(googleStreetURL, {
              // attribution: '<a href="https://opentopomap.org">&copy; OpenStreetMap contributors</a>',
              maxZoom: 21,
              subdomains: ['mt0', 'mt1', 'mt2', 'mt3']
            }),
            L.tileLayer(googleSateliteURL, {
              // attribution: '<a href="https://opentopomap.org">&copy; OpenStreetMap contributors</a>',
              maxZoom: 21,
              subdomains: ['mt0', 'mt1', 'mt2', 'mt3']
            })
          ]

          var selectedBaseLayer;
          var defaultPrefix = '<a href="https://opentopomap.org">leaflet | &copy; OpenStreetMap contributors</a>';
          var selectedPrefix;
          var selectedUrl;
          switch (defaultBaseLayer) {
            case 'GOOGLE_SAT_MAP':
              selectedBaseLayer = [GOOGLE_SAT_MAP];
              selectedPrefix = defaultPrefix
              selectedUrl = googleSateliteURL
              break;
            case 'GOOGLE_STR_MAP':
              selectedBaseLayer = [GOOGLE_STR_MAP];
              selectedPrefix = defaultPrefix
              selectedUrl = googleStreetURL
              break;
            default:
              selectedBaseLayer = [GOOGLE_SAT_MAP];
              selectedPrefix = defaultPrefix + osmHotLink
              selectedUrl = osmHotURL
          }


          // init map
          V.prototype.$map = L.map('map', {
            /*  center: bounds,
            zoom: 5,
            maxBounds: bounds,
            maxBoundsViscosity: 0.75, */

            editable: true,
            zoom: 15,
            minZoom: 5,
            zoomSnap: 0.25,
            preferCanvas: true,
            layers: selectedBaseLayer,

            fullscreenControl: true,
            fullscreenControlOptions: {
              // optional
              title: 'Montre-moi le plein écran!',
              titleCancel: 'Quitter le mode plein écran',
            }
            //crs:  L.CRS.EPSG3857
          })
            .on('load', function (e) {
              e.target.attributionControl.setPrefix(selectedPrefix);
            })
            .setView([28.5, 2], 5) //places the map in Algeria.


          var fitBoundsSouthWest = new L.LatLng(18.976, -8.668)
          var fitBoundsNorthEast = new L.LatLng(37.093, 11.999)
          var fitBoundsArea = new L.LatLngBounds(
            fitBoundsSouthWest,
            fitBoundsNorthEast
          )
          var maxBoundsSouthWest = new L.LatLng(17.748687, -16.12793)
          var maxBoundsNorthEast = new L.LatLng(30.739746, 30.739746)
          var maxBoundsArea = new L.LatLngBounds(
            maxBoundsSouthWest,
            maxBoundsNorthEast
          )

         
          V.prototype.$map.on('viewreset', function (e) {
            V.prototype.$map.setMaxBounds(fitBoundsArea)
          })


          V.prototype.$map.on('baselayerchange', function (e) {
            if (e.name.includes('OSM FR')) {
              V.prototype.$map.attributionControl.setPrefix(defaultPrefix + osmFrLink);
            } else if (e.name.includes('OSM HOT')) {
              V.prototype.$map.attributionControl.setPrefix(defaultPrefix + osmHotLink);
            } else if (e.name.includes('OPEN TOPO')) {
              V.prototype.$map.attributionControl.setPrefix(OpenTopoLink);
            } else {
              V.prototype.$map.attributionControl.setPrefix(defaultPrefix);
            }
          })


          V.prototype.$map.fitBounds(fitBoundsArea, { reset: true })

       
            var WMS = {
              "<span id='osmhot' class='couches'>GOOGLE STREET</span> <br> <img src='http://mt0.google.com/vt/lyrs=m&x=16&y=12&z=5' width='100' height='100' />' ":
                basemaps[0],
              "<span id='osmhot' class='couches'>GOOGLE SAT</span> <br> <img src='http://mt3.google.com/vt/lyrs=s&x=15&y=12&z=5' width='100' height='100' />' ":
                basemaps[1]
            }
            // Add Map layer with Image Perview
            L.control.layers(WMS).addTo(that.$map)
          

          // Add User Location
          // Add ContextMenu onClick right on the Map

          L.control.locate().addTo(that.$map)
          /*   that.$map.addControl(
            L.control.basemaps({
              basemaps: basemaps,
              tileX: 0,
              tileY: 0,
              tileZ: 1
            })
          )
  
  
          // detect fullscreen toggling
          that.$map.on('enterFullscreen', function() {
            if (window.console) window.console.log('enterFullscreen')
          })
          that.$map.on('exitFullscreen', function() {
            if (window.console) window.console.log('exitFullscreen')
          })
          //Base layers definition and addition
  
      
  
          //Adding Routing
          /*     L.Routing.control({
            waypoints: [
              L.latLng(36.1398085,0.2278534),
              L.latLng(31.610351,-2.222044)
            ]
          }).addTo(that.$map); */

          // init draw layer for creating new layers
          V.prototype.$DrawLayer = L.geoJSON().addTo(that.$map)

          // search bar
          V.prototype.$searchControl = new GeoSearchControl({
            provider: new OpenStreetMapProvider(),
            style: 'button',
            showMarker: true,
            showPopup: true,
            autoClose: true,
            retainZoomLevel: false,
            animateZoom: true,
            keepResult: true,
            searchLabel: 'Chercher une adress..'
          }).addTo(that.$map)

          // init marker cluster
          V.prototype.$cluster = L.markerClusterGroup
            .layerSupport()
            .addTo(that.$map)

          //add mesure tools
          var measureControl = L.control.measure({
            position: 'topleft',
            localization: 'fr',
            primaryLengthUnit: 'meters',
            secondaryLengthUnit: 'kilometers'
          })
          measureControl.addTo(that.$map)
          //add new button
          //var helloPopup = L.popup().setContent('Hello World!');
          //

          //add print map



          L.control
            .browserPrint(
              {
                title: 'Imprimer une carte',
                closePopupsOnPrint: false,
                printModes: [
                  L.control.browserPrint.mode.landscape("TABLOID VIEW", "tabloid"),
                  L.control.browserPrint.mode.portrait('Portrait'),
                  L.control.browserPrint.mode.landscape('Paysage'),
                  L.control.browserPrint.mode.auto('Auto'),
                  L.control.browserPrint.mode.custom('Séléctionnez la zone')
                ],
                manualMode: false
              }
            )
            .addTo(that.$map)


          // init draw tool box
          V.prototype.$drawControl = new L.Control.Draw({
            edit: false,
            draw: {
              circle: true,
              circlemarker: false,
              rectangle: true
            }
          })
          // init print-map button

          // expose layer groups as a global variable so we can add layers to it from any component
          V.prototype.$layerGroups = {}
          V.prototype.$filterButton = L.control.filterButton()
          V.prototype.$globalFilterButton = L.control.globalFilterButton()
          V.prototype.$geoProcessingButton = L.control.geoProcessingButton()
          V.prototype.$getCurrentLocation = L.control.getCurrentLocation()
          V.prototype.$showLayersButton = L.control.showLayersButton()
          V.prototype.$toggleButton = L.control.toggleButton()
          V.prototype.$proximityToolButton = L.control.proximityToolButton()
        }
        function fetchBaseLayer(callback) {
          axios({
            method: 'get',
            headers: {
              ContentType: 'application/json',
              Authorization: `Bearer ${localStorage.getItem(
                'sigToken'
              )}`
            },
            url: `${backend}/settings/defaultBaseLayer`,
          }).then(({ data }) => {
            defaultBaseLayer = data;
            that.$store.commit('maps/setMapIsReady', true)
            callback()
          }).catch(() => {
            defaultBaseLayer = 'OSM_FR_MAP';
            that.$store.commit('maps/setMapIsReady', true)
            callback()
          })
        }
        fetchBaseLayer(setup)
        

      }
    }
    )
  }
}

Vue.use(leaflet)