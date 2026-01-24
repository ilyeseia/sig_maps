import RestApi from '../methods/api'

export const state = () => ({
  allResources: [],
  resources: [],
  totalRowsCount: 0
})

export const getters = {
  getResources(state) {
    return state.resources()
  }
}

export const mutations = {
  set(state, { content, totalElements }) {
    state.resources = content
    state.totalRowsCount = totalElements
  },
  setAll(state, payload) {
    state.allResources = payload
  },
  add(state, payload) {
    state.resources.push(payload)
    state.totalRowsCount++
  },
  update(state, payload) {
    const index = state.resources.findIndex(p => p.id === payload.id);
    if (index !== -1) {
      state.resources.splice(index, 1, payload);
    }
  },
  delete(state, payload) {
    state.resources = state.resources.filter(resource => resource.id !== payload.id)
    state.totalRowsCount--
  }
}