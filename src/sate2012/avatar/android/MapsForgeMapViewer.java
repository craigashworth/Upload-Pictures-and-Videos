package sate2012.avatar.android;

import gupta.ashutosh.avatar.R;
import java.util.LinkedList;
import java.util.List;
import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.MapViewMode;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MapsForgeMapViewer extends MapActivity implements LocationListener, OnClickListener {
	private MapView mapView;
	private GeoPoint myCurrentLocation;
	private double locLat;
	private double locLon;
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	public static final int MEDIA_SELECTOR_REQUEST_CODE = 1845235;
	private double pointLocLat;
	private double pointLocLon;
	private Button Tutorial;
	private Button findPosition;
	private Button exit;
	private Button ClearPointsButton;
	MVItemizedOverlay itemizedOverlay;
	MVItemizedOverlay userPointOverlay;
	PointSetter pointer;
	Drawable locationMarker;
	Drawable newMarker;
	Drawable newPoint;
	
	private static SensorManager mySensorManager;
	public static String EXTRA_MESSAGE;
	private boolean sensorrunning;
	private Compass myCompassView;
	private SensorEventListener mySensorEventListener = new SensorEventListener() {
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		public void onSensorChanged(SensorEvent event) {
			myCompassView.updateDirection((float) event.values[0]);
		}
	};
	private Button getPts;
	private CoordinateUpdater plotter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.map_view);
		myCompassView = (Compass) findViewById(R.id.mycompassview);
		mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		getPts = (Button) findViewById(R.id.Update_CoordinatesButton);
		getPts.setOnClickListener(this);
		List<Sensor> mySensors = mySensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
		if (mySensors.size() > 0) {
			mySensorManager.registerListener(mySensorEventListener, mySensors.get(0), SensorManager.SENSOR_DELAY_NORMAL);
			sensorrunning = true;
		} else {
			sensorrunning = false;
			finish();
		}
		LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener mlocListener = new MyLocationListener();
		mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0, mlocListener);
		findPosition = (Button) findViewById(R.id.findPositionButton);
		Tutorial = (Button) findViewById(R.id.tutorial);
		exit = (Button) findViewById(R.id.Exit);
		ClearPointsButton = (Button) findViewById(R.id.Clear_Points_Button);
		findPosition.setOnClickListener(this);
		Tutorial.setOnClickListener(this);
		exit.setOnClickListener(this);
		ClearPointsButton.setOnClickListener(this);
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setMapViewMode(MapViewMode.MAPNIK_TILE_DOWNLOAD);
		Log.d("DEBUG", "setBuiltZoomControls: @ location of");
		mapView.setBuiltInZoomControls(true);
		mapView.setScaleBar(true);
		mapView.setClickable(true);
		findPositionButton(myCurrentLocation);
		mapView.getController().setZoom(14);
		setCenterlocation();
		locationMarker = getResources().getDrawable(R.drawable.ic_launcher);
		pointer = new PointSetter(locationMarker, this);
		newMarker = getResources().getDrawable(R.drawable.ic_launcher);
		
		pointer = new PointSetter(newMarker, this);
		newPoint = getResources().getDrawable(R.drawable.new_pt_marker);
		pointer = new PointSetter(newPoint, this);
		mapView.getOverlays().add(pointer);
		userPointOverlay = new MVItemizedOverlay(newMarker);
		itemizedOverlay = new MVItemizedOverlay(newMarker);
		mapView.getOverlays().add(userPointOverlay);
		mapView.getOverlays().add(itemizedOverlay);
	}

	public void onBackPressed(){
		finish();
	}
	
	
	
	public void onClick(View v) {
		switch (v.getId()) {
		case (R.id.findPositionButton):
			findPositionButton(myCurrentLocation);
			break;
		case (R.id.tutorial):
			LayoutInflater inflater = getLayoutInflater();
			View layout = inflater.inflate(R.layout.toast_layout,
		                           (ViewGroup) findViewById(R.id.toast_layout_root));

			ImageView image = (ImageView) layout.findViewById(R.id.name);
			image.setImageResource(R.drawable.arrow);
			TextView text = (TextView) layout.findViewById(R.id.text);
			text.setText("To begin, select 'Find Position' and the application will display your position and the corresponding map aroundyou. " +
						"To create a new data point, hold your finger down on the GPS location you wish to upload a piece of data from. " +
						 "Next, the application will give you an option to upload different types of data. The data you chose will be shown on the map." +
						 "At anytime, you may update your coordintates or clear the data.");

			Toast toast = new Toast(getApplicationContext());
			toast.setGravity(Gravity.FILL_HORIZONTAL, 0, 0);
			toast.setDuration(Toast.LENGTH_LONG);
			toast.setView(layout);
			int count = 0;
			try {

                while (true && count < 50) {
                    toast.show();
                    sleep(1850);

                    count++;
                }
                }catch (Exception e) {

                    Log.e("LongToast", "", e);
                }
			break;
		case (R.id.Exit):
			finish();
			break;
		case (R.id.Clear_Points_Button):
			userPointOverlay.clear();
			break;
		case (R.id.Update_CoordinatesButton):
			new MyAsyncTask(this).execute();
			//TODO:	-Create ability (dialog box?) to open URL of specific point to view it's info (pic, vid, comment, voice note)
			break;
		}
	}

	private void sleep(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	/**
	 * onDestroy stops or "destroys" the program when this actions is called.
	 */
	protected void onDestroy() {
		super.onDestroy();
		if (sensorrunning)
			mySensorManager.unregisterListener(mySensorEventListener);
	}

	@Override
	/**
	 * onPause pauses the application and saves the data in the savedInstances Bundle. 
	 */
	protected void onPause() {
		super.onPause();
	}

	@Override
	/**
	 * onResume sets the actions that the application runs through when starting the application from pause.
	 */
	protected void onResume() {
		super.onResume();
	}

	/**
	 * setCenterLocation is the method that sets the center of the screen to a
	 * already specified point on the map.
	 */

	protected void setCenterlocation() {
		if (myCurrentLocation == null)
			mapView.getController().setCenter(new GeoPoint(39.00, -100.00));
		else
			mapView.getController().setCenter(myCurrentLocation);
	}

	/**
	 * findPositionButton takes GeoPoint p as its argument and sets the center
	 * of the screen to that point.
	 * 
	 * @param p
	 */
	public void findPositionButton(GeoPoint p) {
		mapView.getController().setCenter(p);
	}

	/**
	 * This class listens for the location manager. When the location manager
	 * sends the location listener the files for the map, it runs through the
	 * methods within the class body.
	 * 
	 * 
	 */
	public class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location loc) {
			GeoPoint gp = new GeoPoint(loc.getLatitude(), loc.getLongitude());
			if (gp != null)
				myCurrentLocation = gp;
			itemizedOverlay.clear();
			DataObject data = new DataObject();
			Drawable newMarker = getResources().getDrawable(R.drawable.ic_launcher);
			Drawable locationMarker = getResources().getDrawable(R.drawable.ic_launcher);
			DataObjectItem overlayItem = new DataObjectItem(gp, data);
			overlayItem.setMarker(MVItemizedOverlay.boundCenterBottom(newMarker));
			DataObjectItem myLocationMarker = new DataObjectItem(myCurrentLocation, data);
			myLocationMarker.setMarker(MVItemizedOverlay.boundCenterBottom(locationMarker));
			itemizedOverlay.addOverlay(overlayItem);
		}

		
		/**
		 * activates when the current provider is disabled, or not available
		 * anymore.
		 */
		public void onProviderDisabled(String provider) {
			Toast.makeText(getApplicationContext(), "GPS Disabled", Toast.LENGTH_LONG).show();
		}

		/**
		 * activates when a provider is found.
		 */
		public void onProviderEnabled(String provider) {
			Toast.makeText(getApplicationContext(), "GPS Enabled", Toast.LENGTH_LONG).show();
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {

		}
	}

	/**
	 * Determines whether one Location reading is better than the current
	 * Location fix
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param myCurrentLocation
	 *            The current Location fix, to which you want to compare the new
	 *            one
	 */
	protected boolean isBetterLocation(Location location, Location myCurrentLocation) {
		if (myCurrentLocation == null)
			return true;
		long timeDelta = location.getTime() - myCurrentLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;
		if (isSignificantlyNewer)
			return true;
		else if (isSignificantlyOlder)
			return false;
		int accuracyDelta = (int) (location.getAccuracy() - myCurrentLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;
		boolean isFromSameProvider = isSameProvider(location.getProvider(), myCurrentLocation.getProvider());
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider)
			return true;
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null)
			return provider2 == null;
		return provider1.equals(provider2);
	}

	public void sendGeoPointToMainClass(Location loc) {
		locLat = loc.getLatitude();
		locLon = loc.getLongitude();
		myCurrentLocation = new GeoPoint((int) (locLat * 1E6), (int) (locLon * 1E6));
	}

	private class PointSetter extends MVItemizedOverlay {
		public PointSetter(Drawable marker, Context contextIn) {
			super(marker, contextIn);
		}
		
		public void plotUpdatedCoords( LinkedList<DataObjectItem> dataList){
			userPointOverlay.clear();
			for(DataObjectItem p: dataList){
				if(p.getData().getType().compareTo("Video") == 0)
					p.setMarker(MVItemizedOverlay.boundCenterBottom(getResources().getDrawable(R.drawable.red_video_marker)));
				else if(p.getData().getType().compareTo("Audio") == 0)
					p.setMarker(MVItemizedOverlay.boundCenterBottom(getResources().getDrawable(R.drawable.yellow_microphone_marker)));
				else if(p.getData().getType().compareTo("Comment") == 0)
					p.setMarker(MVItemizedOverlay.boundCenterBottom(getResources().getDrawable(R.drawable.green_document_marker)));
				else if(p.getData().getType().compareTo("Photo") == 0)
					p.setMarker(MVItemizedOverlay.boundCenterBottom(getResources().getDrawable(R.drawable.blue_camera_marker)));
				else if(p.getData().getType().compareTo("Android") == 0)
					p.setMarker(MVItemizedOverlay.boundCenterBottom(getResources().getDrawable(R.drawable.magenta_android_marker)));
				else if(p.getData().getType().compareTo("Nao") == 0)
					p.setMarker(MVItemizedOverlay.boundCenterBottom(getResources().getDrawable(R.drawable.cyan_nao_marker)));
				userPointOverlay.addOverlay(p);
			}
		}

		@Override
		public boolean onLongPress(GeoPoint point, MapView mapView) {
			DataObject data = new DataObject();
			if (point != null) {
				pointLocLat = point.getLatitude();
				pointLocLon = point.getLongitude();
				Globals.lat = "" + pointLocLat;
				Globals.lng = "" + pointLocLon;
				DataObjectItem newPointItem = new DataObjectItem(point, data);
				newPointItem.setMarker(MVItemizedOverlay.boundCenterBottom(newPoint));
				userPointOverlay.addOverlay(newPointItem);
				data.setLat(pointLocLat);
				data.setLon(pointLocLon);
				Intent senderIntent = new Intent(getApplicationContext(), UploadMedia.class);
				startActivity(senderIntent);
			}
			return true;
		}
	}

	public void onLocationChanged(Location arg0) {
	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	
    private class MyAsyncTask extends AsyncTask<Integer, String, Boolean> {
        private Context context;
        public MyAsyncTask(Context context) {
            this.context = context;
        }
        
        protected void onPreExecute() {
            Toast.makeText(context, "Retrieving coordinates. This may take a few seconds.", Toast.LENGTH_LONG).show();
        }
        
        protected Boolean doInBackground(Integer...params) {
        	plotter = new CoordinateUpdater();
			plotter.CoordinateDataTranslator();
			plotter.getDataList();
			pointer.plotUpdatedCoords(plotter.getDataList());
			return true;
		}
        
        protected void onPostExecute(Boolean result) {
       	 Toast.makeText(context, "All coordinates are up to date.", Toast.LENGTH_LONG).show();
       }
    }
}