<template>
  <footer class="footer-section">
    <!-- Wave divider -->
    <div class="wave-divider">
      <svg viewBox="0 0 1200 120" preserveAspectRatio="none">
        <path d="M321.39,56.44c58-10.79,114.16-30.13,172-41.86,82.39-16.72,168.19-17.73,250.45-.39C823.78,31,906.67,72,985.66,92.83c70.05,18.48,146.53,26.09,214.34,3V0H0V27.35A600.21,600.21,0,0,0,321.39,56.44Z"></path>
      </svg>
    </div>
    
    <div class="footer-content">
      <div class="container">
        <div class="footer-grid">
          <!-- Brand Column -->
          <div class="footer-brand">
            <div class="brand-logo">
              <img src="~assets/logo_mjs_n.png" alt="MJS Logo" height="60" />
              <div class="brand-info">
                <span class="brand-name">GÉOPORTAIL MJS</span>
                <span class="brand-desc">Ministère de la Jeunesse et des Sports</span>
              </div>
            </div>
            <p class="brand-tagline">
              Plateforme officielle de géolocalisation des infrastructures sportives et de jeunesse en Algérie.
            </p>
          </div>
          
          <!-- Links Column -->
          <div class="footer-links">
            <h4 class="footer-title">Liens Rapides</h4>
            <ul>
              <li><a href="#">Accueil</a></li>
              <li><a href="#maps-section">Cartes</a></li>
              <li><a href="#about">À Propos</a></li>
              <li><a href="#contact">Contact</a></li>
            </ul>
          </div>
          
          <!-- Contact Column -->
          <div class="footer-contact">
            <h4 class="footer-title">Contact</h4>
            <ul>
              <li>
                <i class="fas fa-map-marker-alt"></i>
                <span>003 Rue Mohamed Belouizdad, Alger</span>
              </li>
              <li>
                <i class="fas fa-phone"></i>
                <span>023 51 24 24</span>
              </li>
              <li>
                <i class="fas fa-envelope"></i>
                <span>contact@mjs.dz</span>
              </li>
            </ul>
          </div>
          
          <!-- Social Column -->
          <div class="footer-social">
            <h4 class="footer-title">Suivez-nous</h4>
            <div class="social-links">
              <a href="https://www.facebook.com/mjsalgeria/" target="_blank" class="social-link facebook" aria-label="Facebook">
                <i class="fab fa-facebook-f"></i>
              </a>
              <a href="#" class="social-link youtube" aria-label="YouTube">
                <i class="fab fa-youtube"></i>
              </a>
              <a href="#" class="social-link twitter" aria-label="Twitter">
                <i class="fab fa-twitter"></i>
              </a>
              <a href="#" class="social-link instagram" aria-label="Instagram">
                <i class="fab fa-instagram"></i>
              </a>
            </div>
          </div>
        </div>
        
        <!-- Bottom Bar -->
        <div class="footer-bottom">
          <div class="copyright">
            <a href="https://www.eadn.dz" target="_blank" class="eadn-link">
              <img src="~assets/eadn.png" alt="EADN" width="24" />
              <span>EADN</span>
            </a>
            <span class="separator">•</span>
            <span>&copy; {{ currentYear }} Tous droits réservés</span>
          </div>
          <div class="legal-links">
            <a href="#">Mentions légales</a>
            <a href="#">Politique de confidentialité</a>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Scroll to top button -->
    <transition name="fade">
      <button 
        v-show="showScrollTop" 
        class="scroll-top-btn" 
        @click="scrollToTop" 
        aria-label="Retour en haut"
      >
        <i class="fas fa-arrow-up"></i>
      </button>
    </transition>
  </footer>
</template>

<script>
export default {
  name: 'FooterSection',
  data() {
    return {
      showScrollTop: false
    };
  },
  computed: {
    currentYear() {
      return new Date().getFullYear();
    }
  },
  mounted() {
    window.addEventListener('scroll', this.handleScroll);
  },
  beforeDestroy() {
    window.removeEventListener('scroll', this.handleScroll);
  },
  methods: {
    scrollToTop() {
      window.scrollTo({
        top: 0,
        behavior: 'smooth'
      });
    },
    handleScroll() {
      this.showScrollTop = window.scrollY > 400;
    }
  }
};
</script>

<style lang="scss" scoped>
// Design system variables
$primary: #0f172a;
$primary-dark: #020617;
$accent: #f97316;
$accent-secondary: #06b6d4;
$ease-premium: cubic-bezier(0.16, 1, 0.3, 1);

.footer-section {
  background: $primary-dark;
  color: white;
  position: relative;
  overflow: hidden;
}

