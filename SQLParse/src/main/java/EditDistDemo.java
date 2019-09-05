import java.util.HashSet;
import java.util.Set;

public class EditDistDemo
{

	public static void main(String[] args)
	{
		Set<String> A = new HashSet<>();
		Set<String> B = new HashSet<>();
		for(int i=0; i< 5; i++)
		{
			A.add(String.valueOf( i));
			B.add(String.valueOf( i));
		}
		A.add("A");
		A.add("B");
		B.add("+");		
		
		Set<String> intersection = new HashSet<>(A);		
		intersection.retainAll(B);
		System.out.println(intersection);
		Set<String> diff = new HashSet<>(A);		
		diff.removeAll(B);
		System.out.println(diff);
		
	}

}
