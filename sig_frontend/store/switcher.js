  const  state  = () => ({
    swticher:  false
  });

  const  mutations  = {
    toggle(state) {
      state.swticher  =  !state.swticher;
    }
  };


   const  getters  = {
    getSwticherState(state) {
      return  state.swticher;
    }
  };

  export default {
    state,
    getters,
    mutations,
 
};