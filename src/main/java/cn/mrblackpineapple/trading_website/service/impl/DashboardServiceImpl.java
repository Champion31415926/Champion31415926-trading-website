package cn.mrblackpineapple.trading_website.service.impl;

import cn.mrblackpineapple.trading_website.common.api.ApiResult;
import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.common.util.DateUtil;
import cn.mrblackpineapple.trading_website.dto.*;
import cn.mrblackpineapple.trading_website.mapper.*;
import cn.mrblackpineapple.trading_website.service.DashboardService;
import cn.mrblackpineapple.trading_website.vo.ChartVO;
import cn.mrblackpineapple.trading_website.vo.ProductVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private InteractionMapper interactionMapper;
    @Resource
    private EvaluationsMapper evaluationsMapper;

    @Override
    public Result<List<ChartVO>> statisticCount() {
        List<ChartVO> chartVOList = new ArrayList<>();

        int userCount = userMapper.queryCount(new UserRequest());
        ChartVO chartVOUser = new ChartVO("Users", userCount);
        chartVOList.add(chartVOUser);

        int productCount = productMapper.queryCount(new ProductRequest());
        ChartVO chartVOProduct = new ChartVO("Products", productCount);
        chartVOList.add(chartVOProduct);

        int ordersCount = ordersMapper.queryCount(new OrdersRequest());
        ChartVO chartVOOrders = new ChartVO("Orders", ordersCount);
        chartVOList.add(chartVOOrders);

        int messageCount = messageMapper.queryCount(new MessageRequest());
        ChartVO chartVOMessage = new ChartVO("Messages", messageCount);
        chartVOList.add(chartVOMessage);

        int interactionCount = interactionMapper.queryCount(new InteractionRequest());
        ChartVO chartVOInteraction = new ChartVO("Interactions", interactionCount);
        chartVOList.add(chartVOInteraction);

        int evaluationsCount = evaluationsMapper.queryCount(new EvaluationsRequest());
        ChartVO chartVOEvaluations = new ChartVO("Evaluations", evaluationsCount);
        chartVOList.add(chartVOEvaluations);

        return ApiResult.success(chartVOList);
    }

    @Override
    public Result<List<ChartVO>> productShelvesInfo(Integer day) {
        Request request = DateUtil.startAndEndTime(day);
        ProductRequest productRequest = new ProductRequest();
        productRequest.setStartTime(request.getStartTime());
        productRequest.setEndTime(request.getEndTime());
        List<ProductVO> productVOS = productMapper.query(productRequest);
        List<LocalDateTime> dateTimeList = productVOS.stream()
                .map(ProductVO::getCreateTime).collect(Collectors.toList());
        List<ChartVO> chartVOS = DateUtil.countDatesWithinRange(day, dateTimeList);
        return ApiResult.success(chartVOS);
    }
}