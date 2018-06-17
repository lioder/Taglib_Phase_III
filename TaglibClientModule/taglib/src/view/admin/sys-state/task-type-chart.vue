<template>
  <div class='task-type-chart'>
    <el-card>
      <div ref="taskTypeChart" style="width: 100%; height: 275px"></div>
    </el-card>
  </div>
</template>

<script type="text/ecmascript-6">
  // 引入基本模板
  let echarts = require('echarts/lib/echarts')
  // 引入柱状图组件
  require('echarts/lib/chart/pie')
  require('echarts/lib/chart/bar')
  require('echarts/lib/chart/line')
  // 引入提示框和title组件
  require('echarts/lib/component/tooltip')
  require('echarts/lib/component/legend')
  require('echarts/lib/component/title')
  export default {
    name: 'task-type-chart',
    mounted () {
      this.drawTaskChart()
    },
    methods: {
      drawTaskChart () {
        let myChart = echarts.init(this.$refs.taskTypeChart)
        myChart.setOption({
          title: {
            text: '众包任务数量',
            x: 'center'
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          legend: {
            data: ['审核中', '进行中', '已完成']
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: [
            {
              type: 'value'
            }
          ],
          yAxis: [
            {
              type: 'category',
              axisTick: {show: false},
              data: ['审核中', '进行中', '已完成']
            }
          ],
          series: [
            {
              data: [10, 22, 35],
              type: 'bar',
              label: {
                normal: {
                  show: true,
                  position: 'inside'
                }
              }
            }
          ]
        })
        window.addEventListener('resize', function () {
          myChart.resize()
        })
        this.$ajax.get('/statistics/taskPublisher/state').then((res) => {
          let result = res.data
          if (result.code === 0) {
            let map = result.data
            let keys = Object.keys(map)
            let data = []
            keys.forEach((key) => {
              data.push(map[key])
            })
            myChart.setOption({
              legend: {
                data: keys
              },
              yAxis: {
                data: keys
              },
              series: [{
                data: data
              }]
            })
          }
        })
      }
    }
  }
</script>

<style lang="stylus">

</style>
