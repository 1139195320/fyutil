package com.fy.util;

import java.util.Date;
import java.util.LinkedList;

public class MathUtil {

	/**
	 * 
	 * @Description <p>返回两个数乘积的结果，该两数可以很大</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年7月4日 下午2:48:59</p> 
	 * @author <p>fangyang</p>
	 *
	 * @param num1 数1字符串（整数）
	 * @param num2 数2字符串（整数）
	 * @param isLogTime 是否打印耗时日志
	 * @return num1数与num2数的乘积结果
	 */
	public static String multiply(String num1, String num2, boolean isLogTime) {
		Long time1 = new Date().getTime();
		if (null == num1 || null == num2)
			return "0";
		String result = "";
		LinkedList<Integer> list_num1 = new LinkedList<Integer>();
		LinkedList<Integer> list_num2 = new LinkedList<Integer>();
		LinkedList<Integer> list_result = new LinkedList<Integer>();
		try {
			for (int i = 0; i < num1.length(); i++) {
				list_num1.addFirst(Integer.valueOf(num1.charAt(i) + ""));
			}
			for (int i = 0; i < num2.length(); i++) {
				list_num2.addFirst(Integer.valueOf(num2.charAt(i) + ""));
			}
		} catch (NumberFormatException e) {
			return "0";
		}
		int now = 0;
		int temp2 = 0;
		int temp3 = 0;
		while (now != (list_num1.size() + list_num2.size() - 1)) {
			int temp1 = 0;
			for (int i = 0; i < list_num1.size(); i++) {
				for (int j = 0; j < list_num2.size(); j++) {
					if (now == (i + j)) {
						temp1 += list_num1.get(i) * list_num2.get(j);
					}
				}
			}
			temp1 += temp2;
			temp3 = temp1 % 10;
			temp2 = temp1 / 10;
			list_result.addFirst(temp3);
			now++;
		}
		if (temp2 != 0)
			list_result.addFirst(temp2);
		int k = 0;
		for (; k < list_result.size(); k++) {
			if (list_result.get(k) != 0)
				break;
		}
		for (int i = k; i < list_result.size(); i++) {
			result += list_result.get(i);
		}
		if (result.equals(""))
			result = "0";
		Long time2 = new Date().getTime();
		if(isLogTime)
			System.out.println("计算：" + num1 + " × " + num2 + "\n耗时：" + (time2 - time1) + "ms");
		return result;
	}
}
