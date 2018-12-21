package com.fy.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtil {
	
	private static InputStream is = null;
	private static InputStreamReader isr = null;
	private static OutputStream os = null;
	private static OutputStreamWriter osw = null;
	
	private static final String CHARSET_UTF8 = "utf-8";

	/**
	 * 
	 * @Description <p>通过配置文件中配置的key获取value</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年7月4日 下午2:52:27</p> 
	 * @author <p>fangyang</p>
	 *
	 * @param filePath 配置文件路径
	 * @param key 配置信息的键
	 * @return
	 */
	public static String getPropertiesValueByKey(String filePath, String key) {
		Properties pps = new Properties();
		try {
			File file = checkFilePath(filePath, false);
			is = new FileInputStream(file);
			isr = new InputStreamReader(is, CHARSET_UTF8);
			pps.load(isr);
			String value = pps.getProperty(key);
			return value;
		} catch (IOException e) {
			return null;
		} finally {
			closed();
		}
	}

	/**
	 * 
	 * @Description <p>将配置信息写入properties配置文件</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年7月4日 下午2:52:08</p> 
	 * @author <p>fangyang</p>
	 *
	 * @param filePath 配置文件路径
	 * @param data 写入数据信息的集合
	 */
	public static void writeDatasToProperties(String filePath, Map<String , String> data) {
		Properties pps = new Properties();
		try {
			File file = checkFilePath(filePath , true);
			is = new FileInputStream(file);
			// 从输入流中读取属性列表（键和元素对）
			isr = new InputStreamReader(is, CHARSET_UTF8);
			pps.load(isr);
			// 调用 Hashtable 的方法 put。使用 getProperty 方法提供并行性。
			// 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
			os = new FileOutputStream(filePath);
			osw = new OutputStreamWriter(os, CHARSET_UTF8);
			if(null == data) {
				throw new NullPointerException("写入数据不能为null");
			}
			String strChange = "";
			for(Map.Entry<String, String> entry : data.entrySet()) {
				pps.setProperty(entry.getKey() , entry.getValue());
				strChange += entry.getKey() + "/";
			}
			// 以适合使用 load 方法加载到 Properties 表中的格式，
			// 将此 Properties 表中的属性列表（键和元素对）写入输出流
			if(!"".equals(strChange.trim())) {
				pps.store(osw, "Update " + strChange.substring(0, strChange.length() - 1) + " data");
			}
		} catch (IOException e) {
			throw new RuntimeException("信息写入失败：" + e.getMessage());
		}  finally {
			closed();
		}
	}
	
	/**
	 * 
	 * @Description <p>将配置信息写入properties配置文件</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年7月4日 下午2:51:49</p> 
	 * @author <p>fangyang</p>
	 *
	 * @param filePath 配置文件路径
	 * @param key
	 * @param value
	 */
	public static void writeDataToProperties(String filePath, String key, String value) {
		Properties pps = new Properties();
		try {
			File file = checkFilePath(filePath , true);
			is = new FileInputStream(file);
			// 从输入流中读取属性列表（键和元素对）
			isr = new InputStreamReader(is, CHARSET_UTF8);
			pps.load(isr);
			// 调用 Hashtable 的方法 put。使用 getProperty 方法提供并行性。
			// 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
			os = new FileOutputStream(filePath);
			osw = new OutputStreamWriter(os, CHARSET_UTF8);
			pps.setProperty(key, value);
			// 以适合使用 load 方法加载到 Properties 表中的格式，
			// 将此 Properties 表中的属性列表（键和元素对）写入输出流
			pps.store(osw, "Update " + key + " data");
		} catch (IOException e) {
			throw new RuntimeException("信息写入失败：" + e.getMessage());
		}  finally {
			closed();
		}
	}
	
	private static void closed() {
		try {
			if (null != osw)
				osw.close();
			if (null != os)
				os.close();
			if (null != isr)
				isr.close();
			if (null != is)
				is.close();
		} catch (IOException e) {
			osw = null;
			os = null;
			isr = null;
			is = null;
		}
	}

	private static File checkFilePath(String filePath , boolean createInNotExists) throws IOException {
		File file = new File(filePath);
		if(!file.exists()) {
			if(createInNotExists) {
				if(!file.createNewFile()) {
					throw new RuntimeException(filePath + "新建文件失败！");
				}
			}
		}else {
			if(!file.isFile()) {
				throw new RuntimeException(filePath + "不是一个文件！");
			}
		}
		return file;
	}

	/**
	 * 打印系统的JVM配置信息
	 */
	public static void printSystemProperties() {
		Properties pps = System.getProperties();
		pps.list(System.out);
	}

	/**
	 * 
	 * @Description <p>通过配置文件的地址获取配置文件信息</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年7月4日 下午2:51:13</p> 
	 * @author <p>fangyang</p>
	 *
	 * @param filePath 配置文件的地址
	 * @return
	 */
	public static Map<String, String> getDataFromProperties(String filePath) {
		Properties pps = new Properties();
		Map<String, String> data;
		try {
			File file = checkFilePath(filePath , false);
			is = new FileInputStream(file);
			isr = new InputStreamReader(is , CHARSET_UTF8);
			pps.load(isr);
			// 得到配置文件的名字
			Enumeration<?> enumeration = pps.propertyNames();
			data = new HashMap<>();
			while (enumeration.hasMoreElements()) {
				String strKey = (String) enumeration.nextElement();
				String strValue = pps.getProperty(strKey);
				data.put(strKey, strValue);
			}
		} catch (Exception e) {
			return null;
		} finally {
			closed();
		}
		return data;
	}
}
