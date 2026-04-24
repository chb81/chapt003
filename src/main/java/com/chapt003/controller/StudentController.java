package com.chapt003.controller;

import com.chapt003.dto.StudentProfileRequest;
import com.chapt003.dto.StudentProfileResponse;
import com.chapt003.dto.StudentScoreRequest;
import com.chapt003.dto.StudentScoreResponse;
import com.chapt003.response.ApiResponse;
import com.chapt003.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/v1/student")
@Tag(name = "学生信息", description = "学生档案和成绩信息的增删改查接口")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/profile")
    @Operation(summary = "获取学生档案", description = "获取当前登录学生的档案信息")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> getProfile(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        StudentProfileResponse response = studentService.getProfile(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("获取学生档案成功", response));
    }

    @PostMapping("/profile")
    @Operation(summary = "创建学生档案", description = "创建当前登录学生的档案信息")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> createProfile(
            Principal principal,
            @Valid @RequestBody StudentProfileRequest request) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        StudentProfileResponse response = studentService.createOrUpdateProfile(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("创建学生档案成功", response));
    }

    @PutMapping("/profile")
    @Operation(summary = "更新学生档案", description = "更新当前登录学生的档案信息")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> updateProfile(
            Principal principal,
            @Valid @RequestBody StudentProfileRequest request) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        StudentProfileResponse response = studentService.createOrUpdateProfile(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("更新学生档案成功", response));
    }

    @DeleteMapping("/profile")
    @Operation(summary = "删除学生档案", description = "删除当前登录学生的档案信息")
    public ResponseEntity<ApiResponse<Void>> deleteProfile(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        studentService.deleteProfile(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("删除学生档案成功", null));
    }

    @GetMapping("/score")
    @Operation(summary = "获取学生成绩", description = "获取当前登录学生的成绩信息")
    public ResponseEntity<ApiResponse<StudentScoreResponse>> getScore(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        StudentScoreResponse response = studentService.getScore(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("获取学生成绩成功", response));
    }

    @PostMapping("/score")
    @Operation(summary = "创建学生成绩", description = "创建当前登录学生的成绩信息")
    public ResponseEntity<ApiResponse<StudentScoreResponse>> createScore(
            Principal principal,
            @Valid @RequestBody StudentScoreRequest request) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        StudentScoreResponse response = studentService.createOrUpdateScore(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("创建学生成绩成功", response));
    }

    @PutMapping("/score")
    @Operation(summary = "更新学生成绩", description = "更新当前登录学生的成绩信息")
    public ResponseEntity<ApiResponse<StudentScoreResponse>> updateScore(
            Principal principal,
            @Valid @RequestBody StudentScoreRequest request) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        StudentScoreResponse response = studentService.createOrUpdateScore(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("更新学生成绩成功", response));
    }

    @DeleteMapping("/score")
    @Operation(summary = "删除学生成绩", description = "删除当前登录学生的成绩信息")
    public ResponseEntity<ApiResponse<Void>> deleteScore(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        studentService.deleteScore(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("删除学生成绩成功", null));
    }
}
