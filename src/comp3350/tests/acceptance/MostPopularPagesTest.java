package comp3350.tests.acceptance;

import junit.framework.Assert;
import android.test.ActivityInstrumentationTestCase2;
import com.robotium.solo.Solo;
import comp3350.wiki.presentation.HomeActivity;

public class MostPopularPagesTest extends ActivityInstrumentationTestCase2<HomeActivity> {
	private Solo solo;

	public MostPopularPagesTest() {
		super(HomeActivity.class);
	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		// Disable this for full acceptance test
		// System.out.println("Injecting stub database.");
		// Services.getStubDatabase("UUikiDataBase");
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}

	public void testAccessPopularPages() {
		solo.waitForActivity("HomeActivity");
		Assert.assertTrue(solo.searchText("Second Project"));
		solo.clickOnText("Second Project");

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		Assert.assertTrue(solo.searchText("Most Popular"));
		Assert.assertTrue(solo.searchText("Page Title 10"));
		Assert.assertTrue(solo.searchText("Page Title 11"));
		Assert.assertTrue(solo.searchText("Page Title 12"));
		solo.clickOnText("Page Title 11");

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		Assert.assertTrue(solo.searchText("Page Title 11"));
		Assert.assertTrue(solo.searchText("Page Content 11"));
	}

	public void testAccessPopularProjects() {
		solo.waitForActivity("HomeActivity");
		Assert.assertTrue(solo.searchText("Most popular projects"));
		Assert.assertTrue(solo.searchText("Tutorial", 2));
		Assert.assertTrue(solo.searchText("Second Project", 2));
		solo.clickOnText("Tutorial", 2);

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		Assert.assertTrue(solo.searchText("Home Page"));
		Assert.assertTrue(solo.searchText("This is the home page for a wiki"));
		Assert.assertTrue(solo.searchText("You can edit pages with markdown"));
		Assert.assertTrue(solo.searchText("You can also make links to other pages in the wiki"));
		solo.goBack();

		solo.assertCurrentActivity("Expected activity HomeActivity", "HomeActivity");
		solo.clickOnText("Second Project", 2);

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		Assert.assertTrue(solo.searchText("Page Title 6"));
		Assert.assertTrue(solo.searchText("Page Content 6"));
	}
}
