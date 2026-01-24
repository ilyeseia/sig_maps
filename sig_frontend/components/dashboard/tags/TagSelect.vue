<template>
      <form @submit.prevent="onSubmit">
    <div class="modal-card" style="width: 500px">
      <section class="modal-card-body">
        <b-field label="Add some tags">
            <b-taginput
                v-model="tags"
                ellipsis
                icon="label"
                placeholder="Add a tag">
            </b-taginput>
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
import RestApi from '../../../methods/api.js'

export default {
  props: ['elementToEdit'],
  data() {
    return {
      tags: [],
      isLoading : false,
    }
  },
  methods: {
    onSubmit(e) {
      const formData = new FormData(e.target)
      this.isLoading = true

      let element = {tags : []}

      this.tags.forEach(tag => {
        let newTag = {name: tag}
        element.tags.push(newTag)
      })

      this.saveElement(element)
    },
    saveElement(element) {
      RestApi.createOrUpdate(
        this.elementToEdit.type,
        element,
        this.elementToEdit,
        this.$store,
        this.$router,
        this.$notification
      ).then(data => {
        this.$emit('close')
        this.isLoading = false
      })
    }
  },
  beforeMount() {
    if (this.elementToEdit.tags) {
      this.elementToEdit.tags.forEach(tag => this.tags.push(tag.name))
    }
  }
}
</script>
