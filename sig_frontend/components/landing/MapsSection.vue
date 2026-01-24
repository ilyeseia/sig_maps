<template>
  <section class="maps-section">
    <div class="container-fluid">
      <div class="section-header text-center mb-5">
        <h2 class="section-title">Les Cartes Géographiques</h2>
        <p class="section-subtitle">Ministère de la Jeunesse et des Sports</p>
        <div class="title-underline mx-auto"></div>
      </div>

      <div class="maps-grid-wrapper" v-if="isFound">
        <div class="row justify-content-center">
          <div 
            class="col-xl-3 col-lg-4 col-md-6 mb-4" 
            v-for="map in maps" 
            :key="map.id"
          >
            <div class="map-card glass-panel" @click="$emit('go-to-map', map)">
              <div class="map-preview">
                <!-- Using iframe as in original, but styled -->
                <iframe 
                  :id="map.slug" 
                  class="map-iframe"
                  :src="`${rootUrl}/sharedmap/${map.id}?control=false&public=true`" 
                  allowtransparency="true"
                  frameborder="0" 
                  scrolling="no"
                ></iframe>
                <div class="card-overlay">
                  <span class="view-btn">Consulter</span>
                </div>
              </div>
              <div class="map-info">
                <h5 class="map-title">{{ map.name }}</h5>
                <div class="map-meta">
                  <span class="badge-custom privacy">{{ map.privacy || 'Public' }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="empty-state text-center">
        <div class="icon-wrapper mb-3">
          <i class="far fa-map fa-4x text-muted"></i>
        </div>
        <p class="h5 text-muted">Il n'y a pas de carte pour le moment!</p>
      </div>
    </div>
  </section>
</template>

<script>
export default {
  name: 'MapsSection',
  props: {
    maps: {
      type: Array,
      default: () => []
    },
    rootUrl: {
      type: String,
      required: true
    },
    isFound: {
      type: Boolean,
      default: false
    }
  }
};
</script>

<style lang="scss" scoped>
.maps-section {
  padding: 5rem 0;
  background-color: #f8f9fa;
  position: relative;
  
  // Subtle background pattern
  background-image: radial-gradient(#e1e1e1 1px, transparent 1px);
  background-size: 20px 20px;
}

.section-title {
  font-family: 'Righteous', sans-serif;
  color: #131c50;
  font-size: 2.5rem;
  margin-bottom: 0.5rem;
}

.section-subtitle {
  color: #666;
  font-size: 1.1rem;
}

.title-underline {
  width: 60px;
  height: 4px;
  background: #e74c3c;
  border-radius: 2px;
  margin-top: 1rem;
}

.map-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  cursor: pointer;
  height: 100%;
  border: 1px solid rgba(0, 0, 0, 0.05);

  &:hover {
    transform: translateY(-10px);
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);

    .card-overlay {
      opacity: 1;
    }
  }
}

.map-preview {
  position: relative;
  height: 220px;
  overflow: hidden;
  background: #eee;

  .map-iframe {
    width: 100%;
    height: 100%;
    pointer-events: none; // Disable interaction within card to allow click on card
    border: none;
  }

  .card-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(19, 28, 80, 0.6);
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: opacity 0.3s ease;
  }

  .view-btn {
    background: white;
    color: #131c50;
    padding: 0.5rem 1.5rem;
    border-radius: 50px;
    font-weight: 600;
    transform: translateY(10px);
    transition: transform 0.3s;
  }
  
  &:hover .view-btn {
    transform: translateY(0);
  }
}

.map-info {
  padding: 1.5rem;

  .map-title {
    font-weight: 700;
    color: #131c50;
    margin-bottom: 0.5rem;
    font-size: 1.1rem;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .map-meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .badge-custom {
    font-size: 0.75rem;
    padding: 0.25rem 0.8rem;
    border-radius: 12px;
    background: #e3f2fd;
    color: #1976d2;
    font-weight: 600;
    text-transform: uppercase;
  }
}

.empty-state {
  padding: 3rem;
  opacity: 0.6;
}
</style>