// Wave divider at top
.wave-divider {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  line-height: 0;
  transform: translateY(-99%);
  
  svg {
    width: 100%;
    height: 60px;
    
    path {
      fill: $primary-dark;
    }
  }
}

.footer-content {
  padding: 4rem 0 0;
}

.footer-grid {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 3rem;
  padding-bottom: 3rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  
  @media (max-width: 992px) {
    grid-template-columns: 1fr 1fr;
    gap: 2rem;
  }
  
  @media (max-width: 576px) {
    grid-template-columns: 1fr;
    text-align: center;
  }
}

.footer-brand {
  .brand-logo {
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-bottom: 1.5rem;
    
    @media (max-width: 576px) {
      justify-content: center;
    }
    
    img {
      filter: drop-shadow(0 2px 8px rgba(0,0,0,0.3));
    }
    
    .brand-info {
      display: flex;
      flex-direction: column;
      
      .brand-name {
        font-family: 'Outfit', 'Righteous', sans-serif;
        font-size: 1.2rem;
        font-weight: 700;
        background: linear-gradient(135deg, #ffffff 0%, #94a3b8 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
      }
      
      .brand-desc {
        font-size: 0.7rem;
        opacity: 0.5;
        text-transform: uppercase;
        letter-spacing: 0.05em;
      }
    }
  }
  
  .brand-tagline {
    font-size: 0.9rem;
    opacity: 0.6;
    line-height: 1.6;
    max-width: 320px;
    
    @media (max-width: 576px) {
      max-width: 100%;
    }
  }
}

.footer-title {
  font-size: 0.85rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: $accent;
  margin-bottom: 1.25rem;
}

.footer-links, .footer-contact {
  ul {
    list-style: none;
    padding: 0;
    margin: 0;
    
    li {
      margin-bottom: 0.75rem;
      
      a {
        color: rgba(255, 255, 255, 0.6);
        text-decoration: none;
        font-size: 0.9rem;
        transition: all 0.3s ease;
        
        &:hover {
          color: white;
          padding-left: 4px;
        }
      }
    }
  }
}

.footer-contact {
  ul li {
    display: flex;
    align-items: flex-start;
    gap: 10px;
    color: rgba(255, 255, 255, 0.6);
    font-size: 0.9rem;
    
    @media (max-width: 576px) {
      justify-content: center;
    }
    
    i {
      color: $accent;
      width: 16px;
      margin-top: 3px;
    }
  }
}

.social-links {
  display: flex;
  gap: 0.75rem;
  
  @media (max-width: 576px) {
    justify-content: center;
  }
}

.social-link {
  width: 44px;
  height: 44px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.7);
  transition: all 0.3s $ease-premium;
  
  &:hover {
    transform: translateY(-4px);
    color: white;
    
    &.facebook { background: #4267b2; border-color: #4267b2; }
    &.youtube { background: #ff0000; border-color: #ff0000; }
    &.twitter { background: #1da1f2; border-color: #1da1f2; }
    &.instagram { 
      background: linear-gradient(45deg, #f09433, #e6683c, #dc2743, #cc2366, #bc1888);
      border-color: transparent;
    }
  }
}

.footer-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem 0;
  flex-wrap: wrap;
  gap: 1rem;
  
  @media (max-width: 576px) {
    justify-content: center;
    text-align: center;
  }
}

.copyright {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 0.85rem;
  opacity: 0.6;
  
  .eadn-link {
    display: flex;
    align-items: center;
    gap: 6px;
    color: white;
    text-decoration: none;
    font-weight: 600;
    transition: opacity 0.3s;
    
    &:hover {
      opacity: 0.8;
    }
    
    img {
      border-radius: 4px;
    }
  }
  
  .separator {
    opacity: 0.3;
  }
}

.legal-links {
  display: flex;
  gap: 1.5rem;
  
  a {
    color: rgba(255, 255, 255, 0.4);
    text-decoration: none;
    font-size: 0.8rem;
    transition: color 0.3s;
    
    &:hover {
      color: white;
    }
  }
}

// Scroll to top button
.scroll-top-btn {
  position: fixed;
  bottom: 2rem;
  right: 2rem;
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, $accent 0%, darken($accent, 10%) 100%);
  color: white;
  border: none;
  border-radius: 14px;
  box-shadow: 0 4px 20px rgba(249, 115, 22, 0.4);
  cursor: pointer;
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s $ease-premium;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 30px rgba(249, 115, 22, 0.5);
  }
  
  &:active {
    transform: translateY(-2px);
  }
}

// Transitions
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}
.fade-enter, .fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}
</style>
