import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class FixLatLong {

	public ArrayList<String> getCompCodeFromCompLoc() {
		ArrayList<String> codes = new ArrayList<String>();
		String query = "select comp_code from comp_loc;";
		Connection con = DBConnect.getConnection();
		try {
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery(query);
			if (rs != null) {
				while (rs.next()) {
					codes.add(rs.getString("comp_code"));
				}
			}
			s.close();
		} catch (SQLException e) {
			System.err.println("in findByID " + e.getMessage());

		}
		return codes;
	}

	public ArrayList<Integer> getCompIdsFromCompanies(ArrayList<String> comp_codes) {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		String query = "select company_id from company where company_code ='";
		Connection con = DBConnect.getConnection();
		try {
			for (String code : comp_codes) {
				Statement s = con.createStatement();
				ResultSet rs = s.executeQuery(query + code + "';");
				if (rs != null) {
						System.out.println("getting an id");
						ids.add(rs.getInt("company_id"));
				}
				s.close();
			}
		} catch (SQLException e) {
			System.err.println("in findByID " + e.getMessage());

		}
		return ids;
	}
}
