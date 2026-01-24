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
                      mapToShare != null &&
                      props.row.username !== mapToShare.createdBy
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

                <b-table-column label="Supprission" centered>
                  <span
                    v-if="mapToShare != null && props.row.name !== 'Admingroup'"
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
          <!-- Start Permession  -->
          <b-tab-item label="Permissions">
            <b-field label="Nature de la map">
              <b-select
                placeholder="Selectioner une méthode de partage"
                v-model="selectedPermission"
                expanded
              >
                <option
                  v-for="permission in permissions"
                  :value="permission.type"
                  :key="permission.type"
                >
                  {{ permission.name }}
                </option>
              </b-select>
            </b-field>
            <b-field>
              <b-message type="is-warning">
                {{ permissions[selectedPermission].description }}
              </b-message>
            </b-field>

            <div>
              <div class="html-link">
                <div>
                  <b-field
                    class="html-link-field"
                    label="Lien Html"
                    v-if="selectedPermission === 'PUBLIC_WITH_LINK'"
                  >
                    <b-input id="htmlink" :value="getMapPublicLink" />
                  </b-field>
                </div>
                <div v-if="selectedPermission === 'PUBLIC_WITH_LINK'">
                  <b-field>
                    <button
                      @click="copyTextHtmlLink"
                      class="p-button-success btn-copyhtmllink"
                    >
                      <i class="fas fa-copy"></i>
                    </button>
                  </b-field>
                </div>
              </div>

              <div class="iframLink">
                <b-field
                  class="ifram"
                  label="iframe"
                  v-if="selectedPermission === 'PUBLIC_WITH_LINK'"
                >
                  <b-input
                    type="textarea"
                    :value="getiframeLink"
                    id="iframLink"
                  />
                </b-field>
                <div v-if="selectedPermission === 'PUBLIC_WITH_LINK'">
                  <button @click="copyTextIfram" class="p-button-success btn-copyhtmllink">
                    <i class="fas fa-copy"></i>
                  </button>
                </div>
              </div>
            </div>
          </b-tab-item>
          <!-- End Permesssion  -->
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
import { frontend } from '../../../constants'
import RestApi from '../../../methods/api.js'
import { mapState } from 'vuex'

import AutoComplete from '../../../mixins/auto-complete'
import UsersAutoComplete from '../../../mixins/users-auto-complete'
import GroupsAutoComplete from '../../../mixins/groups-auto-complete'

import autocomplete from '../../ui/AutocComplete'
import { backApi } from '../../../methods/serverApi'

export default {
  props: ['mapToShare'],
  components: {
    autocomplete,
  },
  mixins: [AutoComplete, UsersAutoComplete, GroupsAutoComplete],
  data() {
    return {
      isLoading: false,
      keepFirst: false,
      openOnFocus: false,
      name: '',
      selectedPermission: 'PRIVATE',
      permissions: {
        PRIVATE: {
          type: 'PRIVATE',
          name: 'Map en mode privée',
          description:
            'La map est seulemet visible pour les utilisateur du system ayant access a la map',
        },
        PUBLIC: {
          type: 'PUBLIC',
          name: 'Partager une map en public',
          description: 'Partager une map en public,tous le monde peut la voir)',
        },
        PUBLIC_WITH_LINK: {
          type: 'PUBLIC_WITH_LINK',
          name: 'Partager une map en public avec lien',
          description:
            'la map est visible pour les utilisteurs ayant le lien fournit',
        },
      },
    }
  },
  computed: {
    ...mapState('maps', ['currentMap']),
    getMapPublicLink() {
      if (this.mapToShare)
        return `${frontend}/dashboard/maps/public/${this.mapToShare.id}`
      else return ''
    },
    getiframeLink() {
      if (this.mapToShare)
        return `<iframe src="${frontend}/sharedmap/${this.mapToShare.slug}?control=true" allowtransparency="true" frameborder="0" scrolling="no" allowfullscreen mozallowfullscreen webkitallowfullscreen oallowfullscreen msallowfullscreen width="400" height="200"></iframe>`
      else return ''
    },
    filteredDataArray() {
      return this.allUsers.filter((user) => {
        return (
          user.username
            .toString()
            .toLowerCase()
            .indexOf(this.name.toLowerCase()) >= 0 &&
          !this.users.find((rm) => rm.id === user.id)
        )
      })
    },
  },
  methods: {
    switchTab(index) {
      if (this.mapToShare) {
        this.selectedPermission = this.mapToShare.privacy
        this.groups = this.mapToShare.users.content
        this.count = this.mapToShare.users.totalElements
      }
    },
    copyTextHtmlLink(e) {
      e.preventDefault()
      /* Get the text field */
      let copyText = document.getElementById('htmlink')
      /* Select the text field */
      copyText.select()
      copyText.setSelectionRange(0, 99999) /* For mobile devices */

      /* Copy the text inside the text field */
      document.execCommand('copy')
    },
    copyTextIfram(e) {
      e.preventDefault()
      /* Get the text field */
      let copyText = document.getElementById('iframLink')
      /* Select the text field */
      copyText.select()
      copyText.setSelectionRange(0, 99999) /* For mobile devices */

      /* Copy the text inside the text field */
      document.execCommand('copy')
    },
    onSubmit(e) {
      e.preventDefault()

      const newMap = { users: [], groups: [] }
      this.isLoading = true

      newMap.users = this.users.filter((user) => {
        if ('isNew' in user || 'toDelete' in user) {
          return user
        }
      })
      newMap.groups = this.groups
      newMap.privacy = this.selectedPermission
      this.saveMap(newMap)
    },
    saveMap(map) {
      RestApi.shareMap(map, this.mapToShare.id)
        .then((data) => {
          this.$emit('saved')
          this.isLoading = false
          this.$store.commit('maps/update', data)
          this.$notification.success({
            message: 'Success',
            description: "succès de l'opération de partage de la carte",
          })
        })
        .catch(() => {
          this.$emit('saved')
          this.$notification.error({
            message: 'Erreur!',
            description: "l'opération de partage de la carte a échoué",
          })
          this.isLoading = false
        })
    },
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
      backApi
        .post('groups/simple/search', payload, {
          page: this.page - 1,
          limit: this.limit,
          sort: 'name',
          dir: 'asc',
        })
        .then(({ data }) => {
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
      backApi
        .post('users/simple/search', payload, {
          page: this.page - 1,
          limit: this.limit,
          sort: 'firstName',
          dir: 'asc',
        })
        .then(({ data }) => {
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
  },
  beforeMount() {
    if (this.mapToShare) {
      this.selectedPermission = this.mapToShare.privacy
      this.users = this.mapToShare.users.content
      this.users_count = this.mapToShare.users.totalElements
      this.groups = this.mapToShare.groups.content
      this.groups_count = this.mapToShare.groups.totalElements
    }
  },
}
</script>

<style scoped lang="scss">
form .btn-action {
  border: none;
  padding: 5px;
  width: 50px;
  border-radius: 3px;
  margin-top: 31px;
  margin-left: 19px;
}
</style>
