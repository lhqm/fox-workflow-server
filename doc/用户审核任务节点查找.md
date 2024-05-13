---
title: Activiti 7获取用户审核任务节点
date: 2024-02-29
updated: 2024-02-29
categories: 工作流
codeHeightLimit: 400
tags:
- activiti
- java
---

## 前言
这是工作流模块第一篇记录，但是不一定在将来会作为第一篇入手文章以面向更多的读者。

此篇记录的主要目的是关于如何获取`activiti 7某个流程定义的用户任务信息`。

## 获取目的
这篇文章的出现比较突兀。我并没有介绍activiti，而是直接跳到了自动化流程部署的相关内容。

这个流程系统的本质是`流程中台`，用于`其他业务系统或OA系统`远程推送部署实例到本流程中台，然后通过本流程中台去驱动其他业务系统的状态。

看到这里我们不妨思考几个问题:

1. 在工作流系统的辅助下，交互系统（或租户系统）`从一个状态驱动到另一个状态`，如何实现？
2. 如何将流程相关的业务**全部托管**给工作流系统，以实现业务解耦？
3. 如何在多租户模式下，更高效且优雅地通知客户端，告知`流程的相关设计组织形式`，以及及时通知更改？

**聪明的你会怎么做呢？**

## 从节点获取谈设计思路
获取节点的目的是什么？我想这个是最直接也是最难以理解的一个问题。

首先谈任务处理这件事，事件处理我们最经常能想到的就是`状态机`和`工作流引擎`两种方式。

**什么是状态机？**~~还问这个问题的先叉出去~~,状态机就是类似我定义1是用户申请，2是系统审核，3是审核员审核，4是网站发布员审核，然后才到发布。

因此去选择一个状态，往往你只需要将状态往线上动态字典里一加，然后根据动态字典的key按图索骥即可。

但是放到动态流程里呢？我也需要上述这样`<1,用户申请>`的数据结构。而且这个结构需要可以`动态修改`。

那么很显然，我只需要把一个流程里边的相关节点数据取出来就行了，并且我只需要取`用户任务`的流转节点。

因为用户任务，才是`驱动状态更改的关键要素`。

现在我们明确了一条这样的路：

> - 其他工作流驱动客户端需要全托管
> - 完全驱动底层状态机来完成
> - 状态机信息可以来自工作流定义
> - 驱动状态机变迁的是用户任务
> - 在工作流定义之中选择用户任务

## 实现
> 相关注释在代码中已经写明了，就不再赘述了
### 代码展示
```java
    @Autowired
    private RepositoryService repositoryService;

    public Map<String,String> getUserTaskByProcessDefinition(String definitionId){
//        获取到流程的模型实例
        BpmnModel bpmnModel = repositoryService.getBpmnModel(definitionId);
//        在模型的流程表里选择第一个流程作为流程对象定义（一般也只有一个）
        Process process = bpmnModel.getProcesses().get(0);
//        返回数据，这里开始对流程信息进行解构，获取到所有用户任务。因为用户任务才能参与审核，审核才会唤醒流转，流转才会改变状态
        return  process
//                获取到所有流程节点
                .getFlowElements()
//                转为stream流
                .stream()
//                筛选出用户任务
                .filter(item -> item instanceof UserTask)
//                将筛选得到的任务包装为用户任务的对象类型
                .map(item -> (UserTask) item)
//                收集，转为map，得到流程的定义：名字和任务流转编号，以实现状态机驱动
                .collect(Collectors.toMap(UserTask::getId, UserTask::getName));
    }
```

### 流程定义ID的由来
需要注意的是，`流程`、`流程实例`、`流程定义`是三个东西。

可以这样理解：`Integer id=666`，Integer是一个`流程定义`，因为这个流程定义，实际上可以Integer id，他也可以Integer age。但是你的id，类似是流程，他就是一个变量，可以消亡滴，他有自己的生命周期。

那么666这个实例化的过程，实例化以后就是流程实例。

实际场景中，我们可能大部分情况下都是只知道流程的id，可以从流程ID获得流程实例，然后再通过流程实例，取得流程的定义ID。

那么我们可以这样做：

#### 注入流程运行时上下文
```java
    @Autowired
    private ProcessRuntime processRuntime;
```
#### 通过流程实例ID逐步获取流程定义
流程的实例ID可以来源于很多地方，比如说：`自己传入流程实例`、`传入任务节点实例`、`流程定义列表`等等。
```java
    ProcessInstance processInstance = processRuntime.processInstance(task.getProc_inst_id_());
    String processDefinitionId = processInstance.getProcessDefinitionId();
```

