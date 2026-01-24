<template>
  <div style="height: 100%; display: flex; flex-direction: column">
    <Navbar
      @toggleSideMenu="onToggleSideMenu"
      style="position: fixed; top: 0; width: 100%"
      v-if="getVisibility"
    />
    <SideMenu @toggleSideMenu="onToggleSideMenu" v-if="isTablet || isMobile" />
    <Body
      :style="{ 'margin-top': profile.authenticated ? '5rem' : 0 }"
      :class="{ mapBody: this.$route.matched[0].path.indexOf('viewer') > -1 }"
    >
      <nuxt />
    </Body>
  </div>
</template>

<script>
import Navbar from '~/components/layout/Navbar'
import Body from '~/components/layout/Body'
import Footer from '~/components/layout/Footer'
import { mapGetters, mapState } from 'vuex'
import SideMenu from '~/components/dashboard/SideMenu'

export default {
  components: { Navbar, Body, Footer, SideMenu },
  computed: {
    ...mapState(['profile']),
    ...mapGetters({
      currentMapPrivacy: 'maps/currentMapPrivacy',
      isTablet: 'app/getIsTablet',
      isMobile: 'app/getIsMobile',
    }),
    getVisibility() {
      return (this.profile.authenticated || this.isMobile || this.isTablet)
        ? true
        : !this.$route.matched[0].path.indexOf('viewer') > 0 ||
            this.currentMapPrivacy === 'PRIVATE'
    },
  },
  methods: {
    onToggleSideMenu(resize) {
      const sideBar = document.querySelector('.side-menu-outer')
      const wrapper = document.getElementById('main-wrapper')
      if (sideBar != null) {
        this.$store.dispatch('app/toggleSideMenu', {
          sideBar,
          wrapper,
          resize,
        })
      }
    },
    updateDeviceType() {
      this.$store.dispatch('app/setDeviceType', window.innerWidth)
    },
  },
  mounted() {
    this.$nextTick(() => {
      window.addEventListener('resize', this.updateDeviceType)
      this.updateDeviceType()
      this.$nuxt.$loading.start()
    })
  },
  destroyed() {
    window.removeEventListener('resize', this.updateDeviceType)
  },
}
</script>

<style>
html,
body,
#__nuxt,
#__layout {
  height: 100%;
  scroll-behavior: smooth;
}
.body {
  margin-top: 75px;
}
.mapBody {
  overflow: hidden;
  z-index: 0 !important;
}
</style>
