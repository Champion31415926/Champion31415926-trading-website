package cn.mrblackpineapple.trading_website.controller;

import cn.mrblackpineapple.trading_website.aspect.Pager;
import cn.mrblackpineapple.trading_website.aspect.Protector;
import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.common.context.LocalThreadHolder;
import cn.mrblackpineapple.trading_website.dto.OrdersRequest;
import cn.mrblackpineapple.trading_website.model.Orders;
import cn.mrblackpineapple.trading_website.service.OrdersService;
import cn.mrblackpineapple.trading_website.vo.OrdersVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("orders")
public class OrdersController {

    @Resource
    private OrdersService ordersService;

    @Protector
    @PostMapping(value = "/save")
    public Result<String> save(@RequestBody Orders orders) {
        return ordersService.save(orders);
    }

    @Protector
    @PutMapping(value = "/update")
    public Result<String> update(@RequestBody Orders orders) {
        return ordersService.update(orders);
    }

    @Protector(role = "Administrator")
    @PostMapping(value = "/batchDelete")
    public Result<String> batchDelete(@RequestBody List<Integer> ids) {
        return ordersService.batchDelete(ids);
    }

    @Protector
    @Pager
    @PostMapping(value = "/queryUser")
    public Result<List<OrdersVO>> queryUser(@RequestBody OrdersRequest ordersRequest) {
        ordersRequest.setUserId(LocalThreadHolder.getUserId());
        return ordersService.query(ordersRequest);
    }

    @Protector
    @Pager
    @PostMapping(value = "/queryOrdersList")
    public Result<List<OrdersVO>> queryOrdersList(@RequestBody OrdersRequest ordersRequest) {
        return ordersService.queryOrdersList(ordersRequest);
    }

    @Protector(role = "Administrator")
    @PostMapping(value = "/returnMoney/{ordersId}")
    public Result<String> returnMoney(@PathVariable Integer ordersId) {
        return ordersService.returnMoney(ordersId);
    }

    @Protector(role = "Administrator")
    @Pager
    @PostMapping(value = "/query")
    public Result<List<OrdersVO>> query(@RequestBody OrdersRequest ordersRequest) {
        return ordersService.query(ordersRequest);
    }
}