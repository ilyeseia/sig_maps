import axios from 'axios'
import { backend, GeoServerRest, GeoServerWFSURL ,GeoServerUser, GeoServerPassword } from '../../../constants'
function slugify(text)
{
  return text.toString().toLowerCase()
    .replace(/\s+/g, '-')           // Replace spaces with -
    .replace(/[^\w\-]+/g, '')       // Remove all non-word chars
    .replace(/\-\-+/g, '-')         // Replace multiple - with single -
    .replace(/^-+/, '')             // Trim - from start of text
    .replace(/-+$/, '');            // Trim - from end of text
}

export function deleteCoverageStore(layer){
  return new Promise(function(resolve, reject) {
    axios({
      method: 'delete',
      withCredentials: true,
      params : {
        purge :true,
        recurse :true
      },
      headers: {
        Accept: 'application/json'
      },
      url: `${GeoServerRest}/workspaces/limite_admin/coveragestores/${layer.slug}`,
      auth: { username: `${GeoServerUser}`, password: `${GeoServerPassword}` },
      
    })
      .then(response => {
        resolve(response)
      })
      .catch(error => {
        reject(error)
      })
  })

}

export function createCoverageStore(zipFile,filename,format) {
  return new Promise(function(resolve, reject) {
    let layerSlug = slugify(filename)
    axios({
      method: 'put',
      withCredentials: true,
      headers: {
        'Content-Type': 'application/zip',
        Accept: 'application/json'
      },
      params : {
        coverageName :layerSlug
      },
      url: `${GeoServerRest}/workspaces/limite_admin/coveragestores/${layerSlug}/file.${format}`,
      auth: { username: `${GeoServerUser}`, password: `${GeoServerPassword}` }, 
      data: zipFile
    })
      .then(({ data }) => {
        //let layerName = data.coverageStore.url.match(/([^\/]+)(?=\.\w+$)/)[0]
        resolve(data)
      })
      .catch(error => {
        reject(error)
      })
  })
}
