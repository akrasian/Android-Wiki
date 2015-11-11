package comp3350.tests.objects;

import junit.framework.TestCase;
import comp3350.wiki.objects.Page;
import comp3350.wiki.objects.Project;

public class PageTest extends TestCase {
	public PageTest(String arg0) {
		super(arg0);
	}

	public void testFullPageConstructor() {
		System.out.println("\nStarting testFullPageConstructor");

		Project wikiProject = new Project("project3", "Test", null);

		Page wikiPage = new Page("p22", "test title", "#body of awesome", wikiProject);
		assertEquals(wikiProject.getID(), wikiPage.getProjectID());
		assertEquals("project3", wikiPage.getProjectID());
		assertEquals("test title", wikiPage.getTitle());
		assertEquals("<h1>body of awesome</h1>\n", wikiPage.getHTML());
		assertEquals("#body of awesome", wikiPage.getMarkdown());
		assertEquals("p22", wikiPage.getID());
		assertEquals("http://p22/", wikiPage.getLink());
		assertEquals("test title", wikiPage.toString());

		System.out.println("\nFinished testFullPageConstructor");
	}

	public void testGettersAndSetters() {
		System.out.println("\nStarting testGettersAndSetters");

		Project wikiProject = new Project("project3", "Test", "1");

		Page wikiPage = new Page("p22", "test title", "#body of awesome", wikiProject);
		assertEquals(wikiProject.getID(), wikiPage.getProjectID());
		assertEquals("project3", wikiPage.getProjectID());
		assertEquals("test title", wikiPage.getTitle());
		assertEquals("<h1>body of awesome</h1>\n", wikiPage.getHTML());
		assertEquals("#body of awesome", wikiPage.getMarkdown());
		assertEquals("p22", wikiPage.getID());
		assertEquals("http://p22/", wikiPage.getLink());
		assertEquals("test title", wikiPage.toString());

		wikiPage.setTitle("new title for testing");
		wikiPage.setMarkdown("#header\nparagraph");

		assertEquals(wikiProject.getID(), wikiPage.getProjectID());
		assertEquals("project3", wikiPage.getProjectID());
		assertEquals("new title for testing", wikiPage.getTitle());
		assertEquals("<h1>header</h1>\n<p>paragraph</p>\n", wikiPage.getHTML());
		assertEquals("#header\nparagraph", wikiPage.getMarkdown());
		assertEquals("p22", wikiPage.getID());
		assertEquals("http://p22/", wikiPage.getLink());
		assertEquals("new title for testing", wikiPage.toString());

		System.out.println("\nFinished testGettersAndSetters");
	}

	public void testEquals() {
		System.out.println("\nStarting testEquals");

		Page wikiPageOne;
		Page wikiPageTwo;
		Page wikiPageThree;

		Project wikiProject = new Project("1", "Test", "1");

		wikiPageOne = new Page("p404", "Title", "Body", wikiProject);
		wikiPageTwo = new Page("p404", "Title", "Body", wikiProject);
		wikiPageThree = new Page("p405", "Title", "Body", wikiProject);

		assertTrue(wikiPageOne.equals(wikiPageTwo));
		assertFalse(wikiPageOne.equals(wikiPageThree));

		System.out.println("\nFinished testEquals");
	}

	public void testViewCount() {
		System.out.println("\nStarting testViewCount");
		Project wikiProject = new Project("project3", "Test", null);

		Page page0 = new Page("p44", "Title", "Body", wikiProject, 0);
		Page page25 = new Page("p45", "Title", "Body", wikiProject, 25);

		assertEquals(0, page0.getViewCount());
		page0.increaseViewCount();
		assertEquals(1, page0.getViewCount());

		assertEquals(25, page25.getViewCount());

		page0 = new Page("p46", "Title", "Body", wikiProject);
		assertEquals(0, page0.getViewCount());
		System.out.println("\nFinished testViewCount");
	}
}
