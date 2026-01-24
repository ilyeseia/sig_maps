<template>
  <section class="p-5 w-100">
    <div class="d-flex justify-content-between row-column">
      <div>
        <button
          v-if="
            profile.roles.includes('ROLE_ADMIN') ||
            profile.roles.includes('SETTINGS_CREATE_AUTHORITY')
          "
          id="add-new-setting"
          class="button is-primary mb-2"
          @click="$emit('newSetting')"
        >
          <b-icon class="mr-1" pack="fas" icon="plus"></b-icon>Nouveau
        </button>
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
      <template #title> paramètres </template>
      <template #default>
        <div>
          <div
            v-for="(option, index) in settingsType.settingsType"
            :key="option.id"
            class="collpase"
          >
            <div @click="toggle(index)" class="collpase__header">
              <strong>{{ option.code }}</strong>
              <b-icon :icon="index !== isOpen ? 'menu-down' : 'menu-up'">
              </b-icon>
            </div>
            <div
              :class="{
                collpase__content: true,
                collpase__opened: index === isOpen ? true : false,
              }"
            >
              <b-table
                :data="getTableData(option.code)"
                paginated
                backend-pagination
                :per-page="perPage"
                :total="totalRowsCount"
                @page-change="onPageChange"
                :show-detail-icon="true"
                backend-sorting
                :default-sort-direction="defaultSortOrder"
                :default-sort="[sortField, sortOrder]"
                @sort="onSort"
                class="admin-data-table p-datatable-striped"
              >
                <template slot-scope="props">
                  <b-table-column field="code" label="Code" sortable>{{
                    props.row.code
                  }}</b-table-column>
                  <b-table-column field="type" label="Type" sortable>{{
                    props.row.type
                  }}</b-table-column>
                  <b-table-column field="value" label="Valeur" sortable>{{
                    props.row.value
                  }}</b-table-column>
                  <b-table-column
                    field="default_value"
                    label="Valeur par défault"
                    sortable
                  >
                    <span
                      :class="`chips__chip chips__chip--${
                        props.row.default_value ? 'green' : 'red'
                      }`"
                    >
                      {{ props.row.default_value ? 'Oui' : 'Non' }}
                    </span>
                  </b-table-column>
                  <b-table-column field="enabled" label="Activé" sortable>
                    <span
                      :class="`chips__chip chips__chip--${
                        props.row.enabled ? 'green' : 'red'
                      }`"
                    >
                      {{ props.row.enabled ? 'Oui' : 'Non' }}
                    </span>
                  </b-table-column>
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
                    field="edit"
                    label="Actions"
                    centered
                    class="data-table__actions"
                  >
                    <b-tooltip
                      label="Modifier"
                      type="is-dark"
                      position="is-bottom"
                    >
                      <Button
                        v-if="
                          profile.roles.includes('ROLE_ADMIN') ||
                          profile.roles.includes('SETTINGS_UPDATE_AUTHORITY')
                        "
                        icon="pi pi-pencil"
                        class="p-button-rounded p-button-success p-mr-2"
                        @click="$emit('editSetting', props.row.id)"
                      />
                    </b-tooltip>
                    <!-- <b-tooltip label="Supprimer" type="is-dark" position="is-bottom">
                <Button
                  v-if="
                    profile.roles.includes('ROLE_ADMIN') ||
                    profile.roles.includes('SETTINGS_DELETE_AUTHORITY')
                  "
                  icon="pi pi-trash"
                  class="p-button-rounded p-button-danger"
                  @click="deleteSetting(props.row.id)"
                />
              </b-tooltip> -->
                  </b-table-column>
                </template>
              </b-table>
            </div>
          </div>
        </div>
      </template>
    </Panel>
  </section>
</template>

<script>
import { mapState } from 'vuex'
import RestApi from '../../../methods/api'
import Button from 'primevue/button'
import Panel from '../../layout/Panel'

export default {
  layout: 'dashboard',
  components: { Button, Panel },
  data() {
    return {
      searchName: "",
      sortField: 'createDate',
      sortOrder: 'desc',
      defaultSortOrder: 'desc',
      perPage: 15,
      page: 1,
      isOpen: 0,
    }
  },
  computed: {
    ...mapState(['settings', 'profile', 'settingsType']),
    totalRowsCount() {
      return this.settings.totalRowsCount
    },
  },
  watch: {
    searchName(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.searchNameRef.value) {
          this.page = 1
          this.filterSettings()
        }
      }, 500)
    },
  },
  methods: {
    getTableData(id) {
      return this.settings.settings.filter((d) => d.type === id)
    },
  
    toggle(index) {
      if (index === this.isOpen) {
        this.isOpen = -1
      } else {
        this.isOpen = index
      }
    },
    loadAsyncData() {
      RestApi.findAll(
        'settings',
        {
          page: this.page - 1,
          limit: this.perPage,
          sort: this.sortField,
          dir: this.sortOrder,
        },
        'settings/set',
        this.$store,
        this.$router,
        this.$notification
      ),
        RestApi.getAll('settingsType', { limit: -1 }).then(
          ({ content, totalElements }) => {
            this.$store.commit('settingsType/set', {
              content,
              totalElements,
            })
          }
        )
    },
    onSort(field, order) {
      this.sortField = field
      this.sortOrder = order
      if (this.searchName === '') {
        this.loadAsyncData()
      } else {
        this.filterSettings()
      }
    },
    onPageChange(page) {
      this.page = page
      if (this.searchName === '') {
        this.loadAsyncData()
      } else {
        this.filterSettings()
      }
    },
    filterSettings() {
      let payload = {
        condition: 'and',
        rules: [
          {
            label: 'code',
            field: 'code',
            operator: 'ilike',
            type: 'string',
            value: this.searchName,
          },
        ],
      }
      RestApi.findAllByCriteria(
        'settings',
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
    deleteSetting(id) {
      this.$confirm({
        title: 'Êtes-vous sûr de supprimer ce paramètre ?',
        content: 'Cette action supprimera le paramètre de la base de données.',
        okText: 'Oui',
        okType: 'danger',
        cancelText: 'Non',
        onOk: () => {
          this.$nextTick(() => {
            this.$nuxt.$loading.start()
          })
          RestApi.delete(
            'settings',
            id,
            this.$store,
            this.$router,
            this.$notification
          )
            .then(() => {
              this.searchName = ''
              this.loadAsyncData()
              this.$nuxt.$loading.finish()
            })
            .catch((error) => {
              this.$nuxt.$loading.finish()
            })
        },
        onCancel: () => {},
      })
    },
  },
  beforeMount() {
    this.loadAsyncData()
  },
}
</script>
<style lang="scss" scoped>
.collpase {
  max-width: 100%;
  position: relative;
  box-shadow: 0 0.5em 1em -0.125em #0a0a0a1a, 0 0 0 1px #0a0a0a05;
  background-color: #fff;
  border-radius: 0.25rem;
  border-bottom: 1px solid #0a0a0a1a;
  &__header {
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px;
    cursor: pointer;
    box-shadow: 0 0.125em 0.25em #0a0a0a1a;
    border-bottom: 1px solid #0a0a0a1a;
  }
  &__content {
    transition: height 0.3s ease-in-out;
    height: 0;
    overflow: hidden;
  }
  &__opened {
    padding: 10px;
    height: fit-content !important;
  }
}
</style>