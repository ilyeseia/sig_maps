<template>
  <div @click="loadAvatar()">
    <input
      type="file"
      ref="file"
      :name="uploadFieldName"
      @change="loadAvatar"
      accept="image/*"
      class="input-file"
    />
    <a-avatar :size="64" :src="avatar" />
  </div>
</template>
<script>
import { backend } from '../../../constants'
import RestApi from '../../../methods/api.js'
import path from 'path-extra'

export default {
  props: ['user'],
  data() {
    return {
      imageFile: null,
      isLoading: false,
    }
  },
  methods: {
    async loadAvatar() {
      const formData = new FormData(e.target)

      let avatar = ''
      if (this.imageFile) {
        let avatarFileName = path.base(this.imageFile)
        let userOldAvatar = ''

        if (this.user) userOldAvatar = path.base(this.user.avatar)

        if (avatarFileName !== userOldAvatar) {
          let fileName = await RestApi.saveFile(this.imageFile)
          avatar = `${backend}/download/${fileName}`
        }
      }

      this.$emit('saved', avatar)
    },
    async loadAvatarByFolder() {
      const formData = new FormData(e.target)

      let avatar = ''
      if (this.imageFile) {
        let avatarFileName = path.base(this.imageFile)
        let userOldAvatar = ''

        if (this.user) userOldAvatar = path.base(this.user.avatar)

        if (avatarFileName !== userOldAvatar) {
          let fileName = await RestApi.saveFileInFolder(folderName, this.imageFile)
          avatar = `${backend}/download/photo/${fileName}`
        }
      }

      this.$emit('saved', avatar)
    },
  },
}
</script>
