<template></template>

<script>
import RestApi from "../../../../methods/api";

export default {
  layout: "dashboard",
  props: [],
  data() {
    return {
      cords: [],
    };
  },
  beforeMount() {
    let id = this.$route.params.id;
    RestApi.getPublicMap(id)
      .then((map) => {
        /* backApi.get(`maps/layers/${map.id}`).then(({ data }) => {
      
        }) */
        this.$store.commit("maps/setCurrentMap", {
          map,
          mode: "map",
          /* _layers: data, */
        });
        for (const key in this.$route.query) {
          if (Object.hasOwnProperty.call(this.$route.query, key)) {
            const element = this.$route.query[key];
            this.cords.push(element);
          }
        }

        // store cords in localstorge
        localStorage.setItem("getSharedFeature", JSON.stringify(this.cords));

        this.$router.push("/dashboard/viewer");
      })
      .catch((error) => {
        if (error.response && error.response.status === 401) {
          this.$router.push("/error");
        }
      });
  },
  mounted() {},
};
</script>

<style></style>
