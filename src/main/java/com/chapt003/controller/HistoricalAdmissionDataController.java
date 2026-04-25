package com.chapt003.controller;

import com.chapt003.dto.HistoricalAdmissionDataRequest;
import com.chapt003.dto.HistoricalAdmissionDataResponse;
import com.chapt003.response.ApiResponse;
import com.chapt003.service.HistoricalAdmissionDataService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/admin/historical-admission-data")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "管理员-历史录取数据", description = "历史录取数据的增删改查、批量导入、导出等接口")
public class HistoricalAdmissionDataController {

    @Autowired
    private HistoricalAdmissionDataService service;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<HistoricalAdmissionDataResponse>>> getList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String schoolType,
            @RequestParam(required = false) String keyword) {
        Page<HistoricalAdmissionDataResponse> result = service.getList(
                year, city, district, schoolType, keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success("获取历史录取数据成功", result));
    }

    @GetMapping("/school/{schoolId}")
    public ResponseEntity<ApiResponse<List<HistoricalAdmissionDataResponse>>> getBySchool(
            @PathVariable Long schoolId) {
        List<HistoricalAdmissionDataResponse> result = service.getBySchoolId(schoolId);
        return ResponseEntity.ok(ApiResponse.success("获取学校历史数据成功", result));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<HistoricalAdmissionDataResponse>> create(
            @Valid @RequestBody HistoricalAdmissionDataRequest request) {
        HistoricalAdmissionDataResponse result = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("创建历史录取数据成功", result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HistoricalAdmissionDataResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody HistoricalAdmissionDataRequest request) {
        HistoricalAdmissionDataResponse result = service.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("更新历史录取数据成功", result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    @PostMapping("/import")
    public ResponseEntity<ApiResponse<Integer>> batchImport(
            @Valid @RequestBody List<HistoricalAdmissionDataRequest> requests) {
        int count = service.batchImport(requests);
        return ResponseEntity.ok(ApiResponse.success("导入成功，共 " + count + " 条", count));
    }

    @GetMapping("/export")
    public ResponseEntity<ApiResponse<List<HistoricalAdmissionDataResponse>>> exportAll() {
        List<HistoricalAdmissionDataResponse> result = service.exportAll();
        return ResponseEntity.ok(ApiResponse.success("导出成功", result));
    }
}
