package cn.edu.bistu.User.Service;

import cn.edu.bistu.User.mapper.UserDao;
import cn.edu.bistu.auth.mapper.UserMapper;
import cn.edu.bistu.dept.mapper.DeptDao;
import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.common.result.ServiceResultImpl;
import cn.edu.bistu.model.entity.Major;
import cn.edu.bistu.model.entity.SecondaryDept;
import cn.edu.bistu.model.entity.auth.Role;
import cn.edu.bistu.model.entity.auth.User;
import cn.edu.bistu.model.vo.UserVo;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.edu.bistu.common.BeanUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    DeptDao deptDao;

    @Override
    public ServiceResult<JSONObject> getAllUsers(Page<UserVo> page, UserVo userVo) {

        //初始化查询条件
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();

        //设置公有过滤条件name
        if (!BeanUtils.isEmpty(userVo.getName())) {
            userQueryWrapper.like("name", userVo.getName());
        }

        //设置学生的有关条件，className, majorName
        if ("student".equals(userVo.getRoleCategory())) {

            //利用className模糊查询
            if (!BeanUtils.isEmpty(userVo.getName())) {
                userQueryWrapper.like("clazz_name", userVo.getClazzName());
            }

            if (!BeanUtils.isEmpty(userVo.getMajorName())) {
                List<Major> majorList = userDao.getDeptDao().getMajorMapper().selectList(new QueryWrapper<Major>().select("id").like("name", userVo.getMajorName()));
                List<Long> majorIds = new ArrayList<>();
                for (Major major : majorList) {
                    majorIds.add(major.getId());
                }
                if (!majorIds.isEmpty()) {
                    userQueryWrapper.in("major_id", majorIds);
                }
            }

            if (!BeanUtils.isEmpty(userVo.getStudentId())) {
                userQueryWrapper.like("student_id", userVo.getStudentId());
            }
        }

        //设置教师的有关条件，deptName
        if ("teacher".equals(userVo.getRoleCategory())) {
            List<SecondaryDept> secondaryDeptList = userDao.getDeptDao().getSecondaryDeptMapper().selectList(new QueryWrapper<SecondaryDept>().select("id").like("name", userVo.getSecondaryDeptName()));
            List<Long> deptIds = new ArrayList<>();
            for (SecondaryDept secondaryDept : secondaryDeptList) {
                deptIds.add(secondaryDept.getId());
            }
            if (!deptIds.isEmpty()) {
                userQueryWrapper.in("secondary_dept_id", deptIds);
            }

            if (!BeanUtils.isEmpty(userVo.getJobId())) {
                userQueryWrapper.like("job_id", userVo.getJobId());
            }
        }


        if ("all".equals(userVo.getRoleCategory())) {
            if (!BeanUtils.isEmpty(userVo.getJobId())&&BeanUtils.isEmpty(userVo.getJobId())) {
                userQueryWrapper.like("job_id", userVo.getJobId());
            } else if(BeanUtils.isEmpty(userVo.getJobId())&&!BeanUtils.isEmpty(userVo.getJobId())) {
                userQueryWrapper.like("student_id", userVo.getStudentId());
            } else if(!BeanUtils.isEmpty(userVo.getJobId()) && !BeanUtils.isEmpty(userVo.getStudentId())){
                userQueryWrapper.like("job_id", userVo.getJobId());
                userQueryWrapper.or();
                userQueryWrapper.like("student_id", userVo.getStudentId());
            }
        }

        DaoResult<List<JSONObject>> userListByWrapper = userDao.getUserListByWrapper(page, userQueryWrapper);

        return new ServiceResultImpl<JSONObject>(userListByWrapper.getValue());
    }

    @Override
    public void lock(User user) {
        userDao.getUserMapper().updateById(user);
    }

    @Override
    public ServiceResult<JSONObject> updateUser(UserVo userVo) {
        //更新用户
        userDao.getUserMapper().updateById(userVo);
        //返回更新后的用户
        DaoResult<User> updatedUser = userDao.getOneUserById(userVo.getId());
        return new ServiceResultImpl<>(updatedUser.getValue());
    }

    @Override
    public boolean isAdmin(Long id) {
        DaoResult<User> oneUserById = userDao.getOneUserById(id);
        JSONObject userDetailInfo = oneUserById.getDetailInfo();
        List<Role> roleList = (List<Role>)userDetailInfo.get("roleList");


        log.debug("admin: " + cn.edu.bistu.constants.Role.ADMIN.toString());
        log.debug("operator: " + cn.edu.bistu.constants.Role.OPERATOR.toString());

        for (Role role : roleList) {
            log.debug("roleName: " + role.getName());
            if(role.getName().equals(cn.edu.bistu.constants.Role.ADMIN.toString()) ||
                    role.getName().equals(cn.edu.bistu.constants.Role.OPERATOR.toString()) ) {
                return true;
            }
        }

        return false;
    }


}
