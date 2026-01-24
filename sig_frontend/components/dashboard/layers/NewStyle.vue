<template>
  <form @submit.prevent="onSubmit">
    <div class="modal-card" style="width: 500px">
      <header class="modal-card-head">
        <h1 class="modal-card-title">Nouveau style</h1>
      </header>
      <section class="modal-card-body">
        <b-field label="Nom">
          <b-input 
            id="map-name"
            name="name"
            type="text"
            v-model="name"
            placeholder="Nom de style"
            required
            validation-message="Veuillez remplir ce champ"
          ></b-input>
        </b-field>
        <b-checkbox
          :disabled="isEdit && isDefault"
          v-model="isDefault"
          name="default-style"
          >Definir comme un style par défaut
        </b-checkbox>
      </section>
      <footer
        class="d-flex modal-card-foot"
        :style="`justify-content: ${
          this.isEdit && !styleToEdit.isDefault
            ? 'space-between !important'
            : 'flex-end'
        }`"
      >
        <button
          v-if="isEdit && !styleToEdit.isDefault"
          id="close-map-form"
          :class="['button', 'p-button-danger', { 'is-loading': isLoading2 }]"
          type="button"
          :style="`background-color: #d13438; color: #fff`"
          @click="deleteStyle"
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
  props: ['layer', 'styleToEdit', 'isEdit'],
  mixins: [mark, fill, stroke],
  data() {
    return {
      isLoading: false,
      isLoading2: false,
      isDefault: false,
      name: '',
      currentMap: this.$store.state.maps.currentMap,
    }
  },
  methods: {
    deleteStyle() {
      this.$confirm({
        title: 'Êtes-vous sûr de supprimer ce style?',
        content: 'Cette action supprimera le style de la base de données.',
        okText: 'Oui',
        okType: 'danger',
        cancelText: 'Non',
        onOk: () => {
          this.$nextTick(() => {
            this.$nuxt.$loading.start()
          })
          backApi
            .delete(`layer-styles/${this.styleToEdit.id}`)
            .then(() => {
              let l = this.layer
              let styleList = l.style.liste.filter(
                (s) => s.id !== this.styleToEdit.id
              )
              l.style.liste = styleList
              this.$store.commit('maps/updateLayer', l)
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
      if (
        !this.layer.style.liste.some(
          (s) =>
            (!this.isEdit || (this.isEdit && s.id !== this.styleToEdit.id)) &&
            s.name.split('__')[1] === this.name
        )
      ) {
        this.isLoading = true
        let payload = {
          name: this.name,
          displayName: this.name,
          isDefault: this.isDefault,
          layer: {
            id: this.layer.id,
            topo: this.layer.topo,
            slug: this.layer.slug,
            name: this.layer.name
          },
          mapId: this.currentMap.id,
        }
        if (!this.isEdit) {
          payload = {
            ...payload,
            mapLayer: this.layer.style.mapLayer,
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
          }
        }
        backApi[this.isEdit ? 'put' : 'post'](
          this.isEdit ? `layer-styles/${this.styleToEdit.id}` : 'layer-styles',
          payload
        )
          .then(({ data }) => {
            let l = this.layer
            if (
              this.isDefault ||
              (this.isEdit && this.isDefault && !this.styleToEdit.isDefault)
            ) {
              let stylesList = this.layer.style.liste.map((s) => {
                return {
                  ...s,
                  isDefault: false,
                }
              })
              l.style.id = data.id
              l.style.name = data.name
              l.style.displayName = data.displayName
              l.style.style = data.style
              l.style.liste = stylesList
              l.style.mapLayer = data.mapLayer
              this.$store.dispatch('maps/updateMapVersion')
              if (this.isEdit) {
                this.$emit('setAsDefaultStyle', {
                  layer: this.layer,
                  style: data,
                })
              }
            }
            if (this.isEdit) {
              const index = l.style.liste.findIndex((s) => s.id === data.id)
              if (index !== -1) {
                l.style.liste.splice(index, 1, data)
              }
            } else {
              l.style.liste.push(data)
            }
            this.$store.commit('maps/updateLayer', l)
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
                message: 'Erreur!',
                description:
                  error.response && error.response
                    ? error.response.data
                    : "Une erreur inattendue s'est produite !",
              })
          })
      } else {
        this.$notification.warning({
          message: 'Attention !',
          description: `Le nom de nouveau style <<${this.name}>> est existe déjà.`,
        })
      }
    },
  },
  beforeDestroy() {
    this.name = ''
    this.isDefault = false
  },
  mounted() {
    if (this.isEdit) {
      this.name = this.styleToEdit.displayName
      this.isDefault = this.styleToEdit.isDefault
    }
  },
}
</script>