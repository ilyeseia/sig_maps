<template>
  <section class="stats-section" ref="statsSection">
    <!-- Background decoration -->
    <div class="bg-decoration">
      <div class="gradient-orb orb-1"></div>
      <div class="gradient-orb orb-2"></div>
    </div>
    
    <div class="container">
      <div class="section-header text-center">
        <span class="section-badge">
          <i class="fas fa-chart-bar"></i>
          En Chiffres
        </span>
        <h2 class="section-title">STATISTIQUES</h2>
        <p class="section-subtitle">Données clés du secteur Jeunesse & Sports</p>
      </div>
      
      <div class="stats-grid" v-if="stats && stats.length">
        <div 
          class="stat-card" 
          v-for="(stat, index) in stats" 
          :key="stat.id"
          :class="{ 'is-visible': isVisible }"
          :style="{ '--delay': `${index * 0.15}s` }"
        >
          <div class="card-glow" :class="getColorClass(stat.code)"></div>
          <div class="icon-wrapper" :class="getColorClass(stat.code)">
            <i :class="getIconName(stat.code)"></i>
          </div>
          <div class="stat-content">
            <h3 class="stat-title">{{ formatTitle(stat.code) }}</h3>
            <p class="stat-desc">
              {{ getDescription(stat.code) }}
            </p>
          </div>
          <div class="stat-value" :class="getColorClass(stat.code)">
            <span class="count" ref="countRef">{{ isVisible ? stat.value : 0 }}</span>
            <span class="label">établissements</span>
          </div>
        </div>
      </div>
      
      <!-- Empty state -->
      <div v-else class="empty-state">
        <i class="fas fa-spinner fa-spin"></i>
        <p>Chargement des statistiques...</p>
      </div>
    </div>
  </section>
</template>

<script>
export default {
  name: 'StatsSection',
  props: {
    stats: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      isVisible: false,
      observer: null
    };
  },
  mounted() {
    this.setupIntersectionObserver();
  },
  beforeDestroy() {
    if (this.observer) {
      this.observer.disconnect();
    }
  },
  methods: {
    setupIntersectionObserver() {
      this.observer = new IntersectionObserver(
        (entries) => {
          entries.forEach((entry) => {
            if (entry.isIntersecting) {
              this.isVisible = true;
            }
          });
        },
        { threshold: 0.2 }
      );
      
      if (this.$refs.statsSection) {
        this.observer.observe(this.$refs.statsSection);
      }
    },
    formatTitle(code) {
      if (!code) return '';
      return code.replaceAll('_', ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase());
    },
    getColorClass(code) {
      if (code === 'ETABLISSEMENTS_DE_SPORTS') return 'color-orange';
      if (code === 'ETABLISSEMENTS_DE_JEUNESSES') return 'color-cyan';
      return 'color-accent';
    },
    getIconName(code) {
      if (code === 'ETABLISSEMENTS_DE_SPORTS') return 'fas fa-running';
      if (code === 'ETABLISSEMENTS_DE_JEUNESSES') return 'fas fa-users';
      return 'fas fa-building';
    },
    getDescription(code) {
      if (code === 'ETABLISSEMENTS_DE_SPORTS') {
        return 'Infrastructures sportives réparties sur le territoire national.';
      }
      if (code === 'ETABLISSEMENTS_DE_JEUNESSES') {
        return 'Centres et maisons de jeunes à travers le pays.';
      }
      return 'Structures et établissements du secteur.';
    }
  }
};
</script>

<style lang="scss" scoped>
// Design system variables
$primary: #0f172a;
$primary-light: #1e293b;
$accent: #f97316;
$accent-secondary: #06b6d4;
$ease-premium: cubic-bezier(0.16, 1, 0.3, 1);

.stats-section {
  padding: 6rem 0;
  background: linear-gradient(180deg, $primary 0%, $primary-light 100%);
  color: white;
  position: relative;
  overflow: hidden;
}

