package cn.mrblackpineapple.trading_website.controller;

import cn.mrblackpineapple.trading_website.aspect.Pager;
import cn.mrblackpineapple.trading_website.aspect.Protector;
import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.dto.InteractionRequest;
import cn.mrblackpineapple.trading_website.model.Interaction;
import cn.mrblackpineapple.trading_website.service.InteractionService;
import cn.mrblackpineapple.trading_website.vo.ProductVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("interaction")
public class InteractionController {

    @Resource
    private InteractionService interactionService;

    @Protector
    @PostMapping(value = "/likeProduct/{productId}")
    public Result<String> likeProduct(@PathVariable Integer productId) {
        return interactionService.likeProduct(productId);
    }

    @Protector
    @PostMapping(value = "/saveOperation/{productId}")
    public Result<Boolean> saveOperation(@PathVariable Integer productId) {
        return interactionService.saveOperation(productId);
    }

    @Protector
    @PostMapping(value = "/batchDeleteView")
    public Result<String> batchDeleteInteraction() {
        return interactionService.batchDeleteInteraction();
    }

    @Protector(role = "Administrator")
    @PostMapping(value = "/batchDelete")
    public Result<String> batchDelete(@RequestBody List<Integer> ids) {
        return interactionService.batchDelete(ids);
    }

    @Protector(role = "Administrator")
    @Pager
    @PostMapping(value = "/query")
    public Result<List<Interaction>> query(@RequestBody InteractionRequest interactionRequest) {
        return interactionService.query(interactionRequest);
    }

    @Protector
    @PostMapping(value = "/view/{productId}")
    public Result<Void> view(@PathVariable Integer productId) {
        return interactionService.view(productId);
    }

    @Protector
    @PostMapping(value = "/queryUser")
    public Result<List<ProductVO>> queryUser() {
        return interactionService.queryUser();
    }

    @Protector
    @PostMapping(value = "/myView")
    public Result<List<ProductVO>> myView() {
        return interactionService.myView();
    }
}