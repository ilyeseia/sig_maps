<template>
  <div class="ticket">
    <b-switch style="margin-top: 3px" :value="labelEnabled" @input="updateValue"
      >Étiquettes de label</b-switch
    >
    <div v-if="labelEnabled">
      <b-field label="Colonne">
        <b-select
          validation-message="Veuillez remplir ce champ"
          placeholder="Veuillez sélectionner un champ"
          required
          v-model="label.property"
          expanded
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
      <b-field label="Police">
        <b-select v-model="label.police" expanded>
          <option
            v-for="font in avaibleFonts"
            :value="font"
            :key="font"
            :id="font"
          >
            {{ font }}
          </option>
        </b-select>
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
      <b-field label="Style de police">
        <b-select required v-model="label.fontStyle" expanded>
          <option value="normal">Normal</option>
          <option value="italic">Italic</option>
          <option value="oblique">Oblique</option>
        </b-select>
      </b-field>
      <b-field label="Poids de la police">
        <b-select required v-model="label.fontWeight" expanded>
          <option value="normal">Normal</option>
          <option value="bold">Bold</option>
        </b-select>
      </b-field>
      <b-field label="Couleur du police">
        <ejs-colorpicker
          :enableOpacity="false"
          v-model="label.fill"
        ></ejs-colorpicker>
      </b-field>
      <div v-if="layerToEdit.topo === 'LineString'" class="label-placement">
        <b-field label="Décalage perpendiculaire">
          <a-input-number
            v-model="label.perpendicularOffset"
            style="width: 5rem"
            name="perpendicularOffset"
            type="number"
          />
        </b-field>
      </div>
      <div class="label-placement" v-else>
        <b-field label="Axe X">
          <a-input-number
            v-model="label.anchorPointX"
            style="width: 5rem"
            name="anchor-x"
            type="number"
            :min="0"
            :max="1"
          />
        </b-field>
        <b-field label="Axe Y">
          <a-input-number
            v-model="label.andchorPointY"
            style="width: 5rem"
            name="anchor-y"
            type="number"
            :min="0"
            :max="1"
          />
        </b-field>
        <b-field label="Rotation">
          <a-input-number
            v-model="label.rotation"
            style="width: 5rem"
            name="rotation"
            type="number"
          />
        </b-field>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: ['layerToEdit', 'labelEnabled', 'label'],
  data() {
    return {
      avaibleFonts: [],
      defaultLabel: {
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
  computed: {
    getFields() {
      let exceptedItems = ['TEXTAREA', 'CAROUSEL', 'IMAGE']
      return this.layerToEdit.fields.filter(
        (f) => !exceptedItems.includes(f.type)
      )
    },
  },
  methods: {
    updateValue(e) {
      this.$emit('setLabelEnabled', e)
    },
  },
  beforeMount() {
    this.avaibleFonts = [
      'Arial',
      'Tahoma',
      'Courier New',
      'Times New Roman',
      'Verdana',
    ]
  },
  updated() {
    if (!this.labelEnabled) {
      this.$emit('setLabel', this.defaultLabel)
    }
    this.$emit('setLabelEnabled', this.labelEnabled)
  },
}
</script>

<style scoped lang="scss">
.ticket {
  overflow-y: auto;
  padding: 0 30px !important;
  @include respond('tab-port') {
    padding: 0 15px !important;
  }
  @include respond('phone') {
    padding: 0 10px !important;
  }
}
.label-placement {
  display: flex;
  > div:not(:last-child) {
    margin-right: 1.5rem;
  }
}
</style>