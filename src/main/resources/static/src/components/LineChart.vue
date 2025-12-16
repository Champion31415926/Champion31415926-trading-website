<template>
  <div class="line-main">
    <div class="chart-top">
      <span class="tag">{{ tag }}</span>
      <span class="time-show">
        <span class="top-bar" style="font-size: 12px;">Time Selection</span>
        <el-select size="mini" style="width: 90px;" v-model="selectedValue" placeholder="Period">
          <el-option v-for="item in options" :key="item.num" :label="item.name" :value="item.num">
          </el-option>
        </el-select>
      </span>
    </div>
    <div ref="chart" :style="{ width: '100%', height: height }"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts';

export default {
  name: 'LineChart',
  props: {
    tag: {
      type: String,
      default: 'Line Chart'
    },
    values: {
      type: Array,
      required: true
    },
    date: {
      type: Array,
      required: true
    },
    height: {
      type: String,
      default: '220px'
    },
  },
  watch: {
    selectedValue(v1) {
      this.$emit('on-selected', v1);
    },
    values() {
      this.initChart();
    },
    date() {
      this.initChart();
    }
  },
  data() {
    return {
      chart: null,
      options: [
        { num: 7, name: '7 days' }, 
        { num: 30, name: '30 days' }, 
        { num: 60, name: '60 days' },
        { num: 365, name: '1 year' }
      ],
      selectedValue: 365,
    }
  },
  mounted() {
    this.initChart();
    window.addEventListener('resize', this.resizeChart);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeChart);
    if (this.chart) {
      this.chart.dispose();
      this.chart = null;
    }
  },
  methods: {
    resizeChart() {
      if (this.chart) {
        this.chart.resize();
      }
    },
    
    initChart() {
      if (!this.$refs.chart) return;
      
      this.chart = echarts.init(this.$refs.chart);
      const option = {
        grid: {
          left: 30,
          right: 10,
          top: 30,
          bottom: 20,
          borderWidth: 0,
        },
        tooltip: {
          trigger: 'axis',
          formatter: '{b}：{c}',
          backgroundColor: 'rgba(255,255,255,0.9)',
          borderColor: 'rgba(158, 174, 255, 0.3)',
          borderWidth: 1,
          textStyle: {
            color: '#2c3e50'
          }
        },
        xAxis: {
          data: this.date,
          axisLine: { 
            show: true,
            lineStyle: {
              color: 'rgba(158, 174, 255, 0.3)'
            } 
          },
          axisTick: { show: false },
          axisLabel: {
            color: '#7f8c8d',
            fontSize: '12'
          },
        },
        yAxis: {
          axisLine: { show: false },
          axisTick: { show: false },
          axisLabel: {
            color: '#7f8c8d',
            fontSize: '12'
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(158, 174, 255, 0.1)'
            }
          }
        },
        series: [{
          name: 'Count',
          type: 'line',
          smooth: true,
          data: this.values,
          areaStyle: {
            color: 'rgba(158, 174, 255, 0.2)'
          },
          lineStyle: {
            color: '#4967ff',
            width: 2
          },
          itemStyle: {
            color: '#4967ff',
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: true,
            position: 'top',
            color: '#4967ff',
            fontSize: 11
          },
          symbol: 'circle',
          symbolSize: 6
        }]
      };
      
      this.chart.setOption(option);
    },
  },
};  
</script>

<style scoped lang="scss">
.line-main {
  border-radius: 8px;
  width: 100%;

  .chart-top {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
  }

  .tag {
    font-size: 15px;
    color: #2c3e50;
    font-weight: 500;
  }

  .time-show {
    display: flex;
    align-items: center;
    gap: 6px;
    color: #7f8c8d;
    font-size: 12px;

    .top-bar {
      white-space: nowrap;
    }

    ::v-deep .el-select {
      --el-select-input-font-size: 12px;
      
      .el-input__wrapper {
        box-shadow: none;
        border-color: rgba(158, 174, 255, 0.3);
        border-radius: 6px;
      }
    }
  }
}
</style>