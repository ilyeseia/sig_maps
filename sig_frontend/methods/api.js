import { backApi as axios } from './serverApi'
import { backend, WebSocketURL, frontend } from '../constants'
import SockJS from 'sockjs-client'
import { Stomp } from '@stomp/stompjs'
import jwt_decode from 'jwt-decode'
import leafletImage from 'leaflet-image'
import { notification } from 'ant-design-vue'

axios.interceptors.response.use(
  response => {
    window.$nuxt.$loading.finish()
    // Return a successful response back to the calling service
    return response
  },
  error => {
    window.$nuxt.$loading.finish()

    // Return any error which is not due to authentication back to the calling service
    if (error.response && error.response.status === 406) {
      return new Promise((resolve, reject) => {
        cleanSession()
        reject(error)
      })
    }

    // Try request again with new token
    else if (error.response && error.response.status === 401) {
      if (localStorage.getItem('refreshToken') == null) {
        cleanSession()
        Promise.reject(error)
      }
      return window.$nuxt.$store
        .dispatch('profile/refresh')
        .then(() => {
          // New request with new token
          const config = error.config
          config.headers['Authorization'] = `Bearer ${localStorage.getItem(
            'sigToken'
          )}`
          if (config.params && config.params['token'] !== undefined) {
            config.params['token'] = `${localStorage.getItem('sigToken')}`
          }
          return new Promise((resolve, reject) => {
            axios
              .request(config)
              .then(response => {
                resolve(response)
              })
              .catch(error => {
                reject(error)
              })
          })
        })
        .catch(error => {
          Promise.reject(error)
        })
    } else {
      return new Promise((resolve, reject) => {
        reject(error)
      })
    }
  }
)

function cleanSession() {
  localStorage.removeItem('sigToken')
  localStorage.removeItem('refreshToken')
  window.$nuxt.$store.commit('profile/logout')
  window.location.href = frontend
}
class RestApi {
  getImageUrl(imageName) {
    return `${backend}/download/${imageName}`
  }

  getFileUrlByFolderByName(folderName, imageName) {
    return `${backend}/download/${folderName}/${imageName}`
  }

  generateDefaultPassword() {
    return new Promise(function (resolve, reject) {
      const storedToken = localStorage.getItem('sigToken')
      axios({
        method: 'POST',
        headers: {
          Authorization: `Bearer ${storedToken}`
        },
        url: `${backend}/users/generatePassword`
      })
        .then(({ data }) => {
          resolve(data)
        })
        .catch(error => reject(error))
    })
  }

  sendMail(message) {
    const storedToken = localStorage.getItem('sigToken')
    axios({
      method: 'POST',
      headers: {
        Authorization: `Bearer ${storedToken}`
      },
      url: `${backend}/users/sendMail`,
      data: message
    })
  }

  changePassword(user) {
    return new Promise(function (resolve, reject) {
      const storedToken = localStorage.getItem('sigToken')
      axios({
        method: 'post',
        url: `${backend}/users/change-password`,
        headers: {
          Authorization: `Bearer ${storedToken}`
        },
        data: user
      })
        .then(({ data }) => {
          resolve(data)
        })
        .catch(error => {
          reject(error)
        })
    })
  }
  resetPassword(newPassword, id) {
    return new Promise(function (resolve, reject) {
      const storedToken = localStorage.getItem('sigToken')
      axios({
        method: 'post',
        url: `${backend}/users/resetPassword/${id}`,
        headers: {
          Authorization: `Bearer ${storedToken}`
        },
        data: newPassword
      })
        .then(({ data }) => {
          resolve(data)
        })
        .catch(error => {
          reject(error)
        })
    })
  }

