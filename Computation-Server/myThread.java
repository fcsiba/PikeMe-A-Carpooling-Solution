import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

import org.json.JSONObject;

public class myThread implements Runnable {
	double [] lat;
	double [] longi;
	PrintStream give;
	BufferedReader in;
	Socket s;
	int ToNED;
	String DriverID;
	int D_check = 402;
	int D_success = 102;
	

	
	public myThread(BufferedReader B, PrintStream p, Socket s1) {
		this.in = B;
		this.give = p;
		this.s = s1;
	}
	
	
	public void run() {
		int code;
		try {
			System.out.println("new thread made");
			this.lat = new double[1];
			this.longi = new double[1];
			String InputLine;
			StringBuffer response = new StringBuffer();
	
			InputLine = in.readLine();
			response.append(InputLine + "\n");		
		
			System.out.print(response.toString());
	
			JSONObject obj = new JSONObject(response.toString().trim());
			//System.out.println(obj);
	
			String UserID = obj.getString("UserID");
			this.lat[0]= obj.getDouble("latitude");
			this.longi[0] = obj.getDouble("longitude");
			ToNED = obj.getInt("ToNED");
	
	
			DriverID = helperFunc.getLATLNG(lat, longi, ToNED);
			System.out.println(DriverID);
			if(DriverID != "40412") {
				System.out.println("Driver found now checking code");
			code = helperFunc.Check_Driver(UserID, DriverID, longi[0], lat[0], ToNED);
			if(code == 402 || code == 401) {
				give.println("40413");
			}else {
				give.println(DriverID);
			}
		}else {
			give.println("40412");		
		}
	} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		
		try {
			this.in.close();
			this.give.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

