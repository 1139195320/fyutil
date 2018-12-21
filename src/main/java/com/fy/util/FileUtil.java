package com.fy.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;

public class FileUtil {
	
	private static final String CHARSET_UTF8 = "UTF-8";
	private static final String FILE_SEPARATOR = java.io.File.separator;

	public static final Integer SIZE_TYPE_KB = 0x01;
	public static final Integer SIZE_TYPE_MB = 0x02;
	public static final Integer SIZE_TYPE_GB = 0x03;
	public static final Integer SIZE_TYPE_TB = 0x04;
	
	private static FileInputStream fis = null;
	private static InputStreamReader isr = null;
	private static BufferedReader br = null;
	private static FileOutputStream fos = null;
	private static OutputStreamWriter osw = null;
	private static BufferedWriter bw = null;
	
	/**
	 * 
	 * @Description <p>获取文件或文件夹大小</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年7月4日 上午11:25:48</p> 
	 * @author <p>fangyang</p>
	 *
	 * @param path 文件或文件夹路径（绝对路径）
	 * @param sizeCompany
	 * @return
	 */
	public static Double getFileSize(String path, Integer sizeCompany) {
		if(null == path || "".equals(path.trim())
				|| null == sizeCompany) {
			throw new IllegalArgumentException("参数有误！");
		}
		File file = new File(path);
		if (!file.exists()) {
			throw new RuntimeException(path + "路径文件或文件夹不存在！");
		}
		Double size = doGetFileSize(path);

		if (SIZE_TYPE_KB == sizeCompany) {
			return Double.valueOf(new DecimalFormat("0.0").format(size / 1024));
		} else if (SIZE_TYPE_MB == sizeCompany) {
			return Double.valueOf(new DecimalFormat("0.0").format(size / 1024 / 1024));
		} else if (SIZE_TYPE_GB == sizeCompany) {
			return Double.valueOf(new DecimalFormat("0.0").format(size / 1024 / 1024 / 1024));
		} else if (SIZE_TYPE_TB == sizeCompany) {
			return Double.valueOf(new DecimalFormat("0.0").format(size / 1024 / 1024 / 1024 / 1024));
		} else {
			return Double.valueOf(new DecimalFormat("0.0").format(size));
		}
	}

