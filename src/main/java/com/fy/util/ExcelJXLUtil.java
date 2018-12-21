package com.fy.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * 
 * @Description <p>使用JXL实现的excel操作类</p>
 * @version <p>v1.0</p>
 * @Date <p>2018年7月4日 下午2:57:28</p> 
 * @author <p>fangyang</p>
 *
 */
public class ExcelJXLUtil {

	public static final String SUFFIX_XLS = ".xls";
	public static final String SUFFIX_XLSX = ".xlsx";

	/**
	 * 修改excel文件的内容
	 * 
	 * @param filePath
	 *            excel文件路径
	 * @param oneUpdateExcelPojoList
	 *            修改内容的集合
	 * @return 是否修改成功
	 */
	public static boolean updateExcel(String filePath, List<OneUpdateExcelPojo> oneUpdateExcelPojoList) {
		Workbook wk = null;
		WritableWorkbook wwb = null;
		File file = new File(filePath);
		if (!file.exists() || !file.isFile()) {
			throw new RuntimeException(filePath + "路径文件不存在！");
		}
		if (!filePath.endsWith(SUFFIX_XLS) && !filePath.endsWith(SUFFIX_XLSX)) {
			throw new IllegalArgumentException("文件类型错误！");
		}
		if (null == oneUpdateExcelPojoList) {
			throw new NullPointerException("修改内容不能为空！");
		}
		if (0 == oneUpdateExcelPojoList.size()) {
			return false;
		}
		try {
			/* 导入已存在的Excel文件，获得只读的工作薄对象 */
			try {
				wk = Workbook.getWorkbook(file);
			} catch (Exception e) {
				throw new RuntimeException(filePath + "文件已损坏！");
			}
			/* 根据只读的工作薄对象（wk）创建可写入的Excel工作薄对象 */
			wwb = Workbook.createWorkbook(file, wk);
			for (OneUpdateExcelPojo oneUpdateExcelPojo : oneUpdateExcelPojoList) {
				/* 读取工作表 */
				WritableSheet sheet = wwb.getSheet(oneUpdateExcelPojo.getSheetIndex());
				/* 获得要编辑的单元格对象 */
				WritableCell cell = sheet.getWritableCell(oneUpdateExcelPojo.getColIndex(),
						oneUpdateExcelPojo.getRowIndex());
				/* 判断单元格的类型, 做出相应的转化 */
				if (cell.getType() == CellType.LABEL) {
					Label lable = (Label) cell;
					/* 修改单元格的内容 */
					lable.setString(oneUpdateExcelPojo.getContent());
				}
			}
			wwb.write();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (null != wwb)
					wwb.close();
				if (null != wk)
					wk.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 
	 * @Description 封装修改内容信息的类
	 * @version v1.0
	 * @Date 2018年6月7日 下午11:24:55
	 *
	 * @author fangyang
	 */
	public static class OneUpdateExcelPojo {
		private int sheetIndex;
		private int rowIndex;
		private int colIndex;
		private String content;

		public int getSheetIndex() {
			return sheetIndex;
		}

		public int getRowIndex() {
			return rowIndex;
		}

		public int getColIndex() {
			return colIndex;
		}

		public String getContent() {
			return content;
		}

		public OneUpdateExcelPojo(int sheetIndex, int rowIndex, int colIndex, String content) {
			this.sheetIndex = sheetIndex;
			this.rowIndex = rowIndex;
			this.colIndex = colIndex;
			this.content = content;
		}
	}

	/**
	 * 读取excel文件内容
	 * 
	 * @param filePath
	 *            excel文件路径
	 * @return List < List < List < Object 内容> 行> 表> 读取到的内容（excel文件的所有表的所有行列）
	 */
	public static List<List<List<Object>>> readFromExcel(String filePath) {
		FileInputStream fis = null;
		Workbook wk = null;
		File file = new File(filePath);
		if (!file.exists() || !file.isFile()) {
			throw new RuntimeException(filePath + "路径文件不存在！");
		}
		if (!filePath.endsWith(SUFFIX_XLS) && !filePath.endsWith(SUFFIX_XLSX)) {
			throw new IllegalArgumentException("文件类型错误！");
		}
		try {
			List<List<List<Object>>> data = new ArrayList<>();
			List<List<Object>> sheetData = new ArrayList<>();
			/* 导入已存在的Excel文件，获得只读的工作薄对象 */
			fis = new FileInputStream(filePath);
			wk = Workbook.getWorkbook(fis);
			/* 获取Sheet表 */
			Sheet[] sheets = wk.getSheets();
			if (null == sheets || 0 == sheets.length) {
				return null;
			}
			for (Sheet sheet : sheets) {
				/* 获取总行数 */
				int rowNum = sheet.getRows();
				/* 从数据行开始迭代每一行 */
				for (int i = 0; i < rowNum; i++) {
					List<Object> rowData = new ArrayList<>();
					/* getCell(column,row)，表示取得指定列指定行的单元格（Cell） */
					/* getContents()获取单元格的内容，返回字符串数据。适用于字符型数据的单元格 */
					int colNum = sheet.getColumns();
					for (int j = 0; j < colNum; j++) {
						rowData.add(sheet.getCell(j, i).getContents());
					}
					sheetData.add(rowData);
				}
				data.add(sheetData);
			}
			return data;
		} catch (Exception e) {
			return null;
		} finally {
			try {
				if (null != fis)
					fis.close();
				if (null != wk)
					wk.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 向excel文件中写入内容
	 * 
	 * @param filePath
	 *            excel文件路径（存在则写，不存在则新建）
	 * @param titleList
	 *            表格内容的标题的集合，若有则在首行居中显示，若为null则不显示
	 * @param data
	 *            要写入的内容
	 * @return 是否写入成功
	 * @throws FileNotFoundException
	 *             excel所在目标路径文件夹不存在
	 */
	public static boolean writeDataToExcel(String filePath, List<String> titleList,
			Map<String, List<List<Object>>> data) throws FileNotFoundException {
		FileOutputStream fos = null;
		WritableWorkbook wk = null;
		File file = new File(filePath);
		File parentFile = file.getParentFile();
		if (!parentFile.exists()) {
			throw new FileNotFoundException(parentFile.getAbsolutePath() + "路径不存在！");
		}
		if (file.exists() && file.isDirectory()) {
			throw new RuntimeException(filePath + "是一个文件夹！");
		}
		if (!filePath.endsWith(SUFFIX_XLS) && !filePath.endsWith(SUFFIX_XLSX)) {
			throw new IllegalArgumentException("文件类型错误！");
		}
		if (null == data) {
			throw new NullPointerException("写入内容不能为空！");
		}
		if (0 == data.size()) {
			return false;
		}
		try {
			fos = new FileOutputStream(file);
			/* 创建可写入的Excel工作薄，且内容将写入到输出流 */
			wk = Workbook.createWorkbook(fos);
			/* 创建可写入的Excel工作表 */
			WritableSheet sheet = null;
			/* 创建WritableFont 字体对象，参数依次表示黑体、字号12、粗体、非斜体、不带下划线、亮蓝色 */
			WritableFont titleFont = new WritableFont(WritableFont.createFont("黑体"), 12, WritableFont.BOLD, false,
					UnderlineStyle.NO_UNDERLINE, Colour.LIGHT_BLUE);
			/* 创建WritableCellFormat对象，将该对象应用于单元格从而设置单元格的样式 */
			WritableCellFormat titleFormat = new WritableCellFormat();
			/* 设置字体格式 */
			titleFormat.setFont(titleFont);
			/* 设置文本水平居中对齐 */
			titleFormat.setAlignment(Alignment.CENTRE);
			/* 设置文本垂直居中对齐 */
			titleFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			/* 设置背景颜色 */
			titleFormat.setBackground(Colour.GRAY_25);
			/* 设置自动换行 */
			titleFormat.setWrap(true);
			WritableCellFormat cloumnFormat = new WritableCellFormat();
			cloumnFormat.setFont(new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD, false));
			cloumnFormat.setAlignment(Alignment.CENTRE);
			int sheetIndex = 0;
			for (Map.Entry<String, List<List<Object>>> entry : data.entrySet()) {
				boolean hasTitle = false;
				sheet = wk.createSheet(entry.getKey(), sheetIndex++);
				List<List<Object>> sheetData = entry.getValue();
				if (sheetData != null && 0 < sheetData.size()) {
					if (null != titleList && titleList.size() > (sheetIndex - 1)) {
						String title = titleList.get((sheetIndex - 1));
						if (null != title) {
							hasTitle = true;
							int colCount = sheetData.get(0).size();
							/* 把单元格（column1, row1）到单元格（column2, row2）进行合并。 */
							/* Cells(column1, row1, column2, row2); */
							sheet.mergeCells(0, 0, colCount - 1, 0);// 单元格合并方法
							/* 添加Label对象，参数依次表示在第一列，第一行，内容，使用的格式 */
							Label lab_title = new Label(0, 0, title, titleFormat);
							sheet.addCell(lab_title);
						}
					}
					Label label = null;
					for (int i = 0; i < sheetData.size(); i++) {
						List<Object> rowData = sheetData.get(i);
						for (int j = 0; j < rowData.size(); j++) {
							Object colData = rowData.get(j);
							if (hasTitle) {
								label = new Label(j, i + 1, colData + "", cloumnFormat);
							} else {
								label = new Label(j, i, colData + "", cloumnFormat);
							}
							sheet.addCell(label);
						}
					}
				}
			}
			/* 将定义的工作表输出到之前指定的介质中 */
			wk.write();
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (null != wk)
					wk.close();
				if (null != fos)
					fos.close();
			} catch (Exception e) {
			}
		}
	}
}
