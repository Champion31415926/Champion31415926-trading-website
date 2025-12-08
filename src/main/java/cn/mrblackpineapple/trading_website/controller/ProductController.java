package cn.mrblackpineapple.trading_website.controller;

import cn.mrblackpineapple.trading_website.aspect.Log;
import cn.mrblackpineapple.trading_website.aspect.Pager;
import cn.mrblackpineapple.trading_website.aspect.Protector;
import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.common.context.LocalThreadHolder;
import cn.mrblackpineapple.trading_website.dto.OrdersDeliverRequest;
import cn.mrblackpineapple.trading_website.dto.OrdersUpdate;
import cn.mrblackpineapple.trading_website.dto.ProductRequest;
import cn.mrblackpineapple.trading_website.model.Product;
import cn.mrblackpineapple.trading_website.service.ProductService;
import cn.mrblackpineapple.trading_website.vo.ChartVO;
import cn.mrblackpineapple.trading_website.vo.ProductVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Resource
    private ProductService productService;

    @Protector
    @Log(detail = "Place Order")
    @PostMapping(value = "/buyProduct")
    public Result<String> buyProduct(@RequestBody OrdersUpdate ordersUpdate) {
        return productService.buyProduct(ordersUpdate);
    }

    @Protector
    @Log(detail = "Product Listing")
    @PostMapping(value = "/save")
    public Result<String> save(@RequestBody Product product) {
        return productService.save(product);
    }

    @Protector
    @Log(detail = "Product Update")
    @PutMapping(value = "/update")
    public Result<String> update(@RequestBody Product product) {
        return productService.update(product);
    }

    @Protector(role = "Administrator")
    @PostMapping(value = "/batchDelete")
    public Result<String> batchDelete(@RequestBody List<Integer> ids) {
        return productService.batchDelete(ids);
    }

    @Pager
    @PostMapping(value = "/query")
    public Result<List<ProductVO>> query(@RequestBody ProductRequest productRequest) {
        return productService.query(productRequest);
    }

    @Protector
    @PostMapping(value = "/placeAnOrder/{ordersId}")
    public Result<String> placeAnOrder(@PathVariable Integer ordersId) {
        return productService.placeAnOrder(ordersId);
    }

    @Protector
    @Log(detail = "Apply for Refund")
    @PostMapping(value = "/refund/{ordersId}")
    public Result<String> refund(@PathVariable Integer ordersId) {
        return productService.refund(ordersId);
    }

    @Protector
    @Log(detail = "Confirm Receipt")
    @PostMapping(value = "/getGoods/{ordersId}")
    public Result<String> getGoods(@PathVariable Integer ordersId) {
        return productService.getGoods(ordersId);
    }

    @Protector
    @Log(detail = "Product Delivery")
    @PostMapping(value = "/deliverGoods")
    public Result<String> deliverGoods(@RequestBody OrdersDeliverRequest ordersDeliverRequest) {
        return productService.deliverGoods(ordersDeliverRequest);
    }

    @Protector
    @PostMapping(value = "/queryProductInfo")
    public Result<List<ChartVO>> queryProductInfo(@RequestBody ProductRequest productRequest) {
        productRequest.setUserId(LocalThreadHolder.getUserId());
        return productService.queryProductInfo(productRequest);
    }

    @Protector
    @PostMapping(value = "/queryUser")
    public Result<List<ProductVO>> queryUser(@RequestBody ProductRequest productRequest) {
        productRequest.setUserId(LocalThreadHolder.getUserId());
        return productService.query(productRequest);
    }

    @GetMapping(value = "/queryProductList/{id}")
    public Result<List<ProductVO>> queryProductList(@PathVariable Integer id) {
        return productService.queryProductList(id);
    }

}