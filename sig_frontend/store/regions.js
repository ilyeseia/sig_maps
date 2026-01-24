export const state = () => ({
  regions: []
})

export const mutations = {
  clear(state) {
    state.regions = []
  },

  set(state, payload) {
    state.regions = payload
  }
}