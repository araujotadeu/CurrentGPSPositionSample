package com.sample.currentgpsposition;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Current Position GPS Example.
 * 
 * The main functionality of this application is show your current position (manually) on a map.
 * A sample of one way of used GPS API and MAPS API.
 * 
 * The current position will be updated after each 100 meters.
 *
 */
public class MainActivity extends FragmentActivity implements
		ConnectionCallbacks, OnConnectionFailedListener, LocationListener,
		OnMapLoadedCallback {

	// UI
	private LinearLayout progressLayout;

	// Maps objects
	private GoogleMap googleMaps;
	private GoogleApiClient googleApiClient;

	// Location objects
	private Location lastPosition = null;
	private LatLng lastMarker = null;

	private LocationRequest request = LocationRequest.create()
			.setInterval(INTERVAL)
			.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

	// Constants
	private static final int DEFAULT_ZOOM_LEVEL = 16;
	private static final int INTERVAL = 5000;
	private static final int DISTANCE_INTERVAL = 100;

	// Internal flags
	private boolean isInForeground;
	private boolean isMapLoaded = false;
	private boolean needAddMarker = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		getActionBar().setTitle(getString(R.string.string_title));

		progressLayout = (LinearLayout) findViewById(R.id.progressLayout);

		configureMap();
		configureUpGoogleApiClient();
		googleApiClient.connect();
	}

	@Override
	protected void onResume() {
		super.onResume();
		isInForeground = true;

		if (!Utils.checkInternetConnection(this)) {
			Utils.openConnectionSettingsDialog(this);
		}
		
		if (!Utils.checkProviders(this)) {
			Utils.openLocationSettingsDialog(this);
		}
		
		if (lastPosition != null && lastMarker != null) {
			if (lastPosition.getLatitude() != lastMarker.latitude
					&& lastPosition.getLongitude() != lastMarker.longitude) {
				addMarker(lastPosition);
			}
		}
	}

	@Override
	protected void onDestroy() {
		if (googleApiClient != null) {
			googleApiClient.disconnect();
		}
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
		isInForeground = false;
	}

	private void configureMap() {
		if (googleMaps == null) {
			googleMaps = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			googleMaps.setOnMapLoadedCallback(this);
		}
	}

	private void configureUpGoogleApiClient() {
		if (googleApiClient == null) {
			googleApiClient = new GoogleApiClient.Builder(this)
					.addApi(LocationServices.API).addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this).build();
		}
	}

	private void addMarker(Location location) {
		LatLng local = new LatLng(location.getLatitude(),
				location.getLongitude());

		if (isMapLoaded) {
			if (googleMaps != null) {
				googleMaps.clear();
				googleMaps.addMarker(new MarkerOptions().position(local));
				googleMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(
						local, DEFAULT_ZOOM_LEVEL));

				checkProgress();
			}

		} else {
			needAddMarker = true;
		}
	}

	private void checkProgress() {
		if (progressLayout.getVisibility() == View.VISIBLE)
			progressLayout.setVisibility(View.GONE);
	}

	@Override
	public void onLocationChanged(Location location) {

		float metersDistance = 0;

		if (lastPosition != null) {
			metersDistance = location.distanceTo(lastPosition);
		}
		if (metersDistance >= DISTANCE_INTERVAL || lastPosition == null) {
			if (isInForeground) {
				addMarker(location);
				lastMarker = new LatLng(location.getLatitude(),
						location.getLongitude());
			} else {
				Utils.sendNotification(location, this);
			}
			lastPosition = location;
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult connResult) {
		// Do nothing
	}

	@Override
	public void onConnected(Bundle bundle) {
		LocationServices.FusedLocationApi.requestLocationUpdates(
				googleApiClient, request, this);
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// Do nothing
	}

	@Override
	public void onMapLoaded() {
		isMapLoaded = true;
		if (needAddMarker) {
			addMarker(lastPosition);
		}

	}
}