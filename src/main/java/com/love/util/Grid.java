package com.love.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 列表实体类
 * @author liuxq
 * @since 1.0
 */
public class Grid {
	/** 总数  **/
	private int total_record = 0;
	/** 总页数  **/
	private int total_page = 0;

	/** 列表内容 **/
	private List list = new ArrayList();

	public int getTotal_record() {
		return total_record;
	}

	public void setTotal_record(int total_record) {
		this.total_record = total_record;
	}

	public int getTotal_page() {
		return total_page;
	}

	public void setTotal_page(int total_page) {
		this.total_page = total_page;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "Grid [total_record=" + total_record + ", total_page=" + total_page + ", list=" + list + "]";
	}

}
