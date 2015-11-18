import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CsvToTable {

	public void fixCompanies() {
		String csvFile = "file/companies.txt";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				// company id
				String id = line.substring(0, line.indexOf(cvsSplitBy)).trim();
				int company_id = new Integer(id).intValue();
				// remove company id from line
				line = line.substring(line.indexOf(cvsSplitBy) + 1, line.length());
				// company code
				String company_code = line.substring(0, line.indexOf(cvsSplitBy));
				// remove company code from line
				line = line.substring(line.indexOf(cvsSplitBy) + 1, line.length());
				String company_text = line;

				String query = "INSERT INTO company(company_id, company_code, company_text) VALUES (" + company_id
						+ ", " + company_code + ", '" + company_text + "')";
				System.out.println(query);
				Connection con = DBConnect.getConnection();
				try {
					Statement s = con.createStatement();
					s.execute(query);

					s.close();
				} catch (SQLException e) {
					System.err.println(e.getMessage());
				}

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("Done");
	}

	public void fixAddresses() {
		String csvFile = "file/address.txt";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				// address id 1,4100,Ringsted,Giesegårdvej 115
				String id = line.substring(0, line.indexOf(cvsSplitBy)).trim();
				int address_id = new Integer(id).intValue();
				// remove address id from line
				line = line.substring(line.indexOf(cvsSplitBy) + 1, line.length());
				// zip
				String zip = line.substring(0, line.indexOf(cvsSplitBy));
				// remove zip from string
				line = line.substring(line.indexOf(cvsSplitBy) + 1, line.length());
				// district
				String district = line.substring(0, line.indexOf(cvsSplitBy));
				// remove district from string
				line = line.substring(line.indexOf(cvsSplitBy) + 1, line.length());
				// street
				String street = line;
				String query = "INSERT INTO address(address_id, street, zip, district) VALUES (" + address_id + ", '"
						+ street + "', '" + zip + "', '" + district + "')";
				System.out.println(query);
				Connection con = DBConnect.getConnection();
				try {
					Statement s = con.createStatement();
					s.execute(query);

					s.close();
				} catch (SQLException e) {
					System.err.println(e.getMessage());
				}

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("Done");
	}

	public void oldComps() {
		String csvFile = "file/compLoc.txt";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				// company id
				String comp_code = line.substring(0, line.indexOf(cvsSplitBy)).trim();
				// remove company id from line
				line = line.substring(line.indexOf(cvsSplitBy) + 1, line.length());
				// company code
				String latStr = line.substring(0, line.indexOf(cvsSplitBy));
				// remove company code from line
				float lat = new Float(latStr).floatValue();
				line = line.substring(line.indexOf(cvsSplitBy) + 1, line.length());
				String lngStr = line;
				float lng = new Float(lngStr).floatValue();

				String query = "INSERT INTO comp_loc(comp_code, lat, lng) VALUES (" + comp_code + ", " + lat + ", "
						+ lng + ")";
				System.out.println(query);
				Connection con = DBConnect.getConnection();
				try {
					Statement s = con.createStatement();
					s.execute(query);

					s.close();
				} catch (SQLException e) {
					System.err.println(e.getMessage());
				}

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("Done");
	}

	public void fixCompanyAddress() {
		String csvFile = "file/company_address.txt";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ";";
		String query1 = "select company_id from company where company_code=? limit 1";
		String query2 = "select address_id from address where street=? and district =? limit 1";
		String query3 = "INSERT INTO `businesslocator`.`company_address`(`ca_company_id`,`ca_address_id`)VALUES(?,?)";
		try {
			Connection con = DBConnect.getConnection();
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String company_code = line.substring(0, line.indexOf(cvsSplitBy)).trim();
				// remove company code from line
				line = line.substring(line.indexOf(cvsSplitBy) + 1, line.length());
				// String zip = line.substring(0,
				// line.indexOf(cvsSplitBy)).trim();
				// remove zip from line
				line = line.substring(line.indexOf(cvsSplitBy) + 1, line.length());
				String district = line.substring(0, line.indexOf(cvsSplitBy)).trim();
				// remove district from line
				line = line.substring(line.indexOf(cvsSplitBy) + 1, line.length());
				String street = line;
				try {
					PreparedStatement statement1 = con.prepareStatement(query1);
					PreparedStatement statement2 = con.prepareStatement(query2);
					PreparedStatement statement3 = con.prepareStatement(query3);
					statement1.setString(1, company_code);
					ResultSet rs = statement1.executeQuery();
					if (rs.next()) {
						int comp_id = rs.getInt("company_id");
						System.out.println("comp_id =" + comp_id);
						statement3.setInt(1, comp_id);
						statement2.setString(1, street);
						statement2.setString(2, district);
						ResultSet rs2 = statement2.executeQuery();
						if (rs2.next()) {
							int adr_id = rs2.getInt("address_id");
							statement3.setInt(2, adr_id);
							System.out.println("adr_id =" + adr_id);
							statement3.executeUpdate();
						}
					}
				} catch (SQLException e) {
					System.err.println(e.getMessage());
				} 
			}
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("Done");
	}
}
