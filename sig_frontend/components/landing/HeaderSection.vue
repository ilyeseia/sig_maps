<template>
  <header class="landing-header">
    <div class="container-fluid">
      <div class="header-content">
        <!-- Logo Section -->
        <div class="logo-section">
          <a href="https://www.mjs.gov.dz/index.php/fr" target="_blank" class="logo-link">
            <img src="~assets/logo_mjs_n.png" alt="logo" height="80" />
            <span class="brand-text">GÉOPORTAIL MJS</span>
          </a>
        </div>

        <!-- Actions Section -->
        <div class="actions-section">
          <div class="main-actions">
            <button 
              v-if="!profile.authenticated" 
              type="button" 
              class="btn-glass" 
              @click="$router.push('/auth')"
            >
              <i class="fas fa-sign-in-alt mr-2"></i> Connexion
            </button>
            <button 
              v-else 
              type="button" 
              class="btn-glass" 
              @click="$router.push('/dashboard')"
            >
              <i class="fas fa-user-cog mr-2"></i> Dashboard
            </button>
          </div>
          
          <div class="divider"></div>
          
          <div class="share-action">
            <button class="btn-icon" @click="toggleSocial">
              <i class="fas fa-share-alt fa-lg"></i>
            </button>
          </div>
        </div>
      </div>

      <!-- Social Media Dropdown (Glassmorphism) -->
      <transition name="fade">
        <div v-show="showSocial" class="social-dropdown glass-panel">
          <div class="social-item twitter">
            <a href="https://twitter.com/intent/tweet?url=https://sig.mjs.gov.dz/" target="_blank">
              <i class="fab fa-twitter"></i> Partager sur Twitter
            </a>
          </div>
          <div class="social-item facebook">
            <a href="https://www.facebook.com/sharer.php?u=https://sig.mjs.gov.dz/" target="_blank">
              <i class="fab fa-facebook"></i> Partager sur Facebook
            </a>
          </div>
          <div class="social-item linkedin">
            <a href="https://www.linkedin.com/sharing/share-offsite/?url=https://sig.mjs.gov.dz/" target="_blank">
              <i class="fab fa-linkedin-in"></i> Partager sur Linkedin
            </a>
          </div>
          <div class="social-item email">
            <a href="mailto:contact@mjs.dz">
              <i class="fas fa-envelope"></i> Envoyer par mail
            </a>
          </div>
        </div>
      </transition>
    </div>
  </header>
</template>

<script>
import { mapState } from 'vuex';

export default {
  name: 'HeaderSection',
  data() {
    return {
      showSocial: false
    };
  },
  computed: {
    ...mapState(['profile'])
  },
  methods: {
    toggleSocial() {
      this.showSocial = !this.showSocial;
    }
  }
};
</script>

<style lang="scss" scoped>
// Variables would ideally be in a shared file, but defining here for safety
$primary-color: #131c50;
$glass-bg: rgba(255, 255, 255, 0.1);
$glass-border: rgba(255, 255, 255, 0.2);

.landing-header {
  background: $primary-color;
  padding: 1rem 0;
  position: relative;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  z-index: 100;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo-section {
  .logo-link {
    display: flex;
    align-items: center;
    text-decoration: none;
    
    img {
      margin-right: 15px;
      transition: transform 0.3s ease;
    }
    
    .brand-text {
      color: white;
      font-weight: 800;
      font-size: 1.5rem;
      font-family: 'Righteous', 'Muli', sans-serif; // Using existing font
      text-transform: uppercase;
      letter-spacing: 1px;
    }

    &:hover img {
      transform: scale(1.05);
    }
  }
}

.actions-section {
  display: flex;
  align-items: center;
}

// Glassmorphism Button
.btn-glass {
  background: $glass-bg;
  backdrop-filter: blur(10px);
  border: 1px solid $glass-border;
  color: white;
  padding: 0.6rem 1.5rem;
  border-radius: 50px;
  font-weight: 600;
  transition: all 0.3s ease;
  cursor: pointer;
  display: flex;
  align-items: center;
  outline: none;

  &:hover {
    background: rgba(255, 255, 255, 0.2);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  &:active {
    transform: translateY(0);
  }
}

.divider {
  width: 1px;
  height: 30px;
  background: rgba(255, 255, 255, 0.3);
  margin: 0 1.5rem;
}

.btn-icon {
  background: transparent;
  border: none;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 0.5rem;
  border-radius: 50%;

  &:hover {
    background: rgba(255, 255, 255, 0.1);
    transform: rotate(15deg);
  }
}

.social-dropdown {
  position: absolute;
  top: 100%;
  right: 20px;
  margin-top: 10px;
  background: rgba(19, 28, 80, 0.95);
  backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 1rem;
  min-width: 250px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.social-item {
  a {
    display: flex;
    align-items: center;
    color: white;
    text-decoration: none;
    font-size: 0.95rem;
    transition: all 0.2s;
    padding: 0.5rem;
    border-radius: 8px;

    i {
      width: 25px;
      margin-right: 10px;
      font-size: 1.1rem;
    }

    &:hover {
      background: rgba(255, 255, 255, 0.1);
      transform: translateX(5px);
    }
  }

  &.twitter i { color: #1da1f2; }
  &.facebook i { color: #4267b2; }
  &.linkedin i { color: #0077b5; }
  &.email i { color: #ffc107; }
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}
.fade-enter, .fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
