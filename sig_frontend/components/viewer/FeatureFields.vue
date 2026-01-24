<template>
  <div class="fields">
    <ul
      v-for="field in getFields"
      :key="field.id"
      :id="field.id"
      class="fields__item"
      :style="{ display: 'flex', flexDirection: getDirection(field.type) }"
    >
      <li class="fields__label">
        {{ field.name }}
      </li>
      <li class="fields__content">
        <div
          class="chips"
          v-if="
            field.type === 'MULTI_SELECT' &&
            newLayer.feature.properties[field.slug]
          "
        >
          <span
            v-for="(item, index) in newLayer.feature.properties[field.slug]
              .replace('[', '')
              .replace(']', '')
              .split(',')"
            :key="index"
            class="chips__chip"
            :style="{ backgroundColor: getColor() }"
            >{{ item }}
          </span>
        </div>
        <span v-else-if="field.type === 'SELECT'">
          {{ extractSelectedRV(newLayer.feature.properties[field.slug]) }}
        </span>
        <span v-else-if="field.type === 'DATE'">
          {{ newLayer.feature.properties[field.slug] }}
        </span>
        <span v-else-if="field.type === 'DATETIME'">
          {{ newLayer.feature.properties[field.slug] }}
        </span>
        <span v-else-if="field.type === 'TIME'">
          {{ newLayer.feature.properties[field.slug] }}
        </span>
        <div
          v-else-if="
            field.type === 'IMAGE' && newLayer.feature.properties[field.slug]
          "
          class="fields__image"
          v-bind:style="{
            backgroundImage:
              'url(' +
              getImageUrl(newLayer.feature.properties[field.slug]) +
              ')',
          }"
        ></div>
        <!-- Start CAROUSEL  -->
        <div
          v-else-if="
            field.type == 'CAROUSEL' && newLayer.feature.properties[field.slug]
          "
          class="carousel-wrapper"
          :style="{
            display:
              newLayer.feature.properties[field.slug].length > 2
                ? 'blod'
                : 'none',
          }"
        >
          <carousel
            :key="`car-${field.id}`"
            :featureId="newLayer.feature.id"
            :layerId="newLayer.feature.layer.id"
            :fieldName="field.id"
            :images="newLayer.feature.properties[field.slug]"
            :view="true"
            :imageLoaded="false"
          ></carousel>
        </div>
        <!-- End CAROUSEL  -->
        <span
          v-else-if="
            field.type === 'BOOLEAN' &&
            newLayer.feature.properties[field.slug] != null && (newLayer.feature.properties[field.slug].toString() == 'true' || newLayer.feature.properties[field.slug].toString() == 'false')
          "
          :class="`chips__chip chips__chip--${
            JSON.parse(newLayer.feature.properties[field.slug]) == true
              ? 'green'
              : 'red'
          }`"
        >
          {{
            JSON.parse(newLayer.feature.properties[field.slug]) == true
              ? 'Oui &#10003;'
              : 'Non &#x2715;'
          }}
        </span>
        <b-input
          type="textarea"
          readonly
          v-else-if="field.type === 'HTMLEDITOR'"
          v-html="newLayer.feature.properties[field.slug]"
          expanded
        />
        <span style="white-space: no-wrap" v-else-if="field.type === 'NUMBER' || field.type === 'INTEGER'"> 
          {{ newLayer.feature.properties[field.slug] && newLayer.feature.properties[field.slug].toString().replace(/\B(?=(\d{3})+(?!\d))/g, " ") }} 
          </span>
         <span v-else> {{ newLayer.feature.properties[field.slug] }} </span>
      </li>
    </ul>
  </div>
</template>

<script>
import carousel from '../ui/Carousel.vue'
import { backend } from '~/constants'
import { LayerSymbolizer } from '../dashboard/symbologies/style'
import RestApi from '../../methods/api'

export default {
  components: {
    carousel
  },
  data() {
    return {
      colorsPalette: [
        '#ffff00',
        '#ffc0cb',
        '#00ff00',
        '#0000ff',
        '#e9967a',
        '#00ffff',
        '#c0c0c0'
      ]
    }
  },
  props: ['fields', 'newLayer', 'isPublic'],
  computed: {
    getFields() {
      if (this.fields)
        return this.fields
          .filter(
            field =>
              this.isPublic ? field.visible && field.publique : field.visible
          )
          .sort((a, b) => a.order - b.order)
    }
  },
  methods: {
    extractSelectedRV(rv) {
      return RestApi.extractSelectedRV(rv)
    },
    /***
     * retieve a random color from colors pallete
     * output: color string
     */
    getColor() {
      let color = LayerSymbolizer.generateRandomColor()
      while (color.includes('fff') || color.includes('f5f5dc')) {
        color = LayerSymbolizer.generateRandomColor()
      }
      return color
    },

    /***
     * return retrive flex direction
     * input : field type
     * output : row | column
     */
    getDirection(fieldType) {
      const columnGrid = [
        'HTMLEDITOR',
        'IMAGE',
        'TEXTAREA',
        'TEXT',
        'SELECT',
        'MULTI_SELECT',
        'CAROUSEL'
      ]
      return columnGrid.includes(fieldType) ? 'column' : 'row'
    },
    getImages(images) {
      if (!this.imageLoaded) {
        if (images && images.length > 3) {
          this.imagesToUploadUrls = images
          return images
            .replace('[', '')
            .replace(']', '')
            .split(',')
            .map((imagePath, index) => {
              return {
                default: index == 0 ? 1 : null,
                highlight: index == 0 ? 1 : null,
                path: `${backend}/download/layers.${
                  this.newLayer.feature.layer.id
                }.${this.newLayer.feature.id}.carousel/${imagePath.trim()}`
              }
            })
        } else {
          return []
        }
      }
    },
    getImageUrl(path) {
      return `${backend}/download/layers.${this.newLayer.feature.layer.id}.${
        this.newLayer.feature.id
      }/${path}`
    },
    /***
     *  Convert array of strings to and Array
     */
    getherUrlsInArray(str) {
      let arr = []
      let res = ''
      if (str != null) {
        res = str.substring(1, str.length - 1).split(',')
      }
      for (var i = 0; i < res.length; i++) arr.push(res[i])
      this.getImages = arr
      return arr
    }
  }
}
</script>

<style lang="scss">
.fields {
  &__item {
    margin: 0;
    padding: 0.8rem 0;
    &:not(:last-child) {
      border-bottom: 1px solid #f1f3f4;
    }
  }
  &__label {
    font-weight: bold;
    margin-right: 0.5rem;
    &::after {
      content: ':';
    }
  }
  &__image {
    width: 100%;
    height: 15rem;
    margin-top: 0.5rem;
    background-size: cover;
  }
  &__carousel {
    margin-top: 1.5rem;
    width: 100%;
    height: auto;
  }
}
.card #feature-type-list .tabs ul {
  margin-bottom: 0 !important;
}
.chips {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: -5px;
  margin-right: -5px;
  margin-top: 5px;
  &__chip {
    color: #fff;
    border-radius: 15px;
    padding: 0.5rem 0.8rem;
    font-size: 0.85rem;
    margin-bottom: 5px;
    margin-right: 5px;
    font-weight: bold;
    white-space: nowrap;
    @include respond('tab-port') {
      font-size: 0.75rem;
      padding: 0.1rem 0.8rem;
      line-height: 1.3;
    }
    &--red {
      color: white;
      background-color: #e53935 !important;
    }
    &--green {
      color: white;
      background-color: #00897b !important;
    }
  }
}
</style>
