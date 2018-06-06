<template>
  <div class='user-view'>
    <div id="alipay_div"></div>
    <div class="user-info-wrapper">
      <div class="col-left">
        <div class="user-wrapper">
          <div v-show="!showUpload" class="avatar-box" style="width: 150px; height: 150px;"
               @mouseover="toggleChangeAvatarBtn"
               @mouseout="toggleChangeAvatarBtn">
            <img class="avatar" :src="getAvatar" width="150px" height="150px">
            <div class="change-avatar-btn" v-show="showChangeAvatarBtn" @click="toggleUpload">更换头像</div>
          </div>
          <div class="change-avatar-box" v-show="showUpload">
            <el-upload
              class="avatar-uploader"
              action="/upload/avatar"
              :data="uploadData"
              accept="image/*"
              :show-file-list="false"
              :on-success="uploadSuccess">
              <i class="el-icon-plus avatar-uploader-icon"></i>
              <div class="el-upload__tip" slot="tip">图片大小不超过2Mb</div>
            </el-upload>
          </div>
          <div class="userinfo-box">
            <div class="username">{{ user.username }}<span class="level">Lv.{{ getLevel }}</span></div>
            <div class="exp info-line">经验值: {{ user.exp }}</div>
            <div class="title info-line">称号: {{ getTitle }}</div>
            <div class="phone info-line">手机: {{ user.phone }}</div>
            <div class="email info-line">邮箱: {{ user.email }}</div>
          </div>
        </div>
      </div>
      <div class="col-right">
        <div class="reward-wrapper">
          <label>我的积分</label>
          <div class="reward-num">
            {{ user.points }}
          </div>
          <div class="attend" v-if="!user.isAttendant && user.userType === 0">
            <el-button type="danger" round size="mini" @click="attend">签到
            </el-button>
            <p v-show="user.isAttendant" class="attend-tip">今天已经签到过啦</p>
          </div>
          <div class="recharge" v-if="user.userType === 1">
            <el-button type="danger" round size="mini" @click="rechargeDialogVisible = true" v-="user.userType === 1">充值
            </el-button>
            <el-dialog title="充值积分" :visible.sync="rechargeDialogVisible" width="35%" top="25vh">
              <el-form>
                <el-form-item label="充值额度">
                  <div>
                    <el-input v-model="rechargeAmount" placeholder="请输入充值的额度(1RMB = 10积分)">
                      <span slot="prefix">￥</span>
                    </el-input>
                  </div>
                </el-form-item>
              </el-form>
              <div slot="footer" class="dialog-footer">
                <el-button @click="rechargeDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="recharge">确 定</el-button>
              </div>
            </el-dialog>

          </div>
        </div>
        <div class="counts-wrapper" v-show="user.userType === 0">
          <label>我的评价</label>
          <div class="task-counts">
            <div class="block">
              <div class="num">{{ (user.punctualityRate * 100).toFixed(1) }}%</div>
              <div class="label">准时率</div>
            </div>
            <div class="block">
              <div class="num">{{ (user.accuracyRate * 100).toFixed(1) }}%</div>
              <div class="label">正确率</div>
            </div>
            <div class="block">
              <div class="num">{{ (user.satisfactionRate * 100).toFixed(1) }}%</div>
              <div class="label">满意度</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="wrapper user-statistics-wrapper" v-show="this.$store.getters.userType === 0 && showStatistics">
      <div ref="taskTypeChart" style="width: 50%; height: 330px"></div>
    </div>
    <div class="for-worker" v-show="this.$store.getters.userType === 0">
      <div class="wrapper user-activity-wrapper">
        <div style="height: 170px; border-bottom: 1px solid rgba(7, 17, 27, 0.1)">
          <div class="header user-activity-header"><i class="el-icon-upload"></i> 活跃度</div>
          <div class="user-activity-content">
            <div style="position: absolute; top: 0; font-size: 12px; color: #333; transform: translateX(13px)"
                 :style="{left: 80 * (month-1) + 'px'}" v-for="(month, mi) in 12" :key="mi+1000">
              {{ year + '-' + padLeft(2, month)}}
            </div>
            <div style="display: inline-block" v-for="(day, index) in days" :key="index+100">
              <div class="rect-wrapper" v-for="(num,i) in day" :key="i"
                   :style="{top: i%7 * 14 + 20 + 'px', left: (Math.floor(i/7) * 14 + index * 5 * 16) + 'px'}">
                <el-tooltip
                  :content="(activeMap['' + (index+1)][i+1]?activeMap['' + (index+1)][i+1]:'No') + ' tags on ' + year + '-' + padLeft(2, index+1) +'-' + padLeft(2, i+1)"
                  placement="top" :enterable="false">
                  <div class="rect"
                       :class="{
                   'active-4': activeMap['' + (index+1)][i+1]>20,
                   'active-3': activeMap['' + (index+1)][i+1]>10,
                   'active-2': activeMap['' + (index+1)][i+1]>5,
                   'active-1': activeMap['' + (index+1)][i+1]>0,
                   }"></div>
                </el-tooltip>
              </div>
            </div>
          </div>
        </div>
        <div class="user-activity-num-wrapper">
          <div class="block">
            <div class="num">{{dateOfMostActivity === '' ? '尚未进行标注':dateOfMostActivity}}</div>
            <div class="label">标注数目最多的一天</div>
          </div>
          <div class="block">
            <div class="num">{{totalActivity}}</div>
            <div class="label">历史总标记数</div>
          </div>
          <div class="block">
            <div class="num">{{longestConDays}}</div>
            <div class="label">最长连标天数</div>
          </div>
        </div>
      </div>
      <div class="wrapper task-history-wrapper">
        <div class="header task-history-header"><i class="el-icon-document"></i> 审核结果</div>
        <div class="task-history-wrapper">
          <el-table
            :data="taskRecords" stripe>
            <el-table-column
              prop="date"
              label="审核日期"
              width="180">
            </el-table-column>
            <el-table-column
              prop="taskPublisherId"
              label="任务id"
              width="180">
            </el-table-column>
            <el-table-column
              prop="correct"
              label="正确标注/总标注">
              <template slot-scope="scope">
                <span>{{ scope.row.correct }}/{{ scope.row.sum }}</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="price"
              label="本次任务获得积分">
              <template slot-scope="scope">
                <span>{{ scope.row.price}} T币</span>
              </template>
            </el-table-column>
            <el-table-column label="操作">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="plain">查看详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
    <div class="wrapper pay-history-wrapper" v-show="this.$store.getters.userType === 1">
      <div class="pay-history-header">
        <i class="iconfont">&#xe633;</i>充值记录
      </div>
      <div class="pay-history-content">
        <el-table
          :data="payHistory"
          style="width: 100%">
          <el-table-column
            fixed
            prop="createTime"
            label="创建日期">
            <template slot-scope="scope">
              <span>{{ timestampToTime(scope.row.createTime)}}</span>
            </template>
          </el-table-column>
          <el-table-column
            prop="orderNo"
            label="订单号">
          </el-table-column>
          <el-table-column
            prop="amount"
            label="金额">
            <template slot-scope="scope">
              ￥<span style="margin-left: 8px">{{ scope.row.amount }}</span>
            </template>
          </el-table-column>
          <el-table-column
            prop="orderState"
            label="支付状态">
            <template slot-scope="scope">
              <i class="el-icon-circle-check" v-show="scope.row.orderState ==='PAID'"></i>
              <i class="el-icon-circle-close" v-show="scope.row.orderState !=='PAID'"></i>
              <span>{{ scope.row.orderState ==='PAID'?'已支付':'未支付' }}</span>
            </template>
          </el-table-column>
          <el-table-column
            prop="payTime"
            label="支付时间">
            <template slot-scope="scope">
              <span>{{ timestampToTime(scope.row.payTime)}}</span>
            </template>
          </el-table-column>
        </el-table>
        <div class="show-all-btn" v-if="payHistoryWhole.length >= 5">
          <div v-show="showAllHistory" @click="showAllPayHistory">
            <i class="el-icon-d-arrow-right" style="transform: rotate(90deg)"></i>
            展示所有充值记录
          </div>
          <div v-show="!showAllHistory" @click="hideAllPayHistory">
            <i class="el-icon-d-arrow-right" style="transform: rotate(-90deg)"></i>
            收起充值记录
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script type="text/ecmascript-6">
  import {mapMutations} from 'vuex'

  let echarts = require('echarts/lib/echarts')
  require('echarts/lib/chart/pie')
  require('echarts/lib/component/tooltip')
  require('echarts/lib/component/legend')
  require('echarts/lib/component/title')

  export default {
    name: 'user',
    data () {
      return {
        showStatistics: true,
        showAllHistory: true,
        days: [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31],
        rechargeDialogVisible: false,
        rechargeAmount: 0,
        showUpload: false,
        user: {},
        year: 2018,
        showChangeAvatarBtn: false,
        activeMap: {
          1: {1: 1},
          2: {},
          3: {},
          4: {},
          5: {},
          6: {},
          7: {},
          8: {},
          9: {},
          10: {},
          11: {},
          12: {}
        },
        payHistory: [{
          createTime: 1527176273000,
          amount: 90,
          orderState: 'INITIAL'
        }],
        payHistoryWhole: [],
        taskRecords: [{
          userId: 1,
          taskPublisherId: 9,
          date: '2018-9-0',
          price: 87,
          correct: 9,
          sum: 12
        },
          {
            userId: 1,
            taskPublisherId: 9,
            date: '2018-9-0',
            price: 87,
            correct: 9,
            sum: 12
          }]
      }
    },
    computed: {
      dateOfMostActivity: function () {
        let date = ''
        let maxActivity = 0
        for (let month in this.activeMap) {
          for (let day in this.activeMap[month]) {
            if (this.activeMap[month][day] >= maxActivity) {
              date = this.year + '-' + this.padLeft(2, month) + '-' + this.padLeft(2, day)
              maxActivity = this.activeMap[month][day]
            }
          }
        }
        return date
      },
      totalActivity: function () {
        let count = 0
        for (let month in this.activeMap) {
          for (let day in this.activeMap[month]) {
            count += this.activeMap[month][day]
          }
        }
        return count
      },
      longestConDays: function () {
        let days = 1
        let count = 1
        let isNew = true
        for (let month in this.activeMap) {
          for (let day in this.activeMap[month]) {
            isNew = false
            if (day - 1 >= 1) {
              // 没有跨月份的情况
              if (this.activeMap[month][day] > 0 && this.activeMap[month][day - 1] > 0) {
                count++
                console.log(count)
              } else {
                days = count > days ? count : days
                count = 1
              }
            } else {
              // 跨月份(不处理跨年）
              if (month - 1 >= 1 && this.activeMap[month][day] > 0 && this.activeMap[month - 1][this.days[month - 2]] > 0) {
                count++
              } else {
                days = count > days ? count : days
                count = 1
              }
            }
          }
        }
        return isNew ? 0 : (days > count ? days : count)
      },
      uploadData: function () {
        return {
          id: this.user ? this.user.id : 0
        }
      },
      getLevel: function () {
        switch (this.user.level) {
          case 'LEVEL_ONE':
            return 1
          case 'LEVEL_TWO':
            return 2
          case 'LEVEL_THREE':
            return 3
          case 'LEVEL_FOUR':
            return 4
          case 'LEVEL_FIVE':
            return 5
          case 'LEVEL_SIX':
            return 6
          case 'LEVEL_SEVEN':
            return 7
        }
      },
      getTitle: function () {
        switch (this.user.level) {
          case 'LEVEL_ONE':
            return '塑料'
          case 'LEVEL_TWO':
            return '玄铁'
          case 'LEVEL_THREE':
            return '青铜'
          case 'LEVEL_FOUR':
            return '白银'
          case 'LEVEL_FIVE':
            return '黄金'
          case 'LEVEL_SIX':
            return '铂金'
          case 'LEVEL_SEVEN':
            return '钻石'
        }
      },
      getAvatar: function () {
        if (!this.user || !this.user.id || this.user.avatar === 'default_avatar.png') {
          return '/static/image/default_avatar.png'
        } else {
          return '/show/avatar/' + this.user.id + '/' + this.user.avatar
        }
      }
    },
    mounted () {
      let userId = this.$store.getters.id
      this.$ajax.get('/user/info/' + userId).then((res) => {
        let result = res.data
        if (result.code === 0) {
          this.user = result.data
        } else {
          this.$message.error('查看个人信息失败！')
        }
      })
      if (this.$store.getters.userType === 0) {
        this.drawTaskTypeChart()
        this.getUserActivity()
        this.getTaskRecord()
      } else if (this.$store.getters.userType === 1) {
        this.getPayHistory()
      }
    },
    methods: {
      ...mapMutations({
        setAvatar: 'changeAvatar'
      }),
      attend: function () {
        this.$ajax.get('/user/' + this.user.id + '/attend').then((res) => {
          let result = res.data
          if (result.code === 0) {
            this.user.points += 1
            this.user.exp += 1
            this.user.isAttendant = true
          } else {
            this.$message.error('签到失败')
          }
        }).catch(() => {
          this.$message.error('签到失败')
        })
      },
      recharge: function () {
        // this.$ajax.post('/user/' + this.user.id + '/recharge', this.$qs.stringify({
        //     amount: this.rechargeAmount
        //   })
        // ).then((res) => {
        //   let result = res.data
        //   if (result.code === 0) {
        //     this.user.points += (Math.round(Number(this.rechargeAmount) * 10))
        //     this.$message.success('充值成功')
        //   } else {
        //     this.$message.error('充值失败')
        //   }
        // }).catch(() => {
        //   this.$message.error('充值失败')
        // }).finally(() => {
        //   this.rechargeDialogVisible = false
        // })
        this.$ajax.post('/alipay/orders', {
          userId: this.$store.getters.id,
          amount: this.rechargeAmount
        }).then((res) => {
          let order = res.data
          let orderNo = order.orderNo
          this.$ajax.get('/alipay/' + orderNo, {
            params: {
              from: 'pc',
              return_url: 'www.baidu.com'
            }
          }).then((res) => {
            document.getElementById('alipay_div').innerHTML = res.data.substring(0, res.data.indexOf("<script>"))
            document.forms[0].submit()
          })
        })
      },
      drawTaskTypeChart: function () {
        let myChart = echarts.init(this.$refs.taskTypeChart)
        myChart.setOption({
          title: {
            text: '任务话题统计',
            textStyle: {
              fontSize: 20,
              fontWeight: 700
            },
            x: 'center'
          },
          legend: {
            show: true,
            orient: 'horizontal',
            top: 40,
            data: ['动物', '人类', '医学']
          },
          tooltip: {},
          series: {
            type: 'pie',
            radius: '60%',
            center: ['50%', '60%'],
            data: [
              {name: '动物', value: 4},
              {name: '人类', value: 5},
              {name: '医学', value: 10}
            ]
          }
        })
        window.addEventListener('resize', function () {
          myChart.resize()
        })
        this.$ajax.get('/statistics/taskWorker/taskType', {
          params: {
            userId: this.$store.getters.id
          }
        }).then((res) => {
          let result = res.data
          if (result.code === 0) {
            let map = result.data
            let topics = Object.keys(map)
            let data = []
            if (data.length <= 0) {
              this.showStatistics = false
            }
            topics.forEach((topic) => {
              data.push({
                name: topic,
                value: map[topic]
              })
            })
            myChart.setOption({
              legend: {
                data: topics
              },
              series: {
                data: data
              }
            })
          }
        })
      },
      toggleChangeAvatarBtn: function () {
        this.showChangeAvatarBtn = !this.showChangeAvatarBtn
      },
      toggleUpload: function () {
        this.showUpload = !this.showUpload
      },
      uploadSuccess: function (response, file, fileList) {
        this.toggleUpload()
        this.user.avatar = file.name
        this.setAvatar(file.name)
      },
      getUserActivity: function () {
        this.year = new Date().getFullYear()
        this.$ajax.get('/user/' + this.$store.getters.id + '/activity', {
          params: {
            year: this.year
          }
        }).then((res) => {
          let result = res.data
          if (result.code === 0) {
            this.activeMap = result.data
          }
        }).catch(() => {
          this.$message.error('获取活跃度失败')
        })
      },
      getPayHistory: function () {
        this.$ajax.get('/alipay/orders/' + this.$store.getters.id).then((res) => {
          let result = res.data
          if (result.code === 0) {
            this.payHistoryWhole = result.data.reverse()
            if (this.payHistoryWhole.length >= 5) {
              this.payHistory = this.payHistoryWhole.slice(0, 4)
            } else {
              this.payHistory = this.payHistoryWhole
            }
          }
        }).catch(() => {
          this.$message.error('获取充值记录失败')
        })
      },
      getTaskRecord: function () {
        this.$ajax.get('/user/taskRecord/' + this.$store.getters.id).then((res) => {
          let result = res.data
          if (result.code === 0) {
            this.taskRecords = result.data
          }
        }).catch(() => {
          this.$message.error('获取审核结果失败')
        })
      },
      padLeft: function (size, str) {
        str = '' + str
        while (str.length < size) {
          str = '0' + str
        }
        return str
      },
      timestampToTime: function (timestamp) {
        if (timestamp === null || timestamp === undefined) {
          return ''
        }
        let date = new Date(timestamp) // 时间戳为10位需*1000，时间戳为13位的话不需乘1000
        let Y = date.getFullYear() + '-'
        let M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-'
        let D = date.getDate() + ' '
        let h = this.padLeft(2, date.getHours()) + ':'
        let m = this.padLeft(2, date.getMinutes()) + ':'
        let s = this.padLeft(2, date.getSeconds())
        return Y + M + D + h + m + s
      },
      showAllPayHistory: function () {
        this.payHistory = this.payHistoryWhole
        this.showAllHistory = false
      },
      hideAllPayHistory: function () {
        this.payHistory = this.payHistoryWhole.slice(0, 4)
        this.showAllHistory = true
      }
    }
  }
