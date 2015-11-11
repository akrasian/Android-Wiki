package comp3350.tests.objects;

import junit.framework.TestCase;
import comp3350.wiki.objects.Project;

public class ProjectTest extends TestCase {
	public ProjectTest(String arg0) {
		super(arg0);
	}

	public void testCreateProject() {
		Project testProject;

		System.out.println("\nStarting testCreateProject");

		testProject = new Project("project3", "Test Project", null);

		assertNotNull(testProject);
		assertEquals("Test Project", testProject.getTitle());
		assertNull(testProject.getHomeID());
		assertEquals("Test Project", testProject.toString());
		assertEquals("project3", testProject.getID());

		System.out.println("Finishing testCreateProject");
	}

	public void testCopyProject() {
		Project testProject, originalProject;

		System.out.println("\nStarting testCopyProject");

		originalProject = new Project("project3", "Test Project", null);
		testProject = new Project("project3", "Test Project", null);

		assertNotNull(testProject);
		assertEquals("Test Project", testProject.getTitle());
		assertNull(testProject.getHomeID());
		assertEquals("Test Project", testProject.toString());
		assertEquals("project3", testProject.getID());

		assertTrue(testProject.equals(originalProject));

		System.out.println("Finishing testCopyProject");
	}

	public void testCallConstructorWithNullName() {
		Project testProject;
		System.out.println("\nStarting testCallConstructorWithNullName");

		try {
			testProject = new Project("project3", null, null);
			fail("null project title should be caught by constructor and thrown as an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
		}
		try {
			testProject = new Project(null, "title", null);
			fail("null projectID should be caught by constructor and thrown as an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
		}
		// Used to remove the not-used warning (since all the uses are wrapped
		// in try-catches, Eclipse doesn't seem
		// to count those as uses.
		testProject = new Project("project3", "test", null);
		testProject.setName("Getting rid of a warning");

		System.out.println("Finishing testCallConstructorWithNullName");
	}

	public void testSetProjectName() {
		Project testProject;

		System.out.println("\nStarting testSetProjectName");

		testProject = new Project("project3", "Test Project", null);

		assertNotNull(testProject);
		assertEquals("Test Project", testProject.getTitle());

		testProject.setName("New Project Name");

		assertEquals("New Project Name", testProject.getTitle());
		assertEquals("New Project Name", testProject.toString());

		try {
			testProject.setName(null);
			fail("null project title should be caught by constructor and thrown as an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
		}

		System.out.println("Finishing testSetProjectName");
	}

	public void testViewCount() {
		Project testProject;

		System.out.println("\nStarting testViewCount");

		testProject = new Project("project3", "Test Project", null);
		assertEquals(0, testProject.getViewCount());

		testProject.increaseViewCount();
		assertEquals(1, testProject.getViewCount());

		testProject.increaseViewCount();
		testProject.increaseViewCount();
		testProject.increaseViewCount();
		testProject.increaseViewCount();
		testProject.increaseViewCount();
		assertEquals(6, testProject.getViewCount());

		System.out.println("\nFinished testViewCount");
	}
}