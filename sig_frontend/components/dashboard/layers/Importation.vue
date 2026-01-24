<template>
  <form @submit.prevent="onSubmit" class="import-layer">
    <div class="modal-card" style="width: 1000px; height: 65vh">
      <header class="modal-card-head">
        <h1 class="modal-card-title">Importation des données</h1>
      </header>
      <section class="modal-card-body" style="overflow: hidden auto">
        <div v-if="!isLoading">
          <div>
            <b-field label="Type de donnée" label-position="on-border">
              <b-select
                placeholder="Select a resource"
                v-model="selectedFileType"
                expanded
                name="select-importation"
              >
                <option
                  v-for="file in fileTypes"
                  :value="file.code"
                  :key="file.code"
                  :id="file.code"
                >
                  {{ file.label }}
                </option>
              </b-select>
            </b-field>
            <b-field>
              <b-message type="is-warning" ref="element">
                <b-loading
                  :is-full-page="true"
                  v-model="isLoading"
                  :can-cancel="true"
                ></b-loading>
                {{ fileTypes[selectedFileType].message }}
              </b-message>
            </b-field>
            <b-field>
              <b-message ref="element">
                <h6 style="text-align: center; color: #ff0000">
                  Veuillez sélectionner un ficher
                  {{ fileTypes[selectedFileType].extension }}
                </h6>
              </b-message>
            </b-field>
          </div>
          <div class="center-div" style="height: 265px">
            <b-upload
              v-model="dropFiles"
              multiple
              drag-drop
              :disabled="isLoading"
            >
              <section class="section">
                <div class="content has-text-centered">
                  <p>
                    <b-icon icon="upload" size="is-large"> </b-icon>
                  </p>
                  <p>Déposez vos fichiers ici ou cliquez pour télécharger</p>
                </div>
              </section>
            </b-upload>
          </div>
          <div class="tags center-div">
            <span
              v-for="(file, index) in dropFiles"
              :key="index"
              class="tag is-primary"
            >
              {{ file.name }}
              <button
                class="delete is-small"
                type="button"
                @click="deleteDropFile(index)"
              ></button>
            </span>
          </div>
        </div>
        <div v-else>
          <b-progress
            v-if="progress == 25.00"
            size="is-large"
          >
            {{ getText }}
          </b-progress>
          <b-progress
            v-else
            :type="getType"
            :value="progress"
            size="is-large"
            show-value
          >
            {{ getText }}
          </b-progress>
        </div>
      </section>
      <footer class="right-align modal-card-foot">
        <button
          id="close-layer-form"
          class="button"
          type="button"
          @click="$parent.close()"
        >
          Fermer
        </button>
        <button
          id="import-layer"
          type="submit"
          :class="['button', 'is-primary', { 'is-loading': isLoading }]"
        >
          Importer
        </button>
      </footer>
    </div>
  </form>
</template>
<script>
import { backApi } from '~/methods/serverApi'
import path from 'path-extra'
import { createCoverageStore } from './coverage'

import stroke from '~/mixins/style/stroke'
import fill from '~/mixins/style/fill'
import mark from '~/mixins/style/mark'

