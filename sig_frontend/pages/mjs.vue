<template>
  <section>
    <div id="mjs-map"></div>
  </section>
</template>
<script>
import L from "leaflet";

import "../node_modules/leaflet-extra-markers/src/assets/js/leaflet.extra-markers";
import "../node_modules/leaflet-extra-markers/dist/css/leaflet.extra-markers.min.css";
export default {
  layout: "shareIframemjsLayout",

  data() {
    return {};
  },

  mounted() {
    var map = L.map("mjs-map", {
      fullscreenControl: true,
      fullscreenControlOptions: {
        // optional
        title: "Montre-moi le plein écran!",
        titleCancel: "Quitter le mode plein écran",
      },
    }).setView([36.7604, 3.0545], 15);
    // Set open openstreetmap
    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      attributionControl: false,
    }).addTo(map);
    // Set our initial location and zoomlevel
    var redMarker = L.ExtraMarkers.icon({
      icon: "fa-building",
      markerColor: "blue",
      shape: "circle",
      prefix: "fa",
    });

    L.marker([36.7604, 3.0545], { icon: redMarker })
      .bindPopup("Ministère de la Jeunesse et des Sports")
      .openPopup()
      .addTo(map);
  },
};
</script>
<style lang="css">
#mjs-map {
  position: absolute;
  height: 20rem !important;
  width: 100%;
}
</style>
