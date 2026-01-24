<template>
  <form class="new-layer" @submit.prevent="onSubmit">
    <div class="modal-card">
      <header class="modal-card-head">
        <h1 class="modal-card-title">
           Clonage de Carte
        </h1>
      </header>
      <section class="modal-card-body">
        <b-field label="La carte à cloner :">
          <b-input
            id="cloned-carte-name"
            name="name"
            type="text"
            disabled
            :value="mapToClone && mapToClone.name"
          ></b-input>
        </b-field>
        <b-field label="Nom de sortie :">
          <b-input
            id="layer-name"
            name="name"
            type="text"
            v-model="cloneConfig.outputName"
            pattern="[^,-]+"
            :placeholder="`${mapToClone && mapToClone.name}_clone`"
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
        <div class="mt-1">
          <label style="vertical-align: middle; font-weight:bold">Mode :</label>
          <b-radio
            v-model="cloneConfig.privacy"
            name="privacy"
            native-value="PUBLIC"
          >
            Public
          </b-radio>
          <b-radio
            v-model="cloneConfig.privacy"
            name="privacy"
            native-value="PRIVATE"
          >
            Privée
          </b-radio>
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

export default {
  props: ['mapToClone'],
  data() {
    return {
      isLoading: false,
      cloneConfig: {
        outputName: '',
        privacy: null,
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
    cloneMap() {
      backApi
        .post(`maps/${this.mapToClone.slug}/clone`, {
          ...this.cloneConfig,
        })
        .then(({ data }) => {
          this.$emit('saved')
          this.isLoading = true
          this.$store.commit('maps/add', data)
          this.$notification.success({
            message: 'Félicitation !',
            description: `La carte ${this.mapToClone.name} est cloné avec succès.`,
          })
        })
        .catch((error) => {
          this.showErrorMessage(error)
        })
    },
    onSubmit() {
      this.isLoading = true
      this.cloneMap()
    },
  },
  mounted(){
      this.cloneConfig.privacy = this.mapToClone.privacy
  }
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