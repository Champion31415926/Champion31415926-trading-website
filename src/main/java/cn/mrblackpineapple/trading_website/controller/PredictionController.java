package cn.mrblackpineapple.trading_website.controller;

import cn.mrblackpineapple.trading_website.common.api.ApiResult;
import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.service.impl.PricePredictionServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/prediction")
public class PredictionController {

    @Resource
    private PricePredictionServiceImpl pricePredictionServiceImpl;

    public static class PredictionRequest {
        public String name;
        public String description;
        public Double sellerPrice;
    }

    public static class PredictionResponse {
        public Double predictedPrice;
        public String verdict;
        public String note;
    }

    @PostMapping("/price")
    public Result<PredictionResponse> predictPrice(@RequestBody PredictionRequest req) {
        String text =
                (req.name == null ? "" : req.name) + " " +
                        (req.description == null ? "" : req.description);

        double predicted = pricePredictionServiceImpl.predictPrice(text);

        PredictionResponse resp = new PredictionResponse();
        resp.predictedPrice = predicted;

        if (req.sellerPrice != null) {
            double p = req.sellerPrice;
            if (p <= predicted * 0.9) {
                resp.verdict = "Good Deal";
                resp.note = "Predicted price is >=10% lower than seller price";
            } else if (p >= predicted * 1.1) {
                resp.verdict = "Price Too High";
                resp.note = "Predicted price is >10% higher than seller price";
            } else {
                resp.verdict = "Worth Considering";
                resp.note = "Predicted price is close to seller price (±10%)";
            }
        } else {
            resp.verdict = "For Reference Only";
            resp.note = "No seller price provided, returning predicted value only";
        }

        return ApiResult.success(resp);
    }
}