<template>
  <div
    :style="{ margin: margin }"
    :class="{
      'live-preview': true,
      origin: origin != undefined ? origin : true,
    }"
  >
    <div
      :style="{
        position: 'relative',
        width: `${getParentDimension}`,
        height: `${getParentDimension}`,
        backgroundColor: getBorderColor,
        transform: `rotate(${rotation}deg)`,
        transformOrigin: 'center',
        clipPath: getClipPath,
        borderRadius: shapeForm === 'circle' ? '50%' : '0',
      }"
    >
      <div
        :style="{
          position: 'absolute',
          top: '50%',
          left: '50%',
          transform: 'translate(-50%, -50%)',
          width: `${getChildDimension}`,
          height: `${getChildDimension}`,
          backgroundColor: backgroundColor,
          clipPath: getClipPath,
          borderRadius: shapeForm === 'circle' ? '50%' : '0',
          opacity: opacity,
          zIndex: 11,
        }"
      ></div>
      <div
        :style="{
          position: 'absolute',
          top: '50%',
          left: '50%',
          transform: 'translate(-50%, -50%)',
          width: `${getChildDimension}`,
          height: `${getChildDimension}`,
          backgroundColor: '#fff',
          clipPath: getClipPath,
          borderRadius: shapeForm === 'circle' ? '50%' : '0',
          zIndex: 8,
        }"
      ></div>
    </div>
    <!-- End Star -->
  </div>
</template>


<script>
export default {
  props: [
    'margin',
    'shapeForm',
    'width',
    'height',
    'backgroundColor',
    'opacity',
    'borderStyle',
    'borderWidth',
    'borderColor',
    'borderOpacity',
    'rotation',
    'origin',
  ],
  data(){
    return{
      bColor: this.borderColor
    }
  },
  watch:{
    borderColor(newVal, oldVal){
     if(newVal !== oldVal) this.bColor = newVal
    }
  },
  computed: {
    getParentDimension() {
      return `${parseInt(this.borderWidth * 3) + parseInt(this.width) + 8}px`
    },
    getChildDimension() {
      return `${88 - parseInt(this.borderWidth * 6)}%`
    },
    getBorderColor() {
      if ((this.borderColor === '#fff')) this.bColor = '#ffffff'
      let rgba = this.hexToRgb(this.bColor)
      if (rgba) {
        return `rgba(
        ${rgba.r},
        ${rgba.g},
        ${rgba.b},
        ${this.borderOpacity}
      )`
      } else return '#000'
    },
    getClipPath() {
      switch (this.shapeForm) {
        case 'star':
          return 'polygon(50% 0%, 61% 35%, 98% 35%, 68% 57%, 79% 91%, 50% 70%, 21% 91%, 32% 57%, 2% 35%, 39% 35%)'
        case 'triangle':
          return 'polygon(50% 0%, 0% 100%, 100% 100%)'
        default:
          return 'none'
      }
    },
  },
  methods: {
    hexToRgb(hex) {
      var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex)
      return result
        ? {
            r: parseInt(result[1], 16),
            g: parseInt(result[2], 16),
            b: parseInt(result[3], 16),
          }
        : null
    },
  },
}
</script>

<style scoped lang="scss">
.live-preview {
  display: flex;
  justify-content: center;
  margin-left: auto;
  @include respond('tab-port'){
    margin: 0 auto;
  }
  &.origin {
    height: 9rem;
    position: relative;
    width: 9rem;
    align-items: center;
    &::after {
      position: absolute;
      content: '';
      width: 100%;
      height: 1px;
      background-color: #0f0f0f66;
      opacity: 0.8;
      top: 50%;
      left: 0;
      z-index: 10;
    }
    &::before {
      position: absolute;
      content: '';
      height: 100%;
      width: 1px;
      background-color: #0f0f0f66;
      opacity: 0.8;
      top: 0;
      left: 50%;
      z-index: 10;
      transform: rotate(180deg);
    }
  }
}
</style>