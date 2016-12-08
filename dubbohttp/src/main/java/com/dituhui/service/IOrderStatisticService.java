package com.dituhui.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IOrderStatisticService {

	@SuppressWarnings("rawtypes")
	public List getOrderCountStatistic(List<String> deptIdList, Date startDate,
			Date endDate);
	/**
	 * 前10个订单数最多的
	 * @param deptIdList
	 * @param startDate
	 * @param endDate
	 * @return
	 * @Author Juannyoh
	 * 2015-7-2上午9:29:44
	 */
	public List getOrderCountStatisticTop10(List<String> deptIdList, Date startDate,
			Date endDate);
	
	/**
	 * 订单量统计
	 * @param deptIdList
	 * @param admincode
	 * @param level
	 * @param startDate
	 * @param endDate
	 * @return
	 * @Author Juannyoh
	 * 2015-10-29上午10:34:23
	 */
	public List getOrderCountByAdminCode(List<String> deptIdList,
			 String admincode, String level,Date startDate, Date endDate);
	
	/**
	 * 统计用户 订单量 按照 分单成功、坐标定位失败、无区划 三种情况
	 * @param deptIdList
	 * @param dcode
	 * @param startDate
	 * @param endDate
	 * @return
	 * @Author Juannyoh
	 * 2016-11-29下午3:06:51
	 */
	public List queryAllOrderGroupByReultType(List<String> deptIdList,
			 Date startDate, Date endDate);
	
	/**
	 * 按照admincode、分单结果  进行统计
	 * @param deptIdList
	 * @param admincode
	 * @param level
	 * @param startDate
	 * @param endDate
	 * @param resulttype
	 * @return
	 * @Author Juannyoh
	 * 2016-11-29下午4:08:37
	 */
	public List getOrderCountByAdminCode(List<String> deptIdList,
			 String admincode, String level,Date startDate, Date endDate,int resulttype);
	
	/**
	 * 查询分单API与导入的订单  明细
	 * @param deptIdList
	 * @param admincode
	 * @param level
	 * @param startDate
	 * @param endDate
	 * @param resulttype
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 * @Author Juannyoh
	 * 2016-11-30上午9:45:03
	 */
	public Map<String,Object> queryOrderDetailByAdminCode(List<String> deptIdList,
			 String admincode, String level,Date startDate, Date endDate,int resulttype,
			 int pageNumber,int pageSize,
				String ordernum,String orderbatch,String address);
}
