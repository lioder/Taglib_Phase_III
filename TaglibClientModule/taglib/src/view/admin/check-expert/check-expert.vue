<template>
  <div class="check-expert">
    <el-card class="submitted-application">
      <div slot="header">
        <span>待审核的专家申请</span>
      </div>
      <el-table
        :data="expertList"
        style="width: 100%"
        max-height="510">
        <el-table-column label="用户ID"
                         prop="id">
        </el-table-column>
        <el-table-column label="用户名"
                         prop="username"
                         width="180">
        </el-table-column>
        <el-table-column label="等级">
          <template slot-scope="scope">
            <span>Lv.{{getLevel(scope.row.level)}}</span>
          </template>
        </el-table-column>
        <el-table-column label="经验"
                         prop="exp">
        </el-table-column>
        <el-table-column label="积分"
                         prop="points"
                         width="150px">
        </el-table-column>
        <el-table-column label="准确率"
                         width="150px">
          <template slot-scope="scope">
            <span>{{Math.round(scope.row.accuracyRate * 100)}}%</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200px">
          <template slot-scope="scope">
            <el-button
              size="mini"
              @click="check(scope.row.id, true)">通过
            </el-button>
            <el-button
              size="mini"
              @click="check(scope.row.id, false)">不通过
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!--<el-dialog-->
      <!--title="审核任务"-->
      <!--:visible.sync="checkTaskdialogVisible"-->
      <!--width="50%"-->
      <!--top="60px">-->
      <!--<div class="detail-wrapper" v-if="totalItemNum > 0">-->
      <!--<el-row>-->
      <!--<el-col :span="12">-->
      <!--<div><label>任务名称</label><span class="text">{{ task.title }}</span></div>-->
      <!--<div><label>开始日期</label><span class="text">{{ task.startDate }}</span></div>-->
      <!--<div><label>图片数量</label><span class="text">{{ task.picNum }}张</span></div>-->
      <!--</el-col>-->
      <!--<el-col :span="12">-->
      <!--<div><label>任务类型</label><span class="text">{{ taskType(task.taskType) }}</span></div>-->
      <!--<div><label>结束日期</label><span class="text">{{ task.endDate }}</span></div>-->
      <!--<div><label>任务奖励</label><span class="text">{{ task.price }}T币</span></div>-->
      <!--</el-col>-->
      <!--</el-row>-->
      <!--<el-row>-->
      <!--<div><label>任务话题</label>-->
      <!--<div class="topic-wrapper" v-for="topic in task.topics" :key="topic">-->
      <!--<el-tag size="mini">{{ topic }}</el-tag>-->
      <!--</div>-->
      <!--</div>-->
      <!--<div><label>任务描述</label><span class="text">{{ task.description }}</span></div>-->
      <!--</el-row>-->
      <!--<el-row>-->
      <!--<el-carousel>-->
      <!--<el-carousel-item v-for="(item,index) in images.length" :key="item">-->
      <!--<img :src="'/show/'+ task.id + '/' + images[index]" width="100%">-->
      <!--</el-carousel-item>-->
      <!--</el-carousel>-->
      <!--</el-row>-->
      <!--</div>-->
      <!--<span slot="footer" class="dialog-footer">-->
      <!--<el-button @click="check(true)" size="medium">通 过</el-button>-->
      <!--<el-button type="danger" @click="check(false)" size="medium">不 通 过</el-button>-->
      <!--</span>-->
      <!--</el-dialog>-->
    </el-card>
    <!--<div class="pagination-wrapper" v-show="totalItemNum > 0">-->
    <!--<pagination class="my-pagination" :page="page" :totalItemNum="totalItemNum" :pageSize="pageSize"-->
    <!--@changePage="changePage"></pagination>-->
    <!--</div>-->
  </div>
</template>

<script type="text/ecmascript-6">
  export default {
    name: 'check-expert',
    data: function () {
      return {
        expertList: [{
          id: 0,
          username: 'opop',
          level: 'LEVEL_ONE',
          exp: 300,
          points: 900,
          accuracyRate: 0.9
        }]
      }
    },
    methods: {
      getLevel: function (level) {
        switch (level) {
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
      check: function (userId, result) {
        this.$ajax.get('/admin/check/pro/' + userId, {
          params: {
            checkResult: result
          }
        }).then((res) => {
          let result = res.data
          if (result.code === 0) {
            for (let i = 0; i < this.expertList.length; i++) {
              if (userId === this.expertList[i].id) {
                this.expertList.splice(i, 1)
              }
            }
          }
        }).catch(() => {
          this.$message.error('网络错误')
        })
      }
    },
    mounted: function () {
      this.$ajax.get('/admin/check/pro').then((res) => {
        let result = res.data
        if (result.code === 0) {
          this.expertList = result.data
        }
      }).catch(() => {
        this.$message.error('网络错误')
      })
    }
  }
</script>

<style lang="stylus">
  .check-expert
    position: relative
    .submitted-application
      width: cal(100vw - 240px)
      margin 25px 65px
      .my-pagination
        margin-top 30px
        text-align center
      .detail-wrapper
        margin 0 60px
        .el-row
          margin-bottom 10px
        label
          margin-right 20px
          font-weight 500
          color: #99a9bf
        .text
          font-weight 500
          color: #606266
        .topic-wrapper
          display inline-block
          margin-right 20px

</style>
