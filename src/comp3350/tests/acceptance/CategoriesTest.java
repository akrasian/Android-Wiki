package comp3350.tests.acceptance;

import junit.framework.Assert;
import android.test.ActivityInstrumentationTestCase2;
import com.robotium.solo.Solo;
import comp3350.wiki.R;
import comp3350.wiki.presentation.HomeActivity;

public class CategoriesTest extends ActivityInstrumentationTestCase2<HomeActivity> {
	private Solo solo;

	public CategoriesTest() {
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

	public void testAddPageToCategory() {
		solo.waitForActivity("HomeActivity");
		Assert.assertTrue(solo.searchText("Tutorial"));
		solo.clickOnText("Tutorial");

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		solo.sendKey(Solo.MENU);
		solo.clickOnActionBarItem(R.id.newPage);

		solo.assertCurrentActivity("Expected activity EditPageActivity", "EditPageActivity");
		solo.enterText(0, "New Page");
		solo.clearEditText(1);
		solo.enterText(1, "New page body.");
		solo.clickOnButton("Save");

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		Assert.assertTrue(solo.searchText("New Page"));
		Assert.assertTrue(solo.searchText("New page body."));
		solo.sendKey(Solo.MENU);
		solo.clickOnActionBarItem(R.id.editPage);

		solo.assertCurrentActivity("Expected activity EditPageActivity", "EditPageActivity");
		solo.clickOnButton("Category");
		Assert.assertTrue(solo.searchText("Odd Pages"));
		solo.clickOnText("Odd Pages");
		solo.clickOnText("OK");
		solo.clickOnButton("Save");

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		solo.drag(0, 200, 200, 200, 10);// open drawer
		Assert.assertTrue(solo.searchText("Odd Pages"));
		solo.clickOnText("Odd Pages", 2);// for stub, remove the second
											// parameter (..., 2)

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		Assert.assertTrue(solo.searchText("New Page"));
		solo.clickOnText("New Page");

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		Assert.assertTrue(solo.searchText("New Page"));
		Assert.assertTrue(solo.searchText("New page body."));
		solo.sendKey(Solo.MENU);
		solo.clickOnActionBarItem(R.id.deletePage);

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		solo.drag(0, 200, 200, 200, 10);// open drawer
		Assert.assertTrue(solo.searchText("Odd Pages"));
		solo.clickOnText("Odd Pages", 2);// for stub, remove the second
											// parameter (..., 2)

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		Assert.assertFalse(solo.searchText("New Page"));
	}

	public void testAccessCategories() {
		solo.waitForActivity("HomeActivity");
		Assert.assertTrue(solo.searchText("Tutorial"));
		solo.clickOnText("Tutorial");

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		solo.drag(0, 200, 200, 200, 10);// open drawer
		solo.sleep(500);
		Assert.assertTrue(solo.searchText("Odd Pages"));
		solo.clickOnText("Odd Pages", 2);// for stub, remove the second
											// parameter (..., 2)

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		Assert.assertTrue(solo.searchText("Home Page"));
		Assert.assertTrue(solo.searchText("Third Page"));
		solo.clickOnText("Third Page");

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		Assert.assertTrue(solo.searchText("Third Page"));
		Assert.assertTrue(solo.searchText("This page demonstrates quotes"));
		Assert.assertTrue(solo.searchText("This is the first level of a block quote"));
		Assert.assertTrue(solo.searchText("This is the second level of a block quote"));
		Assert.assertTrue(solo.searchText("Ending first level"));
		Assert.assertTrue(solo.searchText("Ending main level"));
		solo.goBack();
		solo.goBack();
		solo.drag(0, 200, 200, 200, 10);// open drawer
		Assert.assertTrue(solo.searchText("Low Pages"));
		solo.clickOnText("Low Pages");

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		Assert.assertTrue(solo.searchText("Home Page"));
		Assert.assertTrue(solo.searchText("Second Page"));
		solo.clickOnText("Second Page");

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		Assert.assertTrue(solo.searchText("Second Page"));
		Assert.assertTrue(solo.searchText("Congratulations, the link worked."));
		solo.goBack();
		solo.goBack();
		solo.drag(0, 200, 200, 200, 10);// open drawer
		Assert.assertTrue(solo.searchText("All"));
		solo.clickOnText("All");

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		Assert.assertTrue(solo.searchText("Home Page"));
		Assert.assertTrue(solo.searchText("Second Page"));
		Assert.assertTrue(solo.searchText("Third Page"));
		Assert.assertTrue(solo.searchText("Low Pages"));
		Assert.assertTrue(solo.searchText("Odd Pages"));
		solo.clickOnText("Home Page");

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		Assert.assertTrue(solo.searchText("Home Page"));
		Assert.assertTrue(solo.searchText("This is the home page for a wiki"));
		Assert.assertTrue(solo.searchText("You can edit pages with markdown"));
		Assert.assertTrue(solo.searchText("You can also make links to other pages in the wiki"));
	}
}
