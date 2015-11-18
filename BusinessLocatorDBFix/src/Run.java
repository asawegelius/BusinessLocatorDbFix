import java.util.ArrayList;

public class Run {

	public static void main(String[] args) {
		//new CsvToTable().fixCompanies();
		//new TestDb().getBusinessForms();
		//new CsvToTable().fixAddresses();
		//new CsvToTable().oldComps();
		FixLatLong blob = new FixLatLong();
		ArrayList<String> codes = blob.getCompCodeFromCompLoc();
		System.out.println("no of codes = " + codes.size());
		ArrayList<Integer> ids = blob.getCompIdsFromCompanies(codes);
		System.out.println("no of ids = " + ids.size());
	}

}
