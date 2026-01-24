<template>
  <div class="view-table" id="app">
    <div class="view-table__content">
      <div
        class="
          p-2
          bg-white
          text-dark
          m-1
          d-flex
          justify-content-between
          row-column
        "
      >
        <div>
          <b-select
            placeholder="Veuillez sélectionner une couche"
            name="layer"
            v-model="selectedSlugLayer"
            required
            validation-message="Ce champ est obligatoire"
            expanded
            style="100%"
          >
            <option v-for="l in layersSlug" :key="l.slug" :value="l.slug">
              {{ l.name }}
            </option>
          </b-select>
        </div>
        <div v-if="showTable" class="badges">
          <strong class="h4">Total </strong>
          <span class="p-badge p-badge-lg">{{ this.totalRecords }}</span>
        </div>
      </div>
      <div
        v-if="showTable"
        class="d-flex justify-content-between row-column p-2"
      >
        <div v-if="exportEntityElemeentPermission[selectedSlugLayer]">
          <b-dropdown aria-role="list">
            <button
              :class="['button', 'is-primary', { 'is-loading': isLoading }]"
              slot="trigger"
            >
              <span>Exporter</span>
              <b-icon icon="menu-down"></b-icon>
            </button>
            <b-dropdown-item
              @click="exportFeatures('geojson', 'geojson')"
              aria-role="listitem"
              >Exporter vers GeoJson</b-dropdown-item
            >
            <b-dropdown-item @click="exportFeatures('shp', 'zip')"
              >Exporter vers Shape</b-dropdown-item
            >
            <b-dropdown-item @click="exportFeatures('csv', 'csv')"
              >Exporter vers CSV</b-dropdown-item
            >
            <b-dropdown-item @click="exportFeatures('excel', 'xlsx')"
              >Exporter vers Excel</b-dropdown-item
            >
          </b-dropdown>
          <button
            @click="resetFilter"
            id="resetAll"
            type="button"
            :disabled="!dirty"
            class="button is-success"
          >
            <b-icon class="mr-1" pack="fas" icon="sync-alt" />Réinitialiser
          </button>
        </div>
        <div v-else>
          <button
            @click="resetFilter"
            id="resetAll"
            type="button"
            :disabled="!dirty"
            class="button is-success"
          >
            <b-icon class="mr-1" pack="fas" icon="sync-alt" />Réinitialiser
          </button>
        </div>
        <div style="flex: 0.3">
          <span style="width: 100%" class="p-input-icon-right">
            <i class="pi pi-search" />
            <InputText
              v-model="searchText"
              placeholder="Recherche globale..."
              style="width: 100%"
              ref="searchTextRef"
            />
          </span>
        </div>
      </div>
      <div
        id="table-wrapper"
        class="table-wrapper bg-white text-dark m-1 border border-info"
        v-if="showTable"
      >
        <DataTable
          id="viewtable"
          :value="getDataTable"
          class="p-datatable-striped p-datatable-md p-datatable-responsive"
          :lazy="true"
          :paginator="true"
          :rows="10"
          :totalRecords="totalRecords"
          :loading="loading"
          @page="onPage($event)"
          @sort="onSortNotViewed($event)"
          data-toggle="tooltip"
          :key="tableKey"
        >
          <template #empty> Aucun resultat trouvé. </template>
          <Column
            v-for="field of layerFields"
            :field="field.slug"
            :header="field.name"
            :key="field.id"
          >
            <template #filter>
              <InputText
                v-if="field.type == 'NUMBER'"
                oninput="this.value = this.value.replace(/[^0-9.]/g, '');
                this.value = this.value.replace(/(\..*)\./g, '$1');"
                :placeholder="`Chercher par ${field.name}`"
                v-model="filterCriterias[field.slug]"
                @keyup.enter="loadDataAsync"
              />
              <InputText
                v-else-if="field.type == 'INTEGER'"
                :placeholder="`Chercher par ${field.name}`"
                v-model="filterCriterias[field.slug]"
                type="number"
                @keyup.enter="loadDataAsync"
              />
              <v-select
                v-else-if="field.type === 'SELECT' && field.resource"
                :options="getOptions(field)"
                :name="field.name"
                v-model="filterCriterias[field.slug]"
                @input="onSelect($event, field)"
                label="text"
                :placeholder="`Charcher par ${field.name}`"
              >
                <div slot="no-options">Aucune option ici!</div>
              </v-select>
              <b-field v-else-if="field.type == 'BOOLEAN'">
                <b-checkbox-button
                  v-model="filterCriterias[field.slug]"
                  native-value="true"
                  type="is-success"
                  @input="loadDataAsync"
                >
                  <b-icon icon="check"></b-icon>
                  <span>Oui</span>
                </b-checkbox-button>
                <b-checkbox-button
                  v-model="filterCriterias[field.slug]"
                  native-value="false"
                  type="is-danger"
                  @input="loadDataAsync"
                >
                  <b-icon icon="close"></b-icon>
                  <span>Non</span>
                </b-checkbox-button>
              </b-field>
              <b-datetimepicker
                v-else-if="field.type == 'DATETIME'"
                v-model="filterCriterias[field.slug]"
                placeholder="Selectionner une date"
                icon="calendar-today"
                locale="fr-FR"
                editable
                @input="loadDataAsync"
                range
              >
              </b-datetimepicker>
              <b-datepicker
                v-else-if="field.type == 'DATE'"
                v-model="filterCriterias[field.slug]"
                placeholder="Selectionner une date"
                icon="calendar-today"
                editableb-datepicker
                @input="loadDataAsync"
                range
              >
              </b-datepicker>
              <b-timepicker
                v-else-if="field.type == 'TIME'"
                placeholder="Cliquez pour sélectionner..."
                icon="clock"
                v-model="filterCriterias[field.slug]"
                @input="loadDataAsync"
              >
              </b-timepicker>
              <InputText
                v-else
                type="text"
                :placeholder="`Chercher par ${field.name}`"
                v-model="filterCriterias[field.slug]"
                @keyup.enter="loadDataAsync"
              />
            </template>
            <template #body="slotProps">
              <span class="p-column-title"> {{ field.name }} </span>
              {{
                (field.type === 'NUMBER' || field.type === 'INTEGER') &&
                slotProps.data[field.slug]
                  ? slotProps.data[field.slug]
                      .toString()
                      .replace(/\B(?=(\d{3})+(?!\d))/g, ' ')
                  : field.type === 'BOOLEAN'
                  ? slotProps.data[field.slug] != null
                    ? slotProps.data[field.slug] == true ||
                      slotProps.data[field.slug] == true
                      ? 'Oui'
                      : 'Non'
                    : '/'
                  : slotProps.data[field.slug]
              }}
            </template>
          </Column>

          <Column
            headerStyle="width: 50px !important"
            field="action"
            header="Actions"
            key="action"
            class="actions"
          >
            <template #body="slotProps">
              <span class="p-column-title"> Actions </span>
              <div class="btn-action data-table__actions">
                <b-tooltip label="Aller à" type="is-dark" position="is-bottom">
                  <Button
                    icon="pi pi-map-marker"
                    class="p-button-rounded p-button-warning p-mr-2"
                    @click="viewEntityElement(slotProps.data)"
                  />
                </b-tooltip>
                <b-tooltip
                  label="Voir detail"
                  type="is-dark"
                  position="is-bottom"
                >
                  <Button
                    icon="pi pi-info"
                    class="p-button-rounded p-button-info p-mr-2"
                    @click="showDetail(slotProps.data.id, 'read')"
                  />
                </b-tooltip>
                <b-tooltip label="Modifer" type="is-dark" position="is-bottom">
                  <Button
                    v-if="
                      buttonPermission[slotProps.data.id] &&
                      buttonPermission[slotProps.data.id][
                        'ENTITY_ELEMENT_UPDATE_AUTHORITY'
                      ]
                    "
                    icon="pi pi-pencil"
                    class="p-button-rounded p-button-success p-mr-2"
                    @click="showDetail(slotProps.data.id, 'edit')"
                  />
                </b-tooltip>
                <b-tooltip
                  label="Supprimer"
                  type="is-dark"
                  position="is-bottom"
                >
                  <Button
                    v-if="
                      buttonPermission[slotProps.data.id] &&
                      buttonPermission[slotProps.data.id][
                        'ENTITY_ELEMENT_DELETE_AUTHORITY'
                      ]
                    "
                    icon="pi pi-trash"
                    class="p-button-rounded p-button-danger"
                    @click="deleteEntityElement(slotProps.data.id)"
                  />
                </b-tooltip>
              </div>
            </template>
          </Column>
        </DataTable>
      </div>
    </div>
    <div
      v-if="!isMobile"
      :class="`view-table__sidemenu ${!profile.authenticated && 'public'}`"
    >
      <div :key="featureSettingsKey" class="view-table__sidemenu-content">
        <FeatureSettings
          v-if="showFeatureSettings"
          :loadingIndicator="loadingIndicator"
          @save="$emit('onSave')"
          @cancel="showFeatureSettings = false"
          :newLayer="newLayer"
          :mode="mode"
          injectedIn="datatable"
          :toggleButton="true"
          @update="onUpdate"
          @close="showDetail(null)"
        />
      </div>
    </div>
    <div class="bottom-sheet" v-else :key="bottomSheetKey">
      <vue-bottom-sheet max-height="calc(100vh - 5.4rem)" ref="myBottomSheet">
        <FeatureSettings
          :key="featureSettingsKey"
          v-if="showFeatureSettings"
          :loadingIndicator="loadingIndicator"
          @save="$emit('onSave')"
          @cancel="showFeatureSettings = false"
          :newLayer="newLayer"
          :mode="mode"
          injectedIn="datatable"
          :toggleButton="false"
          @update="onUpdate"
          @close="showDetail(null)"
        />
      </vue-bottom-sheet>
    </div>
  </div>
