package net.gumbix.dba.companydemo.test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;

import net.gumbix.dba.companydemo.db.DBAccess;
import net.gumbix.dba.companydemo.domain.Project;
import net.gumbix.dba.companydemo.domain.ProjectStatus;
import net.gumbix.dba.companydemo.jdbc.JdbcAccess;
import net.gumbix.dba.companydemo.jdbc.ProjectDAO;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Maximilian Nährlich (maximilian.naehrlich@stud.hs-mannheim.de)
 * This class provides two code examples how to setup your unit test
 * using the JDBC test double (mock database)
 */

public class TestQueries {
	
	@Before
	public void setupTest() throws Exception{
		/* Examples for setting up you test case */
		
		//1. Using the standard test data in mock database
		//get an instance of the standard test data generator
		TestDataGenerator testDataGenerator = new TestDataGeneratorStandard();
		//get you mock database with standard test data
		JdbcAccess accessStandard = new JdbcAccessTestDouble(testDataGenerator);
		//do some testing
		ProjectDAO pDAO = new ProjectDAO(accessStandard);
		//do NOT try to get two mock databases at the same time, so close this database
		accessStandard.close();
		
		
		//2. Using da mock data base with custom data
		//implement your custom data creation and get an instance of it
		
		TestDataGenerator testDataGeneratorCustom = new TestDataGenerator() {
			
			@Override
			public void createMockData(DBAccess access) throws Exception {				
				ProjectStatus nu = new ProjectStatus("New", "Neu");
				access.storeProjectStatus(nu);
				
				Project p = new Project("PRO", "custom mock data");
				p.setStatus(nu);
				access.storeProject(p);
				
				System.out.println("Custom Beispieldaten erzeugt.");
				
			}
		};
		JdbcAccess accessCustom = new JdbcAccessTestDouble(testDataGeneratorCustom);
		//do some testing
		ProjectDAO pDAO2 = new ProjectDAO(accessStandard);
		//do NOT try to get two mock databases at the same time, so close this database
		accessStandard.close();
		
	}
	
	@Test
	public void testMockDB() throws Exception{		
		assertTrue(true);
	}


}
