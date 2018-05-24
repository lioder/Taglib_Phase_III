<template>
  <div class='download-btn' style="border: 2px solid #1ECD97" ref="downloadBtn">
    <svg style="display:none" :style="{display:'block'}" id="progress-button" @click="handleClick" width="100%"
         height="40px" viewBox="0 0 300 70" version="1.1" xmlns="http://www.w3.org/2000/svg"
         xmlns:xlink="http://www.w3.org/1999/xlink">
      <path v-if="(status === 1) || (status === 2) || (status === 3)" stroke-width="3"
            d="M100.1,65h-0.1C83.4,65,70,51.6,70,35.1v-0.1C70,18.4,83.4,5,99.9,5h0.1C116.6,5,130,18.4,130,34.9v0.1C130,51.6,116.6,65,100.1,65z"></path>
      <circle v-if="showRing" :stroke-dashoffset="171 - (171*downloadProgress)" stroke-dasharray="171"
              transform="rotate(270, 100, 35)" stroke="#1ECD97" fill="none" stroke-width="5" cx="100" cy="35"
              r="30.1"/>
      <text v-if="showText" stroke="none" fill="#1ECD97" transform="matrix(1 0 0 1 73.0725 42.5359)" font-size="24px">
        下载标注数据
      </text>
    </svg>
  </div>
</template>

<script type="text/ecmascript-6">
  export default {
    name: 'download-btn',
    data () {
      return {
        status: 0, // 0:ready 1:clicked 2:downloading
        clickable: true,
        showText: true,
        showRing: false,
        downloadProgress: 0
      }
    },
    methods: {
      handleClick () {
        this.$refs.downloadBtn.style.border = 'none'
        if (this.status === 1) {
          return
        }
        if (this.status === 0) {
          this.status = 1
          this.showText = false
          setTimeout(() => {
            this.status = 2
            this.downloadProgress = 0
            this.showRing = true
            this.handleIncreaseProgress()
          }, 300)
        }
      },
      reverting () {
        this.status = 0
        this.showRing = false
        this.downloadProgress = 0
        this.status = 0
        this.showText = true
        this.$refs.downloadBtn.style.border = '2px solid #1ECD97'
      },
      handleIncreaseProgress () {
        this.progressTimers = [
          setTimeout(() => {
            this.handleChangeProgress(0.25)
          }, 1000),
          setTimeout(() => {
            this.handleChangeProgress(0.75)
          }, 2000),
          setTimeout(() => {
            this.handleChangeProgress(1)
          }, 3000),
          setTimeout(() => {
            this.reverting()
          }, 4000)
        ]
      },
      handleChangeProgress (progress) {
        this.downloadProgress = progress
      }
    }
  }
</script>

<style lang="stylus">
  .download-btn
    margin-top 25px
    border-radius 70px
    svg
      cursor: pointer
      transition: all 300ms
    svg:active
      transform: scale(0.9)
    svg > path
      transition: all 300ms
      stroke: #3384C6
      fill: none
    circle
      transition: all 600ms

</style>
