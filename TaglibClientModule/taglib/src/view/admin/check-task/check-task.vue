<template>
  <div class="check-task">
    <el-card class="submitted-tasks">
      <div slot="header">
        <span>待审核的发起任务</span>
      </div>
      <el-table
        :data="taskList"
        style="width: 100%"
        max-height="510">
        <el-table-column label="ID"
                         prop="id">
        </el-table-column>
        <el-table-column label="标题"
                         prop="title"
                         width="180">
        </el-table-column>
        <el-table-column label="任务量"
                         prop="picNum">
        </el-table-column>
        <el-table-column label="价格"
                         prop="price">
        </el-table-column>
        <el-table-column label="发布时间"
                         prop="startDate"
                         width="150px">
        </el-table-column>
        <el-table-column label="结束时间"
                         prop="endDate"
                         width="150px">
        </el-table-column>
        <el-table-column label="操作" width="200px">
          <template slot-scope="scope">
            <el-button
              size="mini"
              @click="showDialog(scope.$index)">审核
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-dialog
        title="审核任务"
        :visible.sync="checkTaskdialogVisible"
        width="50%"
        top="60px">
        <div class="detail-wrapper" v-if="totalItemNum > 0">
          <el-row>
            <el-col :span="12">
              <div><label>任务名称</label><span class="text">{{ task.title }}</span></div>
              <div><label>开始日期</label><span class="text">{{ task.startDate }}</span></div>
              <div><label>图片数量</label><span class="text">{{ task.picNum }}张</span></div>
            </el-col>
            <el-col :span="12">
              <div><label>任务类型</label><span class="text">{{ taskType(task.taskType) }}</span></div>
              <div><label>结束日期</label><span class="text">{{ task.endDate }}</span></div>
              <div><label>任务奖励</label><span class="text">{{ task.price }}T币</span></div>
            </el-col>
          </el-row>
          <el-row>
            <div><label>任务话题</label>
              <div class="topic-wrapper" v-for="topic in task.topics" :key="topic">
                <el-tag size="mini">{{ topic }}</el-tag>
              </div>
            </div>
            <div><label>任务描述</label><span class="text">{{ task.description }}</span></div>
          </el-row>
          <el-row>
            <el-carousel>
              <el-carousel-item v-for="(item,index) in images.length" :key="item">
                <img :src="'/show/'+ task.id + '/' + images[index]" width="100%">
              </el-carousel-item>
            </el-carousel>
          </el-row>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="check(true)" size="medium">通 过</el-button>
          <el-button type="danger" @click="check(false)" size="medium">不 通 过</el-button>
        </span>
      </el-dialog>
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
    name: 'check-task',
    components: {Pagination},
    data () {
      return {
        task: {
          title: '',
          picNum: '',
          taskType: 1,
          price: 0,
          description: '',
          startDate: '',
          endDate: '',
          topics: []
        },
        page: 1,
        pageSize: 8,
        totalItemNum: 0,
        taskList: [],
        images: [],
        checkTaskdialogVisible: false
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
      taskType: function (t) {
        let type = ['分类', '标框', '区域']
        return type[t] + '标注'
      },
      showDialog: function (index) {
        this.checkTaskdialogVisible = true
        this.task = this.taskList[index]
        this.$ajax.get('/tasks/' + this.task.id).then((res) => {
          let result = res.data
          if (result.code === 0) {
            this.images = result.data.images
          }
        })
      },
      deleteTask: function (id) {
        for (let i = 0; i < this.taskList.length; i++) {
          if (this.taskList[i].id === id) {
            this.taskList.splice(i, 1)
            this.totalItemNum--
            break
          }
        }
      },
      check: function (checkResult) {
        this.$ajax.post('/admin/checkTask/' + this.task.id, this.$qs.stringify({
          checkResult: checkResult
        })).then((res) => {
          let result = res.data
          if (result.code === 0) {
            this.$message.success('审核成功')
            this.deleteTask(this.task.id)
          } else {
            this.$message.error('审核失败')
          }
        }).catch(() => {
          this.$message.error('审核失败')
        })
        this.checkTaskdialogVisible = false
      },
      changePage: function (val) {
        this.page = val
      },
      getSubmittedTasks: function () {
        this.$ajax.get('/tasks/list', {
          params: {
            userId: 0,
            state: 'SUBMITTED',
            size: this.pageSize,
            page: this.page
          }
        }).then((res) => {
          let result = res.data
          let page = result.data
          this.taskList = page.data
          this.totalItemNum = page.totalItemNum
        })
      }
    }
  }
</script>

<style lang="stylus">
  .check-task
    position: relative
    .submitted-tasks
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
