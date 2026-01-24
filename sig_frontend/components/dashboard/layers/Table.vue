<template>
  <section class="p-5 w-100">
    <div class="d-flex justify-content-between row-column">
      <div>
        <button
          v-if="
            profile.roles.includes('ROLE_ADMIN') ||
            profile.roles.includes('LAYER_CREATE_AUTHORITY')
          "
          id="add-new-layer"
          class="button is-primary mb-2"
          @click="$emit('newLayer')"
        >
          <b-icon class="mr-1" pack="fas" icon="plus"></b-icon>Nouveau
        </button>
        <button
          v-if="
            profile.roles.includes('ROLE_ADMIN') ||
            profile.roles.includes('LAYER_CREATE_AUTHORITY')
          "
          id="upload-new-layer"
          type="is-info"
          class="button is-primary mb-2"
          @click="$emit('uploadLayer')"
        >
          <b-icon class="mr-1" pack="fas" icon="database"></b-icon>Importer
        </button>
        <b-dropdown
          aria-role="list"
          v-if="
            profile.roles.includes('ROLE_ADMIN') ||
            profile.roles.includes('ENTITY_ELEMENT_MULTI_EXPORT_AUTHORITY')
          "
        >
          <button
            :disabled="checkedRows.length === 0"
            :class="['button', 'is-primary', { 'is-loading': isLoading }]"
            slot="trigger"
          >
            <b-icon icon="file-export"></b-icon>
            <span>Exporter</span>
            <b-icon icon="menu-down"></b-icon>
          </button>
          <b-dropdown-item><b>Vector</b></b-dropdown-item>
          <b-dropdown-item @click="exportLayers('geojson', 'geojson')"
            >Exporter vers GeoJson</b-dropdown-item
          >
          <b-dropdown-item @click="exportLayers('shp', 'zip')"
            >Exporter vers Shape</b-dropdown-item
          >
          <b-dropdown-item @click="exportLayers('csv', 'csv')"
            >Exporter vers CSV</b-dropdown-item
          >
          <b-dropdown-item @click="exportLayers('kml', 'kml')"
            >Exporter vers KML</b-dropdown-item
          >
          <b-dropdown-item @click="exportLayers('gml2', 'gml')"
            >Exporter vers GML2</b-dropdown-item
          >
          <b-dropdown-item @click="exportLayers('gml3', 'gml')"
            >Exporter vers GML3</b-dropdown-item
          >
          <b-dropdown-item @click="exportLayers('excel', 'xlsx')"
            >Exporter vers Excel</b-dropdown-item
          >
          <b-dropdown-item><b>Raster</b></b-dropdown-item>
          <b-dropdown-item @click="exportWmsLayer('image/png', 'png', true)"
            >Exporter vers PNG</b-dropdown-item
          >
          <!-- <b-dropdown-item @click="exportWmsLayer('image/gif','gif',true)">Exporter vers GIF</b-dropdown-item> -->
          <!-- <b-dropdown-item @click="exportWmsLayer('image/tiff','tif')">Exporter vers TIFF</b-dropdown-item> -->
          <!--<b-dropdown-item @click="exportWmsLayer('image/geotiff','tif')">Exporter vers GEOTIFF</b-dropdown-item> -->
          <b-dropdown-item @click="exportWmsLayer('image/jpeg', 'jpeg', true)"
            >Exporter vers JPEG</b-dropdown-item
          >
          <b-dropdown-item @click="exportWmsLayer('image/svg', 'svg')"
            >Exporter vers SVG</b-dropdown-item
          >
          <b-dropdown-item @click="exportWmsLayer('kml', 'kml')"
            >Exporter vers KML</b-dropdown-item
          >
          <!--<b-dropdown-item @click="exportWmsLayer('kmz','kmz')">Exporter vers KMZ</b-dropdown-item> -->
          <!--<b-dropdown-item @click="exportWmsLayer('application/pdf','pdf')">Exporter vers PDF</b-dropdown-item>-->
        </b-dropdown>

        <!--   <button  :class="['button', 'is-danger' ,{'is-loading':isDeleteLoading}]" v-if="profile.roles.includes('ROLE_ADMIN') || profile.roles.includes('LAYER_DELETE_AUTHORITY')" id="delete-layer"  @click="deleteLayers">
            <b-icon class="mr-1" pack="fas" icon="times"/>Supprimer
  </button> -->
      </div>
      <b-input
        v-model="searchName"
        ref="searchNameRef"
        placeholder="Rechercher ici.."
        type="search"
        icon="magnify"
      />
    </div>
    <Panel>
      <template #title> couches </template>
      <template #default>
        <b-table
          :data="tableData"
          paginated
          backend-pagination
          :per-page="perPage"
          :total="totalRowsCount"
          @page-change="onPageChange"
          :checked-rows.sync="checkedRows"
          :show-detail-icon="true"
          backend-sorting
          :default-sort-direction="defaultSortOrder"
          :default-sort="[sortField, sortOrder]"
          @sort="onSort"
          @details-open="showFields"
          checkable
          detailed
        >
          <template slot-scope="props">
            <b-table-column field="name" label="Nom" sortable>{{
              props.row.name
            }}</b-table-column>
            <b-table-column field="type" label="Type" sortable>{{
              props.row.type
            }}</b-table-column>
            <b-table-column field="topo" label="Topologie" sortable>{{
              props.row.topo
            }}</b-table-column>
            <b-table-column
              field="createdBy"
              label="Propriétaire"
              sortable
              centered
              >{{ props.row.createdBy }}</b-table-column
            >
            <b-table-column
              field="createDate"
              label="Date de création"
              sortable
              centered
            >
              <span class="tag is-success">
                {{ props.row.createDate | moment('DD/MM/YYYY') }}
              </span>
            </b-table-column>

            <b-table-column
              class="data-table__actions"
              field="edit"
              label="Actions"
              centered
            >
              <!--           <button id="tag-layer" class="button is-primary is-small" @click="editTags(props.row)">
            <b-icon class="mr-1 p-1" pack="fas" icon="tag"/>Tag
          </button> -->

              <!-- <button
            id="share-layers"
            class="button is-light is-small"
            v-if="
              profile.roles.includes('ROLE_ADMIN') ||
              profile.roles.includes('LAYER_SHARE_AUTHORITY')
            "
            @click="$emit('shareLayers', props.row.id)"
          >
            <b-icon class="mr-1" pack="fas" icon="share"></b-icon>
          </button> -->
              <b-tooltip label="Partager" type="is-dark" position="is-bottom">
                <Button
                  v-if="
                    profile.roles.includes('ROLE_ADMIN') ||
                    profile.roles.includes('LAYER_SHARE_AUTHORITY')
                  "
                  icon="pi pi-share-alt"
                  class="p-button-rounded p-button-secondary p-mr-2"
                  @click="$emit('shareLayers', props.row.id)"
                />
              </b-tooltip>
              <b-tooltip label="Aller à" type="is-dark" position="is-bottom">
                <Button
                  v-if="
                    profile.roles.includes('ROLE_ADMIN') ||
                    profile.roles.includes('LAYER_READ_AUTHORITY')
                  "
                  icon="pi pi-map-marker"
                  class="p-button-rounded p-button-info p-mr-2"
                  @click="viewLayer(props.row)"
                />
              </b-tooltip>
              <b-tooltip label="Modifier" type="is-dark" position="is-bottom">
                <Button
                  v-if="
                    profile.roles.includes('ROLE_ADMIN') ||
                    profile.roles.includes('LAYER_UPDATE_AUTHORITY')
                  "
                  icon="pi pi-pencil"
                  class="p-button-rounded p-button-success p-mr-2"
                  @click="$emit('editLayer', props.row.id)"
                />
              </b-tooltip>
              <b-tooltip label="Cloner" type="is-dark" position="is-bottom">
                <Button
                  v-if="
                    profile.roles.includes('ROLE_ADMIN') ||
                    profile.roles.includes('LAYER_CLONE_AUTHORITY')
                  "
                  icon="pi pi-copy"
                  class="p-button-rounded p-button-warning p-mr-2"
                  @click="$emit('cloneLayer', props.row)"
                />
              </b-tooltip>
              <b-tooltip label="Supprimer" type="is-dark" position="is-bottom">
                <Button
                  v-if="
                    profile.roles.includes('ROLE_ADMIN') ||
                    profile.roles.includes('LAYER_DELETE_AUTHORITY')
                  "
                  icon="pi pi-trash"
                  class="p-button-rounded p-button-danger"
                  @click="deleteLayer(props.row)"
                />
              </b-tooltip>
            </b-table-column>
          </template>

          <template slot="detail" slot-scope="props">
            <div v-for="field in props.row.fields" :key="field.id">
              <table class="table table-hover">
                <tr class="d-flex w-100 justify-content-around">
                  <td style="flex: 1">
                    <b>{{ field.name }}</b>
                  </td>
                  <td style="flex: 1">{{ field.type }}</td>
                </tr>
              </table>
            </div>
          </template>
        </b-table>
      </template>
    </Panel>
  </section>
