package com.fy.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * 
 * @Description <p>剪贴板操作工具类</p>
 * @version <p>v1.0</p>
 * @Date <p>2018年7月4日 下午2:55:13</p> 
 * @author <p>fangyang</p>
 *
 */
public class ClipboardUtil {
	
	/**
	 * 
	 * @Description <p>获取系统剪切板内容[剪切板中内容为图片型]</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年7月4日 下午2:55:36</p> 
	 * @author <p>fangyang</p>
	 *
	 * @return
	 */
	public static Image getImageFromClipboard() {
		Image image = null;
		Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		try {
			if (t != null && t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
				image = (Image) t.getTransferData(DataFlavor.imageFlavor);
			}
		} catch (Exception e) {
			throw new RuntimeException("在此 flavor中不支持所请求的数据！");
		}
		return image;
	}
	
	/**
	 * 
	 * @Description <p>设置系统剪切板内容[内容为图片型]</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年7月4日 下午2:55:46</p> 
	 * @author <p>fangyang</p>
	 *
	 * @param image
	 */
	public static void setImageToClipboard(Image image) {
		Transferable trans = new Transferable() {
			@Override
			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
				if (isDataFlavorSupported(flavor)) {
					return image;
				}
				throw new UnsupportedFlavorException(flavor);
			}

			@Override
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[] { DataFlavor.imageFlavor };
			}

			@Override
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return DataFlavor.imageFlavor.equals(flavor);
			}
		};
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
	}
	
	/**
	 * 
	 * @Description <p>设置系统剪切板内容[内容为文本型]</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年7月4日 下午2:56:02</p> 
	 * @author <p>fangyang</p>
	 *
	 * @param content
	 */
	public static void setStringToClipboard(String content) {
		content = content.trim();
		StringSelection ss = new StringSelection(content);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(ss, null);
	}
	
	/**
	 * 
	 * @Description <p>获取系统剪切板内容[剪切板中内容为文本型]</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年7月4日 下午2:56:15</p> 
	 * @author <p>fangyang</p>
	 *
	 * @return
	 */
	public static String getStringFromClipboard() {
		String result = "";
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable t = clipboard.getContents(null);
		try {
			if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				result = t.getTransferData(DataFlavor.stringFlavor).toString();
			}
		} catch (Exception e) {
			throw new RuntimeException("在此 flavor 中不支持所请求的数据！");
		}
		return result;
	}
}
