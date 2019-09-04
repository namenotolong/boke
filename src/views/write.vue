<template>
  <div >
    <div class="my-header">
      <el-input v-model="input" placeholder="请输入标题" maxlength="100" class="title-input" >
    </el-input>
      <span class="compute-number">{{input.length}}/100</span>
      <el-button type="danger" style="margin-left: 5%" @click="publishArticle()">发布</el-button>
      <el-input
        type="input"
        :rows="2"
        placeholder="请输入简介"
        v-model="textarea"
        maxlength="100"
        class="title-input"
      >
      </el-input>
      <span class="compute-number">{{textarea.length}}/100</span>
    </div>
    <div class="choose-menu">
      <el-button class="choose-menu-one" type="success" @click="goView('index')"><li class="el-icon-caret-left"></li>返回首页</el-button>
      <el-select v-model="value" placeholder="请选择发布类别" class="choose-menu-one">
        <el-option
          v-for="item in options"
          :key="item.id"
          :label="item.name"
          :value="item.name">
        </el-option>
      </el-select>
      <el-radio v-model="radio" label="0">公开</el-radio>
      <el-radio v-model="radio" label="1">私密</el-radio>
    </div>
    <div class="content1">
      <quill-editor
        v-model="content"
        ref="myQuillEditor"
        :options="editorOption"
        @blur="onEditorBlur($event)" @focus="onEditorFocus($event)"
        @change="onEditorChange($event)">
      </quill-editor>
    </div>
    <div class="content2" v-html="content">
    </div>
  </div>
</template>

<script>
  import { delData } from '../utils/util'
  export default {
    name: 'write',
    data(){
      return {
        radio: '0',
        input: "",
        textarea: "",
        content_text: "",
        content: `<br><br>`,
        editorOption: {},
        options: null,
        value: ''
      }
    },
    methods : {
      goView(view){
        this.$router.push({path: '/' + view})
      },
      getSelect(){
        alert(window.getSelection())
      },
      onEditorReady(editor) { // 准备编辑器
      },
      onEditorBlur(){}, // 失去焦点事件
      onEditorFocus(){}, // 获得焦点事件
      onEditorChange(){}, // 内容改变事件
      publishArticle(){
        if ($.trim(this.input) === '') {
          alert("请写入标题")
          return
        }
        if ($.trim(this.textarea) === '') {
          alert("请写入简介")
          return
        }
        if ($.trim($('.content2').text()) === '') {
          alert("请写入内容")
          return
        }
        if ($.trim(this.value) === '') {
          alert("请选择类别")
          return
        }
        this.$confirm('确认发布吗？')
          .then(_ => {
            let article = {title : this.input, introduction :
              this.textarea, content : this.content, catalogName : this.value,
              personal : this.radio, count : 0,userName : JSON.parse(localStorage.getItem('userdata')).name
            }
            this.$axios.post('http://localhost:8888/article/publishArticle', article)
              .then(response => {
                if(response.data.success){
                  this.$message.success(response.data.name)
                } else {
                  this.$message.error(response.data.name)
                }
              })
              .catch(function (error) { // 请求失败处理
                delData()
              });
          })
          .catch(_ => {});
      }
    },
    created () {
    },
    computed: {
      editor() {
        return this.$refs.myQuillEditor.quill;
      },
    },
    mounted () {
      this.$axios.get('http://localhost:8888/getCatalogs')
        .then(response => {
          this.options = response.data
        })
        .catch(function (error) { // 请求失败处理
          delData()
        });
    }
  }
</script>

<style scoped>
  .choose-menu-one:hover{
    color: black;
  }
  .choose-menu-one{
    margin-right: 5%;
  }
  .content1{
    width: 50%;
    float: left;
  }
  .content2{
    width: 50%;
    float: left;
  }
  a{
    text-decoration-line: none;
  }
  .title-input{
    display: inline-block;width: 80%;
    padding: 8px;font-size: 18px;line-height: 24px;
    background-color: #fff;background-image: none;border: 0;border-radius: 4px;
  }
  .compute-number{
    line-height: 40px;
    font-size: 20px;
    margin-right: 8px;margin-top: 0.5%
  }
</style>
