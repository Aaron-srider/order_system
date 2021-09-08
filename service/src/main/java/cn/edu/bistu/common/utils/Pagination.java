package cn.edu.bistu.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public class Pagination {

    public static <T> Page<T> page(Page page, List<T> rowList) {


        //准备分页数据，起始页大小和每页大小
        int offset = 0;
        int size = rowList.size();
        if (page != null) {
            offset = ((Long) (page.offset())).intValue();
            size = ((Long) (page.getSize())).intValue();
        }

        //记录总数
        Integer total = rowList.size();

        //结束索引
        Integer endset = offset + size;

        //如果结束索引大于记录总数
        if (total < endset) {
            endset = total;
        }

        //截取分页数据
        List<T> resultList = rowList.subList(offset, endset);

        //设置分页对象
        Page<T> page1 = new Page<>();
        page1.setTotal(total);
        page1.setSize(endset - offset);
        page1.setCurrent(page.getCurrent());
        page1.setRecords(resultList);
        return page1;
    }

}
