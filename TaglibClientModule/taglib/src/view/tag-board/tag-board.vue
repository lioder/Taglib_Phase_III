<template>
  <div id="tag-board">
    <div class="col-left" ref="colLeft">
      <div v-show="overallDesc === '无' && false" v-if="boardState === 'edit'">
        <h1 class="title">整体描述</h1>
        <div class="content">
          <div class="overall-desc">
            <el-input
              type="textarea"
              autosize
              placeholder="请输入对图片的整体描述"
              v-model="overall">
            </el-input>
            <el-button type="plain" @click="confirmOverall" size="small">确认</el-button>
          </div>
        </div>
      </div>
      <div class="results">
        <h1 class="title">标注结果</h1>
        <div class="content">
          <div class="overall-wrapper" v-show="false">
            <h1 class="label">整体描述</h1>
            <p class="overall-text">{{ overallDesc }}</p>
          </div>
          <div class="part-wrapper">
            <h1 class="label">局部标注</h1>
            <div v-if="tags.length === 0">无</div>
            <ul class="tag-list">
              <li class="tag-item" v-for="(tag,index) in tags" :key="index" v-if="tag.tagType !== 0">
                <div class="single" v-if="tag.descType === 0">
                  <span>{{ index+1 }}. {{ tag.singleDesc }}</span>
                  <div class="icon-group" v-show="boardState === 'edit'">
                    <i class="el-icon-edit" @click="editTag(index)"></i>
                    <el-popover
                      placement="right"
                      width="160"
                      v-model="deleteToolTips[index]">
                      <p style="margin-bottom: 10px">确定删除这个标注吗？</p>
                      <div style="text-align: right;">
                        <el-button size="mini" type="plain" @click="cancelDelete(index)">取消</el-button>
                        <el-button type="primary" size="mini" @click="deleteTag(index)">确定</el-button>
                      </div>
                      <i class="el-icon-delete" slot="reference"></i>
                    </el-popover>
                  </div>
                </div>
                <div class="map" v-if="tag.descType === 1">
                  <div style="margin-bottom: 5px">{{ index+1 }}</div>
                  <div v-for="(e,index) in Object.keys(tag.mapDesc)" :key="index" style="margin-bottom: 3px">{{e}}: {{
                    tag.mapDesc[e] }}
                  </div>
                  <div class="icon-group">
                    <i class="el-icon-edit" @click="editTag(index)"></i>
                    <el-popover
                      placement="right"
                      width="160"
                      v-model="deleteToolTips[index]">
                      <p style="margin-bottom: 10px">确定删除这个标注吗？</p>
                      <div style="text-align: right;">
                        <el-button size="mini" type="plain" @click="cancelDelete(index)">取消</el-button>
                        <el-button type="primary" size="mini" @click="deleteTag(index)">确定</el-button>
                      </div>
                      <i class="el-icon-delete" slot="reference"></i>
                    </el-popover>
                  </div>
                </div>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
    <div class="col-mid">
      <div class="click-shade" v-if="boardState !== 'edit'" @mouseup="0" @mousemove="0"
           @mousedown="0" style="z-index: 10000"></div>
      <div class="img-box">
        <div class="img-body-wrapper">
        <span class="img-body" ref="imgBody">
          <img :src="imageUrl" ref="img" class="img" width="100%" height="100%">
          <div class="tag-content" ref="tagContent" @mouseup="finishTag($event)" @mousemove="move($event)"
               @mousedown="clickContent">
            <div class="tag-wrapper">
                <div class="tag" v-for="(item, index) in tags" v-if="Number(item.tagType) === 1" :key="index"
                     :style="{
                 left: (item.endPosition.x-item.startPosition.x > 0 ? item.startPosition.x : item.endPosition.x)*100 + '%',
                 top: (item.endPosition.y-item.startPosition.y > 0 ? item.startPosition.y : item.endPosition.y)*100 + '%',
                 width: Math.abs(item.endPosition.x-item.startPosition.x) * 100 + '%',
                 height: Math.abs(item.endPosition.y-item.startPosition.y) * 100 + '%',
                 background: showShade ? 'rgba(0, 0, 0, 0)' : getBackgroundColor(index)}"
                     :class="{'active': index === activeIndex}"
                     @mousedown.stop="clickTag($event, index, item)">
                <span class="tag-text" v-show="!(showShade && activeIndex === index)">{{ item.singleDesc }}</span>
                <div class="spot-wrapper">
                  <i class="spot" id="spot" v-for="i in 8" :key="i" @mousedown.stop="clickSpot($event, i, item)"></i>
                </div>
                <div class="em-wrapper" v-show="showShade && activeIndex === index">
                  <div class="em" v-for="i in 4" :key="i"></div>
                </div>
              </div>
            </div>
            <div class="point-tag-wrapper" v-if="taskWorker.taskType === 2">
            <div class="point-tag" v-for="(item, index) in tags" v-if="Number(item.tagType) === 2" :key="index">
                 <div class="point" v-for="(point, i) in item.penPoints" :key="i"
                      :style="{left: point.x*100 + '%', top: point.y*100 + '%'}"></div>
            </div>
            </div>
          </div>
        </span>
        </div>
        <ul class="drop-menu" ref="dropMenu" v-show="showMenu">
          <div>
            <div class="btn delete-btn" @click="deleteTag">删除</div>
            <div class="btn edit-btn" @click="editTag">编辑</div>
          </div>
        </ul>
      </div>
      <transition enter-active-class="bounceIn" leave-active-class="bounceOut" class="animated">
        <div class="input-box-wrapper" v-show="showEdit">
          <div class="input-box">
            <div class="header">
              <span class="title">编辑内容</span>
              <i class="el-icon-close close-btn" @click="cancelConfirm"></i>
            </div>
            <div class="content">
              <div v-if="taskWorker.taskType === 2">
                <el-input size="small" v-model="singleDesc"/>
              </div>
              <div v-if="taskWorker.taskType === 1" style="margin: 20px 0; text-align: center">
                <el-radio-group v-model="singleDesc" ref="options">
                  <el-radio v-for="item in options"
                            :key="item"
                            :label="item">{{ item }}
                  </el-radio>
                </el-radio-group>
              </div>
              <div v-if="taskWorker.taskType === 0 && activeIndex >= 0">
                <div v-for="(label, index) in taskWorker.labels" :key="index">
                  <span>{{ label }}</span>
                  <el-input size="small" style="margin: 10px 0" v-model="tags[activeIndex].mapDesc[label]"></el-input>
                </div>
              </div>
              <div class="my-btn-group">
                <el-button class="btn confirm-btn" size="small" type="primary" @click="confirm">确定</el-button>
                <el-button class="btn cancel-btn" size="small" @click="cancelConfirm">取消</el-button>
              </div>
            </div>
          </div>
        </div>
      </transition>
    </div>
    <div class="col-right">
      <div class="tagutil-wrapper">
        <div class="task-info">
          <h1 class="title">任务信息</h1>
          <div class="content">
            <div class="label">任务描述</div>
            <div class="desc">{{ taskWorker.description }}</div>
            <question-no ref="questionNo" class="question-no" @change-active="changeIndex"></question-no>
            <div class="edit-btn-group" v-if="boardState === 'edit'">
              <el-button type="danger" @click="submit('SUBMITTED')" size="mini">提交</el-button>
              <el-button type="danger" @click="submit('PROCESSING')" size="mini">保存</el-button>
              <el-button type="plain" @click="giveUp" size="mini">放弃</el-button>
            </div>
            <div class="view-btn-group" v-if="boardState === 'view-result'">
              <el-button type="danger" @click="back" size="mini">返回</el-button>
            </div>
            <div class="read-btn-group" v-if="boardState === 'check'">
              <el-button type="plain" @click="check(1)" size="small">通过</el-button>
              <el-button type="danger" @click="check(0)" size="small">拒绝</el-button>
              <el-button type="primary" @click="finishCheck" size="small">完成</el-button>
            </div>
          </div>
        </div>
        <div class="utils" v-if="boardState === 'edit'">
          <h1 class="title">工具区</h1>
          <div class="content">
            <div class="btn-group">
              <el-radio-group v-model="drawMode" size="small" ref="utils">
                <el-radio-button @click="selectMode(1)" :label="1" :disabled="taskWorker.taskType == 2">
                  <i class="iconfont">&#xe648;</i>矩形
                </el-radio-button>
                <el-radio-button @click="selectMode(2)" :label="2" :disabled="taskWorker.taskType !== 2">
                  <i class="iconfont">&#xe6bc;</i>铅笔
                </el-radio-button>
              </el-radio-group>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import lodash from 'lodash'
  import QuestionNo from '../../components/question-no/question-no'

  let isMouseDown = false
  let isEdit = false
  let moveTag = false
  let moveSpot = false
  let newTag = false
  let obj = {} // 鼠标click的位置
  let oldTag = {
    startPosition: {
      x: 0,
      y: 0
    },
    endPosition: {
      x: 0,
      y: 0
    }
  }
  let tagItem = {}
  let spotIndex = 0
  export default {
    name: "TagBoard",
    data () {
      return {
        activeIndex: -1,
        index: 0,
        imageUrl: "https://i1.mifile.cn/f/i/18/mitv4A/40/build.jpg",
        tags: [{
          id: 0,
          startPosition: {
            x: 0.3,
            y: 0.3
          },
          endPosition: {
            x: 0.5,
            ey: 0.5
          },
          tagType: 1,
          penPoints: [],
          descType: 0,
          mapDesc: {},
          singleDesc: 'opo',
          color: 'blue'
        }],
        shade: false,

        // 画板相关
        deleteToolTips: [false, false, false, false],
        boardState: '',
        singleDesc: '',
        overall: '',
        showMenu: false,
        showEdit: false,
        showShade: false,
        drawMode: 1,
        options: ['脸', '手'],
        taskWorker: {
          taskType: 1,
          desc: '',
          labels: [],
          images: [{
            filename: '',
            tags: []
          }]
        }
      }
    },
    components: {QuestionNo},
    computed: {
      overallDesc: function () {
        for (let i = 0; i < this.tags.length; i++) {
          let item = this.tags[i]
          if (Number(item.tagType) === 0) {
            console.log(item.singleDesc)
            return item.singleDesc
          }
        }
        return '无'
      },
      getTips: function () {
        let tips = ['使用<kbd>←</kbd>和<kbd>→</kbd>快捷切换图片',
          '使用数字键<kbd>1</kbd><kbd>2</kbd>...快速选择标注文字',
          '使用<kbd>enter</kbd>键, 快速确认一个标注']
        return tips[Math.floor(Math.random() * tips.length)]
      }
    },
    mounted () {
      let that = this
      let t = this.$refs.tagContent
      t.oncontextmenu = function () {
        return false
      }
      // 当窗口大小调整时，画板大小也同步调整
      window.addEventListener('resize', this.resizeImg)
      setTimeout(() => {
        this.resizeImg()
      }, 50)
      // 离开页面前二次确认
      window.onbeforeunload = function (e) {
        e = e || window.event
        if (e) {
          e.returnValue = 'Sure?'
        }
        return 'Sure?'
      }
      document.onkeyup = function (e) {
        if (e.keyCode === 37) { // 方向键切换图片
          console.log(1)
          that.changeIndex(that.index)
        } else if (e.keyCode === 39) { // 方向键切换图片
          console.log(2)
          that.changeIndex(that.index + 2)
        } else if (that.showEdit && (e.keyCode >= 48 && e.keyCode <= 57)) { // 数字选择标签
          let optionIndex = e.keyCode - 49
          if (optionIndex >= 0 && optionIndex < that.options.length) {
            that.singleDesc = that.options[optionIndex]
          }
        } else if (that.showEdit && e.keyCode === 13) {
          that.confirm()
        }
      }
      // localStorage.setItem('boardState', 'edit')
      // localStorage.setItem('taskWorker', JSON.stringify({
      //   id: 0,
      //   taskId: 1,
      //   userId: 1,
      //   title: '标注',
      //   description: '标注',
      //   taskType: 0,
      //   taskState: 'PROCESSING',
      //   images: [{
      //     filename: 'https://i1.mifile.cn/f/i/18/mitv4A/40/build.jpg',
      //     tags: [{
      //       tagType: 2,
      //       startPosition: {
      //         x: 0.3,
      //         y: 0.1
      //       },
      //       endPosition: {
      //         x: 0.5,
      //         y: 0.5
      //       },
      //       penPoints: [{x: 0.5, y: 0.5}],
      //       descType: 0,
      //       singleDesc: 'da',
      //       mapDesc: {
      //       }
      //     }]
      //   },
      //     {
      //       filename: 'https://i1.mifile.cn/f/i/18/mitv4A/40/build.jpg',
      //       tags: [{
      //         tagType: 2,
      //         startPosition: {
      //           x: 0.3,
      //           y: 0.1
      //         },
      //         endPosition: {
      //           x: 0.5,
      //           y: 0.5
      //         },
      //         penPoints: [{x: 0.5, y: 0.5}],
      //         descType: 0,
      //         singleDesc: 'da',
      //         mapDesc: {
      //           '年龄': '18'
      //         }
      //       }]
      //     }],
      //   labels: ['年龄', '性别'],
      //   topics: []
      // }))
      let taskWorker = localStorage.getItem('taskWorker')
      let boardState = localStorage.getItem('boardState')
      if (boardState) {
        this.boardState = boardState
      }

      this.$nextTick(() => {
          if (taskWorker) {
            this.taskWorker = JSON.parse(taskWorker)
            if (this.boardState === 'check') {
              // 随机审批，少于10个取全部，多余10个取根号
              let temp = this.taskWorker.images
              if (temp.length >= 10) {
                let size = Math.round(Math.sqrt(temp.length))
                size = size < 10 ? 10 : size
                temp = lodash.shuffle(temp).slice(0, size)
              }
              this.taskWorker.images = temp
            }
            this.$refs.questionNo.setSize(this.taskWorker.images.length)
            this.taskWorker.images.forEach((item, index) => {
              if (item.tags === null) {
                item.tags = []
              }
              if (this.boardState === 'edit') {
                this.$refs.questionNo.pass((2 - (item.tags.length > 0)), index)
              }
            })
            this.imageUrl = '/show/' + this.taskWorker.taskId + '/' + this.taskWorker.images[0].filename
            this.tags = this.taskWorker.images[0].tags
            if (this.boardState === 'edit') {
              for (let i = 0; i < (this.tags.length - this.deleteToolTips.length); i++) {
                this.deleteToolTips.push(false)
              }
              this.$ajax.get('/tasks/' + this.taskWorker.taskId).then((res) => {
                let result = res.data
                if (result.code === 0) {
                  this.options = result.data.options
                }
              })
            }
          }
        }
      )
      if (this.boardState === 'edit') {
        this.$notify({
          title: '小贴士',
          dangerouslyUseHTMLString: true,
          message: this.getTips
        })
      } else if (this.boardState === 'view-result') {
        this.$notify({
          title: '小贴士',
          dangerouslyUseHTMLString: true,
          message: '<span style="display: inline-block;width: 13px;height: 13px; background: rgba(255,0,0,0.3)"></span>  说明标注正确<br>' +
          '<span style="display: inline-block;width: 13px;height: 13px; background: rgba(0,255,0,0.3)"></span>  说明标注错误<br>' +
          '<span style="display: inline-block;width: 13px;height: 13px; background: rgba(0,0,255,0.3)"></span>  说明遗漏标注'
        })
      }
    },
    methods: {
      check: function (type) {
        this.$refs.questionNo.pass(type, this.index)
      },
      finishCheck: function () {
        let result = this.$refs.questionNo.result()
        if (result === null) {
          this.$message.error('还有未审核的题!')
          return
        }
        this.$ajax.post('/admin/checkTag/' + this.taskWorker.id, this.$qs.stringify({
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
          this.taskWorker.taskState = state
          this.$ajax.post('/user/tasks', this.taskWorker).then((response) => {
            let result = response.data
            if (result.code === 0) {
              this.$message({
                type: 'success',
                message: '成功!'
              })
              if (this.taskWorker.taskState === 'SUBMITTED') {
                this.$alert(`恭喜你完成了本次任务! <br> 这 ${result.data} T币就作为小奖励啦， <br> 等待任务通过后还会有更多积分奖励哦!`, '提示', {
                  confirmButtonText: '确定',
                  dangerouslyUseHTMLString: true,
                  callback: action => {
                  }
                })
              }
              this._cleanExitQuestion()
              this.$router.push('/myTasks')
            } else {
              this.$message.error('失败，服务器开小差啦')
            }
          }).catch(() => {
            this.$message.error('失败，服务器开小差啦')
          })
        }).catch(() => {
          console.log('hhh')
          this.$message({
            type: 'info',
            message: '已取消提交'
          })
        })
      },
      giveUp: function () {
        this.$confirm('确认放弃本次标注任务？（标注结果将不会保存）', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this._cleanExitQuestion()
          this.$router.push('/home-view')
        }).catch(() => {
        })
      },
      back: function () {
        this.$router.go(-1)
      },
      cancelDelete: function (index) {
        this.deleteToolTips[index] = false
        this.$set(this.deleteToolTips, index, false)
      },
      changeIndex: function (i) {
        if (i <= 0) {
          this.$message.error('这已经是第一张图片了！')
          return
        }
        if (i > this.taskWorker.images.length) {
          this.$message.error('这已经是最后一张图片了！')
          return
        }
        i = i - 1
        this.index = i
        this.imageUrl = '/show/' + this.taskWorker.taskId + '/' + this.taskWorker.images[i].filename
        this.tags = this.taskWorker.images[i].tags
      },
      confirmOverall: function () {
        if (this.overall.trim() === '') {
          this.$message.error('空白标注，请重新输入')
        } else {
          this.tags.push({
            tagType: 0,
            id: 0,
            descType: 0,
            singleDesc: this.overall
          })
          this.$message.success('标注成功')
          this.refreshQuestionNo(this.index)
        }
      },
      adjZIndex: function () {
        let tags = Array.from(document.getElementsByClassName('tag'))
        let squares = []
        tags.forEach((item) => {
          let n = item.offsetWidth * item.offsetHeight
          squares.push(n)
        })
        tags.forEach((item) => {
          let n = item.offsetWidth * item.offsetHeight
          let num = 0
          squares.forEach((item) => {
            if (n <= item) {
              num++
            }
          })
          item.style.zIndex = num
        })
      },
      selectMode: function (mode) {
        this.drawMode = Number(mode)
      },
      resizeImg: function () {
        let naturalWidth = this.$refs.img.naturalWidth
        let naturalHeight = this.$refs.img.naturalHeight
        let t = this.$refs.tagContent
        this.$refs.imgBody.style.height = naturalHeight * t.offsetWidth / naturalWidth + 'px'
        // alert(this.$refs.imgBody.style.height)
      },
      clickTag: function (e, i, item) {
        // 右键点击，显示菜单
        if (e.which === 3) {
          this.$refs.dropMenu.style.left = e.clientX + 'px'
          this.$refs.dropMenu.style.top = e.clientY + 'px'
          this.showMenu = true
          this.activeIndex = i
          return
        }
        isMouseDown = true
        this.showMenu = false
        this.showShade = true
        moveTag = true
        this.activeIndex = i
        let t = this.$refs.tagContent
        obj = {
          x: this._offset(t).left,
          y: this._offset(t).top,
          cx: e.clientX,
          cy: e.clientY,
          w: t.offsetWidth,
          h: t.offsetHeight
        }
        // Object.assign(oldTag, item)
        oldTag.startPosition.x = item.startPosition.x
        oldTag.startPosition.y = item.startPosition.y
        oldTag.endPosition.x = item.endPosition.x
        oldTag.endPosition.y = item.endPosition.y
        tagItem = item
      },
      move: function (e) {
        if (this.drawMode === 1) {
          this.moveTag(e)
        } else {
          this.movePoint(e)
        }
      },
      movePoint: function (e) {
        if (isMouseDown && Number(this.drawMode) === 2) {
          if (!newTag) {
            this.tags.push({
              tagType: 2,
              startPosition: {
                x: 0,
                y: 0
              },
              endPosition: {
                x: 0,
                y: 0
              },
              penPoints: [],
              descType: 0,
              singleDesc: '',
              mapDesc: {}
            })
            newTag = true
            this.activeIndex = this.tags.length - 1
          }
          // 禁止showShade
          this.showShade = false
          let t = this.$refs.tagContent
          obj = {
            x: this._offset(t).left,
            y: this._offset(t).top,
            cx: e.clientX,
            cy: e.clientY,
            w: t.offsetWidth,
            h: t.offsetHeight
          }
          let item = {
            x: (e.clientX - obj.x - this.$refs.colLeft.offsetWidth) / obj.w,
            y: (e.clientY - obj.y) / obj.h,
            tagType: this.drawMode
          }

          this.tags[this.activeIndex].penPoints.push(item)
        }
      },
      moveTag: function (e) {
        if (isMouseDown && moveTag) {
          tagItem.startPosition.x = oldTag.startPosition.x + (e.clientX - obj.cx) / obj.w
          tagItem.startPosition.y = oldTag.startPosition.y + (e.clientY - obj.cy) / obj.h
          tagItem.endPosition.x = oldTag.endPosition.x + (e.clientX - obj.cx) / obj.w
          tagItem.endPosition.y = oldTag.endPosition.y + (e.clientY - obj.cy) / obj.h
          if (tagItem.startPosition.y < 0) {
            tagItem.startPosition.y = 0
          }
          if (tagItem.startPosition.x < 0) {
            tagItem.startPosition.x = 0
          }
          if (tagItem.endPosition.y < 0) {
            tagItem.endPosition.y = 0
          }
          if (tagItem.startPosition.x < 0) {
            tagItem.endPosition.x = 0
          }
          if (tagItem.endPosition.y > 1) {
            tagItem.endPosition.y = 1
          }
          if (tagItem.endPosition.x > 1) {
            tagItem.endPosition.x = 1
          }
          if (tagItem.startPosition.y > 1) {
            tagItem.startPosition.y = 1
          }
          if (tagItem.startPosition.x > 1) {
            tagItem.startPosition.x = 1
          }
        } else if (moveSpot && isMouseDown) {
          if (spotIndex === 1) {
            if (oldTag.startPosition.x > oldTag.endPosition.x) {
              tagItem.endPosition.x = oldTag.endPosition.x + (e.clientX - obj.cx) / obj.w
            } else {
              tagItem.startPosition.x = oldTag.startPosition.x + (e.clientX - obj.cx) / obj.w
            }
            if (oldTag.startPosition.y > oldTag.endPosition.y) {
              tagItem.endPosition.y = oldTag.endPosition.y + (e.clientX - obj.cx) / obj.w
            } else {
              tagItem.startPosition.y = oldTag.startPosition.y + (e.clientX - obj.cx) / obj.w
            }
          } else if (spotIndex === 2) {
            if (oldTag.startPosition.y > oldTag.endPosition.y) {
              tagItem.endPosition.y = oldTag.endPosition.y + (e.clientY - obj.cy) / obj.h
            } else {
              tagItem.startPosition.y = oldTag.startPosition.y + (e.clientY - obj.cy) / obj.h
            }
          } else if (spotIndex === 3) {
            if (oldTag.startPosition.x > oldTag.endPosition.x) {
              tagItem.startPosition.x = oldTag.startPosition.x + (e.clientX - obj.cx) / obj.w
            } else {
              tagItem.endPosition.x = oldTag.endPosition.x + (e.clientX - obj.cx) / obj.w
            }
            if (oldTag.startPosition.y > oldTag.endPosition.y) {
              tagItem.endPosition.y = oldTag.endPosition.y + (e.clientY - obj.cy) / obj.h
            } else {
              tagItem.startPosition.y = oldTag.startPosition.y + (e.clientY - obj.cy) / obj.h
            }
          } else if (spotIndex === 4) {
            if (oldTag.startPosition.x > oldTag.endPosition.x) {
              tagItem.endPosition.x = oldTag.endPosition.x + (e.clientX - obj.cx) / obj.w
            } else {
              tagItem.startPosition.x = oldTag.startPosition.x + (e.clientX - obj.cx) / obj.w
            }
          } else if (spotIndex === 5) {
            if (oldTag.startPosition.x > oldTag.endPosition.x) {
              tagItem.startPosition.x = oldTag.startPosition.x + (e.clientX - obj.cx) / obj.w
            } else {
              tagItem.endPosition.x = oldTag.endPosition.x + (e.clientX - obj.cx) / obj.w
            }
          } else if (spotIndex === 6) {
            if (oldTag.startPosition.x > oldTag.endPosition.x) {
              tagItem.endPosition.x = oldTag.endPosition.x + (e.clientX - obj.cx) / obj.w
            } else {
              tagItem.startPosition.x = oldTag.startPosition.x + (e.clientX - obj.cx) / obj.w
            }
            if (oldTag.startPosition.y > oldTag.endPosition.y) {
              tagItem.startPosition.y = oldTag.startPosition.y + (e.clientY - obj.cy) / obj.h
            } else {
              tagItem.endPosition.y = oldTag.endPosition.y + (e.clientY - obj.cy) / obj.h
            }
          } else if (spotIndex === 7) {
            if (oldTag.startPosition.y > oldTag.endPosition.y) {
              tagItem.startPosition.y = oldTag.startPosition.y + (e.clientY - obj.cy) / obj.h
            } else {
              tagItem.endPosition.y = oldTag.endPosition.y + (e.clientY - obj.cy) / obj.h
            }
          } else if (spotIndex === 8) {
            if (oldTag.startPosition.x > oldTag.endPosition.x) {
              tagItem.startPosition.x = oldTag.startPosition.x + (e.clientX - obj.cx) / obj.w
            } else {
              tagItem.endPosition.x = oldTag.endPosition.x + (e.clientX - obj.cx) / obj.w
            }
            if (oldTag.startPosition.y > oldTag.endPosition.y) {
              tagItem.startPosition.y = oldTag.startPosition.y + (e.clientY - obj.cy) / obj.h
            } else {
              tagItem.endPosition.y = oldTag.endPosition.y + (e.clientY - obj.cy) / obj.h
            }
          }
        } else if (isMouseDown) {
          if (!newTag) {
            let t = this.$refs.tagContent
            obj = {
              x: this._offset(t).left,
              y: this._offset(t).top,
              cx: e.clientX,
              cy: e.clientY,
              w: t.offsetWidth,
              h: t.offsetHeight
            }
            let item = {
              id: 0,
              startPosition: {
                x: (e.clientX - obj.x - this.$refs.colLeft.offsetWidth) / obj.w,
                y: (e.clientY - obj.y) / obj.h
              },
              endPosition: {
                x: (e.clientX - obj.x - this.$refs.colLeft.offsetWidth) / obj.w,
                y: (e.clientY - obj.y) / obj.h
              },
              tagType: this.drawMode
            }
            if (this.taskWorker.taskType === 0) {
              item.descType = 1
              item.mapDesc = {}
              this.taskWorker.labels.forEach((label) => {
                item.mapDesc[label] = ''
              })
            }
            this.tags.push(item)
            this.deleteToolTips.push(false)
            tagItem = item
            newTag = true
          } else {
            this.activeIndex = this.tags.length - 1
            this.showShade = true
            this.tags[this.tags.length - 1].endPosition.x = this.tags[this.tags.length - 1].startPosition.x + (e.clientX - obj.cx) / obj.w
            this.tags[this.tags.length - 1].endPosition.y = this.tags[this.tags.length - 1].startPosition.y + (e.clientY - obj.cy) / obj.h
          }
        }
      },
      clickSpot: function (e, i, item) {
        isMouseDown = true
        moveSpot = true
        this.showShade = true
        let t = this.$refs.tagContent
        obj = {
          x: this._offset(t).left,
          y: this._offset(t).top,
          cx: e.clientX,
          cy: e.clientY,
          w: t.offsetWidth,
          h: t.offsetHeight
        }
        oldTag.startPosition.x = item.startPosition.x
        oldTag.startPosition.y = item.startPosition.y
        oldTag.endPosition.x = item.endPosition.x
        oldTag.endPosition.y = item.endPosition.y
        spotIndex = i
        tagItem = item
      },
      finishTag: function (e) {
        isMouseDown = false
        moveTag = false
        moveSpot = false
        if (newTag) {
          this.showEdit = true
          newTag = false
          this.activeIndex = this.tags.length - 1
        }
        this.showShade = false
        this.adjZIndex()
      },
      clickContent: function () {
        isMouseDown = true
        this.activeIndex = -1
        this.showMenu = false
      },
      cancelConfirm: function () {
        this.showEdit = false
        if (isEdit) {
          isEdit = false
          console.log(oldTag.mapDesc)
          this.tags[this.activeIndex].mapDesc = oldTag.mapDesc
        } else {
          this.tags.splice(this.activeIndex, 1)
          this.activeIndex = -1
        }
      },
      confirm: function () {
        if (this.taskWorker.taskType === 0) {
        } else {
          if (this.singleDesc.trim() === '') {
            this.$message.error('空白的标注，请重新输入')
            return
          }
          this.tags[this.activeIndex].singleDesc = this.singleDesc
          this.tags[this.activeIndex].descType = 0
          this.singleDesc = ''
        }
        this.tags[this.activeIndex].id = 0
        this.showEdit = false
        this.refreshQuestionNo(this.index)
        isEdit = false
      },
      deleteTag: function (i = this.activeIndex) {
        this.deleteToolTips[i] = false
        this.tags.splice(i, 1)
        this.showMenu = false
        this.refreshQuestionNo(this.index)
        this.activeIndex = -1
      },
      editTag: function (i = this.activeIndex) {
        isEdit = true
        this.activeIndex = i
        this.showEdit = true
        if (this.taskWorker.taskType !== 0) {
          this.singleDesc = this.tags[i].singleDesc
        } else {
          oldTag = JSON.parse(JSON.stringify(this.tags[i]))
        }
        this.showMenu = false
      },
      refreshQuestionNo: function (index) {
        this.$refs.questionNo.pass(2 - (this.tags.length > 0), index)
      },
      _offset: function (target) {
        let top, left
        top = 0
        left = 0

        while (target.offsetParent) {
          top += target.offsetTop
          left += target.offsetLeft
          target = target.offsetParent
        }

        return {
          top: top,
          left: left
        }
      },
      _cleanExitQuestion: function () {
        window.onbeforeunload = function (e) {
        }
        document.onkeyup = function (e) {
        }
      },
      getBackgroundColor: function (index) {
        let color = this.tags[index].color
        if (color === undefined || color === null) {
          return 'rgba(255,0,0,0.3)'
        }
        if (color === 'red') {
          return 'rgba(255,0,0,0.3)'
        } else if (color === 'green') {
          return 'rgba(0,255,0,0.3)'
        } else if (color === 'blue') {
          return 'rgba(0,0,255,0.3)'
        }
      }
    }
  }
