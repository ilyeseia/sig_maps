<template>
  <nav class="navbar" role="navigation" aria-label="main navigation">
    <div class="navbar-brand align-items-center">
      <a href="https://www.mjs.gov.dz/index.php" class="company-logo">
        <img src="@/assets/logo_mjs_n.png" />
      </a>
      <div class="d-flex navbar-mobile-actions" v-if="isMobile || isTablet">
        <div
          v-if="currentMap && $route.matched[0].path.indexOf('viewer') > 0"
          id="mapActionsWrapper"
          class="map-actions-wrapper"
        >
          <div class="map-actions">
            <b-tooltip
              :label="currentMap.name"
              type="is-dark"
              position="is-bottom"
            >
              <h2 class="map-name">{{ currentMap.name }}</h2>
            </b-tooltip>
            <b-tooltip v-if="mapIsReady" label="Rechercher" type="is-dark" position="is-left">
              <Button
                icon="pi pi-search"
                id="search"
                class="p-button p-button-info btn-search"
                @click="toggleDisplay(false)"
              />
            </b-tooltip>
          </div>
        </div>
        <span v-if="profile.authenticated" class="notification-dropdown">
          <a-dropdown
            :overlayStyle="{
              width: isMobile ? '100vw' : '350px',
              'margin-top': '4rem',
              'padding-left': '2.5vw',
              'padding-right': '2.5vw',
              overflow: 'hidden auto',
              height: 'fit-content',
              'max-height': '75vh',
              'border-radius': '5px',
              position: 'fixed',
              right: '0 !important',
              background: 'transparent',
            }"
            :trigger="['click']"
          >
            <a-button class="notification-btn" shape="circle">
              <a-badge :count="profile.notificationsCount"
                ><a-avatar
                  :size="40"
                  id="bell"
                  style="background: #fff"
                  shape="circle"
                  icon="bell"
              /></a-badge>
            </a-button>
            <a-menu
              class="notification-list"
              slot="overlay"
              @click="handleMenuClick"
            >
              <a-menu-item
                v-for="notification in getNotifications"
                :key="notification.id"
              >
                <div class="notification-item">
                  <div>
                    <span
                      :class="[
                        'icon',
                        { 'has-text-danger': notification.level == 'SEVERE' },
                        {
                          'has-text-warning': notification.level == 'WARNING',
                        },
                        { 'has-text-info': notification.level == 'INFO' },
                      ]"
                    >
                      <i
                        class="fas"
                        :class="[
                          'icon',
                          {
                            'fa-exclamation-triangle':
                              notification.level == 'SEVERE',
                          },
                          {
                            'fa-exclamation-triangle':
                              notification.level == 'WARNING',
                          },
                          { 'fa-info-circle': notification.level == 'INFO' },
                        ]"
                      ></i>
                    </span>
                  </div>
                  <div>
                    <p v-html="notification.message"></p>
                    <span class="notification--date">{{
                      notification.createDate | moment('from', 'now')
                    }}</span>
                  </div>
                </div>
              </a-menu-item>
              <a-menu-item
                v-if="profile.notifications.length === 0"
                class="hint-message"
              >
                <b-message type="is-info"
                  >Il n'y a aucune notification non lue à afficher !</b-message
                >
              </a-menu-item>
              <a-menu-item key="1" style="text-align: center">
                <nuxt-link
                  id="notification-all"
                  to="/dashboard/notifications"
                  class="notification-view-all"
                >
                  <span>Voir tous</span>
                </nuxt-link>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>
        <a
          id="humbuerger-menu"
          role="button"
          class="navbar-burger burger"
          aria-label="menu"
          aria-expanded="false"
          data-target="navbarBasicExample"
          @click="toggleSideMenu"
        >
          <span style="color: white; hieght: 3px" aria-hidden="true"></span>
          <span style="color: white; hieght: 5px" aria-hidden="true"></span>
          <span style="color: white; hieght: 5px" aria-hidden="true"></span>
        </a>
      </div>
    </div>
    <div id="navbar-menu" :class="['navbar-menu', { 'is-active': isActive }]">
      <div v-if="profile.authenticated" class="navbar-start">
        <div v-if="getVisibility" class="btn-wrapper">
          <b-tooltip
            label="Basculer le menu"
            type="is-dark"
            position="is-right"
          >
            <div class="btn-toggle" @click="toggleSideMenu">
              <i class="fas fa-bars"></i>
            </div>
          </b-tooltip>
        </div>
        <nuxt-link
          id="link-to-administration"
          v-if="profile.authenticated"
          :class="[
            {
              activeTab:
                this.$route.matched[0].path.indexOf('maps') < 0 &&
                this.$route.matched[0].path.indexOf('help') < 0 &&
                this.$route.matched[0].path.indexOf('notifications') < 0 &&
                this.$route.matched[0].path.indexOf('reporting') < 0 &&
                this.$route.matched[0].path.indexOf('dashboard') > -1 &&
                this.$route.matched[0].path.indexOf('viewer') < 0,
            },
            'navbar-item',
          ]"
          to="/dashboard"
          exact
        >
          <b-icon
            pack="fas"
            icon="user-cog"
            style="margin-right: 5px"
          />Administration
        </nuxt-link>
        <nuxt-link
          id="link-to-viewer"
          :class="[
            {
              activeTab:
                this.$route.matched[0].path.indexOf('maps') > -1 ||
                this.$route.matched[0].path.indexOf('viewer') > -1,
            },
            'navbar-item',
          ]"
          exact
          to="/dashboard/maps"
        >
          <b-icon
            pack="fas"
            icon="map-marked-alt"
            style="margin-right: 5px"
          />Cartes
        </nuxt-link>
        <!--  <nuxt-link id="link-to-home" class="navbar-item" to="/dashboard/reports">
          <b-icon pack="fas" icon="chart-pie" style="margin-right:5px"/>Reporting
        </nuxt-link> -->
        <nuxt-link
          id="link-to-administration"
          v-if="
            (profile.authenticated && profile.roles.includes('ROLE_ADMIN')) ||
            profile.roles.includes('REPORTING')
          "
          :class="[
            {
              activeTab: this.$route.matched[0].path.indexOf('reporting') > -1,
            },
            'navbar-item',
          ]"
          to="/dashboard/reporting"
          exact
        >
          <b-icon
            pack="fas"
            icon="chart-bar"
            style="margin-right: 5px"
          />Reporting
        </nuxt-link>

        <nuxt-link
          id="link-to-notifications"
          v-if="profile.authenticated"
          :class="[
            {
              activeTab:
                this.$route.matched[0].path.indexOf('notifications') > -1,
            },
            'navbar-item',
          ]"
          to="/dashboard/notifications"
          exact
        >
          <b-icon
            pack="fas"
            icon="bell"
            style="margin-right: 5px"
          />Notifications
        </nuxt-link>
      </div>

      <div class="navbar-end">
         <div id="mapSearchWrapper" v-if="$route.matched[0].path.indexOf('viewer') > 0">

         </div>
        <div v-if="!profile.authenticated" class="navbar-item">
          <div id="login-logout-button" class="buttons" @click="openLogin()">
            <a
              id="public-login"
              :class="[
                'button',
                'is-light',
                { 'is-success': profile.authenticated },
                { 'is-loading': isLoading },
              ]"
              >Login</a
            >
          </div>
        </div>
        <div class="navbar-item" v-if="profile.authenticated">
          <span class="notification-dropdown" style="margin-right: 24px">
            <a-dropdown
              :overlayStyle="{
                'box-shadow':
                  '0 12px 28px 0 #00000033, 0 2px 4px 0 #0000001a, inset 0 0 0 1px #ffffff80',
                left: 'calc(100vw - 415px) !important',
                width: '400px',
                overflow: 'hidden auto',
                height: 'fit-content',
                'max-height': '60vh',
                'border-radius': '5px',
                position: 'fixed',
                background: '#fff',
              }"
              :trigger="['click']"
            >
              <a-button shape="circle" size="large">
                <a-badge :count="profile.notificationsCount"
                  ><a-avatar
                    :size="40"
                    id="bell"
                    style="background: #fff"
                    shape="circle"
                    icon="bell"
                /></a-badge>
              </a-button>
              <a-menu
                class="notification-list"
                slot="overlay"
                @click="handleMenuClick"
              >
                <a-menu-item
                  v-for="notification in getNotifications"
                  :key="notification.id"
                >
                  <div class="notification-item">
                    <div>
                      <span
                        :class="[
                          'icon',
                          { 'has-text-danger': notification.level == 'SEVERE' },
                          {
                            'has-text-warning': notification.level == 'WARNING',
                          },
                          { 'has-text-info': notification.level == 'INFO' },
                        ]"
                      >
                        <i
                          class="fas"
                          :class="[
                            'icon',
                            {
                              'fa-exclamation-triangle':
                                notification.level == 'SEVERE',
                            },
                            {
                              'fa-exclamation-triangle':
                                notification.level == 'WARNING',
                            },
                            { 'fa-info-circle': notification.level == 'INFO' },
                          ]"
                        ></i>
                      </span>
                    </div>
                    <div>
                      <p v-html="notification.message"></p>
                      <span class="notification--date">{{
                        notification.createDate | moment('from', 'now')
                      }}</span>
                    </div>
                  </div>
                </a-menu-item>
                <a-menu-item
                  v-if="profile.notifications.length === 0"
                  class="hint-message"
                >
                  <b-message type="is-info"
                    >Il n'y a aucune notification non lue à afficher
                    !</b-message
                  >
                </a-menu-item>
                <a-menu-item key="1" style="text-align: center">
                  <nuxt-link
                    id="notification-all"
                    to="/dashboard/notifications"
                    class="notification-view-all"
                  >
                    <span>Voir tous</span>
                  </nuxt-link>
                </a-menu-item>
              </a-menu>
            </a-dropdown>
          </span>
          <span>
            <a-dropdown :trigger="['click']">
              <a-button shape="circle" size="large">
                <a-avatar id="profile" style="background: #008b8b" :size="40">{{
                  profile.username.toUpperCase()[0]
                }}</a-avatar>
              </a-button>
              <a-menu slot="overlay">
                <a-menu-item key="0">
                  <a-avatar shape="square" :src="profile.avatar" :size="40" />
                  Connecté en tant que {{ profile.username }}
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item key="1">
                  <nuxt-link id="profile-link" to="/dashboard/profiles/general">
                    <b-icon
                      pack="fas"
                      icon="user"
                      style="margin-right: 5px"
                    />Votre Profil
                  </nuxt-link>
                </a-menu-item>
                <a-menu-item key="2">
                  <nuxt-link
                    id="password-link"
                    to="/dashboard/profiles/password"
                  >
                    <b-icon
                      pack="fas"
                      icon="key"
                      style="margin-right: 5px"
                    />Mot de passe
                  </nuxt-link>
                </a-menu-item>
                <a-menu-item key="3">
                  <!-- <a href="http://localhost:8080/api/v1.0/download/help/KharitaDz_Help.pdf">click</a> -->
                  <nuxt-link
                    target="_blank"
                    id="profile-link"
                    to="/dashboard/help"
                  >
                    <b-icon
                      pack="fas"
                      icon="question"
                      style="margin-right: 5px"
                    />Aide
                  </nuxt-link>
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item key="4" @click="logout()">
                  <b-icon pack="fas" icon="sign-out-alt" />Se
                  déconnecter</a-menu-item
                >
              </a-menu>
            </a-dropdown>
          </span>
        </div>
      </div>
    </div>
  </nav>
