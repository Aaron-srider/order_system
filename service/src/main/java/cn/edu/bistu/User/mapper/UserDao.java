package cn.edu.bistu.User.mapper;

import cn.edu.bistu.auth.mapper.RoleMapper;
import cn.edu.bistu.auth.mapper.UserMapper;
import cn.edu.bistu.auth.mapper.UserRoleMapper;
import cn.edu.bistu.common.JsonUtils;
import cn.edu.bistu.common.utils.Pagination;
import cn.edu.bistu.dept.mapper.DeptDao;
import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.common.result.DaoResultImpl;
import cn.edu.bistu.model.entity.Clazz;
import cn.edu.bistu.model.entity.College;
import cn.edu.bistu.model.entity.Major;
import cn.edu.bistu.model.entity.SecondaryDept;
import cn.edu.bistu.model.entity.auth.Role;
import cn.edu.bistu.model.entity.auth.User;
import cn.edu.bistu.model.entity.auth.UserRole;
import cn.edu.bistu.model.vo.UserVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Data
public class UserDao {

    @Autowired
    RoleMapper roleMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserRoleMapper userRoleMapper;
    @Autowired
    DeptDao deptDao;

    public DaoResult<User> getOneUserByOpenId(String openId) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("open_id", openId);
        DaoResult<User> daoResult = getOneUserByWrapper(wrapper);
        return daoResult;
    }

    public DaoResult<User> getOneUserById(Long id) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id);
        DaoResult<User> daoResult = getOneUserByWrapper(wrapper);
        return daoResult;
    }

    /**
     * 拿到用户详细的信息，将所有引用id都置换成实体类，将整个user封装到一个JSONObject中返回
     *
     * @param wrapper 查询条件，比如可以根据id查询，或根据openid查询，或者名字查询
     * @return 如果用户的id指针不为空，那么替换为相应的实体；如果用户为空，result字段为空；否则，返回用户完整信息。
     */
    public DaoResult<User> getOneUserByWrapper(QueryWrapper<User> wrapper) {
        User user = userMapper.selectOne(wrapper);

        JSONObject detailInfo = new JSONObject();
        if (user != null) {
            detailInfo = improveUserInfo(user);
        }

        DaoResultImpl<User> result = new DaoResultImpl<>();
        result.setResult(user);
        result.setDetailInfo(detailInfo);
        return result;
    }

    public DaoResult<List<JSONObject>> getUserListByWrapper(Page<UserVo> page, QueryWrapper<User> wrapper) {
        List<User> userList = userMapper.selectList(wrapper);


        List<JSONObject> resultList = new ArrayList<>();
        if (userList != null && userList.size() != 0) {
            for (User user : userList) {
                JSONObject oneUserResult = new JSONObject();
                oneUserResult.put("result", user);
                JSONObject detailInfo = improveUserInfo(user);
                oneUserResult.put("detailInfo", detailInfo);
                resultList.add(oneUserResult);
            }
        }

        Page<JSONObject> page1 = Pagination.page(page, resultList);

        DaoResultImpl<List<JSONObject>> result = new DaoResultImpl<>();
        result.setResult(page1.getRecords());
        page1.setRecords(null);
        result.setDetailInfo(JsonUtils.convertObj2JsonObj(page1));
        return result;
    }

    /**
     * 返回用户的角色。
     *
     * @param id 用户的id
     * @return 如果用户角色关系表中没有用户的id，那么方法返回null，如果存在对应关系，返回对应角色。
     */
    private List<Role> getUserRole(Long id) {
        List<Role> roleList = new ArrayList<>();
        List<UserRole> userRoleList = userRoleMapper.selectList(new QueryWrapper<UserRole>().eq("user_id", id));
        if(!userRoleList.isEmpty()) {
            for (UserRole userRole : userRoleList) {
                Long roleId = userRole.getRoleId();
                Role role = roleMapper.selectById(roleId);
                roleList.add(role);
            }
        }

        return roleList;
    }

    /**
     * @return 数据结构：
     * {
     * "class":{}
     * "college":{}
     * "secondaryDept":{}
     * "major":{}
     * "role":{}
     * }
     */
    private JSONObject improveUserInfo(User user) {
        JSONObject jsonObject = new JSONObject();
        if (user.getClassId() != null) {
            Clazz clazz = deptDao.getClazzMapper().selectById(user.getClassId());
            jsonObject.put("class", clazz);
        }
        if (user.getCollegeId() != null) {
            College college = deptDao.getCollegeMapper().selectById(user.getCollegeId());
            jsonObject.put("college", college);
        }
        if (user.getSecondaryDeptId() != null) {
            SecondaryDept secondaryDept = deptDao.getSecondaryDeptMapper().selectById(user.getSecondaryDeptId());
            jsonObject.put("secondaryDept", secondaryDept);

        }
        if (user.getMajorId() != null) {
            Major major = deptDao.getMajorMapper().selectById(user.getMajorId());
            jsonObject.put("major", major);
        }

        System.out.println(user.getId());
        List<Role> userRoleList = getUserRole(user.getId());
        jsonObject.put("roleList", userRoleList);
        return jsonObject;
    }
}
