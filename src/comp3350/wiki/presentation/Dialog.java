package comp3350.wiki.presentation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import comp3350.wiki.R;

public class Dialog {
	AlertDialog.Builder builder;
	final Activity context;

	public Dialog(final Activity context, String title, String text) {
		this.context = context;
		builder = new AlertDialog.Builder(context);
		builder.setTitle(title).setMessage(text);
	}

	public void addOkButton(final DialogReturnInterface intrfce) {
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				intrfce.execute(context);
			}
		});
	}

	public void show() {
		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
