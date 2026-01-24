<template>
  <form @submit.prevent="onSubmit">
    <div class="modal-card" style="width: 500px">
      <section class="modal-card-body">
        <b-field label="Nom">
          <b-input
            id="map-name"
            name="name"
            type="text"
            :value="mapToEdit && mapToEdit.name"
            placeholder="Nom de map"
            required
            validation-message="Veuillez remplir ce champ"
          ></b-input>
        </b-field>
      </section>
      <footer class="right-align modal-card-foot">
        <button
          id="close-map-form"
          class="button"
          type="button"
          @click="$parent.close()"
        >
          Fermer
        </button>
        <button
          id="save-map"
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
import RestApi from '../../../methods/api.js'
import { mapState } from 'vuex'

export default {
  computed: {
    ...mapState(['users', 'profile'])
  },
  props: ['mapToEdit'],
  data() {
    return {
      isLoading: false,
      layers: []
    }
  },
  methods: {
     onSubmit(e) {
      this.isLoading = true
      const formData = new FormData(e.target)
      const newMap = { layers: [], users: [], groups: [] }
      newMap.name = formData.get('name')
      this.saveMap(newMap)
    },
    saveMap(map) {
      RestApi.createOrUpdate(
        'maps',
        map,
        this.mapToEdit,
        this.$store,
        this.$router,
        this.$notification
      )
        .then(data => {
          this.$emit('saved')
          this.isLoading = false
        })
        .catch(() => {
          this.$emit('saved')
        })
    }
  }
}
</script>

<style scoped>
</style>
