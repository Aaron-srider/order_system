package cn.edu.bistu.model.common.result;

import com.alibaba.fastjson.JSONObject;

/**
 * 封装dao层的返回结果，作为dao层的统一返回格式，本质是一个JSONObject对象，结构如下:
 *          {
 *              "result": {xxx}
 *              "detailInfo": {xxxz}
 *          }
 * result是dao返回结果的有效载荷，而 detailInfo 则是result字段的附加信息。
 * 比如service层想要获取详细的User对象，那么在调用dao层后，得到的result将
 * 是从数据库中直接查找到的User，而detailInfo则是了User的xxx_id字段的扩展。
 *
 * result字段一般为实体类对象。
 * detailInfo则一般一个JSONObject对象，因为附加消息都是拼凑而成，通常更加
 * 复杂，难以用对象表示，所以选用JSONObject作为数据容器，方便构造数据。
 *
 * 对于Dao产生的Page<xxx>对象，应该赋值给DaoResult对象的的result字段，此时没有detailInfo字段。
 * @param <T> T表示"result"字段值的java类型
 */
public interface DaoResult<T> {

    /**
     * 为内部json赋值，调用时要注意，必须保证传入的json对象满足上述统一格式
     * @param value 外部传入的json对象
     */
    void setValue(JSONObject value);

    /**
     * 获取内部的json对象
     */
    JSONObject getValue();

    /**
     * 获取dao层的结果
     */
    T getResult();

    /**
     * 在dao层设置结果
     */
    DaoResult<T> setResult(T result);

    /**
     * 提供给dao层接口的调用者，获取dao的结果
     */
    JSONObject getDetailInfo();

    /**
     * 在dao层设置结果的附加信息
     */
    DaoResult<T> setDetailInfo(JSONObject detailInfo);

    /**
     * 在dao层添加结果的附加信息，key一般是信息对象的名字，obj是附加信息对象
     */
    void addDetailInfo(String key, Object obj);


    /**
     * toString方法，可以委托内部的JSONObject类的toString方法
     */
    @Override
    String toString();
}
