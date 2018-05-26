<template>
  <div class="publish">
    <div class="article">
      <div class="title">发布标注任务</div>
      <el-form v-model="task" label-width="110px" label-position="left" size="medium" ref="taskForm">
        <el-form-item label="任务名称" prop="title">
          <el-input v-model="task.title" placeholder="任务名称"></el-input>
        </el-form-item>
        <el-form-item label="任务描述">
          <el-input
            type="textarea"
            :autosize="{ minRows: 2}"
            placeholder="任务描述"
            v-model="task.description">
          </el-input>
        </el-form-item>
        <el-form-item label="图片主题">
          <el-tag
            v-if="task.topics.length > 0"
            :key="index"
            v-for="(topic, index) in task.topics"
            closable
            :disable-transitions="false"
            @close="handleClose(topic)">
            {{ topic }}
          </el-tag>
          <el-autocomplete
            class="input-new-tag"
            v-if="inputVisible"
            v-model="inputValue"
            size="small"
            :fetch-suggestions="querySearch"
            @keyup.enter.native="handleInputConfirm"
            @select="handleInputConfirm">
          </el-autocomplete>
          <el-button v-else class="button-new-tag" size="small" @click="showInput">+ New Tag</el-button>
        </el-form-item>
        <el-form-item label="截止时间">
          <el-date-picker
            v-model="task.endDate"
            type="datetime"
            format="yyyy-MM-dd hh:mm"
            value-format="yyyy-MM-dd hh:mm"
            placeholder="选择截止时间"
            :picker-options="endDateOpt">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="标注类型" prop="taskType">
          <el-radio-group v-model="task.taskType">
            <el-radio :label="0">分类标注</el-radio>
            <el-radio :label="1">标框标注</el-radio>
            <el-radio :label="2">区域标注</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="分类标注标签" v-show="task.taskType === 0" prop="labels">
          <el-form size="small" class="label-form" v-model="task.labels">
            <el-form-item :label="'标签'+(index+1)" v-for="(label,index) in task.labels" :key="index">
              <el-input v-model="task.labels[index]"></el-input>
              <el-button type="danger" class="delete-btn" @click="deleteLabel(index)">删除</el-button>
            </el-form-item>
            <el-button plain @click="addLabel">添加标签</el-button>
          </el-form>
        </el-form-item>
        <el-form-item label="上传图片" prop="images">
          <el-upload
            ref="uploadFile"
            drag
            action="/upload/task"
            :data="uploadData"
            :auto-upload="false"
            multiple
            :on-change="addImage"
            :before-remove="removeImage"
            accept="application/x-zip-compressed,image/jpg,image/png"
            :file-list="filelist">
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <div class="el-upload__tip" slot="tip">只能上传jpg、png和zip文件，且不超过20Mb</div>
          </el-upload>
        </el-form-item>
        <el-form-item label="标注人数/图" class="num-per-pic">
          <el-input-number v-model="task.numPerPic" :min="1"></el-input-number>
        </el-form-item>
        <el-form-item label="任务总价" class="price">
          <el-input-number v-model="task.price"
                           :min="Math.round(task.images.length * task.numPerPic)"></el-input-number>
          <span>一张图最少1T币哦！</span>
        </el-form-item>
      </el-form>
      <div class="btn-group">
        <el-button type="primary" class="publish-btn" @click="publish">发布</el-button>
        <el-button type="plain" class="cancel-btn" @click="cancel">取消</el-button>
      </div>
    </div>
  </div>
</template>

