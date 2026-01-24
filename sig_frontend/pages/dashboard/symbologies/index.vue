<template>
  <div class="w-100" style="max-height: 100%">
    <Table @editLayer="onEditLayer" />
    <b-modal :active.sync="showModal" has-modal-card @close="layerToEdit = null">
      <NewSymbology @saved="showModal = false" :layerToEdit="layerToEdit" />
    </b-modal>
  </div>
</template>

<script>
import Table from "~/components/dashboard/symbologies/Table";
import NewSymbology from "~/components/dashboard/symbologies/NewSymbology";

export default {
  layout: "dashboard",
  validate({ params, query, store }) {
    return (
      store.state.profile.roles.includes("ROLE_ADMIN") ||
      store.state.profile.roles.includes("LAYER_READ_AUTHORITY")
    );
  },
  data() {
    return {
      showModal: false,
      layerToEdit: null,
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.$nuxt.$loading.start();
      setTimeout(() => this.$nuxt.$loading.finish(), 900);
    });
  },
  components: { Table, NewSymbology },
  methods: {
    onEditLayer(_id) {
      this.layerToEdit = this.$store.state.layers.layers.find(({ id }) => id === _id);
      this.showModal = true;
    },
  },
};
</script>

<style></style>
