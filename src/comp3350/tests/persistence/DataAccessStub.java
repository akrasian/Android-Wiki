package comp3350.tests.persistence;

import java.util.ArrayList;
import java.util.List;

import comp3350.wiki.persistence.DataAccess;
import comp3350.wiki.application.Main;
import comp3350.wiki.objects.Child;
import comp3350.wiki.objects.Page;
import comp3350.wiki.objects.Project;

public class DataAccessStub implements DataAccess {
	private String dbName;
	private String dbType = "stub";

	// If the database was used across multiple runs, these would need to a)
	// Be saved in the database, and b) for huge numbers of entries eventually
	// recovered. As it is just increasing the num is good enough.
	private long nextPageID = 0;
	private long nextProjectID = 0;

	// Imitating the layout of the database by having pages, projects, and
	// categories in separate arraylists for search.
	// This is obviously HORRRIBLY inefficient vs using many other data
	// structures and search methods, but translates trivially to searching
	// databases with select statements.
	private ArrayList<Project> projects;
	private ArrayList<Page> pages;
	private ArrayList<Child> children;

	public DataAccessStub(String dbName) {
		this.dbName = dbName;
		this.projects = new ArrayList<Project>();
		this.pages = new ArrayList<Page>();
		this.children = new ArrayList<Child>();
	}

	public DataAccessStub() {
		this(Main.dbName);
	}

	@Override
	public String newPageID() {
		nextPageID++;
		// Page IDs are kept short since they will be displayed when editing
		// pages.
		return "p" + nextPageID;
	}

	@Override
	public String newProjectID() {
		nextProjectID++;
		return "project" + nextProjectID;
	}

	@Override
	public void open(String dbName) {
		Project project;
		Page page;

		nextPageID = 0;
		nextProjectID = 0;
		projects.clear();
		pages.clear();

		project = new Project("project1", "Tutorial", "p1"); // Has no home page
																// yet.

		projects.add(project);

		Page firstPage = new Page("p1", "Home Page", "#Home\n"
				+ "This is the home page for a wiki\n" + "You can edit pages with markdown\n"
				+ "[You can also make links to other pages in the wiki](p2)", project);
		pages.add(firstPage);

		Page secondPage = new Page("p2", "Second Page", "#Congratulations, the link worked.",
				project);
		pages.add(secondPage);

		Page thirdPage = new Page("p3", "Third Page", "This page demonstrates quotes.\n"
				+ ">This is the first level of a block quote\n"
				+ ">>This is the second level of a block quote\n" + ">Ending first level\n"
				+ "Ending main level.", project);
		pages.add(thirdPage);

		Page oddCategory = new Page("p4", "Odd Pages", "All odd pages in this project", project);
		pages.add(oddCategory);

		Page lowCategory = new Page("p5", "Low Pages",
				"All pages with number below 2 in this project", project);
		pages.add(lowCategory);

		children.add(new Child(oddCategory, firstPage));
		children.add(new Child(oddCategory, thirdPage));

		children.add(new Child(lowCategory, firstPage));
		children.add(new Child(lowCategory, secondPage));

		project = new Project("project2", "Second Project", "p6"); // Has no
																	// home page
		// yet.

		projects.add(project);

		for (int i = 4; i < 20; i++) {
			page = new Page("p6", "Page Title " + i, "Page Content " + i, project);
			pages.add(page);
		}
		System.out.println("Opened " + dbType + " database " + dbName);
	}

	@Override
	public void close() {
		System.out.println("Closed " + dbType + " database " + dbName);
	}

	@Override
	public String getAllProjects(List<Project> projectResult) {
		projectResult.addAll(projects);
		return null;
	}

	@Override
	public Project getProject(String projectID) {
		if (projectID != null && projects != null) {
			for (Project p : projects) {
				if (p != null && projectID.compareTo(p.getID()) == 0) {
					return p;
				}
			}
		}

		return null;
	}

	@Override
	public void insertProject(Project currentProject) {
		int index = findProject(currentProject);
		if (index == -1) {
			projects.add(currentProject);
		}
	}

	public int findProject(Project currentProject) {
		return projects.indexOf(currentProject);
	}

	@Override
	public String updateProject(Project currentProject) {
		int index;
		index = projects.indexOf(currentProject);

		if (index >= 0) {
			projects.set(index, currentProject);
			return null;
		} else {
			return "Error on updating: Couldn't find the original project";
		}

	}

	@Override
	public String deleteProject(Project currentProject) {
		int index;
		String pageIndexes = "";
		index = projects.indexOf(currentProject);
		if (index >= 0) {

			for (int i = 0; i < pages.size(); i++) {
				if (pages.get(i).getProjectID().equals(currentProject.getID())) {
					pageIndexes = i + " " + pageIndexes;
				}
			}
			pageIndexes = pageIndexes.trim();
			String[] indexesStr = pageIndexes.split("\\s+");
			int[] indexes = new int[indexesStr.length];
			if (!indexesStr[0].isEmpty()) {
				for (int i = 0; i < indexesStr.length; i++) {
					indexes[i] = Integer.parseInt(indexesStr[i]);
				}

				for (int i = 0; i < indexes.length; i++) {
					deletePage(currentProject, pages.get(indexes[i]));
				}
			}
			projects.remove(index);
			return null;
		} else {
			return "Couldn't find the correct project. Nothing was deleted.";
		}
	}

