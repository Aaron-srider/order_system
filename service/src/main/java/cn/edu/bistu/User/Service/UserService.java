package cn.edu.bistu.User.Service;

import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.entity.auth.User;
import cn.edu.bistu.model.vo.UserVo;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface UserService {
    /**
     * 根据userVo对象中的属性设置user的查询条件，根据前端传来的角色查询类型不同，分为以下几种情况：
     *      所有角色类型共有的查询条件：
     *          * name
     *
     *      roleCategory=student:
     *          * studentId
     *          * clazzName -> clazzIds
     *          * majorName -> majorIds
     *
     *      roleCategory=teacher:
     *          * jobId
     *          * secondaryDeptName -> deptIds
     *
     *      roleCategory=all:
     *          * jobId
     *         or studentId
     *
     * 如果属性值不为空，那么将其设置为where过滤条件;如果为空，不设置为where过滤条件。
     * @param page
     * @param userVo
     * @return
     */
    public ServiceResult<JSONObject> getAllUsers(Page<UserVo> page, UserVo userVo);

    public void lock(User user);

    public ServiceResult<JSONObject> updateUser(UserVo userVo);
}
