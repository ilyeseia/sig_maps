<template>
  <section class="p-5 w-100">
    <Panel icon="fas fa-info" viewMode="normal">
      <template #title>Traçabilité</template>
      <template #default>
        <div class="audit">
          <section class="audit__filter-section">
            <div>
              <b-field label="Action">
                <b-select
                  expanded
                  placeholder="Chercher par action"
                  v-model="selectedAction"
                >
                  <option :value="null">Tout sélectionner</option>
                  <option value="CREATE">Creation</option>
                  <option value="EDIT">Modification</option>
                  <option value="DELETE">Suppression</option>
                </b-select>
              </b-field>
              <b-field label="Object">
                <b-select
                  expanded
                  placeholder="Chercher par objet"
                  v-model="selectedEntity"
                >
                  <option :value="null">Tout sélectionner</option>
                  <option v-for="e in entities" :key="e" :value="e">
                    {{ e }}
                  </option>
                </b-select>
              </b-field>
              <b-field label="Objet ID">
                <b-input
                  v-model="objectId"
                  ref="objectIdRef"
                  placeholder="Chercher par objet ID"
                  type="search"
                  class="mb-2"
                />
              </b-field>
              <b-field label="Utilisateur">
                <autocomplete
                  field="username"
                  placeholder="Chercher par utilisateur"
                  :data="allUsers"
                  :page="page"
                  :totalPages="totalPages"
                  @loadDataByFilter="loadDataByFilter"
                  @setSelected="setSelected"
                  @clear="clearData"
                  @setPage="setPage"
                  layout="users"
                >
                </autocomplete>
              </b-field>
              <b-field label="Adress IP">
                <b-input
                  v-model="addressIp"
                  ref="addressIpRef"
                  placeholder="Chercher par adress IP"
                  type="search"
                  class="mb-2"
                />
              </b-field>
            </div>
            <div>
              <!-- <span>
                <i class="fa fa-calendar" />
                Date entre
              </span> -->
              <b-field label="Date de débût">
                <b-datetimepicker
                  v-model="selectedStartDate"
                  placeholder="Date de débût"
                  icon="calendar-today"
                  locale="fr-FR"
                  editable
                >
                </b-datetimepicker>
              </b-field>
              <b-field label="Date de fin">
                <b-datetimepicker
                  v-model="selectedEndDate"
                  placeholder="Date de fin"
                  icon="calendar-today"
                  locale="fr-FR"
                  editable
                >
                </b-datetimepicker>
              </b-field>
              <button
                id="filter"
                :class="[
                  'button',
                  'is-primary',
                  'filter-btn',
                  'mb-2',
                  { 'is-loading': isLoading },
                ]"
                @click="loadAsyncData"
              >
                <b-icon class="mr-1" pack="fas" icon="filter"></b-icon>Filter
              </button>
            </div>
          </section>
          <div class="audit__table-wrapper">
            <DataTable
              class="p-datatable-responsive"
              :value="audit.auditRows"
              :lazy="true"
              :paginator="true"
              :rows="10"
              :totalRecords="audit.totalRowsCount"
              @page="onPageChange($event)"
              @sort="onSort($event)"
            >
              <Column
                headerStyle="width: 150px !important"
                field="action"
                header="Opération"
                :sortable="true"
              >
                <template #body="slotProps">
                  <span class="p-column-title">Opération</span>
                  <strong
                    :class="[
                      'tag',
                      { 'is-danger': slotProps.data.action == 'DELETE' },
                      { 'is-warning': slotProps.data.action == 'EDIT' },
                      { 'is-info': slotProps.data.action == 'CREATE' },
                    ]"
                  >
                    {{ getActionName(slotProps.data.action) }}
                  </strong>
                </template>
              </Column>
              <Column
                headerStyle="width: 150px !important"
                field="object"
                header="Objet"
                :sortable="true"
              >
                <template #body="slotProps">
                  <span class="p-column-title">Objet</span>
                  {{ slotProps.data.object }}
                </template>
              </Column>
              <Column
                headerStyle="width: 200px !important"
                field="objectId"
                header="Objet ID"
                :sortable="true"
              >
                <template #body="slotProps">
                  <span class="p-column-title">Objet ID</span>
                  {{ slotProps.data.objectId }}
                </template>
              </Column>
              <Column
                headerStyle="width: 150px !important"
                field="userName"
                header="Effectuer Par"
                :sortable="true"
              >
                <template #body="slotProps">
                  <span class="p-column-title">Effectuer Par</span>
                  {{ slotProps.data.userName }}
                </template>
              </Column>
              <Column
                headerStyle="width: 150px !important"
                field="ipAddress"
                header="Adress IP"
              >
                <template #body="slotProps">
                  <span class="p-column-title">Adress IP</span>
                  {{ slotProps.data.ipAddress }}
                </template>
              </Column>
              <Column
                headerStyle="width: 140px !important"
                field="createDate"
                header="Date d'action"
                :sortable="true"
              >
                <template #body="slotProps">
                  <span class="p-column-title">Date d'action</span>
                  <center>
                    {{
                      slotProps.data.createDate | moment('DD/MM/YYYY, HH:mm:ss')
                    }}
                  </center>
                </template>
                <template #filter> </template>
              </Column>
              <Column
                headerStyle="width: 50px !important"
                field="actions"
                header="Actions"
              >
                <template #body="slotProps">
                  <div class="btn-action data-table__actions">
                    <span class="p-column-title">Actions</span>
                    <b-tooltip
                      label="Voir les détails"
                      type="is-dark"
                      position="is-bottom"
                    >
                      <Button
                        icon="pi pi-info"
                        class="p-button-rounded p-button-info p-mr-2"
                        @click="$emit('viewInfo', slotProps.data)"
                      />
                    </b-tooltip>
                  </div>
                </template>
              </Column>
            </DataTable>
          </div>
        </div>
      </template>
    </Panel>
  </section>
