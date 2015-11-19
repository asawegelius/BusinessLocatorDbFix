import java.util.ArrayList;

public class Run {

	public static void main(String[] args) {
		// new CsvToTable().fixCompanies();
		// new TestDb().getBusinessForms();
		// new CsvToTable().fixAddresses();
		// new CsvToTable().oldComps();
		// new CsvToTable().fixCompanyAddress();
		FixLatLong blob = new FixLatLong();
		blob.addOldLatLongs();
	}

}
