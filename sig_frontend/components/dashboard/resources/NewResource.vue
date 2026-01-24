<template>
  <form @submit.prevent="onSubmit">
    <div class="modal-card col-xs-6 col-md-12">
      <header class="modal-card-head">
        <h1 class="modal-card-title">
          {{
            resourceToEdit ? 'Edition de referentiel' : 'Nouveau referentiel'
          }}
        </h1>
      </header>
      <section class="modal-card-body">
        <b-field label="Nom">
          <b-input
            id="rv-value"
            type="text"
            v-model="resourceName"
            placeholder="Nom de référentiel"
            required
            validation-message="Veuillez remplir ce champ"
          ></b-input>
        </b-field>
        <b-checkbox
          :disabled="resourceToEdit ? true : false"
          v-model="setAsChild"
          name="set as child"
          >Défini comme un fils
        </b-checkbox>
        <b-field v-if="setAsChild" label="Référentiel parent">
          <b-select
            expanded
            :disabled="resourceToEdit || parentResource"
            placeholder="Veuillez sélectionner le référentiel parent"
            v-model="parent"
          >
            <option v-for="r in resources.resources" :key="r.id" :value="r">
              {{ r.name }}
            </option>
          </b-select>
        </b-field>
      </section>
      <footer class="right-align modal-card-foot">
        <button
          id="close-resource-form"
          class="button"
          type="button"
          @click="$parent.close()"
        >
          Fermer
        </button>
        <button
          id="save-resource"
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
import { mapState } from 'vuex'
import RestApi from '~/methods/api.js'
import { backApi } from '~/methods/serverApi'

export default {
  props: ['parentResource', 'resourceToEdit', 'parentId'],
  data() {
    return {
      resourceName: '',
      isLoading: false,
      setAsChild: false,
      sortField: 'name',
      sortOrder: 'asc',
      parent: null,
    }
  },
  computed: {
    ...mapState(['resources']),
  },
  mounted() {
    this.loadAsyncData()
  },
  methods: {
    loadAsyncData() {
      RestApi.findAll(
        'resources',
        {
          page: 0,
          limit: -1,
          sort: this.sortField,
          dir: this.sortOrder,
        },
        'resources/set',
        this.$store,
        this.$router,
        this.$notification
      ).then(() => {
        this.resourceName = this.resourceToEdit && this.resourceToEdit.name
        if (this.parentResource) {
          this.setAsChild = true
          this.parent = this.resources.resources.find(
            (r) => r.id === this.parentResource.id
          )
        }
      })
    },
    onSubmit() {
      this.isLoading = true
      if (this.resourceToEdit) {
        backApi
          .put(`resources/${this.resourceToEdit.id.replace('r-', '')}`, {
            name: this.resourceName.trim(),
            code: this.resourceName.trim(),
          })
          .then(({ data }) => {
            this.$emit('updateResource', data)
            this.isLoading = false
          })
          .catch((error) => {
            this.$emit('updateResource', null)
            this.isLoading = false
            this.$notification.error({
              message: 'Erreur!',
              description:
                error.response && error.response.data
                  ? error.response.data
                  : "Une erreur inattendue s'est produite !",
            })
          })
      } else {
        backApi
          .post(`resources`, {
            name: this.resourceName.trim(),
            code: this.resourceName.trim(),
            parentResource: this.setAsChild ? this.parent : null,
          })
          .then(({ data }) => {
            this.$emit('addResource', { data, parentId: this.parentId })
            this.isLoading = false
          })
          .catch((error) => {
            this.$emit('addResource', null)
            this.isLoading = false
            this.$notification.error({
              message: 'Erreur!',
              description:
                error.response && error.response.data
                  ? error.response.data
                  : "Une erreur inattendue s'est produite !",
            })
          })
      }
    },
  },
}
</script>

<style scoped>
.multiline_wrapper {
  margin: 40px 15px;
}
.multiline-property .left-side {
  font-size: 14px;
  padding: 8px;
  width: 500px;
}
.multiline-property.property-panel-table div.multiline {
  padding-left: 0px;
}
.right-align {
  display: flex;
  flex-direction: row;
  justify-content: flex-end;
}
.list-items {
  min-height: 300px;
  max-height: 300px;
}
</style>
</style>
