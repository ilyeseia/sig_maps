import { required, confirmed, length, email } from "vee-validate/dist/rules";
import { extend } from "vee-validate";

extend("required", {
  ...required,
  message: "Ce champ est requis"
});

extend("confirmed", {
  ...confirmed,
  message: "Cette confirmation de champ ne correspond pas"
});

extend("email", {
  ...email,
  message: "Ce champ doit être un email valide"
});
