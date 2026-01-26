const pkg = require('./package.json')
const base = process.env.NODE_ENV === "production" ? "/" : "/"


module.exports = {
  ssr: false,
  router: {
    base
  },
  server: {
    port: process.env.NODE_ENV === 'production' ? 80 : 5000
  },

  /*
  ** Headers of the page
  */
  head: {
    title: pkg.name,
    meta: [
      { charset: 'utf-8' },
      { name: 'viewport', content: 'width=device-width, initial-scale=1' },
      { hid: 'description', name: 'description', content: pkg.description },
      { httpEquiv: 'Content-Security-Policy', content: "default-src 'self' 'unsafe-inline' 'unsafe-eval' https: data:; img-src 'self' https: data: blob:; font-src 'self' https: data:;" }
    ],
    link: [
      { rel: 'icon', type: 'image/x-icon', href: '/kharitadz-logo-offecial.ico' },
      { rel: "stylesheet", href: "https://fonts.googleapis.com/css2?family=Outfit:wght@400;500;600;700;800&family=DM+Sans:wght@400;500;600;700&family=Righteous&display=swap" }
    ],
    script: [
    ]
  },

  /*
  ** Customize the progress-bar color
  */
  loading: './components/Loading.vue',

  /*
  ** Global CSS
  */
  css: [
    'primevue/resources/primevue.css',
    'primevue/resources/themes/fluent-light/theme.css',
    'primeicons/primeicons.css',
    'ant-design-vue/dist/antd.min.css',
    '@fortawesome/fontawesome-free/css/all.min.css',
    'node_modules/bootstrap/dist/css/bootstrap.min.css',
    '@syncfusion/ej2-base/styles/material.css',
    '@syncfusion/ej2-buttons/styles/material.css',
    '@syncfusion/ej2-popups/styles/material.css',
    '@syncfusion/ej2-splitbuttons/styles/material.css',
    '@syncfusion/ej2-inputs/styles/material.css',
    '@syncfusion/ej2-calendars/styles/material.css',
    '@syncfusion/ej2-dropdowns/styles/material.css',
    '@syncfusion/ej2-navigations/styles/material.css',
    '@syncfusion/ej2-grids/styles/material.css',
  ],

  styleResources: {
    scss: ['./assets/sass/main.scss']
  },

  /*
  ** Global SCSS
  */

  /*
  ** Plugins to load before mounting the App
  */
  plugins: [
    { src: '~/plugins/leaflet', ssr: false },
    { src: '~/plugins/antd' },
    { src: '~/plugins/persistedState.client.js' },
    { src: '~/plugins/vue-moment.js' },
  ],
  // Auto import Components 
  //components: true,
  /*
  ** Nuxt.js modules
  */
  modules: [
    // Doc: https://axios.nuxtjs.org/usage
    '@nuxtjs/axios',
    // Doc: https://buefy.github.io/#/documentation
    'nuxt-buefy',
    '@nuxtjs/dotenv',
    'vue-social-sharing/nuxt',
    '@nuxtjs/style-resources'
  ],
  /*
  ** Axios module configuration
  */
  axios: {
    // See https://github.com/nuxt-community/axios-module#options
  },
  /*   publicRuntimeConfig: {
      
    },
    privateRuntimeConfig: {
     
    },  */
  /*
  ** Build configuration
  */
  target: 'static',
  build: {
    loaders: {
      scss: {
        implementation: require('sass'),
        sassOptions: {
          quietDeps: true,
          silenceDeprecations: ['legacy-js-api', 'import'],
        }
      }
    },
    /*
    ** You can extend webpack config here
    */
    modules: [
      '@nuxtjs/moment'
    ],
    plugins: [

    ],
    extend(config, ctx) {

    }
  }
}

