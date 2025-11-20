package cn.mrblackpineapple.trading_website.mapper;

import cn.mrblackpineapple.trading_website.dto.EvaluationsRequest;
import cn.mrblackpineapple.trading_website.model.Evaluations;
import cn.mrblackpineapple.trading_website.vo.CommentChildVO;
import cn.mrblackpineapple.trading_website.vo.CommentParentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EvaluationsMapper {

    List<CommentParentVO> getParentComments(@Param(value = "contentId") Integer contentId,
                                            @Param(value = "userId") Integer userId,
                                            @Param(value = "contentType") String contentType);

    List<CommentChildVO> query(EvaluationsRequest evaluationsRequest);

    Integer queryCount(EvaluationsRequest evaluationsRequest);

    List<Integer> selectChildComments(@Param(value = "ids") List<Integer> ids);

    void batchDelete(@Param(value = "ids") List<Integer> ids);

    Integer totalCount(@Param(value = "contentId") Integer contentId, @Param(value = "contentType") String contentType);

    void save(Evaluations evaluations);

    List<CommentChildVO> getChildCommentsByParentId(@Param("id") Integer id,
                                                    @Param("userId") Integer userId);


}