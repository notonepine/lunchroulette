package com.notonepine.lunchroulette;

public class Utils {
	public static String getUserAvatarUrl(String userId) {
		return "http://graph.facebook.com/" + userId + "/picture?type=large";
	}
}
