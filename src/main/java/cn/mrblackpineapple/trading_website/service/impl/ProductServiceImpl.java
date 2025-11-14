package cn.mrblackpineapple.trading_website.service.impl;

import cn.mrblackpineapple.trading_website.common.api.ApiResult;
import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.common.context.LocalThreadHolder;
import cn.mrblackpineapple.trading_website.common.enums.InteractionEnum;
import cn.mrblackpineapple.trading_website.dto.OrdersDeliverRequest;
import cn.mrblackpineapple.trading_website.dto.OrdersRequest;
import cn.mrblackpineapple.trading_website.dto.OrdersUpdate;
import cn.mrblackpineapple.trading_website.dto.ProductRequest;
import cn.mrblackpineapple.trading_website.mapper.InteractionMapper;
import cn.mrblackpineapple.trading_website.mapper.OrdersMapper;
import cn.mrblackpineapple.trading_website.mapper.ProductMapper;
import cn.mrblackpineapple.trading_website.model.Interaction;
import cn.mrblackpineapple.trading_website.model.Orders;
import cn.mrblackpineapple.trading_website.model.Product;
import cn.mrblackpineapple.trading_website.service.ProductService;
import cn.mrblackpineapple.trading_website.vo.ChartVO;
import cn.mrblackpineapple.trading_website.vo.OrdersVO;
import cn.mrblackpineapple.trading_website.vo.ProductVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductMapper productMapper;
    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private InteractionMapper interactionMapper;

    private final static Integer AWAITING_PAY = 1;
    private final static Integer OK_PAY = 2;
    private final static Integer REPLY_REFUND = 2;

    @Override
    public Result<String> deliverGoods(OrdersDeliverRequest ordersDeliverRequest) {
        if (ordersDeliverRequest.getOrderId() == null) {
            return ApiResult.error("Order ID cannot be empty");
        }
        if (ordersDeliverRequest.getDeliveryAddressId() == null) {
            return ApiResult.error("Please set seller delivery address");
        }

        OrdersRequest ordersRequest = new OrdersRequest();
        ordersRequest.setId(ordersDeliverRequest.getOrderId());
        List<OrdersVO> ordersVOS = ordersMapper.query(ordersRequest);
        if (ordersVOS.isEmpty()) {
            return ApiResult.error("Order not found");
        }
        OrdersVO ordersVO = ordersVOS.get(0);

        Integer sellerId = ordersVO.getProductId() != null ? productMapper.queryById(ordersVO.getProductId()).getUserId() : null;
        if (sellerId != null && sellerId.equals(LocalThreadHolder.getUserId())) {
            return ApiResult.error("You cannot deliver your own product");
        }

        Orders orders = new Orders();
        orders.setId(ordersDeliverRequest.getOrderId());
        orders.setDeliverAdrId(ordersDeliverRequest.getDeliveryAddressId());
        orders.setDeliverTime(LocalDateTime.now());
        orders.setIsDeliver(true);

        ordersMapper.update(orders);
        return ApiResult.success("Product delivered successfully");
    }

    @Override
    public Result<String> save(Product product) {
        if (!StringUtils.hasText(product.getName())) {
            return ApiResult.error("Product name cannot be empty");
        }
        if (!StringUtils.hasText(product.getCoverList())) {
            return ApiResult.error("Please upload product cover");
        }
        if (product.getPrice() == null) {
            return ApiResult.error("Please enter price");
        }
        if (product.getInventory() == null) {
            return ApiResult.error("Inventory cannot be empty");
        }
        if (product.getCategoryId() == null) {
            return ApiResult.error("Please select product category");
        }
        if (product.getIsBargain() == null) {
            product.setIsBargain(false);
        }
        product.setUserId(LocalThreadHolder.getUserId());
        product.setCreateTime(LocalDateTime.now());
        productMapper.save(product);
        return ApiResult.success("Product added successfully");
    }

    @Override
    public Result<String> update(Product product) {
        if (!StringUtils.hasText(product.getName())) {
            return ApiResult.error("Product name cannot be empty");
        }
        if (!StringUtils.hasText(product.getCoverList())) {
            return ApiResult.error("Please upload product cover");
        }
        if (product.getPrice() == null) {
            return ApiResult.error("Please enter price");
        }
        if (product.getInventory() == null) {
            return ApiResult.error("Inventory cannot be empty");
        }
        if (product.getCategoryId() == null) {
            return ApiResult.error("Please select product category");
        }
        if (product.getIsBargain() == null) {
            product.setIsBargain(false);
        }
        productMapper.update(product);
        return ApiResult.success("Product updated successfully");
    }

    @Override
    public Result<String> batchDelete(List<Integer> ids) {
        productMapper.batchDelete(ids);
        return ApiResult.success("Products deleted successfully");
    }

    @Override
    public Result<List<ProductVO>> query(ProductRequest productRequest) {
        int totalCount = productMapper.queryCount(productRequest);
        List<ProductVO> productVOList = productMapper.query(productRequest);
        return ApiResult.success(productVOList, totalCount);
    }

    @Override
    public Result<String> buyProduct(OrdersUpdate ordersUpdate) {
        if (ordersUpdate.getProductId() == null) {
            return ApiResult.error("Product ID cannot be empty");
        }
        ProductRequest productRequest = new ProductRequest();
        productRequest.setId(ordersUpdate.getProductId());
        List<ProductVO> productVOS = productMapper.query(productRequest);
        if (productVOS.isEmpty()) {
            return ApiResult.error("Product information is abnormal");
        }
        ProductVO productVO = productVOS.get(0);
        if (productVO.getInventory() <= 0
                || (productVO.getInventory() - ordersUpdate.getBuyNumber()) < 0) {
            return ApiResult.error("Product stock is insufficient");
        }
        createOrders(ordersUpdate, productVO);
        ordersMapper.save(ordersUpdate);
        Product product = new Product();
        product.setId(productVO.getId());
        product.setInventory(productVO.getInventory() - ordersUpdate.getBuyNumber());
        productMapper.update(product);

        return ApiResult.success("Order placed successfully");
    }

    private void createOrders(Orders orders, ProductVO productVO) {
        orders.setCode(createOrdersCode());
        orders.setUserId(LocalThreadHolder.getUserId());
        orders.setTradeStatus(AWAITING_PAY);
        orders.setBuyPrice(productVO.getPrice());
        orders.setCreateTime(LocalDateTime.now());
    }

    private String createOrdersCode() {
        long timeMillis = System.currentTimeMillis();
        return String.valueOf(timeMillis);
    }

    @Override
    public Result<String> placeAnOrder(Integer ordersId) {
        Orders orders = new Orders();
        orders.setId(ordersId);
        orders.setTradeStatus(OK_PAY);
        orders.setIsConfirm(false);
        orders.setTradeTime(LocalDateTime.now());
        ordersMapper.update(orders);
        return ApiResult.success("Order placed successfully");
    }

    @Override
    public Result<String> refund(Integer ordersId) {
        OrdersRequest ordersRequest = new OrdersRequest();
        ordersRequest.setId(ordersId);
        ordersRequest.setRefundStatus(1);
        ordersRequest.setUserId(LocalThreadHolder.getUserId());
        int queryCount = ordersMapper.queryCount(ordersRequest);
        if (queryCount > 0) {
            return ApiResult.error("You have already applied for a refund, waiting for seller review, please do not apply repeatedly");
        }
        Orders orders = new Orders();
        orders.setId(ordersId);
        orders.setRefundStatus(REPLY_REFUND);
        ordersMapper.update(orders);
        return ApiResult.success("Refund requested successfully, please wait for seller review");
    }

    @Override
    public Result<String> getGoods(Integer ordersId) {
        OrdersRequest ordersRequest = new OrdersRequest();
        ordersRequest.setId(ordersId);
        ordersRequest.setUserId(LocalThreadHolder.getUserId());
        List<OrdersVO> ordersVOList = ordersMapper.query(ordersRequest);
        if (ordersVOList.isEmpty()) {
            return ApiResult.error("Order abnormal");
        }
        OrdersVO ordersVO = ordersVOList.get(0);
        if (ordersVO.getIsDeliver() == null) {
            if (ordersVO.getIsRefundConfirm() != null && ordersVO.getIsRefundConfirm()) {
                Orders orders = new Orders();
                orders.setId(ordersId);
                orders.setIsConfirm(true);
                orders.setIsConfirmTime(LocalDateTime.now());
                ordersMapper.update(orders);
            } else {
                return ApiResult.error("Seller has not shipped yet");
            }
        }
        Orders orders = new Orders();
        orders.setId(ordersVO.getId());
        orders.setIsConfirm(true);
        orders.setIsConfirmTime(LocalDateTime.now());
        ordersMapper.update(orders);
        return ApiResult.success("Confirmed receipt successfully");
    }

    @Override
    public Result<List<ChartVO>> queryProductInfo(ProductRequest productRequest) {
        List<Integer> productIds = productMapper.queryProductIds(productRequest.getUserId());
        if (productIds.isEmpty()) {
            return ApiResult.success(new ArrayList<>());
        }
        List<Interaction> interactionList = interactionMapper.queryByProductIds(productIds);
        long viewCount = getProductCount(interactionList, InteractionEnum.VIEW.getType());
        long saveCount = getProductCount(interactionList, InteractionEnum.SAVE.getType());
        long loveCount = getProductCount(interactionList, InteractionEnum.LOVE.getType());
        List<ChartVO> chartVOList = new ArrayList<>();
        ChartVO chartVOView = new ChartVO("Product Views", (int) viewCount);
        ChartVO chartVOSave = new ChartVO("Product Saves", (int) saveCount);
        ChartVO chartVOLove = new ChartVO("Product Loves", (int) loveCount);
        chartVOList.add(chartVOView);
        chartVOList.add(chartVOSave);
        chartVOList.add(chartVOLove);
        return ApiResult.success(chartVOList);
    }

    private long getProductCount(List<Interaction> interactionList, Integer type) {
        return interactionList.stream()
                .filter(interaction -> Objects.equals(type, interaction.getType()))
                .count();
    }

    @Override
    public Result<List<ProductVO>> queryProductList(Integer id) {
        ProductVO productVO = productMapper.queryById(id);
        Integer userId = productVO.getUserId();
        ProductRequest productRequest = new ProductRequest();
        productRequest.setUserId(userId);
        List<ProductVO> productVOS = productMapper.query(productRequest);
        return ApiResult.success(productVOS);
    }
}