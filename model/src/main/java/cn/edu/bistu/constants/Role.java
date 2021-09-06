package cn.edu.bistu.constants;

import java.util.Objects;

public enum Role {

    /**
     * 系统管理员
     */
    ADMIN(1),

    /**
     * 业务员
     */
    OPERATOR(2),

    /**
     * 院级领导
     */
    COLLEGE_LEVEL_LEADER(3),

    /**
     * 部门领导
     */
    DEPT_LEVEL_LEADER(4),

    /**
     * 教师
     */
    TEACHER(5),

    /**
     * 本科生
     */
    UNDERGRADUATE(6),

    /**
     * 研究生
     */
    POSTGRADUATE(7);

    private Integer value;

    Role(Integer value) {
        this.value = value;
    }

    //getter of value
    public Integer getValue() {
        return value;
    }

    /**
     * convert int value to Role constant
     *
     * @return corresponding Role constant of the value; null if the value is invalid
     */
    public static Role valueOf(Integer value) {
        Role[] values = values();
        for (Role role : values) {
            if (Objects.equals(role.getValue(), value)) {
                return role;
            }
        }
        return null;
    }

}
