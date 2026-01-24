<template>
  <div class="chart">
    <div class="chart__title">
      <div class="chart__icon">
        <i class="fa fa-chart-line"></i>
      </div>
      <h3 class="chart__text">Statistiques des Utilisateurs</h3>
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
          name="utilisiteur"
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
      .get('stats/users-status')
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
        .get(`stats?entity=user&time=${this.time}`)
        .then(({ data }) => {
          this.graph = data
          this.lineKey += 1
        })
        .catch(() => {})
    },
  },
}
</script>