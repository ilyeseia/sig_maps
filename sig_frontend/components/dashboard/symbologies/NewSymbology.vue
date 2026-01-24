<template>
  <form ref="symbologyForm">
    <div class="symbology modal-card">
      <header class="modal-card-head">
        <h1 class="modal-card-title">
          Gestion de la symbologie de couche
          <span :tooltip="layerToEdit.name" class="text-info">
            {{ layerToEdit.name }}
          </span>
        </h1>
      </header>
      <!-- Start Style Box-->
      <section class="modal-card-body">
        <b-tabs v-model="activeTab" id="feature-type-list">
          <b-tab-item label="Symbologie">
            <template default>
              <b-tabs
                v-model="symbologyTypeTab"
                type="is-toggle"
                expanded
                id="symbologie-tabs"
              >
                <!-- Start Simple Style  -->
                <b-tab-item label="Simple">
                  <simple
                    id="simple"
                    :layerToEdit="layerToEdit"
                    :mapSlug="mapSlug"
                    :label="label"
                    ref="SimpleComponent"
                    @saveStyle="saveStyle"
                    @setLabel="setLabel"
                  ></simple>
                </b-tab-item>
                <!-- End Simple Style  -->
                <!-- Start Classification Style  -->
                <b-tab-item label="Categorisé">
                  <classify
                    id="classfy"
                    :label="label"
                    :layerToEdit="layerToEdit"
                    :mapSlug="mapSlug"
                    ref="ClassifyComponent"
                    @saveStyle="saveStyle"
                    @setLabel="setLabel"
                    @setButtonDisabled="setButtonDisabled"
                  ></classify>
                </b-tab-item>
                <b-tab-item label="Dégradé">
                  <graduated
                    id="graduated"
                    :label="label"
                    :layerToEdit="layerToEdit"
                    :mapSlug="mapSlug"
                    ref="GraduatedComponent"
                    @saveStyle="saveStyle"
                    @setLabel="setLabel"
                    @setButtonDisabled="setButtonDisabled"
                  ></graduated>
                </b-tab-item>
                <!-- Start Classification Style  -->
                <!-- Start Cluser Style  -->
                <b-tab-item v-if="this.layerToEdit" label="Cluster">
                  <cluster
                    id="cluster"
                    :label="label"
                    :mapSlug="mapSlug"
                    :layerToEdit="layerToEdit"
                    ref="ClusterComponent"
                    @saveStyle="saveStyle"
                    @setLabel="setLabel"
                  ></cluster>
                </b-tab-item>
                <!-- End Cluser Style  -->
                <!-- Start Group of Points -->
                <b-tab-item
                  v-if="layerToEdit.topo === 'Point'"
                  label="Group de point"
                >
                  <icon-group
                    id="IconGroup"
                    :label="label"
                    :mapSlug="mapSlug"
                    :layerToEdit="layerToEdit"
                    ref="IconGroupComponent"
                    @saveStyle="saveStyle"
                    @setLabel="setLabel"
                    @setButtonDisabled="setButtonDisabled"
                  ></icon-group>
                </b-tab-item>
                <!-- End Group of Points -->
                <!-- Start Heat Map Style  -->
                <b-tab-item label="HeatMap">
                  <heat-map
                    id="HeatMap"
                    :label="label"
                    :mapSlug="mapSlug"
                    :layerToEdit="layerToEdit"
                    ref="HeatMapComponent"
                    @saveStyle="saveStyle"
                    @setLabel="setLabel"
                  ></heat-map>
                </b-tab-item>
                <!-- End Heat Map Style  -->
                <!--               <b-tab-item label="Personaliser">
                <b-field class="file" label="Charger le style"> </b-field>
                <b-field>
                  <b-upload v-model="customSLDFile">
                    <a class="button">
                      <b-icon icon="upload"></b-icon>
                      <span>Click to upload</span>
                    </a>
                  </b-upload>
                  <span class="file-name" v-if="customSLDFile">
                    {{ customSLDFile.name }}
                  </span>
                </b-field>
              </b-tab-item> -->
              </b-tabs>
            </template>
          </b-tab-item>
          <!-- Start Etiquette  -->
          <b-tab-item
            v-if="
              this.currentSymbologyType !== 'ClusterComponent' &&
              this.currentSymbologyType !== 'HeatMapComponent'
            "
            label="Etiquette"
          >
            <etiquette
              :layerToEdit="layerToEdit"
              :label="label"
              @setLabel="setLabel"
              @setLabelEnabled="setLabelEnabled"
              :labelEnabled="labelEnabled"
            ></etiquette>
          </b-tab-item>
          <!-- End Etiquette  -->
        </b-tabs>
      </section>
      <!-- End styele Box -->
      <!-- Start Action Box -->
      <footer
        class="modal-card-foot"
        style="justify-content: space-between !important"
      >
        <h3>
          Style :
          <span class="text-info">
            {{ getStyleName }}
          </span>
        </h3>
        <div>
          <button
            id="close-layer-form"
            class="button"
            type="button"
            @click="$parent.close()"
          >
            Fermer
          </button>
          <button
            id="save-layer"
            type="submit"
            :disabled="buttonDisabled"
            :class="['button', 'is-primary', { 'is-loading': isLoading }]"
            @click.prevent="onSubmit"
          >
            Enregistrer
          </button>
        </div>
      </footer>
      <!-- End Action Box -->
    </div>
  </form>
