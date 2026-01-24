<template>
  <section class="layers">
    <div id="layers" class="content-wrapper">
      <b-loading :active="loaderIndicator" :is-full-page="false"> </b-loading>
      <!-- Start Button Action  -->
      <div class="btn-wrapper">
        <div class="d-flex align-items-start btn-action">
          <b-tooltip
            label="Ajouter des couches"
            type="is-dark"
            position="is-bottom"
          >
            <button
              v-if="
                mode == 'map' &&
                currentMap.privacy != 'ARCHIVED' &&
                (profile.roles.includes('ROLE_ADMIN') ||
                  profile.roles.includes('ATTACH_LAYER_MAP_AUTHORITY'))
              "
              id="add-layer"
              type="button"
              class="button is-primary is-pulled-left"
              @click="addLayers"
              style="margin-right: 5px"
            >
              <b-icon pack="fas" icon="plus"></b-icon>
            </button>
          </b-tooltip>
          <b-tooltip
            label="Imprimer la carte"
            type="is-dark"
            position="is-bottom"
          >
            <button
              id="print-layer"
              type="button"
              style="margin-right: 5px"
              :class="[
                'button',
                'is-primary',
                { 'is-loading': isPrintLoading },
              ]"
              @click="getMapImage"
            >
              <b-icon pack="fas" icon="print"></b-icon>
            </button>
          </b-tooltip>
          <b-tooltip
            label="Changer le thème"
            type="is-dark"
            position="is-left"
            v-if="
              mode == 'map' &&
              currentMap.privacy != 'ARCHIVED' &&
              (profile.roles.includes('ROLE_ADMIN') ||
                profile.roles.includes('CONFIGURE_LAYER_STYLE_AUTHORITY'))
            "
          >
            <b-dropdown aria-role="list">
              <button
                id="switch-map-themes"
                type="button"
                :class="['button', 'is-danger']"
                slot="trigger"
                @click="getMapThemes"
              >
                <b-icon pack="fas" icon="random"></b-icon>
              </button>
              <b-loading :active="loadingIndicatorThemes" :is-full-page="false">
              </b-loading>
              <b-dropdown-item
                v-for="mt in mapThemes"
                :key="mt.id"
                :class="{ active: mt.isDefault }"
              >
                <div style="width: 100%" class="d-flex justify-content-between">
                  <span
                    class="theme-name"
                    v-if="mt.name.length > 15"
                    :tooltip="mt.name"
                    @click="setDefaultMapTheme(mt.id)"
                  >
                    {{ mt.name }}
                  </span>
                  <span
                    v-else
                    @click="setDefaultMapTheme(mt.id)"
                    class="theme-name"
                  >
                    {{ mt.name }}
                  </span>
                  <b-tooltip label="Modifier" type="is-dark" position="is-left">
                    <span
                      style="cursor: pointer"
                      class="btn-action"
                      @click="showMapTheme(mt, true)"
                    >
                      <b-icon size="is-small" pack="fas" icon="pen"></b-icon>
                    </span>
                  </b-tooltip>
                </div>
              </b-dropdown-item>
              <b-dropdown-item @click="showMapTheme"
                >
                <b-icon size="is-small" pack="fas" icon="plus"></b-icon>
                Nouveau theme</b-dropdown-item
              >
            </b-dropdown>
          </b-tooltip>
        </div>
      </div>
      <!-- End Button Action  -->
      <!-- Start Table -->
      <div
        class="table-wrapper"
        style="overflow: hidden auto"
        v-if="tableData.length > 0"
      >
        <b-table
          :key="layerDataTable"
          :data="tableData"
          @dragstart="dragstart"
          ref="table"
          v-sortable="sortableOptions"
          :draggable="false"
          detailed
          detail-key="id"
          :opened-detailed="openedRows"
          @details-open="openCollapsedItem"
          @details-close="closeCollabedItem"
          @check="showLayer"
          :checked-rows.sync="getChecked"
          :checkable="profile.authenticated"
        >
          <template v-slot="props" :style="{ cursor: getCursor() }">
            <b-table-column class="layer-name" field="name" label="Couche">
              <template>
                <span
                  v-if="getLayerName(props.row.name).length > 15"
                  :tooltip="getLayerName(props.row.name)"
                >
                  {{ getLayerName(props.row.name) }}
                </span>
                <span v-else>
                  {{ getLayerName(props.row.name) }}
                </span>
              </template>
            </b-table-column>
            <b-table-column
              field="edit"
              label="Edition"
              class="layers__actions"
              aria-disabled="true"
              v-if="profile.authenticated"
            >
              <b-tooltip
                label="Modifier Symbologie"
                type="is-dark"
                position="is-bottom"
                v-if="
                  mode == 'map' &&
                  currentMap.privacy != 'ARCHIVED' &&
                  (profile.roles.includes('ROLE_ADMIN') ||
                    profile.roles.includes('CONFIGURE_LAYER_STYLE_AUTHORITY'))
                "
              >
                <span
                  class="btn-action"
                  @click="editSymbology(props.row)"
                  v-if="
                    mode == 'map' && editSymbologyPermission[props.row.slug]
                  "
                >
                  <b-icon pack="fas" icon="palette"></b-icon>
                </span>
              </b-tooltip>
              <b-tooltip
                label="Détacher la Couche"
                type="is-dark"
                position="is-bottom"
                v-if="
                  mode == 'map' &&
                  currentMap.privacy != 'ARCHIVED' &&
                  (profile.roles.includes('ROLE_ADMIN') ||
                    profile.roles.includes('CONFIGURE_LAYER_STYLE_AUTHORITY'))
                "
              >
                <span
                  class="btn-action"
                  @click="removeLayer(props.row)"
                  v-if="
                    profile.roles.includes('ROLE_ADMIN') ||
                    profile.layers
                      .map((layer) => layer.slug)
                      .includes(props.row.slug)
                  "
                >
                  <b-icon pack="fas" icon="times-circle"></b-icon>
                </span>
              </b-tooltip>
              <b-tooltip
                label="Liste des styles"
                type="is-dark"
                position="is-left"
                v-if="
                  mode == 'map' &&
                  currentMap.privacy != 'ARCHIVED' &&
                  (profile.roles.includes('ROLE_ADMIN') ||
                    profile.roles.includes('CONFIGURE_LAYER_STYLE_AUTHORITY'))
                "
              >
                <b-menu-item>
                  <template #label>
                    <b-dropdown
                      aria-role="list"
                      class="is-pulled-right"
                      position="is-bottom-left"
                    >
                      <template #trigger>
                        <div @click="fetchStyles(props.row)">
                          <b-icon icon="dots-vertical"></b-icon>
                        </div>
                      </template>
                      <b-loading
                        :active="loadingIndicatorStyles[props.row.slug]"
                        :is-full-page="false"
                      >
                      </b-loading>
                      <template v-if="props.row.style">
                        <b-dropdown-item
                          v-for="s in props.row.style.liste"
                          :key="s.id"
                          aria-role="listitem"
                          :class="{ active: s.isDefault }"
                        >
                          <div
                            style="width: 100%"
                            class="d-flex justify-content-between"
                          >
                            <span
                              class="style-name"
                              @click="
                                setAsDefaultStyle({
                                  style: s,
                                  layer: props.row,
                                })
                              "
                              v-if="s.displayName.length > 15"
                              :tooltip="s.displayName"
                            >
                              {{ s.displayName }}
                            </span>
                            <span
                              v-else
                              class="style-name"
                              @click="
                                setAsDefaultStyle({
                                  style: s,
                                  layer: props.row,
                                })
                              "
                            >
                              {{ s.displayName }}
                            </span>

                            <b-tooltip
                              label="Modifier"
                              type="is-dark"
                              position="is-left"
                              v-if="
                                profile.roles.includes('ROLE_ADMIN') ||
                                profile.layers
                                  .map((layer) => layer.slug)
                                  .includes(props.row.slug)
                              "
                            >
                              <span
                                class="btn-action"
                                @click="showLayerStyle(props.row, s, true)"
                              >
                                <b-icon
                                  size="is-small"
                                  pack="fas"
                                  icon="pen"
                                ></b-icon>
                              </span>
                            </b-tooltip>
                          </div>
                        </b-dropdown-item>
                      </template>
                      <b-dropdown-item
                        style="margin-top: 3px; border-top: 1px solid #ccc"
                        @click="showLayerStyle(props.row, s, false)"
                        aria-role="listitem"
                        v-if="
                          profile.roles.includes('ROLE_ADMIN') ||
                          profile.layers
                            .map((layer) => layer.slug)
                            .includes(props.row.slug)
                        "
                      >
                      <b-icon size="is-small" pack="fas" icon="plus"></b-icon>
                        Nouveau style
                      </b-dropdown-item>
                    </b-dropdown>
                  </template>
                </b-menu-item>
              </b-tooltip>
            </b-table-column>
          </template>
          <template
            slot="detail"
            slot-scope="props"
            :index="props.row.id"
            v-if="props.row.visible"
          >
            <div v-for="rule in props.row.rules" :key="rule.field">
              <table class="table table-hover">
                <tr class="d-flex w-100 justify-content-between">
                  <td>
                    <b-checkbox
                      v-if="
                        rule.type == 'classify' ||
                        rule.type === 'graduated' ||
                        rule.type == 'iconGroup'
                      "
                      v-model="
                        categories[
                          `${rule.layerSlug}-_-${rule.property}-_-${rule.field}-_-${rule.operator}`
                        ]
                      "
                      @input="doFilter()"
                    >
                    </b-checkbox>
                  </td>
                  <td style="flex: -2">
                    <a-avatar
                      v-if="rule.type === 'icon' || rule.icon"
                      :style="{ transform: `rotate(${rule.rotation}deg)` }"
                      :src="rule.icon"
                    />
                    <div
                      v-if="
                        rule.type === 'graduated' ||
                        rule.type === 'cluster' ||
                        rule.type === 'classify' ||
                        (rule.type === 'simple' && !rule.icon)
                      "
                      :style="rule.style"
                    />
                    <a-avatar
                      v-if="rule.type === 'heatmap'"
                      :size="24"
                      style="
                        background: linear-gradient(
                          to bottom,
                          #33ccff 0%,
                          #ff99cc 100%
                        );
                      "
                    ></a-avatar>
                  </td>
                  <td
                    class="properties"
                    v-if="rule.operator && rule.operator != '='"
                  >
                    {{ rule.property }}
                  </td>
                  <td style="flex: 3">{{ rule.value }}</td>
                </tr>
              </table>
            </div>
          </template>
        </b-table>
      </div>
      <!-- End Table -->
    </div>
  </section>
