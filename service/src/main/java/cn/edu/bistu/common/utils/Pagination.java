package cn.edu.bistu.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public class Pagination {

    public static <T> Page<T> page(Page page, List<T> rowList) {
        Page<T> page1 = new Page<>();

        int offset = 0;
        int size = rowList.size();
        if(page != null) {
            offset = ((Long) (page.offset())).intValue();
            size = ((Long) (page.getSize())).intValue();
        }

        List<T> resultList = rowList.subList(offset, offset + size);

        page1.setTotal(resultList.size());
        page1.setSize(size);

        page1.setRecords(resultList);
        return page1;
    }

}
