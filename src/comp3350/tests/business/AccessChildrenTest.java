package comp3350.tests.business;

import java.util.ArrayList;

import comp3350.wiki.application.Services;
import comp3350.wiki.business.AccessChildren;
import comp3350.wiki.business.AccessPages;
import comp3350.wiki.objects.Page;
import comp3350.tests.persistence.*; // TODO This is just a quick fix
import junit.framework.TestCase;

public class AccessChildrenTest extends TestCase {
	AccessChildren children;

	public AccessChildrenTest(String arg0) {
		super(arg0);
		Services.createDataAccess(new DataAccessStub("wikiStub"));
		children = new AccessChildren();
	}

	public void testGetChildren() {
		AccessPages pages = new AccessPages("project1");
		System.out.println("\nStarting testGetChildren");
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
	}

	public void testSetParent() {
		AccessPages testAccess = new AccessPages("project1");
		Page testParent = testAccess.getPage("p4");
		Page child = testAccess.getPage("p2");

		ArrayList<Page> listOfChildren = children.getChildren(testParent);
		assertEquals(2, listOfChildren.size());

		children.setParent(testParent, child);

		listOfChildren = children.getChildren(testParent);
		assertEquals(3, listOfChildren.size());
	}

	public void testGetNonParent() {
		AccessPages testAccess = new AccessPages("project1");
		Page testChild = testAccess.getPage("p2");

		ArrayList<Page> nonParent = children.getNonParents(testChild);
		assertTrue(3 <= nonParent.size());

		nonParent = children.getNonParents(null);
		assertEquals(0, nonParent.size());
	}
}
