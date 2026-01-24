<template>
  <form @submit.prevent="onSubmit">
    <div
      id="geoProcessing"
      class="
        d-flex
        flex-column
        position-absolute
        border border-light
        geo-processing
      "
    >
      <div class="modal-card">
        <section class="modal-card-body p-0">
          <div class="geo-processing__rows row-column">
            <div class="geo-processing__config p-3">
              <div>
                <h1 class="geo-processing__title mb-4">Configuration</h1>
                <b-field label="Operation Spatial">
                  <b-select
                    placeholder="Veuillez sélectionner l'opération spatiale"
                    name="spatialOperation"
                    v-model="config.operation"
                    required
                    validation-message="Ce champ est obligatoire"
                    expanded
                  >
                    <option
                      v-for="op in spatialOperation"
                      :key="op.key"
                      :value="op.key"
                    >
                      {{ op.name }}
                    </option>
                  </b-select>
                </b-field>
                <b-field class="mb-2" label="Couche d'entrée">
                  <b-select
                    placeholder="Veuillez sélectionnez une couche"
                    name="layerA"
                    v-model="selectedLayerA"
                    required
                    validation-message="Ce champ est obligatoire"
                    expanded
                  >
                    <option
                      v-for="layer in layersSlug"
                      :key="layer.id"
                      :value="layer"
                    >
                      {{
                        layer.name.includes('____')
                          ? layer.name.split('____')[1]
                          : layer.name
                      }}
                    </option>
                  </b-select>
                </b-field>
                <b-checkbox
                  v-if="selectedLayerA"
                  v-model="applyFilterA"
                  name="layerFilterA"
                  >Appliquer un filtre
                </b-checkbox>
                <FilterCreaterias
                  v-if="applyFilterA"
                  layerName="layerA"
                  pkey="layerA"
                  :layer="selectedLayerA"
                  @updateRule="updateRule"
                />
                <div v-if="config.operation !== 'ST_Buffer'">
                  <b-field class="mb-2" label="Couche de superposition">
                    <b-select
                      placeholder="Veuillez sélectionnez une couche"
                      name="layerB"
                      v-model="selectedLayerB"
                      required
                      validation-message="Ce champ est obligatoire"
                      expanded
                    >
                      <option
                        v-for="layer in getOverlayLayerSlugs"
                        :key="layer.id"
                        :value="layer"
                      >
                        {{
                          layer.name.includes('____')
                            ? layer.name.split('____')[1]
                            : layer.name
                        }}
                      </option>
                    </b-select>
                  </b-field>
                  <b-checkbox
                    v-if="selectedLayerB"
                    v-model="applyFilterB"
                    name="layerFilterB"
                    >Appliquer un filtre
                  </b-checkbox>
                  <FilterCreaterias
                    v-if="applyFilterB"
                    layerName="layerB"
                    pkey="layerB"
                    :layer="selectedLayerB"
                    @updateRule="updateRule"
                  />
                </div>
                <div v-else>
                  <div class="d-flex justify-content-between">
                    <b-field label="Distance">
                      <b-input
                        type="number"
                        v-model="config.buffer.distance"
                        placeholder="par ex : 10.0"
                        class=""
                      ></b-input>
                    </b-field>
                    <b-field label="Unité">
                      <b-select
                        placeholder="Veuillez sélectionnez une option"
                        name="unit"
                        v-model="config.buffer.unit"
                      >
                        <option value="meters">Mettre</option>
                        <option value="feet">Pieds</option>
                        <option value="kilometers">Kilomètre</option>
                        <option value="miles">Milles</option>
                      </b-select>
                    </b-field>
                  </div>
                  <div
                    v-if="
                      selectedLayerA && selectedLayerA.topo === 'LineString'
                    "
                  >
                    <b-field label="Style d'embout">
                      <b-select
                        placeholder="Veuillez sélectionnez une option"
                        name="endCapStyle"
                        v-model="config.buffer.endCapStyle"
                        required
                        validation-message="Ce champ est obligatoire"
                        expanded
                      >
                        <option value="round">Round</option>
                        <option value="flat">Plat</option>
                        <option value="square">Carré</option>
                      </b-select>
                    </b-field>
                    <b-field label="Style de jointure">
                      <b-select
                        placeholder="Veuillez sélectionnez une option"
                        name="joinStyle"
                        v-model="config.buffer.joinStyle"
                        required
                        validation-message="Ce champ est obligatoire"
                        expanded
                      >
                        <option value="round">Round</option>
                        <option value="mitre">Mitre</option>
                        <option value="bevel">Biseau</option>
                      </b-select>
                    </b-field>
                    <b-field
                      v-if="config.buffer.joinStyle === 'mitre'"
                      label="Limite mitree"
                    >
                      <b-input
                        type="number"
                        v-model="config.buffer.mitreLimit"
                        placeholder="par ex : 10.0"
                        class=""
                      ></b-input>
                    </b-field>
                    <b-field label="Côté">
                      <b-select
                        placeholder="Veuillez sélectionnez une option"
                        name="joinStyle"
                        v-model="config.buffer.side"
                        required
                        validation-message="Ce champ est obligatoire"
                        expanded
                      >
                        <option value="left">à gauche</option>
                        <option value="right">à droite</option>
                        <option value="both">Les deux</option>
                      </b-select>
                    </b-field>
                  </div>
                  <div
                    v-else-if="
                      selectedLayerA && selectedLayerA.topo === 'Point'
                    "
                  >
                    <b-field label="Ensembles de quads">
                      <b-select
                        placeholder="Veuillez sélectionnez une option"
                        name="quadSegs"
                        v-model="config.buffer.quadSegs"
                        expanded
                      >
                        <option value="0">0</option>
                        <option value="2">2</option>
                        <option value="4">4</option>
                        <option value="6">6</option>
                        <option value="8">8</option>
                      </b-select>
                    </b-field>
                  </div>
                </div>
                <hr />
                <h2 class="geo-processing__sub-title mb-2">
                  Paramètres avancés
                </h2>
                <b-field label="Format de sortie">
                  <b-select
                    placeholder="Veuillez sélectionnez une option"
                    name="layerB"
                    v-model="config.outputFormat"
                    validation-message="Ce champ est obligatoire"
                    required
                    expanded
                  >
                    <option
                      v-for="outputFormat in outputFormats"
                      :key="outputFormat.key"
                      :value="outputFormat.key"
                    >
                      {{ outputFormat.name }}
                    </option>
                  </b-select>
                </b-field>
                <p
                  v-if="selectedLayerA"
                  class="geo-processing__fields_edition"
                  @click="editSelectedFields(true)"
                >
                  Modifier les champs sélectionnés ?
                </p>
                <b-field
                  label="
             Nom de sortie
            "
                >
                  <b-input
                    type="text"
                    v-model="config.newLayerName"
                    placeholder="Le nom de l'opération sera le nom par défaut"
                    class=""
                  ></b-input>
                </b-field>
                <div v-if="config.outputFormat === 'exportData'" class="block">
                  <b-radio
                    v-model="config.ext"
                    name="ext"
                    native-value="geojson"
                    required
                  >
                    GeoJson
                  </b-radio>
                  <b-radio
                    required
                    v-model="config.ext"
                    name="ext"
                    native-value="csv"
                  >
                    Csv
                  </b-radio>
                  <b-radio
                    required
                    v-model="config.ext"
                    name="ext"
                    native-value="excel"
                  >
                    Excel
                  </b-radio>
                </div>
                <br />
                <p>
                  <strong style="color: red">NB :</strong>
                  l'opération spatiale peut prendre un certain temps, merci de
                  vous patientez.
                </p>
              </div>
            </div>
            <div class="geo-processing__help p-3">
              <h1 class="geo-processing__title mb-4">Aide</h1>
              <div class="d-flex align-items-center">
                <h2>
                  {{
                    getSelelectedSpatialOperation &&
                    getSelelectedSpatialOperation.name
                  }}
                </h2>
                <img
                  v-if="getSelelectedSpatialOperation"
                  :src="getSelelectedSpatialOperation.icon"
                />
              </div>
              <p
                v-html="
                  getSelelectedSpatialOperation &&
                  getSelelectedSpatialOperation.description
                "
              ></p>
            </div>
          </div>
        </section>
        <footer
          class="modal-card-foot"
          style="
            padding: 1rem;
            margin: 0;
            justify-content: flex-start !important;
          "
        >
          <div class="nearby-filter__actions" style="margin: 0">
            <button
              :disabled="isLoading"
              :class="['button', 'is-primary', { 'is-loading': isLoading }]"
              id=" filter-layer"
              type="submit"
              style="background: $color-primary !important"
            >
              <b-icon class="mr-1" pack="fas" icon="filter" />Exécuter
            </button>
            <button
              @click="resetAll"
              id="resetAll"
              type="button"
              class="button is-success"
            >
              <b-icon class="mr-1" pack="fas" icon="sync-alt" />Réinitialiser
            </button>
          </div>
        </footer>
      </div>
    </div>
  </form>
