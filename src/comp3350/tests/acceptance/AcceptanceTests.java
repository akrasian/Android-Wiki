package comp3350.tests.acceptance;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AcceptanceTests {
	public static TestSuite suite;

	public static Test suite() {
		suite = new TestSuite("Acceptance tests");
		suite.addTestSuite(MultipleProjectsTest.class);
		suite.addTestSuite(HyperlinksTest.class);
		suite.addTestSuite(CategoriesTest.class);
		suite.addTestSuite(MostPopularPagesTest.class);
		suite.addTestSuite(SearchTest.class);
		return suite;
	}
}