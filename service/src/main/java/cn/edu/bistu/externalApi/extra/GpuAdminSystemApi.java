package cn.edu.bistu.externalApi.extra;

import cn.edu.bistu.common.JsonUtils;
import cn.edu.bistu.constants.ApprovalOperation;
import cn.edu.bistu.externalApi.ExternalApi;
import cn.edu.bistu.externalApi.ExternalApiResult;
import cn.edu.bistu.externalApi.ExternalApiResultImpl;
import cn.edu.bistu.externalApi.extra.exception.GpuSysConnectException;
import cn.edu.bistu.externalApi.extra.exception.GpuSysErrorException;
import cn.edu.bistu.externalApi.extra.exception.GpuSysUserFormatException;
import cn.edu.bistu.message.service.MessageService;
import cn.edu.bistu.model.entity.Message;
import cn.edu.bistu.model.vo.FlowVo;
import cn.edu.bistu.model.vo.UserVo;
import cn.edu.bistu.model.vo.WorkOrderVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * <p>Gpu 资源调度管理系统api。该系统负责在前面所有节点都通过时执行gpu资源的分配，并向对应用户发送信息。
 * 如果该节点审批不通过，有且仅有以下原因：
 * <p>1.申请用户已提交过申请，并且由于资源已满尚未得到分配
 * <p>2.申请用户已在使用资源，在当前资源得到释放前不得重复申请
 * <p>3.由于在工单系统中录入信息有误导致的异常状况
 * <p>
 * @author YHK
 */
@Component
@Slf4j
public class GpuAdminSystemApi implements ExternalApi {

    @Autowired
    GpuSysConnector gpuSysConnector;

    @Autowired
    MessageService messageService;

    @Override
    public String getSignature() {
        return "GPU_SYSTEM";
    }


    @Override
    public ExternalApiResult execute(WorkOrderVo fullPreparedWorkOrder, FlowVo flowVo) {

        ExternalApiResultImpl res = new ExternalApiResultImpl();
        UserVo initiator = fullPreparedWorkOrder.getInitiator();
        String json = null;

        // 建立数据传输对象
        GpuUserDto gpuUser = new GpuUserDto();

        log.info("相关用户：" + initiator.getName() + "\tid：" + initiator.getId());
        log.info("开始处理用户请求");

        try {
            gpuUser.setId(initiator.getId());
            // 设置jobId
            gpuUser.setStudentJobId(initiator.getStudentJobId());

            // 设置资源类型(用户选定)
            gpuUser.setResourceType(Math.toIntExact(fullPreparedWorkOrder.getUserSpecifiedId()));

            gpuUser.setUsername(initiator.getName());
            gpuUser.setRemark(fullPreparedWorkOrder.getTitle() + fullPreparedWorkOrder.getContent());
            gpuUser.setApplyDays(1);
            gpuUser.setBusinessType("GPU_APPLY");
        } catch (NumberFormatException  e) {
            log.error("格式错误 ========= 详细信息：" + e.getMessage());
            log.error("相关用户：" + initiator.getName() + "\tid：" + initiator.getId() + "\t学号：" + initiator.getStudentJobId());
            throw new GpuSysUserFormatException(e.getMessage());
        }



        json = JsonUtils.convertObj2JsonObj(gpuUser).toJSONString();



        try {
            Response response = null;

            try {
                response = gpuSysConnector.postRequest("/ptr/user/apply", json);
            } catch (IOException e) {
                throw new GpuSysConnectException("与gpu系统建立连接失败");
            }

            String body = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            Message message = new Message();
            // 用于接收Gpu系统返回的结果
            ResponseDto result = objectMapper.readValue(body, ResponseDto.class);

            int code = response.code() / 100 ;

            // 如果不为200，则为拒绝
            if (code == 2){
                res.setApprovalOperation(ApprovalOperation.PASS);
                message.setTitle("Gpu资源申请成功");
            }else if(code == 5){
                throw new GpuSysErrorException("Gpu系统内部发生错误" + result.getMsg());
            }else if (code == 60){
                res.setApprovalOperation(ApprovalOperation.REJECT);
                message.setTitle("Gpu资源申请被拒绝");
            }else {
                throw new RuntimeException("请求错误 ：" + result.getMsg());
            }

            // 给申请用户发送处理信息
            message.setSender(21L);
            message.setIsShowSender(1);
            message.setDescription(result.getMsg());
            message.setReceiver(initiator.getId());

            messageService.sendMessage(message);
            log.info("处理完成");
            return res;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
