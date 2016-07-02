package com.xiaoyezi.tools.networktest.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jianmin on 16-7-1.
 */
public class Utils {
    private static final String TAG = "Utils";

    private static double packetIndex = 0;

    public static JSONObject getPayload(String data) throws JSONException {
        JSONObject payLoad = new JSONObject(data);
        return payLoad;
    }

    public static byte[] buildsendPacket(long clientSentTime, String data) {
        Utils.packetIndex++;

        JSONObject packet = new JSONObject();

        try {
            packet.put("packetId", Utils.packetIndex);
            packet.put("clientSentTime", clientSentTime);
            packet.put("data", data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return packet.toString().getBytes();
    }

    public static byte[] buildRecvPacket(byte[] recvBuf, long clientRecvTime) {
        JSONObject packet = null;

        try {
            String recv = new String(recvBuf, "UTF8");

            packet = new JSONObject(recv);
            packet.put("clientRecvTime", clientRecvTime);
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }

        Log.d(TAG, "recv packet:" + packet.toString());

        return packet.toString().getBytes();
    }
}
