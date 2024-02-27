<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import * as echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme

const animationDuration = 6000

export default {
  name: 'BarChart',
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '300px'
    },
    data: {
      type: Array,
      default: function () {
        return [];
      }
    }
  },
  data() {
    return {
      chart: null
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart()
    })
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  watch: {
    data: {
      immediate: false,
      handler(val) {
        if (val) this.initChart();
      }
    }
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$el, 'macarons');
      let xAxisNames=[];
      let xAxis = this.data.filter((item) => {
        xAxisNames.push(item.name);
        return item.name;
      });
      let seriesData=this.data.filter((item) => {
        return item.value;
      });
      const option = {
        tooltip:{
          trugger:'axis',
          axisPointer:{
            type:'shadow'
          }
        },
        xAxis: {
          type: 'category',
          data: xAxisNames
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            data: seriesData,
            type: 'bar',
            emphasis:{
              focus:'series'
            }
          }
        ]
      };
      this.chart.setOption(option);
    }
  }
}
</script>
