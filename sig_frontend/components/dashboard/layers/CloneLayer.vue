<template>
  <form class="new-layer" @submit.prevent="onSubmit">
    <div class="modal-card">
      <header class="modal-card-head">
        <h1 class="modal-card-title">
          {{ 'Clonage de Couche' }}
        </h1>
      </header>
      <section class="modal-card-body">
        <b-field label="La couche à cloner :">
          <b-input
            id="cloned-layer-name"
            name="name"
            type="text"
            disabled
            :value="layerToClone && layerToClone.name"
          ></b-input>
        </b-field>
        <b-field label="Nom de sortie :">
          <b-input
            id="layer-name"
            name="name"
            type="text"
            v-model="cloneConfig.outputName"
            pattern="[^,-]+"
            :placeholder="`${layerToClone && layerToClone.name}_clone`"
            required
            validation-message="Veuillez remplir ce champ correctement"
          ></b-input>
        </b-field>
        <h2 class="clone__title mb-2">Configuration</h2>
        <!-- <div>
          <b-checkbox
            v-model="cloneConfig.cloneFilters"
            name="clone-layer-filter"
            >Garder les filtres
          </b-checkbox>
        </div>
        <div>
          <b-checkbox
            v-model="cloneConfig.filtersShare"
            name="clone-layer-filter-share"
            >Garder le partage des filtres
          </b-checkbox>
        </div> -->
        <div>
          <b-checkbox v-model="cloneConfig.cloneUsers" name="clone-layer-users"
            >Garder le partage avec utilisateurs
          </b-checkbox>
        </div>
        <div>
          <b-checkbox v-model="cloneConfig.cloneGroups" name="clone-group-user"
            >Garder le partage avec groups
          </b-checkbox>
        </div>
      </section>
      <footer class="right-align modal-card-foot">
        <button
          id="close-layer-clone"
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
          Exécuter
        </button>
      </footer>
    </div>
  </form>
</template>

<script>
import { backApi } from '~/methods/serverApi'
import stroke from '~/mixins/style/stroke'
import fill from '~/mixins/style/fill'
import mark from '~/mixins/style/mark'
export default {
  props: ['layerToClone'],
  mixins: [stroke, fill, mark],
  data() {
    return {
      isLoading: false,
      cloneConfig: {
        outputName: '',
        cloneFilters: true,
        filtersShare: true,
        cloneUsers: true,
        cloneGroups: true,
      },
    }
  },
  methods: {
    showErrorMessage(error) {
      this.isLoading = false
      this.$emit('saved')
      this.$notification.error({
        message: 'Erreur !',
        description:
          error.response && error.response.data
            ? error.response.data.message
            : "Une erreur inattendue s'est produite",
      })
    },
    cloneLayer() {
      backApi
        .post(`layers/${this.layerToClone.slug}/clone`, {
          ...this.cloneConfig,
        })
        .then(({ data }) => {
          this.$emit('saved')
          this.isLoading = true
          this.$store.commit('layers/add', data)
          this.$notification.success({
            message: 'Félicitation !',
            description: `La couche ${this.layerToClone.name} est cloné avec succès.`,
          })
        })
        .catch((error) => {
          this.showErrorMessage(error)
        })
    },
    onSubmit() {
      this.isLoading = true
      let simpleStyle = null
      simpleStyle = {
        symbologyType: 'Simple',
        rules: [],
      }
      simpleStyle['labelEnabled'] = false
      let rule = {
        fill: this.fill,
        mark: this.mark,
        stroke: this.stroke,
      }
      simpleStyle.rules.push(rule)
      this.cloneLayer(simpleStyle)
    },
  },
}
</script>
<style lang="scss">
.clone {
  &__title {
    font-weight: bold;
    font-size: 1.05rem;
    color: #030303db;
    font-style: italic;
  }
}
</style>