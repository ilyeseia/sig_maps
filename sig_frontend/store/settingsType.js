import RestApi from '../methods/api'

export const state = () => ({
  settingsType: [],
  totalRowsCount: 0
})

export const mutations = {
  set(state, {content,totalElements}) {
    state.settingsType = content
    state.totalRowsCount = totalElements
  },
  add(state, payload) {
    state.settingsType.unshift(payload)
    state.totalRowsCount++
  },
  update(state, payload) {
    const index = state.settingsType.findIndex(p => p.id === payload.id);
    if (index !== -1) {
      state.settingsType.splice(index, 1, payload);
    }
  },
  delete(state, payload) {
    state.settingsType = state.settingsType.filter(setting => setting.id !== payload.id)
    state.totalRowsCount--
  }
}

export const getters = {
  codes(state) {
    return state.settingsType.map(({ code }) => code)
  }
}
