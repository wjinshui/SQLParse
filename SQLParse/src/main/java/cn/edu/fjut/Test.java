package cn.edu.fjut;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Test {

	public static void main(String[] args) {
		Set<Set<Object>> seta = new HashSet<Set<Object>>();
		Set<Set<Object>> setb = new HashSet<Set<Object>>();
		Set<Object> set1 = new HashSet<Object>() ;		
		Set<Object> set2 = new HashSet<Object>() ;
		Set<Object> set3 = new HashSet<Object>() ;		
		Set<Object> set4 = new HashSet<Object>() ;
		
		set1.add("afd");
		set1.add("123");
		
		set3.add("1234");
		set3.add("abcde");
		
		set4.add("abcde");
		set4.add("1234");
		
		
		set2.add("123");
		set2.add("afd");
		seta.add(set1);
		seta.add(set3);
		seta.add(set4);
		
		
		setb.add(set4);
		setb.add(set2);
		
		System.out.println(seta.equals(setb));
		
		
		
		
		

	}

	private static void equalHashMap() {
		HashMap<Integer, Integer> test1 = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> test2 = new HashMap<Integer, Integer>();
		test1.put(1, 10);
		test1.put(2, 20);
		test1.put(3, 30);
		
		test2.put(3, 30);
		test2.put(1, 10);
		test2.put(2, 20);
		
		System.out.println(test1.equals(test2));
	}

}
