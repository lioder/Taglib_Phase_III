webpackJsonp([1],{C7oE:function(t,e){},CkaO:function(t,e){},NHnr:function(t,e,s){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var a=s("7+uW"),i={render:function(){var t=this.$createElement,e=this._self._c||t;return e("div",{attrs:{id:"app"}},[e("router-view",{staticClass:"main-view"})],1)},staticRenderFns:[]};var n=s("VU/8")({name:"App",data:function(){return{}},methods:{}},i,!1,function(t){s("qrMk")},null,null).exports,o=s("/ocq"),l={render:function(){var t=this.$createElement,e=this._self._c||t;return e("div",{staticClass:"login"},[e("div",{staticClass:"form"},[e("div",{staticClass:"tab-head",attrs:{slot:"head"},slot:"head"},[e("router-link",{attrs:{to:"/login"}},[this._v("登录")]),this._v(" "),e("router-link",{attrs:{to:"/sign"}},[this._v("注册")])],1),this._v(" "),e("keep-alive",[e("router-view")],1)],1)])},staticRenderFns:[]};var r=s("VU/8")({name:"login"},l,!1,function(t){s("CkaO")},null,null).exports,c={render:function(){var t=this.$createElement,e=this._self._c||t;return e("div",{staticClass:"home"},[e("el-menu",{staticClass:"head-nav",attrs:{mode:"horizontal","text-color":"#373F52","default-active":"1"}},[e("el-menu-item",{attrs:{index:"1"}},[e("router-link",{attrs:{to:"/tasks"}},[this._v("任务中心")])],1),this._v(" "),e("el-menu-item",{attrs:{index:"2"}},[e("router-link",{attrs:{to:"/publish"}},[this._v("发布任务")])],1)],1),this._v(" "),e("router-view",{staticClass:"home-view"})],1)},staticRenderFns:[]};var u=s("VU/8")({name:"home",data:function(){return{}},created:function(){},methods:{}},c,!1,function(t){s("C7oE")},null,null).exports,p=s("woOf"),d=s.n(p),v=s("Zx67"),g=s.n(v),h=s("wxAW"),m=s.n(h),f=s("zwoO"),_=s.n(f),k=s("Pf15"),b=s.n(k),y=s("Zrlr"),x=s.n(y),w={name:"board",props:{task:{type:Object}},data:function(){return{taskId:0,taskType:0,index:0,questions:[],filename:"",desc:"",labels:[],overall:"",scene:null,canvas:{},ctx:{},color:"#FF0000",selectUtil:-1,drawing:!1,showTags:!0,tag:{id:0,tagType:0,penPoints:[],startPosition:{x:0,y:0},endPosition:{x:0,y:0},descType:0,mapDesc:{},singleDesc:""},tags:[]}},watch:{color:function(t,e){this.ctx.strokeColor=t,this.ctx.fillStyle=t}},created:function(){var t=this;this.$nextTick(function(){var e=t;t.canvas=t.$refs.canvas,t.ctx=t.canvas.getContext("2d");var s=t.canvas,a=t.ctx;function i(){s.width=.6*window.innerWidth,s.height=window.innerHeight,this&&e._repaint()}function n(t){var e=s.getBoundingClientRect();return{x:t.clientX-e.left,y:t.clientY-e.top}}i(),window.addEventListener("resize",i,!1),window.addEventListener("load",i,!1);var o=function t(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:"#000";x()(this,t),this.width=3,this.color=e,this.drawing=!1},l=function(t){function i(){var t=arguments.length>0&&void 0!==arguments[0]?arguments[0]:"#000";return x()(this,i),_()(this,(i.__proto__||g()(i)).call(this,t))}return b()(i,t),m()(i,[{key:"begin",value:function(t){e.scene?a.putImageData(e.scene,0,0):e.scene=a.getImageData(0,0,s.width,s.height),this.scene=a.getImageData(0,0,s.width,s.height),this.startPosition=t,e.tag.startPosition=this.startPosition}},{key:"draw",value:function(t){a.putImageData(this.scene,0,0);var s=this.startPosition.x,i=this.startPosition.y,n={x:s<=t.x?s:t.x,y:i<=t.y?i:t.y};a.beginPath(),a.strokeStyle=e.color,a.rect(n.x,n.y,Math.abs(s-t.x),Math.abs(i-t.y)),a.stroke()}},{key:"end",value:function(){var t=this.startPosition.x,e=this.startPosition.y,s={x:t<=location.x?t:location.x,y:e<=location.y?e:location.y};a.beginPath(),a.rect(s.x,s.y,Math.abs(t-location.x),Math.abs(e-location.y)),a.stroke()}},{key:"bindEvent",value:function(){var t=this;s.addEventListener("mousedown",function(s){if(s.preventDefault(),1===Number(e.selectUtil)){t.drawing=!0;var a=n(s);t.begin(a)}}),s.addEventListener("mousemove",function(s){if(s.preventDefault(),1===Number(e.selectUtil)&&t.drawing){var a=n(s);t.draw(a)}}),s.addEventListener("mouseup",function(s){if(s.preventDefault(),1===Number(e.selectUtil)){var a=n(s);t.end(a),t.drawing=!1,e.tag.endPosition.x=a.x,e.tag.endPosition.y=a.y}})}}]),i}(o),r=function(t){function i(){var t=arguments.length>0&&void 0!==arguments[0]?arguments[0]:"#000";return x()(this,i),_()(this,(i.__proto__||g()(i)).call(this,t))}return b()(i,t),m()(i,[{key:"begin",value:function(t){a.beginPath(),a.strokeStyle=e.color,a.moveTo(t.x,t.y)}},{key:"draw",value:function(t){a.strokeStyle=e.color,a.lineTo(t.x,t.y),a.stroke()}},{key:"end",value:function(t){a.strokeStyle=e.color,a.lineTo(t.x,t.y),a.stroke()}},{key:"bindEvent",value:function(){var t=this;s.addEventListener("mousedown",function(s){if(s.preventDefault(),2===Number(e.selectUtil)){t.drawing=!0;var a=n(s);e.tag.penPoints.push(a),t.begin(a)}}),s.addEventListener("mousemove",function(s){if(s.preventDefault(),2===Number(e.selectUtil)&&t.drawing){var a=n(s);e.tag.penPoints.push(a),t.draw(a)}}),s.addEventListener("mouseup",function(s){if(s.preventDefault(),2===Number(e.selectUtil)){var a=n(s);e.tag.penPoints.push(a),t.end(a),t.drawing=!1}})}}]),i}(o),c=new l,u=new r;c.bindEvent(),u.bindEvent()})},computed:{styleObject:function(){return{backgroundImage:"url('/taskdata/"+this.taskId+"/"+this.filename+"')",backgroundSize:"75%",backgroundRepeat:"no-repeat",backgroundPosition:"center center"}},tagType:function(){if(!this.tags[0])return 0;var t=this.tags[0].tagType,e=this.tags[0].descType;return 0===t&&0===e?0:2===t&&0===e?1:1===t&&1===e?2:1===t&&0===e?3:void 0},getOverallDesc:function(){for(var t=0;t<this.tags.length;t++)if(0===this.tags[t].tagType)return this.tags[t].singleDesc;return"无"}},mounted:function(){var t=this;this.$nextTick(function(){var e=localStorage.getItem("taskInfo");e&&(e=JSON.parse(e),t.taskId=e.id,t.desc=e.description,t.$ajax.get("/tasks/"+t.taskId).then(function(e){var s=e.data.data;t.questions=s.images,console.log(s.images),console.log(t.questions),t.labels=s.labels,t.filename=t.questions[0].filename,t.taskType=s.taskType,t.tags=t.questions[t.index].tags,t._repaint()}))}),this.$alert("使用矩形工具画出一个矩形后, 要及时在注释区填入注释，并点击确认键保存哦！","小贴士",{confirmButtonText:"确定",type:"warning"}).then(function(){})},methods:{select:function(t){this.selectUtil=t},deleteTag:function(t,e){var s=this;0!==e?this.$ajax.delete("/tasks/"+this.taskId,{params:{tagId:e}}).then(function(e){"delete success"===e.data?(s.tags.splice(t,1),s._repaint()):s.$message.error("删除失败")}):(this.tags.splice(t,1),this._repaint())},confirm:function(){if(""===this.overall.trim())this.$message.error("标注失败，请重新输入");else{var t=this.findOverallTag();this.tag.singleDesc=this.overall,this.tag.tagType=0,t?d()(t,this.tag):this.tags.push(this.tag),this.$message({message:"标注成功",type:"success"}),this.tag=this._getNewTag()}},confirmTag:function(t){this.tag.descType=t,this.tag.tagType=this.selectUtil,1===Number(this.tag.tagType)&&0===this.tag.startPosition.x&&0===this.tag.startPosition.y&&0===this.tag.endPosition.x&&0===this.tag.endPosition.y?this.$message.error("标注失败，没有进行标记"):2===Number(this.tagType)&&0===this.tag.penPoints.length||-1===this.selectUtil?this.$message.error("标注失败，没有进行标记"):""===this.tag.singleDesc&&0===t||this.isEmptyObject(this.tag.mapDesc)&&1===t?this.$message.error("标注失败，请重新输入注释"):(this.tag.color=this.color,this.tags.push(this.tag),console.log(this.tag),this.$message({message:"标注成功",type:"success"}),this.scene=null,console.log(this.tags),this.tag=this._getNewTag())},submit:function(){var t=this;this.$confirm("确认已完成所有的标注任务?(未完成的任务将不计入工作成绩）","提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(function(){console.log(t.questions),t.$ajax.post("/tasks/"+t.taskId+"/tags",t.questions).then(function(e){t.$message({type:"success",message:"提交成功!"}),t.$router.push("/home")}).catch(function(){t.$message({type:"info",message:"提交失败，服务器开小差啦"})})}).catch(function(){t.$message({type:"info",message:"已取消提交"})})},changeIndex:function(t){this.index=t-1,this.tags=this.questions[this.index].tags,this.filename=this.questions[this.index].filename,this.overall="",this.tag=this._getNewTag(),this._repaint()},findOverallTag:function(){for(var t=0;t<this.tags.length;t++)if(0===this.tags[t].tagType)return this.tags[t];return null},_getNewTag:function(){return{id:0,tagType:0,penPoints:[],startPosition:{x:0,y:0},endPosition:{x:0,y:0},descType:0,mapDesc:{},singleDesc:""}},isEmptyObject:function(t){var e=void 0;for(e in t)return!1;return!0},giveUp:function(){var t=this;this.$confirm("确认放弃本次标注任务？（标注结果将不会保存）","提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(function(){t.$router.push("/home")}).catch(function(){})},_repaint:function(){var t=this.tags,e=this.ctx;e.clearRect(0,0,this.canvas.width,this.canvas.height);for(var s=0;s<t.length;s++){var a=t[s];if(e.strokeStyle=a.color,console.log(a.color),console.log(e.strokeStyle),1===Number(a.tagType))e.beginPath(),e.rect(a.startPosition.x,a.startPosition.y,Math.abs(a.endPosition.x-a.startPosition.x),Math.abs(a.endPosition.y-a.startPosition.y)),e.stroke();else if(2===Number(a.tagType)){e.beginPath(),e.moveTo(a.penPoints[0].x,a.penPoints[0].y);for(var i=1;i<a.penPoints.length;i++)e.lineTo(a.penPoints[i].x,a.penPoints[i].y),e.stroke()}}},formatTooltip:function(t){return t+"%"}}},C={render:function(){var t=this,e=t.$createElement,s=t._self._c||e;return s("div",{staticClass:"board-wrapper"},[s("div",{staticClass:"col-left"},[s("div",{staticClass:"taglist-wrapper"},[s("div",{directives:[{name:"show",rawName:"v-show",value:t.showTags,expression:"showTags"}],staticClass:"tags"},[s("h1",{staticClass:"title"},[t._v("注释区")]),t._v(" "),s("div",{staticClass:"content"},[s("h1",{staticClass:"label"},[t._v("整体描述")]),t._v(" "),s("div",{staticClass:"overall-desc"},[s("el-input",{attrs:{type:"textarea",autosize:"",placeholder:"请输入对图片的整体描述"},model:{value:t.overall,callback:function(e){t.overall=e},expression:"overall"}}),t._v(" "),s("el-button",{attrs:{type:"plain"},on:{click:t.confirm}},[t._v("确认")])],1),t._v(" "),s("h1",{staticClass:"label"},[t._v("局部标注")]),t._v(" "),s("div",{directives:[{name:"show",rawName:"v-show",value:0===t.taskType,expression:"taskType === 0"}],staticClass:"map-desc"},[s("ul",t._l(t.labels,function(e,a){return s("li",{key:a},[s("div",{staticClass:"label"},[t._v(t._s(e))]),t._v(" "),s("el-input",{attrs:{size:"mini"},model:{value:t.tag.mapDesc[e],callback:function(s){t.$set(t.tag.mapDesc,e,s)},expression:"tag.mapDesc[item]"}})],1)})),t._v(" "),s("el-button",{attrs:{type:"primary"},on:{click:function(e){t.confirmTag(1)}}},[t._v("确认")])],1),t._v(" "),s("div",{directives:[{name:"show",rawName:"v-show",value:0!==t.taskType,expression:"taskType !== 0"}],staticClass:"single-desc"},[s("el-input",{staticClass:"single-desc-input",attrs:{size:"small",placeholder:"请输入标注",clearable:""},model:{value:t.tag.singleDesc,callback:function(e){t.$set(t.tag,"singleDesc",e)},expression:"tag.singleDesc"}}),t._v(" "),s("el-button",{staticClass:"single-desc-btn",attrs:{type:"plain"},on:{click:function(e){t.confirmTag(0)}}},[t._v("确认")])],1)])]),t._v(" "),s("div",{staticClass:"results"},[s("h1",{staticClass:"title"},[t._v("标注结果")]),t._v(" "),s("div",{staticClass:"content"},[s("div",{staticClass:"overall-wrapper"},[s("h1",{staticClass:"label"},[t._v("整体描述")]),t._v(" "),s("p",{staticClass:"overall-text"},[t._v(t._s(t.getOverallDesc))])]),t._v(" "),s("div",{staticClass:"part-wrapper"},[s("h1",{staticClass:"label"},[t._v("局部标注")]),t._v(" "),0===t.tags.length?s("div",[t._v("无")]):t._e(),t._v(" "),s("ul",{staticClass:"tag-list"},t._l(t.tags,function(e,a){return 0!==e.tagType?s("li",{key:a,staticClass:"tag-item"},[0===e.descType?s("div",{staticClass:"single"},[s("span",[t._v(t._s(a+1)+". "+t._s(e.singleDesc))]),t._v(" "),s("div",{staticClass:"icon-group"},[s("i",{staticClass:"el-icon-delete",on:{click:function(s){t.deleteTag(a,e.id)}}})])]):t._e(),t._v(" "),1===e.descType?s("div",{staticClass:"map"},[s("div",[t._v(t._s(a+1))]),t._v(" "),t._l(Object.keys(e.mapDesc),function(a,i){return s("div",{key:i},[t._v(t._s(a)+": "+t._s(e.mapDesc[a]))])}),t._v(" "),s("div",{staticClass:"icon-group"},[s("i",{staticClass:"el-icon-delete",on:{click:function(s){t.deleteTag(a,e.id)}}})])],2):t._e()]):t._e()}))])])])])]),t._v(" "),s("div",{staticClass:"col-mid"},[s("canvas",{ref:"canvas",style:t.styleObject,attrs:{id:"canvas"}})]),t._v(" "),s("div",{staticClass:"col-right"},[s("div",{staticClass:"tagutil-wrapper"},[s("div",{staticClass:"task-info"},[s("h1",{staticClass:"title"},[t._v("任务信息")]),t._v(" "),s("div",{staticClass:"content"},[s("div",{staticClass:"label"},[t._v("任务描述")]),t._v(" "),s("div",{staticClass:"desc"},[t._v(t._s(t.desc))]),t._v(" "),s("el-pagination",{staticClass:"pagination",attrs:{background:"",small:"",layout:"prev, pager, next","page-size":1,total:t.questions.length},on:{"current-change":t.changeIndex}}),t._v(" "),s("el-button",{attrs:{type:"danger"},on:{click:t.submit}},[t._v("提交")]),t._v(" "),s("el-button",{attrs:{type:"plain"},on:{click:t.giveUp}},[t._v("放弃")])],1)]),t._v(" "),s("div",{staticClass:"utils"},[s("h1",{staticClass:"title"},[t._v("工具区")]),t._v(" "),s("div",{staticClass:"content"},[s("div",{staticClass:"btn-group"},[s("el-radio-group",{ref:"utils",attrs:{size:"small"},model:{value:t.selectUtil,callback:function(e){t.selectUtil=e},expression:"selectUtil"}},[s("el-radio-button",{attrs:{label:"1",disabled:2===t.taskType},on:{click:function(e){t.select(1)}}},[s("i",{staticClass:"iconfont"},[t._v("")]),t._v("矩形\n              ")]),t._v(" "),s("el-radio-button",{attrs:{label:"2",disabled:2!==t.taskType},on:{click:function(e){t.select(2)}}},[s("i",{staticClass:"iconfont"},[t._v("")]),t._v("铅笔\n              ")])],1)],1),t._v(" "),s("el-row",[s("div",{staticClass:"label"},[t._v("拾色器")]),t._v(" "),s("div",{staticClass:"color-picker"},[s("el-color-picker",{attrs:{size:"small"},model:{value:t.color,callback:function(e){t.color=e},expression:"color"}})],1)])],1)])])])])},staticRenderFns:[]};var T=s("VU/8")(w,C,!1,function(t){s("OXwk")},null,null).exports,$={name:"publish",data:function(){return{uploadData:{id:0},filelist:[],task:{title:"",description:"",taskType:0,labels:[],images:[]}}},created:function(){var t=this;this.$ajax.get("/tasks/new").then(function(e){e=e.data,console.log(e),0===e.code&&(t.uploadData.id=e.data)})},methods:{addLabel:function(){this.task.labels.push("")},deleteLabel:function(t){this.task.labels.splice(t,1)},addImage:function(t,e,s){this.task.images.push({filename:e.name}),this.filelist.push({name:e.name})},publish:function(){var t=this;this.$ajax.post("/tasks",this.task).then(function(e){0===e.data.code?t.$message.success("发布成功"):t.$message.error("发布失败")}).catch(function(){t.$message.error("发布失败")})},cancel:function(){var t=this;this.$confirm("确定放弃本次发布任务吗？","提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(function(){t.$router.push("/home")}).catch(function(){})}}},U={render:function(){var t=this,e=t.$createElement,s=t._self._c||e;return s("div",{staticClass:"publish"},[s("div",{staticClass:"article"},[s("div",{staticClass:"title"},[t._v("发布标注任务")]),t._v(" "),s("el-form",{ref:"taskForm",attrs:{"label-width":"110px","label-position":"left",size:"medium"},model:{value:t.task,callback:function(e){t.task=e},expression:"task"}},[s("el-form-item",{attrs:{label:"任务名称",prop:"title"}},[s("el-input",{attrs:{placeholder:"任务名称"},model:{value:t.task.title,callback:function(e){t.$set(t.task,"title",e)},expression:"task.title"}})],1),t._v(" "),s("el-form-item",{attrs:{label:"任务描述"}},[s("el-input",{attrs:{type:"textarea",autosize:{minRows:2},placeholder:"任务描述"},model:{value:t.task.description,callback:function(e){t.$set(t.task,"description",e)},expression:"task.description"}})],1),t._v(" "),s("el-form-item",{attrs:{label:"任务类型",prop:"taskType"}},[s("el-radio-group",{model:{value:t.task.taskType,callback:function(e){t.$set(t.task,"taskType",e)},expression:"task.taskType"}},[s("el-radio",{attrs:{label:0}},[t._v("分类标注")]),t._v(" "),s("el-radio",{attrs:{label:1}},[t._v("标框标注")]),t._v(" "),s("el-radio",{attrs:{label:2}},[t._v("区域标注")])],1)],1),t._v(" "),s("el-form-item",{directives:[{name:"show",rawName:"v-show",value:0===t.task.taskType,expression:"task.taskType === 0"}],attrs:{label:"分类标注标签",prop:"labels"}},[s("el-form",{staticClass:"label-form",attrs:{size:"small"},model:{value:t.task.labels,callback:function(e){t.$set(t.task,"labels",e)},expression:"task.labels"}},[t._l(t.task.labels,function(e,a){return s("el-form-item",{key:a,attrs:{label:"标签"+(a+1)}},[s("el-input",{model:{value:t.task.labels[a],callback:function(e){t.$set(t.task.labels,a,e)},expression:"task.labels[index]"}}),t._v(" "),s("el-button",{staticClass:"delete-btn",attrs:{type:"danger"},on:{click:function(e){t.deleteLabel(a)}}},[t._v("删除")])],1)}),t._v(" "),s("el-button",{attrs:{plain:""},on:{click:t.addLabel}},[t._v("添加标签")])],2)],1),t._v(" "),s("el-form-item",{attrs:{label:"上传图片",prop:"images"}},[s("el-upload",{attrs:{drag:"",action:"/upload",data:t.uploadData,multiple:"","on-success":t.addImage,"file-list":t.filelist}},[s("i",{staticClass:"el-icon-upload"}),t._v(" "),s("div",{staticClass:"el-upload__text"},[t._v("将文件拖到此处，或"),s("em",[t._v("点击上传")])]),t._v(" "),s("div",{staticClass:"el-upload__tip",attrs:{slot:"tip"},slot:"tip"},[t._v("只能上传jpg/png文件，且不超过500kb")])])],1)],1),t._v(" "),s("div",{staticClass:"btn-group"},[s("el-button",{staticClass:"publish-btn",attrs:{type:"primary"},on:{click:t.publish}},[t._v("发布")]),t._v(" "),s("el-button",{staticClass:"cancel-btn",attrs:{type:"plain"},on:{click:t.cancel}},[t._v("取消")])],1)],1)])},staticRenderFns:[]};var P=s("VU/8")($,U,!1,function(t){s("hV0j")},null,null).exports,F=s("mvHQ"),D=s.n(F),E={name:"tasks",data:function(){return{taskInfos:[]}},created:function(){var t=this;this.$ajax.get("/tasks/list").then(function(e){e=e.data,t.taskInfos=e.data})},methods:{enterTask:function(t){var e=this.taskInfos[t];localStorage.setItem("taskInfo",D()(e)),this.$router.push("/board")},taskType:function(t){return["分类","标框","区域"][t]}}},I={render:function(){var t=this,e=t.$createElement,s=t._self._c||e;return s("div",{staticClass:"tasks"},[s("div",{staticClass:"task-wrapper"},[s("h1",{staticClass:"title"},[t._v("任务列表")]),t._v(" "),s("div",{staticClass:"tasks"},t._l(t.taskInfos,function(e,a){return s("div",{key:a,staticClass:"task-card",on:{click:function(e){t.enterTask(a)}}},[s("el-card",{attrs:{"body-style":{padding:"0px"}}},[s("img",{staticClass:"image",attrs:{src:"/taskdata/"+e.id+"/"+e.picName}}),t._v(" "),s("div",{staticClass:"content"},[s("div",{staticClass:"task-title"},[t._v(t._s(e.title))]),t._v(" "),s("div",{staticClass:"info-blocks"},[s("div",{staticClass:"block"},[s("div",{staticClass:"label"},[t._v("总题数")]),t._v(" "),s("div",{staticClass:"num"},[s("strong",[t._v(t._s(e.picNum))]),s("span",{staticClass:"unit"},[t._v("题")])])]),t._v(" "),s("div",{staticClass:"block"},[s("div",{staticClass:"label"},[t._v("类型")]),t._v(" "),s("div",{staticClass:"num"},[s("strong",[t._v(t._s(t.taskType(e.taskType)))])])])])])])],1)}))])])},staticRenderFns:[]};var N=s("VU/8")(E,I,!1,function(t){s("nOG+")},null,null).exports,O={render:function(){var t=this,e=t.$createElement,s=t._self._c||e;return s("el-form",{staticClass:"login-form"},[s("el-form-item",[s("el-input",{staticClass:"username-input",attrs:{placeholder:"手机号/邮箱"},model:{value:t.name,callback:function(e){t.name=e},expression:"name"}},[s("i",{staticClass:"iconfont",attrs:{slot:"prefix"},slot:"prefix"},[t._v("")])])],1),t._v(" "),s("el-form-item",[s("el-input",{staticClass:"password-input",attrs:{placeholder:"密码",type:"password"},model:{value:t.password,callback:function(e){t.password=e},expression:"password"}},[s("i",{staticClass:"iconfont",attrs:{slot:"prefix"},slot:"prefix"},[t._v("")])])],1),t._v(" "),s("el-form-item",[s("el-button",{staticClass:"login-btn",attrs:{type:"primary",round:""},on:{click:t.login}},[t._v("登录")])],1)],1)},staticRenderFns:[]};var j=s("VU/8")({name:"signin",data:function(){return{name:"",password:""}},methods:{login:function(){var t=this;this.$ajax.get("/user/login",{params:{name:this.name,password:this.password}}).then(function(e){0===(e=e.data).code&&t.$router.push("/home")})}}},O,!1,function(t){s("SwBz")},null,null).exports,q={name:"sign",data:function(){var t=this;return{signUpForm:{username:"",password:"",passwordAgain:"",phone:"",email:"",userType:0},rules:{username:[{required:!0,message:"请输入用户名",trigger:"blur"},{min:2,max:10,message:"长度在 2 到 10 个字符"},{pattern:/^[a-zA-Z](\w){1,9}$/,message:"以字母开头，长度在2-10之间， 只能包含字符、数字和下划线"}],password:[{validator:function(e,s,a){""===s?a(new Error("请输入密码")):(""!==t.signUpForm.password&&t.$refs.signUpForm.validateField("passwordAgain"),a())},trigger:"blur"}],passwordAgain:[{validator:function(e,s,a){""===s?a(new Error("请再次输入密码")):s!==t.signUpForm.password?a(new Error("两次输入密码不一致!")):a()},trigger:"blur"}],email:[{required:!0,message:"请输入邮箱地址",trigger:"blur"},{type:"email",message:"请输入正确的邮箱地址",trigger:"blur,change"}],phone:[{required:!0,message:"请输入手机号码",trigger:"blur"},{validator:function(t,e,s){!1===/^1[34578]\d{9}$/.test(e)?s(new Error("请输入正确的手机号")):s()},trigger:"blur"}],userType:[{required:!0}]}}},methods:{sign:function(){var t=this;this.validate()?this.$ajax.post("/user/sign",this.signUpForm).then(function(e){switch(e.data.code){case 0:t.$router.push("/home"),t.$message.success("注册成功");break;case 6:case 7:t.$message.error("注册失败!"+e.message)}}):this.$message.error("注册信息填写不完整！")},validate:function(){var t=void 0;for(t in this.signUpForm)if(""===this.signUpForm[t]&&"userType"!==t)return!1;return!0}}},z={render:function(){var t=this,e=t.$createElement,s=t._self._c||e;return s("el-form",{ref:"signUpForm",staticClass:"sign-form",attrs:{"status-icon":"",rules:t.rules,model:t.signUpForm}},[s("el-form-item",{attrs:{prop:"username"}},[s("el-input",{attrs:{placeholder:"请输入用户名"},model:{value:t.signUpForm.username,callback:function(e){t.$set(t.signUpForm,"username",e)},expression:"signUpForm.username"}},[s("i",{staticClass:"iconfont",attrs:{slot:"prefix"},slot:"prefix"},[t._v("")])])],1),t._v(" "),s("el-form-item",{attrs:{prop:"password"}},[s("el-input",{attrs:{placeholder:"请输入密码",type:"password"},model:{value:t.signUpForm.password,callback:function(e){t.$set(t.signUpForm,"password",e)},expression:"signUpForm.password"}},[s("i",{staticClass:"iconfont",attrs:{slot:"prefix"},slot:"prefix"},[t._v("")])])],1),t._v(" "),s("el-form-item",{attrs:{prop:"passwordAgain"}},[s("el-input",{attrs:{placeholder:"请再次输入密码",type:"password"},model:{value:t.signUpForm.passwordAgain,callback:function(e){t.$set(t.signUpForm,"passwordAgain",e)},expression:"signUpForm.passwordAgain"}},[s("i",{staticClass:"iconfont",attrs:{slot:"prefix"},slot:"prefix"},[t._v("")])])],1),t._v(" "),s("el-form-item",{attrs:{prop:"phone"}},[s("el-input",{attrs:{placeholder:"请输入手机号码"},model:{value:t.signUpForm.phone,callback:function(e){t.$set(t.signUpForm,"phone",e)},expression:"signUpForm.phone"}},[s("i",{staticClass:"iconfont",attrs:{slot:"prefix"},slot:"prefix"},[t._v("")])])],1),t._v(" "),s("el-form-item",{attrs:{prop:"email"}},[s("el-input",{attrs:{placeholder:"请输入邮件地址"},model:{value:t.signUpForm.email,callback:function(e){t.$set(t.signUpForm,"email",e)},expression:"signUpForm.email"}},[s("i",{staticClass:"iconfont",attrs:{slot:"prefix"},slot:"prefix"},[t._v("")])])],1),t._v(" "),s("el-form-item",{attrs:{label:"请选择用户角色",prop:"userType"}},[s("el-radio-group",{staticClass:"choice",model:{value:t.signUpForm.userType,callback:function(e){t.$set(t.signUpForm,"userType",e)},expression:"signUpForm.userType"}},[s("el-radio",{attrs:{label:0}},[t._v("众包工人")]),t._v(" "),s("el-radio",{attrs:{label:1}},[t._v("众包发起者")])],1)],1),t._v(" "),s("el-form-item",[s("el-button",{staticClass:"sign-btn",attrs:{type:"primary",round:""},on:{click:t.sign}},[t._v("注册")])],1)],1)},staticRenderFns:[]};var L=s("VU/8")(q,z,!1,function(t){s("YLPk")},null,null).exports;a.default.use(o.a);var A=[{path:"/",redirect:"/login"},{path:"/home",component:u,redirect:"/tasks",children:[{path:"/publish",component:P},{path:"/tasks",component:N}]},{name:"board",path:"/board",component:T},{path:"/login",component:r,children:[{path:"/",component:j},{path:"/sign",component:L}]}],S=new o.a({routes:A,linkActiveClass:"active"}),R=s("mtWM"),B=s.n(R),M=s("zL8q"),V=s.n(M);a.default.use(V.a),a.default.prototype.$ajax=B.a,a.default.config.productionTip=!1,new a.default({el:"#app",router:S,components:{App:n},template:"<App/>"})},OXwk:function(t,e){},SwBz:function(t,e){},YLPk:function(t,e){},hV0j:function(t,e){},"nOG+":function(t,e){},qrMk:function(t,e){}},["NHnr"]);
//# sourceMappingURL=app.8e700f0a4c920424bff2.js.map