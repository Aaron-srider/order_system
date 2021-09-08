package cn.edu.bistu.dept.mapper;

import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
@Getter
public class DeptMapper {
    @Autowired
    ClazzMapper clazzMapper;

    @Autowired
    CollegeMapper collegeMapper;

    @Autowired
    MajorMapper majorMapper;

    @Autowired
    SecondaryDeptMapper secondaryDeptMapper;
}
