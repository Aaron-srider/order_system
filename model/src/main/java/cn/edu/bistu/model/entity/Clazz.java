package cn.edu.bistu.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("class")
public class Clazz {
    private Long id;
    private String name;
    private Long majorId;
}
