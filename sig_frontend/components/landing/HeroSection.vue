<template>
  <section class="hero-section">
    <!-- Animated Background -->
    <div class="hero-background">
      <transition name="fade-slide" mode="out-in">
        <div :key="currentSlide" class="slide-bg" :style="{ backgroundImage: `url(${currentImage})` }">
          <div class="overlay"></div>
          <div class="noise-overlay"></div>
        </div>
      </transition>
      
      <!-- Floating geometric shapes -->
      <div class="floating-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
      </div>
    </div>

    <div class="container hero-content-container">
      <transition name="fade-up" mode="out-in">
        <div :key="currentSlide" class="row align-items-center h-100">
          <div class="col-lg-7 col-md-10 text-content">
            <span class="hero-badge">
              <i class="fas fa-map-marked-alt"></i>
              Géoportail Officiel
            </span>
            <h1 class="main-title">
              {{ slides[currentSlide].title }}
            </h1>
            <div class="title-underline">
              <span class="underline-glow"></span>
            </div>
            <p class="description">
              {{ slides[currentSlide].description }}
            </p>
            <div class="cta-wrapper">
              <button class="btn-primary-glow" @click="handleCta">
                <span>Découvrir</span>
                <i class="fas fa-arrow-right"></i>
              </button>
              <button class="btn-secondary-glass" @click="$router.push('/auth')">
                <i class="fas fa-sign-in-alt"></i>
                <span>Connexion</span>
              </button>
            </div>
          </div>
        </div>
      </transition>
    </div>

    <!-- Modern Slide Navigation -->
    <div class="slide-controls">
      <button class="control-btn prev" @click="prevSlide" aria-label="Précédent">
        <i class="fas fa-chevron-left"></i>
      </button>
      <div class="indicators">
        <span 
          v-for="(slide, index) in slides" 
          :key="index" 
          class="indicator" 
          :class="{ active: currentSlide === index }"
          @click="goToSlide(index)"
        >
          <span class="indicator-progress" v-if="currentSlide === index"></span>
        </span>
      </div>
      <button class="control-btn next" @click="nextSlide" aria-label="Suivant">
        <i class="fas fa-chevron-right"></i>
      </button>
    </div>

    <!-- Scroll indicator -->
    <div class="scroll-down" @click="$emit('scroll-down')">
      <div class="mouse">
        <div class="wheel"></div>
      </div>
      <span class="text">Explorer</span>
      <div class="scroll-line"></div>
    </div>
  </section>
</template>

<script>
export default {
  name: 'HeroSection',
  data() {
    return {
      currentSlide: 0,
      slides: [
        {
          title: 'GÉOPORTAIL MJS',
          description: "Géoportail MJS est point d'accès à toute la donnée qui concerne les établissements, événements, Pôles de développements, Fédérations et associations du secteur de la jeunesse et du sport.",
          image: 'https://images.pexels.com/photos/3837487/pexels-photo-3837487.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=1920'
        },
        {
          title: 'RECHERCHEZ & CONSULTEZ',
          description: "Recherchez, consulter un établissement, mesurez sa superficie facilement… et gratuitement avec KharitaDZ.",
          image: 'https://images.pexels.com/photos/3837487/pexels-photo-3837487.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=1920'
        },
        {
          title: 'FONDS DE CARTE',
          description: "Plusieurs fonds de cartes à disposition avec différents découpages géographiques… trouvez le fond de carte adapté à vos besoins.",
          image: 'https://images.pexels.com/photos/3837487/pexels-photo-3837487.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=1920'
        }
      ],
      autoplayInterval: null
    }
  },
  computed: {
    currentImage() {
      return this.slides[this.currentSlide].image;
    }
  },
  mounted() {
    this.startAutoplay();
  },
  beforeDestroy() {
    this.stopAutoplay();
  },
  methods: {
    nextSlide() {
      this.currentSlide = (this.currentSlide + 1) % this.slides.length;
      this.resetAutoplay();
    },
    prevSlide() {
      this.currentSlide = (this.currentSlide - 1 + this.slides.length) % this.slides.length;
      this.resetAutoplay();
    },
    goToSlide(index) {
      this.currentSlide = index;
      this.resetAutoplay();
    },
    startAutoplay() {
      this.autoplayInterval = setInterval(this.nextSlide, 6000);
    },
    stopAutoplay() {
      if (this.autoplayInterval) clearInterval(this.autoplayInterval);
    },
    resetAutoplay() {
      this.stopAutoplay();
      this.startAutoplay();
    },
    handleCta() {
      this.$emit('scroll-down');
    }
  }
};
</script>

