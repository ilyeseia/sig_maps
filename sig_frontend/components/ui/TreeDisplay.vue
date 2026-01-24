<template>
  <ul :key="listkey" :class="`tree-list ${root ? 'tree-list-root' : ''}`">
    <li
      :class="` ${
        selectedRV[`r-${n.id}`] && selectedRV[`r-${n.id}`].id === `r-${n.id}`
          ? 'selected'
          : ''
      }`"
      @click.stop="handleClick(n)"
      v-for="n in nodes"
      :key="n.id"
      :id="n.id"
    >
      <div class="d-flex justify-content-between align-items-center">
        <span>{{ n.name }}</span>
        <i class="fa fa-angle-right"></i>
      </div>
      <treeDisplay
        :nodes="n.subResources"
        :index="index"
        @handleItemClick="handleClick"
        :selectedRV="selectedRV"
      ></treeDisplay>
    </li>
    <slot></slot>
  </ul>
</template>

<script>
export default {
  name: 'treeDisplay',
  props: ['root', 'nodes', 'children', 'index', 'selectedRV'],
  data() {
    return {
      listkey: 1,
    }
  },
  methods: {
    handleClick(n) {
      this.$emit('handleItemClick', {
        node: n,
        children: null,
        index: this.index,
      })
    },
  },
}
</script>

<style lang="scss">
.repo {
  .dropdown-menu {
      margin-top: 5px;
    transform: translateX(-77%) !important;
  }
}
.tree-list {
  height: 100%;
  font-weight: normal;
  color: #363636;
  li {
    cursor: pointer;
    position: relative;
    ul {
      margin-left: 2rem;
      li {
        position: relative;
        overflow: hidden;
        &::before {
          position: absolute;
          content: '';
          top: 18px;
          width: 20px;
          height: 2px;
          background-color: transparent;
          border-bottom: 2px dashed #ccc;
        }
        > div {
          position: relative;
          &::after {
            position: absolute;
            content: '';
            height: 500%;
            top: 0;
            width: 2px;
            background-color: transparent;
            border-left: 2px dashed #ccc;
            left: 0;
          }
        }
      }
    }
    > div {
      padding: 0.5rem 1.5rem 0.5rem 2rem;
    }
    &:hover {
      background-color: transparent !important;
      > div {
        background: #00000014;
      }
      ul {
        background-color: transparent;
      }
    }
    &.selected {
      > div {
        background: #00000014;
        font-weight: bold;
        color: #020202;
      }
      ul {
        background-color: transparent;
      }
    }
    i {
      opacity: 0.8;
    }
  }
}
</style>