  getCurrentUser() {
    return new Promise(function (resolve, reject) {
      const storedToken = localStorage.getItem('sigToken')
      let jwtDecoded = jwt_decode(storedToken)
      axios({
        method: 'get',
        url: `${backend}/users/currentUser/${jwtDecoded.sub}`,
        headers: {
          Authorization: `Bearer ${storedToken}`
        }
      })
        .then(({ data }) => {
          resolve(data)
        })
        .catch(error => {
          reject(error)
        })
    })
  }

  getRVChilds(resourceId, parentId) {
    return new Promise(function (resolve, reject) {
      const storedToken = localStorage.getItem('sigToken')
      axios({
        method: 'GET',
        url: `${backend}/resourcevalues/${resourceId}/${parentId}`,
        headers: {
          Authorization: `Bearer ${storedToken}`
        }
      })
        .then(({ data }) => {
          resolve(data)
        })
        .catch(error => {
          reject(error)
        })
    })
  }

  findSettingsByType(type, commit, store) {
    return new Promise(function (resolve, reject) {
      const storedToken = localStorage.getItem('sigToken')
      axios({
        method: 'get',
        url: `${backend}/settings/findByType/${type}`,
        headers: {
          Authorization: `Bearer ${storedToken}`
        }
      })
        .then(({ data }) => {
          resolve(data)
          store.commit(commit, {
            content: data,
            totalElements: data.length
          })
        })
        .catch(error => {
          reject(error)
        })
    })
  }

  extractSelectedRV(rv) {
    return rv != null ? rv.split(':')[1] : ''
  }

  async saveFile(file) {
    const formData = new FormData()
    formData.append('uploadfile', file)
    const storedToken = localStorage.getItem('sigToken')
    const response = await axios.post(`${backend}/upload`, formData, {
      headers: {
        Authorization: `Bearer ${storedToken}`
      }
    })
    return response.data
  }

  async saveFileInFolder(folderName, file) {
    const formData = new FormData()
    formData.append('uploadfile', file)
    const storedToken = localStorage.getItem('sigToken')
    const response = await axios.post(
      `${backend}/upload/${folderName}`,
      formData,
      {
        headers: {
          Authorization: `Bearer ${storedToken}`
        }
      }
    )
    return response.data
  }

  deleteFile(fileName) {
    return new Promise(function (resolve, reject) {
      const storedToken = localStorage.getItem('sigToken')
      axios({
        method: 'delete',
        url: `${backend}/upload/delete/${fileName}`,
        headers: {
          Authorization: `Bearer ${storedToken}`
        }
      })
        .then(({ data }) => {
          resolve(data)
        })
        .catch(error => {
          reject(error)
        })
    })
  }

  download(file) {
    return new Promise(function (resolve, reject) {
      axios
        .get(`${backend}/download/${file}`)
        .then(({ data }) => {
          resolve(data)
        })
        .catch(error => {
          reject(error)
        })
    })
  }

  count(path) {
    return new Promise(function (resolve, reject) {
      const storedToken = localStorage.getItem('sigToken')
      axios({
        method: 'get',
        url: `${backend}/${path}/count`,
        headers: {
          Authorization: `Bearer ${storedToken}`
        }
      })
        .then(res => {
          resolve(res.data)
        })
        .catch(error => {
          notification.error({
            message: 'Erreur!',
            description:
              error.response && error.response.data
                ? error.response.data.message
                : "Une erreur inattendue s'est produite !"
          })
          reject(error)
        })
    })
  }

  post(path, payload) {
    return new Promise(function (resolve, reject) {
      const storedToken = localStorage.getItem('sigToken')
      axios({
        method: 'post',
        url: `${backend}/${path}/${payload.id}`,
        headers: {
          Authorization: `Bearer ${storedToken}`
        },
        data: payload
      })
        .then(res => {
          resolve(res.data)
        })
        .catch(error => {
          reject(error)
        })
    })
  }