<style lang="scss" scoped>
// Using design system variables
$primary: #0f172a;
$accent: #f97316;
$accent-secondary: #06b6d4;
$ease-premium: cubic-bezier(0.16, 1, 0.3, 1);

.hero-section {
  position: relative;
  height: 100vh;
  min-height: 700px;
  overflow: hidden;
  color: white;
  display: flex;
  align-items: center;
}

.hero-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;

  .slide-bg {
    width: 100%;
    height: 100%;
    background-size: cover;
    background-position: center;
    position: absolute;
    transform: scale(1.05);
    animation: zoomOut 6s ease-out forwards;
  }

  .overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(
      135deg, 
      rgba(15, 23, 42, 0.95) 0%, 
      rgba(30, 58, 95, 0.85) 50%,
      rgba(15, 23, 42, 0.9) 100%
    );
  }
  
  .noise-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    opacity: 0.03;
    background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noiseFilter'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.85' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noiseFilter)'/%3E%3C/svg%3E");
    pointer-events: none;
  }
}

// Floating geometric shapes
.floating-shapes {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
  pointer-events: none;
  z-index: 1;
  
  .shape {
    position: absolute;
    border-radius: 50%;
    opacity: 0.1;
    
    &.shape-1 {
      width: 400px;
      height: 400px;
      background: linear-gradient(135deg, $accent 0%, transparent 70%);
      top: -100px;
      right: -100px;
      animation: float 8s ease-in-out infinite;
    }
    
    &.shape-2 {
      width: 300px;
      height: 300px;
      background: linear-gradient(135deg, $accent-secondary 0%, transparent 70%);
      bottom: -50px;
      left: -50px;
      animation: float 10s ease-in-out infinite reverse;
    }
    
    &.shape-3 {
      width: 200px;
      height: 200px;
      background: linear-gradient(135deg, $accent 0%, transparent 70%);
      top: 50%;
      left: 60%;
      animation: float 12s ease-in-out infinite;
    }
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-30px) rotate(5deg); }
}

@keyframes zoomOut {
  from { transform: scale(1.1); }
  to { transform: scale(1); }
}

.hero-content-container {
  position: relative;
  z-index: 2;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.text-content {
  padding: 2rem;
  
  .hero-badge {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 0.5rem 1rem;
    background: rgba(249, 115, 22, 0.15);
    border: 1px solid rgba(249, 115, 22, 0.3);
    border-radius: 50px;
    font-size: 0.85rem;
    font-weight: 600;
    color: $accent;
    margin-bottom: 1.5rem;
    backdrop-filter: blur(10px);
    animation: fadeUp 0.6s $ease-premium forwards;
    animation-delay: 0.1s;
    opacity: 0;
    
    i {
      font-size: 0.9rem;
    }
  }
  
  .main-title {
    font-size: clamp(2.5rem, 5vw, 4rem);
    font-weight: 800;
    margin-bottom: 1rem;
    line-height: 1.1;
    font-family: 'Outfit', 'Righteous', sans-serif;
    letter-spacing: -0.02em;
    animation: fadeUp 0.6s $ease-premium forwards;
    animation-delay: 0.2s;
    opacity: 0;
    
    // Gradient text effect
    background: linear-gradient(135deg, #ffffff 0%, #94a3b8 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .title-underline {
    position: relative;
    width: 100px;
    height: 4px;
    background: rgba(255, 255, 255, 0.2);
    margin-bottom: 1.5rem;
    border-radius: 2px;
    overflow: hidden;
    animation: fadeUp 0.6s $ease-premium forwards;
    animation-delay: 0.3s;
    opacity: 0;
    
    .underline-glow {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, $accent 0%, $accent-secondary 100%);
      animation: shimmerLine 2s ease-in-out infinite;
    }
  }

  .description {
    font-size: 1.15rem;
    line-height: 1.7;
    margin-bottom: 2rem;
    opacity: 0.85;
    max-width: 550px;
    font-family: 'DM Sans', 'Poppins', sans-serif;
    animation: fadeUp 0.6s $ease-premium forwards;
    animation-delay: 0.4s;
    opacity: 0;
  }
  
  .cta-wrapper {
    display: flex;
    gap: 1rem;
    flex-wrap: wrap;
    animation: fadeUp 0.6s $ease-premium forwards;
    animation-delay: 0.5s;
    opacity: 0;
  }
}

@keyframes fadeUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes shimmerLine {
  0%, 100% { 
    transform: translateX(-100%);
    opacity: 0.5;
  }
  50% { 
    transform: translateX(0);
    opacity: 1;
  }
}

// Primary CTA Button with glow
.btn-primary-glow {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  background: linear-gradient(135deg, $accent 0%, darken($accent, 10%) 100%);
  border: none;
  color: white;
  padding: 1rem 2rem;
  font-size: 1rem;
  font-weight: 600;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.3s $ease-premium;
  box-shadow: 0 4px 20px rgba(249, 115, 22, 0.4);
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(
      90deg, 
      transparent, 
      rgba(255,255,255,0.2), 
      transparent
    );
    transition: 0.5s;
  }

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 30px rgba(249, 115, 22, 0.5);
    
    &::before {
      left: 100%;
    }
  }

  &:active {
    transform: translateY(-1px);
  }
  
  i {
    transition: transform 0.3s ease;
  }
  
  &:hover i {
    transform: translateX(4px);
  }
}

