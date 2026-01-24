<template>
  <div class="h-100">
     <section class="section m-4">
         <div class="p-col-12 p-md-2 p-inputgroup">
           <InputText type="text" v-model="name" :value="roleToEdit ? roleToEdit.name : ''" placeholder="Nom"  />
        </div>
        <div class="p-col-12 p-md-2 p-inputgroup">
           <InputText type="text" v-model="label" :value="roleToEdit ? roleToEdit.lable : ''" placeholder="Nom module"/>
        </div>
        <div class="p-col-12 p-md-2">
          <Accordion :activeIndex="active">
	            <AccordionTab header="Permissions">
                  <div>
                    <span class="p-input-icon-right">
                        <InputText v-model="searchText" placeholder="Recherche globale..."  style="width:500px"/>
                    </span>
                    <span>
                       <Button label="Chercher" icon="pi pi-search" class="p-button p-button-success" @click="filterPermissions($event)"/>
                    </span>
                  </div>
                  <DataTable :value="tableData" class="p-datatable-striped p-datatable-md"  :paginator="true" :rows="10"
                             :totalRecords="totalRecords" :loading="loading" @page="onPage($event)" :lazy="true" dataKey="id" :rowHover="true" :selection.sync="selectedPermissions">        
                    
                    <template #empty>
                        Aucun resultat trouvé.
                    </template> 
                    <Column selectionMode="multiple" headerStyle="width: 3em"></Column>
                    <Column field="label" header="Désignation">
                      <template #body="slotProps">
                          {{slotProps.data.label}}
                      </template>
                    </Column>
                    <Column field="name" header="Code">
                      <template #body="slotProps">
                          {{slotProps.data.name}}
                      </template>
                    </Column>
                  </DataTable>
	            </AccordionTab>
            </Accordion>
          </div>
          <div class="p-col-12 p-md-2  float-right">
              <Button label="Enregistrer" @click="save($event)"/>
          </div>
     </section>
  </div>

</template>
<script>
import {backApi} from '~/methods/serverApi'
import { mapState } from 'vuex'
import { backend } from '../../../constants'
import RestApi from '../../../methods/api.js'

import InputText from 'primevue/inputtext'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Button from 'primevue/button'
import AccordionTab from 'primevue/accordiontab';
import Accordion from 'primevue/accordion'
import Checkbox from 'primevue/checkbox'

export default {
  props: ['roleToEdit'],
  data() {
    return {
      name: null,
      label: null,
      loading: false,
      sortField: 'createDate',
      sortOrder: 'asc',
      perPage: 10,
      page: 0,
      totalRecords: 0,
      selectedPermissions: [],
      searchText: '',
      active: 0,
      filters: {}
    }
  },
  components: {
    InputText: InputText,
    DataTable: DataTable,
    Column: Column,
    Button: Button,
    Checkbox: Checkbox,
    Accordion: Accordion,
    AccordionTab: AccordionTab
  },
  computed: {
    ...mapState(['permissions', 'users','profile']),
    tableData() {
      return this.permissions.permissions
    }
  },
  methods: {
    loadDataAsync() {
      const storedToken = localStorage.getItem('sigToken')
      this.loading = true
      backApi.get('permissions')
        .then(({ data }) => {
          this.loading = false
          this.totalRecords = data.totalElements
          this.$store.commit('permissions/set', data.content)
        })
        .catch(error => {
          this.loading = false
        })
    },
    onPage(event) {
      this.page = event.page
      this.loadDataAsync()
    },
    filterPermissions() {
      let payload = {
        condition: 'or',
        rules: [
          {
            label: 'name',
            field: 'name',
            operator: 'ilike',
            type: 'string',
            value: this.searchText
          },
          {
            label: 'label',
            field: 'label',
            operator: 'ilike',
            type: 'string',
            value: this.searchText
          }
        ]
      }
      backApi.post('permissions/search', payload)
        .then(({ data }) => {
          if (data != null) {
            this.totalRecords = data.totalElements
            this.$store.commit('permissions/set', data.content)
          }
        })
        .catch(error => {})
    },
    save() {
      let role = {
        name: this.name,
        label: this.label,
        module: true,
        permissions: this.selectedPermissions
      }
 
      RestApi.createOrUpdate(
        'roles',
        role,
        this.roleToEdit,
        this.$store,
        this.$router,
        this.$notification
      ).then(data => {
        this.$emit('saved')
        this.isLoading = false
      })
    },
    deleteRole(id) {}
  },
  beforeMount() {
    this.loadDataAsync()
  },
  mounted() {
    if (this.roleToEdit) {
      this.name = this.roleToEdit.name
      this.label = this.roleToEdit.label
      this.selectedPermissions = this.roleToEdit.permissions
      /*this.roleToEdit.permissions.forEach(permission => {
        this.selectedPermissions.push(permission)
      })*/
    }
  }
}
</script>
