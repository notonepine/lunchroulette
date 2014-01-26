package com.notonepine.lunchroulette;

import com.facebook.model.GraphUser;

public class Utils {
    public static String getUserAvatarUrl(String userId) {
        return "http://graph.facebook.com/" + userId + "/picture?type=large";
    }
}
