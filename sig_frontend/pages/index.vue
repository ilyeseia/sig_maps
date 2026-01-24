<template>
  <div id="landing-wrapper">
    <!-- Header -->
    <HeaderSection />

    <!-- Hero -->
    <HeroSection @scroll-down="scrollToMaps" />

    <!-- Maps Grid -->
    <div id="maps-section">
      <MapsSection 
        :maps="maps.maps" 
        :is-found="isFound" 
        :root-url="rootUrl"
        @go-to-map="goToMap"
      />
    </div>

    <!-- About Project (KharitaDZ) -->
    <section class="about-section text-center py-5">
      <div class="container">
        <h2 class="section-title">LE PROJET KHARITADZ</h2>
        <div class="title-underline mx-auto"></div>
        <p class="section-desc mt-4">
          Le Système KharitaDZ Réalisé Par <a href="https://www.eadn.dz/" target="_blank">EADN</a>
          est un Géoportail Flexible et adaptable selon le domaine d’activité.
          KharitaDZ offre la possibilité de collecte, la gestion et l'analyse
          de données de tous les établissements de jeunesses et du sport à travers le territoire national.
        </p>
        
        <div class="row mt-5 justify-content-center">
           <div class="col-md-4 mb-4">
             <div class="feature-icon"><i class="fas fa-running fa-2x"></i></div>
           </div>
           <div class="col-md-4 mb-4">
             <div class="feature-icon"><i class="fas fa-school fa-2x"></i></div>
           </div>
           <div class="col-md-4 mb-4">
             <div class="feature-icon"><i class="fas fa-swimmer fa-2x"></i></div>
           </div>
        </div>
      </div>
    </section>

    <!-- Statistics -->
    <StatsSection :stats="stats" />

    <!-- Contact Section -->
    <section class="contact-section py-5">
      <div class="container">
        <h2 class="section-title text-center">Contactez-nous</h2>
        <div class="row mt-5 text-center">
          <div class="col-md-4 mb-4">
            <div class="contact-box glass-panel p-4">
              <i class="fas fa-phone fa-2x mb-3 text-white"></i>
              <p class="text-white">023 51 24 24</p>
            </div>
          </div>
          <div class="col-md-4 mb-4">
             <div class="contact-box glass-panel p-4">
              <i class="fas fa-home fa-2x mb-3 text-white"></i>
              <p class="text-white">003 Rue Mohamed Belouizdad, Sidi M'Hamed 16000</p>
            </div>
          </div>
          <div class="col-md-4 mb-4">
             <div class="contact-box glass-panel p-4">
              <i class="fas fa-globe fa-2x mb-3 text-white"></i>
              <p class="text-white">Ministère de la jeunesse et des sports</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <FooterSection />
  </div>
</template>

<script>
import { mapState } from 'vuex'
import RestApi from '../methods/api.js'
import { backApi } from '~/methods/serverApi'
import pageTitle from '~/mixins/page-title'
import { frontend } from '../constants'

import HeaderSection from '@/components/landing/HeaderSection.vue'
import HeroSection from '@/components/landing/HeroSection.vue'
import MapsSection from '@/components/landing/MapsSection.vue'
import StatsSection from '@/components/landing/StatsSection.vue'
import FooterSection from '@/components/landing/FooterSection.vue'

export default {
  layout: 'portailMjs',
  mixins: [pageTitle],
  components: {
    HeaderSection,
    HeroSection,
    MapsSection,
    StatsSection,
    FooterSection
  },
  data() {
    return {
      isFound: false,
      stats: null,
      rootUrl: frontend,
      isLoading: false,
      currentPage: 1,
      perPage: 3,
      page: {
        title: 'Accueil',
        name: 'Accueil',
        description: 'Géoportail MJS'
      }
    }
  },
  computed: {
    ...mapState(['maps', 'profile']),
  },
  methods: {
    scrollToMaps() {
      const el = document.getElementById('maps-section');
      if (el) el.scrollIntoView({ behavior: 'smooth' });
    },
    loadAsyncData() {
      let page = this.currentPage - 1
      let limit = this.perPage
      // lead public map
      RestApi.getAllPublicMaps(page, limit).then(
        ({ content, totalElements }) => {
          this.$store.commit('maps/set', { content, totalElements })
          if (totalElements > 0) {
            this.isFound = true
          }
        }
      )
      backApi.get('settings/public/statistics').then(({ data }) => {
        this.stats = data
      })
    },
    goToMap(map) {
      if (!this.isLoading) {
        this.isLoading = true
        let url = `maps/public/layersWithFields/${map.id}`
        if (this.profile.authenticated) {
          url = `maps/layersWithFieldsAndResources/${map.id}`
        }
        backApi.get(url).then(({ data }) => {
          map.layers = data
          this.$store.commit('maps/setCurrentMap', {
            map,
            mode: 'map',
          })
          this.isLoading = false
          this.$router.push('/dashboard/viewer')
        }).catch(() => (this.isLoading = false))
      }
    }
  },
  created() {
    this.loadAsyncData()
  }
}
</script>

<style lang="scss" scoped>
// Shared Variables (ideally in assets/sass/variables.scss)
$primary-color: #131c50;
$accent-color: #e74c3c;

#landing-wrapper {
  background-color: #f4f6f9;
  font-family: 'Poppins', sans-serif;
}

.section-title {
  font-family: 'Righteous', sans-serif;
  color: $primary-color;
  font-size: 2.2rem;
  text-transform: uppercase;
}

.title-underline {
  width: 60px;
  height: 4px;
  background: $accent-color;
  border-radius: 2px;
  margin-top: 1rem;
}

.about-section {
  background: white;
  
  .section-desc {
    color: #666;
    max-width: 800px;
    margin: 0 auto;
    line-height: 1.8;
    font-size: 1.1rem;
    
    a {
      color: $accent-color;
      font-weight: 600;
      text-decoration: none;
    }
  }

  .feature-icon {
    width: 100px;
    height: 100px;
    margin: 0 auto;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 2px solid $primary-color;
    color: $primary-color;
    transition: all 0.3s;
    
    &:hover {
      background: $primary-color;
      color: white;
      transform: scale(1.1);
    }
  }
}

.contact-section {
  background: url('~assets/background.png') no-repeat center center;
  background-size: cover;
  background-attachment: fixed;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(19, 28, 80, 0.85);
  }
  
  .container {
    position: relative;
    z-index: 1;
  }
  
  .section-title {
    color: white;
  }
  
  .contact-box {
    height: 100%;
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
    border-radius: 12px;
    backdrop-filter: blur(5px);
    transition: transform 0.3s;
    
    &:hover {
      transform: translateY(-5px);
      background: rgba(255, 255, 255, 0.15);
    }
  }
}
</style>
