package com.love.util;

import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;

/**
 * 分页结果的封装(for Bootstrap Table)
 *
 * 
 * @Date 2017年1月22日 下午11:06:41
 */
public class PageInfoBT<T> {

    // 结果集
    private List<T> content;

    // 总数
    private long total;
    
    

    public PageInfoBT(Page<T> page) {
        this.content = page.getRecords();
        this.total = page.getTotal();
    }

   

    public List<T> getContent() {
		return content;
	}



	public void setContent(List<T> content) {
		this.content = content;
	}



	public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
