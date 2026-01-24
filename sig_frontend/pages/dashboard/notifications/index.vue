<template>
  <section class="section-wrapper p-4">
    <div id="NotificationsId" class="d-flex justify-content-between"></div>
    <Panel>
      <template #title> Notifications </template>
      <template #default>
        <div id="notificationsTableId">
          <b-tabs>
            <b-tab-item>
              <template #header>
                <span>Non lue(s)</span>
                <i
                  class="pi pi-bell p-mr-4 p-text-secondary"
                  style="font-size: 1.5rem"
                  v-badge.success="countNotViewed"
                ></i>
              </template>
              <template>
                <div class="d-flex justify-content-end row-column">
                  <b-input
                    v-model="searchGlobalNotViewed"
                    ref="searcGlobalNotViewedRef"
                    placeholder="Recherche globale.."
                    type="search"
                    icon="magnify"
                    class="mb-2"
                  />
                </div>
              </template>
              <DataTable
                class="p-datatable-responsive"
                :value="notificationsNotViewed"
                responsiveLayout="stack"
                breakpoint="960px"
                :lazy="true"
                :paginator="true"
                :rows="10"
                :totalRecords="countNotViewed"
                @page="onPageNotViewed($event)"
                @sort="onSortNotViewed($event)"
              >
                <Column
                  headerStyle="width: 100px !important"
                  field="level"
                  header="Severité"
                  :sortable="true"
                  class="medium-td-size"
                >
                  <template #body="slotProps">
                    <span class="p-column-title">Severité</span>
                    <div>
                      <span
                        :class="[
                          'icon',
                          {
                            'has-text-danger': slotProps.data.level == 'SEVERE',
                          },
                          {
                            'has-text-warning':
                              slotProps.data.level == 'WARNING',
                          },
                          { 'has-text-info': slotProps.data.level == 'INFO' },
                        ]"
                      >
                        <i
                          class="fas"
                          :class="[
                            'icon',
                            {
                              'fa-exclamation-triangle':
                                slotProps.data.level == 'SEVERE',
                            },
                            {
                              'fa-exclamation-triangle':
                                slotProps.data.level == 'WARNING',
                            },
                            {
                              'fa-info-circle': slotProps.data.level == 'INFO',
                            },
                          ]"
                        ></i>
                      </span>
                      <span
                        :class="[
                          'tag',
                          { 'is-danger': slotProps.data.level == 'SEVERE' },
                          { 'is-warning': slotProps.data.level == 'WARNING' },
                          { 'is-info': slotProps.data.level == 'INFO' },
                        ]"
                      >
                        {{ slotProps.data.level }}
                      </span>
                    </div>
                  </template>
                  <template #filter>
                    <InputText
                      type="text"
                      v-model="levelNotViewed"
                      ref="levelNotViewedRef"
                      class=""
                      placeholder="Chercher par Severité"
                    />
                  </template>
                </Column>
                <Column
                  headerStyle="width: 150px !important"
                  field="object"
                  header="Objet"
                  :sortable="true"
                >
                  <template #body="slotProps">
                    <span class="p-column-title">Objet</span>
                    {{ slotProps.data.object }}
                  </template>
                  <template #filter>
                    <InputText
                      type="text"
                      v-model="objectNotViewed"
                      ref="objectNotViewedRef"
                      class=""
                      placeholder="Chercher par objet"
                    />
                  </template>
                </Column>
                <Column
                  headerStyle="width: 150px !important"
                  field="operation"
                  header="Opération"
                  :sortable="true"
                >
                  <template #body="slotProps">
                    <span class="p-column-title">Opération</span>
                    {{ slotProps.data.operation }}
                  </template>
                  <template #filter>
                    <InputText
                      type="text"
                      v-model="operationNotViewed"
                      ref="operationNotViewedRef"
                      class=""
                      placeholder="Chercher par operation" 
                    />
                  </template>
                </Column>
                <Column
                  headerStyle="width: 300px !important;"
                  field="message"
                  header="Message"
                  :sortable="true"
                >
                  <template #body="slotProps">
                    <span class="p-column-title">Message</span>
                    <p v-html="slotProps.data.message"></p>
                  </template>
                  <template #filter>
                    <InputText
                      type="text"
                      v-model="messageNotViewed"
                      ref="messageNotViewedRef"
                      class=""
                      placeholder="Chercher par message"
                    />
                  </template>
                </Column>
                <Column
                  headerStyle="width: 200px !important"
                  field="createdBy"
                  header="Propriétere"
                  :sortable="true"
                >
                  <template #body="slotProps">
                    <span class="p-column-title">Propriétere</span>
                    {{ slotProps.data.createdBy }}
                  </template>
                  <template #filter>
                    <InputText
                      type="text"
                      v-model="createdByNotViewed"
                      ref="createdByNotViewedRef"
                      class=""
                      placeholder="Chercher par propriétere"
                    />
                  </template>
                </Column>
                <Column
                  headerStle="width: 100px !important"
                  field="createDate"
                  header="Date"
                  :sortable="true"
                >
                  <template #body="slotProps">
                    <span class="p-column-title">Date</span>
                    {{ slotProps.data.createDate | moment('from', 'now') }}
                  </template>
                  <template #filter>
                    <InputText
                      type="text"
                      v-model="createDateNotViewed"
                      ref="createDateNotViewedeRef"
                      class=""
                      placeholder="Chercher par date"
                    />
                  </template>
                </Column>
                <Column headerStyle="width: 100px !important" header="Actions">
                  <template #body="slotProps">
                    <div class="btn-action data-table__actions">
                      <span class="p-column-title">Actions</span>
                      <b-tooltip
                        label="Marquer comme lu"
                        type="is-dark"
                        position="is-bottom"
                      >
                        <Button
                          icon="pi pi-bell"
                          class="p-button-rounded p-button-success p-mr-2"
                          @click="setViewed(slotProps.data.id)"
                        />
                      </b-tooltip>
                    </div>
                  </template>
                </Column>
              </DataTable>
            </b-tab-item>
            <b-tab-item>
              <template #header>
                <span>Lue(s)</span>
                <i
                  class="pi pi-bell p-mr-4 p-text-secondary"
                  style="font-size: 1.5rem"
                  v-badge.danger="countViewed"
                ></i>
              </template>
              <template>
                <div class="d-flex justify-content-end row-column">
                  <b-input
                    v-model="searchGlobalViewed"
                    ref="searcGlobalViewedRef"
                    placeholder="Recherche globale.."
                    type="search"
                    icon="magnify"
                    class="mb-2"
                  />
                </div>
              </template>
              <DataTable
                class="p-datatable-responsive"
                :value="notificationsViewed"
                responsiveLayout="stack"
                breakpoint="900px"
                :lazy="true"
                :paginator="true"
                :rows="10"
                :totalRecords="countViewed"
                @page="onPageViewed($event)"
                @sort="onSortViewed($event)"
              >
                <Column
                  headerStyle="width: 100px !important"
                  field="level"
                  header="Severité"
                  :sortable="true"
                >
                  <template #body="slotProps">
                    <span class="p-column-title">Severité</span>
                    <div>
                      <span
                        :class="[
                          'icon',
                          {
                            'has-text-danger': slotProps.data.level == 'SEVERE',
                          },
                          {
                            'has-text-warning':
                              slotProps.data.level == 'WARNING',
                          },
                          { 'has-text-info': slotProps.data.level == 'INFO' },
                        ]"
                      >
                        <i
                          class="fas"
                          :class="[
                            'icon',
                            {
                              'fa-exclamation-triangle':
                                slotProps.data.level == 'SEVERE',
                            },
                            {
                              'fa-exclamation-triangle':
                                slotProps.data.level == 'WARNING',
                            },
                            {
                              'fa-info-circle': slotProps.data.level == 'INFO',
                            },
                          ]"
                        ></i>
                      </span>
                      <span
                        :class="[
                          'tag',
                          { 'is-danger': slotProps.data.level == 'SEVERE' },
                          { 'is-warning': slotProps.data.level == 'WARNING' },
                          { 'is-info': slotProps.data.level == 'INFO' },
                        ]"
                      >
                        {{ slotProps.data.level }}
                      </span>
                    </div>
                  </template>
                  <template #filter>
                    <InputText
                      type="text"
                      v-model="levelViewed"
                      ref="levelViewedRef"
                      class=""
                      placeholder="Chercher par Severité"
                    />
                  </template>
                </Column>
                <Column
                  headerStyle="width: 150px !important"
                  field="object"
                  header="Objet"
                  :sortable="true"
                >
                  <template #body="slotProps">
                    <span class="p-column-title">Objet</span>
                    {{ slotProps.data.object }}
                  </template>
                  <template #filter>
                    <InputText
                      type="text"
                      v-model="objectViewed"
                      ref="objectViewedRef"
                      class=""
                      placeholder="Chercher par objet"
                    />
                  </template>
                </Column>
                <Column
                  headerStyle="width: 150px !important"
                  field="operation"
                  header="Opération"
                  :sortable="true"
                >
                  <template #body="slotProps">
                    <span class="p-column-title">Opération</span>
                    {{ slotProps.data.operation }}
                  </template>
                  <template #filter>
                    <InputText
                      type="text"
                      v-model="operationViewed"
                      ref="operationViewedRef"
                      class=""
                      placeholder="Chercher par operation"
                    />
                  </template>
                </Column>
                <Column
                  headerStyle="width: 300px !important"
                  field="message"
                  header="Message"
                  :sortable="true"
                >
                  <template #body="slotProps">
                    <span class="p-column-title">Message</span>
                    <p v-html="slotProps.data.message"></p>
                  </template>
                  <template #filter>
                    <InputText
                      type="text"
                      v-model="messageViewed"
                      ref="messageViewedRef"
                      class=""
                      placeholder="Chercher par message"
                    />
                  </template>
                </Column>
                <Column
                  headerStyle="width: 200px !important"
                  field="createdBy"
                  header="Propriétere"
                  :sortable="true"
                >
                  <template #body="slotProps">
                    <span class="p-column-title">Propriétere</span>
                    {{ slotProps.data.createdBy }}
                  </template>
                  <template #filter>
                    <InputText
                      type="text"
                      v-model="createdByViewed"
                      ref="createdByViewedRef"
                      class=""
                      placeholder="Chercher par propriétere"
                    />
                  </template>
                </Column>
                <Column
                  headerStyle="width: 100px !important"
                  field="createDate"
                  header="Date"
                  :sortable="true"
                >
                  <template #body="slotProps">
                    <span class="p-column-title">Date</span>
                    {{ slotProps.data.createDate | moment('from', 'now') }}
                  </template>
                  <template #filter>
                    <InputText
                      type="text"
                      v-model="createDateViewed"
                      ref="createDateViewedRef"
                      class=""
                      placeholder="Chercher par date"
                    />
                  </template>
                </Column>
                <Column headerStyle="width: 100px !important" header="Actions">
                  <template #body="slotProps">
                    <div class="btn-action data-table__actions">
                      <span class="p-column-title">Actions</span>
                      <b-tooltip
                        label="Marquer comme non lu"
                        type="is-dark"
                        position="is-bottom"
                      >
                        <Button
                          icon="pi pi-bell"
                          class="p-button-rounded p-button-danger p-mr-2"
                          @click="setNotViewed(slotProps.data.id)"
                        />
                      </b-tooltip>
                    </div>
                  </template>
                </Column>
              </DataTable>
            </b-tab-item>
          </b-tabs>
        </div>
      </template>
    </Panel>
  </section>
