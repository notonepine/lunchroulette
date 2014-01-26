package com.notonepine.lunchroulette;

import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class NetworkUtils {
	private static final String baseUrl = "http://hacktech4.cloudapp.net:80";
	
	public static void postUser(JSONObject user, AsyncHttpResponseHandler callback) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(baseUrl + "/users", new RequestParams("user", user.toString()), callback);
	}
}
