package com.maoding.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 深圳市设计同道技术有限公司
 * 类    名：ExcelUtils
 * 类描述：Excel操作公共方法
 * 作    者：Zhangchengliang
 * 日    期：2017/7/19
 */
public class ExcelUtils {

	/** 日志对象 */
	private static final Logger log = LoggerFactory.getLogger(ExcelUtils.class);

	/**
	 * 作用：关闭输入流
	 * 作者: Zhangchengliang
	 * 日期：2017/7/19
     */
	public static void closeInputSteam(InputStream in){
		if (in != null){
			try {
				in.close();
			} catch (IOException e) {
				log.error("关闭时产生错误", e);
			}
		}
	}

	/**
	 * 作用：关闭工作簿
	 * 作者: Zhangchengliang
	 * 日期：2017/7/19
	 */
	public static void closeWorkbook(Workbook workbook){
		if (workbook != null){
			try {
				workbook.close();
			} catch (IOException e) {
				log.error("关闭工作簿时产生错误", e);
			}
		}		
	}

	/**
	 * 作用：根据输入流获取工作簿对象
	 * 作者：Zhangchengliang
	 * 日期：2017/7/19
	 */
	public static Workbook getWorkbook(InputStream in) throws IOException{
		if (in == null) return null;

		Workbook wb = null;
		try {
			wb = WorkbookFactory.create(in);
		} catch (InvalidFormatException e) {
			throw new IOException("文件格式错误");
		}
		return wb;
	}

	/**
	 * 作用：根据文件名获取工作簿对象
	 * 作者：Zhangchengliang
	 * 日期：2017/7/19
	 */	
	public static Workbook getWorkbook(String fileName){
		if (fileName == null) return null;

		InputStream in = null;
		Workbook wb = null;
		try {
			in = new FileInputStream((new File(fileName))); 
			wb = getWorkbook(in);
		} catch (FileNotFoundException e) {
			log.error("无法找到"+fileName, e);
			closeInputSteam(in);
		} catch (IOException e) {
			log.error("读取"+fileName+"失败", e);
			closeWorkbook(wb);
			closeInputSteam(in);
		}
		return wb;
	}

	/**
	 * 作用：获取一个空的工作簿对象
	 * 作者：Zhangchengliang
	 * 日期：2017/7/19
     */
	public static Workbook getWorkbook(){
		return new HSSFWorkbook();
	}

	/**
	 * 作用：读取单元格内容
	 * 作者：Zhangchengliang
	 * 日期：2017/7/19
     */
	public static Object readFrom(Cell cell){
		if(cell==null) return null;

		switch (cell.getCellType()) {
			case Cell.CELL_TYPE_BLANK:
				return "";
			case Cell.CELL_TYPE_BOOLEAN:
				return cell.getBooleanCellValue();
			case Cell.CELL_TYPE_ERROR:
				return cell.getErrorCellValue();
			case Cell.CELL_TYPE_FORMULA:
				return cell.getCellFormula().replaceAll("\"", "");
			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					return cell.getDateCellValue();
				} else {
					return cell.getNumericCellValue();
				}
			case Cell.CELL_TYPE_STRING:
				return cell.getStringCellValue();
		}
		return "Unknown Cell Type:" + cell.getCellType();
	}

	/**
	 * 作用：读取一行内容
	 * 作者：Zhangchengliang
	 * 日期：2017/7/19
	 */
	public static List<Object> readFrom(Row row, Short startColumn, Short endColumn){
		if (row == null) return null;
		if (startColumn == null) startColumn = row.getFirstCellNum();
		if (endColumn == null) endColumn = row.getLastCellNum();
		
		//从startColumn处开始读取数据
		List<Object> dataList = new ArrayList<>();
		for(Short i=startColumn; i<=endColumn; i++){
			dataList.add(readFrom(row.getCell(i)));
		}
		return dataList;
	}

	/**
	 * 作用：读取一页内容
	 * 作者：Zhangchengliang
	 * 日期：2017/7/19
	 */
	public static List<List<Object>> readFrom(Sheet sheet, Integer titleRow, Integer startRow,Short startColumn,Short endColumn){
		if (sheet == null) return null;
		if (titleRow == null) titleRow = sheet.getFirstRowNum();
		if (startRow == null) startRow = titleRow + 1;

		//读取标题数据
		List<List<Object>> dataList = new ArrayList<>();
		Integer endRow = sheet.getLastRowNum();
		if ((sheet.getFirstRowNum() <= titleRow) && (titleRow <= sheet.getLastRowNum())) {
			Row row = sheet.getRow(titleRow);
			if (startColumn == null) startColumn = row.getFirstCellNum();
			if (endColumn == null) endColumn = row.getLastCellNum();
			dataList.add(readFrom(row,startColumn,endColumn));
		}
		
		//读取数据行
		for (Integer i=startRow; i<=endRow; i++){
			dataList.add(readFrom(sheet.getRow(i),startColumn,endColumn));
		}
		return dataList;
	}

	/**
	 * 作用：读取工作簿内指定页内容
	 * 作者：Zhangchengliang
	 * 日期：2017/7/19
	 */
	public static List<List<Object>> readFrom(Workbook workbook,Integer sheetIndex,Integer titleRow,Integer startRow,Short startColumn,Short endColumn){
		if (workbook == null) return null;
		if (sheetIndex == null) sheetIndex = workbook.getActiveSheetIndex();
		
		return ((0<=sheetIndex) && (sheetIndex<workbook.getNumberOfSheets())) ?
				readFrom(workbook.getSheetAt(sheetIndex),titleRow,startRow,startColumn,endColumn) : null;
	}
	public static List<List<Object>> readFrom(Workbook workbook,String sheetName,Integer titleRow,Integer startRow,Short startColumn,Short endColumn){
		if ((workbook == null) || (sheetName == null)) return null;
		return readFrom(workbook.getSheet(sheetName),titleRow,startRow,startColumn,endColumn);
	}
}
