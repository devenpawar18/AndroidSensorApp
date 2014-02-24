package com.sensorapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.sensorapp.R;

/**
 * 
 * @author DEVEN Welcome screen of the application.
 */
public class SplashScreenActivity extends Activity {

	/**
	 * Delay in milliseconds indicating the time for which splash screen will be
	 * displayed
	 */
	private static final int DELAY = 2000;
	private static final int START_HOME_ACTIVITY = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash_screen);
		mHandler.sendEmptyMessageDelayed(START_HOME_ACTIVITY, DELAY);

	}

	/**
	 * Handler to navigate to the Home Activity after the specific delay.
	 */
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {

			Intent intent = new Intent();
			intent.setClass(SplashScreenActivity.this, HomeActivity.class);
			startActivity(intent);
			finish();
		};
	};

	/**
	 * Exit the app on back press key
	 */
	public void onBackPressed() {
		if (mHandler != null) {
			mHandler.removeMessages(START_HOME_ACTIVITY);
		}
		super.onBackPressed();
	};

}
