package cn.edu.bistu.auth.mapper;

import cn.edu.bistu.dept.mapper.DeptMapper;
import cn.edu.bistu.model.entity.Clazz;
import cn.edu.bistu.model.entity.College;
import cn.edu.bistu.model.entity.Major;
import cn.edu.bistu.model.entity.SecondaryDept;
import cn.edu.bistu.model.entity.auth.Role;
import cn.edu.bistu.model.entity.auth.User;
import cn.edu.bistu.model.entity.auth.UserRole;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@Data
public class AuthDao {

    @Autowired
    RoleMapper roleMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserRoleMapper userRoleMapper;
    @Autowired
    DeptMapper deptMapper;

    /**
     * 拿到用户详细的信息，将所有引用id都置换成实体类，将整个user封装到一个JSONObject中返回
     *
     * @param wrapper 查询条件，比如可以根据id查询，或根据openid查询，或者名字查询
     * @return 如果用户的id指针不为空，那么替换为相应的实体；如果用户为空，返回null；否则，返回用户完整信息。
     */
    public JSONObject getOneUserByWrapper(QueryWrapper<User> wrapper) {
        User user = userMapper.selectOne(wrapper);
        if(user != null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("result", user);
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
        return null;
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

}
