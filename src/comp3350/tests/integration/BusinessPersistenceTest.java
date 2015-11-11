package comp3350.tests.integration;

import java.util.ArrayList;

import comp3350.tests.persistence.DataAccessStub;
import comp3350.wiki.application.Services;
import comp3350.wiki.business.AccessChildren;
import comp3350.wiki.business.AccessPages;
import comp3350.wiki.business.AccessProjects;
import comp3350.wiki.objects.Page;
import comp3350.wiki.objects.Project;
import junit.framework.TestCase;

public class BusinessPersistenceTest extends TestCase {
	AccessProjects projectsAccess;
	AccessPages pagesAccess;
	AccessChildren childrenAccess;

	public BusinessPersistenceTest(String arg0) {
		super(arg0);
	}

	public void testSingleProject() {
		Services.createDataAccess(new DataAccessStub("wikiStub"));
		ArrayList<Project> projects = new ArrayList<Project>();
		projectsAccess = new AccessProjects();

		projectsAccess.getProjects(projects);
		assertEquals(2, projects.size());

		Project first = projectsAccess.getProject("project1");
		assertEquals("project1", first.getID());
		assertEquals("Tutorial", first.getTitle());

		pagesAccess = new AccessPages(first.getID());

		ArrayList<Page> pages = pagesAccess.getAllPages();
		assertEquals(5, pages.size());
		assertEquals(5, pagesAccess.getPageCount());
		pages.clear();
		pagesAccess.getWikiPages(pages);
		assertEquals(5, pages.size());

		Page page = pagesAccess.getPage(first.getHomeID());
		assertEquals("Home Page", page.getTitle());
		assertEquals("p1", page.getID());
		assertEquals(0, page.getViewCount());
		page.increaseViewCount();
		page.increaseViewCount();
		page.increaseViewCount();
		pagesAccess.setPage(page);
		assertEquals(3, page.getViewCount());

		page = pagesAccess.getPage("p2");
		assertEquals("p2", page.getID());
		assertEquals("Second Page", page.getTitle());

		page.setTitle("Editted Title");
		pagesAccess.setPage(page);

		page = pagesAccess.getPage("p2");
		assertEquals("Editted Title", page.getTitle());

		page.setTitle("Second Page");
		pagesAccess.setPage(page);

		page = pagesAccess.getPage("p4");

		childrenAccess = new AccessChildren();
		ArrayList<Page> children = childrenAccess.getChildren(page);
		assertEquals(2, children.size());
		page = children.get(0);

		assertEquals("Home Page", page.getTitle());

		page = new Page("p22", "A New Page",
				pagesAccess.setAutoHyperlinks("Add this with a link to Home Page"), first);
		pagesAccess.setPage(page);

		page = pagesAccess.getPage("p22");
		assertEquals("A New Page", page.getTitle());
		assertTrue(page.getMarkdown().contains("(p1)"));

		childrenAccess.setParent(pagesAccess.getPage("p4"), page);
		children = childrenAccess.getChildren(pagesAccess.getPage("p4"));
		assertEquals(3, children.size());
		children = childrenAccess.getParents(page);
		assertEquals(1, children.size());
		children = childrenAccess.getNonParents(page);
		assertEquals(5, children.size());
		pagesAccess.deleteWikiPage(page);

		assertEquals(null, pagesAccess.getPage("p22"));
		children = childrenAccess.getChildren(pagesAccess.getPage("p4"));
		assertEquals(2, children.size());

		pages.clear();
		pagesAccess.getWikiPagesBySearch(pages, "page");
		assertEquals(5, pages.size());

		page = pagesAccess.getSequential();
		assertEquals("Home Page", page.getTitle());

		page = pagesAccess.getSequential();
		assertEquals("Second Page", page.getTitle());
	}

	public void testMultipleProjects() {
		Services.createDataAccess(new DataAccessStub("wikiStub"));
		ArrayList<Project> projects = new ArrayList<Project>();
		projectsAccess = new AccessProjects();

		projectsAccess.getProjects(projects);
		assertEquals(2, projects.size());

		Project testProj = new Project("project3", "Test Project", null);
		assertEquals("project3", testProj.getID());
		assertEquals(null, testProj.getHomeID());

		projectsAccess.insertProject(testProj);

		projects.clear();
		projectsAccess.getProjects(projects);
		assertEquals(3, projects.size());

		Project proj = projectsAccess.getProject("project3");
		assertEquals("Test Project", proj.getTitle());

		proj.setName("Editting Name");
		projectsAccess.updateProject(proj);

		proj = projectsAccess.getProject("project3");
		assertEquals("Editting Name", proj.getTitle());

		Page page = new Page("p24", "Page", "Body", proj);
		pagesAccess = new AccessPages(proj);
		pagesAccess.setPage(page);

		page = new Page("p25", "Page 2", "Body", proj);
		pagesAccess = new AccessPages(proj);
		pagesAccess.setPage(page);
		proj.setHomePage(page);

		assertEquals(2, pagesAccess.getPageCount());

		proj = projectsAccess.getProject("project3");
		projectsAccess.deleteProject(proj);

		projectsAccess.getProjects(projects);
		assertEquals(2, projects.size());
	}

	public void testBreaking() {
		Services.createDataAccess(new DataAccessStub("wikiStub"));
		pagesAccess = new AccessPages("project5");
		Page page;
		ArrayList<Page> pages = null;

		pages = pagesAccess.getAllPages();
		assertEquals(0, pages.size());

		page = pagesAccess.getSequential();
		assertEquals(null, page);

		int count = pagesAccess.getPageCount();
		assertEquals(0, count);

		pagesAccess = new AccessPages("project1");
		page = pagesAccess.getPage("p404");
		assertNull(page);
		page = pagesAccess.getPage(null);
		assertNull(page);

		try {
			pagesAccess.getWikiPages(null);
			fail("Shouldn't have worked with the stub database.");
		} catch (Exception err) {
		}

		try {
			pagesAccess.setAutoHyperlinks(null);
			fail("Shouldn't have worked with the stub database.");
		} catch (Exception err) {
		}

		try {
			projectsAccess.insertProject(null);
			fail("Shouldn't have worked with the stub database.");
		} catch (Exception err) {
		}

		try {
			projectsAccess.updateProject(null);
			fail("Shouldn't have worked with the stub database.");
		} catch (Exception err) {
		}

		try {
			projectsAccess.deleteProject(null);
			fail("Shouldn't have worked with the stub database.");
		} catch (Exception err) {
		}
	}
}
