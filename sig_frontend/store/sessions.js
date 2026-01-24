export const state = () => ({
  sessions: [],
  totalRowsCount: 0
})

export const mutations = {
  set(state, {content,totalElements}) {
    state.sessions       = content
    state.totalRowsCount = totalElements
  }
}
