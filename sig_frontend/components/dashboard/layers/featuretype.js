import axios from 'axios'
import {
  GeoServerWFSURL,
  GeoServerUser,
  GeoServerPassword
} from '../../../constants'


export function getLayerBBOX(layer) {
  return new Promise(function(resolve, reject) {
    axios({
      method: 'get',
      withCredentials: true,
      headers: {
        'Content-Type': 'application/xml',
        Accept: 'application/json'
      },
      url: `${GeoServerWFSURL}?service=wfs&version=1.1.0&request=GetCapabilities`,
      auth: { username: `${GeoServerUser}`, password: `${GeoServerPassword}` }
    })
      .then(({ data }) => {
        let parser = new DOMParser()
        let xmlDoc = parser.parseFromString(data, 'text/xml')
        let features = xmlDoc.getElementsByTagName('FeatureType')

        for (let i = 0; i < features.length; i++) {
          let feature = features[i]
          let name = feature.getElementsByTagName('Name')[0].innerHTML
          if (name === `limite_admin:${layer}`) {
            let owsBoundingBox = feature.getElementsByTagName(
              'ows:WGS84BoundingBox'
            )[0]
            let bbox =
              owsBoundingBox.getElementsByTagName('ows:LowerCorner')[0]
                .innerHTML +
              ' ' +
              owsBoundingBox.getElementsByTagName('ows:UpperCorner')[0]
                .innerHTML
            resolve(bbox.split(' ').join(','))
          }
        }
      })
      .catch(error => reject(error))
  })
}