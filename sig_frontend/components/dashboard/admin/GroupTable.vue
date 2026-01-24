<template>
  <section class="p-5 w-100">
    <div class="d-flex justify-content-between row-column">
      <div>
        <button
          v-if="
            profile.roles.includes('ROLE_ADMIN') ||
            profile.roles.includes('GROUP_CREATE_AUTHORITY')
          "
          id="add-new-group "
          class="button mb-2 is-primary"
          @click="$emit('newGroup')"
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
      <template #title> groupes </template>
      <template #default>
        <b-table
          :data="tableData"
          paginated
          backend-pagination
          :per-page="perPage"
          :total="totalRowsCount"
          @page-change="onPageChange"
          backend-sorting
          :default-sort-direction="defaultSortOrder"
          :default-sort="[sortField, sortOrder]"
          @sort="onSort"
        >
          <template slot-scope="props">
            <b-table-column field="name" label="Nom" sortable centered>{{
              props.row.label
            }}</b-table-column>
            <b-table-column field="label" label="Code" sortable centered>{{
              props.row.name
            }}</b-table-column>
            <b-table-column
              field="description"
              label="Description"
              sortable
              centered
              >{{ props.row.description }}</b-table-column
            >
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
              centered
              class="data-table__actions"
              label="Actions"
            >
              <b-tooltip label="Voir" type="is-dark" position="is-bottom">
                <Button
                  v-if="
                    profile.roles.includes('ROLE_ADMIN') ||
                    profile.roles.includes('GROUP_UPDATE_AUTHORITY')
                  "
                  icon="pi pi-info"
                  class="p-button-rounded p-button-info p-mr-2"
                  @click="$emit('viewGroup', props.row.id)"
                />
              </b-tooltip>
              <b-tooltip label="Modifier" type="is-dark" position="is-bottom">
                <Button
                  v-if="
                    profile.roles.includes('ROLE_ADMIN') ||
                    profile.roles.includes('GROUP_UPDATE_AUTHORITY')
                  "
                  icon="pi pi-pencil"
                  class="p-button-rounded p-button-success p-mr-2"
                  @click="$emit('editGroup', props.row.id)"
                />
              </b-tooltip>
              <b-tooltip label="Supprimer" type="is-dark" position="is-bottom">
                <Button
                  v-if="
                    profile.roles.includes('ROLE_ADMIN') ||
                    profile.roles.includes('GROUP_DELETE_AUTHORITY')
                  "
                  icon="pi pi-trash"
                  class="p-button-rounded p-button-danger"
                  @click="deleteGroup(props.row.id)"
                />
              </b-tooltip>
            </b-table-column>
          </template>
          <template slot="detail" slot-scope="props">
            <div v-for="user in props.row.users" :key="user.id">
              <table class="table table-hover">
                <tr class="d-flex w-100 justify-content-around">
                  <td style="flex: 1">
                    <b>{{ user.username }}</b>
                  </td>
                  <td style="flex: 1">{{ user.email }}</td>
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
import RestApi from '../../../methods/api.js'
import Button from 'primevue/button'
import Panel from '../../layout/Panel'

export default {
  layout: 'dashboard',
  data() {
    return {
      searchName: null,
      sortField: 'name',
      sortOrder: 'asc',
      defaultSortOrder: 'asc',
      perPage: 10,
      page: 1,
    }
  },
  components: {
    Button,
    Panel,
  },
  computed: {
    ...mapState(['groups', 'profile']),
    totalRowsCount() {
      return this.groups.totalRowsCount
    },
    tableData() {
      return this.groups.groups.map(
        ({ id, name, label, description, users, createDate, createdBy }) => ({
          id,
          name,
          label,
          description,
          users,
          createDate,
          createdBy,
        })
      )
    },
  },
  watch: {
    searchName(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.searchNameRef.value) {
          this.page = 1
          this.filterGroups()
        }
      }, 500)
    },
  },
  methods: {
    loadAsyncData() {
      RestApi.findAll(
        'groups',
        {
          page: this.page - 1,
          limit: this.perPage,
          sort: this.sortField,
          dir: this.sortOrder,
        },
        'groups/set',
        this.$store,
        this.$router,
        this.$notification
      )
    },
    onSort(field, order) {
      this.sortField = field
      this.sortOrder = order
      this.filterGroups()
    },
    onPageChange(page) {
      this.page = page
      this.filterGroups()
    },
    filterGroups() {
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
        'groups',
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
    deleteGroup(id) {
      this.$confirm({
        title: 'Êtes-vous sûr de supprimer ce groupe?',
        content: 'Cette action supprimera le paramètre de la base de données.',
        okText: 'Oui',
        okType: 'danger',
        cancelText: 'Non',
        onOk: () => {
          this.$nextTick(() => {
            this.$nuxt.$loading.start()
          })
          RestApi.delete(
            'groups',
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
            .catch(() => {
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
