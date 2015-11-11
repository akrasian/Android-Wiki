package comp3350.wiki.presentation;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import comp3350.wiki.presentation.Dialog;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import comp3350.wiki.R;
import comp3350.wiki.objects.Page;
import comp3350.wiki.objects.Project;
import comp3350.wiki.business.AccessChildren;
import comp3350.wiki.business.AccessProjects;
import comp3350.wiki.business.AccessPages;

public class EditPageActivity extends Activity implements OnItemClickListener {

	private AccessPages accessPages;
	private Page page = null;
	private Project project;
	boolean createdPage;
	ListPopupWindow listPopupWindow;

	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_page);

		EditText pageNameEdit;
		Intent intent = getIntent();

		try {
			String projectID = intent.getStringExtra("projectID");
			String pageID = intent.getStringExtra("pageID");

			// Assumptions: If pageID == "" then this is a new page
			// Home pages are always created in EditProject, so they never have
			// blank or null IDs
			// If pageID isn't "", editing existing.
			// projectID is known.

			AccessProjects accessProjects = new AccessProjects();
			project = accessProjects.getProject(projectID);
			if (project == null)
				finish();

			accessPages = new AccessPages(project);
			page = accessPages.getPage(pageID);

			if (page == null) {
				createdPage = true;
				// If a new page was created, making a new view activity rather
				// than returning to the old one and refreshing.
				page = accessPages.createNewPage("Default", "Body text goes here.");
			}

			pageNameEdit = (EditText) findViewById(R.id.editPageName);
			pageNameEdit.setText(page.getTitle());
			EditText editPageText = (EditText) findViewById(R.id.editPageText);

			editPageText.setText(page.getMarkdown());
		} catch (Exception e) {
			e.printStackTrace();
			Dialog dlg = new Dialog(this, "ERROR",
					"The project we were looking for doesn't seem to exist, please retry."
							+ e.getMessage());
			dlg.addOkButton(new FinishInterface());
			dlg.show();
		}
	}

	public void buttonAddCategoryOnClick(View view) {
		new CategoryEditor(this, view);
	}

	class CategoryEditor implements OnItemClickListener {
		EditPageActivity context;
		View view;
		ArrayList<Page> parentOptions;
		AccessChildren accessChildren = new AccessChildren();

		CategoryEditor(EditPageActivity context, View view) {
			this.context = context;

			parentOptions = accessChildren.getNonParents(page);

			listPopupWindow = new ListPopupWindow(context);
			listPopupWindow.setAdapter(new ArrayAdapter<Page>(context,
					android.R.layout.simple_list_item_1, parentOptions));
			listPopupWindow.setAnchorView(view);
			listPopupWindow.setWidth(800);
			listPopupWindow.setHeight(1000);
			listPopupWindow.setModal(true);
			listPopupWindow.setOnItemClickListener(this);

			listPopupWindow.show();
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Page current = parentOptions.get(position);

			Dialog dlg = new Dialog(context, "Success", "Added category " + current.getTitle()
					+ " to this page.");
			dlg.addOkButton(new BlankInterface());
			dlg.show();

			accessChildren.setParent(current, page);
			listPopupWindow.dismiss();
		}
	}

	ArrayList<Page> options;

	public void buttonAddLinkOnClick(View view) {
		options = new ArrayList<Page>();
		accessPages.getWikiPages(options);

		listPopupWindow = new ListPopupWindow(this);
		listPopupWindow.setAdapter(new ArrayAdapter<Page>(this,
				android.R.layout.simple_list_item_1, options));
		listPopupWindow.setAnchorView(view);
		listPopupWindow.setWidth(800);
		listPopupWindow.setHeight(1000);
		listPopupWindow.setModal(true);
		listPopupWindow.setOnItemClickListener(this);

		listPopupWindow.show();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		EditText body = (EditText) findViewById(R.id.editPageText);
		int startSelection = body.getSelectionStart();
		int endSelection = body.getSelectionStart();
		String text = body.getText().toString();
		String beforeSel = text.substring(0, startSelection);
		String afterSel = text.substring(endSelection, text.length());

		Page current = options.get(position);
		String linkID = current.getID();
		String visible = current.getTitle();

		body.setText(beforeSel + "[" + visible + "](" + linkID + ")" + afterSel);

		body.setSelection(startSelection + 4 + linkID.length() + visible.length());
		listPopupWindow.dismiss();
	}

	public void buttonSavePageOnClick(View v) {
		EditText editPageName = (EditText) findViewById(R.id.editPageName);
		String newName = editPageName.getText().toString();

		EditText editPageText = (EditText) findViewById(R.id.editPageText);

		String newBody = editPageText.getText().toString();

		page.setTitle(newName);
		page.setMarkdown(accessPages.setAutoHyperlinks(newBody));
		String errorCode = accessPages.setPage(page);
		if (errorCode != null) {
			Dialog dlg = new Dialog(this, "ERROR", errorCode);
			dlg.addOkButton(new BlankInterface());
			dlg.show();
			return;
		}

		if (createdPage) {
			// If this is a new page, we need to create it.
			// Otherwise, we can just finish and our page will
			// be updated
			Intent viewPage = new Intent(EditPageActivity.this, ViewPageActivity.class);
			viewPage.putExtra("projectID", project.getID());
			viewPage.putExtra("pageID", page.getID());
			EditPageActivity.this.startActivity(viewPage);
		}
		finish();
	}

	public void buttonCancelPageEditOnClick(View view) {
		finish();
	}
}
