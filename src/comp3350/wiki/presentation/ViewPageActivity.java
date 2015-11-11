package comp3350.wiki.presentation;

import comp3350.wiki.R;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Context;
import android.content.Intent;
import comp3350.wiki.business.AccessChildren;
import comp3350.wiki.business.AccessPages;
import comp3350.wiki.business.AccessProjects;
import comp3350.wiki.objects.Page;
import comp3350.wiki.objects.Project;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class ViewPageActivity extends Activity {
	private final static int POPULAR_NUM = 3;
	private ListView drawerList;
	private RelativeLayout categoryLinks;
	private DrawerLayout drawerLayout;
	private Project project;
	private String projectID;
	private String pageID;

	private List<Page> popularPages;
	private ArrayAdapter<Page> popularArrayAdapter;

	private AccessPages accessPages;
	private AccessChildren accessChildren;

	private ArrayList<Page> children;
	private ArrayList<Page> sidebar_links;

	private Page page;

	private boolean counted = false;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_view_page, menu);

		SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextChange(String query) {
				return true;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				Intent searchPageIntent = new Intent(ViewPageActivity.this,
						SearchPageActivity.class);
				searchPageIntent.putExtra("projectID", project.getID());
				searchPageIntent.putExtra("query", query);
				ViewPageActivity.this.startActivity(searchPageIntent);

				return false;
			}

		});

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Dialog dlg;
		Intent editPage;
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.editPage:
			editPage = new Intent(ViewPageActivity.this, EditPageActivity.class);
			editPage.putExtra("projectID", project.getID());
			editPage.putExtra("pageID", pageID); // Page has no
													// identification yet.
			ViewPageActivity.this.startActivity(editPage);

			return true;
		case R.id.deletePage:
			// Case 1: This is the home page
			if (project.getHomeID().equals(pageID)) {
				dlg = new Dialog(this, "ERROR", "Cannot delete the home page.");
				dlg.addOkButton(new BlankInterface());
				dlg.show();
			} else {
				// Case 2: This is not the home page.
				// Open the home page, and delete this page.
				accessPages.deleteWikiPage(page);
				changePage(project.getHomeID());

			}
			return true;
		case R.id.newPage:
			editPage = new Intent(ViewPageActivity.this, EditPageActivity.class);
			editPage.putExtra("projectID", project.getID());
			editPage.putExtra("pageID", "");
			ViewPageActivity.this.startActivity(editPage);
			return true;
		case R.id.homePage:
			Intent homePage = new Intent(ViewPageActivity.this, HomeActivity.class);
			ViewPageActivity.this.startActivity(homePage);
			return true;
		case R.id.helpPage:
			Intent viewPage = new Intent(ViewPageActivity.this, ViewPageActivity.class);
			viewPage.putExtra("projectID", project.getID());
			viewPage.putExtra("pageID", "help");
			ViewPageActivity.this.startActivity(viewPage);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_page);
		Intent intent = getIntent();
		// Saving globally to permit refresh later.
		projectID = intent.getStringExtra("projectID");
		pageID = intent.getStringExtra("pageID");

		TextView textView = (TextView) findViewById(R.id.textViewPopular);
		textView.setText("Most popular pages:");
	}

	@Override
	public void onResume() {
		refreshPage();
		refreshPopular();
		super.onResume();
	}

	public void refreshPopular() {
		try {
			popularPages = accessPages.getTopPages(POPULAR_NUM);
			ListView listView = (ListView) findViewById(R.id.listViewPopular);

			popularArrayAdapter = new ArrayAdapter<Page>(this, android.R.layout.simple_list_item_1,
					android.R.id.text1, popularPages) {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					View view = super.getView(position, convertView, parent);

					TextView text1 = (TextView) view.findViewById(android.R.id.text1);
					Page currPage = popularPages.get(position);
					text1.setText(currPage.getTitle());

					return view;
				}
			};

			listView.setAdapter(popularArrayAdapter);

			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Page selected = popularArrayAdapter.getItem(position);

					Intent viewPageIntent = new Intent(ViewPageActivity.this,
							ViewPageActivity.class);
					viewPageIntent.putExtra("projectID", selected.getProjectID());
					viewPageIntent.putExtra("pageID", selected.getID());
					ViewPageActivity.this.startActivity(viewPageIntent);
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

	public void refreshPage() {
		sidebar_links = new ArrayList<Page>();

		try {
			AccessProjects accessProjects = new AccessProjects();
			project = accessProjects.getProject(projectID);

			if (project == null || project.getTitle() == null || pageID == null) {
				Dialog dlg = new Dialog(this, "ERROR",
						String.format("Can't view this page due to null data."));
				dlg.addOkButton(new BlankInterface());
				dlg.show();
				finish();
			}

			accessPages = new AccessPages(project);
			accessChildren = new AccessChildren();

			setContentView(R.layout.activity_view_page);

			sidebar_links.add(accessPages.createAllPage());

			page = accessPages.getPage(pageID);

			if (page == null) {
				finish();
			} else {
				if (!counted && project.getHomeID() != page.getID()) {
					accessPages.increaseViewCount(page);
					counted = true;
				}
			}

			System.out.println("Looking up parents of " + page);
			children = accessChildren.getChildren(page);

			ArrayList<Page> parents = accessChildren.getParents(page);
			sidebar_links.addAll(parents);

			drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

			// Populate the Navigtion Drawer with options
			categoryLinks = (RelativeLayout) findViewById(R.id.drawerPane);
			drawerList = (ListView) findViewById(R.id.navList);
			DrawerListAdapter adapter = new DrawerListAdapter(this);
			drawerList.setAdapter(adapter);

			// Sidebar Link Item click listeners
			drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					selectItemFromDrawer(position); // Move to new page.
				}
			});

			setWebview(pageID);

		} catch (IllegalStateException ise) {
			ise.printStackTrace();
			Dialog dlg = new Dialog(this, "ERROR",
					"The project or page we were looking for doesn't seem to exist, please retry.");
			dlg.addOkButton(new FinishInterface());
			dlg.show();
		} catch (Exception e) {
			e.printStackTrace();
			Dialog dlg = new Dialog(this, "ERROR", "Something went wrong. Cancelling operation.");
			dlg.addOkButton(new FinishInterface());
			dlg.show();
			finish();
		}
	}

	protected void onDestroy() {
		super.onDestroy();
	}

	private void setWebview(String pageID) {
		WebViewClient yourWebClient = new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// Trim the Link and convert it into an ID. IDs are used
				// everywhere EXCEPT when showing in a web page.
				int len = url.length();
				if (len > 8) {

					String id = url.substring(7, len - 1);
					changePage(id);
				}

				return true;
			}
		};

		WebView webView = (WebView) findViewById(R.id.webViewPage);
		webView.getSettings().setJavaScriptEnabled(false);
		webView.setWebViewClient(yourWebClient);

		if (page != null) {
			setTitle(page.getTitle());
			String page_data = page.getHTML();

			if (children.size() > 0) {
				StringBuilder childLinks = new StringBuilder();
				childLinks.append("Pages in this Category : ");
				for (Page child : children) {
					childLinks.append("<p><a href =\"" + child.getLink() + "\">" + child.getTitle()
							+ "</a></p>");
				}
				page_data = page_data + childLinks.toString();
			}

			webView.loadData(page_data, "text/html", null);
		} else {
			setTitle("Page not Found");
			String page_data = "Error: " + pageID + " is not a valid page in this wiki";
			webView.loadData(page_data, "text/html", null);
		}
	}

	private void changePage(String newPageID) {
		Intent viewPageIntent = new Intent(ViewPageActivity.this, ViewPageActivity.class);
		viewPageIntent.putExtra("projectID", project.getID());
		viewPageIntent.putExtra("pageID", newPageID);
		ViewPageActivity.this.startActivity(viewPageIntent);
	}

	private void selectItemFromDrawer(int position) {
		if (position < sidebar_links.size()) {
			String id = sidebar_links.get(position).getID();

			changePage(id);
		}

		drawerLayout.closeDrawer(categoryLinks);
	}

	class DrawerListAdapter extends BaseAdapter {

		Context mContext;

		public DrawerListAdapter(Context context) {
			mContext = context;
		}

		@Override
		public int getCount() {
			return sidebar_links.size();
		}

		@Override
		public Object getItem(int position) {
			return sidebar_links.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;

			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.drawer_item, parent, false);
			} else {
				view = convertView;
			}

			TextView titleView = (TextView) view.findViewById(R.id.title);

			titleView.setText(sidebar_links.get(position).getTitle());

			return view;
		}
	}
}