</template>
<script>
import Button from 'primevue/button'
import DataTable from 'primevue/datatable'
import TabView from 'primevue/tabview'
import Dropdown from 'primevue/dropdown'
import TabPanel from 'primevue/tabpanel'
import Column from 'primevue/column'
import InputText from 'primevue/inputtext'
import Calendar from 'primevue/calendar'
import Panel from '~/components/layout/Panel'
import BadgeDirective from 'primevue/badgedirective'
import { mapState } from 'vuex'
import { backApi } from '~/methods/serverApi'
import pageTitle from '~/mixins/page-title'

export default {
  directives: { badge: BadgeDirective },
  mixins: [pageTitle],
  data() {
    return {
      sortFieldViewed: 'createDate',
      sortOrderViewed: 'desc',
      perPageViewed: 10,
      pageViewed: 0,
      sortFieldNotViewed: 'createDate',
      sortOrderNotViewed: 'desc',
      perPageNotViewed: 10,
      pageNotViewed: 0,
      searchGlobalViewed: '',
      searchGlobalNotViewed: '',
      objectViewed: '',
      messageViewed: '',
      levelViewed: '',
      operationViewed: '',
      createdByViewed: '',
      createDateViewed: '',
      objectNotViewed: '',
      messageNotViewed: '',
      levelNotViewed: '',
      operationNotViewed: '',
      createdByNotViewed: '',
      createDateNotViewed: '',
      iconName: 'fa-exclamation-triangle',
      page: {
        title: 'Notifications',
        hid: '',
        name: '',
        description: '',
      },
    }
  },
  components: {
    Button,
    DataTable,
    TabView,
    TabPanel,
    Dropdown,
    Column,
    InputText,
    Calendar,
    Panel,
  },
  computed: {
    ...mapState(['users', 'notifications']),
    countViewed() {
      return this.notifications.notificationsViewedCount
    },
    countNotViewed() {
      return this.notifications.notificationsNotViewedCount
    },
    notificationsViewed() {
      return this.notifications.notificationsViewed.map(
        ({ id, object, message, level, operation, createdBy, createDate }) => ({
          id,
          object,
          message: message.includes('____')
            ? message.slice(0, 18) + message.slice(33, -1)
            : message,
          level,
          operation,
          createdBy,
          createDate,
        })
      )
    },
    notificationsNotViewed() {
      return this.notifications.notificationsNotViewed.map(
        ({ id, object, message, level, operation, createdBy, createDate }) => ({
          id,
          object,
          message: message.includes('____')
            ? message.slice(0, 18) + message.slice(33, -1)
            : message,
          level,
          operation,
          createdBy,
          createDate,
        })
      )
    },
  },
  watch: {
    searchGlobalViewed(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.searcGlobalViewedRef.value) {
          this.loadDataByFilterViewed()
        }
      }, 500)
    },
    objectViewed(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.objectViewedRef.value) {
          this.loadDataByFilterViewed()
        }
      }, 500)
    },
    objectNotViewed(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.objectNotViewedRef.value) {
          this.loadDataByFilterNotViewed()
        }
      }, 500)
    },
    messageViewed(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.messageViewedRef.value) {
          this.loadDataByFilterViewed()
        }
      }, 500)
    },
    messageNotViewed(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.messageNotViewedRef.value) {
          this.loadDataByFilterNotViewed()
        }
      }, 500)
    },
    levelViewed(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.levelViewedRef.value) {
          this.loadDataByFilterViewed()
        }
      }, 500)
    },
    levelNotViewed(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.levelNotViewedRef.value) {
          this.loadDataByFilterNotViewed()
        }
      }, 500)
    },
    operationViewed(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.operationViewedRef.value) {
          this.loadDataByFilterViewed()
        }
      }, 500)
    },
    operationNotViewed(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.operationNotViewedRef.value) {
          this.loadDataByFilterNotViewed()
        }
      }, 500)
    },
    createdByViewed(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.createdByViewedRef.value) {
          this.loadDataByFilterViewed()
        }
      }, 500)
    },
    createdByNotViewed(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.createdByNotViewedRef.value) {
          this.loadDataByFilterNotViewed()
        }
      }, 500)
    },
    createDateViewed(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.createDateViewedRef.value) {
          this.loadDataByFilterViewed()
        }
      }, 500)
    },
    createDateByNotViewed(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.createDateByNotViewedRef.value) {
          this.loadDataByFilterNotViewed()
        }
      }, 500)
    },
    searchGlobalNotViewed(newVal) {
      setTimeout(() => {
        if (newVal === this.$refs.searcGlobalNotViewedRef.value) {
          this.loadDataByFilterNotViewed()
        }
      }, 500)
    },
  },
  methods: {
    setViewed(id) {
      backApi
        .put(`user_notifications/viewed/${id}`)
        .then(({ data }) => {
          this.loadDataByFilterNotViewed()
          this.$store.commit('profile/setNotificationsToViewed', data)
          this.$store.commit('notifications/updateToViewed', data)
        })
        .catch((error) => {})
    },
    setNotViewed(id) {
      backApi
        .put(`user_notifications/viewed/${id}`)
        .then(({ data }) => {
          this.loadDataByFilterViewed()
          this.$store.commit('profile/setNotificationsToNotViewed', data)
          this.$store.commit('notifications/updateToNotViewed', data)
        })
        .catch((error) => {})
    },
    loadDataByFilterViewed() {
      let payload = {
        condition: this.searchGlobalViewed === '' ? 'and' : 'or',
        rules: [
          {
            label: 'object',
            field: 'object',
            operator: 'ilike',
            type: 'string',
            value:
              this.objectViewed === ''
                ? this.searchGlobalViewed
                : this.objectViewed,
          },
          {
            label: 'message',
            field: 'message',
            operator: 'ilike',
            type: 'string',
            value:
              this.messageViewed === ''
                ? this.searchGlobalViewed
                : this.messageViewed,
          },
          {
            label: 'level',
            field: 'level',
            operator: 'ilike',
            type: 'string',
            value:
              this.levelViewed === ''
                ? this.searchGlobalViewed
                : this.levelViewed,
          },
          {
            label: 'operation',
            field: 'operation',
            operator: 'ilike',
            type: 'string',
            value:
              this.operationViewed === ''
                ? this.searchGlobalViewed
                : this.operationViewed,
          },
          {
            label: 'createdBy',
            field: 'createdBy',
            operator: 'ilike',
            type: 'string',
            value:
              this.createdByViewed === ''
                ? this.searchGlobalViewed
                : this.createdByViewed,
          },
          /*{
            label: 'createDate',
            field: 'createDate',
            operator: 'ilike',
            type: 'string',
            value:
              this.createDateViewed === ''
                ? this.searchGlobalViewed
                : this.createDateViewed
          }*/
        ],
      }

      backApi
        .post(`/user_notifications/search/${true}`, payload, {
          params: {
            page: this.pageViewed,
            limit: this.perPageViewed,
            sort: this.sortFieldViewed,
            dir: this.sortOrderViewed
          },
        })
        .then(({ data }) => {
          this.$store.commit('notifications/setViewed', {
            content: data.content,
            totalElements: data.totalElements,
          })
          window.scrollTo(0, 0)
        })
        .catch((error) => {})
    },
    loadDataByFilterNotViewed() {
      let payload = {
        condition: this.searchGlobalNotViewed === '' ? 'and' : 'or',
        rules: [
          {
            label: 'object',
            field: 'object',
            operator: 'ilike',
            type: 'string',
            value:
              this.objectNotViewed === ''
                ? this.searchGlobalNotViewed
                : this.objectNotViewed,
          },
          {
            label: 'message',
            field: 'message',
            operator: 'ilike',
            type: 'string',
            value:
              this.messageNotViewed === ''
                ? this.searchGlobalNotViewed
                : this.messageNotViewed,
          },
          {
            label: 'level',
            field: 'level',
            operator: 'ilike',
            type: 'string',
            value:
              this.levelNotViewed === ''
                ? this.searchGlobalNotViewed
                : this.levelNotViewed,
          },
          {
            label: 'operation',
            field: 'operation',
            operator: 'ilike',
            type: 'string',
            value:
              this.operationNotViewed === ''
                ? this.searchGlobalNotViewed
                : this.operationNotViewed,
          },
          {
            label: 'createdBy',
            field: 'createdBy',
            operator: 'ilike',
            type: 'string',
            value:
              this.createdByNotViewed === ''
                ? this.searchGlobalNotViewed
                : this.createdByNotViewed,
          },
          /*{
            label: 'createDate',
            field: 'createDate',
            operator: 'ilike',
            type: 'string',
            value:
              this.createDateNotViewed === ''
                ? this.searchGlobalNotViewed
                : this.createDateNotViewed
          }*/
        ],
      }

      backApi
        .post(`/user_notifications/search/${false}`, payload, {
          params: {
            page: this.pageNotViewed,
            limit: this.perPageNotViewed,
            sort: this.sortFieldNotViewed,
            dir: this.sortOrderNotViewed,
          },
        })
        .then(({ data }) => {
          this.$store.commit('notifications/setNotViewed', {
            content: data.content,
            totalElements: data.totalElements,
          })
          window.scrollTo(0, 0)
        })
        .catch((error) => {})
    },
    onPageViewed(event) {
      this.pageViewed = event.page
      this.loadDataByFilterViewed()
    },
    onSortViewed(event) {
      this.sortFieldViewed = event.sortField
      if (event.sortOrder == 1) this.sortOrderViewed = 'desc'
      else this.sortOrderViewed = 'asc'

      this.loadDataByFilterViewed()
    },
    onPageNotViewed(event) {
      this.pageNotViewed = event.page
      this.loadDataByFilterNotViewed()
    },
    onSortNotViewed(event) {
      this.sortFieldNotViewed = event.sortField
      if (event.sortOrder == 1) this.sortOrderNotViewed = 'desc'
      else this.sortOrderNotViewed = 'asc'
      this.loadDataByFilterNotViewed()
    },
  },
  beforeMount() {
    this.loadDataByFilterViewed()
    this.loadDataByFilterNotViewed()
  },
}
</script>

