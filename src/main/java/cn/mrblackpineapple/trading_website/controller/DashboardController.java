package cn.mrblackpineapple.trading_website.controller;

import cn.mrblackpineapple.trading_website.aspect.Protector;
import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.service.DashboardService;
import cn.mrblackpineapple.trading_website.vo.ChartVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/dashboard")
public class DashboardController {

    @Resource
    private DashboardService dashboardService;

    @Protector(role = "Administrator")
    @GetMapping(value = "/staticCount")
    public Result<List<ChartVO>> staticCount() {
        return dashboardService.statisticCount();
    }

    @Protector(role = "Administrator")
    @GetMapping(value = "/productShelvesInfo/{day}")
    public Result<List<ChartVO>> productShelvesInfo(@PathVariable Integer day) {
        return dashboardService.productShelvesInfo(day);
    }
}