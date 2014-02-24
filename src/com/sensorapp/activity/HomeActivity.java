package com.sensorapp.activity;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sensorapp.ApplicationEx;
import com.sensorapp.R;

/**
 * Home Activity that counts the number of steps and blinks the flash light
 * accordingly
 * 
 * @author DEVEN
 * 
 */
public class HomeActivity extends Activity implements SensorEventListener {

	private Button sensorButton;
	private Button closeButton;
	private SensorManager sensorManager;
	private EditText countEditText;
	boolean isActivityRunning;
	private int stepCount = 0;

	private Camera mCamera;
	private SurfaceHolder mHolder;
	private SurfaceView preview;

	/**
	 * Checks the current status of the flag. (true if flash is on else false)
	 */
	private boolean isFlashON;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		countEditText = (EditText) findViewById(R.id.step_counter_edit_text);
		sensorButton = (Button) findViewById(R.id.btn_sensor);
		closeButton = (Button) findViewById(R.id.btn_close);

		preview = (SurfaceView) findViewById(R.id.PREVIEW);

		sensorButton.setOnClickListener(onClickListener);
		closeButton.setOnClickListener(onClickListener);

	}

	/**
	 * Surface Listener that controls the flash light
	 */
	SurfaceHolder.Callback surfaceListener = new SurfaceHolder.Callback() {

		/**
		 * Release the camera when the surface is destroyed
		 */
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {

			if (mCamera != null) {
				mCamera.stopPreview();
				mCamera.setPreviewCallback(null);
				mCamera.release();
				mCamera = null;
			}

		}

		/**
		 * Sets the camera preview when the surface is created
		 */
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				mHolder = holder;
				mCamera.setPreviewDisplay(mHolder);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		}
	};

	/**
	 * Register the listener for step counter
	 */
	@Override
	protected void onResume() {
		super.onResume();
		isActivityRunning = true;
		Sensor countSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
		if (countSensor != null) {
			sensorManager.registerListener(this, countSensor,
					SensorManager.SENSOR_DELAY_UI);
		} else {
			ApplicationEx.showDialog(HomeActivity.this);
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		isActivityRunning = false;
		/**
		 * if you unregister the last listener, the hardware will stop detecting
		 * the step events
		 */
		// sensorManager.unregisterListener(HomeActivity.this);
	}

	/**
	 * onClick Listener
	 */
	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			int id = view.getId();
			switch (id) {
			case R.id.btn_sensor:
				if (mCamera == null) {
					sensorButton.setText(getResources().getString(
							R.string.disable_sensor));
					turnONFlashLight();
				} else {
					sensorButton.setText(getResources().getString(
							R.string.enable_sensor));
					turnOFFFlashLight();
				}

				break;
			case R.id.btn_close:
				if (mCamera != null)
					turnOFFFlashLight();
				finish();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * Function to Turn the flash light ON
	 */
	public void turnONFlashLight() {
		try {
			// Turn on LED
			isFlashON = true;
			mHolder = preview.getHolder();
			mHolder.addCallback(surfaceListener);
			mCamera = Camera.open();
			mCamera.setPreviewDisplay(mHolder);

			Parameters params = mCamera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_TORCH);
			mCamera.setParameters(params);
			mCamera.startPreview();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getBaseContext(),
					"An exception is thrown while turning ON the flash light",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Function to turn the flash light OFF
	 */
	public void turnOFFFlashLight() {
		try {
			isFlashON = false;
			Parameters params = mCamera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_OFF);
			mCamera.setParameters(params);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getBaseContext(),
					"An exception is thrown while turning OFF the flash light",
					Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.hardware.SensorEventListener#onAccuracyChanged(android.hardware
	 * .Sensor, int)
	 */
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.hardware.SensorEventListener#onSensorChanged(android.hardware
	 * .SensorEvent)
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (isActivityRunning) {
			stepCount = Integer.parseInt(String.valueOf(Math
					.round(event.values[0])));
			countEditText.setText(String.valueOf(event.values[0]));
			/**
			 * If step count = 20, turn the flash light ON
			 */
			if (stepCount == 20) {
				sensorButton.setText(getResources().getString(
						R.string.disable_sensor));
				turnONFlashLight();
			}
			/**
			 * If step count = 50, blink the flash light for around 30 seconds
			 */
			if (stepCount == 50) {
				blinkFlashLight();
			}
		}
	}

	/**
	 * Function to blink the flash light
	 */
	public void blinkFlashLight() {
		final Handler handler = new Handler();
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				long blinkDelay = 50; // Delay in ms
				for (int i = 0; i < 40; i++) {
					if (i % 2 == 0) {
						handler.post(new Runnable() {

							public void run() {
								if (isFlashON)
									turnOFFFlashLight();
								turnONFlashLight();
							}
						});
					} else {
						handler.post(new Runnable() {

							public void run() {
								if (!isFlashON)
									turnONFlashLight();
								turnOFFFlashLight();
							}
						});
					}
					try {
						Thread.sleep(blinkDelay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		});
		thread.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// timer.cancel();
		super.onStop();
	}

}
