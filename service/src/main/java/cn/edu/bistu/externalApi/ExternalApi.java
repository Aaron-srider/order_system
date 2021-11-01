package cn.edu.bistu.externalApi;

import cn.edu.bistu.model.entity.Flow;
import cn.edu.bistu.model.vo.FlowVo;
import cn.edu.bistu.model.vo.WorkOrderVo;

public interface ExternalApi {

    public String getSignature();

    public ExternalApiResult execute(WorkOrderVo fullPreparedWorkOrder, FlowVo flowVo);
}
