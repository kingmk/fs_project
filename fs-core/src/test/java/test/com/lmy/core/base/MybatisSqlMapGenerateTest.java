package test.com.lmy.core.base;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Test;

public class MybatisSqlMapGenerateTest  extends TestCase{
    private static Logger logger = Logger.getLogger(MybatisSqlMapGenerateTest.class);

    public static final String url = "jdbc:mysql://fs_db:3306/fs_dev";  
    public static final String name = "com.mysql.jdbc.Driver";  
    public static final String user = "root";  
    public static final String password = "111111";  

    @Test
    public void test_Generate() throws Exception {
    	//this.generate("fs_chat_record");
    	//this.generate("fs_chat_session");
    	//this.generate("fs_master_fans");
    	//this.generate("fs_master_info");
    	//this.generate("fs_order");
    	this.generate("100106");
    }

    private void generate(String tableName) throws Exception {
        Class.forName(name);//指定连接类型  
        tableName = tableName.toLowerCase();
        String root = "D:/fidel/2017/sqlmap/";
        String packageName = "com.lmy.core.model";
        String packageDaoName = "com.lmy.core.dao";
        Connection conn  = DriverManager.getConnection(url, user, password);//获取连接  
        Statement state = conn.createStatement();
        ResultSet rs = state.executeQuery("select * from    " + tableName+" where 1=2");
        ResultSetMetaData meta = rs.getMetaData();
        ResultSet rsDesc = state.executeQuery("show full columns from " + tableName);
        File file = new File(root + tableName + ".xml");
        PrintWriter fw = new PrintWriter(file);

        fw.println("<?xml version=\"1.0\" encoding= \"UTF-8\" ?> "
                + "       \n<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
        fw.println("<mapper namespace=\"" + tableName + "\">");
        fw.println("<resultMap id=\"" + tableName + "\" type=\"" + packageName+"."+ firstUp(convertS(tableName))+ "\">");
       

        for (int i = 1; i < meta.getColumnCount() + 1; i++) {
            fw.println("	<result column=\"" + meta.getColumnName(i).toLowerCase() + "\" property=\"" + convertS(meta.getColumnName(i).toLowerCase()) + "\"/>");
        }
        fw.println("</resultMap>");


        fw.println("<select id=\"findById\" parameterType=\"long\"  resultMap=\"" + tableName + "\">");
        fw.print("	select *");
        fw.println(" 	from " + tableName + " where " +convertS(meta.getColumnName(1).toLowerCase())+"=#{id}  ");
        fw.println("</select>  ");

        // 单笔插入开始~~~~~~~~~~~~~~~~~~~~~~~~
        //fw.println("<insert id=\"insert\"  parameterType=\"" + packageName+"."+ firstUp(convertS(tableName)) + "\">");
        //fw.println("<selectKey resultType=\"java.lang.Long\" order=\"AFTER\" keyColumn=\"id\" keyProperty=\"id\"> SELECT LAST_INSERT_ID() </selectKey>");
        fw.println("<insert id=\"insert\"  parameterType=\"" + packageName+"."+ firstUp(convertS(tableName)) + "\"  useGeneratedKeys=\"true\" keyProperty=\"id\">");
        fw.print("	insert into " + tableName + "(");
        for (int i = 1; i < meta.getColumnCount() + 1; i++) {
            if (i == 1) {
                fw.print(meta.getColumnName(i).toLowerCase());
            } else {
                fw.print(" ," + meta.getColumnName(i).toLowerCase());
            }
        }
        fw.print(")\n");
        fw.print("   values(");

        for (int i = 1; i < meta.getColumnCount() + 1; i++) {
            int   colType =   meta.getColumnType(i);
            if (i == 1) {
                fw.print("#{" + convertS(meta.getColumnName(i).toLowerCase()) + ",jdbcType="+getCorrespondingStr2(colType)+  "}");
            } else {
                fw.print(" ,#{" + convertS(meta.getColumnName(i).toLowerCase()) +  ",jdbcType="+getCorrespondingStr2(colType)+  "}");
            }
        }
        fw.print(")");
        fw.println(" \n</insert>");
        // 单笔插入结束~~~~~~~~~~~~~~~~~~~~~~~~
        
        // 批量插入开始~~~~~~~~~~~~~~~~~~~~~~~~
        fw.println("<insert id=\"batchInsert\"  parameterType=\"list\">");

        fw.print("	insert into " + tableName + "(");
        for (int i = 1; i < meta.getColumnCount() + 1; i++) {
            if (i == 1) {
                fw.print(meta.getColumnName(i).toLowerCase());
            } else {
                fw.print(" ," + meta.getColumnName(i).toLowerCase());
            }
        }
        fw.print(")\n");
        fw.println("   values");
        fw.println("	<foreach collection=\"list\" item=\"item\" index=\"index\"   separator=\",\"> ");
        for (int i = 1; i < meta.getColumnCount() + 1; i++) {
            int   colType =   meta.getColumnType(i);
            if (i == 1) {
                fw.print("    (#{item." + convertS(meta.getColumnName(i).toLowerCase()) + ",jdbcType="+getCorrespondingStr2(colType)+  "}");
            } else {
                fw.print(" ,#{item." + convertS(meta.getColumnName(i).toLowerCase()) +  ",jdbcType="+getCorrespondingStr2(colType)+  "}");
            }
        }
        fw.println(")");
        fw.println("	</foreach> ");
        fw.println("</insert>");
        // 批量插入结束~~~~~~~~~~~~~~~~~~~~~~~~
        
        
        
        // update
        fw.print("<update id=\"update" + "\"");
        fw.println("	parameterType=\"" +  packageName+"."+ firstUp(convertS(tableName))  + "\">");
        fw.println("	update " + tableName);
        fw.println("<set>");
        for (int i = 1; i < meta.getColumnCount() + 1; i++) {
        	rsDesc.absolute(i);
        	String keyDesc = rsDesc.getString(5);
        	System.out.println("field="+rsDesc.getString(1)+",Key="+keyDesc);
        	if("PRI".equals(keyDesc) || "MUL".equals(keyDesc) || "UNI".equals(keyDesc) ||rsDesc.getString(1).startsWith("create")){
        		continue;
        	}
        	int skipNum = this.nextSkipNum(i + 1 , meta,rsDesc);
        	
            if (skipNum +i == meta.getColumnCount()    ) {
                fw.println("	<if test=\"" + convertS(meta.getColumnName(i).toLowerCase()) + " != null\">" + meta.getColumnName(i).toLowerCase() + "=#{"
                        + convertS(meta.getColumnName(i).toLowerCase()) + "}</if>");
            } else {
                fw.println("	<if test=\"" + convertS(meta.getColumnName(i).toLowerCase()) + " != null\">" + meta.getColumnName(i).toLowerCase() + "=#{"
                        + convertS(meta.getColumnName(i).toLowerCase()) + "},</if>");
            }
        }
        fw.println("</set>");
        fw.println("	where "+(convertS(meta.getColumnName(1).toLowerCase()))+"=#{id}");
        fw.println("</update>");
        fw.println("</mapper>");
        fw.flush();
        fw.close();
        // 生成sql-map结束，生成 java bean model
        File fileBean = new File(root +firstUp(convertS(tableName))+ ".java");
        PrintWriter fwBean = new PrintWriter(fileBean);
        String   className = firstUp(convertS(tableName));
        fwBean.println("package "+packageName+" ;");
        fwBean.println("import com.alibaba.fastjson.JSON;");
        fwBean.println("import com.alibaba.fastjson.serializer.SerializerFeature;");
        fwBean.println("import java.util.Date;");
        fwBean.println("import com.lmy.common.model.base.BaseObject;");
        fwBean.println("@SuppressWarnings(\"serial\")");
        fwBean.println("public class "+className+"  extends BaseObject {");
        int colType = 0;
        int colscale = 0;
        for (int i = 1; i < meta.getColumnCount() + 1; i++) {
            colType =   meta.getColumnType(i);
            colscale = meta. getScale(i);
            logger.info(meta.getColumnLabel(i)+','+meta.getColumnType(i)+","+  meta. getScale(i));
            rsDesc.absolute(i);
            String columnName = rsDesc.getString(1);
            if("id".equals(columnName) || "create_time".equals(columnName)){
            	continue;
            }
            fwBean.println("  /** "+rsDesc.getString("Comment") +" */");
            String fieldName = convertS(meta.getColumnName(i).toLowerCase());
            fwBean.println("  private  "+getCorrespondingStr(colType,colscale)+"  "+fieldName+ ";");
        }
        
        for (int i = 1; i < meta.getColumnCount() + 1; i++) {
            colType =   meta.getColumnType(i);
            colscale = meta. getScale(i);
            logger.info(meta.getColumnLabel(i)+','+meta.getColumnType(i)+","+  meta. getScale(i));
            rsDesc.absolute(i);
            String columnName = rsDesc.getString(1);
            String fieldName = convertS(meta.getColumnName(i).toLowerCase());
            String methodSuffixes = fieldName.substring(0, 1).toUpperCase() +fieldName.substring(1);
            if("id".equals(columnName) || "create_time".equals(columnName)){
            	continue;
            }
            //begin get , set  modify 2016-04 -07
            fwBean.println("  /** "+rsDesc.getString("Comment") +" */");
            fwBean.println("	public "+getCorrespondingStr(colType,colscale) +" get" + methodSuffixes+"(){");
            fwBean.println("		return this."+fieldName+";");
            fwBean.println("	}");
            fwBean.println("  /** "+rsDesc.getString("Comment") +" */");
            fwBean.println("	public "+className +" set" + methodSuffixes+"("+getCorrespondingStr(colType,colscale)+" "+fieldName+"){");
            fwBean.println("		 this."+fieldName+"="+fieldName+";");
            fwBean.println("		 return this;");
            fwBean.println("	}");
            //end get ,set   modify 2016-04 -07
        }
        
        fwBean.println("	public String toString(){");
        fwBean.println("	    return JSON.toJSONString(this,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);");
        fwBean.println("	}");
        fwBean.println("}");
        fwBean.flush();
        fwBean.close();
        
        // 生成java bean model 结束，生成 java Dao
        String   classDaoName = firstUp(convertS(tableName))+"Dao";
        File fileDao = new File(root +classDaoName+ ".java");
        PrintWriter fwDao = new PrintWriter(fileDao);
        fwDao.println("package "+packageDaoName+" ;");
        fwDao.println("import org.springframework.stereotype.Component;");
        fwDao.println("import "+packageName+"."+className +";");
        fwDao.println("@Component");
        fwDao.println("public class "+classDaoName+" extends GenericDAOImpl<"+className+"> {");
        fwDao.println("	@Override");
        fwDao.println("	public String getNameSpace() {");
        fwDao.println("		return \""+tableName+"\";");
        fwDao.println("	}");
        fwDao.println("}");
        fwDao.flush();
        fwDao.close();
        conn.close();
    }

    
    private int nextSkipNum(int i ,  ResultSetMetaData meta,ResultSet rsDesc) throws SQLException{
    	
    	int count = meta.getColumnCount();
    	if(i>=count +1){
    		return 0;
    	}
    	int num = 0;
//    	int k =i;
    	rsDesc.absolute(i-1);
    	while(rsDesc.next()){
    		String keyDesc = rsDesc.getString(5);
    		System.out.println("~~~field="+rsDesc.getString(1)+",Key="+keyDesc);
    		if("PRI".equals(keyDesc) || "MUL".equals(keyDesc) || "UNI".equals(keyDesc) ||rsDesc.getString(1).startsWith("create")){
    			num ++;
//    			k++;
        	}else{
        		break;
        	}
    	}
    	return num;
    }
    
    
    public String firstUp(String str){
        String s =    str.substring(0, 1);
        return s.toUpperCase()+str.substring(1);
    }
    
    private String getCorrespondingStr2(Integer colType){
        Map<String,String> map = new HashMap<String,String>();
        map.put("-7",        "BIT");      
        map.put("-6",        "TINYINT");      
        map.put("5",         "SMALLINT");     
        map.put("4",         "INTEGER");      
        map.put("-5",        "BIGINT");       
        map.put("6",         "FLOAT");        
        map.put("7",         "REAL");         
        map.put("8",         "DOUBLE");       
        map.put("2",         "NUMERIC");      
        map.put("3",         "DECIMAL");      
        map.put("1",         "CHAR");         
        map.put("12",        "VARCHAR");      
        map.put("-1",        "LONGVARCHAR");  
        map.put("91",        "DATE");         
        map.put("92",        "TIME");         
        map.put("93",        "TIMESTAMP");    
        map.put("-2",        "BINARY");       
        map.put("-3",        "VARBINARY");    
        map.put("-4",        "LONGVARBINARY");
        map.put("0",         "NULL");         
        map.put("1111",      "OTHER");        
        map.put("2000",      "JAVA_OBJECT");  
        map.put("2001",      "DISTINCT");     
        map.put("2002",      "STRUCT");       
        map.put("2003",      "ARRAY");        
        map.put("2004",      "BLOB");         
        map.put("2005",      "CLOB");         
        map.put("2006",      "REF");          
        map.put("70",        "DATALINK");     
        map.put("16",        "BOOLEAN");      
        map.put("-8",        "ROWID");        
        map.put("-15",       "NCHAR");        
        map.put("-9",        "NVARCHAR");     
        map.put("-16",       "LONGNVARCHAR"); 
        map.put("2011",      "NCLOB");        
        map.put("2009",      "SQLXML");     
        return map.get(colType.toString());
    }
    
    
    private String getCorrespondingStr(int colType ,int colscale){
        if(Types.TINYINT==colType || Types.SMALLINT==colType  ){
            return "Integer";
        }
        if(Types.INTEGER==colType || Types.BIGINT==colType  || Types.FLOAT ==colType  ){
            if(colscale>0){
                return "BigDecimal";
            }else{
                return "Long";    
            }
        }
        if(Types.DOUBLE==colType   || Types.DECIMAL ==colType || Types.NUMERIC==colType){
            if(colscale>0){
                return "BigDecimal";
            }else{
                return "Long";    
            }
        }
        if(Types.CHAR==colType || Types.VARCHAR==colType  || Types.LONGVARCHAR ==colType || Types.NCHAR==colType || Types.NVARCHAR==colType ){
            return "String";
        }
        if(Types.DATE==colType || Types.TIME==colType  || Types.TIMESTAMP ==colType  ){
            return "Date";
        }
        return "Object";
    }
    
    
    private String convertS(String s) {
        String returnValue = s;
        if (s != null) {
            String[] ss = s.split("_");
            logger.debug(Arrays.toString(ss));
            if (ss != null && ss.length > 1) {
                int i = 0;
                for (String _s : ss) {
                    ++i;
                    if (i == 1) {
                        returnValue = _s;
                    } else {
                        returnValue = returnValue + _s.substring(0, 1).toUpperCase() + _s.substring(1);
                    }
                }
                i = 0;
            }
        } else {
            return returnValue;
        }
        return returnValue;
    }

    public static void main(String[] args) throws Exception {
        MybatisSqlMapGenerateTest test = new MybatisSqlMapGenerateTest();
        logger.info("begin");
        // cd_audit ,
        // cd_business_infor,cd_loan,cd_login_log,cd_review,cd_user,cd_user_infor
        // test.generate("User");
        test.generate("fs_usr");
    }
}
