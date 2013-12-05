package net.gumbix.dba.companydemo.test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;

import net.gumbix.dba.companydemo.db.DBAccess;
import net.gumbix.dba.companydemo.jdbc.JdbcAccess;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Maximilian Nährlich (maximilian.naehrlich@stud.hs-mannheim.de)
 */

public class TestQueries {
	
	DBAccess access;
	
	@Before
	public void setupTest() throws Exception{
		access = new JdbcAccessTestDouble();
	}
	
	@Test
	public void testMockDB() throws Exception{		
		assertTrue(true);
	}


}
