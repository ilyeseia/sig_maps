/*
  CRUD schemas
*/
export const state = () => ({
  maps: [],
  currentMap: {
    layers: [],
    themes: [],
    layersFilter: {}
  },
  layersTotalElements: 0,
  activeLayer: {},
  totalRowsCount: 0,
  mode: 'map',
  drawing: false,
  isReady: false,
  catGetFeatureInfo: true,
  mapVersion: 1
})

export const mutations = {
  updateMapVersion(state) {
    state.mapVersion++;
  },
  setMapIsReady(state, payload) {
    state.isReady = payload;
  },
  setDrawing(state, payload) {
    state.drawing = payload
  },
  set(state, { content, totalElements }) {
    state.maps = content
    state.totalRowsCount = totalElements
  },
  setCurrentMapImage(state, payload) {
    state.currentMap = payload
  },
  setLayers(state, data) {
    const tmpMaps = {}
    Object.assign(tmpMaps, state.currentMap)
    tmpMaps.layers = data
    state.currentMap = tmpMaps
  },
  setThemes(state, data) {
    const tmpMaps = {}
    Object.assign(tmpMaps, state.currentMap)
    tmpMaps.themes = data
    state.currentMap = tmpMaps
  },
  addThemes(state, newTheme) {
    const tmpMaps = {}
    Object.assign(tmpMaps, state.currentMap)
    tmpMaps.themes.push(newTheme)
    state.currentMap = tmpMaps
  },
  deleteTheme(state, themeId) {
    const tmpMaps = {}
    Object.assign(tmpMaps, state.currentMap)
    tmpMaps.themes = tmpMaps.themes.filter(t => t.id !== themeId)
    state.currentMap = tmpMaps
  }, 
  updateTheme(state, theme) {
    const tmpMaps = {}
    Object.assign(tmpMaps, state.currentMap)
    let index = tmpMaps.themes.findIndex(l => l.id === theme.id)
    if (index !== -1) {
      tmpMaps.themes.splice(index, 1, theme)
    }
    state.currentMap = tmpMaps
  },
  setLayerVisible(state, layer) {
    let l = state.currentMap.layers.find(l => l.id === layer.id)
    if (l) l.visible = layer.visible
  },
  updateLayer(state, layer) {
    const tmpMaps = {}
    Object.assign(tmpMaps, state.currentMap)
    let index = tmpMaps.layers.findIndex(l => l.id === layer.id)
    if (index !== -1) {
      tmpMaps.layers.splice(index, 1, layer)
    }
    state.currentMap = tmpMaps
  },
  sortLayers(state, layers) {
    let _layers = state.currentMap.layers

    for (let i = 0; i < _layers.length; i++) {
      const _layer = layers.filter(l => l.id === _layers[i].id)
      _layers[i].order = _layer[0].order
      _layers[i].visible = _layer[0].visible
    }
    _layers.sort((a, b) => b.order - a.order)
  },
  detach(state, newLayers) {
    const tmpMaps = {}
    Object.assign(tmpMaps, state.currentMap)
    tmpMaps.layers = newLayers
    state.currentMap = tmpMaps
  },
  attach(state, newLayers) {
    const tmpMaps = {}
    Object.assign(tmpMaps, state.currentMap)
    newLayers.forEach(newLayer => {
      tmpMaps.layers.unshift(newLayer)
    })
    state.currentMap = tmpMaps
  },
  setCurrentMap(state, { map, mode }) {
    state.currentMap = map
    state.mode = mode
    state.currentMap.layers.sort((a, b) => b.order - a.order)
    state.currentMap.themes = []
  },
  add(state, payload) {
    state.maps.unshift(payload)
    state.totalRowsCount++
  },
  update(state, payload) {
    const index = state.maps.findIndex(p => p.id === payload.id)
    if (index !== -1) {
      state.maps.splice(index, 1, payload)
    }
  },
  delete(state, payload) {
    state.maps = state.maps.filter(map => map.id !== payload.id)
    state.totalRowsCount--
  },
  setActiveLayer(state, layer) {
    state.activeLayer = layer
  },
  deleteUser(state, id) {
    state.myUsers.users = state.myUsers.users.filter(user => user.id != id)
    state.myUsers.count--
  },
  setLayerFilters(state, payload) {
    state.currentMap.layersFilter = payload
  },
  setCanGetFeatureInfo(state, payload) {
    state.catGetFeatureInfo = payload;
  },
}

export const actions = {
  setMapThemes({ commit }, payload) {
    commit('setThemes', payload)
  },
  addMapThemes({ commit }, payload) {
    commit('addThemes', payload)
  },
  updateMapThemes({ commit }, payload) {
    commit('updateTheme', payload)
  },
  deleteMapThemes({ commit }, payload) {
    commit('deleteTheme', payload)
  },
  setMapLayers({commit}, payload){
    commit('setLayers', payload)
  },
  updateMapVersion({ commit }) {
    commit('updateMapVersion')
  },
  updateLayersFilter({ commit, state }, payload) {
    commit('setLayerFilters', payload)
  },
  updateCanGetFeatureInfo({ commit, state }, payload) {
    commit('setCanGetFeatureInfo', payload)
  },
  newGeom({ }, payload) {
    switch (payload) {
      case 'polygon':
        document.querySelector('.leaflet-draw-draw-polygon').click()
        break;
      case 'line':
        document.querySelector('.leaflet-draw-draw-polyline').click()
        break;
      default:
        document.querySelector('.leaflet-draw-draw-marker').click()

    }
  },
  calculateDistance() {
    let deleteMesurementBtn = document.querySelector('.js-deletemarkup')
    if (deleteMesurementBtn != null) {
      deleteMesurementBtn.click()
    }
    document.querySelector(
      '.leaflet-control-measure-interaction'
    ).style.display = 'block'
    document.querySelector('.js-start').click()
  }
}

export const getters = {
  getMapVersion(state) {
    return state.mapVersion
  },
  updateMapVersion(state) {
    return state.mapVersion
  },
  getLayersFilter(state) {
    return state.currentMap.layersFilter
  },
  getCurrentMap(state) {
    return state.currentMap
  },
  getMapIsReady(state) {
    return state.isReady
  },
  getCanFeatureInfo(state){
    return state.catGetFeatureInfo
  },
  names(state) {
    return state.maps.map(({ name }) => name)
  },
  mapLayers(state) {
    //TODO: fix mutation
    return (
      state.currentMap.layers &&
      state.currentMap.layers.sort((a, b) => b.order - a.order)
    )
  },
  mapThemes(state) {
    //TODO: fix mutation
    return state.currentMap.themes.sort((a, b) => a.name.localeCompare(b.name))
  },
  getDefaultMapTheme(state){
    return state.currentMap.themes.find(t => t.isDefault)
  },
  layersSlug(state) {
    return state.currentMap.layers.filter(
      (layer) => layer.typeLimit === 'LAYER'
    ).map(l => {
      return {
        ...l,
        name: l.name && l.name.includes("____") ? l.name.split("____")[1] : l.name
      }
    })
  },
  slugLayerLimitAdmin(state) {
    return state.currentMap.layers
      .filter((layer) => layer.typeLimit === 'ADMIN')
      .map((layer) => layer.slug)
  },
  currentMapPrivacy(state) {
    return (
      state.currentMap.privacy && state.currentMap.privacy
    )
  }
}
