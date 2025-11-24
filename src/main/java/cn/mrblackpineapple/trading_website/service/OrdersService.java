package cn.mrblackpineapple.trading_website.service;

import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.dto.OrdersRequest;
import cn.mrblackpineapple.trading_website.model.Orders;
import cn.mrblackpineapple.trading_website.vo.OrdersVO;

import java.util.List;

public interface OrdersService {

    Result<String> save(Orders orders);

    Result<String> update(Orders orders);

    Result<String> batchDelete(List<Integer> ids);

    Result<List<OrdersVO>> query(OrdersRequest ordersRequest);

    Result<List<OrdersVO>> queryOrdersList(OrdersRequest ordersRequest);

    Result<String> returnMoney(Integer ordersId);

}