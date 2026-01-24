<template>
  <div class="dashboard-page w-100">
    <div v-if="profile.roles.includes('ROLE_ADMIN')" class="p-4">
      <header class="dashboard-header mb-4">
        <h1 class="h3 font-weight-bold text-dark">Vue d'ensemble</h1>
        <p class="text-muted">Bienvenue sur votre tableau de bord SIG.</p>
      </header>
      
      <card class="mb-5"></card>
      
      <div class="stats-grid row mt-4">
        <div class="col-xl-4 col-lg-6 mb-4">
          <map-stat></map-stat>
        </div>
        <div class="col-xl-4 col-lg-6 mb-4">
          <layer-stat></layer-stat>
        </div>
        <div class="col-xl-4 col-lg-12 mb-4">
          <user-stat></user-stat>
        </div>
      </div>
    </div>
    <div v-else style="height: calc(100vh - 80px)" class="container">
      <div class="row">
        <div class="col">
          <div class="img-wrapper">
            <img src="../../assets/setting.svg" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
const Card = () => import('../../components/dashboard/stats/Card.vue')
const MapStat = () => import('../../components/dashboard/stats/MapStat.vue')
const LayerStat = () => import('../../components/dashboard/stats/LayerStat.vue')
const UserStat = () => import('../../components/dashboard/stats/UserStat.vue')

import { mapState } from 'vuex'
import pageTitle from '~/mixins/page-title'

export default {
  layout: 'dashboard',
  components: {
    Card,
    MapStat,
    LayerStat,
    UserStat,
  },
  mixins: [pageTitle],
  validate({ params, query, store }) {
    return store.state.profile.authenticated
  },
  data() {
    return {
      page: {
        title: 'Dashboard',
        hid: '',
        name: '',
        description: '',
      },
    }
  },
  computed: {
    ...mapState(['profile']),
  },
  mounted() {
    this.$nextTick(() => {
      this.$nuxt.$loading.start()
      setTimeout(() => this.$nuxt.$loading.finish(), 900)
    })
    let body = document.getElementsByTagName('body')[0]
    body.style.backgroundColor = '#fafafa'
    body.style.height = 'auto'
  },
  destroyed() {
    let body = document.getElementsByTagName('body')[0]
    document.getElementsByTagName('body')[0].style.height = '100%'
    body.style.backgroundColor = '#fff'
  },
}
</script>

<style scoped lang="scss">
.img-wrapper {
  margin-top: 100px;
  margin-left: 150px;
  align-items: center;
  display: flex;
  justify-content: center;
}
</style>
