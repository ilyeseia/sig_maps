const pkg = require('./package.json')

module.exports = {
  ssr: false,
  target: 'static',
  
  router: {
    base: '/'
  },
  
  server: {
    port: process.env.PORT || 3000,
    host: '0.0.0.0'
  },

  // Environment variables
  env: {
    API_BASE_URL: process.env.API_BASE_URL || 'http://localhost:8080',
    NODE_ENV: process.env.NODE_ENV || 'development'
  },

  // Public runtime config
  publicRuntimeConfig: {
    axios: {
      browserBaseURL: process.env.API_BASE_URL || 'http://localhost:8080'
    }
  },

  // Private runtime config
  privateRuntimeConfig: {
    axios: {
      baseURL: process.env.API_BASE_URL || 'http://backend:8080'
    }
  },

  // Headers of the page
  head: {
    title: 'SIG Maps - Geographic Information System',
    meta: [
      { charset: 'utf-8' },
      { name: 'viewport', content: 'width=device-width, initial-scale=1' },
      { hid: 'description', name: 'description', content: pkg.description },
      { name: 'format-detection', content: 'telephone=no' }
    ],
    link: [
      { rel: 'icon', type: 'image/x-icon', href: '/kharitadz-logo-offecial.ico' },
      { rel: 'stylesheet', href: 'https://fonts.googleapis.com/css2?family=Outfit:wght@400;500;600;700;800&family=DM+Sans:wght@400;500;600;700&family=Righteous&display=swap' }
    ]
  },

  // Loading bar
  loading: './components/Loading.vue',

  // Global CSS
  css: [
    'primevue/resources/primevue.css',
    'primevue/resources/themes/fluent-light/theme.css',
    'primeicons/primeicons.css',
    'ant-design-vue/dist/antd.min.css',
    '@fortawesome/fontawesome-free/css/all.min.css',
    'bootstrap/dist/css/bootstrap.min.css',
    '@/assets/sass/main.scss'
  ],

  styleResources: {
    scss: ['./assets/sass/main.scss']
  },

  // Plugins
  plugins: [
    { src: '~/plugins/leaflet', ssr: false },
    { src: '~/plugins/antd' },
    { src: '~/plugins/persistedState.client.js' },
    { src: '~/plugins/vue-moment.js' }
  ],

  // Nuxt.js modules
  modules: [
    '@nuxtjs/axios',
    'nuxt-buefy',
    '@nuxtjs/dotenv',
    'vue-social-sharing/nuxt',
    '@nuxtjs/style-resources',
    '@nuxtjs/moment'
  ],

  // Axios configuration
  axios: {
    baseURL: process.env.API_BASE_URL || 'http://localhost:8080',
    timeout: 30000,
    headers: {
      'Content-Type': 'application/json'
    }
  },

  // Build configuration
  build: {
    loaders: {
      scss: {
        implementation: require('sass'),
        sassOptions: {
          quietDeps: true,
          silenceDeprecations: ['legacy-js-api', 'import']
        }
      }
    },
    extend(config, ctx) {
      // Add custom webpack config if needed
    }
  },

  // Generate configuration
  generate: {
    fallback: true,
    exclude: [
      /^\/api/
    ]
  }
}
