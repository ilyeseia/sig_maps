<template>
  <div id="main">
    <div class="notfound-content">
      <div class="notfound">
        <h1>Erreur 404</h1>
        <p class="lead">Cette page n'existe pas</p>
        <p>
          Soit vous avez mal orthographié l'URL, soit vous demandez une page qui n'est
          plus ici
        </p>
        <nuxt-link class="notfound-link" to="/">
          <button class="btn-home">Revenir a la page d'accueil</button>
        </nuxt-link>
      </div>

      <img src="../assets/404-illustration.svg" alt="" width="500" />
    </div>
  </div>
</template>

<script>
export default {
  layout: "errorLayout",
  asyncData() {
    return {
      title: "Error",
    };
  },
  props: {
    error: {
      type: Object,
      default: null,
    },
  },
  data() {
    return {};
  },
  head() {
    return {
      title: "Page Not Found 404",
    };
  },
};
</script>
<style scoped>
* {
  transition: all 0.6s;
}

html {
  height: 100%;
}

body {
  font-family: "Lato", sans-serif;
  color: #888;
  margin: 0;
}

#main {
  display: table;
  width: 100%;
  height: 100vh;
  text-align: center;
}

.notfound-content {
  display: flex;
  justify-content: center;
  height: 100vh;
  flex-direction: row;
}

.notfound-content h1 {
  margin-top: 250px;
  font-size: 50px;
  display: inline-block;
  padding-right: 12px;
  animation: type 0.5s alternate infinite;
}
.notfound-content .notfound .btn-home {
  background: #63b9ff;
  padding: 10px;
  margin: 10px;
  border-radius: 5px;
  border: none;
}
.notfound-content .notfound .notfound-link {
  text-decoration: none;
  color: white;
}
@keyframes type {
  from {
    box-shadow: inset -3px 0px 0px #888;
  }
  to {
    box-shadow: inset -3px 0px 0px transparent;
  }
}
</style>
