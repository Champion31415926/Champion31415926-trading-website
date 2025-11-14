package cn.mrblackpineapple.trading_website.mapper;

import cn.mrblackpineapple.trading_website.dto.OrdersRequest;
import cn.mrblackpineapple.trading_website.model.Orders;
import cn.mrblackpineapple.trading_website.vo.OrdersVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrdersMapper {

    int save(Orders orders);

    List<OrdersVO> query(OrdersRequest ordersRequest);

    int queryCount(OrdersRequest ordersRequest);

    int update(Orders orders);

    void batchDelete(@Param(value = "ids") List<Integer> ids);

    List<OrdersVO> queryByProductIds(OrdersRequest ordersRequest);

}