<template>
  <div class="rank">
    <el-row>
      <el-col :span="12">
        <el-card class="rank-card">
          <div slot="header" class="clearfix">
            <span class="title">众包工人排行榜</span>
            <el-select size="small" v-model="workerRankType" placeholder="请选择" style="float: right;"
                       class="rank-type-select">
              <el-option
                v-for="item in workerOptions"
                :key="item.value"
                :value="item">
              </el-option>
            </el-select>
          </div>
          <el-table
            class="rank-table"
            :data="workerRank"
            fit
            style="width: 100%">
            <el-table-column
              type="index">
            </el-table-column>
            <el-table-column
              prop="username"
              label="用户"
              width="145">
              <template slot-scope="scope">
                <img class="avatar" width="30px" height="30px" :src="getAvatar(scope.row.id, scope.row.avatar)">
                <span class="username">{{ scope.row.username }}</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="points"
              label="积分">
              <template slot-scope="scope">
                <stop-circle-icon class="icon"></stop-circle-icon>
                <span class="num">{{ scope.row.points }}</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="exp"
              label="经验">
              <template slot-scope="scope">
                <award-icon class="icon"></award-icon>
                <span class="num">{{ scope.row.exp }}</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="accuracyRate"
              label="准确度">
              <template slot-scope="scope">
                <check-icon class="icon"></check-icon>
                <span class="num">{{(scope.row.accuracyRate*100).toFixed(1) }}%</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="rank-card">
          <div slot="header" class="clearfix">
            <span class="title">众包发起者排行榜</span>
            <el-select size="small" v-model="publisherRankType" placeholder="请选择" style="float: right;"
                       class="rank-type-select">
              <el-option
                v-for="item in publisherOptions"
                :key="item.value"
                :value="item">
              </el-option>
            </el-select>
          </div>
          <el-table
            class="rank-table"
            :data="publisherRank"
            style="width: 100%">
            <el-table-column
              type="index">
            </el-table-column>
            <el-table-column
              prop="username"
              label="用户"
              width="145">
              <template slot-scope="scope">
                <img class="avatar" width="30px" height="30px" :src="getAvatar(scope.row.id, scope.row.avatar)">
                <span class="username">{{ scope.row.username }}</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="points"
              label="积分">
              <template slot-scope="scope">
                <stop-circle-icon class="icon"></stop-circle-icon>
                <span class="num">{{ scope.row.points }}</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="exp"
              label="经验">
              <template slot-scope="scope">
                <award-icon class="icon"></award-icon>
                <span class="num">{{ scope.row.exp }}</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="taskNum"
              label="任务数">
              <template slot-scope="scope">
                <box-icon class="icon"></box-icon>
                <span class="num">{{scope.row.taskNum}}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import {AwardIcon, StopCircleIcon, CheckIcon, ThumbsUpIcon, BoxIcon} from 'vue-feather-icons'

  export default {
    name: 'rank',
    components: {
      AwardIcon, StopCircleIcon, CheckIcon, ThumbsUpIcon, BoxIcon
    },
    data () {
      return {
        workerRankType: '积分',
        workerOptions: ['积分', '经验', '准确度'],
        publisherRankType: '积分',
        publisherOptions: ['积分', '经验', '任务数'],
        workerRank: [],
        publisherRank: []
      }
    },
    watch: {
      'workerRankType': function () {
        const userType = 'worker'
        switch (this.workerRankType) {
          case '积分':
            this.getRank(userType, 'Point')
            break
          case '经验':
            this.getRank(userType, 'Exp')
            break
          case '准确度':
            this.getRank(userType, 'AccuracyRate')
            break
        }
      },
      'publisherRankType': function () {
        const userType = 'pub'
        switch (this.publisherRankType) {
          case '积分':
            this.getRank(userType, 'Point')
            break
          case '经验':
            this.getRank(userType, 'Exp')
            break
          case '任务数':
            this.getRank(userType, 'Task')
            break
        }
      }
    },
    methods: {
      getAvatar: function (id, avatar) {
        if (!id || avatar === 'default_avatar.png') {
          return '/static/image/default_avatar.png'
        } else {
          return '/show/avatar/' + id + '/' + avatar
        }
      },
      getRank: function (userType, rankType) {
        let userId = 0
        if ((userType === 'worker' && this.$store.getters.userType === 0) ||
          (userType === 'pub' && this.$store.getters.userType === 1)) {
          userId = this.$store.getters.id
        }

        this.$ajax.get('/rank/' + userType + rankType, {
          params: {
            userId: userId
          }
        }).then((res) => {
          const result = res.data
          if (userType === 'worker') {
            this.workerRank = result.data.rankList
          } else if (userType === 'pub') {
            this.publisherRank = result.data.rankList
          }
        })
      }
    },
    mounted () {
      this.getRank('worker', 'Point')
      this.getRank('pub', 'Point')
    }
  }
</script>

<style lang="stylus">
  .rank
    margin-top 60px
    padding 0 40px 60px 40px
    .el-col-12
      padding 30px
      .el-card__header
        height 60px
      .el-card__body
        padding-top 0
      .title
        display inline-block
        font-size 20px
        color: #ff4d51
        font-weight: 700
      .rank-type-select
        width 100px
        transform translateY(-5px)
      .rank-table
        .avatar
          display inline-block
          vertical-align top
          border-radius 50%
        .username
          display inline-block
          margin: 3px 0 0 10px
        .icon
          display inline-block
          vertical-align top
          padding-top 2px
          width: 15px
        .num
          display: inline-block
          vertical-align top
          padding-top 4px
</style>
