<template>
  <div class='task-type-chart'>
    <el-card>
      <div ref="taskTypeChart" style="width: 100%; height: 500px"></div>
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
      this.drawTaskTypeChart()
    },
    methods: {
      drawTaskTypeChart () {
        let myChart = echarts.init(this.$refs.taskTypeChart)
        myChart.setOption({
          title: {
            text: '任务话题统计',
            x: 'center'
          },
          tooltip: {
            formatter: '{b}: {c}({d}%)'
          },
          legend: {
            show: true,
            orient: 'horizontal',
            bottom: 0,
            data: ['动物', '植物', '人类', '医学']
          },
          series: [{
            type: 'pie',
            radius: '55%',
            data: [
              {value: 200, name: '动物'},
              {value: 30, name: '植物'},
              {value: 12, name: '人类'},
              {value: 56, name: '医学'}]
          }]
        })
        window.addEventListener('resize', function () {
          myChart.resize()
        })
        this.$ajax.get('/statistics/taskPublisher/taskType').then((res) => {
          let result = res.data
          if (result.code === 0) {
            let map = result.data
            let topics = Object.keys(map)
            let data = []
            topics.forEach((key) => {
              data.push({
                name: key,
                value: map[key]
              })
            })
            myChart.setOption({
              legend: {
                data: topics
              },
              series: [{
                data: data
              }]
            })
          }
        }).catch(() => {
          this.$message.error('统计数据获取失败')
        })
      }
    }
  }
</script>

<style lang="stylus">

</style>
