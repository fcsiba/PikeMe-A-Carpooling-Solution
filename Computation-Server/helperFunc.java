import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

public class helperFunc {
	public String RollBatch;
	public static double longitude;
	public static double latitude;
	public static long duration;
	public static long small = 1000000000;
	

	public static String DriverID_smallest = "";
	public static double longitude_smallest = 0;
	public static double latitude_smallest = 0;
	public static int ToNED_smallest = 0;
	
	
	
	public static String URL_LINK = "";
	public static String URL_LINK2 = "";
	public static double NEDlat = 24.930143;
	public static double NEDlongi = 67.115498;
	public static String RetJson;
	public static double getUserDest(String RollBatch) {
		return 0.0;
	}
	
	public static double getUserSource(String RollBatch) {
		return 0.0;
	}
	
	public static synchronized int Check_Driver(String UserID, String DriverID, double longitude_u, double latitude_u, int ToNED) throws Exception {
		System.out.println("Now starting sync code");
		String url1 = "https://introjected-knots.000webhostapp.com/android/v1/USRREQNEW.php";
		URL obj = new URL(url1);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		PrintStream ps = new PrintStream(con.getOutputStream());
		ps.print("&UserID=" + UserID);
	    ps.print("&DriverID=" + DriverID);               
	    ps.print("&longitude_U=" + longitude_u);
	    ps.print("&latitude_U=" + latitude_u);
	    ps.print("&ToNED=" + ToNED);
//		int responseCode = con.getResponseCode();
//		System.out.println("\nSending GET request to url: " + url);
//		System.out.println("Response code: " + responseCode);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));
		String InputLine;
		StringBuffer response = new StringBuffer();
		while((InputLine = in.readLine())!= null){
			response.append(InputLine + "\n");				
			}
		in.close();
		System.out.println(response.toString());
		JSONObject obj_chck = new JSONObject(response.toString());
		int code = obj_chck.getInt("main");
	
		return code;
	}
	public static String GetJson(String Url) throws Exception{
		String url = Url;
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		int responseCode = con.getResponseCode();
//		System.out.println("\nSending GET request to url: " + url);
//		System.out.println("Response code: " + responseCode);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));
		String InputLine;
		StringBuffer response = new StringBuffer();
		while((InputLine = in.readLine())!= null){
			response.append(InputLine + "\n");				
			}
		in.close();
		return response.toString();
	}
	public static String getURL(double latorigin, double longiorigin, double [] waypoints_lat, double [] waypoints_long, double latdest, double longidest) throws Exception{
		
	

	        // Sensor enabled
	        String sensor = "sensor=false";
	        String mode = "mode=driving";
	        String str_waypoints = "";
	        
	        if(waypoints_lat != null){
	            for(int i = 0;i < waypoints_lat.length;i++) {
	                if(str_waypoints == ""){
	                    str_waypoints = "&waypoints=" + waypoints_lat[i] + "," + waypoints_long[i];
	                }
	                else{
	                    str_waypoints += "%7C" + waypoints_lat[i] + "," + waypoints_long[i];
	                }

	            }
	        }
	


        // Origin of route
        String str_origin = "&origin=" + latorigin + "," + longiorigin;

        // Destination of route
        String str_dest = "destination=" + latdest + "," + longidest;
        
       
        
        String key="&key=AIzaSyDE7J9-t_Vl7sMTu6cU0d3bYE06R5qJCAQ";
        
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode + str_waypoints + key;
        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

	public static String getLATLNG(double [] lat, double [] longi,int toNED) throws Exception{
		duration = 0;
		small = 1000000000;
		longitude_smallest = 0;
		latitude_smallest = 0;
		ToNED_smallest = 0;
		URL_LINK = "";
		URL_LINK2 = "";
		DriverID_smallest = null;
		
		
		String url = "https://introjected-knots.000webhostapp.com/android/v1/driverids.php?&inp=" + toNED;
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
//		int responseCode = con.getResponseCode();
//		System.out.println("\nSending GET request to url: " + url);
//		System.out.println("Response code: " + responseCode);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));
		String InputLine;
		StringBuffer response = new StringBuffer();
		while((InputLine = in.readLine())!= null){
			response.append(InputLine + "\n");				
			}
		in.close();
//		System.out.print(response.toString());
		
		JSONArray res = new JSONArray(response.toString());
		
		if(res.length() == 0) {
			DriverID_smallest = "Driver not found in broadcast";
		}
		
		for (int i =0; i < res.length(); i++) {
			
			JSONObject obj1 = res.getJSONObject(i);
			int ToNED_Driver_DATA = obj1.getInt("ToNED");
			String ID = obj1.getString("DriverID");
			latitude = datahandler.get_lat(obj1.toString());
			longitude = datahandler.get_long(obj1.toString());
			System.out.println("NOW Checking " + ID + " With To NED " + ToNED_Driver_DATA + "LAT/LONG" + latitude + "/" + longitude);
			if(ToNED_Driver_DATA == 1) {
				URL_LINK = getURL(latitude, longitude, lat, longi, NEDlat, NEDlongi);
				System.out.println(URL_LINK);
				RetJson = GetJson(URL_LINK);
				duration = datahandler.get_duration(RetJson);
				//System.out.println(duration);
				if(duration < small) {
					
					small = duration;
					DriverID_smallest = ID;
					latitude_smallest = latitude;
					longitude_smallest = longitude;
					System.out.println( ID + " is the smallest Currently with duration " + small); 
//					System.out.print(small);
//					System.out.println(DriverID);
				}	
			}
			else if(ToNED_Driver_DATA == 0) {
				URL_LINK = getURL(NEDlat, NEDlongi, lat, longi, latitude, longitude);
				System.out.println(URL_LINK);
				RetJson = GetJson(URL_LINK);
				duration = datahandler.get_duration(RetJson);
//				System.out.println(duration);
				if(duration < small) {
					small = duration;
					DriverID_smallest = ID;
					ToNED_smallest = ToNED_Driver_DATA;
					latitude_smallest = latitude;
					longitude_smallest = longitude;
					System.out.println( ID + " is the smallest Currently with duration " + small);
//					System.out.print(small);
//					System.out.print(DriverID);

			
				}
		
			}
	
		}
		
		if(ToNED_smallest == 0) {
			lat = null;
			longi = null;
			URL_LINK2 = getURL(NEDlat, NEDlongi, lat, longi, latitude_smallest, longitude_smallest);
			//System.out.println(URL_LINK2);
			RetJson = GetJson(URL_LINK2);
			duration = datahandler.get_duration(RetJson);
		}
		else {
			lat = null;
			longi = null;
			URL_LINK2 = getURL(latitude_smallest, longitude_smallest, lat, longi, NEDlat, NEDlongi);
			//System.out.println(URL_LINK2);
			RetJson = GetJson(URL_LINK2);
			duration = datahandler.get_duration(RetJson);
		}
		if((small-duration) < 600) {
			/*System.out.print(DriverID_smallest);
			System.out.print("/");
			System.out.print(small);
			System.out.print("/");
			System.out.print(small-duration);
			System.out.print("/");
			System.out.print(duration);*/
			return DriverID_smallest;
		}
		else {
			/*System.out.print(DriverID_smallest);
			System.out.print("/");
			System.out.print(small);
			System.out.print("/");
			System.out.print(small-duration);
			System.out.print("/");
			System.out.print(duration);*/
			return "40412";
		}
		
	}
}
	

