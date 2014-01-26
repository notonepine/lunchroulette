package com.notonepine.lunchroulette;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class NetworkUtils {
	private static final String baseUrl = "http://hacktech4.cloudapp.net:80";
	private static Context sContext;
	
	public static void initialize(Context context) {
		sContext = context;
	}

	public static void postUser(JSONObject user, AsyncHttpResponseHandler callback) {
		httpClient().post(baseUrl + "/users", new RequestParams("user", user.toString()), callback);
	}

	public static void beginSearch(final String userId, double latitude, double longitude, final JsonHttpResponseHandler newUserHandler, final JsonHttpResponseHandler locationHandler) {
		RequestParams params = new RequestParams();
		params.put("id", userId);
		params.put("latitude", latitude);
		params.put("longitude", longitude);
		// Create the search
		httpClient().post(baseUrl + "/searching_users", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				pollSearch(userId, newUserHandler, locationHandler);
			}
		});
	}

	public static void getUserInfo(final String userId, final JsonHttpResponseHandler callback) {
		httpClient().get(baseUrl + "/users/" + userId, callback);
	}

	private static AsyncHttpClient httpClient() {
		AsyncHttpClient client = new AsyncHttpClient();
		client.addHeader("Content-Type", "application/json");
		return client;
	}

	private static void pollSearch(String userId, JsonHttpResponseHandler newUserHandler, JsonHttpResponseHandler locationHandler) {
		SearchResponseHandler searchHandler = new SearchResponseHandler(userId, newUserHandler, locationHandler);
		while (true) {
			httpClient().get(baseUrl + "/searching_users/" + userId + "/search", searchHandler);
		}
	}

	private static class SearchResponseHandler extends JsonHttpResponseHandler {
		JsonHttpResponseHandler newUserHandler;
		JsonHttpResponseHandler locationHandler;

		String userId;
		List<Integer> ids = new ArrayList<Integer>();

		public SearchResponseHandler(String userId, JsonHttpResponseHandler newUserHandler, JsonHttpResponseHandler locationHandler) {
			this.userId = userId;
			this.newUserHandler = newUserHandler;
			this.locationHandler = locationHandler;
		}

		private void repoll() {
			new Handler(sContext.getMainLooper()).postDelayed(new Runnable() {
				@Override
				public void run() {
					httpClient().get(baseUrl + "/searching_users/" + userId + "/search", SearchResponseHandler.this);
				}
			}, 5000);
		}

		@Override
		public void onSuccess(JSONObject response) {
			JSONObject restaurant = response.optJSONObject("restaurant");

			checkNewUsers(response.optJSONArray("matches"));

			if (restaurant != null) {
				locationHandler.onSuccess(restaurant);
			} else {
				// wait and do it again
				repoll();
			}
		}

		private void checkNewUsers(JSONArray idsArray) {
			for (int i = 0; i < idsArray.length(); i++) {
				Integer id = Integer.valueOf(idsArray.optInt(i));
				if (ids.indexOf(id) == -1) {
					NetworkUtils.getUserInfo(id.toString(), newUserHandler);
					ids.add(id);
				}
			}
		}
	}
}
