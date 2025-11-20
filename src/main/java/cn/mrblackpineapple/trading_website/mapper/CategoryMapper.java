package cn.mrblackpineapple.trading_website.mapper;

import cn.mrblackpineapple.trading_website.dto.CategoryRequest;
import cn.mrblackpineapple.trading_website.model.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {

    int save(Category category);

    List<Category> query(CategoryRequest categoryRequest);

    int queryCount(CategoryRequest categoryRequest);

    int update(Category category);

    void batchDelete(@Param(value = "ids") List<Integer> ids);

}
