<template>
  <div class='topic'>
    <div>{{ text }}</div>
  </div>
</template>

<script type="text/ecmascript-6">
  export default {
    name: 'topic',
    props: {
      text: {
        type: String
      }
    }
  }
</script>

<style lang="stylus">
  .topic
    display inline-block
    padding 5px 8px
    background-color #f6f6f7
    border-radius 15px
    color #888888
    font-size 12px
</style>
