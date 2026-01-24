<template>
  <section class="p-5 w-100">
    <div class="d-flex justify-content-between">
      <div>
        <button
          v-if="
            profile.roles.includes('ROLE_ADMIN') ||
            profile.roles.includes('TAG_CREATE_AUTHORITY')
          "
          id="add-new-tag"
          class="button mb-2"
          @click="$emit('newTag')"
        >
          <b-icon class="mr-1" pack="fas" icon="plus"></b-icon>New Tag
        </button>
      </div>
      <div class="d-flex justify-content-around">
        <b-input
          v-model="searchName"
          placeholder="Search..."
          type="search"
          icon="magnify"
        />
        <button @click="filterTags" class="button is-info">Search</button>
      </div>
    </div>
    <b-table
      :data="tableData"
      :paginated="isPaginated"
      :per-page="perPage"
      :current-page.sync="currentPage"
      :pagination-simple="isPaginationSimple"
      :default-sort-direction="defaultSortDirection"
      default-sort="name"
      :show-detail-icon="true"
      detailed
    >
      <template slot-scope="props">
        <b-table-column field="name" label="Name" sortable>{{
          props.row.name
        }}</b-table-column>
        <b-table-column field="Owner" label="Owner" sortable centered>{{
          props.row.createdBy
        }}</b-table-column>
        <b-table-column field="created" label="Created" sortable centered>
          <span class="tag is-success">
            {{ new Date(props.row.createDate).toLocaleDateString() }}
          </span>
        </b-table-column>
        <b-table-column field="edit" label="Edit">
          <button
            v-if="
              profile.roles.includes('ROLE_ADMIN') ||
              profile.roles.includes('TAG_UPDATE_AUTHORITY')
            "
            id="edit-tag"
            class="button is-info is-small"
            @click="$emit('editTag', props.row.id)"
          >
            <b-icon class="mr-1 p-1" pack="fas" icon="edit" />Edit
          </button>
          <button
            v-if="
              profile.roles.includes('ROLE_ADMIN') ||
              profile.roles.includes('TAG_DELETE_AUTHORITY')
            "
            id="delete-tag"
            class="button is-danger is-small"
            @click="$emit('deleteTag', props.row.id)"
          >
            <b-icon class="mr-1" pack="fas" icon="times" />Delete
          </button>
        </b-table-column>
      </template>
    </b-table>
  </section>
</template>


<script>
import { mapState } from 'vuex'
import RestApi from '../../../methods/api.js'

export default {
  layout: 'dashboard',
  data() {
    return {
      searchName: null,
      myFile: null,
      isPaginated: false,
      isPaginationSimple: false,
      defaultSortDirection: 'asc',
      currentPage: 1,
      perPage: 5,
    }
  },
  methods: {
    filterTags() {
      let payload = {}
      payload.name = this.searchName
      const api = new RestApi(this.$store, this.$router, this.$notification)
      api.findAllByCriteria('tags', payload, {})
    },
  },
  computed: {
    ...mapState(['tags', 'users']),
    tableData() {
      return this.tags.tags.map(
        ({
          id,
          name,
          layers,
          entityElements,
          maps,
          createDate,
          createdBy,
        }) => ({
          id,
          name,
          layers,
          entityElements,
          maps,
          createDate,
          createdBy,
        })
      )
    },
  },
  async beforeMount() {
    RestApi.findAll(
      'tags',
      {},
      'tags/set',
      this.$store,
      this.$router,
      this.$notification
    )
  },
}
</script>
