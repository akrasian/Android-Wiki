package comp3350.wiki.persistence;

import java.util.ArrayList;
import java.util.List;

import comp3350.wiki.objects.Project;
import comp3350.wiki.objects.Page;

public interface DataAccess {

	public void open(String string);

	public void close();

	public String newPageID();

	public String newProjectID();

	public String getAllProjects(List<Project> projectResult);

	public Project getProject(String projectName);

	public void insertProject(Project project);

	public String updateProject(Project project);

	public String deleteProject(Project project);

	public String getPageSequential(Project project, List<Page> pageResult);

	public int getPageSequentialSize(Project project);

	public String status(Page page);

	public Page getPage(String pageID);

	public String setPage(Project project, Page page);

	public String insertPage(Project project, Page page);

	public String updatePage(Project project, Page page);

	public String deletePage(Project project, Page page);

	public ArrayList<Page> getChildren(Page page);

	public ArrayList<Page> getAllPages(Project project);

	public ArrayList<Page> getParents(Page page);

	public ArrayList<Page> getNonParents(Page child);

	public void setParent(Page parent, Page child);
}
