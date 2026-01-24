<template>
  <div class="modal-card" style="width: 900px; height: 75vh">
    <header class="modal-card-head">
      <h1 class="modal-card-title">Detail de l'action</h1>
    </header>
    <section class="modal-card-body" style="">
      <div class="d-flex justify-content-between row-column">
        <div class="attributes" style="flex-grow: 1">
          <b-field grouped>
            <b-field label="Utilisateur :"></b-field>
            <b-field :label="action.userName"></b-field>
          </b-field>
          <b-field grouped>
            <b-field label="Object :"></b-field>
            <b-field :label="action.object"></b-field>
          </b-field>
          <b-field v-if="action.action === 'EDIT'" grouped>
            <b-field label="Object ID :"></b-field>
            <b-field :label="action.objectId"></b-field>
          </b-field>
          <b-field grouped>
            <b-field label="API :"></b-field>
            <b-field style="width: 80%" :label="action.url"></b-field>
          </b-field>
        </div>
        <div class="attributes">
          <b-field grouped>
            <b-field label="Type Action :"></b-field>
            <strong
              :class="[
                'tag',
                { 'is-danger': action.action == 'DELETE' },
                { 'is-warning': action.action == 'EDIT' },
                { 'is-info': action.action == 'CREATE' },
              ]"
            >
              {{ getActionName(action.action) }}
            </strong>
          </b-field>
          <b-field grouped>
            <b-field label="Date Action :"></b-field>
            <b-field
              :label="$moment(action.createDate).format('DD/MM/yyyy')"
            ></b-field>
          </b-field>
          <b-field grouped>
            <b-field label="Heure Action :"></b-field>
            <b-field
              :label="$moment(action.createDate).format('HH:mm:ss')"
            ></b-field>
          </b-field>
        </div>
      </div>
      <b-table
        v-if="action.action === 'EDIT' && action.data"
        :data="JSON.parse(action.data)"
      >
        <template slot-scope="p">
          <b-table-column field="attribute" label="Attribut" sortable>
            {{ p.row.attribute }}
          </b-table-column>
          <b-table-column
            field="oldValue"
            :label="
              p.row.deletedValues
                ? 'Valeurs supprimées (liste)'
                : 'Ancienne Valeur'
            "
          >
            <div v-if="p.row.deletedValues">
              <p v-for="v in p.row.deletedValues.split(',')" :key="v">
                {{ v }}
              </p>
            </div>
            <div v-else>
              {{
                p.row.attribute.includes('Date')
                  ? $moment(p.row.oldValue).format('DD/MM/yyyy, HH:mm:ss')
                  : p.row.oldValue
              }}
            </div>
          </b-table-column>
          <b-table-column
            field="newValue"
            :label="
              p.row.addedValues ? 'Valeurs ajoutées (liste)' : 'Nouvelle Valeur'
            "
          >
            <div v-if="p.row.addedValues">
              <p
                style="white-space: nowrap"
                v-for="v in p.row.addedValues.split(',')"
                :key="v"
              >
                {{ v }}
              </p>
            </div>
            <div v-else>
              {{ p.row.newValue }}
            </div>
          </b-table-column>
        </template>
      </b-table>
      <b-table
        v-else-if="action.data"
        :data="Object.entries(JSON.parse(action.data))"
      >
        <template slot-scope="p">
          <b-table-column field="attribute" label="Attribut">
            {{ p.row[0] }}
          </b-table-column>
          <b-table-column field="value" label="valeur">
            {{
              p.row[0].includes('Date')
                ? $moment(p.row[1]).format('DD/MM/yyyy, HH:mm:ss')
                : p.row[1]
            }}
          </b-table-column>
        </template>
      </b-table>
    </section>
    <footer class="right-align modal-card-foot">
      <button
        id="close-group-form"
        class="button"
        type="button"
        @click="$parent.close()"
      >
        Fermer
      </button>
    </footer>
  </div>
</template>
<script>
export default {
  props: ['action'],
  methods: {
    getActionName(action) {
      switch (action) {
        case 'CREATE':
          return 'CRÉATION'
        case 'EDIT':
          return 'MODIFICATION'
        case 'DELETE':
          return 'SUPPRESSION'
      }
    },
  },
}
</script>
<style scoped lang="scss">
.attributes > div {
  margin-bottom: 0 !important;
}
.attributes div .field{
  color: red;
  @include respond('phone'){
    width: fit-content;
  }
}
</style>