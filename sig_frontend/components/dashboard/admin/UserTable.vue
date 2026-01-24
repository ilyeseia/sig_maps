<template>
  <section class="section-wrapper p-5">
    <div id="addUserId" class="d-flex justify-content-between row-column">
      <div>
        <button
          v-if="
            profile.roles.includes('ROLE_ADMIN') ||
            profile.roles.includes('USER_CREATE_AUTHORITY')
          "
          id="add-new-user"
          class="button is-primary mb-2"
          @click="$emit('newUser')"
        >
          <b-icon class="mr-1" pack="fas" icon="plus"></b-icon>Nouveau
        </button>
      </div>
      <b-input
        v-model="searchGlobal"
        ref="searcGlobalRef"
        placeholder="Recherche globale.."
        type="search"
        icon="magnify"
      />
    </div>
    <Panel>
      <template #title> utilisateurs </template>
      <template #default>
        <div id="usersTableId">
          <DataTable
            class="p-datatable-customers p-datatable-responsive"
            :value="usersTable"
            :lazy="true"
            :paginator="true"
            :rows="10"
            :totalRecords="totalRowsCount"
            :loading="loading"
            @page="onPage($event)"
            @sort="onSort($event)"
          >
            <Column field="avatar" header="Avatar" :sortable="true">
              <template #body="slotProps">
                <img
                  :src="slotProps.data.avatar ? slotProps.data.avatar : avatar"
                  style="
                    height: 30px;
                    width: 30px;
                    border-radius: 50%;
                    border: 2px solid #ccc;
                  "
                />
              </template>
            </Column>
            <Column field="firstName" header="Nom" :sortable="true">
              <template #body="slotProps">
                <span class="p-column-title">Nom</span>
                {{ slotProps.data.firstName }}
              </template>
              <template #filter>
                <InputText
                  type="text"
                  v-model="firstName"
                  ref="firstNameRef"
                  class=""
                  placeholder="chercher par nom"
                />
              </template>
            </Column>
            <Column field="lastName" header="Prénom" :sortable="true">
              <template #body="slotProps">
                <span class="p-column-title">Prénom</span>
                {{ slotProps.data.lastName }}
              </template>
              <template #filter>
                <InputText
                  type="text"
                  v-model="lastName"
                  ref="lastNameRef"
                  class=""
                  placeholder="chercher par prénom"
                />
              </template>
            </Column>
            <Column
              field="username"
              header="Nom d'utilisateur"
              :sortable="true"
            >
              <template #body="slotProps">
                <span class="p-column-title">Nom d'utilisateur</span>
                {{ slotProps.data.username }}
              </template>
              <template #filter>
                <InputText
                  type="text"
                  v-model="username"
                  ref="usernameRef"
                  class=""
                  placeholder="chercher par nom d'utilisateur"
                />
              </template>
            </Column>
            <Column field="email" header="Email" :sortable="true">
              <template #body="slotProps">
                <span class="p-column-title">Email</span>
                {{ slotProps.data.email }}
              </template>
              <template #filter>
                <InputText
                  type="text"
                  v-model="email"
                  ref="emailRef"
                  class=""
                  placeholder="chercher par email"
                />
              </template>
            </Column>
            <Column field="enabled" header="Actvié" :sortable="true">
              <template #body="slotProps">
                <span class="p-column-title">Actvié</span>
                <span
                  :class="[
                    'tag',
                    { 'is-danger': slotProps.data.enabled == false },
                    { 'is-success': slotProps.data.enabled == true },
                  ]"
                >
                  {{ slotProps.data.enabled == true ? 'Oui' : 'Non' }}
                </span>
              </template>
              <template #filter>
                <InputText
                  type="text"
                  v-model="enabled"
                  @input="loadDataByFilter"
                  class=""
                  placeholder="chercher par activé"
                />
              </template>
            </Column>
            <Column field="createdBy" header="Propriétere" :sortable="true">
              <template #body="slotProps">
                <span class="p-column-title">Propriétere</span>
                {{ slotProps.data.createdBy }}
              </template>
              <template #filter>
                <InputText
                  type="text"
                  v-model="createdBy"
                  ref="createdByRef"
                  class=""
                  placeholder="chercher par propriétere"
                />
              </template>
            </Column>
            <Column
              field="createDate"
              header="Date de création"
              :sortable="true"
            >
              <template #body="slotProps">
                <span class="p-column-title">Date de création</span>
                {{ slotProps.data.createDate | moment('DD/MM/YYYY') }}
              </template>
              <template #filter>
                <InputText
                  type="text"
                  v-model="createDate"
                  ref="createDateRef"
                  class=""
                  placeholder="chercher par date"
                />
                <!--<Calendar v-model="value" selectionMode="range" :showTime="true" class="p-column-filter" placeholder="chercher par date"/>-->
              </template>
            </Column>
            <Column headerStyle="width: 150px" header="Actions">
              <template #body="slotProps">
                <div class="btn-action data-table__actions">
                  <span class="p-column-title">Actions</span>
                  <b-tooltip
                    label="Réinitialiser le mot de passe"
                    type="is-dark"
                    position="is-bottom"
                  >
                    <Button
                      v-if="
                        profile.roles.includes('ROLE_ADMIN') ||
                        profile.roles.includes('USER_UPDATE_AUTHORITY')
                      "
                      icon="pi pi-refresh"
                      class="p-button-rounded p-button-primary p-mr-2"
                      @click="$emit('resetPassword', slotProps.data.id)"
                    />
                  </b-tooltip>
                  <b-tooltip
                    label="Modifier"
                    type="is-dark"
                    position="is-bottom"
                  >
                    <Button
                      v-if="
                        profile.roles.includes('ROLE_ADMIN') ||
                        profile.roles.includes('USER_UPDATE_AUTHORITY')
                      "
                      icon="pi pi-pencil"
                      class="p-button-rounded p-button-success p-mr-2"
                      @click="$emit('editUser', slotProps.data.id)"
                    />
                  </b-tooltip>
                  <b-tooltip
                    label="Supprimer"
                    type="is-dark"
                    position="is-bottom"
                  >
                    <Button
                      v-if="
                        profile.roles.includes('ROLE_ADMIN') ||
                        profile.roles.includes('USER_DELETE_AUTHORITY')
                      "
                      icon="pi pi-trash"
                      class="p-button-rounded p-button-danger"
                      @click="deleteUser(slotProps.data.id)"
                    />
                  </b-tooltip>
                </div>
              </template>
            </Column>
            <!-- End Action Buttons  -->
          </DataTable>
        </div>
      </template>
    </Panel>
  </section>