</template>

<script>
import { backApi } from '~/methods/serverApi'
import { mapState, mapGetters } from 'vuex'

import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import InputText from 'primevue/inputtext'
import Button from 'primevue/button'
import Dropdown from 'primevue/dropdown'

import RestApi from '../../../methods/api'
import FeatureSettings from '~/components/viewer/FeatureSettings'

import VueBottomSheet from '@webzlodimir/vue-bottom-sheet'
import vSelect from 'vue-select'
import moment from 'moment'
export default {
  data: function () {
    return {
      dataTable: [],
      mode: this.$store.state.maps.mode,
      currentMap: this.$store.state.maps.currentMap,
      selectedSlugLayer: null,
      layerFields: [],
      layerSlugFields: { value: 'slug', text: 'name' },
      showTable: false,
      sortField: 'id',
      sortOrder: 'asc',
      defaultSortOrder: 'asc',
      perPage: 10,
      page: 0,
      totalRecords: 0,
      loading: false,
      searchText: '',
      resultCount: 0,
      isLoading: false,
      isFullPage: true,
      exportEntityElemeentPermission: {},
      showFeatureSettings: false,
      mode: 'read',
      newLayer: {},
      loadingIndicator: false,
      tableKey: 0,
      featureSettingsKey: 0,
      fieldsTypeSelect: [],
      filterCriterias: {},
      permmissions: {
        ENTITY_ELEMENT_UPDATE_AUTHORITY: false,
        ENTITY_ELEMENT_DELETE_AUTHORITY: false,
      },
      buttonPermission: {},
      bottomSheetKey: 1,
      query: {
        condition: 'or',
        rules: [],
      },
      dirty: false,
    }
  },
  computed: {
    ...mapState(['profile']),
    ...mapGetters({
      isMobile: 'app/getIsMobile',
    }),
    getDataTable() {
      let selectFieldsValues = null
      return this.dataTable.map((d) => {
        selectFieldsValues = {}
        this.fieldsTypeSelect.forEach((field) => {
          selectFieldsValues[field.slug] = RestApi.extractSelectedRV(
            d[field.slug]
          )
        })
        return {
          ...d,
          ...selectFieldsValues,
        }
      })
    },
    layersSlug() {
      return this.currentMap.layers.map((l) => {
        return {
          slug: l.slug,
          name: l.name.includes('____') ? l.name.split('____')[1] : l.name,
        }
      })
    },
    activeLayer() {
      return this.currentMap.layers.find(
        (layer) => layer.slug === this.selectedSlugLayer
      )
    },
  },
  components: {
    DataTable: DataTable,
    Column: Column,
    InputText: InputText,
    Dropdown: Dropdown,
    Button,
    FeatureSettings,
    VueBottomSheet,
    vSelect,
  },
  watch: {
    searchText(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.searchTextRef.value) {
          this.loadDataAsync()
        }
      }, 500)
    },
    selectedSlugLayer() {
      this.loadLayer()
      this.showDetail(null)
    },
  },
  methods: {
    getOptions(field) {
      if (
        field.parent &&
        this.filterCriterias[
          this.activeLayer.fields.find((f) => f.id === field.parent).slug
        ]
      ) {
        return field.resource.resourceValues
          .filter(
            (rv) =>
              rv.parentId ===
              this.filterCriterias[
                this.activeLayer.fields.find((f) => f.id === field.parent).slug
              ].value
          )
          .map((f) => {
            return { text: f.value, value: f.id }
          })
          .sort((a, b) => a.text.localeCompare(b.text))
      } else {
        return field.resource.resourceValues
          .map((f) => {
            return { text: f.value, value: f.id }
          })
          .sort((a, b) => a.text.localeCompare(b.text))
      }
    },
    onSelect(e, field) {
      this.filterCriterias[field.slug] = e
      this.loadDataAsync()
    },
    viewEntityElement(entityelement) {
      if (entityelement.coordinates) {
        this.$emit('goTo', {
          coords: entityelement.coordinates,
          topo: entityelement.type
            ? entityelement.type
            : entityelement.geometryType,
        })
      } else {
        this.$notification.warning({
          message: 'Remarque!',
          description: "Impossible d'accéder à cet enregistremente !",
        })
      }
    },
    onSortNotViewed(event) {
      this.sortField = event.sortField
      if (event.sortOrder == 1) this.sortOrder = 'desc'
      else this.sortOrder = 'asc'
      this.loadDataAsync()
    },
    showDetail(entityelementId, mode) {
      if (!this.loadingIndicator) {
        let entityelement = this.dataTable.find((d) => d.id === entityelementId)
        if (entityelement) {
          this.loadingIndicator = true
          let url = this.profile.authenticated
            ? `layers/withFieldsAndResource/maps/${this.currentMap.slug}/feature/${this.activeLayer.id}`
            : `layers/public/withFieldsAndResource/maps/${this.currentMap.slug}/feature/${this.activeLayer.id}`

          backApi
            .get(url)
            .then(({ data }) => {
              this.$store.commit('maps/setActiveLayer', data)
              this.newLayer = {
                feature: {
                  id: entityelement.id,
                  layer: this.activeLayer,
                  geometry: {
                    type: entityelement.geometryType,
                    coordinates: entityelement.coordinates,
                  },
                  properties: {
                    ...entityelement,
                    layer_entity_element: this.activeLayer.id,
                  },
                  type: 'Feature',
                },
                featureType: this.activeLayer.slug,
              }
              this.loadingIndicator = false
              if (this.isMobile) {
                this.openBottomSheet()
              } else {
                document.querySelector(
                  '.view-table__sidemenu'
                ).style.marginRight = 0
                document.querySelector(
                  '.view-table__content'
                ).style.marginRight = '27.5rem'
                let tableWrapperElem = document.getElementById('table-wrapper')
                tableWrapperElem.classList.add('adjust-width')
                setTimeout(() => {
                  tableWrapperElem.scrollLeft =
                    tableWrapperElem.scrollWidth + 100
                }, 500)
              }
              this.mode = mode
              this.featureSettingsKey++
              this.showFeatureSettings = true
            })
            .catch((e) => {
              this.loadingIndicator = false
              this.showFeatureSettings = false
              this.$notification.error({
                message: 'Error!',
                description: "Une erreur inattendue s'est produite !",
              })
            })
        } else {
          if (this.isMobile) {
            this.closeBottomSheet()
            this.showFeatureSettings = false
          } else {
            document.querySelector('.view-table__sidemenu').style.marginRight =
              '-28rem'
            document.querySelector('.view-table__content').style.marginRight =
              '0'
            document.getElementById('table-wrapper') &&
              document
                .getElementById('table-wrapper')
                .classList.remove('adjust-width')
          }
        }
      }
    },
    deleteEntityElement(id) {
      this.$confirm({
        title: "Êtes-vous sûr de supprimer ce point d'intérêt ?",
        content:
          'Cette action supprimera la fonctionnalité de la base de données.',
        okText: 'Oui',
        okType: 'danger',
        cancelText: 'Non',
        onOk: () => {
          this.isDeleteLoading = true
          RestApi.delete(
            'entityelements',
            `${id}/layers/${this.selectedSlugLayer}`,
            null,
            this.$router,
            this.$notification
          )
            .then((res) => {
              this.loadDataAsync()
              this.showDetail(null)
            })
            .catch((error) => {})
        },
        onCancel: () => {},
      })
    },
    openBottomSheet() {
      this.$refs.myBottomSheet.open()
    },
    closeBottomSheet() {
      this.$refs.myBottomSheet.close()
    },
    onUpdate(payload) {
      let index = this.dataTable.findIndex((l) => l.id === payload.id)
      let tmpObject = {}
      Object.entries(payload).forEach(([key, value]) => {
        if (key !== 'geometry') {
          let k =
            key === 'id'
              ? key
              : this.activeLayer.fields.find((f) => f.name === key).slug
          tmpObject[k] =
            key === 'id' ||
            this.activeLayer.fields.find((f) => f.name === key).type !==
              'CAROUSEL'
              ? value
              : value
              ? `[${value}]`
              : null
        }
      })
      this.dataTable.splice(index, 1, {
        ...tmpObject,
        coordinates: payload.geometry.coordinates,
        geometryType: payload.geometry.type
      })
      if (
        this.activeLayer.fields.some(
          (u) => u.type === 'IMAGE' || u.type === 'CAROUSEL'
        )
      ) {
        this.showDetail(null)
      } else {
        this.newLayer = {
          feature: {
            id: tmpObject.id,
            layer: this.activeLayer,
            geometry: payload.geometry,
            properties: {
              ...tmpObject,
              layer_entity_element: this.activeLayer.id,
            },
            type: 'Feature',
          },
          featureType: this.activeLayer.slug,
        }
      }
      this.tableKey++
    },
    getPermissions() {
      if (this.profile.authenticated) {
        for (let l of this.layersSlug) {

          if (this.profile.roles.includes('ROLE_ADMIN')) {
            this.exportEntityElemeentPermission[l.slug] = true
          } else {

            backApi
              .get(
                `layers/check-write-permission/${l.slug}/ENTITY_ELEMENT_MULTI_EXPORT_AUTHORITY`
              )
              .then(() => {

                this.exportEntityElemeentPermission[l.slug] = true
              })
              .catch(() => {
                this.exportEntityElemeentPermission[l.slug] = false
              })
          }
        }
      }
    },
    // getButtonPermissions(entityElementId) {
    //   if (this.profile.authenticated) {
    //     for (let p of Object.keys(this.permmissions)) {
    //       if (this.profile.roles.includes('ROLE_ADMIN')) {
    //         this.assignPermission(entityElementId, p, true)
    //       } else {
    //        setTimeout(() => {
    //           backApi
    //           .get(
    //             `layers/check-write-geometry-permission/${this.activeLayer.slug}/${p}?entity-element=${entityElementId}`
    //           )
    //           .then(() => {
    //             this.assignPermission(entityElementId, p, true)
    //           })
    //           .catch(() => {
    //             this.assignPermission(entityElementId, p, false)
    //           })
    //        }, 50)
    //       }
    //     }
    //   }
    // },
    assignPermission(entityElementId, permission, value) {
      let tmpObject = {}
      tmpObject[entityElementId] = {
        ...this.buttonPermission[entityElementId],
      }
      tmpObject[entityElementId][permission] = value
      this.buttonPermission = Object.assign(
        {},
        this.buttonPermission,
        tmpObject
      )
    },
    loadLayer() {
      this.layerFields = []
      this.totalRecords = 0
      this.showTable = false
      this.page = 0
      this.assignModels(true)
      this.loadDataAsync()
    },
    assignModels(init) {
      let filters = {}
      this.activeLayer.fields
        .filter(
          (f) => f.type != 'IMAGE' && f.type != 'CAROUSEL' && f.visible === true
        )
        .sort((a, b) => a.order - b.order)
        .forEach((f) => {
          filters[f.slug] = f.type === 'BOOLEAN' ? [] : ''
          if (init) this.layerFields.push(f)
        })
      this.filterCriterias = filters
    },
    onSort(field, order) {
      this.sortField = field
      this.sortOrder = order
      this.loadDataAsync()
    },
    onPage(event) {
      this.page = event.page
      this.loadDataAsync()
    },
    buildQuery() {
      let q = {
        condition: this.searchText ? 'or' : 'and',
        rules: [],
      }
      if (this.searchText != '') {
        this.dirty = true
      } else {
        this.dirty = false
      }
      let type = null
      for (const [key, value] of Object.entries(this.filterCriterias)) {
        type = this.activeLayer.fields.find((f) => f.slug === key).type
        if ((value && value.toString() != '') || this.searchText != '') {
          q.rules.push({
            field: key,
            label: key,
            operator:
              type === 'DATETIME' || type === 'DATE'
                ? 'between'
                : type === 'NUMBER' || type === 'INTEGER' || type === 'TIME'
                ? '='
                : 'ilike',
            type: type,
            value:
              this.searchText != ''
                ? type === 'INTEGER' || type === 'NUMBER'
                  ? parseInt(this.searchText)
                  : this.searchText.trim()
                : type === 'INTEGER' || type === 'NUMBER'
                ? parseInt(value)
                : type === 'BOOLEAN'
                ? value[value.length - 1]
                : type === 'SELECT'
                ? value.text ? value.text : value.trim()
                : type === 'DATETIME'
                ? ` '${moment(value[0]).format(
                    'YYYY-MM-DD H:mm:ss'
                  )}' and '${moment(value[1]).format('YYYY-MM-DD H:mm:ss')}'`
                : type === 'DATE'
                ? ` '${moment(value[0]).format('YYYY-MM-DD')}' and '${moment(
                    value[1]
                  ).format('YYYY-MM-DD')}'`
                : type === 'TIME'
                ? moment(value).format('H:mm:ss')
                : value.trim(),
          })
          if (type === 'BOOLEAN' && value.length === 2) {
            this.filterCriterias[key].shift()
          }
        }
      }
      this.query = q
    },
    resetFilter() {
      this.page = 0
      this.assignModels(false)
      if (this.searchText != '') {
        this.searchText = ''
      } else {
        this.loadDataAsync()
      }
      setTimeout(() => {
        document.getElementById('table-wrapper').scrollTo(0, 0)
      }, 500)
    },
    loadDataAsync() {
      this.$nextTick(() => {
        this.$nuxt.$loading.start()
      })
      this.buildQuery()

      let payload = {}
      if (this.query.rules.length > 0) {
        this.dirty = true
        payload = this.query
      } else {
        this.dirty = false
      }

      backApi
        .post(
          `entityelements/public/maps/${this.currentMap.slug}/search/${this.selectedSlugLayer}`,
          payload,
          {
            params: {
              page: this.page,
              limit: this.perPage,
              sort: this.sortField,
              dir: this.sortOrder,
              useFieldSlug: true,
            },
          }
        )
        .then(({ data }) => {
          if (data != null) {
            this.totalRecords = data.totalElements

            this.fieldsTypeSelect = this.activeLayer.fields.filter(
              (field) => field.type == 'SELECT'
            )
            this.dataTable = []
            let buttonPermission = []
            for (let element of data.content) {
              if (element.featureJson) {
                let props = JSON.parse(element.featureJson)
                if (props && props.properties) {
                  this.dataTable.push({
                    ...props.properties,
                    id: element.id,
                    geometryType: props.geometry && props.geometry.type,
                    coordinates: props.geometry && props.geometry.coordinates,
                  })
                  if(this.profile.roles.includes('ROLE_ADMIN')){
                    this.assignPermission(element.id, 'ENTITY_ELEMENT_UPDATE_AUTHORITY',true)
                    this.assignPermission(element.id, 'ENTITY_ELEMENT_DELETE_AUTHORITY',true)
                  }else{
                    buttonPermission.push(
                      {
                      layerSlug: this.activeLayer.slug,
                      permission: 'ENTITY_ELEMENT_UPDATE_AUTHORITY',
                      entityElementId: element.id,
                      isAllowed: false,
                    },
                    {
                      layerSlug: this.activeLayer.slug,
                      permission: 'ENTITY_ELEMENT_DELETE_AUTHORITY',
                      entityElementId: element.id,
                      isAllowed: false,
                    },
                    )
                  }

                }
              }
            }
            if(this.profile.authenticated){
              if(buttonPermission.length > 0){
              backApi.post(
                  `layers/check-write-geometry-permission`, buttonPermission

              ).then(({data}) => {
                data.forEach(p => {
                  this.assignPermission(p.entityElementId, p.permission, p.isAllowed)
                })

              })
            }
            }
            this.showTable = true
            setTimeout(() => {
              document
                .querySelectorAll('.p-datatable-wrapper tbody td')
                .forEach((cell) => {
                  cell.innerText.length > 35
                    ? cell.setAttribute('tooltip', cell.innerText)
                    : cell.removeAttribute('tooltip')
                })
            }, 100)
            this.$nuxt.$loading.finish()
            window.scrollTo(0, 0)
          }
        })
        .catch((error) => {
          this.dataTable = []
          this.totalRecords = 0
          this.$nuxt.$loading.finish()
        })
    },
    /*extractSelectedRV(rv) {
      if (rv != null) {
        let arr = rv.split(':')
        return arr[1]
      }
    },*/
    async exportFeatures(fileType, extension) {
      this.isLoading = true
      let payload = {}
      if (this.query.rules.length > 0) {
        payload = this.query
      }

      RestApi.exportData(
        this.selectedSlugLayer,
        fileType,
        extension,
        payload,
        this.$notification
      )
        .then(() => {
          this.isLoading = false
        })
        .catch((error) => {
          this.$notification.error({
            message: 'Erreur !',
            description:
              error.response && error.response.data
                ? error.response.data.message
                : "Une erreur inattendue s'est produite",
          })
          this.isLoading = false
        })
    },
  },
  beforeMount() {
    if (this.profile.authenticated) this.getPermissions()
    const tdb = document.getElementsByTagName('.p-datatable table')
    if (this.mode == 'layer') {
      this.selectedSlugLayer = this.currentMap.layers[0].slug
      this.loadLayer()
    }
  },
}
</script>

