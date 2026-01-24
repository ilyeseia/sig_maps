<template>
  <div class="global-filter__fields">
    <ejs-querybuilder
      :id="layerName"
      ref="querybuilder"
      @ruleChange="updateRule"
      :showButtons="showButtons"
    >
      <e-columns>
        <e-column
          v-for="field in layer.fields"
          :field="field.slug"
          :type="getFieldType(field.type)"
          :label="field.name"
          :key="field.id"
          :operators="getFieldOperators(field.type)"
          :format="getFieldFormat(field.type)"
          :template="getFieldTemplate(field.type)"
        />
      </e-columns>
    </ejs-querybuilder>
  </div>
</template>

<script>
import Vue from 'vue'
import advancedFilter from '~/mixins/advancedFilter'
import { QueryBuilderPlugin } from '@syncfusion/ej2-vue-querybuilder'
import { ButtonPlugin } from '@syncfusion/ej2-vue-buttons'
import { DropDownListPlugin } from '@syncfusion/ej2-vue-dropdowns'
import { GridPlugin } from '@syncfusion/ej2-vue-grids'
import { DateTimePickerPlugin } from '@syncfusion/ej2-vue-calendars'
import { TimePickerPlugin } from '@syncfusion/ej2-vue-calendars'
import { CheckBoxPlugin } from '@syncfusion/ej2-vue-buttons'
import { enableRipple } from '@syncfusion/ej2-base'
enableRipple(true)
Vue.use(CheckBoxPlugin)
Vue.use(TimePickerPlugin)
Vue.use(DateTimePickerPlugin)
Vue.use(GridPlugin)
Vue.use(DropDownListPlugin)
Vue.use(ButtonPlugin)
Vue.use(QueryBuilderPlugin)
export default {
  mixins: [advancedFilter],
  props: ['layer', 'layerName'],
  methods: {
    updateRule(args) {
      this.$emit('updateRule', { args, layerName: this.layerName })
    }
  },
}
</script>