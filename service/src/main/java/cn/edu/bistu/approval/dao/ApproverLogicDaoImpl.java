package cn.edu.bistu.approval.dao;

import cn.edu.bistu.approval.mapper.ApproverLogicMapper;
import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.common.result.SimpleDaoResultImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ApproverLogicDaoImpl implements ApproverLogicDao{

    @Autowired
    ApproverLogicMapper approverLogicMapper;

    @Override
    public DaoResult getApproverLogicByLogicId(long logicId) {
        return new SimpleDaoResultImpl<>().setResult(approverLogicMapper.selectById(logicId));
    }
}
