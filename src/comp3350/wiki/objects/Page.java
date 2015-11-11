package comp3350.wiki.objects;

import java.io.IOException;

import org.markdown4j.Markdown4jProcessor;

public class Page {
	private String title;
	private String markdown;
	private String id;
	private String projectID;
	private int viewCount;

	public Page(String pageID, String title, String body, Project project) {
		this(pageID, title, body, project, 0);
	}

	public Page(String pageID, String title, String body, Project project, int viewCount) {
		this.id = pageID;
		this.title = title;
		this.markdown = body;
		if (project == null) {
			this.projectID = null;
		} else {
			this.projectID = project.getID();
		}
		this.viewCount = viewCount;
	}

	public String getProjectID() {
		return projectID;
	}

	public String getTitle() {
		return title;
	}

	public String getHTML() {
		// Replace [link title](pageID) with [link title](http://pageid/)
		String html = "";
		try {
			html = new Markdown4jProcessor().process(markdown);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			html = "<p>Oops! There are errors in your markdown syntax</p>";
		}

		html = html.replaceAll("href=\"(\\w*)\"", "href=\"http://$1/\"");

		return html;
	}

	public String getMarkdown() {
		return markdown;
	}

	public String getLink() {
		return "http://" + id + "/";
	}

	public String getID() {
		return id;
	}

	public int getViewCount() {
		return viewCount;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setMarkdown(String body) {
		this.markdown = body;
	}

	@Override
	public boolean equals(Object object) {
		boolean result;
		Page s;

		result = false;

		if (object instanceof Page) {
			s = (Page) object;
			if (((s.getID() == null) && (id == null)) || (s.getID().equals(id))) {
				result = true;
			}
		}
		return result;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	public String toString() {
		return this.title;
	}

	public void increaseViewCount() {
		this.viewCount++;
	}
}
