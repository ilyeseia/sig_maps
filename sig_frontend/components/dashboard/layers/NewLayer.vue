<template>
  <form class="new-layer" @submit.prevent="onSubmit">
    <div class="modal-card">
      <header class="modal-card-head">
        <h1 class="modal-card-title">
          {{ layerToEdit ? 'Edition de couche' : 'Nouvelle Couche' }}
        </h1>
      </header>
      <section class="modal-card-body">
        <b-field label="Nom">
          <b-input
            id="layer-name"
            name="name"
            type="text"
            :value="layerToEdit ? layerToEdit.name : ''"
            pattern="[^,-]+"
            placeholder="Nom de la couche"
            required
            validation-message="Veuillez remplir ce champ correctement"
          ></b-input>
        </b-field>

        <b-field
          label="Type"
          v-if="layerToEdit == null || layerToEdit.layerType != 'RASTER'"
        >
          <div class="control is-expanded">
            <span class="select is-fullwidth is-empty">
              <select :disabled="lockTopo" id="layer-topo" name="topo" required>
                <option
                  v-if="!layerToEdit"
                  selected="selected"
                  hidden="hidden"
                  value=""
                >
                  Type de la Geometry
                </option>
                <option
                  value="Point"
                  :selected="layerToEdit && layerToEdit.topo === 'Point'"
                >
                  Point
                </option>
                <option
                  value="LineString"
                  :selected="layerToEdit && layerToEdit.topo === 'LineString'"
                >
                  Ligne
                </option>
                <option
                  value="Polygon"
                  :selected="layerToEdit && layerToEdit.topo === 'Polygon'"
                >
                  Polygone
                </option>
                <!-- <option
                  value="Polygon"
                  :selected="layerToEdit && layerToEdit.topo === 'MultiPoint'"
                >Multi Point</option> -->
                <option
                  value="MultiPolygon"
                  :selected="layerToEdit && layerToEdit.topo === 'MultiPolygon'"
                >
                  Multi Polygon
                </option>
              </select>
            </span>
          </div>
        </b-field>
        <b-field
          label="Identifiant"
          v-if="layerToEdit == null || layerToEdit.layerType != 'RASTER'"
        >
          <div class="control is-expanded">
            <span class="select is-fullwidth is-empty">
              <select id="layer-idetifiant" name="identifiant">
                <option
                  v-for="field in fields"
                  :selected="
                    layerToEdit &&
                    layerToEdit.identifiant &&
                    field.slug === layerToEdit.identifiant
                  "
                  :key="field.id"
                  :value="field.slug ? field.slug : field.name"
                >
                  {{ field.name }}
                </option>
              </select>
            </span>
          </div>
        </b-field>
        <b-field label="Type de la Limite">
          <div class="control is-expanded">
            <span class="select is-fullwidth is-empty">
              <select id="layer-type-limit" name="typeLimit" required>
                <option
                  v-for="limit in typeLimites"
                  :selected="layerToEdit && limit.key === layerToEdit.typeLimit"
                  :key="limit.key"
                  :value="limit.key"
                >
                  {{ limit.value }}
                </option>
              </select>
            </span>
          </div>
        </b-field>
        <b-field
          label="Propriétés "
          v-if="layerToEdit == null || layerToEdit.layerType != 'RASTER'"
        ></b-field>
        <b-field
          grouped
          v-if="layerToEdit == null || layerToEdit.layerType != 'RASTER'"
        >
          <b-select v-model="selectedField">
            <option value="TEXT">Text</option>
            <option value="NUMBER">Double</option>
            <option value="INTEGER">Entier</option>
            <option value="BOOLEAN">Oui/non</option>
            <option value="TEXTAREA">Zone de text</option>
            <option value="TIME">Heure</option>
            <option value="DATETIME">DATE HEURE</option>
            <option value="DATE">Date</option>
            <option value="IMAGE">Image</option>
            <option value="SELECT">Select</option>
            <option value="MULTI_SELECT">Multi select</option>
            <option value="CAROUSEL">Carousel</option>
            <option value="HTMLEDITOR">Html</option>
          </b-select>
          <button class="button" type="button" @click="addField">
            Ajouter un champ
          </button>
        </b-field>

        <b-table
          :show-detail-icon="true"
          :data="fields"
          custom-row-key="id"
          v-sortable
          detailed
        >
          <template slot-scope="props">
            <b-table-column width="300" field="name" label="Name">
              <b-input
                name="propName"
                v-model="props.row.name"
                type="text"
                required
                placeholder="Définir le nom de la propriété"
                pattern="[^,;,:,?,!]+"
                validation-message="Veuillez remplir ce champ correctement"
              ></b-input>
            </b-table-column>
            <b-table-column field="name" label="Type">
              <b-select
                expanded
                name="propType"
                type="text"
                v-model="props.row.type"
              >
                <option
                  v-if="Object.keys(props.row).length <= 1"
                  selected="selected"
                  disabled="disabled"
                  hidden="hidden"
                >
                  Type de propriété
                </option>
                <option value="TEXT">Text</option>
                <option value="NUMBER">Double</option>
                <option value="INTEGER">Entier</option>
                <option value="TEXTAREA">Zone de text</option>
                <option value="TIME">Heure</option>
                <option value="DATETIME">Date heure</option>
                <option value="BOOLEAN">Oui/non</option>
                <option value="DATE">Date</option>
                <option value="IMAGE">Image</option>
                <option value="SELECT">Select</option>
                <option value="MULTI_SELECT">Multi select</option>
                <option value="CAROUSEL">Carousel</option>
                <option value="HTMLEDITOR">Html</option>
              </b-select>
            </b-table-column>
            <b-table-column field="edit" label="Edition">
              <button
                v-if="props.row.id"
                type="button"
                class="button"
                @click="deleteField(props.row.id)"
              >
                <b-icon pack="fas" icon="times-circle"></b-icon>
              </button>
            </b-table-column>
          </template>
          <template slot="detail" slot-scope="props">
            <div>
              <div class="row">
                <div class="column align-middle" style="margin-top: 9px">
                  <b-checkbox v-model="props.row.required" name="checkbox-1"
                    >Required
                  </b-checkbox>
                  <b-checkbox v-model="props.row.visible" name="checkbox-2"
                    >Visible
                  </b-checkbox>
                  <b-checkbox v-model="props.row.publique" name="checkbox-2"
                    >Publique
                  </b-checkbox>
                </div>
                <div class="column">
                  <list-select
                    v-if="
                      props.row.type === 'SELECT' ||
                      props.row.type === 'MULTI_SELECT'
                    "
                    :key="listKey"
                    placeholder="Sélectionner le référentiel"
                    :list="getListItems()"
                    optionText="text"
                    optionValue="value"
                    :selectedItem="{
                      value: props.row.resource,
                      text: props.row.resource && props.row.resource.name,
                    }"
                    @select="onSelect($event, props.row)"
                  >
                  </list-select>
                </div>
              </div>
              <div v-if="getFieldsItems(props.row).length > 0" class="row">
                <div
                  v-if="props.row.type === 'SELECT'"
                  class="column"
                  style="margin-top: 9px"
                >
                  Parent
                </div>
                <div class="column">
                  <v-select
                    :id="parentKey"
                    v-if="props.row.type === 'SELECT'"
                    :options="getFieldsItems(props.row)"
                    :value="getFieldParent(props.row.parent)"
                    @input="onSelectField($event, props.row)"
                    label="text"
                    placeholder="Selectionner une option"
                  >
                    <div slot="no-options">Aucune option ici!</div>
                  </v-select>
                </div>
              </div>
            </div>
          </template>
        </b-table>
      </section>
      <footer class="right-align modal-card-foot">
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
          :class="['button', 'is-primary', { 'is-loading': isLoading }]"
        >
          Enregistrer
        </button>
      </footer>
    </div>
  </form>
