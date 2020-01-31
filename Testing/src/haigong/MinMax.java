package haigong;

import java.util.Scanner;

public class MinMax {

	public static void main(String[] args) {
		int[] arr = new int[10];
		Scanner scanner = new Scanner(System.in);
		for (int i = 0; i < arr.length; i++) {
			arr[i] = scanner.nextInt();
		}
		int max = arr[0]; 
		int min = arr[0];
		for (int i = 0; i < arr.length; i++) {
			if(arr[i]> max)
				max = arr[i];
			if(arr[i] < min)
				min = arr[i];
		}
		System.out.println(max);
		System.out.println(min);

	}

}
