<template>
  <section class="stats-section">
    <div class="container">
      <div class="section-header text-center mb-5">
        <h2 class="section-title text-white">STATISTIQUES</h2>
        <div class="title-underline mx-auto bg-white"></div>
      </div>
      
      <div class="row" v-if="stats && stats.length">
        <div class="col-lg-4 col-md-6 mb-4" v-for="stat in stats" :key="stat.id">
          <div class="stat-card glass-panel">
            <div class="icon-wrapper" :class="getIconClass(stat.code)">
              <i v-if="stat.code === 'ETABLISSEMENTS_DE_SPORTS'" class="far fa-futbol fa-2x"></i>
              <i v-else-if="stat.code === 'ETABLISSEMENTS_DE_JEUNESSES'" class="fas fa-hotel fa-2x"></i>
              <i v-else class="fas fa-project-diagram fa-2x"></i>
            </div>
            <h3 class="stat-title">{{ formatTitle(stat.code) }}</h3>
            <p class="stat-desc">
              Le MJS dispose d’un nombre important d’établissements de sport
              éparpillé sur le territoire national.
            </p>
            <div class="stat-value">
              <span class="count">{{ stat.value }}</span>
            </div>
          </div>
        </div>
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
  methods: {
    formatTitle(code) {
      if (!code) return '';
      return code.replaceAll('_', ' ');
    },
    getIconClass(code) {
      if (code === 'ETABLISSEMENTS_DE_SPORTS') return 'orange';
      if (code === 'ETABLISSEMENTS_DE_JEUNESSES') return 'green';
      return 'red';
    }
  }
};
</script>

<style lang="scss" scoped>
.stats-section {
  padding: 5rem 0;
  background: #131c50; // Primary color
  color: white;
  position: relative;
  overflow: hidden;

  // Background pattern/graphic could be added here
}

.section-title {
  font-family: 'Righteous', sans-serif;
  font-size: 2.5rem;
}

.title-underline {
  width: 60px;
  height: 4px;
  margin-top: 1rem;
  border-radius: 2px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  padding: 2rem;
  border-radius: 20px;
  text-align: center;
  transition: transform 0.3s;
  height: 100%;

  &:hover {
    transform: translateY(-10px);
    background: rgba(255, 255, 255, 0.1);
  }
}

.icon-wrapper {
  width: 80px;
  height: 80px;
  margin: 0 auto 1.5rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  background: rgba(255, 255, 255, 0.1);
  
  &.orange { color: #f39c12; border: 2px solid #f39c12; }
  &.green { color: #2ecc71; border: 2px solid #2ecc71; }
  &.red { color: #e74c3c; border: 2px solid #e74c3c; }
}

.stat-title {
  font-size: 1.2rem;
  font-weight: 700;
  margin-bottom: 1rem;
  text-transform: uppercase;
}

.stat-desc {
  font-size: 0.9rem;
  opacity: 0.8;
  margin-bottom: 1.5rem;
  line-height: 1.6;
}

.stat-value {
  font-size: 2.5rem;
  font-weight: 800;
  font-family: 'Righteous', sans-serif;
  color: #fff;
}
</style>
