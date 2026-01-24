<template>
  <section>
    <div id="themap"></div>
  </section>
</template>
<script>
import L from 'leaflet'
import { SecuredPublicWMSURL, SecuredWMSURL } from '../../constants'
import axios from 'axios'
import { backend } from '../../constants'
export default {
  layout: 'sharedMapLayout',
  data() {
    return {}
  },
  beforeMount() {
    const that = this
    function setup(baseLayerName) {
      const mapId = that.$route.params.map
      const mapControlled = that.$route.query.control
      const mapPublic = that.$route.query.public
      const url =
        mapControlled === 'true' || mapPublic === 'true'
          ? `${backend}/maps/public/${mapId}/layers-styles`
          : `${backend}/maps/${mapId}/layers-styles`
      let axiosConfig = {
        method: 'get',
        headers: {
          ContentType: 'application/json',
        },
        url: url,
      }
      if (mapControlled !== 'true') {
        axiosConfig.headers['Authorization'] = `Bearer ${localStorage.getItem(
          'sigToken'
        )}`
      }
      axios(axiosConfig)
        .then(({ data }) => {
          let wmsLayers = data
            .filter((d) => d.isVisible === "true")
            .sort((a, b) => a.order - b.order)
            .map((e) => `limite_admin:${e.layer}`)
            .join(',')
          let layerStyles = data
            .filter((d) => d.isVisible === "true")
            .sort((a, b) => a.order - b.order)
            .map((e) => e.style)
            .join(',')

          //OSM HOT tiles attribution and URL
          var osmHotLink =
            '<a href="http://openstreetmap.org">&copy; OpenStreetMap contributors| &copy; OpenStreetMap France</a>'
          var osmHotURL =
            'https://{s}.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png'
          var osmHotAttrib = osmHotLink

          var OSM_HOT_MAP = L.tileLayer(osmHotURL, {
            attribution: osmHotAttrib,
          })

          //OSM FRANCE tiles attribution and URL
          var osmFrLink =
            '<a href="http://openstreetmap.org">&copy; OpenStreetMap contributors| &copy; OpenStreetMap France</a>'
          var osmFrURL =
            'https://{s}.tile.openstreetmap.fr/osmfr/{z}/{x}/{y}.png'
          var osmFrAttrib = osmFrLink

          var OSM_FR_MAP = L.tileLayer(osmFrURL, {
            attribution: osmFrAttrib,
          })

          //OSM OpenTopoMap tiles attribution and URL
          var OpenTopoLink =
            '<a href="https://opentopomap.org">OpenStreetMap</a>'
          var OpenTopoURL = 'https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png'
          var OpenTopoAttrib = OpenTopoLink

          var OPEN_TOPO_MAP = L.tileLayer(OpenTopoURL, {
            attribution: OpenTopoAttrib,
          })

          //GOOGLE SATELITE tiles attribution and URL
          var googleSateliteURL =
            'http://{s}.google.com/vt/lyrs=s&x={x}&y={y}&z={z}'

          var GOOGLE_SAT_MAP = L.tileLayer(googleSateliteURL, {
            maxZoom: 21,
            subdomains: ['mt0', 'mt1', 'mt2', 'mt3'],
          })

          //GOOGLE STREET tiles attribution and URL
          var googleStreetURL =
            'http://{s}.google.com/vt/lyrs=m&x={x}&y={y}&z={z}'

          var GOOGLE_STR_MAP = L.tileLayer(googleStreetURL, {
            maxZoom: 21,
            subdomains: ['mt0', 'mt1', 'mt2', 'mt3'],
            crs: L.CRS.EPSG4326,
          })

          var selectedBaseLayer
          switch (baseLayerName) {
            case 'OSM_HOT_MAP':
              selectedBaseLayer = [OSM_HOT_MAP]
              break
            case 'OSM_FR_MAP':
              selectedBaseLayer = [OSM_FR_MAP]
              break
            case 'OPEN_TOPO_MAP':
              selectedBaseLayer = [OPEN_TOPO_MAP]
              break
            case 'GOOGLE_SAT_MAP':
              selectedBaseLayer = [GOOGLE_SAT_MAP]
              break
            case 'GOOGLE_STR_MAP':
              selectedBaseLayer = [GOOGLE_STR_MAP]
              break
            default:
              selectedBaseLayer = [GOOGLE_SAT_MAP]
          }

          var map = L.map('themap', {
            fullscreenControl: mapControlled === 'true',
            zoomControl: mapControlled === 'true',
            minZoom: 4,
            maxZoom: 4,
            zoomSnap: 0.25,
            layers: selectedBaseLayer,
          })

          let config = {
            layers: wmsLayers,
            format: 'image/png',
            transparent: true,
            styles: layerStyles,
          }
          if (mapControlled !== 'true') {
            config.token = localStorage.getItem('sigToken')
          }
          // Set our initial location and zoomlevel
          var wmsLayer = L.tileLayer.wms(
            mapControlled === 'true' || mapPublic
              ? SecuredPublicWMSURL
              : SecuredWMSURL,
            config
          )
          map.addLayer(wmsLayer)
          map.setView([25, 2.5], 4)
        })
        .catch(() => { })
    }

    function fetchBaseLayer(callback) {
      let axiosConfig = {
        method: 'get',
        headers: {
          ContentType: 'application/json',
        },
        url: `${backend}/settings/defaultBaseLayer`,
      }
      if (that.$route.query.control !== 'true') {
        axiosConfig.headers['Authorization'] = `Bearer ${localStorage.getItem(
          'sigToken'
        )}`
      }
      axios(axiosConfig)
        .then(({ data }) => {
          callback(data)
        })
        .catch(() => {
          callback('GOOGLE_SAT_MAP')
        })
    }
    fetchBaseLayer(setup)
  },
}
</script>
<style lang="css">
#themap {
  position: absolute;
  height: 30rem !important;
  width: 100%;
}
</style>
