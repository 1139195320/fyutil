package store.javac.fyutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alibaba.fastjson.JSONArray;

/**
 * 
 * @Description <p>使用POI实现的excel操作类</p>
 * @version <p>v1.0</p>
 * @Date <p>2018年7月4日 下午2:59:53</p> 
 * @author <p>jack</p>
 *
 */
public class ExcelPOIUtil {

	public static final String SUFFIX_XLS = ".xls";
	public static final String SUFFIX_XLSX = ".xlsx";

	public static final Integer FONT_TARGET_CELL = 0x00;
	public static final Integer FONT_TARGET_ROW = 0x01;
	public static final Integer FONT_TARGET_COL = 0x02;

	private static final List<Integer> FONT_TARGET_LIST = new ArrayList<>();

	public static final short CELL_COLOR_RED = HSSFColor.RED.index;
	public static final short CELL_COLOR_BLUE = HSSFColor.BLUE.index;
	public static final short CELL_COLOR_BLACK = HSSFColor.BLACK.index;
	public static final short CELL_COLOR_WHITE = HSSFColor.WHITE.index;
	public static final short CELL_COLOR_GREEN = HSSFColor.GREEN.index;
	public static final short CELL_COLOR_AQUA = HSSFColor.AQUA.index;
	public static final short CELL_COLOR_BROWN = HSSFColor.BROWN.index;
	public static final short CELL_COLOR_LIME = HSSFColor.LIME.index;
	public static final short CELL_COLOR_YELLOW = HSSFColor.YELLOW.index;
	public static final short CELL_COLOR_GOLD = HSSFColor.GOLD.index;

	private static final List<Short> CELL_COLOR_LIST = new ArrayList<>();

	static {
		FONT_TARGET_LIST.add(FONT_TARGET_CELL);
		FONT_TARGET_LIST.add(FONT_TARGET_ROW);
		FONT_TARGET_LIST.add(FONT_TARGET_COL);

		CELL_COLOR_LIST.add(CELL_COLOR_RED);
		CELL_COLOR_LIST.add(CELL_COLOR_BLUE);
		CELL_COLOR_LIST.add(CELL_COLOR_BLACK);
		CELL_COLOR_LIST.add(CELL_COLOR_WHITE);
		CELL_COLOR_LIST.add(CELL_COLOR_GREEN);
		CELL_COLOR_LIST.add(CELL_COLOR_AQUA);
		CELL_COLOR_LIST.add(CELL_COLOR_BROWN);
		CELL_COLOR_LIST.add(CELL_COLOR_LIME);
		CELL_COLOR_LIST.add(CELL_COLOR_YELLOW);
		CELL_COLOR_LIST.add(CELL_COLOR_GOLD);
	}

	/**
	 * 合并单元格的类
	 * 
	 * @author fy
	 *
	 */
	public static class CellRanger {
		/**
		 * 起始行
		 */
		private Integer firstRow;
		/**
		 * 终止行
		 */
		private Integer lastRow;
		/**
		 * 起始列
		 */
		private Integer firstCol;
		/**
		 * 终止列
		 */
		private Integer lastCol;

		public Integer getFirstRow() {
			return firstRow;
		}

		public void setFirstRow(Integer firstRow) {
			this.firstRow = firstRow;
		}

		public Integer getLastRow() {
			return lastRow;
		}

		public void setLastRow(Integer lastRow) {
			this.lastRow = lastRow;
		}

		public Integer getFirstCol() {
			return firstCol;
		}

		public void setFirstCol(Integer firstCol) {
			this.firstCol = firstCol;
		}

		public Integer getLastCol() {
			return lastCol;
		}

		public void setLastCol(Integer lastCol) {
			this.lastCol = lastCol;
		}

		public CellRanger(int firstRow, int lastRow, int firstCol, int lastCol) {
			this.firstRow = firstRow;
			this.lastRow = lastRow;
			this.firstCol = firstCol;
			this.lastCol = lastCol;
		}
	}

