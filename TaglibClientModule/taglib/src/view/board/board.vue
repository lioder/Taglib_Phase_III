<template>
  <div class="board-wrapper">
    <div class="col-left">
      <div class="taglist-wrapper">
        <div class="tags" v-if="boardState === 'edit'" v-show="showTags">
          <h1 class="title">注释区</h1>
          <div class="content">
            <h1 class="label">整体描述</h1>
            <div class="overall-desc">
              <el-input
                type="textarea"
                autosize
                placeholder="请输入对图片的整体描述"
                v-model="overall">
              </el-input>
              <el-button type="plain" @click="confirm">确认</el-button>
            </div>
            <h1 class="label">局部标注</h1>
            <div class="map-desc" v-show="taskType === 0">
              <ul>
                <li v-for="(item,index) in labels" :key="index">
                  <div class="label">{{ item }}</div>
                  <el-input size="mini" v-model="tag.mapDesc[item]"
                            @keydown.enter.native="confirmTag($event, 1)"></el-input>
                </li>
              </ul>
              <el-button type="primary" @click="confirmTag($event, 1)">确认</el-button>
            </div>
            <div class="single-desc" v-show="taskType !== 0">
              <el-input
                class="single-desc-input"
                v-model="tag.singleDesc"
                size="small"
                placeholder="请输入标注"
                clearable
                @keydown.enter.native="confirmTag($event, 0)"></el-input>
              <el-button class="single-desc-btn" type="plain" @click="confirmTag($event, 0)">确认</el-button>
            </div>
          </div>
        </div>
        <div class="results">
          <h1 class="title">标注结果</h1>
          <div class="content">
            <div class="overall-wrapper">
              <h1 class="label">整体描述</h1>
              <p class="overall-text">{{ getOverallDesc }}</p>
            </div>
            <div class="part-wrapper">
              <h1 class="label">局部标注</h1>
              <div v-if="tags.length === 0">无</div>
              <ul class="tag-list">
                <li class="tag-item" v-for="(tag,index) in tags" :key="index" v-if="tag.tagType !== 0">
                  <div class="single" v-if="tag.descType === 0">
                    <span>{{ index+1 }}. {{ tag.singleDesc }}</span>
                    <div class="icon-group">
                      <i class="el-icon-delete" @click="deleteTag(index, tag.id)"></i>
                    </div>
                  </div>
                  <div class="map" v-if="tag.descType === 1">
                    <div>{{ index+1 }}</div>
                    <div v-for="(e,index) in Object.keys(tag.mapDesc)" :key="index">{{e}}: {{ tag.mapDesc[e] }}</div>
                    <div class="icon-group">
                      <i class="el-icon-delete" @click="deleteTag(index, tag.id)"></i>
                    </div>
                  </div>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="col-mid">
      <canvas ref="canvas" id="canvas" :style="styleObject"></canvas>
    </div>
    <div class="col-right">
      <div class="tagutil-wrapper">
        <div class="task-info">
          <h1 class="title">任务信息</h1>
          <div class="content">
            <div class="label">任务描述</div>
            <div class="desc">{{ desc }}</div>
            <question-no ref="questionNo" class="question-no" @change-active="changeIndex"
                         v-show="boardState === 'check'"></question-no>
            <el-pagination
              v-show="boardState === 'edit'"
              class="pagination"
              background
              small
              layout="prev, pager, next"
              :page-size="1"
              :total="questions.length"
              @current-change="changeIndex">
            </el-pagination>
            <div class="edit-btn-group" v-if="boardState === 'edit'">
              <el-button type="danger" @click="submit('SUBMITTED')">提交</el-button>
              <el-button type="danger" @click="submit('PROCESSING')">保存</el-button>
              <el-button type="plain" @click="giveUp">放弃</el-button>
            </div>
            <div class="read-btn-group" v-if="boardState === 'check'">
              <el-button type="plain" @click="check(1)">通过</el-button>
              <el-button type="danger" @click="check(2)">不通过</el-button>
              <el-button type="primary" @click="finishCheck">完成</el-button>
            </div>
          </div>
        </div>
        <div v-if="boardState === 'edit'" class="utils">
          <h1 class="title">工具区</h1>
          <div class="content">
            <div class="btn-group">
              <el-radio-group v-model="selectUtil" size="small" ref="utils">
                <el-radio-button @click="select(1)" label="1" :disabled="taskType === 2">
                  <i class="iconfont">&#xe648;</i>矩形
                </el-radio-button>
                <el-radio-button @click="select(2)" label="2" :disabled="taskType !== 2">
                  <i class="iconfont">&#xe6bc;</i>铅笔
                </el-radio-button>
              </el-radio-group>
            </div>
            <el-row>
              <div class="label">拾色器</div>
              <div class="color-picker">
                <el-color-picker v-model="color" size="small"></el-color-picker>
              </div>
              <!--<div class="label">缩放</div>-->
              <!--<el-slider-->
              <!--v-model="ratio"-->
              <!--:max="100"-->
              <!--:format-tooltip="formatTooltip">-->
              <!--</el-slider>-->
            </el-row>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script type="text/ecmascript-6">
  import QuestionNo from '../../components/question-no/question-no'

  let lodash = require('lodash')

  const none = -1
  const rect = 1
  const pen = 2
  // 整体描述，无框，一句注释
  const overall = 0
  // 区域标注，铅笔，一句注释
  const region = 1
  // 分类标注，一个框，多个注释
  const sort = 2
  // 标框标注，一个框，无注释
  const box = 3

  export default {
    components: {QuestionNo},
    name: 'board',
    props: {
      task: {
        type: Object
      }
    },
    watch: {
      'color': function (newColor, oldColor) {
        this.ctx.strokeColor = newColor
        this.ctx.fillStyle = newColor
      }
    },
    created () {
      this.$nextTick(() => {
        let that = this
        this.canvas = this.$refs.canvas
        this.ctx = this.canvas.getContext('2d')

        let canvas = this.canvas
        let ctx = this.ctx

        resizeCanvas()

        window.addEventListener('resize', resizeCanvas, false)
        window.addEventListener('load', resizeCanvas, false)
        window.addEventListener('beforeunload', function (e) {
          e.returnValue('确定离开当前页面吗？')
        })

        function resizeCanvas () {
          canvas.width = window.innerWidth * 0.6
          canvas.height = window.innerHeight
          if (this) {
            that._repaint()
          }
        }

        function getPointOnCanvas (event) {
          let canvasOffset = canvas.getBoundingClientRect()
          return {
            x: (event.clientX - canvasOffset.left),
            y: (event.clientY - canvasOffset.top)
          }
        }

        class Basic {
          constructor (color = '#000') {
            this.width = 3
            this.color = color
            this.drawing = false
          }
        }

        class Rect extends Basic {
          constructor (color = '#000') {
            super(color)
          }

          begin (location) {
            // 保存无阴影的画布
            this.noShadowScene = ctx.getImageData(0, 0, canvas.width, canvas.height)

            // 画的矩形没有使用
            if (that.scene) {
              ctx.putImageData(that.scene, 0, 0)
            } else {
              that.scene = this.noShadowScene
            }

            // 画阴影
            let oldFillStyle = ctx.fillStyle
            ctx.fillStyle = 'rgba(7,17,27,0.7)'
            ctx.fillRect(0, 0, canvas.width, canvas.height)
            ctx.fillStyle = oldFillStyle

            this.scene = ctx.getImageData(0, 0, canvas.width, canvas.height)
            this.startPosition = location
            that.tag.startPosition = this.startPosition
          }

          draw (location) {
            // 解决方向问题
            ctx.putImageData(this.scene, 0, 0)
            const startX = this.startPosition.x
            const startY = this.startPosition.y
            const rect = {
              x: startX <= location.x ? startX : location.x,
              y: startY <= location.y ? startY : location.y
            }
            ctx.beginPath()
            ctx.strokeStyle = that.color
            ctx.rect(rect.x, rect.y, Math.abs(startX - location.x), Math.abs(startY - location.y))
            ctx.stroke()
            ctx.clearRect(rect.x, rect.y, Math.abs(startX - location.x), Math.abs(startY - location.y))
          }

          end () {
            const startX = this.startPosition.x
            const startY = this.startPosition.y
            const rect = {
              x: startX <= location.x ? startX : location.x,
              y: startY <= location.y ? startY : location.y
            }
            ctx.beginPath()
            ctx.rect(rect.x, rect.y, Math.abs(startX - location.x), Math.abs(startY - location.y))
            ctx.stroke()
          }

          bindEvent () {
            // 如果鼠标左键按下，则开始画矩形
            canvas.addEventListener('mousedown', (e) => {
              e.preventDefault()
              if (Number(that.selectUtil) === rect) {
                this.drawing = true
                let startPoint = getPointOnCanvas(e)
                this.begin(startPoint)
              }
            })
            // 如果鼠标移动,就跟踪点，并不断重画矩形
            canvas.addEventListener('mousemove', (e) => {
              e.preventDefault()
              if (Number(that.selectUtil) === rect) {
                if (this.drawing) {
                  let location = getPointOnCanvas(e)
                  this.draw(location)
                }
              }
            })
            // 如果鼠标左键释放，则停止绘画
            canvas.addEventListener('mouseup', (e) => {
              e.preventDefault()
              if (Number(that.selectUtil) === rect) {
                let location = getPointOnCanvas(e)
                this.end(location)
                this.drawing = false

                that.tag.endPosition.x = location.x
                that.tag.endPosition.y = location.y
              }
            })
          }
        }

        // 铅笔类
        class Pencil extends Basic {
          constructor (color = '#000') {
            super(color)
          }

          begin (loc) {
            // 先保存画布状态，再改变画布状态
            ctx.beginPath()
            ctx.strokeStyle = that.color
            ctx.moveTo(loc.x, loc.y)
          }

          draw (loc) {
            ctx.strokeStyle = that.color
            ctx.lineTo(loc.x, loc.y)
            ctx.stroke()
          }

          end (loc) {
            ctx.strokeStyle = that.color
            ctx.lineTo(loc.x, loc.y)
            ctx.stroke()
          }

          bindEvent () {
            canvas.addEventListener('mousedown', (e) => {
              e.preventDefault()
              if (Number(that.selectUtil) === 2) {
                this.drawing = true
                let loc = getPointOnCanvas(e)
                that.tag.penPoints.push(loc)
                this.begin(loc)
              }
            })
            canvas.addEventListener('mousemove', (e) => {
              e.preventDefault()
              if (Number(that.selectUtil) === 2) {
                if (this.drawing) {
                  let loc = getPointOnCanvas(e)
                  that.tag.penPoints.push(loc)
                  this.draw(loc)
                }
              }
            })
            canvas.addEventListener('mouseup', (e) => {
              e.preventDefault()
              if (Number(that.selectUtil) === 2) {
                let loc = getPointOnCanvas(e)
                that.tag.penPoints.push(loc)
                this.end(loc)
                this.drawing = false
              }
            })
          }
        }

        const rectangle = new Rect()
        const pen = new Pencil()
        rectangle.bindEvent()
        pen.bindEvent()
      })
    },
    computed: {
      styleObject: function () {
        return {
          backgroundImage: this.taskPublisherId !== 0 ? "url('/show/" + this.taskPublisherId + '/' + this.filename + "')" : "url('/static/show/0/191003284_1025b0fb7d.jpg')",
          backgroundSize: '75%',
          backgroundRepeat: 'no-repeat',
          backgroundPosition: 'center center'
        }
      },
      tagType: function () {
        if (!this.tags[0]) {
          return 0
        }
        const tagType = this.tags[0].tagType
        const descType = this.tags[0].descType
        if (tagType === 0 && descType === 0) {
          return overall
        } else if (tagType === 2 && descType === 0) {
          return region
        } else if (tagType === 1 && descType === 1) {
          return sort
        } else if (tagType === 1 && descType === 0) {
          return box
        }
      },
      getOverallDesc: function () {
        for (let i = 0; i < this.tags.length; i++) {
          if (this.tags[i].tagType === 0) {
            return this.tags[i].singleDesc
          }
        }
        return '无'
      }
      // },
      // imageBound: function () {
      //   return {
      //     x: this.canvas.width * 0.25 / 2,
      //     y: 0,
      //     width: this.canvas.width * 0.75,
      //     height: this.canvas.height * 0.75
      //   }
      // }
    },
    data () {
      return {
        boardState: 'edit',
        taskId: 0,
        taskPublisherId: 0,
        taskType: 0,
        index: 0, // 当前题号-1
        questions: [],
        filename: '',
        desc: '',
        title: '',
        labels: [],
        overall: '',
        scene: null,

        canvas: {},
        ctx: {},
        color: '#FF0000',
        selectUtil: none, // 选中的工具
        drawing: false,
        showTags: true,

        tag: {
          id: 0,
          tagType: 0,
          penPoints: [],
          startPosition: {
            x: 0,
            y: 0
          },
          endPosition: {
            x: 0,
            y: 0
          },
          descType: 0,
          mapDesc: {},
          singleDesc: ''
        },
        tags: []
      }
    },
    mounted () {
      this.$nextTick(() => {
        let taskWorker = localStorage.getItem('taskWorker')
        let boardState = localStorage.getItem('boardState')
        if (boardState) {
          this.boardState = boardState
        }
        if (taskWorker) {
          taskWorker = JSON.parse(taskWorker)
          this.desc = taskWorker.description
          this.taskId = taskWorker.id
          this.taskPublisherId = taskWorker.taskId

          if (this.boardState === 'check') {
            // 随机审批，少于10个取全部，多余10个取根号
            let temp = taskWorker.images
            console.log(temp)
            if (taskWorker.images.length >= 10) {
              let size = Math.round(Math.sqrt(temp.length))
              size = size < 10 ? 10 : size
              temp = lodash.shuffle(taskWorker.images).slice(0, size)
            }
            this.questions = temp
          } else {
            this.questions = taskWorker.images
          }
          this.$refs.questionNo.setSize(this.questions.length)
          this.labels = taskWorker.labels
          this.filename = this.questions[0].filename
          this.taskType = taskWorker.taskType
          this.questions.forEach((question) => {
            if (question.tags === null) {
              question.tags = []
            }
          })
          this.tags = this.questions[0].tags
          this._repaint()
        }
      })
      if (this.boardState === 'edit') {
        this.$alert('使用矩形工具画出一个矩形后, 要及时在注释区填入注释，并点击确认键保存哦！', '小贴士', {
          confirmButtonText: '确定',
          type: 'warning'
        }).then(() => {
        })
      }
    },
    methods: {
      select: function (util) {
        this.selectUtil = util
      },
      deleteTag: function (index) {
        this.tags.splice(index, 1)
        this._repaint()
      },
      confirm: function () {
        if (this.overall.trim() === '') {
          this.$message.error('标注失败，请重新输入')
        } else {
          let target = this.findOverallTag()
          this.tag.singleDesc = this.overall
          this.tag.tagType = 0
          if (target) {
            Object.assign(target, this.tag)
          } else {
            this.tags.push(this.tag)
          }
          this.$message({
            message: '标注成功',
            type: 'success'
          })
          this.tag = this._getNewTag()
          if (this.scene) {
            this.ctx.putImageData(this.scene, 0, 0)
          }
          this.scene = null
          console.log('here')
          this._repaint()
        }
      },
      confirmTag: function (event, type) {
        if (event.which === 229) {
          return
        }
        this.tag.descType = type
        this.tag.tagType = this.selectUtil
        if (Number(this.tag.tagType) === 1 &&
          this.tag.startPosition.x === 0 && this.tag.startPosition.y === 0 &&
          this.tag.endPosition.x === 0 && this.tag.endPosition.y === 0) {
          this.$message.error('标注失败，没有进行标记')
        } else if ((Number(this.tagType) === 2 && this.tag.penPoints.length === 0) || this.selectUtil === none) {
          this.$message.error('标注失败，没有进行标记')
        } else if ((this.tag.singleDesc === '' && type === 0) || (this.isEmptyObject(this.tag.mapDesc) && type === 1)) {
          this.$message.error('标注失败，请重新输入注释')
        } else {
          this.tag.color = this.color
          this.tags.push(this.tag)
          this.$message({
            message: '标注成功',
            type: 'success'
          })
          if (this.scene) {
            this.ctx.putImageData(this.scene, 0, 0)
          }
          this.scene = null
          this.tag = this._getNewTag()
          this._repaint()
        }
      },
      submit: function (state) {
        let confirmMessage = ''
        if (state === 'PROCESSING') {
          confirmMessage = '确认保存当前任务？（保存后可在我的任务中继续完成任务）'
        } else {
          confirmMessage = '确认已完成所有的标注任务?(提交后将不能再次编辑）'
        }
        this.$confirm(confirmMessage, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          let taskWorker = localStorage.getItem('taskWorker')
          if (taskWorker) {
            taskWorker = JSON.parse(taskWorker)
            taskWorker.taskState = state
            taskWorker.images = this.questions
          }
          this.$ajax.post('/user/tasks', taskWorker).then((response) => {
            this.$message({
              type: 'success',
              message: '成功!'
            })
            this.$router.push('/myTasks')
          }).catch(() => {
            this.$message({
              type: 'info',
              message: '失败，服务器开小差啦'
            })
          })
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消提交'
          })
        })
      },
      changeIndex: function (val) {
        this.index = val - 1
        this.tags = this.questions[this.index].tags
        this.filename = this.questions[this.index].filename
        this.overall = ''
        this.tag = this._getNewTag()
        this._repaint()
      },
      findOverallTag: function () {
        for (let i = 0; i < this.tags.length; i++) {
          if (this.tags[i].tagType === 0) {
            return this.tags[i]
          }
        }
        return null
      },
      _getNewTag: function () {
        return {
          id: 0,
          tagType: 0,
          penPoints: [],
          startPosition: {
            x: 0,
            y: 0
          },
          endPosition: {
            x: 0,
            y: 0
          },
          descType: 0,
          mapDesc: {},
          singleDesc: ''
        }
      },
      isEmptyObject: function (e) {
        let t
        for (t in e) {
          return !1
        }
        return !0
      },
      giveUp: function () {
        this.$confirm('确认放弃本次标注任务？（标注结果将不会保存）', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$router.push('/home')
        }).catch(() => {
        })
      },
      _repaint: function () {
        let tags = this.tags
        let ctx = this.ctx
        ctx.clearRect(0, 0, this.canvas.width, this.canvas.height)
        for (let i = 0; i < tags.length; i++) {
          let tag = tags[i]
          ctx.strokeStyle = tag.color
          if (Number(tag.tagType) === rect) {
            ctx.beginPath()
            ctx.rect(tag.startPosition.x, tag.startPosition.y, Math.abs(tag.endPosition.x - tag.startPosition.x), Math.abs(tag.endPosition.y - tag.startPosition.y))
            ctx.stroke()
          } else if (Number(tag.tagType) === pen) {
            ctx.beginPath()
            ctx.moveTo(tag.penPoints[0].x, tag.penPoints[0].y)
            for (let j = 1; j < tag.penPoints.length; j++) {
              ctx.lineTo(tag.penPoints[j].x, tag.penPoints[j].y)
              ctx.stroke()
            }
          }
        }
      },
      formatTooltip (val) {
        return val + '%'
      },
      check: function (type) {
        this.$refs.questionNo.pass(type, this.index)
      },
      finishCheck: function () {
        let result = this.$refs.questionNo.result()
        if (result === null) {
          this.$message.error('还有未审核的题!')
          return
        }
        this.$ajax.post('/admin/checkTag/' + this.taskId, this.$qs.stringify({
          checkResult: result
        })).then((res) => {
          let result = res.data
          if (result.code === 0) {
            localStorage.setItem('boardState', null)
            localStorage.setItem('fromRoute', '/board')
            this.$router.go(-1)
            this.$message.success('审核成功')
          } else {
            this.$message.error('审核失败')
          }
        })
      }
    }
  }
