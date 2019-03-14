package cn.edu.fjut;

import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

import cn.edu.fjut.ast.MySQLParse;
import cn.edu.fjut.bean.SQLTree;
import cn.edu.fjut.bean.SQLTreeNode;

public class Test {

	  public static void main(String[] args) {
		  
		  
	        List<Integer> list1 = new ArrayList<>();
	        list1.add(0);
	        list1.add(1);
	        list1.add(2);
	        List<Integer> list2 = new ArrayList<>();
	        list2.add(3);
	        list2.add(4);
	        list2.add(5);

	        List<List<Integer>> allList = new ArrayList<>();
	        allList.add(list1);
	        allList.add(list2);

//	        calculateCombination(allList, 0, new int[allList.size()]);  
	    }
	
	
	/** * 写法二，递归计算所有组合 * @param inputList 所有数组的列表，数组用List<Integer>存储 * @param beginIndex 代表每一个数组的在inputList中的索引 * @param arr 用于保存每一次递归生成的组合 * */
    public static void  calculateCombination(List<List<Integer>> inputList, int beginIndex, int[] arr) {
        if(beginIndex == inputList.size()){
            //在这里进行你自己的处理，比如打印组合的结果
            for (int i : arr) {
                System.out.print(i+", ");
            }
            System.out.println();
            return;
        }
        for(int c: inputList.get(beginIndex)){
            arr[beginIndex] = c;
            calculateCombination(inputList, beginIndex + 1, arr);
        }
    }



}
