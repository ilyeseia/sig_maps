<template>
  <form @submit.prevent="onSubmit">
    <div class="modal-card" style="width: 800px; height: 75vh">
      <header class="modal-card-head">
        <h1
          class="modal-card-title"
        >
          Carousel
        </h1>
      </header>
      <section
        class="modal-card-body"
        style="width: 800px; height: 800px; overflow: hidden"
      >
        <base-image-input
          v-for="img in images"
          :key="img.id"
          width="150px"
          height="150px"
          :imageUrl="img.file"
          v-model="img.file"
        />
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
        <button
          id="save-layer"
          type="submit"
          :class="['button', 'is-primary', { 'is-loading': isLoading }]"
        >
          Enregistrer
        </button>
      </footer>
    </div>
  </form>
</template>

<script>
import BaseImageInput from '../image/BaseImageInput'

export default {
  components: { BaseImageInput },
  props: ['images'],
  data() {
    return {
      isLoading: false,
    }
  },
  methods: {

  },
  beforeMount(){

  }
}
</script>