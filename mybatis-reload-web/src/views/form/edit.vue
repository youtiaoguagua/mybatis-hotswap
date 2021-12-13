<template>
<div class="app-container">
  <div class="overflow">
      <el-button type="success" :loading="loading" @click="submitXml">提交</el-button>
    </div>
  <div class="json-editor">
    <textarea ref="textarea" />
  </div>
</div>
</template>

<script>
import CodeMirror from 'codemirror'
import 'codemirror/addon/lint/lint.css'
import 'codemirror/lib/codemirror.css'
import 'codemirror/theme/rubyblue.css'
require('script-loader!jsonlint')
import 'codemirror/mode/javascript/javascript'
import 'codemirror/mode/xml/xml'
import 'codemirror/addon/lint/lint'
import 'codemirror/addon/lint/json-lint'
import 'codemirror/addon/hint/xml-hint';
import { getXmlDetail ,submitXmlInfo} from '@/api/table'


export default {
  data() {
    return {
      xmlEditor: false,
      oldXml:"",
      loading:false,
      xmlData:null
    }
  },
  created(){
      this.fetchXmlDetail()
  },
  mounted() {
    this.xmlEditor = CodeMirror.fromTextArea(this.$refs.textarea, {
      lineNumbers: true,
      mode: 'application/xml',
      gutters: ['CodeMirror-lint-markers'],
      // theme: 'eclipse',
      lint: true
    })
    // this.jsonEditor.setValue(JSON.stringify(this.value, null, 2))
    this.xmlEditor.on('change', cm => {
      this.$emit('changed', cm.getValue())
      this.$emit('input', cm.getValue())
    })
  },
  methods: {
    submitXml(){
        let newXml = this.getValue()
        if(newXml==this.oldXml){
          this.$message({
          message: '什么都没修改哦！',
          type: 'warning'
        });
        }else{
          this.loading = true
          this.xmlData.xml = newXml
            submitXmlInfo(this.xmlData).then(resp=>{
                  if(resp.data==true){
                    this.$message({
          message: '修改成功！',
          type: 'success'
        });
                  }
            }).catch(err=>{
              
            }).finally(resp=>{
                  this.loading = false
            })
        }
    },
    fetchXmlDetail(){
      console.log(this.$route)
      getXmlDetail(this.$route.params.id).then(resp=>{
        console.log(resp.data.xml)
        this.xmlData = resp.data
        let res = resp.data.xml.trim("\"")
        this.oldXml = res
        this.xmlEditor.setValue(res)
      })
    },
    getValue() {
      return this.xmlEditor.getValue()
    }
  }
}
</script>

<style lang="scss" scoped>
.json-editor {
  height: 100%;
  position: relative;
  ::v-deep {
    .CodeMirror {
      height: auto;
      min-height: 300px;
    }
    .CodeMirror-scroll {
      min-height: 300px;
    }
    .cm-s-rubyblue span.cm-string {
      color: #F08047;
    }
  }
}

.overflow{
  display:flex;
  justify-content:center;
  margin: 0 0 20px 0;
}
</style>