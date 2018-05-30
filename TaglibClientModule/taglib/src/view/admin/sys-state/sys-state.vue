<template>
  <div class="sys-state">
    <el-row :gutter="50">
      <el-col :span="9">
        <el-card class="user-amount">
          <div ref="userChart" id="userChart" style="width: 100%; height: 275px"></div>
        </el-card>
      </el-col>
      <el-col :span="15">
        <el-card>
          <div ref="userGrowthChart" style="width: 100%; height: 275px;"></div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="50">
      <el-col :span="9">
        <el-card>
          <div ref="taskTypeChart" style="width: 100%; height: 275px"></div>
        </el-card>
      </el-col>
      <el-col :span="15">
        <el-card>
          <div ref="taskChart" style="width: 100%; height: 275px;"></div>
        </el-card>
      </el-col>
    </el-row>
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
    name: 'sys-state',
    components: {},
    methods: {
      drawUserChart () {
        let myChart = echarts.init(this.$refs.userChart)
        // 绘制图表
        myChart.setOption({
          title: {
            text: '用户数量',
            x: 'center'
          },
          legend: {
            show: true,
            orient: 'horizontal',
            bottom: 0,
            data: ['众包工人', '众包发起者']
          },
          tooltip: {
            trigger: 'item',
            position: 'inside',
            formatter: '{b} : {c} ({d}%)'
          },
          series: [
            {
              name: '用户数量',
              type: 'pie',
              radius: '65%',
              center: ['50%', '50%'],
              data: [],
              itemStyle: {
                emphasis: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
                },
                normal: {
                  label: {
                    show: false
                  },
                  labelLine: {
                    show: false
                  }
                }
              }
            }
          ]
        })
        window.addEventListener('resize', function () {
          myChart.resize()
        })
        this.$ajax.get('/admin/list/worker', {
          params: {
            size: 9,
            page: 1,
            keyword: '',
            sortBy: '全部',
            isSec: true
          }
        }).then((res) => {
          let result = res.data
          if (result.code === 0) {
            var page1 = result.data
          }
          this.$ajax.get('/admin/list/publisher', {
            params: {
              size: 9,
              page: 1,
              keyword: '',
              sortBy: '全部',
              isSec: true
            }
          }).then((res) => {
            let result = res.data
            if (result.code === 0) {
              let page2 = result.data
              myChart.setOption({
                series: [
                  {
                    data: [{value: page1.totalItemNum, name: '众包工人'}, {value: page2.totalItemNum, name: '众包发起者'}]
                  }
                ]
              })
            }
          })
        })
      },
      drawTaskChart () {
        let myChart = echarts.init(this.$refs.taskChart)
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
      },
      drawUserGrowthChart: function () {
        let myChart = echarts.init(this.$refs.userGrowthChart)
        let defaultData = [['2000-06-05', 116], ['2000-06-06', 129], ['2000-06-07', 135], ['2000-06-08', 86], ['2000-06-09', 73], ['2000-06-10', 85], ['2000-06-11', 73], ['2000-06-12', 68], ['2000-06-13', 92], ['2000-06-14', 130], ['2000-06-15', 245], ['2000-06-16', 139], ['2000-06-17', 115], ['2000-06-18', 111], ['2000-06-19', 309], ['2000-06-20', 206], ['2000-06-21', 137], ['2000-06-22', 128], ['2000-06-23', 85], ['2000-06-24', 94], ['2000-06-25', 71], ['2000-06-26', 106], ['2000-06-27', 84], ['2000-06-28', 93], ['2000-06-29', 85], ['2000-06-30', 73], ['2000-07-01', 83], ['2000-07-02', 125], ['2000-07-03', 107], ['2000-07-04', 82], ['2000-07-05', 44], ['2000-07-06', 72], ['2000-07-07', 106], ['2000-07-08', 107], ['2000-07-09', 66], ['2000-07-10', 91], ['2000-07-11', 92], ['2000-07-12', 113], ['2000-07-13', 107], ['2000-07-14', 131], ['2000-07-15', 111], ['2000-07-16', 64], ['2000-07-17', 69], ['2000-07-18', 88], ['2000-07-19', 77], ['2000-07-20', 83], ['2000-07-21', 111], ['2000-07-22', 57], ['2000-07-23', 55], ['2000-07-24', 60]]
        let defaultDateList = defaultData.map(function (item) {
          return item[0]
        })
        let defaultValueList = defaultData.map(function (item) {
          return item[1]
        })
        myChart.setOption({
          visualMap: [{
            show: false,
            type: 'continuous',
            seriesIndex: 0,
            min: 0,
            max: 400
          }],
          title: [{
            left: 'center',
            text: '用户增长图'
          }],
          grid: {
            left: '3%',
            right: '4%',
            bottom: '5%',
            containLabel: true
          },
          tooltip: {
            trigger: 'axis'
          },
          xAxis: [{
            data: defaultDateList
          }],
          yAxis: [{
            name: '人数',
            splitLine: {show: false}
          }],
          series: [{
            type: 'line',
            showSymbol: false,
            data: defaultValueList
          }]
        })
        window.addEventListener('resize', function () {
          myChart.resize()
        })
        this.$ajax.get('/statistics/user/tendency', {
          params: {
            startDate: '2018-04-26',
            endDate: new Date().Format('yyyy-MM-dd'),
            interval: 1
          }
        }).then((res) => {
          let result = res.data
          if (result.code === 0) {
            let data = result.data
            let myData = []
            let dates = Object.keys(data)
            let count = 0
            dates.forEach((date) => {
              count += data[date]
              myData.push([date, count])
            })
            var dateList = myData.map(function (item) {
              return item[0]
            })
            var valueList = myData.map(function (item) {
              return item[1]
            })
            myChart.setOption({
              xAxis: [{
                data: dateList
              }],
              series: [
                {
                  data: valueList
                }
              ]
            })
          }
        })
      },
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
        })
        //   .catch(() => {
        //   this.$message.error('统计数据获取失败')
        // })
      }
    },
    mounted () {
      this.drawUserGrowthChart()
      this.drawUserChart()
      this.drawTaskChart()
      this.drawTaskTypeChart()
    }
  }
</script>

<style lang="stylus">
  .sys-state
    padding 25px 65px
    .el-row
      margin-bottom: 20px
      &:last-child
        margin-bottom: 0
</style>
