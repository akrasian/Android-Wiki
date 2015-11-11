package comp3350.wiki.objects;

public class Project {
	private String title;
	private String projectID;
	private String homeID;
	private int viewCount;

	public Project(String projectID, String title, String home) {
		this(projectID, title, home, 0);
	}

	public Project(String projectID, String title, String home, int viewCount) {
		if (projectID == null)
			throw new IllegalArgumentException("projectID is null");
		if (title == null)
			throw new IllegalArgumentException("title is null");

		this.title = title;
		this.homeID = home;
		this.viewCount = viewCount;
		this.projectID = projectID;
	}

	public String toString() {
		return title;
	}

	public String getTitle() {
		return this.title;
	}

	public String getHomeID() {
		return homeID;
	}

	public void setHomePage(Page homepage) {
		homeID = homepage.getID();
	}

	public void setName(String name) {
		if (name == null)
			throw new IllegalArgumentException("new name is null");
		this.title = name;
	}

	public boolean equals(Object object) {
		boolean result;
		Project s;

		result = false;

		if (object instanceof Project) {
			s = (Project) object;
			if (((s.getID() == null) && (projectID == null)) || (s.getID().equals(projectID))) {
				result = true;
			}
		}
		return result;
	}

	public String getID() {
		return projectID;
	}

	public int getViewCount() {
		return viewCount;
	}

	public void increaseViewCount() {
		this.viewCount++;
	}
}