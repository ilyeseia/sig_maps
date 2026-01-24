
export const state = () => ({
  auditRows: [],
  totalRowsCount: 0
})

export const mutations = {
  set(state, {content,totalElements}) {
    state.auditRows = content
    state.totalRowsCount = totalElements
  },
}