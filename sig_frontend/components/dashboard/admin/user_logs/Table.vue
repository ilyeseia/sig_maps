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
      <template #title> logs </template>
      <template #default>
        <div class="bg-white text-dark m-1">
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
              <b-table-column field="username" label="Nom d'utilisateur">{{
                props.row.username
              }}</b-table-column>
              <b-table-column field="browserName" label="Nom du navigateur">{{
                props.row.browserName
              }}</b-table-column>
              <b-table-column
                field="browserVersion"
                label="Version du navigateur"
                >{{ props.row.browserVersion }}</b-table-column
              >
              <b-table-column field="loginDate" label="Date de connexion">
                {{
                  props.row.loginDate
                    ? $moment(props.row.loginDate).format(
                        'DD/MM/yyyy, HH:mm:ss'
                      )
                    : 'Non renseigné'
                }}
              </b-table-column>
              <b-table-column field="logoutDate" label="Date de déconnexion">
                {{
                  props.row.logoutDate
                    ? $moment(props.row.logoutDate).format(
                        'DD/MM/yyyy, HH:mm:ss'
                      )
                    : 'Non renseigné'
                }}
              </b-table-column>
              <b-table-column field="userIp" label="Adresse Ip">{{
                props.row.userIp
              }}</b-table-column>
            </template>
          </b-table>
        </div>
      </template>
    </Panel>
  </section>
</template>
<script>
import { mapState } from 'vuex'
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
      searchName: '',
      sortField: 'username',
      sortOrder: 'desc',
      defaultSortOrder: 'desc',
      perPage: 10,
      page: 1,
      isLoading: true,
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
    ...mapState(['logs']),
    totalRowsCount() {
      return this.logs.totalRowsCount
    },
    tableData() {
      return this.logs.logs
    },
  },
  watch: {
    searchName(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.searchNameRef.value) {
          this.page = 1
          if (newVal === '') {
            this.loadAsyncData()
          } else {
            this.filterLogs()
          }
        }
      }, 500)
    },
  },
  methods: {
    exportCSV() {
      this.$refs.table.exportCSV()
    },
    loadAsyncData() {
      RestApi.findAll(
        'logs',
        {
          page: this.page - 1,
          limit: this.perPage,
          sort: this.sortField,
          dir: this.sortOrder,
        },
        'logs/set',
        this.$store,
        this.$router,
        this.$notification
      ).then(() => (this.isLoading = false))
    },
    onSort(field, order) {
      this.sortField = field
      this.sortOrder = order
      if (this.searchName === '') {
        this.loadAsyncData()
      } else {
        this.filterLogs()
      }
    },
    onPage(page) {
      this.page = page
      if (this.searchName === '') {
        this.loadAsyncData()
      } else {
        this.filterLogs()
      }
    },
    filterLogs() {
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
            label: 'browserName',
            field: 'browserName',
            operator: 'ilike',
            type: 'string',
            value: this.searchName,
          },
          {
            label: 'clientOS',
            field: 'clientOS',
            operator: 'ilike',
            type: 'string',
            value: this.searchName,
          },
        ],
      }
      RestApi.findAllByCriteria(
        'logs',
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
  },
  beforeMount() {
    this.loadAsyncData()
  },
}
</script>
<style lang="scss">
.p-datatable-striped {
  th span {
    line-height: 15px;
  }
  td {
    line-height: 15px;
  }
}
</style>