</template>
<script>
import BaseImageInput from '../../image/BaseImageInput'
import Vue from 'vue'
import { ColorPickerPlugin } from '@syncfusion/ej2-vue-inputs'
import { enableRipple } from '@syncfusion/ej2-base'

enableRipple(true)
Vue.use(ColorPickerPlugin)

import Simple from './types/Simple'
import Classify from './types/Classify'
import Graduated from './types/Graduated'
import Cluster from './types/Cluster'
import IconGroup from './types/IconGroup'
import HeatMap from './types/HeatMap'
import Etiquette from './etiquette/Etiquette'


import Loader from '../../ui/Loader'
import { backApi } from '../../../methods/serverApi'

export default {
  components: {
    BaseImageInput,
    Simple,
    Classify,
    Graduated,
    Cluster,
    IconGroup,
    HeatMap,
    Etiquette,
    Loader,
  },
  props: ['layerToEdit', 'mapSlug'],
  data() {
    return {
      isLoading: false,
      currentSymbologyType: '',
      symbologyTypeTab: null,
      symbologyType: null,
      labelEnabled: false,
      defaultLabelEnabled: false,
      buttonDisabled: false,
      activeTab: 0,
      label: {
        policeSize: '12',
        police: 'Arial',
        fontWeight: 'normal',
        fontStyle: 'normal',
        fill: '#000000',
        property: null,
        anchorPointX: 0.5,
        andchorPointY: 0.5,
        rotation: 0,
        perpendicularOffset: 1,
      },
    }
  },
  watch: {
    symbologyTypeTab(newVal, oldVal) {
      if (newVal !== oldVal) {
        let style = JSON.parse(this.layerToEdit.style.style)
        switch (this.symbologyTypeTab) {
          case 0: // Simple
            this.currentSymbologyType = 'SimpleComponent'
            this.labelEnabled =
              style &&
              style.symbologyType === 'Simple' &&
              this.defaultLabelEnabled
            break
          case 1: // Classify
            this.currentSymbologyType = 'ClassifyComponent'
            this.labelEnabled =
              style &&
              style.symbologyType === 'Classify' &&
              this.defaultLabelEnabled
            break
          case 2: // Graduated
            this.currentSymbologyType = 'GraduatedComponent'
            this.labelEnabled =
              style &&
              style.symbologyType === 'Graduated' &&
              this.defaultLabelEnabled
            break
          case 3: // Cluster
            this.currentSymbologyType = 'ClusterComponent'
            this.labelEnabled = false
            break
          case 4: // IconGroup
            this.currentSymbologyType = 'IconGroupComponent'
            this.labelEnabled =
              style &&
              style.symbologyType === 'IconGroup' &&
              this.defaultLabelEnabled
            break
          case 5: // HeatMap
            this.currentSymbologyType = 'HeatMapComponent'
            this.labelEnabled = false
            break
        }
      }
    },
  },
  computed: {
    getStyleName() {
      if (this.layerToEdit.style) {
        return this.layerToEdit.style.name.split('__')[1] === 'default'
          ? 'par default'
          : this.layerToEdit.style.name.split('__')[1]
      }
    },
  },
  methods: {
    async onSubmit() {
      let form = this.$refs.symbologyForm
      form.reportValidity()
      if (form.checkValidity()) {
        this.isLoading = true
        this.$refs[this.currentSymbologyType].onSubmit()
      } else if (this.labelEnabled && this.activeTab === 0) {
        this.activeTab = 1
        setTimeout(() => {
          form.reportValidity()
        }, 100)
      }
      // TODO: //Custom style component
      // let thisRef = this
      // LayerSymbolizer.generateCustomStyle(
      //   this.layerToEdit.slug,
      //   this.customSLDFile,
      //   (style) => {
      //     RestApi.updateStyle(style, thisRef.layerToEdit.slug)
      //     newLayer.symbologyType = 'Custom'
      //     thisRef.saveStyle(newLayer)
      //   }
      // )
    },
    showNotificationError(error) {
      this.isLoading = false
      this.$emit('close')
      this.$notification.error({
        message: 'Erreur !',
        description:
          error.response && error.response.data
            ? error.response.data.message
            : "Une erreur inattendue s'est produite",
      })
    },
    saveStyle(style) {
      let payload = {
        ...this.layerToEdit.style,
        name: this.layerToEdit.style.name.split('__')[1],
        styleConfig: style,
        layer: {
          id: this.layerToEdit.id,
          slug: this.layerToEdit.slug,
          topo: this.layerToEdit.topo,
          name: this.layerToEdit.name,
        },
      }
      backApi
        .put(`layer-styles/update-style`, payload)
        .then(({ data }) => {
          this.layerToEdit.style = data
          this.$store.commit('maps/updateLayer', this.layerToEdit)
          this.$layerGroups[`wmsLayer`].setParams({ fake: Date.now() }, false)
          this.$map.addControl(this.$drawControl)
          this.isLoading = false
          this.$emit('reloadLayers', this.layerToEdit.slug)
          this.$emit('close')
        })
        .catch((error) => {
          this.showNotificationError(error)
        })
    },
    setLabel(newLabel) {
      this.label = {
        ...this.label,
        ...newLabel,
      }
    },
    setLabelEnabled(val) {
      this.labelEnabled = val
      this.layerToEdit.labelingEnabled = val
    },
    setButtonDisabled(value) {
      this.buttonDisabled = value
    },
  },
  beforeMount() {
    // set the selected tab
    if (this.layerToEdit.style) {
      let style = JSON.parse(this.layerToEdit.style.style)
      this.defaultLabelEnabled = style.labelEnabled
      switch (style.symbologyType) {
        case 'Simple':
          this.symbologyTypeTab = 0
          break
        case 'Classify':
          this.symbologyTypeTab = 1
          break
        case 'Graduated':
          this.symbologyTypeTab = 2
          break
        case 'Cluster':
          this.symbologyTypeTab = 3
          break
        case 'IconGroup':
          this.symbologyTypeTab = 4
          break
        case 'Heatmap':
          this.symbologyTypeTab = 5
          break
        case 'Custom':
          this.symbologyTypeTab = 6
          break
      }
    }
  },
}
</script>
<style lang="scss">
// Syncfusion styles handled globally in nuxt.config.js

