<template>
  <div class="chart">
    <div class="chart__title">
      <div class="chart__icon">
        <i class="fa fa-chart-line"></i>
      </div>
      <h3 class="chart__text">Statistiques des Couches</h3>
    </div>
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
    <bar-chart
      :key="barKey"
      :time="time"
      name="Couche"
      :data="graph"
    ></bar-chart>
    <div style="margin-top: 1rem" />
    <div class="wrapper">
      <div>
        <bar-chart
          :key="entityElementKey"
          time="layer"
          name="Point"
          :data="entityElements"
        ></bar-chart>
      </div>
      <div class="stat-table-wrapper">
        <b-table
          v-if="entityElements.length > 0"
          :data="entityElements"
          paginated
          backend-pagination
          :per-page="perPage"
          :total="size"
          :show-detail-icon="true"
          backend-sorting
          @page-change="getLayerStats"
        >
          <template slot-scope="props">
            <b-table-column field="name" label="Couche" sortable>{{
             props.row.name
            }}</b-table-column>
            <b-table-column field="Total" label="Total de Points" sortable>{{
              props.row.total - 1
            }}</b-table-column>
          </template>
        </b-table>
      </div>
    </div>
  </div>
</template>

<script>
import PieChart from './PieChart'
import LineChart from './LineChart'
import BarChart from './BarChart'
import { backApi } from '../../../methods/serverApi'
export default {
  components: {
    LineChart,
    PieChart,
    BarChart,
  },
  data() {
    return {
      totals: {},
      entityElements: [],
      graph: [],
      time: 'Mon',
      barKey: 1,
      pieKey: 1,
      entityElementKey: 1,
      perPage: 8,
      size: 0,
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
    this.getLayerStats(0)
    this.getStats()
  },
  methods: {
    getStats() {
      backApi
        .get(`stats?entity=layer&time=${this.time}`)
        .then(({ data }) => {
          this.graph = data
          this.barKey += 1
        })
        .catch(() => {})
    },
    getLayerStats(page) {
      backApi
        .get(`stats/entity-elements?page=${page - 1}&size=${this.perPage}`)
        .then(({ data }) => {
          this.entityElements = data.layerStats.map(l => {
            return {
              ...l,
              name: l.name.includes("____") ? l.name.split('____')[1] : l.name
            }
          })
          this.size = data.nbrTotal
          this.entityElementKey += 1
        })
        .catch(() => {})
    },
  },
}
</script>
<style lang="scss">
.stat-table-wrapper{
  padding: 0 1rem;
  @include respond('tab-port'){
    padding: 0
  }
}
</style>