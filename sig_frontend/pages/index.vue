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
    <section id="about" class="about-section">
      <div class="container">
        <div class="about-header">
          <span class="section-badge">
            <i class="fas fa-info-circle"></i>
            À Propos
          </span>
          <h2 class="section-title">LE PROJET KHARITADZ</h2>
          <p class="section-desc">
            Le Système KharitaDZ Réalisé Par 
            <a href="https://www.eadn.dz/" target="_blank">EADN</a>
            est un Géoportail Flexible et adaptable selon le domaine d'activité.
            KharitaDZ offre la possibilité de collecte, la gestion et l'analyse
            de données de tous les établissements de jeunesses et du sport à travers le territoire national.
          </p>
        </div>
        
        <div class="features-grid">
          <div class="feature-card" v-for="(feature, index) in features" :key="index">
            <div class="feature-icon" :class="`color-${feature.color}`">
              <i :class="feature.icon"></i>
            </div>
            <h3 class="feature-title">{{ feature.title }}</h3>
            <p class="feature-desc">{{ feature.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Statistics -->
    <StatsSection :stats="stats" />

    <!-- Contact Section -->
    <section id="contact" class="contact-section">
      <div class="container">
        <div class="contact-header">
          <span class="section-badge light">
            <i class="fas fa-envelope"></i>
            Restons en contact
          </span>
          <h2 class="section-title">Contactez-nous</h2>
        </div>
        
        <div class="contact-grid">
          <div class="contact-card" v-for="(contact, index) in contacts" :key="index">
            <div class="contact-icon">
              <i :class="contact.icon"></i>
            </div>
            <div class="contact-info">
              <h4 class="contact-label">{{ contact.label }}</h4>
              <p class="contact-value">{{ contact.value }}</p>
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
      },
      features: [
        {
          icon: 'fas fa-running',
          title: 'Sports',
          description: 'Infrastructures sportives et complexes à travers le territoire national.',
          color: 'orange'
        },
        {
          icon: 'fas fa-users',
          title: 'Jeunesse',
          description: 'Maisons de jeunes, centres culturels et espaces de loisirs.',
          color: 'cyan'
        },
        {
          icon: 'fas fa-map-marked-alt',
          title: 'Cartographie',
          description: 'Géolocalisation précise et fonds de carte multiples.',
          color: 'purple'
        }
      ],
      contacts: [
        {
          icon: 'fas fa-phone-alt',
          label: 'Téléphone',
          value: '023 51 24 24'
        },
        {
          icon: 'fas fa-map-marker-alt',
          label: 'Adresse',
          value: "003 Rue Mohamed Belouizdad, Sidi M'Hamed 16000"
        },
        {
          icon: 'fas fa-globe',
          label: 'Ministère',
          value: 'Ministère de la jeunesse et des sports'
        }
      ]
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
// Design system variables
$primary: #0f172a;
$primary-light: #1e293b;
$accent: #f97316;
$accent-secondary: #06b6d4;
$ease-premium: cubic-bezier(0.16, 1, 0.3, 1);

#landing-wrapper {
  background-color: #f8fafc;
  font-family: 'DM Sans', 'Poppins', sans-serif;
  overflow-x: hidden;
}

// Section Badge
.section-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 0.5rem 1rem;
  background: rgba(249, 115, 22, 0.1);
  border: 1px solid rgba(249, 115, 22, 0.2);
  border-radius: 50px;
  font-size: 0.8rem;
  font-weight: 600;
  color: $accent;
  margin-bottom: 1rem;
  
  &.light {
    background: rgba(255, 255, 255, 0.1);
    border-color: rgba(255, 255, 255, 0.2);
    color: white;
  }
  
  i { font-size: 0.9rem; }
}

.section-title {
  font-family: 'Outfit', 'Righteous', sans-serif;
  color: $primary;
  font-size: 2.5rem;
  font-weight: 800;
  margin-bottom: 1rem;
  
  @media (max-width: 768px) {
    font-size: 2rem;
  }
}

// About Section
.about-section {
  padding: 6rem 0;
  background: white;
}

.about-header {
  text-align: center;
  max-width: 700px;
  margin: 0 auto 4rem;
  
  .section-desc {
    color: #64748b;
    line-height: 1.8;
    font-size: 1.1rem;
    
    a {
      color: $accent;
      font-weight: 600;
      text-decoration: none;
      transition: color 0.3s;
      
      &:hover {
        color: darken($accent, 10%);
      }
    }
  }
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
}

.feature-card {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  padding: 2rem;
  text-align: center;
  transition: all 0.3s $ease-premium;
  
  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.08);
    border-color: transparent;
    
    .feature-icon {
      transform: scale(1.1);
    }
  }
}

.feature-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 1.5rem;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.3s $ease-premium;
  
  i { font-size: 1.8rem; }
  
  &.color-orange {
    background: rgba(249, 115, 22, 0.1);
    color: $accent;
  }
  
  &.color-cyan {
    background: rgba(6, 182, 212, 0.1);
    color: $accent-secondary;
  }
  
  &.color-purple {
    background: rgba(139, 92, 246, 0.1);
    color: #8b5cf6;
  }
}

.feature-title {
  font-size: 1.2rem;
  font-weight: 700;
  color: $primary;
  margin-bottom: 0.5rem;
}

.feature-desc {
  color: #64748b;
  font-size: 0.9rem;
  line-height: 1.6;
}

// Contact Section
.contact-section {
  padding: 6rem 0;
  background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-image: url('~assets/background.png');
    background-size: cover;
    background-position: center;
    opacity: 0.05;
    pointer-events: none;
  }
}

.contact-header {
  text-align: center;
  margin-bottom: 3rem;
  position: relative;
  z-index: 1;
  
  .section-title {
    color: white;
    background: linear-gradient(135deg, #ffffff 0%, #94a3b8 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }
}

.contact-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
  position: relative;
  z-index: 1;
}

.contact-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  transition: all 0.3s $ease-premium;
  
  &:hover {
    background: rgba(255, 255, 255, 0.1);
    transform: translateY(-4px);
  }
}

.contact-icon {
  width: 50px;
  height: 50px;
  background: rgba(249, 115, 22, 0.15);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: $accent;
  flex-shrink: 0;
  
  i { font-size: 1.2rem; }
}

.contact-info {
  .contact-label {
    font-size: 0.8rem;
    color: rgba(255, 255, 255, 0.5);
    text-transform: uppercase;
    letter-spacing: 0.05em;
    margin-bottom: 0.25rem;
  }
  
  .contact-value {
    color: white;
    font-size: 0.95rem;
    margin: 0;
  }
}
</style>
