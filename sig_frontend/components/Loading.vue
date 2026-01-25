<template>
  <transition name="fade">
    <div v-if="loading" class="loading-page">
      <!-- Background overlay -->
      <div class="loading-backdrop"></div>
      
      <!-- Loading content -->
      <div class="loading-content">
        <!-- Modern spinner -->
        <div class="loader-container">
          <div class="loader-ring">
            <div class="ring ring-1"></div>
            <div class="ring ring-2"></div>
            <div class="ring ring-3"></div>
          </div>
          <div class="loader-icon">
            <i class="fas fa-map-marked-alt"></i>
          </div>
        </div>
        
        <!-- Loading text -->
        <div class="loading-text">
          <span class="text-main">Chargement</span>
          <span class="dots">
            <span class="dot"></span>
            <span class="dot"></span>
            <span class="dot"></span>
          </span>
        </div>
      </div>
    </div>
  </transition>
</template>

<script>
export default {
  data: () => ({
    loading: false,
  }),
  methods: {
    start() {
      this.loading = true;
    },
    finish() {
      this.loading = false;
    },
  },
};
</script>

<style lang="scss" scoped>
// Design system variables
$primary: #0f172a;
$primary-light: #1e293b;
$accent: #f97316;
$accent-secondary: #06b6d4;
$ease-premium: cubic-bezier(0.16, 1, 0.3, 1);

.loading-page {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 9999;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-backdrop {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(15, 23, 42, 0.9);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
}

.loading-content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2rem;
}

// Modern ring loader
.loader-container {
  position: relative;
  width: 100px;
  height: 100px;
}

.loader-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  
  .ring {
    position: absolute;
    width: 100%;
    height: 100%;
    border-radius: 50%;
    border: 3px solid transparent;
    
    &.ring-1 {
      border-top-color: $accent;
      animation: spin 1.2s linear infinite;
    }
    
    &.ring-2 {
      width: 80%;
      height: 80%;
      top: 10%;
      left: 10%;
      border-right-color: $accent-secondary;
      animation: spin 1.5s linear infinite reverse;
    }
    
    &.ring-3 {
      width: 60%;
      height: 60%;
      top: 20%;
      left: 20%;
      border-bottom-color: rgba(255, 255, 255, 0.3);
      animation: spin 1s linear infinite;
    }
  }
}

.loader-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 1.5rem;
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes pulse {
  0%, 100% { 
    opacity: 0.5;
    transform: translate(-50%, -50%) scale(0.9);
  }
  50% { 
    opacity: 1;
    transform: translate(-50%, -50%) scale(1);
  }
}

// Loading text with animated dots
.loading-text {
  display: flex;
  align-items: center;
  gap: 4px;
  color: white;
  font-family: 'DM Sans', sans-serif;
  font-size: 1rem;
  font-weight: 500;
  letter-spacing: 0.05em;
}

.dots {
  display: flex;
  gap: 4px;
  
  .dot {
    width: 4px;
    height: 4px;
    background: $accent;
    border-radius: 50%;
    animation: bounce 1.2s ease-in-out infinite;
    
    &:nth-child(2) { animation-delay: 0.2s; }
    &:nth-child(3) { animation-delay: 0.4s; }
  }
}

@keyframes bounce {
  0%, 60%, 100% { 
    transform: translateY(0);
    opacity: 0.5;
  }
  30% { 
    transform: translateY(-6px);
    opacity: 1;
  }
}

// Fade transition
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter, .fade-leave-to {
  opacity: 0;
}
</style>
