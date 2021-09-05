package cn.edu.bistu.workOrder.rest;

import cn.edu.bistu.approval.service.ApprovalService;
import cn.edu.bistu.common.exception.FrontDataMissingException;
import cn.edu.bistu.common.exception.WorkOrderNotExistsException;
import cn.edu.bistu.flow.service.FlowNodeService;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import cn.edu.bistu.workOrder.exception.AttachmentNotExistsException;
import cn.edu.bistu.common.BeanUtils;
import cn.edu.bistu.common.MapService;
import cn.edu.bistu.common.config.ValidationWrapper;
import cn.edu.bistu.common.utils.MimeTypeUtils;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.model.common.Result;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.vo.WorkOrderHistoryVo;
import cn.edu.bistu.model.vo.WorkOrderVo;
import cn.edu.bistu.workOrder.service.WorkOrderHistoryService;
import cn.edu.bistu.workOrder.service.WorkOrderService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

@Slf4j
@RestController
public class WorkOrderController {

    @Autowired
    ApprovalService approvalRecordService;

    @Autowired
    WorkOrderService workOrderService;

    @Autowired
    WorkOrderHistoryService workOrderHistorService;

    @Autowired
    FlowNodeService flowNodeService;

    @Autowired
    ValidationWrapper globalValidator;

    /**
     * 返回分页的工单列表，支持名称模糊搜索
     * 入参：size(10)，current(1)，title(NULL)
     *
     * @return
     */
    @PostMapping("/workOrder/list")
    public Result list(@RequestBody WorkOrderVo workOrderVo, HttpServletRequest req) {
        MapService userInfo = (MapService) req.getAttribute("userInfo");
        Long id = userInfo.getVal("id", Long.class);
        workOrderVo.setInitiatorId(id);

        IPage<WorkOrderVo> result = workOrderService.listWorkOrder(workOrderVo);

        Map<String, Object> resultMap = BeanUtils.bean2Map(result,
                new String[]{
                        "serialVersionUID",
                        "hitCount",
                        "optimizeCountSql",
                        "orders",
                        "isSearchCount"
                });

        return Result.ok(resultMap);
    }


    /**
     * 返回分页的历史工单列表，支持名称模糊搜索
     * 入参：size(10)，current(1)，title(NULL)
     *
     * @return
     */
    @PostMapping("/workOrder/history")
    public Result history(@RequestBody WorkOrderHistoryVo workOrderHistoryVo, HttpServletRequest req) {
        MapService userInfo = (MapService) req.getAttribute("userInfo");
        Long id = userInfo.getVal("id", Long.class);
        workOrderHistoryVo.setInitiatorId(id);

        IPage<WorkOrderHistoryVo> result = workOrderHistorService.listWorkOrderHistory(workOrderHistoryVo);

        Map<String, Object> resultMap = BeanUtils.bean2Map(result,
                new String[]{
                        "serialVersionUID",
                        "hitCount",
                        "optimizeCountSql",
                        "orders",
                        "isSearchCount"
                });

        List<WorkOrderHistoryVo> list = (List<WorkOrderHistoryVo>) resultMap.get("records");

        if (!list.isEmpty()) {
            log.debug(((List<WorkOrderHistoryVo>) resultMap.get("records")).get(0).getCreateTime().toString());
        }

        return Result.ok(resultMap);
    }

