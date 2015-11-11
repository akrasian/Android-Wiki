package comp3350.wiki.business;

import java.util.ArrayList;

import comp3350.wiki.application.Main;
import comp3350.wiki.application.Services;
import comp3350.wiki.objects.Page;
import comp3350.wiki.persistence.DataAccess;

public class AccessChildren {
	private DataAccess dataAccess;

	public AccessChildren() {
		dataAccess = (DataAccess) Services.getDataAccess(Main.dbName);
	}

	public ArrayList<Page> getChildren(Page parent) {
		if (parent == null || parent.getID() == null) {
			System.out.println("Null pointer in " + parent);
			return new ArrayList<Page>();
		}

		String id = parent.getID();
		System.out.println("Looking for children of " + id);
		System.out.println("Project ID = " + parent.getProjectID());
		if (parent.getID().equals("all")) {
			return dataAccess.getAllPages(dataAccess.getProject(parent.getProjectID()));
		} else if (parent.getID().equals("help")) {
			return new ArrayList<Page>();
		} else {
			return dataAccess.getChildren(parent);
		}
	}

	public ArrayList<Page> getParents(Page child) {
		if (child == null || child.getID() == null)
			return new ArrayList<Page>();

		return dataAccess.getParents(child);
	}

	public ArrayList<Page> getNonParents(Page child) {
		if (child == null || child.getID() == null)
			return new ArrayList<Page>();

		return dataAccess.getNonParents(child);
	}

	public void setParent(Page parent, Page child) {
		dataAccess.setParent(parent, child);
	}
}
