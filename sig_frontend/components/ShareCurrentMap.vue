<template>
  <section>
    <form action="">
      <div class="modal-card" style="width: 800px; height: 75vh">
        <header class="modal-card-head">
          <p class="modal-card-title">Partagez votre carte avec d'autres</p>
          <button type="button" class="delete" @click="$emit('close')" />
        </header>
        <section class="modal-card-body">
          <b-tabs class="tab">
            <b-tab-item label="Partager la carte actuelle" icon="share">
              <div>
                <div class="html-link">
                  <div>
                    <b-field class="html-link-field" label="Lien Html">
                      <b-input id="htmlink" :value="sharedLink" />
                    </b-field>
                  </div>
                  <div>
                    <b-field>
                      <button
                        @click="copyTextHtmlLink"
                        class="p-button-success btn-copyhtmllink"
                      >
                        <i class="fas fa-copy"></i>
                      </button>
                    </b-field>
                  </div>
                </div>
              </div>

              <div class="social-media">
                <h1 class="social-media-title">
                  Partagez votre carte sur les plateformes de médias sociaux
                </h1>
                <div
                  class="d-flex justify-content-around align-items-center row-column"
                >
                  <div class="">
                    <ShareNetwork
                      @open="open"
                      network="twitter"
                      :url="sharedLink"
                      title="Hi there, I hope you are doing well, I would like to share with KharitaDZ map."
                      description="This week, I’d like to introduce you to 'Vite', which means 'Fast'. It’s a brand new development setup created by Evan You."
                      quote="EADN is one of the best comapny in Algeria"
                      hashtags="EDAN, DZ"
                    >
                      <button class="btn-share-twitter">
                        <i class="fab fa-twitter"></i> Share on Twitter
                      </button>
                    </ShareNetwork>
                  </div>
                  <div class="">
                    <ShareNetwork
                      @open="open"
                      network="facebook"
                      :url="sharedLink"
                      title="Hi there, I hope you are doing well, I would like to share with KharitaDZ map."
                      description="This week, I’d like to introduce you to 'Vite', which means 'Fast'. It’s a brand new development setup created by Evan You."
                      quote="EADN is one of the best comapny in Algeria"
                      hashtags="EDAN, DZ"
                    >
                      <button class="btn-share-facebook">
                        <i class="fab fa-facebook"></i> Share on Facebook
                      </button>
                    </ShareNetwork>
                  </div>
                </div>
              </div>
            </b-tab-item>
            <b-tab-item label="Intégrer une carte" icon="coding">
              <div>
                <div class="iframLink">
                  <b-field class="" label="Iframe">
                    <b-input
                      type="textarea"
                      :value="iframeLink"
                      id="iframLink"
                    />
                  </b-field>
                  <div>
                    <button
                      @click="copyTextIfram"
                      class="p-button-success btn-copyhtmllink"
                    >
                      <i class="fas fa-copy"></i>
                    </button>
                  </div>
                </div>
              </div>
            </b-tab-item>
          </b-tabs>
        </section>
        <footer class="modal-card-foot">
          <b-button label="Fermer" @click="$emit('close')" />
        </footer>
      </div>
    </form>
  </section>
</template>
<script>
import { frontend } from '../constants'
import { mapState } from 'vuex'
export default {
  props: ['features'],
  data() {
    return {
      getMapPublicLink: 'Abdeldjalil',
      getiframeLink: 'Hachimi',
      currentMap: this.$store.state.maps.currentMap,
      sharedLink: null,
      iframeLink: null,
      layerName: '',
      corrdinate: {},
    }
  },
  computed: {
    ...mapState(['maps']),
  },
  methods: {
    open(e) {
      e.preventDefault()
    },
    copyTextHtmlLink(e) {
      e.preventDefault()

      /* Get the text field */
      let copyText = document.getElementById('htmlink')
      /* Select the text field */
      copyText.select()
      copyText.setSelectionRange(0, 99999) /* For mobile devices */

      /* Copy the text inside the text field */
      document.execCommand('copy')
    },
    copyTextIfram(e) {
      e.preventDefault()
      /* Get the text field */
      let copyText = document.getElementById('iframLink')
      /* Select the text field */
      copyText.select()
      copyText.setSelectionRange(0, 99999) /* For mobile devices */

      /* Copy the text inside the text field */
      document.execCommand('copy')
    },
  },
  created() {
    const featurObj = this.$layerGroups['featureInfo']
    const obj = featurObj[Object.keys(featurObj)[1]]
    Object.entries(obj).map((key, index) => {
      key.map((item) => {
        for (const key in item['feature']) {
          if (Object.hasOwnProperty.call(item['feature'], key)) {
            const element = item['feature']['geometry']
            this.corrdinate = element
            break
          }
        }
      })
    })
  },
  beforeMount() {
    this.sharedLink = `${frontend}/dashboard/maps/public/${this.currentMap.id}?topo=${this.maps.activeLayer.topo}&lan=${this.corrdinate['coordinates'][1]}&lang=${this.corrdinate['coordinates'][0]}`
    this.iframeLink = `<iframe src="${frontend}/shareiframposition/?topo=${this.maps.activeLayer.topo}&lan=${this.corrdinate['coordinates'][1]}&lang=${this.corrdinate['coordinates'][0]}" allowtransparency="true" frameborder="0" scrolling="no" allowfullscreen mozallowfullscreen webkitallowfullscreen oallowfullscreen msallowfullscreen width="100%" height="300" ></iframe>`
  },
}
</script>
<style lang="scss" >
form {
  .box-model {
    width: 500px;
    position: absolute;
    padding: 10px;
    top: 0;
    left: 50%;
    background: gray;
    color: white;
  }

  .html-link, .iframLink {
    display: flex;
    width: 100%;
    align-items: flex-end;
    > div:nth-child(1){
      flex-grow: 1;
    }
  }
  .iframLink{
    align-items: flex-start;
    > div:nth-child(2){
      margin-top: 30px;
      @include respond('phone'){
        margin-top: 25px;
      }
    }
  }
  .btn-copyhtmllink {
    border: none;
    padding: 5px;
    width: 50px;
    border-radius: 3px;
    margin: 0 !important;
    margin-left: 1.5rem !important;
    i{
      color: #fff;
    }
  }
  .social-media{
    margin-top: 5rem;
  }
  .social-media-title {
    font-size: 1.2rem;
    text-align: center;
    font-weight: bold;
    margin-bottom: 2rem;
  }
  .btn-share-twitter {
    border: none;
    background: #1da1f2;
    color: white;
    padding: 10px;
  }
  .btn-share-facebook {
    border: none;
    background: #4267b2;
    color: white;
    padding: 10px;
  }
  .modal-card-body {
    overflow-x: hidden;
  }
}
</style>