    /**
     * 根据工单号返回工单附件
     * 入参：工单号
     *
     * @return
     */
    @GetMapping("/workOrder/attachment")
    public void downloadAttachment(Long workOrderId, HttpServletResponse resp) throws IOException {

        if (workOrderId == null) {
            throw new FrontDataMissingException(new String[]{
                    "workOrderId"
            }, ResultCodeEnum.FRONT_DATA_MISSING);
        }

        //查询附件
        WorkOrder workOrder = workOrderService.getById(workOrderId);
        if (workOrder == null) {
            throw new WorkOrderNotExistsException("workOrderId: " + workOrderId, ResultCodeEnum.WORKORDER_NOT_EXISTS);
        }
        byte[] attachmentBytes = workOrder.getAttachment();

        //log.debug("" + attachmentBytes.length);

        if (attachmentBytes == null) {
            throw new AttachmentNotExistsException(null, ResultCodeEnum.ATTACHMENT_NOT_EXISTS);
        }

        //获取附件的MIME类型
        String mimeType = MimeTypeUtils.getType(workOrder.getAttachmentName());
        //设置响应的MIME类型
        resp.setContentType(mimeType);

        log.debug("mimeType:" + mimeType);

        //让浏览器以附件形式处理响应数据
        resp.setHeader("Content-Disposition", "downloadAttachment; fileName=" + URLEncoder.encode(workOrder.getAttachmentName(), "UTF-8"));

        log.debug("attachmentName:" + workOrder.getAttachmentName());

        //将二进制附件写入到http响应体中
        ServletOutputStream out = resp.getOutputStream();
        out.write(attachmentBytes, 0, attachmentBytes.length);
    }

    /**
     * 上传工单附件，只有工单发起者能调用此接口为自己的工单上传附件，
     * 若工单访问接口的用户与工单发起者不同，那么不允许访问
     * 入参：附件、工单号
     *
     * @return 如果缺失上传文件，返回错误代码102
     */
    @PostMapping("/workOrder/attachment")
    public Result uploadAttachment(
            @RequestPart("attachment") MultipartFile attachment
            , @RequestPart("workOrderId") String json
            , HttpServletRequest req
    ) throws IOException {

        //获取用户id
        MapService userInfo = (MapService) req.getAttribute("userInfo");
        Long visitorId = userInfo.getVal("id", Long.class);

        //从json中获取workOrderId
        JSONObject jsonObject = JSONObject.parseObject(json);
        Long workOrderId = jsonObject.getLong("workOrderId");

        //workOrderId缺失
        if (workOrderId == null) {
            log.debug("workOrderId missing");
            return Result.build(null, ResultCodeEnum.FRONT_DATA_MISSING);
        }

        WorkOrder workOrder = workOrderService.getById(workOrderId);

        //工单不存在
        if (workOrder == null) {
            log.debug("workOrderId：" + ResultCodeEnum.WORKORDER_NOT_EXISTS.toString());
            return Result.build(null, ResultCodeEnum.WORKORDER_NOT_EXISTS);
        }

        //接口访问者与工单发起者不同
        if (!workOrder.getInitiatorId().equals(visitorId)) {
            log.debug("id " + visitorId + "：" + ResultCodeEnum.HAVE_NO_RIGHT.toString());
            return Result.build(null, ResultCodeEnum.HAVE_NO_RIGHT);
        }

        //上传附件
        if (attachment.getSize() != 0 && !attachment.getOriginalFilename().equals("")) {
            byte[] bytes = attachment.getBytes();
            workOrder = new WorkOrder();
            workOrder.setId(workOrderId);
            workOrder.setAttachment(bytes);
            workOrder.setAttachmentName(attachment.getOriginalFilename());
            workOrderService.updateById(workOrder);
            return Result.ok();
        } else {
            return Result.build(null, ResultCodeEnum.FRONT_DATA_MISSING);
        }

    }

    /**
     * 提交工单接口，保存工单信息，同时工单被流转到第一个审批节点
     *
     * @return
     */
    @PostMapping("/workOrder/submission")
    public Result submitWorkOrder(@RequestBody WorkOrderVo workOrderVo,
                                  HttpServletRequest req) {

        //检验前端数据是否完整
        try {
            globalValidator.setRequiredPropsName(new String[]{"flowId", "content", "title"});

            globalValidator.checkParamIntegrity(workOrderVo);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            globalValidator.setPropsNameNull();
        }

        //获取工单提交用户id
        MapService userInfo = (MapService) req.getAttribute("userInfo");
        Long id = userInfo.getVal("id", Long.class);
        workOrderVo.setInitiatorId(id);                     //发起者id

        workOrderService.submitWorkOrder(workOrderVo);





        return Result.ok();
    }


