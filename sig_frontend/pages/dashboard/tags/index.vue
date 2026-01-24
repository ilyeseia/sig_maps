<template>
  <div class="w-100" style="max-height: 100%">
    <Table @newTag="showModal = true" @deleteTag="onDeleteTag" @editTag="onEditTag" />
    <b-modal :active.sync="showModal" has-modal-card @close="tagToEdit = null">
      <NewTag @saved="showModal = false" :tagToEdit="tagToEdit" />
    </b-modal>
  </div>
</template>

<script>
import Table from "~/components/dashboard/tags/Table";
import NewTag from "~/components/dashboard/tags/NewTag";
import RestApi from "../../../methods/api";

export default {
  layout: "dashboard",
  validate({ params, query, store }) {
    return (
      store.state.profile.roles.includes("ROLE_ADMIN") ||
      store.state.profile.roles.includes("TAG_READ_AUTHORITY")
    );
  },
  data() {
    return {
      showModal: false,
      tagToEdit: null,
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.$nuxt.$loading.start();

      setTimeout(() => this.$nuxt.$loading.finish(), 900);
    });
  },
  components: { Table, NewTag },
  methods: {
    onEditTag(_id) {
      this.tagToEdit = this.$store.state.tags.tags.find(({ id }) => id === _id);
      this.showModal = true;
    },
    onDeleteTag(id) {
      this.$confirm({
        title: "Êtes-vous sûr de vouloir supprimer cette balise?",
        content: "Cette action supprimera le paramètre de la base de données.",
        okText: "Oui",
        okType: "danger",
        cancelText: "Non",
        onOk: () => {
          RestApi.delete("tags", id, this.$store, this.$router, this.$notification);
        },
        onCancel: () => {},
      });
    },
  },
};
</script>

<style></style>
