import RestApi from '../methods/api'

export const state = () => ({
  permissions: [],
  totalRowsCount : 0
})

export const mutations = {
  set(state, payload) {
    state.permissions = payload
    state.totalRowsCount=payload.length
  },
  add(state, payload) {
    state.permissions.push(payload)
    state.totalRowsCount++
  },
  update(state, payload) {
    let index = state.permissions.findIndex(l => l.id === payload.id)
    state.permissions[index] = payload
  },
  delete(state, id) {
    state.permissions = state.permissions.filter(role => role.id !== id)
    state.totalRowsCount--
  }
}

export const getters = {
  names(state) {
    return state.permissions.map(({ name }) => name)
  }
}
