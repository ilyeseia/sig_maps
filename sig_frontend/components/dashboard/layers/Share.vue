<template>
  <form name="resetAutoComplete" @submit.prevent="onSubmit">
    <div class="modal-card" style="height: 600px; width: 75vh">
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
              @sort="onSortUsers"
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
                <b-table-column label="Suppression" centered>
                  <span
                    v-if="
                      layerToShare != null &&
                      props.row.username !== layerToShare.createdBy
                    "
                    class="btn-action"
                    @click="deleteUser(props.row.id)"
                  >
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
          <b-tab-item label="Groupes">
            <b-field maped>
              <autocomplete
                field="name"
                placeholder="e.g. GroupAdmin"
                :data="allGroups"
                :page="page"
                :totalPages="totalPages"
                @loadDataByFilter="loadGroupsByFilter"
                @setSelected="setSelected"
                @clear="clearData"
                @setPage="setPage"
                layout="groups"
              >
              </autocomplete>
              <button
                id="add-new-group-map-prop"
                class="button"
                type="button"
                @click="addGroup"
                title="add new pgrouprop"
              >
                <b-icon pack="fas" icon="plus" />
              </button>
            </b-field>
            <b-table
              :data="getGroups"
              paginated
              backend-pagination
              :per-page="groups_perPage"
              :total="groups_count"
              :show-detail-icon="true"
              backend-sorting
              @page-change="onPageChangeGroups"
              @sort="onSortGroups"
            >
              <template slot-scope="props">
                <b-table-column field="name" label="Nom" sortable>{{
                  props.row.name
                }}</b-table-column>

                <b-table-column label="Suppression" centered>
                  <span
                    v-if="
                      layerToShare != null && props.row.name !== 'Admingroup'
                    "
                    class="btn-action"
                    @click="deleteGroup(props.row.id)"
                  >
                    <b-icon
                      pack="fas"
                      icon="times-circle"
                      style="cursor: pointer !important"
                    ></b-icon>
                  </span>
                </b-table-column>
              </template>
            </b-table>
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
import RestApi from '../../../methods/api'
import AutoComplete from '../../../mixins/auto-complete'
import UsersAutoComplete from '../../../mixins/users-auto-complete'
import GroupsAutoComplete from '../../../mixins/groups-auto-complete'

import autocomplete from '../../ui/AutocComplete'

export default {
  props: ['layerToShare'],
  components: {
    autocomplete,
  },
  mixins: [AutoComplete, UsersAutoComplete, GroupsAutoComplete],
  data() {
    return {
      isLoading: false,
      keepFirst: false,
      openOnFocus: false,
      username: '',
      groups: [],
      allGroups: [],
      groupname: '',
      name: '',
    }
  },
  methods: {
    loadGroupsByFilter(keyword, callback) {
      let payload = {
        condition: 'and',
        rules: [
          {
            label: 'name',
            field: 'name',
            operator: 'ilike',
            type: 'string',
            value: keyword,
          },
        ],
      }
      RestApi.findAllByCriteria(
        'groups',
        payload,
        {
          page: this.page - 1,
          limit: this.limit,
          sort: 'name',
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
        let newGroups = data.content.filter(
          (group) =>
            this.groups.filter((g) => {
              if ('toDelete' in g) {
                if (g.username === group.name && !g.toDelete) {
                  return g
                }
              } else if (g.name === group.name) {
                return g
              }
            }).length == 0 && group
        )
        this.allGroups.push(...newGroups)
        if (newGroups.length == 0 && this.page <= this.totalPages) {
          callback()
          this.loadGroupsByFilter(keyword, callback)
        }
        callback()
      })
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
    onSubmit() {
      let shareLayer = { users: [], groups: [] }
      if (this.layerToShare) {
        shareLayer.users = this.users.filter((user) => {
          if ('isNew' in user || 'toDelete' in user) {
            return user
          }
        })
        shareLayer.groups = this.groups.filter((group) => {
          if ('isNew' in group || 'toDelete' in group) {
            return group
          }
        })
      }
      this.save(shareLayer)
    },
    save(shareLayer) {
      this.isLoading = true
      RestApi.shareLayer(shareLayer, this.layerToShare.id)
        .then((data) => {
          this.$emit('saved')
          this.isLoading = false
          //this.$store.commit('layers/update', data)
          this.$notification.success({
            message: 'Success',
            description: 'Couche partagée avec succès',
          })
        })
        .catch(() => {
          this.$emit('saved')
          this.isLoading = false
          this.$notification.error({
            message: 'Erreur!',
            description: "L'opération de partage de couche a échoué",
          })
        })
    },
  },
  beforeMount() {
    if (this.layerToShare) {
      this.users = this.layerToShare.users.content
      this.users_count = this.layerToShare.users.totalElements
      this.groups = this.layerToShare.groups.content
      this.groups_count = this.layerToShare.groups.totalElements
    }
  },
}
</script>

<style scoped>
form .html-link {
  display: flex;
}
form .html-link .html-link-field {
  width: 640px;
  float: left;
}
form .btn-copyhtmllink {
  margin-top: 33px;
  margin-left: 20px;
  border: none;
  padding: 5px;
  width: 50px;
  border-radius: 3px;
}
form .iframLink {
  display: flex;
}
form .iframLink .ifram {
  width: 640px;
}
form .btn-action {
  border: none;
  padding: 5px;
  width: 50px;
  border-radius: 3px;
  margin-top: 31px;
  margin-left: 19px;
}
</style>