export default {
  mixins: [stroke, fill, mark],
  data() {
    return {
      isLoading: false,
      dropFiles: [],
      selectedFileType: 'shp',
      progress: 0,
      fileTypes: {
        shp: {
          label: 'Shapefile',
          code: 'shp',
          extension: '.zip',
          message:
            'Le fichier shape ainsi que tous les fichiers necessaires doivent etre compresser dans un fichier zip',
        },
        csv: {
          label: 'Spreadsheet (CSV)',
          code: 'csv',
          extension: '.csv',
          message:
            'le fichier csv doit contenir une column de geometry au format WKT',
        },
        geojson: {
          label: 'GeoJSON',
          code: 'geojson',
          extension: 'JSON',
          message:
            'GeoJSON: les coordonnées des données doivent être Lat/Lon (WGS84).',
        },
        kml: {
          label: 'KML',
          code: 'kml',
          extension: '.kml',
          message: 'les coordonnées des données doivent être Lat/Lon (WGS84)',
        },
        geotiff: {
          label: 'Raster (Geo TIFF)',
          extension: '.zip',
          code: 'geotiff',
          message:
            'Le fichier tiff ainsi que tous les fichiers nécessaires doivent etre compresser dans un fichier zip',
        },
        imagemosaic: {
          label: 'Raster (Mosaic)',
          extension: '.zip',
          code: 'imagemosaic',
          message:
            'tous les fichiers nécessaires doivent etre compresser dans un fichier zip',
        },
        worldimage: {
          label: 'WorldImage(GIF,PNG,JPEG,TIFF)',
          extension: '.zip',
          code: 'worldimage',
          message:
            "Un fichier raster accompagné d'un fichier de données spatiales le tous compresser dans un zip",
        },
      },
    }
  },
  computed: {
    getType() {
      return this.progress <= 25
        ? 'is-warning'
        : this.progress === 100
        ? 'is-primary'
        : null
    },
    getText() {
      return this.progress <= 25
        ? `Importation du ficher (${this.progress.toFixed(2) * 4}%)`
        : this.progress === 100
        ? 'Terminé'
        : 'Insertion de données..'
    },
  },
  methods: {
    async onSubmit(e) {
      this.uploadLayers()
    },
    removeCrsFromGeoJSON(file) {
      return new Promise(function (resolve, reject) {
        let reader = new FileReader()

        reader.onload = (event) => {
          let featureCollection = JSON.parse(event.target.result)

          delete featureCollection['crs']

          let blob = new Blob([JSON.stringify(featureCollection)], {
            type: 'application/json',
          })
          let file = new File([blob], 'file.json')

          resolve(file)
        }

        reader.readAsText(file)
      })
    },
    importRasterLayer(formData, filename) {
      createCoverageStore(formData, filename, this.selectedFileType).then(
        (coverageStore) => {
          let layer = {
            name: filename,
            layerType: 'RASTER',
          }
          backApi
            .post('layers')
            .then((data) => {
              this.$store.commit('profile/setLayers', data)
              this.$emit('imported')
              this.isLoading = false
              this.$store.commit('layers/add', data)
              this.$notification.success({
                message: `Felicitation ! le ficher ${filename} est importé avec succes`,
              })
            })
            .catch((error) => {
              this.$notification.error({
                message: 'Error!',
                description: error.message,
              })
              this.isLoading = false
            })
        }
      )
    },
    showNotificationError(error) {
      this.$emit('imported')
      this.$notification.error({
        message: 'Erreur!',
        description:
          error.response && error.response.data
            ? error.response.data.message
            : "Une erreur inattendue s'est produite !",
      })
      this.isLoading = false
    },
    importVectorLayer(formData, filename, extension) {
      let folderName =
        'tmp.' + new Date().toLocaleDateString().replaceAll('/', '-')
      const option1 = {
        onUploadProgress: (progressEvent) => {
          var percentCompleted =
            Math.round((progressEvent.loaded * 100) / progressEvent.total) / 4
          this.progress = percentCompleted
        },
      }
      backApi
        .post(`upload/${folderName}`, formData, option1)
        .then(({ data }) => {
          backApi
            .post(
              `entityelements/import/${folderName}.${data.replace(
                '.',
                '__'
              )}/${filename.replaceAll('(', '').replaceAll(')', '')}/${extension}`
            )
            .then(({ data }) => {
              this.progress = 100
              setTimeout(() => {
                this.$store.commit('profile/setLayers', data)
                this.$store.commit('layers/add', data)
                this.$emit('imported')
                this.isLoading = false
                this.$notification.success({
                  message: `Felicitation ! la couche ${filename} est importé avec succes`,
                })
              }, 1000)
            })
            .catch((error) => {
              this.showNotificationError(error)
            })
        })
        .catch(() => {
          this.showNotificationError(error)
        })
    },
    uploadLayers() {
      this.dropFiles.forEach(async (myFile) => {
        this.isLoading = true
        const filename = path.base(myFile.name)

        switch (this.selectedFileType) {
          case 'geotiff':
          case 'imagemosaic':
          case 'worldimage': {
            this.importRasterLayer(myFile, filename)
            break
          }

          case 'geojson':
            this.removeCrsFromGeoJSON(myFile).then((file) => {
              const formData = new FormData()
              formData.append('uploadfile', file)
              this.importVectorLayer(formData, filename, this.selectedFileType)
            })
            break
          case 'csv':
          case 'shp':
          case 'kml': {
            const formData = new FormData()
            formData.append('uploadfile', myFile)
            this.importVectorLayer(formData, filename, this.selectedFileType)
            break
          }
        }
      })
    },
    deleteDropFile(index) {
      this.dropFiles.splice(index, 1)
    },
  },
}
</script>
<style>
.right-align {
  display: flex;
  flex-direction: row;
  justify-content: flex-end;
}
.center-div {
  display: flex;
  justify-content: center;
  align-items: center;
}

.header {
  padding: 10px;
  text-align: center;
  background: #1abc9c;
  color: white;
  font-size: 16px;
}
.import-layer .modal-card-body {
  position: relative;
}
.import-layer .modal-card-body .progress-wrapper {
  position: absolute;
  width: 80%;
  top: 50%;
  left: 10%;
  transition: all 0.3s ease-in;
}
</style>
