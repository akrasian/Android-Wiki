package comp3350.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import comp3350.tests.objects.ProjectTest;
import comp3350.tests.objects.PageTest;
import comp3350.tests.objects.ChildTest;
import comp3350.tests.persistence.DataAccessTest;
import comp3350.tests.business.AccessChildrenTest;
import comp3350.tests.business.AccessPagesTest;
import comp3350.tests.business.AccessProjectsTest;

public class UnitTests {
	public static TestSuite suite;

	public static Test suite() {
		suite = new TestSuite("All tests");
		testObjects();
		testBusiness();
		suite.addTestSuite(DataAccessTest.class);
		return suite;
	}

	private static void testObjects() {
		suite.addTestSuite(ProjectTest.class);
		suite.addTestSuite(PageTest.class);
		suite.addTestSuite(ChildTest.class);
	}

	private static void testBusiness() {
		suite.addTestSuite(AccessPagesTest.class);
		suite.addTestSuite(AccessProjectsTest.class);
		suite.addTestSuite(AccessChildrenTest.class);
	}
}
