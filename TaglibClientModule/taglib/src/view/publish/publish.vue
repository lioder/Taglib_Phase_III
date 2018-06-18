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
          <el-select v-model="task.topics" multiple>
            <el-option
              v-for="topic in topics"
              :key="topic.value"
              :label="topic.value"
              :value="topic.value">
            </el-option>
          </el-select>
          <!--<el-button v-else class="button-new-tag" size="small" @click="showInput">+ New Tag</el-button>-->
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
            <el-radio :label="0" v-show="false">分类标注</el-radio>
            <el-radio :label="1">标框标注</el-radio>
            <el-radio :label="2" v-show="false">区域标注</el-radio>
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
        <el-form-item label="候选标签" size="small" v-model="task.options" v-show="task.taskType === 1">
          <el-tag
            v-if="task.options.length > 0"
            :key="index"
            v-for="(option, index) in task.options"
            closable
            :disable-transitions="false"
            @close="handleOptionClose(index)">
            {{ option }}
          </el-tag>
          <el-input
            class="input-new-tag"
            v-model="optionValue"
            size="small"
            @keyup.enter.native="handleOptionConfirm"></el-input>
          <el-button size="small" plain @click="handleOptionConfirm">添加</el-button>
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
            :before-upload="checkPrice"
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
        <el-form-item label="任务单价(/图∙人)" class="price">
          <el-input-number v-model="task.price"
                           :min="1"></el-input-number>
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
  import JsZip from 'jszip'

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
          taskType: 1,
          endDate: '',
          price: 0,
          numPerPic: 0,
          labels: [],
          topics: [],
          startDate: (new Date()).Format('yyyy-MM-dd hh:mm'),
          images: [],
          options: []
        },
        inputVisible: false,
        inputValue: '',
        optionValue: '',
        topics: [{value: '动物'}, {value: '植物'}, {value: '车辆'}, {value: '船舶'}, {value: '运动'}, {value: '美食'}, {value: 'IT'}, {value: '机械'}, {value: '医学'}, {value: '人类'}],
        picSum: 0,
        finishPublish: false
      }
    },
    methods: {
      checkPrice: async function (file) {
        // 对压缩包统计图片个数
        let extension = file.name.substr(file.name.lastIndexOf('.') + 1)
        let filteredName = []
        if (extension === 'zip') {
          await JsZip.loadAsync(file).then(function (zip) {
            let filenames = Object.keys(zip.files)
            filenames.forEach(filename => {
              if (filename.indexOf('/') === -1) {
                let extension2 = filename.substr(filename.lastIndexOf('.') + 1)
                if (extension2 === 'jpg' || extension2 === 'png') {
                  filteredName.push(filename)
                }
              }
            })
          })
        }
        if (extension === 'zip') {
          this.picSum += filteredName.length
        } else {
          this.picSum++
        }
        if (this.uploadData.id === 0) {
          return Promise.reject(new Error('not a error'))
        }
      },
      handleOptionClose: function (index) {
        this.task.options.splice(index, 1)
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
        let fileList = this.$refs.uploadFile.uploadFiles

        let extension = file.name.substr(file.name.lastIndexOf('.') + 1)
        let wrong = false
        if (!(extension === 'jpg' || extension === 'png' || extension === 'zip')) {
          this.$message.error('文件类型只支持jpg、png和zip哦')
          this.$refs.uploadFile.abort(file)
          this.removeImage(file)
          console.log(this.task.images.length)
          fileList.splice(fileList.indexOf(file), 1)
          wrong = true
        }
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
        if (!wrong) {
          this.task.images.push(file.name)
        }
      },
      removeImage: function (file) {
        if (this.finishPublish) {
          return false
        }
        let filelist = this.$refs.uploadFile.uploadFiles
        for (let i = 0; i < filelist.length; i++) {
          if (filelist[i].name === file.name) {
            console.log(i)
            this.task.images.splice(i, 1)
            let images = []
            for (let j = 0; j < this.task.images.length; j++) {
              console.log(filelist)
              console.log(j)
              console.log(i)
              console.log(j === i)
              if (j !== i) {
                console.log('here')
                images.push(this.task.images[j])
              }
            }
            console.log(this.task.images)
            // this.task.images.splice(0, 1)
            // let a = [0, 1]
            // a.splice(0, 1)
            // console.log(a)
            break
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
          task.endDate === '' || task.price <= 0 || task.images.length === 0 || task.options.length === 0) {
          return false
        }
        return true
      },
      publish: function () {
        if (this.validate()) {
          // 先假上传一遍
          this.finishPublish = true
          this.$refs.uploadFile.submit()
          this.$ajax.get('/user/info/' + this.$store.getters.id).then((res) => {
            let result = res.data
            if (result.code === 0) {
              let user = result.data
              // 判断钱够不够
              if (this.picSum * this.task.price * this.numPerPic > user.points) {
                this.$message.error('积分不足，请充值')
                this.finishPublish = false
                return
              }
              // 够了就真上传
              let totalPrice = this.task.price * this.task.numPerPic * this.task.images.length
              this.task.price = totalPrice
              this.$ajax.post('/tasks/new', this.task).then((res) => {
                if (res.data.code === 0) {
                  this.$message.success('提交成功，请等待审核')
                  this.uploadData.id = res.data.data.id
                  // console.log(temp)
                  // console.log(this.$refs.uploadFile)
                  this.$refs.uploadFile.submit()
                  this.$router.push('/home')
                } else {
                  this.$message.error('提交失败')
                }
              }).catch(() => {
                this.$message.error('提交失败')
              })
            }
          }).catch(() => {
            this.$message.error('获取用户信息失败')
          })
        } else {
          this.$message.error('信息填写不完整')
        }
      },
      cancel: function () {
        this.$confirm('确定放弃本次任务发布吗？', '提示', {
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
      handleOptionConfirm: function () {
        this.optionValue = String.trim(this.optionValue)
        if (this.optionValue !== '') {
          this.task.options.push(this.optionValue)
        }
        this.optionValue = ''
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