	private static Double doGetFileSize(String path) {
		File file = new File(path);
		Double size = 0.0;
		if (file.isFile()) {
			return (double) file.length();
		}
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files.length == 0) {
				return 0.0;
			}
			for (File thisFile : files) {
				if (thisFile.isFile()) {
					size += thisFile.length();
				} else {
					size += doGetFileSize(thisFile.getAbsolutePath());
				}
			}
		}
		return size;
	}
	
	/**
	 * 
	 * @Description <p>从指定文件夹复制文件或目录至另一个指定文件夹</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年7月4日 下午2:37:04</p> 
	 * @author <p>fangyang</p>
	 *
	 * @param fromDir 指定源文件夹（绝对路径）
	 * @param toDir 另一个指定文件夹（绝对路径）
	 * @param oldFileName 待复制文件（或目录）的原来的文件名
	 * @param newFileName 待复制文件（或目录）的新的文件名
	 * @param isLog 是否打印复制过程日志
	 * @return
	 * @throws FileNotFoundException 源文件（或目录）路径不存在
	 */
	public static boolean copyFile(String fromDir, String toDir, String oldFileName, String newFileName , boolean isLog) throws FileNotFoundException {
		if(null == fromDir || "".equals(fromDir.trim())
				|| null == toDir || "".equals(toDir.trim())
				|| null == oldFileName || "".equals(oldFileName.trim())
				|| null == newFileName || "".equals(newFileName.trim())) {
			throw new IllegalArgumentException("参数有误！");
		}
		String oldFilePath;
		if (fromDir.endsWith(FILE_SEPARATOR)) {
			oldFilePath = fromDir + oldFileName;
		} else {
			oldFilePath = fromDir + FILE_SEPARATOR + oldFileName;
		}
		if (!toDir.endsWith(FILE_SEPARATOR)) {
			toDir = toDir + FILE_SEPARATOR;
		}
		File oldFile = new File(oldFilePath);
		if(!oldFile.exists()) {
			throw new FileNotFoundException("源文件（或目录）" + oldFilePath + "路径不存在！");
		}
		String newFilePath = toDir + newFileName;
		File newFile = new File(newFilePath);
		if (newFile.exists()) {
			throw new RuntimeException("目标文件路径" + newFilePath + "文件或文件夹已存在！");
		}
		return doCopyFile(oldFilePath , newFilePath , isLog);
	}
	
	private static boolean doCopyFile(String oldFilePath , String newFilePath , boolean isLog) {
		File oldFile = new File(oldFilePath);
		File newFile = new File(newFilePath);
		try {
			if(oldFile.isDirectory()) {
				if(!newFile.mkdirs()) {
					if(isLog) {
						System.out.println(newFilePath + "创建失败！");
					}
				}else {
					if(isLog) {
						System.out.println("now copy " + newFilePath + " ...");
					}
					File[] files = oldFile.listFiles();
					if(null != files && 0 < files.length) {
						if (!oldFilePath.endsWith(FILE_SEPARATOR)) {
							oldFilePath += FILE_SEPARATOR;
						}
						if (!newFilePath.endsWith(FILE_SEPARATOR)) {
							newFilePath += FILE_SEPARATOR;
						}
						for(File f : files) {
							doCopyFile(oldFilePath + f.getName() , newFilePath + f.getName() , isLog);
						}
					}
				}
			} else {
				if(isLog) {
					System.out.println("now copy " + newFilePath + " ...");
				}
				String content = "";
				fis = new FileInputStream(oldFile);
				isr = new InputStreamReader(fis, CHARSET_UTF8);
				br = new BufferedReader(isr);
				int c = 0;
				while ((c = br.read()) != -1) {
					content += (char) c;
				}
				 /*开始复制*/
				if (newFile.exists()) {
					if(isLog) {
						System.out.println(newFilePath + "已存在，复制失败！");
					}
				}else {
					newFile.createNewFile();
					fos = new FileOutputStream(newFile);
					osw = new OutputStreamWriter(fos , CHARSET_UTF8);
					osw.write(content);
				}
			}
			return true;
		} catch (IOException e) {
			throw new RuntimeException("复制失败！" + e.getMessage());
		} finally {
			closed();
		}
	}

	/**
	 * 
	 * @Description <p>删除文件夹（用了递归完整删除文件夹及里面的内容）</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年7月4日 上午11:16:41</p> 
	 * @author <p>fangyang</p>
	 *
	 * @param dir 文件夹路径（绝对路径）
	 * @param dirName 文件夹名
	 * @param isLogDelete 是否打印删除的日志
	 */
	public static void deleteDir(String dir, String dirName, boolean isLogDelete) {
		if(null == dir || "".equals(dir.trim())
				|| null == dirName || "".equals(dirName.trim())) {
			throw new IllegalArgumentException("参数有误！");
		}
		String dirPath = dir + dirName;
		File file = new File(dirPath);
		if (file.exists() && file.isDirectory()) {
			 /*删除子文件及子目录*/
			File files[] = file.listFiles();
			for (File f : files) {
				if (f.isFile() && f.delete()) {
					if (isLogDelete)
						System.out.println("文件" + f.getName() + "删除成功！");
					continue;
				}
				if (f.isDirectory()) {
					deleteDir(dirPath + FILE_SEPARATOR, f.getName(), isLogDelete);
				}
			}
			if (file.delete()) {
				if (isLogDelete)
					System.out.println(dirPath + "文件夹删除成功！");
			} else {
				if (isLogDelete)
					System.out.println(dirPath + "文件夹删除失败！");
			}
		} else {
			if (isLogDelete)
				System.out.println(dir + "下的" + dirName + "文件夹不存在！");
		}
	}
	
	/**
	 * 
	 * @Description <p>删除文件</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年7月4日 上午11:14:47</p> 
	 * @author <p>fangyang</p>
	 *
	 * @param dir 文件路径（绝对路径）
	 * @param fileName 文件名
	 * @return
	 */
	public static boolean deleteFile(String dir, String fileName) {
		if(null == dir || "".equals(dir.trim())
				|| null == fileName || "".equals(fileName.trim())) {
			throw new IllegalArgumentException("参数有误！");
		}
		String filePath = dir + fileName;
		File file = new File(filePath);
		if (file.exists() && file.isFile()) {
			try {
				if (file.delete()) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				throw new RuntimeException("文件删除失败！" + e.getMessage());
			}
		} else {
			throw new RuntimeException(dir + "下的" + fileName + "文件不存在！");
		}
	}
	
	/**
	 * 
	 * @Description <p>向文件写入内容</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年7月4日 上午11:12:43</p> 
	 * @author <p>fangyang</p>
	 *
	 * @param dir 文件绝对路径所在目录
	 * @param fileName 文件名
	 * @param writeContent 要写入的内容
	 * @param append 是否追加（true追加，false覆盖）
	 * @return
	 * @throws FileNotFoundException 目标文件未找到
	 */
	public static boolean writeFile(String dir, String fileName, String writeContent, boolean append)
			throws FileNotFoundException {
		if(null == dir || "".equals(dir.trim())
				|| null == fileName || "".equals(fileName.trim())
				|| null == writeContent) {
			throw new IllegalArgumentException("参数有误！");
		}
		String filePath = dir + fileName;
		File file = new File(filePath);
		if(file.exists()) {
			if(!file.isFile()) {
				throw new RuntimeException(filePath + "路径不是一个文件！");
			}
		}else {
			throw new FileNotFoundException(filePath + "路径未找到！");
		}
		try {
			fos = new FileOutputStream(file, append);
			osw = new OutputStreamWriter(fos);
			bw = new BufferedWriter(osw);
			if (append && 0 < file.length()) {
				writeContent = "\r\n" + writeContent;
			}
			try {
				bw.write(writeContent);
				return true;
			} catch (NumberFormatException e) {
				throw new RuntimeException("写入内容有误！" + e.getMessage());
			} catch (IOException e) {
				throw new RuntimeException("写入失败！" + e.getMessage());
			} finally {
				closed();
			}
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException(fileName + "文件未找到！");
		}
	}

	/**
	 * 
	 * @Description <p>创建目录</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年7月4日 上午11:04:02</p> 
	 * @author <p>fangyang</p>
	 *
	 * @param dir 文件夹绝对路径所在父目录
	 * @param dirName 文件夹名
	 * @return
	 */
	public static boolean createDir(String dir, String dirName) {
		if(null == dir || "".equals(dir.trim())
				|| null == dirName || "".equals(dirName.trim())) {
			throw new IllegalArgumentException("参数有误！");
		}
		String dirPath = dir + dirName;
		File file = new File(dirPath);
		if (file.exists()) {
			throw new RuntimeException(dir + "下的" + dirName + "已有重名文件或文件夹存在！");
		}
		try {
			if (file.getAbsoluteFile().mkdirs()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw new RuntimeException("目录创建失败！" + e.getMessage());
		}
	}
	
	/**
	 * 
	 * @Description <p>创建单个文件</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年7月4日 上午11:02:59</p> 
	 * @author <p>fangyang</p>
	 *
	 * @param dir 文件绝对路径目录
	 * @param fileName 文件名
	 * @return
	 * @throws FileNotFoundException 目标目录未找到
	 */
	public static boolean createFile(String dir, String fileName) throws FileNotFoundException {
		if(null == dir || "".equals(dir.trim())
				|| null == fileName || "".equals(fileName.trim())) {
			throw new IllegalArgumentException("参数有误！");
		}
		File dirFile = new File(dir);
		if (!dirFile.exists()) {
			throw new FileNotFoundException("目标目录" + dir + "不存在！");
		}
		if (!dir.endsWith(FILE_SEPARATOR))
			dir += FILE_SEPARATOR;
		String filePath = dir + fileName;
		File file = new File(filePath);
		if (file.exists()) {
			throw new RuntimeException(dir + "下的" + fileName + "已有重名文件或文件夹存在！");
		} else if (filePath.endsWith(FILE_SEPARATOR)) {
			throw new RuntimeException("目标文件不能为目录！");
		}
		try {
			if (file.createNewFile()) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			throw new RuntimeException("文件创建失败！" + e.getMessage());
		}
	}
	
	/**
	 * 
	 * @Description <p>（无中文乱码） 读取文件内容（BufferedReader缓冲字符流）之 read</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年7月4日 上午11:00:13</p> 
	 * @author <p>fangyang</p>
	 *
	 * @param filePath 文件绝对路径
	 * @return 读取到的内容
	 * @throws FileNotFoundException 路径未找到
	 */
	public static String readFile1(String filePath) throws FileNotFoundException {
		if(null == filePath || "".equals(filePath.trim())){
			throw new IllegalArgumentException("参数有误！");
		}
		File file = new File(filePath);
		String result = "";
		if(file.exists()) {
			if(!file.isFile()) {
				throw new RuntimeException(filePath + "路径不是一个文件！");
			}
		}else {
			throw new FileNotFoundException(filePath + "路径未找到！");
		}
		try {
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis, CHARSET_UTF8);
			br = new BufferedReader(isr);
			int chr;
			while ((chr = br.read()) != -1) {
				result += (char) chr;
			}
		} catch (IOException e) {
			throw new RuntimeException("读取失败：" + e.getMessage());
		} finally {
			closed();
		}
		return result;
	}
	
	/**
	 * 
	 * @Description <p>（无中文乱码） 读取文件内容（BufferedReader缓冲字符流）之 readLine</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年7月4日 上午10:59:19</p> 
	 * @author <p>fangyang</p>
	 *
	 * @param filePath 文件绝对路径
	 * @return 读取到的内容
	 * @throws FileNotFoundException 路径未找到
	 */
	public static String readFile2(String filePath) throws FileNotFoundException {
		if(null == filePath || "".equals(filePath.trim())){
			throw new IllegalArgumentException("参数有误！");
		}
		File file = new File(filePath);
		if(file.exists()) {
			if(!file.isFile()) {
				throw new RuntimeException(filePath + "路径不是一个文件！");
			}
		}else {
			throw new FileNotFoundException(filePath + "路径未找到！");
		}
		String result = "";
		try {
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis, CHARSET_UTF8);
			br = new BufferedReader(isr);
			String line = "";
			while ((line = br.readLine()) != null) {
				 /*默认每次取一行，没有换行加上*/
				result += line + "\n";
			}
		} catch (IOException e) {
			throw new RuntimeException("读取失败：" + e.getMessage());
		} finally {
			closed();
		}
		return result;
	}

	private static void closed() {
		try {
			if(bw != null)
				bw.close();
			if (osw != null)
				osw.close();
			if (fos != null)
				fos.close();
			if (br != null)
				br.close();
			if (isr != null)
				isr.close();
			if (fis != null)
				fis.close();
		} catch (IOException e) {
			bw = null;
			osw = null;
			fos = null;
			br = null;
			isr = null;
			fis = null;
		}
	}
}
