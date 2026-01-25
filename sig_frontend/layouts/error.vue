<template>
  <div class="error-page">
    <!-- Background decoration -->
    <div class="bg-decoration">
      <div class="gradient-orb orb-1"></div>
      <div class="gradient-orb orb-2"></div>
    </div>
    
    <div class="error-content">
      <!-- Error illustration -->
      <div class="error-visual">
        <div class="error-code">
          <span class="digit">4</span>
          <div class="zero-container">
            <i class="fas fa-map-marker-alt"></i>
          </div>
          <span class="digit">4</span>
        </div>
        <div class="error-glitch" data-text="404">404</div>
      </div>
      
      <!-- Error message -->
      <div class="error-message">
        <h1 class="error-title">Page introuvable</h1>
        <p class="error-desc">
          Oups ! La page que vous recherchez semble avoir disparu de notre carte.
          <br />Elle a peut-être été déplacée ou n'existe plus.
        </p>
      </div>
      
      <!-- Actions -->
      <div class="error-actions">
        <nuxt-link to="/" class="btn-primary">
          <i class="fas fa-home"></i>
          <span>Retour à l'accueil</span>
        </nuxt-link>
        <button class="btn-secondary" @click="goBack">
          <i class="fas fa-arrow-left"></i>
          <span>Page précédente</span>
        </button>
      </div>
      
      <!-- Helpful links -->
      <div class="helpful-links">
        <span class="links-label">Liens utiles :</span>
        <div class="links-list">
          <nuxt-link to="/">Accueil</nuxt-link>
          <nuxt-link to="/dashboard">Dashboard</nuxt-link>
          <nuxt-link to="/auth">Connexion</nuxt-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  layout: 'errorLayout',
  props: {
    error: {
      type: Object,
      default: null,
    },
  },
  head() {
    const statusCode = this.error?.statusCode || 404;
    return {
      title: `Erreur ${statusCode} - Page non trouvée`,
    };
  },
  methods: {
    goBack() {
      this.$router.go(-1);
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

.error-page {
  min-height: 100vh;
  background: linear-gradient(180deg, $primary 0%, $primary-light 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
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
  
  .gradient-orb {
    position: absolute;
    border-radius: 50%;
    filter: blur(100px);
    opacity: 0.15;
    
    &.orb-1 {
      width: 600px;
      height: 600px;
      background: $accent;
      top: -200px;
      right: -200px;
      animation: float 8s ease-in-out infinite;
    }
    
    &.orb-2 {
      width: 500px;
      height: 500px;
      background: $accent-secondary;
      bottom: -200px;
      left: -200px;
      animation: float 10s ease-in-out infinite reverse;
    }
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-30px); }
}

.error-content {
  position: relative;
  z-index: 1;
  text-align: center;
  max-width: 600px;
}

// Error visual with 404
.error-visual {
  margin-bottom: 2rem;
  position: relative;
}

.error-code {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  
  .digit {
    font-family: 'Outfit', sans-serif;
    font-size: 8rem;
    font-weight: 800;
    color: transparent;
    -webkit-text-stroke: 2px rgba(255, 255, 255, 0.2);
    
    @media (max-width: 576px) {
      font-size: 5rem;
    }
  }
  
  .zero-container {
    width: 120px;
    height: 120px;
    background: rgba(249, 115, 22, 0.15);
    border: 2px solid rgba(249, 115, 22, 0.3);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    animation: pulse 2s ease-in-out infinite;
    
    @media (max-width: 576px) {
      width: 80px;
      height: 80px;
    }
    
    i {
      font-size: 3rem;
      color: $accent;
      animation: bounce 2s ease-in-out infinite;
      
      @media (max-width: 576px) {
        font-size: 2rem;
      }
    }
  }
}

@keyframes pulse {
  0%, 100% { 
    transform: scale(1);
    box-shadow: 0 0 0 0 rgba(249, 115, 22, 0.3);
  }
  50% { 
    transform: scale(1.05);
    box-shadow: 0 0 40px rgba(249, 115, 22, 0.2);
  }
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.error-glitch {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-family: 'Outfit', sans-serif;
  font-size: 10rem;
  font-weight: 800;
  color: transparent;
  pointer-events: none;
  opacity: 0.03;
  
  @media (max-width: 576px) {
    font-size: 6rem;
  }
}

// Error message
.error-message {
  margin-bottom: 2.5rem;
}

.error-title {
  font-family: 'Outfit', sans-serif;
  font-size: 2rem;
  font-weight: 700;
  color: white;
  margin-bottom: 1rem;
  
  @media (max-width: 576px) {
    font-size: 1.5rem;
  }
}

.error-desc {
  color: rgba(255, 255, 255, 0.6);
  font-size: 1rem;
  line-height: 1.7;
  max-width: 450px;
  margin: 0 auto;
}

// Action buttons
.error-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
  margin-bottom: 3rem;
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  background: linear-gradient(135deg, $accent 0%, darken($accent, 10%) 100%);
  color: white;
  padding: 1rem 2rem;
  border-radius: 50px;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s $ease-premium;
  box-shadow: 0 4px 20px rgba(249, 115, 22, 0.4);
  
  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 30px rgba(249, 115, 22, 0.5);
  }
}

.btn-secondary {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  color: white;
  padding: 1rem 2rem;
  border-radius: 50px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s $ease-premium;
  
  &:hover {
    background: rgba(255, 255, 255, 0.15);
    transform: translateY(-2px);
  }
}

// Helpful links
.helpful-links {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  flex-wrap: wrap;
  
  .links-label {
    color: rgba(255, 255, 255, 0.4);
    font-size: 0.85rem;
  }
  
  .links-list {
    display: flex;
    gap: 1.5rem;
    
    a {
      color: rgba(255, 255, 255, 0.6);
      text-decoration: none;
      font-size: 0.85rem;
      transition: color 0.3s;
      
      &:hover {
        color: $accent;
      }
    }
  }
}
</style>
