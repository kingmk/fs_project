package com.lmy.core.dao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
@Repository
public   class BaseDao  {
	protected static Logger logger = Logger.getLogger(BaseDao.class);
	@Autowired
	private org.mybatis.spring.SqlSessionTemplate lszhSqlSessionWriter;
	
	public void setElifeSqlSessionWriter(
			org.mybatis.spring.SqlSessionTemplate lszhSqlSessionWriter) {
		this.lszhSqlSessionWriter = lszhSqlSessionWriter;
	}
	public org.mybatis.spring.SqlSessionTemplate getSqlSession() {
		return lszhSqlSessionWriter;
	}
	
}
