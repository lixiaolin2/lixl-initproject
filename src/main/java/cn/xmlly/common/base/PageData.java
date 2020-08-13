package cn.xmlly.common.base;

import cn.xmlly.common.utils.ControllerUtils;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 分页返回值结构
 *
 * @param <T>
 */
@Data
public class PageData<T> {

    private long count;

    private List<T> data;

    private int pageNo = 1;

    private int limit = 20;

    public PageData() {
        HttpServletRequest request = ControllerUtils.getRequest();
        if (request != null) {
            String no = request.getParameter("pageNo");
            String limit = request.getParameter("limit");
            if (no != null && !no.equals("")) {
                this.pageNo = Integer.parseInt(no);
            }
            if (limit != null && !limit.equals("")) {
                this.limit = Integer.parseInt(limit);
            }
        }
    }

    public PageData(Integer pageNo, Integer pageSize) {
        if (pageNo != null) {
            this.pageNo = pageNo;
        }
        if (pageSize != null) {
            this.limit = pageSize;
        }
    }

}
