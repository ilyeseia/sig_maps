<template>
  <div class="side-menu-outer" @click="outsideToggle">
    <form class="side-menu-outer__body">
      <div class="side-menu" id="side-menu">
        <div>
          <div v-if="isMobile || isTablet" class="side-menu__app-name">
            <nuxt-link id="Home" to="/"> <strong>GéoPortail</strong>  <strong>MJS</strong></nuxt-link>
          </div>
          <a-menu
            :defaultSelectedKeys="defaultSelectedKeys"
            :defaultOpenKeys="defaultOpenKeys"
            mode="inline"
          >
            <a-menu-item
              v-if="profile.authenticated && (isMobile || isTablet)"
              key="map-link"
              class="menu-item-parent"
            >
              <nuxt-link id="maps-link" to="/dashboard/maps">
                <b-icon
                  pack="fas"
                  icon="map-marked-alt"
                  style="margin-right: 5px"
                />Carte
              </nuxt-link>
            </a-menu-item>
            <a-sub-menu
              v-if="
                profile.roles.includes('ROLE_ADMIN') ||
                isIncludes('USER') ||
                isIncludes('GROUP')
              "
              mode="inline"
              key="sub1"
              class="menu-item-parent"
            >
              <span slot="title">
                <b-icon pack="fas" icon="user-cog" />
                <span>Administration</span>
              </span>
              <a-menu-item
                v-if="
                  ((isMobile || isTablet) &&
                   ( profile.roles.includes('ROLE_ADMIN')) ||
                  isIncludes('USER'))
                "
                key="dashboard-link"
              >
                <nuxt-link id="dashboard-link" to="/dashboard">
                  <b-icon
                    pack="fas"
                    icon="chart-line"
                    style="margin-right: 5px"
                  />Dashboard
                </nuxt-link>
              </a-menu-item>
              <a-menu-item
                v-if="
                  profile.roles.includes('ROLE_ADMIN') || isIncludes('USER')
                "
                key="users-link"
              >
                <nuxt-link id="users-link" to="/dashboard/admin/users">
                  <b-icon
                    pack="fas"
                    icon="user"
                    style="margin-right: 5px"
                  />Utilisateurs
                </nuxt-link>
              </a-menu-item>
              <a-menu-item
                v-if="
                  profile.roles.includes('ROLE_ADMIN') || isIncludes('GROUP')
                "
                key="groups-link"
              >
                <nuxt-link id="groups-link" to="/dashboard/admin/groups">
                  <b-icon
                    pack="fas"
                    icon="user-friends"
                    style="margin-right: 5px"
                  />Groupes
                </nuxt-link>
              </a-menu-item>
              <a-menu-item
                v-if="profile.roles.includes('ROLE_ADMIN')"
                key="session-link"
              >
                <nuxt-link id="session-link" to="/dashboard/admin/sessions">
                  <b-icon
                    pack="fas"
                    icon="user-lock"
                    style="margin-right: 5px"
                  />Sessions
                </nuxt-link>
              </a-menu-item>
              <a-menu-item
                v-if="profile.roles.includes('ROLE_ADMIN')"
                key="log-link"
              >
                <nuxt-link id="log-link" to="/dashboard/admin/user_logs">
                  <b-icon
                    pack="fas"
                    icon="history"
                    style="margin-right: 5px"
                  />Journal
                </nuxt-link>
              </a-menu-item>
            </a-sub-menu>
            <a-menu-item
              v-if="profile.roles.includes('ROLE_ADMIN') || isIncludes('LAYER')"
              key="layer-link"
              class="menu-item-parent"
            >
              <nuxt-link id="layers-link" to="/dashboard/layers">
                <b-icon
                  pack="fas"
                  icon="layer-group"
                  style="margin-right: 5px"
                />Couches
              </nuxt-link>
            </a-menu-item>
            <a-sub-menu
              v-if="
                profile.roles.includes('ROLE_ADMIN') || isIncludes('SETTINGS')
              "
              mode="inline"
              key="sub2"
              class="menu-item-parent"
            >
              <span slot="title">
                <b-icon pack="fas" icon="cogs" />
                <span>Paramétres</span>
              </span>
              <a-menu-item
                v-if="
                  profile.roles.includes('ROLE_ADMIN') || isIncludes('SETTINGS')
                "
                key="settingsType-link"
              >
                <nuxt-link id="settingsType-link" to="/dashboard/settingsType">
                  <b-icon
                    pack="fas"
                    icon="industry"
                    style="margin-right: 5px"
                  />Type des paramétres
                </nuxt-link>
              </a-menu-item>
              <a-menu-item
                v-if="
                  profile.roles.includes('ROLE_ADMIN') || isIncludes('SETTINGS')
                "
                key="settings-link"
              >
                <nuxt-link id="settings-link" to="/dashboard/settings">
                  <b-icon
                    pack="fas"
                    icon="list"
                    style="margin-right: 5px"
                  />Liste des paramètres
                </nuxt-link>
              </a-menu-item>
            </a-sub-menu>
            <a-menu-item
              v-if="
                profile.roles.includes('ROLE_ADMIN') || isIncludes('RESOURCE')
              "
              key="referentiels-link"
              class="menu-item-parent"
            >
              <nuxt-link id="referentiels-link" to="/dashboard/referentiels">
                <b-icon
                  pack="fas"
                  icon="server"
                  style="margin-right: 5px"
                />Référentiels
              </nuxt-link>
            </a-menu-item>
            <a-menu-item
              v-if="
                profile.roles.includes('ROLE_ADMIN') || isIncludes('AUDITING')
              "
              key="audit-link"
              class="menu-item-parent"
            >
              <nuxt-link id="layers-link" to="/dashboard/auditing">
                <b-icon
                  pack="fas"
                  icon="info"
                  style="margin-right: 5px"
                />Traçabilité
              </nuxt-link>
            </a-menu-item>
            <a-menu-item
              v-if="
                ((isMobile || isTablet) &&
                  profile.authenticated &&
                  profile.roles.includes('ROLE_ADMIN')) ||
                profile.roles.includes('REPORTING')
              "
              key="reporting-link"
              class="menu-item-parent"
            >
              <nuxt-link id="reportings-link" to="/dashboard/reporting">
                <b-icon
                  pack="fas"
                  icon="chart-bar"
                  style="margin-right: 5px"
                />Reporting
              </nuxt-link>
            </a-menu-item>
            <template v-if="isMobile || isTablet">
              <a-menu-item
                v-if="profile.authenticated"
                key="notification-link"
                class="menu-item-parent"
              >
                <nuxt-link
                  id="notifications-link"
                  to="/dashboard/notifications"
                >
                  <b-icon
                    pack="fas"
                    icon="bell"
                    style="margin-right: 5px"
                  />Notifications
                </nuxt-link>
              </a-menu-item>
              <a-sub-menu
                v-if="profile.authenticated"
                mode="inline"
                key="sub3"
                class="menu-item-parent"
              >
                <span slot="title">
                  <b-icon pack="fas" icon="user-cog" />
                  <span>Compte</span>
                </span>
                <a-menu-item key="profile-link" class="menu-item-parent">
                  <nuxt-link
                    id="profiles-link"
                    to="/dashboard/profiles/general"
                  >
                    <b-icon
                      pack="fas"
                      icon="user"
                      style="margin-right: 5px"
                    />Profile
                  </nuxt-link>
                </a-menu-item>
                <a-menu-item key="password-link" class="menu-item-parent">
                  <nuxt-link
                    id="passwords-link"
                    to="/dashboard/profiles/password"
                  >
                    <b-icon
                      pack="fas"
                      icon="key"
                      style="margin-right: 5px"
                    />Mot de passe
                  </nuxt-link>
                </a-menu-item>
              </a-sub-menu>
              <a-menu-item
                v-if="profile.authenticated"
                key="help-link"
                class="menu-item-parent"
              >
                <nuxt-link id="helps-link" to="/dashboard/help" target="_blank">
                  <b-icon
                    pack="fas"
                    icon="question"
                    style="margin-right: 5px"
                  />Aide
                </nuxt-link>
              </a-menu-item>
              <a-menu-item
                key="logout-link"
                class="menu-item-parent"
                @click="logInOut()"
              >
                <b-icon
                  pack="fas"
                  icon="sign-out-alt"
                  :style="`margin-right: 5px; transform: ${
                    profile.authenticated ? 'rotate(180deg)' : 'none'
                  }`"
                />{{
                  profile.authenticated ? 'Se déconnecter' : 'Se connecter'
                }}
              </a-menu-item>
            </template>
          </a-menu>
        </div>
      </div>
    </form>
    <div class="side-menu-outer__footer">
      <h2>KharitaDZ, Powered by <a href="https://eadn.dz/" target="_blank" rel="noreferrer">EADN</a></h2>
    </div>
  </div>
</template>
<script>
import RestApi from '~/methods/api'
import { mapState, mapGetters } from 'vuex'

export default {
  data() {
    return {
      defaultOpenKeys: ['sub1', 'sub2'],
      defaultSelectedKeys: [],
    }
  },
  computed: {
    ...mapState(['profile']),
    ...mapGetters({
      isMobile: 'app/getIsMobile',
      isTablet: 'app/getIsTablet',
    }),
  },
  watch: {
    $route(val) {
      if (val.name !== 'dashboard-viewer') {
        this.$emit('toggleSideMenu', true)
      }
    },
  },
  methods: {
    outsideToggle($event) {
      if (!document.querySelector('.ant-menu').contains($event.target)) {
        this.$emit('toggleSideMenu')
      }
    },
    isIncludes(filterValue) {
      let role = this.profile.roles.filter((role) =>
        role.startsWith(filterValue)
      )
      return role != '' ? true : false
    },
    logInOut() {
      if (this.profile.authenticated) {
        RestApi.logout()
      } else {
        this.$router.push('/auth')
      }
    },
  },
  mounted() {
    let selected = document.querySelector('.nuxt-link-active')
    if (selected) {
      this.defaultSelectedKeys[0] = selected.getAttribute('id')
      if (
        selected.getAttribute('id') === 'settings-link' ||
        selected.getAttribute('id') === 'settingsType-link'
      ) {
        this.defaultOpenKeys = ['sub2']
      }
    }
  },
}
</script>

<style  lang="scss">
.side-menu-outer {
  position: fixed;
  z-index: 10;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  &__body {
    flex-grow: 1;
  }
  &__footer {
    padding: 1rem 0 0.8rem 1.5rem;
    z-index: 999;
    background-color: white;
    width: 250px;
    h2{
      font-size: .85rem;
      font-weight: bold;
    }
  }
  @include respond('tab-land') {
    width: 100vw;
    left: -100vw;
    background-color: rgba(0, 0, 0, 0.3);
  }
  transition: left 0.3s ease-out;
}
.side-menu {
  position: absolute;
  top: 5rem;
  left: 0;
  height: calc(100vh - 3.7rem);
  overflow-y: auto;
  z-index: 10;
  overflow-x: visible;
  &__app-name {
    background-color: white;
    font-size: 1.4rem;
    padding-top: 1.5rem;
    padding-left: 1.5rem;
    padding-bottom: 1rem;
    border-bottom: 1px solid $color-grey-light;
    color: $color-primary;
    strong:nth-child(1) {
      color: #000;
    }
  }
  .ant-menu {
    width: 256px;
    padding-top: 1rem;
    border-right: 1px solid #e8e8e8;
    ul{
      margin-top: -1rem;
    }
    @include respond('tab-port') {
      padding-top: 0;
    }
  }
  ul {
    min-height: 100%;
    padding: 2rem 0 2.5rem 0;
  }
  @include hideScroll;

  > div {
    height: calc(100vh - 5rem);
  }
  .nuxt-link-exact-active,
  .ant-menu-submenu-selected,
  .ant-menu-submenu-title:hover {
    color: $color-primary !important;
  }
  .ant-menu-submenu-selected .ant-menu-submenu-arrow::after,
  .ant-menu-submenu-selected .ant-menu-submenu-arrow::before,
  .ant-menu-submenu-title:hover .ant-menu-submenu-arrow::after,
  .ant-menu-submenu-title:hover .ant-menu-submenu-arrow::before {
    background: $color-primary !important;
  }
  .ant-menu-item a {
    text-decoration: none;
    &:hover {
      color: $color-primary !important;
    }
  }
  .ant-menu-inline .ant-menu-item:after {
    border-color: $color-primary;
  }
}
</style>
