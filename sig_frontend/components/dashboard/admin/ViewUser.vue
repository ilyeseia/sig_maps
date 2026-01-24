<template>
  <div style="overflow: auto" class="h-100">
    <ValidationObserver ref="observer" v-slot="{ passes }">
      <section class="section">
        <input
          id="file-input"
          type="file"
          ref="file"
          accept="image/*"
          style="display: none"
          @change="loadAvatar()"
        />
        <b-field label="Avatar">
          <div @click="$refs.file.click()">
            <a-avatar :size="64" :src="avatar" />
          </div>
        </b-field>

        <b-field grouped>
          <b-field expanded>
            <ValidationProvider
              rules="required"
              name="FirstName"
              v-slot="{ errors, valid }"
            >
              <b-field
                :type="{ 'is-danger': errors[0], 'is-success': valid }"
                :message="errors"
                label="Nom"
              >
                <b-input
                  name="firstName"
                  type="text"
                  v-model="firstName"
                  placeholder="Nom"
                  required
                  v-on:blur="generateUsername()"
                ></b-input>
              </b-field>
            </ValidationProvider>
          </b-field>
          <b-field expanded>
            <ValidationProvider
              rules="required"
              name="LastName"
              v-slot="{ errors, valid }"
            >
              <b-field
                :type="{ 'is-danger': errors[0], 'is-success': valid }"
                :message="errors"
                label="Prénom"
              >
                <b-input
                  name="lastName"
                  type="text"
                  v-model="lastName"
                  placeholder="Prénom"
                  required
                  v-on:blur="generateUsername()"
                ></b-input>
              </b-field>
            </ValidationProvider>
          </b-field>
        </b-field>
        <b-field grouped>
          <b-field label="Téléphone" expanded>
            <b-input
              name="homePhone"
              type="text"
              v-model="homePhone"
              placeholder="Téléphone"
              v-cleave="masks.fixe"
            ></b-input>
          </b-field>
          <b-field label="Mobile" expanded>
            <b-input
              name="mobile"
              type="text"
              v-model="mobile"
              placeholder="Mobile"
              v-cleave="masks.mobile"
            ></b-input>
          </b-field>
          <b-field label="Fax" expanded>
            <b-input
              name="fax"
              type="text"
              v-model="fax"
              placeholder="Fax"
              v-cleave="masks.fixe"
            ></b-input>
          </b-field>
        </b-field>

        <b-field grouped>
          <b-field expanded>
            <ValidationProvider
              rules="required"
              name="UserName"
              v-slot="{ errors, valid }"
            >
              <b-field
                :type="{ 'is-danger': errors[0], 'is-success': valid }"
                :message="errors"
                label="Nom d'utilisateur"
              >
                <b-input
                  name="username"
                  type="text"
                  v-model="username"
                  placeholder="Nom d'utilisateur"
                  required
                ></b-input>
              </b-field>
            </ValidationProvider>
          </b-field>
          <b-field expanded>
            <ValidationProvider
              rules="required|email"
              name="Email"
              v-slot="{ errors, valid }"
            >
              <b-field
                label="Email"
                :type="{ 'is-danger': errors[0], 'is-success': valid }"
                :message="errors"
              >
                <b-input
                  type="email"
                  v-model="email"
                  placeholder="Email"
                  required
                ></b-input>
              </b-field>
            </ValidationProvider>
          </b-field>
        </b-field>

        <b-field grouped>
          <b-field expanded>
            <ValidationProvider
              v-if="!userToEdit"
              :rules="{
                required: true,
                regex: /((?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$.%]).{6,20})/,
              }"
              vid="password"
              name="Password"
              v-slot="{ errors, valid }"
            >
              <b-field
                label="Mot de passe"
                :type="{ 'is-danger': errors[0], 'is-success': valid }"
                :message="errors"
              >
                <b-input
                  type="password"
                  v-model="password"
                  placeholder="Entre 6 à 20 caractères avec au moins un chiffre, une lettre majuscule, une lettre minuscule et un symbole spécial (@ #. $%) exemple P@ssword1990"
                  password-reveal
                ></b-input>
              </b-field>
            </ValidationProvider>
          </b-field>
          <b-field label="Mot de passe par défaut" v-if="!userToEdit">
            <button
              :class="['button', 'is-success', { 'is-loading': isGenerated }]"
              type="button"
              @click="generatePassword"
            >
              Génerer mot de passe
            </button>
          </b-field>
        </b-field>
        <b-field expanded>
          <ValidationProvider
            v-if="!userToEdit"
            rules="required|confirmed:password"
            name="Password Confirmation"
            v-slot="{ errors, valid }"
          >
            <b-field
              label="Confirm Password"
              :type="{ 'is-danger': errors[0], 'is-success': valid }"
              :message="errors"
            >
              <b-input type="password" v-model="passwordConfirmation"></b-input>
            </b-field>
          </ValidationProvider>
        </b-field>
        <b-field grouped>
          <b-field label="Date Activation" expanded>
            <b-datetimepicker
              id="activationDate"
              v-model="activationDate"
              placeholder="Click to select..."
              icon="calendar-today"
            >
            </b-datetimepicker>
          </b-field>
          <b-field label="Date Expiration" expanded>
            <b-datetimepicker
              id="desactivationDate"
              v-model="desactivationDate"
              placeholder="Click to select..."
              icon="calendar-today"
            >
            </b-datetimepicker>
          </b-field>
        </b-field>

        <b-field label="User Active" v-if="!isAdmin">
          <b-switch v-model="enabled"> </b-switch>
        </b-field>

        <b-field label="Affectation">
          <a-tree-select
            style="width: 100%"
            :tree-data="regionsData"
            tree-checkable
            :show-checked-strategy="SHOW_PARENT"
            v-model="checkedRegions"
            search-placeholder="Please select"
          />
        </b-field>

        <b-field v-if="!isAdmin" label="Permissions">
          <a-tree-select
            style="width: 100%"
            tree-checkable
            @expand="onExpand"
            :show-checked-strategy="SHOW_PARENT"
            :expandedKeys="expandedKeys"
            :autoExpandParent="autoExpandParent"
            :treeData="treeData"
            v-model="checkedKeys"
          />
        </b-field>

        <div class="buttons">
          <button
            :class="['button', 'is-primary', { 'is-loading': isLoading }]"
            @click="passes(submit)"
          >
            <span class="icon is-small">
              <i class="fas fa-check"></i>
            </span>
            <span>Save</span>
          </button>
        </div>
      </section>
    </ValidationObserver>
  </div>
