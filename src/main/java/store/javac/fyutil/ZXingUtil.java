package store.javac.fyutil;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class ZXingUtil {
	
	public static final int QRCOLOR_DEFAULT = 0xFF000000;
	public static final int BGCOLOR_DEFAULT = 0xFFFFFFFF;

	public static final int WIDTH_DEFAULT = 400;
	public static final int HEIGHT_DEFAULT = 400;

	/**
	 * 二维码图形的颜色，默认是黑色
	 */
	private static int qrColor = QRCOLOR_DEFAULT;
	/**
	 * 二维码图片的背景颜色，默认是白色
	 */
	private static int bgColor = BGCOLOR_DEFAULT;

	/**
	 * 二维码图片宽，默认400
	 */
	private static int width = WIDTH_DEFAULT; 
	/**
	 * 二维码图片高，默认400
	 */
	private static int height = HEIGHT_DEFAULT;

	public static int getQrColor() {
		return qrColor;
	}

	public static void setQrColor(int qrC) {
		qrColor = qrC;
	}

	public static int getBgColor() {
		return bgColor;
	}

	public static void setBgColor(int bgC) {
		bgColor = bgC;
	}

	public static int getWidth() {
		return width;
	}

	public static void setWidth(int w) {
		width = w;
	}

	public static int getHeight() {
		return height;
	}

	public static void setHeight(int h) {
		height = h;
	}

	/*用于设置QR二维码参数*/
	private static Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>() {
		private static final long serialVersionUID = 1L;
		{
			put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);// 设置QR二维码的纠错级别（H为最高级别）具体级别信息
			put(EncodeHintType.CHARACTER_SET, "utf-8");// 设置编码方式
			put(EncodeHintType.MARGIN, 0);
		}
	};
	
	/**
	 * 
	 * @Description <p>解析二维码图片</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年7月3日 下午3:59:20</p> 
	 * @author <p>jack</p>
	 *
	 * @param qrCodeFilePath 二维码图片路径
	 * @return 含有二维码图片内容信息的集合（content，encode...）
	 * @throws FileNotFoundException 路径错误
	 */
	public static Map<String, String> getDataFromQrCodeFile(String qrCodeFilePath) throws FileNotFoundException {
		File codeFile = new File(qrCodeFilePath);
		if(null == qrCodeFilePath || "".equals(qrCodeFilePath.trim())) {
			throw new NullPointerException("codeFilePath不能为空！");
		}
		if(!codeFile.exists()) {
			throw new FileNotFoundException(qrCodeFilePath + "路径文件未找到！");
		}
		BufferedImage image;
		try {
			image = ImageIO.read(new File(qrCodeFilePath));
			LuminanceSource source = new BufferedImageLuminanceSource(image);
			Binarizer binarizer = new HybridBinarizer(source);
			BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
			Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>();
			hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
			Result result = new MultiFormatReader().decode(binaryBitmap, hints);// 对图像进行解码
			Map<String, String> data = new HashMap<>();
			data.put("content", result.getText());
			data.put("numbits", result.getNumBits() + "");
			//当前时间
			String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(result.getTimestamp()));
			data.put("time",  time);
			data.put("content", result.getText());
			data.put("encode", result.getBarcodeFormat().toString());
			return data;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 
	 * @Description <p>生成二维码图片</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年7月3日 下午3:57:40</p> 
	 * @author <p>jack</p>
	 *
	 * @param logoFilePath logo图片文件的路径（可选）
	 * @param codeFile 生成的二维码图片文件
	 * @param qrUrl 二维码内容
	 * @param note 二维码下面的文本描述（可选）
	 * @return 是否生成成功
	 * @throws Exception
	 */
	public static boolean drawLogoQRCode(String logoFilePath, File codeFile, String qrUrl, String note)
			throws Exception {
		if(null == codeFile) {
			throw new NullPointerException("codeFile不能为空！");
		}
		File pFile = codeFile.getParentFile();
		if(!pFile.exists()) {
			throw new FileNotFoundException(pFile.getAbsolutePath() + "路径不存在！");
		}
		if(codeFile.exists()) {
			throw new RuntimeException(codeFile.getAbsolutePath() + "路径有文件存在！");
		}
		return doDrawLogoQRCode(logoFilePath, codeFile, qrUrl, note);
	}

	/**
	 * 
	 * @Description <p>生成二维码图片</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年7月3日 下午3:57:40</p> 
	 * @author <p>jack</p>
	 *
	 * @param logoFilePath logo图片文件的路径（可选）
	 * @param codeFilePath 生成的二维码图片路径
	 * @param qrUrl 二维码内容
	 * @param note 二维码下面的文本描述（可选）
	 * @return 是否生成成功
	 * @throws Exception
	 */
	public static boolean drawLogoQRCode(String logoFilePath, String codeFilePath, String qrUrl, String note)
			throws Exception {
		File codeFile = new File(codeFilePath);
		if(null == codeFilePath || "".equals(codeFilePath.trim())) {
			throw new NullPointerException("codeFilePath不能为空！");
		}
		File pFile = codeFile.getParentFile();
		if(!pFile.exists()) {
			throw new FileNotFoundException(pFile.getAbsolutePath() + "路径不存在！");
		}
		if(codeFile.exists()) {
			throw new RuntimeException(codeFilePath + "路径有文件存在！");
		}
		return doDrawLogoQRCode(logoFilePath, codeFile, qrUrl, note);
	}

	private static boolean doDrawLogoQRCode(String logoFilePath, File codeFile, String qrUrl, String note) throws IOException, WriterException
			{
		File logoFile = null;
		if (null != logoFilePath) {
			logoFile = new File(logoFilePath);
		}
		if(null == qrUrl || "".equals(qrUrl.trim())) throw new NullPointerException("qrUrl不能为空");
		MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
		// 参数顺序分别为：编码内容，编码类型，生成图片宽度，生成图片高度，设置参数
		BitMatrix bm = multiFormatWriter.encode(qrUrl, BarcodeFormat.QR_CODE, width, height, hints);
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// 开始利用二维码数据创建Bitmap图片，分别设为黑（0xFFFFFFFF）白（0xFF000000）两色
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, bm.get(x, y) ? qrColor : bgColor);
			}
		}
		int width = image.getWidth();
		int height = image.getHeight();
		if (Objects.nonNull(logoFile) && logoFile.exists()) {
			// 构建绘图对象
			Graphics2D g = image.createGraphics();
			// 读取Logo图片
			BufferedImage logo = ImageIO.read(logoFile);
			// 开始绘制logo图片
			g.drawImage(logo, width * 2 / 5, height * 2 / 5, width * 2 / 10, height * 2 / 10, null);
			g.dispose();
			logo.flush();
		}
		// 自定义文本描述
		if (null != note && !"".equals(note.trim())) {
			// 新的图片，把带logo的二维码下面加上文字
			BufferedImage outImage = new BufferedImage(400, 445, BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D outg = outImage.createGraphics();
			// 画二维码到新的面板
			outg.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
			// 画文字到新的面板
			outg.setColor(Color.BLACK);
			outg.setFont(new Font("楷体", Font.BOLD, 30)); // 字体、字型、字号
			int strWidth = outg.getFontMetrics().stringWidth(note);
			if (strWidth > 399) {
				// 长度过长就截取前面部分
				// 长度过长就换行
				String note1 = note.substring(0, note.length() / 2);
				String note2 = note.substring(note.length() / 2, note.length());
				int strWidth1 = outg.getFontMetrics().stringWidth(note1);
				int strWidth2 = outg.getFontMetrics().stringWidth(note2);
				outg.drawString(note1, 200 - strWidth1 / 2, height + (outImage.getHeight() - height) / 2 + 12);
				BufferedImage outImage2 = new BufferedImage(400, 485, BufferedImage.TYPE_4BYTE_ABGR);
				Graphics2D outg2 = outImage2.createGraphics();
				outg2.drawImage(outImage, 0, 0, outImage.getWidth(), outImage.getHeight(), null);
				outg2.setColor(Color.BLACK);
				outg2.setFont(new Font("宋体", Font.BOLD, 30)); // 字体、字型、字号
				outg2.drawString(note2, 200 - strWidth2 / 2,
						outImage.getHeight() + (outImage2.getHeight() - outImage.getHeight()) / 2 + 5);
				outg2.dispose();
				outImage2.flush();
				outImage = outImage2;
			} else {
				outg.drawString(note, 200 - strWidth / 2, height + (outImage.getHeight() - height) / 2 + 12); // 画文字
			}
			outg.dispose();
			outImage.flush();
			image = outImage;
		}
		image.flush();
		return ImageIO.write(image, "png", codeFile);
	}

}
