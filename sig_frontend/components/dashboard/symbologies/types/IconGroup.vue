<template>
  <div>
    <b-field label="Colonne">
      <b-select
        expanded
        placeholder="Veuillez sélectionner un champ"
        v-model="iconGroupSelectedField"
      >
        <option
          v-for="field in getFields"
          :value="field.slug"
          :key="field.id"
          :id="field.id"
        >
          {{ field.name }}
        </option>
      </b-select>
    </b-field>
    <!-- Start Adding Shape and Size  -->
    <div class="custom-icon" v-if="layerToEdit.topo === 'Point'">
      <div class="row w-100">
        <div class="col">
          <!-- Start Add Size  -->
          <b-field label="Taille des images">
            <b-select name="category_id" v-model="icon.size">
              <option value="8">8</option>
              <option value="10">10</option>
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
        </div>
        <div class="col">
          <b-field class="file">
            <img class="download-image" :src="imageFile" />
          </b-field>
        </div>
      </div>
      <!-- End Shape Preview  -->
    </div>
    <!-- End Adding Shape and Size  -->
    <label><strong>Symboles</strong></label>
    <b-field class="actions">
      <button
        @click="generateClassifyIconGroup()"
        :class="['button', { 'is-loading': isLoading }]"
        type="button"
        :disabled="!iconGroupSelectedField"
      >
        <b-icon size="is-small" class="mr-1" pack="fas" icon="shapes"> </b-icon>
        Classer
      </button>
      <button
        @click="addIconGroupRule()"
        class="button"
        type="button"
        icon-left="github-circle"
      >
        <b-icon size="is-small" class="mr-1" pack="fas" icon="plus"> </b-icon>
        Ajouter
      </button>
      <button
        @click="deleteCheckedIconGroupRules()"
        :disabled="iconGroupCheckedRows.length === 0"
        class="button"
        type="button"
      >
        <b-icon size="is-small" class="mr-1" pack="fas" icon="minus"> </b-icon>
        Supprimer
      </button>
    </b-field>
    <b-table
      :data="classifyIconGroup"
      default-sort="field"
      :checked-rows.sync="iconGroupCheckedRows"
      checkable
    >
      <template slot-scope="props">
        <b-table-column field="symbol" sortable label="Image">
          <base-image-input
            width="50px"
            height="50px"
            :imageUrl="props.row.iconUrl"
            v-model="props.row.iconUrl"
          />
        </b-table-column>
        <b-table-column field="operator" label="Operateur" sortable>
          <b-select v-model="props.row.operator" expanded>
            <option v-for="op in operatorList" :key="op">
              {{ op }}
            </option>
          </b-select>
        </b-table-column>
        <b-table-column field="valeur" label="Valeur" sortable>
          {{ props.row.value }}
        </b-table-column>
        <b-table-column field="edit" label="Éditer">
          <button
            type="button"
            class="button"
            @click="deleteIconGroupRule(props.row.id)"
          >
            <b-icon pack="fas" icon="times-circle"></b-icon>
          </button>
        </b-table-column>
      </template>
    </b-table>
  </div>
</template>

<script>
import { backApi } from '../../../../methods/serverApi'
import { backend } from '../../../../constants'
import BaseImageInput from '../../../image/BaseImageInput'
import { LayerSymbolizer } from '../style'
import uuidv1 from 'uuid'
import RestApi from '../../../../methods/api'

import icon from '~/mixins/style/icon'

