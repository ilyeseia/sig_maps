import { frontend } from "../constants"

export const state = () => ({
    isMobile: false,
    isTablet: false,
    baseLayers: null
})

export const mutations = {
    setIsMobile(state, payload) {
        state.isMobile = payload
    },
    setIsTablet(state, payload) {
        state.isTablet = payload
    },
    setBaseLayers(state, payload){
        state.baseLayers = payload
    }
}
export const getters = {
    getIsMobile(state) {
        return state.isMobile
    },
    getIsTablet(state) {
        return state.isTablet
    },
    getBaseLayers(state) {
        return state.baseLayers
    }
}

export const actions = {
    setBaseLayers({ commit, state }, payload) {
        commit('setBaseLayers', payload)
    },
    setDeviceType({ commit, state }, payload) {
        //Temporary checking
        if(!frontend.includes('sig.mjs.gov.dz')){
            if (payload < 600) {
                commit('setIsMobile', true)
                commit('setIsTablet', false)
            } else if (payload > 600 && payload < 1200) {
                commit('setIsMobile', false)
                commit('setIsTablet', true)
            } else {
                commit('setIsMobile', false)
                commit('setIsTablet', false)
            }
        }
    },
    toggleSideMenu({ commit, state }, { sideBar, wrapper, resize }) {
        if (resize) {
            if (!state.isMobile && !state.isTablet) {
                sideBar.style.left = 0
                if(wrapper) wrapper.style.marginLeft = '256px'
            } else {
                sideBar.style.left = '-100vw'
                if(wrapper) wrapper.style.marginLeft = 0
            }
        } else {
            if (sideBar.style.left === '-100vw' || !sideBar.style.left) {
                sideBar.style.left = 0
                if (state.isMobile || state.isTablet) {
                    if(wrapper) wrapper.style.marginLeft = '0'
                } else {
                    if(wrapper) wrapper.style.marginLeft = '256px'
                }
            } else {
                sideBar.style.left = '-100vw'
                if(wrapper) wrapper.style.marginLeft = 0
            }
        }
    }
}