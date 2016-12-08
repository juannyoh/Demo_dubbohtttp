package com.dituhui.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IOrderStatisticDao {

	@SuppressWarnings("rawtypes")
	public List getOrderCountGroupByAreaId(List<String> deptIdList,
			Date startDate, Date endDate);
	
	/**
	 * 首页显示10个，按订单数量排序
	 * @param deptIdList
	 * @param startDate
	 * @param endDate
	 * @return
	 * @Author Juannyoh
	 * 2015-7-2上午9:26:40
	 */
	public List getOrderCountGroupByAreaIdTop10(List<String> deptIdList,
			Date startDate, Date endDate);
	
	/**
	 * 订单量统计
	 * @param deptIdList
	 * @param enterpriseId
	 * @param admincode
	 * @param level
	 * @param startDate
	 * @param endDate
	 * @return
	 * @Author Juannyoh
	 * 2015-10-28上午9:31:42
	 */
	public List queryOrderCountByAdminCode(List<String> deptIdList,
			 String admincode, String level,Date startDate, Date endDate);
	
	
	/**
	 * 统计用户全国  分单成功、地址解析失败、无区划 三种状态的订单数量
	 * @param deptIdList
	 * @param startDate
	 * @param endDate
	 * @return
	 * @Author Juannyoh
	 * 2016-11-29下午2:53:12
	 */
	public List<Map<String,Object>> queryAllOrderGroupByReultType(List<String> deptIdList,
			 Date startDate, Date endDate);
	
	/**
	 * 根据admincode、分单结果类型 进行统计
	 * @param deptIdList
	 * @param admincode
	 * @param level
	 * @param startDate
	 * @param endDate
	 * @param resulttype
	 * @return
	 * @Author Juannyoh
	 * 2016-11-29下午3:24:49
	 */
	public List queryOrderCountByAdminCode(List<String> deptIdList,
			 String admincode, String level,Date startDate, Date endDate,int resulttype);
	
	/**
	 * 查询坐标失败订单的详细
	 * @param deptIdList
	 * @param admincode
	 * @param level
	 * @param startDate
	 * @param endDate
	 * @param resulttype
	 * @return
	 * @Author Juannyoh
	 * 2016-11-29下午4:29:51
	 */
	public List queryOrderCountByAdminCodeDetail(List<String> deptIdList,
			 String admincode, String level,Date startDate, Date endDate,int resulttype,
			 int pageNumber,int pageSize,String ordernum,String orderbatch,String address);
	
	/**
	 * 查询明细的总数量
	 * @param deptIdList
	 * @param admincode
	 * @param level
	 * @param startDate
	 * @param endDate
	 * @param resulttype
	 * @param ordernum
	 * @param orderbatch
	 * @param address
	 * @return
	 * @Author Juannyoh
	 * 2016-11-30下午2:57:55
	 */
	public int queryOrderCountByAdminCodeDetailCount(List<String> deptIdList,
			 String admincode, String level,Date startDate, Date endDate,int resulttype,
			 String ordernum,String orderbatch,String address);
}
