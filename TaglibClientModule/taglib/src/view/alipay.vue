<template>
  <div class='alipay'>
    <div ref="content">hhh</div>
  </div>
</template>

<script type="text/ecmascript-6">
  import {mapMutations} from 'vuex'
  export default {
    name: 'alipay',
    mounted () {
      // let totalAmount = this.getQueryString('total_amount')
      let outTradeNo = this.getQueryString('out_trade_no')
      this.$ajax.get('/alipay/detail/' + outTradeNo).then((res) => {
        let result = res.data
        if (result.code === 0) {
          let order = result.data

          this.$ajax.get('/user/info/' + order.userId).then((res) => {
            let result = res.data
            if (result.code === 0) {
              let user = result.data
              let payload = {
                userId: user.id,
                username: user.username,
                userType: user.userType,
                avatar: user.avatar
              }
              this.userLogin(payload)
              this.$router.push('/user')
            }
          })
        }
      })
    },
    methods: {
      ...mapMutations({
        userLogin: 'login'
      }),
      getQueryString: function (name) {
        let reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)")
        let url = window.location.href
        let r = url.substr(url.indexOf('?')).match(reg)
        if (r != null) {
          return decodeURIComponent(r[2])
        } else {
          return null
        }
      }
    }
  }
</script>

<style lang="stylus">

</style>
