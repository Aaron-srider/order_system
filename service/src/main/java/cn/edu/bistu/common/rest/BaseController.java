package cn.edu.bistu.common.rest;

import cn.edu.bistu.common.MapService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BaseController {
    public Long getVisitorId(HttpServletRequest req) {
        MapService mapService = (MapService) req.getAttribute("userInfo");
        Long id = mapService.getVal("id", Long.class);
        return id;
    }

    public void cors(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Method", "*");
    }
}
