package cn.edu.bistu.model.common.result;

import cn.edu.bistu.model.entity.College;
import cn.edu.bistu.model.entity.Major;
import cn.edu.bistu.model.entity.auth.Role;
import cn.edu.bistu.model.entity.auth.User;
import com.alibaba.fastjson.JSONObject;


public class DaoResultImpl<T> implements DaoResult<T>{

    /**
     * 内部的JSONObject
     */
    private JSONObject value = new JSONObject();

    @Override
    public void setValue(JSONObject value) {
        this.value = value;
    }

    public JSONObject getValue() {
        return value;
    }

    public T getResult() {
        return (T) value.get("result");
    }

    public DaoResult<T> setResult(T result) {
        //this.result = result;
        value.put("result", result);
        return this;
    }

    public JSONObject getDetailInfo() {
        return (JSONObject) value.get("detailInfo");
    }

    public DaoResult<T> setDetailInfo(JSONObject detailInfo) {
        //this.detailInfo = detailInfo;
        value.put("detailInfo", detailInfo);
        return this;
    }

    public void addDetailInfo(String key, Object obj) {
        if (getDetailInfo() == null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(key, obj);
            setDetailInfo(jsonObject);
        } else {
            JSONObject detailInfo = getDetailInfo();
            detailInfo.put(key, obj);
        }
    }

    @Override
    public String toString() {
        return value.toJSONString();
    }

    public static void main(String[] args) {
        User user = new User();
        user.setSessionKey("ja;lsdkjf");
        user.setCollegeId(2);

        Major major = new Major();
        major.setId(1L);
        major.setName("testMajor");

        College college = new College();
        college.setId(1L);
        college.setName("testCollege");

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("major", major);
        jsonObject.put("college", college);

        DaoResultImpl<User> userDaoResult = new DaoResultImpl<>();

        userDaoResult.setResult(user);

        userDaoResult.setDetailInfo(jsonObject);


        System.out.println(userDaoResult);

        Role role  = new Role();
        role.setName(cn.edu.bistu.constants.Role.ADMIN);
        userDaoResult.addDetailInfo("role", role);

        System.out.println(userDaoResult);
    }
}