  put(path, payload) {
    return new Promise(function (resolve, reject) {
      const storedToken = localStorage.getItem('sigToken')
      axios({
        method: 'put',
        url: `${backend}/${path}/${payload.id}`,
        headers: {
          Authorization: `Bearer ${storedToken}`
        },
        data: payload
      })
        .then(res => {
          resolve(res.data)
        })
        .catch(error => {
          reject(error)
        })
    })
  }

  putCollection(path, payload) {
    return new Promise(function (resolve, reject) {
      const storedToken = localStorage.getItem('sigToken')
      axios({
        method: 'put',
        url: `${backend}/${path}/collection`,
        headers: {
          Authorization: `Bearer ${storedToken}`
        },
        data: payload
      })
        .then(res => {
          resolve(res.data)
        })
        .catch(error => {
          reject(error)
        })
    })
  }

  getAllPublicMapsWithFilter(page, limit, name) {
    return new Promise(function (resolve, reject) {
      axios({
        method: 'get',
        url: `${backend}/maps/public/filter`,
        params: {
          page,
          limit,
          name
        }
      })
        .then(({ data }) => {
          resolve(data)
        })
        .catch(error => {
          reject(error)
        })
    })
  }

  getAllPublicMaps(page, limit) {
    return new Promise(function (resolve, reject) {
      axios({
        method: 'get',
        url: `${backend}/maps/public`,
        params: {
          page,
          limit
        }
      })
        .then(({ data }) => {
          resolve(data)
        })
        .catch(error => {
          reject(error)
        })
    })
  }

  getPublicMap(id) {
    return new Promise(function (resolve, reject) {
      axios({
        method: 'get',
        url: `${backend}/maps/public/${id}`
      })
        .then(({ data }) => {
          resolve(data)
        })
        .catch(error => {
          reject(error)
        })
    })
  }

  get(path, id) {
    return new Promise(function (resolve, reject) {
      const storedToken = localStorage.getItem('sigToken')
      axios({
        method: 'get',
        url: `${backend}/${path}/${id}`,
        headers: {
          Authorization: `Bearer ${storedToken}`
        }
      })
        .then(({ data }) => {
          resolve(data)
        })
        .catch(error => {
          reject(error)
        })
    })
  }

  getAll(path, args) {
    return new Promise(function (resolve, reject) {
      const storedToken = localStorage.getItem('sigToken')
      axios({
        method: 'get',
        url: `${backend}/${path}`,
        headers: {
          Authorization: `Bearer ${storedToken}`
        },
        params: {
          page: args.page,
          limit: args.limit,
          sort: args.sort,
          dir: args.dir
        }
      })
        .then(({ data }) => {
          resolve(data)
        })
        .catch(error => {
          reject(error)
        })
    })
  }

  postCollection(path, payload, store, router, notification) {
    return new Promise(function (resolve, reject) {
      const storedToken = localStorage.getItem('sigToken')
      axios({
        method: 'post',
        url: `${backend}/${path}/collection`,
        headers: {
          Authorization: `Bearer ${storedToken}`
        },
        data: payload
      })
        .then(({ data }) => {
          resolve(data)
        })
        .catch(error => {
          notification.error({
            message: 'Erreur!',
            description:
              error.response && error.response.data
                ? error.response.data.message
                : "Une erreur inattendue s'est produite !"
          })
          reject(error)
        })
    })
  }

  post(path, payload, store, router, notification) {
    return new Promise(function (resolve, reject) {
      const storedToken = localStorage.getItem('sigToken')
      axios({
        method: 'post',
        url: `${backend}/${path}`,
        headers: {
          Authorization: `Bearer ${storedToken}`
        },
        data: payload
      })
        .then(({ data }) => {
          resolve(data)
        })
        .catch(error => {
          if (error.response && error.response.status === 401) {
            store.dispatch('profile/refresh', router)
          } else {
            notification.error({
              message: 'Erreur!',
              description:
                error.response && error.response.data
                  ? error.response.data.message
                  : "Une erreur inattendue s'est produite !"
            })
            reject(error)
          }
        })
    })
  }

