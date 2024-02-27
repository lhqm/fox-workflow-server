<template>
  <svg aria-hidden="true" :style="getStyle">
    <use :xlink:href="icon"></use>
  </svg>
</template>

<script>
  export default {
    name: 'icon-svg',
    props: {
      name: {
        type: String,
        required: true
      },
      size: {
        type: [Number, String],
        default: 16
      }
    },
    computed: {
      icon() {
        return `#Z6-${this.name}`;
      },
      getStyle() {
        let s = `${this.size}`;
        s = `${s.replace('px', '')}px`;
        return {
          width: s,
          height: s
        };
      }
    }
  };
</script>