</template>
<script>
import Button from 'primevue/button'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import InputText from 'primevue/inputtext'
import Calendar from 'primevue/calendar'

import { mapState } from 'vuex'
import RestApi from '~/methods/api'
import Panel from '../../layout/Panel'
import avatarImage from '@/assets/icons/user.png'
export default {
  layout: 'dashboard',
  data() {
    return {
      sortField: 'createDate',
      sortOrder: 'asc',
      perPage: 10,
      page: 0,
      loading: false,
      firstName: '',
      lastName: '',
      username: '',
      email: '',
      enabled: '',
      createdBy: '',
      createDate: '',
      searchGlobal: '',
      value: '',
      avatar: avatarImage,
    }
  },
  components: {
    Button,
    DataTable,
    Column,
    InputText,
    Calendar,
    Panel,
  },
  computed: {
    ...mapState(['users', 'profile']),
    totalRowsCount() {
      return this.users.totalRowsCount
    },
    usersTable() {
      return this.users.users.map(
        ({
          id,
          avatar,
          firstName,
          lastName,
          username,
          email,
          enabled,
          createdBy,
          createDate,
        }) => ({
          id,
          avatar,
          firstName,
          lastName,
          username,
          email,
          enabled,
          createdBy,
          createDate,
        })
      )
    },
  },
  watch: {
    searchGlobal(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.searcGlobalRef.value) {
          this.loadDataByFilter()
        }
      }, 500)
    },
    firstName(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.firstNameRef.value) {
          this.loadDataByFilter()
        }
      }, 500)
    },
    lastName(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.lastNameRef.value) {
          this.loadDataByFilter()
        }
      }, 500)
    },
    username(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.usernameRef.value) {
          this.loadDataByFilter()
        }
      }, 500)
    },
    email(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.emailRef.value) {
          this.loadDataByFilter()
        }
      }, 500)
    },
    createdBy(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.createdByRef.value) {
          this.loadDataByFilter()
        }
      }, 500)
    },
    createDate(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.createDateRef.value) {
          this.page = 1
          this.loadDataByFilter()
        }
      }, 500)
    },
  },
  methods: {
    loadAsyncData() {
      RestApi.findAll(
        'users',
        {
          page: this.page,
          limit: this.perPage,
          sort: this.sortField,
          dir: this.sortOrder,
        },
        'users/set',
        this.$store,
        this.$router,
        this.$notification
      )
    },
    loadDataByFilter() {
      let payload = {
        condition: this.searchGlobal === '' ? 'and' : 'or',
        rules: [
          {
            label: 'firstName',
            field: 'firstName',
            operator: 'ilike',
            type: 'string',
            value: this.firstName === '' ? this.searchGlobal : this.firstName,
          },
          {
            label: 'lastName',
            field: 'lastName',
            operator: 'ilike',
            type: 'string',
            value: this.lastName === '' ? this.searchGlobal : this.lastName,
          },
          {
            label: 'username',
            field: 'username',
            operator: 'ilike',
            type: 'string',
            value: this.username === '' ? this.searchGlobal : this.username,
          },
          {
            label: 'email',
            field: 'email',
            operator: 'ilike',
            type: 'string',
            value: this.email === '' ? this.searchGlobal : this.email,
          },
          {
            label: 'createdBy',
            field: 'createdBy',
            operator: 'ilike',
            type: 'string',
            value: this.createdBy === '' ? this.searchGlobal : this.createdBy,
          },
        ],
      }

      RestApi.findAllByCriteria(
        'users',
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
    onPage(event) {
      this.page = event.page
      this.loadDataByFilter()
    },
    onSort(event) {
      this.sortField = event.sortField
      if (event.sortOrder == 1) this.sortOrder = 'desc'
      else this.sortOrder = 'asc'

      this.loadDataByFilter()
    },
    resetFilter() {
      this.searchGlobal = ''
      this.username = ''
      this.lastName = ''
      this.firstName = ''
      this.email = ''
      this.createdBy = ''
    },
    deleteUser(id) {
      this.$confirm({
        title: 'Êtes-vous sûr de supprimer cet utilisateur?',
        content: "Cette action supprimera l'utilisateur de la base de données.",
        okText: 'Oui',
        okType: 'danger',
        cancelText: 'Non',
        onOk: () => {
          this.$nextTick(() => {
            this.$nuxt.$loading.start()
          })
          RestApi.delete(
            'users',
            id,
            this.$store,
            this.$router,
            this.$notification
          )
            .then(() => {
              this.resetFilter()
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

<style lang="scss">
.p-datatable-wrapper .p-filter-column {
  position: relative;
  background-color: 'red';
  overflow: hidden;
  input {
    width: 100%;
  }
}
#usersTableId {
  margin-top: 1rem;
  overflow: auto !important;
  @include respond('tab-port') {
    overflow: visible !important;
  }
}
#usersTableId .p-datatable table {
  position: relative;
  border-collapse: collapse;
  width: 100%;
  table-layout: fixed !important;
}
#usersTableId tbody > tr > td {
  line-height: 1.5;
  word-wrap: break-word;
}
#usersTableId tbody > tr > td:hover {
  text-overflow: unset !important;
  white-space: normal;
}
.p-datatable .p-datatable-thead > tr > th {
  width: 210px !important;
}
</style>
