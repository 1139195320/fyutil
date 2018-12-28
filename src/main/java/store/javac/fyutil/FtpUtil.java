package store.javac.fyutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FtpUtil {

	/**
	 * 当前的ftp连接
	 */
	private static FTPClient ftpClient = null;
	/**
	 * 当前连接的server
	 */
	private static String current_server;
	/**
	 * 当前连接的port
	 */
	private static int current_port;

	/**
	 * 连接FTP服务器
	 * @param server 服务器地址
	 * @param port 连接端口
	 * @return
	 */
	public boolean connect(String server, int port) {
		try {
			if (ftpClient.isConnected()) {
				return true;
			}
			ftpClient.connect(server, port);
			current_server = server;
			current_port = port;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 设置连接为被动模式
	 * 
	 * @return
	 */
	public boolean setPASV() {
		if (null == ftpClient)
			return false;
		ftpClient.enterLocalPassiveMode();
		return true;
	}

	/**
	 * 设置连接为主动模式
	 * 
	 * @return
	 */
	public boolean setPORT() {
		if (null == ftpClient)
			return false;
		ftpClient.enterLocalActiveMode();
		return true;
	}

	/**
	 * 打开FTP服务器
	 * @param username 用户名
	 * @param passwd 密码
	 * @return
	 */
	public boolean open(String username, String passwd) {
		if (!connect(current_server , current_port)) {
			return false;
		}
		boolean result = false;
		try {
			result = ftpClient.login(username, passwd);
			// 检测连接是否成功
			int reply = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				close();
				result = false;
			}
		} catch (IOException e) {
			result = false;
		}
		return result;
	}

	/**
	 * 关闭FTP服务器
	 */
	public void close() {
		try {
			if (ftpClient != null) {
				if (ftpClient.isConnected()) {
					ftpClient.logout();
					ftpClient.disconnect();
				}

				ftpClient = null;
			}
		} catch (IOException e) {
		}
	}

	/**
	 * 上传文件到FTP服务器
	 * 
	 * @param filename 上传的文件名
	 * @param path 上传到的ftp目录，从ftp根目录起
	 * @param is 上传文件的流
	 * @return
	 */
	public boolean upload(String filename, String path, InputStream is) {
		boolean result = false;
		try {
			cd(path);
			ftpClient.setBufferSize(1024);
			ftpClient.setControlEncoding("GBK");
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			result = ftpClient.storeFile(filename, is);
		} catch (IOException e) {
			result = false;
		}finally {
			try {
				if(null != is) is.close();
			} catch (Exception e2) {
			}
		}
		return result;
	}

	/**
	 * 上传文件到FTP服务器
	 * 
	 * @param filename 上传的文件名
	 * @param path 上传到的ftp目录，从ftp根目录起
	 * @param filepath 上传文件的路径
	 * @return
	 */
	public boolean upload(String filename, String path, String filepath) {
		boolean result = false;
		FileInputStream is = null;
		try {
			cd(path);
			ftpClient.setBufferSize(1024);
			ftpClient.setControlEncoding("GBK");
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			is = new FileInputStream(new File(filepath));
			result = ftpClient.storeFile(filename, is);
			
		} catch (IOException e) {
			result = false;
		}finally {
			try {
				if(null != is) is.close();
			} catch (Exception e2) {
			}
		}
		return result;
	}

	/**
	 * 上传文件
	 * 
	 * @param filename 上传的文件名
	 * @param path 上传到的ftp目录，从ftp根目录起
	 * @param file 上传的文件
	 * @return
	 */
	public boolean upload(String filename, String path, File file) {
		boolean result = false;
		FileInputStream is = null;
		try {
			cd(path);
			ftpClient.setBufferSize(1024);
			ftpClient.setControlEncoding("GBK");
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			is = new FileInputStream(file);
			result = ftpClient.storeFile(filename, is);
			is.close(); // 关闭输入流
		} catch (IOException e) {
			result = false;
		}finally {
			try {
				if(null != is) is.close();
			} catch (Exception e2) {
			}
		}
		return result;
	}

	/**
	 * 循环切换目录
	 * 
	 * @param dir
	 * @return
	 */
	private boolean cd(String dir) {
		boolean result = true;
		try {
			String[] dirs = dir.split("/");
			if (dirs.length == 0) {
				return ftpClient.changeWorkingDirectory(dir);
			}

			result = ftpClient.changeToParentDirectory();
			for (String dirss : dirs) {
				result = result && ftpClient.changeWorkingDirectory(dirss);
			}

			result = true;
		} catch (IOException e) {
			result = false;
		}
		return result;
	}

}
