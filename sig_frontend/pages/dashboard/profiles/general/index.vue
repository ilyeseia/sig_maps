<template>
  <div class="profile w-100 p-5">
    <Panel icon="fa fa-user" viewMode="normal">
      <template #title> Profile </template>
      <template #default>
        <ValidationObserver
          style="width: 100%"
          ref="observer"
          v-slot="{ passes }"
        >
          <section class="section">
            <input
              id="file-input"
              type="file"
              ref="file"
              accept="image/*"
              style="display: none"
              @change="loadAvatar()"
            />
            <b-field label="Avatar">
              <div style="cursor: pointer" @click="$refs.file.click()">
                <a-avatar :size="64" :src="avatar" />
              </div>
            </b-field>
            <b-field grouped class="row-column">
              <b-field label="Nom d'utilisateur" expanded>
                <b-input
                  id="user-name"
                  name="name"
                  type="text"
                  v-model="username"
                  placeholder="user name"
                  readonly
                ></b-input>
              </b-field>
              <b-field expanded>
                <ValidationProvider
                  rules="required|email"
                  name="Email"
                  v-slot="{ errors, valid }"
                >
                  <b-field
                    label="Email"
                    :type="{ 'is-danger': errors[0], 'is-success': valid }"
                    :message="errors"
                  >
                    <b-input type="email" v-model="email"></b-input>
                  </b-field>
                </ValidationProvider>
              </b-field>
            </b-field>
            <b-field grouped class="row-column">
              <b-field expanded>
                <ValidationProvider
                  rules="required"
                  name="firstName"
                  v-slot="{ errors, valid }"
                >
                  <b-field
                    :type="{ 'is-danger': errors[0], 'is-success': valid }"
                    :message="errors"
                    label="Nom"
                  >
                    <b-input
                      name="firstName"
                      type="text"
                      v-model="firstName"
                      placeholder="Nom"
                      required
                    ></b-input>
                  </b-field>
                </ValidationProvider>
              </b-field>
              <b-field expanded>
                <ValidationProvider
                  rules="required"
                  name="lastName"
                  v-slot="{ errors, valid }"
                >
                  <b-field
                    :type="{ 'is-danger': errors[0], 'is-success': valid }"
                    :message="errors"
                    label="Prénom"
                  >
                    <b-input
                      name="lastName"
                      type="text"
                      v-model="lastName"
                      placeholder="Prénom"
                      required
                    ></b-input>
                  </b-field>
                </ValidationProvider>
              </b-field>
            </b-field>
            <!-- show the groups&maps$layers of current user-->
            <div style="max-height: 500px; overflow: auto">
              <b-tabs id="profile-tabs">
                <b-tab-item label="Groupes">
                  <b-table :data="profile.groups">
                    <template slot-scope="props">
                      <b-table-column field="name" label="Nom">{{
                        props.row.name
                      }}</b-table-column>
                    </template>
                  </b-table>
                </b-tab-item>
                <b-tab-item label="Cartes">
                  <b-table :data="profile.maps">
                    <template slot-scope="props">
                      <b-table-column field="name" label="Nom" sortable>{{
                        props.row.name
                      }}</b-table-column>
                    </template>
                  </b-table>
                </b-tab-item>
                <!-- Start Permession  -->
                <b-tab-item label="Couches">
                  <b-table :data="profile.layers">
                    <template slot-scope="props">
                      <b-table-column field="name" label="Nom" sortable>{{
                        props.row.name && props.row.name.includes('____')
                          ? props.row.name.split('____')[1]
                          : props.row.name
                      }}</b-table-column>
                    </template>
                  </b-table>
                </b-tab-item>
                <!-- End Permesssion  -->
              </b-tabs>
            </div>
            <div class="buttons">
              <button
                id="save-resource"
                @click="passes(submit)"
                :class="['button', 'is-primary', { 'is-loading': isLoading }]"
              >
                <span class="icon is-small">
                  <i class="fas fa-check"></i>
                </span>
                <span>Enregistrer</span>
              </button>
            </div>
          </section>
        </ValidationObserver>
      </template>
    </Panel>
  </div>
</template>
<script>
import { backend } from '../../../../constants'
import RestApi from '../../../../methods/api.js'
import * as icons from '@/assets/icons'
import AvatarUpload from '../../../../components/dashboard/admin/AvatarUpload'
import { ValidationObserver, ValidationProvider } from 'vee-validate'
import '../vee-validate'
import { mapState } from 'vuex'
import Panel from '~/components/layout/Panel'
import pageTitle from '~/mixins/page-title'

export default {
  layout: 'dashboard',
  components: {
    AvatarUpload,
    ValidationObserver,
    ValidationProvider,
    Panel,
  },
  mixins: [pageTitle],
  data() {
    return {
      isLoading: false,
      user: {},
      username: '',
      email: '',
      firstName: '',
      lastName: '',
      avatar: null,
      page: {
        title: '',
        hid: '',
        name: '',
        description: '',
      },
    }
  },
  computed: {
    ...mapState(['profile']),
  },
  mounted() {
    this.$nextTick(() => {
      this.$nuxt.$loading.start()

      setTimeout(() => this.$nuxt.$loading.finish(), 900)
    })
  },
  methods: {
    async loadAvatar() {
      let file = this.$refs.file.files[0]
      let fileName = await RestApi.saveFileInFolder('photo', file)
      this.avatar = `${backend}/download/photo/${fileName}`
    },
    submit() {
      const newUser = {}

      this.isLoading = true

      newUser.username = this.username
      newUser.email = this.email
      newUser.avatar = this.avatar
      newUser.firstName = this.firstName
      newUser.lastName = this.lastName

      this.saveUser(newUser)
    },
    saveUser(user) {
      RestApi.createOrUpdate(
        'users/update-profile',
        user,
        this.user,
        this.$store,
        this.$router,
        this.$notification
      )
        .then((data) => {
          this.$emit('saved')
          this.isLoading = false
        })
        .catch(() => {
          this.isLoading = false
        })
    },
  },
  beforeMount() {
    RestApi.getCurrentUser().then((user) => {
      this.user = user
      this.username = user.username
      this.email = user.email
      this.avatar = user.avatar
      this.firstName = user.firstName
      this.lastName = user.lastName
      this.page.title = `${user.firstName} ${user.lastName}`
      if (!this.avatar) {
        this.avatar = icons.default['user']
      }
    })
  },
}
</script>
<style lang="scss">
.profile .is-success {
  background-color: white !important;
}
.has-text-success {
  color: $color-primary !important;
}
</style>