# order_system 和 Gpu管理系统的接口开发分支

## 基本说明

由于在项目中新增模块Model引起的循环依赖问题未想到良好的解决方案，所以Gpu管理系统对接模块全部放置在externalApi.extra包下。

该模块完全单向依赖于主系统，可直接移除externalApi.extra包以卸载该模块。


**技术选型方面**，虽然会新增原有项目的依赖，但出于易用性和项目成员熟悉程度的考虑，请求发送模块采用的是okHttp框架，而没有使用spring原生工具进行开发；

对Json的解析使用的则是spring原生的解析器Jackson
## 主要增加内容：

- 一个项目依赖：  _okHttp3_
- 一个接口： _**POST**_ :  _/gpu/api/message_   
- 一个审批逻辑： _"Gpu资源调度管理系统"_
- 一个**ExternalApi**的实现类：_GpuAdminSystemApi_

## 数字字典：
### 返回码说明：
gpu系统中返回码为200时，表示工单审批通过；为6000时表明审批拒绝

正常情况下该节点审批不会不通过
如果出现了这种情况，有且仅有以下原因：

1. 申请用户已提交过申请，并且由于资源已满尚未得到分配
2. 申请用户已在使用资源，在当前资源得到释放前不得重复申请
3. 由于在工单系统中录入信息有误导致的异常状况

### 资源类型（resourceType）说明：
- 类型：int
- 独占型资源：1010
- 共享型资源：3030

### 业务类型（bussinessType）说明：
- 类型：String
- Gpu申请：GPU_APPLY
- Gpu续期：GPU_RENEWAL


## 管理系统审批说明：
1. 获取工单发起者信息，包括学号、用户id等，以及用户指定信息（申请资源类型、申请天数）

```javascript
{

    "wosId": 25753288,

    "studentJobId": 2021011072,

    "username": "小王",

    "remark": "我要上网",  // 可为空

    "resourceType": 1010, // 详情见数字字典

    "businessType": "GPU_APPLY", // 详情见数字字典

    "applyDays": 1

}
```
2. 检验合法性

3. 尝试与Gpu系统建立连接，如果失败则抛出GpuSysConnectException异常

4. 获取工单审批结果，200则为通过，6000为拒绝，如果是其他返回码则会抛出异常（RunTimeException）

5. 根据审批结果给用户发送信息

## 新增接口说明：
新增接口： /gpu/api/message

该接口用于gpu系统给本系统中用户发送信息

该接口不被认证拦截器拦截

两系统间使用apiKey进行验证
