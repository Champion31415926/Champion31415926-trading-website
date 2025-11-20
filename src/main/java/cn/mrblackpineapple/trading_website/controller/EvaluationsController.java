package cn.mrblackpineapple.trading_website.controller;

import cn.mrblackpineapple.trading_website.aspect.Pager;
import cn.mrblackpineapple.trading_website.aspect.Protector;
import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.dto.EvaluationsRequest;
import cn.mrblackpineapple.trading_website.model.Evaluations;
import cn.mrblackpineapple.trading_website.service.EvaluationsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evaluations")
public class EvaluationsController {

    @Resource
    private EvaluationsService evaluationsService;

    @Protector
    @PostMapping(value = "/insert")
    public Result<Object> insert(@RequestBody Evaluations evaluations) {
        return evaluationsService.insert(evaluations);
    }

    @Protector
    @PostMapping(value = "/upvoteOperation")
    public Result<Object> upvoteOperation(@RequestBody Evaluations evaluations) {
        return evaluationsService.upvoteOperation(evaluations);
    }

    @GetMapping(value = "/list/{contentId}/{contentType}")
    public Result<Object> list(@PathVariable Integer contentId,
                               @PathVariable String contentType) {
        return evaluationsService.list(contentId, contentType);
    }

    @Pager
    @PostMapping(value = "/query")
    public Result<Object> query(@RequestBody EvaluationsRequest evaluationsRequest) {
        return evaluationsService.query(evaluationsRequest);
    }

    @Protector(role = "Administrator")
    @PostMapping(value = "/batchDelete")
    public Result<Object> batchDelete(@RequestBody List<Integer> ids) {
        return evaluationsService.batchDelete(ids);
    }

    @Protector
    @DeleteMapping(value = "/delete/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        return evaluationsService.delete(id);
    }

}