</template>

<script>
import { mapGetters } from 'vuex'

import bufferImage from '../../assets/algebriqueOperations/buffer.svg'
import intersectionImage from '../../assets/algebriqueOperations/intersection.svg'
import unionImage from '../../assets/algebriqueOperations/union.svg'
import touchesImage from '../../assets/algebriqueOperations/touches.svg'
import disjointImage from '../../assets/algebriqueOperations/disjoint.svg'
import equalsImage from '../../assets/algebriqueOperations/equals.svg'
import containsImage from '../../assets/algebriqueOperations/contains.svg'
import crossesImage from '../../assets/algebriqueOperations/crosses.svg'
import differenceImage from '../../assets/algebriqueOperations/difference.svg'
import symDifferenceImage from '../../assets/algebriqueOperations/symDifference.svg'

import FilterCreaterias from '../ui/FilterCriterias.vue'
import { backApi } from '../../methods/serverApi'

import stroke from '~/mixins/style/stroke'
import fill from '~/mixins/style/fill'
import mark from '~/mixins/style/mark'

import FieldsSelection from '../ui/FieldsSelection.vue'
import { SecuredWMSURL, backend } from '../../constants'

import { LayerSymbolizer } from '../dashboard/symbologies/style'
export default {
  mixins: [stroke, fill, mark],
  components: {
    FilterCreaterias,
    FieldsSelection,
  },
  data() {
    return {
      showModal: true,
      spatialOperation: [
        {
          key: 'ST_Buffer',
          name: 'Tampon',
          icon: bufferImage,
          description:
            "Cet algorithme calcule une zone tampon pour toutes les entités d'une couche en entrée, en utilisant une distance fixe ou dynamique. <br />Le paramètre segments contrôle le nombre de segments de ligne à utiliser pour approximer un quart de cercle lors de la création de décalages arrondis. < br />Le paramètre de style d'extrémité contrôle la manière dont les fins de ligne sont gérées dans le tampon. <br /> Le paramètre de style de jointure spécifie si des jointures rondes, à onglet ou en biseau doivent être utilisées lors du décalage des coins d'une ligne. <br />Le paramètre de limite d'onglet s'applique uniquement aux styles de jointure à onglet et contrôle la distance maximale par rapport à la courbe de décalage à utiliser lors de la création d'une jointure à onglet.",
        },
        {
          key: 'ST_Clip',
          name: 'Clip',
          icon: intersectionImage,
          description:
            "Cet algorithme coupe une couche vectorielle en utilisant les caractéristiques d'une couche polygonale supplémentaire. Seules les parties des entités de la couche en entrée qui se trouvent dans les polygones de la couche de superposition seront ajoutées à la couche résultante. <br />Les attributs des entités ne sont pas modifiés, bien que les propriétés telles que la surface ou la longueur des entités soient modifiées par l'opération de découpage. Si de telles propriétés sont stockées en tant qu'attributs, ces attributs devront être mis à jour manuellement.",
        },
        {
          key: 'ST_Intersects',
          name: 'Intersects',
          icon: intersectionImage,
          description:
            "Si une géométrie ou une géographie partage une partie de l'espace, elles se croisent.",
        },
        {
          key: 'ST_Intersection',
          name: 'Intersection',
          icon: intersectionImage,
          description:
            "Cet algorithme extrait les portions d'entités qui se chevauchent dans les couches d'entrée et de superposition. Les entités de la couche d'intersection en sortie se voient attribuer les attributs des entités superposées des couches en entrée et superposées.",
        },
        {
          key: 'ST_Union',
          name: 'Union',
          icon: unionImage,
          description:
            "Cet algorithme vérifie les chevauchements entre les entités au sein de la couche en entrée et crée des entités distinctes pour les parties qui se chevauchent et qui ne se chevauchent pas. La zone de chevauchement créera autant d'entités qui se chevauchent identiques qu'il y a d'entités qui participent à ce chevauchement. <br /> Une couche de superposition peut également être utilisée, auquel cas les entités de chaque couche sont divisées au niveau de leur chevauchement avec les entités de l'autre, créant une couche contenant toutes les parties des couches d'entrée et de superposition. La table attributaire de la couche Union est remplie avec les valeurs attributaires de la couche d'origine respective pour les entités non chevauchantes, et les valeurs attributaires des deux couches pour les entités chevauchantes.",
        },
        {
          key: 'ST_DISJOINT',
          name: 'Disjointe',
          icon: disjointImage,
          description:
            "Les chevauchements, les touches, à l'intérieur de tous impliquent que les géométries ne sont pas spatialement disjointes. Si l'un des éléments susmentionnés renvoie vrai, alors les géométries ne sont pas spatialement disjointes. Disjoint implique faux pour l'intersection spatiale.",
        },
        {
          key: 'ST_Equals',
          name: 'Equals',
          icon: equalsImage,
          description:
            "Returns TRUE if the given Geometries are 'spatially equal'",
        },
        {
          key: 'ST_Contains',
          name: 'Contains',
          icon: containsImage,
          description:
            "La géométrie A contient la géométrie B si et seulement si aucun point de B ne se trouve à l'extérieur de A, et qu'au moins un point de l'intérieur de B se trouve à l'intérieur de A.",
        },
        {
          key: 'ST_Crosses',
          name: 'Crosses',
          icon: crossesImage,
          description:
            "ST_Crosses prend deux objets géométriques et renvoie TRUE si leur intersection 'se croise spatialement', c'est-à-dire que les géométries ont certains, mais pas tous les points intérieurs en commun. <br />L'intersection des intérieurs des géométries ne doit pas être l'ensemble vide et doit avoir une dimensionnalité inférieure à la dimension maximale des deux géométries d'entrée. De plus, l'intersection des deux géométries ne doit être égale à aucune des géométries sources. Sinon, il renvoie FALSE.",
        },
        {
          key: 'ST_Touches',
          name: 'Touches',
          icon: touchesImage,
          description:
            "Cet algorithme  renvoie VRAI si les seuls points communs entre g1 et g2 se trouvent dans l'union des frontières de g1 et g2. La relation ST_Touches s'applique à toutes les paires de relations Zone/Zone, Ligne/Ligne, Ligne/Zone, Point/Zone et Point/Ligne, mais pas à la paire Point/Point.",
        },
        {
          key: 'ST_Difference',
          name: 'Difference',
          icon: differenceImage,
          description:
            "Cet algorithme extrait les entités de la couche en entrée qui se trouvent à l'extérieur ou se chevauchent partiellement des entités de la couche de superposition. Les entités de la couche en entrée qui chevauchent partiellement les entités de la couche de superposition sont divisées le long de la limite de ces entités et seules les parties en dehors des entités de la couche de superposition sont conservées. <br />Les attributs ne sont pas modifiés, bien que les propriétés telles que la surface ou la longueur des entités soient modifiées par l'opération de différence. Si de telles propriétés sont stockées en tant qu'attributs, ces attributs devront être mis à jour manuellement.",
        },
        {
          key: 'ST_SymDifference',
          name: 'Différence symétrique',
          icon: symDifferenceImage,
          description:
            "Cet algorithme extrait les portions d'entités des couches d'entrée et de superposition qui ne se chevauchent pas. Les zones de chevauchement entre les deux couches sont supprimées. La table attributaire de la couche Différence symétrique contient les attributs d'origine des couches Entrée et Différence.",
        },
      ],
      outputFormats: [
        {
          key: 'newLayer',
          name: 'Créer une nouvelle couche',
        },
        {
          key: 'exportData',
          name: 'Enregistrer dans un fichier',
        },
      ],
      applyFilterA: false,
      applyFilterB: false,
      config: {
        operation: null,
        newLayerName: '',
        outputFormat: null,
        layerFilterA: null,
        layerFilterB: null,
        selectedFields: null,
        buffer: {
          distance: 0,
          unit: 'meters',
          endCapStyle: 'round',
          joinStyle: 'round',
          mitreLimit: 2,
          side: 'both',
          quadSegs: 0,
        },
        ext: 'geojson',
      },
      selectedLayerA: null,
      selectedLayerB: null,
      isLoading: false,
    }
  },
  computed: {
    getSelelectedSpatialOperation() {
      return (
        this.config.operation &&
        this.spatialOperation.filter((s) => s.key === this.config.operation)[0]
      )
    },
    ...mapGetters({
      layersSlug: 'maps/layersSlug',
      mapLayers: 'maps/mapLayers',
      currentMap: 'maps/getCurrentMap',
     mapThemes: 'maps/mapThemes'
    }),
    getOverlayLayerSlugs() {
      if (
        this.config.operation === 'ST_Touches' &&
        this.layersSlug.filter((s) => s === this.selectedLayerA).length > 0
      ) {
        if (
          this.layersSlug.filter((s) => s === this.selectedLayerA)[0].topo ===
          'Point'
        ) {
          return this.layersSlug.filter((l) => l.topo !== 'Point')
        } else {
          return this.layersSlug
        }
      } else if (
        this.config.operation === 'ST_Union' &&
        this.layersSlug.filter((s) => s === this.selectedLayerA).length > 0
      ) {
        return this.layersSlug.filter(
          (l) =>
            l.topo ===
            this.layersSlug.filter((s) => s === this.selectedLayerA)[0].topo
        )
      } else return this.layersSlug
    },
  },
  watch: {
    selectedLayerA() {
      this.applyFilterA = false
      this.config.layerFilterA = null
      this.config.buffer.side = 'both'
      this.config.selectedFields = null
    },
    selectedLayerB() {
      this.applyFilterB = false
      this.config.layerFilterB = null
      this.config.selectedFields = null
    },
  },
  methods: {
    editSelectedFields(openModal) {
      let fields = [
        ...this.selectedLayerA.fields.map((f) => {
          return {
            ...f,
            layer: 'layerA',
          }
        }),
      ]
      if (
        (this.config.operation === 'ST_Union' ||
          this.config.operation === 'ST_Intersection' ||
          this.config.operation === 'ST_SymDifference') &&
        this.selectedLayerB &&
        this.selectedLayerB.id != this.selectedLayerA.id
      ) {
        fields.push(
          ...this.selectedLayerB.fields.map((f) => {
            return {
              ...f,
              name: this.selectedLayerA.fields.some((s) => s.slug === f.slug)
                ? `overlay-${f.name}`
                : f.name,
              order: this.selectedLayerA.fields.length + 1,
              layer: 'layerB',
            }
          })
        )
      }
      var sel =
        this.config.selectedFields !== null
          ? this.config.selectedFields
          : fields
      if (openModal) {
        this.$buefy.modal.open({
          parent: this,
          component: FieldsSelection,
          hasModalCard: true,
          props: {
            fields: fields.map((f, index) => {
              return {
                ...f,
                order: sel.some((s) => s.id === f.id)
                  ? sel.find((s) => s.id === f.id).order
                  : fields.length + index + 1,
              }
            }),
            selectedFields: sel,
            withDetail: this.config.outputFormat === 'newLayer' ? true : false,
          },
          events: {
            updateSelectedFields: (value) => {
              this.config.selectedFields = value
            },
          },
        })
      } else {
        return fields
      }
    },
    resetAll() {
      this.config = {
        operation: null,
        newLayerName: '',
        outputFormat: null,
        layerFilterA: null,
        layerFilterB: null,
        selectedFields: null,
        buffer: {
          distance: 0,
          unit: 'meters',
          endCapStyle: 'round',
          joinStyle: 'round',
          mitreLimit: 2,
          side: 'both',
          quadSegs: 0,
        },
      }
      this.selectedLayerA = null
      this.selectedLayerB = null
      this.applyFilterA = false
      this.applyFilterB = false
    },
    onSubmit() {
      let defaultTheme = this.mapThemes.find((t) => t.isDefault)
      if (!defaultTheme) {
        this.$notification.error({
          message:
            'Veuillez sélectionner le thème par défaut sur lequel vous travaillez !',
        })
      }else{
      this.isLoading = true
      let simpleStyle = {
        symbologyType: 'Simple',
        rules: [],
      }
      simpleStyle['labelEnabled'] = false
      let rule = {
        fill: {
          opacity: 1,
          color: LayerSymbolizer.generateRandomColor(),
        },
        mark: this.mark,
        stroke: {
          color: LayerSymbolizer.generateRandomColor(),
          width: 1,
          opacity: 0.5,
        },
      }
      simpleStyle.rules.push(rule)
      let params = ''
      if (this.selectedLayerA && this.selectedLayerA.topo === 'LineString') {
        const { endCapStyle, joinStyle, mitreLimit, side } = this.config.buffer
        params = `endcap=${endCapStyle} join=${joinStyle}`
        if (joinStyle === 'mitre') {
          params += ` mitre_limit=${mitreLimit}`
        }
        params += ` side=${side}`
      } else if (this.selectedLayerA && this.selectedLayerA.topo === 'Point') {
        const { quadSegs } = this.config.buffer
        params = `quad_segs=${quadSegs}`
      }

      this.config = {
        ...this.config,
        selectedFields: this.config.selectedFields
          ? this.config.selectedFields
          : this.editSelectedFields(false),
        layerFilterA: this.applyFilterA ? this.config.layerFilterA : null,
        layerFilterB: this.applyFilterB ? this.config.layerFilterB : null,
        workingMap: this.currentMap.slug,
        targetTheme: {
            id: defaultTheme.id,
        },
        layerIdA: this.selectedLayerA.id,
        layerIdB: this.selectedLayerB ? this.selectedLayerB.id : '',
        order: this.currentMap.layers.length + 1,
        layerStyle: {
          isDefault: true,
          styleConfig: {
            isDefault: true,
            symbologyType: 'Simple',
            rules: [
              {
                fill: this.fill,
                mark: this.mark,
                stroke: this.stroke,
              },
            ],
          },
          mapId: this.currentMap.id,
        }
      }
      if (this.config.outputFormat === 'exportData') {
        this.config.selectedFields.map((f) => {
          return {
            ...f,
            visible: true,
          }
        })
      }
      this.config.buffer['params'] = params
      let config = {
        method: 'post',
        headers: {
          Authorization: `Bearer ${localStorage.getItem('sigToken')}`,
        },
        url: `${backend}/geo-processing/run`,
        data: this.config,
        timeout: 600000,
      }
      if (this.config.outputFormat === 'exportData') {
        config['responseType'] = 'blob'
      }
      backApi(config)
        .then((response) => {
          this.isLoading = false
          if (response.data && this.config.outputFormat === 'newLayer') {
            this.$store.commit('maps/attach', response.data)
            this.clearLayers()
            this.loadLayers()
            this.$notification.success({
              message: 'Félicitations!',
              description: "L'opération spatiale est  appliquée avec succès",
            })
          } else if (
            response.data &&
            this.config.outputFormat === 'exportData'
          ) {
            const url = window.URL.createObjectURL(new Blob([response.data]))
            const link = document.createElement('a')
            link.href = url

            link.setAttribute(
              'download',
              `${
                this.config.newLayerName
                  ? this.config.newLayerName
                  : this.config.operation
              }.${this.config.ext}`
            ) //or any other extension
            document.body.appendChild(link)
            link.click()
            link.remove()
            this.$notification.success({
              message: 'Félicitation!',
              description: `La couche ${this.config.newLayerName} est exportée avec succès.`,
            })
            this.isLoading = false
            setTimeout(() => {
              this.checkedRows = []
            }, 500)
          } else {
            this.$notification.warning({
              message: 'Remarque!',
              description: "L'opération spatiale ne produit aucune donnée",
            })
          }
        })
        .catch((error) => {
          this.isLoading = false
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
    updateRule(payload) {
      if (payload.layerName === 'layerA') {
        this.config.layerFilterA = payload.args.rule
      } else {
        this.config.layerFilterB = payload.args.rule
      }
    },
    clearLayers() {
      if (this.$layerGroups[`wmsLayer`]) {
        this.$map.removeLayer(this.$layerGroups['wmsLayer'])
      }
      if (this.$layerGroups[`wmsProperies`])
        this.$map.removeLayer(this.$layerGroups['wmsProperies'])

      this.mapLayers.forEach((layer) => {
        if (this.$layerGroups[`${layer.slug}`])
          this.$map.removeLayer(this.$layerGroups[`${layer.slug}`])
      })
    },
    loadLayers() {
      let mapLayers = this.currentMap.layers
        .filter((l) => l.visible)
        .map((layer) => layer)

      let myLayers = mapLayers.sort((a, b) => a.order - b.order)
      let wmsLayers = myLayers
        .map((layer) => `limite_admin:${layer.slug}`)
        .join(',')

      let wmsStyles = myLayers
        .map((layer) => (layer.layerType === 'RASTER' ? '' : layer.style && layer.style.name))
        .join(',')

      const token = localStorage.getItem('sigToken')
      this.$layerGroups[`wmsLayer`] = L.tileLayer
        .wms(`${SecuredWMSURL}`, {
          layers: wmsLayers,
          format: 'image/png',
          transparent: true,
          styles: wmsStyles,
          token,
        })
        .addTo(this.$map)
    },
  },
}
</script>

<style lang="scss">
.geo-processing {
  position: relative;
  border-radius: 5px;
  background-color: #fff;
  z-index: 399;
  top: 11px;
  right: -50rem;
  overflow: hidden;
  min-width: 50rem;
  height: 97%;
  @include respond('phone') {
    height: 60vh !important;
    max-height: 60vh !important;
    min-width: 0;
  }
  .modal-card {
    max-width: 100vw !important;
    margin: 0 !important;
    width: 100%;
    height: 100%;
    .modal-card-body {
      overflow: hidden;
    }
  }
  @include respond('phone') {
    .modal-card {
      overflow-y: auto;
      .modal-card-body {
        overflow: visible;
      }
    }
  }
  @include mapModalForMobile;
  &__config {
    flex: 0.6;
    padding-right: 1rem;
    position: relative;
    overflow-y: auto;
    overflow-x: hidden;
    @include respond('phone') {
      p {
        position: static !important;
      }
    }
    p {
      position: absolute;
      width: 98%;
    }
  }
  &__help {
    flex: 0.4;
    position: relative;
    padding: 0.5rem 1rem;
    border-left: 1px solid #f5f5f5;
    @include respond('phone') {
      overflow-y: visible;
      p {
        position: static !important;
      }
    }
    h2 {
      font-weight: 600;
      font-size: 1.15rem;
    }
    p {
      position: absolute;
      text-align: justify;
      width: 90%;
    }
    img {
      width: 30px;
      margin-left: 0.8rem;
    }
  }
  &__rows {
    display: flex;
    height: 100%;
  }
  &__title {
    font-weight: bold;
    font-size: 1.35rem;
    color: #030303db;
    @include respond('phone') {
      margin-bottom: 0.5rem !important;
    }
  }
  &__sub-title {
    font-weight: bold;
    font-size: 1.05rem;
    color: #030303db;
    font-style: italic;
  }
  &__fields_edition {
    position: static !important;
    cursor: pointer;
    text-decoration: underline;
    margin-bottom: 5px;
  }
  .e-rule-field {
    display: flex !important;
    align-items: center;
  }
  .help {
    margin-top: 0;
  }
}
</style>
