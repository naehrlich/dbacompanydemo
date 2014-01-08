package net.gumbix.dba.companydemo.test;

import static org.junit.Assert.*;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import net.gumbix.dba.companydemo.db.DBAccess;
import net.gumbix.dba.companydemo.domain.Address;
import net.gumbix.dba.companydemo.domain.Car;
import net.gumbix.dba.companydemo.domain.CompanyCar;
import net.gumbix.dba.companydemo.domain.Department;
import net.gumbix.dba.companydemo.domain.Employee;
import net.gumbix.dba.companydemo.domain.Personnel;
import net.gumbix.dba.companydemo.domain.Project;
import net.gumbix.dba.companydemo.domain.ProjectStatus;
import net.gumbix.dba.companydemo.domain.StatusReport;
import net.gumbix.dba.companydemo.domain.WorksOn;
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

		Map<CompanyCar, Personnel> map = new HashMap<CompanyCar, Personnel>();
		assertTrue(access.getCompanyCars().equals(map));

		access.close();
	}

	@Test
	public void testDataStandard() throws Exception {

		testDataGenerator = new TestDataGeneratorStandard();
		access = new JdbcAccessTestDouble(testDataGenerator);

		Map<CompanyCar, Personnel> map = new HashMap<CompanyCar, Personnel>();

		//alle Autos erstellen, um Firmenwagen erstellen zu können
		Car touran = new Car("Touran", "VW");
		Car passat = new Car("Passat", "VW");
		Car sklasse = new Car("S-Klasse", "Mercedes");

		//alle Firmenwägen
		CompanyCar companyCar1234 = new CompanyCar("MA-MA 1234", sklasse);
		CompanyCar companyCar1235 = new CompanyCar("MA-MA 1235", passat);
		CompanyCar companyCar1236 = new CompanyCar("MA-MA 1236", touran);
		CompanyCar companyCar1237 = new CompanyCar("MA-MA 1237", passat);	
		CompanyCar companyCar1238 = new CompanyCar("MA-MA 1238", touran);
		CompanyCar companyCar1240 = new CompanyCar("MA-MA 1240", passat);
		CompanyCar companyCar1241 = new CompanyCar("MA-MA 1241", passat);
	
		//Abteilungen erstellen, um Angestellte erstellen zu können
		Department management = new Department(1, "Management");
		access.storeDepartment(management);
		Department verkauf = new Department(3, "Verkauf & Marketing");
		access.storeDepartment(verkauf);
		Department entwicklung = new Department(5, "Forschung & Entwicklung");
		access.storeDepartment(entwicklung);
		Department produktion = new Department(6, "Produktion");
		access.storeDepartment(produktion);
		Department kundendienst = new Department(8, "Kundendienst");
		access.storeDepartment(kundendienst);
		
		//Angestellte erstellen
		Employee eins = addEmployee(1, "Lohe", "Fransiska",
	            1967, 12, 01, 15000.0, "ChefstraÃŸe", "1a", "68305", "Mannheim",
	            "+49 621 12345-100", management, "Vorstand", null, companyCar1234);
		Employee elf = addEmployee(11, "MÃ¼ller", "Walter", 1949, 02, 11,
	            5000.0, "Flussweg", "23", "68113", "Mannheim", "+49 621 12345-500",
	            produktion, "Produktionsleiter", eins, companyCar1235);
		Employee sechzehn = addEmployee(16, "Fischer, Dr.", "Jan",
	            1968, 04, 10, 6900.0, "Untere straÃŸe", "2", "68163", "Mannheim",
	            "+49 621 12345-600", entwicklung, "F&E_Leiter", eins, companyCar1236);
		Employee vier = addEmployee(4, "Richter", "Simone",
	            1971, 6, 6, 6100.0, "Ahornweg", "2", "68163", "Mannheim",
	            "+49 621 12345-300", verkauf, "Verkaufsleitung", eins, companyCar1237);
		Employee sieben = addEmployee(7, "Simon", "Frank",
	            1971, 10, 20, 5900.0, "Holzweg", "23", "68163", "Mannheim",
	            "+49 621 12345-330", kundendienst, "Kundendienstleitung", eins, companyCar1240);
		Employee acht = addEmployee(8, "Nix", "Karl",
	            1961, 9, 12, 3280.0, "RitterstraÃŸe", "12", "68163", "Mannheim",
	            "+49 621 12345-340", kundendienst, "Service-Mitarbeiter", sieben, companyCar1241);
		
		//Projektstatus
		ProjectStatus nu = new ProjectStatus("New", "Neu");
		ProjectStatus inProcess = new ProjectStatus("InProcess", "In Bearbeitung");
		ProjectStatus blocked = new ProjectStatus("Blocked", "Blockiert");
		ProjectStatus cancelled = new ProjectStatus("Cancelled", "Abgebrochen");
		ProjectStatus finished = new ProjectStatus("Finished", "Abgeschlossen"); 

		//Projekt von eins
		/*Project hirePeople = new Project("LES", "Personal einstellen");
		hirePeople.setStatus(nu);
		WorksOn workson = new WorksOn(eins, hirePeople, 10, "VertrÃ¤ge ausstellen");
		eins.addProject(workson);
		StatusReport hirePeopleReport1 = new StatusReport(
	            new GregorianCalendar(2011, 10, 17).getTime(),
	            "Das ist der erste Statusbericht", hirePeople);
		StatusReport hirePeopleReport2 = new StatusReport(
	            new GregorianCalendar(2011, 10, 18).getTime(),
	            "Das ist noch ein Statusbericht", hirePeople);
		*/
		map.put(companyCar1234, eins);
		map.put(companyCar1235, elf);
		map.put(companyCar1236, sechzehn);
		map.put(companyCar1237, vier);
		map.put(companyCar1238, null);
		map.put(companyCar1240, sieben);
		map.put(companyCar1241, acht);
		
		assertTrue(access.getCompanyCars().equals(map));
	}

	/**
	 * Hilfsmethode um Angestellte erstellen zu können
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
	private Employee addEmployee(long personnelNumber, String lastName, String firstName, int year,
			int month, int day, double salary, String street,
			String houseNumber, String zip, String city, String phone,
			Department dep, String position, Personnel boss, CompanyCar car)
			throws Exception {
		Employee employee = new Employee(personnelNumber, lastName, firstName,
				new GregorianCalendar(year, month, day).getTime(), new Address(
						street, houseNumber, zip, city), phone);
		//employee.setSalary(salary);
		employee.setCar(car);
		//employee.setDepartment(dep);
		//employee.setPosition(position);
		//employee.setBoss(boss);
		return employee;
	}

}