</template>

<script>
import { mapState } from 'vuex'
import Panel from '../../layout/Panel'
import {
  SecuredPublicWMSURL,
  GeoServerWFSURL,
  GeoServerUser,
  GeoServerPassword,
} from '../../../constants'
import RestApi from '../../../methods/api.js'
import TagSelect from '../tags/TagSelect'
import { getLayerBBOX } from './featuretype'
import { saveAs } from 'file-saver'
import Button from 'primevue/button'
import { backApi as axios } from '../../../methods/serverApi'
export default {
  components: { TagSelect, Button, Panel },
  layout: 'dashboard',
  data() {
    return {
      isFullPage: true,
      searchName: "",
      myFile: null,
      sortField: 'name',
      sortOrder: 'asc',
      defaultSortOrder: 'asc',
      perPage: 10,
      page: 1,
      checkedRows: [],
      isLoading: false,
      ENTITY_ELEMENT_MULTIEXPORT_AUTHORITY: false,
    }
  },
  watch: {
    searchName(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.searchNameRef.value) {
          this.page = 1
          this.filterLayers()
        }
      }, 500)
    },
  },
  methods: {
    viewLayer(layer) {
      axios.get(`layers/withFields/${layer.id}`).then(({ data }) => {
        let layers = []
        layers.push(data)
        let map = { name: layer.name, privacy: 'PRIVATE', layers }
        this.$store.commit('maps/setActiveLayer', data)
        this.$store.commit('maps/setCurrentMap', { map, mode: 'layer' })
        this.$router.push('/dashboard/viewer')
      })
    },
    showFields(row) {
      axios.get(`layers/withFields/${row.id}`).then(({ data }) => {
        row['fields'] = data.fields
      })
    },
    loadAsyncData() {
      RestApi.findAll(
        'layers',
        {
          page: this.page - 1,
          limit: this.perPage,
          sort: this.sortField,
          dir: this.sortOrder,
        },
        'layers/set',
        this.$store,
        this.$router,
        this.$notification
      )
    },
    onSort(field, order) {
      this.sortField = field
      this.sortOrder = order
      this.loadAsyncData()
    },
    onPageChange(page) {
      this.page = page
      this.filterLayers()
    },
    editTags(layer) {
      this.$buefy.modal.open({
        parent: this,
        component: TagSelect,
        hasModalCard: true,
        props: { elementToEdit: layer },
      })
    },
    filterLayers() {
      let payload = {
        condition: 'and',
        rules: [
          {
            label: 'name',
            field: 'name',
            operator: 'ilike',
            type: 'string',
            value: this.searchName,
          },
        ],
      }
      RestApi.findAllByCriteria(
        'layers',
        payload,
        {
          page: this.page - 1,
          limit: this.perPage,
          sort: this.sortField,
          dir: this.sortOrder,
        },
        this.$store,
        this.$router,
        this.$notification
      )
    },
    async deleteLayers() {
      this.$nextTick(() => {
        this.$nuxt.$loading.start()
        setTimeout(() => this.$nuxt.$loading.finish(), 900)
      })
      const response = await Promise.all(
        this.checkedRows.map(async (layer) =>
          RestApi.delete(
            'layers',
            layer.id,
            this.$store,
            this.$router,
            this.$notification
          )
        )
      )
      this.loadAsyncData()
    },
    exportWmsLayer(format, fileExtension, canavas = false) {
      this.checkedRows.map(({ slug }) => {
        getLayerBBOX(slug).then((bbox) => {
          if (canavas) {
            var image = new Image()
            image.crossOrigin = 'anonymous'
            image.src = `${SecuredPublicWMSURL}?request=GetMap&service=WMS&version=1.1.1&layers=limite_admin:${slug}&bbox=${bbox}&srs=EPSG:4326&width=768&height=679&styles=limite_admin:${slug}&format=${format}`
            // get file name - you might need to modify this if your image url doesn't contain a file extension otherwise you can set the file name manually
            var fileName = image.src.split(/(\\|\/)/g).pop()
            image.onload = function () {
              var canvas = document.createElement('canvas')
              canvas.width = this.naturalWidth // or 'width' if you want a special/scaled size
              canvas.height = this.naturalHeight // or 'height' if you want a special/scaled size
              canvas.getContext('2d').drawImage(this, 0, 0)
              var blob = canvas.toDataURL(format)

              var link = document.createElement('a')
              document.body.appendChild(link) // for Firefox
              link.setAttribute('href', blob)

              link.setAttribute('download', `${slug}.${fileExtension}`)
              link.click()
              link.remove()
              this.$notification.success({
                message: 'Success!',
                description: `La couche nommé ${slug} est exporté avec succès`,
              })
            }
          } else {
            axios({
              method: 'get',
              withCredentials: true,
              // params : {list : 'available_with_geom'},
              headers: {
                'Content-Type': 'application/xml',
                Accept: 'application/json',
              },
              url: `${SecuredPublicWMSURL}?request=GetMap&service=WMS&version=1.1.1&layers=limite_admin:${slug}&bbox=${bbox}&srs=EPSG:4326&width=768&height=679&styles=limite_admin:${slug}&format=${format}`,
              auth: {
                username: `${GeoServerUser}`,
                password: `${GeoServerPassword}`,
              },
            }).then((response) => {
              saveAs(
                new Blob([response.data], { type: format }),
                `${slug}.${fileExtension}`
              )
              this.$notification.success({
                message: 'Success!',
                description: `La couche nommé ${slug} est exporté avec succès`,
              })
            })
          }
        })
      })
    },
    exportWfsLayers(fileType) {
      this.checkedRows.map(({ slug }) => {
        axios({
          method: 'get',
          withCredentials: true,
          // params : {list : 'available_with_geom'},
          headers: {
            'Content-Type': 'application/xml',
            Accept: 'application/json',
          },
          url: `${GeoServerWFSURL}?service=wfs&version=1.0.0&request=GetFeature&typeNames=limite_admin:${slug}&outputFormat=application%2Fvnd.google-earth.kml%2Bxml`,
          auth: {
            username: `${GeoServerUser}`,
            password: `${GeoServerPassword}`,
          },
        }).then((response) => {
          const url = window.URL.createObjectURL(new Blob([response.data]))
          const link = document.createElement('a')
          link.href = url

          if (fileType === 'shp') fileType = 'zip'
          if (fileType === 'excel') fileType = 'xlsx'

          link.setAttribute('download', `${slug}.${fileType}`) //or any other extension
          document.body.appendChild(link)
          link.click()
          link.remove()
          this.$notification.success({
            message: 'Success!',
            description: `Exported layer ${slug}`,
          })
        })
      })
    },
    deleteLayer(layer) {
      this.$confirm({
        title: 'Êtes-vous sûr de vouloir supprimer cette couche ?',
        content: 'Cette action supprimera la couche de la base de données.',
        okText: 'Oui',
        okType: 'danger',
        cancelText: 'Non',
        onOk: () => {
          this.$nextTick(() => {
            this.$nuxt.$loading.start()
          })
          RestApi.delete(
            'layers',
            layer.id,
            this.$store,
            this.$router,
            this.$notification
          )
            .then(() => {
              this.$store.commit('profile/deleteLayer', layer.id)
              this.loadAsyncData()
              this.$nuxt.$loading.finish()
              this.searchName = ''
            })
            .catch((error) => {
              this.$notification.error({
                message: 'Erreur !',
                description:
                  error.response && error.response.data
                    ? error.response.data.message
                    : "Une erreur inattendue s'est produite",
              })
              this.$nuxt.$loading.finish()
            })
        },
        onCancel: () => {},
      })
    },
    async exportLayers(fileType, extension) {
      if (this.checkedRows.length > 0) {
        this.isLoading = true
        const requests = this.checkedRows.map(({ slug }) =>
          RestApi.exportData(slug, fileType, extension, {}, this.$notification)
            .then(() => {
              this.isLoading = false
              setTimeout(() => {
                this.checkedRows = []
              }, 500)
            })
            .catch(() => {
              this.$notification.error({
                message: 'Erreur!',
                description: `Une erreur inattendue s'est produite  !`,
              })
              this.isLoading = false
            })
        )
      }
    },
  },
  computed: {
    ...mapState('maps', ['activeLayer']),
    ...mapState(['layers', 'profile']),
    totalRowsCount() {
      return this.layers.totalRowsCount
    },
    tableData() {
      return this.layers.layers.map(
        ({
          id,
          name,
          slug,
          topo,
          type,
          createDate,
          createdBy,
          tags,
          typeLimit,
          identifiant,
          fields,
          iconUrl,
          notification,
          users,
          groups,
        }) => ({
          id,
          notification,
          name: name && name.includes('____') ? name.split('____')[1] : name,
          slug,
          topo,
          type,
          typeLimit,
          createDate,
          createdBy,
          tags,
          identifiant,
          fields,
          iconUrl,
          selected: false,
          users,
          groups,
        })
      )
    },
  },
  beforeMount() {
    this.loadAsyncData()
  },
}
</script>