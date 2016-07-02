package com.xiaoyezi.tools.networktest.models;

import android.util.Log;

import com.xiaoyezi.tools.networktest.utils.Utils;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jianmin on 16-7-1.
 */
public class TcpModel extends NetModel {
    private static final String TAG = "TcpModel";

    Socket mSocket = null;
    DataOutputStream mOutputStream = null;
    DataInputStream mInputStream = null;

    byte[] mBuf = new byte[1024];

    private int mReceivedCount = 0;
    private int mSentCount = 0;

    public TcpModel(String host, String port) {
        super(host, port);
    }

    @Override
    public int init() throws IOException {
        mSocket = new Socket(getHost(), Integer.parseInt(getPort()));
        mOutputStream = new DataOutputStream(mSocket.getOutputStream());
        mInputStream = new DataInputStream(mSocket.getInputStream());
        return 0;
    }

    @Override
    public int clean() {
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (Exception e) {
            }
        }

        return 0;
    }

    @Override
    public int sendData(String data) {
        try {
            if (mOutputStream != null) {
                Log.d(TAG, "sendData:" + mSocket);

                // Send data
                Map<String, String> map = new HashMap<>();
                map.put("data", data);
                map.put("clientSendTime", "haha");
                JSONObject json = new JSONObject(map);
                mOutputStream.write(json.toString().getBytes());
                mSentCount++;

                Log.d(TAG, "send Data:" + json.toString() + "[" + mSentCount + "]");
            } else {
                Log.d(TAG, "Send data failed for mOutputStream is null!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public int recvData() {
        try {
            // Receive data
            if (mInputStream != null) {
                //Log.d(TAG, "BEGIN read  data!!!![ " + mInputStream + "]");
                int len = mInputStream.read(mBuf);
                mReceivedCount++;
                Log.d(TAG, "received data len: [" + len + "]recvCount[" + mReceivedCount + "]");

                // Save it
                //saveLog(readData);

                return 0;
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public boolean isConnected() {
        return mSocket != null;
    }

    @Override
    public void saveLog(String data) {
        try {
            JSONObject payLoad = Utils.getPayload(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadLog() {
    }
}
