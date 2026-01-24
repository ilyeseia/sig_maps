<template>
  <div class="modal-card fields-selection" style="height: 75vh">
    <header class="modal-card-head">
      <h1 class="modal-card-title">Paramétrage des champs</h1>
    </header>
    <section class="modal-card-body">
      <b-table
        ref="fieldSelection"
        :data="getData"
        @check="editSelection"
        :checked-rows.sync="getChecked"
        v-sortable
        checkable
        :detailed="withDetail"
      >
        <template v-slot="props">
          <b-table-column class="layer-name" field="name" label="Non">
            <template>
              <p :style="{'font-weight':  props.row.layer === 'layerA' ? 'normal' : 'bold'}"> {{props.row.name}} </p>
            
            </template>
          </b-table-column>
          <b-table-column field="type" label="Type">
            {{ props.row.type }}
          </b-table-column>
        </template>
        <template slot="detail" slot-scope="props">
          <div>
            <div class="row">
              <div class="column align-middle" style="margin-top: 5px">
                <b-checkbox v-model="props.row.required" name="checkbox-1"
                  >Required
                </b-checkbox>
                <b-checkbox v-model="props.row.visible" name="checkbox-2"
                  >Visible
                </b-checkbox>
                <b-checkbox v-model="props.row.publique" name="checkbox-2"
                  >Publique
                </b-checkbox>
              </div>
            </div>
          </div>
        </template>
      </b-table>
    </section>
    <footer class="right-align modal-card-foot">
      <button
        id="close-layer-form"
        class="button"
        type="button"
        @click="$parent.close()"
      >
        Fermer
      </button>
      <button id="save-layer" @click="save" :class="['button', 'is-primary']">
        Enregistrer
      </button>
    </footer>
  </div>
</template>

<script>
import Sortable from 'sortablejs'
import intersection from 'lodash/intersection'

const createSortable = (el, options, vnode) => {
  return Sortable.create(el, {
    ...options,
    onEnd: function (evt) {
      vnode.context.sortFields(evt)
    },
  })
}

const sortable = {
  name: 'sortable',
  bind(el, binding, vnode) {
    const table = el.querySelector('table')
    table._sortable = createSortable(
      table.querySelector('tbody'),
      binding.value,
      vnode
    )
  },
}
export default {
  directives: { sortable },
  data() {
    return {
      sel: [],
    }
  },
  computed: {
    getChecked() {
      return this.fields.filter((f) => this.sel.some((s) => s.id === f.id))
    },
    getData(){
      return this.fields.sort((b, a) => b.order - a.order)
    }
  },
  props: ['fields', 'selectedFields', 'withDetail'],
  mounted() {
    this.sel = this.selectedFields
  },
  methods: {
    editSelection(payload) {
      this.selectedFields = [...intersection(this.fields, payload)]
    },
    save() {
      this.$emit('updateSelectedFields', this.selectedFields)
      if (this.selectedFields.length > 0) {
        this.$parent.close()
      } else {
        this.$notification.warning({
          message: 'Remarque!',
          description: `Vous devez sélectionner au moins un champ !`,
        })
      }
    },
    sortFieldsWithinLayer(evt) {
      let data = this.sel

      let oldIndex = evt.oldIndex
      let newIndex = evt.newIndex

      const item = data[oldIndex]

      if (newIndex > oldIndex) {
        for (let i = oldIndex; i < newIndex; i++) {
          data[i] = data[i + 1]
        }
      } else {
        for (let i = oldIndex; i > newIndex; i--) {
          data[i] = data[i - 1]
        }
      }

      data[newIndex] = item
      return data
    },
    sortFields(evt) {
      this.sel = this.sortFieldsWithinLayer(evt)
      for (let i = 0; i < this.sel.length; i++) {
        this.sel[i].order = i + 1
      }
      this.$emit('updateSelectedFields', this.sel)
    },
  },
}
</script>
<style lang="scss">
.fields-selection {
  .detail-container {
    padding-top: 0 !important;
    padding-bottom: 0 !important;
  }
}
</style>