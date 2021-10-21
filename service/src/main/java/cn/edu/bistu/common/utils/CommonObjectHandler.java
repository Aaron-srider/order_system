package cn.edu.bistu.common.utils;

import cn.edu.bistu.common.BeanUtils;
import cn.edu.bistu.common.DateFormat;
import cn.edu.bistu.model.entity.ApprovalRecord;
import cn.edu.bistu.model.entity.Message;
import cn.edu.bistu.model.entity.auth.User;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


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

        ifNotClazzThenSetValue(metaObject, "deleted", 0, Message.class);
        ifClazzThenSetValue(metaObject, "isLock", 0, User.class);
        ifClazzThenSetValue(metaObject, "approvalDatetime", DateFormat.dateFormat(new Date()), ApprovalRecord.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateTime", DateFormat.dateFormat(new Date()));
    }

    /**
     * 只有当前对象是指定的类型时才自动填充
     */
    private void ifClazzThenSetValue(MetaObject metaObject, String field ,Object value, Class clazz) {
        if (metaObject.getOriginalObject().getClass().equals(clazz)) {
            metaObject.setValue(field, value);
        }
    }

    /**
     * 指定一些类，对这些类的对象，均不填充；其他类填充
     */
    private void ifNotClazzsThenSetValue(MetaObject metaObject, String field ,Object value, Class[] clazzList) {
        Class<?> originalObjectClazz = metaObject.getOriginalObject().getClass();

        List<Class> clazzs = Arrays.asList(clazzList);
        boolean contains = clazzs.contains(originalObjectClazz);

        if(!contains) {
            metaObject.setValue(field, value);
        }

    }

    /**
     * 指定一些类，对这些类的对象，均不填充；其他类填充
     */
    private void ifNotClazzThenSetValue(MetaObject metaObject, String field ,Object value, Class clazz) {
        Class[] classes = new Class[]{clazz};
        ifNotClazzsThenSetValue(metaObject, "deleted", 0, classes);

    }

}
