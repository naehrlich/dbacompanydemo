package net.gumbix.dba.companydemo.test;

import static org.junit.Assert.*;

import java.util.Date;

import net.gumbix.dba.companydemo.db.DBAccess;
import net.gumbix.dba.companydemo.domain.Address;
import net.gumbix.dba.companydemo.domain.Employee;
import net.gumbix.dba.companydemo.domain.Project;
import net.gumbix.dba.companydemo.domain.ProjectStatus;
import net.gumbix.dba.companydemo.domain.ProjectStatusEnum;
import net.gumbix.dba.companydemo.domain.WorksOn;
import net.gumbix.dba.companydemo.jdbc.EmployeeDAO;
import net.gumbix.dba.companydemo.jdbc.JdbcAccess;
import net.gumbix.dba.companydemo.jdbc.ProjectDAO;
import net.gumbix.dba.companydemo.jdbc.ProjectStatusDAO;
import net.gumbix.dba.companydemo.jdbc.WorksOnDAO;

import org.junit.Before;
import org.junit.Test;

public class TestProject {

	private JdbcAccess access;
	private ProjectDAO pDAO;
	private ProjectStatusDAO psDAO;
	private Project outProject; // out = object under test

	private ProjectStatus nu;
	private ProjectStatus inProcess;
	private ProjectStatus blocked;
	private ProjectStatus cancelled;
	private ProjectStatus finished;

	@Before
	public void setup() throws Exception {
		TestDataGenerator testDataGeneratorCustom = new TestDataGenerator() {
			@Override
			public void createMockData(DBAccess access) throws Exception {
				ProjectStatus nu = new ProjectStatus("New", "Neu");
				ProjectStatus inProcess = new ProjectStatus("InProcess",
						"In Bearbeitung");
				ProjectStatus blocked = new ProjectStatus("Blocked",
						"Blockiert");
				ProjectStatus cancelled = new ProjectStatus("Cancelled",
						"Abgebrochen");
				ProjectStatus finished = new ProjectStatus("Finished",
						"Abgeschlossen");

				access.storeProjectStatus(nu);
				access.storeProjectStatus(inProcess);
				access.storeProjectStatus(blocked);
				access.storeProjectStatus(cancelled);
				access.storeProjectStatus(finished);
			}
		};
		access = new JdbcAccessTestDouble(testDataGeneratorCustom);

		pDAO = new ProjectDAO(access);
		psDAO = new ProjectStatusDAO(access);
		outProject = new Project("TP1", "Neues Testprojekt");

		nu = psDAO.load(ProjectStatusEnum.New);
		inProcess = psDAO.load(ProjectStatusEnum.InProcess);
		blocked = psDAO.load(ProjectStatusEnum.Blocked);
		cancelled = psDAO.load(ProjectStatusEnum.Cancelled);
		finished = psDAO.load(ProjectStatusEnum.Finished);

	}

	@Test
	public void testProjectSaveStatusNew() throws Exception {
		pDAO.store(outProject);
		outProject = pDAO.load("TP1");
		// has the new project status new?
		assertTrue(outProject.getStatus().getStatus() == ProjectStatusEnum.New);
	}

	// @Test
	public void testProjectNextStatusInProcess() throws Exception {
		// is the next status InProcess?
		assertTrue(outProject.getNextStatus().get(0) == ProjectStatusEnum.InProcess);

		// try to set wrong status
		try {
			outProject.setNextStatus(nu);
		} catch (Exception ex) {
			assertTrue(true);
		}
		try {
			outProject.setNextStatus(blocked);
		} catch (Exception ex) {
			assertTrue(true);
		}
		try {
			outProject.setNextStatus(cancelled);
		} catch (Exception ex) {
			assertTrue(true);
		}
		try {
			outProject.setNextStatus(finished);
		} catch (Exception ex) {
			assertTrue(true);
		}

		// set correct next status
		try {
			outProject.setNextStatus(inProcess);
		} catch (Exception ex) {
			assertTrue(false);
		}

		// is the current status InProcess?
		assertTrue(outProject.getStatus().getStatus() == ProjectStatusEnum.InProcess);

		// store
		pDAO.store(outProject);
	}

