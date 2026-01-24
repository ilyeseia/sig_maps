<template>
  <section v-if="isMobile">
    <div class="copyright">
      <h2>KharitaDZ, Powered by <a href="https://eadn.dz/" rel="noreferrer" target="_blank">EADN</a> </h2>
    </div>
  </section>
  <section v-else>

  </section>
</template>
<script>
import 'leaflet-easybutton/src/easy-button.js'
import 'leaflet-easybutton/src/easy-button.css'
import { backApi } from '~/methods/serverApi'
import { mapState, mapGetters } from 'vuex'
export default {
  data() {
    return {
      productVersion: '',
    }
  },
  computed: {
    ...mapState(['profile']),
    ...mapGetters({
      mapIsReady: 'maps/getMapIsReady',
      isMobile: 'app/getIsMobile',
    }),
  },
  watch: {
    mapIsReady(val) {
      if (val) {
        this.setup()
      }
    },
  },
  methods: {
    setup() {
      if (!this.isMobile) {
        backApi
          .get('settings/public/version?code=SIG_SYSTEM_VERSION')
          .then(({ data }) => {
            L.easyButton(
              `<p class="prodcut-version"> ${data.toLocaleUpperCase()} |  CRÉÉ PAR EADN </p>`,
              function () {
                window.open('https://www.eadn.dz/')
              },
              { position: 'bottomleft' }
            ).addTo(this.$map)
          })
          .catch((error) => {
            console.error('Error', error)
          })
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.copyright{
  position: fixed;
  bottom: 3.26rem;
  left: 50%;
  transform: translateX(-50%);
  z-index: 100;
  width: fit-content;
  background-color: rgba(255, 255, 255, 1);
  padding: .1rem .3rem;
  border-radius: 3px;
  h2{
    font-size: .8rem;
    font-weight: bold;
    a, a:visited{
      text-decoration: none;
      color: $color-primary;
    }
  }
}
</style>
