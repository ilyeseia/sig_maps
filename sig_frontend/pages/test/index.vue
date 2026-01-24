
<template>
  <section class="p-5 w-100">
    <div class="d-flex justify-content-between">
      <div>
        <button
          id="add-new-map"
          v-if="
            profile.roles.includes('ROLE_ADMIN') ||
            profile.roles.includes('MAP_CREATE_AUTHORITY')
          "
          class="button mb-2"
          @click="$emit('newMap')"
        >
          <b-icon class="mr-1" pack="fas" icon="plus"></b-icon>Nouvelle Carte
        </button>
      </div>
      <div class="d-flex justify-content-around">
        <b-input
          v-model="searchName"
          placeholder="Search..."
          type="search"
          icon="magnify"
          @input="filterMaps"
        />
      </div>
    </div>
   <a-card hoverable  class="justify-content-around align-items-center" style="width:200px;height:150px"   v-for="map in maps.maps"
        :key="map.id"
        :id="map.id">
    <img  @click="goToMap(map)"
      slot="cover"
      alt="example"
      src="@/assets/icons/map.png"
    />
    <template slot="actions" class="ant-card-actions">
      <a-icon  v-if="profile.roles.includes('ROLE_ADMIN') || profile.roles.includes('MAP_UPDATE_AUTHORITY')" @click="$emit('shareMap', map)" key="setting" type="share-alt" />
      <a-icon  v-if="profile.roles.includes('ROLE_ADMIN') || profile.roles.includes('MAP_UPDATE_AUTHORITY')" @click="$emit('editMap', map)" key="edit" type="edit" />
      <a-icon  v-if="profile.roles.includes('ROLE_ADMIN') || profile.roles.includes('MAP_UPDATE_AUTHORITY')" @click="deleteMap(map)" key="ellipsis" type="delete" />
    </template>
    <a-card-meta :title="map.name" :description= "map.createdBy">
      <a-avatar
        slot="avatar"
        src="https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png"
      />
    </a-card-meta>
  </a-card>
  </section>
</template>

<script>
import { mapState } from 'vuex'
import RestApi from '../../methods/api.js'

export default {
  layout: 'dashboard',
  data() {
    return {
      searchName: '',
      sortField: 'createDate',
      sortOrder: 'asc',
      currentPage: 1,
      perPage: 10,
      totalRecords: 0,
      rangeBefore: 3,
      rangeAfter: 1,
      order: '',
      size: 'is-small',
      isSimple: false,
      isRounded: false,
    }
  },
  computed: {
    ...mapState(['maps', 'users','profile']),
    totalRowsCount() {
      return this.maps.totalRowsCount
    },
    tableData() {
      return this.maps.maps.map(
        ({
          id,
          name,
          slug,
          layers,
          privacy,
          users,
          groups,
          createDate,
          createdBy,
          tags,
        }) => ({
          id,
          slug,
          name,
          privacy,
          users,
          groups,
          layers,
          createdBy,
          createDate,
          tags,
        })
      )
    },
  },
  methods: {
    show(){
    },
    loadAsyncData() {
      RestApi.findAll(
        'maps',
        {
          page: this.currentPage - 1,
          limit: this.perPage,
          sort: this.sortField,
          dir: this.sortOrder,
        },
        'maps/set',
        this.$store,
        this.$router,
        this.$notification
      )
    },
    filterMaps() {
      let payload = {
        condition: 'and',
        rules: [
          {
            label: 'name',
            field: 'name',
            operator: 'ilike',
            type: 'string',
            value: this.searchName,
          },
        ],
      }
      RestApi.findAllByCriteria(
        'maps',
        payload,
        {},
        this.$store,
        this.$router,
        this.$notification
      )
    },
    editTags(map) {
      this.$buefy.$modal.open({
        parent: this,
        component: TagSelect,
        hasModalCard: true,
        props: { elementToEdit: map },
      })
    },
    goToMap(map) {
      this.$store.commit('maps/setCurrentMap', { map, mode: 'map' })
      this.$router.push('/dashboard/viewer')
    },
    deleteMap(map) {
      this.$confirm({
        title: 'Êtes-vous sûr de vouloir supprimer cette carte?',
        content: 'Cette action supprimera la couche de la base de données.',
        okText: 'Oui',
        okType: 'danger',
        cancelText: 'Non',
        onOk: () => {
          RestApi.delete(
            'maps',
            map.id,
            this.$store,
            this.$router,
            this.$notification
          )
        },
        onCancel: () => {},
      })
    },
  },
  beforeMount() {
    this.loadAsyncData()
  }
}
</script>