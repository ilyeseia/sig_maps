<template>
  <form @submit.prevent="onSubmit">
    <div class="modal-card" style="width: 500px">
      <header class="modal-card-head">
        <h1 class="modal-card-title">Edition de la notification</h1>
      </header>
      <section class="modal-card-body">
        <b-field label="Layer">
          <b-select
            expanded
            type="text"
            name="layer"
            :value="notificationToEdit ? notificationToEdit.layer.id : ''"
          >
            <option
              v-for="layer in layers"
              :value="layer.id"
              :key="layer.id"
              :id="layer.id"
            >
              {{ layer.name }}
            </option>
          </b-select>
        </b-field>
        <b-field label="Template">
          <b-input
            id="setting-code"
            name="template"
            maxlength="200"
            type="textarea"
            :value="notificationToEdit ? notificationToEdit.template : ''"
            placeholder="setting name"
            validation-message="Veuillez remplir ce champ"
            required
          >
          </b-input>
        </b-field>
      </section>
      <footer class="right-align modal-card-foot">
        <button
          id="close-setting-form"
          class="button"
          type="button"
          @click="$parent.close()"
        >
          Close
        </button>
        <button
          id="save-setting"
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
export default {
  props: ['notificationToEdit'],
  data() {
    return {
      isLoading: false,
      layers: [],
    }
  },
  methods: {
    onSubmit(e) {
      const formData = new FormData(e.target)
      const newNotification = {}

      this.isLoading = true

      newNotification.layer = { id: formData.get('layer') }
      newNotification.template = formData.get('template')

      this.saveNotification(newNotification)
    },
    saveNotification(notification) {
      RestApi.createOrUpdate(
        'notifications',
        notification,
        this.notificationToEdit,
        this.$store,
        this.$router,
        this.$notification
      ).then((data) => {
        this.$emit('saved')
        this.isLoading = false
      })
    },
  },
  beforeMount() {
    RestApi.getAll('layers', {}).then((layers) => {
      this.layers = layers.content
    })
  },
}
</script>

<style>
.right-align {
  display: flex;
  flex-direction: row;
  justify-content: flex-end;
}
</style>
