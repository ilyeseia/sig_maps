<template>
  <section class="action-shortcut">
    <div class="action-shortcut__layers action-shortcut__actions">
      <h2>Carte</h2>
      <div class="d-flex justify-content-around">
        <div
          v-if="
            this.profile.roles.includes('ROLE_ADMIN') ||
            this.profile.roles.includes('MAP_READ_AUTHORITY')
          "
          class="action-shortcut__action"
        >
          <Button
            icon="fas fa-filter"
            class="p-button-rounded p-button-success p-mr-2"
            @click="$emit('filterLayers')"
          />
          <p>
            Filtrage <br />
            avancé
          </p>
        </div>
        <div class="action-shortcut__action">
          <Button
            icon="fas fa-layer-group"
            class="p-button-rounded p-button-success p-mr-2"
            @click="$emit('showLayers')"
          />
          <p>
            Liste <br />
            des couches
          </p>
        </div>
        <div class="action-shortcut__action">
          <Button
            icon="fas fa-search"
            class="p-button-rounded p-button-success p-mr-2"
            @click="$emit('globalFilter')"
          />
          <p>
            Recherche <br />
            Global
          </p>
        </div>
      </div>
    </div>
    <div
      v-if="profile.authenticated"
      class="action-shortcut__adding action-shortcut__actions"
    >
      <h2>Ajouter nouveau</h2>
      <div class="d-flex justify-content-around">
        <div class="action-shortcut__action">
          <Button
            icon="fa fa-map-marker-alt"
            class="p-button-rounded p-button-success p-mr-2"
            @click="$emit('newGeometry', 'point')"
          />
          <p>Point</p>
        </div>
        <div class="action-shortcut__action">
          <Button
            icon="fa fa-draw-polygon"
            class="p-button-rounded p-button-success p-mr-2"
            @click="$emit('newGeometry', 'polygon')"
          />
          <p>Polygon</p>
        </div>
        <div class="action-shortcut__action">
          <Button
            icon="fa fa-wave-square"
            class="p-button-rounded p-button-success p-mr-2"
            @click="$emit('newGeometry', 'line')"
          />
          <p>Line</p>
        </div>
      </div>
    </div>
    <div class="action-shortcut__tools action-shortcut__actions">
      <h2>Outils</h2>
      <div class="d-flex justify-content-around">
        <div class="action-shortcut__action">
          <Button
            icon="fas fa-street-view"
            class="p-button-rounded p-button-success p-mr-2"
            @click="$emit('showProximityTool')"
          />
          <p>Approximation</p>
        </div>
        <div class="action-shortcut__action">
          <Button
            icon="fa fa-ruler-combined"
            class="p-button-rounded p-button-success p-mr-2"
            @click="$emit('measureDistance')"
          />
          <p>Mesure</p>
        </div>
        <div
          v-if="
            this.profile.roles.includes('ROLE_ADMIN') ||
            this.profile.roles.includes('GEOPROCESSING')
          "
          class="action-shortcut__action"
        >
          <Button
            icon="fas fa-shapes"
            class="p-button-rounded p-button-success p-mr-2"
            @click="$emit('showGeoProccessingTools')"
          />
          <p>Géotraitement</p>
        </div>
        <div class="action-shortcut__action">
          <Button
            icon="fas fa-directions"
            class="p-button-rounded p-button-success p-mr-2"
            @click="$emit('shareLayers')"
          />
          <p>Impression</p>
        </div>
        <div class="action-shortcut__action">
          <Button
            icon="fas fa-print"
            class="p-button-rounded p-button-success p-mr-2"
            @click="$emit('shareLayers')"
          />
          <p>Itinéraire</p>
        </div>
      </div>
    </div>
    <div
      id="action-shortcut__base-layer"
      class="
        action-shortcut__adding
        action-shortcut__actions
        action-shortcut__base-layer
      "
    >
      <h2>Couches de base</h2>
    </div>
  </section>
</template>

<script>
import Button from 'primevue/button'
import { mapState, mapGetters } from 'vuex'
export default {
  components: {
    Button,
  },
  computed: {
    ...mapState(['profile']),
    ...mapGetters({
      baseLayers: 'app/getBaseLayers',
    }),
  },
  props: ['baseLayers'],
  mounted() {
    let actionsShortcut = document.querySelector('.action-shortcut__base-layer')
    if (actionsShortcut && this.baseLayers) {
      actionsShortcut.appendChild(this.baseLayers)
    }
  },
}
</script>

<style lang="scss">
.action-shortcut {
  height: 50vh;
  background-color: $color-grey-light-1;
  &__actions {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 1rem 1rem;
    background-color: #fff;
    &:not(:last-child) {
      margin-bottom: 6px;
    }
    h2 {
      font-weight: 500;
    }
    > div {
      margin-top: 1rem;
      width: 80%;
      flex-wrap: wrap;
    }
    &:nth-child(1) {
      padding-top: 0;
    }
    button {
      height: 2.8rem !important;
      width: 2.8rem !important;
    }
  }
  &__action {
    width: 33.33%;
    text-align: center;
    &:nth-child(n + 4) {
      margin-top: 1rem;
    }
    p {
      margin-top: 5px;
      line-height: 13px;
    }
  }
}
</style>