</template>
<script>
import RestApi from '../../../methods/api'
import Sortable from 'sortablejs'
const createSortable = (el, options, vnode) => {
  return Sortable.create(el, {
    ...options,
    onEnd: function (evt) {
      vnode.context.sortFields(evt)
    },
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
  },
}
import { ListSelect } from 'vue-search-select'
import { mapState } from 'vuex'
import vSelect from 'vue-select'
import 'vue-select/dist/vue-select.css'

import stroke from '~/mixins/style/stroke'
import fill from '~/mixins/style/fill'
import mark from '~/mixins/style/mark'
import { backApi } from '../../../methods/serverApi'

export default {
  mixins: [stroke, fill, mark],
  components: {
    ListSelect,
    vSelect,
  },
  directives: { sortable },
  props: ['layerToEdit'],
  data() {
    return {
      typeLimites: [
        {
          key: 'LAYER',
          value: 'PAR DEFAULT',
        },
        {
          key: 'ADMIN',
          value: 'ADMINISTRATIVE',
        },
      ],
      selectedField: 'NUMBER',
      isLoading: false,
      fields: [],
      listKey: 1,
      lockTopo: false,
      parentKey: 1,
    }
  },
  computed: {
    ...mapState(['resources']),
  },
  methods: {
    getListItems() {
      return this.resources.resources.map((f) => {
        return { text: f.name, value: f }
      })
    },
    onSelect(event, row) {
      console.log("select")
      row.resource = event.value
      this.listKey += 1
    },
    onReset(e, row){
      console.log("rest")
      row.resource = ""
      this.listKey++
    },
    getFieldsItems(_field) {
      return this.fields
        .filter((f) => f.type == 'SELECT' && f.name != _field.name)
        .map((f) => {
          return { text: f.name, value: f.id }
        })
    },
    onSelectField(event, row) {
      row.parent = event ? event.value : null
      this.parentKey++
    },
    getFieldParent(parentId) {
      if (parentId != null) {
        let _field = this.fields.find((f) => f.id == parentId)
        if (_field) return { text: _field.name, value: _field.name }
      }
    },
    async onSubmit(e) {
      const formData = new FormData(e.target)
      var newLayer = { fields: [] }
      this.isLoading = true
      newLayer.name = formData.get('name')
      newLayer.topo = this.lockTopo
        ? this.layerToEdit.topo
        : formData.get('topo')
      newLayer.identifiant = formData.get('identifiant')
      newLayer.typeLimit = formData.get('typeLimit')

      this.fields.forEach(
        ({
          id,
          name,
          required,
          type,
          order,
          resource,
          visible,
          publique,
          parent,
        }) => {
          newLayer.fields.push({
            id,
            name,
            order,
            required,
            type,
            visible,
            resource,
            publique,
            parent,
          })
        }
      )
      this.saveLayer(newLayer)
    },
    showNotificationError(error) {
      this.$emit('saved')
      this.isLoading = false
    },
    showSuccess() {
      this.$emit('saved')
      this.isLoading = false
    },
    saveLayer(layer) {
      RestApi.createOrUpdate(
        'layers',
        layer,
        this.layerToEdit,
        this.$store,
        this.$router,
        this.$notification
      )
        .then(() => {
          this.showSuccess()
        })
        .catch((error) => this.showNotificationError(error))
    },
    addField() {
      let order = 1

      if (this.fields && this.fields.length) {
        order = this.fields[this.fields.length - 1].order + 1
      }

      this.fields.push({
        type: this.selectedField,
        required: true,
        visible: true,
        publique: true,
        parent: null,
        order,
      })
    },
    deleteField(id) {
      this.$confirm({
        title: 'Supprission de la carte',
        style: ' top: 20',
        content:
          "Voulez vous supprimer ce champ ? l'opération vas supprimer les données liées à ce champ ?",
        okText: 'Oui',
        okType: 'danger',
        cancelText: 'Non',
        zIndex: '5000',
        onOk: () => {
          this.fields = this.fields.filter((field) => field.id != id)
        },
        onCancel: () => {},
      })
    },
    sortFieldsWithinLayer(evt) {
      let data = this.fields

      let oldIndex = evt.oldIndex
      let newIndex = evt.newIndex

      const item = data[oldIndex]

      if (newIndex > oldIndex) {
        for (let i = oldIndex; i < newIndex; i++) {
          data[i] = data[i + 1]
        }
      } else {
        for (let i = oldIndex; i > newIndex; i--) {
          data[i] = data[i - 1]
        }
      }

      data[newIndex] = item
      return data
    },
    sortFields(evt) {
      let data = this.sortFieldsWithinLayer(evt)
      this.fields = data

      for (let i = 0; i < data.length; i++) {
        this.fields[i].order = i
      }
    },
  },
  beforeMount() {
    RestApi.findAll(
      'resources',
      { limit: -1 },
      'resources/set',
      this.$store,
      this.$router,
      this.$notification
    )
    if (this.layerToEdit) {
      this.layerToEdit.fields.forEach(
        ({
          id,
          name,
          slug,
          type,
          required,
          resource,
          visible,
          order,
          publique,
          parent,
        }) =>
          this.fields.push({
            id,
            name,
            slug,
            type,
            visible,
            order,
            required,
            resource,
            publique,
            parent,
          })
      )
      let list = this.fields.sort((a, b) => {
        return a.order - b.order
      })
    }
    if (this.layerToEdit)
      backApi.get(`layers/${this.layerToEdit.id}/has-data`).then(({ data }) => {
        this.lockTopo = data
      })
  },
}
</script>
<style  lang="scss">
.new-layer {
  /*!
 * # Semantic UI 2.4.0 - Dropdown
 */
  .ui.dropdown {
    cursor: pointer;
    position: relative;
    display: inline-block;
    outline: none;
    text-align: left;
    transition: width 0.1s ease, -webkit-box-shadow 0.1s ease;
    transition: box-shadow 0.1s ease, width 0.1s ease,
      -webkit-box-shadow 0.1s ease;
    -webkit-tap-highlight-color: rgba(0, 0, 0, 0);
  }
  .ui.dropdown .menu {
    cursor: auto;
    position: absolute;
    display: none;
    outline: none;
    top: 100%;
    min-width: -webkit-max-content;
    min-width: -moz-max-content;
    min-width: max-content;
    margin: 0;
    padding: 0 0;
    background: #fff;
    font-size: 1em;
    text-shadow: none;
    text-align: left;
    -webkit-box-shadow: 0 2px 3px 0 rgba(34, 36, 38, 0.15);
    box-shadow: 0 2px 3px 0 rgba(34, 36, 38, 0.15);
    border: 1px solid rgba(34, 36, 38, 0.15);
    border-radius: 0.28571429rem;
    -webkit-transition: opacity 0.1s ease;
    transition: opacity 0.1s ease;
    z-index: 11;
    will-change: transform, opacity;
  }
  .ui.dropdown .menu > * {
    white-space: nowrap;
  }
  .ui.dropdown > input:not(.search):first-child,
  .ui.dropdown > select {
    display: none !important;
  }
  .ui.dropdown > .dropdown.icon {
    position: relative;
    font-size: 0.85714286em;
    margin: 0 0 0 1em;
  }
  .ui.dropdown .menu > .item .dropdown.icon {
    width: auto;
    float: right;
    margin: 0 0 0 1em;
  }
  .ui.dropdown .menu > .item .dropdown.icon + .text {
    margin-right: 1em;
  }
  .ui.dropdown > .text {
    display: inline-block;
    -webkit-transition: none;
    transition: none;
  }
  .ui.dropdown .menu > .item {
    position: relative;
    cursor: pointer;
    display: block;
    border: none;
    height: auto;
    text-align: left;
    border-top: none;
    line-height: 1em;
    color: rgba(0, 0, 0, 0.87);
    padding: 0.78571429rem 1.14285714rem !important;
    font-size: 1rem;
    text-transform: none;
    font-weight: 400;
    -webkit-box-shadow: none;
    box-shadow: none;
    -webkit-touch-callout: none;
  }
  .ui.dropdown .menu > .item:first-child {
    border-top-width: 0;
  }
  .ui.dropdown .menu .item > [class*='right floated'],
  .ui.dropdown > .text > [class*='right floated'] {
    float: right !important;
    margin-right: 0 !important;
    margin-left: 1em !important;
  }
  .ui.dropdown .menu .item > [class*='left floated'],
  .ui.dropdown > .text > [class*='left floated'] {
    float: left !important;
    margin-left: 0 !important;
    margin-right: 1em !important;
  }
  .ui.dropdown .menu .item > .flag.floated,
  .ui.dropdown .menu .item > .icon.floated,
  .ui.dropdown .menu .item > .image.floated,
  .ui.dropdown .menu .item > img.floated {
    margin-top: 0;
  }
  .ui.dropdown .menu > .header {
    margin: 1rem 0 0.75rem;
    padding: 0 1.14285714rem;
    color: rgba(0, 0, 0, 0.85);
    font-size: 0.78571429em;
    font-weight: 700;
    text-transform: uppercase;
  }
  .ui.dropdown .menu > .divider {
    border-top: 1px solid rgba(34, 36, 38, 0.1);
    height: 0;
    margin: 0.5em 0;
  }
  .ui.dropdown.dropdown .menu > .input {
    width: auto;
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    margin: 1.14285714rem 0.78571429rem;
    min-width: 10rem;
  }
  .ui.dropdown .menu > .header + .input {
    margin-top: 0;
  }
  .ui.dropdown .menu > .input:not(.transparent) input {
    padding: 0.5em 1em;
  }
  .ui.dropdown .menu > .input:not(.transparent) .button,
  .ui.dropdown .menu > .input:not(.transparent) .icon,
  .ui.dropdown .menu > .input:not(.transparent) .label {
    padding-top: 0.5em;
    padding-bottom: 0.5em;
  }
  .ui.dropdown .menu > .item > .description,
  .ui.dropdown > .text > .description {
    float: right;
    margin: 0 0 0 1em;
    color: rgba(0, 0, 0, 0.4);
  }
  .ui.dropdown .menu > .message {
    padding: 0.78571429rem 1.14285714rem;
    font-weight: 400;
  }
  .ui.dropdown .menu > .message:not(.ui) {
    color: rgba(0, 0, 0, 0.4);
  }
  .ui.dropdown .menu .menu {
    top: 0 !important;
    left: 100%;
    right: auto;
    margin: 0 0 0 -0.5em !important;
    border-radius: 0.28571429rem !important;
    z-index: 21 !important;
  }
  .ui.dropdown .menu .menu:after {
    display: none;
  }
  .ui.dropdown .menu > .item > .flag,
  .ui.dropdown .menu > .item > .icon,
  .ui.dropdown .menu > .item > .image,
  .ui.dropdown .menu > .item > .label,
  .ui.dropdown .menu > .item > img,
  .ui.dropdown > .text > .flag,
  .ui.dropdown > .text > .icon,
  .ui.dropdown > .text > .image,
  .ui.dropdown > .text > .label,
  .ui.dropdown > .text > img {
    margin-top: 0;
  }
  .ui.dropdown .menu > .item > .flag,
  .ui.dropdown .menu > .item > .icon,
  .ui.dropdown .menu > .item > .image,
  .ui.dropdown .menu > .item > .label,
  .ui.dropdown .menu > .item > img,
  .ui.dropdown > .text > .flag,
  .ui.dropdown > .text > .icon,
  .ui.dropdown > .text > .image,
  .ui.dropdown > .text > .label,
  .ui.dropdown > .text > img {
    margin-left: 0;
    float: none;
    margin-right: 0.78571429rem;
  }
  .ui.dropdown .menu > .item > .image,
  .ui.dropdown .menu > .item > img,
  .ui.dropdown > .text > .image,
  .ui.dropdown > .text > img {
    display: inline-block;
    vertical-align: top;
    width: auto;
    margin-top: -0.5em;
    margin-bottom: -0.5em;
    max-height: 2em;
  }
  .ui.dropdown .ui.menu > .item:before,
  .ui.menu .ui.dropdown .menu > .item:before {
    display: none;
  }
  .ui.menu .ui.dropdown .menu .active.item {
    border-left: none;
  }
  .ui.buttons > .ui.dropdown:last-child .menu,
  .ui.menu .right.dropdown.item .menu,
  .ui.menu .right.menu .dropdown:last-child .menu {
    left: auto;
    right: 0;
  }
  .ui.label.dropdown .menu {
    min-width: 100%;
  }
  .ui.dropdown.icon.button > .dropdown.icon {
    margin: 0;
  }
  .ui.button.dropdown .menu {
    min-width: 100%;
  }
  .ui.selection.dropdown {
    cursor: pointer;
    word-wrap: break-word;
    line-height: 1em;
    white-space: normal;
    outline: 0;
    -webkit-transform: rotate(0deg);
    transform: rotate(0deg);
    min-width: 14em;
    min-height: 2.71428571em;
    background: #fff;
    display: inline-block;
    padding: 0.78571429em 2.1em 0.78571429em 1em;
    color: rgba(0, 0, 0, 0.87);
    -webkit-box-shadow: none;
    box-shadow: none;
    border: 1px solid rgba(34, 36, 38, 0.15);
    border-radius: 0.28571429rem;
    -webkit-transition: width 0.1s ease, -webkit-box-shadow 0.1s ease;
    transition: width 0.1s ease, -webkit-box-shadow 0.1s ease;
    transition: box-shadow 0.1s ease, width 0.1s ease;
    transition: box-shadow 0.1s ease, width 0.1s ease,
      -webkit-box-shadow 0.1s ease;
  }
  .ui.selection.dropdown.active,
  .ui.selection.dropdown.visible {
    z-index: 10;
  }
  select.ui.dropdown {
    height: 38px;
    padding: 0.5em;
    border: 1px solid rgba(34, 36, 38, 0.15);
    visibility: visible;
  }
  .ui.selection.dropdown > .delete.icon,
  .ui.selection.dropdown > .dropdown.icon,
  .ui.selection.dropdown > .search.icon {
    cursor: pointer;
    position: absolute;
    width: auto;
    height: auto;
    line-height: 1.21428571em;
    top: 0.78571429em;
    right: 1em;
    z-index: 3;
    margin: -0.78571429em;
    padding: 0.91666667em;
    opacity: 0.8;
    -webkit-transition: opacity 0.1s ease;
    transition: opacity 0.1s ease;
  }
  .ui.compact.selection.dropdown {
    min-width: 0;
  }
  .ui.selection.dropdown .menu {
    overflow-x: hidden;
    overflow-y: auto;
    -webkit-backface-visibility: hidden;
    backface-visibility: hidden;
    -webkit-overflow-scrolling: touch;
    border-top-width: 0 !important;
    width: auto;
    outline: none;
    margin: 0 -1px;
    min-width: calc(100% + 2px);
    width: calc(100% + 2px);
    border-radius: 0 0 0.28571429rem 0.28571429rem;
    -webkit-box-shadow: 0 2px 3px 0 rgba(34, 36, 38, 0.15);
    box-shadow: 0 2px 3px 0 rgba(34, 36, 38, 0.15);
    -webkit-transition: opacity 0.1s ease;
    transition: opacity 0.1s ease;
  }
  .ui.selection.dropdown .menu:after,
  .ui.selection.dropdown .menu:before {
    display: none;
  }
  .ui.selection.dropdown .menu > .message {
    padding: 0.78571429rem 1.14285714rem;
  }
  @media only screen and (max-width: 767px) {
    .ui.selection.dropdown .menu {
      max-height: 8.01428571rem;
    }
  }
  @media only screen and (min-width: 768px) {
    .ui.selection.dropdown .menu {
      max-height: 10.68571429rem;
    }
  }
  @media only screen and (min-width: 992px) {
    .ui.selection.dropdown .menu {
      max-height: 16.02857143rem;
    }
  }
  @media only screen and (min-width: 1920px) {
    .ui.selection.dropdown .menu {
      max-height: 21.37142857rem;
    }
  }
  .ui.selection.dropdown .menu > .item {
    border-top: 1px solid #fafafa;
    padding: 0.78571429rem 1.14285714rem !important;
    white-space: normal;
    word-wrap: normal;
  }
  .ui.selection.dropdown .menu > .hidden.addition.item {
    display: none;
  }
  .ui.selection.dropdown:hover {
    border-color: rgba(34, 36, 38, 0.35);
    -webkit-box-shadow: none;
    box-shadow: none;
  }
  .ui.selection.active.dropdown,
  .ui.selection.active.dropdown .menu {
    border-color: #96c8da;
    -webkit-box-shadow: 0 2px 3px 0 rgba(34, 36, 38, 0.15);
    box-shadow: 0 2px 3px 0 rgba(34, 36, 38, 0.15);
  }
  .ui.selection.dropdown:focus {
    border-color: #96c8da;
    -webkit-box-shadow: none;
    box-shadow: none;
  }
  .ui.selection.dropdown:focus .menu {
    border-color: #96c8da;
    -webkit-box-shadow: 0 2px 3px 0 rgba(34, 36, 38, 0.15);
    box-shadow: 0 2px 3px 0 rgba(34, 36, 38, 0.15);
  }
  .ui.selection.visible.dropdown > .text:not(.default) {
    font-weight: 400;
    color: rgba(0, 0, 0, 0.8);
  }
  .ui.selection.active.dropdown:hover,
  .ui.selection.active.dropdown:hover .menu {
    border-color: #96c8da;
    -webkit-box-shadow: 0 2px 3px 0 rgba(34, 36, 38, 0.15);
    box-shadow: 0 2px 3px 0 rgba(34, 36, 38, 0.15);
  }
  .ui.active.selection.dropdown > .dropdown.icon,
  .ui.visible.selection.dropdown > .dropdown.icon {
    opacity: '';
    z-index: 3;
  }
  .ui.active.selection.dropdown {
    border-bottom-left-radius: 0 !important;
    border-bottom-right-radius: 0 !important;
  }
  .ui.active.empty.selection.dropdown {
    border-radius: 0.28571429rem !important;
    -webkit-box-shadow: none !important;
    box-shadow: none !important;
  }
  .ui.active.empty.selection.dropdown .menu {
    border: none !important;
    -webkit-box-shadow: none !important;
    box-shadow: none !important;
  }
  .ui.search.dropdown {
    min-width: '';
  }
  .ui.search.dropdown > input.search {
    background: none transparent !important;
    border: none !important;
    -webkit-box-shadow: none !important;
    box-shadow: none !important;
    cursor: text;
    top: 0;
    left: 1px;
    width: 100%;
    outline: none;
    -webkit-tap-highlight-color: rgba(255, 255, 255, 0);
    padding: inherit;
    position: absolute;
    z-index: 2;
  }
  .ui.search.dropdown > .text {
    cursor: text;
    position: relative;
    left: 1px;
    z-index: 3;
  }
  .ui.search.selection.dropdown > input.search,
  .ui.search.selection.dropdown > span.sizer {
    line-height: 1.21428571em;
    padding: 0.67857143em 2.1em 0.67857143em 1em;
  }
  .ui.search.selection.dropdown > span.sizer {
    display: none;
    white-space: pre;
  }
  .ui.search.dropdown.active > input.search,
  .ui.search.dropdown.visible > input.search {
    cursor: auto;
  }
  .ui.search.dropdown.active > .text,
  .ui.search.dropdown.visible > .text {
    pointer-events: none;
  }
  .ui.active.search.dropdown input.search:focus + .text .flag,
  .ui.active.search.dropdown input.search:focus + .text .icon {
    opacity: 0.45;
  }
  .ui.active.search.dropdown input.search:focus + .text {
    color: hsla(0, 0%, 45.1%, 0.87) !important;
  }
  .ui.search.dropdown .menu {
    overflow-x: hidden;
    overflow-y: auto;
    -webkit-backface-visibility: hidden;
    backface-visibility: hidden;
    -webkit-overflow-scrolling: touch;
  }
  @media only screen and (max-width: 767px) {
    .ui.search.dropdown .menu {
      max-height: 8.01428571rem;
    }
  }
  @media only screen and (min-width: 768px) {
    .ui.search.dropdown .menu {
      max-height: 10.68571429rem;
    }
  }
  @media only screen and (min-width: 992px) {
    .ui.search.dropdown .menu {
      max-height: 16.02857143rem;
    }
  }
  @media only screen and (min-width: 1920px) {
    .ui.search.dropdown .menu {
      max-height: 21.37142857rem;
    }
  }
  .ui.multiple.dropdown {
    padding: 0.22619048em 2.1em 0.22619048em 0.35714286em;
  }
  .ui.multiple.dropdown .menu {
    cursor: auto;
  }
  .ui.multiple.search.dropdown,
  .ui.multiple.search.dropdown > input.search {
    cursor: text;
  }
  .ui.multiple.dropdown > .label {
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
    display: inline-block;
    vertical-align: top;
    white-space: normal;
    font-size: 1em;
    padding: 0.35714286em 0.78571429em;
    margin: 0.14285714rem 0.28571429rem 0.14285714rem 0;
    -webkit-box-shadow: 0 0 0 1px rgba(34, 36, 38, 0.15) inset;
    box-shadow: inset 0 0 0 1px rgba(34, 36, 38, 0.15);
  }
  .ui.multiple.dropdown .dropdown.icon {
    margin: '';
    padding: '';
  }
  .ui.multiple.dropdown > .text {
    position: static;
    padding: 0;
    max-width: 100%;
    margin: 0.45238095em 0 0.45238095em 0.64285714em;
    line-height: 1.21428571em;
  }
  .ui.multiple.dropdown > .label ~ input.search {
    margin-left: 0.14285714em !important;
  }
  .ui.multiple.dropdown > .label ~ .text {
    display: none;
  }
  .ui.multiple.search.dropdown > .text {
    display: inline-block;
    position: absolute;
    top: 0;
    left: 0;
    padding: inherit;
    margin: 0.45238095em 0 0.45238095em 0.64285714em;
    line-height: 1.21428571em;
  }
  .ui.multiple.search.dropdown > .label ~ .text {
    display: none;
  }
  .ui.multiple.search.dropdown > input.search {
    position: static;
    padding: 0;
    max-width: 100%;
    margin: 0.45238095em 0 0.45238095em 0.64285714em;
    width: 2.2em;
    line-height: 1.21428571em;
  }
  .ui.inline.dropdown {
    cursor: pointer;
    display: inline-block;
    color: inherit;
  }
  .ui.inline.dropdown .dropdown.icon {
    margin: 0 0.21428571em 0 0.21428571em;
    vertical-align: baseline;
  }
  .ui.inline.dropdown > .text {
    font-weight: 700;
  }
  .ui.inline.dropdown .menu {
    cursor: auto;
    margin-top: 0.21428571em;
    border-radius: 0.28571429rem;
  }
  .ui.dropdown .menu .active.item {
    background: transparent;
    font-weight: 700;
    color: rgba(0, 0, 0, 0.95);
    -webkit-box-shadow: none;
    box-shadow: none;
    z-index: 12;
  }
  .ui.dropdown .menu > .item:hover {
    background: rgba(0, 0, 0, 0.05);
    color: rgba(0, 0, 0, 0.95);
    z-index: 13;
  }
  .ui.loading.dropdown > i.icon {
    height: 1em !important;
  }
  .ui.loading.selection.dropdown > i.icon {
    padding: 1.5em 1.28571429em !important;
  }
  .ui.loading.dropdown > i.icon:before {
    border-radius: 500rem;
    border: 0.2em solid rgba(0, 0, 0, 0.1);
  }
  .ui.loading.dropdown > i.icon:after,
  .ui.loading.dropdown > i.icon:before {
    position: absolute;
    content: '';
    top: 50%;
    left: 50%;
    margin: -0.64285714em 0 0 -0.64285714em;
    width: 1.28571429em;
    height: 1.28571429em;
  }
  .ui.loading.dropdown > i.icon:after {
    -webkit-box-shadow: 0 0 0 1px transparent;
    box-shadow: 0 0 0 1px transparent;
    -webkit-animation: dropdown-spin 0.6s linear;
    animation: dropdown-spin 0.6s linear;
    -webkit-animation-iteration-count: infinite;
    animation-iteration-count: infinite;
    border-radius: 500rem;
    border-color: #767676 transparent transparent;
    border-style: solid;
    border-width: 0.2em;
  }
  .ui.loading.dropdown.button > i.icon:after,
  .ui.loading.dropdown.button > i.icon:before {
    display: none;
  }
  @-webkit-keyframes dropdown-spin {
    0% {
      -webkit-transform: rotate(0deg);
      transform: rotate(0deg);
    }
    to {
      -webkit-transform: rotate(1turn);
      transform: rotate(1turn);
    }
  }
  @keyframes dropdown-spin {
    0% {
      -webkit-transform: rotate(0deg);
      transform: rotate(0deg);
    }
    to {
      -webkit-transform: rotate(1turn);
      transform: rotate(1turn);
    }
  }
  .ui.default.dropdown:not(.button) > .text,
  .ui.dropdown:not(.button) > .default.text {
    color: hsla(0, 0%, 74.9%, 0.87);
  }
  .ui.default.dropdown:not(.button) > input:focus ~ .text,
  .ui.dropdown:not(.button) > input:focus ~ .default.text {
    color: hsla(0, 0%, 45.1%, 0.87);
  }
  .ui.loading.dropdown > .text {
    -webkit-transition: none;
    transition: none;
  }
  .ui.dropdown .loading.menu {
    display: block;
    visibility: hidden;
    z-index: -1;
  }
  .ui.dropdown > .loading.menu {
    left: 0 !important;
    right: auto !important;
  }
  .ui.dropdown > .menu .loading.menu {
    left: 100% !important;
    right: auto !important;
  }
  .ui.dropdown .menu .selected.item,
  .ui.dropdown.selected {
    background: rgba(0, 0, 0, 0.03);
    color: rgba(0, 0, 0, 0.95);
  }
  .ui.dropdown > .filtered.text {
    visibility: hidden;
  }
  .ui.dropdown .filtered.item {
    display: none !important;
  }
  .ui.dropdown.error,
  .ui.dropdown.error > .default.text,
  .ui.dropdown.error > .text {
    color: #9f3a38;
  }
  .ui.selection.dropdown.error {
    background: #fff6f6;
    border-color: #e0b4b4;
  }
  .ui.dropdown.error > .menu,
  .ui.dropdown.error > .menu .menu,
  .ui.selection.dropdown.error:hover {
    border-color: #e0b4b4;
  }
  .ui.dropdown.error > .menu > .item {
    color: #9f3a38;
  }
  .ui.multiple.selection.error.dropdown > .label {
    border-color: #e0b4b4;
  }
  .ui.dropdown.error > .menu > .item:hover {
    background-color: #fff2f2;
  }
  .ui.dropdown.error > .menu .active.item {
    background-color: #fdcfcf;
  }
  .ui.dropdown > .clear.dropdown.icon {
    opacity: 0.8;
    -webkit-transition: opacity 0.1s ease;
    transition: opacity 0.1s ease;
  }
  .ui.dropdown > .clear.dropdown.icon:hover {
    opacity: 1;
  }
  .ui.disabled.dropdown,
  .ui.dropdown .menu > .disabled.item {
    cursor: default;
    pointer-events: none;
    opacity: 0.45;
  }
  .ui.dropdown .menu {
    left: 0;
  }
  .ui.dropdown .menu .right.menu,
  .ui.dropdown .right.menu > .menu {
    left: 100% !important;
    right: auto !important;
    border-radius: 0.28571429rem !important;
  }
  .ui.dropdown > .left.menu {
    left: auto !important;
    right: 0 !important;
  }
  .ui.dropdown .menu .left.menu,
  .ui.dropdown > .left.menu .menu {
    left: auto;
    right: 100%;
    margin: 0 -0.5em 0 0 !important;
    border-radius: 0.28571429rem !important;
  }
  .ui.dropdown .item .left.dropdown.icon,
  .ui.dropdown .left.menu .item .dropdown.icon {
    width: auto;
    float: left;
    margin: 0 0 0 0;
  }
  .ui.dropdown .item .left.dropdown.icon + .text,
  .ui.dropdown .left.menu .item .dropdown.icon + .text {
    margin-left: 1em;
    margin-right: 0;
  }
  .ui.upward.dropdown > .menu {
    top: auto;
    bottom: 100%;
    -webkit-box-shadow: 0 0 3px 0 rgba(0, 0, 0, 0.08);
    box-shadow: 0 0 3px 0 rgba(0, 0, 0, 0.08);
    border-radius: 0.28571429rem 0.28571429rem 0 0;
  }
  .ui.dropdown .upward.menu {
    top: auto !important;
    bottom: 0 !important;
  }
  .ui.simple.upward.active.dropdown,
  .ui.simple.upward.dropdown:hover {
    border-radius: 0.28571429rem 0.28571429rem 0 0 !important;
  }
  .ui.upward.dropdown.button:not(.pointing):not(.floating).active {
    border-radius: 0.28571429rem 0.28571429rem 0 0;
  }
  .ui.upward.selection.dropdown .menu {
    border-top-width: 1px !important;
    border-bottom-width: 0 !important;
    -webkit-box-shadow: 0 -2px 3px 0 rgba(0, 0, 0, 0.08);
    box-shadow: 0 -2px 3px 0 rgba(0, 0, 0, 0.08);
  }
  .ui.upward.selection.dropdown:hover {
    -webkit-box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.05);
    box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.05);
  }
  .ui.active.upward.selection.dropdown,
  .ui.upward.selection.dropdown.visible {
    border-radius: 0 0 0.28571429rem 0.28571429rem !important;
  }
  .ui.upward.selection.dropdown.visible {
    -webkit-box-shadow: 0 0 3px 0 rgba(0, 0, 0, 0.08);
    box-shadow: 0 0 3px 0 rgba(0, 0, 0, 0.08);
  }
  .ui.upward.active.selection.dropdown:hover {
    -webkit-box-shadow: 0 0 3px 0 rgba(0, 0, 0, 0.05);
    box-shadow: 0 0 3px 0 rgba(0, 0, 0, 0.05);
  }
  .ui.upward.active.selection.dropdown:hover .menu {
    -webkit-box-shadow: 0 -2px 3px 0 rgba(0, 0, 0, 0.08);
    box-shadow: 0 -2px 3px 0 rgba(0, 0, 0, 0.08);
  }
  .ui.dropdown .scrolling.menu,
  .ui.scrolling.dropdown .menu {
    overflow-x: hidden;
    overflow-y: auto;
  }
  .ui.scrolling.dropdown .menu {
    overflow-x: hidden;
    -webkit-backface-visibility: hidden;
    backface-visibility: hidden;
    -webkit-overflow-scrolling: touch;
  }
  .ui.dropdown .scrolling.menu,
  .ui.scrolling.dropdown .menu {
    overflow-y: auto;
    min-width: 100% !important;
    width: auto !important;
  }
  .ui.dropdown .scrolling.menu {
    position: static;
    border: none;
    -webkit-box-shadow: none !important;
    box-shadow: none !important;
    border-radius: 0 !important;
    margin: 0 !important;
    border-top: 1px solid rgba(34, 36, 38, 0.15);
  }
  .ui.dropdown .scrolling.menu .item:first-child,
  .ui.dropdown .scrolling.menu > .item.item.item,
  .ui.scrolling.dropdown .menu .item.item.item,
  .ui.scrolling.dropdown .menu .item:first-child {
    border-top: none;
  }
  .ui.dropdown > .animating.menu .scrolling.menu,
  .ui.dropdown > .visible.menu .scrolling.menu {
    display: block;
  }
  @media (-ms-high-contrast: none) {
    .ui.dropdown .scrolling.menu,
    .ui.scrolling.dropdown .menu {
      min-width: calc(100% - 17px);
    }
  }
  @media only screen and (max-width: 767px) {
    .ui.dropdown .scrolling.menu,
    .ui.scrolling.dropdown .menu {
      max-height: 10.28571429rem;
    }
  }
  @media only screen and (min-width: 768px) {
    .ui.dropdown .scrolling.menu,
    .ui.scrolling.dropdown .menu {
      max-height: 15.42857143rem;
    }
  }
  @media only screen and (min-width: 992px) {
    .ui.dropdown .scrolling.menu,
    .ui.scrolling.dropdown .menu {
      max-height: 20.57142857rem;
    }
  }
  @media only screen and (min-width: 1920px) {
    .ui.dropdown .scrolling.menu,
    .ui.scrolling.dropdown .menu {
      max-height: 20.57142857rem;
    }
  }
  .ui.simple.dropdown .menu:after,
  .ui.simple.dropdown .menu:before {
    display: none;
  }
  .ui.simple.dropdown .menu {
    position: absolute;
    display: block;
    overflow: hidden;
    top: -9999px !important;
    opacity: 0;
    width: 0;
    height: 0;
    -webkit-transition: opacity 0.1s ease;
    transition: opacity 0.1s ease;
  }
  .ui.simple.active.dropdown,
  .ui.simple.dropdown:hover {
    border-bottom-left-radius: 0 !important;
    border-bottom-right-radius: 0 !important;
  }
  .ui.simple.active.dropdown > .menu,
  .ui.simple.dropdown:hover > .menu {
    overflow: visible;
    width: auto;
    height: auto;
    top: 100% !important;
    opacity: 1;
  }
  .ui.simple.dropdown:hover > .menu > .item:hover > .menu,
  .ui.simple.dropdown > .menu > .item:active > .menu {
    overflow: visible;
    width: auto;
    height: auto;
    top: 0 !important;
    left: 100% !important;
    opacity: 1;
  }
  .ui.simple.disabled.dropdown:hover .menu {
    display: none;
    height: 0;
    width: 0;
    overflow: hidden;
  }
  .ui.simple.visible.dropdown > .menu {
    display: block;
  }
  .ui.fluid.dropdown {
    display: block;
    width: 100%;
    min-width: 0;
  }
  .ui.fluid.dropdown > .dropdown.icon {
    float: right;
  }
  .ui.floating.dropdown .menu {
    left: 0;
    right: auto;
    -webkit-box-shadow: 0 2px 4px 0 rgba(34, 36, 38, 0.12),
      0 2px 10px 0 rgba(34, 36, 38, 0.15) !important;
    box-shadow: 0 2px 4px 0 rgba(34, 36, 38, 0.12),
      0 2px 10px 0 rgba(34, 36, 38, 0.15) !important;
    border-radius: 0.28571429rem !important;
  }
  .ui.floating.dropdown > .menu {
    margin-top: 0.5em !important;
    border-radius: 0.28571429rem !important;
  }
  .ui.pointing.dropdown > .menu {
    top: 100%;
    margin-top: 0.78571429rem;
    border-radius: 0.28571429rem;
  }
  .ui.pointing.dropdown > .menu:after {
    display: block;
    position: absolute;
    pointer-events: none;
    content: '';
    visibility: visible;
    -webkit-transform: rotate(45deg);
    transform: rotate(45deg);
    width: 0.5em;
    height: 0.5em;
    -webkit-box-shadow: -1px -1px 0 0 rgba(34, 36, 38, 0.15);
    box-shadow: -1px -1px 0 0 rgba(34, 36, 38, 0.15);
    background: #fff;
    z-index: 2;
    top: -0.25em;
    left: 50%;
    margin: 0 0 0 -0.25em;
  }
  .ui.top.left.pointing.dropdown > .menu {
    top: 100%;
    bottom: auto;
    left: 0;
    right: auto;
    margin: 1em 0 0;
  }
  .ui.top.left.pointing.dropdown > .menu:after {
    top: -0.25em;
    left: 1em;
    right: auto;
    margin: 0;
    -webkit-transform: rotate(45deg);
    transform: rotate(45deg);
  }
  .ui.top.right.pointing.dropdown > .menu {
    top: 100%;
    bottom: auto;
    right: 0;
    left: auto;
    margin: 1em 0 0;
  }
  .ui.top.pointing.dropdown > .left.menu:after,
  .ui.top.right.pointing.dropdown > .menu:after {
    top: -0.25em;
    left: auto !important;
    right: 1em !important;
    margin: 0;
    -webkit-transform: rotate(45deg);
    transform: rotate(45deg);
  }
  .ui.left.pointing.dropdown > .menu {
    top: 0;
    left: 100%;
    right: auto;
    margin: 0 0 0 1em;
  }
  .ui.left.pointing.dropdown > .menu:after {
    top: 1em;
    left: -0.25em;
    margin: 0 0 0 0;
    -webkit-transform: rotate(-45deg);
    transform: rotate(-45deg);
  }
  .ui.left:not(.top):not(.bottom).pointing.dropdown > .left.menu {
    left: auto !important;
    right: 100% !important;
    margin: 0 1em 0 0;
  }
  .ui.left:not(.top):not(.bottom).pointing.dropdown > .left.menu:after {
    top: 1em;
    left: auto;
    right: -0.25em;
    margin: 0 0 0 0;
    -webkit-transform: rotate(135deg);
    transform: rotate(135deg);
  }
  .ui.right.pointing.dropdown > .menu {
    top: 0;
    left: auto;
    right: 100%;
    margin: 0 1em 0 0;
  }
  .ui.right.pointing.dropdown > .menu:after {
    top: 1em;
    left: auto;
    right: -0.25em;
    margin: 0 0 0 0;
    -webkit-transform: rotate(135deg);
    transform: rotate(135deg);
  }
  .ui.bottom.pointing.dropdown > .menu {
    top: auto;
    bottom: 100%;
    left: 0;
    right: auto;
    margin: 0 0 1em;
  }
  .ui.bottom.pointing.dropdown > .menu:after {
    top: auto;
    bottom: -0.25em;
    right: auto;
    margin: 0;
    -webkit-transform: rotate(-135deg);
    transform: rotate(-135deg);
  }
  .ui.bottom.pointing.dropdown > .menu .menu {
    top: auto !important;
    bottom: 0 !important;
  }
  .ui.bottom.left.pointing.dropdown > .menu {
    left: 0;
    right: auto;
  }
  .ui.bottom.left.pointing.dropdown > .menu:after {
    left: 1em;
    right: auto;
  }
  .ui.bottom.right.pointing.dropdown > .menu {
    right: 0;
    left: auto;
  }
  .ui.bottom.right.pointing.dropdown > .menu:after {
    left: auto;
    right: 1em;
  }
  .ui.pointing.upward.dropdown .menu,
  .ui.top.pointing.upward.dropdown .menu {
    top: auto !important;
    bottom: 100% !important;
    margin: 0 0 0.78571429rem;
    border-radius: 0.28571429rem;
  }
  .ui.pointing.upward.dropdown .menu:after,
  .ui.top.pointing.upward.dropdown .menu:after {
    top: 100% !important;
    bottom: auto !important;
    -webkit-box-shadow: 1px 1px 0 0 rgba(34, 36, 38, 0.15);
    box-shadow: 1px 1px 0 0 rgba(34, 36, 38, 0.15);
    margin: -0.25em 0 0;
  }
  .ui.right.pointing.upward.dropdown:not(.top):not(.bottom) .menu {
    top: auto !important;
    bottom: 0 !important;
    margin: 0 1em 0 0;
  }
  .ui.right.pointing.upward.dropdown:not(.top):not(.bottom) .menu:after {
    top: auto !important;
    bottom: 0 !important;
    margin: 0 0 1em 0;
    -webkit-box-shadow: -1px -1px 0 0 rgba(34, 36, 38, 0.15);
    box-shadow: -1px -1px 0 0 rgba(34, 36, 38, 0.15);
  }
  .ui.left.pointing.upward.dropdown:not(.top):not(.bottom) .menu {
    top: auto !important;
    bottom: 0 !important;
    margin: 0 0 0 1em;
  }
  .ui.left.pointing.upward.dropdown:not(.top):not(.bottom) .menu:after {
    top: auto !important;
    bottom: 0 !important;
    margin: 0 0 1em 0;
    -webkit-box-shadow: -1px -1px 0 0 rgba(34, 36, 38, 0.15);
    box-shadow: -1px -1px 0 0 rgba(34, 36, 38, 0.15);
  }
  @font-face {
    font-family: Dropdown;
    src: url(data:application/font-woff;charset=utf-8;base64,d09GRgABAAAAAAVgAA8AAAAACFAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAABGRlRNAAABWAAAABwAAAAchGgaq0dERUYAAAF0AAAAHAAAAB4AJwAPT1MvMgAAAZAAAABDAAAAVnW4TJdjbWFwAAAB1AAAAEsAAAFS8CcaqmN2dCAAAAIgAAAABAAAAAQAEQFEZ2FzcAAAAiQAAAAIAAAACP//AANnbHlmAAACLAAAAQoAAAGkrRHP9WhlYWQAAAM4AAAAMAAAADYPK8YyaGhlYQAAA2gAAAAdAAAAJANCAb1obXR4AAADiAAAACIAAAAiCBkAOGxvY2EAAAOsAAAAFAAAABQBnAIybWF4cAAAA8AAAAAfAAAAIAEVAF5uYW1lAAAD4AAAATAAAAKMFGlj5HBvc3QAAAUQAAAARgAAAHJoedjqd2ViZgAABVgAAAAGAAAABrO7W5UAAAABAAAAANXulPUAAAAA1r4hgAAAAADXu2Q1eNpjYGRgYOABYjEgZmJgBEIOIGYB8xgAA/YAN3jaY2BktGOcwMDKwMI4jTGNgYHBHUp/ZZBkaGFgYGJgZWbACgLSXFMYHFT/fLjFeOD/AQY9xjMMbkBhRpAcAN48DQYAeNpjYGBgZoBgGQZGBhDwAfIYwXwWBgMgzQGETAwMqn8+8H649f8/lHX9//9b7Pzf+fWgusCAkY0BzmUE6gHpQwGMDMMeAACbxg7SAAARAUQAAAAB//8AAnjadZBPSsNAGMXfS+yMqYgOhpSuSlKadmUhiVEhEMQzFF22m17BbbvzCh5BXCUn6EG8gjeQ4DepwYo4i+/ffL95j4EDA+CFC7jQuKyIeVHrI3wkleq9F7XrSInKteOeHdda8bOoaeepSc00NWPz/LRec9G8GabyGtEdF7h19z033GAMTK7zbM42xNEZpzYof0RtQ5CUHAQJ73OtVyutc+3b7Ou//b8XNlsPx3jgjUifABdhEohKJJL5iM5p39uqc7X1+sRQSqmGrUVhlsJ4lpmEUVwyT8SUYtg0P9DyNzPADDs+tjrGV6KRCRfsui3eHcL4/p8ZXvfMlcnEU+CLv7hDykOP+AKTPTxbAAB42mNgZGBgAGKuf5KP4vltvjLIMzGAwLV9ig0g+vruFFMQzdjACOJzMIClARh0CTJ42mNgZGBgPPD/AJD8wgAEjA0MjAyogAMAbOQEAQAAAAC7ABEAAAAAAKoAAAH0AAABgAAAAUAACAFAAAgAwAAXAAAAAAAAACoAKgAqADIAbACGAKAAugDSeNpjYGRgYOBkUGFgYgABEMkFhAwM/xn0QAIADdUBdAB42qWQvUoDQRSFv3GjaISUQaymSmGxJoGAsRC0iPYLsU50Y6IxrvlRtPCJJKUPIBb+PIHv4EN4djKuKAqCDHfmu+feOdwZoMCUAJNbAlYUMzaUlM14jjxbngOq7HnOia89z1Pk1vMCa9x7ztPkzfMyJbPj+ZGi6Xp+omxuPD+zaD7meaFg7mb8GrBqHmhwxoAxlm0uiRkpP9X5m26pKRoMxTGR1D49Dv/Yb/91o6l8qL6eu5n2hZQzn68utR9m3FU2cB4t9cdSLG2utI+44Eh/P9bqKO+oJ/WxmXssj77YkrjasZQD6SFddythk3Wtzrf+UF2p076Udla1VNzsERP3kkjVRKel7mp1udXYcHtZSlV7RfmJe1GiFWveluaeKD5/MuJcSk8Tpm/vvwPIbmJleNpjYGKAAFYG7ICTgYGRiZGZkYWRlZGNkZ2Rg5GTLT2nsiDDEEIZsZfmZRqZujmDaDcDAxcI7WIOpS2gtCWUdgQAZkcSmQAAAAFblbO6AAA=)
      format('woff');
    font-weight: 400;
    font-style: normal;
  }
  .ui.dropdown > .dropdown.icon {
    font-family: Dropdown;
    line-height: 1;
    height: 1em;
    width: 1.23em;
    -webkit-backface-visibility: hidden;
    backface-visibility: hidden;
    font-weight: 400;
    font-style: normal;
    text-align: center;
    width: auto;
  }
  .ui.dropdown > .dropdown.icon:before {
    content: '\F0D7';
  }
  .ui.dropdown .menu .item .dropdown.icon:before {
    content: '\F0DA';
  }
  .ui.dropdown .item .left.dropdown.icon:before,
  .ui.dropdown .left.menu .item .dropdown.icon:before {
    content: '\F0D9';
  }
  .ui.vertical.menu .dropdown.item > .dropdown.icon:before {
    content: '\F0DA';
  }
  .ui.dropdown > .clear.icon:before {
    content: '\F00D';
  }
  /*!
 * # Semantic UI 2.4.1 - Label
 */
  .ui.label {
    display: inline-block;
    line-height: 1;
    vertical-align: baseline;
    margin: 0 0.14285714em;
    background-color: #e8e8e8;
    background-image: none;
    padding: 0.5833em 0.833em;
    color: rgba(0, 0, 0, 0.6);
    text-transform: none;
    font-weight: 700;
    border: 0 solid transparent;
    border-radius: 0.28571429rem;
    -webkit-transition: background 0.1s ease;
    transition: background 0.1s ease;
  }
  .ui.label:first-child {
    margin-left: 0;
  }
  .ui.label:last-child {
    margin-right: 0;
  }
  .ui.label > a,
  a.ui.label {
    cursor: pointer;
  }
  .ui.label > a {
    color: inherit;
    opacity: 0.5;
    -webkit-transition: opacity 0.1s ease;
    transition: opacity 0.1s ease;
  }
  .ui.label > a:hover {
    opacity: 1;
  }
  .ui.label > img {
    width: auto !important;
    vertical-align: middle;
    height: 2.1666em !important;
  }
  .ui.label > .icon {
    width: auto;
    margin: 0 0.75em 0 0;
  }
  .ui.label > .detail {
    display: inline-block;
    vertical-align: top;
    font-weight: 700;
    margin-left: 1em;
    opacity: 0.8;
  }
  .ui.label > .detail .icon {
    margin: 0 0.25em 0 0;
  }
  .ui.label > .close.icon,
  .ui.label > .delete.icon {
    cursor: pointer;
    margin-right: 0;
    margin-left: 0.5em;
    font-size: 0.92857143em;
    opacity: 0.5;
    -webkit-transition: background 0.1s ease;
    transition: background 0.1s ease;
  }
  .ui.label > .delete.icon:hover {
    opacity: 1;
  }
  .ui.dropdown {
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
  }
  .ui.dropdown,
  .ui.dropdown input {
    font-size: 14px;
  }
  .ui.dropdown .menu {
    -webkit-box-sizing: content-box;
    box-sizing: content-box;
  }
  .ui.dropdown .menu > .item {
    font-size: 14px;
  }
  .ui.selection.dropdown {
    min-height: 14px;
  }
  .ui.selection.dropdown .menu {
    min-width: 100%;
    width: 100%;
  }
  .ui.multiple.dropdown > .label {
    position: relative;
    padding-right: calc(0.78571em + 15px);
  }
  .ui.multiple.dropdown > .label i.icon.delete:before {
    content: '\D7';
    font-family: Arial, sans-serif;
  }
  .ui.multiple.dropdown > .label i.icon.delete {
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
    position: absolute;
    right: 10px;
    display: inline-block;
    width: 18px;
    height: 24px;
    margin: -5px -10px;
  }
  .ui.multiple.dropdown > .label i.icon.delete:after,
  .ui.multiple.dropdown > .label i.icon.delete:before {
    position: absolute;
    content: ' ';
    height: 12px;
    width: 3px;
    top: 25%;
    left: 25%;
    -webkit-transform: translate(-25%, -25%);
    transform: translate(-25%, -25%);
    background-color: #333;
  }
  .ui.multiple.dropdown > .label i.icon.delete:before {
    -webkit-transform: rotate(45deg);
    transform: rotate(45deg);
  }
  .ui.multiple.dropdown > .label i.icon.delete:after {
    -webkit-transform: rotate(-45deg);
    transform: rotate(-45deg);
  }
}

.wrap {
  margin: 0 auto;
  width: 300px;
  text-align: center;
}

.header {
  padding: 10px;
  text-align: center;
  background: #1abc9c;
  color: white;
  font-size: 16px;
}

.right-align {
  display: flex;
  flex-direction: row;
  justify-content: flex-end;
}
</style>
