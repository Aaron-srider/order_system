package cn.edu.bistu.common.utils;

import cn.edu.bistu.common.BeanUtils;
import cn.edu.bistu.common.DateFormat;
import cn.edu.bistu.model.entity.Message;
import cn.edu.bistu.model.entity.auth.User;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class CommonObjectHandler implements MetaObjectHandler {
    public void setValue(MetaObject metaObject, String name, Object value){
        if(metaObject.hasGetter(name)) {
            metaObject.setValue(name, value);
        }
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("createTime",  DateFormat.dateFormat(new Date()));
        if (!metaObject.getOriginalObject().getClass().getTypeName().equals(Message.class.getTypeName())){
            metaObject.setValue("deleted", 0);
        }
        if (metaObject.getOriginalObject().getClass().equals(User.class)) {
            metaObject.setValue("isLock", 0);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateTime", DateFormat.dateFormat(new Date()));
    }
}
