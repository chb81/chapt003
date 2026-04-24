package com.chapt003.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;

/**
 * 分页请求
 */
public class PageRequest {
    @Min(value = 0, message = "页码必须大于等于0")
    private int page = 0;           // 页码，从0开始
    
    @Min(value = 1, message = "每页大小必须大于等于1")
    @Max(value = 100, message = "每页大小不能超过100")
    private int size = 20;          // 每页大小
    
    @Pattern(regexp = "^[a-zA-Z_]+$", message = "排序字段只能包含字母、下划线")
    private String sortBy = "messageTime";  // 排序字段
    
    @Pattern(regexp = "^(ASC|DESC)$", message = "排序方向必须是ASC或DESC")
    private String sortDirection = "ASC";   // 排序方向
    
    // 构造函数
    public PageRequest() {}
    
    public PageRequest(int page, int size) {
        this.page = page;
        this.size = size;
    }
    
    public PageRequest(int page, int size, String sortBy, String sortDirection) {
        this.page = page;
        this.size = size;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }
    
    // Getters and Setters
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    
    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }
    
    // 计算偏移量
    public int getOffset() {
        return page * size;
    }
}