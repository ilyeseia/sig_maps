<template>
  <form @submit.prevent="onSubmit">
    <div class="modal-card" style="height: 500px; width: 75vh">
      <section class="modal-card-body"></section>
      <footer class="right-align modal-card-foot">
        <button id="close-map-form" class="button" type="button" @click="$parent.close()">
          Close
        </button>
        <button
          id="save-map"
          type="submit"
          :class="['button', 'is-primary', { 'is-loading': isLoading }]"
        >
          Enregistrer
        </button>
      </footer>
    </div>
  </form>
</template>
<script>
import RestApi from "../../../methods/api.js";

export default {
  props: ["mapToEdit"],
  data() {
    return {
      isLoading: false,
      keepFirst: false,
      openOnFocus: false,
      name: "",
      selected: null,
      users: [],
      allUsers: [],

      groups: [],
      allGroups: [],
      groupName: "",
    };
  },
  methods: {
    onSubmit(e) {
      alert("Hi");
      const formData = new FormData(e.target);
      const newMap = { users: [] };

      this.isLoading = true;

      newMap.name = formData.get("name");
      newMap.description = formData.get("description");

      newMap.users = this.users;
      newMap.groups = this.groups;

      this.saveMap(newMap);
    },
    saveMap(map) {
      RestApi.shareMap(map, this.mapToEdit.id).then((data) => {
        this.$emit("saved");
        this.isLoading = false;
      });
    },
    addUser() {
      if (this.selected) this.users.push(this.selected);
    },

    deleteUser(id) {
      this.users = this.users.filter((user) => user.id != id);
    },
    addGroup() {
      if (this.selected) this.groups.push(this.selected);
    },
    deleteGroup(id) {
      this.groups = this.groups.filter((group) => group.id != id);
    },
  },
  computed: {
    filteredDataArray() {
      return this.allUsers.filter((option) => {
        return (
          option.username.toString().toLowerCase().indexOf(this.name.toLowerCase()) >= 0
        );
      });
    },
    loadAllGroups() {
      return this.allGroups.filter((option) => {
        return (
          option.name.toString().toLowerCase().indexOf(this.groupName.toLowerCase()) >= 0
        );
      });
    },
  },
  beforeMount() {
    RestApi.getAll("users", {}).then((users) => (this.allUsers = users));
    if (this.mapToEdit) {
      this.mapToEdit.users.forEach((user) =>
        this.users.push({
          id: user.id,
          isNewUser: false,
          username: user.username,
          email: user.email,
        })
      );
    }
    RestApi.getAll("groups", {}).then((groups) => (this.allGroups = groups));
    if (this.mapToEdit) {
      this.mapToEdit.groups.forEach((group) =>
        this.groups.push({
          id: group.id,
          name: group.name,
        })
      );
    }
  },
};
</script>
