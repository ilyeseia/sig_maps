<template>
  <div style="padding: 0 1rem" class="h-100">
    <ValidationObserver ref="observer" v-slot="{ passes }">
      <section class="section new-user">
        <input
          id="file-input"
          type="file"
          ref="file"
          accept="image/*"
          style="display: none"
          @change="loadAvatar()"
        />
        <b-field label="Avatar">
          <div @click="$refs.file.click()">
            <a-avatar style="cursor: pointer" :size="64" :src="avatar" />
          </div>
        </b-field>

        <b-field grouped class="row-column">
          <b-field expanded>
            <ValidationProvider
              rules="required"
              name="FirstName"
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
                  v-on:blur="generateUsername()"
                ></b-input>
              </b-field>
            </ValidationProvider>
          </b-field>
          <b-field expanded>
            <ValidationProvider
              rules="required"
              name="LastName"
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
                  v-on:blur="generateUsername()"
                ></b-input>
              </b-field>
            </ValidationProvider>
          </b-field>
        </b-field>
        <b-field grouped class="row-column">
          <b-field label="Téléphone" expanded>
            <b-input
              name="homePhone"
              type="text"
              v-model="homePhone"
              placeholder="Téléphone"
              v-cleave="masks.fixe"
            ></b-input>
          </b-field>
          <b-field label="Mobile" expanded>
            <b-input
              name="mobile"
              type="text"
              v-model="mobile"
              placeholder="Mobile"
              v-cleave="masks.mobile"
            ></b-input>
          </b-field>
          <b-field label="Fax" expanded>
            <b-input
              name="fax"
              type="text"
              v-model="fax"
              placeholder="Fax"
              v-cleave="masks.fixe"
            ></b-input>
          </b-field>
        </b-field>

        <b-field grouped class="row-column">
          <b-field expanded>
            <ValidationProvider
              rules="required"
              name="UserName"
              v-slot="{ errors, valid }"
            >
              <b-field
                :type="{ 'is-danger': errors[0], 'is-success': valid }"
                :message="errors"
                label="Nom d'utilisateur"
              >
                <b-input
                  name="username"
                  type="text"
                  v-model="username"
                  placeholder="Nom d'utilisateur"
                  required
                ></b-input>
              </b-field>
            </ValidationProvider>
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
                <b-input
                  type="email"
                  v-model="email"
                  placeholder="Email"
                  required
                ></b-input>
              </b-field>
            </ValidationProvider>
          </b-field>
        </b-field>

        <b-field grouped class="row-column">
          <b-field expanded>
            <ValidationProvider
              v-if="!userToEdit"
              :rules="{
                required: true,
                regex: /((?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$.%]).{6,20})/,
              }"
              vid="password"
              name="Password"
              v-slot="{ errors, valid }"
            >
              <b-field
                label="Mot de passe"
                :type="{ 'is-danger': errors[0], 'is-success': valid }"
                :message="errors"
              >
                <b-input
                  type="password"
                  v-model="password"
                  placeholder="Entre 6 à 20 caractères avec au moins un chiffre, une lettre majuscule, une lettre minuscule et un symbole spécial (@ #. $%) exemple P@ssword1990"
                  password-reveal
                ></b-input>
              </b-field>
            </ValidationProvider>
          </b-field>
          <b-field label="Mot de passe par défaut" v-if="!userToEdit">
            <button
              id="btn-rest"
              :class="['button', 'is-success', { 'is-loading': isGenerated }]"
              type="button"
              @click="generatePassword"
            >
              Génerer mot de passe
            </button>
          </b-field>
        </b-field>
        <b-field expanded>
          <ValidationProvider
            v-if="!userToEdit"
            rules="required|confirmed:password"
            name="Confirmez le mot de passe"
            v-slot="{ errors, valid }"
          >
            <b-field
              label="Confirm Password"
              :type="{ 'is-danger': errors[0], 'is-success': valid }"
              :message="errors"
            >
              <b-input type="password" v-model="passwordConfirmation"></b-input>
            </b-field>
          </ValidationProvider>
        </b-field>
        <b-field grouped class="row-column">
          <b-field label="Date Activation" expanded>
            <b-datetimepicker
              id="activationDate"
              v-model="activationDate"
              placeholder="Click to select..."
              icon="calendar-today"
            >
            </b-datetimepicker>
          </b-field>
          <b-field label="Date Expiration" expanded>
            <b-datetimepicker
              id="desactivationDate"
              v-model="desactivationDate"
              placeholder="Click to select..."
              icon="calendar-today"
            >
            </b-datetimepicker>
          </b-field>
        </b-field>
        <b-field label="Utilisateur actif" v-if="!isAdmin">
          <b-switch v-model="enabled" true-value="true" false-value="false">
          </b-switch>
        </b-field>
        <b-field label="Groupes assignés" v-if="!isAdmin">
          <multi-select
            :options="getGroups"
            :selected-options="userGroups"
            @select="groupOnMultiSelect($event)"
          >
          </multi-select>
        </b-field>
        <div :key="divisionKey">
          <div
            v-for="index in Object.keys(selectedAdminList)"
            :key="index"
            class="
              affectation
              d-flex
              justify-content-between
              align-items-center
            "
          >
            <div>
              <b-field label="Limite adminstratives">
                <b-select
                  placeholder="Veuillez sélectionner une couche"
                  name="layer"
                  v-model="selectedAdminList[index]"
                  @input="getEntityElementBySelectedLayer"
                  required
                  validation-message="Ce champ est obligatoire"
                  expanded
                >
                  <option
                    v-for="l in getDivistion(selectedAdminList[index])"
                    :key="l.value.slug"
                    :value="l"
                  >
                    {{
                      l.text.includes('____') ? l.text.split('____')[1] : l.text
                    }}
                  </option>
                </b-select>
              </b-field>
            </div>
            <div
              v-if="
                selectedAdminList[index] != null &&
                entityElements[selectedAdminList[index].value.slug] != null
              "
            >
              <b-field label="Affectation" expanded>
                <multi-select
                  :options="entityElements[selectedAdminList[index].value.slug]"
                  :selected-options="
                    checkedDivisions[index] ? checkedDivisions[index] : []
                  "
                  @select="onMultiSelect($event, index)"
                >
                </multi-select>
              </b-field>
            </div>
            <div v-if="selectedAdminList[index]">
              <b-tooltip label="Supprimer" type="is-dark" position="is-bottom">
                <b-button
                  style="margin-top: 30px"
                  icon-left="close"
                  @click="deleteDivision(index)"
                ></b-button>
              </b-tooltip>
            </div>
          </div>
        </div>
        <b-button
          v-if="Object.keys(selectedAdminList).length < divisions.length"
          class="mt-3"
          icon-left="plus"
          @click="addDivision"
        >
          Ajouter
        </b-button>

        <div class="button-wrapper">
          <div class="buttons" style="margin: 0">
            <button
              :disabled="isLoading"
              :class="['button', 'is-secondary']"
              @click="getBack()"
            >
              <span class="icon is-small">
                <i class="fas fa-arrow-left"></i>
              </span>
              <span>Revenir</span>
            </button>
          </div>
          <div class="buttons" style="margin: 0">
            <button
              :class="['button', 'is-primary', { 'is-loading': isLoading }]"
              @click="passes(submit)"
            >
              <span class="icon is-small">
                <i class="fas fa-check"></i>
              </span>
              <span>Enregistrer</span>
            </button>
          </div>
        </div>
      </section>
    </ValidationObserver>
  </div>
