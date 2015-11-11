package comp3350.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import comp3350.tests.integration.BusinessPersistenceTest;
import comp3350.tests.integration.HSQLDatabaseTest;

public class IntegrationTests {
	public static TestSuite suite;

	public static Test suite() {
		suite = new TestSuite("Integration tests");
		suite.addTestSuite(HSQLDatabaseTest.class);
		suite.addTestSuite(BusinessPersistenceTest.class);
		return suite;
	}
}