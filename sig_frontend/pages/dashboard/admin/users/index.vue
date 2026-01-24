<template>
  <div>
    <UserTable
      v-if="!showUserModal"
      @newUser="onNewUser"
      @editUser="onEditUser"
      @resetPassword="OnResetPassword"
    />
    <NewUser
      @close="showUserModal = false"
      v-if="showUserModal"
      @saved="showUserModal = false"
      :userToEdit="userToEdit"
    />
  </div>
</template>
<script>
import { backApi } from '~/methods/serverApi'
import UserTable from '../../../../components/dashboard/admin/UserTable'
import NewUser from '../../../../components/dashboard/admin/NewUser'
import pageTitle from '~/mixins/page-title'

export default {
  layout: 'dashboard',
  mixins: [pageTitle],
  validate({ params, query, store }) {
    return (
      store.state.profile.roles.includes('ROLE_ADMIN') ||
      store.state.profile.roles.includes('USER_READ_AUTHORITY')
    )
  },
  data() {
    return {
      showUserModal: false,
      showGroupModal: false,
      userToEdit: null,
      groupToEdit: null,
      page: {
        title: "Gestion des utilisateurs",
        hid: '',
        name: '',
        description: '',
      },
    }
  },
  components: { UserTable, NewUser },
  methods: {
    onNewUser(){
      this.showUserModal = true; 
      this.userToEdit = null
    },
    onEditUser(_id) {
      this.userToEdit = this.$store.state.users.users.find(
        ({ id }) => id === _id
      )
      this.showUserModal = true
    },
    async generateDefaultPassword() {
      let password = null
      await backApi
        .post('users/generatePassword')
        .then(({ data }) => {
          password = data
        })
        .catch((error) => {})
      return password
    },
    resetPassword(newPassword, id) {
      return new Promise(function (resolve, reject) {
        backApi
          .post(`users/resetPassword/${id}`, newPassword)
          .then(({ data }) => {
            resolve(data)
          })
          .catch((error) => {
            reject(error)
          })
      })
    },
    OnResetPassword(_id) {
      let currentUser = this.$store.state.users.users.find(
        ({ id }) => id === _id
      )
      this.$confirm({
        title: 'Réinitialisation du password ',
        content: 'Confirmez vous la réinitialisation du password ?',
        okText: 'Yes',
        okType: 'danger',
        cancelText: 'No',
        onOk: () => {
          this.generateDefaultPassword()
            .then((res) => {
              let userId = currentUser.id
              let newPassword = res
              this.resetPassword(newPassword, userId)
                .then(() => {
                  this.$notification.success({
                    message: 'Le mot de passe a été bien réinitialisé',
                  })
                })
                .catch((error) => {
                  console.log(error.message)
                })
            })
            .catch((error) => {
              console.log(error.message)
            })
        },
        onCancel: () => {},
      })
    },
  },
}
</script>
