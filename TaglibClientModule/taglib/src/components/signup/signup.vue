<template>
  <div>
    <el-steps :space="200" :active="step" finish-status="success" :align-center="true" :simple="true">
      <el-step title="步骤 1"></el-step>
      <el-step title="步骤 2"></el-step>
    </el-steps>
    <el-form class="sign-form" status-icon :rules="rules" ref="signUpForm" :model="signUpForm" v-show="step === 1">
      <el-form-item prop="username">
        <el-input v-model="signUpForm.username" placeholder="请输入用户名">
          <i class="iconfont" slot="prefix">&#xe65a;</i>
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input v-model="signUpForm.password" placeholder="请输入密码" type="password">
          <i class="iconfont" slot="prefix">&#xe62a;</i>
        </el-input>
      </el-form-item>
      <el-form-item prop="passwordAgain">
        <el-input v-model="signUpForm.passwordAgain" placeholder="请再次输入密码" type="password">
          <i class="iconfont" slot="prefix">&#xe62a;</i>
        </el-input>
      </el-form-item>
      <el-form-item prop="phone">
        <el-input v-model="signUpForm.phone" placeholder="请输入手机号码">
          <i class="iconfont" slot="prefix">&#xe64f;</i>
        </el-input>
      </el-form-item>
      <el-form-item prop="email">
        <el-input v-model="signUpForm.email" placeholder="请输入邮件地址">
          <i class="iconfont" slot="prefix">&#xe6ab;</i>
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button class="next-btn" @click="nextStep" type="primary" round>下一步</el-button>
      </el-form-item>
    </el-form>
    <div class="user-detail-info-card" v-show="step === 2">
      <el-form :model="signUpForm">
        <el-form-item label="我是">
          <el-radio-group v-model="signUpForm.userType" class="choice">
            <el-radio :label="0">众包工人</el-radio>
            <el-radio :label="1">众包发起者</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <div class="topic-title" style="color: #606266">我的兴趣是</div>
          <ul class="topic-content">
            <li v-for="(topic, index) in topicList" :key="index" class="topic-tag"
                ref="topics">
              <el-tag type="info" @click.native="chooseTopic($event)">{{ topic }}</el-tag>
            </li>
          </ul>
        </el-form-item>
        <el-form-item>
          <el-button class="sign-btn" @click="sign" type="primary" round>注册</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script type="text/ecmascript-6">
  export default {
    name: 'sign',
    data () {
      var validatePass = (rule, value, callback) => {
        if (value === '') {
          callback(new Error('请输入密码'))
        } else {
          if (this.signUpForm.password !== '') {
            this.$refs.signUpForm.validateField('passwordAgain')
          }
          callback()
        }
      }
      var validatePass2 = (rule, value, callback) => {
        if (value === '') {
          callback(new Error('请再次输入密码'))
        } else if (value !== this.signUpForm.password) {
          callback(new Error('两次输入密码不一致!'))
        } else {
          callback()
        }
      }
      return {
        step: 1,
        topicList: [
          '动物', '植物', '车辆', '船舶', '运动', '美食', 'IT', '机械', '医学', '人类'
        ],
        signUpForm: {
          username: '',
          password: '',
          passwordAgain: '',
          phone: '',
          email: '',
          userType: 0,
          topics: []
        },
        rules: {
          username: [{
            required: true,
            message: '请输入用户名',
            trigger: 'blur'
          }, {
            min: 2,
            max: 10,
            message: '长度在 2 到 10 个字符'
          }, {
            pattern: /^[a-zA-Z](\w){1,9}$/,
            message: '以字母开头，长度在2-10之间， 只能包含字符、数字和下划线'
          }],
          password: [
            {validator: validatePass, trigger: 'blur'}
          ],
          passwordAgain: [
            {validator: validatePass2, trigger: 'blur'}
          ],
          email: [
            {required: true, message: '请输入邮箱地址', trigger: 'blur'},
            {type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur,change'}
          ],
          phone: [{
            required: true,
            message: '请输入手机号码',
            trigger: 'blur'
          },
            {
              validator: function (rule, value, callback) {
                if (/^1[34578]\d{9}$/.test(value) === false) {
                  callback(new Error('请输入正确的手机号'))
                } else {
                  callback()
                }
              },
              trigger: 'blur'
            }
          ],
          userType: [
            {required: true}
          ]
        }
      }
    },
    methods: {
      chooseTopic: function (event) {
        let tag = event.target
        let tagContent = tag.textContent
        let contains = this.signUpForm.topics.some(s => {
          return s === tagContent
        })
        if (contains) {
          this.signUpForm.topics.splice(this.signUpForm.topics.indexOf(tagContent), 1)
          tag.style.background = 'hsla(220,4%,58%,.1)'
          tag.style.borderColor = 'hsla(220,4%,58%,.2)'
          tag.style.color = '#909399'
        } else {
          this.signUpForm.topics.push(tagContent)
          tag.style.background = 'rgba(64,158,255,.1)'
          tag.style.border = '1px solid rgba(64,158,255,.2)'
          tag.style.color = '#409eff'
        }
      },
      nextStep: function () {
        if (this.validate()) {
          this.step = 2
        } else {
          this.$message.error('先填完步骤一信息，才能进入步骤二哦！')
        }
      },
      sign: function () {
        if (this.validate()) {
          this.$ajax.post('/user/sign', this.signUpForm).then((response) => {
            switch (response.data.code) {
              case 0:
                this.$router.push('/home')
                this.$message.success('注册成功')
                break
              case 6:
              case 7:
                this.$message.error('注册失败!' + response.message)
                break
            }
          })
        }
      },
      validate: function () {
        let t
        for (t in this.signUpForm) {
          if (this.signUpForm[t] === '' && t !== 'userType') {
            return false
          }
        }
        return true
      }
    }
  }
</script>

<style lang="stylus">
  .el-steps
    height 10px
    width 75%
    margin 0 auto
    .el-step__main
      .el-step__title
        font-size 14px
      .el-step__arrow::before
        transform rotate(-45deg) translateX(3px)
        height 10px
      .el-step__arrow::after
        transform rotate(45deg) translateX(2px)
        height 10px

  .sign-form
    margin 0 auto
    padding 3% 5%
    background #fff

  .sign-btn, .next-btn
    width: 100%

  .user-detail-info-card
    margin-top 15px
    padding 0 15px
    .el-form-item
      margin-bottom 5px
    .topic-content
      transform translateX(-10px)
      .topic-tag
        display inline-block
        margin 0 8px 10px 8px
        cursor pointer
        .el-tag--info
          text-align center
          min-width 50px

  .sign-btn
    margin 20px 0
</style>
