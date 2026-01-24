import RestApi from '../methods/api'

export const state = () => ({
 logs: [],
  totalRowsCount: 0
})

export const mutations = {
  set(state, {content,totalElements}) {
    state.logs = content
    state.totalRowsCount = totalElements
  }
}
