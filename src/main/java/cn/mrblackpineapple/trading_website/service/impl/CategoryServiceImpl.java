package cn.mrblackpineapple.trading_website.service.impl;

import cn.mrblackpineapple.trading_website.common.api.ApiResult;
import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.dto.CategoryRequest;
import cn.mrblackpineapple.trading_website.mapper.CategoryMapper;
import cn.mrblackpineapple.trading_website.model.Category;
import cn.mrblackpineapple.trading_website.service.CategoryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;


@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public Result<String> save(Category category) {
        if (!StringUtils.hasText(category.getName())) {
            return ApiResult.error("The category name of the product cannot be empty.");
        }
        categoryMapper.save(category);
        return ApiResult.success("New category of goods has been successfully added.");
    }

    @Override
    public Result<String> update(Category category) {
        categoryMapper.update(category);
        return ApiResult.success("Product category modifies successfully.");
    }

    @Override
    public Result<String> batchDelete(List<Integer> ids) {
        categoryMapper.batchDelete(ids);
        return ApiResult.success("Product category deletes successfully.");
    }

    @Override
    public Result<List<Category>> query(CategoryRequest categoryRequest) {
        int totalCount = categoryMapper.queryCount(categoryRequest);
        List<Category> categoryList = categoryMapper.query(categoryRequest);
        return ApiResult.success(categoryList, totalCount);
    }
}