// Secondary glass button
.btn-secondary-glass {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  color: white;
  padding: 1rem 1.5rem;
  font-size: 1rem;
  font-weight: 500;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.3s $ease-premium;

  &:hover {
    background: rgba(255, 255, 255, 0.15);
    border-color: rgba(255, 255, 255, 0.25);
    transform: translateY(-2px);
  }
}

// Slide controls
.slide-controls {
  position: absolute;
  bottom: 60px;
  right: 60px;
  z-index: 10;
  display: flex;
  align-items: center;
  gap: 16px;

  @media (max-width: 768px) {
    right: 20px;
    bottom: 100px;
  }

  .control-btn {
    background: rgba(255, 255, 255, 0.08);
    border: 1px solid rgba(255, 255, 255, 0.15);
    color: white;
    width: 44px;
    height: 44px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    backdrop-filter: blur(10px);
    transition: all 0.3s $ease-premium;

    &:hover {
      background: $accent;
      border-color: $accent;
      transform: scale(1.1);
    }
  }

  .indicators {
    display: flex;
    gap: 8px;

    .indicator {
      width: 32px;
      height: 4px;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 2px;
      cursor: pointer;
      transition: all 0.3s ease;
      position: relative;
      overflow: hidden;

      &.active {
        background: rgba(255, 255, 255, 0.3);
        
        .indicator-progress {
          position: absolute;
          top: 0;
          left: 0;
          height: 100%;
          background: $accent;
          border-radius: 2px;
          animation: progress 6s linear forwards;
        }
      }
      
      &:hover:not(.active) {
        background: rgba(255, 255, 255, 0.4);
      }
    }
  }
}

@keyframes progress {
  from { width: 0; }
  to { width: 100%; }
}

// Scroll indicator
.scroll-down {
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  opacity: 0.7;
  transition: all 0.3s ease;

  &:hover {
    opacity: 1;
    
    .mouse {
      border-color: $accent;
    }
  }

  .mouse {
    width: 24px;
    height: 38px;
    border: 2px solid rgba(255, 255, 255, 0.5);
    border-radius: 12px;
    position: relative;
    margin-bottom: 8px;
    transition: border-color 0.3s ease;

    .wheel {
      width: 3px;
      height: 8px;
      background: $accent;
      border-radius: 2px;
      position: absolute;
      top: 6px;
      left: 50%;
      transform: translateX(-50%);
      animation: wheel 1.5s infinite;
    }
  }

  .text {
    font-size: 0.7rem;
    text-transform: uppercase;
    letter-spacing: 2px;
    font-weight: 500;
    margin-bottom: 8px;
  }
  
  .scroll-line {
    width: 1px;
    height: 30px;
    background: linear-gradient(to bottom, rgba(255,255,255,0.5), transparent);
    animation: scrollLine 1.5s infinite;
  }
}

@keyframes wheel {
  0% { top: 6px; opacity: 1; }
  100% { top: 20px; opacity: 0; }
}

@keyframes scrollLine {
  0%, 100% { opacity: 0.5; height: 30px; }
  50% { opacity: 1; height: 40px; }
}

// Vue transitions
.fade-slide-enter-active, .fade-slide-leave-active {
  transition: opacity 1.2s ease;
}
.fade-slide-enter, .fade-slide-leave-to {
  opacity: 0;
}

.fade-up-enter-active {
  transition: all 0.8s $ease-premium;
  transition-delay: 0.3s;
}
.fade-up-leave-active {
  transition: all 0.5s ease;
}
.fade-up-enter {
  opacity: 0;
  transform: translateY(30px);
}
.fade-up-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}
</style>
