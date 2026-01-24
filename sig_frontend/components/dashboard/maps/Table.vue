<template>
  <div class="container-fluid">
    <div class="content-wrapper mt-5">
      <div class="d-flex justify-content-between row-column">
        <div>
          <button
            type="button"
            class="button is-primary mb-2"
            id="add-new-map"
            v-if="
              profile.roles.includes('ROLE_ADMIN') ||
              profile.roles.includes('MAP_CREATE_AUTHORITY')
            "
            @click="$emit('newMap')"
          >
            <i class="fas fa-plus-circle"></i> Nouvelle
          </button>
        </div>
        <b-input
          placeholder="Rechercher ici.."
          type="search"
          icon="magnify"
          icon-clickable
          v-model="searchName"
          ref="searchNameRef"
        >
        </b-input>
      </div>
    </div>
    <!-- Start Displaying Map -->
    <section class="display-maps">
      <div class="wrapper" v-for="map in maps.maps" :key="map.id" :id="map.id">
        <div class="card" @click="goToMap(map)">
          <!-- Start Banner  -->
          <p v-if="map.privacy.toLowerCase() == 'public'">
            <span class="banner" style="background: #080"> Publique </span>
          </p>
          <p v-else-if="map.privacy.toLowerCase() == 'public_with_link'">
            <span class="banner" style="background: orange"> Partagé </span>
          </p>
          <p v-else-if="map.privacy.toLowerCase() == 'private'">
            <span class="banner" style="background: red">Privé </span>
          </p>
          <p v-else>
            <span class="banner" style="background: gray">Archivé </span>
          </p>

          <!-- End Banner  -->
          <!-- <img
            src="../../../assets/map-hand.jpeg"
            class="card-img-top"
            alt="..."
          /> -->
          <iframe
            :id="`${map.slug}-id`"
            class="iframe"
            :src="`${rootUrl}/sharedmap/${map.id}?control=false`"
            allowtransparency="true"
            frameborder="0"
            scrolling="no"
            width="400"
            height="256"
          ></iframe>
          <!--  START ARCHIVED MAP -->
          <div class="card-body" v-if="map.privacy.toLowerCase() == 'archived'">
            <div>
              <h5 class="card-title">{{ map.name }}</h5>
              <p class="card-text">
                Propriétaire : <span class="created">{{ map.createdBy }} </span>
              </p>
            </div>
            <div class="btn-action">
              <!-- Start Delete button  -->
              <i
                v-if="
                  profile.roles.includes('ROLE_ADMIN') ||
                  profile.roles.includes('MAP_DELETE_AUTHORITY')
                "
                @click.stop="deleteMap(map)"
                class="fas fa-trash"
                data-toggle="tooltip"
                data-placement="top"
                title="Supprimer la carte"
              >
              </i>
              <!-- End Delete button  -->

              <!-- Start Archived button  -->
              <i
                v-if="
                  profile.roles.includes('ROLE_ADMIN') ||
                  profile.roles.includes('MAP_ARCHIVE_AUTHORITY')
                "
                @click.stop="archiveMap(map)"
                class="fas fa-undo"
                data-toggle="tooltip"
                data-placement="top"
                title="Désarchiver la carte"
              ></i>
              <!-- End Archived button  -->
            </div>
          </div>
          <!--  END ARCHIVED MAP -->

          <div class="card-body" v-if="map.privacy.toLowerCase() != 'archived'">
            <div>
              <strong>{{ map.name }}</strong>
              <p class="card-text">
                Propriétaire : <span class="created">{{ map.createdBy }} </span>
              </p>
            </div>
            <div class="btn-action">
              <!-- Start Share Button  -->
              <i
                v-if="
                  profile.roles.includes('ROLE_ADMIN') ||
                  profile.roles.includes('MAP_SHARE_AUTHORITY')
                "
                @click.stop="$emit('shareMap', map)"
                class="fas fa-share-alt-square"
                data-toggle="tooltip"
                data-placement="top"
                title="Partager la carte"
              ></i>
              <!-- End Share button  -->
              <!-- Start Edit Button  -->
              <i
                v-if="
                  profile.roles.includes('ROLE_ADMIN') ||
                  profile.roles.includes('MAP_UPDATE_AUTHORITY')
                "
                @click.stop="$emit('editMap', map)"
                class="fas fa-edit"
                data-toggle="tooltip"
                data-placement="top"
                title="Modifier la carte"
              >
              </i>
              <!-- End Edit Button  -->

              <!-- Start CLone button  -->
              <!-- <i
                v-if="
                  profile.roles.includes('ROLE_ADMIN') ||
                  profile.roles.includes('MAP_CLONE_AUTHORITY')
                "
                @click.stop="cloneMap(map)"
                class="fas fa-copy"
                data-toggle="tooltip"
                data-placement="top"
                title="Cloner la carte"
              ></i> -->
              <!-- End CLone button  -->

              <!-- Start Delete button  -->
              <i
                v-if="
                  profile.roles.includes('ROLE_ADMIN') ||
                  profile.roles.includes('MAP_DELETE_AUTHORITY')
                "
                @click.stop="deleteMap(map)"
                class="fas fa-trash"
                data-toggle="tooltip"
                data-placement="top"
                title="Supprimer la carte"
              >
              </i>
              <!-- End Delete button  -->

              <!-- Start Archived button  -->
              <i
                v-if="
                  profile.roles.includes('ROLE_ADMIN') ||
                  profile.roles.includes('MAP_ARCHIVE_AUTHORITY')
                "
                @click.stop="archiveMap(map)"
                class="fas fa-archive"
                data-toggle="tooltip"
                data-placement="top"
                title="Archiver la carte"
              ></i>
              <!-- End Archived button  -->
            </div>
          </div>
        </div>
      </div>
    </section>
    <!-- End Displaying Map -->

    <!-- Start Pagenation -->
    <div class="pagenation">
      <div v-if="totalRowsCount > 0">
        <a-pagination
          class="pg-footer"
          @change="onPageChange"
          :page-size="perPage"
          :total="totalRowsCount"
          show-less-items
        />
      </div>
      <div class="ml-2" v-if="totalRowsCount > 0">
        <b-select v-model="perPage" @input="OnSizeChange">
          <option value="10">10</option>
          <option value="20">20</option>
          <option value="30">30</option>
          <option value="40">40</option>
          <option value="50">50</option>
        </b-select>
      </div>
    </div>
    <b-modal
      :can-cancel="false"
      :active.sync="showCloneModal"
      has-modal-card
      @close="mapToClone = null"
    >
      <CloneMap
        @saved="showCloneModal = false"
        :mapToClone="mapToClone"
      ></CloneMap>
    </b-modal>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { backend, frontend } from '../../../constants'
