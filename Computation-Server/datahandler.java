import org.json.JSONArray;
import org.json.JSONObject;


public class datahandler {
	
	public static double get_lat(String response) throws Exception {
		JSONObject obj = new JSONObject(response);
		String lat = obj.getString("latitude");
	
		return Double.parseDouble(lat);
	}
	
	public static double get_long(String response) throws Exception {
		JSONObject obj = new JSONObject(response);
		String longitude = obj.getString("longitude");
		
		return Double.parseDouble(longitude);
		
	}
	
	public static int get_duration(String response) throws Exception {
		int duration = 0;
		JSONObject res = new JSONObject(response);
		JSONArray arr1 = res.getJSONArray("routes");
		JSONObject res1 = arr1.getJSONObject(0);
		JSONArray arr2 = res1.getJSONArray("legs");
		for (int i = 0; i< arr2.length(); i++) {
		JSONObject res2 = arr2.getJSONObject(i);
		JSONObject res3 = new JSONObject(res2.get("duration"));
		System.out.println(res2.getJSONObject("duration"));
		Integer time1 = res2.getJSONObject("duration").getInt("value");
		//String time = res3.getString("value");
		duration += time1;
		}
		return (duration);
	}
	public static double get_distance(String response) {
		return 0.0;
	}

}
