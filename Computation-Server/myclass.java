
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

import org.json.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;

public class myclass {
	
	public static double [] longitude = new double[1];
	public static double [] latitude = new double[1];
	public static String DriverID;
	public static int ToNED;

	public static void main(String[] args) throws Exception {
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		ServerSocket ss = new ServerSocket(4056);
		while(true) {
			Socket s1 = null;
			try {
			s1 = ss.accept();
			PrintStream p = new PrintStream(s1.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(s1.getInputStream(),"utf-8"));		
			Runnable T1 = new myThread(in,p,s1);
			executorService.execute(T1);
		
			} catch (Exception e) {
			// TODO Auto-generated catch block
			ss.close();
			e.printStackTrace();
			}
	
		}
	}	
}
