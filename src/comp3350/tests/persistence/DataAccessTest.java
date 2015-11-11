package comp3350.tests.persistence;

import java.util.ArrayList;

import junit.framework.TestCase;
import comp3350.wiki.application.Services;
import comp3350.wiki.application.Main;
import comp3350.wiki.objects.Page;
import comp3350.wiki.objects.Project;
import comp3350.wiki.persistence.DataAccess;

public class DataAccessTest extends TestCase {
	private static String dbName = Main.dbName;
	static DataAccess dataAccess;

	public DataAccessTest(String arg0) {
		super(arg0);
	}

	public void testDataAccess() {
		Services.closeDataAccess();

		System.out.println("\nStarting Persistence test DataAccess (using stub)");

		// Use the following statement to run with the stub database
		dataAccess = (DataAccess) Services.createDataAccess(new DataAccessStub(dbName));

		dataAccessTest();

		System.out.println("Finished Persistence test DataAccess (using stub)");
	}

	public static void dataAccessTest() {
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
		assertEquals("Error", result);
		result = dataAccess.status(new Page("p159", "An Unique Title", "Body", null));
		assertEquals("Unique", result);

		page = new Page("p24", "Test Page", "Body", project);
		dataAccess.insertPage(project, page);
		page = dataAccess.getPage("p24");
		assertEquals("Test Page", page.getTitle());
		assertEquals("Body", page.getMarkdown());

		result = dataAccess.insertPage(project, null);
		assertEquals("ERROR: Attempting to add null page to the wiki", result);

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
		assertEquals("Error", result);

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
		assertEquals("Third Page", pages.get(1).getTitle());
		assertEquals("New Title", pages.get(2).getTitle());

		pages = dataAccess.getNonParents(page);
		assertEquals(5, pages.size());
		assertEquals("Home Page", pages.get(0).getTitle());
		assertEquals("Second Page", pages.get(1).getTitle());
		assertEquals("Third Page", pages.get(2).getTitle());
		assertEquals("Low Pages", pages.get(3).getTitle());
		assertEquals("New Title", pages.get(4).getTitle());

		dataAccess.deletePage(project, page);
		page = dataAccess.getPage("p24");
		assertNull(page);
	}
}