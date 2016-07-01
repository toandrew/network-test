package com.xiaoyezi.tools.networktest.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jianmin on 16-6-30.
 */
public class Constants {
    public static final String DEFAULT_HOST = "192.168.89.2";

    public static final String ENET_LIB_NAME = "xenet";

    public enum TRANSPORT_TYPE {
        TYPE_NONE,
        TYPE_TCP,
        TYPE_UDP,
        TYPE_RUDP
    }
}
