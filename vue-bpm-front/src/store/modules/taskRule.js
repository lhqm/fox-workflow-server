export default {
    namespaced: true,
    state: {
        taskRules: [{
          taskRuleId: 1,
          ruleKey:'byUser',
          taskRuleName: "按指定的人员"
        },{
          taskRuleId: 2,
          ruleKey:'byPosition',
          taskRuleName: "按指定的岗位"
        }, {
          taskRuleId: 3,
          ruleKey:'byRole',
          taskRuleName: "按指定的角色"
        }, {
          taskRuleId: 4,
          ruleKey:'byDept',
          taskRuleName: "按指定的部门"
        }, {
          taskRuleId: 5,
          ruleKey:'byGroup',
          taskRuleName: "按照指定的分组"
        }],
        returnWay: [{
          returnWayKey: 'none',
          returnWayName: "禁用"
        }, {
          returnWayKey: 'previousStep',
          returnWayName: "退回到上一步"
        }, {
          returnWayKey: 'allStepsTaken',
          returnWayName: "退回到历史步骤"
        }],
        sexSelect: [{
          value: 0,
          label: "女"
        }, {
          value: 1,
          label: "男"
        }],
        userSatet: [{
          value: 0,
          label: "禁用"
        }, {
          value: 1,
          label: "启用"
        }],
        startRules:[
          {
            taskRuleId: 0,
            ruleKey:'byStarter',
            taskRuleName: "系统默认(第一个节点使用)"
          }
        ]
    },
    getters: {
        getTaskRules: state => {
          return state.taskRules;
        },
        getStartRules: state => {
          return state.startRules
        },
        getUserSatet: state => {
          return state.userSatet
        },
        getSexSelect: state => {
          return state.sexSelect
        },
        returnWays:state=>{
          return state.returnWay
        }
    }
};
