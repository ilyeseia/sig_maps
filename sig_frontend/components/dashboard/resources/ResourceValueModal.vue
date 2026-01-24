<template>
  <form @submit.prevent="onSubmit">
    <div class="modal-card" style="width: 500px; height: fit-content">
      <header class="modal-card-head">
        <h1 class="modal-card-title">
          {{ resourceVToEdit.isNew ? 'Nouvelle valeur' : 'Edition de la valeur' }}
        </h1>
      </header>
      <section class="modal-card-body">
        <b-field label="Valeur">
          <b-input
            id="rv-value"
            type="text"
            v-model="value"
            placeholder="value"
            required
            validation-message="Veuillez remplir ce champ"
          ></b-input>
        </b-field>
      </section>
      <footer class="right-align modal-card-foot">
        <button
          id="close-settingsType-form"
          class="button"
          type="button"
          @click="$parent.close()"
        >
          Fermer
        </button>
        <button
          id="save-settingsType"
          type="submit"
          :disabled="resourceVToEdit.value === value"
          :class="['button', 'is-primary', { 'is-loading': isLoading }]"
        >
          Enregistrer
        </button>
      </footer>
    </div>
  </form>
</template>

<script>
import { backApi } from '~/methods/serverApi'
export default {
  props: ['resourceVToEdit'],
  data() {
    return {
      value: '',
      isLoading: false,
    }
  },
  mounted() {
    if (this.resourceVToEdit) {
      this.value = this.resourceVToEdit.value
    }
  },
  methods: {
    onSubmit() {
      this.isLoading = true
      if (this.resourceVToEdit.isNew) {
        backApi
          .post(`resourcevalues`, {
            ...this.resourceVToEdit,
            value: this.value.trim(),
          })
          .then(({ data }) => {
            this.$emit('addValue', data)
            this.isLoading = false
          })
          .catch((error) => {
            this.$emit('addValue', null)
            this.isLoading = false
            this.$notification.error({
              message: 'Erreur!',
              description:
                error.response && error.response.data
                  ? error.response.data.message
                  : "Une erreur inattendue s'est produite !",
            })
          })
      } else {
        backApi
          .put(`resourcevalues/${this.resourceVToEdit.id}`, {
            ...this.resourceVToEdit,
            value: this.value.trim(),
          })
          .then(({ data }) => {
            this.$emit('updateValue', {
              ...data,
              updated: true,
            })
            this.isLoading = false
          })
          .catch((error) => {
            this.$emit('updateValue', {
              updated: false,
            })
            this.isLoading = false
            this.$notification.error({
              message: 'Erreur!',
              description:
                error.response && error.response.data
                  ? error.response.data.message
                  : "Une erreur inattendue s'est produite !",
            })
          })
      }
    },
  },
}
</script>