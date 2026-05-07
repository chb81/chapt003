package com.chapt003.controller;

import com.chapt003.dto.SchoolListRequest;
import com.chapt003.dto.SchoolListResponse;
import com.chapt003.dto.SchoolRequest;
import com.chapt003.dto.SchoolResponse;
import com.chapt003.response.ApiResponse;
import com.chapt003.service.SchoolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/schools")
@Tag(name = "学校管理", description = "学校列表查询、详情查看、搜索筛选、数据导入导出等接口")
public class SchoolController {

    @Autowired
    private SchoolService schoolService;

    @GetMapping
    @Operation(summary = "获取学校列表", description = "支持关键词搜索、城市/区县/类型筛选、分数范围过滤、排序和分页")
    public ResponseEntity<ApiResponse<SchoolListResponse>> getSchoolList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double minScore,
            @RequestParam(required = false) Double maxScore,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        SchoolListRequest request = SchoolListRequest.builder()
                .keyword(keyword)
                .city(city)
                .district(district)
                .schoolType(type)
                .minScore(minScore != null ? java.math.BigDecimal.valueOf(minScore) : null)
                .maxScore(maxScore != null ? java.math.BigDecimal.valueOf(maxScore) : null)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .page(page)
                .size(size)
                .build();

        SchoolListResponse response = schoolService.getSchoolList(request);
        return ResponseEntity.ok(ApiResponse.success("获取学校列表成功", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取学校详情", description = "根据学校ID获取学校详细信息")
    public ResponseEntity<ApiResponse<SchoolResponse>> getSchoolById(@PathVariable Long id) {
        SchoolResponse response = schoolService.getSchoolById(id);
        return ResponseEntity.ok(ApiResponse.success("获取学校详情成功", response));
    }

    @GetMapping("/cities")
    @Operation(summary = "获取城市列表", description = "获取所有有学校的城市列表")
    public ResponseEntity<ApiResponse<List<String>>> getAllCities() {
        List<String> cities = schoolService.getAllCities();
        return ResponseEntity.ok(ApiResponse.success("获取城市列表成功", cities));
    }

    @GetMapping("/districts")
    @Operation(summary = "获取区县列表", description = "根据城市获取该城市下的区县列表")
    public ResponseEntity<ApiResponse<List<String>>> getDistrictsByCity(@RequestParam String city) {
        List<String> districts = schoolService.getDistrictsByCity(city);
        return ResponseEntity.ok(ApiResponse.success("获取区县列表成功", districts));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建学校", description = "管理员创建新学校（需要管理员权限）")
    public ResponseEntity<ApiResponse<SchoolResponse>> createSchool(@Valid @RequestBody SchoolRequest request) {
        SchoolResponse response = schoolService.createSchool(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("创建学校成功", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新学校", description = "管理员更新学校信息（需要管理员权限）")
    public ResponseEntity<ApiResponse<SchoolResponse>> updateSchool(
            @PathVariable Long id,
            @Valid @RequestBody SchoolRequest request) {
        SchoolResponse response = schoolService.updateSchool(id, request);
        return ResponseEntity.ok(ApiResponse.success("更新学校成功", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除学校", description = "管理员删除学校（需要管理员权限）")
    public ResponseEntity<ApiResponse<Void>> deleteSchool(@PathVariable Long id) {
        schoolService.deleteSchool(id);
        return ResponseEntity.ok(ApiResponse.success("删除学校成功", null));
    }

    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "批量导入学校", description = "管理员批量导入学校数据（需要管理员权限）")
    public ResponseEntity<ApiResponse<Void>> importSchools(@Valid @RequestBody List<SchoolRequest> requests) {
        schoolService.importSchools(requests);
        return ResponseEntity.ok(ApiResponse.success("导入学校数据成功", null));
    }

    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "导出学校数据", description = "管理员导出所有学校数据（需要管理员权限）")
    public ResponseEntity<ApiResponse<List<SchoolResponse>>> exportSchools() {
        List<SchoolResponse> schools = schoolService.exportAllSchools();
        return ResponseEntity.ok(ApiResponse.success("导出学校数据成功", schools));
    }
}
