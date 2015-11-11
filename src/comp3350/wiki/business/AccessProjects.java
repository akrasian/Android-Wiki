package comp3350.wiki.business;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import comp3350.wiki.application.Main;
import comp3350.wiki.application.Services;
import comp3350.wiki.objects.Project;
import comp3350.wiki.persistence.DataAccess;

public class AccessProjects {

	private DataAccess dataAccess;

	public AccessProjects() {
		dataAccess = (DataAccess) Services.getDataAccess(Main.dbName);
	}

	public String getProjects(List<Project> projects) {
		if (projects == null)
			throw new IllegalArgumentException("AccessProject: List cannot be null");
		projects.clear();
		return dataAccess.getAllProjects(projects);
	}

	public Project getProject(String projectID) {
		if (projectID == null)
			throw new IllegalArgumentException("AccessProject: projectID cannot be null");

		Project project = dataAccess.getProject(projectID);
		return project;
	}

	public void insertProject(Project currentProject) {
		dataAccess.insertProject(currentProject);
	}

	public String updateProject(Project currentProject) {
		return dataAccess.updateProject(currentProject);
	}

	public String deleteProject(Project currentCourse) {
		if (currentCourse == null) {
			return "Cannot Delete Null Project";
		}
		return dataAccess.deleteProject(currentCourse);
	}

	public void increaseViewCount(Project currProject) {
		currProject.increaseViewCount();
		updateProject(currProject);
	}

	// Returns the top num projects, if there's fewer than num projects, returns
	// all of the projects.
	public String getTopProjects(List<Project> currProjects, int num) {
		String result = getProjects(currProjects);
		if (result != null) {
			return result;
		}
		Collections.sort(currProjects, new ProjectViewComparator());
		while (currProjects.size() > num) {
			currProjects.remove(currProjects.size() - 1);
		}
		return null;
	}

	public Project createNewProject(String title) {
		String ProjectID = dataAccess.newProjectID();
		return new Project(ProjectID, title, null);
	}
}

class ProjectViewComparator implements Comparator<Project> {
	@Override
	public int compare(Project proj1, Project proj2) {
		return proj2.getViewCount() - proj1.getViewCount();
	}
}