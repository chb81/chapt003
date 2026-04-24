package com.chapt003.dto;

import java.util.List;

/**
 * 分页结果
 */
public class PageResult<T> {
    private List<T> content;         // 数据列表
    private int page;               // 当前页码
    private int size;               // 每页大小
    private long totalElements;     // 总元素数
    private int totalPages;         // 总页数
    private boolean first;          // 是否第一页
    private boolean last;           // 是否最后一页
    private boolean empty;          // 是否为空
    
    // 构造函数
    public PageResult() {}
    
    public PageResult(List<T> content, int page, int size, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
        this.first = page == 0;
        this.last = page >= totalPages - 1;
        this.empty = content == null || content.isEmpty();
    }
    
    // Getters and Setters
    public List<T> getContent() { return content; }
    public void setContent(List<T> content) { this.content = content; }
    
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    
    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
    
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    
    public boolean isFirst() { return first; }
    public void setFirst(boolean first) { this.first = first; }
    
    public boolean isLast() { return last; }
    public void setLast(boolean last) { this.last = last; }
    
    public boolean isEmpty() { return empty; }
    public void setEmpty(boolean empty) { this.empty = empty; }
    
    // 便捷方法
    public boolean hasNext() { return !last; }
    public boolean hasPrevious() { return !first; }
}