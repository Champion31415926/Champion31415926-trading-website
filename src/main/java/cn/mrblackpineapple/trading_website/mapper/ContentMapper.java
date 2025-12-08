package cn.mrblackpineapple.trading_website.mapper;

import cn.mrblackpineapple.trading_website.dto.ContentRequest;
import cn.mrblackpineapple.trading_website.model.Content;
import cn.mrblackpineapple.trading_website.vo.ContentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ContentMapper {

    int save(Content content);

    List<ContentVO> query(ContentRequest contentRequest);

    int queryCount(ContentRequest contentRequest);

    int update(Content content);

    void batchDelete(@Param(value = "ids") List<Integer> ids);

}