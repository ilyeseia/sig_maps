<template>
  <vue-upload-multiple-image
    :idUpload="!view ? `upload${fieldName}` : null"
    :idEdit="!view ? `update${fieldName}` : null"
    @upload-success="beforeUpload"
    @before-remove="beforeRemove"
    @edit-image="editImage"
    dragText="Telecharger des photos"
    browseText="Selectioner"
    :data-images="getImages"
    :maxImage="getCarouselMaxSize"
    accept="image/jpeg,image/png,image"
    popupText=""
    dropText=""
    primaryText=""
    :showEdit="!view ? true : false"
    :showDelete="!view ? true : false"
    :showAdd="!view ? true : false"
  ></vue-upload-multiple-image>
</template>

<script>
import { backend, MAXCAROUSELSIZE, MAXIMAGESIZE } from "~/constants";
import VueUploadMultipleImage from "vue-upload-multiple-image";
export default {
  props: ["id", "fieldName", "layerId", "featureId", "images", "view", "imageLoaded"],
  components: {
    VueUploadMultipleImage,
  },
  data() {
    return {
      field: this.fieldName,
    };
  },
  computed: {
    getCarouselMaxSize() {
      return MAXCAROUSELSIZE;
    },
    getImages() {
      if (!this.imageLoaded) {
        if (this.images && this.images.length > 3) {
          return this.images
            .replace("[", "")
            .replace("]", "")
            .split(",")
            .map((imagePath, index) => {
              return {
                default: index == 0 ? 1 : null,
                highlight: index == 0 ? 1 : null,
                path: `${backend}/download/layers.${this.layerId}.${
                  this.featureId
                }.carousel.${this.fieldName}/${imagePath.trim()}`,
              };
            });
        } else {
          return [];
        }
      } else {
        return [];
      }
    },
  },
  methods: {
    beforeUpload(formData, index, fileList) {
      if (index + 1 <= MAXCAROUSELSIZE) {
        let allowUpload = true;
        let indexToRemove;
        try {
          fileList.forEach((f, index) => {
            if (f.name) {
              if (this.calculateImageSize(f.path) > MAXIMAGESIZE) {
                allowUpload = false;
                indexToRemove = index;
                throw new Error();
              }
            }
          });
        } catch (e) {
          this.$confirm({
            title: `L'image ne peut pas être supérieure à ${MAXIMAGESIZE / 1024} MO`,
            okText: "Fermer",
            okType: "danger",
            cancelText: "Non ",
            class: "max-size-dialog",
            onOk: () => {
              fileList.splice(indexToRemove, 1);
            },
            onCancel: () => {
              fileList.splice(indexToRemove, 1);
            },
          });
        }
        if (allowUpload) this.uploadImageSuccess(formData, index, fileList);
      } else {
        fileList.splice(index, 1);
        this.$confirm({
          title: "",
          content: `Vous avez dépassé le nombre maximale des images (${MAXCAROUSELSIZE}) à telecharger !`,
          okText: "Fermer",
          okType: "danger",
          cancelText: "Non",
          class: "max-length-dialog",
        });
      }
    },
    uploadImageSuccess(formData, index, fileList) {
      this.$emit("setImagesUrlToUpload", {
        fieldName: this.fieldName,
        imagesToUploadUrls: [],
      });
      this.$emit("setImagesToUpload", {
        fieldName: this.fieldName,
        imagesToUpload: [],
      });
      let imagesToUpload = [];
      let imagesToUploadUrls = [];
      fileList.forEach((f, index) => {
        if (f.name) {
          imagesToUpload.push(this.dataURItoBlob(f.path));
        } else {
          let spilletedPath = f.path.split("/");
          imagesToUploadUrls.push(spilletedPath[spilletedPath.length - 1]);
        }
      });
      this.$emit("setImagesUrlToUpload", {
        fieldName: this.fieldName,
        imagesToUploadUrls,
      });
      this.$emit("setImagesToUpload", {
        fieldName: this.fieldName,
        imagesToUpload,
      });
    },
    calculateImageSize(base64Image) {
      var sizeInBytes = 4 * Math.ceil(base64Image.length / 3) * 0.5624896334383812;
      return sizeInBytes / 1000;
    },
    dataURItoBlob(dataURI) {
      // convert base64/URLEncoded data component to raw binary data held in a string
      var byteString;
      if (dataURI.split(",")[0].indexOf("base64") >= 0)
        byteString = atob(dataURI.split(",")[1]);
      else byteString = unescape(dataURI.split(",")[1]);

      // separate out the mime component
      var mimeString = dataURI.split(",")[0].split(":")[1].split(";")[0];

      // write the bytes of the string to a typed array
      var ia = new Uint8Array(byteString.length);
      for (var i = 0; i < byteString.length; i++) {
        ia[i] = byteString.charCodeAt(i);
      }

      return new Blob([ia], { type: "png" });
    },
    beforeRemove(index, done, fileList) {
      this.$confirm({
        title: "Attention",
        content: "Êtes-vous sûr de vouloir supprimer cette image ?",
        okText: "Oui",
        okType: "danger",
        cancelText: "Non",
        onOk: () => {
          this.$nextTick(() => {
            this.$nuxt.$loading.start();

            setTimeout(() => this.$nuxt.$loading.finish(), 900);
          });
          done();
          this.$emit("setImagesUrlToUpload", {
            fieldName: this.fieldName,
            imagesToUploadUrls: fileList
              .filter((f) => !f.name)
              .map((file) => {
                let spilletedPath = file.path.split("/");
                return spilletedPath[spilletedPath.length - 1];
              }),
          });
          this.$emit("setImagesToUpload", {
            fieldName: this.fieldName,
            imagesToUpload: fileList
              .filter((f) => f.name)
              .map((f) => this.dataURItoBlob(f.path)),
          });
        },
        onCancel: () => {},
      });
    },
    editImage(formData, index, fileList) {
      setTimeout(() => {
        this.beforeUpload(formData, index, fileList);
      }, 1000);
    },
  },
};
</script>
<style>
.max-length-dialog .ant-modal-confirm-btns button:nth-child(1),
.max-size-dialog .ant-modal-confirm-btns button:nth-child(1) {
  display: none !important;
}
</style>
