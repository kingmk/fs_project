package test.com.lmy.core.service;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;

import junit.framework.TestCase;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmy.common.component.AES256Encryption;

public class TestExcelRead extends TestCase {

	private static Logger logger = LoggerFactory.getLogger(TestExcelRead.class);
	
	
	private  String getCellStringValue(Cell cell){
		if(cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK){
			return null;
		}else if(cell.getCellType() == Cell.CELL_TYPE_BOOLEAN){
			return String.valueOf(cell.getBooleanCellValue());
		}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
			Double d = cell.getNumericCellValue();
			double p = d.doubleValue()-d.longValue();
			if(p!=0.00){
				java.text.DecimalFormat df = new java.text.DecimalFormat("########.00");
				return String.valueOf(df.format(d));
			}
			return String.valueOf((d.longValue()));
		}else{
			return cell.getStringCellValue().trim();
		}
	}
	
	public void test_01() throws Exception{
		Workbook wb  = new XSSFWorkbook(  new FileInputStream( new File("D:/fidel/workspace/doc/2017/sqlResult_1227638.csv") ) );
		Sheet sheet1 = wb.getSheetAt(0);
		if(sheet1==null ){
			
		}
		int i = 0;
		for (Row row : sheet1) {//第一列手机号码;第二列用户姓名{
			logger.info("id:{},usrId:{},name:{},cert_no:{},bankName:{},cardNum:{},mobile{}" 
					,getCellStringValue(  row.getCell(0)  )
						,getCellStringValue(  row.getCell(1)  )
							,getCellStringValue(  row.getCell(2)  )
								,getCellStringValue(  row.getCell(3)  )
									,getCellStringValue(  row.getCell(4)  )
										,getCellStringValue(  row.getCell(5)  )
							  );
			if(i>10){
				break;
			}
		}
		wb.close();
	}
	
	public void test_02() throws Exception{
		BufferedReader br = new BufferedReader( new FileReader( new File("D:/fidel/workspace/doc/2017/sqlResult_1227638.txt") ) );
		String _str = "";
		String [] _aStr = null;
		int i = 0;
		StringBuffer sb =  new StringBuffer();
		while(  (_str = br.readLine() )!=null ){
			_aStr = _str.split(",");
			if(i ==0){
				sb.append(_aStr[0]).append(",");
				sb.append(_aStr[1]).append(",");
				sb.append(_aStr[2]).append(",");
				sb.append(_aStr[3]).append(",");
				sb.append(_aStr[4]).append(",");
				sb.append(_aStr[5]).append(",");
				sb.append(_aStr[6]);
			}else{
				sb.append(_aStr[0]).append(",");
				sb.append(_aStr[1]).append(",");
				sb.append("\""+ AES256Encryption.decrypt(_aStr[2].replaceAll("\"", "") )+"\"").append(",");
				sb.append("\""+AES256Encryption.decrypt(_aStr[3].replaceAll("\"", "") ) +"\"").append(",");
				sb.append(_aStr[4]).append(",");
				sb.append("\""+AES256Encryption.decrypt(_aStr[5].replaceAll("\"", "") )+"\"").append(",");
				sb.append(_aStr[6]);				
			}
			sb.append("\n");
			i++;
		}
		br.close();
		BufferedWriter bw = new BufferedWriter( new FileWriter( new File("D:/fidel/workspace/doc/2017/plaint_1227638.txt") ) );
		bw.write(sb.toString());
		bw.close();
	}
	

	public void test_03() throws Exception{
													
		System.out.println( AES256Encryption.decrypt("qp5sAH/d9IPDq6h3OH/LuUSwJNBMhx8Hx9piS4kUpQ4=") );
	}
	
	
}
