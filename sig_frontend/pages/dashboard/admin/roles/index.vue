<template>
  <div class="w-100" style="height: 100%">
    <RoleTable
      v-if="!showRoleModal"
      @newRole="showRoleModal = true"
      @deleteRole="onDeleteRole"
      @editRole="onEditRole"
    />
    <NewRole
      @close="showRoleModal = false"
      v-if="showRoleModal"
      @saved="showRoleModal = false"
      :roleToEdit="roleToEdit"
    />
  </div>
</template>
<script>
import RoleTable from "../../../../components/dashboard/admin/RoleTable";
import NewRole from "../../../../components/dashboard/admin/NewRole";
import RestApi from "../../../../methods/api";

export default {
  layout: "dashboard",

  validate({ params, query, store }) {
    return (
      store.state.profile.roles.includes("ROLE_ADMIN") ||
      store.state.profile.roles.includes("Role_READ_AUTHORITY")
    );
  },
  data() {
    return {
      showRoleModal: false,
      roleToEdit: null,
    };
  },

  components: { RoleTable, NewRole },
  methods: {
    onEditRole(_id) {
      this.roleToEdit = this.$store.state.roles.roles.find(({ id }) => id === _id);
      this.showRoleModal = true;
    },
    mounted() {
      this.$nextTick(() => {
        this.$nuxt.$loading.start();

        setTimeout(() => this.$nuxt.$loading.finish(), 900);
      });
    },
    onDeleteRole(id) {
      this.$confirm({
        title: "Êtes-vous sûr de supprimer ce rôle?",
        content: "Cette action supprimera le paramètre de la base de données.",
        okText: "Oui",
        okType: "danger",
        cancelText: "Non",
        onOk: () => {
          RestApi.delete("roles", id, this.$store, this.$router, this.$notification);
        },
        onCancel: () => {},
      });
    },
  },
};
</script>
