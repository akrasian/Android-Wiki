package comp3350.tests.acceptance;

import junit.framework.Assert;
import android.test.ActivityInstrumentationTestCase2;
import com.robotium.solo.Solo;
import comp3350.wiki.R;
import comp3350.wiki.presentation.HomeActivity;

public class HyperlinksTest extends ActivityInstrumentationTestCase2<HomeActivity> {
	private Solo solo;

	public HyperlinksTest() {
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

	public void testCreateHyperlink() {
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
		solo.sendKey(Solo.MENU);
		solo.clickOnActionBarItem(R.id.newPage);

		solo.assertCurrentActivity("Expected activity EditPageActivity", "EditPageActivity");
		solo.enterText(0, "New Page");
		solo.clearEditText(1);
		solo.clickOnButton("Link");
		Assert.assertTrue(solo.searchText("Test Home Page"));
		solo.clickOnText("Test Home Page");
		solo.clickOnButton("Save");

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		Assert.assertTrue(solo.searchText("New Page"));
		Assert.assertTrue(solo.searchText("Test Home Page"));
		solo.clickOnText("Test Home Page", 2);

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		Assert.assertTrue(solo.searchText("Test Home Page"));
		Assert.assertTrue(solo.searchText("Test page body."));
		solo.goBack();
		solo.goBack();
		solo.goBack();

		solo.assertCurrentActivity("Expected activity HomeActivity", "HomeActivity");
		Assert.assertTrue(solo.searchText("Test Project"));
		solo.clickOnView(solo.getView(R.id.editToggleButton));
		solo.clickOnText("Test Project");

		solo.assertCurrentActivity("Expected activity EditProjectActivity", "EditProjectActivity");
		solo.clickOnButton("Delete Project");

		solo.assertCurrentActivity("Expected activity HomeActivity", "HomeActivity");
		Assert.assertFalse(solo.searchText("Test Project"));
	}

	public void testManualHyperlink() {
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
		solo.sendKey(Solo.MENU);
		solo.clickOnActionBarItem(R.id.newPage);

		solo.assertCurrentActivity("Expected activity EditPageActivity", "EditPageActivity");
		solo.enterText(0, "New Page");
		solo.clearEditText(1);
		solo.enterText(1, "[Manual Link](p0)");// (p0) for real db, (p22) for
												// stub
		solo.clickOnButton("Save");

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		Assert.assertTrue(solo.searchText("New Page"));
		Assert.assertTrue(solo.searchText("Manual Link"));
		solo.clickOnText("Manual Link");

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		Assert.assertTrue(solo.searchText("Test Home Page"));
		Assert.assertTrue(solo.searchText("Test page body."));
		solo.goBack();
		solo.goBack();
		solo.goBack();

		solo.assertCurrentActivity("Expected activity HomeActivity", "HomeActivity");
		Assert.assertTrue(solo.searchText("Test Project"));
		solo.clickOnView(solo.getView(R.id.editToggleButton));
		solo.clickOnText("Test Project");

		solo.assertCurrentActivity("Expected activity EditProjectActivity", "EditProjectActivity");
		solo.clickOnButton("Delete Project");

		solo.assertCurrentActivity("Expected activity HomeActivity", "HomeActivity");
		Assert.assertFalse(solo.searchText("Test Project"));
	}

	public void testAutoHyperlink() {
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
		solo.sendKey(Solo.MENU);
		solo.clickOnActionBarItem(R.id.newPage);

		solo.assertCurrentActivity("Expected activity EditPageActivity", "EditPageActivity");
		solo.enterText(0, "New Page");
		solo.clearEditText(1);
		solo.enterText(1, "Typing Test Home Page should set up an auto hyperlink.");
		solo.clickOnButton("Save");

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		Assert.assertTrue(solo.searchText("New Page"));
		Assert.assertTrue(solo.searchText("Test Home Page"));
		solo.clickOnText("Test Home Page", 2);

		solo.assertCurrentActivity("Expected activity ViewPageActivity", "ViewPageActivity");
		Assert.assertTrue(solo.searchText("Test Home Page"));
		Assert.assertTrue(solo.searchText("Test page body."));
		solo.goBack();
		solo.goBack();
		solo.goBack();

		solo.assertCurrentActivity("Expected activity HomeActivity", "HomeActivity");
		Assert.assertTrue(solo.searchText("Test Project"));
		solo.clickOnView(solo.getView(R.id.editToggleButton));
		solo.clickOnText("Test Project");

		solo.assertCurrentActivity("Expected activity EditProjectActivity", "EditProjectActivity");
		solo.clickOnButton("Delete Project");

		solo.assertCurrentActivity("Expected activity HomeActivity", "HomeActivity");
		Assert.assertFalse(solo.searchText("Test Project"));
	}
}
