package com.dituhui.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;


@Repository
public class OrderStatisticDao implements IOrderStatisticDao {

	@PersistenceContext(unitName="SupermapJPA")
	private EntityManager em;
	
	private static Logger LOGGER = Logger.getLogger(OrderStatisticDao.class);

	@SuppressWarnings("rawtypes")
	public List getOrderCountGroupByAreaId(List<String> deptIdList,
			Date startDate, Date endDate) {
		StringBuilder sb = new StringBuilder();
		sb.append(
				"select count(t.id) as num,area_id from egisp_dev.ORDER_FENDAN t ")
				.append("where t.department_id in (:deptIdList) ")
				.append("and t.fendan_time between :startDate and :endDate ")
				.append("and t.fendan_status_id in (1,3) ")
				.append("group by t.area_id");
		Query query = em.createNativeQuery(sb.toString());
		query.setParameter("deptIdList", deptIdList)
				.setParameter("startDate", startDate, TemporalType.TIMESTAMP)
				.setParameter("endDate", endDate, TemporalType.TIMESTAMP)
				.unwrap(SQLQuery.class)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query.getResultList();
	}

	/**
	 * 显示10个，按订单数量排序
	 */
	@Override
	public List getOrderCountGroupByAreaIdTop10(List<String> deptIdList,
			Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append(
				"select count(t.id) as num,area_id from egisp_dev.ORDER_FENDAN t ")
				.append("where t.department_id in (:deptIdList) ")
				.append("and t.fendan_time between :startDate and :endDate ")
				.append("and t.fendan_status_id in (1,3) ")
				.append("group by t.area_id")
				.append(" order by num desc limit 10 ");
		Query query = em.createNativeQuery(sb.toString());
		query.setParameter("deptIdList", deptIdList)
				.setParameter("startDate", startDate, TemporalType.TIMESTAMP)
				.setParameter("endDate", endDate, TemporalType.TIMESTAMP)
				.unwrap(SQLQuery.class)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query.getResultList();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List queryOrderCountByAdminCode(List<String> deptIdList,
			String admincode, String level,Date startDate, Date endDate) {
		StringBuilder sb = new StringBuilder();
		String admincodetemp="";
		int m=1,n=2;
		if(level==null||level.equals("")){
			level="1";
			m=1;n=2;
		}
		else{
			int levels=Integer.parseInt(level);
			admincodetemp=dealAdmincode(admincode,levels);
			if(admincode!=null&&!admincode.equals("")){
				if(IsMunicipalities(admincode)){
					switch (levels) {
					case 1://市
						m=1;n=6;
						break;
					case 3://区
						m=1;n=6;
						break;
					default:
						m=1;n=6;
						break;
					}
				}
				//其他省市区
				else {
					switch (levels) {
					case 1://省
						m=1;n=4;
						break;
					case 2://市
						m=1;n=6;
						break;
					case 3://区
						m=1;n=6;
						break;
					default:
						m=1;n=6;
						break;
					}
				}
			}
		}
		sb.append("select COUNT(*)count,substr(a.admincode,"+m+","+n+") admincode_ from ( ")
		.append("select base.admincode from order_fendan fendan ")
		.append("LEFT JOIN order_base base  ")
		.append("on fendan.id=base.id where 1=1  ")
		.append("and fendan.fendan_status_id in(1,3) ");
		
		if(!StringUtils.isEmpty(admincode)){
			sb.append(" and base.admincode like '"+admincodetemp+"%' ")
				.append(" and base.admincode <>'"+admincode+"' ");
		}
		if(startDate!=null&&endDate!=null){
			sb.append("and fendan.fendan_time between :startDate and :endDate ");
		}
		if(deptIdList!=null&&deptIdList.size()>0){
			sb.append(" and fendan.department_id in (:deptIdList) ");
		}
		
		sb.append(" UNION ALL select admincode from sys_api_fendan apifendan where 1=1 ");
		if(!StringUtils.isEmpty(admincode)){
			sb.append(" and apifendan.admincode like '"+admincodetemp+"%' ")
				.append(" and apifendan.admincode <>'"+admincode+"' ");
		}
		if(startDate!=null&&endDate!=null){
			sb.append("and apifendan.fendan_time between :startDate and :endDate ");
		}
		if(deptIdList!=null&&deptIdList.size()>0){
			sb.append(" and apifendan.deptid in (:deptIdList) ");
		}
		
		sb.append(")a GROUP BY admincode_ ")
		.append("ORDER BY count desc ");
		
		//LOGGER.info("**************"+sb);
		
		Query query = em.createNativeQuery(sb.toString());
		query.setParameter("deptIdList", deptIdList)
				.setParameter("startDate", startDate, TemporalType.TIMESTAMP)
				.setParameter("endDate", endDate, TemporalType.TIMESTAMP)
				.unwrap(SQLQuery.class)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query.getResultList();
	}
	
	/**
	 * 处理 截取 admincode
	 * @param admincode
	 * @param levels
	 * @param m
	 * @param n
	 * @return
	 * @Author Juannyoh
	 * 2016-11-29下午1:32:53
	 */
	public String dealAdmincode(String admincode,int levels){
		String admincodetemp=null;
		if(StringUtils.isEmpty(admincode)){
			return null;
		}
		if(admincode!=null&&!admincode.equals("")){
			if(IsMunicipalities(admincode)){
				switch (levels) {
				case 1://市
					admincodetemp=admincode.substring(0, 3);
					break;
				case 3://区
					admincodetemp=admincode;
					break;
				default:
					admincodetemp=admincode;
					break;
				}
			}
			//其他省市区
			else {
				switch (levels) {
				case 1://省
					admincodetemp=admincode.substring(0, 2);
					break;
				case 2://市
					admincodetemp=admincode.substring(0, 4);
					break;
				case 3://区
					admincodetemp=admincode;
					break;
				default:
					admincodetemp=admincode;
					break;
				}
			}
		}
		return admincodetemp;
	}
	
	/**
	 * //判断直辖市 北京110，天津120，重庆500，上海310
	 * @param admincode
	 * @return
	 * @Author Juannyoh
	 * 2016-11-29下午4:22:04
	 */
	public boolean IsMunicipalities(String admincode){
		if(StringUtils.isEmpty(admincode)){
			return false;
		}
		return (admincode.indexOf("110")==0||admincode.indexOf("120")==0||admincode.indexOf("500")==0||admincode.indexOf("310")==0);
	}

	@Override
	public List<Map<String, Object>> queryAllOrderGroupByReultType(
			List<String> deptIdList,Date startDate, Date endDate) {
		StringBuilder sbsql=new StringBuilder();
		sbsql.append("select a.resulttype,count(*) sumcount from ")
			.append(" (select ob.admincode, ")
			.append(" case when (of.fendan_status_id=1 or of.fendan_status_id=3) then 1")
			.append(" when (of.fendan_status_id=2 or of.fendan_status_id=4) then 2")
			.append(" else 3 end  as resulttype")
			.append(" from order_base ob")
			.append(" LEFT JOIN order_fendan of ")
			.append(" on ob.id=of.id ")
			.append(" where 1=1 ");
		
		if(startDate!=null&&endDate!=null){
			sbsql.append(" and ob.import_time between :startDate and :endDate ");
		}
		if(deptIdList!=null&&deptIdList.size()>0){
			sbsql.append(" and ob.department_id in (:deptIdList) ");
		}
		
		sbsql.append(" union ALL")
			.append(" select saf.admincode,saf.resulttype ")
			.append(" from sys_api_fendan saf")
			.append(" where 1=1 ");
		
		if(startDate!=null&&endDate!=null){
			sbsql.append(" and saf.fendan_time between :startDate and :endDate ");
		}
		if(deptIdList!=null&&deptIdList.size()>0){
			sbsql.append(" and saf.deptid in (:deptIdList) ");
		}
		
		sbsql.append(") a")
			.append(" GROUP BY a.resulttype")
			.append(" ORDER BY sumcount desc");
		
		Query query = em.createNativeQuery(sbsql.toString());
		query.setParameter("deptIdList", deptIdList)
				.setParameter("startDate", startDate, TemporalType.TIMESTAMP)
				.setParameter("endDate", endDate, TemporalType.TIMESTAMP)
				.unwrap(SQLQuery.class)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query.getResultList();
	}

	@Override
	public List queryOrderCountByAdminCode(List<String> deptIdList,
			String admincode, String level, Date startDate, Date endDate,
			int resulttype) {
		StringBuilder sb = new StringBuilder();
		String admincodetemp="";
		int m=1,n=2;
		if(level==null||level.equals("")){
			level="1";
			m=1;n=2;
		}
		else{
			int levels=Integer.parseInt(level);
			admincodetemp=dealAdmincode(admincode,levels);
			if(admincode!=null&&!admincode.equals("")){
				if(IsMunicipalities(admincode)){
					switch (levels) {
					case 1://市
						m=1;n=6;
						break;
					case 3://区
						m=1;n=6;
						break;
					default:
						m=1;n=6;
						break;
					}
				}
				//其他省市区
				else {
					switch (levels) {
					case 1://省
						m=1;n=4;
						break;
					case 2://市
						m=1;n=6;
						break;
					case 3://区
						m=1;n=6;
						break;
					default:
						m=1;n=6;
						break;
					}
				}
			}
		}
		sb.append("select COUNT(*)count,substr(a.admincode,"+m+","+n+") admincode_ from ( ")
			.append(" select ob.admincode, ")
			.append(" case when (of.fendan_status_id=1 or of.fendan_status_id=3) then 1")
			.append(" when (of.fendan_status_id=2 or of.fendan_status_id=4) then 2")
			.append(" else 3 end  as resulttype")
			.append(" from order_base ob")
			.append(" LEFT JOIN order_fendan of ")
			.append(" on ob.id=of.id")
			.append(" where 1=1 ");
		
		if(!StringUtils.isEmpty(admincode)){
			sb.append(" and ob.admincode like '"+admincodetemp+"%' ")
				.append(" and ob.admincode <>'"+admincode+"' ");
		}
		if(startDate!=null&&endDate!=null){
			sb.append("and ob.import_time between :startDate and :endDate ");
		}
		if(deptIdList!=null&&deptIdList.size()>0){
			sb.append(" and ob.department_id in (:deptIdList) ");
		}
		
		sb.append(" UNION ALL select saf.admincode,saf.resulttype from sys_api_fendan saf where 1=1 ");
		if(!StringUtils.isEmpty(admincode)){
			sb.append(" and saf.admincode like '"+admincodetemp+"%' ")
				.append(" and saf.admincode <>'"+admincode+"' ");
		}
		if(startDate!=null&&endDate!=null){
			sb.append("and saf.fendan_time between :startDate and :endDate ");
		}
		if(deptIdList!=null&&deptIdList.size()>0){
			sb.append(" and saf.deptid in (:deptIdList) ");
		}
		
		sb.append(")a ");
		//按照分单结果类型
		if(resulttype>0){
			sb.append(" where a.resulttype=:resulttype ");
		}
		sb.append(" GROUP BY admincode_ ")
			.append("ORDER BY count desc ");
		
		//LOGGER.info("**************"+sb);
		
		Query query = em.createNativeQuery(sb.toString());
		if(resulttype>0){
			query.setParameter("resulttype", resulttype);
		}
		query.setParameter("deptIdList", deptIdList)
				.setParameter("startDate", startDate, TemporalType.TIMESTAMP)
				.setParameter("endDate", endDate, TemporalType.TIMESTAMP)
				.unwrap(SQLQuery.class)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query.getResultList();
	}

	
	@Override
	public List queryOrderCountByAdminCodeDetail(List<String> deptIdList,
			String admincode, String level, Date startDate, Date endDate,
			int resulttype,int pageNumber,int pageSize,
			String ordernum,String orderbatch,String address) {
		StringBuilder sbsql = new StringBuilder();
		String admincodetemp="";
		if(level==null||level.equals("")){
			level="1";
		}
		else{
			int levels=Integer.parseInt(level);
			admincodetemp=dealAdmincode(admincode,levels);
		}
		
		sbsql.append("select * from ")
			.append(" (select SUBSTR(ob.import_time,1,19) fendantime,ob.number ordernum,ob.address,ob.admincode,ob.province,")
			.append(" ob.city,ob.county,of.area_id areaid,of.smx,of.smy,1 as ordertype,")
			.append(" case when (of.fendan_status_id=1 or of.fendan_status_id=3) then 1")
			.append(" when (of.fendan_status_id=2 or of.fendan_status_id=4) then 2")
			.append(" else 3 end  as resulttype")
			.append(" from order_base ob")
			.append(" LEFT JOIN order_fendan of ")
			.append(" on ob.id=of.id")
			.append(" where 1=1 ");
		
		if(!StringUtils.isEmpty(admincode)){
			sbsql.append(" and ob.admincode like '"+admincodetemp+"%' ");
				
		}
		if(startDate!=null&&endDate!=null){
			sbsql.append(" and ob.import_time between :startDate and :endDate ");
		}
		if(deptIdList!=null&&deptIdList.size()>0){
			sbsql.append(" and ob.department_id in (:deptIdList) ");
		}
		if(!StringUtils.isEmpty(ordernum)){ //订单编号
			sbsql.append(" and ob.number like :ordernum ");
		}
		if(!StringUtils.isEmpty(orderbatch)){ //订单批次
			sbsql.append(" and ob.batch like :orderbatch ");
		}
		if(!StringUtils.isEmpty(address)){ //订单地址
			sbsql.append(" and ob.address like :address ");
		}
		
		sbsql.append(" union ALL")
			.append(" select SUBSTR(saf.fendan_time,1,19) fendantime,saf.order_num ordernum,saf.address,saf.admincode,saf.province,")
			.append(" saf.city,saf.county,saf.areaid,saf.smx,saf.smy,2 as ordertype, saf.resulttype ")
			.append(" from sys_api_fendan saf")
			.append(" where 1=1 ");
			
		if(!StringUtils.isEmpty(admincode)){
			sbsql.append(" and saf.admincode like '"+admincodetemp+"%' ");
		}
		if(startDate!=null&&endDate!=null){
			sbsql.append(" and saf.fendan_time between :startDate and :endDate ");
		}
		if(deptIdList!=null&&deptIdList.size()>0){
			sbsql.append(" and saf.deptid in (:deptIdList) ");
		}
		
		if(!StringUtils.isEmpty(ordernum)){ //订单编号
			sbsql.append(" and saf.order_num like :ordernum ");
		}
		if(!StringUtils.isEmpty(address)){ //订单地址
			sbsql.append(" and saf.address like :address ");
		}
		
		sbsql.append(" ) a ");
		//按照分单结果类型
		if(resulttype>0){
			sbsql.append(" where a.resulttype=:resulttype ");
		}
		
		sbsql.append(" ORDER BY fendantime desc");
		
		Query query = em.createNativeQuery(sbsql.toString());
		
		//分页
		if(pageNumber>0){
			int min=(pageNumber-1)*pageSize;
			query.setFirstResult(min);
			query.setMaxResults(pageSize);
		}
		
		if(resulttype>0){
			query.setParameter("resulttype", resulttype);
		}
		
		if(!StringUtils.isEmpty(ordernum)){ //订单编号
			query.setParameter("ordernum", "%"+ordernum+"%");
		}
		if(!StringUtils.isEmpty(orderbatch)){ //订单批次
			query.setParameter("orderbatch", orderbatch+"%");
		}
		if(!StringUtils.isEmpty(address)){ //订单地址
			query.setParameter("address", "%"+address+"%");
		}
		
		query.setParameter("deptIdList", deptIdList)
				.setParameter("startDate", startDate, TemporalType.TIMESTAMP)
				.setParameter("endDate", endDate, TemporalType.TIMESTAMP)
				.unwrap(SQLQuery.class)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		
		return query.getResultList();
	}

	@Override
	public int queryOrderCountByAdminCodeDetailCount(List<String> deptIdList,
			String admincode, String level, Date startDate, Date endDate,
			int resulttype, String ordernum, String orderbatch, String address) {
		StringBuilder sbsql = new StringBuilder();
		String admincodetemp="";
		int result=0;
		if(level==null||level.equals("")){
			level="1";
		}
		else{
			int levels=Integer.parseInt(level);
			admincodetemp=dealAdmincode(admincode,levels);
		}
		
		sbsql.append("select SUM(counts) sumcount from ")
			.append(" (select count(*) counts")
			.append(" from order_base ob")
			.append(" LEFT JOIN order_fendan of ")
			.append(" on ob.id=of.id")
			.append(" where 1=1 ");
		
		if(resulttype==3){
			sbsql.append(" and  of.id is null ");
		}else if(resulttype==2){
			sbsql.append(" and (of.fendan_status_id=2 or of.fendan_status_id=4)");
		}else if(resulttype==1){
			sbsql.append(" and (of.fendan_status_id=1 or of.fendan_status_id=3)");
		}
		
		if(!StringUtils.isEmpty(admincode)){
			sbsql.append(" and ob.admincode like '"+admincodetemp+"%' ");
		}
		if(startDate!=null&&endDate!=null){
			sbsql.append(" and ob.import_time between :startDate and :endDate ");
		}
		if(deptIdList!=null&&deptIdList.size()>0){
			sbsql.append(" and ob.department_id in (:deptIdList) ");
		}
		if(!StringUtils.isEmpty(ordernum)){ //订单编号
			sbsql.append(" and ob.number like :ordernum ");
		}
		if(!StringUtils.isEmpty(orderbatch)){ //订单批次
			sbsql.append(" and ob.batch like :orderbatch ");
		}
		if(!StringUtils.isEmpty(address)){ //订单地址
			sbsql.append(" and ob.address like :address ");
		}
		
		sbsql.append(" union ALL")
			.append(" select count(*) counts")
			.append(" from sys_api_fendan saf")
			.append(" where 1=1 ");
			
		if(!StringUtils.isEmpty(admincode)){
			sbsql.append(" and saf.admincode like '"+admincodetemp+"%' ");
		}
		if(startDate!=null&&endDate!=null){
			sbsql.append(" and saf.fendan_time between :startDate and :endDate ");
		}
		if(deptIdList!=null&&deptIdList.size()>0){
			sbsql.append(" and saf.deptid in (:deptIdList) ");
		}
		if(!StringUtils.isEmpty(ordernum)){ //订单编号
			sbsql.append(" and saf.order_num like :ordernum ");
		}
		if(!StringUtils.isEmpty(address)){ //订单地址
			sbsql.append(" and saf.address like :address ");
		}
		//按照分单结果类型
		if(resulttype>0){
			sbsql.append(" and saf.resulttype=:resulttype ");
		}
		
		sbsql.append(" ) a ");
		
		Query query = em.createNativeQuery(sbsql.toString());
		
		if(resulttype>0){
			query.setParameter("resulttype", resulttype);
		}
		
		if(!StringUtils.isEmpty(ordernum)){ //订单编号
			query.setParameter("ordernum", "%"+ordernum+"%");
		}
		if(!StringUtils.isEmpty(orderbatch)){ //订单批次
			query.setParameter("orderbatch", orderbatch+"%");
		}
		if(!StringUtils.isEmpty(address)){ //订单地址
			query.setParameter("address", "%"+address+"%");
		}
		
		query.setParameter("deptIdList", deptIdList)
				.setParameter("startDate", startDate, TemporalType.TIMESTAMP)
				.setParameter("endDate", endDate, TemporalType.TIMESTAMP)
				.unwrap(SQLQuery.class)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List resultlist= query.getResultList();
		if(resultlist!=null&&resultlist.size()>0){
			Map m=(Map)resultlist.get(0);
			result=m.get("sumcount")==null?0:Integer.parseInt(m.get("sumcount").toString());
		}
		return result;
	}
	
}
