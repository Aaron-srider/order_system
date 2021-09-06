package cn.edu.bistu.common.rest;

import cn.edu.bistu.common.MapService;

import javax.servlet.http.HttpServletRequest;

public class BaseController {
    public Long getVisitorId(HttpServletRequest req) {
        MapService mapService = (MapService) req.getAttribute("userInfo");
        Long id = mapService.getVal("id", Long.class);
        return id;
    }
}
