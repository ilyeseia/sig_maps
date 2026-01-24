import { backApi } from '~/methods/serverApi'
export default {
  data() {
    return {
      users: [],
      allUsers: [],
      users_count: 0,
      users_page: 1,
      users_perPage: 10,
      users_sortField: 'createDate',
      users_sortOrder: 'asc',
    }
  },
  computed: {
    getUsers() {
      return this.users.filter(user => {
        if ('toDelete' in user) {
          if (!user.toDelete) return user
        } else {
          return user
        }
      })
    }
  },
  methods: {
    loadUsersAsyncData(page) {
      let url =
        this.layerToShare != null
          ? `layers/layerSharedWithOthers/users/${this.layerToShare.id}`
          : this.mapToShare != null ? `maps/mapSharedWithOthers/users/${this.mapToShare.id}`
            : this.groupToEdit != null && `groups/groupSharedWithOthers/all/${this.groupToEdit.id}`

      backApi
        .get(url, {
          params: {
            page: page ? page : this.users_page - 1,
            limit: this.users_perPage,
            sort: this.users_sortField,
            dir: this.users_sortOrder
          }
        })
        .then(({ data }) => {
          data.users.content.forEach(user => {
            if (this.users.filter(u => u.id === user.id).length === 0) {
              this.users.push(user)
            }
          })
          if (typeof (page) === 'number') {
            this.users_count--
          }
        })
    },
    addUser() {
      if (
        this.selected &&
        this.users.filter(user => user.username == this.selected.username && !user.toDelete)
          .length == 0
      ) {
        this.users.unshift({ ...this.selected, isNew: true, toDelete: false })
      //  setTimeout(() => {
      //   this.allUsers = this.allUsers.filter(user => this.users.includes(user))
      //  }, 2000)
        this.users_count++
        document.forms["resetAutoComplete"].reset()
      }
    },
    deleteUser(id) {
      this.users = this.users.map(user => {
        if (user.id === id) {
          return { ...user, toDelete: true }
        } else {
          return user
        }
      })
      this.loadUsersAsyncData(this.users_page)
    },
    onSortUsers(field, order) {
      this.users_sortField = field
      this.users_sortOrder = order
      this.loadUsersAsyncData()
    },
    onPageChangeUsers(page) {
      this.users_page = page
      this.loadUsersAsyncData()
    }
  }
}