</script>

<style lang="stylus">
  #tag-board
    width 100%
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
      min-height 100vh
      width 200px
      box-sizing border-box
      border-right 2px solid rgba(7, 17, 27, 0.6)
      background-color #f6f6f7
      .content
        padding 20px 25px 10px 25px
        .el-input, textarea, .el-button
          margin-bottom: 10px
        .el-button
          font-size: 12px
        .overall-desc
          margin-bottom 10px
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
            &:last-child
              border-bottom 0
            .icon-group
              position: absolute
              top: 15px
              right: 0
              i
                &:hover
                  color: #409eff
              .el-icon-edit
                margin-right 7px
    .col-mid
      position fixed
      width: calc(100% - 450px)
      height: 100%
      .click-shade
        position fixed
        top 0
        left 200px
        width: calc(100% - 450px)
        height: 100%
        background-color transparent
      .btn
        cursor pointer
      .img-box
        position fixed
        top 0
        left: 200px
        width: calc(100% - 450px)
        height: 100%
        background-color rgb(155, 155, 155)
        .img-body-wrapper
          display flex
          justify-content center
          align-items center
          height: 100%
          .img-body
            position relative
            overflow hidden
            display inline-block
            width: 100%
            height 300px
            .img
              position absolute
              width 100%
              height 100%
            .tag-content
              position: absolute
              width: 100%
              height: 100%
              z-index: 100
              cursor crosshair
              .tag-wrapper
                position relative
                width 100%
                height 100%
                .tag
                  position absolute
                  cursor move
                  padding 1px
                  border 1px dashed #959595
                  .tag-text
                    position absolute
                    left: 0
                    top: 0
                    width: 100%
                    height: 100%
                    display flex
                    justify-content center
                    align-items center
                    color #fff
                    font-size 16px
                  .spot-wrapper
                    .spot
                      visibility hidden
                      display inline-block
                      position absolute
                      width: 9px
                      height 9px
                      cursor default !important
                      border 1px solid #fff
                      border-radius 50%
                      background-color #959595
                      &:nth-child(1)
                        top: -6px
                        left -6px
                      &:nth-child(2)
                        top: -6px
                        left: 50%
                        transform translateX(-5px)
                      &:nth-child(3)
                        top: -6px
                        right: -6px
                      &:nth-child(4)
                        top: 50%
                        left -6px
                        transform translateY(-5px)
                      &:nth-child(5)
                        top: 50%
                        right: -6px
                        transform translateY(-5px)
                      &:nth-child(6)
                        bottom: -6px
                        left -6px
                      &:nth-child(7)
                        bottom: -6px
                        left: 50%
                        transform translateX(-5px)
                      &:nth-child(8)
                        bottom: -6px
                        right: -6px
                  &.active
                    .spot
                      visibility visible
                  .em-wrapper
                    .em
                      position absolute
                      background-color rgba(0, 0, 0, 0.4)
                      z-index: -1
                      width: 50000px
                      height 50000px
                      &:nth-child(1)
                        bottom 100%
                        transform translateX(-10000px)
                      &:nth-child(2)
                        height: 100%
                        left 100%
                        transform translateY(-1px)
                      &:nth-child(3)
                        top: 100%
                        transform translateX(-1px)
                      &:nth-child(4)
                        right: 100%
                        transform translateY(-1px)
              .point-tag-wrapper
                position relative
                  width 100%
                  height 100%
                .point
                  position absolute
                  background: #000000
                  border-radius 50%
                  width: 2px
                  height 2px
                  z-index: 100000
        .drop-menu
          position fixed
          width: 100px
          z-index: 100000
          text-align center
          background-color #fff
          box-shadow 0 16px 24px 2px rgba(0, 0, 0, .06), 0 6px 30px 5px rgba(0, 0, 0, .12), 0 8px 10px -7px rgba(0, 0, 0, .2)
          .btn
            display inline-block
            width: 100%
            height: 35px
            line-height 35px
            border-radius 0
          .btn:hover
            background-color #aaa
      .input-box-wrapper
        position fixed
        left 0
        top 0
        width 100%
        height 100%
        display flex
        justify-content center
        z-index: 10000
        animation-duration: 0.5s;
        .input-box
          width 300px
          margin: auto
          .header
            width 100%
            height 40px
            padding 0 20px
            line-height 40px
            background-color #333
            color: #fff
            .title
              float left
              margin 0
              padding 0
              background: none
            .close-btn
              float right
              line-height 40px
              color #aaa
              &:hover
                color: #fff
          .content
            width: 100%
            padding 10px 20px 25px 20px
            background-color #fff
            .input
              width inherit
            .el-input
              margin 20px 0
            .my-btn-group
              display flex
              justify-content center
              .btn
                width: 80px
              .confirm-btn
                margin-right 20px
    .col-right
      position fixed
      right 0
      top: 0
      width 250px
      height: 100%
      min-height 100vh
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
