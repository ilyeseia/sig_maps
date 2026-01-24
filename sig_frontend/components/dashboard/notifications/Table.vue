<template>
  <section class="p-5 w-100">
    <div class="d-flex justify-content-between">
      <div>
        <button
          v-if="
            profile.roles.includes('ROLE_ADMIN') ||
            profile.roles.includes('SETTINGS_CREATE_AUTHORITY')
          "
          id="add-new-notification"
          class="button is-primary mb-2"
          @click="$emit('newNotification')"
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
      <template #title> notifications </template>
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
        >
          <template slot-scope="props">
            <b-table-column field="layer" label="Layer" sortable>{{
              props.row.layer.name
            }}</b-table-column>
            <b-table-column field="template" label="Template" sortable>{{
              props.row.template
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
                {{ new Date(props.row.createDate).toLocaleDateString() }}
              </span>
            </b-table-column>
            <b-table-column field="edit" class="data-table__actions" label="Edition">
              <b-tooltip label="Modifier" type="is-dark" position="is-bottom">
                <Button
                  v-if="
                    profile.roles.includes('ROLE_ADMIN') ||
                    profile.roles.includes('SETTINGS_UPDATE_AUTHORITY')
                  "
                  icon="pi pi-pencil"
                  class="p-button-rounded p-button-success p-mr-2"
                  @click="$emit('editNotification', props.row.id)"
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
                  @click="deleteNotification(props.row.id)"
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
      searchName: null,
      sortField: 'layer',
      sortOrder: 'asc',
      defaultSortOrder: 'asc',
      perPage: 10,
      page: 1
    }
  },
  components: {
    Button,
    Panel
  },
  computed: {
    ...mapState(['notifications', 'users', 'profile']),
    totalRowsCount() {
      return this.notifications.totalRowsCount
    },
    tableData() {
      return this.notifications.notifications.map(
        ({ id, layer, template, createDate, createdBy }) => ({
          id,
          layer,
          template,
          createDate,
          createdBy
        })
      )
    }
  },
  watch: {
    searchName(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.searchNameRef.value) {
          this.filterNotifications()
        }
      }, 500)
    }
  },
  methods: {
    loadAsyncData() {
      RestApi.findAll(
        'notifications',
        {
          page: this.page - 1,
          limit: this.perPage,
          sort: this.sortField,
          dir: this.sortOrder
        },
        'notifications/set',
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
    filterNotifications() {
      let payload = {
        condition: 'and',
        rules: [
          {
            label: 'code',
            field: 'code',
            operator: 'ilike',
            type: 'string',
            value: this.searchName
          }
        ]
      }
      RestApi.findAllByCriteria(
        'notifications',
        payload,
        {},
        this.$store,
        this.$router,
        this.$notification
      )
    },
    deleteNotification(id) {
      this.$confirm({
        title: 'Êtes-vous sûr de supprimer cette notification?',
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
            'notifications',
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
  beforeMount() {
    this.loadAsyncData()
  }
}
</script>
