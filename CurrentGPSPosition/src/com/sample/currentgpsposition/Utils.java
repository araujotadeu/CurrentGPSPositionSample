package com.sample.currentgpsposition;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

/**
 * Common methods and constants.
 */
public class Utils {

	public static final int NOTIFICATION_ID = 999;

	public static boolean checkProviders(Context context) {
		LocationManager locationManager = null;
		boolean gpsEnabled = false;
		boolean networkEnabled = false;

		if (locationManager == null)
			locationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);

		try {
			gpsEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
		}

		try {
			networkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}

		if (!gpsEnabled && !networkEnabled) {
			return false;
		} else {
			return true;
		}
	}

	public static void openLocationSettingsDialog(final Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(R.string.string_not_providers_text).setTitle(
				R.string.string_not_providers_title);
		builder.setNeutralButton(activity.getString(R.string.string_ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});

		AlertDialog dialog = builder.create();

		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				Intent myIntent = new Intent(
						Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				activity.startActivity(myIntent);
			}
		});
		dialog.setCancelable(false);
		dialog.show();
	}

	public static void sendNotification(Location location, Context context) {

		String text = context.getString(R.string.string_not_text)
				+ String.valueOf(location.getLatitude()) + ","
				+ String.valueOf(location.getLongitude());

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.ic_notification_location)
				.setContentTitle(context.getString(R.string.string_not_title))
				.setContentText(text);

		Intent resultIntent = new Intent(context, MainActivity.class);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
				0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(resultPendingIntent);
		builder.setAutoCancel(true);

		NotificationManager notifyMgr = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		notifyMgr.notify(NOTIFICATION_ID, builder.build());
	}

	public static boolean checkInternetConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}
	
	public static void openConnectionSettingsDialog(final Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(R.string.string_not_connection_text).setTitle(
				R.string.string_not_connection_title);
		builder.setNeutralButton(activity.getString(R.string.string_ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});

		AlertDialog dialog = builder.create();

		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				Intent myIntent = new Intent(
						Settings.ACTION_SETTINGS);
				activity.startActivity(myIntent);
			}
		});
		dialog.setCancelable(false);
		dialog.show();
	}
}
