<template>
  <form
    ref="form"
    :class="`feature position-absolute rounded-1 ${
      injectedIn === 'datatable'
        ? 'feature-datatable'
        : injectedIn === 'mobilemap'
        ? 'feature-map-mobile'
        : ''
    }`"
    @reset="onCancel"
  >
    <div class="d-flex feature-wrapper">
      <div class="card">
        <b-tabs id="feature-type-list" v-model="activeTab">
          <b-tab-item :visible="mode === 'create'">
            <template slot="header">
              <b-icon pack="fas" icon="image"></b-icon>
              <span>Type</span>
            </template>
            <b-loading
              :active="loadingIndicator ? loadingIndicator : false"
              :is-full-page="false"
            >
            </b-loading>
            <div />
            <b-collapse
              v-for="layer in getLayersToAddOptions"
              :key="layer.id"
              class="card card--collapsed d-block"
              aria-id="contentIdForA11y3"
            >
              <div
                slot="trigger"
                slot-scope="props"
                class="card-header"
                role="button"
                aria-controls="contentIdForA11y3"
              >
                <p class="card-header-title">{{ layer.name }}</p>
                <a class="card-header-icon">
                  <b-icon :icon="props.open ? 'menu-down' : 'menu-up'"></b-icon>
                </a>
              </div>
              <div class="card-content">
                <div class="content">
                  <button
                    type="button"
                    :name="layer.name"
                    class="btn layer-icon"
                    @click="onTypeSelect(layer)"
                  >
                    <img
                      :name="layer.name"
                      :src="layer.customIcon ? layer.iconUrl : defaultIcon"
                      style="height: 48px"
                    />
                  </button>
                </div>
              </div>
            </b-collapse>
          </b-tab-item>
          <b-tab-item
            label="Attributes"
            :disabled="mode == 'create' && selectedType == ''"
          >
            <template v-if="injectedIn !== 'datatable'" slot="header">
              <b-icon pack="fas" icon="database"></b-icon>
              <span :class="`layer-name ${mode === 'create' && 'reduce'}`"
                >La couche
                <strong
                  :tooltip="
                    activeLayerName && activeLayerName.includes('____')
                      ? activeLayerName.split('____')[1]
                      : activeLayerName
                  "
                >
                  {{
                    activeLayerName && activeLayerName.includes('____')
                      ? activeLayerName.split('____')[1]
                      : activeLayerName
                  }}
                </strong>
              </span>
            </template>
            <div class="d-flex justify-content-between">
              <div></div>
              <b-loading
                :active="loadingIndicator ? loadingIndicator : false"
                :is-full-page="false"
              >
              </b-loading>
              <div class="d-flex justify-content-around">
                <b-pagination
                  v-if="mode == 'edit' && total > 1"
                  @change="getFeature"
                  :total="total"
                  :current.sync="currentPage"
                  :range-before="rangeBefore"
                  :range-after="rangeAfter"
                  :order="order"
                  :size="size"
                  :simple="isSimple"
                  :rounded="isRounded"
                  :per-page="perPage"
                  :icon-prev="prevIcon"
                  :icon-next="nextIcon"
                  aria-next-label="Next page"
                  aria-previous-label="Previous page"
                  aria-page-label="Page"
                  aria-current-label="Current page"
                >
                </b-pagination>
              </div>
            </div>
            <div
              v-if="injectedIn === 'datatable' && toggleButton"
              class="d-flex justify-content-end my-3"
            >
              <b-tooltip
                label="Réduire le menu"
                type="is-dark"
                position="is-left"
              >
                <button
                  id="close"
                  type="button"
                  class="button"
                  @click="$emit('close')"
                  style="background-color: rgb(239, 239, 239); border: none"
                >
                  <b-icon pack="fas" icon="arrow-right"></b-icon>
                </button>
              </b-tooltip>
            </div>
            <div
              v-if="
                !getNearbyPoints &&
                !canEditFeature &&
                (mode == 'edit' || mode === 'read') &&
                !profile.authenticated
              "
            >
              <feature-fields
                :isPublic="!profile.authenticated"
                :fields="activeLayer.fields"
                :newLayer="newLayer"
              ></feature-fields>
            </div>
            <div
              v-if="
                !getNearbyPoints &&
                !canEditFeature &&
                (mode === 'edit' || mode === 'read') &&
                profile.authenticated
              "
            >
              <div v-if="!canEditFeature && !editGeometryMode">
                <feature-fields
                  :fields="activeLayer.fields"
                  :newLayer="newLayer"
                ></feature-fields>
              </div>
              <div
                v-if="
                  editGeometryMode &&
                  featureLayer.feature.geometry.type == 'Point'
                "
                style="margin-top: 15px"
              >
                <b-field grouped class="row-column">
                  <b-field expanded label="Longitude">
                    <b-input
                      name="longitude"
                      placeholder="longitude"
                      type="text"
                      v-model="featureLayer.toGeoJSON().geometry.coordinates[0]"
                    >
                    </b-input>
                  </b-field>
                  <b-field expanded label="Latitude">
                    <b-input
                      name="latitude"
                      placeholder="latitude"
                      type="text"
                      v-model="featureLayer.toGeoJSON().geometry.coordinates[1]"
                    >
                    </b-input>
                  </b-field>
                </b-field>
              </div>
            </div>
            <div v-if="getNearbyPoints" style="margin-top: 15px">
              <nearby
                :featureId="newLayer.feature.id"
                :geometry="newLayer.feature.geometry"
                :draw="false"
                @setGoTo="setGoTo"
                @closed="closePanel"
                @goToElement="goToElement"
              ></nearby>
            </div>
            <div
              class="fields-wrapper"
              :style="{
                display:
                  ((!canEditFeature && mode === 'create') ||
                    (canEditFeature && mode === 'edit')) &&
                  !getNearbyPoints
                    ? 'block'
                    : 'none',
              }"
            >
              <b-field
                v-for="field in activeLayer.fields
                  ? activeLayer.fields
                      .filter((layer) => layer.visible === true)
                      .sort((a, b) => a.order - b.order)
                  : activeLayer.fields"
                :key="field.id"
                :id="field.id"
                :label="field.name"
              >
                <b-input
                  :required="field.required"
                  validation-message="Veuillez remplir ce champ"
                  v-if="field.type === 'TEXT'"
                  :name="field.name"
                  :value="newLayer.feature.properties[field.slug]"
                  type="text"
                ></b-input>
                <b-input
                  :required="field.required"
                  validation-message="Veuillez remplir ce champ"
                  v-if="field.type == 'NUMBER'"
                  oninput="this.value = this.value.replace(/[^0-9.]/g, '');
                this.value = this.value.replace(/(\..*)\./g, '$1');"
                  :name="field.name"
                  :value="newLayer.feature.properties[field.slug]"
                  type="text"
                ></b-input>
                <b-input
                  :required="field.required"
                  v-if="field.type == 'INTEGER'"
                  validation-message="Veuillez remplir ce champ"
                  :name="field.name"
                  :value="newLayer.feature.properties[field.slug]"
                  type="number"
                ></b-input>
                <vue-editor
                  v-if="field.type == 'HTMLEDITOR'"
                  :required="field.required"
                  v-model="htmlContent[field.order]"
                />
                <v-select
                  :id="OSRVid"
                  v-if="field.type === 'SELECT' && field.resource"
                  :options="OSRVData[field.id]"
                  :name="field.name"
                  v-model="OSRVSelectedValues[field.id]"
                  @input="onSelect($event, field)"
                  :disabled="OSRVDisabled[field.id]"
                  label="text"
                  placeholder="Selectionner une option"
                >
                <template v-if="!editGeometryMode"  #search="{attributes, events}">
                  <input
                    class="vs__search"
                    :required="field.required ? !OSRVSelectedValues[field.id] : false"
                    v-bind="attributes"
                    v-on="events"
                  />
                </template>
                  <div slot="no-options">Aucune option ici!</div>
                </v-select>
                <multi-select
                  v-if="field.type === 'MULTI_SELECT' && field.resource"
                  :options="getMultiSelectsItems(field.resource.resourceValues)"
                  :selected-options="getSelectedItems(field.name)"
                  :name="field.name"
                  :isError="
                    field.required &&
                    !selectedMultiple[field.name].values.length > 0 &&
                    selectedMultiple[field.name].dirty
                  "
                  @select="onMultiSelect($event, field.name)"
                  :class=" field.required &&
                    !selectedMultiple[field.name].values.length > 0 &&
                    selectedMultiple[field.name].dirty ? 'required-field' : ''"
                >
                </multi-select>
                <!-- Start CAROUSEL  -->
                <div v-if="field.type == 'CAROUSEL'" class="carousel-wrapper">
                  <carousel
                    :key="`carousel-${field.id}`"
                    :featureId="newLayer.feature.id"
                    :layerId="activeLayer.id"
                    :fieldName="field.id"
                    :images="newLayer.feature.properties[field.slug]"
                    @setImagesUrlToUpload="setImagesUrlToUpload"
                    @setImagesToUpload="setImagesToUpload"
                    :view="false"
                    :imageLoaded="imageLoaded"
                  ></carousel>
                </div>
                <!-- End CAROUSEL  -->
                <div v-if="field.type === 'BOOLEAN'" class="block">
                  <b-radio
                    :required="field.required"
                    :value="
                      newLayer.feature.properties[field.slug] != null 
                      && ( !!newLayer.feature.properties[field.slug] == true ||  !!newLayer.feature.properties[field.slug] == false)
                        ? JSON.parse(newLayer.feature.properties[field.slug])
                        : ''
                    "
                    :name="field.name"
                    native-value="true"
                  >
                    Oui
                  </b-radio>
                  <b-radio
                    :required="field.required"
                    :value="
                      newLayer.feature.properties[field.slug] != null 
                       && ( !!newLayer.feature.properties[field.slug] == true ||  !!newLayer.feature.properties[field.slug] == false)
                        ? JSON.parse(newLayer.feature.properties[field.slug])
                        : ''
                    "
                    :name="field.name"
                    native-value="false"
                  >
                    Non
                  </b-radio>
                </div>
                <b-input
                  :required="field.required"
                  validation-message="Veuillez remplir ce champ"
                  type="textarea"
                  v-if="field.type === 'TEXTAREA'"
                  :name="field.name"
                  :value="newLayer.feature.properties[field.slug]"
                  expanded
                >
                </b-input>
                <base-image-input
                  :key="field.slug"
                  style="width: 100% !important; height: 13rem"
                  v-if="field.type == 'IMAGE'"
                  :readOnly="false"
                  :imageUrl="
                    getImageUrl(newLayer.feature.properties[field.slug])
                  "
                  v-model="images[field.slug]"
                />
                <b-datepicker
                  :required="field.required"
                  v-if="field.type == 'DATE'"
                  :name="field.name"
                  :value="
                    newLayer.feature.properties[field.slug] &&
                    reverse(newLayer.feature.properties[field.slug])
                  "
                  placeholder="Selectionner une date"
                  icon="calendar-today"
                  editableb-datepicker
                >
                </b-datepicker>
                <b-datetimepicker
                  :required="field.required"
                  v-model="datetime[field.name]"
                  v-if="field.type == 'DATETIME'"
                  placeholder="Selectionner une date"
                  icon="calendar-today"
                  locale="fr-FR"
                  editable
                >
                </b-datetimepicker>
                <b-timepicker
                  :required="field.required"
                  v-if="field.type == 'TIME'"
                  :value="
                    newLayer.feature.properties[field.slug] &&
                    getDateFromTime(newLayer.feature.properties[field.slug])
                  "
                  rounded
                  placeholder="Cliquez pour sélectionner..."
                  icon="clock"
                  :name="field.name"
                >
                </b-timepicker>
              </b-field>
              <button
                v-if="
                  injectedIn === 'datatable' &&
                  ((permmissions.ENTITY_ELEMENT_CREATE_AUTHORITY &&
                    canEditFeature) ||
                    editGeometryMode ||
                    mode === 'edit' ||
                    (permmissions.ENTITY_ELEMENT_UPDATE_AUTHORITY &&
                      canEditFeature) ||
                    editGeometryMode ||
                    mode === 'edit')
                "
                :disabled="isLoading"
                :class="['button', 'is-primary', 'mt-2']"
                id="save"
                @click.prevent="onSubmit"
                style="background: $color-primary !important"
              >
                <b-icon class="mr-1" pack="fas" icon="filter" />Sauvegarder
              </button>
              <!--<b-field grouped>
                <b-field expanded label="Longitude">
                  <b-input
                    name="longitude"
                    placeholder="longitude"
                    type="text"
                    v-model="featureLayer.toGeoJSON().geometry.coordinates[0]"
                  >
                  </b-input>
                </b-field>
                <b-field expanded label="Latitude">
                  <b-input
                    name="latitude"
                    placeholder="latitude"
                    type="text"
                    v-model="featureLayer.toGeoJSON().geometry.coordinates[1]"
                  >
                  </b-input>
                </b-field>
              </b-field>-->
            </div>
          </b-tab-item>
        </b-tabs>
      </div>
      <div
        v-if="
          injectedIn !== 'datatable' &&
          currentMap.privacy.toLowerCase() != 'archived'
        "
        class="btn-group-vertical pl-1 feature__actions"
        role="group"
        aria-label="Basic example"
      >
        <!-- Start Save Feauture  -->
        <div
          v-if="
            (permmissions.ENTITY_ELEMENT_CREATE_AUTHORITY && canEditFeature) ||
            editGeometryMode ||
            mode === 'create' ||
            (permmissions.ENTITY_ELEMENT_UPDATE_AUTHORITY && canEditFeature) ||
            editGeometryMode ||
            mode === 'create'
          "
          class="action__wrapper"
        >
          <b-tooltip label="Sauvegarder" type="is-dark" position="is-bottom">
            <Button
              id="save-feature"
              type="submit"
              icon="pi pi-save"
              style="height: 41.3px"
              @click.prevent="onSubmit"
              :class="[
                'button',
                'p-button',
                'p-button-success',
                'btn-save',
                { 'is-loading': isLoading },
              ]"
              :disabled="mode == 'create' && !selectedType"
            />
          </b-tooltip>
          <p v-if="injectedIn === 'mobilemap'">Sauvegarder</p>
        </div>

        <!-- End Save Feauture  -->
        <!-- Start Delete Button  -->
        <div
          v-if="permmissions.ENTITY_ELEMENT_DELETE_AUTHORITY && mode === 'edit'"
          class="action__wrapper"
        >
          <b-tooltip label="Effacer" type="is-dark" position="is-bottom">
            <Button
              @click="deleteFeature"
              id="delete-feature"
              icon="pi pi-trash"
              style="height: 41.3px"
              :class="[
                'button',
                'p-button',
                'p-button-danger',
                'btn-save',
                { 'is-loading': isDeleteLoading },
              ]"
              :disabled="mode == 'create' && !selectedType"
            />
          </b-tooltip>
          <p v-if="injectedIn === 'mobilemap'">Effacer</p>
        </div>

        <!-- End Delete button  -->
        <!-- Start Edit Button  -->
        <div
          v-if="
            permmissions.ENTITY_ELEMENT_UPDATE_AUTHORITY &&
            mode == 'edit' &&
            !editGeometryMode
          "
          class="action__wrapper"
        >
          <b-tooltip label="Éditer" type="is-dark" position="is-bottom">
            <Button
              :disabled="canEditFeature"
              id="edit-feature"
              icon="pi pi-pencil"
              class="p-button p-button-primary btn-share"
              @click="editFeature"
            />
          </b-tooltip>
          <p v-if="injectedIn === 'mobilemap'">Éditer</p>
        </div>
        <!-- End Edit Button -->
        <!-- Start Nearby Button  -->
        <div v-if="mode !== 'create'" class="action__wrapper">
          <b-tooltip label="Proche" type="is-dark" position="is-bottom">
            <b-button
              class="p-button p-button-primary btn-share btn-proche"
              @click="showNearbySection"
              icon-left="fa fa-street-view"
              icon-pack="fas"
              v-if="mode != 'create'"
            >
            </b-button>
          </b-tooltip>
          <p v-if="injectedIn === 'mobilemap'">Proche</p>
        </div>
        <!-- End Nearby Button -->
        <!-- Start Itiniraire Button  -->
        <div
          v-if="activeLayer.topo === 'Point' && mode !== 'create'"
          class="action__wrapper"
        >
          <b-tooltip label="Itinéraire" type="is-dark" position="is-bottom">
            <Button
              pack="fas"
              icon="pi pi-directions"
              id="direction"
              class="p-button p-button-primary btn-share"
              @click="$emit('showDirection')"
              :disabled="mode == 'create' && !selectedType"
            />
          </b-tooltip>
          <p v-if="injectedIn === 'mobilemap'">Itinéraire</p>
        </div>
        <!-- End Itiniraire Button  -->
        <!-- Start Edit Feature -->
        <div
          v-if="
            permmissions.ENTITY_ELEMENT_UPDATE_AUTHORITY &&
            mode == 'edit' &&
            !canEditFeature
          "
          class="action__wrapper"
        >
          <b-tooltip
            label="Modifier la géométrie"
            type="is-dark"
            position="is-bottom"
          >
            <Button
              :disabled="editGeometryMode"
              id="edit-geometry"
              icon="pi pi-map-marker"
              class="p-button p-button-primary btn-share"
              @click="editGeometry"
            />
          </b-tooltip>
          <p v-if="injectedIn === 'mobilemap'">Géométrie</p>
        </div>
        <!-- End Edit Feauture -->
        <!-- Start Share Button  -->
        <div
          v-if="permmissions.ENTITY_ELEMENT_SHARE_AUTHORITY"
          class="action__wrapper"
        >
          <b-tooltip label="Parteger" type="is-dark" position="is-bottom">
            <Button
              icon="pi pi-share-alt"
              id="share"
              class="p-button p-button-primary btn-share"
              @click="cardModal"
            />
          </b-tooltip>
          <p v-if="injectedIn === 'mobilemap'">Parteger</p>
        </div>
        <!-- End Share Button  -->

        <!-- Start Cancel Button  -->
        <div v-if="injectedIn === 'map'" class="action__wrapper">
          <b-tooltip label="Annuler" type="is-dark" position="is-bottom">
            <Button
              id="cancel-feature-settings"
              icon="pi pi-times"
              class="p-button p-button-secondary btn-cancel"
              type="reset"
            />
          </b-tooltip>
        </div>
        <!-- End Cancel Button  -->
      </div>
      <div
        v-else-if="injectedIn === 'map'"
        class="feature__actions"
        style="position: absolute; top: 20px; !important"
      >
        <button
          id="cancel-feature-settings"
          type="reset"
          class="btn btn-secondary btn-md flex-grow-0"
          icon=""
        >
          <b-icon pack="fas" icon="times" />
        </button>
      </div>
    </div>
  </form>
</template>

<script>
import { backApi } from '../../methods/serverApi'
import { mapState, mapGetters } from 'vuex'
import * as icons from '~/assets/icons'
import { backend } from '../../constants'
import RestApi from '../../methods/api'
import BaseImageInput from '../image/BaseImageInput'
import { VueEditor } from 'vue2-editor'
import { ListSelect, MultiSelect } from 'vue-search-select'
import 'vue-search-select/dist/VueSearchSelect.css'
import vSelect from 'vue-select'
import 'vue-select/dist/vue-select.css'
import FeatureFields from './FeatureFields.vue'
import ShareCurrentMap from '../ShareCurrentMap'
import Button from 'primevue/button'
import carousel from '../ui/Carousel'
import nearby from './Nearby.vue'

export default {
  components: {
    BaseImageInput,
    VueEditor,
    ListSelect,
    MultiSelect,
    FeatureFields,
    ShareCurrentMap,
    Button,
    carousel,
    vSelect,
    nearby,
  },

  data() {
    return {
      listImagesUploaded: [],
      currentMap: this.$store.state.maps.currentMap,
      selectedType: '',
      activeTab: 0,
      defaultIcon: null,
      isLoading: false,
      isDeleteLoading: false,
      currentPage: 1,
      canEditFeature: false,
      perPage: 1,
      total: 1,
      rangeBefore: 3,
      rangeAfter: 1,
      order: '',
      size: 'is-small',
      isSimple: true,
      isRounded: false,
      prevIcon: 'chevron-left',
      nextIcon: 'chevron-right',
      featureLayer: {},
      editGeometryMode: false,
      getNearbyPoints: false,
      htmlContent: [],
      longitude: null,
      latitude: null,
      selectedOne: {},
      selectedMultiple: {},
      datetime: {},
      time: {},
      arr: [],
      imageObj: [],
      listImagesUploaded: [],
      listImagesToDeleted: [],
      imgUrls: [],
      getImages: [],
      imgUrls: [],
      urls: [],
      arry: [],
      imagesToUpload: {},
      imagesToUploadUrls: {},
      imageLoaded: false,
      images: {},
      imageInputKey: 1,
      permmissions: {
        ENTITY_ELEMENT_CREATE_AUTHORITY: false,
        ENTITY_ELEMENT_UPDATE_AUTHORITY: false,
        ENTITY_ELEMENT_DELETE_AUTHORITY: false,
        ENTITY_ELEMENT_SHARE_AUTHORITY: false,
      },
      OSRVData: [],
      OSRVSelectedValues: [],
      OSRVDisabled: [],
      OSRVid: 1,
      goTo: [],
      waypoints: [],
    }
  },
  /*
      mode :  création ou edition d'un POI
  */
  props: [
    'newLayer',
    'mode',
    'features',
    'loadingIndicator',
    'injectedIn',
    'toggleButton',
  ],
  computed: {
    ...mapState(['profile', 'maps']),
    ...mapGetters({
      isMobile: 'app/getIsMobile',
      mapLayers: 'maps/mapLayers'
    }),
    getLayersToAddOptions(){
      return  this.mapLayers && this.mapLayers.filter(l =>  l.topo === this.newLayer.feature.geometry.type).map(l => {
            return {
              ...l,
              name:
                l.name && l.name.includes('____')
                  ? l.name.split('____')[1]
                  : l.name
            }
          })
    },
    activeLayer() {
      return this.maps.activeLayer
    },
    activeLayerName() {
      return this.maps.activeLayer.name
    },
    layerCopy() {
      // keep a copy of the layer before edit, for history
      return { ...this.newLayer.feature }
    },
  },
  watch: {
    loadingIndicator(newVal) {
      if (!newVal) {
        this.isAuthorizedLayer()
        this.mode === 'edit' && this.assignModel()
      }
    },
    editGeometryMode(newVal, oldVal) {
      if (newVal !== oldVal) {
        if (newVal) {
          this.$store.dispatch('maps/updateCanGetFeatureInfo', false)
        } else {
          this.$store.dispatch('maps/updateCanGetFeatureInfo', true)
        }
      }
    },
  },
  methods: {
    goToElement(payload) {
      this.$emit('goToElement', payload)
    },
    setGoTo(coords) {
      this.$emit('setGoTo', coords)
    },
    setGoToWithTrigger(coords) {
      this.goTo = coords
      this.showDirection()
    },
    closePanel() {
      this.$emit('closed')
    },
    showNearbySection() {
      this.getNearbyPoints = true
      this.canEditFeature = false
      this.editGeometryMode = false
    },
    extractSelectedRV(rv) {
      if (rv != null) {
        let arr = rv.split(':')
        return {
          value: arr[0],
          text: arr[1],
        }
      } else {
        return {
          value: '',
          text: '',
        }
      }
    },
    isAuthorizedLayer() {
      let url = ''
      if (
        this.profile.authenticated &&
        this.newLayer &&
        this.newLayer.feature &&
        this.newLayer.feature.layer != undefined
      ) {
        for (let p of Object.keys(this.permmissions)) {
          if (this.profile.roles.includes('ROLE_ADMIN')) {
            this.permmissions[p] = true
          } else {
            if (p === 'ENTITY_ELEMENT_SHARE_AUTHORITY') {
              url = `layers/check-write-permission/${this.newLayer.feature.layer.slug}/${p}`
            } else {
              url = `layers/check-write-geometry-permission/${this.newLayer.feature.layer.slug}/${p}?entity-element=${this.newLayer.feature.id}`
            }
            backApi
              .get(url)
              .then(() => {
                this.permmissions[p] = true
              })
              .catch(() => {
                this.permmissions[p] = false
              })
          }
        }
      }
    },
    getUrls(url) {
      this.urls = url
      this.arry = this.urls
    },
    getherUrlsInArray(str) {
      let arr = []
      let res = ''
      if (str != null) {
        res = str.substring(1, str.length - 1).split(',')
      }
      for (var i = 0; i < res.length; i++) {
        arr.push(res[i])
      }
      this.getImages = arr
      return arr
    },
    removeafterUpolad(img) {
      let newObj = this.imageObj.filter((item) => item != img)
      this.imageObj = newObj
    },
    removeAfterSubmited(img, i) {
      console.log('image Afetr uploaded', img, i)
      document.getElementsByClassName(`container${i}`)[0].style.display = 'none'
      /*     let newObj = this.imageObj.filter((item) => item != img);
      console.log("New Object:", newObj);
      this.imageObj = newObj; */
    },
    cardModal() {
      this.$buefy.modal.open({
        parent: this,
        component: ShareCurrentMap,
        hasModalCard: true,
        customClass: 'custom-class custom-class-2',
        trapFocus: true,
      })
    },
    /***
     * retieve list items with this format {text: 'some text, value: 'value}
     */
    getMultiSelectsItems(item) {
      return (
        item &&
        item.map((f) => {
          return { text: f.value, value: f.value }
        })
      )
    },
    /***
     * get the selected items that belongs to field name
     */
    getSelectedItems(fieldName) {
      return (
        this.selectedMultiple[fieldName] &&
        this.selectedMultiple[fieldName].values
      )
    },
    onSelect(event, field) {
      this.selectedOne[field.name].text = event ? event.text : ""
      this.selectedOne[field.name].value = event ? event.value : ""
      this.selectedOne[field.name].dirty = true
      this.OSRVid++
      this.activeLayer.fields
        .filter((f) => f.type == 'SELECT' && f.parent == field.id)
        .forEach((f) => {
          // if (f.parent == field.id) {
          let resourceId = f.resource.id
          let parentId = event ? event.value : ""

          //reset the values of childs
          this.resetSelect(f)

          if (resourceId != null && parentId != null) {
            RestApi.getRVChilds(resourceId, parentId).then((data) => {
              this.OSRVData[f.id] = data.map((f) => {
                return { text: f.value, value: f.id }
              })
              this.OSRVid++
            })
          }

          this.OSRVDisabled[f.id] = false
          //}
        })
    },
    resetSelect(field) {
      this.OSRVSelectedValues[field.id] = ''
      this.selectedOne[field.name].text = ''
      this.selectedOne[field.name].value = ''
      this.activeLayer.fields
        .filter((f) => f.type == 'SELECT' && f.parent == field.id)
        .forEach((f) => {
          this.OSRVData[f.id] = []
          this.OSRVSelectedValues[f.id] = ''
          this.selectedOne[f.name].text = ''
          this.selectedOne[f.name].value = ''
          this.resetSelect(f)
        })
    },
    onMultiSelect(items, fieldName) {
      this.selectedMultiple[fieldName].values = items
      this.selectedMultiple[fieldName].dirty = true
      this.$forceUpdate()
    },
    loadRV() {
      this.activeLayer.fields
        .filter((f) => f.type == 'SELECT')
        .forEach((field) => {
          if (field.resource != null && field.parent == null) {
            this.OSRVData[field.id] = field.resource.resourceValues.map((f) => {
              return { text: f.value, value: f.id }
            })
          } else if (this.mode != 'edit') {
            this.OSRVDisabled[field.id] = true
          }
        })
    },

    /***
     * fill in the default selected items
     */
    assignModel() {
      if (this.mode !== 'edit') {
        for (const f of this.activeLayer.fields) {
          if (f.type === 'MULTI_SELECT') {
            this.selectedMultiple[f.name] = {
              values: [],
              dirty: false,
            }
          } else if (f.type === 'SELECT') {
            this.selectedOne[f.name] = {
              value: '',
              text: '',
              dirty: false,
            }
          }
        }
      } else {
        for (const f of this.activeLayer.fields) {
          if (f.type === 'MULTI_SELECT') {
            this.selectedMultiple[f.name] = {
              values: this.newLayer.feature.properties[f.slug]
                ? (typeof this.newLayer.feature.properties[f.slug] === 'string' ? this.newLayer.feature.properties[f.slug]
                    .replace('[', '')
                    .replace(']', '')
                    .split(',')
                    .map((v) => {
                      return { value: v, text: v }
                    })
                : this.newLayer.feature.properties[f.slug]
                    .map((v) => {
                      return { value: v, text: v }
                    })) : [],
              dirty: false,
            }
          } else if (f.type === 'SELECT') {
            this.selectedOne[f.name] = {
              value: this.extractSelectedRV(
                this.newLayer.feature.properties[f.slug]
              ).value,
              text: this.extractSelectedRV(
                this.newLayer.feature.properties[f.slug]
              ).text,
              dirty: false,
            }
          } else if (f.type === 'DATETIME') {
            console.log(this.newLayer.feature.properties[f.slug])
            this.datetime[f.name] =
              this.newLayer.feature.properties[f.slug] && this.newLayer.feature.properties[f.slug] != 'Invalid Date' 
                ? new Date(
                    this.reverseDT(this.newLayer.feature.properties[f.slug])
                  )
                : ''
          } else if (f.type === 'CAROUSEL') {
            this.imagesToUploadUrls[f.id] =
              this.newLayer.feature.properties[f.slug]
          }
        }
      }
    },
    getDateFromTime(time) {
      let res = ''
      let date = ''
      if (time && time != '') {
        res = time.split(':')
        date = new Date()
        date.setHours(res[0])
        date.setMinutes(res[1])
        date.setSeconds(0)
      }
      return date
    },
    getFeature(pageNumber) {
      if (this.injectedIn !== 'datatable') {
        this.newLayer = this.features[pageNumber - 1]

        if (this.newLayer.feature.layer.name != this.activeLayer.name) {
          backApi
            .get(
              `layers/withFieldsAndResource/maps/${this.currentMap.slug}/feature/${this.newLayer.feature.layer.id}`
            )
            .then(({ data }) => {
              this.$store.commit('maps/setActiveLayer', data)
              this.isAuthorizedLayer()
            })
        }

        if (this.editGeometryMode) {
          this.featureLayer.disableEdit()
          this.$map.removeLayer(this.featureLayer)

          let feature = this.features[this.currentPage].feature
          let iconUrl = feature.layer.customIcon ? feature.layer.iconUrl : null

          let layer = L.geoJSON(feature, {
            pointToLayer: (feature, latlng) => {
              const icon = L.icon({
                iconUrl,
                iconSize: [48, 48],
                popupAnchor: [0, -32],
              })
              return L.marker(
                latlng,
                iconUrl
                  ? {
                      icon,
                    }
                  : null
              )
            },
          }).addTo(this.$map)

          let layers = layer._layers
          this.featureLayer = layers[Object.keys(layers)[0]]
          this.featureLayer.enableEdit()
        } else {
          if (this.$layerGroups['featureInfo'])
            this.$map.removeLayer(this.$layerGroups['featureInfo'])

          let feature = this.newLayer.feature
          let iconUrl = feature.layer.customIcon ? feature.layer.iconUrl : null

          let marker = (latlng) => {
            if (iconUrl) return L.marker(latlng, null)
            else
              return L.circleMarker(latlng, {
                fillOpacity: 0.5,
                radius: 20,
              })
          }

          this.$layerGroups['featureInfo'] = L.geoJSON(feature, {
            pointToLayer: (feature, latlng) => {
              return marker(latlng)
            },
          }).addTo(this.$map)
        }
      }
    },
    editFeature() {
      this.getNearbyPoints = false
      let feature = this.newLayer.feature

      let editors = this.maps.activeLayer.fields.filter(
        (field) => field.type == 'HTMLEDITOR'
      )
      editors.forEach((editor) => {
        this.htmlContent[editor.order] = feature.properties[editor.slug]
      })

      this.maps.activeLayer.fields
        .filter((field) => field.type == 'SELECT')
        .forEach((rvf) => {
          this.OSRVSelectedValues[rvf.id] = this.extractSelectedRV(
            feature.properties[rvf.slug]
          )

          this.activeLayer.fields
            .filter((field) => field.type == 'SELECT')
            .forEach((f) => {
              if (f.parent == rvf.id) {
                let resourceId = f.resource.id
                let parentId = this.extractSelectedRV(
                  feature.properties[rvf.slug]
                ).value
                RestApi.getRVChilds(resourceId, parentId).then((data) => {
                  this.OSRVData[f.id] = data.map((f) => {
                    return { text: f.value, value: f.id }
                  })
                  this.OSRVid++
                })
                this.OSRVDisabled[f.id] = false
              }
            })
          this.loadRV()
        })
      if (this.mode === 'read') {
        this.canEditFeature = false
      } else {
        this.canEditFeature = true
      }
    },
    editGeometry() {
      this.getNearbyPoints = false
      if (this.$layerGroups['featureInfo'])
        this.$map.removeLayer(this.$layerGroups['featureInfo'])
      this.editGeometryMode = true
      let feature = this.features[this.currentPage - 1].feature
      let iconUrl = feature.layer.customIcon ? feature.layer.iconUrl : null

      let layer = L.geoJSON(feature, {
        pointToLayer: (feature, latlng) => {
          const icon = L.icon({
            iconUrl,
            iconSize: [48, 48],
            popupAnchor: [0, -32],
          })
          return L.marker(
            latlng,
            iconUrl
              ? {
                  icon,
                }
              : null
          )
        },
      }).addTo(this.$map)

      let layers = layer._layers
      this.featureLayer = layers[Object.keys(layers)[0]]
      this.featureLayer.enableEdit()
    },
    deleteFeature() {
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
            `${this.newLayer.feature.id}/layers/${this.newLayer.feature.layer.slug}`,
            null,
            this.$router,
            this.$notification
          )
            .then((res) => {
              this.$layerGroups[`wmsLayer`].setParams(
                {
                  fake: Date.now(),
                },
                false
              )
              this.$map.removeLayer(this.$layerGroups['featureInfo'])

              this.isDeleteLoading = false
              this.$emit('save')
            })
            .catch((error) => {
              this.isDeleteLoading = false
              this.$emit('save')
            })
        },
        onCancel: () => {},
      })
    },
    onTypeSelect(layer) {
      backApi
        .get(
          `layers/withFieldsAndResource/maps/${this.currentMap.slug}/feature/${layer.id}`
        )
        .then(({ data }) => {
          this.$store.commit('maps/setActiveLayer', data)

          /* when user clicks an icon on the feature selector */
          this.newLayer.featureType = layer.slug
          this.selectedType = layer.slug
          this.activeTab = 1
          this.assignModel()
          this.loadRV()
          if (this.newLayer.feature.geometry.type === 'Point')
            this.newLayer.setIcon(
              L.icon({
                iconUrl:
                  layer.iconUrl === null
                    ? icons.default['defaultIcon']
                    : layer.iconUrl,
                iconSize: [48, 48],
                popupAnchor: [0, -32],
              })
            )
        })
    },
    reverse(s) {
      return  new Date(s)
    },
    reverseDT(s) {
      if (s) {
        let d = s && s.split(',')
        let r = d[0].split('/').reverse().join('/')

        return r + ',' + d[1]
      }
    },
    async onSubmit(e) {
      let form = this.$refs.form
      form.reportValidity()
      let formIsValid = form.checkValidity()
      if (!formIsValid && this.editGeometryMode) {
        this.mode = 'edit'
        this.editGeometryMode = false
        this.canEditFeature = true
        this.featureLayer.disableEdit()
        if (this.$layerGroups['featureInfo'])
          this.$map.removeLayer(this.$layerGroups['featureInfo'])
        if (this.featureLayer) this.$map.removeLayer(this.featureLayer)
        setTimeout(() => {
          form.reportValidity()
        }, 100)
      } else if (formIsValid) {
        this.imageLoaded = true
        const formData = new FormData(form)
        const layerId = this.newLayer.feature.id
        let valid = true
        let newProps = {}
        // cast form data to JSON object
        for (const field of this.activeLayer.fields) {
          if (field.type === 'SELECT' && this.selectedOne[field.name]) {
            newProps[field.name] =
              this.selectedOne[field.name].value +
              ':' +
              this.selectedOne[field.name].text
          } else if (field.type === 'MULTI_SELECT') {
            newProps[field.name] =
              this.selectedMultiple[field.name].values.length > 0
                ? this.selectedMultiple[field.name].values.map((f) => f.value)
                : null

            valid =
              field.required &&
              this.selectedMultiple[field.name].values.length > 0
                ? true
                : !field.required
                ? true
                : false
          } else if (field.type === 'HTMLEDITOR') {
            newProps[field.name] = this.htmlContent[field.order]
          } else if (
            // upload the images if and only if the layer ID exists
            field.type === 'IMAGE' &&
            layerId
          ) {
            if (Object.keys(this.images).length > 0) {
              if (this.images[field.slug]) {
                //les noms des dossiers (le path) doit etre séparer par des points c'est une régle à suivre example layers.layers_id.feature_id == layers/lauers_id/feature_id
                let fileUrl = await RestApi.saveFileInFolder(
                  'layers.' + this.activeLayer.id + '.' + layerId,
                  this.images[field.slug]
                )
                newProps[field.name] = `${fileUrl}`
              } else {
                newProps[field.name] =
                  this.newLayer.feature.properties[field.slug]
              }
            } else {
              newProps[field.name] =
                this.newLayer.feature.properties[field.slug]
            }
          } else if (field.type === 'CAROUSEL' && layerId) {
            if (
              this.imagesToUpload[field.id] &&
              this.imagesToUpload[field.id].length > 0
            ) {
              for (let image of this.imagesToUpload[field.id]) {
                let path = await RestApi.saveFileInFolder(
                  'layers.' +
                    this.activeLayer.id +
                    '.' +
                    layerId +
                    '.carousel.' +
                    field.id,
                  image
                )
                this.imagesToUploadUrls[field.id].push(path)
              }
            }
            newProps[field.name] =
              this.imagesToUploadUrls[field.id] &&
              this.imagesToUploadUrls[field.id]
          } else if (field.type === 'DATE' || field.type === 'TIME') {
            if (!this.time[field.name]) {
              this.time[field.name] = formData.get(field.name)
            }
            newProps[field.name] = this.time[field.name]
              ? this.time[field.name]
              : formData.get(field.name)
          } else if (field.type === 'DATETIME') {
            const options = {
              year: 'numeric',
              month: 'numeric',
              day: 'numeric',
              hour: 'numeric',
              minute: 'numeric',
              second: 'numeric',
            }
            if (
              this.datetime[field.name] != undefined &&
              this.datetime[field.name] != ''
            ) {
              newProps[field.name] = new Date(
                this.datetime[field.name]
              ).toLocaleDateString('fr-FR', options)
            } else {
              newProps[field.name] = ''
            }
          } else {
            newProps[field.name] = formData.get(field.name)
          }
        }
        this.newLayer.feature.properties = newProps
        // set layers's GeoJSON data
        /*  if (this.mode === 'create')  */
        if (this.editGeometryMode) {
          let coords = []
          coords[1] = parseFloat(formData.get('latitude'))
          coords[0] = parseFloat(formData.get('longitude'))
          if (this.featureLayer.feature.geometry.type == 'Point')
            this.newLayer.feature.geometry.coordinates = coords
          else
            this.newLayer.feature.geometry.coordinates =
              this.featureLayer.toGeoJSON().geometry.coordinates

          this.editGeometryMode = false
          this.featureLayer.disableEdit()
          if (this.$layerGroups['featureInfo'])
            this.$map.removeLayer(this.$layerGroups['featureInfo'])
          if (this.featureLayer) this.$map.removeLayer(this.featureLayer)
          // update layer's GeoJSON data
          if (this.mode === 'edit') {
            if (this.$layerGroups['featureInfo'])
              this.$map.removeLayer(this.$layerGroups['featureInfo'])
            this.newLayer.feature.id = layerId
            delete this.newLayer.feature.layer
          }
          if (!this.profile.roles.includes('ROLE_ADMIN')) {
            RestApi.isAuthorizedArea(this.newLayer.feature.geometry)
              .then(() => {
                if (valid) {
                  this.isLoading = true
                  this.saveFeature(e, layerId)
                }
              })
              .catch(() => {
                this.$notification.warning({
                  message: 'Attention !',
                  description:
                    "Vous n'êtes pas autorisé à modifier la géométrie ici!!",
                })
              })
          } else {
            if (valid) {
              this.isLoading = true
              this.saveFeature(e, layerId)
            }
          }
        } else {
          // update layer's GeoJSON data
          if (this.mode === 'edit') {
            if (this.$layerGroups['featureInfo'])
              this.$map.removeLayer(this.$layerGroups['featureInfo'])
            this.newLayer.feature.id = layerId
            delete this.newLayer.feature.layer
          }

          if (valid) {
            this.isLoading = true
            this.saveFeature(e, layerId)
          }
        }
      }
    },
    onCancel() {
      // remove new layer (no layer id)
      if (this.$layerGroups['featureInfo'])
        this.$map.removeLayer(this.$layerGroups['featureInfo'])

      if (this.editGeometryMode) {
        this.featureLayer.disableEdit()
        this.$map.removeLayer(this.featureLayer)
        this.$store.dispatch('maps/updateCanGetFeatureInfo', true)
      }

      if (!this.newLayer.feature.id) this.$DrawLayer.removeLayer(this.newLayer)
      // reset layer on edit mode

      //Set edit geometry mode to false
      this.$emit('cancel')
    },
    saveFeature(e, id) {
      const method = this.mode === 'create' ? 'post' : 'put'
      const layerId = id ? id : ''
      const layer = this.selectedType || this.newLayer.featureType
      this.newLayer.layer = this.activeLayer

      backApi[method](
        `entityelements/${layer}/${layerId}`,
        this.newLayer.feature
      )
        .then(({ data: { id } }) => {
          const layerSlug = this.selectedType || this.newLayer.featureType

          this.newLayer.feature.id = id
          this.newLayer.feature.layer = this.activeLayer
          this.isLoading = false

          if (!layerId == '') {
            this.$notification.success({
              message: 'La sauvegarde est réussie avec succés',
            })
          }

          if (this.mode === 'create') {
            let symbologyType = this.newLayer.layer.symbologyType

            this.$DrawLayer.removeLayer(this.newLayer)
            this.$layerGroups[`wmsLayer`].setParams({ fake: Date.now() }, false)
          }

          if (this.mapLayers != null && this.mode === 'edit') {
            this.$layerGroups[`wmsLayer`].setParams({ fake: Date.now() }, false)
            this.mapLayers.forEach((layer) => {
              if (this.$layerGroups[`${layer.slug}_filter`] != null) {
                this.$map.removeLayer(this.$layerGroups[`${layer.slug}_filter`])
              }
            })

            if (this.$layerGroups['customMarker'] != null)
              this.$map.removeLayer(this.$layerGroups['customMarker'])

            if (this.$layerGroups['filterLayer'] != null)
              this.$map.removeLayer(this.$layerGroups['filterLayer'])
            //Update the layer images folder
            for (let slug of Object.keys(this.imagesToUploadUrls)) {
              if (this.imagesToUploadUrls[slug]) {
                backApi.put(
                  `delete/layers.${this.activeLayer.id}.${this.newLayer.feature.id}.carousel.${slug}`,
                  this.imagesToUploadUrls[slug]
                )
              }
            }
          }
          // Resubmit the form  to solve the undefined folder issue
          if (layerId == '') {
            this.mode = 'edit'
            this.onSubmit(e)
          }
          if (this.injectedIn === 'datatable') {
            this.$emit('update', {
              ...this.newLayer.feature.properties,
              id: this.newLayer.feature.id,
              geometry: this.newLayer.feature.geometry,
            })
          }
          this.$emit('save')
        })
        .catch((error) => {
          this.isLoading = false
          this.$DrawLayer.removeLayer(this.newLayer)
          this.$notification.error({
            message:
              error.response && error.response.data
                ? error.response.data.message
                : "l'opération de mise à jour a échoué !",
          })
          this.$emit('save')
        })
    },
    /**
     * if lengt >2
     */

    // Carousel Methods
    getImageUrl(path) {
      return path
        ? `${backend}/download/layers.${this.activeLayer.id}.${this.newLayer.feature.id}/${path}`
        : null
    },
    setImagesToUpload({ fieldName, imagesToUpload }) {
      this.imagesToUpload[fieldName] = imagesToUpload
    },
    setImagesUrlToUpload({ fieldName, imagesToUploadUrls }) {
      this.imagesToUploadUrls[fieldName] = imagesToUploadUrls
    },
  },
  beforeUpdate() {
    // this.getImagesFromDb();
    if (this.features) {
      let old = this.total
      this.total = this.features.length
      if (old != this.total) {
        this.currentPage = 1
      }
    }
  },
  beforeMount() {
    this.arry = this.urls
    if (this.features) {
      this.total = this.features.length
    }
    if (this.mode === 'edit') {
      this.activeTab = 1
    }
    if (this.injectedIn === 'datatable') {
      this.activeTab = 1
      this.editFeature()
      this.assignModel()
    }
    this.defaultIcon = icons.default['defaultIcon']
  },
  beforeDestroy() {
    //remove etiniraire
    if (!this.isMobile && this.$layerGroups['itinerary'] != null) {
      let path = document.querySelector('.leaflet-routing-container')
      if (path)
        setTimeout(() => {
          path.style.marginRight = '-443px'
        }, 300)
      this.$emit('closed')
      this.$map.removeControl(this.$layerGroups['itinerary'])
      this.$layerGroups['itinerary'] = null
    }
  },
}
</script>

<style lang="scss">
@include respond('phone') {
  .feature-wrapper {
    flex-direction: column-reverse !important;
    height: 100%;
  }
}
.feature {
  position: relative;
  z-index: 400;
  top: 11px;
  left: 50px;
  height: 100%;
  max-height: 100%;
  width: 27.5rem;
  @include mapModalForMobile;
  @include respond('phone') {
    left: unset;
    max-height: 60vh;
  }
  .button:focus {
    color: #fff !important;
  }
  &.fixed-height {
    height: 60vh !important;
    .feature-wrapper {
      justify-content: flex-end;
    }
  }
  nav {
    overflow: visible;
    .layer-name {
      display: block;
      width: 350px;
      text-overflow: ellipsis;
      white-space: nowrap;
      overflow: hidden;
      &.reduce {
        width: 250px;
      }
    }
  }
  &-datatable {
    top: 0;
    right: 0;
    left: inherit;
    .card {
      width: 100% !important;
    }
    .b-tabs .tab-content {
      margin-top: 0 !important;
    }
    .tabs {
      display: none;
    }
  }
  &-map-mobile {
    .card {
      position: static !important;
      top: unset;
      left: unset;
      right: unset;
      width: 100vw !important;
      max-width: 100vw !important;
      overflow-x: hidden;
      .b-tabs {
        width: 100% !important;
      }
    }
    .tabs {
      display: flex;
    }
  }
  &__actions {
    position: absolute;
    top: 200px;
    right: -0.7rem;
    transform: translateY(-50%);
    @include hideScroll;
    button {
      width: 3.2rem !important;
    }
    @include respond('phone') {
      position: static;
      top: unset;
      right: unset;
      overflow-x: scroll;
      overflow-y: hidden;
      white-space: nowrap;
      transform: none;
      display: block;
      margin: 9px 2rem 0 2rem;
      min-height: 55px;
      text-align: center;
      .b-tooltip.is-dark:after,
      .b-tooltip.is-bottom.is-dark:before {
        display: none;
      }
      .action__wrapper {
        display: inline-block;
        vertical-align: top;
        width: 18%;
        margin-top: 3px;
        button {
          background-color: $color-primary !important;
          border-color: $color-primary !important;
          height: 2.8rem !important;
          width: 2.8rem !important;
          border-radius: 50%;
        }
        p {
          width: 100%;
          margin-top: 5px;
          line-height: 13px;
        }
      }
    }
  }
  .b-tabs {
    nav::-webkit-scrollbar {
      background: transparent;
      width: 0;
    }
    .tab-content {
      height: 100% !important;
      margin-top: 40px;
      height: 100%;
      padding: 0 1rem !important;
    }
  }
  .tab-content {
    overflow-y: auto !important;
    height: 100% !important;
    max-height: 100% !important;
    padding: 0 1rem !important;
    @include respond('phone') {
      overflow-y: visible;
      max-height: fit-content;
    }
  }
  .modal-card .tabs ul {
    margin-bottom: 1rem !important;
  }
  .b-tabs {
    position: relative;
    height: 100%;
    max-height: 100%;
    display: flex;
    overflow: hidden;
    flex-direction: column;
  }
  .tabs {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    z-index: 500;
    padding: 0;
    height: fit-content;
  }
  .card {
    position: absolute;
    width: 90%;
    height: 97%;
    max-height: 97%;
    @include mapModalForMobile;
    &--collapsed {
      width: 100%;
      position: static;
      margin-top: 1rem;
      height: fit-content;
      @include respond('phone') {
        margin-top: 0;
        margin-bottom: 1rem;
      }
    }
    .card-header {
      height: 50px !important;
    }
    .card-content .content {
      display: flex;
      justify-content: center;
    }
  }
}
.carousel-wrapper {
  display: flex;
  justify-content: center;
  width: 100%;
  height: 17rem;
  > div {
    width: 100%;
    height: 100;
  }
  .image-container {
    width: 100% !important;
    height: 85% !important;
    > div {
      display: flex;
      flex-direction: column;
      height: 100%;
      .preview-image {
        position: relative;
        flex-grow: 1 !important;
        .image-overlay {
          height: 85%;
        }
        .show-image.centered {
          width: 97%;
          height: 81%;
          top: 43%;
          img {
            width: 100%;
            height: 100%;
            max-height: 100%;
            max-width: 100%;
          }
        }
      }
      .image-bottom {
        bottom: -5px;
      }
      .image-bottom-left {
        visibility: hidden !important;
      }
    }
  }
}
.feature .tabs ul,
#feature-type-list ul {
  margin-bottom: 0 !important;
}
.fields-wrapper {
  padding: 1rem 0;
}
.ui.selection.dropdown {
  box-shadow: inset 0 1px 2px rgba(10, 10, 10, 0.1);
  .menu {
    border-color: $color-primary !important;
  }
}
.ui.selection.dropdown:focus,
.ui.selection.active.dropdown:hover {
  border-color: $color-primary;
  box-shadow: 0 0 0 0.125em rgba($color-primary, 0.25) !important;
}
form .upload-images {
  display: block;
  margin-top: 10px;
  text-align: center;
  border: 1px solid #555;
}
form .layer-icon:not(:disabled) {
  cursor: pointer;
}
form .layer-icon:not(:disabled):hover {
  border: 1px solid #dbdbdb;
}
form .carsoule {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

form .layer-icon:disabled {
  cursor: not-allowed !important;
}
form .btn-share {
  color: #fff;
  margin-bottom: 5px;
  width: 3.2rem;
  background: #209cee;
  border: 1px solid #209cee;
  height: 2.5rem;
}
:is(form) .btn-delete,
:is(form) .btn-cancel,
:is(form) .btn-save {
  width: 3.2rem;
  margin-bottom: 5px;
}
.btn-proche:disabled {
  background-color: #209cee !important;
  opacity: 0.65 !important;
  &:hover i {
    color: white !important;
  }
}
</style>
<style>
.myDivIcon {
  border: none;
}
</style>
