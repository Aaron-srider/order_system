package cn.edu.bistu.model.common.validation;

import cn.edu.bistu.model.common.CheckUserRole;
import cn.edu.bistu.model.common.validation.WhenStudent;
import cn.edu.bistu.model.common.validation.WhenTeacher;
import cn.edu.bistu.model.entity.auth.User;
import cn.edu.bistu.model.vo.UserVo;
import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomSequenceProvider implements DefaultGroupSequenceProvider<UserVo> {

    @Override
    public List<Class<?>> getValidationGroups(UserVo userVo) {
        List<Class<?>> defaultGroupSequence = new ArrayList<>();

        defaultGroupSequence.add(UserVo.class);

        if(userVo != null) {
            Long roleId = userVo.getRoleId();

            if(roleId!= null) {
                String roleCase = CheckUserRole.checkUserRole(roleId);

                if(roleCase.equals("teacher")) {
                    defaultGroupSequence.add(WhenTeacher.class);
                }
                if(roleCase.equals("student")) {
                    defaultGroupSequence.add(WhenStudent.class);
                }
            }

        }

        return defaultGroupSequence;
    }
}