</script>

<style lang="stylus">
  .user-view
    padding 66px 0
    width: 1050px
    margin auto
    .user-info-wrapper
      display flex
      margin-bottom 20px
      padding 50px 0
      background-color #fff
      border 1px solid rgba(7, 17, 27, 0.1)
      .col-left
        flex 1
        border-right 1px solid rgba(7, 17, 27, 0.2)
        padding-left 50px
        .user-wrapper
          display flex
          .avatar-box
            flex 0 0 150px
            position: relative
            .change-avatar-btn
              position absolute
              bottom 5px
              left 0
              margin: 0 5px
              padding: 5px 0
              width: 140px
              text-align center
              background-color rgba(7, 17, 27, 0.3)
              color: #fff
              font-size 12px
          .change-avatar-box
            flex: 0 0 150px
            .avatar-uploader .el-upload
              border: 1px dashed #d9d9d9
              border-radius: 6px
              cursor: pointer
              position: relative
              overflow: hidden
            .avatar-uploader .el-upload:hover
              border-color: #409EFF
            .avatar-uploader-icon
              font-size: 28px
              color: #8c939d
              width: 150px
              height: 150px
              line-height: 150px
              text-align: center
          .userinfo-box
            flex 1
            margin-left 40px
            .username
              display inline-block
              margin-bottom 10px
              font-size 24px
              .level
                display inline-block
                padding 3px 8px
                margin-left 5px
                border 1px solid #ff383a
                border-radius 15px
                color: #ff383a
                font-size 16px
                font-weight 500
            .info-line
              font-size 14px
              line-height 30px
              color: #888888
      .col-right
        flex 0 0 300px
        padding 20px
        label
          display block
          margin-bottom 15px
          color: #409eff
          font-size 18px
        .reward-wrapper
          margin-bottom 50px
          text-align center
          .reward-num
            margin-bottom 10px
            color: #f56c6c
            font-size: 20px
          .attend-tip
            color: #888888
            font-size 12px
        .counts-wrapper
          text-align center
          .task-counts
            display flex
            .block
              flex: 1
              border-right 1px solid rgba(7, 17, 27, 0.1)
              &:last-child
                border-right none
              .num
                margin-bottom 10px
                font-weight 700
                font-size 16px
              .label
                font-size 14px
                font-weight 350
                color #888888
      .el-tabs__item:focus.is-active.is-focus:not(:active)
        -webkit-box-shadow: none
        box-shadow: none
    .wrapper
      padding 30px 50px
      margin-bottom 20px
      border 1px solid rgba(7, 17, 27, 0.1)
      background-color #fff
      .header
        margin-bottom 20px
        font-size: 18px
    .user-activity-wrapper
      height 250px
      .user-activity-num-wrapper
        display flex
        .block
          flex 1
          margin 20px 0
          padding 10px 0
          border-right 1px solid rgba(7, 17, 27, 0.1)
          text-align center
          .num
            margin-bottom 5px
            font-weight 400
            font-size 20px
            color #495057
          .label
            color #adb5bd
          &:last-child
            border none
      .user-activity-header
        margin: 5px 0 15px 0
        font-size 18px
      .user-activity-content
        position relative
        .rect-wrapper
          position absolute
          .rect
            display inline-block
            width 10px
            height 10px
            background-color rgb(235, 237, 240)
            &.active-1
              background-color #b9e7e7
            &.active-2
              background-color #77c7be
            &.active-3
              background-color #75adb4
            &.active-4
              background-color #597f84

    .pay-history-wrapper
      .pay-history-content
        .el-table
          .el-icon-circle-check
            color: #1ecd97
          .el-icon-circle-close
            color #f56c6c
        .show-all-btn
          padding-top 25px
          width: 100%
          text-align center
          color: #409eff
          cursor: pointer
</style>
