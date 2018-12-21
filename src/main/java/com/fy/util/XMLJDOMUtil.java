package com.fy.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class XMLJDOMUtil {

	public static final String XML_SUFFIX = ".xml";
	private static String left = "<";
	private static String leftSeal = "</";
	private static String right = ">";

	/**
	 * 编写新的XMLDoc时是否实行换行
	 */
	private static boolean makeXmlIsChangeLine = false;

	public static boolean isMakeXmlIsChangeLine() {
		return makeXmlIsChangeLine;
	}

	public static void setMakeXmlIsChangeLine(boolean makeXmlIsChangeLine) {
		XMLJDOMUtil.makeXmlIsChangeLine = makeXmlIsChangeLine;
	}

	/**
	 * 
	 * @Description XMLDoc文件节点实体类
	 * @version v1.0
	 * @Date 2018年6月9日 下午2:14:26
	 *
	 * @author fangyang
	 */
	public static class XMLDocNode {
		private String nodeName;
		private StringBuffer sb;
		private Map<String, Object> attrs = new HashMap<>();
		private String changeLine = makeXmlIsChangeLine ? "\r\n" : "";
		private String attrSplit = " ";

		public String getAttrSplit() {
			return attrSplit;
		}

		public void setAttrSplit(String attrSplit) {
			this.attrSplit = attrSplit;
		}

		public String getNodeName() {
			return nodeName;
		}

		public String getXmlData() {
			return sb.toString() + leftSeal + nodeName + right + changeLine;
		}

		public XMLDocNode(String nodeName) {
			this.nodeName = nodeName;
			sb = new StringBuffer(left + nodeName + right + changeLine);
		}

		public XMLDocNode(String nodeName, String nodeText) {
			this.nodeName = nodeName;
			sb = new StringBuffer(left + nodeName + right + changeLine + nodeText + changeLine);
		}

		public XMLDocNode append(XMLDocNode childNode) {
			sb.append(childNode.getXmlData());
			return this;
		}

		public XMLDocNode append(String text) {
			sb.append(text + changeLine);
			return this;
		}

		/**
		 * 给该节点添加属性
		 * 
		 * @param attrData
		 *            存放属性信息的键值对
		 * @return
		 */
		public XMLDocNode setAttribute(Map<String, Object> attrData) {
			if (null != attrData && 0 < attrData.size()) {
				attrs.putAll(attrData);
				String temp = sb.toString();
				StringBuffer tempStart = new StringBuffer(temp.substring(0, 1 + nodeName.length()));
				String tempEnd = temp.substring(1 + nodeName.length());
				for (Map.Entry<String, Object> entry : attrData.entrySet()) {
					tempStart.append(attrSplit + entry.getKey() + "=\"" + entry.getValue() + "\"");
				}
				tempStart.append(tempEnd);
				sb = tempStart;
			}
			return this;
		}

		public Object getAttribute(String attrName) {
			Object attrVal = null;
			if (null != attrName && 0 < attrs.size()) {
				for (Map.Entry<String, Object> entry : attrs.entrySet()) {
					if (attrName.equals(entry.getKey())) {
						attrVal = entry.getValue();
						break;
					}
				}
			}
			return attrVal;
		}

		public Map<String, Object> getAttributes() {
			return this.attrs;
		}
	}

	/**
	 * 将XMLDoc内容写入xml文件
	 * 
	 * @param filePath
	 *            文件所在绝对路径
	 * @param xmlDoc
	 *            要写入的XMLDoc
	 * @param append
	 *            是否追加（true追加，false覆盖）
	 * @return 是否写入成功
	 * @throws FileNotFoundException
	 *             文件所指向目录未找到
	 */
	public static boolean writeXMLDocToXMLFile(String filePath, XMLDocNode xmlDoc, boolean append)
			throws FileNotFoundException {
		File file = new File(filePath);
		BufferedWriter bw = null;
		OutputStreamWriter osw = null;
		FileOutputStream fos = null;
		File dirFile = file.getParentFile();
		if (null == filePath || "".equals(filePath.trim())) {
			throw new IllegalArgumentException("文件路径不能为空！");
		}
		if (!filePath.endsWith(XML_SUFFIX)) {
			throw new IllegalArgumentException("文件类型错误！");
		}
		if (!dirFile.exists()) {
			throw new FileNotFoundException(dirFile.getAbsolutePath() + "路径未找到！");
		}
		if (!file.exists()) {
			try {
				if (!file.createNewFile()) {
					throw new RuntimeException(filePath + "文件不存在且创建失败！");
				}
			} catch (IOException e) {
				throw new RuntimeException(filePath + "文件不存在且创建异常！");
			}
		}
		if (!file.isFile()) {
			throw new IllegalArgumentException(filePath + "不是文件！");
		}
		try {
			fos = new FileOutputStream(file, append);
			osw = new OutputStreamWriter(fos);
			bw = new BufferedWriter(osw);
			String content = (xmlDoc != null) ? xmlDoc.getXmlData() : "";
			if (append && 0 < file.length()) {
				content = "\r\n" + content;
			}
			bw.write(content);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (osw != null)
					osw.close();
				if (fos != null)
					fos.close();
			} catch (Exception e2) {
			}
		}
	}

	/**
	 * 将自定义内容写入xml文件
	 * 
	 * @param filePath
	 *            文件所在绝对路径
	 * @param content
	 *            要写入的内容
	 * @param append
	 *            是否追加（true追加，false覆盖）
	 * @return 是否写入成功
	 * @throws FileNotFoundException
	 *             文件所指向目录未找到
	 */
	public static boolean writeTextToXMLFile(String filePath, String content, boolean append)
			throws FileNotFoundException {
		File file = new File(filePath);
		BufferedWriter bw = null;
		OutputStreamWriter osw = null;
		FileOutputStream fos = null;
		File dirFile = file.getParentFile();
		if (null == filePath || "".equals(filePath.trim())) {
			throw new IllegalArgumentException("文件路径不能为空！");
		}
		if (!filePath.endsWith(XML_SUFFIX)) {
			throw new IllegalArgumentException("文件类型错误！");
		}
		if (!dirFile.exists()) {
			throw new FileNotFoundException(dirFile.getAbsolutePath() + "路径未找到！");
		}
		if (!file.exists()) {
			try {
				if (!file.createNewFile()) {
					throw new RuntimeException(filePath + "文件不存在且创建失败！");
				}
			} catch (IOException e) {
				throw new RuntimeException(filePath + "文件不存在且创建异常！");
			}
		}
		if (!file.isFile()) {
			throw new IllegalArgumentException(filePath + "不是文件！");
		}
		try {
			fos = new FileOutputStream(file, append);
			osw = new OutputStreamWriter(fos);
			bw = new BufferedWriter(osw);
			content = (null != content) ? content : "";
			if (append && 0 < file.length()) {
				content = "\r\n" + content;
			}
			bw.write(content);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (osw != null)
					osw.close();
				if (fos != null)
					fos.close();
			} catch (Exception e2) {
			}
		}
	}

	/**
	 * 读取XML文件（注意：读取的时候不会读取XML文件头信息）
	 * 
	 * @param filePath
	 *            文件路径
	 * @param isLog
	 *            是否打印读取过程
	 * @return 读取到的文件内容
	 * @throws FileNotFoundException
	 *             该文件不存在
	 */
	public static String readXMLFile(String filePath, boolean isLog) throws FileNotFoundException {
		SAXBuilder saxBuilder = new SAXBuilder();
		FileInputStream fis = null;
		InputStreamReader isr = null;
		if (null == filePath || "".equals(filePath.trim())) {
			throw new IllegalArgumentException("文件路径不能为空！");
		}
		if (!filePath.endsWith(XML_SUFFIX)) {
			throw new IllegalArgumentException("文件类型错误！");
		}
		File file = new File(filePath);
		if (!file.exists() || file.isDirectory()) {
			throw new FileNotFoundException(filePath + "文件不存在！");
		}
		String data = null;
		try {
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis, "utf-8");
			Document document = null;
			try {
				document = saxBuilder.build(isr);
			} catch (Exception e) {
				throw new RuntimeException("XML文档结构错误！", e);
			}
			if (null != document) {
				data = doReadXMLFile(document, null, isLog);
			}
		} catch (IOException e) {
			throw new RuntimeException(filePath + "文件读取失败！");
		} finally {
			try {
				if (null != isr)
					isr.close();
				if (null != fis)
					fis.close();
			} catch (IOException e) {
			}
		}
		return data;
	}

	private static String doReadXMLFile(Document document, Element rootElement, boolean isLog) {
		boolean isRoot = false;
		StringBuffer sb = new StringBuffer("");
		if (null == rootElement) {
			rootElement = document.getRootElement();
			isRoot = true;
			sb.append("<" + rootElement.getName() + ">");
			if (isLog) {
				System.out.println("<" + rootElement.getName() + ">");
			}
		}
		List<Element> elementList = rootElement.getChildren();
		if (null == elementList)
			return "";
		for (Element element : elementList) {
			sb.append("<" + element.getName() + ">");
			if (isLog) {
				System.out.println("<" + element.getName() + ">");
			}
			if (0 < element.getChildren().size()) {
				sb.append(doReadXMLFile(document, element, isLog));
			} else {
				sb.append(element.getTextTrim());
				if (isLog) {
					System.out.println(element.getTextTrim());
				}
			}
			sb.append(leftSeal + element.getName() + right);
			if (isLog) {
				System.out.println(leftSeal + element.getName() + right);
			}
		}
		if (isRoot) {
			sb.append(leftSeal + rootElement.getName() + right);
			if (isLog) {
				System.out.println(leftSeal + rootElement.getName() + right);
			}
		}
		return sb.toString();
	}

	/**
	 * 读取XML文件里指定节点的节点内容
	 * 
	 * @param filePath
	 *            XML文件路径
	 * @param nodeName
	 *            指定节点名
	 * @return 节点内容的集合
	 * @throws FileNotFoundException
	 *             文件不存在
	 */
	public static List<Object> getNodeTextByName(String filePath, String nodeName) throws FileNotFoundException {
		SAXBuilder saxBuilder = new SAXBuilder();
		FileInputStream fis = null;
		InputStreamReader isr = null;
		if (null == filePath || "".equals(filePath.trim())) {
			throw new IllegalArgumentException("文件路径不能为空！");
		}
		if (!filePath.endsWith(XML_SUFFIX)) {
			throw new IllegalArgumentException("文件类型错误！");
		}
		File file = new File(filePath);
		if (!file.exists() || file.isDirectory()) {
			throw new FileNotFoundException(filePath + "文件不存在！");
		}
		List<Object> data = null;
		try {
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis, "utf-8");
			Document document = null;
			try {
				document = saxBuilder.build(isr);
			} catch (Exception e) {
				throw new RuntimeException("XML文档结构错误！", e);
			}
			if (null != document) {
				data = doGetNodeTextByName(document, null, nodeName);
			}
		} catch (IOException e) {
			throw new RuntimeException(filePath + "文件读取失败！");
		} finally {
			try {
				if (null != isr)
					isr.close();
				if (null != fis)
					fis.close();
			} catch (IOException e) {
			}
		}
		return data;
	}

	private static List<Object> doGetNodeTextByName(Document document, Element rootElement, String nodeName) {
		List<Object> objList = new ArrayList<>();
		if (null == rootElement) {
			rootElement = document.getRootElement();
			if (nodeName.equals(rootElement.getName())) {
				objList.add(rootElement.getTextTrim());
			}
		}
		List<Element> elementList = rootElement.getChildren();
		if (null == elementList)
			return null;
		for (Element element : elementList) {
			if (nodeName.equals(element.getName())) {
				objList.add(element.getTextTrim());
			}
			if (0 < element.getChildren().size()) {
				List<Object> childObjList = doGetNodeTextByName(document, element, nodeName);
				if (null != childObjList) {
					objList.addAll(childObjList);
				}
			}
		}
		return objList;
	}

	/**
	 * 读取XML文件里指定节点的指定属性的所有属性值
	 * 
	 * @param filePath
	 *            XML文件路径
	 * @param nodeName
	 *            指定节点名
	 * @param attrName
	 *            指定属性名
	 * @return 所有该节点的所有该属性的属性值的集合
	 * @throws FileNotFoundException
	 *             文件不存在
	 */
	public static List<Object> getNodeAttrByNodeName(String filePath, String nodeName, String attrName)
			throws FileNotFoundException {
		List<Object> data = null;
		List<Map<String, Object>> attrData = getNodeAttrByNodeName(filePath, nodeName);
		if (null != attrData && 0 < attrData.size()) {
			data = new ArrayList<>();
			for (Map<String, Object> map : attrData) {
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					if (entry.getKey().equals(attrName)) {
						data.add(entry.getValue());
					}
				}
			}
		}
		return data;
	}

	/**
	 * 读取XML文件里指定节点的所有属性值
	 * 
	 * @param filePath
	 *            filePath
	 * @param nodeName
	 *            指定节点名
	 * @return 读取到一个指定节点，将其内属性名和属性值以键值对存于Map，将所有的Map存于List集合返回
	 * @throws FileNotFoundException
	 *             文件不存在
	 */
	public static List<Map<String, Object>> getNodeAttrByNodeName(String filePath, String nodeName)
			throws FileNotFoundException {
		SAXBuilder saxBuilder = new SAXBuilder();
		FileInputStream fis = null;
		InputStreamReader isr = null;
		if (null == filePath || "".equals(filePath.trim())) {
			throw new IllegalArgumentException("文件路径不能为空！");
		}
		if (!filePath.endsWith(XML_SUFFIX)) {
			throw new IllegalArgumentException("文件类型错误！");
		}
		File file = new File(filePath);
		if (!file.exists() || file.isDirectory()) {
			throw new FileNotFoundException(filePath + "文件不存在！");
		}
		List<Map<String, Object>> data = null;
		try {
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis, "utf-8");
			Document document = null;
			try {
				document = saxBuilder.build(isr);
			} catch (Exception e) {
				throw new RuntimeException("XML文档结构错误！", e);
			}
			if (null != document) {
				data = doGetNodeAttrByNodeName(document, null, nodeName);
			}
		} catch (IOException e) {
			throw new RuntimeException(filePath + "文件读取失败！");
		} finally {
			try {
				if (null != isr)
					isr.close();
				if (null != fis)
					fis.close();
			} catch (IOException e) {
			}
		}
		return data;
	}

	private static List<Map<String, Object>> doGetNodeAttrByNodeName(Document document, Element rootElement,
			String nodeName) {
		List<Map<String, Object>> data = new ArrayList<>();
		Map<String, Object> tempData = null;
		if (null == rootElement) {
			rootElement = document.getRootElement();
			if (nodeName.equals(rootElement.getName())) {
				tempData = new HashMap<>();
				for (Attribute attribute : rootElement.getAttributes()) {
					tempData.put(attribute.getName(), attribute.getValue());
				}
				if (tempData.size() > 0)
					data.add(tempData);
			}
		}
		List<Element> elementList = rootElement.getChildren();
		if (null == elementList)
			return null;
		for (Element element : elementList) {
			if (nodeName.equals(element.getName())) {
				tempData = new HashMap<>();
				for (Attribute attribute : element.getAttributes()) {
					tempData.put(attribute.getName(), attribute.getValue());
				}
				if (tempData.size() > 0)
					data.add(tempData);
			}
			if (0 < element.getChildren().size()) {
				List<Map<String, Object>> childData = doGetNodeAttrByNodeName(document, element, nodeName);
				if (null != childData) {
					data.addAll(childData);
				}
			}
		}
		return data;
	}

}
