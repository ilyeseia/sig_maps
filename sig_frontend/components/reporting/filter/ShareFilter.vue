<template>
  <form name="resetAutoComplete" @submit.prevent="onSubmit">
    <div class="modal-card" style="width: 800px; height: 75vh">
      <header class="modal-card-head">
        <h1 class="modal-card-title">Partage du Filtre</h1>
      </header>
      <section class="modal-card-body">
        <b-tabs id="user-tabs">
          <b-tab-item label="Utilisateurs">
            <b-field maped>
              <autocomplete
                field="username"
                placeholder="e.g. Admin"
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
              <button
                id="add-new-map-prop"
                class="button"
                type="button"
                @click="addUser"
                title="add new prop"
              >
                <b-icon pack="fas" icon="plus" />
              </button>
            </b-field>
            <b-table
              :data="getUsers"
              paginated
              backend-pagination
              :per-page="users_perPage"
              :total="users_count"
              :show-detail-icon="true"
              backend-sorting
              @page-change="onPageChangeUsers"
            >
              <template slot-scope="props">
                <b-table-column
                  field="username"
                  label="Nom d'utilisateur"
                  sortable
                  >{{ props.row.username }}</b-table-column
                >
                <b-table-column field="email" label="Email" sortable>{{
                  props.row.email
                }}</b-table-column>
                <b-table-column label="Avec permission" centered>
                  <b-checkbox
                    :disabled="props.row.filterClonedFrom != null"
                    v-model="usersWithPermisssions[props.row.email]"
                  ></b-checkbox>
                </b-table-column>
                <b-table-column
                  v-if="!props.row.filterClonedFrom"
                  label="Suppression"
                  centered
                >
                  <span class="btn-action" @click="deleteUser(props.row.id)">
                    <b-icon
                      pack="fas"
                      icon="times-circle"
                      style="cursor: pointer !important"
                    ></b-icon>
                  </span>
                </b-table-column>
              </template>
            </b-table>
             <input hidden type="reset" value="Reset the form" />
          </b-tab-item>
        </b-tabs>
      </section>
      <footer class="right-align modal-card-foot">
        <button
          id="close-map-form"
          class="button"
          type="button"
          @click="$parent.close()"
        >
          Fermer
        </button>
        <button
          id="save-map"
          type="submit"
          :class="['button', 'is-primary', { 'is-loading': isLoading }]"
        >
          Enregistrer
        </button>
      </footer>
    </div>
  </form>
</template>

<script>
import { backApi } from '~/methods/serverApi'
import AutoComplete from '~/mixins/auto-complete'
import UsersAutoComplete from '~//mixins/users-auto-complete'
import { mapState } from 'vuex'

import autocomplete from '../../ui/AutocComplete'

export default {
  mixins: [AutoComplete, UsersAutoComplete],
  components: {
    autocomplete,
  },
  props: ['filter'],
  data() {
    return {
      isLoading: false,
      usersWithPermisssions: {},
      users_perPage: 10,
      users_count: 0,
    }
  },
  computed: {
    ...mapState(['profile']),
  },
  methods: {
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
      backApi
        .post(
          `users/search`,
          payload,
          {
            params: {
              page: this.page - 1,
              limit: this.limit,
              sort: 'firstName',
              dir: 'asc',
            },
          }
        )
        .then(({ data }) => {
          this.totalPages =
            Math.ceil(data.totalElements / this.limit) > 0
              ? Math.ceil(data.totalElements / this.limit)
              : 1
          let newUsers = data.content.filter(
            (user) =>
              user.username !== this.profile.username &&
              this.users.filter((u) => u.username === user.username).length ==
                0 &&
              user
          )
          this.allUsers.push(...newUsers)
          if (newUsers.length == 0 && this.page <= this.totalPages) {
            callback()
            this.loadDataByFilter(keyword, callback)
          }
          callback()
        })
    },
    onSubmit() {
      let SharedFilter = {
        users: this.users
          .map((user) => {
            if ('toDelete' in user || 'isNew' in user) return user
            else if (
              this.usersWithPermisssions[user.email] &&
              user.filterClonedFrom == null
            ) {
              return { ...user, isNew: true }
            }
          })
          .filter((user) => user && user),
        filter: {
          ...this.filter,
          filterConfig: JSON.stringify(this.filter.filterConfig)
        },
        layerSlug: this.filter.filterConfig.layer.value,
        usersWithPermission: this.usersWithPermisssions,
      }
      this.save(SharedFilter)
    },
    save(SharedFilter) {
      this.isLoading = true
      backApi
        .post('filters/share', SharedFilter)
        .then((data) => {
          this.$emit('closeShareFilterModal')
          this.isLoading = false
          this.$notification.success({
            message: 'Success',
            description: 'Filtre partagé avec succès',
          })
        })
        .catch(() => {
          this.$emit('closeShareFilterModal')
          this.isLoading = false
          this.$notification.error({
            message: 'Erreur!',
            description: "L'opération de partage de couche a échoué",
          })
        })
    },
    grantPermission(email) {
      this.usersWithPermisssions[email] = true
    },
    onPageChangeUsers(page) {
      this.getUsersBelongToFilter(page - 1)
    },
    getUsersBelongToFilter(page) {
      backApi
        .get(`filters/${this.filter.id}/share-list`, {
          params: {
            page,
            limit: this.users_perPage,
            sort: 'firstName',
            dir: 'asc',
          },
        })
        .then(({ data }) => {
          this.users = data.content.filter(
            (user) => user.username !== this.profile.username
          )
          this.users_count = data.totalElements - 1
          this.users.map((u) => {
            this.usersWithPermisssions[u.email] =
              u.filterClonedFrom !== null ? true : false
          })
        })
        .catch(() => {
          getUsersBelongToFilter()
        })
    },
  },
  beforeMount() {
    this.getUsersBelongToFilter(0)
  },
}
</script>
