package cn.mrblackpineapple.trading_website.mapper;

import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.vo.ChartVO;

import java.util.List;

public interface DashboardService {

    Result<List<ChartVO>> statisticCount();

    Result<List<ChartVO>> productShelvesInfo(Integer day);

}