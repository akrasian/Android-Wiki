package comp3350.tests.business;

import java.util.ArrayList;

import junit.framework.TestCase;
import comp3350.tests.persistence.DataAccessStub;
import comp3350.wiki.application.Services;
import comp3350.wiki.business.AccessPages;
import comp3350.wiki.business.AccessProjects;
import comp3350.wiki.objects.Page;
import comp3350.wiki.objects.Project;

public class AccessPagesTest extends TestCase {
	AccessProjects projAccess;
	AccessPages testAccess;

	public AccessPagesTest(String arg0) {
		super(arg0);
		Services.createDataAccess(new DataAccessStub("wikiStub"));
		projAccess = new AccessProjects();
	}

	public void testSetPage() {
		Project project = projAccess.getProject("project1");
		AccessPages testAccess = new AccessPages(project);

		System.out.println("\nStarting testSetPage");

		Page duplicateID = new Page("p4", "new and unused title", "", project, 0);
		assertEquals(null, testAccess.setPage(duplicateID));

		duplicateID.setTitle("Odd Pages");
		testAccess.setPage(duplicateID);

		// New ID with same Title is bad
		Page duplicateTitle = new Page("p999", "Low Pages", "", project, 0);
		assertEquals("Duplicate Title Exists", testAccess.setPage(duplicateTitle));

		Page unique = new Page("1000", "another unique title", "", project, 0);
		assertEquals(null, testAccess.setPage(unique));

		// Second insert with same ID and Title still works, it's only a problem
		// for new IDs and different Titles
		assertEquals(null, testAccess.setPage(unique));

		Page noLongerUniqueTitle = new Page("p1001", "another unique title", "", project, 0);
		assertEquals("Duplicate Title Exists", testAccess.setPage(noLongerUniqueTitle));

	}

