
export const state = () => ({
    features: [],
    newFeature: null,
    totalRowsCount : 0
})

export const mutations = {
    clear(state){
        state.features = []
    },
    add(state, payload) {
        state.features.push(payload)
    },
    addNewFeature(state, payload) {
        state.newFeature = payload
    },
    update(state, { layerId, newProps, newPosition }) {
        const index = state.features.findIndex(({ id }) => id === layerId)

        state.features[index].properties = newProps
        state.features[index].geometry.coordinates = newPosition
    }
}

