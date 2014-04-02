package net.gumbix.dba.companydemo.test;

import static org.junit.Assert.*;
import net.gumbix.dba.companydemo.db.DBAccess;
import net.gumbix.dba.companydemo.domain.Car;
import net.gumbix.dba.companydemo.domain.CompanyCar;
import net.gumbix.dba.companydemo.domain.Department;
import net.gumbix.dba.companydemo.domain.Employee;
import net.gumbix.dba.companydemo.domain.Personnel;
import net.gumbix.dba.companydemo.domain.Project;
import net.gumbix.dba.companydemo.domain.ProjectStatus;
import net.gumbix.dba.companydemo.jdbc.JdbcAccess;

import org.junit.Test;

/**
 * @author Katrin Andraschko (katrin.andraschko@stud.hs-mannheim.de)
 */
public class TestReportStatistic {

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

		assertTrue(access.getNumberOfPersonnel() == 0);
		assertTrue(access.getNumberOfWorkers() == 0);
		assertTrue(access.getNumberOfProjects() == 0);
		assertTrue(access.getNumberOfDepartments() == 0);
		assertTrue(access.getNumberOfCompanyCars() == 0);
		assertTrue(access.getNumberOfFreeCars() == 0);
		assertTrue(access.getNumberOfUsedCars() == 0);

		access.close();
	}

	@Test
	public void testDataCustom() throws Exception {

		TestDataGenerator testDataGeneratorCustom = new TestDataGenerator() {

			@Override
			public void createMockData(DBAccess access) throws Exception {

				Car passat = new Car("Passat", "VW");
				access.storeCar(passat);
				CompanyCar car1 = new CompanyCar("Hallo1", passat);
				access.storeCompanyCar(car1);
				CompanyCar car2 = new CompanyCar("Hallo2", passat);
				access.storeCompanyCar(car2);

				Department dep1 = new Department(1, "Hallo1");
				access.storeDepartment(dep1);
				Department dep2 = new Department(2, "Hallo2");
				access.storeDepartment(dep2);

				ProjectStatus nu = new ProjectStatus("New", "Neu");
				access.storeProjectStatus(nu);
				ProjectStatus blocked = new ProjectStatus("Blocked",
						"Blockiert");
				access.storeProjectStatus(blocked);
				Project proj1 = new Project("1", "Hallo1");
				proj1.setStatus(blocked);
				access.storeProject(proj1);
			}
		};

		access = new JdbcAccessTestDouble(testDataGeneratorCustom);
		
		assertTrue(access.getNumberOfPersonnel() == 0);
		assertTrue(access.getNumberOfWorkers() == 0);
		assertTrue(access.getNumberOfProjects() == 1);
		assertTrue(access.getNumberOfDepartments() == 2);
		assertTrue(access.getNumberOfCompanyCars() == 2);

		CompanyCar car = access.loadCompanyCar("Hallo1");
		access.deleteCompanyCar(car);

		Department dep = access.loadDepartment(1);
		access.deleteDepartment(dep);

		Project proj = access.loadProject("1");
		access.deleteProject(proj);

		assertTrue(access.getNumberOfPersonnel() == 0);
		assertTrue(access.getNumberOfWorkers() == 0);
		assertTrue(access.getNumberOfProjects() == 0);
		assertTrue(access.getNumberOfDepartments() == 1);
		assertTrue(access.getNumberOfCompanyCars() == 1);

		access.close();
	}

	@Test
	public void testDataStandard() throws Exception {

		testDataGenerator = new TestDataGeneratorStandard();
		access = new JdbcAccessTestDouble(testDataGenerator);

		assertTrue(access.getNumberOfPersonnel() == 21);
		assertTrue(access.getNumberOfWorkers() == 4);
		assertTrue(access.getNumberOfProjects() == 4);
		assertTrue(access.getNumberOfDepartments() == 9);
		assertTrue(access.getNumberOfCompanyCars() == 7);
		assertTrue(access.getNumberOfFreeCars() == 1);
		assertTrue(access.getNumberOfUsedCars() == 6);
		
		Employee pers1 = access.loadEmployee(2);
		access.deletePersonnel(pers1);
		
		assertTrue(access.getNumberOfPersonnel() == 20);
		assertTrue(access.getNumberOfWorkers() == 4);
		
		Personnel pers2 = access.loadWorker(12);
		access.deletePersonnel(pers2);
		
		assertTrue(access.getNumberOfPersonnel() == 19);
		assertTrue(access.getNumberOfWorkers() == 3);
		
		access.close();
	}

}