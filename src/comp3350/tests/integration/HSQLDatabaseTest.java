package comp3350.tests.integration;

import java.util.ArrayList;

import comp3350.wiki.application.Services;
import comp3350.wiki.business.AccessChildren;
import comp3350.wiki.business.AccessPages;
import comp3350.wiki.business.AccessProjects;
import comp3350.wiki.objects.Page;
import comp3350.wiki.objects.Project;
import comp3350.wiki.persistence.DataAccess;
import junit.framework.TestCase;

public class HSQLDatabaseTest extends TestCase {
	AccessProjects projAccess;
	AccessPages pageAccess;
	DataAccess dataAccess;
	static int count = 0;

	public HSQLDatabaseTest(String arg0) {
		super(arg0);
		dataAccess = Services.createDataAccess("database/SC");
		projAccess = new AccessProjects();
	}

	private void end() {
		if (count == 19) {
			Services.closeDataAccess();
		} else {
			count++;
		}
	}

	public void testBasicAccess() {
		ArrayList<Project> projects = new ArrayList<Project>();
		ArrayList<Page> pages = new ArrayList<Page>();
		Project project;
		Project projectNull = dataAccess.getProject("project404");
		Page page;

		dataAccess.getAllProjects(projects);
		assertEquals(2, projects.size());
		assertEquals("Tutorial", projects.get(0).getTitle());

		project = dataAccess.getProject("project1");
		assertNotNull(project);
		assertEquals("Tutorial", project.getTitle());

		project = dataAccess.getProject("project5");
		assertNull(project);

		project = new Project("project4", "Test", null);
		dataAccess.insertProject(project);
		project = dataAccess.getProject("project4");
		assertNotNull(project);
		assertEquals("Test", project.getTitle());

		project = dataAccess.getProject("project4");
		project.setName("Test Update");
		dataAccess.updateProject(project);
		project = dataAccess.getProject("project4");
		assertEquals("Test Update", project.getTitle());

		dataAccess.deleteProject(project);
		project = dataAccess.getProject("project4");
		assertNull(project);

		project = dataAccess.getProject("project1");

		dataAccess.getPageSequential(project, pages);
		assertEquals(5, pages.size());
		assertEquals(5, dataAccess.getPageSequentialSize(project));
		assertEquals("Home Page", pages.get(0).getTitle());
		assertEquals("Second Page", pages.get(1).getTitle());
		assertEquals("Third Page", pages.get(2).getTitle());
		assertEquals("Odd Pages", pages.get(3).getTitle());
		assertEquals("Low Pages", pages.get(4).getTitle());

		pages.clear();
		dataAccess.getPageSequential(projectNull, pages);
		assertTrue(pages.isEmpty());

		page = dataAccess.getPage("p1");
		assertEquals("Home Page", page.getTitle());
		page = dataAccess.getPage("p404");
		assertNull(page);
		page = dataAccess.getPage(null);
		assertNull(page);

		pages = dataAccess.getAllPages(project);
		assertEquals(5, pages.size());
		assertEquals("Home Page", pages.get(0).getTitle());
		assertEquals("Second Page", pages.get(1).getTitle());
		assertEquals("Third Page", pages.get(2).getTitle());
		assertEquals("Odd Pages", pages.get(3).getTitle());
		assertEquals("Low Pages", pages.get(4).getTitle());
		pages = dataAccess.getAllPages(projectNull);
		assertTrue(pages.isEmpty());
		pages = dataAccess.getAllPages(null);
		assertTrue(pages.isEmpty());

		String result = dataAccess.status(dataAccess.getPage("p1"));
		assertEquals("ID Exists", result);
		result = dataAccess.status(new Page("p23", "Home Page", "Body", project));
		assertEquals("Duplicate Title Exists", result);
		result = dataAccess.status(null);
		assertEquals("Null Data", result);
		result = dataAccess.status(new Page("p159", "An Unique Title", "Body", null));
		assertEquals("Unique", result);

		page = new Page("p24", "Test Page", "Body", project);
		dataAccess.insertPage(project, page);
		page = dataAccess.getPage("p24");
		assertEquals("Test Page", page.getTitle());
		assertEquals("Body", page.getMarkdown());

		result = dataAccess.insertPage(project, null);
		assertEquals("ERROR: Page cannot be null", result);

		page.setMarkdown("Editted Body");
		dataAccess.updatePage(project, page);
		page = dataAccess.getPage("p24");
		assertEquals("Test Page", page.getTitle());
		assertEquals("Editted Body", page.getMarkdown());

		page.setTitle("New Title");
		dataAccess.setPage(project, page);
		page = dataAccess.getPage("p24");
		assertEquals("New Title", page.getTitle());
		assertEquals("Editted Body", page.getMarkdown());

		result = dataAccess.setPage(project, null);
		assertEquals("Null Data", result);

		pages = dataAccess.getChildren(dataAccess.getPage("p4"));
		assertEquals(2, pages.size());
		assertEquals("Home Page", pages.get(0).getTitle());
		assertEquals("Third Page", pages.get(1).getTitle());

		pages = dataAccess.getChildren(dataAccess.getPage("p1"));
		assertTrue(pages.isEmpty());

		pages = dataAccess.getParents(dataAccess.getPage("p1"));
		assertEquals(2, pages.size());
		assertEquals("Odd Pages", pages.get(0).getTitle());
		assertEquals("Low Pages", pages.get(1).getTitle());

		dataAccess.setParent(dataAccess.getPage("p4"), page);
		pages = dataAccess.getChildren(dataAccess.getPage("p4"));
		assertEquals(3, pages.size());
		assertEquals("Home Page", pages.get(0).getTitle());
		assertEquals("New Title", pages.get(1).getTitle());
		assertEquals("Third Page", pages.get(2).getTitle());

		pages = dataAccess.getNonParents(page);
		assertEquals(5, pages.size());
		assertEquals("Home Page", pages.get(0).getTitle());
		assertEquals("Second Page", pages.get(1).getTitle());
		assertEquals("New Title", pages.get(2).getTitle());
		assertEquals("Third Page", pages.get(3).getTitle());
		assertEquals("Low Pages", pages.get(4).getTitle());

		dataAccess.deletePage(project, page);
		page = dataAccess.getPage("p24");
		assertNull(page);
	}

