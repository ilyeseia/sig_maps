<template>
  <div :class="{panel: true, disableOverlfow: !viewMode}" style="min-height: 100px">
    <p class="panel-heading" style="margin-bottom: 0">
      <i v-if="!viewMode" class="fa fa-list-ul" />
      <i v-else-if="viewMode === 'normal'" :class="icon" />
      {{ !viewMode ? 'List des ' : '' }}
      <span class="panel-heading--bold"> <slot name="title"></slot> </span>
    </p>
    <div class="panel-block">
      <slot></slot>
    </div>
  </div>
</template>

<script>
export default {
  props: ['viewMode', 'icon'],
}
</script>

<style lang="scss">
.panel {
  margin-top: 1rem;
  .disableOverlfow{
    overflow-x: hidden;
  }
  overflow-y: visible !important;
  .panel-heading {
    &--bold {
      font-weight: bold;
    }
  }
  .panel-block {
    > div {
      width: 100%;
    }
    .level {
      justify-content: center;
    }
  }
}
</style>