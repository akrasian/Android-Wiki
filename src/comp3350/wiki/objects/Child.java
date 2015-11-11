package comp3350.wiki.objects;

public class Child {

	private String parentID;
	private String childID;

	public Child(String newParentID, String newChldID) {
		parentID = newParentID;
		childID = newChldID;
	}

	public Child(Page newParent, Page newChild) {
		parentID = newParent.getID();
		childID = newChild.getID();
	}

	public String getParentID() {
		return parentID;
	}

	public String getChildID() {
		return childID;
	}

	public String toString() {
		return parentID + " => " + childID;
	}

	public boolean equals(Object object) {
		boolean result = false;

		if (object instanceof Child) {
			Child child = (Child) object;
			if (child.parentID.equals(parentID) && child.childID.equals(childID)) {
				result = true;
			}
		}
		return result;
	}
}
