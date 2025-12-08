package cn.mrblackpineapple.trading_website.service;

import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.dto.InteractionRequest;
import cn.mrblackpineapple.trading_website.model.Interaction;
import cn.mrblackpineapple.trading_website.vo.ProductVO;

import java.util.List;

public interface InteractionService {

    Result<String> save(Interaction interaction);

    Result<String> batchDelete(List<Integer> ids);

    Result<List<Interaction>> query(InteractionRequest interactionRequest);

    Result<Boolean> saveOperation(Integer productId);

    Result<String> likeProduct(Integer productId);

    Result<List<ProductVO>> queryUser();

    Result<Void> view(Integer productId);

    Result<List<ProductVO>> myView();

    Result<String> batchDeleteInteraction();

}