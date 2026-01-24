export default {
  data() {
    return {
      searchCriteria: '',
      commonsOperators: [
        { value: 'ilike', key: 'Contient' },
        { value: '=', key: 'Egale' },
        { value: '<=', key: 'Inférieur ou égale' },
        { value: '<', key: 'Inférieur' },
        { value: '>=', key: 'supérieur ou égale' },
        { value: '>', key: 'Supérieur' },
        { value: 'empty', key: 'Est vide' }
      ],
      dateOperators: [
        { value: '=', key: 'Egale' },
        { value: '<=', key: 'Inférieur ou égale' },
        { value: '<', key: 'Inférieur' },
        { value: '>=', key: 'Supérieur ou égale' },
        { value: '>', key: 'Supérieur' }
      ],
      showButtons: { groupInsert: false, groupDelete: true, ruleDelete: true },
      dateTemplate: {
        create: () => {
          return createElement('input', { attrs: { type: 'Date' } })
        },
        destroy: args => {
          let datetime = getComponent(
            document.getElementById(args.elementId),
            'datetimepicker'
          )
          if (datetime) {
            datetime.destroy()
          }
        },
        write: args => {
          let dateTimeInstance = new DateTimePicker({
            value: null,
            format: 'dd/MM/yyyy HH:mm:ss',
            change: e => {
              this.$refs.querybuilder.ej2Instances.notifyChange(
                e.value,
                e.element
              )
            }
          })
          dateTimeInstance.appendTo('#' + args.elements.id)
        }
      },
      timeTemplate: {
        create: () => {
          return createElement('input', { attrs: { type: 'Time' } })
        },
        destroy: args => {
          let time = getComponent(
            document.getElementById(args.elementId),
            'timepicker'
          )
          if (time) {
            time.destroy()
          }
        },
        write: args => {
          let timeInstance = new TimePicker({
            value: null,
            format: 'HH:mm',
            change: e => {
              console.log(e)
              this.$refs.querybuilder.ej2Instances.notifyChange(
                e.value,
                e.element
              )
            }
          })
          timeInstance.appendTo('#' + args.elements.id)
        }
      }
    }
  },
  methods: {
    getFieldFormat(t) {
      if (t) {
        switch (t) {
          case 'DATETIME':
            return 'dd/MM/yyyy HH:mm:ss'
          case 'DATE':
            return 'dd/MM/yyyy'
          case 'TIME':
            return 'HH:mm'
          default:
            return ''
        }
      }
    },
    getFieldOperators(t) {
      if (t) {
        switch (t) {
          case 'DATETIME':
            return this.dateOperators
          case 'DATE':
            return this.dateOperators
          case 'TIME':
            return this.dateOperators
          default:
            return this.commonsOperators
        }
      }
    },
    getFieldType(t) {
      if (t) {
        switch (t) {
          case 'DATETIME':
            return 'date'
          case 'DATE':
            return 'date'
          case 'TIME':
            return 'date'
          case 'NUMBER':
            return 'number'
          case 'INTEGER':
            return 'number'
          default:
            return 'string'
        }
      }
    },
    getFieldTemplate(t) {
      if (t) {
        switch (t) {
          case 'TIME':
            return this.timeTemplate
          default:
            return null
        }
      }
    }
  }
}