<style lang="scss">
.view-table {
  display: flex;
  > div:nth-child(1) {
    flex-grow: 1;
  }
  &__content {
    transition: all 0.3s ease-in-out;
  }
  &__sidemenu {
    position: fixed;
    transition: all 0.3s ease-in-out;
    right: 0;
    top: 6px;
    margin-right: -28rem;
    width: 27.5rem;
    height: 89%;
    margin-top: 132px;
    box-shadow: 0 0 20px #0000004d;
    @include respond('tab-port') {
      margin-top: 7.5rem;
      height: 92.5%;
    }
    &.public {
      margin-top: 60px;
      height: 93.5%;
      .view-table__sidemenu-content {
        height: 103% !important;
      }
    }
    &-content {
      position: relative;
      width: 100%;
      height: 100%;
    }
  }
}
#table-wrapper {
  margin-left: 10px !important;
  overflow-x: auto;
  width: 98.5vw;
  min-height: fit-content;
  transition: all 0.3s ease-in-out;
  // padding-bottom: 80px;
  @include respond('tab-port') {
    margin: 0 auto !important;
    width: 97vw;
    border: none !important;
  }
  input{
    border: 1px solid rgba(60,60,60,.26);
    border-radius: 4px;
  }
  // .p-paginator{
  //   position: fixed;
  //   width: 100vw;
  //   bottom: 20px;
  //   background-color: transparent;
  //   @include respond('tab-port') {
  //     position: static;
  //     bottom: unset;
  //   }
  // }
}
.adjust-width {
  width: calc(100vw - 29rem) !important;
}
.p-datatable table {
  position: relative;
  border-collapse: collapse;
  width: 100%;
  table-layout: auto !important;
}
#viewtable thead th {
  overflow: visible;
  min-width: 250px;
  span {
    line-height: 15px;
  }
}
#viewtable tbody > tr > td {
  line-height: 1.5;
  text-align: left;
  max-width: 250px;
  min-width: 250px;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}
#viewtable tbody > tr > td:last-child {
  overflow: visible;
}
#viewtable .vs__dropdown-toggle {
  min-height: 25px;
  position: relative;
  input {
    height: 25px;
    width: 0;
    border: none;
  }
  .vs__selected {
    margin-top: 4px;
  }
}
.badges {
  span {
    font-size: 1.7rem !important;
  }
  @include respond('phone') {
    margin-top: 1rem;
    margin-left: auto;
  }
}
</style>
