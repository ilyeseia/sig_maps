
export const state = () => ({
  layers: [],
  customeIcon:true, 
  layerGroups: [],
  currentLayer: null,
  totalRowsCount: 0,
})

export const mutations = {
  clear(state) {
    state.layers = []
  },
  setRowsCount(state, count) {
    state.totalRowsCount = count
  },
  set(state, { content, totalElements }) {
    state.layers = content
    state.totalRowsCount = totalElements
  },
  add(state, payload) {
    let layer = state.layers.find(l => l.id === payload.id)

    if (layer) {
      let layers = state.layers.filter(l => l.id !== payload.id)
      layers.unshift(payload)
      state.layers = [...layers]
    } else {
      state.layers.unshift(payload)
    }

    state.totalRowsCount++
  },
  update(state, payload) {
    const index = state.layers.findIndex(p => p.id === payload.id);
    if (index !== -1) {
      state.layers.splice(index, 1, payload);
    }
  },
  delete(state, payload) {
    state.layers = state.layers.filter(layer => layer.id !== payload.id)
    state.totalRowsCount--
  },
  addLayerGroup(state, payload) {
    state.layerGroups.push(payload)
  },
  addField(state, newField) {
    state.layers.forEach(layer => {
      layer.fields.push(newField)
    })
  }
}

export const getters = {
  names(state) {
    return state.layers.map(({ name }) => {
      if(name && name.includes("____")){
        return name.spit("____")[1]
      }else{
        return name;
      }
    })
  }
}
