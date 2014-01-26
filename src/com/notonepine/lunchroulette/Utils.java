package com.notonepine.lunchroulette;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class Utils {
	public static String getUserAvatarUrl(String userId) {
		return "http://graph.facebook.com/" + userId + "/picture?type=large";
	}
	public static Location getLocation(Context context) {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	}
}
