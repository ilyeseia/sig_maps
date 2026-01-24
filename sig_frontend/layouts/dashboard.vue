<template>
  <div style="height: 100%; display: flex; flex-direction: column">
    <Navbar
      @toggleSideMenu="onToggleSideMenu"
      style="position: fixed; top: 0; width: 100%"
    />
    <Body style="flex: 1" class="d-flex dashboard-body">
      <SideMenu @toggleSideMenu="onToggleSideMenu" />
      <div id="main-wrapper" class="dashboard-wrapper">
        <nuxt />
      </div>
    </Body>
    <!--  Need Editing  <Footer /> -->
  </div>
</template>

<script>
import Navbar from '~/components/layout/Navbar'
import Body from '~/components/layout/Body'
import Footer from '~/components/layout/Footer'
import SideMenu from '~/components/dashboard/SideMenu'

export default {
  components: { Navbar, Body, Footer, SideMenu },
  watch: {
    $route(value) {
      this.$nextTick(() => {
        this.$nuxt.$loading.start()
      })
    },
  },
  methods: {
    onToggleSideMenu(resize) {
      const sideBar = document.querySelector('.side-menu-outer')
      const wrapper = document.getElementById('main-wrapper')
      this.$store.dispatch('app/toggleSideMenu', {
        sideBar,
        wrapper,
        resize
      })
    },
    updateDeviceType() {
      this.$store.dispatch('app/setDeviceType', window.innerWidth)
    },
  },
  mounted() {
    this.$nextTick(() => {
      this.$nuxt.$loading.start()
    })
    window.addEventListener('resize', this.updateDeviceType)
    window.addEventListener('resize', this.onToggleSideMenu)
    this.updateDeviceType()
    this.onToggleSideMenu(true)
  },
  destroyed() {
    window.removeEventListener('resize', this.updateDeviceType)
    window.removeEventListener('resize', this.onToggleSideMenu)
  },
}
</script>

<style lang="scss">
html,
body,
#__nuxt,
#__layout {
  height: 100%;
  scroll-behavior: smooth;
}
.dashboard-body {
  margin-top: 0 !important;
}
.dashboard-wrapper {
  transition: all 0.3s ease-in;
  flex-grow: 1;
  overflow-x: hidden;
  margin-left: 256px;
  margin-top: 78px;
  @include hideScroll;
}
</style>
