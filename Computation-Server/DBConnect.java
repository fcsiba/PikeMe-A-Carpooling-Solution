import java.sql.*;
public class DBConnect {

	private Connection con;
	private Statement st;
	private ResultSet rs;
	
	public DBConnect() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/id8797211_android","id8797211_hasnain","123456789");
			st = con.createStatement();
			
		}catch(Exception e) {
			System.out.println("Error :"+e);
		}
	}
	
}
