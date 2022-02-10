package cn.edu.bistu.common.rest;

import cn.edu.bistu.admin.user.Service.UserService;
import cn.edu.bistu.common.MapService;
import cn.edu.bistu.workOrder.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BaseController {

    @Autowired
    WorkOrderService workOrderService;

    @Autowired
    UserService userService;

    public Long getVisitorId(HttpServletRequest req) {
        MapService mapService = (MapService) req.getAttribute("userInfo");
        Long id = mapService.getVal("id", Long.class);
        return id;
    }

    public void cors(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Method", "*");
    }

    public boolean isAdmin(Long visitorId) {
        return userService.isAdmin(visitorId);
    }
}