  findAll(path, args, commit, store, router, notification) {
    return new Promise(function (resolve, reject) {
      axios
        .get(path, {
          params: {
            page: args.page,
            limit: args.limit,
            sort: args.sort,
            dir: args.dir
          }
        })
        .then(({ data }) => {
          resolve(data.content)
          window.scrollTo(0, 0);
          store.commit(commit, {
            content: data.content,
            totalElements: data.totalElements
          })
        })
        .catch(error => {
          notification.error({
            message: 'Erreur!',
            description:
              error.response && error.response.data
                ? error.response.data.message
                : "Une erreur inattendue s'est produite !"
          })
          reject(error)
        })
    })
  }

  find(path, id, store) {
    return new Promise(function (resolve, reject) {
      axios(`${path}/${id}`)
        .then(({ data }) => {
          store.commit(`${path}/add`, data)
          resolve(data)
        })
        .catch(error => {
          reject(error)
        })
    })
  }

  loadRegions(layerSlug, layerIdentifiant) {
    return new Promise(function (resolve, reject) {
      axios(`entityelements/${layerSlug}/${layerIdentifiant}`)
        .then(({ data }) => {
          resolve(data)
        })
        .catch(error => {
          reject(error)
        })
    })
  }

  getAllByCriteria(path, payload, args) {
    return new Promise(function (resolve, reject) {
      axios
        .post(`${path}/search`, payload, {
          params: {
            page: args.page,
            limit: args.limit,
            sort: args.sort,
            dir: args.dir
          }
        })
        .then(({ data }) => {
          resolve(data)
        })
        .catch(error => {
          reject(error)
        })
    })
  }

  findAllByCriteria(path, payload, args, store, router, notification) {
    return new Promise(function (resolve, reject) {
      axios
        .post(`${path}/search`, payload, {
          params: {
            page: args.page,
            limit: args.limit,
            sort: args.sort,
            dir: args.dir
          }
        })
        .then(({ data }) => {
          resolve(data)
          store.commit(`${path}/set`, {
            content: data.content,
            totalElements: data.totalElements
          })
          window.scrollTo(0, 0)
        })
        .catch(error => {
          notification.error({
            message: 'Erreur!',
            description:
              error.response && error.response.data
                ? error.response.data.message
                : "Une erreur inattendue s'est produite !"
          })
          reject(error)
        })
    })
  }

  filterAllLayersInMap(path, payload, args, store, router, notification) {
    return new Promise(function (resolve, reject) {
      axios
        .post(`${path}`, payload, {
          params: {
            page: args.page,
            limit: args.limit,
            sort: args.sort,
            dir: args.dir
          }
        })
        .then(({ data }) => {
          resolve(data)
          store.commit(`${path}/set`, {
            content: data.content,
            totalElements: data.totalElements
          })
        })
        .catch(error => {
          notification.error({
            message: 'Erreur!',
            description:
              error.response && error.response.data
                ? error.response.data.message
                : "Une erreur inattendue s'est produite !"
          })
          reject(error)
        })
    })
  }

  remove(path, id) {
    return new Promise(function (resolve, reject) {
      const storedToken = localStorage.getItem('sigToken')
      axios({
        method: 'delete',
        url: `${backend}/${path}/${id}`,
        headers: {
          Authorization: `Bearer ${storedToken}`
        }
      })
        .then(({ data }) => {
          resolve(data)
        })
        .catch(error => {
          reject(error)
        })
    })
  }

  delete(path, id, store, router, notification) {
    return new Promise(function (resolve, reject) {
      const storedToken = localStorage.getItem('sigToken')
      axios({
        method: 'delete',
        url: `${backend}/${path}/${id}`,
        headers: {
          Authorization: `Bearer ${storedToken}`
        }
      })
        .then(({ data }) => {
          if (store) store.commit(`${path}/delete`, id)
          notification.success({
            message: 'La Suppression est réussie avec succés'
          })
          resolve(data)
        })
        .catch(error => {
          notification.error({
            message: 'Erreur!',
            description:
              error.response && error.response.data
                ? error.response.data.message
                : "Une erreur inattendue s'est produite !"
          })
          reject(error)
        })
    })
  }

