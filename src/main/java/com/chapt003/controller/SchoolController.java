package com.chapt003.controller;

import com.chapt003.dto.SchoolListRequest;
import com.chapt003.dto.SchoolListResponse;
import com.chapt003.dto.SchoolRequest;
import com.chapt003.dto.SchoolResponse;
import com.chapt003.response.ApiResponse;
import com.chapt003.service.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/schools")
public class SchoolController {

    @Autowired
    private SchoolService schoolService;

    @GetMapping
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
                .type(type != null ? com.chapt003.entity.enums.SchoolType.valueOf(type) : null)
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
    public ResponseEntity<ApiResponse<SchoolResponse>> getSchoolById(@PathVariable Long id) {
        SchoolResponse response = schoolService.getSchoolById(id);
        return ResponseEntity.ok(ApiResponse.success("获取学校详情成功", response));
    }

    @GetMapping("/cities")
    public ResponseEntity<ApiResponse<List<String>>> getAllCities() {
        List<String> cities = schoolService.getAllCities();
        return ResponseEntity.ok(ApiResponse.success("获取城市列表成功", cities));
    }

    @GetMapping("/districts")
    public ResponseEntity<ApiResponse<List<String>>> getDistrictsByCity(@RequestParam String city) {
        List<String> districts = schoolService.getDistrictsByCity(city);
        return ResponseEntity.ok(ApiResponse.success("获取区县列表成功", districts));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SchoolResponse>> createSchool(@Valid @RequestBody SchoolRequest request) {
        SchoolResponse response = schoolService.createSchool(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("创建学校成功", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SchoolResponse>> updateSchool(
            @PathVariable Long id,
            @Valid @RequestBody SchoolRequest request) {
        SchoolResponse response = schoolService.updateSchool(id, request);
        return ResponseEntity.ok(ApiResponse.success("更新学校成功", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSchool(@PathVariable Long id) {
        schoolService.deleteSchool(id);
        return ResponseEntity.ok(ApiResponse.success("删除学校成功", null));
    }

    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> importSchools(@Valid @RequestBody List<SchoolRequest> requests) {
        schoolService.importSchools(requests);
        return ResponseEntity.ok(ApiResponse.success("导入学校数据成功", null));
    }
}
