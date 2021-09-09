package cn.edu.bistu.common.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public class Pagination {

    /**
     * 为指定的List列表做分页，如果分页页数<0，那么返回所有记录。
     *
     * @param page    page对象，包含有效数据:
     *                size：分页大小
     *                current：分页页数
     * @param rowList 待分页的列表
     * @param <T>     分页记录的类型
     * @return 返回包含分页数据的Page对象，Page中包含的数据的类型与传入的原始数据列表元素的类型相同。
     */
    public static <T> Page<T> page(Page page, List<T> rowList) {
        //记录总数
        Integer total = rowList.size();

        Integer offset = 0;
        Integer size = 0;
        Integer current = 0;
        Integer endset = 0;
        List<T> resultList = null;

        //判断是否分页
        if (getAll(page)) {
            offset = 0;
            size = total;

            //结束索引，如果结束索引大于记录总数
            endset = total;
            current = 1;
        } else {
            //准备分页数据，起始页大小和每页大小
            offset = ((Long) (page.offset())).intValue();
            size = ((Long) (page.getSize())).intValue();
            //结束索引，如果结束索引大于记录总数
            endset = offset + size > total ? total : offset + size;

            size = endset - offset;

            current = ((Long)(page.getCurrent())).intValue();
        }

        //截取分页数据
        resultList = rowList.subList(offset, endset);

        //设置分页对象
        Page<T> resultPage = new Page<>();
        resultPage.setTotal(total);
        resultPage.setSize(size);
        resultPage.setCurrent(current);
        resultPage.setRecords(resultList);
        return resultPage;
    }

    /**
     * 检查page对象，判断是否要返回所有记录；如果分页页数<0，那么返回所有记录。
     *
     * @return 返回所有记录则返回true；否则返回false
     */
    private static boolean getAll(Page page) {
        if (page == null) {
            return true;
        } else if (page.offset() == 0) {
            return true;
        }
        return false;
    }

}
