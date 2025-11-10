package cn.mrblackpineapple.trading_website.mapper;

import cn.mrblackpineapple.trading_website.dto.ProductRequest;
import cn.mrblackpineapple.trading_website.model.Product;
import cn.mrblackpineapple.trading_website.vo.ProductVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {

    int save(Product product);

    ProductVO queryById(@Param(value = "id") Integer id);

    List<ProductVO> query(ProductRequest productRequest);

    int queryCount(ProductRequest productRequest);

    int update(Product product);

    void batchDelete(@Param(value = "ids") List<Integer> ids);

    List<Integer> queryProductIds(@Param(value = "userId") Integer userId);

    List<ProductVO> queryProductList(@Param(value = "ids") List<Integer> ids);
}