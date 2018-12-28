package store.javac.fyutil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 
 * @Description <p>命令执行操作工具类</p>
 * @version <p>v1.0</p>
 * @Date <p>2018年7月4日 下午2:56:45</p> 
 * @author <p>fangyang</p>
 *
 */
public class CommandUtil {

	/**
	 * 执行一批的cmd命令
	 * @param cmdCommandList 存放cmd命令语句的集合
	 * @return 执行这批cmd命令产生的结果返回（如果没有返回结果，则为null）
	 */
	public static String execCmdCommand(List<String> cmdCommandList) {
		/**
		 * cmd /c dir 是执行完dir命令后关闭命令窗口。
		 * cmd /k dir 是执行完dir命令后不关闭命令窗口。
		 * cmd /c start dir 会打开一个新窗口后执行dir指令，原窗口会关闭。
		 * cmd /k start dir 会打开一个新窗口后执行dir指令，原窗口不会关闭。
		 */
		BufferedReader br = null;
		InputStream is =null;
		Process process = null;
		try {
			for(String cmdCommand :cmdCommandList) {
				process= Runtime.getRuntime().exec(cmdCommand);
				process.waitFor();
			}
			is = process.getInputStream();
			br = new BufferedReader(new InputStreamReader(is , "gbk"));
			StringBuffer sb = new StringBuffer();
			String line = null;
			while((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			return sb.toString();
		} catch (Exception e) {
			return null;
		}finally {
			try {
				if(is != null) is.close();
				if(br != null) br.close();
			} catch (IOException e) {
			}
			if(process != null) process.destroy();
		}
	}

	/**
	 * 执行一批的shell命令
	 * @param shellCommandList 存放shell命令语句的集合
	 * @return 执行这批shell命令产生的结果返回（如果没有返回结果，则为null）
	 */
	public static String execShellCommand(List<String> shellCommandList) {
		Process process = null;
		InputStream is = null;
		BufferedReader br = null;
		try {
			int offset = 0;
			for(String shellCommand : shellCommandList) {
				offset ++;
				process= Runtime.getRuntime().exec(shellCommand);
				if(offset != shellCommandList.size()) process.waitFor();
			}
			is= process.getInputStream();
			br= new BufferedReader(new InputStreamReader(is, "utf-8"));
			StringBuffer sb = new StringBuffer();
			String line = null;
			while((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			process.waitFor();
			return sb.toString();
		} catch (Exception e) {
			return null;
		}finally {
			try {
				if(br != null) br.close();
				if(is != null) is.close();
			} catch (IOException e) {
			}
			if(null != process) process.destroy();
		}
	}
}
