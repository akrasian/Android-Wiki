package comp3350.tests.acceptance;

import comp3350.wiki.presentation.HomeActivity;
import com.robotium.solo.Solo;
import comp3350.wiki.R;
import junit.framework.Assert;
import android.test.ActivityInstrumentationTestCase2;

public class MultipleProjectsTest extends ActivityInstrumentationTestCase2<HomeActivity> {
	private Solo solo;

	public MultipleProjectsTest() {
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

	public void testAccessProjectAndEditPages() {
		solo.waitForActivity("HomeActivity");
		Assert.assertTrue(solo.searchText("Tutorial"));
		solo.clickOnText("Tutorial");

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		Assert.assertTrue(solo.searchText("Home Page"));
		Assert.assertTrue(solo.searchText("This is the home page for a wiki"));
		Assert.assertTrue(solo.searchText("You can edit pages with markdown"));
		Assert.assertTrue(solo.searchText("You can also make links to other pages in the wiki"));
		solo.drag(0, 200, 200, 200, 10);// open drawer
		Assert.assertTrue(solo.searchText("All"));
		solo.clickOnText("All");
		Assert.assertTrue(solo.searchText("Second Page"));
		solo.clickOnText("Second Page");

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		Assert.assertTrue(solo.searchText("Second Page"));
		Assert.assertTrue(solo.searchText("Congratulations, the link worked."));
		solo.sendKey(Solo.MENU);
		solo.clickOnActionBarItem(R.id.editPage);

		solo.assertCurrentActivity("Expected activity EditPageActivity", "EditPageActivity");
		Assert.assertTrue(solo.searchEditText("Second Page"));
		Assert.assertTrue(solo.searchEditText("#Congratulations, the link worked."));
		solo.clearEditText(0);
		solo.enterText(0, "Edited Page");
		solo.clearEditText(1);
		solo.enterText(1, "#Edited body.");
		solo.clickOnButton("Save");

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		Assert.assertTrue(solo.searchText("Edited Page"));
		Assert.assertTrue(solo.searchText("Edited body."));
		solo.sendKey(Solo.MENU);
		solo.clickOnActionBarItem(R.id.editPage);

		solo.assertCurrentActivity("Expected activity EditPageActivity", "EditPageActivity");
		Assert.assertTrue(solo.searchEditText("Edited Page"));
		Assert.assertTrue(solo.searchEditText("Edited body."));
		solo.clearEditText(0);
		solo.enterText(0, "Second Page");
		solo.clearEditText(1);
		solo.enterText(1, "#Congratulations, the link worked.");
		solo.clickOnButton("Save");

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		Assert.assertTrue(solo.searchText("Second Page"));
		Assert.assertTrue(solo.searchText("Congratulations, the link worked."));
	}

	public void testCreateProjectAndPages() {
		solo.waitForActivity("HomeActivity");
		solo.clickOnButton("Create Project");

		solo.assertCurrentActivity("Expected activity EditProjectActivity", "EditProjectActivity");
		solo.enterText(0, "Test Project");
		solo.clickOnButton("Save");

		solo.assertCurrentActivity("Expected activity EditPageActivity", "EditPageActivity");
		solo.enterText(0, "Test Home Page");
		solo.clearEditText(1);
		solo.enterText(1, "Test page body.");
		solo.clickOnButton("Save");

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		Assert.assertTrue(solo.searchText("Test Home Page"));
		Assert.assertTrue(solo.searchText("Test page body."));
		solo.goBack();

		solo.assertCurrentActivity("Expected activity HomeActivity", "HomeActivity");
		Assert.assertTrue(solo.searchText("Test Project"));
		solo.clickOnText("Test Project");

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
		solo.clickOnActionBarItem(R.id.deletePage);
		solo.goBack();
		solo.goBack();

		solo.assertCurrentActivity("Expected activity HomeActivity", "HomeActivity");
		solo.clickOnView(solo.getView(R.id.editToggleButton));
		solo.clickOnText("Test Project");

		solo.assertCurrentActivity("Expected activity EditProjectActivity", "EditProjectActivity");
		Assert.assertTrue(solo.searchEditText("Test Project"));
		solo.clearEditText(0);
		solo.enterText(0, "Edited Test Project");
		solo.clickOnButton("Save");
		solo.goBack();

		solo.assertCurrentActivity("Expected activity HomeActivity", "HomeActivity");
		Assert.assertTrue(solo.searchText("Edited Test Project"));
		solo.clickOnView(solo.getView(R.id.editToggleButton));
		solo.clickOnText("Edited Test Project");

		solo.assertCurrentActivity("Expected activity EditProjectActivity", "EditProjectActivity");
		solo.clickOnButton("Delete Project");

		solo.assertCurrentActivity("Expected activity HomeActivity", "HomeActivity");
		Assert.assertFalse(solo.searchText("Edited Test Project"));
	}
}