</script>

<style lang="stylus">
  .board-wrapper
    width 100%
    min-height 100vh
    display flex
    background-color #f6f6f7
    .el-button
      padding 7px 10px
    .title
      background-color rgba(7, 17, 27, 0.6)
      color #fff
      text-align center
      font-size 14px
      padding 5px 0
    .label
      margin-bottom 10px
      color: #409eff
    .col-left
      flex 1 0 auto
      .taglist-wrapper
        min-height 100%
        border-right 2px solid rgba(7, 17, 27, 0.6)
        background-color #f6f6f7
        .tags
          .content
            padding 20px 25px 10px 25px
            .el-input, textarea, .el-button
              margin-bottom: 10px
            .el-button
              font-size: 12px
            .overall-desc
              margin-bottom 10px
              border-bottom 1px solid rgba(7, 17, 27, 0.2)
            .single-desc
              .single-desc-input
                width 72%
              .single-desc-btn
                min-width 25%
                height 32px
        .results
          .content
            padding: 20px 25px
            .overall-wrapper
              padding-bottom 15px
              margin-bottom 15px
              border-bottom 1px solid rgba(7, 17, 27, 0.2)
            .tag-item
              position relative
              padding 15px 0
              border-bottom 1px solid rgba(7, 17, 27, 0.1)
              .icon-group
                position: absolute
                top: 15px
                right: 0
                i
                  &:hover
                    color: #409eff
    .col-mid
      flex 0 0 auto
      #canvas
        z-index 100
    .col-right
      flex 1 0 auto
      .question-no
        margin-bottom 15px
        padding-bottom 5px
        border-bottom 1px solid rgba(7, 17, 27, 0.2)
      .tagutil-wrapper
        min-height 100%
        background-color #f6f6f7
        border-left 2px solid rgba(7, 17, 27, 0.6)
      .title
        padding 5px
        font-size: 14px
        text-align center
        background-color rgba(7, 17, 27, 0.6)
        color #fff
      .task-info
        .content
          padding 15px 25px
          line-height 20px
          .desc, .pagination
            margin-bottom 15px
            padding-bottom 15px
            border-bottom 1px solid rgba(7, 17, 27, 0.2)
          .el-button
            margin-right 0
            font-size 12px
      .utils
        .content
          padding 20px 25px
          .btn-group
            padding-bottom 15px
            margin-bottom 15px
            border-bottom 1px solid rgba(7, 17, 27, 0.1)
          .color-picker
            margin-bottom 15px
            padding-top 0
</style>
