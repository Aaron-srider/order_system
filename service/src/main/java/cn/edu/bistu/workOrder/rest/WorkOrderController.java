package cn.edu.bistu.workOrder.rest;

import cn.edu.bistu.approval.service.ApprovalService;
import cn.edu.bistu.common.MD5Utils;
import cn.edu.bistu.common.exception.ResultCodeException;
import cn.edu.bistu.common.rest.BaseController;
import cn.edu.bistu.common.utils.Pagination;
import cn.edu.bistu.flow.service.FlowNodeService;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.common.result.ServiceResultImpl;
import cn.edu.bistu.model.common.validation.Insert;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import cn.edu.bistu.model.vo.PageVo;
import cn.edu.bistu.model.vo.WorkOrderHistoryVo;
import cn.edu.bistu.model.vo.WorkOrderVo;
import cn.edu.bistu.workOrder.exception.AttachmentNotExistsException;
import cn.edu.bistu.common.config.ValidationWrapper;
import cn.edu.bistu.common.utils.MimeTypeUtils;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.model.common.result.Result;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.workOrder.service.WorkOrderHistoryService;
import cn.edu.bistu.workOrder.service.WorkOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.org.apache.xpath.internal.operations.Mult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Objects;

@Slf4j
@RestController
@Validated
@CrossOrigin
public class WorkOrderController extends BaseController {

    @Autowired
    ApprovalService approvalRecordService;

    @Autowired
    WorkOrderService workOrderService;

    @Autowired
    WorkOrderHistoryService workOrderHistorService;

    @Autowired
    FlowNodeService flowNodeService;

    /**
     * 返回用户的分页工单列表数据，支持工单标题模糊搜索
     * 入参：size(10)，current(1)，title(NULL)
     */
    @GetMapping("/workOrders")
    public Result list(PageVo pageVo,
                       WorkOrderVo workOrderVo,
                       HttpServletRequest req) throws NoSuchFieldException, IllegalAccessException {

        pageVo = Pagination.setDefault(pageVo.getCurrent(), pageVo.getSize());

        if (workOrderVo.getTitle() == null) {
            workOrderVo.setTitle("");
        }

        workOrderVo.setInitiatorId(getVisitorId(req));

        Page<WorkOrderVo> page = new Page<>(pageVo.getCurrent(), pageVo.getSize());

        //获取结果
        ServiceResult serviceResult = workOrderService.listWorkOrder(workOrderVo, page);
        return Result.ok(serviceResult.getServiceResult());
    }

    /**
     * 返回对应用户分页的历史工单列表，支持名称模糊搜索
     * 入参：size(10)，current(1)，title("")
     */
    @GetMapping("/workOrder/histories")
    public Result history(PageVo pageVo,
                          WorkOrderHistoryVo workOrderHistoryVo,
                          HttpServletRequest req) {

        pageVo = Pagination.setDefault(pageVo.getCurrent(), pageVo.getSize());

        if (workOrderHistoryVo.getWorkOrderVo() == null) {
            workOrderHistoryVo.setWorkOrderVo(new WorkOrderVo());
        }

        if (StringUtils.isEmpty(workOrderHistoryVo.getWorkOrderVo().getTitle())) {
            workOrderHistoryVo.getWorkOrderVo().setTitle("");
        }

        //封装工单对象
        workOrderHistoryVo.getWorkOrderVo().setInitiatorId(getVisitorId(req));

        //封装分页对象
        Page<WorkOrderHistoryVo> page = new Page<>(pageVo.getCurrent(), pageVo.getSize());

        ServiceResult result = workOrderHistorService.listWorkOrderHistory(workOrderHistoryVo, page);

        return Result.ok(result.getServiceResult());
    }

    /**
     * 根据工单号返回工单附件
     * 入参：工单号
     *
     * @return
     */
    @GetMapping("/workOrder/attachment/{workOrderId}/{attachmentDownloadId}")
    public void downloadAttachment(
            @PathVariable("workOrderId") @NotNull Long workOrderId,
            @PathVariable("attachmentDownloadId") @NotNull String attachmentDownloadId,
            HttpServletRequest req, HttpServletResponse resp) throws IOException {

        WorkOrder workOrder = checkWorkOrderExists(workOrderId);

        byte[] attachmentBytes = workOrder.getAttachment();

        if (attachmentBytes == null) {
            log.info("工单号为:\"" + workOrderId + "\"的工单没有附件");
            throw new AttachmentNotExistsException(null, ResultCodeEnum.ATTACHMENT_NOT_EXISTS);
        }

        String dbAttachmentDownloadId = workOrderService.getOne(new QueryWrapper<WorkOrder>().select("attachment_download_id")
                .eq("id", workOrder.getId())).getAttachmentDownloadId();

        if (attachmentDownloadId.equals(dbAttachmentDownloadId)) {
            retAttachment(workOrder.getAttachmentName(), attachmentBytes, resp);
        }
    }

    private WorkOrder checkWorkOrderExists(Long workOrderId) {
        //查询附件
        WorkOrder workOrder = workOrderService.getById(workOrderId);
        if (workOrder == null) {
            log.info("工单号为:\"" + workOrderId + "\"的工单不存在");
            throw new ResultCodeException("workOrderId: " + workOrderId, ResultCodeEnum.WORKORDER_NOT_EXISTS);
        }
        return workOrder;

    }

