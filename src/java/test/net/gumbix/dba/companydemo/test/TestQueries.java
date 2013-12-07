package net.gumbix.dba.companydemo.test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;

import net.gumbix.dba.companydemo.db.DBAccess;
import net.gumbix.dba.companydemo.domain.Project;
import net.gumbix.dba.companydemo.jdbc.JdbcAccess;
import net.gumbix.dba.companydemo.jdbc.ProjectDAO;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Maximilian Nährlich (maximilian.naehrlich@stud.hs-mannheim.de)
 */

public class TestQueries {
	
	JdbcAccess access;
	
	@Before
	public void setupTest() throws Exception{
		access = new JdbcAccessTestDouble();
	}
	
	@Test
	public void testMockDB() throws Exception{		
		assertTrue(access != null);
		
		ProjectDAO pDAO = new ProjectDAO(access);
		Project p = pDAO.load("SEC");
		System.out.println(p);
	}


}
