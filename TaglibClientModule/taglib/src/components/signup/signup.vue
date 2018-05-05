<template>
  <el-form class="sign-form" status-icon :rules="rules" ref="signUpForm" :model="signUpForm">
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
    <el-form-item label="请选择用户角色" prop="userType">
      <el-radio-group v-model="signUpForm.userType" class="choice">
        <el-radio :label="0">众包工人</el-radio>
        <el-radio :label="1">众包发起者</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item>
      <el-button class="sign-btn" @click="sign" type="primary" round>注册</el-button>
    </el-form-item>
  </el-form>
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
        signUpForm: {
          username: '',
          password: '',
          passwordAgain: '',
          phone: '',
          email: '',
          userType: 0
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
        } else {
          this.$message.error('注册信息填写不完整！')
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
  .sign-form
    margin 0 auto
    padding 3% 5%
    background #fff
    .sign-btn
      width: 100%
</style>
