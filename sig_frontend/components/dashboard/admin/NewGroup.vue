<template>
  <form name="resetAutoComplete" @submit.prevent="onSubmit">
    <div class="modal-card" style="width: 700px; height: 75vh">
      <header class="modal-card-head">
        <h1 class="modal-card-title">
          {{ groupToEdit ? 'Edition de groupe' : 'Nouveau groupe' }}
        </h1>
      </header>
      <section class="modal-card-body">
        <b-field label="Nom">
          <b-input
            id="group-name"
            name="name"
            type="text"
            :value="groupToEdit ? groupToEdit.name : ''"
            placeholder="Nom de groupe"
            required
            validation-message="Veuillez remplir ce champ"
            :disabled="groupToEdit"
          ></b-input>
        </b-field>

        <!--<b-field label="Code">
          <b-input
            id="group-label"
            name="label"
            type="text"
            :value="groupToEdit ? groupToEdit.label : ''"
            placeholder=""
            required
          ></b-input>
        </b-field>-->

        <b-field label="Description">
          <b-input
            id="group-description"
            name="description"
            type="textarea"
            :value="groupToEdit ? groupToEdit.description : ''"
            placeholder="Description du groupe"
          ></b-input>
        </b-field>
        <template>
          <b-tabs v-model="activeTab">
            <b-tab-item label="Utilisateurs">
              <b-field maped>
                <autocomplete
                  ref="autocomplete"
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
                  id="add-new-group-prop"
                  class="button"
                  type="button"
                  @click="addUser('none')"
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
                  <b-table-column field="username" label="Username" sortable>{{
                    props.row.username
                  }}</b-table-column>
                  <b-table-column field="email" label="Email" sortable>{{
                    props.row.email
                  }}</b-table-column>
                  <b-table-column label="Suppression" centered>
                    <span
                      v-if="
                        groupToEdit != null &&
                        props.row.username != groupToEdit.createdBy
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
            <b-tab-item label="Permissions">
              <b-field maped>
                <autocomplete
                  field="label"
                  placeholder="e.g creer une champ"
                  :data="allPermissions"
                  :page="page"
                  :totalPages="totalPages"
                  @loadDataByFilter="findPermissionsByFilter"
                  @setSelected="setSelected"
                  @clear="clearData"
                  @setPage="setPage"
                  layout="permissions"
                >
                </autocomplete>
                <button
                  id="add-new-permission-prop"
                  class="button"
                  type="button"
                  @click="addPermission"
                  title="add new prop"
                >
                  <b-icon pack="fas" icon="plus" />
                </button>
              </b-field>
              <b-table
                :data="getPermissions"
                paginated
                backend-pagination
                :per-page="permissions_perPage"
                :total="permissions_count"
                :show-detail-icon="true"
                backend-sorting
                @page-change="onPageChangePermissions"
                @sort="onSortPermissions"
              >
                <template slot-scope="props">
                  <b-table-column field="label" label="Etiquette" sortable>{{
                    props.row.label
                  }}</b-table-column>
                  <b-table-column label="Suppression" centered>
                    <span
                      class="btn-action"
                      @click="deletePermission(props.row.id)"
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
        </template>
      </section>
      <footer class="right-align modal-card-foot">
        <button
          id="close-group-form"
          class="button"
          type="button"
          @click="$parent.close()"
        >
          Fermer
        </button>
        <button
          id="save-group"
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
import RestApi from '../../../methods/api.js'

import AutoComplete from '../../../mixins/auto-complete'
import UsersAutoComplete from '../../../mixins/users-auto-complete'
import permissionsAutoComplete from '../../../mixins/permissions-auto-complete'

import autocomplete from '../../ui/AutocComplete'

export default {
  props: ['groupToEdit'],
  components: {
    autocomplete
  },
  mixins: [AutoComplete, UsersAutoComplete, permissionsAutoComplete],
  data() {
    return {
      isLoading: false,
      keepFirst: false,
      openOnFocus: false,
      activeTab: 0
    }
  },
  methods: {  
    onSubmit(e) {
      const formData = new FormData(e.target)
      const newGroup = { users: [], permissions: [] }

      this.isLoading = true

      newGroup.name = this.groupToEdit ? this.groupToEdit.name : formData.get('name').trim()
      newGroup.label = this.groupToEdit ? this.groupToEdit.name : formData.get('name').trim()
      newGroup.description = formData.get('description')
      newGroup.users = this.users.filter(user => {
          if('isNew' in user || 'toDelete' in user){
            return user
          }
        })
      newGroup.permissions = this.permissions.filter((permission) => {
        if ('isNew' in permission || 'toDelete' in permission) {
          return permission
        }
      })
      this.saveGroup(newGroup)
    },
    saveGroup(group) {
      RestApi.createOrUpdate(
        'groups',
        group,
        this.groupToEdit,
        this.$store,
        this.$router,
        this.$notification
      )
        .then(data => {
          this.$emit('saved')
          this.isLoading = false
        })
        .catch(() => {
          this.$emit('saved')
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
            value: keyword === '' ? keyword : keyword
          },
          {
            label: 'lastName',
            field: 'lastName',
            operator: 'ilike',
            type: 'string',
            value: keyword === '' ? keyword : keyword
          }
        ]
      }
      RestApi.findAllByCriteria(
        'users',
        payload,
        {
          page: this.page - 1,
          limit: this.limit,
          sort: 'firstName',
          dir: 'asc'
        },
        this.$store,
        this.$router,
        this.$notification
      ).then(data => {
        this.totalPages =
          Math.ceil(data.totalElements / this.limit) > 0
            ? Math.ceil(data.totalElements / this.limit)
            : 1
        let newUsers = data.content.filter(
          user =>
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
    findPermissionsByFilter(keyword, callback) {
      let payload = {
        condition: 'and',
        rules: [
          {
            label: 'label',
            field: 'label',
            operator: 'ilike',
            type: 'string',
            value: keyword
          }
        ]
      }
      RestApi.findAllByCriteria(
        'permissions',
        payload,
        {
          page: this.page - 1,
          limit: this.limit,
          sort: 'label',
          dir: 'asc'
        },
        this.$store,
        this.$router,
        this.$notification
      ).then(data => {
        this.totalPages =
          Math.ceil(data.totalElements / this.limit) > 0
            ? Math.ceil(data.totalElements / this.limit)
            : 1
         let newPermissions = data.content.filter(
            (permission) =>
              this.permissions.filter((p) => {
              if ('toDelete' in p) {
                if (p.id === permission.id && !p.toDelete) {
                  return p
                }
              } else if (p.id === permission.id) {
                return p
              }
            }).length == 0 && permission
          )
        this.allPermissions.push(...newPermissions)
        if (newPermissions.length == 0 && this.page <= this.totalPages) {
          callback()
          this.loadDataByFilter(keyword, callback)
        }
        callback()
      })
    }
  },
  beforeMount() {
    if (this.groupToEdit) {
      this.users = this.groupToEdit.users.content
      this.users_count = this.groupToEdit.users.totalElements
      this.permissions = this.groupToEdit.permissions.content
      this.permissions_count = this.groupToEdit.permissions.totalElements
    }
  }
}
</script>
<style>
.right-align {
  display: flex;
  flex-direction: row;
  justify-content: flex-end;
}
</style>
