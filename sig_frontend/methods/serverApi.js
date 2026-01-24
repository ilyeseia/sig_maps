import axios from 'axios';
import {
    backend,
    GeoServerWorkspaceName,
    GeoServerRest,
    GeoServerUser,
    GeoServerPassword
  } from '../constants'

export const backApi = axios.create({
    baseURL: `${backend}/`,
    headers: {
      ContentType: 'application/json',
      Accept: 'application/json',
      common: {
        Authorization: `Bearer ${localStorage.getItem('sigToken')}`,
      }
    },
})

//Create Geoserver axios 
export const geoServerApi = axios.create({
    baseURL: `${GeoServerRest}/workspaces/${GeoServerWorkspaceName}/`,
    withCredentials: true,
    headers: {
      Accept: 'application/vnd.ogc.sld+xml',
    },
    auth: { username: `${GeoServerUser}`, password: `${GeoServerPassword}` },
  })