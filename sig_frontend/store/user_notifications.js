
export const state = () => ({
  user_notifications: [],
  totalRowsCount: 0
})

export const mutations = {
  set(state, {content,totalElements}) {
    state.user_notifications = content
    state.totalRowsCount = totalElements
  },
  add(state, payload) {
    state.user_notifications.push(payload)
    state.totalRowsCount++
  },
  update(state, payload) {
    let index = state.user_notifications.findIndex(l => l.id === payload.id)
    state.user_notifications[index] = payload
  },
  delete(state, id) {
    state.user_notifications = state.user_notifications.filter(setting => setting.id !== id)
    state.totalRowsCount--
  }
}
