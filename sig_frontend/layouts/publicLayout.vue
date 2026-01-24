<template>
  <div style="height: 100%; display: flex; flex-direction: column">
    <Body>
      <nuxt />
    </Body>
  </div>
</template>

<script>
import Body from "~/components/layout/Body";
import Footer from "~/components/layout/Footer";

export default {
  components: { Body, Footer },
};
</script>

<style>
html,
body,
#__nuxt,
#__layout {
  height: 100%;
}
</style>
