<template>
  <form @submit.prevent="onSubmit">
    <div class="modal-card" style="width: 500px">
      <header class="modal-card-head">
        <h1 class="modal-card-title">
          {{
            settingsTypeToEdit
              ? "Edition le type de paramétre"
              : "Nouveau type de paramétre"
          }}
        </h1>
      </header>
      <section class="modal-card-body">
        <b-field label="Code">
          <b-input
            id="settingsType-code"
            type="text"
            v-model="code"
            placeholder="code"
            required
                              validation-message="Veuillez remplir ce champ"
          ></b-input>
        </b-field>
        <b-field label="Descrption">
          <b-input
            id="settingsType-description"
            name="description"
            type="textarea" 
            v-model="description"
            placeholder="description"
          ></b-input>
        </b-field>
        <b-field grouped class="d-flex justify-content-between">
          <b-field label="Par défault">
            <b-checkbox
              id="settingsType-default-value"
              v-model="default_value"
            ></b-checkbox>
          </b-field>
          <b-field label="Activé">
            <b-checkbox id="settingsType-enabled" v-model="enabled"></b-checkbox>
          </b-field>
        </b-field>
      </section>
      <footer class="right-align modal-card-foot">
        <button
          id="close-settingsType-form"
          class="button"
          type="button"
          @click="$parent.close()"
        >
          Fermer
        </button>
        <button
          id="save-settingsType"
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
  props: ["settingsTypeToEdit"],
  data() {
    return {
      isLoading: false,
      code: "",
      description: "",
      enabled: false,
      default_value: false,
    };
  },
  methods: {
    onSubmit(e) {
      const newSettingsType = {};

      this.isLoading = true;

      newSettingsType.code = this.code;
      newSettingsType.type = this.type;
      newSettingsType.description = this.description;
      newSettingsType.default_value = this.default_value;
      newSettingsType.enabled = this.enabled;

      this.saveSettingsType(newSettingsType);
    },
    saveSettingsType(newSettingsType) {
      RestApi.createOrUpdate(
        "settingsType",
        newSettingsType,
        this.settingsTypeToEdit,
        this.$store,
        this.$router,
        this.$notification
      )
        .then((data) => {
          this.$emit("saved");
          this.isLoading = false;
        })
        .catch((error) => {
          this.$emit("saved");
          this.isLoading = false;
        });
    },
  },
  beforeMount() {
    if (this.settingsTypeToEdit) {
      this.code = this.settingsTypeToEdit.code;
      this.description = this.settingsTypeToEdit.description;
      this.default_value = this.settingsTypeToEdit.default_value;
      this.enabled = this.settingsTypeToEdit.enabled;
    }
  },
};
</script>

<style>
.right-align {
  display: flex;
  flex-direction: row;
  justify-content: flex-end;
}
</style>