</template>

<script>
import { mapState, mapGetters } from 'vuex'
import { backApi } from '~/methods/serverApi'
import { SecuredPublicWMSURL, SecuredWMSURL } from '../../constants'
import RestApi from '../../methods/api'
import NewSymbology from '../dashboard/symbologies/NewSymbology'
import NewStyle from '../dashboard/layers/NewStyle'
import NewMap from '../dashboard/maps/NewMap.vue'
import NewTheme from '../dashboard/maps/NewTheme.vue'
import LayerList from './LayerList'
import Sortable from 'sortablejs'
import Loader from '../ui/Loader'
import leafletImage from 'leaflet-image'

const createSortable = (el, options, vnode) => {
  let order = []

  return Sortable.create(el, {
    ...options,

    onStart: function(evt) {
      console.log(evt)
      // when the sort starts, store the initial order of the array
      order = this.toArray()

      //  let backupOpenedItems = this.openedRows;
      this.openedRows = []
    },

    onEnd: function(evt) {
      // when the sort ends, set the order to the initial state
      this.sort(order)

      // change the order using splice
      const data = vnode.context.tableData

      data.splice(evt.newIndex, 0, ...data.splice(evt.oldIndex, 1))

      // now it is safe, you can update the order parameter
      data.forEach((o, i) => {
        o.order = data.length - i
      })
      vnode.context.sortLayers(data)
    }
  })
}

