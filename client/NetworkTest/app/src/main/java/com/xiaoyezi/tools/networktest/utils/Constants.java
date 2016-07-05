package com.xiaoyezi.tools.networktest.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jianmin on 16-6-30.
 */
public class Constants {
    public static final String ENET_LIB_NAME = "xenet";

    public enum TRANSPORT_TYPE {
        TYPE_NONE,
        TYPE_TCP,
        TYPE_UDP,
        TYPE_RUDP
    }

    public static final String TCP_DEFAULT_FILE = "network_test_tool_tcp.data";
    public static final String UDP_DEFAULT_FILE = "network_test_tool_udp.data";
    public static final String RUDP_DEFAULT_FILE = "network_test_tool_rudp.data";
}