	public void testGetWikiPages() {

		pageAccess = new AccessPages(projAccess.getProject("project1"));
		ArrayList<Page> pages = new ArrayList<Page>();

		System.out.println("\nStarting testGetWikiPages");

		pageAccess.getWikiPages(pages);
		assertTrue(pages.size() >= 5);
		assertTrue(5 <= pageAccess.getPageCount());

		pages = null;
		try {
			pageAccess.getWikiPages(pages);
			fail("Should have failed on null page");
		} catch (IllegalArgumentException err) {
		}

		System.out.println("\nFinished testGetWikiPages");
		end();
	}

	public void testGetSequential() {

		pageAccess = new AccessPages(projAccess.getProject("project2"));
		Page page = null;

		System.out.println("\nStarting testGetSequential");

		page = pageAccess.getSequential();
		assertEquals("Page Title 10", page.getTitle());

		page = pageAccess.getSequential();
		assertEquals("Page Title 11", page.getTitle());

		page = pageAccess.getSequential();
		assertEquals("Page Title 12", page.getTitle());

		page = pageAccess.getSequential();
		assertEquals("Page Title 13", page.getTitle());

		page = pageAccess.getSequential();
		assertEquals("Page Title 14", page.getTitle());

		page = pageAccess.getSequential();
		assertEquals("Page Title 15", page.getTitle());

		page = pageAccess.getSequential();
		assertEquals("Page Title 16", page.getTitle());

		page = pageAccess.getSequential();
		assertEquals("Page Title 17", page.getTitle());

		page = pageAccess.getSequential();
		assertEquals("Page Title 18", page.getTitle());

		page = pageAccess.getSequential();
		assertEquals("Page Title 19", page.getTitle());

		page = pageAccess.getSequential();
		assertEquals("Page Title 20", page.getTitle());

		page = pageAccess.getSequential();
		assertEquals("Page Title 6", page.getTitle());

		page = pageAccess.getSequential();
		assertEquals("Page Title 7", page.getTitle());

		page = pageAccess.getSequential();
		assertEquals("Page Title 8", page.getTitle());

		page = pageAccess.getSequential();
		assertEquals("Page Title 9", page.getTitle());

		page = pageAccess.getSequential();
		assertNull(page);

		page = pageAccess.getSequential();
		assertEquals("Page Title 10", page.getTitle());

		System.out.println("\nFinished testGetSequential");
		end();
	}

