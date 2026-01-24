import axios from 'axios'
import {
  GeoServerWorkspace,
  GeoServerRest,
  GeoServerDataStore,
  GeoServerUser,
  GeoServerPassword,
  GeoServerWorkspaceName
} from '../constants'

export const actions = {
  async nuxtServerInit({ commit }) {
    if (!GeoServerUser || !GeoServerPassword) {
      console.warn('GeoServer credentials not configured. Skipping initialization.')
      return
    }

    const auth = {
      username: GeoServerUser,
      password: GeoServerPassword
    }

    const headers = {
      'Content-Type': 'application/json',
      Accept: 'application/json'
    }

    try {
      await axios.get(`${GeoServerRest}/workspaces/${GeoServerWorkspaceName}`, {
        auth,
        headers,
        withCredentials: true
      })
    } catch (error) {
      if (error.response && error.response.status === 404) {
        await axios.post(`${GeoServerRest}/workspaces`, GeoServerWorkspace, {
          auth,
          headers,
          withCredentials: true
        }).catch(e => console.error('Failed to create workspace:', e.message))
      }
    }

    try {
      await axios.get(`${GeoServerRest}/workspaces/${GeoServerWorkspaceName}/datastores/database`, {
        auth,
        headers,
        withCredentials: true
      })
    } catch (error) {
      if (error.response && error.response.status === 404) {
        await axios.post(`${GeoServerRest}/workspaces/${GeoServerWorkspaceName}/datastores`, GeoServerDataStore, {
          auth,
          headers,
          withCredentials: true
        }).catch(e => console.error('Failed to create datastore:', e.message))
      }
    }
  }
}