import RestApi from '../../../methods/api.js'
import TagSelect from '../tags/TagSelect'
import { backApi } from '~/methods/serverApi'
import CloneMap from './CloneMap.vue'
export default {
  components: { CloneMap },
  data() {
    return {
      showCloneModal: false,
      searchName: '',
      sortField: 'createDate',
      sortOrder: 'desc',
      currentPage: 1,
      perPage: 10,
      order: '',
      size: 'is-small',
      isSimple: false,
      isRounded: false,
      rootUrl: frontend,
      mapToClone: null,
      isLoading: false,
    }
  },
  computed: {
    ...mapState(['maps', 'profile']),
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
  watch: {
    searchName(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.searchNameRef.value) {
          this.page = 1
          this.filterMaps()
        }
      }, 500)
    },
  },
  methods: {
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
    onPageChange(page) {
      this.currentPage = page
      if (this.searchName === '') {
        this.loadAsyncData()
      } else {
        this.filterMaps()
      }
    },
    OnSizeChange(size) {
      this.perPage = parseInt(size)
      if (this.searchName === '') {
        this.loadAsyncData()
      } else {
        this.filterMaps()
      }
    },

    getPrivacyIcon(map) {
      switch (map) {
        case '':
        case '':
        case '':
      }
    },
    getMapImage(map) {
      if (!map.image) {
        return `${backend}/download/map.png`
      }
      return `${backend}/download/${map.image}`
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
        {
          page: this.currentPage - 1,
          limit: this.perPage,
          sort: this.sortField,
          dir: this.sortOrder,
        },
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
      if (!this.isLoading) {
        this.isLoading = true
        backApi
          .get(`maps/layersWithFieldsAndResources/${map.id}`)
          .then(({ data }) => {
            map.layers = data
            this.isLoading = false
            this.$store.commit('maps/setCurrentMap', {
              map,
              mode: 'map',
            })
            this.$router.push('/dashboard/viewer')
          })
          .catch(() => (this.isLoading = false))
      }
    },
    getProviders() {
      RestApi.findSettingsByType('PROVIDER', 'settings/set', this.$store)
    },

    deleteMap(map) {
      this.$confirm({
        title: 'Supprission de la carte',
        content: 'Voulez vous supprimer cette carte ?',
        okText: 'Oui',
        okType: 'danger',
        cancelText: 'Non',
        onOk: () => {
          this.$nextTick(() => {
            this.$nuxt.$loading.start()
          })
          RestApi.delete(
            'maps',
            map.id,
            this.$store,
            this.$router,
            this.$notification
          )
            .then(() => {
              this.searchName = ''
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
    archiveMap(map) {
      let newMap = {}
      if (map.privacy != 'ARCHIVED') {
        this.$confirm({
          title: 'Archivage de la carte',
          content: "Voulez vous d'archiver cette carte ?",
          okText: 'Oui',
          okType: 'danger',
          cancelText: 'No',
          onOk: () => {
            newMap.privacy = 'ARCHIVED'
            RestApi.archiveMap(newMap, map.id, this.$store, this.$notification)
          },
          onCancel: () => {},
        })
      } else {
        this.$confirm({
          title: 'Désarchivage de la carte',
          content: 'Voulez vous désarchiver cette carte ?',
          okText: 'Oui',
          okType: 'danger',
          cancelText: 'No',
          onOk: () => {
            newMap.privacy = 'PRIVATE'
            RestApi.archiveMap(newMap, map.id, this.$store, this.$notification)
          },
          onCancel: () => {},
        })
      }
    },
    cloneMap(map) {
      this.mapToClone = map
      this.showCloneModal = true
    },
  },
  beforeMount() {
    this.loadAsyncData()
    // à revoir avec hachimi
    // this.getProviders();
  },
}
</script>
<style scoped lang="scss">
.content-wrapper button {
  background: $color-primary;
  outline: none;
  border: none;
}
.content-wrapper button:hover {
  background: $color-primary;
}
.content-wrapper .btn-search-map {
  /* display: flex;
  justify-content: flex-end; */
}
.content-wrapper .search-input {
  width: 250px;
  float: right;
  margin-top: 0;
  @media (max-width: 768px) {
    width: 100%;
    margin-top: 1rem;
    float: left;
  }
}
/* Start Displaying Map */
.display-maps {
  margin-top: 0.8rem;
  display: flex;
  align-items: stretch;
  flex-wrap: wrap;
  min-height: 14rem;
  margin-left: -10px;
  margin-right: -10px;
}
.display-maps .wrapper {
  max-width: calc(100% / 7);
  padding: 10px;
}

@media only screen and (max-width: 1600px) {
  .display-maps .wrapper {
    max-width: calc(100% / 5);
  }
}
@media only screen and (max-width: 1100px) {
  .display-maps .wrapper {
    max-width: 50%;
  }
}
@media only screen and (max-width: 560px) {
  .display-maps .wrapper {
    max-width: 100%;
  }
}
.display-maps .card {
  height: 100%;
  overflow: hidden;
  border-radius: 5px;
}
.display-maps .card .iframe {
  z-index: 0;
  height: 256px !important;
  pointer-events: none;
}
.display-maps .card .card-body {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 0.8rem 1.25rem;
}
.display-maps .card .card-text {
  width: 100%;
  line-height: 2rem;
}
.display-maps .card .btn-action {
  margin-top: 1rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.display-maps .card .btn-action i {
  color: #fff;
  background: $color-primary;
  text-align: center;
  line-height: 1.1rem;
  padding: 0.8rem;
  border-radius: 50%;
  font-size: 1.1rem;
}
.display-maps .card:hover {
  /* transform: scale(1.05);*/
  cursor: pointer;
  transition: all 0.2s ease-in-out;
  box-shadow: 4px 5px 23px -5px #ebe8e8;
}
i:hover {
  transform: scale(1.15);
  cursor: pointer;
  transition: all 0.2s ease-in-out;
  box-shadow: 4px 5px 23px -5px #ebe8e8;
}
.display-maps .card img {
  margin-top: -20px;
}
.display-maps .banner {
  position: absolute;
  top: 1.1rem;
  left: -27px;
  text-align: center;
  color: white;
  background: $color-primary;
  width: 9rem;
  padding: 0.1rem;
  transform: rotate(-43deg);
  z-index: 5;
  font-size: 1rem;
  border: 3px dotted #fff;
}
.display-maps .created {
  background: $color-primary;
  padding: 0.35rem 0.6rem;
  color: white;
  border-radius: 60px;
}
/* End Displaying Map */

/* Start Pagenation  */
.pagenation {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 20px;
}
</style>