const sortable = {
  name: 'sortable',
  bind(el, binding, vnode) {
    const table = el.querySelector('table')
    table._sortable = createSortable(
      table.querySelector('tbody'),
      binding.value,
      vnode
    )
  }
}

export default {
  components: { NewSymbology, NewStyle, NewMap, NewTheme, LayerList, Loader },
  directives: { sortable },
  props: ['layerToEdit'],
  data() {
    return {
      sortableOptions: { chosenClass: 'is-selected' },
      mode: this.$store.state.maps.mode,
      isPrintLoading: false,
      showDetailIcon: false,
      defaultOpenedDetails: [1],
      categories: {},
      backupCategories: {},
      layerDataTable: 1,
      openedRows: [],
      checkedLayers: [],
      editSymbologyPermission: {},
      loadingIndicatorStyles: {},
      styleLoading: {},
      loaderIndicator: false,
      loadingIndicatorThemes: false
    }
  },
  watch: {
    tableData(navVal, oldVal) {
      if (navVal.length > oldVal.length) {
        if (this.profile.authenticated) {
          this.getPermissions()
        }
      }
    },
    defaultMapTheme(newVal, oldVal) {
      if (newVal && oldVal && newVal.name !== oldVal.name) {
        this.setDefaultMapTheme(newVal.id)
      }
    }
  },
  computed: {
    ...mapState('maps', ['currentMap']),
    ...mapState(['profile']),
    ...mapGetters({
      mapLayers: 'maps/mapLayers',
      layerFilters: 'maps/getLayersFilter',
      defaultMapTheme: 'maps/getDefaultMapTheme',
      mapThemes: 'maps/mapThemes',
      isMobile: 'app/getIsMobile'
    }),

    getChecked() {
      return this.tableData.filter(f => f.visible)
    },
    tableData() {
      let ref = this
      function getRules(layer) {
        ref.loadingIndicatorStyles[layer.slug] = false
        let data = []
        if (layer.style) {
          data = JSON.parse(layer.style.style)
        }
        let rules = []
        if (data.rules && data.rules.length > 0) {
          let rule = data.rules[0]
          if (data.symbologyType === 'Cluster') {
            rules.push({
              type: 'cluster',
              field: layer.name,
              order: layer.order,
              icon: data.iconUrl,
              style: {
                backgroundColor: rule.fill.color,
                borderStyle: 'solid',
                borderWidth: rule.stroke.width + 'px',
                borderColor: rule.stroke.color,
                width: '15px',
                height: '15px',
                clipPath: ref.getClipPath(layer.topo, rule.mark.name),
                transform: `rotate(${rule.mark.rotation}deg)`
              }
            })
          } // Start Simple Style Condition
          else if (data.symbologyType === 'Simple') {
            if (rule.icon) {
              rules.push({
                type: 'icon',
                field: layer.name,
                icon: layer.iconUrl,
                rotation: rule.icon.rotation
              })
            } else {
              rules.push({
                type: 'simple',
                shapeForm: rule.mark.name,
                field: layer.name,
                style: {
                  backgroundColor: ref.getStyleColor(rule, layer.topo),
                  borderColor: rule.stroke.color,
                  borderWidth: rule.stroke.width,
                  borderStyle: 'solid',
                  width:
                    layer.topo === 'LineString'
                      ? '20px'
                      : layer.topo === 'Polygon'
                        ? '25px'
                        : rule.mark.name === 'triangle'
                          ? '20px'
                          : '15px',
                  height:
                    layer.topo === 'LineString'
                      ? '4px'
                      : layer.topo === 'Polygon'
                        ? '20px'
                        : '15px',
                  clipPath: ref.getClipPath(layer.topo, rule.mark.name),
                  transform: `rotate(${rule.mark.rotation}deg)`
                }
              })
            }
          } //  End Simple Style Condition
          else if (data.symbologyType === 'Classify') {
            for (const {
              filter: { field, operator, value, property, color },
              mark: { rotation }
            } of data.rules) {
              if (field != null) {
                rules.push({
                  type: 'classify',
                  layerSlug: layer.slug,
                  operator,
                  field,
                  value,
                  order: layer.order,
                  property,
                  style: {
                    backgroundColor: color,
                    width: '15px',
                    height: '15px',
                    clipPath: ref.getClipPath(layer.topo, rule.mark.name),
                    transform: `rotate(${rotation}deg)`
                  }
                })
              }
            }
          } else if (data.symbologyType === 'Graduated') {
            for (const {
              filter: { field, index, property, color, operator },
              mark: { rotation }
            } of data.rules) {
              if (field != null) {
                rules.push({
                  type: 'graduated',
                  layerSlug: layer.slug,
                  field,
                  order: layer.order,
                  property,
                  operator,
                  value: field,
                  style: {
                    backgroundColor: color,
                    opacity:
                      data.selectedMethod === 'size'
                        ? 1
                        : parseInt(index) / parseInt(data.classes),
                    width:
                      data.selectedMethod === 'size'
                        ? `${12 + parseInt(index) * 1.5}px`
                        : '15px',
                    height:
                      data.selectedMethod === 'size'
                        ? `${12 + parseInt(index) * 1.5}px`
                        : '15px',
                    clipPath: ref.getClipPath(layer.topo, rule.mark.name),
                    transform: `rotate(${rotation}deg)`
                  }
                })
              }
            }
          } else if (data.symbologyType === 'IconGroup') {
            for (const {
              filter: { field, iconUrl, value, operator, property }
            } of data.rules) {
              if (field != null) {
                rules.push({
                  type: 'iconGroup',
                  layerSlug: layer.slug,
                  field,
                  value,
                  order: layer.order,
                  operator,
                  property,
                  icon: iconUrl
                })
              }
            }
          } else if (data.symbologyType === 'Heatmap') {
            rules.push({
              type: 'heatmap',
              field: layer.name
            })
          }
        }

        return rules
      }
      return this.mapLayers
        .map(
          ({
            id,
            name,
            slug,
            order,
            topo,
            style,
            iconUrl,
            fields,
            customIcon,
            labelingEnabled,
            symbologyType,
            visible,
            typeLimit,
            mapLayerId
          }) => ({
            id,
            name,
            slug,
            order,
            rules: getRules({
              name,
              slug,
              style,
              order,
              symbologyType,
              iconUrl,
              customIcon,
              labelingEnabled,
              topo
            }),
            style,
            fields,
            topo,
            visible,
            typeLimit,
            mapLayerId
          })
        )
        .sort((a, b) => b.order - a.order)
    }
  },
  methods: {
    setAsDefaultStyle({ style, layer }) {
      if (style.id !== layer.style.id) {
        backApi
          .get(`layer-styles/${style.mapLayer}/styles/${style.id}/default`)
          .then(() => {
            let stylesList = layer.style.liste.map(s => {
              return {
                ...s,
                isDefault: style.id === s.id ? true : false
              }
            })
            layer.style = { ...style, isDefault: true }
            layer.style.liste = stylesList
            this.resetSelectedCategoryFilters(layer.slug)
            let filters = this.layerFilters
            delete filters[layer.slug]
            this.$store.dispatch('maps/updateLayersFilter', filters)
            this.$store.commit('maps/updateLayer', layer)
            this.$store.dispatch('maps/updateMapVersion')
          })
          .catch(() => {})
      }
    },
    setDefaultMapTheme(themeId) {
      if (themeId !== this.defaultMapTheme.id) {
        this.$nuxt.$loading.start()
        backApi
          .get(`themes/${themeId}/maps/${this.currentMap.id}/default`)
          .then(() => {
            let themes = this.mapThemes.map(tm => {
              return {
                ...tm,
                isDefault: themeId === tm.id ? true : false
              }
            })
            backApi
              .get(`maps/layersWithFieldsAndResources/${this.currentMap.id}`)
              .then(({ data }) => {
                if(this.layerFilters && Object.keys(this.layerFilters).length > 0){
                  let filters = this.layerFilters
                for (const l of data) {
                  this.resetSelectedCategoryFilters(l.slug)
                  delete filters[l.slug]
                }
                this.$store.dispatch('maps/updateLayersFilter', filters)
                }
                this.$store.dispatch('maps/setMapLayers', data)
                this.$store.dispatch('maps/updateMapVersion')
                this.$nuxt.$loading.finish()
              })
              .catch(() => this.$nuxt.$loading.finish())
            this.$store.dispatch('maps/setMapThemes', themes)
          })
          .catch(() => {
            this.$nuxt.$loading.finish()
          })
      }
    },
    fetchStyles(layer) {
      if (!this.loadingIndicatorStyles[layer.slug] && layer.style) {
        this.loadingIndicatorStyles[layer.slug] = true
        backApi
          .get(`layer-styles/${layer.slug}/maps/${layer.style.mapLayer}`)
          .then(({ data }) => {
            let incomingStyle = data.find(s => s.isDefault)
            if (
              layer.style.liste &&
              layer.style.liste.find(s => s.isDefault).id !== incomingStyle.id
            ) {
              this.setAsDefaultStyle({
                style: incomingStyle,
                layer: layer
              })
            }
            layer.style.liste = data
            this.$store.commit('maps/updateLayer', layer)
            this.loadingIndicatorStyles[layer.slug] = false
          })
          .catch(() => {
            this.loadingIndicatorStyles[layer.slug] = false
          })
      }
    },
    showLayerStyle(layer, style, edit) {
      this.$buefy.modal.open({
        parent: this,
        component: NewStyle,
        hasModalCard: true,
        props: { layer: layer, styleToEdit: style, isEdit: edit },
        events: { setAsDefaultStyle: this.setAsDefaultStyle }
      })
    },
    showMapTheme(theme, edit) {
      this.$buefy.modal.open({
        parent: this,
        component: NewTheme,
        hasModalCard: true,
        canCancel: false,
        events: { setDefaultMapTheme: this.setDefaultMapTheme },
        props: { theme: theme, currentMapId: this.currentMap.id, isEdit: edit }
      })
    },
    getLayerName(layer) {
      return layer.includes('____') ? layer.split('____')[1] : layer
    },
    getPermissions() {
      if (this.profile.authenticated && this.currentMap.layers.length > 0) {
        for (let l of this.currentMap.layers) {
          if (!this.editSymbologyPermission[l.slug]) {
            this.loaderIndicator = true
            if (this.profile.roles.includes('ROLE_ADMIN')) {
              this.editSymbologyPermission[l.slug] = true
              this.layerDataTable += 1
              this.loaderIndicator = false
            } else {
              backApi
                .get(
                  `layers/check-write-permission/${
                    l.slug
                  }/CONFIGURE_LAYER_STYLE_AUTHORITY`
                )
                .then(() => {
                  this.editSymbologyPermission[l.slug] = true
                  this.layerDataTable += 1
                  this.loaderIndicator = false
                })
                .catch(() => {
                  this.editSymbologyPermission[l.slug] = false
                  this.loaderIndicator = false
                })
            }
          }
        }
      }
    },
    getCursor() {
      return this.profile.authenticated ? 'move' : 'moev'
    },
    getClipPath(layerTopo, markName) {
      return layerTopo === 'Polygon' || layerTopo === 'MultiPolygon'
        ? 'polygon(44% 33%, 86% 4%, 100% 60%, 73% 98%, 23% 81%, 0 64%, 0 14%)'
        : layerTopo === 'LineString'
          ? 'polygon(0 42%, 100% 42%, 100% 57%, 0 57%)'
          : markName === 'circle'
            ? 'circle(50% at 50% 50%)'
            : markName === 'triangle'
              ? 'polygon(50% 0%, 0% 100%, 100% 100%)'
              : markName === 'star'
                ? 'polygon(50% 0%, 61% 35%, 98% 35%, 68% 57%, 79% 91%, 50% 70%, 21% 91%, 32% 57%, 2% 35%, 39% 35%)'
                : 'none'
    },
    openCollapsedItem(row) {},
    closeCollabedItem(row) {
      // this.openedRows = this.openedRows.filter((item) => item.id !== row.id);
    },
    // prevent layers from being dragable
    dragstart(payload) {
      if (this.profile.authenticated == false) {
        payload.event.preventDefault()
      }
    },
    generateCqlFilter(property, values, operator, condition) {
      let filter = ''
      for (let i = 0; i < values.length; i++) {
        if (operator === '<>') {
          values[i].split('-').forEach((v, index) => {
            filter += `parseDouble(${property})`
            filter += this.getOperator(index === 0 ? '>=' : '<=', 'number')
            filter += v.replace("'", "''")
            filter +=
              i < values.length - 1 && index != 0
                ? "' or "
                : index == 1
                  ? "'"
                  : condition
                    ? condition
                    : "' and "
          })
        } else {
          filter += property
          filter += this.getOperator(operator, typeof values[i])
          filter +=
            typeof values[i] === 'string'
              ? values[i].replace("'", "''")
              : values[i]
          filter +=
            i == values.length - 1
              ? typeof values[i] === 'string'
                ? "' "
                : "') "
              : typeof values[i] === 'string'
                ? "' or "
                : "') or "
        }
      }
      return filter
    },
    /**
     * Methods return the operator in Stirng format
     * @input  operator
     * @output operator
     */
    getOperator(operator, type) {
      switch (operator) {
        case '<':
          return "<'"
        case '>':
          return ">'"
        case '<=':
          return "<='"
        case '>=':
          return ">='"
        default:
          return type === 'string' ? "='" : "=parseDouble('"
      }
    },
    /**
     * Methods return the layer style color
     * @input  sld style
     * @output color
     */
    getStyleColor(style, topo) {
      switch (topo) {
        case 'LineString':
          return style.stroke.color
        default:
          return style.fill.color
      }
    },
    resetSelectedCategoryFilters(layerSlug) {
      let categoriesKeys = Object.keys(this.categories)
      if (categoriesKeys.length > 0) {
        categoriesKeys.forEach(c => {
          if (c.includes(layerSlug)) {
            this.categories[c] = false
          }
        })
      }
    },
    reloadLayers(layerSlug) {
      this.resetSelectedCategoryFilters(layerSlug)
      this.doFilter()
    },
    doFilter() {
      let selectedLayer = []
      this.clearLayers()
      let filtering = Object.entries(this.categories)
        .filter(cat => cat[1])
        .sort((a, b) => {
          if (a[1] && b[1]) {
            const order1 = this.getLayerOrder(a[0].split('-_-')[0])
            const order2 = this.getLayerOrder(b[0].split('-_-')[0])
            return order1 - order2
          }
        })
        .reduce((acc, cur) => {
          const [layerSlug, property, field, operator] = cur[0].split('-_-')
          selectedLayer.push(layerSlug)
          if (!acc[layerSlug]) {
            acc[layerSlug] = {}
            if (!acc[layerSlug][property]) {
              acc[layerSlug][property] = []
            }
          }
          if (operator === '<>') {
            const [min, max] = field.split('-')
            for (let i = parseFloat(min); i <= parseFloat(max); i++) {
              acc[layerSlug][property].push(i.toString())
            }
          } else {
            acc[layerSlug][property].push(field)
          }
          return acc
        }, {})
      if (Object.keys(filtering).length > 0) {
        for (let [layerSlug, fields] of Object.entries(filtering)) {
          let filterCreaterias = ''
          Object.entries(fields).forEach(([field, value]) => {
            filterCreaterias = this.generateCqlFilter(
              field,
              value,
              typeof values === 'string' &&
              value[0].includes('-') &&
              value[0].split(':').length !== 2
                ? '<>'
                : '='
            )
          })
          this.addLayerToMap(filterCreaterias, layerSlug)
        }
      }
      this.$store.dispatch('maps/updateLayersFilter', filtering)
      this.loadLayers(selectedLayer.length > 0 ? selectedLayer : null)
    },
    addLayerToMap(filter, layer) {
      let wmsUrl =
        this.profile.authenticated === true
          ? SecuredWMSURL
          : SecuredPublicWMSURL
      this.$layerGroups[`${layer}`] = L.tileLayer
        .wms(`${wmsUrl}`, {
          layers: `limite_admin:${layer}`,
          format: 'image/png',
          styles: `limite_admin:${
            this.mapLayers.find(l => l.slug === layer).style.name
          }`,
          transparent: true,
          cql_filter: filter
        })
        .setZIndex(8000 + this.getLayerOrder(layer))
        .addTo(this.$map)

      const token = localStorage.getItem('sigToken')
      if (this.profile.authenticated)
        this.$layerGroups[`${layer}`].setParams({ token })
    },
    clearLayers() {
      if (this.$layerGroups[`wmsLayer`]) {
        this.$map.removeLayer(this.$layerGroups['wmsLayer'])
      }
      if (this.$layerGroups[`wmsProperies`])
        this.$map.removeLayer(this.$layerGroups['wmsProperies'])

      this.mapLayers.forEach(layer => {
        if (this.$layerGroups[`${layer.slug}`])
          this.$map.removeLayer(this.$layerGroups[`${layer.slug}`])
      })
    },
    loadLayers(filtredLayer) {
      let myLayers = null
      let maxOrder = 0
      if (filtredLayer != null) {
        myLayers = this.mapLayers
          .filter(
            l =>
              l.visible &&
              filtredLayer.filter(fLayer => fLayer === l.slug).length == 0
          )
          .map(layer => layer)
          .sort((a, b) => a.order - b.order)
      } else {
        myLayers = this.mapLayers
          .filter(l => l.visible)
          .map(layer => layer)
          .sort((a, b) => a.order - b.order)
      }
      if (myLayers && myLayers.length > 0) {
        maxOrder = myLayers[myLayers.length - 1].order
      }
      let wmsLayers = myLayers
        .map(layer => `limite_admin:${layer.slug}`)
        .join(',')
      let wmsStyles = myLayers
        .map(
          layer =>
            layer.layerType === 'RASTER' ? '' : layer.style && layer.style.name
        )
        .join(',')

      let wmsUrl =
        this.profile.authenticated === true
          ? SecuredWMSURL
          : SecuredPublicWMSURL
      let options = {
        layers: wmsLayers,
        format: 'image/png',
        transparent: true,
        styles: wmsStyles
      }
      this.$layerGroups[`wmsLayer`] = L.tileLayer.wms(wmsUrl, options)

      const token = localStorage.getItem('sigToken')
      if (this.profile.authenticated)
        this.$layerGroups[`wmsLayer`].setParams({ token })

      this.$layerGroups[`wmsLayer`].setZIndex(8000 + maxOrder).addTo(this.$map)
    },
    addLayers() {
      this.getMapThemes()
      this.$buefy.modal.open({
        parent: this,
        component: LayerList,
        hasModalCard: true,
        events: { setDefaultMapTheme: this.setDefaultMapTheme }
      })
    },
    editSymbology(layer) {
      this.$buefy.modal.open({
        parent: this,
        component: NewSymbology,
        hasModalCard: true,
        events: { reloadLayers: this.reloadLayers },
        props: { layerToEdit: layer, mapSlug: this.currentMap.slug }
      })
    },
    getLayerOrder(layerSlug) {
      return (
        this.mapLayers.filter(l => l.slug === layerSlug)[0] &&
        this.mapLayers.filter(l => l.slug === layerSlug)[0].order
      )
    },
    getMaxLayerOrder(layers) {
      return layers && layers.sort((a, b) => a.order - b.order)[0].order
    },
    sortLayers(_layers) {
      let mapLayers = []

      let order = _layers.length

      _layers.forEach(layer => {
        let mapLayer = {}

        mapLayer.mapLayerId = layer.style.mapLayer
        mapLayer.order = order
        mapLayers.push(mapLayer)
        order -= 1
      })

      backApi.put(`maps/sort/`, mapLayers).then(({ data }) => {
        this.$store.commit('maps/sortLayers', _layers)
        this.$map.removeLayer(this.$layerGroups[`wmsLayer`])
        this.layerDataTable += 1
        this.doFilter()
      })
    },
    getMapImage() {
      let _this = this
      leafletImage(this.$map, function(err, canvas) {
        let base64 = canvas.toDataURL()

        var link = document.createElement('a')

        document.body.appendChild(link) // for Firefox

        link.setAttribute('href', base64)

        link.setAttribute('download', `${_this.currentMap.name}.png`)
        link.click()
        link.remove()
      })
    },
    printMap() {
      this.isPrintLoading = true
      let _this = this
      return new Promise(function(resolve, reject) {
        leafletImage(_this.$map, function(err, canvas) {
          function dataURLtoBlob(dataurl) {
            var arr = dataurl.split(','),
              mime = arr[0].match(/:(.*?);/)[1],
              bstr = atob(arr[1]),
              n = bstr.length,
              u8arr = new Uint8Array(n)
            while (n--) {
              u8arr[n] = bstr.charCodeAt(n)
            }
            return new Blob([u8arr], { type: mime })
          }

          let blob = dataURLtoBlob(canvas.toDataURL())
          var file = new File([blob], 'map.png')
          let imageUrl = RestApi.saveFile(file)
          resolve(imageUrl)
          _this.isPrintLoading = false
        })
      })
    },
    getMapThemes() {
      backApi.get(`themes/maps/${this.currentMap.id}`).then(({ data }) => {
        this.$store.dispatch('maps/setMapThemes', data)
      })
    },
    showLayer(layers) {
      let hide = true
      this.mapLayers.forEach(layer => {
        if (
          (layers.filter(l => l.id === layer.id).length > 0 &&
            !layer.visible) ||
          (layers.filter(l => l.id === layer.id).length === 0 && layer.visible)
        ) {
          hide = true
          Object.keys(this.backupCategories).forEach(key => {
            if (key.split('-_-')[0] === layer.slug) {
              if (!this.categories[key])
                this.categories[key] = this.backupCategories[key]
              delete this.backupCategories[key]
              hide = false
            }
          })
          if (hide) {
            Object.keys(this.categories).forEach(key => {
              if (key.split('-_-')[0] === layer.slug) {
                if (!this.backupCategories[key])
                  this.backupCategories[key] = this.categories[key]
                delete this.categories[key]
              }
            })
          }
        }
        if (layers.filter(l => l.id === layer.id).length > 0) {
          this.$store.commit('maps/setLayerVisible', {
            ...layer,
            visible: true
          })
          backApi.get(
            `maps/set-visibility?map-layer-id=${
              layer.mapLayerId
            }&visibility=true`
          )
        } else {
          this.$store.commit('maps/setLayerVisible', {
            ...layer,
            visible: false
          })
          backApi.get(
            `maps/set-visibility?map-layer-id=${
              layer.mapLayerId
            }&visibility=false`
          )
        }
      })
      this.doFilter()
    },
    // delete layer on the map
    removeLayer(layer) {
      this.$confirm({
        title: 'Êtes-vous sûr de vouloir supprimer cette couche?',
        okText: 'Oui',
        okType: 'danger',
        cancelText: 'Non',
        onOk: () => {
          this.$nextTick(() => {
            this.$nuxt.$loading.start()

            setTimeout(() => this.$nuxt.$loading.finish(), 900)
          })
          let layerToDetach = this.mapLayers.find(l => l.id === layer.id)
          let data = this.mapLayers.filter(l => {
            return l.id !== layer.id && l.order > layer.order
          })

          let mapLayers = []
          data.forEach(l => {
            let mapLayer = {}
            mapLayer.mapLayerId = l.style.mapLayer
            mapLayer.mapManipulation = 'ATTACH'
            mapLayer.order = l.order - 1
            mapLayers.push(mapLayer)
          })

          mapLayers.push({
            map: {
              id: this.currentMap.id,
              privacy: this.currentMap.privacy
            },
            layer: {
              id: layerToDetach.id,
              name: layerToDetach.name,
              topo: layerToDetach.topo
            },
            mapLayerId: layerToDetach.style.mapLayer,
            mapManipulation: 'DETACH'
          })
          backApi
            .post(`maps/detach/${this.currentMap.id}/${layer.id}`, mapLayers)
            .then(() => {
              let newLayers = this.currentMap.layers
                .filter(l => l.slug !== layer.slug)
                .map(l => ({
                  ...l,
                  order: l.order > layerToDetach.order ? l.order - 1 : l.order
                }))
              if (this.$layerGroups['featureInfo'])
                this.$map.removeLayer(this.$layerGroups['featureInfo'])

              if (this.$layerGroups['wmsLayer'])
                this.$map.removeLayer(this.$layerGroups['wmsLayer'])
              this.$store.commit('maps/detach', newLayers)
              this.layerDataTable++
              let myLayers = newLayers
                .filter(l => l.visible)
                .map(layer => layer)
                .sort((a, b) => a.order - b.order)

              let wmsLayers = myLayers
                .map(layer => `limite_admin:${layer.slug}`)
                .join(',')
              let wmsStyles = myLayers
                .map(
                  layer =>
                    layer.layerType === 'RASTER' ? '' : layer.style.name
                )
                .join(',')

              const token = localStorage.getItem('sigToken')
              this.$layerGroups[`wmsLayer`] = L.tileLayer
                .wms(`${SecuredWMSURL}`, {
                  layers: wmsLayers,
                  format: 'image/png',
                  transparent: true,
                  styles: wmsStyles,
                  token
                })
                .setZIndex(5000)
                .addTo(this.$map)

              //this.getFeatureInfo()

              this.$notification.success({
                message: 'La couche a été détachée avec succés '
              })
            })
            .catch(error => {
              console.log(error)
              this.$notification.error({
                message: 'Erreur lors de dettachement de la couche!'
              })
            })
        },
        onCancel: () => {}
      })
    }
    /*getFeatureInfo() {
      var geojson = null
      let vm = this.$map
      let thisRef = this
      this.$map.on('popupclose', function(e) {
        if (geojson) {
          vm.removeLayer(geojson)
        }
      })

      const url = thisRef.profile.authenticated
        ? SecuredWMSURL
        : SecuredPublicWMSURL
      this.$map.on('click', function(e) {
        var _layers = this._layers,
          versions = []
        for (var x in _layers) {
          var _layer = _layers[x]
          if (_layer.wmsParams) {
            versions.push(_layer.wmsParams.version)
          }
        }

        let myLayers = thisRef.currentMap.layers
          .filter(l => l.visible)
          .map(layer => layer)
          .sort((a, b) => b.order - a.order)
        let wmsLayers = myLayers
          .map(layer => `limite_admin:${layer.slug}`)
          .join(',')
        var loc = e.latlng,
          xy = e.containerPoint, // xy = this.latLngToContainerPoint(loc,this.getZoom())
          size = this.getSize(),
          bounds = this.getBounds(),
          crs = this.options.crs,
          sw = crs.project(bounds.getSouthWest()),
          ne = crs.project(bounds.getNorthEast()),
          obj = {
            service: 'WMS', // WMS (default)
            request: 'GetFeatureInfo',
            version: versions[0],
            layers: wmsLayers,
            map: thisRef.mapSlug,
            buffer: 24,
            bbox: bounds.toBBoxString(), // works only with EPSG4326, but not with EPSG3857
            //bbox: sw.x + ',' + sw.y + ',' + ne.x + ',' + ne.y, // works with both EPSG4326, EPSG3857
            width: size.x,
            height: size.y,
            query_layers: wmsLayers,
            info_format: 'application/json', // text/plain (default), application/json for JSON (CORS enabled servers), text/javascript for JSONP (JSONP enabled servers)
            feature_count: 50
            //exceptions: 'application/json', // application/vnd.ogc.se_xml (default)
            // format_options: 'callback: parseResponse' // callback: parseResponse (default), use only with JSONP enabled servers, when you want to change the callback name
          }
        if (parseFloat(obj.version) >= 1.3) {
          obj.crs = 'EPSG:4326'
          obj.i = Math.round(xy.x)
          obj.j = Math.round(xy.y)
        } else {
          obj.srs = 'EPSG:4326'
          obj.x = Math.round(xy.x)
          obj.y = Math.round(xy.y)
        }

        const storedToken = localStorage.getItem('sigToken')

        let axiosConfig = { url: url + L.Util.getParamString(obj, url, true) }
        if (thisRef.profile.authenticated)
          axiosConfig.params = { token: storedToken }

        axios(axiosConfig)
          .then(({ data }) => {
            if (geojson) {
              map.removeLayer(geojson)
            }
            if (data.features) {
              var features = data.features

              if (features.length) {
                let newLayers = []
                for (var i in features) {
                  var feature = features[i]
                  var newLayer = { feature: { properties: {} } }

                  newLayer.feature.geometry = feature.geometry
                  var properties = feature.properties

                  var layerId = properties['layer_entity_element']

                  var layer = thisRef.currentMap.layers.find(
                    layer => layer.id === layerId
                  )

                  newLayer.feature = feature
                  newLayer.feature.layer = layer
                  newLayer.featuaddFeatureInfoToMapre.id = properties['id']

                  newLayer.featureType = layer.slug
                  newLayers.push(newLayer)
                }

                thisRef.(newLayers)
                thisRef.$emit('editFeatureInfo', {
                  features: newLayers,
                  current: 0
                })
              } else {
              }
            } else {
            }
          })
          .catch(error => {
            console.log(error)
            if (geojson) {
              vm.removeLayer(geojson)
            }

            //vm.openPopup(html, loc)
          })
      })
    }*/
  },
  mounted() {
    if (this.profile.authenticated) {
      this.getMapThemes()
    }
  }
}
</script>

