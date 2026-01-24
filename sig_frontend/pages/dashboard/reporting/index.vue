<template>
  <div class="w-100">
    <div class="p-4">
      <Panel icon="fa fa-chart-bar" viewMode="normal">
        <template #title> Reporting </template>
        <template #default>
          <div>
            <div class="reporting-header">
              <v-select
                style="margin: 1rem 0"
                :options="getLayers"
                :value="selectedLayer"
                @input="onSelect"
                label="text"
                class="layers-select-box"
                placeholder="Veuillez sélectionner une couche"
              >
                <div slot="no-options">Aucune option ici!</div>
              </v-select>
              <div>
                <button
                  id="upload-new-layer"
                  type="is-info"
                  class="button is-primary mb-2"
                  @click="showModal = true"
                >
                  <b-icon class="mr-1" pack="fas" icon="filter"></b-icon>Mes
                  filtres
                </button>
                <button
                  v-if="layerData.length > 0"
                  id="upload-new-layer"
                  type="is-info"
                  class="button mb-2"
                  @click="createNewFilter"
                >
                  <b-icon class="mr-1" pack="fas" icon="plus"></b-icon>Ajouter
                  filtre
                </button>
              </div>
            </div>
            <div v-if="showFilterSection">
              <div class="filter-section">
                <form @submit.prevent="onSubmit">
                  <div class="fields">
                    <b-field label="Nom">
                      <b-input
                        id="filter-name"
                        name="name"
                        type="text"
                        :value="selectedFilter ? selectedFilter.name : ''"
                        placeholder="Nom de filtre"
                        :disabled="
                          selectedFilter.filterClonedFrom &&
                          selectedFilter.filterClonedFrom[0] == null
                        "
                        required
                        validation-message="Veuillez remplir ce champ"
                      ></b-input>
                    </b-field>
                    <b-field label="Description">
                      <b-input
                        id="filter-description"
                        name="description"
                        style="min-height: 80px"
                        :value="
                          selectedFilter ? selectedFilter.description : ''
                        "
                        type="textarea"
                        placeholder="Description de filtre"
                      ></b-input>
                    </b-field>
                  </div>
                  <div class="actions">
                    <div>
                      <button
                        v-if="
                          Object.keys(selectedFilter).length === 0 ||
                          (selectedFilter.filterClonedFrom &&
                            selectedFilter.filterClonedFrom[0] != null)
                        "
                        type="submit"
                        :class="[
                          'button',
                          'is-primary',
                          { 'is-loading': isLoading },
                        ]"
                      >
                        <span class="icon is-small">
                          <i class="fas fa-save"></i>
                        </span>
                        <label
                          style="margin-bottom: 0; cursor: pointer"
                          for="savefilter"
                          >Enregistrer</label
                        >
                      </button>
                      <input
                        hidden
                        type="radio"
                        id="savefilter"
                        name="filter"
                        value="savefilter"
                      />
                      <input
                        hidden
                        type="radio"
                        id="newfilter"
                        name="filter"
                        value="newfilter"
                      />
                      <button
                        v-if="
                          selectedFilter.filterClonedFrom &&
                          selectedFilter.filterClonedFrom[0] != null
                        "
                        type="submit"
                        :class="[
                          'button',
                          { 'is-loading': newFilterIsLoading },
                        ]"
                      >
                        <span class="icon is-small">
                          <i class="far fa-save"></i>
                        </span>
                        <label
                          style="margin-bottom: 0; cursor: pointer"
                          for="newfilter"
                          >Enregistrer comme un nouveau filtre</label
                        >
                      </button>
                    </div>
                    <div
                      v-if="
                        selectedFilter.filterClonedFrom &&
                        selectedFilter.filterClonedFrom[0] != null
                      "
                    >
                      <b-tooltip
                        label="Partager"
                        type="is-dark"
                        position="is-bottom"
                      >
                        <button
                          v-if="Object.keys(selectedFilter).length > 0"
                          type="button"
                          style="margin-right: 0; background: #6c757d"
                          @click="shareFilter(selectedFilter)"
                          class="button"
                        >
                          <span style="color: white" class="icon is-small">
                            <i class="fas fa-share-alt"></i>
                          </span>
                        </button>
                      </b-tooltip>
                      <b-tooltip
                        label="Supprimer"
                        type="is-dark"
                        position="is-bottom"
                      >
                        <button
                          v-if="Object.keys(selectedFilter).length > 0"
                          type="button"
                          style="margin-right: 0"
                          @click="deleteFilter(selectedFilter.id)"
                          :class="[
                            'button',
                            'is-danger',
                            { 'is-loading': deleteIsLoading },
                          ]"
                        >
                          <span class="icon is-small">
                            <i class="fas fa-trash"></i>
                          </span>
                        </button>
                      </b-tooltip>
                    </div>
                  </div>
                </form>
              </div>
              <hr />
            </div>
            <button
              v-if="
                config.rendererName &&
                config.rendererName.includes('Table') &&
                layerData.length > 0 &&
                (profile.roles.includes('ROLE_ADMIN') ||
                  profile.roles.includes(
                    'ENTITY_ELEMENT_MULTI_EXPORT_AUTHORITY'
                  ))
              "
              id="upload-new-layer"
              type="is-info"
              class="button is-primary mb-2"
              style="float: right; margin-top: 0.5rem"
              @click="exportData"
            >
              <b-icon class="mr-1" pack="fas" icon="database"></b-icon>Exporter
            </button>
            <div style="clear: both; display: table; content: ''"></div>
            <div class="pivot-table-wrapper">
              <vue-pivottable-ui
                v-model="config"
                :data="layerData"
                :localeStrings="localeStrings[locale]"
                :rendererName="rendererName"
                :aggregatorName="aggregatorName"
                :tableColorScaleGenerator="colorScaleGenerator"
                :attributes="attributes"
                :valueFilter="valueFilter"
                :rows="rows"
                :cols="cols"
                :vals="vals"
                :disabledFromDragDrop="disabledFromDragDrop"
                :sortonlyFromDragDrop="sortonlyFromDragDrop"
                :hiddenFromDragDrop="hiddenFromDragDrop"
                rowOrder="value_a_to_z"
              >
                <colgroup slot="colGroup">
                  <col :width="300" />
                  <col />
                </colgroup>
                <div v-if="loading" slot="output">Chargement en cours...</div>
                <template
                  v-if="!loading"
                  slot="output"
                  slot-scope="{ pivotData }"
                >
                  {{ pivotData }}
                </template>
                <template slot="pvtAttr" slot-scope="{ name }">
                  {{ name }}
                </template>
              </vue-pivottable-ui>
            </div>
          </div>
        </template>
      </Panel>
    </div>
    <b-modal :active.sync="showModal" has-modal-card>
      <Table
        @closeModal="closeModal"
        @setSelectedFilter="setSelectedFilter"
        @deleteFilter="deleteFilter"
        @shareFilter="shareFilter"
      />
    </b-modal>
    <b-modal :active.sync="showShareModal" has-modal-card>
      <ShareFilter
        @closeShareFilterModal="closeShareFilterModal"
        :filter="selectedFilter"
      />
    </b-modal>
  </div>
