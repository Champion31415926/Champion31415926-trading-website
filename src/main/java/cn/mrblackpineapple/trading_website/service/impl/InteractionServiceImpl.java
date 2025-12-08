package cn.mrblackpineapple.trading_website.service.impl;

import cn.mrblackpineapple.trading_website.common.api.ApiResult;
import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.common.context.LocalThreadHolder;
import cn.mrblackpineapple.trading_website.common.enums.InteractionEnum;
import cn.mrblackpineapple.trading_website.dto.InteractionRequest;
import cn.mrblackpineapple.trading_website.dto.ProductRequest;
import cn.mrblackpineapple.trading_website.mapper.InteractionMapper;
import cn.mrblackpineapple.trading_website.mapper.MessageMapper;
import cn.mrblackpineapple.trading_website.mapper.ProductMapper;
import cn.mrblackpineapple.trading_website.mapper.UserMapper;
import cn.mrblackpineapple.trading_website.model.Interaction;
import cn.mrblackpineapple.trading_website.model.Message;
import cn.mrblackpineapple.trading_website.model.User;
import cn.mrblackpineapple.trading_website.service.InteractionService;
import cn.mrblackpineapple.trading_website.vo.ProductVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class InteractionServiceImpl implements InteractionService {

    @Resource
    private InteractionMapper interactionMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private UserMapper userMapper;

    @Override
    public Result<String> likeProduct(Integer productId) {
        InteractionRequest interactionRequest
                = createInteractionRequest(productId, InteractionEnum.LOVE.getType());
        int count = interactionMapper.queryCount(interactionRequest);
        if (count != 0) {
            return ApiResult.error("Operation cannot be repeated.");
        }
        ProductRequest productRequest = new ProductRequest();
        productRequest.setId(productId);
        List<ProductVO> productVOS = productMapper.query(productRequest);
        if (productVOS.isEmpty()) {
            return ApiResult.error("Product information query failed.");
        }
        ProductVO productVO = productVOS.getFirst();
        if (Objects.equals(productVO.getUserId(), LocalThreadHolder.getUserId())) {
            return ApiResult.error("Cannot show interest in your own listing!");
        }
        Integer publisherId = productVO.getUserId();
        Integer userId = LocalThreadHolder.getUserId();
        User user = new User();
        user.setId(userId);
        User operator = userMapper.getByActive(user);
        Message message = new Message();
        message.setUserId(publisherId);
        message.setIsRead(false);
        message.setCreateTime(LocalDateTime.now());
        message.setContent("User [" + operator.getUserName() + "] is interested in your listing: [" + productVO.getName() + "]!");
        messageMapper.save(message);

        // Save the 'like' interaction
        Interaction interaction = createInteraction(productId, InteractionEnum.LOVE.getType());
        interactionMapper.save(interaction);

        return ApiResult.success("The seller has felt your enthusiasm, time to place an order!");
    }

    @Override
    public Result<String> save(Interaction interaction) {
        interactionMapper.save(interaction);
        return ApiResult.success("Interaction recorded successfully.");
    }

    @Override
    public Result<String> batchDelete(List<Integer> ids) {
        interactionMapper.batchDelete(ids);
        return ApiResult.success("Interactions deleted successfully.");
    }

    @Override
    public Result<Boolean> saveOperation(Integer productId) {
        InteractionRequest interactionRequest =
                createInteractionRequest(productId, InteractionEnum.SAVE.getType());
        List<Interaction> interactionList = interactionMapper.query(interactionRequest);
        if (interactionList.isEmpty()) {
            Interaction interaction = createInteraction(productId, InteractionEnum.SAVE.getType());
            interactionMapper.save(interaction);
        } else {
            List<Integer> interactionIds = interactionList.stream().map(Interaction::getId)
                    .collect(Collectors.toList());
            interactionMapper.batchDelete(interactionIds);
        }
        return ApiResult.success(interactionList.isEmpty() ? "Collection successful." : "Collection cancelled.", interactionList.isEmpty());
    }

    private InteractionRequest createInteractionRequest(Integer productId, Integer type) {
        InteractionRequest request = new InteractionRequest();
        request.setUserId(LocalThreadHolder.getUserId());
        request.setType(type);
        request.setProductId(productId);
        return request;
    }

    private Interaction createInteraction(Integer productId, Integer type) {
        Interaction interaction = new Interaction();
        interaction.setUserId(LocalThreadHolder.getUserId());
        interaction.setType(type);
        interaction.setProductId(productId);
        interaction.setCreateTime(LocalDateTime.now());
        return interaction;
    }

    @Override
    public Result<List<Interaction>> query(InteractionRequest interactionRequest) {
        int totalCount = interactionMapper.queryCount(interactionRequest);
        List<Interaction> interactionList = interactionMapper.query(interactionRequest);
        return ApiResult.success(interactionList, totalCount);
    }

    @Override
    public Result<List<ProductVO>> queryUser() {
        InteractionRequest interactionRequest = new InteractionRequest();
        interactionRequest.setUserId(LocalThreadHolder.getUserId());
        interactionRequest.setType(InteractionEnum.SAVE.getType());
        List<Interaction> interactionList = interactionMapper.query(interactionRequest);
        List<Integer> productIds = interactionList.stream()
                .map(Interaction::getProductId).collect(Collectors.toList());

        List<ProductVO> productVOS = productMapper.queryProductList(productIds);
        return ApiResult.success(productVOS);
    }

    @Override
    public Result<Void> view(Integer productId) {
        InteractionRequest interactionRequest
                = createInteractionRequest(productId, InteractionEnum.VIEW.getType());
        List<Interaction> interactionList = interactionMapper.query(interactionRequest);
        if (interactionList.isEmpty()) {
            Interaction interaction = createInteraction(productId, InteractionEnum.VIEW.getType());
            interactionMapper.save(interaction);
        }
        return ApiResult.success();
    }

    @Override
    public Result<List<ProductVO>> myView() {
        InteractionRequest interactionRequest = new InteractionRequest();
        interactionRequest.setUserId(LocalThreadHolder.getUserId());
        interactionRequest.setType(InteractionEnum.VIEW.getType());
        List<Interaction> interactionList = interactionMapper.query(interactionRequest);
        List<Integer> productIds = interactionList.stream()
                .map(Interaction::getProductId).collect(Collectors.toList());
        if (productIds.isEmpty()) {
            return ApiResult.success(new ArrayList<>());
        }
        List<ProductVO> productVOS = productMapper.queryProductList(productIds);
        return ApiResult.success(productVOS);
    }

    @Override
    public Result<String> batchDeleteInteraction() {
        InteractionRequest interactionRequest = new InteractionRequest();
        interactionRequest.setUserId(LocalThreadHolder.getUserId());
        interactionRequest.setType(InteractionEnum.VIEW.getType());
        List<Interaction> interactionList = interactionMapper.query(interactionRequest);
        List<Integer> ids = interactionList.stream()
                .map(Interaction::getId).collect(Collectors.toList());
        if (!ids.isEmpty()) {
            interactionMapper.batchDelete(ids);
        }
        return ApiResult.success("View history deleted successfully.");
    }
}