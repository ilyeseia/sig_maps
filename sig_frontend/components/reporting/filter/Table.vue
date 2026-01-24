<template>
  <form @submit.prevent="onSubmit">
    <div class="modal-card">
      <header class="modal-card-head">
        <h1 class="modal-card-title">Mes Filtres</h1>
      </header>
      <div class="layers-list modal-card" style="width: 800px">
        <render-data
          :data="userFilters"
          alertMessage="Il y'a aucun filtre à afficher"
          :loading="loadingData"
        >
          <section
            class="modal-card-body"
            style="height: 75vh; overflow: hidden auto"
          >
            <b-table
              :data="userFilters"
              paginated
              backend-pagination
              :per-page="perPage"
              :total="totalRowsCount"
              @page-change="onPageChange"
              backend-sorting
            >
              <template slot-scope="props">
                <b-table-column width="30%" field="email" label="Nom">
                  <span :tooltip="props.row.name"> {{ props.row.name }}</span>
                </b-table-column>
                <b-table-column width="20%" field="layer" label="Couche">
                  <span
                    :tooltip="props.row.filterConfig.layer.text"
                  >
                    {{ props.row.filterConfig.layer.text }}</span
                  >
                </b-table-column>
                <b-table-column field="createdBy" label="Propriétaire">
                  <span :tooltip="props.row.createdBy">
                    {{ props.row.createdBy }}</span
                  >
                </b-table-column>
                <b-table-column
                  width="20%"
                  field="createDate"
                  label="Date de création"
                  sortable
                  centered
                >
                  <span class="tag is-success">
                    {{
                      props.row.createDate &&
                      props.row.createDate | moment('DD/MM/YYYY')
                    }}
                  </span>
                </b-table-column>
                <b-table-column
                  width="23%"
                  field="edit"
                  label="Actions"
                  class="data-table__actions"
                >
                  <b-tooltip
                    label="Sélectionner"
                    type="is-dark"
                    position="is-bottom"
                  >
                    <div class="btn-action btn-select">
                      <i
                        @click="selectFilter(props.row)"
                        class="fas fa-check"
                        data-toggle="tooltip"
                        data-placement="top"
                      >
                      </i>
                    </div>
                  </b-tooltip>
                  <b-tooltip
                    label="Partager"
                    type="is-dark"
                    position="is-bottom"
                  >
                    <div
                      v-if="
                        props.row.filterClonedFrom &&
                        props.row.filterClonedFrom[0] != null
                      "
                      class="btn-action btn-partager"
                    >
                      <i
                        @click="shareFilter(props.row)"
                        class="fas fa-share-alt"
                        data-toggle="tooltip"
                        data-placement="top"
                      >
                      </i>
                    </div>
                  </b-tooltip>
                  <b-tooltip
                    label="Supprimer"
                    type="is-dark"
                    position="is-bottom"
                  >
                    <div
                      v-if="
                        props.row.filterClonedFrom &&
                        props.row.filterClonedFrom[0] != null
                      "
                      class="btn-action btn-delete"
                    >
                      <i
                        @click="deleteFilter(props.row.id)"
                        class="fas fa-trash"
                        data-toggle="tooltip"
                        data-placement="top"
                      >
                      </i>
                    </div>
                  </b-tooltip>
                </b-table-column>
              </template>
            </b-table>
          </section>
        </render-data>
        <footer class="right-align modal-card-foot">
          <button
            id="close-layer-form"
            class="button"
            type="button"
            @click="$parent.close()"
          >
            Fermer
          </button>
        </footer>
      </div>
    </div>
  </form>
</template>

<script>
import { backApi } from '../../../methods/serverApi'
import { mapState } from 'vuex'
import RenderData from '~/components/layout/RenderData.vue'
export default {
  components: { RenderData },
  data() {
    return {
      userFilters: [],
      page: 1,
      perPage: 10,
      sortField: 'name',
      sortOrder: 'asc',
      totalRowsCount: 2,
      loadingData: false
    }
  },
  computed: {
    ...mapState(['profile'])
  },
  methods: {
    getLayerName(layerId) {
      return this.profile.layers.some(l => l.id === layerId) && this.profile.layers.find(l => l.id === layerId).name
    },
    selectFilter(filter) {
      this.$emit('setSelectedFilter', filter)
    },
    deleteFilter(id) {
      this.$emit('deleteFilter', id)
    },
    shareFilter(filter) {
      this.$emit('shareFilter', filter)
    },
    onPageChange(page) {
      this.page = page
      this.getUserFilter()
    },
    getUserFilter() {
      this.loadingData = true
      backApi
        .get('filters/by-user', {
          page: this.page - 1,
          limit: this.perPage,
          sort: this.sortField,
          dir: this.sortOrder
        })
        .then(({ data }) => {
          this.userFilters  = data.content.map(l => {
            let filterConfig = JSON.parse(l.filterConfig)
            let layer  =  this.profile.layers.some(layer => layer.id === filterConfig.layer) && this.profile.layers.find(layer => layer.id === filterConfig.layer)
            filterConfig.layer = {
              text: layer.name,
              value: layer.slug
            }

            return {
              ...l,
              filterConfig
            }
          })
          this.totalRowsCount = data.totalElements
          this.loadingData = false
        })
        .catch(e => {
          this.loadingData = false
        })
    },
    closeModal() {
      this.$emit('closeModal')
    }
  },
  beforeMount() {
    this.getUserFilter()
  }
}
</script>

<style scoped lang="scss">
.btn-action {
  cursor: pointer;
  i {
    color: #fff;
    width: 33px;
    height: 33px;
    text-align: center;
    line-height: 23px;
    padding: 6px;
    border-radius: 40px;
    @include respond('tab-port') {
      width: 25px;
      height: 25px;
      padding: 3px;
      line-height: 20px;
    }
  }
  &.btn-delete i {
    background-color: #dc3545;
  }
  &.btn-select i {
    background-color: #17a2b8;
  }
  &.btn-partager i {
    background-color: #6c757d;
  }
  @media screen and (max-width: 1500px) {
    i {
      background: red;
      margin-bottom: 1rem !important;
    }
  }
}

table {
  position: relative;
}
table td:not(:last-child) {
  max-width: 0;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;

  [tooltip]::before,
  [tooltip]::after {
    text-transform: none;
    font-size: 0.9em;
    line-height: 1;
    user-select: none;
    pointer-events: none;
    position: absolute;
    display: none;
  }

  [tooltip]::before {
    content: '';
    border: 5px solid transparent;
    z-index: 1001;
  }

  [tooltip]::after {
    content: attr(tooltip);
    text-align: left;
    min-width: 3em;
    max-width: 21em;
    white-space: pre-wrap;
    overflow: hidden;
    padding: 1ch 1.5ch;
    border-radius: 0.3ch;
    box-shadow: 0 1em 2em -0.5em rgba(0, 0, 0, 0.35);
    background: #333;
    color: #fff;
    z-index: 1000;
  }

  [tooltip]:hover::before,
  [tooltip]:hover::after {
    display: block;
  }

  [tooltip='']::before,
  [tooltip='']::after {
    display: none !important;
  }
  @include respond('tab-port') {
    max-width: unset;
    overflow: visible;
  }
}
table td:last-child {
  span {
    @include respond('tab-port') {
      margin-top: 12px;
    }
  }
}
</style>
