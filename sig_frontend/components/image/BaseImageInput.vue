<template>
  <div
    class="base-image-input"
    :style="{ 'background-image': `url(${imageData})`,'width' : this.width,'height' : this.height }"
    @click="chooseImage"
  >
    <span
      v-if="!imageData"
      class="placeholder"
    >
      Choisir une image
    </span>
    <input
      class="file-input"
      ref="fileInput"
      type="file"
      @input="onSelectFile"
    >
  </div>
</template>

<script>
export default {
  data() {
    return {
      imageData: null,
    }
  },
  props: ['imageUrl', 'readOnly','width','height'],

  methods: {
    chooseImage() {
      if (!this.readOnly) this.$refs.fileInput.click()
    },
    onSelectFile() {
      const input = this.$refs.fileInput
      const files = input.files
      if (files && files[0]) {
        const reader = new FileReader()
        reader.onload = e => {
          this.imageData = e.target.result
        }
        reader.readAsDataURL(files[0])
        this.$emit('input', files[0])
      }
    }
  },
  beforeMount() {
    if (this.imageUrl) this.imageData = this.imageUrl
  }
}
</script>
<style scoped>
.base-image-input {
  display: block;

  cursor: pointer;
  background-size: cover;
  background-position: center center;
}
.placeholder {
  background: #f0f0f0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #333;
  font-size: 16px;
  font-family: Helvetica;
}
.placeholder:hover {
  background: #e0e0e0;
}
.file-input {
  display: none;
}
</style>
