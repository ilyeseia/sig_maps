import { backend } from '../../../constants'
import uuidv1 from 'uuid'
import { backApi as axios } from '~/methods/serverApi';
export class LayerSymbolizer {

  static generateRandomColor() {
    let Colors = {}
    Colors.names = {
      aqua: '#00ffff',
      azure: '#f0ffff',
      beige: '#f5f5dc',
      black: '#000000',
      blue: '#0000ff',
      brown: '#a52a2a',
      cyan: '#00ffff',
      darkblue: '#00008b',
      darkcyan: '#008b8b',
      darkgrey: '#a9a9a9',
      darkgreen: '#006400',
      darkkhaki: '#bdb76b',
      darkmagenta: '#8b008b',
      darkolivegreen: '#556b2f',
      darkorange: '#ff8c00',
      darkorchid: '#9932cc',
      darkred: '#8b0000',
      darksalmon: '#e9967a',
      darkviolet: '#9400d3',
      fuchsia: '#ff00ff',
      gold: '#ffd700',
      green: '#008000',
      indigo: '#4b0082',
      khaki: '#f0e68c',
      lightblue: '#add8e6',
      lightcyan: '#e0ffff',
      lightgreen: '#90ee90',
      lightgrey: '#d3d3d3',
      lightpink: '#ffb6c1',
      lightyellow: '#ffffe0',
      lime: '#00ff00',
      magenta: '#ff00ff',
      maroon: '#800000',
      navy: '#000080',
      olive: '#808000',
      orange: '#ffa500',
      pink: '#ffc0cb',
      purple: '#800080',
      violet: '#800080',
      red: '#ff0000',
      silver: '#c0c0c0',
      white: '#ffffff',
      yellow: '#ffff00'
    }

    Colors.random = function () {
      var result
      var count = 0
      for (var prop in this.names)
        if (Math.random() < 1 / ++count) {
          result = this.names[prop]
        }
      return result
    }

    return Colors.random()
  }


  static generateRandomColors(layerId, fieldName, fieldType) {
    return new Promise(function (resolve, reject) {
      const storedToken = localStorage.getItem('sigToken')
      axios({
        method: 'get',
        url: `${backend}/entityelements/property/${layerId}/${fieldName}`,
        headers: {
          Authorization: `Bearer ${storedToken}`
        }
      })
        .then(({ data }) => {
          let style = []
          let value = null
          data.filter(e => e).sort().forEach(field => {
            value = fieldType === "SELECT" ? (field.split(":").length > 1 ? field.split(":")[1] : field) : field
            style.push({ id: uuidv1(), field, value: value,  color: LayerSymbolizer.generateRandomColor(), operator: '=' })
          })
          resolve(style)
        })
        .catch(error => (console.log(error), reject(error)))
    })
  }

  static generateGroupsWithRandomColors(layerId, fieldName, mode, method, color, classes, precision) {
    return new Promise(function (resolve, reject) {
      const storedToken = localStorage.getItem('sigToken')
      axios({
        method: 'get',
        url: `${backend}/entityelements/property/${layerId}/${fieldName}/classify?classification=${mode ? mode : ""}&classes=${classes}`,
        headers: {
          Authorization: `Bearer ${storedToken}`
        }
      })
        .then(({ data }) => {
          let rules = [...new Set(data)]
          rules = rules.filter(e => e)
          rules.sort()

          let style = []

          rules.forEach((field, index) => {
            style.push({
              id: uuidv1(),
              index: index + 1,
              from: field.minvalueperinterval.toFixed(precision),
              to: field.maxvalueperinterval.toFixed(precision),
              size: method === 'size' ? index * 3.5 : 1,
              color,
              operator: '<>'
            })
          })

          resolve(style)
        })
        .catch(error => reject(error))
    })
  }

  static generateEmptyImages(layerId, fieldName, fieldType) {
    return new Promise(function (resolve, reject) {
      const storedToken = localStorage.getItem('sigToken')
      axios({
        method: 'get',
        url: `${backend}/entityelements/property/${layerId}/${fieldName}`,
        headers: {
          Authorization: `Bearer ${storedToken}`
        }
      })
        .then(({ data }) => {
          let rules = [...new Set(data)]
          rules = rules.filter(e => e)
          rules.sort()

          let style = []
          let value = null
          rules.forEach(field => {
            value = fieldType === "SELECT" ? (field.split(":").length > 1 ? field.split(":")[1] : field) : field
            style.push({ id: uuidv1(), field, value: value, iconUrl: `${backend}/download/default.png`, operator: '=' })
          })
          resolve(style)
        })
        .catch(error => reject(error))
    })
  }
}