<style lang="scss">
.layers {
  .content-wrapper {
    position: absolute;
    background: white;
    top: 11px;
    height: 97%;
    width: 360px;
    bottom: 0.75rem;
    right: -360px;
    z-index: 399;
    border-radius: 5px;
    display: flex;
    flex-direction: column;
    @include respond('phone') {
      position: static;
      top: unset;
      right: unset;
      width: 100%;
      height: fit-content;
      max-height: 75vh;
    }
    .btn-wrapper {
      display: flex;
      justify-content: space-between;
      margin-bottom: 10px;
      padding: 10px;
    }
    &.opened {
      right: 0;
    }
  }
  .table-wrapper {
    flex-grow: 1;
    padding: 0 10px;
    @include respond('phone') {
      padding: 0 5px;
    }
    tbody {
      td:nth-child(1) {
        padding-left: 0 !important;
        padding-bottom: 0 !important;
        padding-top: 0 !important;
      }
      td:nth-child(2) {
        padding-left: 0 !important;
      }
      tr {
        cursor: move;
      }
      .detail-container {
        tr {
          align-items: center;
          .check {
            margin-top: 8.5px;
          }
        }
      }
    }
    thead {
      th:nth-child(1) {
        padding: 0 !important;
      }
      th:nth-child(2) {
        padding-left: 0 !important;
      }
    }
  }
  &__actions {
    display: flex;
    flex-wrap: nowrap;
    span:nth-child(3) {
      li {
        .mdi-dots-vertical {
          color: #000;
        }
        list-style: none;
      }
    }
  }
  .dropdown-menu {
    max-height: 250px;
    width: 150px;
    overflow: hidden auto;
    .dropdown-item {
      cursor: default;
      padding-right: 0.5rem;
    }
    .dropdown-item {
      &.active {
        background-color: $color-primary;
      }
      &:last-child {
        cursor: pointer;
      }
    }
    .style-name,
    .theme-name {
      flex: 0.95;
      cursor: pointer;
      text-overflow: ellipsis;
      white-space: nowrap;
      overflow: hidden;
      &::after {
        font-size: 0.65rem;
      }
    }
  }
}
.table-wrapper span.btn-action {
  cursor: pointer;
}
.table tr {
  line-height: 1.7;
}

table {
  position: relative;
}
table tbody td {
  min-height: 41px;
}
table td.layer-name {
  width: 100%;
  max-width: 0;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
  @include respond('tab-port') {
    max-width: 100%;
    overflow: auto;
  }

  [tooltip]::before,
  [tooltip]::after {
    text-transform: none;
    font-size: 0.9em;
    line-height: 1;
    user-select: none;
    pointer-events: none;
    position: absolute;
    display: none;
  }

  [tooltip]::before {
    content: '';
    border: 5px solid transparent;
    z-index: 1001;
  }

  [tooltip]::after {
    content: attr(tooltip);
    text-align: left;
    min-width: 3em;
    max-width: 21em;
    white-space: pre-wrap;
    overflow: hidden;
    padding: 10px 5px;
    border-radius: 0.3ch;
    box-shadow: 0 1em 2em -0.5em rgba(0, 0, 0, 0.35);
    background: #333;
    color: #fff;
    z-index: 1000;
  }

  [tooltip]:hover::before,
  [tooltip]:hover::after {
    display: block;
  }

  [tooltip='']::before,
  [tooltip='']::after {
    display: none !important;
  }
}
.symbologyEdition {
  pointer-events: auto !important;
}
</style>
