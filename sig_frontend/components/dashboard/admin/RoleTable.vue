<template>
  <section class="p-5 w-100">
    <div class="d-flex justify-content-between">
      <div>
        <Button
          v-if="
            profile.roles.includes('ROLE_ADMIN') ||
            profile.roles.includes('USER_CREATE_AUTHORITY')
          "
          id="add-new-user"
          class="button mb-2"
          @click="$emit('newRole')"
        >
          Nouveau Role
        </Button>
      </div>
      <div>
        <span class="p-input-icon-right">
          <i class="pi pi-search" />
          <InputText
            v-model="searchText"
            placeholder="Recherche globale"
            @input="filterRoles"
            style="width: 500px"
          />
        </span>
      </div>
    </div>
    <div class="bg-white text-dark m-1 border border-info">
      <DataTable
        :value="tableData"
        class="p-datatable-striped p-datatable-md"
        :lazy="true"
        :paginator="true"
        :rows="10"
        :loading="loading"
        :totalRecords="totalRecords"
        @page="onPage($event)"
      >
        <div class="d-flex justify-content-between p-2"></div>
        <template #empty> Aucun resultat trouvé. </template>
        <Column field="label" header="Nom du Module">
          <template #body="slotProps">
            {{ slotProps.data.label }}
          </template>
        </Column>
        <Column field="name" header="Nom du role">
          <template #body="slotProps">
            {{ slotProps.data.name }}
          </template>
        </Column>
        <Column field="createdBy" header="Propriétere">
          <template #body="slotProps">
            {{ slotProps.data.createdBy }}
          </template>
        </Column>
        <Column field="createDate" header="Date de création">
          <template #body="slotProps">
            {{ new Date(slotProps.data.createDate).toLocaleDateString() }}
          </template>
        </Column>
        <Column>
          <template #body="slotProps">
            <Button
              icon="pi pi-pencil"
              class="p-button p-button-success p-mr-2"
              @click="$emit('editRole', slotProps.data.id)"
            />
            <Button
              icon="pi pi-trash"
              class="p-button p-button-danger"
              @click="$emit('deleteRole', slotProps.data.id)"
            />
          </template>
        </Column>
      </DataTable>
    </div>
  </section>
</template>

<script>
import { backApi } from '~/methods/serverApi'
import { mapState } from 'vuex'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import InputText from 'primevue/inputtext'
import Dropdown from 'primevue/dropdown'
import Button from 'primevue/button'


export default {
  layout: 'dashboard',
  data() {
    return {
      searchText: '',
      sortField: 'name',
      sortOrder: 'asc',
      defaultSortOrder: 'asc',
      perPage: 10,
      page: 0,
      totalRecords: 0,
    }
  },
  components: {
    DataTable: DataTable,
    Column: Column,
    InputText: InputText,
    Dropdown: Dropdown,
    Button: Button,
  },
  computed: {
    ...mapState(['roles', 'users']),
    tableData() {
      return this.roles.roles
    },
  },
  methods: {
    loadAsyncData() {
      this.loading = true
      backApi
        .get('roles', {
          params: {
            page: this.page,
            limit: this.perPage,
            sort: this.sortField,
            dir: this.sortOrder,
          },
        })
        .then(({ data }) => {
          this.totalRecords = data.totalElements
          this.$store.commit('roles/set', data.content)
          this.loading = false
        })
        .catch((error) => {
          this.loading = false
        })
    },
    onSort(field, order) {
      this.sortField = field
      this.sortOrder = order
      this.loadAsyncData()
    },
    onPage(event) {
      this.page = event.page
      this.loadAsyncData()
    },
    filterRoles() {
      let payload = {
        condition: 'or',
        rules: [
          {
            label: 'name',
            field: 'name',
            operator: 'ilike',
            type: 'string',
            value: this.searchText,
          },
          {
            label: 'label',
            field: 'label',
            operator: 'ilike',
            type: 'string',
            value: this.searchText,
          },
        ],
      }
      backApi.post("roles/search", {
        data: payload,
        params: {
          page: this.page,
          limit: this.perPage,
          sort: this.sortField,
          dir: this.sortOrder,
        }
      })
        .then(({ data }) => {
          if (data != null) {
            this.totalRecords = data.totalElements
            this.$store.commit('roles/set', data.content)
          }
        })
        .catch((error) => {})
    },
  },
  beforeMount() {
    this.loadAsyncData()
  },
}
</script>
