<template>
  <section class="hero-section">
    <div class="hero-background">
      <transition name="fade-slide" mode="out-in">
        <div :key="currentSlide" class="slide-bg" :style="{ backgroundImage: `url(${currentImage})` }">
          <div class="overlay"></div>
        </div>
      </transition>
    </div>

    <div class="container hero-content-container">
      <transition name="fade-up" mode="out-in">
        <div :key="currentSlide" class="row align-items-center h-100">
          <div class="col-lg-7 col-md-10 text-content">
            <h1 class="main-title">
              {{ slides[currentSlide].title }}
            </h1>
            <div class="title-underline"></div>
            <p class="description">
              {{ slides[currentSlide].description }}
            </p>
            <div class="cta-wrapper">
              <button class="btn-primary-glass" @click="handleCta">
                Découvrir
                <i class="fas fa-arrow-right ml-2"></i>
              </button>
            </div>
          </div>
          <!-- Optional Right Side Image/Graphic if needed -->
          <!-- <div class="col-lg-5 d-none d-lg-block">...</div> -->
        </div>
      </transition>
    </div>

    <!-- Slide Navigation -->
    <div class="slide-controls">
      <button class="control-btn prev" @click="prevSlide">
        <i class="fas fa-chevron-left"></i>
      </button>
      <div class="indicators">
        <span 
          v-for="(slide, index) in slides" 
          :key="index" 
          class="indicator" 
          :class="{ active: currentSlide === index }"
          @click="currentSlide = index"
        ></span>
      </div>
      <button class="control-btn next" @click="nextSlide">
        <i class="fas fa-chevron-right"></i>
      </button>
    </div>

    <div class="scroll-down" @click="$emit('scroll-down')">
      <div class="mouse">
        <div class="wheel"></div>
      </div>
      <span class="text">Explorer</span>
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
          description: "Géoportail MJS est point d’accès à toute la donnée qui concerne les établissements, événements, Pôles de développements, Fédérations et associations du secteur de la jeunesse et du sport.",
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
    },
    prevSlide() {
      this.currentSlide = (this.currentSlide - 1 + this.slides.length) % this.slides.length;
    },
    startAutoplay() {
      this.autoplayInterval = setInterval(this.nextSlide, 6000);
    },
    stopAutoplay() {
      if (this.autoplayInterval) clearInterval(this.autoplayInterval);
    },
    handleCta() {
      this.$emit('scroll-down');
    }
  }
};
</script>

<style lang="scss" scoped>
.hero-section {
  position: relative;
  height: 85vh; // Modern full viewport look
  min-height: 600px;
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
  }

  .overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, rgba(19, 28, 80, 0.9) 0%, rgba(19, 28, 80, 0.6) 100%);
  }
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
  
  .main-title {
    font-size: 3.5rem;
    font-weight: 800;
    margin-bottom: 1rem;
    line-height: 1.1;
    font-family: 'Righteous', sans-serif;
    text-shadow: 0 2px 10px rgba(0,0,0,0.3);
    
    @media (max-width: 768px) {
      font-size: 2.5rem;
    }
  }

  .title-underline {
    width: 80px;
    height: 4px;
    background: #e74c3c; // Red accent
    margin-bottom: 1.5rem;
    border-radius: 2px;
  }

  .description {
    font-size: 1.2rem;
    line-height: 1.6;
    margin-bottom: 2rem;
    opacity: 0.9;
    max-width: 600px;
  }
}

.btn-primary-glass {
  background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
  border: none;
  color: white;
  padding: 1rem 2.5rem;
  font-size: 1.1rem;
  font-weight: 600;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(231, 76, 60, 0.4);
  display: inline-flex;
  align-items: center;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 25px rgba(231, 76, 60, 0.6);
  }

  &:active {
    transform: translateY(-1px);
  }
}

.slide-controls {
  position: absolute;
  bottom: 50px;
  right: 50px;
  z-index: 10;
  display: flex;
  align-items: center;
  gap: 20px;

  .control-btn {
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.3);
    color: white;
    width: 40px;
    height: 40px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    backdrop-filter: blur(5px);
    transition: all 0.3s;

    &:hover {
      background: white;
      color: #131c50;
    }
  }

  .indicators {
    display: flex;
    gap: 10px;

    .indicator {
      width: 10px;
      height: 10px;
      background: rgba(255, 255, 255, 0.3);
      border-radius: 50%;
      cursor: pointer;
      transition: all 0.3s;

      &.active {
        background: white;
        transform: scale(1.2);
      }
    }
  }
}

.scroll-down {
  position: absolute;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  opacity: 0.8;
  transition: opacity 0.3s;

  &:hover {
    opacity: 1;
  }

  .mouse {
    width: 26px;
    height: 40px;
    border: 2px solid white;
    border-radius: 20px;
    position: relative;
    margin-bottom: 5px;

    .wheel {
      width: 4px;
      height: 4px;
      background: white;
      border-radius: 50%;
      position: absolute;
      top: 8px;
      left: 50%;
      transform: translateX(-50%);
      animation: wheel 1.5s infinite;
    }
  }

  .text {
    font-size: 0.8rem;
    text-transform: uppercase;
    letter-spacing: 1px;
  }
}

@keyframes wheel {
  0% { top: 8px; opacity: 1; }
  100% { top: 25px; opacity: 0; }
}

// Transitions
.fade-slide-enter-active, .fade-slide-leave-active {
  transition: opacity 1s ease;
}
.fade-slide-enter, .fade-slide-leave-to {
  opacity: 0;
}

.fade-up-enter-active {
  transition: all 0.8s ease;
  transition-delay: 0.3s; // Wait for bg
}
.fade-up-leave-active {
  transition: all 0.5s ease;
}
.fade-up-enter {
  opacity: 0;
  transform: translateY(20px);
}
.fade-up-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}
</style>
