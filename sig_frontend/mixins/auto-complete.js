
export default {
    data() {
        return {
            limit: 10,
            page: 1,
            totalPages: 1
        }
    },
    methods: {
        clearData(callback) {
            if(this.allUsers) this.allUsers = []
            if(this.allGroups) this.allGroups = []
            if(this.allPermissions) this.allPermissions = []
            this.totalPages = 1
            this.page = 1
            callback()
        },
        setPage(value) {
            this.page = value

        },
        setSelected(value) {
            this.selected = value
        },
    }
}