export default {
  mixins: [icon],
  props: ['layerToEdit', 'label', 'mapSlug'],
  components: {
    BaseImageInput,
  },
  data() {
    return {
      iconGroupSelectedField: null,
      iconGroupCheckedRows: [],
      operatorList: ['=', '<', '>', '>=', '<='],
      imageFile: `${backend}/download/default.png`,
      classifyIconGroup: [],
      isLoading: false,
      style: {
        'stroke-width': 1,
        'stroke-opacity': 2,
        'fill-opacity': 1,
        'icon-size': 8,
        'custome-icon-size': 16,
        'custome-group-Icon': 16,
        'icon-shape': 'circle',
        fill: '#008000',
        stroke: '#FFFFFF',
        customIcon: false,
      },
      iconGroupStyle: {
        symbologyType: 'IconGroup',
        rules: [],
      },
    }
  },
  computed: {
    getFields() {
      let exceptedItems = ['TEXTAREA', 'CAROUSEL', 'IMAGE']
      return this.layerToEdit.fields.filter(
        (f) => !exceptedItems.includes(f.type)
      )
    },
  },
  methods: {
    generateClassifyIconGroup() {
      this.isLoading = true
      this.$emit('setButtonDisabled', true)
      LayerSymbolizer.generateEmptyImages(
        this.layerToEdit.id,
        this.iconGroupSelectedField,
        this.layerToEdit.fields.filter(
          (f) => f.slug === this.iconGroupSelectedField
        )[0].type
      )
        .then(async (data) => {
          let classification = []
          data.forEach((filter) => {
            classification.push({
              ...filter,
              property: this.iconGroupSelectedField,
            })
          })
          this.classifyIconGroup = Array.from(classification)
          this.isLoading = false
          this.$emit('setButtonDisabled', false)
        })
        .catch(() => {
          this.isLoading = false
          this.$emit('setButtonDisabled', false)
        })
    },
    addIconGroupRule() {
      this.classifyIconGroup.push({
        id: uuidv1(),
        iconUrl: `${backend}/download/default.png`,
        field: '',
        operator: '=',
        property: this.iconGroupSelectedField,
      })
    },
    deleteCheckedIconGroupRules() {
      for (const rule of this.iconGroupCheckedRows) {
        this.deleteIconGroupRule(rule.id)
      }
      this.iconGroupCheckedRows = []
    },
    deleteIconGroupRule(id) {
      this.classifyIconGroup = this.classifyIconGroup.filter(
        (rule) => rule.id != id
      )
    },
    async saveIconGroupSymbology() {
      for (let iconGroup of this.classifyIconGroup) {
        if (iconGroup.iconUrl !== iconGroup.oldIconUrl) {
          if (iconGroup.iconUrl === `${backend}/download/default.png`) {
            iconGroup.iconUrl = iconGroup.iconUrl
          } else {
            let fileName = await RestApi.saveFile(iconGroup.iconUrl)
            iconGroup.iconUrl = `${backend}/download/${fileName}`
          }
        } else {
          iconGroup.iconUrl = iconGroup.iconUrl
        }
      }
    },
    async onSubmit() {
      await this.saveIconGroupSymbology()
      this.classifyIconGroup.forEach((filter) => {
        let rule = {
          filter,
          icon: {
            url: filter.iconUrl,
            rotation: 0,
            size: this.icon.size,
          },
        }
        this.iconGroupStyle['labelEnabled'] = this.layerToEdit.labelingEnabled
        if (this.layerToEdit.labelingEnabled) {
          rule['label'] = { ...this.label }
        }
        this.iconGroupStyle.rules.push(rule)
      })
      this.$emit('saveStyle', this.iconGroupStyle)
    },
  },
  beforeMount() {
    let data = JSON.parse(this.layerToEdit.style.style)
    if (this.layerToEdit.style && data.symbologyType === 'IconGroup') {
      data.rules.forEach((rule, index) => {
        if (index === 0) {
          if (rule.icon) this.icon = rule.icon
          this.iconGroupSelectedField = rule.filter.property
          data.labelEnabled && this.$emit('setLabel', data.rules[0].label)
        }
        const { field, value, operator, iconUrl, color, property } = rule.filter
        this.classifyIconGroup.push({
          id: uuidv1(),
          field,
          value,
          color,
          iconUrl,
          oldIconUrl: iconUrl,
          operator,
          property,
        })
      })
      this.imageFile = `${backend}/download/default.png`
    }
  },
}
</script>

<style scoped lang="scss">
.download-image {
  padding: 5px;
  background-size: cover;
  height: 65px !important;
  width: 65px !important;
}
</style>