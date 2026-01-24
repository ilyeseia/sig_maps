<template>
  <div class="chart">
    <div class="chart__title">
      <div class="chart__icon">
        <i class="fa fa-chart-line"></i>
      </div>
      <h3 class="chart__text">Statistiques Cartographiques</h3>
    </div>
    <div class="wrapper">
      <div>
        <b-button
          @click="time = 'Mon'"
          :type="{ 'is-primary-light': time === 'Mon' ? true : false }"
          >Par moi</b-button
        >
        <b-button
          @click="time = 'Year'"
          :type="{ 'is-primary-light': time === 'Year' ? true : false }"
          >Par année</b-button
        >
        <line-chart
          :key="lineKey"
          :time="time"
          name="Carte"
          :data="graph"
        ></line-chart>
      </div>
      <div>
        <pie-chart :key="pieKey" :data="totals"></pie-chart>
      </div>
    </div>
  </div>
</template>

<script>
import PieChart from './PieChart'
import LineChart from './LineChart'
import { backApi } from '../../../methods/serverApi'
export default {
  components: {
    LineChart,
    PieChart,
  },
  data() {
    return {
      totals: {},
      graph: [],
      time: 'Mon',
      lineKey: 1,
      pieKey: 1,
    }
  },
  watch: {
    time(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.getStats()
      }
    },
  },
  created() {
    backApi
      .get('stats/map')
      .then(({ data }) => {
        this.totals = data
        this.pieKey += 1
      })
      .catch(() => {})
    this.getStats()
  },
  methods: {
    getStats() {
      backApi
        .get(`stats?entity=map&time=${this.time}`)
        .then(({ data }) => {
          this.graph = data
          this.lineKey += 1
        })
        .catch(() => {})
    },
  },
}
</script>

<style lang="scss">
.chart {
  border-radius: 8px;
  padding: 1.5rem;
  background-color: #fff;
  box-shadow: 0 10px 15px -5px #3e396b12;
  margin-bottom: 2rem;
  @include respond('phone'){
    padding: 1rem .8rem;
  }
  &__title {
    display: flex;
    align-items: center;
    margin-bottom: 1.5rem;
  }
  &__icon {
    width: 2.75rem;
    border: 1px solid rgb(231, 239, 248);
    height: 2.75rem;
    background: rgb(241, 248, 254);
    box-shadow: 0 2px 15px -5px $color-primary;
    text-align: center;
    line-height: 2.75rem;
    margin-right: 1.5rem;
    border-radius: 8px;
    vertical-align: middle;
    i {
      color: $color-primary;
      font-size: 1.15rem;
    }
  }
  &__text {
    color: $color-primary;
    position: relative;
    font-size: 1.5rem;
    font-weight: 400;
  }
  .wrapper {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    @media screen and (max-width: '996px') {
      flex-direction: column;
    }
    overflow: hidden;
    > div:nth-child(1) {
      position: relative;
      flex-basis: 68%;
      min-width: 0;
      @media screen and (max-width: '996px') {
        width: 100%;
      }
    }
    > div:nth-child(2) {
      position: relative;
      flex-basis: 30%;
      @media screen and (max-width: '996px') {
        width: 100%;
      }
    }
  }
}
.is-primary-light {
  background-color: #effaf3;
  color: $color-primary;
  border-color: transparent;
  &:hover{
    color: $color-primary;
    border-color: transparent;
  }
  &:focus{
    color: $color-primary;
    border-color: transparent;
  }
}
</style>