	@Override
	public String getPageSequential(Project project, List<Page> outbound_pages) {
		outbound_pages.clear();

		if (project == null)
			return null;

		for (Page p : pages) {
			if (p.getProjectID().equals(project.getID())) {
				outbound_pages.add(p);
			}
		}
		return null;
	}

	@Override
	// This method is here to count the total number of pages within the given
	// project.
	// We need it so as to not violate Law of Demeter
	public int getPageSequentialSize(Project project) {
		int count = 0;
		for (Page p : pages) {
			if (p.getProjectID().equals(project.getID())) {
				count++;
			}
		}
		return count;
	}

	@Override
	public Page getPage(String pageID) {
		for (Page p : pages) {
			if (p != null && p.getID().equals(pageID)) {
				return p;
			}
		}

		return null;
	}

	@Override
	public ArrayList<Page> getAllPages(Project project) {
		ArrayList<Page> result = new ArrayList<Page>();
		for (Page p : pages) {
			if (p != null && project != null && p.getProjectID().equals(project.getID())) {
				result.add(p);
			}
		}
		return result;
	}

	public ArrayList<Page> getAllPages(String projectID) {
		ArrayList<Page> result = new ArrayList<Page>();
		for (Page p : pages) {
			if (p != null && p.getProjectID().equals(projectID)) {
				result.add(p);
			}
		}

		return result;
	}

	@Override
	public String status(Page page) {
		if (page == null)
			return "Error";
		for (Page p : pages) {
			if (p.getProjectID().equals(page.getProjectID())) {
				if (p.getID().equals(page.getID()))
					return "ID Exists";
				if (p.getTitle().equals(page.getTitle()))
					return "Duplicate Title Exists";
			}
		}
		return "Unique";
	}

	@Override
	public String setPage(Project project, Page page) {
		String status = status(page);

		int index = pages.indexOf(page);

		if (status.equals("ID Exists")) {
			pages.set(index, page);
			status = null;
		} else if (status.equals("Unique")) {
			pages.add(page);
			status = null;
		}

		return status;
	}

	@Override
	public String insertPage(Project project, Page currentPage) {
		if (currentPage == null)
			return "ERROR: Attempting to add null page to the wiki";
		pages.add(currentPage);
		return null;
	}

	@Override
	public String updatePage(Project project, Page currentPage) {
		int index;

		if (!project.getID().equals(currentPage.getProjectID())) {
			return "The page you're trying to update doesn't belong to that project.";
		}

		index = pages.indexOf(currentPage);

		if (index >= 0) {
			pages.set(index, currentPage);
		}

		return null;
	}

	@Override
	public void setParent(Page parent, Page child) {
		children.add(new Child(parent, child));
	}

	@Override
	public String deletePage(Project project, Page currentPage) {
		int index;

		if (!project.getID().equals(currentPage.getProjectID())) {
			return "The page you're trying to delete doesn't belong to that project.";
		}

		index = pages.indexOf(currentPage);

		if (index >= 0) {
			pages.remove(index);

			removeChildRelationshipsOf(currentPage.getID());
		}
		return null;
	}

	public void removeChildRelationshipsOf(String pageID) {
		// Now need to remove all child links that refer to this since it's not
		// in a database with on delete cascade.

		String pageIndexes = "";

		for (int i = 0; i < children.size(); i++) {
			if (children.get(i).getParentID().equals(pageID)
					|| children.get(i).getChildID().equals(pageID)) {
				pageIndexes = i + " " + pageIndexes;
			}
		}
		pageIndexes = pageIndexes.trim();

		String[] indexesStr = pageIndexes.split("\\s+");
		int[] indexes = new int[indexesStr.length];
		if (!indexesStr[0].isEmpty()) {
			for (int i = 0; i < indexesStr.length; i++) {
				indexes[i] = Integer.parseInt(indexesStr[i]);
			}
			for (int i = 0; i < indexes.length; i++) {
				children.remove(i);
			}
		}
	}

	@Override
	public ArrayList<Page> getChildren(Page parentPage) {
		ArrayList<Page> newChildren = new ArrayList<Page>();

		for (Child child : children) {
			if (child.getParentID().equals(parentPage.getID())) {
				newChildren.add(getPage(child.getChildID()));
			}
		}

		return newChildren;
	}

	@Override
	public ArrayList<Page> getParents(Page childPage) {
		ArrayList<Page> newParents = new ArrayList<Page>();

		if (childPage == null)
			return newParents;

		for (Child parent : children) {
			if (parent.getChildID().equals(childPage.getID())) {
				newParents.add(getPage(parent.getParentID()));
			}
		}

		return newParents;
	}

	@Override
	public ArrayList<Page> getNonParents(Page page) {

		if (page == null)
			return new ArrayList<Page>();

		ArrayList<Page> parents = getParents(page);
		ArrayList<Page> pages = getAllPages(page.getProjectID());
		pages.removeAll(parents);

		return pages;
	}

}
