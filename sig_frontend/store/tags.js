import RestApi from '../methods/api'

export const state = () => ({
  tags: [],
  totalRowsCount: 0
})

export const mutations = {
  set(state, {content,totalElements}) {
    state.tags = content
    state.totalRowsCount = totalElements
  },
  add(state, payload) {
    state.tags.push(payload)
  },
  update(state, payload) {
    let index = state.tags.findIndex(l => l.id === payload.id)
    state.tags[index] = payload
  },
  delete(state, id) {
    state.tags = state.tags.filter(tag => tag.id !== id)
  }
}
