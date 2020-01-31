package haigong;

import java.util.Scanner;



public class Func {

	public static void main(String[] args) {
		int x,y;
		Scanner scanner = new Scanner(System.in);
		x = scanner.nextInt();
		if(x <0)
			y = x;
		else if (x >= 0 && x < 10)
			y = 3*x -2;
		else
			y = 4*x;
		System.out.println(y);

	}

}