    private void retAttachment(String attachmentName, byte[] attachmentBytes, HttpServletResponse resp) throws IOException {
        //获取附件的MIME类型
        String mimeType = MimeTypeUtils.getType(attachmentName);
        //设置响应的MIME类型
        resp.setContentType(mimeType);
        log.info("工单附件的mimeType:" + mimeType);

        //让浏览器以附件形式处理响应数据
        resp.setHeader("Content-Disposition", "downloadAttachment; fileName=" + URLEncoder.encode(attachmentName, "UTF-8"));
        log.info("工单附件名称:" + attachmentName);

        resp.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

        //将二进制附件写入到http响应体中
        ServletOutputStream out = resp.getOutputStream();
        out.write(attachmentBytes, 0, attachmentBytes.length);
    }

    private boolean hasPermitToUploadAttachment(Long visitorId, Long workOrderInitiatorId) {
        return visitorId.equals(workOrderInitiatorId) || isAdmin(visitorId);
    }

    /**
     * 上传工单附件，只有工单发起者能调用此接口为自己的工单上传附件，
     * 若工单访问接口的用户与工单发起者不同，那么不允许访问
     * 入参：附件、工单号
     *
     * @return 如果缺失上传文件，返回错误代码102
     */
    @PutMapping("/workOrder/attachment/{workOrderId}")
    public Result uploadAttachment(
            @RequestPart("attachment") MultipartFile attachment
            , @PathVariable("workOrderId") @NotNull Long workOrderId
            , HttpServletRequest req
    ) throws IOException {

        WorkOrder workOrder = checkWorkOrderExists(workOrderId);

        //代码级别的用户权限检测，只有工单发起者和管理员可以工单上传附件
        Long visitorId = getVisitorId(req);
        if (!hasPermitToUploadAttachment(visitorId, workOrder.getInitiatorId())) {
            throw new ResultCodeException("visitor id: " + visitorId + "has not right", ResultCodeEnum.HAVE_NO_RIGHT);
        }

        //上传附件
        if (!attachmentFormatCorrect(attachment)) { //附件异常
            log.info("未检测到附件，上传附件失败");
            return Result.build(null, ResultCodeEnum.FRONT_DATA_MISSING);
        } else { //附件正常
            workOrder = new WorkOrder(workOrderId);

            //设置附件
            setWorkOrderAttachment(workOrder, attachment);

            //更新附件
            workOrderService.updateById(workOrder);

            workOrder.setAttachment(null);
            log.info("更新工单附件：{}" + workOrder);
            return Result.ok();
        }
    }

    private void setWorkOrderAttachment(WorkOrder workOrder, MultipartFile attachment) throws IOException {
        workOrder.setAttachment(attachment.getBytes());
        workOrder.setAttachmentName(attachment.getOriginalFilename());
        workOrder.setAttachmentSize(String.format("%.2f", attachment.getSize() / 1024.0));
        //生成附件下载id
        String md5DownloadId = generateDownloadId(workOrder.getId(),attachment.getOriginalFilename());
        workOrder.setAttachmentDownloadId(md5DownloadId);
    }

    private boolean attachmentFormatCorrect(MultipartFile attachment) {
        return !(attachment.getSize() == 0) && !Objects.equals(attachment.getOriginalFilename(), "");
    }

    private String generateDownloadId(Long workOrderId, String attachmentName) {
        String rowDownloadId = System.currentTimeMillis() + workOrderId + attachmentName;
        String md5DownloadId = MD5Utils.MD5(rowDownloadId);
        return md5DownloadId;
    }

    /**
     * 提交工单接口，保存工单信息，同时工单被流转到第一个审批节点
     *
     * @return
     */
    @PostMapping("/workOrder")
    public Result submitWorkOrder(
            @Validated(Insert.class) @RequestBody WorkOrderVo workOrderVo,
            HttpServletRequest req) {
        //获取工单提交用户id
        Long visitorId = getVisitorId(req);
        workOrderVo.setInitiatorId(visitorId);
        ServiceResult result = workOrderService.submitWorkOrder(workOrderVo);
        return Result.ok(result.getServiceResult());
    }


    /**
     * 撤回工单
     * <p>
     *
     * @param req
     * @return 如果工单未被审批，或撤回者不是工单发起者，都返回错误代码；否则撤回成功
     */
    @PutMapping("/workOrder/revoke")
    public Result revoke(@NotNull Long workOrderId,
                         HttpServletRequest req) {
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
        ServiceResult<WorkOrderVo> detail = workOrderService.detail(workOrderId, visitorId);
        return Result.ok(detail.getServiceResult());
    }

    /**
     * 查看历史工单详情
     */
    @GetMapping("/workOrder/history/detail")
    public Result historyDetail(@NotNull Long workOrderHistoryId, HttpServletRequest req) throws NoSuchFieldException, IllegalAccessException {

        WorkOrderHistory workOrderHistory = new WorkOrderHistory();
        workOrderHistory.setId(workOrderHistoryId);

        //获取结果
        ServiceResult<WorkOrderHistoryVo> serviceResult = workOrderHistorService.detail(workOrderHistory, getVisitorId(req));
        return Result.ok(serviceResult.getServiceResult());
    }

    @DeleteMapping("/workOrder/attachment/{id}")
    public Result deleteAttachment(HttpServletRequest req
            , @NotNull @PathVariable("id") Long id) {

        WorkOrder workOrder = workOrderService.getById(id);

        //代码级别的用户权限检测，只有工单发起者和管理员可以工单删除附件
        Long visitorId = getVisitorId(req);
        if (!hasPermitToUploadAttachment(visitorId, workOrder.getInitiatorId())) {
            throw new ResultCodeException("visitor id: " + visitorId + "has not right", ResultCodeEnum.HAVE_NO_RIGHT);
        }

        workOrderService.deleteAttachmentByWorkOrderId(id);
        return Result.ok();
    }
}
