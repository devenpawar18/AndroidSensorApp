package com.sensorapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Application level class to initialize and maintain various application life
 * cycle specific details
 */
public class ApplicationEx extends android.app.Application {
	/** Application Context */
	public static Context context;

	/**
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();

	}

	/**
	 * Shows the error dialog
	 */

	public static void showDialog(Context context) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(context.getResources().getString(
				R.string.feature_not_supported_title));
		dialog.setMessage(context.getResources().getString(
				R.string.feature_not_supported_message));
		dialog.setCancelable(false);
		dialog.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});
		dialog.show();
	}

}
