package store.javac.fyutil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpClientUtil {
	
	private HttpClientUtil() {}
	
	/**
	 * 设置连接超时
	 */
	private static Integer connect_timeout = 3000;
	/**
	 * 设置指定时间内服务器没有返回数据的超时
	 */
	private static Integer data_timeout = 3000;
	
	private static final String CHARSET_UTF8 = "utf-8";
	
	private static HttpURLConnection conn = null;
	private static OutputStreamWriter osw = null;
	private static BufferedReader br = null;
	
	public static void setConnectTimeout(Integer connectTimeout) {
		connect_timeout = connectTimeout;
	}
	
	public static void setDataTimeout(Integer dataTimeout) {
		data_timeout = dataTimeout;
	}
	
	/**
	 * 
	 * @Description <p>get网络请求</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年7月3日 下午3:27:55</p> 
	 * @author <p>jack</p>
	 *
	 * @param url 请求地址
	 * @return 请求返回结果
	 */
	public static String getDataDoGet(String url) {
		try {
			URL httpUrl = new URL(url);
			conn = (HttpURLConnection) httpUrl.openConnection();
			conn.setConnectTimeout(connect_timeout);
			conn.setReadTimeout(data_timeout);
			conn.setRequestMethod("GET");
			conn.setUseCaches(false);
			conn.connect();
			if(HttpURLConnection.HTTP_OK != conn.getResponseCode()){
				/*200*/
				return null;
			}
			br = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String lines;
			String response = "";
			while ((lines = br.readLine()) != null) {
				lines = new String(lines.getBytes(), CHARSET_UTF8);
				response += lines + "\n";
			}
			return response;
		} catch (Exception e) {
			return null;
		}finally {
			closed();
		}
	}

	/**
	 * 
	 * @Description <p>post网络请求</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年7月3日 下午2:39:04</p> 
	 * @author <p>jack</p>
	 *
	 * @param url 请求的地址
	 * @param params 请求的参数，null即为get请求
	 * @return 请求返回的结果
	 */
	public static String getDataDoPost(String url, Map<String, String> params) {
		try {
			URL httpUrl = new URL(url);
			conn = (HttpURLConnection) httpUrl.openConnection();
			conn.setConnectTimeout(connect_timeout);
			conn.setReadTimeout(data_timeout);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setUseCaches(false);
			/*发送POST请求必须设置如下两行*/
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.connect();
			if(null == params){
				return getDataDoGet(url);
			}
			osw = new OutputStreamWriter(conn.getOutputStream());
			String paramStr = "";
			for (Map.Entry<String, String> entry : params.entrySet()) {
				paramStr += entry.getKey() + "=" + entry.getValue() + "&";
			}
			osw.write(paramStr);
			osw.flush();
			if(HttpURLConnection.HTTP_OK != conn.getResponseCode()){
				/*200*/
				return null;
			}
			br = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String lines;
			String response = "";
			while ((lines = br.readLine()) != null) {
				lines = new String(lines.getBytes(), CHARSET_UTF8);
				response += lines + "\n";
			}
			return response;
		} catch (Exception e) {
			return null;
		} finally {
			closed();
		}
	}
	
	private static void closed() {
		try {
			if (null != br) {
				br.close();
			}
			if (null != osw) {
				osw.close();
			}
			if (null != conn) {
				conn.disconnect();
			}
		} catch (Exception e2) {
			br = null;
			osw = null;
			conn = null;
		}
	}

}
