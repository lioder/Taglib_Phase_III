<template>
  <div class='my-task' v-loading.fullscreen.lock="fullscreenLoading">
    <el-tabs class="state-tab" v-model="state">
      <el-tab-pane name="PROCESSING">
        <span slot="label"><i class="el-icon-time"></i> 进行中的任务<span v-show="state === 'PROCESSING'"> ({{ taskInfos.length }})</span></span>
      </el-tab-pane>
      <el-tab-pane name="SUBMITTED">
        <span slot="label"><i class="iconfont">&#xe642;</i> 已提交的任务<span v-show="state === 'SUBMITTED'"> ({{ taskInfos.length }})</span></span>
      </el-tab-pane>
      <el-tab-pane name="PASS">
        <span slot="label"><i class="el-icon-circle-check"></i> {{this.$store.getters.userType === 0 ? '通过的任务':'等待专家的任务'}}<span
          v-show="state === 'PASS'"> ({{ taskInfos.length }})</span></span>
      </el-tab-pane>
      <el-tab-pane name="DONE" v-if="this.$store.getters.userType === 1">
        <span slot="label"><i class="el-icon-circle-check"></i> 已完成的任务<span v-show="state === 'DONE'"> ({{ taskInfos.length }})</span></span>
      </el-tab-pane>
      <el-tab-pane name="REJECT" v-if="this.$store.getters.userType === 0">
        <span slot="label"><i class="el-icon-circle-close"></i> 失败的任务<span v-show="state === 'REJECT'"> ({{ taskInfos.length }})</span></span>
      </el-tab-pane>
      <el-tab-pane name="OVERTIME">
        <span slot="label"><i class="el-icon-circle-close"></i> 超时的任务<span v-show="state === 'OVERTIME'"> ({{ taskInfos.length }})</span></span>
      </el-tab-pane>
      <div class="task-out-wrapper">
        <div class="task-wrapper">
          <div class="tasks">
            <div class="task-card-wrapper" v-for="(item, index) in taskInfos" :key="index">
              <task-card :task-info="item" :state="state"></task-card>
            </div>
            <div class="task-card-wrapper" v-for="(item,index) in 3" :key="index-20">
              <div class="empty"></div>
            </div>
          </div>
        </div>
      </div>
    </el-tabs>
    <div v-if="totalItemNum === 0"><i class="el-icon-warning"></i>没有在该状态的任务</div>
    <pagination v-if="totalItemNum > 0" :page="page" :totalItemNum="totalItemNum" :pageSize="9"
                @changePage="changePage"></pagination>
  </div>
</template>

<script type="text/ecmascript-6">
  import TaskCard from '../../components/task-card/task-card'
  import Pagination from '../../components/pagination/pagination'

  export default {
    components: {
      Pagination,
      TaskCard
    },
    name: 'my-task',
    data () {
      return {
        page: 1,
        totalItemNum: 0,
        state: 'PROCESSING',
        taskInfos: [],
        fullscreenLoading: false
      }
    },
    watch: {
      'state': function () {
        this.taskInfos = []
        this.$nextTick(() => {
          this.getTaskInfos()
        })
      }
    },
    mounted () {
      this.getTaskInfos()
    },
    methods: {
      changePage: function (val) {
        this.page = val
        this.getTaskInfos()
      },
      getTaskInfos: function () {
        this.fullscreenLoading = true
        if (this.$store.getters.userType === 1) {
          this.$ajax.get('/tasks/list', {
            params: {
              userId: this.$store.getters.id,
              state: this.state,
              size: 9,
              page: this.page
            }
          }).then((res) => {
            let result = res.data
            let page = result.data
            this.taskInfos = page.data
            this.totalItemNum = page.totalItemNum
          }).finally(() => {
            setTimeout(() => {
              this.fullscreenLoading = false
            }, 500)
          })
        } else {
          this.$ajax.get('/user/' + this.$store.getters.id + '/tasks', {
            params: {
              state: this.state,
              size: 9,
              page: this.page
            }
          }).then((res) => {
            let result = res.data
            let page = result.data
            this.taskInfos = page.data
            this.totalItemNum = page.totalItemNum
          }).finally(() => {
            setTimeout(() => {
              this.fullscreenLoading = false
            }, 500)
          })
        }
      }
    }
  }
</script>

<style lang="stylus">
  .my-task
    margin 70px 100px 0 100px
    padding 50px 100px
    background-color #fff

  .el-tabs__item:focus.is-active.is-focus:not(:active)
    box-shadow none
    border-radius 0

  .el-tabs__item
    font-size 16px

  .task-out-wrapper
    .task-wrapper
      .tasks
        display: flex
        flex-wrap: wrap
        justify-content: space-between
        align-content flex-start
        align-items flex-start
        padding-top 20px
        @media (max-width 1400px)
          width 700px
        @media (max-width 1089px)
          width 300px
        .task-card-wrapper
          flex 0 0 300px
          margin 0 12px
          &:nth-child(3n)
            margin-right 0
          &.empty
            width: 0
            height 0
            visibility hidden
</style>