	public void testPageByID() {

		pageAccess = new AccessPages(projAccess.getProject("project1"));
		Page page = null;

		System.out.println("\nStarting testPageByID");

		page = pageAccess.getPage("p1");
		assertEquals("Home Page", page.getTitle());

		page = pageAccess.getPage("p2");
		assertEquals("Second Page", page.getTitle());

		page = pageAccess.getPage("p3");
		assertEquals("Third Page", page.getTitle());

		page = pageAccess.getPage("p200");
		assertNull(page);

		System.out.println("\nFinished testPageByID");
		end();
	}

	public void testGetWikiPagesBySearch() {
		Project project = new Project("testGetWikiPagesBySearch", "Test Search", "page1");
		projAccess.insertProject(project);

		pageAccess = new AccessPages(project);
		pageAccess.setPage(new Page("page1", "First Page", "Llama", project));
		pageAccess.setPage(new Page("page2", "Second Page", "LLAMA", project));
		pageAccess.setPage(new Page("page3", "Third", "Page", project));
		pageAccess.setPage(new Page("page4", "Page", "Fourth", project));
		pageAccess.setPage(new Page("page5", "Fifth", "Interruption Page Page", project));
		pageAccess.setPage(new Page("page6", "Sixth lLama", "Page", project));

		ArrayList<Page> pages = new ArrayList<Page>();

		System.out.println("\nStarting testGetWikiPagesBySearch");

		pageAccess.getWikiPagesBySearch(pages, "First");
		assertEquals(1, pages.size());
		assertEquals("First Page", pages.get(0).getTitle());
		pages.clear();

		pageAccess.getWikiPagesBySearch(pages, "Fourth");
		assertEquals(1, pages.size());
		assertEquals("Page", pages.get(0).getTitle());
		pages.clear();

		pageAccess.getWikiPagesBySearch(pages, "nonexistant_word");
		assertEquals(0, pages.size());
		pages.clear();

		pageAccess.getWikiPagesBySearch(pages, "Second Page");
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
		pageAccess.getWikiPagesBySearch(pages, "lLAMA");
		assertEquals(3, pages.size());
		pages.clear();

		// Always gets all pages with any of the terms
		pageAccess.getWikiPagesBySearch(pages, "llAma pAge");
		assertEquals(6, pages.size());
		pages.clear();

		pageAccess.getWikiPagesBySearch(pages, "");
		assertTrue(5 <= pages.size());

		pages.clear();

		try {
			pageAccess.getWikiPagesBySearch(null, "test");
			fail("Passing a null page list should throw an IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
		}
		projAccess.deleteProject(project);
		System.out.println("Finished testGetWikiPages");
		end();
	}

	public void testInsertPage() {
		pageAccess = new AccessPages(projAccess.getProject("project1"));

		Page page = new Page("p23", "Adding Test", "body", projAccess.getProject("project1"));

		System.out.println("\nStarting testInsertPage");

		pageAccess.setPage(page);

		page = pageAccess.getPage("p23");

		assertEquals("Adding Test", page.getTitle());
		pageAccess.deleteWikiPage(page);

		System.out.println("\nFinished testInsertPage");
		end();
	}

	public void testUpdatePage() {

		pageAccess = new AccessPages(projAccess.getProject("project1"));
		Page page;

		System.out.println("\nStarting testUpdatePage");

		page = pageAccess.getPage("p2");
		assertEquals("#Congratulations, the link worked.", page.getMarkdown());
		page.setMarkdown("#Changed\n The link worked.");

		pageAccess.setPage(page);

		page = pageAccess.getPage("p2");
		assertEquals("#Changed\n The link worked.", page.getMarkdown());
		page.setMarkdown("#Congratulations, the link worked.");
		pageAccess.setPage(page);

		System.out.println("\nFinished testUpdatePage");
		end();
	}

	public void testDeletePage() {
		pageAccess = new AccessPages(projAccess.getProject("project1"));
		Page page;

		System.out.println("\nStarting testDeletePage");

		page = new Page("p404", "Delete Test", "test", projAccess.getProject("project1"), 0);
		pageAccess.setPage(page);

		page = pageAccess.getPage("p404");
		pageAccess.deleteWikiPage(page);

		page = pageAccess.getPage("p404");
		assertNull(page);

		System.out.println("\nFinished testDeletePage");
		end();
	}

	public void testAutoHyperlink() {
		pageAccess = new AccessPages(projAccess.getProject("project1"));
		ArrayList<Page> pages = new ArrayList<Page>();
		String testBody;

		System.out.println("\nStarting testAutoHyperlink");

		pageAccess.getWikiPages(pages);

		testBody = pageAccess.setAutoHyperlinks("This page does not exist");
		assertEquals("This page does not exist", testBody);

		testBody = pageAccess.setAutoHyperlinks("This page does not exist Home Page.");
		assertTrue(testBody.contains("[Home Page](p1)"));

		System.out.println("Finished testAutoHyperlink");
		end();
	}

	public void testAutoHyperlinkNullBodyString() {

		pageAccess = new AccessPages(projAccess.getProject("project1"));

		System.out.println("\nStarting testAutoHyperlinkNullBodyString");

		try {
			pageAccess.setAutoHyperlinks(null);
			fail("Calling setAutoHyperlinks with a null argument should throw an IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
		} finally {
			System.out.println("Finished testAutoHyperlinkNullBodyString");
		}
		end();
	}

	public void testTopPages() {
		Project project = new Project("project3", "trial", null);
		projAccess.insertProject(project);
		pageAccess = new AccessPages(project);

		System.out.println("\nStarting testTopPages");

		Page firstPage = new Page("p404", "test1", "test", project);
		pageAccess.setPage(firstPage);
		Page secondPage = new Page("p405", "test4", "test", project);
		pageAccess.setPage(secondPage);
		Page thirdPage = new Page("p406", "test9", "test", project);
		pageAccess.setPage(thirdPage);

		ArrayList<Page> pages = pageAccess.getTopPages(3);
		assertEquals(3, pages.size());

		Page page = pageAccess.getPage(firstPage.getID());
		page.increaseViewCount();
		pageAccess.setPage(page);

		page = pageAccess.getPage(secondPage.getID());
		pageAccess.increaseViewCount(page);
		pageAccess.increaseViewCount(page);
		pageAccess.increaseViewCount(page);
		pageAccess.increaseViewCount(page);

		page = pageAccess.getPage(thirdPage.getID());
		pageAccess.increaseViewCount(page);
		pageAccess.increaseViewCount(page);
		pageAccess.increaseViewCount(page);
		pageAccess.increaseViewCount(page);
		pageAccess.increaseViewCount(page);
		page.increaseViewCount();
		page.increaseViewCount();
		page.increaseViewCount();
		page.increaseViewCount();
		pageAccess.setPage(page);

		pages = pageAccess.getTopPages(3);
		assertEquals("test9", pages.get(0).getTitle());
		assertEquals("test4", pages.get(1).getTitle());
		assertEquals("test1", pages.get(2).getTitle());
		assertEquals(9, pages.get(0).getViewCount());
		assertEquals(4, pages.get(1).getViewCount());
		assertEquals(1, pages.get(2).getViewCount());

		System.out.println("\nFinished testTopPage");
		projAccess.deleteProject(project);
		end();
	}

	static int counter = 3;

	public void testGetProjects() {
		projAccess = new AccessProjects();
		ArrayList<Project> projects = new ArrayList<Project>();

		System.out.println("\nStarting testGetProjects");

		projAccess.getProjects(projects);
		assertNotNull(projects);
		assertTrue(projects.size() >= 2);

		projects = null;
		try {
			projAccess.getProjects(projects);
			fail("Should have failed on null project");
		} catch (IllegalArgumentException err) {
		}

		System.out.println("\nFinished testGetProjects");
		end();
	}

	public void testGetProject() {
		projAccess = new AccessProjects();
		Project project = null;

		System.out.println("\nStarting testGetProject");

		project = projAccess.getProject("project1");
		assertEquals("Tutorial", project.getTitle());

		try {
			projAccess.getProject(null);
			fail("Should have failed on null string");
		} catch (IllegalArgumentException err) {
		}

		System.out.println("\nFinished testGetProject");
		end();
	}

	public void testInsertProject() {
		projAccess = new AccessProjects();

		Project project = new Project("project3", "Adding Test", null);

		System.out.println("\nStarting testInsertProject");

		projAccess.insertProject(project);
		project = projAccess.getProject("project3");
		assertEquals("Adding Test", project.getTitle());

		projAccess.deleteProject(project);
		System.out.println("\nFinished testInsertProject");
		end();
	}

	public void testUpdateProject() {
		projAccess = new AccessProjects();

		Project project = new Project("project3", "Testing Update", null);

		System.out.println("\nStarting testUpdateProject");

		projAccess.insertProject(project);

		project = projAccess.getProject("project3");

		assertEquals("Testing Update", project.getTitle());
		project.setName("New Name");

		projAccess.updateProject(project);

		project = projAccess.getProject("project3");
		assertNotNull(project);
		assertEquals("New Name", project.getTitle());

		projAccess.deleteProject(project);
		System.out.println("\nFinished testUpdateProject");
		end();
	}

	public void testDeleteProject() {
		projAccess = new AccessProjects();

		Project project = new Project("project3", "Deleting Test", null);
		projAccess.insertProject(project);

		System.out.println("\nStarting testDeleteProject");

		project = projAccess.getProject("project3");
		assertNotNull(project);

		projAccess.deleteProject(project);
		project = projAccess.getProject("project3");
		assertNull(project);
		System.out.println("\nFinished testDeleteProject");
		end();
	}

	AccessChildren children;

	public void testGetChildren() {
		AccessPages pages = new AccessPages("project1");
		System.out.println("\nStarting testGetChildren");
		children = new AccessChildren();

		Page firstPage = pages.getPage("p1");
		Page secondPage = pages.getPage("p2");
		Page thirdPage = pages.getPage("p3");

		Page oddCategory = pages.getPage("p4");
		ArrayList<Page> oddChildren = children.getChildren(oddCategory);

		Page lowCategory = pages.getPage("p5");
		ArrayList<Page> lowChildren = children.getChildren(lowCategory);

		System.out.println(thirdPage);
		assertTrue(oddChildren.contains(firstPage));
		assertTrue(oddChildren.contains(thirdPage));

		assertTrue(lowChildren.contains(firstPage));
		assertTrue(lowChildren.contains(secondPage));
		assertFalse(lowChildren.contains(thirdPage));

		System.out.println("\nFinished testGetChildren");
		end();
	}

	public void testSetParent() {
		children = new AccessChildren();
		pageAccess = new AccessPages("project1");
		Page testParent = new Page("p404", "parent", "body", projAccess.getProject("project1"));
		Page child = new Page("p405", "child", "body", projAccess.getProject("project1"));

		pageAccess.setPage(testParent);
		pageAccess.setPage(child);

		ArrayList<Page> listOfChildren = children.getChildren(testParent);
		assertEquals(0, listOfChildren.size());

		children.setParent(testParent, child);

		listOfChildren = children.getChildren(testParent);
		assertEquals(1, listOfChildren.size());

		pageAccess.deleteWikiPage(testParent);
		pageAccess.deleteWikiPage(child);
		end();
	}

	public void testGetNonParent() {
		children = new AccessChildren();
		AccessPages testAccess = new AccessPages("project1");
		Page testChild = testAccess.getPage("p2");

		ArrayList<Page> nonParent = children.getNonParents(testChild);
		assertTrue(3 <= nonParent.size());

		nonParent = children.getNonParents(null);
		assertEquals(0, nonParent.size());
		end();
	}

	public void testTopPage() {
		projAccess = new AccessProjects();
		System.out.println("\nStarting testTopPage");

		ArrayList<Project> projects = new ArrayList<Project>();
		projAccess.getProjects(projects);

		projAccess.getTopProjects(projects, 3);
		assertEquals(2, projects.size());

		Project project = new Project("project3", "Trial", null);
		project.increaseViewCount();
		projAccess.increaseViewCount(project);

		projAccess.insertProject(project);
		projAccess.getProjects(projects);

		projAccess.getTopProjects(projects, 3);
		assertEquals(3, projects.size());
		assertEquals("Trial", projects.get(0).getTitle());
		assertEquals(2, projects.get(0).getViewCount());

		projAccess.deleteProject(project);

		System.out.println("\nFinished testTopPage");
		end();
	}
}
