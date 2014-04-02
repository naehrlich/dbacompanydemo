package net.gumbix.dba.companydemo.test;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import net.gumbix.dba.companydemo.db.DBAccess;
import net.gumbix.dba.companydemo.domain.Address;
import net.gumbix.dba.companydemo.domain.Car;
import net.gumbix.dba.companydemo.domain.CompanyCar;
import net.gumbix.dba.companydemo.domain.Department;
import net.gumbix.dba.companydemo.domain.Employee;
import net.gumbix.dba.companydemo.domain.Personnel;
import net.gumbix.dba.companydemo.jdbc.JdbcAccess;
import org.junit.Test;

/**
 * @author Katrin Andraschko (katrin.andraschko@stud.hs-mannheim.de)
 */
public class TestReportCompanyCars {

	private TestDataGenerator testDataGenerator;
	private JdbcAccess access;

	@Test
	public void testNoData() throws Exception {

		TestDataGenerator testDataGeneratorCustom = new TestDataGenerator() {
			@Override
			public void createMockData(DBAccess access) throws Exception {
				System.out.println("Custom Beispieldaten erzeugt.");
			}
		};

		access = new JdbcAccessTestDouble(testDataGeneratorCustom);

		List<CompanyCar> list = new ArrayList<CompanyCar>();
		assertTrue(access.getCompanyCars().equals(list));

		access.close();
	}

	@Test
	public void testDataStandard() throws Exception {

		testDataGenerator = new TestDataGeneratorStandard();
		access = new JdbcAccessTestDouble(testDataGenerator);

		List<CompanyCar> list = new ArrayList<CompanyCar>();

		Car touran = new Car("Touran", "VW");
		Car passat = new Car("Passat", "VW");
		Car sklasse = new Car("S-Klasse", "Mercedes");

		CompanyCar companyCar1234 = new CompanyCar("MA-MA 1234", sklasse);
		CompanyCar companyCar1235 = new CompanyCar("MA-MA 1235", passat);
		CompanyCar companyCar1236 = new CompanyCar("MA-MA 1236", touran);
		CompanyCar companyCar1237 = new CompanyCar("MA-MA 1237", passat);
		CompanyCar companyCar1238 = new CompanyCar("MA-MA 1238", touran);
		CompanyCar companyCar1240 = new CompanyCar("MA-MA 1240", passat);
		CompanyCar companyCar1241 = new CompanyCar("MA-MA 1241", passat);

		Employee eins = addEmployee(1, "Lohe", "Fransiska", 1967, 12, 01,
				15000.0, "Chefstraße", "1a", "68305", "Mannheim",
				"+49 621 12345-100", null, "Vorstand", null, companyCar1234);
		Employee elf = addEmployee(11, "Müller", "Walter", 1949, 02, 11,
				5000.0, "Flussweg", "23", "68113", "Mannheim",
				"+49 621 12345-500", null, "Produktionsleiter", eins,
				companyCar1235);
		Employee sechzehn = addEmployee(16, "Fischer, Dr.", "Jan", 1968, 04,
				10, 6900.0, "Untere straße", "2", "68163", "Mannheim",
				"+49 621 12345-600", null, "F&E_Leiter", eins, companyCar1236);
		Employee vier = addEmployee(4, "Richter", "Simone", 1971, 6, 6, 6100.0,
				"Ahornweg", "2", "68163", "Mannheim", "+49 621 12345-300",
				null, "Verkaufsleitung", eins, companyCar1237);
		Employee sieben = addEmployee(7, "Simon", "Frank", 1971, 10, 20,
				5900.0, "Holzweg", "23", "68163", "Mannheim",
				"+49 621 12345-330", null, "Kundendienstleitung", eins,
				companyCar1240);
		Employee acht = addEmployee(8, "Nix", "Karl", 1961, 9, 12, 3280.0,
				"Ritterstraße", "12", "68163", "Mannheim",
				"+49 621 12345-340", null, "Service-Mitarbeiter", sieben,
				companyCar1241);

		companyCar1234.setDriver(eins);
		companyCar1235.setDriver(elf);
		companyCar1236.setDriver(sechzehn);
		companyCar1237.setDriver(vier);
		companyCar1240.setDriver(sieben);
		companyCar1241.setDriver(acht);

		list.add(companyCar1234);
		list.add(companyCar1235);
		list.add(companyCar1236);
		list.add(companyCar1237);
		list.add(companyCar1238);
		list.add(companyCar1240);
		list.add(companyCar1241);

		List<CompanyCar> result = access.getCompanyCars();

		// Ueberpruefung, ob die gleichen Elemente in der Liste stehen
		// (Reihenfolge irrelevant)
		for (int i = 0; i < list.size(); i++) {
			boolean sameCar = false;
			CompanyCar firstCar = list.get(i);
			String firstPlate = firstCar.getLicensePlate();
			for (int j = 0; j < result.size(); j++) {
				CompanyCar secondCar = result.get(j);
				String secondPlate = secondCar.getLicensePlate();

				if (firstPlate.equals(secondPlate)) {
					long firstDriverNumber = 0;
					if (firstCar.getDriver() != null) {
						firstDriverNumber = firstCar.getDriver()
								.getPersonnelNumber();
					}
					long secondDriverNumber = 0;
					if (secondCar.getDriver() != null) {
						secondDriverNumber = secondCar.getDriver()
								.getPersonnelNumber();
					}
					if (firstDriverNumber == secondDriverNumber) {
						sameCar = true;
					}
				}
			}
			if (sameCar == false) {
				assertTrue(false);
			}
		}
		access.close();
		assertTrue(true);
	}

	/**
	 * Hilfsmethode um Angestellte erstellen zu k�nnen
	 * 
	 * @param lastName
	 * @param firstName
	 * @param year
	 * @param month
	 * @param day
	 * @param salary
	 * @param street
	 * @param houseNumber
	 * @param zip
	 * @param city
	 * @param phone
	 * @param dep
	 * @param position
	 * @param boss
	 * @param car
	 * @return
	 * @throws Exception
	 */
	private Employee addEmployee(long personnelNumber, String lastName,
			String firstName, int year, int month, int day, double salary,
			String street, String houseNumber, String zip, String city,
			String phone, Department dep, String position, Personnel boss,
			CompanyCar car) throws Exception {
		Employee employee = new Employee(personnelNumber, lastName, firstName,
				new GregorianCalendar(year, month, day).getTime(), new Address(
						street, houseNumber, zip, city), phone);
		// employee.setSalary(salary);
		employee.setCar(car);
		// employee.setDepartment(dep);
		// employee.setPosition(position);
		// employee.setBoss(boss);
		return employee;
	}

}
