<template>
  <div class="cluster-symbology">
    <!-- Start Cluster Switch Icon  Still Need Editing -->
    <b-field>
      <b-switch v-model="customIcon">Icône personnalisée</b-switch>
    </b-field>
    <!-- End Cluster Switch Icon  -->
    <b-field v-if="customIcon">
      <b-tabs>
        <b-tab-item label="Icônes personnalisées">
          <!-- Start Image upload -->
          <div class="custom-icon prevent-default">
            <div>
              <b-field class="file">
                <base-image-input
                  class="download-image"
                  width="150px"
                  height="150px"
                  :imageUrl="getImage()"
                  v-model="imageFile"
                />
              </b-field>
              <label class="upload-image-label">
                <i
                  class="fas fa-cloud-upload-alt fa-2x"
                  style="color: $color-primary"
                ></i>
              </label>
            </div>
          </div>
          <!-- End Image upload -->
        </b-tab-item>
        <b-tab-item label="Icônes Bitmap">
          <!-- Start Bitmap Icons -->
          <div class="bitmap-icons">
            <!-- Start Image upload -->
            <div>
              <div>
                <b-field class="file">
                  <img :src="imagIcon" alt="selected icon" />
                </b-field>
              </div>
            </div>
            <div>
              <div class="icons-list">
                <div
                  v-for="icon in bitmapsIcons"
                  :class="{ selected: icon.path === imagIcon }"
                  :key="icon.path"
                >
                  <img
                    :src="icon.path"
                    :alt="icon.name"
                    @click="getImagUrl(icon.path)"
                  />
                </div>
              </div>
            </div>
          </div>
          <!-- End Bitmap Icons -->
        </b-tab-item>
      </b-tabs>
    </b-field>
    <div
      v-if="!customIcon"
      class="d-flex justify-content-between style-configuration"
    >
      <div>
        <div class="custom-icon">
          <b-field label="Forme ">
            <b-select name="category_id" v-model="mark.name">
              <option value="circle">Circle</option>
              <option value="square">Square</option>
              <option value="triangle">Triangle</option>
              <option value="star">Star</option>
            </b-select>
          </b-field>
          <b-field v-if="mark.name !== 'circle'" label="Rotation">
            <b-input
              style="width: 5rem"
              name="shape-rotation"
              v-model="mark.rotation"
              type="number"
            ></b-input>
          </b-field>
        </div>
        <b-field label="Couleur du remplissage">
          <ejs-colorpicker
            :enableOpacity="false"
            v-model="fill.color"
          ></ejs-colorpicker>
        </b-field>
        <b-field label="Transparence du remplissage">
          <b-slider
            style="width: 300px"
            :step="0.01"
            :min="0"
            :max="1"
            v-model="fill.opacity"
          ></b-slider>
        </b-field>
        <b-field label="Coleur du trait">
          <ejs-colorpicker
            :enableOpacity="false"
            v-model="stroke.color"
          ></ejs-colorpicker>
        </b-field>
        <b-field label="Transparence du trait">
          <b-slider
            style="width: 300px"
            :step="0.01"
            :min="0"
            :max="1"
            v-model="stroke.opacity"
          ></b-slider>
        </b-field>
        <b-field label="Largeur du trait">
          <a-input-number
            id="inputNumber"
            :min="1"
            :max="10"
            v-model="stroke.width"
          />
        </b-field>
      </div>
      <div>
        <live-preview
          margin="7px auto"
          :shapeForm="mark.name"
          :width="32"
          :height="32"
          :backgroundColor="fill.color"
          :opacity="fill.opacity"
          borderStyle="solid"
          :borderWidth="stroke.width"
          :borderColor="stroke.color"
          :borderOpacity="stroke.opacity"
          :rotation="mark.rotation"
        >
        </live-preview>

        <b-field label="Couleur du fill du police">
          <ejs-colorpicker
            :enableOpacity="false"
            v-model="label.fill"
          ></ejs-colorpicker>
        </b-field>
        <b-field label="Taille du police">
          <a-input-number
            v-model="label.policeSize"
            expanded
            id="inputNumber"
            :min="8"
            :max="48"
          />
        </b-field>
        <b-field label="Périmètre">
          <a-input-number
            v-model="clusterStyle.transformation.cellSize"
            expanded
            id="inputNumber"
            :min="30"
            :max="120"
          />
        </b-field>
      </div>
    </div>
  </div>
</template>

<script>
import { backApi } from '../../../../methods/serverApi'
import { backend } from '../../../../constants'
import BaseImageInput from '../../../image/BaseImageInput'
import path from 'path-extra'
import { LayerSymbolizer } from '../style'

