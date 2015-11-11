package comp3350.tests.business;

import java.util.ArrayList;

import junit.framework.TestCase;
import comp3350.tests.persistence.DataAccessStub;
import comp3350.wiki.objects.Project;
import comp3350.wiki.application.Services;
import comp3350.wiki.business.AccessProjects;

public class AccessProjectsTest extends TestCase {
	AccessProjects testAccess;

	public AccessProjectsTest(String arg0) {
		super(arg0);
		Services.createDataAccess(new DataAccessStub("wikiStub"));
		testAccess = new AccessProjects();
	}

	public void testGetProjects() {
		testAccess = new AccessProjects();
		ArrayList<Project> projects = new ArrayList<Project>();

		System.out.println("\nStarting testGetProjects");

		testAccess.getProjects(projects);
		assertNotNull(projects);
		assertTrue(projects.size() >= 2);

		projects = null;
		try {
			testAccess.getProjects(projects);
			fail("Should have failed on null project");
		} catch (IllegalArgumentException err) {
		}

		System.out.println("\nFinished testGetProjects");
	}

	public void testGetProject() {
		testAccess = new AccessProjects();
		Project project = null;

		System.out.println("\nStarting testGetProject");

		project = testAccess.getProject("project1");
		assertEquals("Tutorial", project.getTitle());

		try {
			testAccess.getProject(null);
			fail("Should have failed on null string");
		} catch (IllegalArgumentException err) {
		}

		System.out.println("\nFinished testGetProject");
	}

	public void testInsertProject() {
		testAccess = new AccessProjects();

		Project project = new Project("project4", "Adding Test", null);

		System.out.println("\nStarting testInsertProject");

		testAccess.insertProject(project);
		project = testAccess.getProject(project.getID());
		assertEquals("Adding Test", project.getTitle());

		testAccess.deleteProject(project);
		System.out.println("\nFinished testInsertProject");
	}

	public void testUpdateProject() {
		testAccess = new AccessProjects();

		Project project = new Project("project4", "Testing Update", null);

		System.out.println("\nStarting testUpdateProject");

		testAccess.insertProject(project);

		project = testAccess.getProject("project4");

		assertEquals("Testing Update", project.getTitle());
		project.setName("New Name");

		testAccess.updateProject(project);

		project = testAccess.getProject("project4");
		assertNotNull(project);
		assertEquals("New Name", project.getTitle());

		testAccess.deleteProject(project);
		System.out.println("\nFinished testUpdateProject");
	}

	public void testDeleteProject() {
		testAccess = new AccessProjects();

		Project project = new Project("project4", "Deleting Test", null);
		testAccess.insertProject(project);

		System.out.println("\nStarting testDeleteProject");

		project = testAccess.getProject("project4");
		assertNotNull(project);

		testAccess.deleteProject(project);
		project = testAccess.getProject("project4");
		assertNull(project);
		System.out.println("\nFinished testDeleteProject");
	}

	public void testTopPage() {
		testAccess = new AccessProjects();
		System.out.println("\nStarting testTopPage");

		ArrayList<Project> projects = new ArrayList<Project>();
		testAccess.getProjects(projects);

		testAccess.getTopProjects(projects, 3);
		assertEquals(2, projects.size());

		Project project = new Project("project4", "Trial", null);
		project.increaseViewCount();
		testAccess.increaseViewCount(project);

		testAccess.insertProject(project);
		testAccess.getProjects(projects);

		testAccess.getTopProjects(projects, 3);
		assertEquals(3, projects.size());
		assertEquals("Trial", projects.get(0).getTitle());

		testAccess.deleteProject(project);

		System.out.println("\nFinished testTopPage");
	}
}