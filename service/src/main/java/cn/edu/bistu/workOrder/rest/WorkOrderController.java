package cn.edu.bistu.workOrder.rest;

import cn.edu.bistu.approval.service.ApprovalService;
import cn.edu.bistu.common.config.ParamIntegrityChecker;
import cn.edu.bistu.common.exception.FrontDataMissingException;
import cn.edu.bistu.common.exception.WorkOrderNotExistsException;
import cn.edu.bistu.common.rest.BaseController;
import cn.edu.bistu.flow.service.FlowNodeService;
import cn.edu.bistu.model.common.ServiceResult;
import cn.edu.bistu.model.common.validation.Insert;
import cn.edu.bistu.model.common.validation.Revoke;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import cn.edu.bistu.model.vo.PageVo;
import cn.edu.bistu.workOrder.exception.AttachmentNotExistsException;
import cn.edu.bistu.common.MapService;
import cn.edu.bistu.common.config.ValidationWrapper;
import cn.edu.bistu.common.utils.MimeTypeUtils;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.model.common.Result;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.workOrder.service.WorkOrderHistoryService;
import cn.edu.bistu.workOrder.service.WorkOrderService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.IOException;
import java.net.URLEncoder;

@Slf4j
@RestController
@Validated
public class WorkOrderController extends BaseController {

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

    @Autowired
    ParamIntegrityChecker paramIntegrityChecker;

    /**
     * 返回分页的工单列表，支持名称模糊搜索
     * 入参：size(10)，current(1)，title(NULL)
     *
     * @return
     */
    @GetMapping("/workOrders")
    public Result list(PageVo pageVo,
                       WorkOrder workOrder,
                       HttpServletRequest req) throws NoSuchFieldException, IllegalAccessException {


        if (pageVo.getSize() == null) {
            pageVo.setSize(10);
        }
        if (pageVo.getCurrent() == null) {
            pageVo.setCurrent(1);
        }
        if (workOrder.getTitle() == null) {
            workOrder.setTitle("");
        }
        //paramIntegrityChecker.setOptionalPropsName(MapService.map()
        //        .putMap("size", 10)
        //        .putMap("current", 1)
        //);
        //paramIntegrityChecker.checkMapParamIntegrity(workOrderMap);

        //封装工单对象
        workOrder.setInitiatorId(getVisitorId(req));
        //String title = workOrderMap.getVal("title", String.class);

        //封装分页对象
        Page<WorkOrder> page = new Page<>(pageVo.getCurrent(), pageVo.getSize());
        //Integer size = (Integer) workOrderMap.get("size");
        //Integer current = (Integer) workOrderMap.get("current");

        //获取结果
        ServiceResult<JSONObject> serviceResult = workOrderService.listWorkOrder(workOrder, page);
        JSONObject result = serviceResult.getServiceResult();
        return Result.ok(result);
    }


    /**
     * 返回分页的历史工单列表，支持名称模糊搜索
     * 入参：size(10)，current(1)，title("")
     *
     */
    @GetMapping("/workOrder/histories")
    public Result history(PageVo pageVo,
                          WorkOrderHistory workOrderHistory,
                          HttpServletRequest req) throws NoSuchFieldException, IllegalAccessException {

        if (pageVo.getSize() == null) {
            pageVo.setSize(10);
        }
        if (pageVo.getCurrent() == null) {
            pageVo.setCurrent(1);
        }
        if (workOrderHistory.getTitle() == null) {
            workOrderHistory.setTitle("");
        }

        //paramIntegrityChecker.setOptionalPropsName(MapService.map()
        //        .putMap("size", 10)
        //        .putMap("current", 1)
        //        .putMap("title", "")
        //);
        //paramIntegrityChecker.checkMapParamIntegrity(workOrderHistoryMap);

        //封装工单对象
        workOrderHistory.setInitiatorId(getVisitorId(req));
        //String title = workOrderHistoryMap.getVal("title", String.class);

        //封装分页对象
        Page<WorkOrderHistory> page = new Page<>(pageVo.getCurrent(), pageVo.getSize());
        //Integer size = workOrderHistoryMap.getVal("size", Integer.class);
        //Integer current = workOrderHistoryMap.getVal("current", Integer.class);

        Page<JSONObject> result = workOrderHistorService.listWorkOrderHistory(workOrderHistory, page);

        return Result.ok(result);
    }

    /**
     * 根据工单号返回工单附件
     * 入参：工单号
     *
     * @return
     */
    @GetMapping("/workOrder/attachment")
    public void downloadAttachment(@NotNull Long workOrderId, HttpServletResponse resp) throws IOException {

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
    @PutMapping("/workOrder/attachment")
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
    @PostMapping("/workOrder")
    public Result submitWorkOrder(
            @Validated(Insert.class) @RequestBody WorkOrder workOrder,
                                  HttpServletRequest req) {

        //获取工单提交用户id
        Long visitorId = getVisitorId(req);
        workOrder.setInitiatorId(visitorId);                     //发起者id
        workOrderService.submitWorkOrder(workOrder);
        return Result.ok();
    }


    /**
     * 撤回工单
     *
     //* @param json 撤回工单的id
     * @param req
     * @return 如果工单未被审批，或撤回者不是工单发起者，都返回错误代码；否则撤回成功
     */
    @PutMapping("/workOrder/revoke")
    public Result revoke(@NotNull Long workOrderId,
                         HttpServletRequest req){
        Long approverId = getVisitorId(req);

        workOrderService.revoke(workOrderId, approverId);

        return Result.ok();
    }

    /**
     * 查看工单详情
     * <p>
     * //* @param json 查询工单的id：
     * workOrderId      工单id
     *
     * @return
     */
    @GetMapping("/workOrder/detail")
    public Result detail(@NotNull Long workOrderId, HttpServletRequest req) throws NoSuchFieldException, IllegalAccessException {

        Long visitorId = getVisitorId(req);

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

        //获取结果
        ServiceResult<JSONObject> serviceResult = workOrderService.detail(workOrder);
        JSONObject result = serviceResult.getServiceResult();
        return Result.ok(result);
    }

    /**
     * 查看历史工单详情
     * <p>
     * //* @param json 查询工单的id
     *
     * @param req
     * @return
     */
    @GetMapping("/workOrder/history/detail")
    public Result historyDetail(@NotNull Long workOrderHistoryId, HttpServletRequest req) throws NoSuchFieldException, IllegalAccessException {
        Long visitorId = getVisitorId(req);

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

        //获取结果
        ServiceResult<JSONObject> serviceResult = workOrderHistorService.detail(workOrderHistory);
        JSONObject result = serviceResult.getServiceResult();
        return Result.ok(result);
    }

}
