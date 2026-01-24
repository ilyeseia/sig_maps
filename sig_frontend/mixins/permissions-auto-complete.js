import { backApi } from '~/methods/serverApi'
export default {
  data() {
    return {
      permissions: [],
      allPermissions: [],
      permissions_count: 0,
      permissions_page: 1,
      permissions_perPage: 5,
      permissions_sortField: '',
      permissions_sortOrder: ''
    }
  },
  computed: {
    getPermissions(){
      return this.permissions.filter(permission => {
        if('toDelete' in permission){
          if(!permission.toDelete) return permission
        }else{
          return permission
        }
      })
    }
  },
  methods: {
    loadPermissionsAsyncData(page) {
      let url = `groups/groupSharedWithOthers/permissions/${
        this.groupToEdit.id
      }`
      backApi
        .get(url, {
          params: {
            page: page ? page : this.permissions_page - 1,
            limit: this.permissions_perPage,
            sort: this.permissions_sortField,
            dir: this.permissions_sortOrder
          }
        })
        .then(({ data }) => {
          data.permissions.content.forEach(permision => {
            if (this.permissions.filter(u => u.id === permision.id).length === 0) {
              this.permissions.push(permision)
            }
          })
          if (typeof (page) === 'number') {
            this.permissions_count--
          }
        })
    },
    addPermission() {
      if (
        this.selected &&
        this.permissions.filter(permission => permission.id == this.selected.id && !permission.toDelete)
          .length == 0
      ) {
        if (this.selected) this.permissions.unshift({...this.selected, isNew: true, toDelete: false})
        // this.allPermissions = this.allPermissions.filter(
        //   permission => !this.permissions.includes(permission)
        // )
        document.forms["resetAutoComplete"].reset()
        this.permissions_count++
      }
    },
    deletePermission(id) {
      this.permissions = this.permissions.map(permission => {
        if (permission.id === id) {
          return { ...permission, toDelete: true }
        }else{
          return permission
        }
      })
      this.loadPermissionsAsyncData(this.permissions_page)
    },
    onSortPermissions(field, order) {
      this.permissions_sortField = field
      this.permissions_sortOrder = order
      this.loadPermissionsAsyncData()
    },
    onPageChangePermissions(page) {
      this.permissions_page = page
      this.loadPermissionsAsyncData()
    }
  }
}
