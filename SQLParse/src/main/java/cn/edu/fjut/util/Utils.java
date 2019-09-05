package cn.edu.fjut.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import cn.edu.fjut.bean.ExerciseSubmission;

public class Utils {
	
	/**
	 * 取两个集合的交集,与默认的retainAll相比，该方法允许存在重复的元素
	 * @param nodes1
	 * @param nodes2
	 * @return
	 */
	public static List<String> calIntersec(List<String> nodes1, List<String> nodes2)
	{		
		
		List<String> temp = new ArrayList<>(nodes2);
		List<String> result = new ArrayList<>();
		for (String node : nodes1) {
			if(temp.contains(node))
			{
				result.add(node);
				temp.remove(node);
			}
		}
		
		return result;
	}
	
	/**取两个集合的差集, 
	 * 
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static List<String> calDiff(List<String> list1, List<String> list2) {
		List<String> temp = new ArrayList<>(list1);
		for (String node : list2) {
			temp.remove(node);			
		}
		return temp;
	}
	
	public static String Standardize(String query)
	{
		
		return query;
	}
	
	public static String removeBlank(String text)
	{
		text = text.replaceAll("[\r\n\t]+", " ");
		text = text.replaceAll("\\s{2,}", " ");
		return text;
	}
	
	/**
	 * 得到两个列表的排列组合，
	 * 例如 list1中有 a, b, c, list2中有1,2，3, 4 则可得< 
	 * <<a,1>, <b，２>, <c,3>>, 
	 * < <a,1>, <b,2>, <c, 4>>,
	 * <<a,1>, <b,3> < c, 2>>,
	 * <<a, 1>, <b, 3>, <c, 4>>
	 * <<a, 2>, < b,1 >,<c,3>>
	 * >
	 * @param list1
	 * @param list2
	 * @return
	 */
	public List<List> getPermutations(List list1, List list2)
	{
		List<List> result = new ArrayList<>();
		
		return result;
	}
	
	
	
	public static void main(String[] args) {
		List<String> list1 = new ArrayList<>();
		List<String> list2 = new ArrayList<>();
		list1.add("a");
		list1.add("a");
		list1.add("a");
		list1.add("b");
		list1.add("c");
		list1.add("e");
		
		list2.add("a");
		list2.add("a");
		list2.add("c");
		list2.add("d");
		
		
		System.out.println(calIntersec(list2, list1));
		System.out.println(calDiff(list1, list2));
		System.out.println(calDiff(list1, list2));
		
	}


}
