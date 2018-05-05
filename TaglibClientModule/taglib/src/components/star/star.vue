<template>
  <div class="star" :class="'star-'+size">
    <span class="star-item" v-for="itemClass in itemClasses" :class="itemClass" :key="itemClass.id"></span>
  </div>
</template>

<script type="text/ecmascript-6">
  const num = 5
  export default {
    name: 'star',
    props: {
      size: {
        type: Number,
        default: 48
      },
      score: {
        type: Number,
        default: 5
      }
    },
    computed: {
      itemClasses: function () {
        const CLS_ON = 'on'
        const CLS_OFF = 'off'
        const CLS_HALF = 'half'
        let itemClasses = []
        let score = Math.floor(this.score * 2) / 2
        for (let i = 0; i < score; i++) {
          itemClasses.push(CLS_ON)
        }
        const hasDecimal = (score % 1) !== 0
        if (hasDecimal) {
          itemClasses.push(CLS_HALF)
        }
        while (itemClasses.length < num) {
          itemClasses.push(CLS_OFF)
        }
        return itemClasses
      }
    }

  }
</script>

<style lang="stylus" rel="stylesheet/stylus">
  @import "../../common/stylus/mixin.styl"
  .star
    font-size: 0
    .star-item
      display: inline-block
    &.star-48
      span
        margin-right: 20px
        padding: 2px 0px
        width: 20px
        height: 19px
        background-size: 20px 19px
        background-repeat: no-repeat
        &:last-child
          margin-right 0
      .on
        bg-img('star48_on')
      .off
        bg-img('star48_off')
      .half
        bg-img('star48_half')
    &.star-36
      span
        margin-right: 8px
        padding: 2px 0
        width: 15px
        height: 14.5px
        background-size: 15px 14.5px
        background-repeat: no-repeat
        &:last-child
          margin-right 0
      .on
        bg-img('star36_on')
      .off
        bg-img('star36_off')
      .half
        g-img('star36_half')
    &.star-24
      span
        margin-right: 4px
        padding: 2px 0
        width: 10px
        height: 9.5px
        background-size: 10px 9.5px
        background-repeat: no-repeat
        &:last-child
          margin-right 0
      .on
        bg-img('star24_on')
      .off
        bg-img('star24_off')
      .half
        g-img('star24_half')

</style>
