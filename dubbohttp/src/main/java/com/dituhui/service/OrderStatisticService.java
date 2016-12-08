package com.dituhui.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.dituhui.dao.IAreaQueryDao;
import com.dituhui.dao.IOrderStatisticDao;

@Service
public class OrderStatisticService implements IOrderStatisticService {

	@Autowired
	private IOrderStatisticDao orderStatisticDao;
	
	@Autowired
	private IAreaQueryDao areaQueryDao;

	@Override
	@SuppressWarnings("rawtypes")
	public List getOrderCountStatistic(List<String> deptIdList, Date startDate,
			Date endDate) {
		return orderStatisticDao.getOrderCountGroupByAreaId(deptIdList,
				startDate, endDate);
	}

	@Override
	public List getOrderCountStatisticTop10(List<String> deptIdList,
			Date startDate, Date endDate) {
		return orderStatisticDao.getOrderCountGroupByAreaIdTop10(deptIdList,
				startDate, endDate);
	}

	@Override
	public List getOrderCountByAdminCode(List<String> deptIdList,
			String admincode, String level, Date startDate, Date endDate) {
		return orderStatisticDao.queryOrderCountByAdminCode(deptIdList, admincode, level, startDate, endDate);
	}

	@Override
	public List queryAllOrderGroupByReultType(
			List<String> deptIdList, Date startDate, Date endDate) {
		if(endDate==null&&startDate==null){//如果开始日期、结束日期均为空，则统计近一个月数据
			endDate=new Date();
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(endDate);
			calendar.add(Calendar.MONTH, -1);
			startDate=calendar.getTime();
		}else if(endDate==null&&startDate!=null){//如果结束日期是空，则把截止日期设为当天
			endDate=new Date();
		}else if(endDate!=null&&startDate==null){//结束日期不为空，开始日期为空，默认结束日期往前推一个月
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(endDate);
			calendar.add(Calendar.MONTH, -1);
			startDate=calendar.getTime();
		}
		return this.orderStatisticDao.queryAllOrderGroupByReultType(deptIdList, startDate, endDate);
	}

	@Override
	public List getOrderCountByAdminCode(List<String> deptIdList,
			String admincode, String level, Date startDate, Date endDate,
			int resulttype) {
		if(endDate==null&&startDate==null){//如果开始日期、结束日期均为空，则统计近一个月数据
			endDate=new Date();
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(endDate);
			calendar.add(Calendar.MONTH, -1);
			startDate=calendar.getTime();
		}else if(endDate==null&&startDate!=null){//如果结束日期是空，则把截止日期设为当天
			endDate=new Date();
		}else if(endDate!=null&&startDate==null){//结束日期不为空，开始日期为空，默认结束日期往前推一个月
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(endDate);
			calendar.add(Calendar.MONTH, -1);
			startDate=calendar.getTime();
		}
		return this.orderStatisticDao.queryOrderCountByAdminCode(deptIdList, admincode, level, startDate, endDate, resulttype);
	}

	@Override
	public Map<String,Object> queryOrderDetailByAdminCode(List<String> deptIdList,
			String admincode, String level, Date startDate, Date endDate,
			int resulttype, int pageNumber, int pageSize,
			String ordernum,String orderbatch,String address) {
		Map<String,Object> resultmap=null;
		
		if(endDate==null&&startDate==null){//如果开始日期、结束日期均为空，则统计近一个月数据
			endDate=new Date();
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(endDate);
			calendar.add(Calendar.MONTH, -1);
			startDate=calendar.getTime();
		}else if(endDate==null&&startDate!=null){//如果结束日期是空，则把截止日期设为当天
			endDate=new Date();
		}else if(endDate!=null&&startDate==null){//结束日期不为空，开始日期为空，默认结束日期往前推一个月
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(endDate);
			calendar.add(Calendar.MONTH, -1);
			startDate=calendar.getTime();
		}
		List resultlist= this.orderStatisticDao.queryOrderCountByAdminCodeDetail(deptIdList, admincode, level, startDate, endDate, resulttype, pageNumber, pageSize,
				ordernum,orderbatch,address);
		//有区划的时候，需要区划名称
		if(resultlist!=null&&resultlist.size()>0){
			for(int i=0;i<resultlist.size();i++){
				Map<String,Object> map=(Map<String, Object>) resultlist.get(i);
				map.put("rowid", i+1);
				if(resulttype==1){//分单成功，有区划
					int ordertype=Integer.parseInt(map.get("ordertype").toString());
					String areaid=map.get("areaid")==null?null:map.get("areaid").toString();
					if(ordertype==2){//分单api
						if(StringUtils.isNotEmpty(areaid)){
							map.put("areaname", areaid);
						}
					}else{//导入的订单
						if(StringUtils.isNotEmpty(areaid)){
							String name=this.areaQueryDao.queryNameByIdOrNumber(areaid, null, null);
							map.put("areaname", name);
						}
					}
				}else{
					map.put("areaname", "");
				}
			}
		}
				
		if(resultlist!=null&&resultlist.size()>0){
			int sumcount=this.orderStatisticDao.queryOrderCountByAdminCodeDetailCount(deptIdList, admincode, level, startDate, endDate, resulttype, ordernum, orderbatch, address);
			resultmap=new HashMap<String,Object>();
			if(pageNumber>0&&pageSize>0){
				resultmap.put("page", pageNumber);
				resultmap.put("records", sumcount);
				resultmap.put("rows", resultlist);
				resultmap.put("total", sumcount%pageSize==0?sumcount/pageSize:(sumcount/pageSize+1));
			}
			else{
				resultmap.put("rows", resultlist);
			}
		}
		return resultmap;
	}

}
