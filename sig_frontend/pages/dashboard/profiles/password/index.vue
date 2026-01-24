<template>
  <div class="profile w-100 p-5">
    <Panel icon="fa fa-key" viewMode="normal">
      <template #title> Edition de mot de passe </template>
      <template #default>
        <ValidationObserver
          style="width: 100%"
          ref="observer"
          v-slot="{ passes }"
        >
          <section class="section">
            <ValidationProvider
              rules="required"
              name="Mot de passe actuel"
              v-slot="{ errors, valid }"
            >
              <b-field
                label="Current Password"
                validation-message="Veuillez remplir ce champ"
                :type="{ 'is-danger': errors[0], 'is-success': valid }"
                :message="errors"
              >
                <b-input
                  type="password"
                  v-model="currentPassword"
                  password-reveal
                ></b-input>
              </b-field>
            </ValidationProvider>

            <ValidationProvider
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
                validation-message="Veuillez remplir ce champ"
                :type="{ 'is-danger': errors[0], 'is-success': valid }"
                :message="errors"
              >
                <b-input
                  type="password"
                  v-model="newPassword"
                  placeholder="Le mot de passe doit etre entre 6 à 20 caractères avec au moins un chiffre, une lettre majuscule, une lettre minuscule et un symbole spécial (@ #. $%) exemple P@ssword1990"
                ></b-input>
              </b-field>
            </ValidationProvider>
            <ValidationProvider
              rules="required|confirmed:password"
              name="Password Confirmation"
              v-slot="{ errors, valid }"
            >
              <b-field
                label="Confirmez le mot de passe"
                validation-message="Veuillez remplir ce champ"
                :type="{ 'is-danger': errors[0], 'is-success': valid }"
                :message="errors"
              >
                <b-input
                  type="password"
                  v-model="newPasswordConfirmation"
                ></b-input>
              </b-field>
            </ValidationProvider>
            <div style="margin-top: 25px" class="buttons">
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
          </section>
        </ValidationObserver>
      </template>
    </Panel>
  </div>
</template>
<script>
import RestApi from '../../../../methods/api.js'
import { ValidationObserver, ValidationProvider } from 'vee-validate'
import '../vee-validate'
import { extend } from 'vee-validate'
import Panel from '~/components/layout/Panel'
import { regex } from 'vee-validate/dist/rules'
import pageTitle from '~/mixins/page-title'

extend('regex', regex)

export default {
  layout: 'dashboard',
  mixins: [pageTitle],
  components: {
    ValidationObserver,
    ValidationProvider,
    Panel,
  },
  data() {
    return {
      isLoading: false,
      currentPassword: '',
      newPassword: '',
      userId: null,
      newPasswordConfirmation: '',
      page: {
        title: 'Edition de mot de passe',
        hid: '',
        name: '',
        description: '',
      },
    }
  },
  methods: {
    submit() {
      this.isLoading = true
      let user = {
        userId: this.userId,
        oldPassword: this.currentPassword,
        newPassword: this.newPassword,
      }
      RestApi.changePassword(user)
        .then(() => {
          this.$emit('saved')
          this.isLoading = false

          this.$notification.success({
            message: 'Félication ',
            message: 'Votre mot de passe a été modifié avec succès',
          })
        })
        .catch((error) => {
          this.isLoading = false
          if (error.response) {
            if (error.response.status == '409') {
              this.$notification.error({
                message: 'Erreur! ',
                message: 'Votre mot de passe actuel est erroné',
              })
            } else {
              this.$notification.error({
                message: 'Erreur! ',
                description:
                  error.response && error.response.data
                    ? error.response.data
                    : "Une erreur inattendue s'est produite !",
              })
            }
          }
          this.$emit('cancel')
        })
    },
  },
  beforeMount() {
    RestApi.getCurrentUser().then(({ id }) => {
      this.userId = id
    })
  },
  mounted() {
    this.$nextTick(() => {
      this.$nuxt.$loading.start()

      setTimeout(() => this.$nuxt.$loading.finish(), 900)
    })
  },
}
</script>