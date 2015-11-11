package comp3350.wiki.persistence;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.List;

import comp3350.wiki.objects.Page;
import comp3350.wiki.objects.Project;

public class DataAccessObject implements DataAccess {
	private Connection db;

	private String dbName;
	private String dbType;

	private String cmdString;

	public DataAccessObject(String dbName) {
		this.dbName = dbName;
	}

	@Override
	public void open(String dbPath) {
		String url;
		try {
			System.out.println("Creating connection");
			// Setup for HSQL
			dbType = "HSQL";
			Class.forName("org.hsqldb.jdbcDriver").newInstance();
			url = "jdbc:hsqldb:file:" + dbPath;

			db = DriverManager.getConnection(url, "SA", "");
			initData();

		} catch (Exception e) {
			processSQLError(e);
		}
		System.out.println("Opened " + dbType + " database " + dbPath);
	}

	public void initData() {
	}

	@Override
	public void close() {
		try { // commit all changes to the database
			cmdString = "shutdown compact";
			Statement statement = db.createStatement();
			statement.executeQuery(cmdString);
			db.close();
		} catch (Exception e) {
			processSQLError(e);
		}
		System.out.println("Closed " + dbType + " database " + dbName);
	}

	@Override
	public String newPageID() {
		// Kludge: Find free ID
		int possibleID = 1;
		String workingID = null;

		while (workingID == null) {
			workingID = "p" + possibleID;

			if (getPage(workingID) != null) {
				// This projectID already in use.
				workingID = null;
			}
			possibleID++;
		}

		return workingID;
	}

	@Override
	public String newProjectID() {
		// Kludge: Find free ID
		int possibleID = 0;
		String workingID = null;

		while (workingID == null) {
			workingID = "project" + possibleID;

			if (getProject(workingID) != null) {
				// This projectID already in use.
				workingID = null;
			}
			possibleID++;
		}

		return workingID;
	}

	public String checkWarning(Statement st, int updateCount) {
		String result;

		result = null;
		try {
			SQLWarning warning = st.getWarnings();
			if (warning != null) {
				result = warning.getMessage();
			}
		} catch (Exception e) {
			result = processSQLError(e);
		}
		if (updateCount != 1) {
			result = "Tuple not inserted correctly.";
		}
		return result;
	}

	public String processSQLError(Exception e) {
		String result = "*** SQL Error: " + e.getMessage();

		e.printStackTrace();

		return result;
	}

	@Override
	public String getAllProjects(List<Project> projectResult) {
		String result = null;
		try {
			cmdString = "Select * from projects";
			Statement statement = db.createStatement();
			ResultSet rs4 = statement.executeQuery(cmdString);

			while (rs4.next()) {
				String id = rs4.getString("id");
				String title = rs4.getString("title");
				String home = rs4.getString("home");
				int viewCount = rs4.getInt("views");
				projectResult.add(new Project(id, title, home, viewCount));
			}
			rs4.close();
		} catch (Exception e) {
			processSQLError(e);
		}

		return result;
	}

	@Override
	public ArrayList<Page> getParents(Page page) {
		ArrayList<Page> result = new ArrayList<Page>();
		try {
			cmdString = "select * from children where child = ?";
			PreparedStatement statement = db.prepareStatement(cmdString);
			statement.setString(1, page.getID());

			ResultSet row = statement.executeQuery();

			// This only ever gets the first one!!!
			while (row.next()) {
				String parent = row.getString("parent");

				result.add(getPage(parent));
			}
			row.close();
		} catch (Exception e) {
			processSQLError(e);
		}

		return result;
	}

	@Override
	public Project getProject(String projectID) {
		Project result = null;

		try {
			cmdString = "Select * from projects where id = ?";
			PreparedStatement statement = db.prepareStatement(cmdString);
			statement.setString(1, projectID);
			ResultSet row = statement.executeQuery();

			while (row.next()) {
				String id = row.getString("id");
				String title = row.getString("title");
				String home = row.getString("home");
				int viewCount = row.getInt("views");

				result = new Project(id, title, home, viewCount);
			}
			row.close();
		} catch (Exception e) {
			processSQLError(e);
		}

		return result;
	}

