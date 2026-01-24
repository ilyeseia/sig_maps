
export const state = () => ({
  users: [],
  totalRowsCount: 0,
  avatars : []
})

export const getters = {
  getUsers: (state) => {
    return state.users
  },
  getTotalRowsCount: (state) => {
    return state.totalRowsCount
  }
}

export const mutations = {
  set(state, {content,totalElements}) {
    state.users = content
    state.totalRowsCount = totalElements
    state.avatars = state.users.map((user) => user.avatar )
  },
  add(state, payload) {
    state.users.unshift(payload)
    state.totalRowsCount++
  },
  update(state, payload) {
    let index = state.users.findIndex(l => l.id === payload.id)
    state.users[index] = payload
  },
  delete(state, payload) {
    state.users = state.users.filter(user => user.id !== payload.id)
    state.totalRowsCount--
  }
}
