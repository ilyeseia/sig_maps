<template>
  <form @submit.prevent="onSubmit">
    <div class="modal-card" style="width: 500px">
      <section class="modal-card-body">
           <b-field label="Users"></b-field>
        <b-field grouped>
           <b-autocomplete expanded
               v-model="name"
                placeholder="e.g. Admin"
                :keep-first="keepFirst"
                :open-on-focus="openOnFocus"
                :data="filteredDataArray"
                field="username"
                @select="option => selected = option">
            </b-autocomplete>
          <button
            id="add-new-tag-prop"
            class="button"
            type="button"
            @click="addTag"
            title="add new prop">
            <b-icon pack="fas" icon="plus"/>
          </button>
        </b-field>
        <b-field v-for="(tag) in tags" :key="tag.id" :id="tag.id">
          <b-input :value="tag.name" disabled></b-input>
          <button
            v-if="!tag.isNewTag"
            type="button"
            class="button"
            @click="deleteTag(tag.id)"
          >
            <b-icon pack="fas" icon="times-circle"></b-icon>
          </button>
        </b-field>
      </section>
      <footer class="right-align modal-card-foot">
        <button id="close-map-form" class="button" type="button" @click="$parent.close()">Close</button>
        <button
          id="save-tag"
          type="submit"
          :class="['button','is-primary' ,{'is-loading':isLoading}]"
        >Enregistrer</button>
      </footer>
    </div>
  </form>
</template>
<script>
import RestApi from '../../../methods/api.js'

export default {
  props: ['elementToTag'],
  data() {
    return {
      isLoading: false,
      tags,
      allTags: []
    }
  },
  computed: {
    filteredDataArray() {
      return this.allUsers.filter((option) => {
        return (
          option.username.
             toString()
            .toLowerCase()
            .indexOf(this.name.toLowerCase()) >= 0
        )
      })
    }
  },
  methods: {
    onSubmit(e) {
      const formData = new FormData(e.target)
      const newTag = { }

      this.isLoading = true

      newTag.name = formData.get('name')
      newTag.message = formData.get('msg')

      this.saveTag(newTag)
    },
    saveTag(tag) {
      RestApi.createOrUpdate('tags', tag, this.tagToEdit,this.$store, this.$router, this.$notification).then(data => {
        this.$emit('saved')
        this.isLoading = false
      })

    },
    addTag() {
      this.tags.push({ isNewTag: true })
    },
    deleteTag(id) {
      this.tags = this.tags.filter(tag => tag.id != id)
    }
  },
  beforeMount() {
    RestApi.getAll('tags',{}).then((tags) => this.allTags = tags)
  }
}
</script>
