package cn.edu.fjut;

import java.util.List;

public class Test {

	  public static void main(String[] args) {
		  String str = "8929;select production_year, count(*) from movie where; production_year > 1990 and production_year < 1994 group by production_year;";
		  System.out.println(str.matches("^\\d*;[\\S\\s]+;[\\S\\s]+;$"));
		  
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
