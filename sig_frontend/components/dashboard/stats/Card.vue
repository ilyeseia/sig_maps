<template>
  <section class="stats p-5 w-100">
    <div class="stats__card stats__card--purple">
      <div class="stats__detail">
        <div class="stats__number">
          <p>
            <ICountUp :delay="delay" :endVal="stats.mapNB" :options="options" />
          </p>
          <h5>Carte{{ stats.layerNB > 1 ? 's' : '' }}</h5>
        </div>
        <div class="stats__icon-card">
          <i class="fa fa-map"></i>
        </div>
      </div>
    </div>
    <div class="stats__card stats__card--teal">
      <div class="stats__detail">
        <div class="stats__number">
          <p>
            <ICountUp
              :delay="delay"
              :endVal="stats.layerNB"
              :options="options"
            />
          </p>
          <h5>Couche{{ stats.layerNB > 1 ? 's' : '' }}</h5>
        </div>
        <div class="stats__icon-card">
          <i class="fa fa-layer-group"></i>
        </div>
      </div>
    </div>
    <div class="stats__card stats__card--green">
      <div class="stats__detail">
        <div class="stats__number">
          <p>
            <ICountUp
              :delay="delay"
              :endVal="stats.userNB"
              :options="options"
            />
          </p>
          <h5>Utilisateur{{ stats.layerNB > 1 ? 's' : '' }}</h5>
        </div>
        <div class="stats__icon-card">
          <i class="fa fa-user"></i>
        </div>
      </div>
    </div>
    <div class="stats__card stats__card--orange">
      <div class="stats__detail">
        <div class="stats__number">
          <p>
            <ICountUp
              :delay="delay"
              :endVal="stats.groupNB"
              :options="options"
            />
          </p>
          <h5>Groupe{{ stats.layerNB > 1 ? 's' : '' }}</h5>
        </div>
        <div class="stats__icon-card">
          <i class="fa fa-users"></i>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import { backApi } from '../../../methods/serverApi'
import ICountUp from 'vue-countup-v2'
export default {
  components: {
    ICountUp,
  },
  data() {
    return {
      delay: 1000,
      options: {
        useEasing: false,
        useGrouping: true,
        separator: ',',
        decimal: '.',
        prefix: '',
        suffix: '',
      },
      stats: {
        layerNB: 0,
        mapNB: 0,
        userNB: 0,
        groupNB: 0,
      },
    }
  },
  created() {
    backApi
      .get('stats/total')
      .then(({ data }) => {
        this.stats = data
      })
      .catch(() => {})
  },
  methods: {
    onReady: function (instance, CountUp) {
      const that = this
      instance.update(that.endVal + 100)
    },
  },
}
</script>
<style scoped lang="scss">
.stats {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  flex-wrap: wrap;
  font-family: 'Poppins sans-serif';
  &__card {
    flex-basis: 24%;
    border-radius: 16px;
    height: 140px;
    padding: 1.5rem;
    display: flex;
    align-items: center;
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.1);
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 12px 20px rgba(0, 0, 0, 0.1);
    }

    &--purple {
      background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
    }
    &--teal {
      background: linear-gradient(135deg, #14b8a6 0%, #0d9488 100%);
    }
    &--green {
      background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
    }
    &--orange {
      background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
    }
    @media screen and (max-width: '996px') {
      margin-bottom: 1rem;
      flex-basis: 48%;
    }
    @media screen and (max-width: '660px') {
      margin-bottom: 1rem;
      flex-basis: 100%;
    }
  }
  &__detail {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  &__number {
    p {
      color: #fff;
      font-size: 2.2rem;
      font-weight: 700;
      margin-bottom: 0;
      line-height: 1;
    }
    h5 {
      color: rgba(255, 255, 255, 0.9);
      font-size: 1rem;
      text-transform: uppercase;
      letter-spacing: 1px;
      margin-top: 5px;
    }
  }
  &__icon-card {
    font-size: 2.8rem;
    opacity: 0.8;
    i {
      color: #fff;
    }
  }
}
</style>