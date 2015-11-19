import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

public class FixLatLong {

	public ArrayList<FieldContainer> getCompCodeFromCompLoc() {
		ArrayList<FieldContainer> codes = new ArrayList<FieldContainer>();
		String query = "select comp_code, lat, lng from comp_loc;";
		Connection con = DBConnect.getConnection();
		try {
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery(query);
			if (rs != null) {
				while (rs.next()) {
					FieldContainer cont = new FieldContainer();
					cont.setCompCode(rs.getString("comp_code"));
					cont.setLat(rs.getFloat("lat"));
					cont.setLng(rs.getFloat("lng"));
					codes.add(cont);
				}
			}
			s.close();
		} catch (SQLException e) {
			System.err.println("in findByID " + e.getMessage());

		}
		return codes;
	}

	public ArrayList<FieldContainer> getCompIdsFromCompanies(ArrayList<FieldContainer> comp_codes) {
		String query = "select company_id from company where company_code =? limit 1";
		Connection con = DBConnect.getConnection();
		try {
			for (int i = 0; i < comp_codes.size(); i++) {
				PreparedStatement s = con.prepareStatement(query);
				s.setString(1, comp_codes.get(i).getCompCode());
				ResultSet rs = s.executeQuery();
				if (rs.next()) {
					comp_codes.get(i).setCompId(rs.getInt("company_id"));
				}
				s.close();
			}
		} catch (SQLException e) {
			System.err.println("in getCompIds " + e.getMessage());
			e.printStackTrace();
		}
		return comp_codes;
	}

	public ArrayList<FieldContainer> getAddressIdsForCompanies(ArrayList<FieldContainer> list) {
		String query = "select ca_address_id from company_address where ca_company_id =? limit 1";
		Connection con = DBConnect.getConnection();
		try {
			int found = 0;
			for (int i = 0; i < list.size(); i++) {
				PreparedStatement s = con.prepareStatement(query);
				s.setInt(1, list.get(i).getCompId());
				ResultSet rs = s.executeQuery();
				if (rs.next()) {
					found++;
					System.out.println("number of found address ids is now " + found);
					list.get(i).setAddressId(rs.getInt("ca_address_id"));
				}
				s.close();
			}
		} catch (SQLException e) {
			System.err.println("in getCompIds " + e.getMessage());
			e.printStackTrace();
		}
		return list;
	}

	public void addOldLatLongs() {
		ArrayList<FieldContainer> foundComps = getCompCodeFromCompLoc();
		ArrayList<FieldContainer> foundCompIds = getCompIdsFromCompanies(foundComps);
		ArrayList<FieldContainer> foundaddrIds = getAddressIdsForCompanies(foundCompIds);
		insertOldLatLongs(foundaddrIds);
	}

	public void insertOldLatLongs(ArrayList<FieldContainer> container) {
		Connection connection = null;
		ArrayList<FieldContainer> uniques = new ArrayList<FieldContainer>();
		String query1 = "SELECT street,zip, district FROM address where address_id=?;";
		String query2 = "INSERT INTO address_with_location(street,zip,district,lat,lng)VALUES(?,?,?,?,?);";
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;

		HashSet<Integer> addressIds = new HashSet<Integer>();
		int setSize = addressIds.size();
		for (FieldContainer c : container) {
			System.out.println("the id is " + c.getAddressId());
			if (c.getAddressId() > 0) {
				addressIds.add(c.getAddressId());
				if (setSize < addressIds.size()) {
					uniques.add(c);
				}
				setSize = addressIds.size();
			}
		}
		System.out.println("there were " + container.size() + " entries and unique ones were " + uniques.size());
		try {
			connection = DBConnect.getConnection();
		} catch (Exception e) {
			System.err.println("There was an error getting the connection");
		}
		try {
			connection.setAutoCommit(false);
			System.err.println("The autocommit was disabled!");
		} catch (SQLException e) {
			System.err.println("There was an error disabling autocommit");
		}
		for (FieldContainer cont : uniques) {
			try {
				pstmt = connection.prepareStatement(query1);
				pstmt2 = connection.prepareStatement(query2);
				pstmt.setInt(1, cont.getAddressId());
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					pstmt2.setString(1, rs.getString("street"));
					pstmt2.setString(2, rs.getString("zip"));
					pstmt2.setString(3, rs.getString("district"));
					pstmt2.setFloat(4, cont.getLat());
					pstmt2.setFloat(5, cont.getLng());
					pstmt2.execute();
					connection.commit();
					System.err.println("The transaction was successfully executed");
				}
			} catch (SQLException e) {
				try {
					// We rollback the transaction, atomicity!
					connection.rollback();
					System.err.println(e.getMessage());
					System.err.println("The transaction was rollback");
				} catch (SQLException e1) {
					System.err.println("There was an error making a rollback");
				}
			}
		}
	}

	public Double[] getLatLong(String address) {
		GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyAAdfFpqk9vHsZFN4z6-3oqVUVpNI8khTM");
		GeocodingResult[] results = null;
		Double[] coding = new Double[2];
		try {
			results = GeocodingApi.geocode(context, address).await();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		coding[0] = results[0].geometry.location.lat;
		coding[1] = results[0].geometry.location.lng;
		return coding;
	}

}
