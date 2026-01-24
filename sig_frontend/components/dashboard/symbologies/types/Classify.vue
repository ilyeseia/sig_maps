<template>
  <div>
    <b-field label="Colonne">
      <b-select
        expanded
        placeholder="Veuillez sélectionner un champ"
        v-model="classifySelectedField"
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
    <div
      style="margin-top: 30px"
      class="custom-icon"
      v-if="layerToEdit.topo === 'Point'"
    >
      <!-- Start Add Size  -->
      <b-field label="Taille">
        <b-select name="category_id" v-model.number="mark.size">
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
      <!-- Start Shape Preview  -->
      <live-preview
        :shapeForm="mark.name"
        :width="mark.size"
        :height="mark.size"
        :backgroundColor="fill.color"
        :opacity="fill.opacity"
        borderStyle="solid"
        :borderWidth="0"
        :borderColor="stroke.color"
        :borderOpacity="1"
        :rotation="mark.rotation"
      >
      </live-preview>
      <!-- End Shape Preview  -->
    </div>
    <label><strong>Symboles</strong></label>
    <b-field class="actions">
      <button
        @click="classify()"
        class="button"
        :disabled="!classifySelectedField"
        :class="['button', { 'is-loading': isLoading }]"
        type="button"
      >
        <b-icon size="is-small" class="mr-1" pack="fas" icon="shapes"> </b-icon>
        Classer
      </button>
      <button
        @click="addClassifyRule()"
        class="button"
        type="button"
        icon-left="github-circle"
      >
        <b-icon size="is-small" class="mr-1" pack="fas" icon="plus"> </b-icon>
        Ajouter
      </button>
      <button
        @click="deleteCheckedClassifyRules()"
        :disabled="classifyCheckedRows.length === 0"
        class="button"
        type="button"
      >
        <b-icon size="is-small" class="mr-1" pack="fas" icon="minus"> </b-icon>
        Supprimer
      </button>
    </b-field>
    <b-table
      :data="classifyTable"
      default-sort="field"
      :checked-rows.sync="classifyCheckedRows"
      checkable
    >
      <template slot-scope="props">
        <b-table-column field="symbol" sortable label="Couleur">
          <ejs-colorpicker
            :enableOpacity="false"
            :id="props.row.field"
            v-model="props.row.color"
          ></ejs-colorpicker>
        </b-table-column>
        <b-table-column field="operator" label="Operateur" sortable>
          <b-select v-model="props.row.operator" expanded>
            <option v-for="op in operatorList" :key="op">
              {{ op }}
            </option>
          </b-select>
        </b-table-column>
        <b-table-column field="valeur" label="Valeur" sortable>
          <b-input v-model="props.row.value"> </b-input>
        </b-table-column>
        <b-table-column field="edit" label="Éditer">
          <button
            type="button"
            class="button"
            @click="deleteClassifyRule(props.row.id)"
          >
            <b-icon pack="fas" icon="times-circle"></b-icon>
          </button>
        </b-table-column>
      </template>
    </b-table>
  </div>
</template>

<script>
import { LayerSymbolizer } from '../style'
import uuidv1 from 'uuid'
import LivePreview from '../../../ui/LivePreview.vue'

import mark from '~/mixins/style/mark'
import fill from '~/mixins/style/fill'
import stroke from '~/mixins/style/stroke'

export default {
  props: ['layerToEdit', 'label', 'mapSlug'],
  mixins: [mark, fill, stroke],
  components: {
    LivePreview,
  },
  data() {
    return {
      classifyCheckedRows: [],
      classifySelectedField: null,
      isLoading: false,
      classifyStyle: {
        'icon-size': 8,
        'icon-shape': 'circle',
        rotation: 0,
      },
      classifyStyle: {
        symbologyType: 'Classify',
        rules: [],
      },
      classifyTable: [],
      operatorList: ['=', '<', '>', '>=', '<='],
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
    classify() {
      this.isLoading = true
      this.$emit('setButtonDisabled', true)
      LayerSymbolizer.generateRandomColors(
        this.layerToEdit.id,
        this.classifySelectedField,
        this.layerToEdit.fields.filter(
          (f) => f.slug === this.classifySelectedField
        )[0].type
      )
        .then(async (data) => {
          let classification = []
          data.forEach((filter) => {
            classification.push({
              ...filter,
              property: this.classifySelectedField,
            })
          })
          this.classifyTable = Array.from(classification)
          this.isLoading = false
          this.$emit('setButtonDisabled', false)
        })
        .catch(() => {
          this.isLoading = false
          this.$emit('setButtonDisabled', false)
        })
    },
    addClassifyRule() {
      // put size and shape here
      this.classifyTable.push({
        id: uuidv1(),
        property: this.classifySelectedField,
        color: '#008000',
        field: '',
        operator: '=',
      })
    },
    deleteClassifyRule(id) {
      this.classifyTable = this.classifyTable.filter((rule) => rule.id != id)
    },
    deleteCheckedClassifyRules() {
      for (const rule of this.classifyCheckedRows) {
        this.deleteClassifyRule(rule.id)
      }
      this.classifyCheckedRows = []
    },
    async onSubmit() {
      this.classifyTable.forEach((filter) => {
        let rule = {
          filter,
          mark: {
            ...this.mark,
          },
          fill: {
            ...this.fill,
            color: filter.color,
          },
          stroke: {
            ...this.stroke,
            opacity: 1,
            color: filter.color,
          },
        }
        this.classifyStyle['labelEnabled'] = this.layerToEdit.labelingEnabled
        if (this.layerToEdit.labelingEnabled) {
          rule['label'] = { ...this.label }
        }
        this.classifyStyle.rules.push(rule)
      })
      this.classifyStyle['labelEnabled'] = this.layerToEdit.labelingEnabled
      this.$emit('saveStyle', this.classifyStyle)
    },
  },
  beforeMount() {
    let data = JSON.parse(this.layerToEdit.style.style)
    if (
      this.layerToEdit.style &&
      data.symbologyType === 'Classify'
    ) {
      data.rules.forEach((rule, index) => {
        if (index === 0) {
          if (rule.filter) this.classifySelectedField = rule.filter.property
          if (rule.fill) this.fill = rule.fill
          if (rule.stroke) this.stroke = rule.stroke
          if (rule.mark) this.mark = data.rules[0].mark
          data.labelEnabled && this.$emit('setLabel', data.rules[0].label)
        }
        const { field, value, operator, color, property } = rule.filter
        this.classifyTable.push({
          id: uuidv1(),
          field,
          value,
          color,
          operator,
          property,
        })
      })
    }
  },
}
</script>