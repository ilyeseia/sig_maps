<template>
  <b-autocomplete
    style="width: 100%"
    :data="data"
    :placeholder="placeholder"
    :field="field"
    :loading="isFetching"
    :check-infinite-scroll="true"
    @typing="getAsyncData"
    @select="setValue"
    @infinite-scroll="getMoreAsyncData"
    :max-height="250"
  >
    <template slot-scope="props">
      <div v-if="layout === 'users'" class="media">
        <div class="media-left">
          <img
            width="32"
            style="margin-top: 3px"
            :src="`${props.option.avatar ? props.option.avatar : getAvatar}`"
          />
        </div>
        <div class="media-content">
          {{ props.option.username }}
          <br />
          <small>
            {{ props.option.firstName }}
            {{ props.option.lastName }}
          </small>
        </div>
      </div>
      <div v-else-if="layout === 'groups'">
        {{ props.option.name }}
      </div>
      <div v-else-if="layout === 'permissions'">
        {{ props.option.label }}
      </div>
    </template>
    <template #footer>
      <span v-show="page > totalPages">
        C'est ça! Aucun autre enregistrement trouvé.
      </span>
    </template>
  </b-autocomplete>
</template>

<script>
import avatar from '../../assets/icons/user.png'
import debounce from 'lodash/debounce'
export default {
  props: [
    'page',
    'totalPages',
    'placeholder',
    'field',
    'data',
    'layout',
    'added',
  ],
  computed: {
    getAvatar() {
      return avatar
    },
  },
  data() {
    return {
      selected: null,
      isFetching: false,
      name: '',
    }
  },
  methods: {
    setValue(value) {
      this.$emit('setSelected', value)
    },
    getAsyncData: debounce(function (name) {
      // String update
      if (this.name !== name) {
        this.name = name

        this.$emit('clear', () => {
          this.fetchData(name)
        })
      } else if (this.page <= this.totalPages) {
        this.fetchData(name)
      }
      // String cleared
      if (!name.length) {
        this.$emit('clear', () => {})
        return
      }
      // Reached last page
      if (this.page > this.totalPages) {
        return
      }
    }, 250),
    getMoreAsyncData: debounce(function () {
      this.getAsyncData(this.name)
    }, 250),
    fetchData(name) {
      if (!this.isFetching) {
        this.isFetching = true
        this.$emit('loadDataByFilter', name, () => {
          this.isFetching = false
          this.$emit('setPage', this.page + 1)
        })
      }
    },
  },
}
</script>