'use strict'
const utils = require('./utils')
const webpack = require('webpack')
const config = require('../config')
const merge = require('webpack-merge')
const path = require('path')
const baseWebpackConfig = require('./webpack.base.conf')
const CopyWebpackPlugin = require('copy-webpack-plugin')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const FriendlyErrorsPlugin = require('friendly-errors-webpack-plugin')
const portfinder = require('portfinder')

const HOST = process.env.HOST
const PORT = process.env.PORT && Number(process.env.PORT)

var taskInfos = require('../taskInfo')
var tasks = require('../task')
tasks = tasks.data

const devWebpackConfig = merge(baseWebpackConfig, {
    module: {
      rules: utils.styleLoaders({sourceMap: config.dev.cssSourceMap, usePostCSS: true})
    },
    // cheap-module-eval-source-map is faster for development
    devtool: config.dev.devtool,


    // these devServer options should be customized in /config/index.js
    devServer: {
      before (app) {
        var bodyParser = require('body-parser')
        // parse application/x-www-form-urlencoded
        app.use(bodyParser.urlencoded({extended: false}))

        // parse application/json
        app.use(bodyParser.json())

        app.get('/user/tasks/search', (req, res) => {
          let size = req.query.size
          let page = req.query.page

          setTimeout(()=>{
            return res.json({
              data: {
                currentPage: page,
                totalItemNum: taskInfos.data.length,
                sortBy: "全部",
                isSec: true,
                data: taskInfos.data.slice((page - 1) * size, page * size)
              }
            })
          }, 5000)
        })

        app.get('/user/:id/tasks', (req, res) => {
          let size = req.query.size
          let page = req.query.page

          return res.json({
            data: {
              currentPage: page,
              totalItemNum: taskInfos.data.length,
              sortBy: "全部",
              isSec: true,
              data: taskInfos.data
            }
          })
        })

        app.get('/temp/user/:id', (req, res) => {
          let taskId = req.params.id
          return res.json({
            code: 0,
            data: {
              id: 0,
              taskId: 0,
              title: 'test',
              price: 2,
              description: 'testDesc',
              taskType: 0,
              images: [
                {
                  filename: '191003284_1025b0fb7d.jpg',
                  tags:[]
                },
                {
                  filename: '191003287_2915c11d8e.jpg',
                  tags:[]
                },
                {
                  filename: '191003284_1025b0fb7d.jpg',
                  tags:[]
                },
                {
                  filename: '191003284_1025b0fb7d.jpg',
                  tags:[]
                },
                {
                  filename: '191003287_2915c11d8e.jpg',
                  tags:[]
                },
                {
                  filename: '191003284_1025b0fb7d.jpg',
                  tags:[]
                },
                {
                  filename: '191003284_1025b0fb7d.jpg',
                  tags:[]
                },
                {
                  filename: '191003287_2915c11d8e.jpg',
                  tags:[]
                },
                {
                  filename: '191003284_1025b0fb7d.jpg',
                  tags:[]
                },
                {
                  filename: '191003284_1025b0fb7d.jpg',
                  tags:[]
                },
                {
                  filename: '191003287_2915c11d8e.jpg',
                  tags:[]
                },
                {
                  filename: '191003284_1025b0fb7d.jpg',
                  tags:[]
                }
              ]
            }
          })
        })

        app.get('/tasks/list', (req, res) => {
          setTimeout(()=>{
            let size = req.query.size
            let page = req.query.page
            return res.json({
              code: 0,
              data: {
                currentPage: 1,
                totalItemNum: 9,
                data: taskInfos.data
              }
            })
          }, 5000)
        })

        app.post('/tasks/new', (req, res) => {
          return res.json({
            code: 0,
            message: "success",
            data: null
          })
        })

        app.post('/tasks', (req, res) => {
          return res.json({
            code: 0,
            message: "success",
            data: null
          })
        })

        app.post('/user/login', (req, res) => {
          return res.json({
            status: 0,
            message: "",
            result: "123456789"
          })
        })

        app.post('/upload', (req, res) => {
          console.log('upload')
          return res.json({
            code: 0,
            message: "success",
            data: null
          })
        })
        app.get('/user/loginByToken', (req, res) => {
          return res.json({
            code: 0,
            message: "success",
            data: {
              id: 123,
              username: 'xun',
              userType: 2,
              avatar: 'default_avatar.png',
              applyState: 'PASS'
            }
          })
        })

        app.get('/user/:id/attend', (req, res) => {
          let id = req.params.id
          if (Number(id) === 123) {
            return res.json({
              code: 0,
              message: "success",
              data: 320
            })
          }
        })

        app.get('/statistics/task/timeline', (req, res) => {
          return res.json({
            code: 0,
            message: "success",
            data: [
              { taskPublisherId: 1,
                publishToExamineTime: 90,
                examineToExpertSubmitTime: 800,
                expertSubmitToAutoExamineTime: 800,
                autoExamineToEndTime: 7
              },
              { taskPublisherId: 2,
                publishToExamineTime: 90,
                examineToExpertSubmitTime: 800,
                expertSubmitToAutoExamineTime: 800,
                autoExamineToEndTime: 7
              },
              { taskPublisherId: 3,
                publishToExamineTime: 90,
                examineToExpertSubmitTime: 800,
                expertSubmitToAutoExamineTime: 800,
                autoExamineToEndTime: 7
              },
              { taskPublisherId: 4,
                publishToExamineTime: 90,
                examineToExpertSubmitTime: 800,
                expertSubmitToAutoExamineTime: 800,
                autoExamineToEndTime: 7
              },
              { taskPublisherId: 5,
                publishToExamineTime: 90,
                examineToExpertSubmitTime: 800,
                expertSubmitToAutoExamineTime: 800,
                autoExamineToEndTime: 7
              },
              { taskPublisherId: 6,
                publishToExamineTime: 90,
                examineToExpertSubmitTime: 800,
                expertSubmitToAutoExamineTime: 800,
                autoExamineToEndTime: 7
              },
              { taskPublisherId: 7,
                publishToExamineTime: 90,
                examineToExpertSubmitTime: 800,
                expertSubmitToAutoExamineTime: 800,
                autoExamineToEndTime: 7
              },
              { taskPublisherId: 8,
                publishToExamineTime: 90,
                examineToExpertSubmitTime: 800,
                expertSubmitToAutoExamineTime: 800,
                autoExamineToEndTime: 7
              },
              { taskPublisherId: 9,
                publishToExamineTime: 90,
                examineToExpertSubmitTime: 800,
                expertSubmitToAutoExamineTime: 800,
                autoExamineToEndTime: 7
              },
              { taskPublisherId: 10,
                publishToExamineTime: 90,
                examineToExpertSubmitTime: 800,
                expertSubmitToAutoExamineTime: 800,
                autoExamineToEndTime: 7
              },
              { taskPublisherId: 11,
                publishToExamineTime: 90,
                examineToExpertSubmitTime: 800,
                expertSubmitToAutoExamineTime: 800,
                autoExamineToEndTime: 7
              },
              { taskPublisherId: 12,
                publishToExamineTime: 90,
                examineToExpertSubmitTime: 800,
                expertSubmitToAutoExamineTime: 800,
                autoExamineToEndTime: 7
              },
              { taskPublisherId: 13,
                publishToExamineTime: 90,
                examineToExpertSubmitTime: 800,
                expertSubmitToAutoExamineTime: 800,
                autoExamineToEndTime: 7
              },
              { taskPublisherId: 14,
                publishToExamineTime: 90,
                examineToExpertSubmitTime: 800,
                expertSubmitToAutoExamineTime: 800,
                autoExamineToEndTime: 7
              },
              { taskPublisherId: 15,
                publishToExamineTime: 90,
                examineToExpertSubmitTime: 800,
                expertSubmitToAutoExamineTime: 800,
                autoExamineToEndTime: 7
              },
              { taskPublisherId: 16,
                publishToExamineTime: 90,
                examineToExpertSubmitTime: 800,
                expertSubmitToAutoExamineTime: 800,
                autoExamineToEndTime: 7
              },
              { taskPublisherId: 17,
                publishToExamineTime: 90,
                examineToExpertSubmitTime: 800,
                expertSubmitToAutoExamineTime: 800,
                autoExamineToEndTime: 7
              },
              { taskPublisherId: 18,
                publishToExamineTime: 90,
                examineToExpertSubmitTime: 800,
                expertSubmitToAutoExamineTime: 800,
                autoExamineToEndTime: 7
              },
              { taskPublisherId: 19,
                publishToExamineTime: 90,
                examineToExpertSubmitTime: 800,
                expertSubmitToAutoExamineTime: 800,
                autoExamineToEndTime: 7
              },
              { taskPublisherId: 20,
                publishToExamineTime: 90,
                examineToExpertSubmitTime: 800,
                expertSubmitToAutoExamineTime: 800,
                autoExamineToEndTime: 7
              },
              { taskPublisherId: 21,
                publishToExamineTime: 90,
                examineToExpertSubmitTime: 800,
                expertSubmitToAutoExamineTime: 800,
                autoExamineToEndTime: 7
              }
            ]
          })
        })

        /*app.post('/user/:id/recharge', (req, res) => {
          return res.json({
            code: 0,
            message: 'success',
            data: null
          })
        })*/

        // 领取任务奖励
        app.get('/user/task-record/open-reward/:id', (req, res) => {
          return res.json({
            code: 0,
            message: 'success',
            data: null
          })
        })

        app.get('/rank/*', (req, res) => {
          return res.json({
            code: 0,
            message: 'success',
            data: {
              rankList: [
                {
                  username: 'lioder',
                  avatar: 'default_avatar.png',
                  points: 800,
                  exp: 900,
                  satisfactionRate: 0.908,
                  accuracyRate: 0.897
                },
                {
                  username: 'xun',
                  avatar: 'default_avatar.png',
                  points: 600,
                  exp: 900,
                  satisfactionRate: 0.998,
                  accuracyRate: 0.567
                },
                {
                  username: 'lioder',
                  avatar: 'default_avatar.png',
                  points: 800,
                  exp: 900,
                  satisfactionRate: 0.908,
                  accuracyRate: 0.897
                },
                {
                  username: 'xun',
                  avatar: 'default_avatar.png',
                  points: 600,
                  exp: 900,
                  satisfactionRate: 0.998,
                  accuracyRate: 0.567
                },
                {
                  username: 'lioder',
                  avatar: 'default_avatar.png',
                  points: 800,
                  exp: 900,
                  satisfactionRate: 0.908,
                  accuracyRate: 0.897
                },
                {
                  username: 'xun',
                  avatar: 'default_avatar.png',
                  points: 600,
                  exp: 900,
                  satisfactionRate: 0.998,
                  accuracyRate: 0.567
                },
                {
                  username: 'lioder',
                  avatar: 'default_avatar.png',
                  points: 800,
                  exp: 900,
                  satisfactionRate: 0.908,
                  accuracyRate: 0.897
                },
                {
                  username: 'xun',
                  avatar: 'default_avatar.png',
                  points: 600,
                  exp: 900,
                  satisfactionRate: 0.998,
                  accuracyRate: 0.567
                },
                {
                  username: 'lioder',
                  avatar: 'default_avatar.png',
                  points: 800,
                  exp: 900,
                  satisfactionRate: 0.908,
                  accuracyRate: 0.897
                },
                {
                  username: 'xun',
                  avatar: 'default_avatar.png',
                  points: 600,
                  exp: 900,
                  satisfactionRate: 0.998,
                  accuracyRate: 0.567
                },
                {
                  username: 'lioder',
                  avatar: 'default_avatar.png',
                  points: 800,
                  exp: 900,
                  satisfactionRate: 0.908,
                  accuracyRate: 0.897
                },
                {
                  username: 'xun',
                  avatar: 'default_avatar.png',
                  points: 600,
                  exp: 900,
                  satisfactionRate: 0.998,
                  accuracyRate: 0.567
                },
                {
                  username: 'lioder',
                  avatar: 'default_avatar.png',
                  points: 800,
                  exp: 900,
                  satisfactionRate: 0.908,
                  accuracyRate: 0.897
                },
                {
                  username: 'xun',
                  avatar: 'default_avatar.png',
                  points: 600,
                  exp: 900,
                  satisfactionRate: 0.998,
                  accuracyRate: 0.567
                },
                {
                  username: 'lioder',
                  avatar: 'default_avatar.png',
                  points: 800,
                  exp: 900,
                  satisfactionRate: 0.908,
                  accuracyRate: 0.897
                },
                {
                  username: 'xun',
                  avatar: 'default_avatar.png',
                  points: 600,
                  exp: 900,
                  satisfactionRate: 0.998,
                  accuracyRate: 0.567
                }]
            }
          })
        })

        app.get('/user/info/:id', (req, res) => {
          return res.json({
            code: 0,
            message: 'success',
            data: {
              "id": 123,
              "username": "Xun",
              "password": "123456",
              "phone": "18851822717",
              "email": "1160245392@qq.com",
              "userType": 1,
              "points": 300,
              "avatar": "default_avatar.png",
              "level": 'LEVEL_ONE',
              "exp": 90,
              "accuracyRate": 0.8908,
              "punctualityRate": 0.9078,
              "satisfactionRate": 0.569,
              "isAttendant": false
            }
          })
        })
        app.get('/recommend/user', (req, res) => {
          setTimeout(()=>{
            return res.json({
              code: 0,
              data: taskInfos.data
            })
          }, 5000)
        })

        app.get('/recommend/hotTask', (req, res) => {
          return res.json({
            code: 0,
            data: taskInfos.data.slice(0,5)
          })
        })


        app.post('/admin/checkTask/:id', (req, res) => {
          return res.json({
            code: 0
          })
        })

        app.get('/recommend/item', (req, res)=>{
          return res.json({
            code: 0,
            data: taskInfos.data
          })
        })
        app.post('/admin/checkTag/:id', (req, res) => {
          return res.json({
            code: 0
          })
        })
      },
    clientLogLevel: 'warning',
    historyApiFallback: {
      rewrites: [
        {from: /.*/, to: path.posix.join(config.dev.assetsPublicPath, 'index.html')},
      ],
    },
    hot: true,
    contentBase: false, // since we use CopyWebpackPlugin.
    compress: true,
    host: HOST || config.dev.host,
    port: PORT || config.dev.port,
    open: config.dev.autoOpenBrowser,
    overlay: config.dev.errorOverlay
      ? {warnings: false, errors: true}
      : false,
    publicPath: config.dev.assetsPublicPath,
    proxy: config.dev.proxyTable,
    quiet: true, // necessary for FriendlyErrorsPlugin
    watchOptions: {
      poll: config.dev.poll,
    }
  },
  plugins
:
[
  new webpack.DefinePlugin({
    'process.env': require('../config/dev.env')
  }),
  new webpack.HotModuleReplacementPlugin(),
  new webpack.NamedModulesPlugin(), // HMR shows correct file names in console on update.
  new webpack.NoEmitOnErrorsPlugin(),
  // https://github.com/ampedandwired/html-webpack-plugin
  new HtmlWebpackPlugin({
    filename: 'index.html',
    template: 'index.html',
    inject: true
  }),
  // copy custom static assets
  new CopyWebpackPlugin([
    {
      from: path.resolve(__dirname, '../static'),
      to: config.dev.assetsSubDirectory,
      ignore: ['.*']
    }
  ])
]
})

module.exports = new Promise((resolve, reject) => {
  portfinder.basePort = process.env.PORT || config.dev.port
  portfinder.getPort((err, port) => {
    if (err) {
      reject(err)
    } else {
      // publish the new Port, necessary for e2e tests
      process.env.PORT = port
      // add port to devServer config
      devWebpackConfig.devServer.port = port

      // Add FriendlyErrorsPlugin
      devWebpackConfig.plugins.push(new FriendlyErrorsPlugin({
        compilationSuccessInfo: {
          messages: [`Your application is running here: http://${devWebpackConfig.devServer.host}:${port}`],
        },
        onErrors: config.dev.notifyOnErrors
          ? utils.createNotifierCallback()
          : undefined
      }))

      resolve(devWebpackConfig)
    }
  })
})
