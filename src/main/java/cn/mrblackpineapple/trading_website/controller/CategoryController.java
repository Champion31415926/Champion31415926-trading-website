package cn.mrblackpineapple.trading_website.controller;

import cn.mrblackpineapple.trading_website.aspect.Pager;
import cn.mrblackpineapple.trading_website.aspect.Protector;
import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.dto.CategoryRequest;
import cn.mrblackpineapple.trading_website.model.Category;
import cn.mrblackpineapple.trading_website.service.CategoryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @Protector(role = "Administrator")
    @PostMapping(value = "/save")
    public Result<String> save(@RequestBody Category category) {
        return categoryService.save(category);
    }

    @Protector(role = "Administrator")
    @PutMapping(value = "/update")
    public Result<String> update(@RequestBody Category category) {
        return categoryService.update(category);
    }

    @Protector(role = "Administrator")
    @PostMapping(value = "/batchDelete")
    public Result<String> batchDelete(@RequestBody List<Integer> ids) {
        return categoryService.batchDelete(ids);
    }

    @Pager
    @PostMapping(value = "/query")
    public Result<List<Category>> queryByPost(@RequestBody CategoryRequest categoryRequest) {
        return categoryService.query(categoryRequest);
    }


    @GetMapping(value = "/query")
    public Result<List<Category>> queryByGet() {
        CategoryRequest req = new CategoryRequest();
        req.setCurrent(1);
        req.setSize(999);
        return categoryService.query(req);
    }
}
