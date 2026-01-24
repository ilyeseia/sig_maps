<template>
  <section>
    <div id="map-position"></div>
  </section>
</template>
<script>
import L from "leaflet";
import { SecuredWMSURL } from "../constants";
export default {
  layout: "shareIframePositionLayout",

  data() {
    return {};
  },

  mounted() {
    const layersObj = this.$store.state.maps.currentMap;
    let route = this.$route.query;
    let obj = JSON.parse(JSON.stringify(layersObj.layers));
    let wmsLayers = obj.map((layer) => `limite_admin:${layer.slug}`).join(",");
    let layerStyles = obj
      .map((layer) => (layer.layerType === "RASTER" ? "" : layer.slug))
      .join(",");
    const token = localStorage.getItem("sigToken");
    if (route.topo === "Point") {
      var map = L.map("map-position", {
        fullscreenControl: true,
        fullscreenControlOptions: {
          // optional
          title: "Montre-moi le plein écran!",
          titleCancel: "Quitter le mode plein écran",
        },
      });
      // Set open openstreetmap
      L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {}).addTo(map);
      // Set our initial location and zoomlevel
      var wmsLayer = L.tileLayer.wms(SecuredWMSURL, {
        layers: wmsLayers,
        format: "image/png",
        transparent: true,
        styles: layerStyles,
        token,
      });
      map.addLayer(wmsLayer);
      map.setView([route.lan, route.lang], 15);
      L.marker([route.lan, route.lang]).addTo(map);
    } else if (route.topo == "Polygon") {
      let f = route.lang.split(",");
      const rsl = f.reduce(function (result, value, index, array) {
        if (index % 2 === 0) result.push(array.slice(index - 1, index + 1));
        return result;
      }, []);
      let rslt = [];
      for (let i = 1; i < rsl.length; i++) {
        rslt.push(rsl[i]);
      }
      var map = L.map("map-position", {
        fullscreenControl: true,
        fullscreenControlOptions: {
          // optional
          title: "Montre-moi le plein écran!",
          titleCancel: "Quitter le mode plein écran",
        },
      });
      // Set open openstreetmap
      L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {}).addTo(map);
      // Set our initial location and zoomlevel
      var wmsLayer = L.tileLayer.wms(SecuredWMSURL, {
        layers: wmsLayers,
        format: "image/png",
        transparent: true,
        styles: layerStyles,
        token,
      });
      map.addLayer(wmsLayer);
      var poly = L.polygon(rslt, { color: "green" });
      map.addLayer(poly);
      map.fitBounds(poly.getBounds());
    } else if (route.topo == "LineString") {
      let x = route.lan.toString();
      const latX = x.split(",");
      let y = route.lang.toString();
      const langY = y.split(",");
      var pointA = new L.LatLng(latX[1], latX[0]);
      var pointB = new L.LatLng(langY[1], langY[0]);
      var pointList = [pointA, pointB];
      var map = L.map("map-position", {
        fullscreenControl: true,
        fullscreenControlOptions: {
          // optional
          title: "Montre-moi le plein écran!",
          titleCancel: "Quitter le mode plein écran",
        },
      });
      // Set open openstreetmap
      L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {}).addTo(map);
      // Set our initial location and zoomlevel
      var wmsLayer = L.tileLayer.wms(SecuredWMSURL, {
        layers: wmsLayers,
        format: "image/png",
        transparent: true,
        styles: layerStyles,
        token,
      });
      map.addLayer(wmsLayer);
      var polyline = L.polyline(pointList, { color: "green" });
      map.addLayer(polyline);
      map.fitBounds([
        [latX[1], latX[0]],
        [langY[1], langY[0]],
      ]);
    }
  },
};
</script>
<style lang="css">
#map-position {
  position: absolute;
  height: 20rem !important;
  width: 100%;
}
</style>
