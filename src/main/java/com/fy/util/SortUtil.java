package com.fy.util;

public class SortUtil {
	

	private SortUtil() {}
	
	public static String sort(String str , boolean isAsc) {
		return new String(sortChar(str.toCharArray(), isAsc));
	}
	
	public static char[] sort(char[] arr, boolean isAsc) {
		return sortChar(arr, isAsc);
	}
	
	public static int[] sort(int[] arr , boolean isAsc) {
		return binarySort(arr, isAsc);
	}
	
	public static float[] sort(float[] arr , boolean isAsc) {
		for (int i = 1; i < arr.length; i++) {
			float temp = arr[i];
			int l = 0;
			int r = i - 1;
			int mid = -1;
			while (l <= r) {
				mid = l + (r - l) / 2;
				if ((arr[mid] > temp && isAsc) || (arr[mid] < temp && !isAsc)) {
					r = mid - 1;
				} else { 
					// 元素相同时，也插入在后面的位置
					l = mid + 1;
				}
			}
			for (int j = i - 1; j >= l; j--) {
				arr[j + 1] = arr[j];
			}
			arr[l] = temp;
		}
		return arr;
	}
	
	public static double[] sort(double[] arr , boolean isAsc) {
		for (int i = 1; i < arr.length; i++) {
			double temp = arr[i];
			int l = 0;
			int r = i - 1;
			int mid = -1;
			while (l <= r) {
				mid = l + (r - l) / 2;
				if ((arr[mid] > temp && isAsc) || (arr[mid] < temp && !isAsc)) {
					r = mid - 1;
				} else { 
					// 元素相同时，也插入在后面的位置
					l = mid + 1;
				}
			}
			for (int j = i - 1; j >= l; j--) {
				arr[j + 1] = arr[j];
			}
			arr[l] = temp;
		}
		return arr;
	}

	/**
	 * 
	 * @Description <p>排序字符串</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年6月28日 上午9:11:46</p> 
	 * @author <p>fangyang</p>
	 *
	 * @param str 要排序字符的字符串
	 * @param isAsc 是否顺序（从小到大）
	 * @return 排序后的字符串
	 */
	public static String sortStr(String str , boolean isAsc) {
		return new String(sortChar(str.toCharArray(), isAsc));
	}
	
	/**
	 * 
	 * @Description <p>排序字符数组</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年6月28日 上午9:11:46</p> 
	 * @author <p>fangyang</p>
	 *
	 * @param arr 字符数组
	 * @param isAsc 是否顺序（从小到大）
	 * @return 排序后的字符数组
	 */
	public static char[] sortChar(char[] arr , boolean isAsc) {
		for (int i = 1; i < arr.length; i++) {
			char temp = arr[i];
			int l = 0;
			int r = i - 1;
			int mid = -1;
			while (l <= r) {
				mid = l + (r - l) / 2;
				if ((arr[mid] > temp && isAsc) || (arr[mid] < temp && !isAsc)) {
					r = mid - 1;
				} else { 
					// 元素相同时，也插入在后面的位置
					l = mid + 1;
				}
			}
			for (int j = i - 1; j >= l; j--) {
				arr[j + 1] = arr[j];
			}
			arr[l] = temp;
		}
		return arr;
	}
	
	/**
	 * 
	 * @Description <p>选择排序</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年6月27日 上午11:58:14</p> 
	 * @author <p>fangyang</p>
	 *
	 * @param arr 要排序的数组
	 * @param isAsc 是否顺序排序（从低到高）
	 * @return 排序之后的数组
	 */
	public static int[] choiseSort(int[]arr , boolean isAsc) {
		int[]temp_arr;
		for(int i = 0 ; i < arr.length - 1 ; i++) {
			int index = i;
			int temp = arr[i];
			for(int j = i + 1 ; j < arr.length ; j++) {
				if((isAsc && arr[j] < temp) || (!isAsc && arr[j] > temp)) {
					index = j;
					temp = arr[j];
				}
			}
			if((isAsc && arr[i] > arr[index]) || (!isAsc && arr[i] < arr[index])) {
				temp_arr = swapTwoNum(arr[i], arr[index]);
				arr[i] = temp_arr[0];
				arr[index] = temp_arr[1];
			}
		}
		return arr;
	}
	
	/**
	 * 
	 * @Description <p>冒泡排序</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年6月27日 上午11:58:31</p> 
	 * @author <p>fangyang</p>
	 *
	 * @param arr 要排序的数组
	 * @param isAsc 是否顺序排序（从低到高）
	 * @return 排序之后的数组
	 */
	public static int[] bubbleSort(int[]arr , boolean isAsc) {
		int[]temp_arr;
		for(int i = 0 ; i < arr.length - 1 ; i++) {
			for(int j = 1 ; j < arr.length ; j++) {
				if((isAsc && arr[j] < arr[j - 1]) || (!isAsc && arr[j] > arr[j - 1])) {
					temp_arr = swapTwoNum(arr[j], arr[j - 1]);
					arr[j] = temp_arr[0];
					arr[j - 1] = temp_arr[1];
				}
			}
		}
		return arr;
	}

	/**
	 * 
	 * @Description <p>二分法排序（即插入排序）</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年6月27日 上午11:45:24</p> 
	 * @author <p>fangyang</p>
	 *
	 * @param arr 要排序的数组
	 * @param isAsc 是否顺序排序（从低到高）
	 * @return 排序之后的数组
	 */
	public static int[] binarySort(int[] arr , boolean isAsc) {
		for (int i = 1; i < arr.length; i++) {
			int temp = arr[i];
			int l = 0;
			int r = i - 1;
			int mid = -1;
			while (l <= r) {
				mid = l + (r - l) / 2;
				if ((arr[mid] > temp && isAsc) || (arr[mid] < temp && !isAsc)) {
					r = mid - 1;
				} else { 
					// 元素相同时，也插入在后面的位置
					l = mid + 1;
				}
			}
			for (int j = i - 1; j >= l; j--) {
				arr[j + 1] = arr[j];
			}
			arr[l] = temp;
		}
		return arr;
	}

	/**
	 * 
	 * @Description <p>交换两个数</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年6月27日 下午3:36:05</p> 
	 * @author <p>fangyang</p>
	 *
	 * @param a
	 * @param b
	 * @return 返回一个数组，[交换后的a，交换后的b]
	 */
	public static int[] swapTwoNum(int a, int b) {
		a += b;
		b = a - b;
		a -= b;
		return new int[] { a, b };
	}
}