</template>

<script>
import Button from 'primevue/button'
import RestApi from '../../../methods/api'
import Panel from '../../layout/Panel.vue'
import autocomplete from '../../ui/AutocComplete.vue'
import AutoComplete from '../../../mixins/auto-complete'
import UsersAutoComplete from '../../../mixins/users-auto-complete'
import { mapState } from 'vuex'

import DataTable from 'primevue/datatable'
import TabView from 'primevue/tabview'
import Column from 'primevue/column'
import moment from 'moment'
import { backApi } from '../../../methods/serverApi'

export default {
  mixins: [AutoComplete, UsersAutoComplete],
  components: {
    Panel,
    autocomplete,
    Button,
    DataTable,
    TabView,
    Column,
  },
  data() {
    return {
      selectedEntity: null,
      selectedAction: null,
      userName: null,
      selectedStartDate: null,
      selectedEndDate: null,
      sortField: 'createDate',
      sortOrder: 'desc',
      objectId: '',
      addressIp: '',
      perPage: 10,
      dataPage: 0,
      entities: [],
      isLoading: false,
    }
  },
  computed: {
    ...mapState(['audit']),
  },
  // watch: {
  //   addressIp(newVal) {
  //     setTimeout(() => {
  //       if (newVal === this.$refs.addressIpRef.value) {
  //         this.loadAsyncData()
  //       }
  //     }, 500)
  //   },
  //   objectId(newVal) {
  //     setTimeout(() => {
  //       if (newVal === this.$refs.objectIdRef.value) {
  //         this.loadAsyncData()
  //       }
  //     }, 500)
  //   },
  //   userName(newVal, oldVal) {
  //     if (newVal != oldVal) {
  //       this.loadAsyncData()
  //     }
  //   },
  //   selectedEntity(newVal, oldVal) {
  //     if (newVal != oldVal) {
  //       this.loadAsyncData()
  //     }
  //   },
  //   selectedAction(newVal, oldVal) {
  //     if (newVal != oldVal) {
  //       this.loadAsyncData()
  //     }
  //   },
  //   selectedStartDate(newVal, oldVal) {
  //     if (newVal != oldVal) {
  //       this.loadAsyncData()
  //     }
  //   },
  //   selectedEndDate(newVal, oldVal) {
  //     if (newVal != oldVal) {
  //       this.loadAsyncData()
  //     }
  //   },
  // },
  methods: {
    getActionName(action) {
      switch (action) {
        case 'CREATE':
          return 'CRÉATION'
        case 'EDIT':
          return 'MODIFICATION'
        case 'DELETE':
          return 'SUPPRESSION'
      }
    },
    loadAsyncData() {
      this.isLoading = true
      let payload = {
        condition: 'and',
        rules: [
          {
            label: 'ipAddress',
            field: 'ipAddress',
            operator: 'ilike',
            type: 'string',
            value: this.addressIp ? this.addressIp.trim() : '',
          },
          {
            label: 'objectId',
            field: 'objectId',
            operator: 'ilike',
            type: 'string',
            value: this.objectId ? this.objectId.trim() : '',
          },
          {
            label: 'userName',
            field: 'userName',
            operator: 'ilike',
            type: 'string',
            value: this.userName ? this.userName : '',
          },
          {
            label: 'object',
            field: 'object',
            operator: 'ilike',
            type: 'string',
            value: this.selectedEntity ? this.selectedEntity : '',
          },
          {
            label: 'action',
            field: 'action',
            operator: 'ilike',
            type: 'string',
            value: this.selectedAction ? this.selectedAction : '',
          },
          {
            label: 'createDate',
            field: 'createDate',
            operator:
              this.selectedStartDate && this.selectedEndDate
                ? '<>'
                : this.selectedStartDate && !this.selectedEndDate
                ? '>='
                : '<=',
            type: 'date',
            value: this.selectedStartDate
              ? this.selectedStartDate && this.selectedEndDate
                ? moment(this.selectedStartDate).format('YYYY-MM-DD H:mm:ss') +
                  '_' +
                  moment(this.selectedEndDate).format('YYYY-MM-DD H:mm:ss')
                : this.selectedStartDate && !this.selectedEndDate
                ? moment(this.selectedStartDate).format('YYYY-MM-DD H:mm:ss')
                : moment(this.selectedEndDate).format('YYYY-MM-DD H:mm:ss')
              : '',
          },
        ],
      }
      RestApi.findAllByCriteria(
        'audit',
        payload,
        {
          page: this.dataPage,
          limit: this.limit,
          sort: this.sortField,
          dir: this.sortOrder,
        },
        this.$store,
        this.$router,
        this.$notification
      )
        .then(() => (this.isLoading = false))
        .catch(() => (this.isLoading = false))
    },
    loadDataByFilter(keyword, callback) {
      let payload = {
        condition: 'or',
        rules: [
          {
            label: 'firstName',
            field: 'firstName',
            operator: 'ilike',
            type: 'string',
            value: keyword === '' ? keyword : keyword,
          },
          {
            label: 'lastName',
            field: 'lastName',
            operator: 'ilike',
            type: 'string',
            value: keyword === '' ? keyword : keyword,
          },
        ],
      }
      RestApi.findAllByCriteria(
        'users',
        payload,
        {
          page: this.page - 1,
          limit: this.limit,
          sort: 'firstName',
          dir: 'asc',
        },
        this.$store,
        this.$router,
        this.$notification
      ).then((data) => {
        this.totalPages =
          Math.ceil(data.totalElements / this.limit) > 0
            ? Math.ceil(data.totalElements / this.limit)
            : 1
        let newUsers = data.content.filter(
          (user) =>
            this.users.filter((u) => {
              if ('toDelete' in u) {
                if (u.username === user.username && !u.toDelete) {
                  return u
                }
              } else if (u.username === user.username) {
                return u
              }
            }).length == 0 && user
        )
        this.allUsers.push(...newUsers)
        if (newUsers.length == 0 && this.page <= this.totalPages) {
          callback()
          this.loadDataByFilter(keyword, callback)
        }
        callback()
      })
    },
    onSort(e) {
      this.sortField = e.sortField
      if (e.sortOrder == 1) this.sortOrder = 'desc'
      else this.sortOrder = 'asc'
      this.loadAsyncData()
    },
    onPageChange(e) {
      this.dataPage = e.page
      this.loadAsyncData()
    },
    setSelected(val) {
      this.userName = val && val.username.toString()
    },
  },
  mounted() {
    this.$nextTick(() => {
      this.$nuxt.$loading.start()
    })
    this.loadAsyncData()
    this.isLoading = false
    backApi.get('audit/entities').then(({ data }) => {
      let excluded = [
        'flyway_schema_history',
        'spatial_ref_sys',
        'user_logged_actions',
        'user_log',
        'user_notification',
        'notification',
        'job',
        'jobRunDetails'
      ]
      this.entities = data
        .filter((e) => !excluded.includes(e))
        .map((e) => {
          return e
            .split('_')
            .map((c, index) => {
              return index > 0 ? c.charAt(0).toUpperCase() + c.slice(1) : c
            })
            .join('')
        })
    })
  },
}
</script>
<style lang="scss">
.audit {
  &__table-wrapper {
    overflow: auto;
  }
  .p-datatable-thead tr:nth-child(2) th {
    display: none;
  }
  .p-datatable-tbody td {
    margin-top: 8px;
    text-align: center;
    vertical-align: middle;
  }
  &__filter-section {
    margin-top: 1rem;
    margin-bottom: 2rem;
    overflow: visible;
    > div {
      min-width: 100%;
      max-width: 100%;
      display: flex;
      flex-direction: row;
      flex-wrap: wrap;
      > div {
        min-width: 250px;
        width: 230px;
        margin-right: 1rem;
        @include respond('phone') {
          width: 100%;
          margin-right: 0;
        }
      }
      > span {
        margin-top: 5px;
        margin-right: 1rem;
        color: $color-primary;
        font-weight: bold;
        min-width: fit-content;
      }
    }

    > div {
      width: 180px !important;
      margin: 0 1rem;
      @include respond('tab-port') {
        width: 40%;
      }
      @include respond('phone') {
        width: 100%;
        margin: 0;
      }
      &:nth-child(2) {
        margin-bottom: 1rem;
      }
    }
  }
  .filter-btn {
    margin-top: 28px;
    @include respond('tab-port') {
      margin-top: 26px;
    }
    @include respond('phone') {
      margin-top: 1rem;
    }
  }
}
</style>