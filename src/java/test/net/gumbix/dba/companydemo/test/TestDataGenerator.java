package net.gumbix.dba.companydemo.test;

import net.gumbix.dba.companydemo.db.DBAccess;

public interface TestDataGenerator {
	
	public void createMockData(DBAccess access) throws Exception;

}
