<template>
  <section class="p-5 w-100">
    <div class="d-flex justify-content-between">
      <div>
        <button
          class="button is-primary mb-2"
          v-if="
            profile.roles.includes('ROLE_ADMIN') ||
            profile.roles.includes('RESOURCE_CREATE_AUTHORITY')
          "
          id="add-new-resource"
          @click="$emit('newResource')"
        >
          <b-icon class="mr-1" pack="fas" icon="plus"></b-icon>Nouveau    
        </button>
      </div>

      <div class="d-flex justify-content-around">
        <b-input
          v-model="searchName"
          ref="searchNameRef"
          placeholder="Rechercher ici.."
          type="search"
          icon="magnify"
        />
      </div>
    </div>
    <Panel>
      <template #title> référentiels </template>
      <template #default>
            <b-table
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
      detailed
    >
      <template slot-scope="props">
        <b-table-column field="name" label="Nom" sortable>{{
          props.row.name
        }}</b-table-column>
        <b-table-column field="code" label="Code" sortable>{{
          props.row.code
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
            {{ props.row.createDate  | moment('DD/MM/YYYY') }}
          </span>
        </b-table-column>
        <b-table-column class="data-table__actions" field="edit" label="Actions">
          <b-tooltip label="Modifier" type="is-dark" position="is-bottom">
            <Button
              v-if="
                profile.roles.includes('ROLE_ADMIN') ||
                profile.roles.includes('RESOURCE_UPDATE_AUTHORITY')
              "
              icon="pi pi-pencil"
              class="p-button-rounded p-button-success p-mr-2"
              @click="$emit('editResource', props.row.id)"
            />
          </b-tooltip>
          <b-tooltip label="Supprimer" type="is-dark" position="is-bottom">
            <Button
              v-if="
                profile.roles.includes('ROLE_ADMIN') ||
                profile.roles.includes('RESOURCE_DELETE_AUTHORITY')
              "
              icon="pi pi-trash"
              class="p-button-rounded p-button-danger"
              @click="deleteResource(props.row.id)"
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
import RestApi from '../../../methods/api.js'
import Button from 'primevue/button'
import Panel from '../../layout/Panel'

export default {
  layout: 'dashboard',
  components: {
    Button,
    Panel
  },
  data() {
    return {
      searchName: null,
      sortField: 'name',
      sortOrder: 'asc',
      defaultSortOrder: 'asc',
      perPage: 10,
      page: 1
    }
  },
  methods: {
    loadAsyncData() {
      RestApi.findAll(
        'resources',
        {
          page: 0,
          limit: this.perPage,
          sort: this.sortField,
          dir: this.sortOrder
        },
        'resources/set',
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
      this.loadAsyncData()
    },
    filterResources() {
      let payload = {
        condition: 'and',
        rules: [
          {
            label: 'name',
            field: 'name',
            operator: 'ilike',
            type: 'string',
            value: this.searchName
          }
        ]
      }
      RestApi.findAllByCriteria(
        'resources',
        payload,
        {},
        this.$store,
        this.$router,
        this.$notification
      )
    },
    deleteResource(id) {
      this.$confirm({
        title: 'Êtes-vous sûr de supprimer cette ressource?',
        content: 'Cette action supprimera le paramètre de la base de données.',
        okText: 'Oui',
        okType: 'danger',
        cancelText: 'Non',
        onOk: () => {
          this.$nextTick(() => {
            this.$nuxt.$loading.start()

            setTimeout(() => this.$nuxt.$loading.finish(), 900)
          })
          RestApi.delete(
            'resources',
            id,
            this.$store,
            this.$router,
            this.$notification
          )
            .then(() => {
              this.loadAsyncData()
              this.$nuxt.$loading.finish()
            })
            .catch(error => {
              console.log(error)
            })
        },
        onCancel: () => {}
      })
    }
  },
  watch: {
    searchName(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.searchNameRef.value) {
          this.filterResources()
        }
      }, 500)
    }
  },
  computed: {
    ...mapState(['resources', 'profile']),
    totalRowsCount() {
      return this.resources.totalRowsCount
    },
    tableData() {
      return this.resources.resources.map(
        ({ id, code, name, resourceValues, createDate, createdBy }) => ({
          id,
          code,
          name,
          createDate,
          createdBy,
          resourceValues
        })
      )
    }
  },
  beforeMount() {
    this.loadAsyncData()
  }
}
</script>
