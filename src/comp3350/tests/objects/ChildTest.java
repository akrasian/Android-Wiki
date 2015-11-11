package comp3350.tests.objects;

import junit.framework.TestCase;
import comp3350.wiki.objects.Child;

public class ChildTest extends TestCase {
	public ChildTest(String arg0) {
		super(arg0);
	}

	public void testTypicalCase() {
		System.out.println("\nStarting testTypicalCase");

		Child test = new Child("5", "6");

		assertEquals("6", test.getChildID());
		assertEquals("5", test.getParentID());
		assertEquals("5 => 6", test.toString());

		System.out.println("\nFinished testTypicalCase");
	}

	public void testEquals() {
		System.out.println("\nStarting testEquals");

		Child test1 = new Child("1", "2");
		Child test2 = new Child("1", "2");
		Child test3 = new Child("1", "3");
		Child test4 = new Child("2", "2");
		Child test5 = new Child("2", "3");

		assertTrue(test1.equals(test2));
		assertFalse(test1.equals(test3));
		assertFalse(test1.equals(test4));
		assertFalse(test1.equals(test5));

		System.out.println("\nFinished testEquals");
	}
}
