<template>
  <div class="pagination">
    <el-pagination
      ref="pagination"
      background
      layout="prev, pager, next"
      :page-size="pageSize"
      :current-page="page"
      :total="totalItemNum"
      prev-text="上一页"
      next-text="下一页"
      @current-change="changePage">
    </el-pagination>
  </div>
</template>

<script type="text/ecmascript-6">
  export default {
    name: 'pagination',
    props: {
      pageSize: {
        type: Number
      },
      page: {
        type: Number
      },
      totalItemNum: {
        type: Number
      }
    },
    methods: {
      changePage: function (page) {
        this.$emit('changePage', page)
      }
    }
  }
</script>

<style lang="stylus">
  .pagination
    text-align center
    .is-background
      .btn-next, .btn-prev
        margin 0 8px
        padding 0 10px
        border 1px solid rgba(7, 17, 27, 0.5)
        &.disabled
          border 1px solid #c0c4cc
      .el-pager li

        border 1px solid rgba(7, 17, 27, 0.5)
      /*&.active*/
        /*border none*/
</style>