	/**
	 * 
	 * @Description 单元格或行的样式的类
	 * @version v1.0
	 * @Date 2018年6月6日 下午10:11:17
	 *
	 * @author jack
	 */
	public static class CellStyler {
		/**
		 * 前景色
		 */
		private short cellForegroundColor;
		/**
		 * 背景色
		 */
		private short cellBackgroundColor;
		/**
		 * 行
		 */
		private Integer row;
		/**
		 * 列
		 */
		private Integer col;
		/**
		 * true 单元格样式，false 行样式
		 */
		private Boolean isCellStyle;

		public short getCellForegroundColor() {
			return cellForegroundColor;
		}

		public short getCellBackgroundColor() {
			return cellBackgroundColor;
		}

		public Integer getRow() {
			return row;
		}

		public Integer getCol() {
			return col;
		}

		public Boolean getIsCellStyle() {
			return isCellStyle;
		}

		public CellStyler(short cellForegroundColor, short cellBackgroundColor, Integer row) {
			super();
			if (!CELL_COLOR_LIST.contains(cellForegroundColor)) {
				throw new IllegalArgumentException(cellForegroundColor + "该颜色不存在！");
			}
			if (!CELL_COLOR_LIST.contains(cellBackgroundColor)) {
				throw new IllegalArgumentException(cellBackgroundColor + "该颜色不存在！");
			}
			if (row == null) {
				throw new NullPointerException("行数不能为空！");
			}
			if (row < 1) {
				throw new IllegalArgumentException("行数不能小于1！");
			}
			this.cellForegroundColor = cellForegroundColor;
			this.cellBackgroundColor = cellBackgroundColor;
			this.row = row;
			this.isCellStyle = false;
		}

		public CellStyler(short cellForegroundColor, short cellBackgroundColor, Integer row, Integer col) {
			super();
			if (!CELL_COLOR_LIST.contains(cellForegroundColor)) {
				throw new IllegalArgumentException(cellForegroundColor + "该颜色不存在！");
			}
			if (!CELL_COLOR_LIST.contains(cellBackgroundColor)) {
				throw new IllegalArgumentException(cellBackgroundColor + "该颜色不存在！");
			}
			if (row == null) {
				throw new NullPointerException("行数不能为空！");
			}
			if (row < 1) {
				throw new IllegalArgumentException("行数不能小于1！");
			}
			if (col == null) {
				throw new NullPointerException("列数不能为空！");
			}
			if (col < 1) {
				throw new IllegalArgumentException("列数不能小于1！");
			}
			this.cellForegroundColor = cellForegroundColor;
			this.cellBackgroundColor = cellBackgroundColor;
			this.row = row;
			this.col = col;
			this.isCellStyle = true;
		}
	}

	public static class CellFontStyle {
		private int row;
		private int col;
		private int rowOrCol;
		private boolean hasUnderline;
		private short fontColor;
		private int fontTarget;

		public int getRow() {
			return row;
		}

		public int getCol() {
			return col;
		}

		public boolean isHasUnderline() {
			return hasUnderline;
		}

		public short getFontColor() {
			return fontColor;
		}

		public int getFontTarget() {
			return fontTarget;
		}

		public int getRowOrCol() {
			return rowOrCol;
		}

		public CellFontStyle(int rowOrCol, short fontColor,
				boolean hasUnderline, int fontTarget) {
			this.rowOrCol = rowOrCol;
			this.hasUnderline = hasUnderline;
			this.fontColor = fontColor;
			this.fontTarget = fontTarget;
			if (!CELL_COLOR_LIST.contains(fontColor)) {
				throw new IllegalArgumentException(fontColor + "该颜色不存在！");
			}
			if (rowOrCol < 0) {
				throw new NullPointerException("行列数不能小于0！");
			}
			if (!FONT_TARGET_LIST.contains(fontTarget)) {
				throw new IllegalArgumentException(fontTarget + "输入不正确！");
			}
		}

