<template>
  <section>
    <div>
      <button class="btn-current-pos" @click="getCurrentPosition">
        <i class="fa fa-location-arrow" aria-hidden="true" style="color: $color-primary"></i>
      </button>
    </div>
  </section>
</template>
<script>
export default {
  data: {},
  methods: {
    getCurrentPosition(e) {
      if ("geolocation" in navigator) {
        navigator.geolocation.getCurrentPosition((position) => {
          var marker = L.marker([
            position.coords.latitude,
            position.coords.longitude,
          ]).addTo(this.$map);
          this.$map.setView([position.coords.latitude, position.coords.longitude], 13);
          marker
            .bindPopup(
              `<b>
              Votre emplacement est</b><br>
              latitude : ${position.coords.latitude} et longitude :${position.coords.longitude} `
            )
            .openPopup();
        });
      } else {
        alert("Impossible de récupérer votre position");
        throw new Error("Impossible de récupérer votre position");
      }
    },
  },
  mounted() {
    /*   if (this.profile.authenticated == false) {

    } */
  },
};
</script>

<style scoped>
/* Start Current Position Button  */

.btn-current-pos {
  position: absolute !important;
  /* top: 302px; */
  top: 350px;
  right: 10px;
  z-index: 500 !important;
  outline: none;
  width: 42px;
  line-height: 10px;
  height: 40px;
  padding: 10px 5px;
  border: 2px solid $color-primary;
  border-radius: 3px;
  background: white;
}
/* End Current Position Button  */
</style>