import RestApi from '../../../../methods/api'
import LivePreview from '../../../ui/LivePreview.vue'

import stroke from '~/mixins/style/stroke'
import fill from '~/mixins/style/fill'
import mark from '~/mixins/style/mark'
import icon from '~/mixins/style/icon'

export default {
  props: ['layerToEdit', 'label', 'mapSlug'],
  mixins: [stroke, fill, mark, icon],
  components: {
    BaseImageInput,
    LivePreview,
  },
  data() {
    return {
      customIcon: false,
      clusterStyle: {
        symbologyType: 'Cluster',
        transformation: {
          name: 'PointStacker',
          cellSize: 90,
        },
        rules: [],
      },
      imageFile: `${backend}/download/default.png`,
      bitmapsIcons: [],
      currentImage: '',
      imagIcon: '',
    }
  },
  watch: {
    bitmapsIcons() {
      if (this.bitmapsIcons.length > 0 && !this.currentImage) {
        this.imagIcon = this.bitmapsIcons[0].path
        this.$forceUpdate()
      }
    },
  },
  methods: {
    getImage() {
      return this.imageFile
    },
    getIcons() {
      backApi
        .get('files/icons.cluster')
        .then(({ data }) => {
          this.bitmapsIcons = data
        })
        .catch((error) => {
          console.error('Error', error)
        })
    },
    getImagUrl(img) {
      this.currentImage = img
      this.imagIcon = img ? img : this.bitmapsIcons[0].path
      this.$forceUpdate()
    },
    async saveClusterSymbology() {
      this.clusterStyle.name = this.layerToEdit.name
      this.clusterStyle.topo = this.layerToEdit.topo
      this.clusterStyle.slug = this.layerToEdit.slug

      this.clusterStyle.labelingEnabled = false

      let xmlSLD = LayerSymbolizer.generateClusterStyle(this.clusterStyle)
      return {
        xmlSLD,
        iconUrl: this.clusterStyle.iconUrl,
      }
    },
    async onSubmit() {
      if (this.customIcon) {
        if (this.currentImage.search(/png/i) != -1) {
          this.icon.url = this.currentImage
        } else if (this.imageFile) {
          let iconFileName = path.base(this.imageFile)
          let layerOldIcon = path.base(this.layerToEdit.iconUrl)

          if (iconFileName !== layerOldIcon) {
            if (this.imageFile === `${backend}/download/default.png`) {
              this.icon.url = this.imageFile
            } else {
              let fileName = await RestApi.saveFile(this.imageFile)
              this.icon.url = `${backend}/download/${fileName}`
            }
          } else {
            this.icon.url = this.layerToEdit.iconUrl
          }
        }
      }
      this.label.property = 'count'
      ;[...Array(3)].forEach((_, index) => {
        let rule = {
          filter: {
            property: 'count',
            field: index === 0 ? '1' : index === 1 ? '1-9' : 9,
            operator: index === 0 ? '<=' : index === 1 ? '<>' : '>',
          },
          fill: this.fill,
          mark: {
            ...this.mark,
            size: index === 0 ? 18 : index === 1 ? 26 : 32,
          },
          stroke: this.stroke,
          label: { ...this.label },
        }
        if (this.customIcon) {
          rule['icon'] = {
            ...this.icon,
            size: index === 0 ? 18 : index === 1 ? 26 : 32,
          }
        }
        this.clusterStyle.rules.push(rule)
      })
      this.clusterStyle['labelEnabled'] = false
      this.clusterStyle['iconUrl'] = this.icon.url
      this.$emit('saveStyle', this.clusterStyle)
    },
  },
  beforeMount() {
    this.getIcons()
    let data = JSON.parse(this.layerToEdit.style.style)
    if (
      this.layerToEdit.style &&
      data.symbologyType === 'Cluster'
    ) {
      if (data.rules[0] != undefined) {
        if (data.rules[0].fill) this.fill = data.rules[0].fill
        if (data.rules[0].stroke) this.stroke = data.rules[0].stroke
        if (data.rules[0].mark) this.mark = data.rules[0].mark
        if (data.rules[0].icon) {
          this.customIcon = true
          this.icon = data.rules[0].icon
          this.imagIcon = data.rules[0].icon.url
          this.imageFile = data.rules[0].icon.url
        }
        this.$emit('setLabel', data.rules[0].label)
      }
      this.clusterStyle.transformation.cellSize = data.transformation.cellSize
    }
  },
}
</script>