  printMap(map) {
    return new Promise(function (resolve, reject) {
      leafletImage(map, function (err, canvas) {
        function dataURLtoBlob(dataurl) {
          var arr = dataurl.split(','),
            mime = arr[0].match(/:(.*?);/)[1],
            bstr = atob(arr[1]),
            n = bstr.length,
            u8arr = new Uint8Array(n)
          while (n--) {
            u8arr[n] = bstr.charCodeAt(n)
          }
          return new Blob([u8arr], { type: mime })
        }

        let blob = dataURLtoBlob(canvas.toDataURL())
        var file = new File([blob], 'map.png')
        let imageUrl = RestApi.saveFile(file)
        resolve(imageUrl)
      })
    })
  }

  shareMap(map, id) {
    return new Promise(function (resolve, reject) {
      const storedToken = localStorage.getItem('sigToken')
      axios({
        method: 'post',
        url: `${backend}/maps/share/${id}`,
        headers: {
          Authorization: `Bearer ${storedToken}`
        },
        data: map
      })
        .then(({ data }) => {
          resolve(data)
        })
        .catch(error => reject(error))
    })
  }

  //Cette methode permet d'autoriser l'utilisateur a ajouter des points interets dans sa zone
  isAuthorizedArea(geometry) {
    return new Promise(function (resolve, reject) {
      axios
        .post(`entityelements/authorized`, geometry)
        .then(({ data }) => {
          if (data) {
            resolve(data)
          } else {
            reject('error')
          }
        })
        .catch((error) => {
          reject(error)
        })
    })
  }

  shareLayer(layer, id) {
    return new Promise(function (resolve, reject) {
      const storedToken = localStorage.getItem('sigToken')
      axios({
        method: 'post',
        url: `${backend}/layers/share/${id}`,
        headers: {
          Authorization: `Bearer ${storedToken}`
        },
        data: layer
      })
        .then(({ data }) => {
          resolve(data)
        })
        .catch(error => reject(error))
    })
  }

  archiveMap(map, id, store, notification) {
    return new Promise(function (resolve, reject) {
      const storedToken = localStorage.getItem('sigToken')
      axios({
        method: 'post',
        url: `${backend}/maps/archive/${id}`,
        headers: {
          Authorization: `Bearer ${storedToken}`
        },
        data: map
      })
        .then(({ data }) => {
          if (map.privacy == 'ARCHIVED')
            notification.success({
              message: 'Success!',
              description: 'la carte est bien archivée'
            })
          else {
            notification.success({
              message: 'Success!',
              description: 'la carte est bien désarchivée'
            })
          }
          store.commit('maps/update', data)
          resolve(data)
        })
        .catch(error => reject(error))
    })
  }

  createOrUpdate(path, payload, elementToEdit, store, router, notification) {
    return new Promise(function (resolve, reject) {
      const storedToken = localStorage.getItem('sigToken')
      const method = elementToEdit ? 'put' : 'post'
      const id = method === 'put' ? elementToEdit.id : ''
      const commit = method === 'put' ? `${path}/update` : `${path}/add`

      axios({
        method,
        url: `${backend}/${path}/${id}`,
        headers: {
          Authorization: `Bearer ${storedToken}`
        },
        data: payload
      })
        .then(({ data }) => {
          notification.success({
            message: 'Success!',
            description: 'La sauvegarde est réussie avec succés'
          })
          store.commit(commit, data)
          resolve(data)
        })
        .catch(error => {
          notification.error({
            message: 'Error!',
            description:
              error.response && error.response
                ? error.response.data
                : "Une erreur inattendue s'est produite !"
          })
          reject(error)
        })
    })
  }

