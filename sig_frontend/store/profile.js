import { backApi } from '../methods/serverApi'
import RestApi from '../methods/api'

export const state = () => ({
  authenticated: false,
  roles: [],
  avatar: null,
  username: '',
  notifications: [],
  notificationsCount: 0,
  groups: [],
  layers: [],
  maps: []
})
export const getters = {
  getNotifications(state) {
    return state.notifications.filter(n => !n.viewed).slice(0, 10).sort((a, b) => b.createDate - a.createDate).map(n => {
      if(n.object === "Couche" || n.object === "Carte"){
        return{
          ...n,
          message: n.message.includes("____") ?  n.message.slice(0, 18) + n.message.slice(33, -1) : n.message
        }
      }else{
        return n
      }
    })
  }
}
export const mutations = {
  setNotifications(state, payload) {
    state.notifications = payload
  },
  setNotificationsToViewed(state, payload) {
    state.notifications = state.notifications.filter(
      notification => notification.id !== payload.id
    )
    state.notificationsCount--
  },
  setNotificationsToNotViewed(state, payload) {
    state.notifications.unshift(payload)
    state.notificationsCount++
  },
  login(
    state,
    { claims, username, avatar, notifications, layers, groups, maps }
  ) {
    state.authenticated = true
    state.username = username
    state.avatar = avatar
    state.roles = claims
    state.layers = layers
    state.groups = groups
    state.maps = maps
    state.notifications = notifications.content
    let newNotifications = state.notifications.filter(
      notification => notification.viewed === false
    )
    state.notificationsCount = notifications.totalElements
  },
  logout(state) {
    state.authenticated = false
    state.username = null
    state.avatar = null
    state.notifications = null
    state.newNotificationsCount = null
    state.users = []
    state.roles = []
  },
  setLayers(state, payload) {
    state.layers.push(payload)
  },
  deleteLayer(state, payload) {
    state.layers = state.layers.filter(layer => layer.id !== payload)
  },
}

export const actions = {
  refresh({ commit, state }) {
    return RestApi.refreshToken(state.username)
      .then(({ accessToken, refreshToken }) => {
        localStorage.setItem('sigToken', accessToken)
        localStorage.setItem('refreshToken', refreshToken)
        backApi.defaults.headers.common[
          'Authorization'
        ] = `Bearer ${accessToken}`
        RestApi.getCurrentUser().then(user => {
          commit('login', user)
          RestApi.enableBackendSync(accessToken, user.claims, commit)
        })
      })
      .catch(error => {})
  }
}
