import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class TestDb {

	// returns all bookings
	public void getBusinessForms() {
		ArrayList<String> names = new ArrayList<String>();
		Connection con = DBConnect.getConnection();
		String query = "SELECT * from business_form";
		try {
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery(query);
			if (!(!rs.isBeforeFirst() && rs.getRow() == 0)) {
				while (rs.next()) {
					System.out.println("got an entry");
					names.add(rs.getString("business_form_text"));
				}
			}
			s.close();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		for(String s: names)
			System.out.print(s + " ");
	}

}