  exportData(slug, fileType, extension, payload, notification) {
    return new Promise(function (resolve, reject) {
      const storedToken = localStorage.getItem('sigToken')
      axios({
        method: 'post',
        headers: {
          Authorization: `Bearer ${storedToken}`,
        },
        params: { limit: -1 },
        url: `${backend}/entityelements/export/${slug}/${fileType}`,
        data: payload,
        responseType: 'blob',
      })
        .then(({ data }) => {
          const url = window.URL.createObjectURL(new Blob([data]))
          const link = document.createElement('a')
          link.href = url

          link.setAttribute('download', `${slug}.${extension}`) //or any other extension
          document.body.appendChild(link)
          link.click()
          link.remove()
          notification.success({
            message: 'Félicitation!',
            description: `La couche ${slug} est exportée avec succès.`,
          })
          resolve(data)
        })
        .catch(error => reject(error))
    })
  }

  closeSession(session) {
    return new Promise(function (resolve, reject) {
      const storedToken = localStorage.getItem('sigToken')
      axios({
        method: 'delete',
        url: `${backend}/sessions`,
        headers: {
          Authorization: `Bearer ${storedToken}`
        },
        data: session
      })
        .then(({ data }) => {
          resolve(data)
        })
        .catch(error => reject(error))
    })
  }

  findCurrentUserLayers() {
    let thisRef = this
    return new Promise(function (resolve, reject) {
      thisRef
        .getCurrentUser()
        .then(({ layers }) => resolve(layers))
        .catch(error => reject(error))
    })
  }

  refreshToken(username) {
    return new Promise(function (resolve, reject) {
      let refreshToken = localStorage.getItem('refreshToken')
      let requestBody = {
        refreshToken,
        username
      }
      axios({
        method: 'post',
        params: {
          refreshToken
        },
        url: `${backend}/refresh`,
        data: requestBody
      })
        .then(({ data }) => {
          resolve(data)
        })
        .catch(error => {
          reject(error)
        })
    })
  }

  enableBackendSync(token, commit) {
    let socket = null

    let new_conn = function () {
      socket = new SockJS(`${WebSocketURL}?token=${token}`)

      let stompClient = Stomp.over(socket)
      stompClient.connect(
        {},
        function () {
          let stores = [
            'group',
            'user',
            'layer',
            'filter',
            'map',
            'resource',
            'settings',
            'settings_type'
          ]

          for (const store of stores) {
            stompClient.subscribe(
              `/user/notification/${store.toLowerCase()}`,
              function (response) {
                let notification = JSON.parse(response.body)
                if (notification.type != null) {
                  let commitName = `${notification.type
                    }/${notification.transaction.toLowerCase()}`

                  commit(commitName, notification.object)
                  commit('notifications/add', notification.userNotificationDto)
                }
                commit(
                  'profile/setNotificationsToNotViewed',
                  notification.userNotificationDto
                )
              }
            )
          }
        }
      )
    }
    new_conn()
  }

  getMapPrevisualization(id) {
    return new Promise(function (resolve, reject) {
      axios({
        method: 'get',
        url: `${backend}/carto/map/${id}`
      })
        .then(({ data }) => {
          resolve(data)
        })
        .catch(error => reject(error))
    })
  }

  closeSession() {
    localStorage.removeItem('sigToken')
    localStorage.removeItem('refreshToken')
    window.$nuxt.$store.commit('profile/logout')
    window.$nuxt.$router.push('/')
  }
  logout() {
    axios
      .post('/logout', localStorage.getItem('sigToken'), {
        headers: { 'Content-Type': 'text/plain' },
      })
      .then(() => {
        this.closeSession()
      })
      .catch((error) => {
        this.closeSession()
      })
  }
}

export default new RestApi()
