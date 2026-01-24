<template>
  <div class="simple-symbology">
    <b-field v-if="layerToEdit.topo === 'Point'">
      <!-- Start Switch Icon  Still Need Editing -->
      <b-switch v-model="customIcon">Icône personnalisée</b-switch>
      <!-- End Switch Icon -->
    </b-field>
    <!-- Start Image upload -->
    <b-tabs v-if="customIcon === true">
      <b-tab-item label="Icônes personnalisées">
        <!-- Start Image upload -->
        <div class="custom-icon prevent-default">
          <div class="form-shape">
            <b-field label="Taille">
              <b-select name="category_id" v-model="icon.size" required>
                <option value="8">8</option>
                <option value="16">16</option>
                <option value="20">20</option>
                <option value="24">24</option>
                <option value="28">28</option>
                <option value="32">32</option>
                <option value="40">40</option>
                <option value="44">44</option>
                <option value="48">48</option>
              </b-select>
            </b-field>
            <b-field label="Rotation">
              <b-input
                style="width: 5rem"
                name="shape-rotation"
                v-model="icon.rotation"
                type="number"
              ></b-input>
            </b-field>
          </div>
          <!-- End Add Size  -->
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
            <div style="display: flex">
              <!-- Start Add Size  -->
              <b-field label="Taille" style="margin-right: 1rem">
                <b-select name="category_id" v-model="icon.size" required>
                  <option value="8">8</option>
                  <option value="16">16</option>
                  <option value="20">20</option>
                  <option value="24">24</option>
                  <option value="28">28</option>
                  <option value="32">32</option>
                  <option value="40">40</option>
                  <option value="44">44</option>
                  <option value="48">48</option>
                </b-select>
              </b-field>
              <b-field label="Rotation">
                <b-input
                  style="width: 5rem"
                  name="shape-rotation"
                  v-model="icon.rotation"
                  type="number"
                ></b-input>
              </b-field>
              <!-- End Add Size  -->
            </div>
            <div>
              <b-field class="file">
                <img
                  :style="{
                    width: `${icon.size * 1.5}px`,
                    height: `${icon.size * 1.5}px`,
                    transform: `rotate(${icon.rotation}deg)`,
                  }"
                  :src="imagIcon"
                  alt="selected icon"
                />
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
    <div class="d-flex justify-content-between style-configuration">
      <div v-if="customIcon === false">
        <div class="custom-icon" v-if="layerToEdit.topo === 'Point'">
          <!-- Start Add Size  -->
          <b-field label="Taille">
            <b-select name="category_id" v-model="mark.size">
              <option value="4">4</option>
              <option value="8">8</option>
              <option value="10">10</option>
              <option value="12">12</option>
              <option value="16">16</option>
              <option value="20">20</option>
              <option value="24">24</option>
              <option value="28">28</option>
              <option value="32">32</option>
              <option value="36">36</option>
              <option value="40">40</option>
              <option value="44">44</option>
              <option value="48">48</option>
            </b-select>
          </b-field>
          <!-- End Add Size  -->
          <!-- Start Add Shape  -->
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
        <div class="line" v-show="lineStyle">
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
              v-model.number="fill.opacity"
            ></b-slider>
          </b-field>
        </div>
        <b-field label="Couleur du trait">
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
            v-model.number="stroke.opacity"
          ></b-slider>
        </b-field>
        <b-field label="Largeur du trait">
          <a-input-number
            id="inputNumber"
            :min="1"
            :max="10"
            v-model.number="stroke.width"
          />
        </b-field>
      </div>
      <!-- End Add Shape  -->
      <live-preview
        v-if="!customIcon && layerToEdit.topo === 'Point'"
        :shapeForm="mark.name"
        :width="mark.size"
        :height="mark.size"
        :backgroundColor="fill.color"
        :opacity="fill.opacity"
        borderStyle="solid"
        :borderWidth="stroke.width"
        :borderColor="stroke.color"
        :borderOpacity="stroke.opacity"
        :rotation="mark.rotation"
      >
      </live-preview>
      <!-- End Shape Preview  -->
    </div>
  </div>
</template>
<script>
import { backend } from '../../../../constants'
import BaseImageInput from '../../../image/BaseImageInput'
import path from 'path-extra'
import RestApi from '../../../../methods/api'
import { backApi } from '../../../../methods/serverApi'
import LivePreview from '../../../ui/LivePreview.vue'

import stroke from '~/mixins/style/stroke'
import fill from '~/mixins/style/fill'
import mark from '~/mixins/style/mark'
import icon from '~/mixins/style/icon'

export default {
  mixins: [stroke, fill, mark, icon],
  props: ['layerToEdit', 'label', 'mapSlug'],
  components: {
    BaseImageInput,
    LivePreview,
  },
  data() {
    return {
      customIcon: false,
      lineStyle: true,
      bitmapsIcons: [],
      currentImage: '',
      imagIcon: '',
      imageFile: `${backend}/download/default.png`,
      simpleStyle: {
        symbologyType: 'Simple',
        rules: [],
      },
    }
  },
  watch: {
    bitmapsIcons() {
      if (
        this.bitmapsIcons.length > 0 &&
        this.imageFile.includes('default.png')
      ) {
        this.imagIcon = this.bitmapsIcons[0].path
      }
    },
  },
  methods: {
    getImage() {
      return this.imageFile
    },
    getIcons() {
      backApi
        .get('files/icons.simple')
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
      this.simpleStyle['labelEnabled'] = this.layerToEdit.labelingEnabled
      let rule = {
        fill: this.fill,
        mark: this.mark,
        stroke: this.stroke,
      }
      if (this.layerToEdit.labelingEnabled) {
        rule['label'] = { ...this.label }
      }
      if (this.customIcon) {
        rule['icon'] = { ...this.icon }
        this.simpleStyle['iconUrl'] = this.icon.url
      }
      this.simpleStyle.rules.push(rule)
      this.$emit('saveStyle', this.simpleStyle)
    },
  },
  beforeMount() {
    let data = JSON.parse(this.layerToEdit.style.style)
    this.getIcons()
    this.lineStyle = this.layerToEdit.topo === 'LineString' ? false : true
    if (
      this.layerToEdit.style &&
      data.symbologyType === 'Simple'
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
      }
      data.labelEnabled && this.$emit('setLabel', data.rules[0].label)
    }
  },
}
</script>

<style lang="scss">
.simple-symbology,
.cluster-symbology {
  .field {
    > label + div {
      margin-left: 0.2rem;
    }
    .b-slider {
      margin-left: 0.8rem;
    }
  }
  .tab-item {
    padding: 10px 45px !important;
    @include hideScroll;
  }
  .bitmap-icons {
    > div:nth-child(1) {
      display: flex;
      justify-content: space-around;
      align-items: center;
      img {
        width: 55px;
        height: 55px;
        background-size: cover;
      }
    }
    .icons-list {
      margin-top: 20px;
      display: flex;
      width: 100%;
      flex-direction: row;
      justify-content: flex-start;
      flex-wrap: wrap;
      padding-left: -8px;
      padding-right: -8px;
      > div {
        padding: 8px;
        cursor: pointer;
        border: 1px solid #ccc;
        &:hover {
          background-color: rgba(204, 204, 204, 0.3);
        }
        &.selected {
          background-color: rgba(204, 204, 204, 0.3) !important;
        }
      }
      img {
        width: 35px;
        height: 35px;
        background-size: cover;
      }
    }
  }
}
</style>