	@Override
	public void insertProject(Project project) {
		try {
			cmdString = "insert into projects (id, title, home, views) values (?, ?, ?, ?)";
			PreparedStatement s = db.prepareStatement(cmdString);

			s.setString(1, project.getID());
			s.setString(2, project.getTitle());
			s.setString(3, project.getHomeID());
			s.setInt(4, project.getViewCount());

			s.executeUpdate();
		} catch (Exception e) {
			processSQLError(e);
		}

	}

	@Override
	public void setParent(Page parent, Page child) {
		try {
			cmdString = "insert into children (parent, child) values (?, ?)";
			PreparedStatement s = db.prepareStatement(cmdString);
			s.setString(1, parent.getID());
			s.setString(2, child.getID());

			s.executeUpdate();
		} catch (Exception e) {
			processSQLError(e);
		}
	}

	@Override
	public String updateProject(Project project) {

		try {
			cmdString = "update projects set title = ?, home = ?, views = ? where id = ?";
			PreparedStatement s = db.prepareStatement(cmdString);

			s.setString(1, project.getTitle());
			s.setString(2, project.getHomeID());
			s.setInt(3, project.getViewCount());
			s.setString(4, project.getID());

			s.executeUpdate();
		} catch (Exception e) {
			processSQLError(e);
		}
		return null;
	}

	@Override
	public String deleteProject(Project project) {

		try {
			cmdString = "delete from projects where id = ?";
			PreparedStatement s = db.prepareStatement(cmdString);
			s.setString(1, project.getID());

			// On delete cascade will delete categories and pages involved.
			s.executeUpdate();
		} catch (Exception e) {
			processSQLError(e);
		}

		return null;
	}

	@Override
	public String getPageSequential(Project project, List<Page> pageResult) {
		if (project != null) {
			try {
				cmdString = "select * from pages where project = ?";
				PreparedStatement s = db.prepareStatement(cmdString);
				s.setString(1, project.getID());

				// On delete cascade will delete categories and pages involved.
				ResultSet row = s.executeQuery();

				while (row.next()) {

					String id = row.getString("id");
					String body = row.getString("body");
					String title = row.getString("title");
					int viewCount = row.getInt("views");
					pageResult.add(new Page(id, title, body, project, viewCount));
				}
				row.close();
			} catch (Exception e) {
				processSQLError(e);
			}
		}

		return null;
	}

	@Override
	public Page getPage(String currentPage) {
		Page result = null;

		if (currentPage == null)
			return result;

		try {
			cmdString = "select * from pages where id = ?";
			PreparedStatement s = db.prepareStatement(cmdString);
			s.setString(1, currentPage);
			ResultSet row = s.executeQuery();

			while (row.next()) {

				String id = row.getString("id");
				String body = row.getString("body");
				String title = row.getString("title");
				String projectID = row.getString("project");
				Project project = getProject(projectID);
				int viewCount = row.getInt("views");
				result = new Page(id, title, body, project, viewCount);
			}
			row.close();
		} catch (Exception e) {
			processSQLError(e);
		}
		return result;
	}

	@Override
	public String status(Page page) {
		if (page == null || page.getID() == null)
			return "Null Data";

		try {
			cmdString = "select * from pages where (id = ? or title = ?) and project = ?";
			PreparedStatement s = db.prepareStatement(cmdString);
			s.setString(1, page.getID());
			s.setString(2, page.getTitle());
			s.setString(3, page.getProjectID());
			ResultSet row = s.executeQuery();

			if (row.next()) {
				String id = row.getString("id");
				String title = row.getString("title");

				if (id.equals(page.getID())) {
					return "ID Exists";
				} else if (title.equals(page.getTitle())) {
					return "Duplicate Title Exists";
				}
			}
			row.close();
		} catch (Exception e) {
			processSQLError(e);
			return "Error";
		}

		return "Unique";
	}