		public CellFontStyle(int row, int col, short fontColor,
				boolean hasUnderline) {
			this.row = row;
			this.col = col;
			this.hasUnderline = hasUnderline;
			this.fontColor = fontColor;
			this.fontTarget = FONT_TARGET_CELL;
			if (!CELL_COLOR_LIST.contains(fontColor)) {
				throw new IllegalArgumentException(fontColor + "该颜色不存在！");
			}
			if (row < 0) {
				throw new NullPointerException("行数不能小于0！");
			}
			if (col < 0) {
				throw new NullPointerException("列数不能小于0！");
			}
		}
	}

	/**
	 * 生成新的excel文件并写入数据
	 * 
	 * @param dataMap
	 *            要写入的数据 Map<String（sheet表名）, List<List<Object单元格数据>行集合>（对应的表数据）>
	 * @param fileDir
	 *            excel文件所在文件夹目录（绝对路径）
	 * @param fileName
	 *            excel文件名（不含后缀）
	 * @param excelSuffix
	 *            excel文件名的后缀
	 * @param cellRangerList
	 *            List< List< CellRanger > 表> 合并的单元格的集合
	 * @param cellStylerList
	 *            List< List< CellStyler > 表> 单元格或行的（前景色，背景色）样式的集合
	 * @param CellFontStyleList
	 *            List< List< CellFontStyle > > 表> 单元格或行或列的字体（字体颜色，下划线）样式的集合
	 * @param isCover
	 *            是否允许覆盖，当此文件存在时
	 * @return 是否操作成功
	 * @throws FileNotFoundException
	 *             fileDir路径不存在
	 */
	public static boolean generateExcelFile(Map<String, List<List<Object>>> dataMap, String fileDir, String fileName,
			String excelSuffix, List<List<CellRanger>> cellRangerList, List<List<CellStyler>> cellStylerList,
			List<List<CellFontStyle>> CellFontStyleList, boolean isCover) throws FileNotFoundException {
		if (null == fileDir) {
			throw new NullPointerException("文件生成目录不能为空！");
		}
		if (!SUFFIX_XLS.equals(excelSuffix) && !SUFFIX_XLSX.equals(excelSuffix)) {
			throw new IllegalArgumentException("文件类型错误！");
		}
		File dirFile = new File(fileDir);
		if (!dirFile.exists()) {
			throw new FileNotFoundException(fileDir + "路径不存在！");
		}
		fileName += excelSuffix;
		String filePath = fileDir + File.separator + fileName;
		if (!isCover) {
			File excelFile = new File(filePath);
			if (excelFile.exists()) {
				throw new RuntimeException(fileDir + "下的" + fileName + "文件已存在！");
			}
		}
		/* 创建HSSFWorkbook对象 */
		HSSFWorkbook wb = new HSSFWorkbook();
		if (null == dataMap || 0 == dataMap.size()) {
			return false;
		}
		/* 创建HSSFSheet对象 */
		HSSFSheet sheet = null;
		/* 创建HSSFRow对象 */
		HSSFRow row = null;
		/* 创建HSSFCell对象 */
		HSSFCell cell = null;
		List<List<Object>> dataList = null;
		int sheetIndex = 0;
		int rowIndex;
		int colIndex;
		HSSFFont fontStyle = null;
		for (Map.Entry<String, List<List<Object>>> entry : dataMap.entrySet()) {
			sheet = wb.createSheet(entry.getKey());
			List<CellRanger> cellRangerDataList = null;
			List<CellStyler> cellStylerDataList = null;
			List<CellFontStyle> cellFontStyleDataList = null;
			/* 设置CellRanger的样式 */
			if (null != cellRangerList && cellRangerList.size() > sheetIndex && (cellRangerDataList = cellRangerList.get(sheetIndex)) != null) {
				for (CellRanger cellRanger : cellRangerDataList) {
					int firstRow = cellRanger.getFirstRow();
					int lastRow = cellRanger.getLastRow();
					int firstCol = cellRanger.getFirstCol();
					int lastCol = cellRanger.getLastCol();
					if (firstRow < 0 || lastRow < 0 || firstCol < 0 || lastCol < 0 || firstRow > lastRow
							|| firstCol > lastCol) {
						continue;
					}
					sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
				}
			}
			/* 设置CellStyler样式 */
			if (null != cellStylerList && cellStylerList.size() > sheetIndex) {
				cellStylerDataList = cellStylerList.get(sheetIndex);
			}
			/* 设置CellFontStyler样式 */
			if (null != CellFontStyleList && CellFontStyleList.size() > sheetIndex) {
				cellFontStyleDataList = CellFontStyleList.get(sheetIndex);
			}
			sheetIndex++;
			dataList = entry.getValue();
			if (null == dataList || 0 == dataList.size()) {
				continue;
			}
			rowIndex = 0;
			for (List<Object> rowDataList : dataList) {
				row = sheet.createRow(rowIndex++);
				if (null == rowDataList || 0 == rowDataList.size()) {
					continue;
				}
				colIndex = 0;

				for (Object colData : rowDataList) {
					cell = row.createCell(colIndex++);
					if (null == colData) {
						continue;
					}
					/* 设置单元格的值 */
					cell.setCellValue(colData + "");
					HSSFCellStyle style = wb.createCellStyle();
					/*设置CellFontStyle*/
					if(null != cellFontStyleDataList && 0 != cellFontStyleDataList.size()) {
						CellFontStyle cellFontStyleHistory = null;
						for(CellFontStyle cellFontStyle : cellFontStyleDataList) {
							int fontTarget = cellFontStyle.getFontTarget();
							int rowTarget;
							int colTarget;
							int rowOrCol;
							if(FONT_TARGET_CELL == fontTarget) {
								rowTarget = cellFontStyle.getRow();
								colTarget = cellFontStyle.getCol();
								if(rowIndex == rowTarget && colIndex == colTarget) {
									fontStyle = wb.createFont();
									fontStyle.setColor(cellFontStyle.getFontColor());
									if (cellFontStyle.isHasUnderline()) {
										fontStyle.setUnderline(HSSFFont.U_SINGLE);
									}
									style.setFont(fontStyle);
									cell.setCellStyle(style);
									cellFontStyleHistory = cellFontStyle;
									break;
								}
							}else if(FONT_TARGET_ROW == fontTarget) {
								rowOrCol = cellFontStyle.getRowOrCol();
								if(rowIndex == rowOrCol) {
									fontStyle = wb.createFont();
									fontStyle.setColor(cellFontStyle.getFontColor());
									if (cellFontStyle.isHasUnderline()) {
										fontStyle.setUnderline(HSSFFont.U_SINGLE);
									}
									style.setFont(fontStyle);
									cell.setCellStyle(style);
									break;
								}
							}else if(FONT_TARGET_COL == fontTarget) {
								rowOrCol = cellFontStyle.getRowOrCol();
								if(colIndex == rowOrCol) {
									fontStyle = wb.createFont();
									fontStyle.setColor(cellFontStyle.getFontColor());
									if (cellFontStyle.isHasUnderline()) {
										fontStyle.setUnderline(HSSFFont.U_SINGLE);
									}
									style.setFont(fontStyle);
									cell.setCellStyle(style);
									break;
								}
							}
						}
						cellFontStyleDataList.remove(cellFontStyleHistory);
					}
					/*设置CellStyler*/
					if (null != cellStylerDataList && 0 != cellStylerList.size()) {
						CellStyler cellStylerHistory = null;
						for (CellStyler cellStyler : cellStylerDataList) {
							short cellForegroundColor = cellStyler.getCellForegroundColor();
							short cellBackgroundColor = cellStyler.getCellBackgroundColor();
							int styleRow = cellStyler.getRow();
							int styleCol = 0;
							boolean isCellStyle = cellStyler.getIsCellStyle();
							if (isCellStyle) {
								styleCol = cellStyler.getCol();
							}
							/* 设置CellStyle填充方案 */
							  if (isCellStyle && styleRow == rowIndex && styleCol == colIndex) {
								style.setFillPattern(HSSFCellStyle.DIAMONDS);
								style.setFillForegroundColor(cellForegroundColor);
								style.setFillBackgroundColor(cellBackgroundColor);
								cell.setCellStyle(style);
								cellStylerHistory = cellStyler;
								break;
							}else if (!isCellStyle && styleRow == rowIndex) {
								style.setFillPattern(HSSFCellStyle.DIAMONDS);
								style.setFillForegroundColor(cellForegroundColor);
								style.setFillBackgroundColor(cellBackgroundColor);
								/* 不加这一句的话，有数据的单元格会没有样式 */
								cell.setCellStyle(style);
							}
						}
						cellStylerDataList.remove(cellStylerHistory);
					}
				}
			}
		}
		FileOutputStream fos = null;
		try {
			/* 输出Excel文件 */
			fos = new FileOutputStream(filePath);
			wb.write(fos);
			fos.flush();
			return true;
		} catch (IOException e) {
			return false;
		} finally {
			try {
				if (null != fos)
					fos.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 
	 * 功能描述: Excel操纵类，可以根据Excel模板来生成Excel对象 版本信息：1.0
	 */
	public static class ExcelTemplate {
		private static final String DATAS = "datas";

		private HSSFWorkbook workbook;
		private HSSFSheet sheet;
		private HSSFRow currentRow;
		@SuppressWarnings("rawtypes")
		private Map styles = new HashMap(); // 数据行的默认样式配置
		@SuppressWarnings("rawtypes")
		private Map confStyles = new HashMap(); // 通过设置"#STYLE_XXX"来标识的样式配置
		private int initrow; // 数据输出起始行
		private int initcol; // 数据输出起始列
		private int num; // index number
		private int currentcol; // 当前列
		private int currentRowIndex; // 当前行index
		/* 在行高没有设置时的默认值 */
		private static final int ROWHEIGHT_DEFAULT = 30;
		/* 在列宽没有设置时的默认值 */
		private static final int ROWWIDTH_DEFAULT = 20;
		/* 列宽 */
		private static List<Integer> rowWidth = new ArrayList<>();
		private int lastLowNum = 0;
		private String cellStyle = null;

		private ExcelTemplate() {
		}

		/**
		 * 指定模板创建ExcelTemplate对象
		 * 
		 * @param templates
		 *            模板名称
		 * @param isRemoveConfigRow
		 *            是否删除配置行
		 * @return 根据模板已初始化完成的ExcelTemplate对象
		 */
		private static ExcelTemplate newInstance(String templates, boolean isRemoveConfigRow) {
			return doNewInstance(templates, isRemoveConfigRow);
		}

		/**
		 * 指定模板创建ExcelTemplate对象
		 * 
		 * @param templates
		 *            模板名称
		 * @param isRemoveConfigRow
		 *            是否删除配置行
		 * @param rowWidthConfig
		 *            行宽的配置参数集合
		 * @return 根据模板已初始化完成的ExcelTemplate对象
		 */
		private static ExcelTemplate newInstance(String templates, boolean isRemoveConfigRow,
				List<Integer> rowWidthConfig) {
			rowWidth = rowWidthConfig;
			return doNewInstance(templates, isRemoveConfigRow);
		}

		private static ExcelTemplate doNewInstance(String templates, boolean isRemoveConfigRow) {
			try {
				ExcelTemplate excel = new ExcelTemplate();
				POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(templates));
				excel.workbook = new HSSFWorkbook(fs);
				excel.sheet = excel.workbook.getSheetAt(0);
				/* 查找配置 */
				excel.initConfig();
				/* 查找其它样式配置 */
				excel.readCellStyles();
				if (isRemoveConfigRow) {
					/* 删除配置行 */
					excel.sheet.removeRow(excel.sheet.getRow(excel.initrow));
				}
				return excel;
			} catch (Exception e) {
				throw new RuntimeException("创建Excel对象出现异常");
			}
		}

		/**
		 * 设置特定的单元格样式，此样式可以通过在模板文件中定义"#STYLE_XX"来得到，如： #STYLE_1，传入的参数就是"STYLE_1"
		 * 
		 * @param style
		 */
		public void setCellStyle(String style) {
			cellStyle = style;
		}

		/**
		 * 取消特定的单元格格式，恢复默认的配置值，即DATAS所在行的值
		 */
		public void setCellDefaultStyle() {
			cellStyle = null;
		}

		/**
		 * 创建新行
		 * 
		 * @param index
		 *            从0开始计数
		 */
		private void createRow(int index) {
			/* 如果在当前插入数据的区域有后续行，则将其后面的行往后移动 */
			if (lastLowNum > initrow && index > 0) {
				sheet.shiftRows(index + initrow, lastLowNum + index, 1, true, true);
			}
			currentRow = sheet.createRow(index + initrow);
			/* HeightInPoints的单位是点，而Height的单位是1/20个点 */
			currentRow.setHeight((short) (ROWHEIGHT_DEFAULT * 20));
			currentRowIndex = index;
			currentcol = initcol;
		}

		/**
		 * 根据传入的字符串值，在当前行上创建新列
		 * 
		 * @param value
		 *            列的值（字符串）
		 */
		private void createCell(String value) {
			HSSFCell cell = createCell();
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(value);
		}

		/**
		 * 根据传入的日期值，在当前行上创建新列 在这种情况下（传入日期），你可以在模板中定义对应列 的日期格式，这样可以灵活通过模板来控制输出的日期格式
		 * 
		 * @param value
		 *            日期
		 */
		public void createCell(Date value) {
			HSSFCell cell = createCell();
			cell.setCellValue(value);
		}

		/**
		 * 创建当前行的序列号列，通常在一行的开头便会创建 注意要使用这个方法，你必需在创建行之前调用initPageNumber方法
		 */
		public void createSerialNumCell() {
			HSSFCell cell = createCell();
			cell.setCellValue(currentRowIndex + num);
		}

		@SuppressWarnings("deprecation")
		private HSSFCell createCell() {
			HSSFCell cell = currentRow.createCell((short) currentcol++);
			/* cell.setEncoding(HSSFCell.ENCODING_UTF_16); */
			cell.setCellType(HSSFCell.ENCODING_UTF_16);
			HSSFCellStyle style = (HSSFCellStyle) styles.get(new Integer(cell.getCellNum()));
			if (style != null) {
				cell.setCellStyle(style);
			}

			/* 设置了特定格式 */
			if (cellStyle != null) {
				HSSFCellStyle ts = (HSSFCellStyle) confStyles.get(cellStyle);
				if (ts != null) {
					cell.setCellStyle(ts);
				}
			}
			return cell;
		}

		/**
		 * 获取当前HSSFWorkbook的实例
		 * 
		 * @return
		 */
		public HSSFWorkbook getWorkbook() {
			return workbook;
		}

		/**
		 * 获取模板中定义的单元格样式，如果没有定义，则返回空
		 * 
		 * @param style
		 *            模板定义的样式名称
		 * @return 模板定义的单元格的样式，如果没有定义则返回空
		 */
		public HSSFCellStyle getTemplateStyle(String style) {
			return (HSSFCellStyle) confStyles.get(style);
		}

		/**
		 * 替换模板中的文本参数 参数以“#”开始
		 * 
		 * @param props
		 */
		@SuppressWarnings({ "rawtypes", "deprecation" })
		public void replaceParameters(Properties props) {
			if (props == null || props.size() == 0) {
				return;
			}
			Set propsets = props.entrySet();
			Iterator rowit = sheet.rowIterator();
			while (rowit.hasNext()) {
				HSSFRow row = (HSSFRow) rowit.next();
				if (row == null)
					continue;
				int cellLength = row.getLastCellNum();
				for (int i = 0; i < cellLength; i++) {
					HSSFCell cell = (HSSFCell) row.getCell((short) i);
					if (cell == null)
						continue;
					String value = cell.getStringCellValue();
					if (value != null && value.indexOf("#") != -1) {
						for (Iterator iter = propsets.iterator(); iter.hasNext();) {
							Map.Entry entry = (Map.Entry) iter.next();
							value = value.replaceAll("#" + entry.getKey(), (String) entry.getValue());
						}
					}
					cell.setCellType(HSSFCell.ENCODING_UTF_16);
					cell.setCellValue(value);
				}
			}
		}

		/**
		 * 初始化Excel配置
		 */
		@SuppressWarnings({ "rawtypes", "deprecation" })
		private void initConfig() {
			lastLowNum = sheet.getLastRowNum();
			Iterator rowit = sheet.rowIterator();
			boolean configFinish = false;
			/* 列数 */
			int columns = sheet.getRow(0).getPhysicalNumberOfCells();
			/* 设置列宽，第一个参数是列数，第二个参数是宽度，这个参数的单位是1/256个字符宽度 */
			if (rowWidth.size() <= 0) {
				for (int i = 0; i < columns; i++) {
					sheet.setColumnWidth(i, ROWWIDTH_DEFAULT * 256);
				}
			} else if (rowWidth.size() > 0 && rowWidth.size() <= columns) {
				int temp = 0;
				for (int i = 0; i < rowWidth.size(); i++, temp++) {
					sheet.setColumnWidth(i, rowWidth.get(i) * 256);
				}
				for (int i = temp; i < columns; i++) {
					sheet.setColumnWidth(i, rowWidth.get(i) * 256);
				}
			} else {
				for (int i = 0; i < rowWidth.size(); i++) {
					sheet.setColumnWidth(i, rowWidth.get(i) * 256);
				}
			}

			while (rowit.hasNext()) {
				if (configFinish) {
					break;
				}
				HSSFRow row = (HSSFRow) rowit.next();
				if (row == null)
					continue;
				int cellLength = row.getLastCellNum();
				for (int i = 0; i < cellLength; i++) {
					HSSFCell cell = (HSSFCell) row.getCell((short) i);
					if (cell == null)
						continue;
					String config = cell.getStringCellValue();
					if (DATAS.equalsIgnoreCase(config)) {
						/* 本行是数据开始行和样式配置行，需要读取相应的配置信息 */
						initrow = row.getRowNum();
						initcol = cell.getCellNum();
						configFinish = true;
					}
					if (configFinish) {
						readCellStyle(cell);
					}
				}
			}
		}

		/**
		 * 读取cell的样式
		 * 
		 * @param cell
		 */
		@SuppressWarnings({ "unchecked", "deprecation" })
		private void readCellStyle(HSSFCell cell) {
			HSSFCellStyle style = cell.getCellStyle();
			if (style == null)
				return;
			styles.put(new Integer(cell.getCellNum()), style);
		}

		/**
		 * 读取模板中其它单元格的样式配置
		 */
		@SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
		private void readCellStyles() {
			Iterator rowit = sheet.rowIterator();
			while (rowit.hasNext()) {
				HSSFRow row = (HSSFRow) rowit.next();
				if (row == null)
					continue;
				int cellLength = row.getLastCellNum();
				for (int i = 0; i < cellLength; i++) {
					HSSFCell cell = (HSSFCell) row.getCell((short) i);
					if (cell == null)
						continue;
					String value = cell.getStringCellValue();
					if (value != null && value.indexOf("#STYLE_") != -1) {
						HSSFCellStyle style = cell.getCellStyle();
						if (style == null)
							continue;
						confStyles.put(value.substring(1), style);
						row.removeCell(cell);
					}
				}
			}
		}

		private static boolean doWriteDataToExcelFile(ExcelTemplate template, String fileSavePath,
				List<List<Object>> listData) {
			for (int i = 0; i < listData.size(); i++) {
				/* 创建一行 */
				template.createRow(i + 1);
				for (int j = 0; j < listData.get(0).size(); j++) {
					/* 创建列 */
					template.createCell(listData.get(i).get(j).toString());
				}
			}
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(fileSavePath);
				template.getWorkbook().write(fos);
				return true;
			} catch (IOException e) {
				throw new RuntimeException("文件写入发生异常，请检查！");
			} finally {
				try {
					if (null != fos)
						fos.close();
				} catch (IOException e) {
				}
			}
		}

		/**
		 * 根据模板来将数据导出到excel文件
		 * 
		 * @param templatePath
		 *            模板文件路径，文件以.xls结尾
		 * @param fileSavePath
		 *            导出的文件路径，文件以.xls结尾
		 * @param listData
		 *            数据文件<列<行>>
		 * @param isRemoveConfigRow
		 *            是否删除表头
		 * @param rowWidth
		 *            对列宽的配置（元素为每一列宽的集合）允许为空
		 * @return 数据导出文件的执行是否成功
		 */
		public static boolean writeDataToExcelFile(String templatePath, String fileSavePath,
				List<List<Object>> listData, boolean isRemoveConfigRow, List<Integer> rowWidth) {
			if (null == templatePath || null == fileSavePath)
				return false;
			/* 获取模板样式,需自行创建 */
			ExcelTemplate template = null;
			if (rowWidth != null) {
				template = ExcelTemplate.newInstance(templatePath, isRemoveConfigRow, rowWidth);
			} else {
				template = ExcelTemplate.newInstance(templatePath, isRemoveConfigRow);
			}
			return doWriteDataToExcelFile(template, fileSavePath, listData);
		}
	}

	/**
	 * 读取一个excel表格内容（以.xls或者.xlsx结尾）
	 * 
	 * @param filePath
	 *            文件路径（不能为空）
	 * @param columnCount
	 *            表格字段数（可为空，默认为第一行的表头字段数量）
	 * @return 将每一行数据内容存为一个List集合， 然后将所有的集合按行数顺序存在一个JsonArray中，
	 *         第一个List为表头值数据，返回JsonArray格式的字符串
	 */
	public static String getDataFromExcel(String filePath, Integer columnCount) {
		if (null == filePath)
			return null;
		/* 判断是否为excel类型文件 */
		if (!filePath.endsWith(SUFFIX_XLS) && !filePath.endsWith(SUFFIX_XLSX)) {
			throw new IllegalArgumentException("文件不是以xls或xlsx结尾！");
		}
		FileInputStream fis = null;
		Workbook wookbook = null;
		try {
			fis = new FileInputStream(filePath);
			/* 2003版本的excel，用.xls结尾 */
			wookbook = new HSSFWorkbook(fis);// 得到工作簿
		} catch (Exception ex) {
			try {
				fis = new FileInputStream(filePath);
				/* 2007版本的excel，用.xlsx结尾 */
				wookbook = new XSSFWorkbook(fis);// 得到工作簿
			} catch (IOException e) {
				throw new IllegalArgumentException("文件类型错误！");
			}
		} finally {
			try {
				if (null != fis)
					fis.close();
			} catch (Exception e) {
			}
		}
		/* 得到一个工作表 */
		Sheet sheet = wookbook.getSheetAt(0);
		/* 获得表头 */
		Row rowHead = sheet.getRow(0);
		if (null == rowHead) {
			throw new RuntimeException("未找到表头数据，请检查！");
		}
		if (columnCount != null) {
			/* 判断表头是否正确 */
			if (rowHead.getPhysicalNumberOfCells() != columnCount) {
				throw new RuntimeException("表头的数量不对!");
			}
		} else {
			columnCount = rowHead.getPhysicalNumberOfCells();
		}

		/* 获得数据的总行数 */
		int totalRowNum = sheet.getLastRowNum();
		JSONArray ja_result = new JSONArray();
		List<String> list_row = null;
		/* 获得所有数据 */
		for (int i = 0; i <= totalRowNum; i++) {
			/* 获得第i行对象 */
			Row row = sheet.getRow(i);
			list_row = new ArrayList<>();
			Cell cell = null;
			/* 要获取的属性值 */
			String field = "";
			for (int j = 0; j < columnCount; j++) {
				/* 获得获得第i行第j列的 String类型对象 */
				cell = row.getCell((short) j);
				if (null != cell) {
					cell.setCellType(Cell.CELL_TYPE_STRING);
					field = cell.getStringCellValue().toString();
				} else {
					field = "";
				}
				list_row.add(field);
			}
			ja_result.add(i, list_row);
		}
		return JSONArray.toJSONString(ja_result);
	}
}