	// @Test
	public void testProjectNextStatusBlocked() throws Exception {
		// is the next status Blocked?
		boolean statusBlocked = false;
		for (ProjectStatusEnum nextStatus : outProject.getNextStatus()) {
			if (nextStatus == ProjectStatusEnum.Blocked) {
				statusBlocked = true;
				break;
			}
		}
		assertTrue(statusBlocked);

		// try to set wrong status
		try {
			outProject.setNextStatus(nu);
		} catch (Exception ex) {
			assertTrue(true);
		}
		try {
			outProject.setNextStatus(inProcess);
		} catch (Exception ex) {
			assertTrue(true);
		}

		// set correct next status
		try {
			outProject.setNextStatus(blocked);
		} catch (Exception ex) {
			assertTrue(false);
		}

		// is the current status Blocked?
		assertTrue(outProject.getStatus().getStatus() == ProjectStatusEnum.Blocked);

		// store
		pDAO.store(outProject);

		// we reset the status to InProcess for the next tests
		outProject.setNextStatus(inProcess);
		pDAO.store(outProject);
	}

	// @Test
	public void testProjectNextStatusCancelled() throws Exception {
		// is the next status Blocked?
		boolean statusCancelled = false;
		for (ProjectStatusEnum nextStatus : outProject.getNextStatus()) {
			if (nextStatus == ProjectStatusEnum.Cancelled) {
				statusCancelled = true;
				break;
			}
		}
		assertTrue(statusCancelled);

		// try to set wrong status
		try {
			outProject.setNextStatus(nu);
		} catch (Exception ex) {
			assertTrue(true);
		}
		try {
			outProject.setNextStatus(inProcess);
		} catch (Exception ex) {
			assertTrue(true);
		}

		// We must test 2 cases
		// Case 1: The project has no personel working on the project -> the
		// project can be cancelled
		// Case 2: The project has personel working on the project -> the
		// project can not be cancelled

		// Case 1
		// set correct next status
		try {
			outProject.setNextStatus(cancelled);
		} catch (Exception ex) {
			assertTrue(false);
		}

		// is the current status Cancelled?
		assertTrue(outProject.getStatus().getStatus() == ProjectStatusEnum.Cancelled);

		// reset the status for the next test
		outProject.setNextStatus(inProcess);
		pDAO.store(outProject);

		// Case 2
		// we have to assign personel to the project
		Employee emp = new Employee("Worker", "Mr.", new Date("11.12.13"),
				new Address("", "", "", ""), "");
		new EmployeeDAO(access).store(emp);
		WorksOn wo = new WorksOn(emp, outProject, 100.0, "Tester");
		new WorksOnDAO(access).store(wo);
		outProject = pDAO.load("TP1");
		assertTrue(!outProject.getEmployees().isEmpty());

		// try to set status (it should not be allowed now!)
		try {
			outProject.setNextStatus(cancelled);
			assertTrue(false);
		} catch (Exception ex) {
			assertTrue(true);
		}

		// reset the status for the next test
		outProject.setNextStatus(inProcess);
		pDAO.store(outProject);
	}

	// @Test
	public void testProjectNextStatusFinished() throws Exception {
		// is the next status Blocked?
		boolean statusFinished = false;
		for (ProjectStatusEnum nextStatus : outProject.getNextStatus()) {
			if (nextStatus == ProjectStatusEnum.Finished) {
				statusFinished = true;
				break;
			}
		}
		assertTrue(statusFinished);

		// try to set wrong status
		try {
			outProject.setNextStatus(nu);
		} catch (Exception ex) {
			assertTrue(true);
		}
		try {
			outProject.setNextStatus(inProcess);
		} catch (Exception ex) {
			assertTrue(true);
		}
		
		//when a project is set to Finished all personel is removed from that project
		// set correct next status
		try {
			outProject.setNextStatus(finished);
		} catch (Exception ex) {
			assertTrue(false);
		}
		
		// is the current status Cancelled?
		assertTrue(outProject.getStatus().getStatus() == ProjectStatusEnum.Finished);
		
		//check if the project has no personel before store
		assertTrue(outProject.getEmployees().isEmpty());
		// store
		pDAO.store(outProject);
		outProject = pDAO.load("TP1");
		//check if the project has no personel after store
		assertTrue(outProject.getEmployees().isEmpty());
	}

}
