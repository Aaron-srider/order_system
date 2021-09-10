package cn.edu.bistu.User.mapper;

import cn.edu.bistu.auth.mapper.RoleMapper;
import cn.edu.bistu.auth.mapper.UserMapper;
import cn.edu.bistu.auth.mapper.UserRoleMapper;
import cn.edu.bistu.dept.mapper.DeptMapper;
import cn.edu.bistu.model.common.DaoResult;
import cn.edu.bistu.model.common.DaoResultImpl;
import cn.edu.bistu.model.entity.Clazz;
import cn.edu.bistu.model.entity.College;
import cn.edu.bistu.model.entity.Major;
import cn.edu.bistu.model.entity.SecondaryDept;
import cn.edu.bistu.model.entity.auth.Role;
import cn.edu.bistu.model.entity.auth.User;
import cn.edu.bistu.model.entity.auth.UserRole;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
    DeptMapper deptMapper;

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
        DaoResultImpl<User> result = new DaoResultImpl<>();
        User user = userMapper.selectOne(wrapper);

        JSONObject detailInfo = new JSONObject();
        if(user != null) {
            detailInfo = improveUserInfo(user);
            result.setDetailInfo(detailInfo);
        }
        result.setResult(user);
        return result;
    }

    /**
     * 返回用户的角色。
     *
     * @param id 用户的id
     * @return 如果用户角色关系表中没有用户的id，那么方法返回null，如果存在对应关系，返回对应角色。
     */
    private Role getUserRole(Long id) {
        UserRole userRole = userRoleMapper.selectById(id);
        if(userRole != null) {
            Long roleId = userRole.getRoleId();
            Role role = roleMapper.selectById(roleId);
            return role;
        }
        return null;
    }

    /**
     * @return 数据结构：
     *      {
     *          "class":{}
     *          "college":{}
     *          "secondaryDept":{}
     *          "major":{}
     *          "role":{}
     *      }
     */
    private JSONObject improveUserInfo(User user) {
        JSONObject jsonObject = new JSONObject();
        if (user.getClassId() != null) {
            Clazz clazz = deptMapper.getClazzMapper().selectById(user.getClassId());
            jsonObject.put("class", clazz);
        }
        if (user.getCollegeId() != null) {
            College college = deptMapper.getCollegeMapper().selectById(user.getCollegeId());
            jsonObject.put("college", college);
        }
        if (user.getSecondaryDeptId() != null) {
            SecondaryDept secondaryDept = deptMapper.getSecondaryDeptMapper().selectById(user.getSecondaryDeptId());
            jsonObject.put("secondaryDept", secondaryDept);

        }
        if(user.getMajorId()!= null) {
            Major major = deptMapper.getMajorMapper().selectById(user.getMajorId());
            jsonObject.put("major", major);
        }

        Role userRole = getUserRole(user.getId());
        jsonObject.put("role", userRole);
        return jsonObject;
    }
}
