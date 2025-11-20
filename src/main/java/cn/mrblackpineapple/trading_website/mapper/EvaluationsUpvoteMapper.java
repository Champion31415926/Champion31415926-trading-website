package cn.mrblackpineapple.trading_website.mapper;

import cn.mrblackpineapple.trading_website.model.EvaluationsUpvote;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EvaluationsUpvoteMapper {

    void save(EvaluationsUpvote evaluationsUpvote);

    void delete(EvaluationsUpvote evaluationsUpvote);

    int queryCount(EvaluationsUpvote evaluationsUpvote);

}