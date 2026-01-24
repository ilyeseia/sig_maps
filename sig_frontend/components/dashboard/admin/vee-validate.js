import { required, confirmed, length, email } from "vee-validate/dist/rules";
import { extend } from "vee-validate";

extend("required", {
  ...required,
  message: "Ce champ est requis"
});

extend("email", {
  ...email,
  message: "Ce champ doit être un email valide"
});

extend("confirmed", {
  ...confirmed,
  message: "Ce champ de confirmation ne correspond pas"
});

extend("length", {
  ...length,
  message: "Ce champ doit avoir 2 options"
});
