package haigong;

public class Shuiqianhua {

	public static void main(String[] args) {
		for (int i = 100; i < 1000; i++) {
			int hundreads = i / 100; // ��λ��
			int tens = (i - hundreads * 100) / 10;  //ʮλ��
			int units = i % 10;   //��λ��
			if(hundreads * hundreads * hundreads + tens * tens * tens + units * units * units == i)
				System.out.println(i);			
		}
	}
}
