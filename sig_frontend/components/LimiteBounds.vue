<template>
  <section></section>
</template>

<script>
import "leaflet-easybutton/src/easy-button.js";
import "leaflet-easybutton/src/easy-button.css";
export default {
  data() {
    return {
      northEast: {
        lat: 28.5,
        lng: 2,
      },
      southWest: {
        lat: 28.5,
        lng: 2,
      },

      fitBoundsArea: null,

      maxBoundsArea: null,
    };
  },
  methods: {
    onViewReset(e) {
      this.$map.setMaxBounds(this.maxBoundsArea);
      L.rectangle(this.maxBoundsArea, { color: "#00FF00", weight: 1 }).addTo(this.$map);
    },
  },
  beforeMount() {},
  mounted() {
    /*    var fitBoundsSouthWest = new L.LatLng(28.5, 2);
    var fitBoundsNorthEast = new L.LatLng(28.5, 2);
    this.fitBoundsArea = new L.LatLngBounds(fitBoundsSouthWest, fitBoundsNorthEast);
    var maxBoundsSouthWest = new L.LatLng(28.5, 2);
    var maxBoundsNorthEast = new L.LatLng(28.5, 2);
    this.maxBoundsArea = new L.LatLngBounds(maxBoundsSouthWest, maxBoundsNorthEast);
    this.$map.fitBounds(this.fitBoundsArea, { reset: true });
    //this.$map.setMaxBounds(this.$map.getCenter());

    this.$map.off("viewreset", this.onViewReset); */
  },
};
</script>

<style scoped></style>
