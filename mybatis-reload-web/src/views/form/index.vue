<template>
  <div class="app-container">
    <el-table
    v-loading="listLoading"
    :data="list.filter(data => !search || data.name.toLowerCase().includes(search.toLowerCase()))"
    style="width: 100%">
    <el-table-column
      label="mybatis文件"
      prop="name">
    </el-table-column>
    <el-table-column
      label="路径"
      prop="path">
    </el-table-column>
    <el-table-column
      align="right">
      <template slot="header" slot-scope="scope">
        <el-input
          v-model="search"
          size="mini"
          placeholder="输入关键字搜索"/>
      </template>
      <template slot-scope="scope">
        <router-link :to="'/form/edit/'+scope.row.name">
            <el-button type="primary" size="small" icon="el-icon-edit">
              编辑
            </el-button>
          </router-link>
      </template>
    </el-table-column>
  </el-table>
  </div>
</template>

<script>
import { getList } from '@/api/table'
  export default {
    data() {
      return {
        listLoading:false,
        list: [],
        search: ''
      }
    },
      created() {
    this.getXmlData()
  },
    methods: {
      getXmlData(){
      this.listLoading = true
      getList().then(response => {
        this.list = response.data
        this.listLoading = false
      })
      }
    },
  }
</script>

<style scoped>
.line{
  text-align: center;
}
</style>

