package comp3350.wiki.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import comp3350.wiki.application.Main;
import comp3350.wiki.application.Services;
import comp3350.wiki.objects.Page;
import comp3350.wiki.objects.Project;
import comp3350.wiki.persistence.DataAccess;

public class AccessPages {
	private DataAccess dataAccess;
	private Project project;
	private Page page;

	// For sequential access: These are not kept in sync with the pages in the
	// database.
	private int currentPage;
	private List<Page> pages;

	public AccessPages(Project project) {
		if (project == null)
			System.err.println("Passed null project to access pages");

		dataAccess = (DataAccess) Services.getDataAccess(Main.dbName);
		this.project = project;
		pages = null;
		page = null;
		currentPage = 0;
	}

	public void appendCategories(Page currentPage, ArrayList<Page> sidebar_links) {
		if (currentPage == null || sidebar_links == null)
			return;

		if (dataAccess == null)
			System.err.println("Data access is null!");

		// Append a linkable item to this sidebar_links array for every category
		// which this is a member of.
		ArrayList<Page> links = dataAccess.getChildren(currentPage);
		if (links == null)
			return;

		sidebar_links.addAll(links);
	}

	public AccessPages(String projectID) {
		dataAccess = (DataAccess) Services.getDataAccess(Main.dbName);
		this.project = dataAccess.getProject(projectID);

		pages = null;
		page = null;
		currentPage = 0;
	}

	public void getWikiPagesBySearch(List<Page> pages, String query) {
		if (pages == null)
			throw new IllegalArgumentException("Access Pages: pages cannot be null");
		pages.clear();

		ArrayList<Page> searchPages = getAllPages();
		ArrayList<SearchResult> unsortedPages = new ArrayList<SearchResult>();

		for (Page p : searchPages) {
			double score = searchScore(p, query);
			if (score > 0) {
				unsortedPages.add(new SearchResult(p, score));
			}
		}

		Collections.sort(unsortedPages);

		for (SearchResult s : unsortedPages) {
			pages.add(s.page);
		}
	}

	private double searchScore(Page page, String query) {
		// Score is currently based only one the number of search keywords found
		// in page + title
		// This could be made much more sophisticated but I'm just uploading the
		// simplest to test for now.

		String title = page.getTitle().toLowerCase(Locale.ENGLISH);
		String body = page.getMarkdown().toLowerCase(Locale.ENGLISH);
		query = query.toLowerCase(Locale.ENGLISH);

		String[] searchTokens = query.split("\\s+");

		double score = 0;

		for (String token : searchTokens) {
			if (body.contains(token) || title.contains(token)) {
				score++;
			}
		}

		return score;
	}

	// Using the native score requires an array of comparable objects, need to
	// save score with page hence this container class.
	private class SearchResult implements Comparable<SearchResult> {
		public Page page;
		public double score;

		SearchResult(Page page, double score) {
			this.page = page;
			this.score = score;
		}

		@Override
		public int compareTo(SearchResult other) {
			int result = 0;
			if (score < other.score) {
				result = 1;
			} else if (score > other.score) {
				result = -1;
			}

			return result;
		}
	}

	public Page getSequential() {
		// AccessPages will have an internal array of pages just for the purpose
		// of presenting them in order to the outside world I guess...

		if (pages == null) {
			pages = new ArrayList<Page>();
			dataAccess.getPageSequential(project, pages);
			currentPage = 0;
		}
		if (currentPage < pages.size()) {
			page = (Page) pages.get(currentPage);
			currentPage++;
		} else {
			pages = null;
			page = null;
			currentPage = 0;
		}
		return page;
	}

	public Page getPage(String pageID) {
		System.out.println("Getting page " + pageID);

		if (pageID == null || pageID.equals(""))
			return null;

		Page result = null;

		if (pageID.equals("all")) {
			result = createAllPage();
		} else if (pageID.equals("help")) {
			result = createHelpPage();
		} else {
			result = dataAccess.getPage(pageID);
		}

		return result;
	}

	public ArrayList<Page> getAllPages() {
		return dataAccess.getAllPages(project);
	}

	public String setPage(Page page) {
		if (page == null || page.getID() == null || page.getID().equals("")) {
			return "No ID";
		}

		if (page.getTitle().equals("")) {
			return "No Title";
		}

		if (page.getID().equals("all") || page.getID().equals("help")) {
			return "Cannot change constant pages";
		}

		return dataAccess.setPage(project, page);
	}

	public String deleteWikiPage(Page currentPage) {
		return dataAccess.deletePage(project, currentPage);
	}

	public String getWikiPages(List<Page> pages) {
		if (pages == null)
			throw new IllegalArgumentException("Access Pages: pages cannot be null");
		pages.clear();
		return dataAccess.getPageSequential(project, pages);
	}

	public String setAutoHyperlinks(String body) {
		if (body == null)
			throw new IllegalArgumentException("setAutoHyperlinks: body String cannot be null");

		ArrayList<Page> pages = new ArrayList<Page>();
		String currTitle;

		getWikiPages(pages);

		if (pages.size() > 0) {
			for (Page currPage : pages) {
				currTitle = currPage.getTitle();
				body = body.replaceAll("(?<=\\s|^|\"|\')" + currTitle
						+ "(?=\\s|\\.|\\,|\\!|\\?|$|;|\"|\')", "[" + currPage.getTitle() + "]("
						+ currPage.getID() + ")");
			}
		}

		return body;
	}

	public int getPageCount() {
		if (project == null) {
			System.err.println("PageAccess: The project is null");
			return 0;
		}
		return dataAccess.getPageSequentialSize(project);
	}

	public void increaseViewCount(Page currPage) {
		currPage.increaseViewCount();
		setPage(currPage);
	}

	// Returns the top num projects, if there's fewer than num projects, returns
	// all of the projects.
	public ArrayList<Page> getTopPages(int num) {
		ArrayList<Page> currPages = getAllPages();
		Collections.sort(currPages, new PageViewComparator());
		while (currPages.size() > num) {
			currPages.remove(currPages.size() - 1);
		}
		return currPages;
	}

	public Page createNewPage(String title, String body) {
		String pageID = dataAccess.newPageID();
		return new Page(pageID, title, body, project);
	}

	public Page createAllPage() {
		return new Page("all", "All", "All Pages in " + project, project);
	}

	public Page createHelpPage() {
		return new Page(
				"help",
				"Help",
				"##Help\nTo view categories in the wiki, open the sliding drawer on the left. 'All' contains all pages, the other categories are user editable.\nAny page which is a category will list the children at the bottom of the page, see below for an example.",
				project);
	}

	class PageViewComparator implements Comparator<Page> {
		@Override
		public int compare(Page page1, Page page2) {
			return page2.getViewCount() - page1.getViewCount();
		}
	}
}