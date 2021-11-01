package cn.edu.bistu.externalApi;

import cn.edu.bistu.admin.User.mapper.UserDao;
import cn.edu.bistu.approval.WorkOrderFlower;
import cn.edu.bistu.common.exception.ResultCodeException;
import cn.edu.bistu.constants.ApprovalOperation;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.model.entity.ApprovalRecord;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.entity.auth.User;
import cn.edu.bistu.model.vo.FlowVo;
import cn.edu.bistu.model.vo.WorkOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExternalApiImpl implements ExternalApi {

    @Autowired
    UserDao userDao;

    @Autowired
    WorkOrderFlower workOrderFlower;

    @Override
    public String getSignature() {
        return "THIS_SYSTEM";
    }

    @Override
    public ExternalApiResult execute(WorkOrderVo fullPreparedWorkOrder, FlowVo flowVo) {
        //为工单发起者（研究生）添加导师
        User user = new User();
        user.setId(fullPreparedWorkOrder.getInitiator().getId());
        if (fullPreparedWorkOrder.getUserSpecifiedId() == null) {
            throw new ResultCodeException("",
                    ResultCodeEnum.USER_SPECIFIED_ID_NULL);
        }
        user.setTutorId(fullPreparedWorkOrder.getUserSpecifiedId());
        userDao.simpleUpdateUserById(user);
        ExternalApiResultImpl externalApiResult = new ExternalApiResultImpl();
        externalApiResult.setApprovalOperation(ApprovalOperation.PASS);
        return externalApiResult;
    }
}
