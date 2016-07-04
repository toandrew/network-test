package com.xiaoyezi.tools.networktest.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

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

    public static JSONObject buildsendPacket(long clientSentTime, String data) {
        Utils.packetIndex++;

        JSONObject packet = new JSONObject();

        try {
            packet.put("packetId", Utils.packetIndex);
            packet.put("clientSentTime", clientSentTime);
            packet.put("data", data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return packet;
    }

    public static JSONObject buildRecvPacket(byte[] recvBuf, long clientRecvTime) {
        JSONObject packet = null;

        try {
            String recv = new String(recvBuf, "UTF8");
            Log.d(TAG, "buildRecvPacket:" + recv);
            packet = new JSONObject(recv);
            packet.put("clientRecvTime", clientRecvTime);
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }

        Log.d(TAG, "recv packet:" + packet.toString());

        return packet;
    }

    public static int fullyRead(InputStream in, byte[] buf, int len) throws IOException {
        if (len == 0 || buf == null || buf.length == 0 ) {
            return 0;
        }

        int count = len;
        int pos = 0;
        while (count > 0) {
            int readCount = in.read(buf, pos, count);
            count -= readCount;
            pos += readCount;
        }

        return len;

    }
}