.symbology.modal-card {
  width: 800px;
  height: 85vh !important;
  max-height: 85vh;
  .modal-card-body {
    padding: 0 !important;
    position: relative;
    overflow: hidden !important;
    #feature-type-list {
      position: absolute;
      height: 100%;
      width: 100%;
    }
    > .b-tabs {
      display: flex;
      flex-direction: column;
      > .tabs {
        padding: 20px 20px 0 20px;
        margin-bottom: 1rem;
        @include respond('tab-port') {
          padding: 15px 15px 0 15px;
        }
        @include respond('phone') {
          padding: 10px 10px 0 10px;
        }
      }
      .tab-content {
        flex-grow: 1;
        overflow: hidden auto;
        ul {
          margin-bottom: 0 !important;
        }
        padding: 0 !important;
        .tab-item {
          height: 100%;
          .tabs {
            padding: 0 30px;
            @include respond('tab-port') {
              padding: 0 20px;
            }
            @include respond('phone') {
              padding: 0 10px;
              @include hideScroll;
            }
          }
          > div {
            display: flex;
            flex-direction: column;
            height: 100%;
          }
          .tab-item {
            padding: 20px 40px;
            @include respond('tab-port') {
              padding: 15px 30px;
            }
            @include respond('phone') {
              padding: 10px 20px;
            }
          }
        }
      }
    }
    .field {
      &:last-child {
        margin-bottom: 1rem;
      }
    }
  }
}
.modal-card-title {
  span {
    max-width: 30%;
    min-width: 30%;
    line-height: 1.5;
    text-align: left;
    display: inline-block;
    vertical-align: middle;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
  }
}
.custom-icon {
  display: flex;
  height: 80px;
  flex-direction: row;
  align-items: center;
  margin-bottom: 10px;
  &.prevent-default {
    height: 100%;
    margin-top: 2rem;
    justify-content: space-evenly;
    flex-direction: row !important;
    .upload-image-label {
      text-align: center;
      display: block;
    }
  }
  > div:not(:last-child) {
    margin-right: 1.5rem;
  }
  .shape {
    width: 150px;
    margin-top: 30px;
    margin-left: 19px;
  }
  @include respond('tab-port') {
    flex-direction: column-reverse;
    align-items: flex-start;
    height: fit-content;
    &.prevent-default {
      flex-direction: row !important;
      align-items: center;
    }
  }
  @include respond('phone') {
    &.prevent-default {
      flex-direction: column !important;
    }
  }
}

form .download-image {
  border: 2px solid $color-primary;
  border-radius: 20px;
}

form .icons > div {
  border: 1px solid gray;
  width: 40px;
  margin: 2px;
}
form .icons > div:hover {
  cursor: pointer;
  background: honeydew;
}
form .icons > div img {
  width: 30px;
  display: block;
  margin: auto;
}
form .download-image label i {
  margin-left: 57px;
}
.form-shape {
  display: flex;
  justify-content: space-between;
  flex: 0.4;
  &--icon-group {
    justify-content: flex-start;
    > div {
      margin-right: 2rem;
    }
  }
}
.actions {
  margin-bottom: 0.8rem !important;
  button {
    margin-right: 0.5rem;
  }
  @include respond('phone') {
    display: flex;
    flex-direction: column;
    button {
      &:not(:last-child) {
        margin-bottom: 0.7rem;
      }
    }
  }
}
@media (min-width: 768px) {
  .col-md {
    flex-basis: unset !important;
    flex-grow: unset !important;
    max-width: unset !important;
  }
}
.style-configuration {
  @include respond('tab-port') {
    flex-direction: column-reverse;
  }
}
</style>
