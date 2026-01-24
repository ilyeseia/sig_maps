<template>
  <div class="w-100" style="max-height: 100%">
    <LoginForm />
  </div>
</template>

<script>
import LoginForm from '~/components/auth/LoginForm'
import pageTitle from '~/mixins/page-title'
import { mapState } from 'vuex'
export default {
  layout: 'authLayout',
  mixins: [pageTitle],

  data() {
    return {
      page: {
        title: 'Connexion',
        hid: '',
        name: '',
        description: '',
      },
    }
  },
  components: { LoginForm },
  computed: {
    ...mapState(['profile']),
  },
  mounted() {
    this.$nextTick(() => {
      this.$nuxt.$loading.start()
      if(this.profile.authenticated) this.$router.push("/dashboard/maps")
      setTimeout(() => this.$nuxt.$loading.finish(), 300)
    })
  },
}
</script>

<style></style>
