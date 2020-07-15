/*import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class server {

	public static void main(String[] args) throws IOException {
		ExecutorService executorService = Executors.newFixedThreadPool(5);

		double [] number = {0,0};
		int ToNED =1;
		PrintStream p;
		ServerSocket ss = new ServerSocket(4041);
		Socket s1 = ss.accept();
		p = new PrintStream(s1.getOutputStream());
		Scanner sc = new Scanner(s1.getInputStream());
		for(int i =0 ; i<2; i++) {
			number[i] = sc.nextDouble();
		}
		Runnable T1 = new myThread(number[0], number[1], ToNED, p, s1);
		executorService.execute(T1);
		
		
		}

	}*/


