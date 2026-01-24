<template>
  <section class="p-5 w-100">
    <b-table
      :data="tableData"
      paginated
      backend-pagination
      :per-page="perPage"
      :total="totalRowsCount"
      @page-change="onPageChange"
      backend-sorting
      :default-sort-direction="defaultSortOrder"
      :default-sort="[sortField, sortOrder]"
      @sort="onSort"
    >
      <template slot-scope="props">
        <b-table-column field="message" label="Message" sortable>{{
          props.row.message
        }}</b-table-column>
        <b-table-column field="viewed" label="Viewed" sortable>{{
          props.row.viewed
        }}</b-table-column>
        <b-table-column field="viewed_date" label="Viewed Date" sortable>{{
          props.row.viewedDate
        }}</b-table-column>
      </template>
    </b-table>
  </section>
</template>

<script>
import { mapState } from "vuex";
import RestApi from "../../../../methods/api.js";
import pageTitle from '~/mixins/page-title'
export default {
  layout: "dashboard",
    mixins: [pageTitle],
  data() {
    return {
      searchName: null,
      sortField: "message",
      sortOrder: "asc",
      defaultSortOrder: "asc",
      perPage: 10,
      page: 1,
      totalRowsCount: 0,
           page: {
        title: "Notifications",
        hid: '',
        name: '',
        description: '',
      },
    };
  },
  computed: {
    ...mapState(["user_notifications"]),
    tableData() {
      return this.user_notifications.user_notifications.map(
        ({ id, message, viewed, viewed_date }) => ({
          id,
          message,
          viewed,
          viewed_date,
        })
      );
    },
  },
  methods: {
    loadAsyncData() {
      RestApi.findAll(
        "user_notifications",
        {
          page: this.page - 1,
          limit: this.perPage,
          sort: this.sortField,
          dir: this.sortOrder,
        },
        "user_notifications/set",
        this.$store,
        this.$router,
        this.$notification
      );
    },
    onSort(field, order) {
      this.sortField = field;
      this.sortOrder = order;
      this.loadAsyncData();
    },
    onPageChange(page) {
      this.page = page;
      this.loadAsyncData();
    },
    filterSettings() {
      let payload = {
        condition: "and",
        rules: [
          {
            label: "code",
            field: "code",
            operator: "ilike",
            type: "string",
            value: this.searchName,
          },
        ],
      };
      RestApi.findAllByCriteria(
        "user_notifications",
        payload,
        {},
        this.$store,
        this.$router,
        this.$notification
      );
    },
  },
  beforeMount() {
    RestApi.count("user_notifications").then((count) => {
      this.totalRowsCount = count;
      this.loadAsyncData();
    });
  },
  mounted() {
    this.$nextTick(() => {
      this.$nuxt.$loading.start();

      setTimeout(() => this.$nuxt.$loading.finish(), 900);
    });
  },
};
</script>
