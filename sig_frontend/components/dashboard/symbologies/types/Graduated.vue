<template>
  <div>
    <b-field label="Colonne">
      <b-select
        placeholder="Veuillez sélectionner un champ"
        expanded
        v-model="classifySelectedField"
      >
        <option
          v-for="field in getNumericField"
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
    </div>
    <b-field class="custom-icon">
      <b-field label="Mode">
        <b-select
          placeholder="Sélectionnez un mode de classification"
          expanded
          v-model="graduatedStyle.selectedMode"
        >
          <option v-for="m in mode" :value="m.key" :key="m.key" :id="m.key">
            {{ m.value }}
          </option>
        </b-select>
      </b-field>
      <b-field label="Classes">
        <b-input
          style="width: 5rem"
          name="classes"
          :min="0"
          v-model="graduatedStyle.classes"
          type="number"
        ></b-input>
      </b-field>
      <b-field label="Méthode">
        <b-select
          placeholder="Sélectionnez un méthode"
          expanded
          v-model="selectedMethod"
        >
          <option value="color">Couleur</option>
          <option v-if="layerToEdit.topo === 'Point'" value="size">
            Taille
          </option>
        </b-select>
      </b-field>
    </b-field>
    <b-field class="custom-icon">
      <b-field label="Précision">
        <b-input
          style="width: 5rem"
          name="classes"
          :min="0"
          :max="10"
          v-model="graduatedStyle.precision"
          type="number"
        ></b-input>
      </b-field>
      <b-field label="Couleur">
        <ejs-colorpicker
          :enableOpacity="false"
          v-model="selectedColor"
        ></ejs-colorpicker>
      </b-field>
    </b-field>
    <label><strong>Symboles</strong></label>
    <b-field class="actions">
      <button
        @click="classify()"
        :disabled="!classifySelectedField || !graduatedStyle.selectedMode "
        class="button"
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
      :data="getIntervals"
      default-sort="field"
      :checked-rows.sync="classifyCheckedRows"
      checkable
    >
      <template slot-scope="props">
        <b-table-column field="symbol" sortable label="Symbole">
          <live-preview
            :shapeForm="mark.name"
            :width="
              selectedMethod === 'size'
                ? mark.size * 0.5 + parseInt(props.row.size)
                : mark.size
            "
            :height="
              selectedMethod === 'size'
                ? mark.size * 0.5 + parseInt(props.row.size)
                : mark.size
            "
            :backgroundColor="props.row.color"
            :opacity="
              selectedMethod === 'color' ? props.row.index / opacityFactor : 1
            "
            borderStyle="solid"
            :borderWidth="stroke.width"
            :borderColor="stroke.color"
            :borderOpacity="stroke.opacity"
            :rotation="mark.rotation"
            :origin="false"
          >
          </live-preview>
        </b-table-column>
        <b-table-column field="valeur" label="Valeur minimale" sortable>
          <b-input
            type="text"
            oninput="this.value = this.value.replace(/[^0-9.]/g, '');
                this.value = this.value.replace(/(\..*)\./g, '$1');"
            v-model="props.row.from"
          >
          </b-input>
        </b-table-column>
        <b-table-column field="valeur" label="Valeur Maximale" sortable>
          <b-input
            type="text"
            oninput="this.value = this.value.replace(/[^0-9.]/g, '');
                this.value = this.value.replace(/(\..*)\./g, '$1');"
            v-model="props.row.to"
          >
          </b-input>
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
import { backApi } from '../../../../methods/serverApi'
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
      graduatedStyle: {
        symbologyType: 'Graduated',
        selectedMethod: 'color',
        selectedMode: null,
        baseSize: 16,
        classes: 3,
        precision: 2,
        rules: [],
      },
      selectedMethod: 'color',
      selectedColor: '$color-primary',
      opacityFactor: 1,
      classifyTable: [],
      operatorList: ['=', '<', '>', '>=', '<='],
      mode: [
        {
          key: 'EQUAL_COUNT',
          value: 'Equal Count (Quantile)',
        },
        {
          key: 'EQUAL_INTERVAL',
          value: 'Equal Interval',
        },
      ],
    }
  },
  computed: {
    getNumericField() {
      let numericTypes = ['NUMBER', 'INTEGER']
      return this.layerToEdit.fields.filter((f) =>
        numericTypes.includes(f.type)
      )
    },
    getIntervals() {
      this.classifyTable = this.classifyTable.map((v) => {
        return {
          ...v,
          from: parseFloat(v.from).toFixed(this.graduatedStyle.precision),
          to: parseFloat(v.to).toFixed(this.graduatedStyle.precision),
        }
      })
      return this.classifyTable
    },
  },
  watch: {
    selectedMethod(newVal, oldVal) {
      if (newVal !== oldVal) {
        if (newVal === 'size') {
          this.opacityFactor = 1
        } else {
          this.opacityFactor = this.graduatedStyle.classes
        }
      }
    },
    selectedColor(newVal, oldVal) {
      if (newVal != oldVal && this.classifyTable.length > 0) {
        this.classify()
      }
    },
  },
  methods: {
    classify() {
      if (this.graduatedStyle.classes > 0) {
        this.isLoading = true
        this.$emit('setButtonDisabled', true)
        if (this.selectedMethod === 'color') {
          this.opacityFactor = this.graduatedStyle.classes
        }
        LayerSymbolizer.generateGroupsWithRandomColors(
          this.layerToEdit.id,
          this.classifySelectedField,
          this.graduatedStyle.selectedMode,
          this.selectedMethod,
          this.selectedColor,
          this.graduatedStyle.classes,
          this.graduatedStyle.precision
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
          .catch((error) => {
            this.isLoading = false
            this.$emit('setButtonDisabled', false)
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
    addClassifyRule() {
      // put size and shape here
      this.classifyTable.push({
        id: uuidv1(),
        property: this.classifySelectedField,
        index: this.classifyTable.length + 1,
        from: 0.0,
        to: 0.0,
        size: this.classifyTable.length * 3.5,
        color: '$color-primary',
        operator: '<>',
      })
      this.opacityFactor++
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
      this.classifyTable.forEach((filter, index) => {
        let rule = {
          filter: {
            ...filter,
            field: `${parseFloat(filter.from).toFixed(
              this.graduatedStyle.precision
            )}-${parseFloat(filter.to).toFixed(this.graduatedStyle.precision)}`,
          },
          mark: {
            ...this.mark,
            size:
              this.selectedMethod === 'size'
                ? index * 3.5 + this.mark.size * 0.5
                : this.mark.size,
          },
          fill: {
            ...this.fill,
            color: filter.color,
            opacity:
              this.selectedMethod === 'color'
                ? (index + 1) / this.graduatedStyle.classes
                : 1,
          },
          stroke: {
            ...this.stroke,
            color: filter.color,
            opacity:
              this.selectedMethod === 'color'
                ? (index + 1) / this.graduatedStyle.classes
                : 1,
          },
        }
        this.graduatedStyle['labelEnabled'] = this.layerToEdit.labelingEnabled
        if (this.layerToEdit.labelingEnabled) {
          rule['label'] = { ...this.label }
        }
        this.graduatedStyle.rules.push(rule)
      })
      this.graduatedStyle['labelEnabled'] = this.layerToEdit.labelingEnabled
      this.graduatedStyle.selectedMethod = this.selectedMethod
      this.$emit('saveStyle', this.graduatedStyle)
    },
  },
  beforeMount() {
    let data = JSON.parse(this.layerToEdit.style.style)
    if (this.layerToEdit.style && data.symbologyType === 'Graduated') {
      data.rules.forEach((rule, index) => {
        if (index === 0) {
          if (rule.filter) this.classifySelectedField = rule.filter.property
          if (rule.fill) {
            this.selectedColor = rule.fill.color
          }
          if (rule.stroke) this.stroke = rule.stroke
          if (rule.mark)
            this.mark = {
              ...data.rules[0].mark,
              size:
                data.selectedMethod === 'size'
                  ? data.rules[0].mark.size * 2
                  : data.rules[0].mark.size,
            }
          data.labelEnabled && this.$emit('setLabel', data.rules[0].label)
        }
        this.classifyTable.push({
          ...rule.filter,
        })
      })
      this.graduatedStyle = {
        ...this.graduatedStyle,
        classes: data.classes,
        selectedMode: data.selectedMode,
        selectedMethod: data.selectedMethod,
        precision: data.precision,
      }
      this.selectedMethod = data.selectedMethod
      this.opacityFactor = data.classes
    }
  },
}
</script>

<style scoped lang="scss">
.actions {
  margin-bottom: 0.8rem !important;
  button {
    margin-right: 0.5rem;
  }
}
</style>