	@Override
	public String setPage(Project project, Page page) {
		// A: Assume page is in wiki and update
		String status = "Unknown Error while Saving";
		try {
			status = status(page);

			if (status.equals("ID Exists")) {
				updatePage(project, page);
				status = null;
			} else if (status.equals("Unique")) {
				insertPage(project, page);
				status = null;
			}
		} catch (Exception e) {
			processSQLError(e);
		}
		return status;
	}

	@Override
	public String insertPage(Project project, Page page) {
		if (page == null) {
			return "ERROR: Page cannot be null";
		}
		try {
			cmdString = "insert into pages (id, body, title, views, project) values (?, ?, ?, ?, ?)";
			PreparedStatement s = db.prepareStatement(cmdString);
			s.setString(1, page.getID());
			s.setString(2, page.getMarkdown());
			s.setString(3, page.getTitle());
			s.setInt(4, 0);
			s.setString(5, page.getProjectID());
			// On delete cascade will delete categories and pages involved.
			s.executeUpdate();

		} catch (Exception e) {
			processSQLError(e);
		}

		return null;
	}

	@Override
	public String updatePage(Project project, Page page) {
		try {
			cmdString = "update pages set title = ?, body = ?, views = ? where id = ?";
			PreparedStatement s = db.prepareStatement(cmdString);

			s.setString(1, page.getTitle());
			s.setString(2, page.getMarkdown());
			s.setInt(3, page.getViewCount());
			s.setString(4, page.getID());
			s.executeUpdate();
		} catch (Exception e) {
			processSQLError(e);
		}

		return null;
	}

	@Override
	public String deletePage(Project project, Page page) {
		try {
			cmdString = "delete from pages where id = ?";
			PreparedStatement s = db.prepareStatement(cmdString);

			s.setString(1, page.getID());
			s.executeUpdate();

		} catch (Exception e) {
			processSQLError(e);
		}

		return null;
	}

	@Override
	public ArrayList<Page> getChildren(Page page) {
		ArrayList<Page> result = new ArrayList<Page>();
		try {
			cmdString = "select child from children where parent = ?";
			PreparedStatement s = db.prepareStatement(cmdString);
			s.setString(1, page.getID());

			ResultSet row = s.executeQuery();

			while (row.next()) {
				String child = row.getString("child");
				result.add(getPage(child));
			}
			row.close();
		} catch (Exception e) {
			processSQLError(e);
		}

		return result;
	}

	@Override
	public ArrayList<Page> getNonParents(Page page) {
		ArrayList<Page> result = new ArrayList<Page>();
		if (page != null) {
			try {
				cmdString = "select id from pages where project = ? except (select distinct children.parent from children where children.child = ?)";

				PreparedStatement s = db.prepareStatement(cmdString);
				s.setString(1, page.getProjectID());
				s.setString(2, page.getID());

				ResultSet row = s.executeQuery();

				while (row.next()) {
					String id = row.getString("id");
					result.add(getPage(id));
				}

			} catch (Exception e) {
				processSQLError(e);
			}
		}

		return result;
	}

	@Override
	public int getPageSequentialSize(Project project) {
		int count = 0;
		try {
			cmdString = "select count(*) as PageCount from pages where project = ?";

			PreparedStatement s = db.prepareStatement(cmdString);
			s.setString(1, project.getID());
			ResultSet row = s.executeQuery();
			row.next();

			count = Integer.parseInt(row.getString("PageCount"));
			row.close();
		} catch (Exception e) {
			processSQLError(e);
		}
		return count;
	}

	@Override
	public ArrayList<Page> getAllPages(Project project) {
		ArrayList<Page> result = new ArrayList<Page>();

		if (project == null || project.getID() == null) {
			return result;
		}

		try {
			cmdString = "select * from pages where project = ?";
			PreparedStatement s = db.prepareStatement(cmdString);
			s.setString(1, project.getID());

			ResultSet row = s.executeQuery();

			while (row.next()) {
				String id = row.getString("id");
				String body = row.getString("body");
				String title = row.getString("title");
				int viewCount = Integer.parseInt(row.getString("views"));
				result.add(new Page(id, title, body, project, viewCount));
			}
			row.close();
		} catch (Exception e) {
			processSQLError(e);
		}

		return result;
	}
}
