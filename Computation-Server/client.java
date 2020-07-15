import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class client {

	public static void main(String args[]) throws Exception{
		double [] number = {0,0};
		String ID;
		
		PrintStream p ;
		Scanner sc = new Scanner(System.in);
		Socket s = new Socket("127.0.0.1",4041);
		Scanner sc1 = new Scanner(s.getInputStream());
			for(int i =0 ; i<2; i++) {
			number[i] = sc.nextDouble();
			p = new PrintStream(s.getOutputStream());
			p.println(number[i]);
			}
			
				ID = sc1.next();
				System.out.println(ID);
			
		
		
	}
}
