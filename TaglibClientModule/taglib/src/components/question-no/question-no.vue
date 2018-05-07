<template>
  <div class="question-no">
    <div v-for="(item, index) in questions" class="no-block" @click="select(index)" :key="index"
         :class="{'active':active === index, 'pass':item.state === 1, 'reject': item.state === 0}">{{ index+1 }}
    </div>
  </div>
</template>

<script type="text/ecmascript-6">
  const undone = 2
  export default {
    name: 'question-no',
    data () {
      return {
        active: 0,
        size: 0,
        questions: []
      }
    },
    methods: {
      select: function (index) {
        this.active = index
        this.$emit('change-active', index + 1)
      },
      setSize: function (size) {
        this.size = size
        this.refresh()
      },
      pass: function (type, index) {
        this.questions[index].state = type
      },
      result: function () {
        let count = 0
        let finished = true
        this.questions.forEach((item) => {
          if (item.state === 1) {
            count++
          }
          if (item.state === undone) {
            finished = false
          }
        })
        return finished ? count / this.size : null
      },
      refresh: function () {
        this.questions = []
        for (let i = 0; i < this.size; i++) {
          this.questions.push({
            state: undone
          })
        }
      }
    },
    mounted () {
      this.refresh()
    }
  }
</script>

<style lang="stylus">
  .question-no
    width: 100%
    display flex
    flex-wrap wrap
    justify-content flex-start
    .no-block
      flex: 0 0 20px
      margin 0px 5px 10px 5px
      background-color #f6f6f7
      border 1.5px solid rgba(0, 17, 27, 0.3)
      border-radius 5px
      text-align center
      font-size 12px
      color #666
      cursor pointer
      &.active
        background-color #409eff
        border 1.5px solid #409eff
        color: #fff
      &.pass
        border 1.5px solid #13ce66
      &.reject
        border-color #ff4d51
</style>
