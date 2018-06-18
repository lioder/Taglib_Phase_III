<template>
  <div class='task-timeline-chart'>
    <div class="time-option">
      <span style="display: inline-block; margin-right: 10px; font-size: 16px"><i class="el-icon-time"></i> 选择时间段</span>
      <el-radio-group v-model="timeGap" size="medium">
        <el-radio-button v-for="time in timeOptions" :label="time" :key="time">{{time}}</el-radio-button>
      </el-radio-group>
    </div>
    <el-card>
      <div ref="taskTimelineChart" id="taskTimelineChart" style="width: 100%; height: 600px;"></div>
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
    name: 'task-timeline-chart',
    data () {
      return {
        timeGap: '近三天',
        timeOptions: ['近三天', '近一周', '近一个月', '近一年']
      }
    },
    mounted () {
      this.drawTaskTimelineChart()
    },
    computed: {
      rollDay: function () {
        if (this.timeGap === '近三天') {
          return -3
        } else if (this.timeGap === '近一周') {
          return -7
        } else if (this.timeGap === '近一个月') {
          return -30
        } else if (this.timeGap === '近一年') {
          return -365
        }
      }
    },
    watch: {
      'timeGap': function () {
        this.drawTaskTimelineChart()
      }
    },
    methods: {
      drawTaskTimelineChart: function () {
        let myChart = echarts.init(this.$refs.taskTimelineChart)
        myChart.setOption({
          title: {
            text: '众包任务生命周期',
            x: 'center'
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          legend: {
            data: ['管理员审核中', '专家完成任务中', '工人完成任务中', '任务提前结束时间'],
            y: '40px'
          },
          grid: {
            left: '8%',
            right: '4%',
            bottom: '3%',
            top: '18%',
            containLabel: true
          },
          xAxis: {
            type: 'value'
          },
          yAxis: {
            type: 'category',
            data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
          },
          dataZoom: [
            {
              type: 'slider',
              show: true,
              yAxisIndex: [0],
              left: '2%'
            }
          ],
          series: [
            {
              name: '管理员审核中',
              type: 'bar',
              stack: '总量',
              label: {
                normal: {
                  show: true,
                  position: 'insideRight'
                }
              },
              data: [320, 302, 301, 334]
            },
            {
              name: '专家完成任务中',
              type: 'bar',
              stack: '总量',
              label: {
                normal: {
                  show: true,
                  position: 'insideRight'
                }
              },
              data: [120, 132, 101, 134]
            },
            {
              name: '工人完成任务中',
              type: 'bar',
              stack: '总量',
              label: {
                normal: {
                  show: true,
                  position: 'insideRight'
                }
              },
              data: [220, 182, 191, 234]
            },
            {
              name: '任务提前结束时间',
              type: 'bar',
              stack: '总量',
              label: {
                normal: {
                  show: true,
                  position: 'insideRight'
                }
              },
              data: [150, 212, 201, 154]
            }
          ]
        })
        window.addEventListener('resize', function () {
          myChart.resize()
        })
        this.$ajax.get('/statistics/task/timeline', {
          params: {
            startDate: this.roll_date(this.rollDay),
            endDate: new Date().Format('yyyy-MM-dd')
          }
        }).then((res) => {
          let result = res.data
          if (result.code === 0) {
            let timeCircles = result.data
            let ids = []
            let times1 = [], times2 = [], times3 = [], times4 = []
            timeCircles.forEach(timeCircle => {
              ids.push(timeCircle.taskPublisherId + '')
              times1.push(timeCircle.publishToExamineTime + '')
              times2.push(timeCircle.examineToExpertSubmitTime + '')
              times3.push(timeCircle.expertSubmitToAutoExamineTime + '')
              times4.push(timeCircle.autoExamineToEndTime + '')
            })
            myChart.setOption({
              grid: {
                left: '8%',
                right: '10%',
                bottom: '3%',
                top: '15%',
                containLabel: true
              },
              xAxis: {
                name: '时间/分'
              },
              yAxis: {
                name: '任务ID',
                type: 'category',
                data: ids
              },
              series: [
                {
                  name: '管理员审核中',
                  type: 'bar',
                  stack: '总量',
                  label: {
                    normal: {
                      show: true,
                      position: 'inside'
                    }
                  },
                  data: times1
                },
                {
                  name: '专家完成任务中',
                  type: 'bar',
                  stack: '总量',
                  label: {
                    normal: {
                      show: true,
                      position: 'inside'
                    }
                  },
                  data: times2
                },
                {
                  name: '工人完成任务中',
                  type: 'bar',
                  stack: '总量',
                  label: {
                    normal: {
                      show: true,
                      position: 'inside'
                    }
                  },
                  data: times3
                },
                {
                  name: '任务提前结束时间',
                  type: 'bar',
                  stack: '总量',
                  label: {
                    normal: {
                      show: true,
                      position: 'inside'
                    }
                  },
                  data: times4
                }
              ]
            })
          }
        })
      },
      roll_date: function (day) {
        let date1 = new Date()
        let date2 = new Date(date1)
        date2.setDate(date1.getDate() + day)
        return date2.getFullYear() + '-' + this.padLeft(2, date2.getMonth() + 1) + '-' + this.padLeft(2, date2.getDate())
      },
      padLeft: function (size, str) {
        str = '' + str
        while (str.length < size) {
          str = '0' + str
        }
        return str
      }
    }
  }
</script>

<style lang="stylus">
  .task-timeline-chart
    .time-option
      margin-bottom 30px
</style>
