package cn.edu.bistu.dept.mapper;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@Getter
public class DeptDao {

    @Autowired
    CollegeMapper collegeMapper;

    @Autowired
    MajorMapper majorMapper;

    @Autowired
    SecondaryDeptMapper secondaryDeptMapper;
}
