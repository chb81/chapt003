package com.chapt003.controller;

import com.chapt003.dto.PlanComparisonResponse;
import com.chapt003.dto.RiskAssessmentResponse;
import com.chapt003.dto.ScoreRankResponse;
import com.chapt003.response.ApiResponse;
import com.chapt003.service.PlanAnalysisService;
import com.chapt003.service.ScoreRankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/v1/plan-analysis")
@Tag(name = "志愿方案分析", description = "志愿方案风险评估、方案对比、分数位次换算")
public class PlanAnalysisController {

    @Autowired
    private PlanAnalysisService planAnalysisService;

    @Autowired
    private ScoreRankService scoreRankService;

    @GetMapping("/{planId}/risk")
    @Operation(summary = "方案风险评估", description = "评估志愿方案的整体风险等级、滑档概率、保底安全度等")
    public ResponseEntity<ApiResponse<RiskAssessmentResponse>> assessRisk(
            Principal principal,
            @PathVariable Long planId) {
        if (principal == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "请先登录"));
        }
        return ResponseEntity.ok(ApiResponse.success("风险评估完成",
                planAnalysisService.assessRisk(null, planId)));
    }

    @PostMapping("/compare")
    @Operation(summary = "方案对比", description = "对比两个或多个志愿方案的综合指标")
    public ResponseEntity<ApiResponse<PlanComparisonResponse>> comparePlans(
            Principal principal,
            @RequestBody List<Long> planIds) {
        if (principal == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "请先登录"));
        }
        return ResponseEntity.ok(ApiResponse.success("方案对比完成",
                planAnalysisService.comparePlans(null, planIds)));
    }

    @GetMapping("/score-rank")
    @Operation(summary = "分数位次换算", description = "根据分数和城市换算全市排名和百分位")
    public ResponseEntity<ApiResponse<ScoreRankResponse>> convertScoreToRank(
            @RequestParam BigDecimal totalScore,
            @RequestParam String city,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(ApiResponse.success("换算完成",
                scoreRankService.convertScoreToRank(totalScore, city, year)));
    }

    @GetMapping("/my-rank")
    @Operation(summary = "我的位次", description = "根据当前学生的成绩自动换算位次")
    public ResponseEntity<ApiResponse<ScoreRankResponse>> getMyRank(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "请先登录"));
        }
        return ResponseEntity.ok(ApiResponse.success("获取位次成功",
                scoreRankService.getStudentRank(null)));
    }

    @GetMapping("/score-rank/years")
    @Operation(summary = "可用年份数据", description = "查看指定城市有哪些年份的位次数据")
    public ResponseEntity<ApiResponse<List<Integer>>> getAvailableYears(@RequestParam String city) {
        return ResponseEntity.ok(ApiResponse.success("获取可用年份成功",
                scoreRankService.getAvailableYears(city)));
    }
}
