import RestApi from '../methods/api'

export const state = () => ({
  roles: [],
  totalRowsCount : 0
})

export const mutations = {
  set(state, payload) {
    state.roles = payload
    state.totalRowsCount=payload.length
  },
  add(state, payload) {
    state.roles.push(payload)
    state.totalRowsCount++
  },
  update(state, payload) {
    let index = state.roles.findIndex(l => l.id === payload.id)
    state.roles[index] = payload
  },
  delete(state, id) {
    state.roles = state.roles.filter(role => role.id !== id)
    state.totalRowsCount--
  }
}

export const getters = {
  names(state) {
    return state.roles.map(({ name }) => name)
  }
}
