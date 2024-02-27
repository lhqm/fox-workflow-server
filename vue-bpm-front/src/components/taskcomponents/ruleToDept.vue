<template>
  <el-row :gutter="20">
    <el-col  :xs="6" :sm="6" :md="6" :lg="6" :xl="6">
      <div>
        <el-card class="box-card" >
          <div slot="header" class="clearfix">
            <el-input placeholder="输入关键字进行过滤" v-model="filterText"></el-input>
          </div>
          <div class="text item">
            <el-tree
              class="filter-tree"
              :data="treedata"
              :props="defaultProps"
              :filter-node-method="filterNode"
              :default-expand-all="true"
              @node-click="expandUsers"
              ref="tree">
            </el-tree>
          </div>
        </el-card>
      </div>
    </el-col>
    <el-col  :xs="18" :sm="18" :md="18" :lg="18" :xl="18">
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <span>查询</span>
        </div>
        <el-row :gutter="12">
          <el-table :data="selectData">
            <el-table-column type="index" width="50" label="序号"></el-table-column>
            <el-table-column prop="id" label="部门ID" ></el-table-column>
            <el-table-column prop="name" label="部门名称"></el-table-column>
            <el-table-column label="所属公司" align="center">
              <template slot-scope="scope">
                <div class="operation-btn">
                  <el-button icon="el-icon-delete" slot="reference"
                             type="text"
                             class="btn-danger"
                             @click="removeNode(scope.row)">移除</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-row>
      </el-card>
    </el-col>
  </el-row>
</template>

<script>
import { getOrgData } from '@/api/orgm/orgm'
export default {
  name: "ruleToDept",
  data() {
    return {
      treedata:[],
      selectData:[],
      filterText: '',
      defaultProps: {
        value:'id',
        children: 'children',
        label: 'name'
      },
    };
  },
  components: {},
  mounted(){
    this.loadData();
  },
  methods: {
    saveTaskUser(){
      return this.selectData;
    },
    //tree输入查询
    filterNode(value, data) {
      if (!value) return true;
      return data.name.indexOf(value) !== -1;
    },//初始加载数据
    loadData(){
      getOrgData().then(res => {
        if(res.code=="200"){
          this.treedata = res.data.departList;
        }
      });
    },
    //节点点击事件
    expandUsers(node){
      const index=this.selectData.indexOf(node);
      if(index!=-1){
        return;
      }
      this.selectData.push(node);
    },
    removeNode(row){
      const index=this.selectData.indexOf(row);
      if(index!=-1){
        this.selectData.splice(index,1);
      }
    }
  },
};
</script>

<style scoped>
.m-b-1 {
  margin-bottom: 10px;
}
</style>
