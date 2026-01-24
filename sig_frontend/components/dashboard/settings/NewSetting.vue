<template>
  <form @submit.prevent="onSubmit">
    <div class="modal-card" style="width: 500px">
      <header class="modal-card-head">
        <h1 class="modal-card-title">
          {{ settingToEdit ? 'Edition du paramétre' : 'Nouveau paramétre' }}
        </h1>
      </header>
      <section class="modal-card-body">
        <b-field label="Code">
          <b-input
            id="setting-code"
            type="text"
            v-model="code"
            placeholder="nom du paramètre"
            :disabled="settingToEdit"
            required
            validation-message="Veuillez remplir ce champ"
          ></b-input>
        </b-field>

        <b-field label="Type">
          <b-select placeholder="Selectionner le type" expanded v-model="type">
            <option
              v-for="option in settingsType.settingsType"
              :value="option.code"
              :key="option.id"
            >
              {{ option.code }}
            </option>
          </b-select>
        </b-field>

        <b-field label="Valeur">
          <b-input
            id="setting-value"
            name="value"
            type="text"
            v-model="value"
            placeholder="valeur du paramètre"
            required
            validation-message="Veuillez remplir ce champ"
          ></b-input>
        </b-field>
        <b-field grouped class="d-flex justify-content-between">
          <b-field label="Par défault">
            <b-checkbox
              id="setting-default-value"
              v-model="default_value"
            ></b-checkbox>
          </b-field>
          <b-field label="Activé">
            <b-checkbox id="setting-enabled" v-model="enabled"></b-checkbox>
          </b-field>
        </b-field>
      </section>
      <footer class="right-align modal-card-foot">
        <button
          id="close-setting-form"
          class="button"
          type="button"
          @click="$parent.close()"
        >
          Fermer
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
import { mapState } from 'vuex'

export default {
  props: ['settingToEdit'],

  data() {
    return {
      isLoading: false,
      code: '',
      type: '',
      value: '',
      enabled: true,
      default_value: false,
    }
  },
  computed: {
    ...mapState(['settingsType']),
  },
  methods: {
    onSubmit(e) {
      this.saveSetting()
    },
    saveSetting() {
      const newSetting = {}

      this.isLoading = true

      newSetting.code = this.code
      newSetting.type = this.type
      newSetting.value = this.value
      newSetting.default_value = this.default_value
      newSetting.enabled = this.enabled

      RestApi.createOrUpdate(
        'settings',
        newSetting,
        this.settingToEdit,
        this.$store,
        this.$router,
        this.$notification
      )
        .then((data) => {
          this.$emit('saved')
          this.isLoading = false
        })
        .catch((error) => {
          this.$emit('saved')
          this.isLoading = false
        })
    },
  },
  beforeMount() {
    RestApi.getAll('settingsType', { limit: -1 }).then(
      ({ content, totalElements }) => {
        this.$store.commit('settingsType/set', {
          content,
          totalElements,
        })
      }
    )

    if (this.settingToEdit) {
      this.code = this.settingToEdit.code
      this.type = this.settingToEdit.type
      this.value = this.settingToEdit.value
      this.default_value = this.settingToEdit.default_value
      this.enabled = this.settingToEdit.enabled
    }
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