</template>
<script>
import { backApi } from '~/methods/serverApi'
import { backend } from '../../../constants'
import RestApi from '../../../methods/api.js'
import { mapState } from 'vuex'
import * as icons from '@/assets/icons'
import AvatarUpload from './AvatarUpload'
import { ValidationObserver, ValidationProvider } from 'vee-validate'
import './vee-validate'
import { extend } from 'vee-validate'
import { regex, required } from 'vee-validate/dist/rules'
import { MultiSelect } from 'vue-search-select'
import 'vue-search-select/dist/VueSearchSelect.css'
import Cleave from 'cleave.js'
import groupBy from 'lodash/groupBy'

extend('regex', regex)
extend('required', required)
const cleave = {
  name: 'cleave',
  bind(el, binding) {
    const input = el.querySelector('input')
    input._vCleave = new Cleave(input, binding.value)
  },
  unbind(el) {
    const input = el.querySelector('input')
    input._vCleave.destroy()
  },
}

export default {
  layout: 'dashboard',
  components: {
    MultiSelect,
    AvatarUpload,
    ValidationObserver,
    ValidationProvider,
  },
  props: ['userToEdit'],
  directives: { cleave },
  data() {
    return {
      isLoading: false,
      avatar: null,
      isAdmin: false,
      adminRole: null,
      enabled: 'true',
      firstName: '',
      lastName: '',
      homePhone: null,
      mobile: null,
      fax: null,
      activationDate: new Date(),
      desactivationDate: null,
      username: '',
      password: null,
      passwordConfirmation: null,
      email: '',
      checkedDivisions: {},
      divisions: [],
      entityElements: {},
      isGenerated: false,
      selectedAdminList: {},
      divisionKey: 1,
      userGroups: [],
      masks: {
        fixe: {
          blocks: [0, 3, 1, 2, 3, 3],
          delimiters: ['+ ', ' (', ') ', '-', '-'],
          numericOnly: true,
        },
        mobile: {
          blocks: [0, 3, 1, 3, 3, 3],
          delimiters: ['+ ', ' (', ') ', '-', '-'],
          numericOnly: true,
        },
      },
      lastSelectItem: {},
    }
  },
  computed: {
    ...mapState(['groups']),
    getGroups() {
      return this.groups.groups.map(({ id, label }) => ({
        value: id,
        text: label,
      }))
    },
  },
  methods: {
    getDivistion(selectedLayer) {
      return selectedLayer
        ? [selectedLayer]
        : this.divisions.filter((d) => {
            return !Object.values(this.selectedAdminList).some((value) =>
              value ? value.value.slug === d.value.slug : false
            )
          })
    },
    deleteDivision(index) {
      this.$confirm({
        title: 'Êtes-vous sûr de supprimer cette affectation?',
        content: 'Cette action supprimera le paramètre de la base de données.',
        okText: 'Oui',
        okType: 'danger',
        cancelText: 'Non',
        onOk: () => {
          delete this.entityElements[this.selectedAdminList[index].value.slug]
          delete this.selectedAdminList[index]
          delete this.checkedDivisions[index]
          this.divisionKey++
        },
        onCancel: () => {},
      })
    },
    addDivision() {
      let tmpObject = {}
      tmpObject[
        Math.ceil(Math.random() * 100) +
          Object.keys(this.selectedAdminList)[
            Object.keys(this.selectedAdminList).length - 1
          ]
      ] = null
      this.selectedAdminList = Object.assign(
        {},
        this.selectedAdminList,
        tmpObject
      )
    },
    onMultiSelect($event, index) {
      let tmpObject = {}
      tmpObject[index] = $event
      this.checkedDivisions = Object.assign(
        {},
        this.checkedDivisions,
        tmpObject
      )
    },
    groupOnMultiSelect($event) {
      this.userGroups = $event
    },
    getBack() {
      this.$emit('saved')
    },
    async loadAvatar() {
      let file = this.$refs.file.files[0]
      let fileName = await RestApi.saveFileInFolder('photo', file)
      this.avatar = `${backend}/download/photo/${fileName}`
    },
    submit() {
      const newUser = {}

      this.isLoading = true

      newUser.firstName = this.firstName
      newUser.lastName = this.lastName
      newUser.homePhone = this.homePhone
      newUser.mobile = this.mobile
      newUser.fax = this.fax
      newUser.activationDate = this.activationDate
      newUser.desactivationDate = this.desactivationDate

      newUser.enabled = this.enabled
      newUser.username = this.username
      newUser.email = this.email
      newUser.password = this.password
      newUser.avatar = this.avatar
      newUser.divisions = []
      newUser.groups = this.userGroups.map((g) => {
        return {
          id: g.value,
          name: g.text,
          description: '',
        }
      })

      Object.keys(this.checkedDivisions).forEach((k) => {
        newUser.divisions.push(...this.checkedDivisions[k])
      })

      this.saveUser(newUser)
    },
    generateUsername() {
      if (!this.userToEdit) {
        this.firstName = this.firstName.trim()
        this.lastName = this.lastName.trim()
        this.username =
          this.lastName.toLowerCase().trim().split(' ').join('') +
          '.' +
          this.firstName.toLowerCase().trim().split(' ').join('')
      }
    },
    getEntityElementBySelectedLayer($event) {
      if ($event.value && $event.value.identifiant !== null) {
        backApi
          .get(
            `entityelements/layers?layerid=${$event.value.id}&identifiant=${$event.value.identifiant}`
          )
          .then(({ data }) => {
            let tmpObject = {}
            tmpObject[$event.value.slug] = data.map((d) => {
              return {
                value: d.id,
                text: d.text,
              }
            })

            this.entityElements = Object.assign(
              {},
              this.entityElements,
              tmpObject
            )
          })
          .catch((error) => {})
      } else {
        this.$notification.warning({
          message: 'Remarque!',
          description: `S'il vous plaît définir un identifiant pour la couche ${$event.value.name} !`,
        })
      }
    },
    generatePassword() {
      this.isGenerated = true
      backApi
        .post('users/generatePassword')
        .then(({ data }) => {
          if (data != null) {
            this.password = data
            this.passwordConfirmation = data
            this.isGenerated = false
          }
        })
        .catch((error) => {
          this.isGenerated = false
        })
    },
    saveUser(user) {
      RestApi.createOrUpdate(
        'users',
        user,
        this.userToEdit,
        this.$store,
        this.$router,
        this.$notification
      )
        .then(() => {
          this.$parent.showUserModal = false
        })
        .catch((error) => {
          this.isLoading = false
        })
    },
  },
  beforeMount() {
    RestApi.findAll(
      'groups',
      {
        page: this.page,
        limit: -1,
        sort: this.sortField,
        dir: this.sortOrder,
      },
      'groups/set',
      this.$store,
      this.$router,
      this.$notification
    )
    backApi.get(`layers/findByTypeLimit/Admin`).then(({ data }) => {
      this.divisions = data.map((l) => {
        return {
          value: l,
          text: l.name,
        }
      })
      let index = null
      if (this.userToEdit && this.userToEdit.divisions.length > 0) {
        for (const [key, value] of Object.entries(
          groupBy(this.userToEdit.divisions, 'layerSlug')
        )) {
          index = Math.ceil(Math.random() * 100)
          this.checkedDivisions[index] = value

          this.selectedAdminList[index] = {
            value: {
              ...data.find((d) => d.slug === key),
            },
            text: data.find((d) => d.slug === key).name,
          }

          this.getEntityElementBySelectedLayer(this.selectedAdminList[index])
        }
      } else {
        let tmpObject = {}
        tmpObject[Math.ceil(Math.random() * 100)] = null
        this.selectedAdminList = Object.assign(
          {},
          this.selectedAdminList,
          tmpObject
        )
      }
    })
    if (this.userToEdit) {
      this.firstName = this.userToEdit.firstName
      this.lastName = this.userToEdit.lastName
      this.homePhone = this.userToEdit.homePhone
      this.mobile = this.userToEdit.mobile
      this.fax = this.userToEdit.fax
      if (this.userToEdit.activationDate != null)
        this.activationDate = new Date(this.userToEdit.activationDate)
      if (this.userToEdit.desactivationDate)
        this.desactivationDate = new Date(this.userToEdit.desactivationDate)
      this.avatar = this.userToEdit.avatar
      this.enabled = this.userToEdit.enabled
      this.username = this.userToEdit.username
      this.email = this.userToEdit.email
      this.userGroups = this.userToEdit.groups.map((g) => {
        return {
          value: g.id,
          text: g.name,
        }
      })
    }

    if (!this.avatar) {
      this.avatar = icons.default['user']
    }
  },
}
</script>

<style lang="scss">
.button-wrapper {
  margin-top: 2rem;
  display: flex;
  justify-content: space-between;
}
#btn-rest {
  background-color: $color-primary !important;
}
.new-user .is-success {
  background-color: transparent !important;
}
.new-user .has-text-success {
  color: $color-primary !important;
}
.affectation {
  > div:nth-child(1) {
    width: 30%;
  }
  > div:nth-child(2) {
    width: 65%;
  }
  @media screen and (max-width: 960px) {
    flex-direction: column;
    > div {
      width: 100% !important;
      margin-bottom: 1rem;
    }
  }
}
</style>
