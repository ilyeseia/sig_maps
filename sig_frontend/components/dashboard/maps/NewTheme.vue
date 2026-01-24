<template>
  <form @submit.prevent="onSubmit">
    <div class="modal-card" style="width: 500px">
      <header class="modal-card-head">
        <h1 class="modal-card-title">Nouveau theme</h1>
      </header>
      <section class="modal-card-body">
        <b-field label="Nom">
          <b-input
            id="map-name"
            name="name"
            type="text"
            v-model="name"
            placeholder="Nom de theme"
            required
            validation-message="Veuillez remplir ce champ"
          ></b-input>
        </b-field>
        <b-field>
          <b-checkbox
            :disabled="isEdit && isDefault"
            v-model="isDefault"
            name="default-style"
            >Definir comme un theme par défaut
          </b-checkbox>
        </b-field>
        <div v-if="!isEdit">
          <h2 class="mb-2">
          <strong><i>Paramètres :</i></strong>
        </h2>
        <b-field>
          <b-radio v-model="snapshotType" native-value="layersAndStyles">
            Garder les couches avec leurs styles.
          </b-radio>
        </b-field>
        <b-field>
          <b-radio v-model="snapshotType" native-value="layers">
            Garder les couches sans leurs styles.
          </b-radio>
        </b-field>
        <b-field>
          <b-radio v-model="snapshotType" native-value="empty">
            Theme vide.
          </b-radio>
        </b-field>
        </div>
      </section>
      <footer
        class="d-flex modal-card-foot"
        :style="`justify-content: ${
          this.isEdit && !theme.isDefault
            ? 'space-between !important'
            : 'flex-end'
        }`"
      >
        <button
          v-if="isEdit && !theme.isDefault"
          id="close-map-form"
          :class="['button', 'p-button-danger', { 'is-loading': isLoading2 }]"
          type="button"
          :style="`background-color: #d13438; color: #fff`"
          @click="deleteTheme"
        >
          Supprimer
        </button>
        <div>
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
        </div>
      </footer>
    </div>
  </form>
</template>
<script>
import { backApi } from '../../../methods/serverApi'

import mark from '~/mixins/style/mark'
import fill from '~/mixins/style/fill'
import stroke from '~/mixins/style/stroke'
export default {
  props: ['theme', 'currentMapId', 'isEdit'],
  mixins: [mark, fill, stroke],
  data() {
    return {
      isLoading: false,
      isLoading2: false,
      name: '',
      isDefault: false,
      isSnapshot: false,
      snapshotType: 'layersAndStyles',
    }
  },
  methods: {
    deleteTheme() {
      this.$confirm({
        title: 'Êtes-vous sûr de supprimer ce theme?',
        content: 'Cette action supprimera le theme de la base de données.',
        okText: 'Oui',
        okType: 'danger',
        cancelText: 'Non',
        onOk: () => {
          this.$nextTick(() => {
            this.$nuxt.$loading.start()
          })
          backApi
            .delete(`themes/${this.theme.id}/maps/${this.currentMapId}`)
            .then(() => {
              this.$store.dispatch('maps/deleteMapThemes', this.theme.id)
              this.$emit('close')
              this.$notification.success({
                message: 'La Suppression est réussie avec succés',
              })
            })
            .catch((error) => {
              this.$emit('close')
              this.$notification.error({
                message: 'Erreur !',
                description:
                  error.response && error.response.data
                    ? error.response.data.message
                    : "Une erreur inattendue s'est produite",
              })
            })
        },
        onCancel: () => {},
      })
    },
    onSubmit() {
      this.isLoading = true
      let payload = {
        name: this.name,
        isDefault: this.isDefault,
        snapshotType: this.snapshotType,
        map: {
          id: this.currentMapId,
        },
      }
      if (this.snapshotType === 'layers') {
        payload.layerStyle = {
          isDefault: true,
          styleConfig: {
            symbologyType: 'Simple',
            rules: [
              {
                fill: this.fill,
                mark: this.mark,
                stroke: this.stroke,
              },
            ],
          },
          mapId: this.currentMapId,
        }
      }
      backApi[this.isEdit ? 'put' : 'post'](
        this.isEdit ? `themes/${this.theme.id}` : 'themes',
        payload
      )
        .then(({ data }) => {
          if (this.isEdit) {
            this.$store.dispatch('maps/updateMapThemes', data)
          } else {
            this.$store.dispatch('maps/addMapThemes', data)
          }
          if (
            (this.isEdit && !this.theme.isDefault && data.isDefault) ||
            (!this.isEdit && this.isDefault)
          ) {
            this.$emit('setDefaultMapTheme', data.id)
          }
          if (this.isSnapshot) {
          }
          this.$emit('close')
          this.isLoading = false
          this.$notification.success({
            message: 'Félicitation !',
            description: 'La sauvegarde est réussie avec succés',
          })
        })
        .catch((error) => {
          this.$emit('close')
          this.$notification.error({
            message: 'Error !',
            description:
              error.response && error.response
                ? error.response.data
                : "Une erreur inattendue s'est produite !",
          })
        })
    },
  },
  beforeDestroy() {
    this.name = ''
    this.isDefault = false
  },
  mounted() {
    if (this.isEdit) {
      this.name = this.theme.name
      this.isDefault = this.theme.isDefault
    }
  },
}
</script>