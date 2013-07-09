package br.unb.unbiquitous.wheel.ubiquitos;

import java.io.File;
import java.util.PropertyResourceBundle;

import org.apache.log4j.Level;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import br.unb.unbiquitous.ubiquitos.uos.adaptabitilyEngine.SmartSpaceGateway;
import br.unb.unbiquitous.ubiquitos.uos.context.UOSApplicationContext;
import br.unb.unbiquitous.ubiquitos.uos.driverManager.drivers.Clickable;
import br.unb.unbiquitous.ubiquitos.uos.driverManager.drivers.DefaultDrivers;
import br.unb.unbiquitous.wheel.R;
import br.unb.unbiquitous.wheel.mouse.MouseDriver;
import br.unb.unbiquitous.wheel.mouse.MousePointer;
import br.unb.unbiquitous.wheel.network.NetworkManager;
import de.mindpipe.android.logging.log4j.LogConfigurator;

public class DroidMouseWheelActivity extends Activity {
	private static final String INSTANCE_ID = "droid_mouse_instance";
	
	private Point mousePosition;
	private int eventAction;
	private boolean eventWasAMouseMove;
	private MouseDriver mouseDriver;

	UOSApplicationContext context;
	
	private NetworkManager networkManager;
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		context.tearDown();
		Log.d("DROID MOUSE", "onDestroy");
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                        R.layout.custom_title);
		MousePointer.mousePointerImage.setAlpha(0);
		
		this.mouseDriver = new MouseDriver();
		this.mousePosition = new Point();
		this.networkManager = new NetworkManager(this);
		this.context = new UOSApplicationContext();
		
		addLeftButtonClick();
		addRightButtonClick();
		addMoveArea();

		try {
			
			final LogConfigurator logConfigurator = new LogConfigurator();
			logConfigurator.setFileName(Environment.getExternalStorageDirectory() + File.separator + "droidMouseWheel.log");
	        logConfigurator.setRootLevel(Level.DEBUG);
			logConfigurator.setUseFileAppender(false);
	        logConfigurator.setUseLogCatAppender(true);
	        logConfigurator.setRootLevel(Level.DEBUG);
	        logConfigurator.setLevel("org.apache", Level.ERROR);
	        logConfigurator.configure();

	        networkManager.setWifiEnabled(true);
		} catch (Exception e) {
			Log.e("onCreate", e.getMessage());
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		try {
			context.init(new PropertyResourceBundle(getResources().openRawResource(R.raw.droidbiquitous)));
			((SmartSpaceGateway)context.getGateway()).getDriverManager().deployDriver(mouseDriver.getDriver(), mouseDriver, INSTANCE_ID);
			((SmartSpaceGateway)context.getGateway()).getDriverManager().initDrivers(context.getGateway());

		} catch (Exception e) {
			Log.d("UOS", e.toString());
		}
	}

	private void addMoveArea() {
		final View touchPad = (MousePointer) this
				.findViewById(R.id.mouse_pointer);

		touchPad.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//Log.d("DROID MOUSE", "DEDos: " + Integer.toString(event.getPointerCount()));
				eventAction = event.getAction();
				switch (eventAction) {
				case MotionEvent.ACTION_DOWN:
					eventWasAMouseMove = false;
					touchPad.setSoundEffectsEnabled(true);
					mousePosition.x = (int) event.getX();
					mousePosition.y = (int) event.getY();

					//Log.d("DROID MOUSE", "Action Down: " + event.getAction());
					//Log.d("DROID MOUSE", "Começou a mover: " + mousePosition.x
							///+ ", " + mousePosition.y);
					break;
				case MotionEvent.ACTION_MOVE:
					int axisY = (int) event.getY() - mousePosition.y;
					
					if(event.getPointerCount() == 1) {
						eventWasAMouseMove = true;
						touchPad.setSoundEffectsEnabled(false);
						int axisX = (int) event.getX() - mousePosition.x;
	
						mouseDriver.move(axisX, axisY);
	
						mousePosition.x = (int) event.getX();
	
						//Log.d("DROID MOUSE", "Action move: " + event.getAction());
						//Log.d("DROID MOUSE", "Mouse moveu para: " + axisX + ", "
								//+ axisY);
					} else {
						mouseDriver.scroll(axisY);
					}
					
					mousePosition.y = (int) event.getY();
					break;
				}

				return false;
			}
		});
	}

	private void addLeftButtonClick() {
		View touchPad = (MousePointer) this
				.findViewById(R.id.mouse_pointer);

		touchPad.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!eventWasAMouseMove) {
					Log.d("DROID MOUSE", "CLICOU");
					mouseDriver.buttonPressed(Clickable.BUTTON_LEFT);
					mouseDriver.buttonReleased(Clickable.BUTTON_LEFT);
				}
			}
		});
	}

	private void addRightButtonClick() {
		View touchPad = (MousePointer) this
				.findViewById(R.id.mouse_pointer);

		touchPad.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				if (eventAction != MotionEvent.ACTION_MOVE) {
					Log.d("DROID MOUSE", "CLICOU LONGO");
					mouseDriver.buttonPressed(Clickable.BUTTON_RIGHT);
					mouseDriver.buttonReleased(Clickable.BUTTON_RIGHT);
					return true;
				}
				return false;
			}
		});
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}