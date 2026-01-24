<template>
  <section class="p-5 w-100">
    <div class="d-flex justify-content-between row-column">
      <div>
        <button
          v-if="
            profile.roles.includes('ROLE_ADMIN') ||
            profile.roles.includes('SETTINGS_CREATE_AUTHORITY')
          "
          id="add-new-settingsType"
          class="button is-primary mb-2"
          @click="$emit('newSettingsType')"
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
      <template #title> types de paramètres </template>
      <template #default>
        <b-table
          admin-data-table
          :data="tableData"
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
          checkable
        >
          <template slot-scope="props">
            <b-table-column field="code" label="Code" sortable>{{
              props.row.code
            }}</b-table-column>
            <b-table-column field="description" label="Description" sortable>{{
              props.row.description
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
              class="data-table__actions"
              centered
            >
              <b-tooltip label="Modifier" type="is-dark" position="is-bottom">
                <Button
                  v-if="
                    profile.roles.includes('ROLE_ADMIN') ||
                    profile.roles.includes('SETTINGS_UPDATE_AUTHORITY')
                  "
                  icon="pi pi-pencil"
                  class="p-button-rounded p-button-success p-mr-2"
                  @click="$emit('editSettingsType', props.row.id)"
                />
              </b-tooltip>
              <b-tooltip label="Supprimer" type="is-dark" position="is-bottom">
                <Button
                  v-if="
                    profile.roles.includes('ROLE_ADMIN') ||
                    profile.roles.includes('SETTINGS_DELETE_AUTHORITY')
                  "
                  icon="pi pi-trash"
                  class="p-button-rounded p-button-danger"
                  @click="deleteSettingsType(props.row.id)"
                />
              </b-tooltip>
            </b-table-column>
          </template>
        </b-table>
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
  data() {
    return {
      searchName: '',
      sortField: 'createDate',
      sortOrder: 'desc',
      defaultSortOrder: 'desc',
      perPage: 10,
      page: 1,
    }
  },
  components: { Button, Panel },
  computed: {
    ...mapState(['settingsType', 'profile']),
    totalRowsCount() {
      return this.settingsType.totalRowsCount
    },
    tableData() {
      return this.settingsType.settingsType.map(
        ({
          id,
          code,
          description,
          createDate,
          createdBy,
          default_value,
          enabled,
        }) => ({
          id,
          code,
          description,
          createDate,
          createdBy,
          default_value,
          enabled,
        })
      )
    },
  },
  watch: {
    searchName(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.searchNameRef.value) {
          this.page = 1
          this.filterSettingsType()
        }
      }, 500)
    },
  },
  methods: {
    loadAsyncData() {
      RestApi.findAll(
        'settingsType',
        {
          page: this.page - 1,
          limit: this.perPage,
          sort: this.sortField,
          dir: this.sortOrder,
        },
        'settingsType/set',
        this.$store,
        this.$router,
        this.$notification
      )
    },
    onSort(field, order) {
      this.sortField = field
      this.sortOrder = order
      if (this.searchName === '') {
        this.loadAsyncData()
      } else {
        this.filterSettingsType()
      }
    },
    onPageChange(page) {
      this.page = page
      if (this.searchName === '') {
        this.loadAsyncData()
      } else {
        this.filterSettingsType()
      }
    },
    filterSettingsType() {
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
        'settingsType',
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
    deleteSettingsType(id) {
      this.$confirm({
        title: 'Êtes-vous sûr de supprimer ce type de paramètres?',
        content: 'Cette action supprimera le paramètre de la base de données.',
        okText: 'Oui',
        okType: 'danger',
        cancelText: 'Non',
        onOk: () => {
          this.$nextTick(() => {
            this.$nuxt.$loading.start()
          })
          RestApi.delete(
            'settingsType',
            id,
            this.$store,
            this.$router,
            this.$notification
          )
            .then((data) => {
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
