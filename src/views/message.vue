<template>
  <div>
    <div class="content">
      <el-page-header @back="goBack" content="" style="position: fixed">
      </el-page-header>
      <div class=user_content>
        <div class="user-info user-data-base">
          <div class="link-class" @click="getSendEvent">
            <el-link href="#" ><span>我的动态</span></el-link>
          </div>
          <div class="link-class" @click="getRecEvent">
            <el-link href="#" ><span>我的消息</span></el-link>
          </div>
        </div>
      </div>
      <div class="article-content">
        <div>
          <ol>
            <li v-for="(event_v, index) in events.list" v-bind:key="event_v.id" @click="checkCount(index)">
             <div v-if="type == 1">
               <p><span style="color: red" v-if="event_v.status == 1">new</span>
                 <el-link @click="goView1(event_v.fromUserName)">{{event_v.fromUserName}}</el-link>
                 <span class="common-content">{{event_v.time}}回复了你：{{event_v.content}}</span></p>
               <div style="margin-left: 2%">
                 <el-link @click="showArticle(event_v.common.articleId,event_v.common.articleUserName)">{{event_v.common.content}}</el-link>
                 <br>
               </div>
               <div v-if="count == index">
                 <el-input
                   placeholder="回复点什么"
                   v-model="input"
                   clearable style="width: 80%">
                 </el-input>
                 <el-button @click="doReply(event_v.common.articleId, event_v.fromUserName, event_v.commonId)">回复</el-button>
               </div>
             </div>
              <div v-else>
                <span class="common-content">
                  {{event_v.time}}回复了：<el-link @click="goView1(event_v.toUserName)">{{event_v.toUserName}}</el-link>
                </span><br>
                <el-link @click="showArticle(event_v.common.articleId,event_v.common.articleUserName)">{{event_v.content}}</el-link>
              </div>
              <hr>
            </li>
          </ol>
          <div>
            <div class="block">
              <el-pagination
                background
                layout="prev, pager, next"
                :total="events.pager.pageCount * 10"
                @next-click="next_click"
                @prev-click="prev_click"
                @current-change="current_change"
              >
              </el-pagination>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'message',
    data() {
      return{
        //当前登陆用户
        user: '',
        //当前的事件
        events: '',
        //当前的页数
        pageCount: 1,
        //1代表受到的消息，0代表发出的消息
        type: 1,
        //回复信息
        input: '',
        //当前点击的序号
        count: -1
      }
    },
    mounted () {
      this.user = localStorage.getItem('userdata')
      this.user = JSON.parse(this.user).name
      this.getRecEvent()
    },
    methods : {
      //页数改变
      current_change(cont){
        this.pageCount = cont
        this.getEvents()
      },
      //上一页
      prev_click(){
        this.pageCount--
        this.getEvents()
      },
      //下一页
      next_click(){
        this.pageCount++
        this.getEvents()
      },
      //回复
      doReply(articleId, fromUserName,commonId) {
        if ($.trim(this.input) == ''){
          this.$message.error('请输入具体内容')
          return
        }
        let common = {articleId: articleId, userName: this.user, content: this.input,replyUser: fromUserName, commonId: commonId}
        this.$axios.post("http://zhsdsb.top:8888/common/doCommonOfCommon", common)
          .then(response => {
            if (response.data.success){
              this.$message.success("回复成功")
              this.input = ''
            } else {
              console.log(response.data.data)
            }
          }).catch(function (error) {
          console.log(error)
        })
      },
      //切换count值，让输入框对应显示
      checkCount(count){
        this.count = count
      },
      //查看用户
      goView1(userName){
        window.location.href = '/userIndex?userName=' + userName
      },
      //查看某个评论的文章
      showArticle(id, userName){
        window.location.href = '/articleComponent?id=' + id + '&userName=' + userName
      },
      goBack() {
        window.location.href = "/index"
      },
      //拉取事件消息,1代表自己收到的  0代表发出的
      getEvents(){
        if (this.user != ''){
          this.$axios.get("http://zhsdsb.top:8888/event/getEvents",{
            params : {
              userName: this.user,
              type: this.type,
              pageCount: this.pageCount
            }
          }).then(response => {
            if (response.data.success){
              this.events = response.data.data
              console.log(this.events)
            }
          }).catch(function (error) {
            console.log(error)
          })
        }
      },
      //获取受到的消息
      getRecEvent(){
        this.pageCount = 1
        this.type = 1
        this.getEvents()
      },
      //获取发出的消息
      getSendEvent(){
        this.pageCount = 1
        this.type = 0
        this.getEvents()
      }
    },

  }
</script>

<style scoped>
  .common-content{
    color: #8a8a8a;margin-bottom: 4px;font-size: 14px;line-height: 24px;
  }
  .link-class{
    margin-bottom: 10%;
  }
  .user-info{
    text-align: center;
    min-height: 800px;
  }
  .article-content{
    margin-left: 21%;
    margin-top: 3%;
    width: 60%;
  }
  h1{
    text-align: center;
  }
  .user-all-articles{
    min-height: 900px;
  }
  .user-data-base{
    background-color: #f5f6f7;
    margin-bottom: 6%;
  }
  .user_content{
    margin-left: 5%;
    width: 15%;
    float: left;
    position: fixed;
  }
  .content{
    font-size: 18px;
    line-height: 1.57142857;
    color: #333;
  }
  a{
    text-decoration-line: none;
    color:black;
  }
  li{
    list-style-type:none;
  }
  span{
    padding: 10px ;
    margin: 10px ;
    width: 100px;
  }
</style>
