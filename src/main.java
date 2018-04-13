import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

public class main {
	  
	public static void main(String[] args) throws IOException {
		Random rand = new Random();
		
		int r = rand.nextInt(100) + 1;
		
		System.out.println(r);
	}

}