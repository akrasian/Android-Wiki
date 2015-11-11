package comp3350.wiki.presentation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import comp3350.wiki.R;
import comp3350.wiki.objects.Page;
import comp3350.wiki.objects.Project;
import comp3350.wiki.business.AccessPages;
import comp3350.wiki.business.AccessProjects;

public class EditProjectActivity extends Activity {

	private AccessProjects accessProjects;
	private Project project;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_project);

		EditText nameEditText = (EditText) findViewById(R.id.editProjectName);
		Intent intent = getIntent();
		String projectID = intent.getStringExtra("projectID");

		accessProjects = new AccessProjects();
		project = null;

		// Now we search our arrayList for the project with that name
		try {
			project = accessProjects.getProject(projectID);
			if (project != null) {
				nameEditText.setText(project.getTitle());
			}
		} catch (Exception e) {
			e.printStackTrace();
			Dialog dlg = new Dialog(this, "ERROR", "Something went wrong. Cancelling operation.");
			dlg.addOkButton(new FinishInterface());
			dlg.show();
		}
	}

	public void buttonSaveProjectOnClick(View view) {

		try {
			EditText editProjectName = (EditText) findViewById(R.id.editProjectName);
			String newName = editProjectName.getText().toString();

			if (newName == null || newName.equals("")) {
				throw new IllegalStateException();
			}

			if (project == null) {
				project = accessProjects.createNewProject(newName);
				accessProjects.insertProject(project);

				// Must create a home page for this at the same time!
				AccessPages accessPages = new AccessPages(project);

				Page homePage = accessPages.createNewPage("Default Title", "Default Body.");
				accessPages.setPage(homePage);

				project.setHomePage(homePage);

				AccessProjects accessProjects = new AccessProjects();
				accessProjects.updateProject(project);

				// Only try to make a new page if the project is empty.
				Intent editPage = new Intent(EditProjectActivity.this, EditPageActivity.class);
				editPage.putExtra("projectID", project.getID());
				editPage.putExtra("pageID", homePage.getID());
				EditProjectActivity.this.startActivity(editPage);
			} else {
				project.setName(newName);
				accessProjects.updateProject(project);

				// Reset the display first:

				Intent viewPageIntent = new Intent(EditProjectActivity.this, ViewPageActivity.class);
				viewPageIntent.putExtra("projectID", project.getID());
				viewPageIntent.putExtra("pageID", project.getHomeID());

				EditProjectActivity.this.startActivity(viewPageIntent);
			}

			finish();

		} catch (IllegalStateException ise) {
			Dialog dlg = new Dialog(this, "Invalid Name",
					"Name must contains 1 or more characters.");
			dlg.addOkButton(new BlankInterface());
			dlg.show();
		} catch (Exception e) {
			e.printStackTrace();
			Dialog dlg = new Dialog(this, "ERROR", "Something went wrong. Cancelling operation.");
			dlg.addOkButton(new BlankInterface());
			dlg.show();
		}
	}

	public void buttonDeleteProjectOnClick(View view) {
		accessProjects.deleteProject(project);
		finish();
	}

	public void buttonCancelProjectEditOnClick(View view) {
		finish();
	}
}
