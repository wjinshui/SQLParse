package haigong;

import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		MyPoint myPoint = new MyPoint();
		Scanner scanner = new Scanner(System.in);
		int x = scanner.nextInt();
		int y = scanner.nextInt();
		myPoint.setX(x);
		myPoint.setY(y);
		myPoint.show();
	}
}

class MyPoint
{
	private int x, y;
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public void show()
	{
		System.out.println("(" + x + ", " + y + ")");
	}
}