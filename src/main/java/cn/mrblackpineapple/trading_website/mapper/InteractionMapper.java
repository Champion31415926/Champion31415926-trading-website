package cn.mrblackpineapple.trading_website.mapper;

import cn.mrblackpineapple.trading_website.dto.InteractionRequest;
import cn.mrblackpineapple.trading_website.model.Interaction;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface InteractionMapper {

    int save(Interaction interaction);

    List<Interaction> query(InteractionRequest interactionRequest);

    List<Interaction> queryByProductIds(@Param(value = "ids")List<Integer> ids);

    int queryCount(InteractionRequest interactionRequest);

    void batchDelete(@Param(value = "ids") List<Integer> ids);

}