package comp3350.tests.acceptance;

import junit.framework.Assert;
import android.test.ActivityInstrumentationTestCase2;
import com.robotium.solo.Solo;
import comp3350.wiki.R;
import comp3350.wiki.presentation.HomeActivity;

public class SearchTest extends ActivityInstrumentationTestCase2<HomeActivity> {
	private Solo solo;

	public SearchTest() {
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

	public void testSearchWithResults() {
		solo.waitForActivity("HomeActivity");
		Assert.assertTrue(solo.searchText("Tutorial"));
		solo.clickOnText("Tutorial");

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		solo.clickOnActionBarItem(R.id.search);
		solo.enterText(0, "Home");
		solo.sendKey(Solo.ENTER);

		solo.assertCurrentActivity("Expected activity SearchPageActivity", "SearchPageActivity");
		Assert.assertTrue(solo.searchText("Home Page"));
		Assert.assertFalse(solo.searchText("Second Page"));
		Assert.assertFalse(solo.searchText("Third Page"));
		solo.clickOnText("Home Page");

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		Assert.assertTrue(solo.searchText("Home Page"));
		Assert.assertTrue(solo.searchText("This is the home page for a wiki"));
		Assert.assertTrue(solo.searchText("You can edit pages with markdown"));
		Assert.assertTrue(solo.searchText("You can also make links to other pages in the wiki"));
		solo.goBack();
		solo.goBack();

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		solo.clickOnActionBarItem(R.id.search);
		solo.clearEditText(0);
		solo.enterText(0, "congratulations");
		solo.sendKey(Solo.ENTER);

		solo.assertCurrentActivity("Expected activity SearchPageActivity", "SearchPageActivity");
		Assert.assertFalse(solo.searchText("Home Page"));
		Assert.assertTrue(solo.searchText("Second Page"));
		Assert.assertFalse(solo.searchText("Third Page"));
		solo.clickOnText("Second Page");

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		Assert.assertTrue(solo.searchText("Second Page"));
		Assert.assertTrue(solo.searchText("Congratulations, the link worked."));
		solo.goBack();
		solo.goBack();

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		solo.clickOnActionBarItem(R.id.search);
		solo.clearEditText(0);
		solo.enterText(0, "page");
		solo.sendKey(Solo.ENTER);

		solo.assertCurrentActivity("Expected activity SearchPageActivity", "SearchPageActivity");
		Assert.assertTrue(solo.searchText("Home Page"));
		Assert.assertTrue(solo.searchText("Second Page"));
		Assert.assertTrue(solo.searchText("Third Page"));
	}

	public void testSearchNoResults() {
		solo.waitForActivity("HomeActivity");
		Assert.assertTrue(solo.searchText("Tutorial"));
		solo.clickOnText("Tutorial");

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		solo.clickOnActionBarItem(R.id.search);
		solo.enterText(0, "no result");
		solo.sendKey(Solo.ENTER);

		solo.assertCurrentActivity("Expected activity SearchPageActivity", "SearchPageActivity");
		Assert.assertFalse(solo.searchText("Home Page"));
		Assert.assertFalse(solo.searchText("Second Page"));
		Assert.assertFalse(solo.searchText("Third Page"));
		Assert.assertFalse(solo.searchText("Odd Pages"));
		Assert.assertFalse(solo.searchText("Low Pages"));
	}
}
