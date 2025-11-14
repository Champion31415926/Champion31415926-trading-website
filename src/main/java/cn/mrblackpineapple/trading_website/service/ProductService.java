package cn.mrblackpineapple.trading_website.service;

import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.dto.OrdersDeliverRequest;
import cn.mrblackpineapple.trading_website.dto.OrdersUpdate;
import cn.mrblackpineapple.trading_website.dto.ProductRequest;
import cn.mrblackpineapple.trading_website.model.Product;
import cn.mrblackpineapple.trading_website.vo.ChartVO;
import cn.mrblackpineapple.trading_website.vo.ProductVO;

import java.util.List;

public interface ProductService {

    Result<String> save(Product product);

    Result<String> update(Product product);

    Result<String> batchDelete(List<Integer> ids);

    Result<List<ProductVO>> query(ProductRequest productRequest);

    Result<String> buyProduct(OrdersUpdate ordersUpdate);

    Result<String> placeAnOrder(Integer ordersId);

    Result<String> refund(Integer ordersId);

    Result<List<ChartVO>> queryProductInfo(ProductRequest productRequest);

    Result<String> getGoods(Integer ordersId);

    Result<List<ProductVO>> queryProductList(Integer id);

    Result<String> deliverGoods(OrdersDeliverRequest ordersDeliverRequest);

}