<template>
  <el-form class="login-form">
    <el-form-item>
      <el-input v-model="name" class="username-input" placeholder="手机号/邮箱">
        <i class="iconfont" slot="prefix">&#xe65a;</i>
      </el-input>
    </el-form-item>
    <el-form-item>
      <el-input v-model="password" class="password-input" placeholder="密码" type="password" @keyup.enter.native="login">
        <i class="iconfont" slot="prefix">&#xe62a;</i>
      </el-input>
    </el-form-item>
    <el-form-item>
      <el-button class="login-btn" @click="login" type="primary" round>登录</el-button>
    </el-form-item>
  </el-form>
</template>

<script type="text/ecmascript-6">
  import Cookie from 'js-cookie'
  import {mapMutations} from 'vuex'

  export default {
    name: 'signin',
    data () {
      return {
        name: '',
        password: ''
      }
    },
    methods: {
      ...mapMutations({
        userLogin: 'login'
      }),
      login: function () {
        this.$ajax.post('/user/login', {
          name: this.name,
          password: this.password
        }).then((response) => {
            let data = response.data
            if (data.status === 0) {
              Cookie.set('login-token', data.result)
              this.$ajax.get('/user/loginByToken', {
                params: {
                  name: this.name,
                  password: this.password
                }
              }).then((response) => {
                response = response.data
                if (response.code === 0) {
                  let user = response.data
                  let payload = {
                    userId: user.id,
                    username: user.username,
                    userType: user.userType,
                    avatar: user.avatar,
                    applyState: user.applyState
                  }
                  this.userLogin(payload)

                  switch (this.$store.getters.userType) {
                    // 是工人
                    case 0:
                      this.$router.push('/home-view')
                      break
                    // 是发布者
                    case 1:
                      this.$router.push('/myTasks')
                      break
                    // 是管理员
                    case 2:
                      this.$router.push('/admin')
                      break
                  }
                }
              })
            } else {
              this.$message.error('登录失败')
            }
          }
        )
      }
    }
  }
</script>

<style lang="stylus">
  .login-form
    margin 0 auto
    padding 2% 5%
    background #fff
    .login-btn
      width 100%
</style>