// Background decoration
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  overflow: hidden;
  
  .gradient-orb {
    position: absolute;
    border-radius: 50%;
    filter: blur(80px);
    opacity: 0.15;
    
    &.orb-1 {
      width: 500px;
      height: 500px;
      background: $accent;
      top: -200px;
      right: -100px;
    }
    
    &.orb-2 {
      width: 400px;
      height: 400px;
      background: $accent-secondary;
      bottom: -150px;
      left: -100px;
    }
  }
}

.section-header {
  margin-bottom: 4rem;
  position: relative;
  z-index: 1;
  
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
    
    i { font-size: 0.9rem; }
  }
}

.section-title {
  font-family: 'Outfit', 'Righteous', sans-serif;
  font-size: 2.5rem;
  font-weight: 800;
  margin-bottom: 0.5rem;
  background: linear-gradient(135deg, #ffffff 0%, #94a3b8 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  
  @media (max-width: 768px) {
    font-size: 2rem;
  }
}

.section-subtitle {
  color: rgba(255, 255, 255, 0.6);
  font-size: 1rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  position: relative;
  z-index: 1;
}

.stat-card {
  position: relative;
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  padding: 2rem;
  border-radius: 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  transition: all 0.4s $ease-premium;
  overflow: hidden;
  
  // Animation on scroll
  opacity: 0;
  transform: translateY(40px);
  
  &.is-visible {
    animation: fadeUp 0.6s $ease-premium forwards;
    animation-delay: var(--delay);
  }

  &:hover {
    transform: translateY(-8px);
    background: rgba(255, 255, 255, 0.06);
    border-color: rgba(255, 255, 255, 0.15);
    
    .card-glow {
      opacity: 0.15;
    }
    
    .icon-wrapper {
      transform: scale(1.1);
    }
  }
  
  .card-glow {
    position: absolute;
    top: -50%;
    left: 50%;
    transform: translateX(-50%);
    width: 200px;
    height: 200px;
    border-radius: 50%;
    filter: blur(60px);
    opacity: 0;
    transition: opacity 0.4s ease;
    pointer-events: none;
    
    &.color-orange { background: $accent; }
    &.color-cyan { background: $accent-secondary; }
    &.color-accent { background: $accent; }
  }
}

@keyframes fadeUp {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.icon-wrapper {
  width: 80px;
  height: 80px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 1.5rem;
  transition: transform 0.3s $ease-premium;
  
  i {
    font-size: 2rem;
  }
  
  &.color-orange {
    background: rgba(249, 115, 22, 0.15);
    color: $accent;
  }
  
  &.color-cyan {
    background: rgba(6, 182, 212, 0.15);
    color: $accent-secondary;
  }
  
  &.color-accent {
    background: rgba(249, 115, 22, 0.15);
    color: $accent;
  }
}

.stat-content {
  flex: 1;
  margin-bottom: 1.5rem;
}

.stat-title {
  font-size: 1rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
  text-transform: capitalize;
  color: white;
}

.stat-desc {
  font-size: 0.85rem;
  opacity: 0.6;
  line-height: 1.5;
  max-width: 250px;
}

.stat-value {
  display: flex;
  flex-direction: column;
  align-items: center;
  
  .count {
    font-size: 3rem;
    font-weight: 800;
    font-family: 'Outfit', 'Righteous', sans-serif;
    line-height: 1;
    
    .color-orange & { color: $accent; }
    .color-cyan & { color: $accent-secondary; }
    .color-accent & { color: $accent; }
  }
  
  .label {
    font-size: 0.75rem;
    text-transform: uppercase;
    letter-spacing: 0.1em;
    opacity: 0.5;
    margin-top: 0.25rem;
  }
  
  &.color-orange .count { color: $accent; }
  &.color-cyan .count { color: $accent-secondary; }
  &.color-accent .count { color: $accent; }
}

.empty-state {
  text-align: center;
  padding: 4rem;
  opacity: 0.6;
  
  i {
    font-size: 2rem;
    margin-bottom: 1rem;
    color: $accent;
  }
}
</style>
