package store.javac.fyutil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringsUtil {

	/**
	 * 获取一段话中某个单词所有出现的位置
	 * @param parentStr 母字符串
	 * @param splitStr 切割字符串
	 * @param targetWord 目标单词
	 * @param isIgnoreCase 是否忽略大小写
	 * @return
	 */
	public static List<Integer> findWordFromString(String parentStr , String splitStr , String targetWord , boolean isIgnoreCase){
		List<Integer> result = new ArrayList<>();
		if(null != parentStr && null != splitStr && null != targetWord) {
			if(isIgnoreCase) {
				parentStr = parentStr.toLowerCase();
			}
			String arr[] = parentStr.split(splitStr);
			Integer index = 0;
			String word = null;
			for(int i = 0 ; i < arr.length ; i ++) {
				word = arr[i];
				if(word.equals(targetWord)) {
					result.add(index);
				}
				index += word.length() + splitStr.length();
			}
		}
		return result;
	}
	
	/**
	 * 统计一段话中各个单词的数量
	 * @param parentStr 母字符串
	 * @param splitStr 切割字符串
	 * @param isIgnoreCase 是否忽略大小写
	 * @return
	 */
	public static Map<String , Integer> countWordInString(String parentStr , String splitStr , boolean isIgnoreCase){
		Map<String, Integer> result = new HashMap<>();
		if(null != parentStr && null != splitStr) {
			if(isIgnoreCase) {
				parentStr = parentStr.toLowerCase();
			}
			String []arr = parentStr.split(splitStr);
			String key = null;
			Integer val = 1;
			for(int i = 0 ; i < arr.length ; i ++) {
				key = arr[i];
				val = 1;
				if(result.containsKey(key)) {
					val = result.get(key) + 1;
				}
				result.put(key, val);
			}
		}
		return result;
	}
	
	/**
	 * 统计一个字符串中各个字符的个数
	 * @param targetStr 目标字符串
	 * @return
	 */
	public static Map<Character, Integer> countCharacterInString(String targetStr){
		Map<Character, Integer> result = new HashMap<>();
		Character key = null;
		Integer val = 1;
		if(null != targetStr) {
			for(int i = 0 ; i < targetStr.length() ; i ++) {
				key =targetStr.charAt(i);
				val = 1;
				if(result.containsKey(key)) {
					val = result.get(key) + 1;
				}
				result.put(key, val);
			}
		}
		return result;
	}
	
	/**
	 * 获取一个字符串中某个字符所有出现的位置
	 * @param parentStr 母字符串
	 * @param targetC 目标字符
	 * @return
	 */
	public static List<Integer> findCharacterFromString(String parentStr , Character targetC) {
		List<Integer> result = new ArrayList<>();
		if(null != parentStr && null != targetC) {
			char []arr = parentStr.toCharArray();
			for(int i = 0 ; i < arr.length ; i ++) {
				if(arr[i] == targetC) {
					result.add(i);
				}
			}
		}
		return result;
	}
}
