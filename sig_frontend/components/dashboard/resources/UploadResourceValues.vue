<template>
  <form @submit.prevent="onSubmit">
    <div class="modal-card col-xs-6 col-md-12">
      <header class="modal-card-head">
        <h1 class="modal-card-title">Importation des donnée</h1>
      </header>
      <section class="modal-card-body">
        <div v-if="!hasLoaded">
          <div v-if="!isLoading" class="center-div" style="height: 265px">
            <b-upload v-model="file" drag-drop :disabled="isLoading">
              <section class="section">
                <div class="content has-text-centered">
                  <p>
                    <b-icon icon="upload" size="is-large"> </b-icon>
                  </p>
                  <p>Déposez votre fichiers ici ou cliquez pour télécharger</p>
                </div>
              </section>
            </b-upload>
          </div>
          <div v-else>
            <b-progress
              type="is-primary"
              :value="progress"
              size="is-large"
              show-value
            >
              Lecutre du ficher...{{ progress }}(%)
            </b-progress>
          </div>
        </div>
        <div v-else-if="!isSaving && rvFamilyTree.length > 0">
          <div v-for="(s, index) in rvFamilyTree" :key="s.resourceName">
            <b-field
              class="mr-3 mb-2"
              :label="`${index > 0 ? 'Resource parent ' : 'Resource'}   ${
                s.resourceName
              }`"
            >
              <b-select
                :placeholder="`${
                  index === 0
                    ? 'Veuillez selectionner une colonne'
                    : 'Veuillez selectionner une colonne de groupement'
                }`"
                required
                validation-message="Ce champ est obligatoire"
                expanded
                v-model="selectedCritirias[s.resourceName]"
              >
                <option v-for="op in columnsHeader" :key="op" :value="op">
                  {{ op }}
                </option>
              </b-select>
            </b-field>
          </div>
          <b-checkbox v-if="uploadRVDataConfig.criterias.value == null" class="mt-1" v-model="addMissingValues" name="missing-values"
            >Ajouter les valeurs manquantes
          </b-checkbox>
        </div>
        <div v-if="isSaving">
          <b-progress>Insertion de données..</b-progress>
        </div>
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
          :class="['button', 'is-primary', { 'is-loading': isSaving }]"
        >
          Enregistrer
        </button>
      </footer>
    </div>
  </form>
</template>

<script>
import { backApi } from '~/methods/serverApi'
import path from 'path-extra'
import groupBy from 'lodash/groupBy'

export default {
  props: ['uploadRVDataConfig'],
  data() {
    return {
      columnsHeader: [],
      selectOptions: { type: 'Multiple' },
      toolbar: ['Add', 'Edit', 'Delete', 'Update', 'Cancel'],
      isSaving: false,
      isLoading: false,
      hasLoaded: false,
      resourceValuesData: [],
      file: null,
      progress: 0,
      selectedCritirias: {},
      rvFamilyTree: [],
      addMissingValues: false,
    }
  },
  watch: {
    file(newVal) {
      if (newVal) {
        this.importResourceValue()
        // if(this.uploadRVDataConfig.criterias.value){
        //   this.onSubmit()
        // }
      }
    },
  },
  computed: {
    getColumnHeader() {
      return this.columnsHeader.filter(
        (c) => !Object.values(this.selectedCritirias).includes(c)
      )
    },
  },
  methods: {
    onSubmit() {
      let uploadData = this.resourceValuesData
      this.isSaving = true
      //RV by parent resource value
      if (this.uploadRVDataConfig.criterias.value) {
        if (this.columnsHeader.length > 1) {
          uploadData = this.resourceValuesData.filter((rv) => {
            return (
              rv[Object.values(this.selectedCritirias)[1]].toLowerCase() ===
              this.uploadRVDataConfig.criterias.value.toLowerCase()
            )
          })
        }
        uploadData = uploadData.map((rv) => {
          return {
            value: rv[Object.values(this.selectedCritirias)[0]].trim(),
            resourceId: this.rvFamilyTree[0].resourceId,
            parentId: this.uploadRVDataConfig.criterias.id,
          }
        })
        this.submitData(uploadData, false, null)
      } else {
        //RV by single resource value
        if (this.rvFamilyTree.length === 1) {
          uploadData = Object.keys(
            groupBy(
              this.resourceValuesData,
              Object.values(this.selectedCritirias)[0]
            )
          ).map((rv) => {
            return {
              value: rv.trim(),
              resourceId: this.rvFamilyTree[0].resourceId,
              parentId: null,
            }
          })
          this.submitData(uploadData, false, null)
        } else {
          //RV by multiple resource values
          this.handleMultipleUpload(null)
        }
      }
      //  this.submitData(uploadData)
    },
    handleMultipleUpload(submitedData, preSelResourceName) {
      let resource = this.rvFamilyTree[this.rvFamilyTree.length - 1]
      let parentId
      let uploadData = []
      for (const [rv, value] of Object.entries(
        groupBy(
          this.resourceValuesData,
          this.selectedCritirias[resource.resourceName]
        )
      )) {
        if (submitedData) {
          parentId = submitedData.find(
            (s) => s.value === value[0][preSelResourceName]
          )
          if (parentId) {
            uploadData.push({
              value: rv.trim(),
              resourceId: resource.resourceId,
              parentId: parentId.id,
            })
          }
        } else {
          uploadData.push({
            value: rv.trim(),
            resourceId: resource.resourceId,
            parentId: null,
          })
        }
      }
      let previousSelectedName = this.selectedCritirias[resource.resourceName]
      this.rvFamilyTree = this.rvFamilyTree.slice(
        0,
        this.rvFamilyTree.length - 1
      )
      this.submitData(
        uploadData,
        this.rvFamilyTree.length > 0 ? true : false,
        previousSelectedName
      )
    },
    submitData(uploadData, next, previousSelectedName) {
      backApi
        .post(
          `resourcevalues/importfile?with-missing-values=${
            this.uploadRVDataConfig.criterias.value ? true : this.rvFamilyTree.length === 0 ? true : this.addMissingValues
          }`,
          uploadData
        )
        .then(({ data }) => {
          if (next &&  (data.skippedValues.length > 0 || data.addedValues.length > 0)) {
              if (this.rvFamilyTree.length > 0) {
                this.handleMultipleUpload(
                  [...data.skippedValues, ...data.addedValues],
                  previousSelectedName
                )
              } else {
                this.$emit('addValue', data.addedValues)
                this.isSaving = false
                this.$parent.close()
              }
          } else {
            this.$emit('addValue', data.addedValues)
            this.isSaving = false
            this.$parent.close()
          }
        })
        .catch((error) => {
          console.log(error)
          this.$emit('addValue', null)
          this.isSaving = false
          this.$parent.close()
          this.$notification.error({
            message: 'Erreur!',
            description:
              error.response && error.response.data
                ? error.response.data.message
                : "Une erreur inattendue s'est produite !",
          })
        })
    },
    importResourceValue() {
      const filename = path.base(this.file.name)

      const formData = new FormData()
      formData.append('file', this.file)

      this.isLoading = true
      this.columnsHeader = []
      let total = 1
      const option = {
        onUploadProgress: (progressEvent) => {
          var percentCompleted =
            Math.round(progressEvent.loaded * 100) / progressEvent.total
          this.progress = percentCompleted.toFixed(2)
          total = progressEvent.total
        },
      }

      backApi
        .post(`resourcevalues/import`, formData, option)
        .then(({ data }) => {
          data[0].forEach((column) => {
            if (column !== 'geom') this.columnsHeader.push(column)
            this.hasLoaded = true
            this.showColumnsHeader = true
          })
          this.progress = 100
          setTimeout(() => {
            this.isLoading = false
          }, 200)

          let obj = {}
          data.slice(1, data.length).forEach((r) => {
            this.columnsHeader.forEach((column, index) => {
              obj[column] = r[index]
            })
            this.resourceValuesData.push(obj)
            obj = {}
          })
          if (this.columnsHeader.length === 1) {
            this.rvFamilyTree = this.rvFamilyTree.slice(0, 1)
          }
        })
        .catch((error) => {
          this.$notification.error({
            message: 'Error!',
            description: error.message,
          })
          this.isLoading = false
        })
    },
    appendChild(resource) {
      this.rvFamilyTree.push({
        resourceId: resource.id ? resource.id.replace('r-', '') : null,
        resourceName: resource.name,
        keyExtractor: '',
      })
      if (resource.parentResource) {
        this.appendChild(resource.parentResource)
      }
    },
  },
  beforeMount() {
    this.appendChild(
      this.uploadRVDataConfig.criterias.value
        ? this.uploadRVDataConfig.criterias.parentResource
        : this.uploadRVDataConfig.criterias
    )
  },
}
</script>

<style lang="scss" scoped>
</style>