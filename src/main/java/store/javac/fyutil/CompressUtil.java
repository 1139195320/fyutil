package store.javac.fyutil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.tools.tar.TarOutputStream;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

/**
 * 
 * @Description <p>压缩和解压缩的工具包</p>
 * @version <p>v1.0</p>
 * @Date <p>2018年7月4日 下午2:57:09</p> 
 * @author <p>jack</p>
 *
 */
public class CompressUtil {

	private static final String ZIP_SUFFIX = ".zip";
	private static final String TAR_GZ_SUFFIX = ".tar.gz";
	private static final int BYTE_1024 = 1024;
	public static final String CHARSET_UTF8 = "UTF-8";
	private static final String FILE_PATH_SEPARATOR = java.io.File.separator;

	/**
	 * zip解压文件
	 * 
	 * @param targetFile
	 *            zip目标压缩源文件
	 * @param unzipFilePath
	 *            解压后的目标文件夹
	 * @param isLog
	 *            是否打印压缩日志
	 * @return 是否解压成功
	 */
	public static boolean unZip(String targetFilePath, String unzipFilePath, boolean isLog) {
		long start = System.currentTimeMillis();
		if (null == targetFilePath || "".equals(targetFilePath.trim())) {
			throw new IllegalArgumentException("zip目标压缩源文件不能为空！");
		}
		if (null == unzipFilePath || "".equals(unzipFilePath.trim())) {
			throw new IllegalArgumentException("解压后的目标文件夹" + unzipFilePath + "不能为空！");
		}
		if (!targetFilePath.endsWith(ZIP_SUFFIX)) {
			throw new IllegalArgumentException(targetFilePath + "文件类型错误！");
		}
		File targetFile = new File(targetFilePath);
		File unzipDirFile = new File(unzipFilePath);
		File unzipDirParentFile = unzipDirFile.getParentFile();
		if (!targetFile.exists() || !targetFile.isFile()) {
			throw new RuntimeException(targetFile.getPath() + "文件不存在！");
		}
		if (unzipDirFile.exists()) {
			throw new RuntimeException(unzipFilePath + "同名文件或文件夹已存在！");
		} else {
			if (!unzipDirParentFile.exists()) {
				throw new RuntimeException(unzipDirParentFile.getAbsolutePath() + "路径不存在！");
			}
		}
		ZipFile zipFile = null;
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			zipFile = new ZipFile(targetFile);
			Enumeration<?> entries = zipFile.getEntries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				if (isLog) {
					System.out.println("正在解压" + entry.getName() + "...");
				}
				/* 如果是文件夹，就创建个文件夹 */
				if (entry.isDirectory()) {
					String dirPath = unzipFilePath + FILE_PATH_SEPARATOR + entry.getName();
					File dir = new File(dirPath);
					dir.mkdirs();
				} else {
					/* 如果是文件，就先创建一个文件，然后用io流把内容copy过去 */
					File file = new File(unzipFilePath + FILE_PATH_SEPARATOR + entry.getName());
					/* 保证这个文件的父文件夹必须要存在 */
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					file.createNewFile();
					/* 将压缩文件内容写入到这个文件中 */
					is = zipFile.getInputStream(entry);
					fos = new FileOutputStream(file);
					int len;
					byte[] buf = new byte[BYTE_1024];
					while ((len = is.read(buf)) != -1) {
						fos.write(buf, 0, len);
					}
				}
			}
			long end = System.currentTimeMillis();
			if (isLog) {
				System.out.println("解压完成，耗时：" + (end - start) + " ms");
			}
			return true;
		} catch (IOException e) {
			return false;
		} finally {
			try {
				if (null != fos) {
					fos.close();
				}
				if (null != is) {
					is.close();
				}
				if (zipFile != null) {
					zipFile.close();
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * zip方式压缩文件夹
	 * 
	 * @param targetDir
	 *            需要压缩的文件夹路径
	 * @param zipFilePath
	 *            压缩文件输出路径
	 * @param isKeepDirStructure
	 *            是否保留原来的目录结构,true:保留目录结构;
	 *            false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
	 * @param isOverride
	 *            若压缩文件存在，是否覆盖
	 * @param isLog
	 *            是否打印压缩日志
	 * @return 是否压缩成功
	 */
	public static boolean zipDir(String targetDir, String zipFilePath, boolean isKeepDirStructure, boolean isOverride,
			boolean isLog) {
		boolean result = true;
		long start = System.currentTimeMillis();
		if (null == targetDir || "".equals(targetDir.trim())) {
			throw new IllegalArgumentException("被压缩的目标文件夹不能为空！");
		}
		File targetDirFile = new File(targetDir);
		if (!targetDirFile.exists() || !targetDirFile.isDirectory()) {
			throw new IllegalArgumentException("被压缩的目标文件夹" + targetDir + "不存在！");
		}
		if (null == zipFilePath) {
			throw new IllegalArgumentException("压缩文件路径不能为空！");
		}
		if (!zipFilePath.endsWith(ZIP_SUFFIX)) {
			throw new IllegalArgumentException(zipFilePath + "压缩文件类型错误！");
		}
		File zipFile = new File(zipFilePath);
		File zipParentDirFile = zipFile.getParentFile();
		if (null == zipParentDirFile) {
			throw new RuntimeException(zipFilePath + "路径不存在！");
		}
		if (!zipParentDirFile.exists()) {
			throw new RuntimeException(zipParentDirFile.getAbsolutePath() + "路径不存在！");
		}
		if (zipFile.exists()) {
			if (zipFile.isDirectory()) {
				throw new RuntimeException(zipFilePath + "路径有同名文件夹存在！");
			} else {
				if (!isOverride) {
					throw new RuntimeException(zipFilePath + "路径有同名文件存在！");
				}
			}
		}
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		try {
			fos = new FileOutputStream(zipFile);
			zos = new ZipOutputStream(fos);
			result = doZipDir(targetDirFile, zos, targetDirFile.getName(), isKeepDirStructure, isLog);
			long end = System.currentTimeMillis();
			if (isLog) {
				System.out.println("压缩完成，耗时：" + (end - start) + " ms");
			}
		} catch (IOException e) {
			result = false;
		} finally {
			try {
				if (zos != null) {
					zos.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
			}
		}
		return result;
	}

	private static boolean doZipDir(File targetFile, ZipOutputStream zos, String fileName, boolean isKeepDirStructure,
			boolean isLog) {
		byte[] buf = new byte[BYTE_1024];
		boolean result = true;
		FileInputStream fis = null;
		try {
			if (targetFile.isFile()) {
				/* 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字 */
				zos.putNextEntry(new ZipEntry(fileName));
				/* copy文件到zip输出流中 */
				int len;
				fis = new FileInputStream(targetFile);
				while ((len = fis.read(buf)) != -1) {
					zos.write(buf, 0, len);
				}
				zos.closeEntry();
			} else {
				File[] listFiles = targetFile.listFiles();
				if (listFiles == null || listFiles.length == 0) {
					/* 需要保留原来的文件结构时,需要对空文件夹进行处理 */
					if (isKeepDirStructure) {
						if (isLog) {
							System.out.println("正在压缩" + fileName + "...");
						}
						/* 空文件夹的处理 */
						zos.putNextEntry(new ZipEntry(fileName + FILE_PATH_SEPARATOR));
						/* 没有文件，不需要文件的copy */
						zos.closeEntry();
					}
				} else {
					for (File file : listFiles) {
						if (isLog) {
							System.out.println("正在压缩" + file.getName() + "...");
						}
						/* 判断是否需要保留原来的文件结构 */
						if (isKeepDirStructure) {
							result &= doZipDir(file, zos, fileName + FILE_PATH_SEPARATOR + file.getName(),
									isKeepDirStructure, isLog);
						} else {
							result &= doZipDir(file, zos, file.getName(), isKeepDirStructure, isLog);
						}
					}
				}
			}
		} catch (IOException e) {
			result = false;
			e.printStackTrace();
		} finally {
			try {
				if (null != fis) {
					fis.close();
				}
			} catch (IOException e) {
			}
		}
		return result;
	}

	/**
	 * zip方式压缩多文件
	 * 
	 * @param targetFiles
	 *            需要压缩的文件的集合
	 * @param zipFilePath
	 *            压缩文件的输出路径
	 * @param isOverride
	 *            若压缩文件存在，是否覆盖
	 * @param isLog
	 *            是否打印压缩日志
	 * @return 是否压缩成功
	 */
	public static boolean zipFiles(List<File> targetFiles, String zipFilePath, boolean isOverride, boolean isLog) {
		long start = System.currentTimeMillis();
		if (null == targetFiles || 0 == targetFiles.size()) {
			throw new IllegalArgumentException("被压缩的目标文件不能为空！");
		}
		for (File tempFile : targetFiles) {
			if (!tempFile.exists()) {
				throw new IllegalArgumentException("被压缩的目标文件" + tempFile.getAbsolutePath() + "不存在！");
			}
		}
		if (null == zipFilePath) {
			throw new IllegalArgumentException("压缩文件路径不能为空！");
		}
		if (!zipFilePath.endsWith(ZIP_SUFFIX)) {
			throw new IllegalArgumentException(zipFilePath + "压缩文件类型错误！");
		}
		File zipFile = new File(zipFilePath);
		File zipParentDirFile = zipFile.getParentFile();
		if (null == zipParentDirFile) {
			throw new RuntimeException(zipFilePath + "路径不存在！");
		}
		if (!zipParentDirFile.exists()) {
			throw new RuntimeException(zipParentDirFile.getAbsolutePath() + "路径不存在！");
		}
		if (zipFile.exists()) {
			if (zipFile.isDirectory()) {
				throw new RuntimeException(zipFilePath + "路径有同名文件夹存在！");
			} else {
				if (!isOverride) {
					throw new RuntimeException(zipFilePath + "路径有同名文件存在！");
				}
			}
		}
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		FileInputStream fis = null;
		try {
			fos = new FileOutputStream(zipFile);
			zos = new ZipOutputStream(fos);
			for (File targetFile : targetFiles) {
				if (null == targetFile) {
					continue;
				}
				if (isLog) {
					System.out.println("正在压缩" + targetFile.getName() + "...");
				}
				byte[] buf = new byte[BYTE_1024];
				zos.putNextEntry(new ZipEntry(targetFile.getName()));
				int len;
				fis = new FileInputStream(targetFile);
				while ((len = fis.read(buf)) != -1) {
					zos.write(buf, 0, len);
				}
				zos.closeEntry();
			}
			long end = System.currentTimeMillis();
			if (isLog) {
				System.out.println("压缩完成，耗时：" + (end - start) + " ms");
			}
			return true;
		} catch (IOException e) {
			return false;
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (zos != null) {
					zos.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 解压tar.gz类型的压缩文件
	 * 
	 * @param targetFilePath
	 *            要解压的压缩文件
	 * @param untarFilePath
	 *            解压后文件的路径
	 * @param isLog
	 *            是否打印解压日志
	 * @return 解压出的文件列表
	 * @throws FileNotFoundException
	 *             要解压的压缩文件或解压后文件的路径未找到
	 */
	public static List<String> unTarFile(String targetFilePath, String untarFilePath, boolean isLog)
			throws FileNotFoundException {
		if (null == targetFilePath || "".equals(targetFilePath.trim())) {
			throw new IllegalArgumentException(targetFilePath + "压缩文件路径未找到！");
		}
		if (null == untarFilePath || "".equals(untarFilePath.trim())) {
			throw new IllegalArgumentException("解压后的目标文件夹" + untarFilePath + "不能为空！");
		}
		if (!targetFilePath.endsWith(TAR_GZ_SUFFIX)) {
			throw new IllegalArgumentException(targetFilePath + "文件类型错误！");
		}
		File targetFile = new File(targetFilePath);
		File untarDirFile = new File(untarFilePath);
		if (!targetFile.exists() || !targetFile.isFile()) {
			throw new FileNotFoundException(targetFile.getPath() + "文件不存在！");
		}
		if (!untarDirFile.exists()) {
			throw new FileNotFoundException(untarFilePath + "解压后文件的路径不存在！");
		}
		
		long startTime = System.currentTimeMillis();

		List<String> fileList = new ArrayList<String>();

		OutputStream os = null;
		FileInputStream fis = null;
		GZIPInputStream gis = null;

		TarArchiveInputStream tais = null;
		TarArchiveEntry tae = null;
		TarArchiveEntry[] taes = null;

		File thisFile = null;
		File childFile = null;
		String thisFileName = null;

		try {
			fis = new FileInputStream(targetFilePath);
			gis = new GZIPInputStream(fis);
			tais = new TarArchiveInputStream(gis);

			while ((tae = tais.getNextTarEntry()) != null) {
				thisFileName = untarDirFile.getAbsolutePath() + FILE_PATH_SEPARATOR + tae.getName();
				thisFile = new File(thisFileName);
				if (isLog) {
					System.out.println("正在解压" + thisFileName + "...");
				}
				if (tae.isDirectory()) {
					if (!thisFile.exists()) {
						thisFile.mkdir();
					}
					taes = tae.getDirectoryEntries();
					for (int i = 0; i < taes.length; i++) {
						try {
							childFile = new File(thisFileName + FILE_PATH_SEPARATOR + taes[i].getName());
							fileList.add(childFile.getAbsolutePath());
							os = new FileOutputStream(childFile);
							byte[] buf = new byte[BYTE_1024];
							int len = 0;
							while ((len = tais.read(buf)) != -1) {
								os.write(buf, 0, len);
							}
						} catch (Exception e) {
						} finally {
							if (null != os) {
								os.close();
								os = null;
							}
						}
					}
				} else {
					fileList.add(thisFileName);
					/* 保证这个文件的父文件夹必须要存在 */
					if (!thisFile.getParentFile().exists()) {
						thisFile.getParentFile().mkdirs();
					}
					thisFile.createNewFile();
					os = new FileOutputStream(thisFile);
					try {
						byte[] buf = new byte[BYTE_1024];
						int len = 0;
						while ((len = tais.read(buf)) != -1) {
							os.write(buf, 0, len);
						}
					} catch (Exception e) {
					} finally {
						if (null != os) {
							os.close();
							os = null;
						}
					}
				}
			}
			long endTime = System.currentTimeMillis();
			if (isLog) {
				System.out.println("解压完成，消耗时长：" + (endTime - startTime) + "ms");
			}
		} catch (Exception e) {
			throw new RuntimeException("解压失败：" + e.getMessage());
		} finally {
			try {
				if (tais != null) {
					tais.close();
				}
				if (gis != null) {
					gis.close();
				}
				if (fis != null) {
					fis.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (Exception ce) {
				tais = null;
				gis = null;
				fis = null;
				os = null;
			}
		}
		return fileList;

	}

	/**
	 * tar方式压缩多文件
	 * 
	 * @param fileList
	 *            需要被压缩的多文件集合
	 * @param tarFileDir
	 *            压缩后文件存放的路径
	 * @param tarFileName
	 *            压缩后文件的名字（不含后缀.tar.gz）
	 * @param isOverride
	 *            若压缩文件存在，是否覆盖
	 * @param isLog
	 *            是否打印压缩日志
	 * @return 压缩后压缩文件的路径
	 * @throws FileNotFoundException
	 *             压缩文件路径未找到
	 */
	public static String tarFiles(List<File> fileList, String tarFileDir, String tarFileName, boolean isOverride,
			boolean isLog) throws FileNotFoundException {
		File tarDirFile = new File(tarFileDir);
		if (null == fileList || 0 == fileList.size()) {
			throw new IllegalArgumentException("被压缩的目标文件不能为空！");
		}
		for (File tempFile : fileList) {
			if (!tempFile.exists()) {
				throw new IllegalArgumentException("被压缩的目标文件" + tempFile.getAbsolutePath() + "不存在！");
			}
		}
		if (null == tarFileDir) {
			throw new IllegalArgumentException("压缩文件路径不能为空！");
		}
		if (!tarDirFile.exists()) {
			throw new FileNotFoundException(tarFileDir + "路径不存在！");
		}
		long startTime = System.currentTimeMillis();
		/* 生成的tar归档文件的路径 */
		String tarFilePath = tarDirFile.getAbsolutePath() + FILE_PATH_SEPARATOR + tarFileName + ".tar";
		/* 生成的gz压缩文件的路径 */
		String gzFilePath = tarFilePath + ".gz";
		File tarFile = new File(tarFilePath);
		File gzFile = new File(gzFilePath);
		if (tarFile.exists()) {
			if (tarFile.isDirectory()) {
				throw new RuntimeException(tarFilePath + "路径有同名文件夹存在！");
			} else {
				if (!isOverride) {
					throw new RuntimeException(tarFilePath + "路径有同名文件存在！");
				}
			}
		}
		if (gzFile.exists()) {
			if (gzFile.isDirectory()) {
				throw new RuntimeException(gzFilePath + "路径有同名文件夹存在！");
			} else {
				if (!isOverride) {
					throw new RuntimeException(gzFilePath + "路径有同名文件存在！");
				}
			}
		}
		/* 把文件归档成tar包 */
		TarArchiveOutputStream tos = null;
		try {
			tos = new TarArchiveOutputStream(new FileOutputStream(tarFilePath));
			/* 抑制由于文件路径名过长而抛异常错误 */
			tos.setLongFileMode(TarOutputStream.LONGFILE_GNU);
			String basePath = null;
			for (File file : fileList) {
				basePath = file.getName();
				doTarFile(file, tos, basePath, isLog);
			}
		} catch (IOException e) {
			throw new RuntimeException("归档失败！" + e.getMessage());
		} finally {
			try {
				if (null != tos) {
					tos.close();
				}
			} catch (IOException e) {
			}
		}
		/* 把tar包压缩成gzip文件 */
		BufferedInputStream bis = null;
		GzipCompressorOutputStream gcos = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(tarFilePath));
			gcos = new GzipCompressorOutputStream(new BufferedOutputStream(new FileOutputStream(gzFilePath)));
			byte[] buffer = new byte[BYTE_1024];
			int read = -1;
			while ((read = bis.read(buffer)) != -1) {
				gcos.write(buffer, 0, read);
			}
			/* 删除中间tar归档文件 */
			new File(tarFilePath).deleteOnExit();
		} catch (IOException e) {
			throw new RuntimeException("压缩失败！" + e.getMessage());
		} finally {
			try {
				if (null != gcos) {
					gcos.close();
				}
				if (null != bis) {
					bis.close();
				}
			} catch (IOException e) {
			}
		}
		long endTime = System.currentTimeMillis();
		if (isLog) {
			System.out.println("压缩完成，消耗时长：" + (endTime - startTime) + "ms");
		}
		return gzFilePath;
	}

	/**
	 * tar方式压缩文件或文件夹
	 * 
	 * @param srcFileOrDirPath
	 *            需要被压缩的文件或文件夹路径
	 * @param tarFileDir
	 *            压缩后文件存放的路径
	 * @param tarFileName
	 *            压缩后文件的名字（不含后缀.tar.gz）
	 * @param isOverride
	 *            若压缩文件存在，是否覆盖
	 * @param isLog
	 *            是否打印压缩日志
	 * @return 压缩后压缩文件的路径
	 * @throws FileNotFoundException
	 *             需要被压缩的文件或文件夹路径未找到
	 */
	public static String tarFileOrDir(String srcFileOrDirPath, String tarFileDir, String tarFileName,
			boolean isOverride, boolean isLog) throws FileNotFoundException {
		File srcFile = new File(srcFileOrDirPath);
		File tarDirFile = new File(tarFileDir);
		if (null == srcFileOrDirPath || "".equals(srcFileOrDirPath.trim())) {
			throw new IllegalArgumentException("被压缩的目标文件或文件夹不能为空！");
		}
		if (!srcFile.exists()) {
			throw new FileNotFoundException("被压缩的目标文件或文件夹" + srcFileOrDirPath + "不存在！");
		}
		if (null == tarFileDir) {
			throw new IllegalArgumentException("压缩文件路径不能为空！");
		}
		if (!tarDirFile.exists()) {
			throw new RuntimeException(tarFileDir + "路径不存在！");
		}
		long startTime = System.currentTimeMillis();
		/* 生成的tar归档文件的路径 */
		String tarFilePath = tarDirFile.getAbsolutePath() + FILE_PATH_SEPARATOR + tarFileName + ".tar";
		/* 生成的gz压缩文件的路径 */
		String gzFilePath = tarFilePath + ".gz";
		File tarFile = new File(tarFilePath);
		File gzFile = new File(gzFilePath);
		if (tarFile.exists()) {
			if (tarFile.isDirectory()) {
				throw new RuntimeException(tarFilePath + "路径有同名文件夹存在！");
			} else {
				if (!isOverride) {
					throw new RuntimeException(tarFilePath + "路径有同名文件存在！");
				}
			}
		}
		if (gzFile.exists()) {
			if (gzFile.isDirectory()) {
				throw new RuntimeException(gzFilePath + "路径有同名文件夹存在！");
			} else {
				if (!isOverride) {
					throw new RuntimeException(gzFilePath + "路径有同名文件存在！");
				}
			}
		}
		/* 把文件归档成tar包 */
		TarArchiveOutputStream tos = null;
		try {
			tos = new TarArchiveOutputStream(new FileOutputStream(tarFilePath));
			/* 抑制由于文件路径名过长而抛异常错误 */
			tos.setLongFileMode(TarOutputStream.LONGFILE_GNU);
			String basePath = srcFile.getName();
			doTarFile(srcFile, tos, basePath, isLog);
		} catch (IOException e) {
			throw new RuntimeException("归档失败！" + e.getMessage());
		} finally {
			try {
				if (null != tos) {
					tos.close();
				}
			} catch (IOException e) {
			}
		}
		/* 把tar包压缩成gzip文件 */
		BufferedInputStream bis = null;
		GzipCompressorOutputStream gcos = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(tarFilePath));
			gcos = new GzipCompressorOutputStream(new BufferedOutputStream(new FileOutputStream(gzFilePath)));
			byte[] buffer = new byte[BYTE_1024];
			int read = -1;
			while ((read = bis.read(buffer)) != -1) {
				gcos.write(buffer, 0, read);
			}
			/* 删除中间tar归档文件 */
			new File(tarFilePath).deleteOnExit();
			long endTime = System.currentTimeMillis();
			if (isLog) {
				System.out.println("压缩完成，耗时：" + (endTime - startTime) + "ms");
			}
		} catch (IOException e) {
			throw new RuntimeException("压缩失败！" + e.getMessage());
		} finally {
			try {
				if (null != gcos) {
					gcos.close();
				}
				if (null != bis) {
					bis.close();
				}
			} catch (IOException e) {
			}
		}
		return gzFilePath;
	}

	private static void doTarFile(File file, TarArchiveOutputStream tos, String basePath, boolean isLog) {
		if (isLog) {
			System.out.println("正在压缩" + file.getAbsolutePath() + "...");
		}
		BufferedInputStream bis = null;
		try {
			if (file.isFile()) {
				/* 具体归档操作 */
				TarArchiveEntry tEntry = new TarArchiveEntry(basePath);
				tEntry.setSize(file.length());
				tos.putArchiveEntry(tEntry);
				bis = new BufferedInputStream(new FileInputStream(file));

				byte[] buffer = new byte[BYTE_1024];
				int read = -1;
				while ((read = bis.read(buffer)) != -1) {
					tos.write(buffer, 0, read);
				}
				tos.closeArchiveEntry();
			} else {
				File[] listFiles = file.listFiles();
				for (File f : listFiles) {
					doTarFile(f, tos, basePath + FILE_PATH_SEPARATOR + f.getName(), isLog);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("归档失败" + e.getMessage());
		} finally {
			try {
				if (null != bis) {
					bis.close();
				}
			} catch (IOException e) {
			}
		}
	}

}
