package cn.edu.bistu.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormat {

    /**
     * 格式化日期成字符串类型
     */
    public static String dateFormat(Date date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat();
        dateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
        String format = dateFormat.format(date);
        return format;

    }
}
