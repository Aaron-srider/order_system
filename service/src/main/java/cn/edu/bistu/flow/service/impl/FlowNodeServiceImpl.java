package cn.edu.bistu.flow.service.impl;

import cn.edu.bistu.flow.mapper.FlowNodeMapper;
import cn.edu.bistu.flow.service.FlowNodeService;
import cn.edu.bistu.model.entity.FlowNode;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FlowNodeServiceImpl extends ServiceImpl<FlowNodeMapper, FlowNode> implements FlowNodeService {
}