	public void testGetWikiPagesBySearch() {
		Project project = new Project("testGetWikiPagesBySearch", "Test Search", "page1");
		projAccess.insertProject(project);

		AccessPages testAccess = new AccessPages(project);
		testAccess.setPage(new Page("page1", "First Page", "Llama", project));
		testAccess.setPage(new Page("page2", "Second Page", "LLAMA", project));
		testAccess.setPage(new Page("page3", "Third", "Page", project));
		testAccess.setPage(new Page("page4", "Page", "Fourth", project));
		testAccess.setPage(new Page("page5", "Fifth", "Interruption Page Page", project));
		testAccess.setPage(new Page("page6", "Sixth lLama", "Page", project));

		ArrayList<Page> pages = new ArrayList<Page>();

		System.out.println("\nStarting testGetWikiPagesBySearch");

		testAccess.getWikiPagesBySearch(pages, "First");
		assertEquals(1, pages.size());
		assertEquals("First Page", pages.get(0).getTitle());
		pages.clear();

		testAccess.getWikiPagesBySearch(pages, "Fourth");
		assertEquals(1, pages.size());
		assertEquals("Page", pages.get(0).getTitle());
		pages.clear();

		testAccess.getWikiPagesBySearch(pages, "nonexistant_word");
		assertEquals(0, pages.size());
		pages.clear();

		testAccess.getWikiPagesBySearch(pages, "Second Page");
		assertEquals(6, pages.size());
		// Second Page should have the most matches and it should show up only
		// once
		assertEquals("Second Page", pages.get(0).getTitle());
		// Some other page should be next
		for (int i = 1; i < 6; i++) {
			assertNotSame("Second Page", pages.get(i).getTitle());
		}
		pages.clear();

		// Case Insensitive and Order Insensitive to queries
		testAccess.getWikiPagesBySearch(pages, "lLAMA");
		assertEquals(3, pages.size());
		pages.clear();

		// Always gets all pages with any of the terms
		testAccess.getWikiPagesBySearch(pages, "llAma pAge");
		assertEquals(6, pages.size());
		pages.clear();

		testAccess.getWikiPagesBySearch(pages, "");
		assertTrue(5 <= pages.size());

		pages.clear();

		try {
			testAccess.getWikiPagesBySearch(null, "test");
			fail("Passing a null page list should throw an IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
		}
		projAccess.deleteProject(project);
		System.out.println("Finished testGetWikiPages");
	}

	public void testGetWikiPages() {

		testAccess = new AccessPages(projAccess.getProject("project1"));
		ArrayList<Page> pages = new ArrayList<Page>();

		System.out.println("\nStarting testGetWikiPages");

		testAccess.getWikiPages(pages);
		assertTrue(pages.size() >= 5);

		assertTrue(5 <= testAccess.getPageCount());

		pages = null;
		try {
			testAccess.getWikiPages(pages);
			fail("Should have failed on null page");
		} catch (IllegalArgumentException err) {
		}

		System.out.println("\nFinished testGetWikiPages");
	}

	public void testGetSequential() {

		testAccess = new AccessPages(projAccess.getProject("project2"));
		Page page = null;

		System.out.println("\nStarting testGetSequential");

		page = testAccess.getSequential();
		assertEquals("Page Title 4", page.getTitle());

		page = testAccess.getSequential();
		assertEquals("Page Title 5", page.getTitle());

		page = testAccess.getSequential();
		assertEquals("Page Title 6", page.getTitle());

		page = testAccess.getSequential();
		assertEquals("Page Title 7", page.getTitle());

		page = testAccess.getSequential();
		assertEquals("Page Title 8", page.getTitle());

		page = testAccess.getSequential();
		assertEquals("Page Title 9", page.getTitle());

		page = testAccess.getSequential();
		assertEquals("Page Title 10", page.getTitle());

		page = testAccess.getSequential();
		assertEquals("Page Title 11", page.getTitle());

		page = testAccess.getSequential();
		assertEquals("Page Title 12", page.getTitle());

		page = testAccess.getSequential();
		assertEquals("Page Title 13", page.getTitle());

		page = testAccess.getSequential();
		assertEquals("Page Title 14", page.getTitle());

		page = testAccess.getSequential();
		assertEquals("Page Title 15", page.getTitle());

		page = testAccess.getSequential();
		assertEquals("Page Title 16", page.getTitle());

		page = testAccess.getSequential();
		assertEquals("Page Title 17", page.getTitle());

		page = testAccess.getSequential();
		assertEquals("Page Title 18", page.getTitle());

		page = testAccess.getSequential();
		assertEquals("Page Title 19", page.getTitle());

		page = testAccess.getSequential();
		assertNull(page);

		page = testAccess.getSequential();
		assertEquals("Page Title 4", page.getTitle());

		System.out.println("\nFinished testGetSequential");
	}

	public void testPageByID() {

		testAccess = new AccessPages(projAccess.getProject("project1"));
		Page page = null;

		System.out.println("\nStarting testPageByID");

		page = testAccess.getPage("p1");
		assertEquals("Home Page", page.getTitle());

		page = testAccess.getPage("p2");
		assertEquals("Second Page", page.getTitle());

		page = testAccess.getPage("p3");
		assertEquals("Third Page", page.getTitle());

		page = testAccess.getPage("p200");
		assertNull(page);

		System.out.println("\nFinished testPageByID");
	}

	public void testInsertPage() {

		testAccess = new AccessPages(projAccess.getProject("project1"));

		Page page = new Page("p22", "Adding Test", "body", projAccess.getProject("project1"));

		System.out.println("\nStarting testInsertPage");

		testAccess.setPage(page);

		page = testAccess.getPage("p22");
		assertEquals("Adding Test", page.getTitle());

		String error = testAccess.setPage(null);
		assertEquals("No ID", error);

		System.out.println("\nFinished testInsertPage");
	}

	public void testUpdatePage() {

		testAccess = new AccessPages(projAccess.getProject("project1"));
		Page page;

		System.out.println("\nStarting testUpdatePage");

		page = testAccess.getPage("p2");
		assertEquals("#Congratulations, the link worked.", page.getMarkdown());
		page.setMarkdown("#Changed\n The link worked.");

		testAccess.setPage(page);

		page = testAccess.getPage("p2");
		assertEquals("#Changed\n The link worked.", page.getMarkdown());

		System.out.println("\nFinished testUpdatePage");
	}

	public void testDeletePage() {
		testAccess = new AccessPages(projAccess.getProject("project1"));
		Page page;

		System.out.println("\nStarting testDeletePage");

		page = new Page("p404", "Delete Test", "test", projAccess.getProject("project1"), 0);
		testAccess.setPage(page);

		page = testAccess.getPage("p404");
		testAccess.deleteWikiPage(page);

		page = testAccess.getPage("p404");
		assertNull(page);

		System.out.println("\nFinished testDeletePage");
	}

	public void testAuto() {
		projAccess = new AccessProjects();
		testAccess = new AccessPages(projAccess.getProject("project1"));

		String test = " Home Page test";
		String test1 = testAccess.setAutoHyperlinks(test);
		String test2 = testAccess.setAutoHyperlinks(test1);

		assertEquals(" [Home Page](p1) test", test1);
		assertEquals(test1, test2);

		test = "Home Page test";
		test1 = testAccess.setAutoHyperlinks(test);
		test2 = testAccess.setAutoHyperlinks(test1);

		assertEquals("[Home Page](p1) test", test1);
		assertEquals(test1, test2);

		test = " Home Page.test";
		test1 = testAccess.setAutoHyperlinks(test);
		test2 = testAccess.setAutoHyperlinks(test1);

		assertEquals(" [Home Page](p1).test", test1);
		assertEquals(test1, test2);

		test = "Home Pagetest";
		test1 = testAccess.setAutoHyperlinks(test);

		assertEquals(test, test1);
	}

	public void testAutoHyperlinkNullBodyString() {

		testAccess = new AccessPages(projAccess.getProject("project1"));

		System.out.println("\nStarting testAutoHyperlinkNullBodyString");

		try {
			testAccess.setAutoHyperlinks(null);
			fail("Calling setAutoHyperlinks with a null argument should throw an IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
		} finally {
			System.out.println("Finished testAutoHyperlinkNullBodyString");
		}
	}

	public void testTopPages() {
		testAccess = new AccessPages(projAccess.getProject("project1"));

		System.out.println("\nStarting testTopPages");

		ArrayList<Page> pages = testAccess.getTopPages(3);
		assertEquals(3, pages.size());

		Page page = testAccess.getPage("p5");
		page.increaseViewCount();
		page.increaseViewCount();

		page = testAccess.getPage("p4");
		testAccess.increaseViewCount(page);

		pages = testAccess.getTopPages(3);
		assertEquals("Low Pages", pages.get(0).getTitle());
		assertEquals("Odd Pages", pages.get(1).getTitle());
		assertEquals(2, pages.get(0).getViewCount());
		assertEquals(1, pages.get(1).getViewCount());

		System.out.println("\nFinished testTopPage");
	}
}
