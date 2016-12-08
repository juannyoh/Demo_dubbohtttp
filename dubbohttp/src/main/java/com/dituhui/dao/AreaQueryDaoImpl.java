package com.dituhui.dao;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class AreaQueryDaoImpl implements IAreaQueryDao {
	
	@PersistenceContext(unitName="OracleJPA")
	private EntityManager oracleEM;

	@Override
	public String queryNameByIdOrNumber(String id, String number,String dcode) {
		String areaname=null;
		if(StringUtils.isEmpty(id)&&StringUtils.isEmpty(number)){
			return null;
		}
		StringBuilder sbsql=new StringBuilder();
		sbsql.append(" select name from BIZ_AREA where 1=1 ");
		if(!StringUtils.isEmpty(id)){
			sbsql.append(" and id=:id ");
		}
		else if(!StringUtils.isEmpty(number)){
			sbsql.append(" and area_num=:number ");
			if(!StringUtils.isEmpty(dcode)){
				sbsql.append(" and dcode like :dcode ");
			}
		}
		
		Query query=oracleEM.createNativeQuery(sbsql.toString());
		if(!StringUtils.isEmpty(id)){
			query.setParameter("id",id);
		}
		else if(!StringUtils.isEmpty(number)){
			query.setParameter("number",number);
			if(!StringUtils.isEmpty(dcode)){
				query.setParameter("dcode",dcode+"%");
			}
		}
		
		query.unwrap(SQLQuery.class)
			.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List resultlist=query.getResultList();
		if(resultlist!=null&&resultlist.size()>0){
			Map m=(Map)resultlist.get(0);
			areaname=m.get("NAME")==null?null:m.get("NAME").toString();
		}
		return areaname;
	}

}
