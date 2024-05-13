---
title: Activiti 7绕过processRuntime发起多租户流程
date: 2024-03-22
updated: 2024-03-22
categories: 工作流
codeHeightLimit: 400
tags:
- activiti
- java
---

昨天遇到一个棘手的问题，晚上回家躺床上想通了，今天来实践了一下，果然可行，先看思路分析：


## runtimeService和processRuntime的区别

`runtimeService`最开始出现在**activiti 5**，是用来执行挂起任务的一系列操作的，而多租户这个概念刚好也在`activiti 5`出现

`processRuntime`最开始出现在**activiti 7**，是为了支持**流程的启动、挂起、延迟执行、动态更新**等一系列操作。

`processRuntime`是`runtimeService`的进一步封装，是`runtimeService`、`repositoryService`和其他一些接口的整合。

## 问题描述

### 通过processRuntime启动

已知processRuntime启动流程的代码如下：

```java
        ProcessInstance processInstance = processRuntime.start(ProcessPayloadBuilder
                .start()
                .withProcessDefinitionKey(processTaskParams.getProcessKey())
                .withName(processTaskParams.getProcessName())
                .withVariable(userTask.getId()+"_byStarter",username)
                .withBusinessKey(businessKey)
                .build());
```

在这里很容易发现一个问题：**无法指定租户！**

是的，这个API无法指定租户，甚至他底层在执行的时候，可以通过SQL执行日志发现，他的SQL执行条件里边有一条：

```sql
where tenant_id ='' or tenant_id=null
```

啊？

activiti官方搞什么飞机？

这什么玩意？

下层接口都可以指定租户，到顶层了不行了？

那我只能顺从了，换接口！

### 通过runtimeService启动

> 根据最开始的解释我们可以知道，`processRuntime本质上只是runtimeService和repositoryService的一个整合`而已，那么解决方案就已经是呼之欲出了。
>
> **使用runtimeService启动流程**

好吧，来写代码！

```java
org.activiti.engine.runtime.ProcessInstance processInstance = runtimeService.
    startProcessInstanceByKeyAndTenantId(processTaskParams.getProcessKey(),
      			businessKey,
                vars,
                processTaskParams.getTenantId() ==null?"main":processTaskParams.getTenantId());
```

问题又来了.......

我原本以为tenantId能进去就解决了这个问题，但是当我运行测试以后我发现：

![1711074696969](./tenantProcess.assets/1711074696969.png)

啊？

我靠，传不进去流程名称，他也不给我手动加？

什么鬼？

这个api总共就四个参量，就没一个可以设置名字的........

#### 失败的解决案例

挣扎一下，activiti是通过MySQL进行流程上下文查询的，那么我们**直接去改数据库表不就行了么！**

最后debug到，是在一张叫`act_his_procinst`的表里，这张表里有个name字段，刚刚加进去的数据是空的！

测试一下给这行数据加名字，同时再起一个流程对照。

有效果...........个毛线。

流程实例依旧没有赋值。

好吧，两种办法都不行，那我只能.......~~下班！六点了！~~

## 解决方案

这个方案是我晚上躺床上想起来白天这个问题突然灵光一现想起来的：

**看activiti源码怎么写的啊！**

### 源码分析

```java
    public ProcessInstance start(StartProcessPayload startProcessPayload) {
        ProcessDefinition processDefinition = null;
        if (startProcessPayload.getProcessDefinitionId() != null) {
            processDefinition = this.processDefinition(startProcessPayload.getProcessDefinitionId());
        }

        if (processDefinition == null && startProcessPayload.getProcessDefinitionKey() != null) {
            processDefinition = this.processDefinition(startProcessPayload.getProcessDefinitionKey());
        }

        if (processDefinition == null) {
            throw new IllegalStateException("At least Process Definition Id or Key needs to be provided to start a process");
        } else if (!this.securityPoliciesManager.canWrite(processDefinition.getKey())) {
            throw new ActivitiForbiddenException("Operation not permitted for " + processDefinition.getKey() + " due security policy violation");
        } else {
            return this.processInstanceConverter.from(this.runtimeService.createProcessInstanceBuilder().processDefinitionId(startProcessPayload.getProcessDefinitionId()).processDefinitionKey(startProcessPayload.getProcessDefinitionKey()).businessKey(startProcessPayload.getBusinessKey()).variables(startProcessPayload.getVariables()).name(startProcessPayload.getName()).start());
        }
    }
```

这个看着太费劲了，抽丝剥茧一下！

```java
this.processInstanceConverter.from(this.runtimeService.createProcessInstanceBuilder().processDefinitionId(startProcessPayload.getProcessDefinitionId()).processDefinitionKey(startProcessPayload.getProcessDefinitionKey()).businessKey(startProcessPayload.getBusinessKey()).variables(startProcessPayload.getVariables()).name(startProcessPayload.getName()).start());
```

这下看懂了！

所以说总的思路就是，还是`runtimeService`，只是要换一种启动的包装方式，就是通过`processRuntime`的`processInstanceConverter`，借鸡下蛋！

### 代码编写

现在就可以开始写代码了。因为processRuntime封装的这个start方法就两个判断，一个判空一个安全性判断，但是我们不一定都用得上，所以说我们选择直接舍弃掉这些判断，因为有相关拦截。

代码总体如下：

```java
        ProcessInstance processInstance = processInstanceConverter.from(runtimeService.createProcessInstanceBuilder()
                .tenantId(processTaskParams.getTenantId() == null ? "main" : processTaskParams.getTenantId())
                .businessKey(businessKey)
                .processDefinitionKey(processTaskParams.getProcessKey())
                .variables(vars)
                .name(processTaskParams.getProcessName()).start());
```

其实就是一个依葫芦画瓢的过程。

现在看看效果！

### 测试

![1711075621150](./tenantProcess.assets/1711075621150.png)

可以看到，有流程名称了！

再去看看流转消息

![1711075656610](./tenantProcess.assets/1711075656610.png)

**也可以正常工作了！**

## 写在最后

这件事给我的启发就是，有事没事瞄两眼源码，总会有发现。虽然我也不知道activiti官方咋想的，这么个重要接口不给租户方案.......

