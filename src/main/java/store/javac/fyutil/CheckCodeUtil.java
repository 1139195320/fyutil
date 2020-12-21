package store.javac.fyutil;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * 
 * @Description <p>图形验证码的操作工具类</p>
 * @version <p>v1.0</p>
 * @Date <p>2018年7月4日 下午2:54:42</p> 
 * @author <p>jack</p>
 *
 */
public class CheckCodeUtil {

	/**
	 * 禁止实例化
	 */
	private CheckCodeUtil() {
	}

	private static final String CODE_STR[] = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
			"o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

	private static final String CODE_NUM[] = { "1", "2", "3", "4", "5", "6", "7", "8", "9" };

	private static final Random RANDOM = new Random();

	/**
	 * 生成随机字符串图形验证码图片或流
	 * 
	 * @param codeImgWidth
	 *            图形验证码的宽
	 * @param codeImgHeight
	 *            图形验证码的高
	 * @param checkCodeLen
	 *            验证码的长度
	 * @param checkCodeImgFilePath
	 *            储存生成图形验证码图片的路径
	 * @param interfaceLineCount
	 *            干扰线的数量
	 * @return 储存图形验证码的信息（checkCode、fileOutputStream）
	 * @throws FileNotFoundException
	 */
	public static Map<String, Object> drawCheckCodeImg(Integer codeImgWidth, Integer codeImgHeight,
			Integer checkCodeLen, String checkCodeImgFilePath, Integer interfaceLineCount)
			throws FileNotFoundException {
		if (null == checkCodeLen || checkCodeLen <= 0)
			return null;
		String checkCode = getRandomStringInStrAndNum(checkCodeLen);
		return doDrawRandomCheckCodeImg(codeImgWidth, codeImgHeight, checkCode,
				new FileOutputStream(new File(checkCodeImgFilePath)), interfaceLineCount);
	}

	/**
	 * 生成随机字符串图形验证码图片或流
	 * 
	 * @param codeImgWidth
	 *            图形验证码的宽
	 * @param codeImgHeight
	 *            图形验证码的高
	 * @param checkCodeLen
	 *            验证码的长度
	 * @param fos
	 *            储存图形验证码的流
	 * @param interfaceLineCount
	 *            干扰线的数量
	 * @return 储存图形验证码的信息（checkCode、fileOutputStream）
	 */
	public static Map<String, Object> drawCheckCodeImg(Integer codeImgWidth, Integer codeImgHeight,
			Integer checkCodeLen, FileOutputStream fos, Integer interfaceLineCount) {
		if (null == checkCodeLen || checkCodeLen <= 0)
			return null;
		String checkCode = getRandomStringInStrAndNum(checkCodeLen);
		return doDrawRandomCheckCodeImg(codeImgWidth, codeImgHeight, checkCode, fos, interfaceLineCount);
	}

	/**
	 * 生成指定字符串图形验证码图片或流
	 * 
	 * @param codeImgWidth
	 *            图形验证码的宽
	 * @param codeImgHeight
	 *            图形验证码的高
	 * @param checkCode
	 *            指定的字符串验证码
	 * @param checkCodeImgFilePath
	 *            储存生成图形验证码图片的路径
	 * @param interfaceLineCount
	 *            干扰线的数量
	 * @return 储存图形验证码的信息（checkCode、fileOutputStream）
	 * @throws FileNotFoundException
	 */
	public static Map<String, Object> drawCheckCodeImg(Integer codeImgWidth, Integer codeImgHeight, String checkCode,
			String checkCodeImgFilePath, Integer interfaceLineCount) throws FileNotFoundException {
		if (null == checkCode || checkCode.length() == 0)
			return null;
		return doDrawRandomCheckCodeImg(codeImgWidth, codeImgHeight, checkCode,
				new FileOutputStream(new File(checkCodeImgFilePath)), interfaceLineCount);
	}

	/**
	 * 生成指定字符串图形验证码图片或流
	 * 
	 * @param codeImgWidth
	 *            图形验证码的宽
	 * @param codeImgHeight
	 *            图形验证码的高
	 * @param checkCode
	 *            指定的字符串验证码
	 * @param fos
	 *            储存图形验证码的流
	 * @param interfaceLineCount
	 *            干扰线的数量
	 * @return 储存图形验证码的信息（checkCode、fileOutputStream）
	 */
	public static Map<String, Object> drawCheckCodeImg(Integer codeImgWidth, Integer codeImgHeight, String checkCode,
			FileOutputStream fos, Integer interfaceLineCount) {
		if (null == checkCode || checkCode.length() == 0)
			return null;
		return doDrawRandomCheckCodeImg(codeImgWidth, codeImgHeight, checkCode, fos, interfaceLineCount);
	}

