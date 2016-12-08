package com.dituhui.dao;

public interface IAreaQueryDao {

	/**
	 * 根据网点id或者编号 查找区划名称
	 * 注：通过编号查询时最好同时传dcode
	 * @param id
	 * @param number
	 * @return
	 * @Author Juannyoh
	 * 2016-11-30上午10:18:14
	 */
	public String queryNameByIdOrNumber(String id,String number,String dcode);
}
