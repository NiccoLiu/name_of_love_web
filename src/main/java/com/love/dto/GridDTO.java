package com.love.dto;

import java.util.List;

/**
 * 列表DTO
 * 
 * @author liuxinq
 */
public class GridDTO<T> {
    /**
     * 总记录数
     */
    private int totalRecord;

    /**
     * 总页数
     */
    private int totalPage;

    /**
     * 列表
     */
    private List<T> list;

    public int getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(int totalRecord) {
        this.totalRecord = totalRecord;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "GridDTO [totalRecord=" + totalRecord + ", totalPage=" + totalPage + ", list=" + list
                + "]";
    }

}