</template>
<script>
import { backApi } from "~/methods/serverApi";
import { backend } from "../../../constants";
import RestApi from "../../../methods/api.js";
import * as icons from "@/assets/icons";
import AvatarUpload from "./AvatarUpload";
import { ValidationObserver, ValidationProvider } from "vee-validate";
import { TreeSelect } from "ant-design-vue";
import "./vee-validate";
import { extend } from "vee-validate";
import { regex, required } from "vee-validate/dist/rules";
import Cleave from "cleave.js";
extend("regex", regex);
extend("required", required);
const SHOW_PARENT = TreeSelect.SHOW_PARENT;
const cleave = {
  name: "cleave",
  bind(el, binding) {
    const input = el.querySelector("input");
    input._vCleave = new Cleave(input, binding.value);
  },
  unbind(el) {
    const input = el.querySelector("input");
    input._vCleave.destroy();
  },
};

export default {
  layout: "dashboard",
  components: {
    AvatarUpload,
    ValidationObserver,
    ValidationProvider,
  },
  props: ["userToEdit"],
  directives: { cleave },
  data() {
    return {
      isLoading: false,
      avatar: null,
      isAdmin: false,
      adminRole: null,
      enabled: false,
      firstName: "",
      lastName: "",
      homePhone: null,
      mobile: null,
      fax: null,
      activationDate: new Date(),
      desactivationDate: null,
      username: "",
      password: null,
      passwordConfirmation: null,
      email: "",
      expandedKeys: [],
      autoExpandParent: true,
      checkedKeys: [],
      checkedRegions: [],
      treeData: [],
      SHOW_PARENT,
      regionsData: [],
      isGenerated: false,
      masks: {
        fixe: {
          blocks: [0, 3, 1, 2, 3, 3],
          delimiters: ["+ ", " (", ") ", "-", "-"],
          numericOnly: true,
        },
        mobile: {
          blocks: [0, 3, 1, 3, 3, 3],
          delimiters: ["+ ", " (", ") ", "-", "-"],
          numericOnly: true,
        },
      },
    };
  },
  methods: {
    onExpand(expandedKeys) {
      this.expandedKeys = expandedKeys;
      this.autoExpandParent = false;
    },
    async loadAvatar() {
      let file = this.$refs.file.files[0];
      let fileName = await RestApi.saveFileInFolder("photo", file);
      this.avatar = `${backend}/download/photo/${fileName}`;
    },
    submit() {
      const newUser = {};

      //this.isLoading = true

      newUser.firstName = this.firstName;
      newUser.lastName = this.lastName;
      newUser.homePhone = this.homePhone;
      newUser.mobile = this.mobile;
      newUser.fax = this.fax;
      newUser.activationDate = this.activationDate;
      newUser.desactivationDate = this.desactivationDate;

      newUser.enabled = this.enabled;
      newUser.username = this.username;
      newUser.email = this.email;
      newUser.password = this.password;
      newUser.avatar = this.avatar;

      newUser.entityElements = [];

      this.checkedRegions.forEach((regionId) =>
        newUser.entityElements.push({ id: regionId })
      );

      if (!this.isAdmin) {
        newUser.roles = [];

        this.allRoles.forEach((role) => {
          if (this.checkedKeys.includes(role.id)) newUser.roles.push(role);
        });
      }

      this.saveUser(newUser);
    },
    generateUsername() {
      if (!this.userToEdit) {
        this.username = this.lastName.toLowerCase() + "." + this.firstName.toLowerCase();
      }
    },
    generatePassword() {
      const storedToken = localStorage.getItem("sigToken");
      this.isGenerated = true;
      backApi
        .post(`users/generatePassword`)
        .then(({ data }) => {
          if (data != null) {
            this.password = data;
            this.passwordConfirmation = data;
            this.isGenerated = false;
          }
        })
        .catch((error) => {
          this.isGenerated = false;
        });
    },
    saveUser(user) {
      RestApi.createOrUpdate(
        "users",
        user,
        this.userToEdit,
        this.$store,
        this.$router,
        this.$notification
      );
    },
  },
  beforeMount() {
    backApi.get(`entityelements/wilaya/wilaya`).then(({ data }) => {
      let element = {
        title: "Wilayas",
        value: "0-0",
        key: "0-0",
        children: [],
      };
      data.forEach(({ id, property }) =>
        element.children.push({ title: property, key: id, value: id })
      );
      this.regionsData.push(element);
    });

    if (this.userToEdit) {
      this.firstName = this.userToEdit.firstName;
      this.lastName = this.userToEdit.lastName;
      this.homePhone = this.userToEdit.homePhone;
      this.mobile = this.userToEdit.mobile;
      this.fax = this.userToEdit.fax;
      if (this.userToEdit.activationDate != null)
        this.activationDate = new Date(this.userToEdit.activationDate);
      if (this.userToEdit.desactivationDate)
        this.desactivationDate = new Date(this.userToEdit.desactivationDate);
      this.avatar = this.userToEdit.avatar;
      this.enabled = this.userToEdit.enabled;
      this.username = this.userToEdit.username;
      this.email = this.userToEdit.email;
    }

    if (!this.avatar) {
      this.avatar = icons.default["user"];
    }

    RestApi.getAll("roles/modules", {}).then((roles) => {
      this.allRoles = roles;

      let modules = this.allRoles.map((role) => role.label);
      let uniqueModules = new Set(modules);
      let topLevel = {
        title: "Toutes les permissions",
        key: "1",
        value: "1",
        children: [],
        disableCheckbox: true,
      };

      uniqueModules.forEach((module) => {
        let firstLevel = {
          title: module,
          key: module,
          value: module,
          children: [],
          disableCheckbox: true,
        };

        this.allRoles
          .filter((role) => role.label === module)
          .forEach((role) => {
            let secondLevel = {
              title: role.name,
              key: role.id,
              value: role.id,
              children: [],
            };

            firstLevel.children.push(secondLevel);

            role.permissions.forEach((permission) => {
              let random = Math.random();
              let thirdLevel = {
                title: permission.label,
                key: permission.id + random,
                value: permission.id + random,
                disableCheckbox: true,
              };

              secondLevel.children.push(thirdLevel);
            });
          });

        topLevel.children.push(firstLevel);
      });

      if (this.userToEdit) {
        this.isAdmin = this.userToEdit.username === "admin";
        this.userToEdit.roles.forEach((role) => {
          this.checkedKeys.push(role.id);
          this.checkedKeys.push(role.label);
        });

        this.userToEdit.entityElements.forEach((element) => {
          this.checkedRegions.push(element.id);
        });
      }

      this.treeData.push(topLevel);
    });
  },
};
</script>