    /**
     * 撤回工单
     *
     * @param json 撤回工单的id
     * @param req
     * @return 如果工单未被审批，或撤回者不是工单发起者，都返回错误代码；否则撤回成功
     */
    @PostMapping("/workOrder/revoke")
    public Result revoke(@RequestBody String json, HttpServletRequest req) {

        MapService userInfo = (MapService) req.getAttribute("userInfo");
        Long initiator = userInfo.getVal("id", Long.class);

        JSONObject jsonObject = JSONObject.parseObject(json);

        Long workOrderId = jsonObject.getLong("workOrderId");

        //工单id缺失
        if (workOrderId == null) {
            throw new FrontDataMissingException("workOrderId missing", ResultCodeEnum.FRONT_DATA_MISSING);
        }

        workOrderService.revoke(workOrderId, initiator);

        return Result.ok();
    }

    /**
     * 查看工单详情
     *
     * @param json 查询工单的id
     * @param req
     * @return
     */
    @PostMapping("/workOrder/detail")
    public Result detail(@RequestBody String json, HttpServletRequest req) {

        MapService userInfo = (MapService) req.getAttribute("userInfo");
        Long visitorId = userInfo.getVal("id", Long.class);

        JSONObject jsonObject = JSONObject.parseObject(json);

        Long workOrderId = jsonObject.getLong("workOrderId");

        if (workOrderId == null) {
            log.debug("workOrderId missing");
            return Result.build(null, ResultCodeEnum.FRONT_DATA_MISSING);
        }

        WorkOrder workOrder = workOrderService.getById(workOrderId);

        if (workOrder == null) {
            log.debug("workOrderId：" + ResultCodeEnum.WORKORDER_NOT_EXISTS.toString());
            return Result.build(null, ResultCodeEnum.WORKORDER_NOT_EXISTS);
        }

        if (!workOrder.getInitiatorId().equals(visitorId)) {
            log.debug("id " + visitorId + "：" + ResultCodeEnum.HAVE_NO_RIGHT.toString());
            return Result.build(null, ResultCodeEnum.HAVE_NO_RIGHT);
        }

        workOrder.setAttachment(null);

        Result result = workOrderService.detail(workOrder);

        return result;
    }

    /**
     * 查看历史工单详情
     *
     * @param json 查询工单的id
     * @param req
     * @return
     */
    @PostMapping("/workOrder/history/detail")
    public Result historyDetail(@RequestBody String json, HttpServletRequest req) {

        MapService userInfo = (MapService) req.getAttribute("userInfo");
        Long visitorId = userInfo.getVal("id", Long.class);

        JSONObject jsonObject = JSONObject.parseObject(json);

        Long workOrderHistoryId = jsonObject.getLong("workOrderHistoryId");

        if (workOrderHistoryId == null) {
            log.debug("workOrderHistoryId missing");
            return Result.build(null, ResultCodeEnum.FRONT_DATA_MISSING);
        }

        WorkOrderHistory workOrderHistory = workOrderHistorService.getById(workOrderHistoryId);

        if (workOrderHistory == null) {
            log.debug("workOrderId：" + ResultCodeEnum.WORKORDER_NOT_EXISTS.toString());
            return Result.build(null, ResultCodeEnum.WORKORDER_NOT_EXISTS);
        }

        if (!workOrderHistory.getInitiatorId().equals(visitorId)) {
            log.debug("id " + visitorId + "：" + ResultCodeEnum.HAVE_NO_RIGHT.toString());
            return Result.build(null, ResultCodeEnum.HAVE_NO_RIGHT);
        }


        workOrderHistory.setAttachment(null);

        Result result = workOrderHistorService.detail(workOrderHistory);

        return result;
    }

}
