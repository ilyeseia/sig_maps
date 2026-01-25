<template>
  <header class="landing-header" :class="{ 'header-scrolled': isScrolled }">
    <div class="container-fluid">
      <div class="header-content">
        <!-- Logo Section -->
        <div class="logo-section">
          <a href="https://www.mjs.gov.dz/index.php/fr" target="_blank" class="logo-link">
            <div class="logo-wrapper">
              <img src="~assets/logo_mjs_n.png" alt="logo" height="70" />
            </div>
            <div class="brand-wrapper">
              <span class="brand-text">GÉOPORTAIL</span>
              <span class="brand-sub">Ministère Jeunesse & Sports</span>
            </div>
          </a>
        </div>

        <!-- Actions Section -->
        <div class="actions-section">
          <nav class="nav-links" v-if="!isMobile">
            <a href="#maps-section" class="nav-link">Cartes</a>
            <a href="#about" class="nav-link">À Propos</a>
            <a href="#contact" class="nav-link">Contact</a>
          </nav>
          
          <div class="divider" v-if="!isMobile"></div>
          
          <div class="main-actions">
            <button 
              v-if="!profile.authenticated" 
              type="button" 
              class="btn-glass" 
              @click="$router.push('/auth')"
            >
              <i class="fas fa-sign-in-alt"></i>
              <span>Connexion</span>
            </button>
            <button 
              v-else 
              type="button" 
              class="btn-glass btn-dashboard" 
              @click="$router.push('/dashboard')"
            >
              <i class="fas fa-th-large"></i>
              <span>Dashboard</span>
            </button>
          </div>
          
          <div class="share-action">
            <button class="btn-icon" @click="toggleSocial" aria-label="Partager">
              <i class="fas fa-share-alt"></i>
            </button>
          </div>
        </div>
      </div>

      <!-- Social Media Dropdown -->
      <transition name="dropdown">
        <div v-show="showSocial" class="social-dropdown">
          <div class="social-item twitter">
            <a href="https://twitter.com/intent/tweet?url=https://sig.mjs.gov.dz/" target="_blank">
              <i class="fab fa-twitter"></i> 
              <span>Twitter</span>
            </a>
          </div>
          <div class="social-item facebook">
            <a href="https://www.facebook.com/sharer.php?u=https://sig.mjs.gov.dz/" target="_blank">
              <i class="fab fa-facebook"></i> 
              <span>Facebook</span>
            </a>
          </div>
          <div class="social-item linkedin">
            <a href="https://www.linkedin.com/sharing/share-offsite/?url=https://sig.mjs.gov.dz/" target="_blank">
              <i class="fab fa-linkedin-in"></i> 
              <span>LinkedIn</span>
            </a>
          </div>
          <div class="social-item email">
            <a href="mailto:contact@mjs.dz">
              <i class="fas fa-envelope"></i> 
              <span>Email</span>
            </a>
          </div>
        </div>
      </transition>
    </div>
  </header>
</template>

<script>
import { mapState, mapGetters } from 'vuex';