</template>

<script>
import RestApi from '~/methods/api'
import LoginForm from '../auth/LoginForm.vue'
import { mapState, mapGetters } from 'vuex'
import Button from 'primevue/button'
export default {
  components: { LoginForm, Button },
  data() {
    return {
      isActive: false,
      isComponentModalActive: false,
      isLoading: false,
      isFullPage: true,
    }
  },
  watch: {
    $route() {
      this.isActive = false
    },
    mapIsReady(val) {
      if (val) {
        this.appendSearchArea()
      }else{
        this.toggleDisplay(true)
      }
    },
  },
  /* Start Header */

  /* End  Header  */
  computed: {
    ...mapState(['profile']),
    ...mapGetters({
      isMobile: 'app/getIsMobile',
      isTablet: 'app/getIsTablet',
      getNotifications: 'profile/getNotifications',
      mapIsReady: 'maps/getMapIsReady',
      currentMap: 'maps/getCurrentMap',
    }),
    getVisibility() {
      return (
        this.$route.matched[0].path.indexOf('help') < 0 &&
        this.$route.matched[0].path.indexOf('maps') < 0 &&
        this.$route.matched[0].path.indexOf('notifications') < 0 &&
        this.$route.matched[0].path.indexOf('reporting') < 0 &&
        this.$route.matched[0].path.indexOf('dashboard') > -1 &&
        this.$route.matched[0].path.indexOf('viewer') < 0
      )
    },
  },
  methods: {
    toggleDisplay(init) {
      let mapActions = document.querySelector('.map-actions')
      let geosearchLeafletBar = document.querySelector(
        '#mapActionsWrapper form'
      )
      if (mapActions && geosearchLeafletBar) {
        if (init || mapActions.classList.contains('mobile-navbar-item--hidden')) {
          console.log("1")
          geosearchLeafletBar.classList.add('mobile-navbar-item--hidden')
          mapActions.classList.remove('mobile-navbar-item--hidden')
        } else {
          console.log("2")
          mapActions.classList.add('mobile-navbar-item--hidden')
          geosearchLeafletBar.classList.remove('mobile-navbar-item--hidden')
        }
      }
    },
    toggleSideMenu() {
      this.$emit('toggleSideMenu')
    },
    open() {
      this.$nextTick(() => {
        this.$nuxt.$loading.start()
        setTimeout(() => this.$nuxt.$loading.finish(), 900)
      })
    },
    appendSearchArea() {
      let searchBar = document.querySelector('.leaflet-control-geosearch form')
      let mapActionsWrapper = null
      let oldSearchBar = null
      if (this.isMobile || this.isTablet) {
        mapActionsWrapper = document.getElementById('mapActionsWrapper')
        oldSearchBar = mapActionsWrapper.querySelector('#mapActionsWrapper form');
      } else {
        mapActionsWrapper = document.querySelector('.navbar-end #mapSearchWrapper')
        oldSearchBar = mapActionsWrapper.querySelector('.navbar-end form');
      }
      if (searchBar) {
        searchBar.classList.add('active')
        searchBar.classList.add('mobile-navbar-item--hidden')
        if(oldSearchBar){
          mapActionsWrapper.removeChild(oldSearchBar)
        }
        if (
          ((this.isMobile || this.isTablet)) ||
          (!this.isMobile && !this.isTablet)
        ) {
         mapActionsWrapper.prepend(searchBar)
        }
        
      }
    },
    openLogin() {
      this.$router.push('/auth')
    },
    handleMenuClick() {},
    logout() {
      RestApi.logout()
    },
  },
}
</script>

