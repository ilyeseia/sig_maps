<template>
  <div class="body">
    <slot></slot>
  </div>
</template>

<style lang="scss">
.body {
  padding: 0;
  margin: 0;
  flex-grow: 1 !important;
  position: relative;
  /* overflow-x: hidden; */
}

.modal-card-foot {
  justify-content: flex-end !important;
}
.pagination {
  ul {
    margin-bottom: 0 !important;
  }
  a {
    border-radius: 50% !important;
  }
}

.p-datatable td {
  .p-column-title {
    display: none;
  }
}
.data-table__actions {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  > span {
    margin: 0 0.3rem;
  }
  @include respond('tab-port') {
    justify-content: space-between;
  }
}

// Import Bulma's core
@import 'bulma/sass/utilities/_all';

// Set your colors
$primary: $color-primary;
.is-success {
  border-color: $primary !important;
  background-color: $primary !important;
}
.has-text-success {
  color: $primary !important;
}
.e-input-group:not(.e-float-icon-left):not(.e-float-input)::before,
.e-input-group:not(.e-float-icon-left):not(.e-float-input)::after,
.e-checkbox-wrappe,
.e-frame.e-check {
  background-color: $primary !important;
}
.e-dropdownbase .e-list-item.e-active {
  color: $primary !important;
}
.p-button-success {
  background-color: $primary !important;
}
.p-paginator {
  margin-top: 1.5rem;
  .p-paginator-first,
  .p-paginator-last {
    display: none !important;
  }
  &-element {
    color: #7a7a7a !important;
  }
  button {
    border-radius: 50% !important;
    background-color: #d4d4d4 !important;
    width: 2.25rem !important;
    height: 2.25rem !important;
    .pi-angle-left:before,
    .pi-angle-right:before {
      color: #7a7a7a !important;
    }
    &:not(:first-child) {
      margin: 0 10px;
    }
  }
  .p-highlight {
    background-color: $primary !important;
    border-color: $primary !important;
    color: white !important;
    &:focus,
    &:hover,
    &:visited {
      box-shadow: none !important;
    }
  }
}
.leaflet-control {
  z-index: 100;
}
.leaflet-top {
  // cursor: not-allowed !important;
  // position: fixed !important;
  // z-index: 1001 !important;
  // top: 14.8% !important;
  // height: fit-content !important;
  // background: red;
}
.ant-btn:not(.ant-btn-danger):hover {
  border-color: $primary !important;
  color: $primary !important;
}
.ant-pagination-item-active {
  border-color: $primary;
  background-color: $primary;
  color: white;
}
.ant-menu-root {
  .ant-menu-item a {
    font-size: 0.9rem;
  }
  .menu-item-parent {
    font-size: 1rem;
    > div a,
    > div span,
    > a {
      font-size: 1rem !important;
    }
    &:hover{
      color: $primary !important; 
    }
  }
}
.ant-menu-sub.ant-menu-inline > .ant-menu-item {
  height: 2.5rem !important;
  line-height: 2.5rem !important;
}
.ant-menu-item-selected > a {
  color: $primary !important;
}
.ant-pagination-item-active:focus,
.ant-pagination-item-active:hover {
  border-color: $primary !important;
}
.ant-pagination-item {
  border-radius: 50%;
}
.ant-pagination-prev a,
.ant-pagination-next a {
  border-radius: 50% !important;
  .anticon {
    vertical-align: 0;
  }
}
.ant-modal-confirm-body {
  .ant-modal-confirm-title {
    font-size: 1.1rem;
  }
  .ant-modal-confirm-content {
    font-size: 1rem;
  }
}
.ant-modal-confirm-btns button span {
  font-size: 1rem;
}
.modal-card-title {
  text-align: center;
  font-weight: bold;
  font-size: 1rem !important;
}

// Hide the scrollbar
.hidden-scrollbar {
  @include hideScroll;
}
.btn-primary:not(:disabled):not(.disabled):active {
  background: $primary !important;
}

.admin-data-table {
  overflow: scroll hidden;
  @include hideScroll;
}
// Links
$link: $primary;
$link-invert: $primary-invert;
$link-focus-border: $primary;

@keyframes tooltips-up {
  from {
    opacity: 0.9;
    transform: translate(0, 10%);
  }
  to {
    opacity: 0.9;
    transform: translate(0, 3%);
  }
}

[tooltip]:hover::before,
[tooltip]:hover::after {
  animation: tooltips-up 300ms ease-out forwards;
}
.leaflet-draw-draw-rectangle,
.leaflet-draw-draw-circle {
  height: 0 !important;
  border: none !important;
}

#scale-position {
  z-index: 100 !important;
}

// Import Bulma and Buefy styles
@import 'bulma';
@import 'buefy/src/scss/buefy';
</style>
