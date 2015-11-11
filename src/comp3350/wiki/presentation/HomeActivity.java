package comp3350.wiki.presentation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import comp3350.wiki.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import comp3350.wiki.application.Main;
import comp3350.wiki.objects.Project;
import comp3350.wiki.business.AccessProjects;

public class HomeActivity extends Activity {
	private static final int POPULAR_NUM = 3;
	private AccessProjects accessProjects;
	private ArrayList<Project> projectList;
	private ArrayList<Project> popularProjects;
	private ArrayAdapter<Project> projectArrayAdapter;
	private ArrayAdapter<Project> popularArrayAdapter;
	private boolean viewMode = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		copyDatabaseToDevice();

		Main.startUp();

		setContentView(R.layout.activity_home);
		accessProjects = new AccessProjects();
		projectList = new ArrayList<Project>();

		String result = accessProjects.getProjects(projectList);

		TextView textView = (TextView) findViewById(R.id.textViewPopular);
		textView.setText("Most popular projects:");

		if (result != null) {
			Dialog dlg = new Dialog(this, "ERROR",
					"There was an error in our database, shutting down.");
			dlg.addOkButton(new BlankInterface());
			dlg.show();
		} else {
			setListView();
			refreshPopular();
		}
	}

	private void refreshPopular() {
		popularProjects = new ArrayList<Project>();
		String result = "";
		try {
			result = accessProjects.getTopProjects(popularProjects, POPULAR_NUM);
			if (result != null) {
				throw new IllegalStateException(result);
			}
			ListView listView = (ListView) findViewById(R.id.listViewPopular);

			popularArrayAdapter = createArrayAdapter(popularProjects);

			listView.setAdapter(popularArrayAdapter);

			listView.setOnItemClickListener(createOurItemClickListener(popularArrayAdapter));

		} catch (IllegalStateException ise) {
			ise.printStackTrace();
			Dialog dlg = new Dialog(this, "ERROR",
					"There was an error in our database, error message: " + ise.getMessage());
			dlg.addOkButton(new FinishInterface());
			dlg.show();
			System.out.println(ise.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
			Dialog dlg = new Dialog(this, "ERROR",
					"There was an error in our database, shutting down.");
			dlg.addOkButton(new FinishInterface());
			dlg.show();
		}
	}

	private void copyDatabaseToDevice() {

		final String DB_PATH = "db";

		String[] assetNames;
		Context context = getApplicationContext();
		File dataDirectory = context.getDir(DB_PATH, Context.MODE_PRIVATE);
		AssetManager assetManager = getAssets();

		try {

			assetNames = assetManager.list(DB_PATH);
			System.out.println(assetNames);
			for (int i = 0; i < assetNames.length; i++) {
				assetNames[i] = DB_PATH + "/" + assetNames[i];
			}

			copyAssetsToDirectory(assetNames, dataDirectory);

			Main.setDBPathName(dataDirectory.toString() + "/" + Main.dbName);

		} catch (IOException ioe) {
			Dialog dlg = new Dialog(this, "ERROR", "Unable to access application data:");
			dlg.addOkButton(new BlankInterface());
			dlg.show();
			finish();
		}
	}

	public void copyAssetsToDirectory(String[] assets, File directory) throws IOException {
		AssetManager assetManager = getAssets();

		for (String asset : assets) {
			String[] components = asset.split("/");
			String copyPath = directory.toString() + "/" + components[components.length - 1];
			char[] buffer = new char[1024];
			int count;

			File outFile = new File(copyPath);

			if (!outFile.exists()) {
				InputStreamReader in = new InputStreamReader(assetManager.open(asset));
				FileWriter out = new FileWriter(outFile);

				count = in.read(buffer);
				while (count != -1) {
					out.write(buffer, 0, count);
					count = in.read(buffer);
				}

				out.close();
				in.close();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		Main.shutDown();
	}

	protected void onResume() {
		// Test for new projects: Otherwise this just keeps displaying the same
		// ones.
		System.out.println("Looking for new projects...");
		accessProjects = new AccessProjects();

		projectList = new ArrayList<Project>();
		accessProjects.getProjects(projectList);
		setListView();
		refreshPopular();
		super.onResume();
	}

	protected void setListView() {
		projectList = new ArrayList<Project>();
		String result = "";

		try {
			result = accessProjects.getProjects(projectList);
			if (result != null) {
				throw new IllegalStateException(result);
			}
			ListView listView = (ListView) findViewById(R.id.listViewProjects);
			viewMode = true;
			listView.setBackgroundColor(Style.projectListBackground);
			projectArrayAdapter = createArrayAdapter(projectList);

			listView.setAdapter(projectArrayAdapter);

			listView.setOnItemClickListener(createOurItemClickListener(projectArrayAdapter));

		} catch (IllegalStateException ise) {
			ise.printStackTrace();
			Dialog dlg = new Dialog(this, "ERROR",
					"There was an error in our database, the error was:\n" + ise.getMessage());
			dlg.addOkButton(new FinishInterface());
			dlg.show();
		} catch (Exception e) {
			e.printStackTrace();
			Dialog dlg = new Dialog(this, "ERROR", "Something unexpected happened. Please retry");
			dlg.addOkButton(new FinishInterface());
			dlg.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_home, menu);
		return true;
	}

	public void buttonCreateProjectOnClick(View view) {
		Intent createProjectIntent = new Intent(HomeActivity.this, EditProjectActivity.class);
		createProjectIntent.putExtra("projectID", "");
		HomeActivity.this.startActivity(createProjectIntent);
	}

	private ArrayAdapter<Project> createArrayAdapter(final ArrayList<Project> projects) {
		return new ArrayAdapter<Project>(this, android.R.layout.simple_list_item_1,
				android.R.id.text1, projects) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);

				TextView text1 = (TextView) view.findViewById(android.R.id.text1);
				text1.setText(projects.get(position).getTitle());

				return view;
			}
		};
	}

	private OnItemClickListener createOurItemClickListener(final ArrayAdapter<Project> adapter) {
		return new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Project selected = adapter.getItem(position);

				if (!viewMode) {
					Intent editProjectIntent = new Intent(HomeActivity.this,
							EditProjectActivity.class);
					editProjectIntent.putExtra("projectID", selected.getID());
					HomeActivity.this.startActivity(editProjectIntent);
				} else {
					accessProjects.increaseViewCount(selected);

					String homeID = selected.getHomeID();

					Intent viewPageIntent = new Intent(HomeActivity.this, ViewPageActivity.class);
					viewPageIntent.putExtra("projectID", selected.getID());
					viewPageIntent.putExtra("pageID", homeID);
					HomeActivity.this.startActivity(viewPageIntent);
				}
			}
		};
	}

	public void toggleViewEditOnClick(View view) {
		ListView listView = (ListView) findViewById(R.id.listViewProjects);
		viewMode = !viewMode;
		if (viewMode) {
			listView.setBackgroundColor(Style.projectListBackground);
		} else {
			listView.setBackgroundColor(Style.editProjectListBackground);
		}
	}
}
