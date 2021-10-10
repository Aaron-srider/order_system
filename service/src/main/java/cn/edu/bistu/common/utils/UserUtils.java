package cn.edu.bistu.common.utils;

import cn.edu.bistu.User.mapper.UserDao;
import cn.edu.bistu.auth.mapper.RoleMapper;
import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.entity.auth.Role;
import com.alibaba.fastjson.JSONObject;
import com.sun.xml.internal.txw2.annotation.XmlCDATA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserUtils {

    @Autowired
    UserDao userDao;

    public Role convertConstant2Entity(cn.edu.bistu.constants.Role constant) {
        DaoResult<JSONObject> allRoles = userDao.getAllRoles();
        JSONObject result = allRoles.getResult();
        List<Role> roleList = (List<Role>) result.get("roleList");
        for (int i = 0; i < roleList.size(); i++) {
            if (roleList.get(i).getName().equals(constant.name())) {
                return roleList.get(i);
            }
        }
        return null;
    }
}
