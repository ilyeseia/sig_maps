

<template>
  <section class="p-5 w-100">
    <div class="d-flex justify-content-between row-column">
      <div>
        <button
          id="add-new-notification"
          class="button is-primary mb-2"
          @click="exportCSV($event)"
        >
          <b-icon class="mr-1" pack="fas" icon="file-export"></b-icon>Exporter
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
      <template #title> sessions </template>
      <template #default>
        <div class="bg-white text-dark">
          <b-table
            ref="table"
            :data="tableData"
            paginated
            backend-pagination
            :per-page="perPage"
            :total="totalRowsCount"
            @page-change="onPage"
            backend-sorting
          >
            <template slot-scope="props">
              <b-table-column
                field="username"
                label="Nom d'utilisateur"
              
                >{{ props.row.userName }}</b-table-column
              >
              <b-table-column field="email" label="Email">{{
                props.row.email
              }}</b-table-column>
              <b-table-column field="status" label="Status">
                <span>
                  <a-avatar :style="props.row.color" :size="16" />
                  {{ props.row.tokenExpired ? 'Actif' : 'Fermer' }}
                </span>
              </b-table-column>
              <b-table-column field="close" label="Fermer la session" centered>
                <Button
                  icon="fas fa-lock"
                  class="p-button p-button-success p-mr-2"
                  @click="closeSession(props.row.userName)"
                />
              </b-table-column>
            </template>
          </b-table>
          <!-- <DataTable
            ref="table"
            :value="tableData"
            class="p-datatable-striped p-datatable-md"
            :lazy="true"
            :paginator="true"
            :rows="perPage"
            :loading="isLoading"
            :totalRecords="totalRowsCount"
            @page="onPage($event)"
          >
            <template #empty> Aucun resultat trouvé. </template>
            <Column field="username" header="Nom d'utilisateur">
              <template #body="slotProps">
                {{ slotProps.data.userName }}
              </template>
            </Column>

            <Column field="email" header="Email">
              <template #body="slotProps">
                {{ slotProps.data.email }}
              </template>
            </Column>

            <Column field="status" header="Status">
              <template #body="slotProps">
                <span>
                  <a-avatar :style="slotProps.data.color" :size="16" />
                  {{ slotProps.data.tokenExpired ? 'Actif' : 'Fermer' }}
                </span>
              </template>
            </Column>

            <Column field="close" header="Fermer la session">
              <template #body="slotProps">
                <Button
                  icon="fas fa-lock"
                  class="p-button p-button-success p-mr-2"
                  @click="closeSession(slotProps.data.userName)"
                />
              </template>
            </Column>
          </DataTable> -->
        </div>
      </template>
    </Panel>
  </section>
</template>
<script>
import { mapState } from 'vuex'
import { backApi } from '~/methods/serverApi'
import RestApi from '../../../../methods/api.js'

import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import InputText from 'primevue/inputtext'
import Dropdown from 'primevue/dropdown'
import Button from 'primevue/button'

import Panel from '../../../layout/Panel'

export default {
  layout: 'dashboard',
  data() {
    return {
      searchName: null,
      sortField: 'username',
      sortOrder: 'asc',
      defaultSortOrder: 'asc',
      perPage: 10,
      page: 0,
      totalRowsCount: 0,
      avatars: [],
      userActive: '',
      isLoading: false,
    }
  },
  components: {
    DataTable: DataTable,
    Column: Column,
    InputText: InputText,
    Dropdown: Dropdown,
    Button: Button,
    Panel,
  },
  computed: {
    ...mapState(['sessions', 'users', 'profile']),
    tableData() {
      return (
        this.sessions.sessions &&
        this.sessions.sessions.map(
          ({
            id,
            userName,
            avatar,
            email,
            enabled,
            roles,
            createdBy,
            createDate,
            tokenExpired,
          }) => ({
            id,
            avatar,
            userName,
            email,
            createDate,
            createdBy,
            enabled,
            roles,
            color: { backgroundColor: tokenExpired ? '#008000' : '#FF0000' },
            tokenExpired,
          })
        )
      )
    },
  },
  watch: {
    searchName(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.searchNameRef.value) {
          this.filterSessions()
        }
      }, 500)
    },
  },
  methods: {
    exportCSV() {
      this.$refs.table.exportCSV()
    },
    closeSession(userName) {
      backApi
        .delete(`sessions/${userName}`)
        .then(({ data }) => {
          this.loadAsyncData()
          this.$notification.success({
            message: 'Success! session closed',
          })
        })
        .catch((error) => {
          if (error.response && error.response.status === 401) {
            localStorage.removeItem('sigToken')
            localStorage.removeItem('refreshToken')
            this.$store.commit('profile/logout')
            this.$router.push('/')
          } else {
            this.$notification.error({
              message: 'Error!',
              description: error.message,
            })
          }
          reject(error)
        })
    },
    loadAsyncData() {
      RestApi.findAll(
        'sessions',
        {
          page: this.page,
          limit: this.perPage,
          sort: this.sortField,
          dir: this.sortOrder,
        },
        'sessions/set',
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
    onPage(page) {
      this.page = page
      this.loadAsyncData()
    },
    filterSessions() {
      let payload = {
        condition: 'or',
        rules: [
          {
            label: 'username',
            field: 'username',
            operator: 'ilike',
            type: 'string',
            value: this.searchName,
          },
          {
            label: 'email',
            field: 'email',
            operator: 'ilike',
            type: 'string',
            value: this.searchName,
          },
        ],
      }
      RestApi.findAllByCriteria(
        'sessions',
        payload,
        {
          page: this.page,
          limit: this.perPage,
          sort: this.sortField,
          dir: this.sortOrder,
        },
        this.$store,
        this.$router,
        this.$notification
      )
    },
  },
  beforeMount() {
    this.loadAsyncData()
  },
}
</script>
