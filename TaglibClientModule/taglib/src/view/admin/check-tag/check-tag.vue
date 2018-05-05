<template>
  <div class="check-tag">
    <el-card>
      <div slot="header">
        <span>待审核的工人标注</span>
        <div class="check-btn-group">
          <el-button size="mini" type="plain" @click="check(1)">批量通过</el-button>
          <el-button size="mini" type="danger">批量不通过</el-button>
        </div>
      </div>
      <el-table
        :data="taskList"
        @selection-change="handleSelectionChange"
        max-height="510">
        <el-table-column
          type="selection"
          width="55">
        </el-table-column>
        <el-table-column
          label="工人任务ID"
          prop="id">
        </el-table-column>
        <el-table-column
          label="任务名称"
          prop="title">
        </el-table-column>
        <el-table-column
          label="任务量"
          prop="picNum">
        </el-table-column>
        <el-table-column
          label="提交时间"
          prop="endDate">
        </el-table-column>
        <el-table-column label="奖励"
                         prop="price">
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button class="detail-btn" type="plain" icon="el-icon-back" circle
                       @click="showTagDetailDialog(scope.$index)"></el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <div class="pagination-wrapper" v-show="totalItemNum > 0">
      <pagination class="my-pagination" :page="page" :totalItemNum="totalItemNum" :pageSize="pageSize"
                  @changePage="changePage"></pagination>
    </div>
  </div>
</template>

<script type="text/ecmascript-6">
  import Pagination from '../../../components/pagination/pagination'

  export default {
    name: 'check-tag',
    components: {Pagination},
    data () {
      return {
        page: 1,
        totalItemNum: 0,
        pageSize: 9,
        taskList: [],
        chosenTask: []
      }
    },
    mounted () {
      this.getSubmittedTasks()
    },
    watch: {
      'page': function () {
        this.getSubmittedTasks()
      }
    },
    methods: {
      showTagDetailDialog: function (index) {
        this.index = index
        this.$ajax.get('/user/' + this.taskList[index].id).then((res) => {
          let result = res.data
          if (result.code === 0) {
            localStorage.setItem('taskWorker', JSON.stringify(result.data))
            localStorage.setItem('boardState', 'check')
            this.$router.push('/board')
          }
        })
      },
      changePage: function (page) {
        this.page = page
      },
      handleSelectionChange: function (val) {
        this.chosenTask = val
      },
      getSubmittedTasks: function () {
        this.$ajax.get('/user/0/tasks', {
          params: {
            size: this.pageSize,
            page: this.page,
            state: 'SUBMITTED'
          }
        }).then((res) => {
          let result = res.data
          let page = result.data
          this.taskList = page.data
          this.totalItemNum = page.totalItemNum
        })
      },
      deleteTask: function (id) {
        for (let i = 0; i < this.taskList.length; i++) {
          if (this.taskList[i].id === id) {
            this.taskList.splice(i, 1)
            break
          }
        }
        this.totalItemNum--
      },
      check: function (rate) {
        for (let j = 0; j < this.chosenTask.length; j++) {
          this.$ajax.post('/admin/checkTag/' + this.chosenTask[j].id, this.$qs.stringify({
            checkResult: rate
          })).then((res) => {
            let result = res.data
            if (result.code === 0) {
              this.$message.success('审核成功')
              this.deleteTask(this.chosenTask[j].id)
              this.chosenTask.splice(j, 1)
            } else {
              this.$message.error('审核失败')
            }
          }).catch(() => {
            this.$message.error('审核失败')
          })
        }
      }
    }
  }
</script>

<style lang="stylus">
  .check-tag
    margin 25px 65px
    .check-btn-group
      float: right
      transform translateY(-5px)
    .detail-btn
      border-radius 50%
      padding 6px
      transform rotate(180deg)
    .my-pagination
      margin-top 30px
      text-align center
</style>
