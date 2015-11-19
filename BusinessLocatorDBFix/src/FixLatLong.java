import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
	
	public ArrayList<FieldContainer> getAddressIdsForCompanies(ArrayList<FieldContainer> list){
		String query = "select address_id from company_address where company_id =? limit 1";
		Connection con = DBConnect.getConnection();
		try {
			for (int i = 0; i < list.size(); i++) {
				PreparedStatement s = con.prepareStatement(query);
				s.setInt(1, list.get(i).getCompId());
				ResultSet rs = s.executeQuery();
				if (rs.next()) {
					list.get(i).setCompId(rs.getInt("address_id"));
				}
				s.close();
			}
		} catch (SQLException e) {
			System.err.println("in getCompIds " + e.getMessage());
			e.printStackTrace();
		}
		return list;
	}
	
	
	public Double[] getLatLong(String address) {
		GeoApiContext context = new GeoApiContext()
				.setApiKey("AIzaSyAAdfFpqk9vHsZFN4z6-3oqVUVpNI8khTM");
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
	
	
	public class FieldContainer{
		String compCode;
		int compId;
		int addressId;
		float lat;
		float lng;
		
		/**
		 * @return the compCode
		 */
		public String getCompCode() {
			return compCode;
		}
		/**
		 * @param compCode the compCode to set
		 */
		public void setCompCode(String compCode) {
			this.compCode = compCode;
		}
		/**
		 * @return the compId
		 */
		public int getCompId() {
			return compId;
		}
		/**
		 * @param compId the compId to set
		 */
		public void setCompId(int compId) {
			this.compId = compId;
		}
		/**
		 * @return the addressId
		 */
		public int getAddressId() {
			return addressId;
		}
		/**
		 * @param addressId the addressId to set
		 */
		public void setAddressId(int addressId) {
			this.addressId = addressId;
		}
		/**
		 * @return the lat
		 */
		public float getLat() {
			return lat;
		}
		/**
		 * @param lat the lat to set
		 */
		public void setLat(float lat) {
			this.lat = lat;
		}
		/**
		 * @return the lng
		 */
		public float getLng() {
			return lng;
		}
		/**
		 * @param lng the lng to set
		 */
		public void setLng(float lng) {
			this.lng = lng;
		}
		
	}
}
