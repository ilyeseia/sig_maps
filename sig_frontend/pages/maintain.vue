<template>
  <section class="container">
    <div class="wrapper">
      <h1 class="display-1 text-center">👨‍🔧</h1>
      <h2c class="h1 text-center">Nous reviendrons bientôt!</h2c>

      <div>
        <p class="lead text-center">
          Désolé pour le désagrément mais nous effectuons des travaux de maintenance au
          moment, nous serons heureux de vous obliger, vous pouvez toujours
          <a href="mailto:admin@eadn.dz">contactez-nous</a>, sinon nous serons de retour
          en ligne prochainement<br />
          Bonne journée
        </p>
        <p class="text-center">&mdash; L'équipe EADN</p>
      </div>
    </div>
  </section>
</template>
<script>
export default {
  layout: "maintainLayout",
};
</script>
<style scoped>
.wrapper {
  display: flex;
  flex-direction: column;
  height: 75vh;
  justify-content: center !important;
  padding: 20px;
}
</style>