export default {
  name: 'HeaderSection',
  data() {
    return {
      showSocial: false,
      isScrolled: false
    };
  },
  computed: {
    ...mapState(['profile']),
    ...mapGetters({
      isMobile: 'app/getIsMobile'
    })
  },
  mounted() {
    window.addEventListener('scroll', this.handleScroll);
  },
  beforeDestroy() {
    window.removeEventListener('scroll', this.handleScroll);
  },
  methods: {
    toggleSocial() {
      this.showSocial = !this.showSocial;
    },
    handleScroll() {
      this.isScrolled = window.scrollY > 50;
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
$glass-bg: rgba(255, 255, 255, 0.08);
$glass-border: rgba(255, 255, 255, 0.12);
$ease-premium: cubic-bezier(0.16, 1, 0.3, 1);

.landing-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  padding: 1rem 0;
  transition: all 0.4s $ease-premium;
  background: transparent;
  
  &.header-scrolled {
    background: rgba(15, 23, 42, 0.95);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    padding: 0.75rem 0;
    box-shadow: 0 4px 30px rgba(0, 0, 0, 0.2);
    
    .logo-wrapper img {
      height: 55px;
    }
    
    .brand-wrapper {
      .brand-text {
        font-size: 1.2rem;
      }
      .brand-sub {
        font-size: 0.65rem;
      }
    }
  }
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 2rem;
}

.logo-section {
  .logo-link {
    display: flex;
    align-items: center;
    text-decoration: none;
    gap: 1rem;
    
    .logo-wrapper {
      position: relative;
      
      img {
        height: 70px;
        transition: all 0.3s $ease-premium;
        filter: drop-shadow(0 2px 8px rgba(0,0,0,0.2));
      }
    }
    
    .brand-wrapper {
      display: flex;
      flex-direction: column;
      
      .brand-text {
        color: white;
        font-weight: 800;
        font-size: 1.4rem;
        font-family: 'Outfit', 'Righteous', sans-serif;
        letter-spacing: 0.05em;
        transition: all 0.3s ease;
        background: linear-gradient(135deg, #ffffff 0%, #94a3b8 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
      }
      
      .brand-sub {
        color: rgba(255, 255, 255, 0.6);
        font-size: 0.7rem;
        font-weight: 500;
        letter-spacing: 0.1em;
        text-transform: uppercase;
        transition: all 0.3s ease;
      }
    }

    &:hover {
      .logo-wrapper img {
        transform: scale(1.05);
      }
      .brand-text {
        background: linear-gradient(135deg, #ffffff 0%, $accent 100%);
        -webkit-background-clip: text;
      }
    }
  }
}

.actions-section {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.nav-links {
  display: flex;
  gap: 0.5rem;
  
  .nav-link {
    color: rgba(255, 255, 255, 0.7);
    text-decoration: none;
    padding: 0.5rem 1rem;
    font-size: 0.9rem;
    font-weight: 500;
    border-radius: 8px;
    transition: all 0.3s ease;
    
    &:hover {
      color: white;
      background: rgba(255, 255, 255, 0.08);
    }
  }
}

.btn-glass {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: $glass-bg;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid $glass-border;
  color: white;
  padding: 0.7rem 1.3rem;
  border-radius: 50px;
  font-weight: 600;
  font-size: 0.9rem;
  transition: all 0.3s $ease-premium;
  cursor: pointer;
  outline: none;

  i {
    font-size: 0.85rem;
  }

  &:hover {
    background: rgba(255, 255, 255, 0.15);
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  }

  &:active {
    transform: translateY(0);
  }
  
  &.btn-dashboard {
    background: linear-gradient(135deg, $accent 0%, darken($accent, 10%) 100%);
    border-color: transparent;
    
    &:hover {
      box-shadow: 0 8px 25px rgba(249, 115, 22, 0.4);
    }
  }
}

.divider {
  width: 1px;
  height: 28px;
  background: rgba(255, 255, 255, 0.15);
  margin: 0 0.5rem;
}

.btn-icon {
  background: transparent;
  border: 1px solid transparent;
  color: rgba(255, 255, 255, 0.7);
  cursor: pointer;
  transition: all 0.3s $ease-premium;
  padding: 0.6rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;

  &:hover {
    background: rgba(255, 255, 255, 0.1);
    color: white;
    transform: rotate(15deg);
  }
}

.social-dropdown {
  position: absolute;
  top: calc(100% + 10px);
  right: 2rem;
  background: rgba(15, 23, 42, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 0.75rem;
  display: flex;
  gap: 0.5rem;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
}

.social-item {
  a {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    gap: 6px;
    color: white;
    text-decoration: none;
    font-size: 0.75rem;
    font-weight: 500;
    transition: all 0.3s ease;
    padding: 0.75rem 1rem;
    border-radius: 12px;
    min-width: 70px;

    i {
      font-size: 1.2rem;
      transition: transform 0.3s ease;
    }

    &:hover {
      background: rgba(255, 255, 255, 0.08);
      
      i {
        transform: scale(1.2);
      }
    }
  }

  &.twitter i { color: #1da1f2; }
  &.facebook i { color: #4267b2; }
  &.linkedin i { color: #0077b5; }
  &.email i { color: $accent; }
}

// Dropdown transition
.dropdown-enter-active {
  animation: dropIn 0.3s $ease-premium;
}
.dropdown-leave-active {
  animation: dropIn 0.2s reverse ease-in;
}

@keyframes dropIn {
  from {
    opacity: 0;
    transform: translateY(-10px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

// Mobile responsive
@media (max-width: 768px) {
  .header-content {
    padding: 0 1rem;
  }
  
  .brand-wrapper {
    display: none !important;
  }
  
  .logo-wrapper img {
    height: 50px !important;
  }
  
  .btn-glass span {
    display: none;
  }
  
  .btn-glass {
    padding: 0.7rem;
    border-radius: 50%;
    
    i {
      font-size: 1rem;
      margin: 0;
    }
  }
}
</style>
