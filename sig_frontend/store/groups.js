export const state = () => ({
  groups: [],
  totalRowsCount: 0
})

export const mutations = {
  set(state, { content, totalElements }) {
    state.groups = content
    state.totalRowsCount = totalElements
  },
  add(state, payload) {
    state.groups.unshift(payload)
    state.totalRowsCount++
  },
  update(state, payload) {
    const index = state.groups.findIndex(p => p.id === payload.id)
    if (index !== -1) {
      state.groups.splice(index, 1, payload)
    }
  },
  delete(state, payload) {
    state.groups = state.groups.filter(group => group.id !== payload.id)
    state.totalRowsCount--
  }
}

export const getters = {
  names(state) {
    return state.groups.map(({ name }) => name)
  }
}
