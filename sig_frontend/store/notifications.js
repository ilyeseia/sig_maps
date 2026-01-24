export const state = () => ({
  notificationsViewed: [],
  notificationsViewedCount: 0,
  notificationsNotViewed: [],
  notificationsNotViewedCount: 0
})

export const mutations = {
  setViewed(state, { content, totalElements }) {
    state.notificationsViewed = content
    state.notificationsViewedCount = totalElements
  },
  setNotViewed(state, { content, totalElements }) {
    state.notificationsNotViewed = content
    state.notificationsNotViewedCount = totalElements
  },
  add(state, payload) {
    state.notificationsNotViewed.unshift(payload)
    state.notificationsNotViewedCount++
  },
  updateToViewed(state, payload) {
    state.notificationsNotViewed = state.notificationsNotViewed.filter(
      notification => notification.id !== payload.id
    )
    state.notificationsNotViewedCount--

    state.notificationsViewed.push(payload)
    state.notificationsViewedCount++
  },
  updateToNotViewed(state, payload) {
    state.notificationsViewed = state.notificationsViewed.filter(
      notification => notification.id !== payload.id
    )
    state.notificationsViewedCount--

    state.notificationsNotViewed.push(payload)
    state.notificationsNotViewedCount++
  },
  update(state, payload) {
    const index = state.notifications.findIndex(p => p.id === payload.id)
    if (index !== -1) {
      state.notifications.splice(index, 1, payload)
    }
  },
  delete(state, id) {
    state.notifications = state.notifications.filter(
      notification => notification.id !== id
    )
    state.totalRowsCount--
  }
}

export const getters = {
  codes(state) {
    return state.notifications.map(({ code }) => code)
  }
}
