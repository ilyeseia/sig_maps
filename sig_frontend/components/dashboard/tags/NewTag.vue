<template>
  <form @submit.prevent="onSubmit">
    <div class="modal-card" style="width: 500px">
      <section class="modal-card-body">
        <b-field label="Name">
          <b-input
            id="tag-name"
            name="name"
            type="text"
            :value="tagToEdit ? tagToEdit.name : ''"
            placeholder="map name"
            required
          ></b-input>
        </b-field>
         <b-field label="Message">
          <b-input
            id="tag-msg"
            name="msg"
            maxlength="200"
            type="textarea"
            :value="tagToEdit ? tagToEdit.message : ''"
          ></b-input>
        </b-field>
      </section>
      <footer class="right-align modal-card-foot">
        <button id="close-map-form" class="button" type="button" @click="$parent.close()">Close</button>
        <button
          id="save-map"
          type="submit"
          :class="['button','is-primary' ,{'is-loading':isLoading}]"
        >Enregistrer</button>
      </footer>
    </div>
  </form>
</template>
<script>
import  RestApi  from '../../../methods/api.js'

export default {
  props: ['tagToEdit'],
  data() {
    return {
      isLoading: false,
      layers: []
    }
  },
  methods: {
    onSubmit(e) {
      const formData = new FormData(e.target)
      const newTag = { layers: [] }

      this.isLoading = true

      newTag.name = formData.get('name')
      newTag.message = formData.get('message')

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
  }
}
</script>
