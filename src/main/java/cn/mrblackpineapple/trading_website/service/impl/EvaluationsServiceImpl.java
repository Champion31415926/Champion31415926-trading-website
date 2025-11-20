package cn.mrblackpineapple.trading_website.service.impl;

import cn.mrblackpineapple.trading_website.common.api.ApiResult;
import cn.mrblackpineapple.trading_website.common.api.PageResult;
import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.common.context.LocalThreadHolder;
import cn.mrblackpineapple.trading_website.dto.EvaluationsRequest;
import cn.mrblackpineapple.trading_website.mapper.EvaluationsMapper;
import cn.mrblackpineapple.trading_website.mapper.EvaluationsUpvoteMapper;
import cn.mrblackpineapple.trading_website.mapper.UserMapper;
import cn.mrblackpineapple.trading_website.model.Evaluations;
import cn.mrblackpineapple.trading_website.model.EvaluationsUpvote;
import cn.mrblackpineapple.trading_website.model.User;
import cn.mrblackpineapple.trading_website.service.EvaluationsService;
import cn.mrblackpineapple.trading_website.vo.CommentChildVO;
import cn.mrblackpineapple.trading_website.vo.CommentParentVO;
import cn.mrblackpineapple.trading_website.vo.EvaluationsVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EvaluationsServiceImpl implements EvaluationsService {

    @Resource
    private EvaluationsMapper evaluationsMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private EvaluationsUpvoteMapper evaluationsUpvoteMapper;

    @Override
    public Result<Object> insert(Evaluations evaluations) {
        evaluations.setCommenterId(LocalThreadHolder.getUserId());
        User queryConditionEntity = User.builder().id(LocalThreadHolder.getUserId()).build();
        User user = userMapper.getByActive(queryConditionEntity);
        if (user.getIsWord()) {
            return ApiResult.error("Account has been muted");
        }
        evaluations.setCreateTime(LocalDateTime.now());
        evaluationsMapper.save(evaluations);
        return ApiResult.success("Comment successfully");
    }

    @Override
    public Result<Object> list(Integer contentId, String contentType) {
        List<CommentParentVO> parentComments = evaluationsMapper.getParentComments(
                contentId,
                LocalThreadHolder.getUserId(),
                contentType
        );
        Integer count = evaluationsMapper.totalCount(contentId, contentType);
        return ApiResult.success(new EvaluationsVO(count, parentComments));
    }

    @Override
    public Result<Object> query(EvaluationsRequest evaluationsRequest) {
        evaluationsRequest.setUserId(LocalThreadHolder.getUserId());
        List<CommentChildVO> list = evaluationsMapper.query(evaluationsRequest);
        Integer totalPage = evaluationsMapper.queryCount(evaluationsRequest);
        return PageResult.success(list, totalPage);
    }

    @Override
    public Result<Object> batchDelete(List<Integer> ids) {
        evaluationsMapper.batchDelete(ids);
        return ApiResult.success();
    }

    @Override
    public Result<String> delete(Integer id) {
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(id);
        evaluationsMapper.batchDelete(ids);
        return ApiResult.success();
    }

    @Override
    public Result<Object> upvoteOperation(Evaluations evaluations) {
        Integer userId = LocalThreadHolder.getUserId();
        Integer evaluationsId = evaluations.getId();

        EvaluationsUpvote query = new EvaluationsUpvote();
        query.setUserId(userId);
        query.setEvaluationsId(evaluationsId);

        boolean hasUpvoted = evaluationsUpvoteMapper.queryCount(query) > 0;

        if (hasUpvoted) {
            EvaluationsUpvote deleteCondition = new EvaluationsUpvote();
            deleteCondition.setUserId(userId);
            deleteCondition.setEvaluationsId(evaluationsId);
            evaluationsUpvoteMapper.delete(deleteCondition);
        } else {
            EvaluationsUpvote newUpvote = new EvaluationsUpvote();
            newUpvote.setUserId(userId);
            newUpvote.setEvaluationsId(evaluationsId);
            evaluationsUpvoteMapper.save(newUpvote);
        }

        EvaluationsUpvote countQuery = new EvaluationsUpvote();
        countQuery.setEvaluationsId(evaluationsId);
        int total = evaluationsUpvoteMapper.queryCount(countQuery);

        Map<String, Object> rep = new HashMap<>();
        rep.put("count", total);
        rep.put("haveUpvote", !hasUpvoted);

        return ApiResult.success(rep);
    }
}