<script type="text/ecmascript-6">
  import '../../common/js/date'

  export default {
    name: 'publish',
    data () {
      return {
        uploadData: {
          id: 0
        },
        endDateOpt: this.disabledDate(),
        filelist: [],
        task: {
          userId: this.$store.getters.id,
          title: '',
          description: '',
          taskType: 0,
          endDate: '',
          price: 0,
          numPerPic: 0,
          labels: [],
          topics: [],
          startDate: (new Date()).Format('yyyy-MM-dd hh:mm'),
          images: []
        },
        inputVisible: false,
        inputValue: ''
      }
    },
    watch: {
      'task.numPerPic': function () {
        this.refreshPrice()
      },
      'task.images': function () {
        this.refreshPrice()
      }
    },
    methods: {
      refreshPrice: function () {
        this.task.price = this.task.images.length * this.task.numPerPic
      },
      disabledDate: function () {
        return {
          disabledDate (time) {
            return time.getTime() < Date.now() // 开始时间不选时，结束时间最大值小于等于当天
          }
        }
      },
      addLabel: function () {
        this.task.labels.push('')
      },
      deleteLabel: function (index) {
        this.task.labels.splice(index, 1)
      },
      addImage: function (file, filelist) {
        if (!this.checkSize(file)) {
          this.removeImage(file)
        }
        let extension = file.name.substr(file.name.lastIndexOf('.') + 1)
        if (!(extension === 'jpg' || extension === 'png' || extension === 'zip')) {
          this.$message.error('文件类型只支持jpg、png和zip哦')
          this.removeImage(file)
        }
        let fileList = this.$refs.uploadFile.uploadFiles
        let count = 0
        for (let i = 0; i < fileList.length; i++) {
          if (fileList[i].name === file.name) {
            if (count === 1) {
              this.$message.error('请不要上传同名图片！')
              this.$refs.uploadFile.abort(file)
              fileList.splice(fileList.indexOf(file), 1)
              return
            } else {
              count++
            }
          }
        }
        this.task.images.push(file.name)
      },
      removeImage: function (file) {
        let filelist = this.$refs.uploadFile.uploadFiles
        for (let i = 0; i < filelist.length; i++) {
          if (filelist[i].name === file.name) {
            filelist.splice(i, 1)
            this.task.images.splice(i, 1)
            return
          }
        }
      },
      checkSize: function (file) {
        const isLt2M = file.size / 1024 / 1024 < 20
        if (!isLt2M) {
          this.$message.error('上传文件大小不能超过 20 MB!')
        }
        return isLt2M
      },
      validate: function () {
        let task = this.task
        if (task.title === '' || task.description === '' ||
          (task.taskType === 0 && task.labels.length === 0) ||
          task.endDate === '' || task.price <= 0 || task.images.length === 0) {
          return false
        }
        return true
      },
      publish: function () {
        if (this.validate()) {
          this.$ajax.post('/tasks/new', this.task).then((res) => {
            if (res.data.code === 0) {
              this.$message.success('发布成功，已提交审核')
              this.uploadData.id = res.data.data.id
              this.$refs.uploadFile.submit()
              this.$router.push('/home')
            } else {
              this.$message.error('发布失败')
            }
          }).catch(() => {
            this.$message.error('发布失败')
          })
        } else {
          this.$message.error('信息填写不完整')
        }
      },
      cancel: function () {
        this.$confirm('确定放弃本次发布任务吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$router.push('/home')
        }).catch(() => {

        })
      },
      showInput: function () {
        this.inputVisible = true
      },
      handleInputConfirm: function () {
        this.inputVisible = false
        this.inputValue = String.trim(this.inputValue)
        if (this.inputValue !== '') {
          this.task.topics.push(this.inputValue)
        }
        this.inputValue = ''
      },
      querySearch (queryString, cb) {
        let topics = [{value: '动物'}, {value: '植物'}, {value: '车辆'}, {value: '船舶'}, {value: '运动'}, {value: '美食'}, {value: 'IT'}, {value: '机械'}, {value: '医学'}, {value: '人类'}]
        var results = queryString ? topics.filter(this.createFilter(queryString)) : topics
        // 调用 callback 返回建议列表的数据
        cb(results)
      },
      createFilter (queryString) {
        return (topic) => {
          return (topic.value.toLowerCase().indexOf(queryString.toLowerCase()) === 0)
        }
      },
      handleClose (topic) {
        this.task.topics.splice(this.task.topics.indexOf(topic), 1)
      }
    }
  }
</script>

<style lang="stylus">
  .publish
    padding 70px 0 100px 0
    .article
      position: relative;
      margin 0 auto
      width 800px
      padding 50px 100px 100px 100px
      background: #fff
      .el-input
        width 300px
      textarea
        width 600px
      .title
        margin-bottom 50px
        padding-left 15px
        border-left 3px solid #409eff
        color: #000
        font-size 20px
      .label-form
        .el-form-item__label
          line-height 30px
        .delete-btn
          margin-left 10px
          height 30px
      .btn-group
        position: absolute
        bottom 50px
        left 200px
        .publish-btn
          margin-right 20px
      .el-tag + .el-tag
        margin-left: 10px
      .button-new-tag
        height: 32px
        line-height: 30px
        padding-top: 0
        padding-bottom: 0
      .input-new-tag
        .el-input
          width: 90px
          vertical-align: bottom
      .price, .num-per-pic
        .el-input
          width inherit
</style>
