package class11;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ExceptionDemo {
	public static void main(String[] args) throws Exception
	{
		int a = 10, b = 0, c = 0;
		Scanner scanner = new Scanner(System.in);
		System.out.println("������ѧ����ĩ�ɼ�");
		b = scanner.nextInt();
		if (b >= 60)
			throw (new MarkException());
		c = a / b;

		System.out.println("Hello world");
	}
}

class MarkException extends Exception {
	public MarkException() {
		super("��������");
	}
}