	private static Map<String, Object> doDrawRandomCheckCodeImg(Integer codeImgWidth, Integer codeImgHeight,
			String checkCode, FileOutputStream fos, Integer interfaceLineCount) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Integer checkCodeLen = checkCode.length();
			// 创建画板
			BufferedImage bi = new BufferedImage(codeImgWidth, codeImgHeight, BufferedImage.TYPE_INT_BGR);
			// 创建画笔
			Graphics pen = bi.getGraphics();
			// 设置画板背景颜色
			pen.setColor(Color.WHITE);
			// 设置字体
			pen.setFont(new Font("微软雅黑", Font.BOLD, codeImgWidth / checkCodeLen));
			// 画出一个矩形
			pen.fillRect(0, 0, codeImgWidth, codeImgHeight);
			// 把字符写入图片
			Integer x_ = RANDOM.nextInt(2) + 3;
			for (int i = 0; i < checkCodeLen; i++) {
				Integer fontSize = RANDOM.nextInt(4) + codeImgWidth / checkCodeLen;
				if (fontSize >= codeImgHeight)
					fontSize = codeImgHeight - 1;
				pen.setFont(new Font("微软雅黑", Font.BOLD, fontSize));
				pen.setColor(getRandomColor());
				// 距离下[0 , codeImgHeight - fontSize],距离上[fontSize , codeImgHeight]
				Integer y = RANDOM.nextInt(codeImgHeight - fontSize) + fontSize;
				pen.drawString(checkCode.charAt(i) + "", x_, y);
				x_ += fontSize - RANDOM.nextInt(2) - 1;
			}
			if (interfaceLineCount != null && interfaceLineCount > 0) {
				for (int i = 0; i < interfaceLineCount; i++) {
					pen.setColor(getRandomColor());
					pen.drawLine(RANDOM.nextInt(10), RANDOM.nextInt(codeImgHeight),
							RANDOM.nextInt(20) + (codeImgWidth - 20), RANDOM.nextInt(codeImgHeight - 3) + 3);
				}
			}
			// 输出图片到客户端
			ImageIO.write(bi, "png", fos);
			result.put("checkCode", checkCode);
			result.put("fileOutputStream", fos);
		} catch (Exception e) {
			return null;
		}
		return result;
	}
	
	/**
	 * 获取随机字符串，包含数字和字符，长度为len
	 * 
	 * @param len
	 * @return
	 */
	public static String getRandomStringInStrAndNum(Integer len) {
		if (null == len)
			return null;
		Object code[] = addTwoArray(CODE_STR, CODE_NUM);
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < len; i++) {
			result.append(code[RANDOM.nextInt(code.length)]);
		}
		return result.toString();
	}

	/**
	 * 获取随机字符串，只包含字符，长度为len
	 * 
	 * @param len
	 * @return
	 */
	public static String getRandomStringInStr(Integer len) {
		if (null == len)
			return null;
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < len; i++) {
			result.append(CODE_STR[RANDOM.nextInt(CODE_STR.length)]);
		}
		return result.toString();
	}

	/**
	 * 获取随机字符串，只包含数字，长度为len
	 * 
	 * @param len
	 * @return
	 */
	public static String getRandomStringInNum(Integer len) {
		if (null == len)
			return null;
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < len; i++) {
			result.append(CODE_NUM[RANDOM.nextInt(CODE_NUM.length)]);
		}
		return result.toString();
	}

	/**
	 * 获取随机颜色
	 * 
	 * @return
	 */
	public static Color getRandomColor() {
		int red = RANDOM.nextInt(255);
		int green = RANDOM.nextInt(255);
		int blue = RANDOM.nextInt(255);
		Color color = new Color(red, green, blue);
		return color;
	}

	/**
	 * 将数组a和数组b合并，返回合并后的新数组
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static Object[] addTwoArray(Object[] a, Object[] b) {
		if (null == a) {
			if (null == b) {
				return null;
			} else {
				return b;
			}
		} else {
			if (null == b) {
				return a;
			} else {
				Object temp[] = new Object[a.length + b.length];
				for (int i = 0; i < a.length; i++) {
					temp[i] = a[i];
				}
				for (int i = 0; i < b.length; i++) {
					temp[a.length + i] = b[i];
				}
				return temp;
			}
		}
	}
}
