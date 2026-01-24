<template>
  <section class="p-5 w-100 repo">
    <Panel icon="fa fa-chart-bar" viewMode="normal">
      <template #title> Référentiels </template>
      <template #default>
        <div class="repo__wrapper">
          <div class="repo__header">
            <div class="d-flex">
              <i
                @click="goToResource(0)"
                style="cursor: pointer"
                class="fa fa-home"
              ></i>
              <div v-for="(r, index) in getPath" :key="r.id">
                <span
                  @click="goToResource(index === 0 ? 1 : index)"
                  class="route"
                  >{{ r.type === 'RV' ? r.value : r.name }}</span
                >
                <span v-if="index > 0 && index < getPath.length - 1"> > </span>
              </div>
            </div>
          </div>
          <div class="repo__virtual-header"></div>
          <div class="repo__body">
            <div :style="{ transform: getTranslate() }" class="repo__panel">
              <div
                v-for="(r, index) in resources"
                :key="r.id"
                id="repo__item"
                :class="`${
                  index > 0 && index === resources.length - 1
                    ? 'repo__item repo__item--extended'
                    : 'repo__item'
                }`"
              >
                <template v-if="r.type === 'R'">
                  <div class="repo__item-header">
                    <div
                      class="d-flex justify-content-between align-items-center"
                    >
                      <h2 v-if="!resourceValues[r.id].api.applyFilter">
                        <i class="fa fa-database"></i>
                        {{ r.name }} (Liste)
                      </h2>
                      <div v-else>
                        <b-input
                          :placeholder="`Rechercher par ${r.name}..`"
                          type="search"
                          icon="magnify"
                          icon-clickable
                          v-model="quering[r.id]"
                          @icon-click="performApi(null, r.id)"
                          @keyup.enter.native="performApi(null, r.id)"
                          :id="r.id"
                        ></b-input>
                      </div>
                      <div>
                        <b-tooltip
                          label="Filter"
                          type="is-dark"
                          position="is-bottom"
                        >
                          <Button
                            icon="fa fa-filter"
                            class="p-button-raised p-button-rounded"
                            @click="applyFilter(r.id)"
                          />
                        </b-tooltip>

                        <b-dropdown aria-role="list">
                          <button
                            style="margin-top: -3px"
                            :class="['button', 'is-info']"
                            slot="trigger"
                          >
                            <i class="fa fa-ellipsis-v"></i>
                          </button>
                          <b-dropdown-item
                            @click="importData(r, index)"
                            aria-role="listitem"
                            >Importer des données</b-dropdown-item
                          >
                          <b-dropdown-item @click="deleteAll(r.id, index)"
                            >Supprimer tout</b-dropdown-item
                          >
                        </b-dropdown>
                      </div>
                    </div>
                  </div>
                  <div
                    :key="itemBody"
                    :id="`repo_item-body-${r.id}`"
                    class="repo__item-body"
                  >
                    <b-loading
                      :active="resourceValues[r.id].api.isLoading"
                      :is-full-page="false"
                    ></b-loading>
                    <div
                      @click="addRV(r)"
                      class="new-record d-flex align-items-center"
                    >
                      <i class="fa fa-plus"></i>
                      Ajouter
                    </div>
                    <ul>
                      <li
                        :class="`d-flex justify-content-between align-items-center ${
                          selectedRV[r.id] && selectedRV[r.id].id === rv.id
                            ? 'selected'
                            : ''
                        }`"
                        v-for="rv in resourceValues[r.id].data"
                        :key="rv.id"
                        @click="setSelectedRV(rv, r.children, index)"
                        :id="rv.id"
                      >
                        <span>{{ rv.value }}</span>
                        <i class="fa fa-angle-right"></i>
                      </li>
                      <infinite-loading @infinite="performApi($event, r.id)">
                        <div slot="spinner"></div>
                        <div slot="no-more">
                          <span
                            v-if="resourceValues[r.id].api.page > 1"
                            style="color: rgba(0, 0, 0, 0.7)"
                            >Plus de résultat
                          </span>
                        </div>
                        <div slot="no-results"></div>
                      </infinite-loading>
                    </ul>
                  </div>
                </template>
                <template v-else-if="r.type === 'RV' || r.type === 'Resource'">
                  <div class="repo__item-header">
                    <div
                      class="d-flex justify-content-between align-items-center"
                    >
                      <h2>
                        <i class="fa fa-copy"></i>
                        {{ r.type === 'RV' ? r.value : r.name }}
                      </h2>
                      <div v-if="r.type === 'RV'">
                        <b-tooltip
                          label="Supprimer le référentiel"
                          type="is-dark"
                          position="is-left"
                        >
                          <Button
                            icon="fa fa-ellipsis-gv"
                            class="p-button-raised p-button-rounded"
                          />
                        </b-tooltip>
                      </div>
                      <div v-else>
                        <b-tooltip
                          label="Supprimer la référentiel"
                          type="is-dark"
                          position="is-bottom"
                        >
                          <Button
                            @click="deleteResource(r.id)"
                            icon="fa fa-trash"
                            class="p-button-raised p-button-rounded"
                          />
                        </b-tooltip>
                      </div>
                    </div>
                  </div>
                  <div class="repo__item-body">
                    <div
                      v-if="r.type === 'RV'"
                      @click="addCollection(r.resourceId)"
                      class="new-record d-flex align-items-center"
                    >
                      <i class="fa fa-plus"></i>
                      Ajouter des fils
                    </div>
                    <div
                      v-else
                      class="new-record d-flex align-items-center"
                    ></div>
                    <div
                      :id="`repo__selected-item-${r.id}`"
                      class="repo__selected-item"
                    >
                      <div
                        class="
                          d-flex
                          justify-content-between
                          align-items-center
                          pr-3
                        "
                      >
                        <h2 :id="`heading-${r.id}`">
                          <strong>Nom :</strong>
                          {{ r.type === 'RV' ? r.value : r.name }}
                        </h2>
                        <div>
                          <b-tooltip
                            :label="`${r.type === 'RV' ? 'Modifier la valeur': 'Modifier le référentiel'}`"
                            type="is-dark"
                            position="is-left"
                          >
                            <Button
                              icon="fa fa-edit"
                              class="p-button-raised p-button-rounded"
                              @click="
                                r.type === 'RV'
                                  ? editRV(r.resourceId)
                                  : editResource(r)
                              "
                            />
                          </b-tooltip>
                          <b-tooltip
                            v-if="r.type === 'RV'"
                            label="Supprimer la valeur"
                            type="is-dark"
                            position="is-left"
                          >
                            <Button
                              icon="fa fa-trash"
                              class="p-button-raised p-button-rounded"
                              @click="deleteRV(r.id, r.resourceId, index)"
                            />
                          </b-tooltip>
                        </div>
                      </div>
                      <div v-if="r.type === 'Resource'">
                        <h2 :id="`code-${r.id}`">
                          <strong>Code :</strong>
                          {{ r.code }}
                        </h2>
                        <h2 :id="`owner-${r.id}`">
                          <strong>Propriétaire :</strong>
                          {{ r.createdBy }}
                        </h2>
                        <h2 :id="`date-${r.id}`">
                          <strong>Date de création :</strong>
                          {{ r.createDate | moment('DD/MM/YYYY') }}
                        </h2>
                        <h2 :id="`date-${r.id}`">
                          <strong>Dernière mise à jour :</strong>
                          {{ r.lastModifiedDate | moment('DD/MM/YYYY') }}
                        </h2>
                        <h2
                          :class="`${
                            resources.length - 1 > index ? 'selected' : ''
                          }`"
                          @click="getResource(r.id, null, index)"
                        >
                          <strong style="font-weight: bold" class="route"
                            >Valeurs de réferentiel
                          </strong>
                          (Liste)
                        </h2>
                      </div>
                      <h2
                        :class="
                          selectedRV[r.id] && selectedRV[r.id] === c.id
                            ? 'selected'
                            : ''
                        "
                        v-for="c in r.children"
                        :key="c.id"
                      >
                        <strong>Réferentiel :</strong>
                        <span
                          class="route"
                          @click="getResource(c.id, r.id, index)"
                          >{{ c.name }}
                        </span>
                      </h2>
                    </div>
                  </div>
                </template>
                <template v-if="r.type === 'AllResources'">
                  <div class="repo__item-header">
                    <div
                      class="d-flex justify-content-between align-items-center"
                    >
                      <h2 v-if="!resourceValues[r.id].api.applyFilter">
                        <i class="fa fa-database"></i>
                        Référentiels
                      </h2>
                      <div v-else>
                        <b-input
                          :placeholder="`Rechercher ici..`"
                          type="search"
                          icon="magnify"
                          icon-clickable
                          autofocus
                          v-model="quering[r.id]"
                          @icon-click="performApi(null, r.id)"
                          @keyup.enter.native="performApi(null, r.id)"
                          :id="r.id"
                        ></b-input>
                      </div>
                      <div>
                        <b-tooltip
                          label="Filter"
                          type="is-dark"
                          position="is-bottom"
                        >
                          <Button
                            icon="fa fa-filter"
                            class="p-button-raised p-button-rounded"
                            @click="applyFilter(r.id)"
                          />
                        </b-tooltip>
                      </div>
                    </div>
                  </div>
                  <div class="repo__item-body">
                    <b-loading
                      :active="resourceValues[r.id].api.isLoading"
                      :is-full-page="false"
                    ></b-loading>
                    <div
                      @click="addCollection()"
                      class="new-record d-flex align-items-center"
                    >
                      <i class="fa fa-plus"></i>
                      Nouveau référentiel
                    </div>
                    <TreeDisplay
                      @handleItemClick="handleItemClick"
                      :nodes="getAllResources()"
                      :index="index"
                      :selectedRV="selectedRV"
                      :root="true"
                    >
                      <template>
                        <infinite-loading @infinite="performApi($event, r.id)">
                          <div slot="spinner"></div>
                          <div slot="no-more">
                            <span
                              v-if="resourceValues[r.id].api.page > 1"
                              style="color: rgba(0, 0, 0, 0.7)"
                              >Plus de résultat
                            </span>
                          </div>
                          <div slot="no-results"></div>
                        </infinite-loading>
                      </template>
                    </TreeDisplay>
                  </div>
                </template>
              </div>
            </div>
          </div>
        </div> </template
    ></Panel>
    <b-modal
      :active.sync="showModal"
      has-modal-card
      @close="resourceVToEdit = null"
    >
      <ResourceValueModal
        :resourceVToEdit="selectedRvToEdit"
        @updateValue="updateValue"
        @addValue="addValue"
      ></ResourceValueModal>
    </b-modal>
    <b-modal
      :active.sync="showNewResourceModal"
      has-modal-card
      @close="
        resourceToEdit = null
        parentResource = null
      "
    >
      <NewResourceModal
        :parentResource="parentResource"
        :resourceToEdit="resourceToEdit"
        :parentId="parentId"
        @addResource="addResource"
        @updateResource="updateResource"
      ></NewResourceModal>
    </b-modal>
    <b-modal
      :active.sync="showUploadResourceValuesModal"
      has-modal-card
      :can-cancel="false"
      @close="resourceVToEdit = null"
    >
      <UploadResourceModal
        :uploadRVDataConfig="uploadRVDataConfig"
        @addValue="addValue"
      ></UploadResourceModal>
    </b-modal>
  </section>
</template>

<script>
import pageTitle from '~/mixins/page-title'
import Panel from '~/components/layout/Panel'
import TreeDisplay from '~/components/ui/TreeDisplay'
import ResourceValueModal from '~/components/dashboard/resources/ResourceValueModal'
import NewResourceModal from '~/components/dashboard/resources/NewResource'
import UploadResourceModal from '~/components/dashboard/resources/UploadResourceValues'
import Button from 'primevue/button'
import { backApi } from '~/methods/serverApi'
import InfiniteLoading from 'vue-infinite-loading'

export default {
  layout: 'dashboard',
  mixins: [pageTitle],
  validate({ params, query, store }) {
    return (
      store.state.profile.roles.includes('ROLE_ADMIN') ||
      store.state.profile.roles.includes('RESOURCE_READ_AUTHORITY')
    )
  },
  components: {
    Panel,
    Button,
    InfiniteLoading,
    ResourceValueModal,
    NewResourceModal,
    UploadResourceModal,
    TreeDisplay,
  },
  data() {
    return {
      showModal: false,
      showNewResourceModal: false,
      showUploadResourceValuesModal: false,
      page: {
        title: 'Gestion des référentiels',
        hid: '',
        name: '',
        description: '',
      },
      resourceValues: {},
      resourceToEdit: null,
      selectedRV: {},
      selectedRvToEdit: null,
      resources: [],
      parentResource: null,
      parentId: null,
      quering: {},
      uploadRVDataConfig: {
        criterias: {},
      },
      lock: false,
      itemBody: 0,
    }
  },
  computed: {
    getPath() {
      return this.resources.filter((r, index) => index !== 1)
    },
  },
  methods: {
    getAllResources() {
      let parents = this.resourceValues['AllResources'].data
        .filter((r) => r.parentResource === null)
        .map((p) => {
          return { ...p, subResources: [] }
        })
      let children = this.resourceValues['AllResources'].data
        .filter((r) => r.parentResource !== null)
        .map((p) => {
          return { ...p, subResources: [] }
        })
      for (let p of parents) {
        p = this.loopOver(p, children)
      }
      return this.quering['AllResources'] && this.quering['AllResources'] !== ''
        ? [...parents, ...children]
        : parents.length > 0
        ? parents
        : children
    },
    addCollection(parentId) {
      this.showNewResourceModal = true
      this.parentId = parentId
      if (parentId) {
        this.parentResource = this.resources.some((r) => r.id === parentId)
          ? this.resources.filter((r) => r.id === parentId)[0]
          : null
      }
    },
    editResource(resource) {
      this.showNewResourceModal = true
      this.resourceToEdit = resource
      this.parentResource = resource.parentResource
    },
    getTranslate() {
      if (document.getElementById('repo__item')) {
        if (window.innerWidth <= '1000' && this.resources.length > 1) {
          return `translateX(-${100 * (this.resources.length - 1)}%)`
        } else if (this.resources.length > 3) {
          return `translateX(-${27 * (this.resources.length - 3)}%)`
        }
      }
      return `translateX(0)`
    },
    setSelectedRV(payload, children, index) {
      //check if comming payload belongs to a resource
      if (payload['resourceValues'] === null) {
        this.goToResource(index)
        if (index === 1) {
          this.resources = this.resources.slice(0, 1)
        }
        this.resources.push({
          ...payload,
          id: 'r-' + payload.id,
          type: 'Resource',
        })
        this.selectedRV['r-' + payload.id] = {
          ...payload,
          id: 'r-' + payload.id,
        }
      } else {
        this.goToResource(index)
        this.resources.push({
          ...payload,
          type: 'RV',
          children,
        })
        this.selectedRV[payload.resourceId] = {
          ...payload,
          children,
        }
      }
    },
    handleItemClick(payload) {
      // if (!this.lock) {
      for (let key in this.selectedRV) {
        delete this.selectedRV[key]
      }
      let node = this.getNode(payload)
      this.setSelectedRV(node, payload.children, payload.index)
      // }
    },
    getNode(payload) {
      if (payload['node'] != null) {
        payload = this.getNode(payload.node)
      }
      return payload
    },
    performApi($state, resourceID) {
      if ($state === null) {
        this.resourceValues[resourceID].api.page = 0
        this.resourceValues[resourceID].api.total = 0
        this.resourceValues[resourceID].data = []
      }
      if (
        this.resourceValues[resourceID].api.page === 0 ||
        this.resourceValues[resourceID].total /
          this.resourceValues[resourceID].api.size >
          this.resourceValues[resourceID].api.page
      ) {
        let routeConfig = null
        if (resourceID === 'AllResources') {
          routeConfig = backApi['post'](
            'resources/search',
            {
              condition: 'and',
              rules: [
                {
                  field: 'name',
                  label: 'name',
                  operator: 'ilike',
                  type: 'string',
                  value: this.quering['AllResources']
                    ? this.quering['AllResources']
                    : '',
                },
              ],
            },
            {
              params: {
                sort: this.resourceValues[resourceID].api.sort,
                page: this.resourceValues[resourceID].api.page,
                limit: this.resourceValues[resourceID].api.size,
                dir: 'asc',
              },
            }
          )
        } else {
          routeConfig = backApi['get'](
            Object.keys(this.selectedRV).some(
              (key) => this.selectedRV[key] === resourceID
            )
              ? `resourcevalues/resource/${resourceID}/${Object.keys(
                  this.selectedRV
                ).find((key) => this.selectedRV[key] === resourceID)}`
              : `resourcevalues/resource/${resourceID}`,
            {
              params: {
                search: this.quering[resourceID]
                  ? this.quering[resourceID]
                  : '',
                sort: this.resourceValues[resourceID].api.sort,
                page: this.resourceValues[resourceID].api.page,
                limit: this.resourceValues[resourceID].api.size,
                dir: 'asc',
              },
            }
          )
        }
        this.resourceValues[resourceID].api.isLoading = true
        routeConfig
          .then(({ data }) => {
            let tmpObject = this.resourceValues[resourceID]
            tmpObject.data.push(...data.content)
            tmpObject.total = data.totalElements
            if (resourceID === 'AllResources') {
              this.getAllResources()
            }

            setTimeout(() => {
              tmpObject.api.isLoading = false
              tmpObject.api.page++

              this.resourceValues[resourceID].api.page > 0 && $state
                ? $state.loaded()
                : this.itemBody++
            }, 200)
            this.resourceValues = Object.assign(
              {},
              this.resourceValues,
              tmpObject
            )
          })
          .catch(() => {
            this.resourceValues[resourceID].api.isLoading = false
            this.resourceValues[resourceID].api.page > 0 &&
              $state &&
              $state.loaded()
          })
      } else {
        this.resourceValues[resourceID].api.page > 0 &&
          $state &&
          $state.complete()
      }
    },
    editRV(resourceID) {
      this.showModal = true
      this.selectedRvToEdit = this.selectedRV[resourceID]
    },
    addRV(resource) {
      this.showModal = true
      this.selectedRvToEdit = {
        value: '',
        isNew: true,
        resourceId: resource.id,
        parentId: resource.parentResource
          ? Object.keys(this.selectedRV).find(
              (key) => this.selectedRV[key] === resource.id
            )
          : null,
      }
    },
    appendChild(resource, child, children) {
      resource.subResources.push(child)
      child = this.loopOver(child, children)
      return resource
    },
    loopOver(parent, children) {
      for (let c of children.filter(
        (c) => c.parentResource.id === parent.id
      )) {
        parent = this.appendChild(parent, c, children)
      }
      return parent
    },
    deleteRV(rvId, resourceId, index) {
      this.$confirm({
        title: 'Êtes-vous sûr de supprimer cette valeur?',
        content: 'Cette action supprimera le paramètre de la base de données.',
        okText: 'Oui',
        okType: 'danger',
        cancelText: 'Non',
        onOk: () => {
          this.$nextTick(() => {
            this.$nuxt.$loading.start()
          })
          backApi
            .delete(`resourcevalues/${rvId}`)
            .then(() => {
              document
                .getElementById(rvId)
                .classList.add('repo__animation-delete')
              document
                .getElementById(`heading-${rvId}`)
                .classList.add('repo__animation-delete')
              this.$nuxt.$loading.finish()
              setTimeout(() => {
                this.resourceValues[resourceId].data = this.resourceValues[
                  resourceId
                ].data.filter((r) => r.id !== rvId)
                this.resourceValues[resourceId].total--
                this.goToResource(index - 1)
              }, 1000)
            })
            .catch((error) => {
              this.$nuxt.$loading.finish()
              this.$notification.error({
                message: 'Erreur!',
                description:
                  error.response && error.response.data
                    ? error.response.data.message
                    : "Une erreur inattendue s'est produite !",
              })
            })
        },
        onCancel: () => {},
      })
    },
    addResource(payload) {
      this.showNewResourceModal = false
      this.resourceToEdit = null
      this.parentResource = null
      this.parentId = null
      if (payload) {
        this.resourceValues['AllResources'].data.push(payload.data)
        if (payload.parentId) {
          this.resources
            .find((r) => r.id === payload.parentId)
            .children.push(payload.data)
        }
      }
    },
    updateResource(payload) {
      this.showNewResourceModal = false
      this.resourceToEdit = null
      this.parentResource = null
      if (payload) {
        const index = this.resourceValues['AllResources'].data.findIndex(
          (p) => p.id === payload.id
        )
        if (index !== -1) {
          this.resourceValues['AllResources'].data.splice(index, 1, payload)
        }
        if (this.resources.some((r) => r.id === 'r-' + payload.id)) {
          this.resources.find((r) => r.id === 'r-' + payload.id).name =
            payload.name
          this.resources.find((r) => r.id === 'r-' + payload.id).code =
            payload.code
          document
            .getElementById(`repo__selected-item-r-${payload.id}`)
            .classList.add('repo__animation-edit')
          setTimeout(() => {
            document
            .getElementById(`repo__selected-item-r-${payload.id}`)
              .classList.remove('repo__animation-edit')
          }, 500)
        }
      }
    },
    deleteResource(resourceId) {
      this.$confirm({
        title: 'Êtes-vous sûr de supprimer ce referentiel?',
        content:
          'Cette action supprimera le referentiel incluant tous les referentiels imbriqués',
        okText: 'Oui',
        okType: 'danger',
        cancelText: 'Non',
        onOk: () => {
          this.$nextTick(() => {
            this.$nuxt.$loading.start()
          })
          backApi
            .delete(`resources/${resourceId.replace('r-', '')}`)
            .then(() => {
              document
                .getElementById(`repo__selected-item-${resourceId}`)
                .classList.add('repo__animation-delete')
              this.$nuxt.$loading.finish()
              setTimeout(() => {
                this.resourceValues['AllResources'].data = this.resourceValues[
                  'AllResources'
                ].data.filter((r) => r.id !== resourceId.replace('r-', ''))
                this.goToResource(0)
              }, 500)
            })
            .catch((error) => {
              this.$nuxt.$loading.finish()
              this.$notification.error({
                message: 'Erreur!',
                description:
                  error.response && error.response.data
                    ? error.response.data.message
                    : "Une erreur inattendue s'est produite !",
              })
            })
        },
        onCancel: () => {},
      })
    },
    updateValue(payload) {
      this.showModal = false
      if (payload.updated) {
        this.resources.filter(
          (r) => r.resourceId === payload.resourceId
        )[0].value = payload.value
        this.resourceValues[payload.resourceId].data.find(
          (d) => d.id === payload.id
        ).value = payload.value
        this.selectedRV[payload.resourceId].value = payload.value
        document
          .getElementById(payload.id)
          .classList.add('repo__animation-edit')
        document
          .getElementById(`heading-${payload.id}`)
          .classList.add('repo__animation-edit')
        setTimeout(() => {
          document
            .getElementById(`heading-${payload.id}`)
            .classList.remove('repo__animation-edit')
          document
            .getElementById(payload.id)
            .classList.remove('repo__animation-edit')
        }, 800)
      }
    },
    addValue(payload) {
      this.showModal = false
      this.showUploadResourceValuesModal = false
      if (payload) {
        let data = []
        if (!Array.isArray(payload)) {
          data.push(payload)
        } else {
          data = payload
        }
        for (let payload of data) {
          this.resourceValues[payload.resourceId] &&
            this.resourceValues[payload.resourceId].data.unshift(payload)
          this.resourceValues[payload.resourceId] &&
            this.resourceValues[payload.resourceId].total++
          setTimeout(() => {
            document
              .getElementById(payload.id)
              .classList.add('repo__animation-add')
          }, 100)
        }
      }
    },
    getResource(resourceID, rvId, index) {
      // if (!this.lock) {
      resourceID = resourceID.replace('r-', '')
      if (this.quering[resourceID] != '') {
        let tmpObject = this.quering
        tmpObject[resourceID] = ''
        this.quering = Object.assign({}, this.quering, {
          ...tmpObject,
        })
      }
      if (!rvId || !this.resources.some((r) => r.id === resourceID)) {
        if (rvId && this.selectedRV[rvId]) {
          this.goToResource(index)
        }
        if (rvId) this.selectedRV[rvId] = resourceID
        if (!this.resourceValues[resourceID]) {
          this.resourceValues[resourceID] = {
            data: [],
            total: 0,
            api: {
              applyFilter: false,
              isLoading: false,
              page: 0,
              size: 30,
              sort: 'value',
              dir: 'asc',
            },
          }
        } else {
          this.resourceValues[resourceID].data = []
          this.resourceValues[resourceID].api.page = 0
        }
        backApi
          .get(`resources/${resourceID}`)
          .then(({ data }) => {
            backApi
              .get(`resources/${resourceID}/children`)
              .then(({ data: children }) => {
                if (!this.resources.some((r) => r.id === data.id)) {
                  if (this.resources[this.resources.length - 1].type === 'R') {
                    this.resources.pop()
                  }
                  this.resources.push({
                    ...data,
                    type: 'R',
                    children,
                  })
                  this.resourceValues[resourceID].api.isLoading = true
                } else {
                  this.itemBody++
                }
              })
              .catch(() => {
                this.resources.pop()
                delete this.resourceValues[resourceId]
              })
          })
          .catch(() => {})
      } else {
        this.goToResource(index)
      }
      // }
    },
    goToResource(index) {
      this.resources = this.resources.slice(0, index + 1)
      Object.keys(this.selectedRV).forEach((key, i) => {
        if (i >= index) {
          delete this.selectedRV[key]
        }
      })
    },
    applyFilter(resourceID) {
      this.resourceValues[resourceID].api.applyFilter =
        !this.resourceValues[resourceID].api.applyFilter
    },
    deleteAll(resourceID, index) {
      this.$confirm({
        title: 'Êtes-vous sûr de supprimer tous le valeurs de ce referentiel?',
        content: 'Cette action supprimera le paramètre de la base de données.',
        okText: 'Oui',
        okType: 'danger',
        cancelText: 'Non',
        onOk: () => {
          let parentId = null
          if (
            this.resources[index - 1] &&
            this.resources[index - 1].type === 'RV'
          ) {
            parentId = this.resources[index - 1].id
          }
          this.$nextTick(() => {
            this.$nuxt.$loading.start()
          })
          backApi
            .delete(
              `resourcevalues/${
                parentId ? `${parentId}/delete-all` : `resource/${resourceID}`
              }`
            )
            .then(({ data }) => {
              this.$nuxt.$loading.finish()
              if (data.length < this.resourceValues[resourceID].total) {
                this.$notification.warning({
                  message: 'Remarque!',
                  description:
                    "Ne peut pas supprimer toutes les valeurs, veuillez d'abord supprimer ses enfants",
                })
              }
              this.resourceValues[resourceID].data.forEach((d) => {
                if (data.some((a) => a === d.id)) {
                  document
                    .getElementById(d.id)
                    .classList.add('repo__animation-delete')
                }
              })
              setTimeout(() => {
                this.resourceValues[resourceID].data = this.resourceValues[
                  resourceID
                ].data.filter((d) => !data.includes(d.id))
                this.resourceValues[resourceID].total =
                  this.resourceValues[resourceID].total - data.length
                this.goToResource(index - 1)
              }, 500)
            })
            .catch((error) => {
              this.$nuxt.$loading.finish()
              this.$notification.error({
                message: 'Erreur!',
                description:
                  error.response && error.response.data
                    ? error.response.data.message
                    : "Une erreur inattendue s'est produite !",
              })
            })
        },
        onCancel: () => {},
      })
    },
    importData(resource, index) {
      index -= 2
      this.showUploadResourceValuesModal = true
      Object.keys(this.selectedRV).forEach((key, i) => {
        if (i === index - 1) {
          this.uploadRVDataConfig.criterias = {
            ...this.selectedRV[key],
            parentResource: resource,
          }
          index = 0
        }
        if (i === index) {
          this.uploadRVDataConfig.criterias = {
            ...this.selectedRV[key],
          }
        }
      })
    },
  },
  beforeMount() {
    this.resources.push({
      type: 'AllResources',
      applyFilter: false,
      query: '',
      id: 'AllResources',
    })
    this.resourceValues['AllResources'] = {
      data: [],
      total: 0,
      api: {
        isLoading: true,
        applyFilter: false,
        query: '',
        page: 0,
        size: 30,
        sort: 'name',
        dir: 'asc',
      },
    }
  },
}
</script>

<style lang="scss" scoped>
.repo {
  h2,
  li {
    color: rgba(0, 0, 0, 0.54);
  }
  i {
    color: #0000008a;
    font-size: 1.2rem;
  }
  .route {
    font-weight: normal;
    &:hover {
      text-decoration: underline;
      cursor: pointer;
      color: #0066cc;
    }
  }
  &__wrapper {
    border-radius: 8px 8px 0 0;
    width: 100%;
    height: 70vh;
    box-shadow: 0 1px 2px 0 #3c40434d, 0 1px 3px 1px #3c404326;
    margin: 2rem 1rem;
    transform: translate3d(0, 0, 0);
    overflow: hidden;
    @include respond('phone') {
      margin: 1rem 0.1rem;
    }
  }
  &__header {
    div {
      align-items: flex-start !important;
      overflow-x: auto;
      white-space: nowrap;
      @include hideScroll;
    }
    width: 100%;
    height: 3.5rem;
    padding: 1rem;
    background: #fafafa;
    box-shadow: 0 -1px 0 #0000001f inset;
    i {
      margin-top: 2px;
      font-size: 1.2rem;
      font-weight: bold;
    }
    span {
      margin: 0 0.4rem;
      font-weight: bold;
    }
  }
  &__virtual-header {
    position: absolute;
    top: 3.5rem;
    left: 0;
    height: 3.8rem;
    max-height: 3.8rem;
    width: 100%;
    background: #fafafa;
    box-shadow: 0 -1px 0 #0000001f inset;
    z-index: 0;
  }
  &__body {
    display: flex;
    justify-content: space-between;
    width: 100%;
    height: calc(100% - 3.5rem);
    top: 3.5rem;
    left: 0;
    position: absolute;
    overflow: hidden;
  }
  &__panel {
    position: relative;
    display: block;
    height: 100%;
    width: 100%;
    white-space: nowrap;
    transition: 0.2s transform cubic-bezier(0.4, 0, 0.2, 1);
  }
  &__item {
    position: relative;
    display: inline-flex;
    width: 27%;
    height: 100%;
    max-height: 100%;
    border-right: 1px solid rgba(0, 0, 0, 0.12);
    flex-direction: column;
    vertical-align: top;
    &:nth-child(1) &-body ul {
      overflow: auto !important;
    }
    @media screen and (max-width: 1000px) {
      width: 100%;
    }
    &-header {
      width: 100%;
      padding: 0.8rem 0.8rem 0.8rem 1.1rem;
      background: #fafafa;
      box-shadow: 0 -1px 0 #0000001f inset;
      height: 3.8rem;
      max-height: 3.8rem;
      input {
        display: none !important;
      }
      h2 {
        text-transform: capitalize;
        color: #0000008a;
        font-weight: bold;
      }
      .p-button.p-button-icon-only {
        padding: 0 0.3rem;
      }
      button {
        background: #fafafa;
        color: #0000008a;
        border: none;
        box-shadow: none;
        font-size: 1.2rem;
        &:hover {
          color: #000;
          background: #fafafa;
        }
      }
    }
    &-body {
      position: relative;
      flex-grow: 1;
      height: 0;
      display: flex;
      flex-direction: column;
      overflow: hidden;
      .new-record {
        cursor: pointer;
        padding: 0.95rem 1.7rem;
        color: #1a73e8;
        height: 3.2rem;
        i {
          color: #1a73e8;
          margin-right: 0.8rem;
        }
        &:hover {
          background-color: rgba(26, 115, 232, 0.2);
        }
      }
      ul {
        overflow-y: auto;
        height: 100%;
        li {
          padding: 0.5rem 1.5rem 0.5rem 2rem;
          cursor: pointer;
          position: relative;
          &:hover {
            background: #00000014;
          }
          &.selected {
            background: #00000014;
            font-weight: bold;
            color: #020202;
          }
          i {
            opacity: 0.8;
          }
        }
      }
      .new-record {
        border-bottom: 1px solid rgba(0, 0, 0, 0.12);
      }
    }
    &--extended {
      display: inline-flex;
      width: 46%;
      @media screen and (max-width: 1000px) {
        width: 100%;
      }
    }
  }
  &__selected-item {
    position: relative;
    .b-tooltip {
      &:nth-child(1) {
        margin-right: 0.2rem;
      }
    }
    h2 {
      padding: 0.8rem 1.65rem;
      &:not(:first-child) {
      }
      position: relative;
      &.selected {
        background: #00000014;
      }
    }
    button {
      background: transparent;
      color: #0000008a;
      box-shadow: none;
      border: none;
      font-size: 1.2rem;
      &:hover {
        background: transparent;
        color: #0000008a;
        box-shadow: none;
        border: none;
      }
    }
  }
  @mixin animation($background) {
    position: absolute;
    content: '';
    top: 0;
    left: 0;
    height: 100%;
    background-color: $background;
    animation: backgroundAnimation 0.45s cubic-bezier(0.075, 0.82, 0.165, 1);
    @keyframes backgroundAnimation {
      0% {
        background-color: $background;
      }
      100% {
        width: 100%;
      }
    }
  }
  &__animation-add {
    &::after {
      @include animation(rgba(0, 128, 128, 0.3));
    }
  }
  &__animation-edit {
    padding: 5px;
    &::after {
      @include animation(rgba(245, 130, 2, 0.5));
    }
  }
  &__animation-delete {
    padding: 5px;
    &::after {
      @include animation(rgba(255, 0, 0, 0.2));
    }
  }
}
</style>