<style lang="scss">
.p-datatable-wrapper .p-filter-column {
  position: relative;
  background-color: 'red';
  overflow: hidden;
  input {
    width: 100%;
  }
}
#notificationsTableId {
  margin-top: 1rem;
  overflow: auto !important;
  @include respond('tab-port') {
    overflow-x: hidden !important;
    width: 100%;
    .b-tabs .tab-content {
      padding: 1rem 0;
    }
    tr{
      padding: 0 .3rem;
    }
  }
}
#notificationsTableId .p-datatable table {
  position: relative;
  border-collapse: collapse;
  width: 100%;
  table-layout: fixed !important;
}
#notificationsTableId tbody > tr > td {
  margin-top: 10px;
  line-height: 1.5 !important;
  word-wrap: break-word;
  margin-left: 10px;
  padding: 10px 0;
  text-align: center;
  vertical-align: middle;
}
#notificationsTableId tbody > tr > td:hover {
  text-overflow: unset !important;
  white-space: normal;
}
.p-datatable .p-datatable-thead > tr > th {
  text-align: center !important;
}

#notificationsTableId .tabs a {
  padding: 1.5em 1em;
}
#notificationsTableId .small-td-size {
  width: 100px !important;
}
#notificationsTableId .medium-td-size {
  width: 150px !important;
}
#notificationsTableId .large-td-size {
  width: 300px !important;
}
</style>
