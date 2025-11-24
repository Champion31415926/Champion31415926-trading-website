package cn.mrblackpineapple.trading_website.service.impl;

import cn.mrblackpineapple.trading_website.common.api.ApiResult;
import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.common.context.LocalThreadHolder;
import cn.mrblackpineapple.trading_website.dto.OrdersRequest;
import cn.mrblackpineapple.trading_website.mapper.OrdersMapper;
import cn.mrblackpineapple.trading_website.mapper.ProductMapper;
import cn.mrblackpineapple.trading_website.model.Orders;
import cn.mrblackpineapple.trading_website.model.Product;
import cn.mrblackpineapple.trading_website.service.OrdersService;
import cn.mrblackpineapple.trading_website.vo.OrdersVO;
import cn.mrblackpineapple.trading_website.vo.ProductVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class OrdersServiceImpl implements OrdersService {

    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private ProductMapper productMapper;

    private final static Integer AWAITING_PAY = 1;
    private final static Integer OK_PAY = 2;
    private final static Integer CHANNEL_PAY = 3;
    private final static Integer REFUNDING = 1;
    private final static Integer REPLY_REFUND = 2;
    private final static Integer REJECT_REFUND = 3;
    private final static Integer OK_REFUND = 4;

    @Override
    public Result<String> save(Orders orders) {
        if (orders.getProductId() == null) {
            return ApiResult.error("Product ID cannot be empty");
        }

        ProductVO productVO = productMapper.queryById(orders.getProductId());
        if (productVO == null) {
            return ApiResult.error("Product parameters are abnormal");
        }

        if (Objects.equals(productVO.getUserId(), LocalThreadHolder.getUserId())) {
            return ApiResult.error("Cannot purchase your own product!");
        }

        if (orders.getAddressId() == null) {
            return ApiResult.error("Please select a shipping address");
        }
        if (orders.getBuyNumber() == null || orders.getBuyNumber() == 0) {
            return ApiResult.error("Purchase quantity cannot be empty");
        }
        if (productVO.getInventory() - orders.getBuyNumber() < 0) {
            return ApiResult.error("Product stock is insufficient");
        }

        createOrderInfo(productVO.getPrice(), orders);
        ordersMapper.save(orders);

        Product product = new Product();
        product.setId(productVO.getId());
        product.setInventory(productVO.getInventory() - orders.getBuyNumber());
        productMapper.update(product);

        return ApiResult.success("Order created successfully, please pay as soon as possible");
    }


    private void createOrderInfo(BigDecimal price, Orders orders) {
        orders.setBuyPrice(price);
        orders.setBuyPrice(orders.calculateTotalPrice());
        orders.setCode(String.valueOf(System.currentTimeMillis()));
        orders.setUserId(LocalThreadHolder.getUserId());
        orders.setTradeStatus(AWAITING_PAY);
        orders.setRefundStatus(REFUNDING);
        orders.setCreateTime(LocalDateTime.now());
    }

    @Override
    public Result<String> update(Orders orders) {
        ordersMapper.update(orders);
        if (orders.getRefundStatus() != null && orders.getRefundStatus() == OK_REFUND) {
            orders.setCreateTime(LocalDateTime.now());
            ordersMapper.update(orders);
        }
        if (orders.getTradeStatus() != null && orders.getTradeStatus() == CHANNEL_PAY) {
            inventoryRestore(orders.getId());
        }
        return ApiResult.success("Order updated successfully");
    }

    @Override
    public Result<String> batchDelete(List<Integer> ids) {
        ordersMapper.batchDelete(ids);
        return ApiResult.success("Orders deleted successfully");
    }

    private void inventoryRestore(Integer id) {
        OrdersRequest ordersRequest = new OrdersRequest();
        ordersRequest.setId(id);
        List<OrdersVO> ordersVOS = ordersMapper.query(ordersRequest);
        if (!ordersVOS.isEmpty()) {
            OrdersVO ordersVO = ordersVOS.get(0);
            Integer buyNumber = ordersVO.getBuyNumber();
            Integer productId = ordersVO.getProductId();
            ProductVO productVO = productMapper.queryById(productId);
            if (productVO != null) {
                Product product = new Product();
                product.setId(productVO.getId());
                product.setInventory(productVO.getInventory() + buyNumber);
                productMapper.update(product);
            }
        }
    }

    @Override
    public Result<List<OrdersVO>> query(OrdersRequest ordersRequest) {
        int totalCount = ordersMapper.queryCount(ordersRequest);
        List<OrdersVO> ordersVOList = ordersMapper.query(ordersRequest);
        return ApiResult.success(ordersVOList, totalCount);
    }

    @Override
    public Result<List<OrdersVO>> queryOrdersList(OrdersRequest ordersRequest) {
        List<Integer> productIds = productMapper.queryProductIds(LocalThreadHolder.getUserId());
        if (productIds.isEmpty()) {
            return ApiResult.success(new ArrayList<>());
        }
        ordersRequest.setProductIds(productIds);
        List<OrdersVO> ordersVOList = ordersMapper.queryByProductIds(ordersRequest);
        return ApiResult.success(ordersVOList);
    }

    @Override
    public Result<String> returnMoney(Integer ordersId) {
        Orders orders = new Orders();
        orders.setId(ordersId);
        orders.setRefundStatus(OK_REFUND);
        orders.setIsRefundConfirm(true);
        orders.setRefundTime(LocalDateTime.now());
        ordersMapper.update(orders);
        return ApiResult.success("Refund successful");
    }
}