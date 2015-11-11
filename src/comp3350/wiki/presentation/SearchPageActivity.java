package comp3350.wiki.presentation;

import java.util.ArrayList;

import comp3350.wiki.R;
import comp3350.wiki.business.AccessPages;
import comp3350.wiki.business.AccessProjects;
import comp3350.wiki.objects.Page;
import comp3350.wiki.objects.Project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SearchPageActivity extends Activity {

	private ArrayList<Page> links;
	private ArrayAdapter<Page> popularArrayAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_page);
		Intent intent = getIntent();
		String projectID = intent.getStringExtra("projectID");
		String query = intent.getStringExtra("query");

		AccessProjects accessProjects = new AccessProjects();
		Project project = accessProjects.getProject(projectID);

		AccessPages accessPages = new AccessPages(project);

		try {
			links = new ArrayList<Page>();
			accessPages.getWikiPagesBySearch(links, query);

			ListView listView = (ListView) findViewById(R.id.navList);

			popularArrayAdapter = new ArrayAdapter<Page>(this, android.R.layout.simple_list_item_1,
					android.R.id.text1, links) {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					View view = super.getView(position, convertView, parent);
					return view;
				}
			};

			listView.setAdapter(popularArrayAdapter);

			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Page selected = popularArrayAdapter.getItem(position);

					Intent viewPageIntent = new Intent(SearchPageActivity.this,
							ViewPageActivity.class);
					viewPageIntent.putExtra("projectID", selected.getProjectID());
					viewPageIntent.putExtra("pageID", selected.getID());
					SearchPageActivity.this.startActivity(viewPageIntent);
				}
			});

		} catch (Exception e) {
			Dialog dlg = new Dialog(this, "ERROR",
					"There was an error in our database, shutting down.");
			dlg.addOkButton(new BlankInterface());
			dlg.show();
			// finish();
		}
	}
}