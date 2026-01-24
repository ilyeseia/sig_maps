<template>
  <div v-if="isLoaded" class="pdf-reader">
    <div class="pdf-reader__pages">
      <div
        v-for="i in numPages"
        :key="i"
        :id="`page-${i + 1}`"
        @click="goTo(i + 1)"
        :style="{ opacity: i + 1 === pageToShow ? 1 : 0.7 }"
      >
        <div :class="{ selected: i + 1 === pageToShow ? true : false }">
          <pdf
            :src="src"
            :page="i + 1"
            style="display: block; width: 100%; margin: 0 auto"
          ></pdf>
        </div>
        <span class="pdf-reader__page-number">{{ i }}</span>
      </div>
    </div>
    <div class="pdf-reader__selected-page">
      <div class="pdf-reader__toolbar">
        <h3>Manuel d'utilisation</h3>
        <div>
          <strong>{{ pageToShow - 1 }}</strong
          >&nbsp;/&nbsp;{{ numPages }}
          <div class="separator"></div>
          <i @click="zoom -= 10" class="fa fa-minus"></i>
          <b-input
            oninput="this.value = this.value.replace(/[^0-9.]/g, '');
                this.value = this.value.replace(/(\..*)\./g, '$1');"
            type="text"
            v-model="zoom"
          ></b-input>
          <i @click="zoom += 10" class="fa fa-plus"></i>
        </div>
      </div>
      <div class="pdf-reader__page">
        <pdf
          :src="src"
          :page="pageToShow"
          @link-clicked="linkClicked"
          class="pdf"
          :style="`transform: scale(${zoom / 100})`"
          :key="key"
        ></pdf>
      </div>
    </div>
  </div>
</template>

<script>
import pdf from 'vue-pdf'
import pageTitle from '~/mixins/page-title'
import { backend } from '../../../constants'
var loadingTask = pdf.createLoadingTask(
  `${backend}/download/help/KharitaDz_Help.pdf`
)

export default {
  mixins: [pageTitle],
  components: {
    pdf,
  },
  data() {
    return {
      helpDocument: null,
      currentPage: 0,
      src: loadingTask,
      numPages: 0,
      pageToShow: 2,
      zoom: 100,
      isLoaded: false,
      key: 1,
      page: {
        title: "Manuel d'utilisation",
        hid: '',
        name: '',
        description: '',
      },
    }
  },
  watch: {
    pageToShow(newVal, oldVal) {
      if (newVal != oldVal && newVal > 0) {
        document.getElementsByClassName('pdf-reader__page')[0].scrollTo(0, 0)
        document.getElementsByClassName('pdf-reader__pages')[0].scrollTop =
          document.getElementById(`page-${this.pageToShow}`).offsetTop - 20
      }
    },
  },
  methods: {
    logContent() {
      this.$refs.myPdfComponent.pdf.forEachPage(function (page) {
        return page.getTextContent().then(function (content) {
          var text = content.items.map((item) => item.str)
        })
      })
    },
    linkClicked(e) {
      this.pageToShow = e
      window.scrollY = 0
    },
    goTo(i) {
      this.pageToShow = i
    },
  },
  beforeMount() {
    this.$nextTick(() => {
      this.$nuxt.$loading.start()
      this.src.promise
        .then((pdf) => {
          this.numPages = pdf.numPages > 0 ? pdf.numPages - 1 : 0
          this.$nuxt.$loading.finish()
          setTimeout(() => {
            this.isLoaded = true
          }, 200)
        })
        .catch((e) => {
          this.$nuxt.$loading.finish()
        })
    })
    document.getElementById('__nuxt').style.height = '100%'
    document.getElementsByTagName('html')[0].classList.add('hidden-scrollbar')
  },
  beforeDestroy() {
    this.numPages = 0
    document
      .getElementsByTagName('html')[0]
      .classList.remove('hidden-scrollbar')
  },
}
</script>
<style lang="scss">
.pdf-reader {
  position: absolute;
  display: flex;
  max-height: 100%;
  width: 100%;
  overflow: hidden;
  &__pages {
    flex: 0.15;
    overflow-y: auto;
    padding: 20px 0;
    > div {
      cursor: pointer;
      width: 60%;
      margin: 0 auto;
      margin-bottom: 15px;
      transition: all 0.1s ease-out;
      &:hover {
        opacity: 1 !important;
      }
    }
    .selected {
      border: 4px solid $color-primary;
    }
  }
  &__selected-page {
    flex: 0.85;
    display: flex;
    flex-direction: column;
    .pdf {
      display: block;
      width: 90%;
      height: 100%;
      margin: 0 auto;
    }
  }
  &__toolbar {
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: #dbdbdb;
    padding: 10px 30px;
    h3 {
      font-weight: bold;
    }
    div {
      display: flex;
      align-items: center;
    }
    .separator {
      height: 23px;
      width: 1px;
      background-color: #01010152;
      margin: 0 8px 0 15px;
    }
    i {
      margin: 0 10px;
      cursor: pointer;
    }
    input {
      width: 50px;
      height: 35px;
    }
  }
  &__page {
    flex-grow: 1;
    overflow-y: auto;
  }
  &__page-number {
    display: block;
    margin: 0 auto;
    text-align: center;
  }
}
</style>