package com.notonepine.lunchroulette;

import org.json.JSONObject;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class NetworkUtils {
	private static final String baseUrl = "http://hacktech4.cloudapp.net:80";
	
	public static void postUser(JSONObject user, AsyncHttpResponseHandler callback) {
		httpClient().post(baseUrl + "/users", new RequestParams("user", user.toString()), callback);
	}
	
	public static void beginSearch(String userId, double latitude, double longitude) {
		RequestParams params = new RequestParams();
		params.put("id", userId);
		params.put("latitude", latitude);
		params.put("longitude", longitude);
		httpClient().post(baseUrl + "/searching_users", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				Log.d("adf", response);
			}
		});
	}
	
	private static AsyncHttpClient httpClient() {
		AsyncHttpClient client = new AsyncHttpClient();
		client.addHeader("Content-Type", "application/json");
		return client;
	}
}
