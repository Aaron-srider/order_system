package cn.edu.bistu.model.common;

public class CheckUserRole {

    public static String checkUserRole(Long roleId) {
        String roleCase;
        switch (roleId.intValue()) {
            case 3:
            case 4:
            case 5:
                roleCase = "teacher";
                break;
            case 6:
            case 7:
                roleCase = "student";
                break;
            default:
                roleCase = null;
        }
        return roleCase;
    }
}
