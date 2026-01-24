import RestApi from '../methods/api'

export const state = () => ({
  settings: [],
  totalRowsCount: 0
})

export const mutations = {
  set(state, {content,totalElements}) {
    state.settings = content
    state.totalRowsCount = totalElements
  },
  add(state, payload) {
    state.settings.unshift(payload)
    state.totalRowsCount++
  },
  update(state, payload) {
    const index = state.settings.findIndex(p => p.id === payload.id);
    if (index !== -1) {
      state.settings.splice(index, 1, payload);
    }
  },
  delete(state, payload) {
    state.settings = state.settings.filter(setting => setting.id !== payload.id)
    state.totalRowsCount--
  }
}

export const getters = {
  codes(state) {
    return state.settings.map(({ code }) => code)
  }
}