<style lang="scss" scoped>
.navbar-start {
  align-items: center;
}
.company-logo {
  img {
    width: 50px;
    @include respond('phone') {
      width: 41px;
    }
  }
}
.btn-wrapper .btn-toggle {
  border: none !important;
  cursor: pointer;
  background: transparent;
  opacity: 0.8;
  margin-right: 1rem;
  margin-left: 0.8rem;
  &:focus {
    border: none;
    background-color: transparent;
  }
  &:hover {
    opacity: 1;
  }
  i {
    font-size: 1.3rem;
    color: white;
    vertical-align: middle;
    border: none;
  }
}
.navbar {
  height: 5rem !important;
  min-height: 5rem !important;
  display: flex;
  justify-content: center;
  overflow: hidden;
}
.navbar #public-login {
  width: 150px;
  background: white;
  font-size: 18px;
  color: $color-primary;
  font-weight: normal;
}
.navbar #profile {
  background: white !important;
  color: $color-primary;
}
.navbar #bell {
  color: $color-primary;
}
.navbar a:hover {
  text-decoration: none;
}
.navbar {
  background: $color-primary;
}
@media (min-width: 1200px) {
  .navbar-brand {
    display: flex;
    flex-shrink: 0;
    min-height: 3.25rem;
  }
  .navbar-menu {
    flex-grow: 1;
    flex-shrink: 0;
  }
}
@include respond('tab-land') {
  .navbar-burger {
    margin-left: 0.8rem;
    display: block !important;
  }
  .navbar #navbar-menu {
    display: none;
  }
  .navbar-brand {
    padding-top: 0rem;
    @include respond('phone') {
      padding-top: 0.2rem;
    }
    width: 100% !important;
    display: flex;
    justify-content: space-between;
  }
  .navbar-menu {
    width: 100%;
    margin-top: 10px;
    background-color: $color-primary;
  }
}
.navbar #link-to-viewer {
  color: white;
}
.navbar #link-to-viewer:hover {
  background: none !important;
  color: rgb(231, 231, 231) !important;
}
.navbar #link-to-administration {
  color: white;
}
.navbar #link-to-administration:hover {
  background: none !important;
  color: rgb(231, 231, 231) !important;
}
.navbar #link-to-notifications {
  color: white;
}
.navbar #link-to-notifications:hover {
  background: none !important;
  color: rgb(231, 231, 231) !important;
}
.navbar-item {
  color: white;
  opacity: 0.8;
  button {
    opacity: 0.8;
    &:hover {
      opacity: 1;
      border-color: transparent !important;
    }
  }
  span:nth-child(1) button {
    .ant-avatar {
      display: flex;
      justify-content: center;
      align-items: center;
    }
  }
  &:link {
    background-color: transparent;
  }
}
.activeTab {
  color: white !important;
  opacity: 1;
  font-weight: bold;
}
.notification-view-all {
  color: $color-primary;
}
.ant-dropdown-menu-item:hover,
.ant-dropdown-menu-submenu-title:hover {
  background-color: rgba($color-primary, 0.03);
}

.notification-list {
  position: relative;
  padding-top: 55px;
  &::before {
    position: absolute;
    top: 0;
    left: 0;
    content: 'Notifications';
    padding: 15px 10px 10px 12px;
    font-weight: bold;
    font-size: 1.5rem;
  }
  .notification-item {
    display: flex;
    flex-direction: row;
    justify-content: flex-start;
    align-items: center;
    padding: 0 0 0 10px;
    > div:nth-child(1) {
      i {
        margin-top: 1rem;
        font-size: 2.3rem;
      }
      margin-right: 1rem;
    }
    > div:nth-child(2) {
      padding: 0 10px;
      p {
        overflow: hidden;
        white-space: pre-line;
        width: 100%;
        font-size: 1rem;
        line-height: 1.2rem;
      }
      .notification--date {
        font-weight: bold;
        color: $color-primary;
        font-size: 0.95rem;
      }
    }
  }
  .hint-message {
    &:hover {
      background-color: transparent;
    }
  }
  .ant-dropdown {
    ul {
      box-shadow: 0 12px 28px 0 #00000033, 0 2px 4px 0 #0000001a,
        inset 0 0 0 1px #ffffff80;
    }
  }
}
</style>
