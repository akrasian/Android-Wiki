package comp3350.wiki.presentation;

import android.app.Activity;

/*
 * This interface is used so that we can pass functions to be executed when buttons on dialogs are pressed.
 */

public interface DialogReturnInterface {
	public void execute(final Activity context);
}

class FinishInterface implements DialogReturnInterface {
	@Override
	public void execute(final Activity context) {
		context.finish();
	}
}

class BlankInterface implements DialogReturnInterface {
	@Override
	public void execute(final Activity context) {
	}
}