</template>

<script>
import vSelect from 'vue-select'
import 'vue-select/dist/vue-select.css'
import { VuePivottableUi, PivotUtilities } from 'vue-pivottable'
import 'vue-pivottable/dist/vue-pivottable.css'
import { scaleLinear } from 'd3-scale'
import Panel from '../../../components/layout/Panel'
import { backApi } from '../../../methods/serverApi'
import { mapState } from 'vuex'
import TableToExcel from '@linways/table-to-excel'
import Table from '../../../components/reporting/filter/Table'
import ShareFilter from '../../../components/reporting/filter/ShareFilter'
import pageTitle from '~/mixins/page-title'

export default {
  mixins: [pageTitle],
  components: {
    VuePivottableUi,
    Panel,
    vSelect,
    Table,
    ShareFilter,
  },
  name: 'app',
  data() {
    return {
      selectedLayer: null,
      layers: [],
      layerData: [],
      showFilterSection: false,
      isLoading: false,
      newFilterIsLoading: false,
      deleteIsLoading: false,
      valueFilter: {},
      config: {},
      filteredData: [],
      selectedFilter: {},
      data: [],
      attributes: [],
      rows: [],
      cols: [],
      vals: ['Total'],
      disabledFromDragDrop: [], // ['Payer Gender'],
      hiddenFromDragDrop: ['Total'],
      sortonlyFromDragDrop: [], // ['Party Size'],
      pivotColumns: [],
      loading: false,
      aggregatorName: 'Count',
      rendererName: 'Table',
      showModal: false,
      showShareModal: false,
      localeStrings: {
        en: {
          renderError: 'An error occurred rendering the PivotTable results.',
          computeError: 'An error occurred computing the PivotTable results.',
          uiRenderError: 'An error occurred rendering the PivotTable UI.',
          selectAll: 'Select All',
          selectNone: 'Select None',
          tooMany: 'too many values to show',
          filterResults: 'Filter values',
          totals: 'Totals',
          only: 'only',
          rendererNames: {
            Table: 'Table',
            'Table Heatmap': 'Table Heatmap',
            'Table Col Heatmap': 'Table Col Heatmap',
            'Table Row Heatmap': 'Table Row Heatmap',
            'Export Table TSV': 'Export Table TSV',
            'Grouped Column Chart': 'Grouped Column Chart',
            'Stacked Column Chart': 'Stacked Column Chart',
            'Grouped Bar Chart': 'Grouped Bar Chart',
            'Stacked Bar Chart': 'Stacked Bar Chart',
            'Line Chart': 'Line Chart',
            'Dot Chart': 'Dot Chart',
            'Area Chart': 'Area Chart',
            'Scatter Chart': 'Scatter Chart',
            'Multiple Pie Chart': 'Multiple Pie Chart',
          },
          aggregatorMap: {
            Count: 'Count',
            'Count Unique Values': 'Count Unique Values',
            'List Unique Values': 'List Unique Values',
            Sum: 'Sum',
            'Integer Sum': 'Integer Sum',
            Average: 'Average',
            Median: 'Median',
            'Sample Variance': 'Sample Variance',
            'Sample Standard Deviation': 'Sample Standard Deviation',
            Minimum: 'Minimum',
            Maximum: 'Maximum',
            First: 'First',
            Last: 'Last',
            'Sum over Sum': 'Sum over Sum',
            'Sum as Fraction of Total': 'Sum as Fraction of Total',
            'Sum as Fraction of Rows': 'Sum as Fraction of Rows',
            'Sum as Fraction of Columns': 'Sum as Fraction of Columns',
            'Count as Fraction of Total': 'Count as Fraction of Total',
            'Count as Fraction of Rows': 'Count as Fraction of Rows',
            'Count as Fraction of Columns': 'Count as Fraction of Columns',
          },
        },
        fr: {
          renderError:
            "Une erreur s'est produite lors du rendu des résultats du tableau croisé dynamique.",
          computeError:
            "Une erreur s'est produite lors du calcul des résultats du tableau croisé dynamique.",
          uiRenderError:
            "Une erreur s'est produite lors du rendu de l'interface utilisateur du tableau croisé dynamique.",
          selectAll: 'Sélectionner tout',
          selectNone: 'Ne rien sélectionner',
          tooMany: 'Trop de valeurs à afficher',
          filterResults: 'Filtrer les valeurs',
          totals: 'Totaux',
          only: 'Seule',
          rendererMap: {
            Table: 'Table',
            'Table Heatmap': 'Table Heatmap',
            'Table Col Heatmap': 'Table Col Heatmap',
            'Table Row Heatmap': 'Table Row Heatmap',
            'Export Table TSV': 'Export Table TSV',
            'Grouped Column Chart': 'Grouped Column Chart',
            'Stacked Column Chart': 'Stacked Column Chart',
            'Grouped Bar Chart': 'Grouped Bar Chart',
            'Stacked Bar Chart': 'Stacked Bar Chart',
            'Line Chart': 'Line Chart',
            'Dot Chart': 'Dot Chart',
            'Area Chart': 'Area Chart',
            'Scatter Chart': 'Scatter Chart',
            'Multiple Pie Chart': 'Multiple Pie Chart',
          },
          aggregatorMap: {
            Count: 'Compter',
            'Count Unique Values': 'Compter les valeurs uniques',
            'List Unique Values': 'Répertorier les valeurs uniques',
            Sum: 'Somme',
            'Integer Sum': 'Somme entière',
            Average: 'Moyenne',
            Median: 'Médiane',
            'Sample Variance': 'Échantillon de variance',
            'Sample Standard Deviation': "Écart type d'échantillon",
            Minimum: 'Minimum',
            Maximum: 'Maximum',
            First: 'Premier',
            Last: 'Dernier',
            'Sum over Sum': 'Somme sur somme',
            'Sum as Fraction of Total': 'Somme en tant que fraction du total',
            'Sum as Fraction of Rows': 'Somme en tant que fraction de lignes',
            'Sum as Fraction of Columns':
              'Somme en tant que fraction de colonnes',
            'Count as Fraction of Total': 'Compter comme une fraction du total',
            'Count as Fraction of Rows': 'Compter comme une fraction de lignes',
            'Count as Fraction of Columns':
              'Compter comme une fraction de colonnes',
          },
        },
      },
      locale: 'fr',
      page: {
        title: 'Reporting',
        hid: '',
        name: '',
        description: '',
      },
    }
  },
  computed: {
    ...mapState(['profile']),
    getLayers() {
      return this.profile.layers.map((l) => {
        return {
          value: l.slug,
          text:
            l.name && l.name.includes('____')
              ? l.name.split('____')[1]
              : l.name,
        }
      })
    },
    unusedAttrs() {
      return this.config.unusedAttrs
    },
    aggregators() {
      const usFmt = PivotUtilities.numberFormat()
      // const usFmtInt = PivotUtilities.numberFormat({ digitsAfterDecimal: 0 })
      // const usFmtPct = PivotUtilities.numberFormat({
      //   digitsAfterDecimal: 1,
      //   scaler: 100,
      //   suffix: '%'
      // })
      return ((tpl) => ({
        // 'Count': tpl.count(usFmtInt),
        // 'Count Unique Values': tpl.countUnique(usFmtInt),
        // 'List Unique Values': tpl.listUnique(', '),
        Sum: tpl.sum(usFmt),
        // 'Integer Sum': tpl.sum(usFmtInt),
        // 'Average': tpl.average(usFmt),
        // 'Median': tpl.median(usFmt),
        // 'Sample Variance': tpl.var(1, usFmt),
        // 'Sample Standard Deviation': tpl.stdev(1, usFmt),
        // 'Minimum': tpl.min(usFmt),
        // 'Maximum': tpl.max(usFmt),
        // 'First': tpl.first(usFmt),
        // 'Last': tpl.last(usFmt),
        // 'Sum over Sum': tpl.sumOverSum(usFmt),
        // 'Sum as Fraction of Total': tpl.fractionOf(tpl.sum(), 'total', usFmtPct),
        // 'Sum as Fraction of Rows': tpl.fractionOf(tpl.sum(), 'row', usFmtPct),
        // 'Sum as Fraction of Columns': tpl.fractionOf(tpl.sum(), 'col', usFmtPct),
        // 'Count as Fraction of Total': tpl.fractionOf(tpl.count(), 'total', usFmtPct),
        // 'Count as Fraction of Rows': tpl.fractionOf(tpl.count(), 'row', usFmtPct),
        // 'Count as Fraction of Columns': tpl.fractionOf(tpl.count(), 'col', usFmtPct)
      }))(PivotUtilities.aggregatorTemplates)
    },
    renderers() {
      const TableRenderer = Renderer.TableRenderer
      // const PlotlyRenderer = Renderer.PlotlyRenderer
      return (() => ({
        Table: TableRenderer.Table,
        'Table Heatmap': TableRenderer['Table Heatmap'],
        'Table Col Heatmap': TableRenderer['Table Col Heatmap'],
        'Table Row Heatmap': TableRenderer['Table Row Heatmap'],
        'Export Table TSV': TableRenderer['Export Table TSV'],
        // 'Grouped Column Chart': PlotlyRenderer['Grouped Column Chart'],
        // 'Stacked Column Chart': PlotlyRenderer['Stacked Column Chart'],
        // 'Grouped Bar Chart': PlotlyRenderer['Grouped Bar Chart'],
        // 'Stacked Bar Chart': PlotlyRenderer['Stacked Bar Chart'],
        // 'Line Chart': PlotlyRenderer['Line Chart'],
        // 'Dot Chart': PlotlyRenderer['Dot Chart'],
        // 'Area Chart': PlotlyRenderer['Area Chart'],
        // 'Scatter Chart': PlotlyRenderer['Scatter Chart'],
        // 'Multiple Pie Chart': PlotlyRenderer['Multiple Pie Chart']
      }))()
    },
  },
  methods: {
    getLayerData() {
      this.$nuxt.$loading.start()
      if (Object.keys(this.selectedFilter).length === 0) {
        this.rows = []
        this.cols = []
        this.vals = []
        this.rendererName = 'Table'
        this.aggregatorName = 'Count'
        this.vals = ['Total']
        Object.assign(this.config, {})
      }
      backApi
        .get(`entityelements/layers/${this.selectedLayer.value}`)
        .then(({ data }) => {
          if (data && data.length > 0) {
            let fieldWithSelectType = []
            for (const [key, value] of Object.entries(data[data.length - 1])) {
              if (value === 'SELECT') {
                fieldWithSelectType.push(key)
              }
            }
            this.layerData = data.slice(0, -1).map((d) => {
              if (fieldWithSelectType.length > 0) {
                let row = {}
                fieldWithSelectType.forEach((f) => {
                  for (const [key, value] of Object.entries(d)) {
                    row[key] =
                      value && value.split(':').length > 1
                        ? value.split(':')[1]
                        : value
                  }
                })
                return row
              } else {
                return d
              }
            })
          }
          this.$nuxt.$loading.finish()
        })
        .catch((error) => {
          this.$nuxt.$loading.finish()
          this.$notification.error({
            message: 'Erreur !',
            description:
              error.response && error.response.data
                ? error.response.data.message
                : "Une erreur inattendue s'est produite",
          })
        })
    },
    colorScaleGenerator(values) {
      const scale = scaleLinear()
        .domain([0, Math.max.apply(null, values)])
        .range(['#fff', '#77f'])
      return (x) => {
        return { 'background-color': scale(x) }
      }
    },
    exportData() {
      if (document.getElementsByClassName('pvtOutput')[0] !== undefined) {
        TableToExcel.convert(document.getElementsByClassName('pvtOutput')[0], {
          name: `${this.selectedLayer.value}.xlsx`,
          sheet: {
            name: 'Sheet 1',
          },
        })
      }
    },
    onSubmit(e) {
      if (!this.selectedLayer) {
        this.$notification.error({
          message: 'Erreur !',
          description: 'Veuillez sélectionner une couche !',
        })
        return
      }
      const formData = new FormData(e.target)
      const newFilter = {}

      if (formData.get('filter') === 'newfilter') {
        this.newFilterIsLoading = true
      } else {
        this.isLoading = true
      }

      newFilter.name = formData.get('name')
      newFilter.description = formData.get('description')
      let filterConfig = {
        rows: this.rows,
        cols: this.cols,
        aggregatorName: this.config.aggregatorName,
        rendererName: this.config.rendererName,
        vals: this.config.vals,
        layer: this.profile.layers.find(l => l.slug === this.selectedLayer.value).id
      }
      newFilter.filterConfig = JSON.stringify(filterConfig)
      if (
        formData.get('filter') === 'newfilter' ||
        this.selectedFilter.id === undefined
      ) {
        backApi
          .post(`filters/layers/${this.selectedLayer.value}`, newFilter)
          .then(({ data }) => {
            if (formData.get('filter') === 'newfilter') {
              this.newFilterIsLoading = false
            } else {
              this.isLoading = false
            }
            filterConfig.layer = this.selectedLayer
            this.selectedFilter = {
              ...data, 
              filterConfig
            }
            this.selectedFilter.filterClonedFrom = [data.id]
            this.$notification.success({
              message: 'Success !',
              description: 'Le filtre est enregistré avec success',
            })
          })
          .catch((e) => {
            if (formData.get('filter') === 'newfilter') {
              this.newFilterIsLoading = false
            } else {
              this.isLoading = false
            }
            this.isLoading = false
            this.$notification.error({
              message: 'Erreur !',
              description:
                error.response && error.response.data
                  ? error.response.data.message
                  : "Une erreur inattendue s'est produite",
            })
          })
      } else {
        this.selectedFilter.name = formData.get('name')
        this.selectedFilter.description = formData.get('description')
        this.selectedFilter.filterConfig = newFilter.filterConfig
        backApi
          .put(`filters`, this.selectedFilter)
          .then(() => {
            this.isLoading = false
            this.$notification.success({
              message: 'Success !',
              description: 'Le filtre est enregistré avec success',
            })
          })
          .catch((e) => {
            this.isLoading = false
            this.$notification.error({
              message: 'Erreur !',
              description:
                error.response && error.response.data
                  ? error.response.data.message
                  : "Une erreur inattendue s'est produite",
            })
          })
      }
    },
    deleteFilter(id) {
      this.$confirm({
        title: 'Supprission de filtre',
        style: ' top: 20',
        content: 'Voulez vous supprimer ce filter ?',
        okText: 'Oui',
        okType: 'danger',
        cancelText: 'Non',
        zIndex: 5000,
        onOk: () => {
          this.deleteIsLoading = true
          backApi
            .delete(`filters/${id}`)
            .then(() => {
              this.deleteIsLoading = false
              this.showModal = false
              this.showFilterSection = false
              this.selectedFilter = {}
              this.$notification.success({
                message: 'Success !',
                description: 'Le filtre est  supprimé avec success',
              })
            })
            .catch((e) => {
              this.deleteIsLoading = false
              this.showModal = false
              this.$notification.error({
                message: 'Erreur !',
                description:
                  error.response && error.response.data
                    ? error.response.data.message
                    : "Une erreur inattendue s'est produite",
              })
            })
        },
        onCancel: () => {},
      })
    },
    createNewFilter() {
      this.showFilterSection = true
      this.selectedFilter = {}
    },
    setSelectedFilter(filter) {
      this.selectedFilter = filter
      this.showModal = false
    },
    onSelect(e) {
      this.selectedFilter = {}
      this.selectedLayer = e
    },
    closeModal() {
      this.showModal = false
    },
    closeShareFilterModal() {
      this.showShareModal = false
    },
    shareFilter(filter) {
      this.selectedFilter = filter
      this.showShareModal = true
    },
  },
  watch: {
    selectedLayer(newVal) {
      if (newVal) {
        this.getLayerData()
      }
    },
    selectedFilter(newVal) {
      if (Object.keys(newVal).length > 0) {
        this.selectedLayer = newVal.filterConfig.layer
        this.rows = newVal.filterConfig.rows
        this.cols = newVal.filterConfig.cols
        this.aggregatorName = newVal.filterConfig.aggregatorName
        this.rendererName = newVal.filterConfig.rendererName
        this.vals = newVal.filterConfig.vals
        this.showFilterSection = true
      }
    },
    cols(newVal) {
      if (
        Object.keys(this.selectedFilter).keys === 0 &&
        newVal.length === 0 &&
        this.rows.length === 0
      ) {
        this.showFilterSection = false
      }
    },
    rows(newVal) {
      if (
        Object.keys(this.selectedFilter).keys === 0 &&
        newVal.length === 0 &&
        this.cols.length === 0
      ) {
        this.showFilterSection = false
      }
    },
    config: {
      handler(value, oldValue) {
        const PivotData = PivotUtilities.PivotData
        if (
          value.cols.indexOf('Unused 1') > -1 ||
          value.rows.indexOf('Unused 1') > -1
        ) {
          this.data = this.layerData
          this.filteredData = new PivotData(value).getFilteredData()
        } else {
          this.data = this.layerData
          this.filteredData = new PivotData(value).getFilteredData()
        }
      },
      deep: true,
      immediate: false,
    },
  },
  mounted() {
    this.$nextTick(() => {
      this.$nuxt.$loading.start()
      setTimeout(() => this.$nuxt.$loading.finish(), 300)
    })
  },
}
</script>

<style  lang="scss">
.reporting-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  @media screen and (max-width: '776px') {
    flex-direction: column;
    align-items: flex-end;
  }
}
.main {
  max-width: 980px;
  margin: 8vh auto 20px;
}
.title {
  text-align: center;
  margin-bottom: 20px;
}
h1 {
  margin-bottom: 0px;
}
.table-responsive {
  display: block;
  width: 100%;
  overflow-x: auto;
}
pre {
  text-align: left;
  background-color: #f8f8f8;
  padding: 1.2em 1.4em;
  line-height: 1.5em;
  margin: 60px 0 0;
  overflow: auto;
}
code {
  padding: 0;
  margin: 0;
}
.pvtDropdown {
  max-width: 85% !important;
}
.layers-select-box {
  width: 40%;
  @include respond('phone') {
    width: 100%;
  }
}
.filter-section {
  width: 40%;
  @include respond('tab-port') {
    width: 100%;
  }
  .actions {
    margin-top: 1rem;
    display: flex;
    justify-content: space-between;
    @include respond('phone') {
      > div {
        margin-top: 1rem;
      }
    }
  }
}
.pivot-table-wrapper{
  @include respond('tab-port'){
    overflow-x: scroll;
  }
}
</style>