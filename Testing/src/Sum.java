import java.util.Scanner;


public class Sum {
  public static void main (String [] args) {
	  int [] a = new int [10];
	  int i, max,min;
	  Scanner input =new Scanner (System.in);
	  for(i=0;i<a.length;i++);
	  a[i]=input .nextInt();
	  max=a[i];
	  for(i=1;i<a.length;i++) {
		  if(max<a[0]) max=a[i];
		 
		  if(max>a[i]) min=a[i];
	  }
  System.out.printf("最大数字是:%d,最小数字是：%d,max,min");
  }
}