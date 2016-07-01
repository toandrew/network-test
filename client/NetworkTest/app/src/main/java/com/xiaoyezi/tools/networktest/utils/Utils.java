package com.xiaoyezi.tools.networktest.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jianmin on 16-7-1.
 */
public class Utils {
    public static JSONObject getPayload(String data) throws JSONException {
        JSONObject payLoad = new JSONObject(data);
        return payLoad;
    }
}
