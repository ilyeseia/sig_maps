<template>
  <footer class="footer-section">
    <div class="container-fluid">
      <div class="row align-items-center">
        <!-- Logo/EADN -->
        <div class="col-md-6 text-center text-md-left mb-3 mb-md-0">
          <div class="d-flex align-items-center justify-content-center justify-content-md-start">
            <a href="https://www.eadn.dz" target="_blank" class="eadn-link">
              <img src="~assets/eadn.png" alt="EADN Logo" width="30" class="mr-2" />
            </a>
            <p class="copyright mb-0">
              <a href="https://www.eadn.dz" target="_blank" class="text-white font-weight-bold">EADN</a> 
              &copy; {{ new Date().getFullYear() }} | Tous droits réservés
            </p>
          </div>
        </div>

        <!-- Social Links -->
        <div class="col-md-6 text-center text-md-right">
          <ul class="social-list list-unstyled mb-0">
            <li class="social-item">
              <a href="https://www.facebook.com/mjsalgeria/" target="_blank" class="social-link">
                <i class="fab fa-facebook-f"></i>
              </a>
            </li>
            <li class="social-item">
              <a href="#" class="social-link">
                <i class="fab fa-youtube"></i>
              </a>
            </li>
          </ul>
        </div>
      </div>
    </div>
    
    <button class="scroll-top-btn" @click="scrollToTop" title="Go to top">
      <i class="fas fa-chevron-up"></i>
    </button>
  </footer>
</template>

<script>
export default {
  name: 'FooterSection',
  methods: {
    scrollToTop() {
      window.scrollTo({
        top: 0,
        behavior: 'smooth'
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.footer-section {
  background: #0f1642; // Darker shade of primary
  color: white;
  padding: 1.5rem 0;
  position: relative;
}

.copyright {
  font-size: 0.9rem;
  opacity: 0.8;
  a { text-decoration: none; color: white; }
}

.social-list {
  display: flex;
  justify-content: center;
  
  @media (min-width: 768px) {
    justify-content: flex-end;
  }
}

.social-item {
  margin-left: 1rem;
}

.social-link {
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  transition: all 0.3s;

  &:hover {
    background: #e74c3c;
    transform: translateY(-3px);
    color: white;
  }
}

.scroll-top-btn {
  position: fixed;
  bottom: 2rem;
  right: 2rem;
  width: 50px;
  height: 50px;
  background: #e74c3c;
  color: white;
  border: none;
  border-radius: 50%;
  box-shadow: 0 4px 15px rgba(0,0,0,0.3);
  cursor: pointer;
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
  
  &:hover {
    transform: scale(1.1);
    background: #c0392b;
  }
}
</style>
