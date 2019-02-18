package cn.edu.fjut;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.alibaba.druid.sql.ast.expr.SQLExprUtils;

import cn.edu.fjut.util.SQLHelper;

public class Test {

	public static void main(String[] args) {
		SQLHelper help = new SQLHelper();
		String string = help.getFormatSQL("select first_name,last_name,title,production_year,description from person a,role b where a.id=b.id and a.id in( select a.id from person a,role b where a.id=b.id group by a.id having count(*)=(select max(cnt) from (select a.id ,count(*) cnt from person a,role b where a.id=b.id group by a.id)) );");
		System.out.println(string);
				

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
