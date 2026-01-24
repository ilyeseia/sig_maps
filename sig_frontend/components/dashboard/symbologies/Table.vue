<template>
  <section class="p-5 w-100">
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
        <b-table-column field="topo" label="Type" sortable>{{
          props.row.topo
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
              profile.roles.includes('LAYER_UPDATE_AUTHORITY')
            "
            id="edit-layer"
            class="button is-info is-small"
            @click="$emit('editLayer', props.row.id)"
          >
            <b-icon class="mr-1 p-1" pack="fas" icon="edit" />Edit
          </button>
        </b-table-column>
      </template>

      <template slot="detail" slot-scope="props">
        <div v-for="field in props.row.fields" :key="field.id">
          <table class="table table-hover">
            <tr class="d-flex w-100 justify-content-around">
              <td style="flex: 1">
                <b>{{ field.name }}</b>
              </td>
              <td style="flex: 1">{{ field.type }}</td>
            </tr>
          </table>
        </div>
      </template>
    </b-table>
  </section>
</template>

<script>
import { mapState } from "vuex";
import RestApi from "../../../methods/api.js";

export default {
  layout: "dashboard",
  data() {
    return {
      isPaginated: false,
      isPaginationSimple: false,
      defaultSortDirection: "asc",
      currentPage: 1,
      perPage: 5,
    };
  },
  computed: {
    ...mapState(["layers", "profile"]),
    tableData() {
      return this.layers.layers.map(
        ({
          id,
          name,
          slug,
          topo,
          type,
          createDate,
          customIcon,
          labelingEnabled,
          symbologyType,
          createdBy,
          typeLimit,
          identifiant,
          fields,
          iconUrl,
        }) => ({
          id,
          name,
          slug,
          topo,
          type,
          typeLimit,
          customIcon,
          labelingEnabled,
          symbologyType,
          createDate,
          createdBy,
          identifiant,
          fields,
          iconUrl,
          selected: false,
        })
      );
    },
  },
  async beforeMount() {
    RestApi.findAll(
      "layers",
      {},
      "layers/set",
      this.$store,
      this.$router,
      this.$notification
    );
  },
};
</script>
