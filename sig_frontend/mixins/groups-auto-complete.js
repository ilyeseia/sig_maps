import { backApi } from '~/methods/serverApi'
export default {
  data() {
    return {
      groups: [],
      allGroups: [],
      groups_count: 0,
      groups_page: 1,
      groups_perPage: 10,
      groups_sortField: 'createDate',
      groups_sortOrder: 'asc'
    }
  },
  computed: {
    getGroups() {
      return  this.groups.filter(group => {
        if ('toDelete' in group) {
          if (!group.toDelete) return group
        } else {
          return group
        }
      })
    }
  },
  methods: {
    loadGroupsAsyncData(page) {
      let url =
        this.layerToShare != null
          ? `layers/layerSharedWithOthers/groups/${this.layerToShare.id}`
          : `maps/mapSharedWithOthers/groups/${this.mapToShare.id}`
      backApi
        .get(url, {
          params: {
            page: page ? page : this.groups_page - 1,
            limit: this.groups_perPage,
            sort: this.groups_sortField,
            dir: this.groups_sortOrder
          }
        })
        .then(({ data }) => {
          data.groups.content.forEach(group => {
            if (this.groups.filter(u => u.id === group.id).length === 0) {
              this.groups.push(group)
            }
          })
          if (typeof (page) === 'number') {
            this.groups_count--
          }
        })
    },
    addGroup() {
      if (
        this.selected &&
        this.groups.filter(group => group.name == this.selected.name &&  !group.toDelete).length == 0
      ) {
        if (this.selected) this.groups.unshift({...this.selected, isNew: true})
        // this.allGroups = this.allGroups.filter(
        //   group => !this.groups.includes(group)
        // )
        document.forms["resetAutoComplete"].reset()
        this.groups_count++
      }
    },
    deleteGroup(id) {
      this.groups = this.groups.map(group => {
        if (group.id === id) {
          return { ...group, toDelete: true }
        }else{
          return group
        }
      })
      this.loadGroupsAsyncData(this.groups_page)
    },
    onSortGroups(field, order) {
      this.groups_sortField = field
      this.groups_sortOrder = order
      this.loadGroupsAsyncData()
    },
    onPageChangeGroups(page) {
      this.groups_page = page
      this.loadGroupsAsyncData()
    }
  }
}
