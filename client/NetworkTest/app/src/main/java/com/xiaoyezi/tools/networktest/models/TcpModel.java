package com.xiaoyezi.tools.networktest.models;

import android.util.Log;

import com.xiaoyezi.tools.networktest.analytics.Analytics;
import com.xiaoyezi.tools.networktest.utils.Utils;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;

/**
 * Created by jianmin on 16-7-1.
 */
public class TcpModel extends NetModel {
    private static final String TAG = "TcpModel";

    private Analytics mAnalytics = Analytics.getInstance();

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
        mSocket.setSoTimeout(5000);

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

        mReceivedCount = 0;
        mSentCount = 0;

        mAnalytics.reset();

        return 0;
    }

    @Override
    public int sendData(String data) {
        try {
            if (mOutputStream != null) {
                Log.d(TAG, "sendData:" + mSocket);

                // build data
                JSONObject sendData = Utils.buildsendPacket((new Date()).getTime(), data);

                // send it
                mOutputStream.write(sendData.toString().getBytes());

                // change sent packet count
                mAnalytics.setSentCount(++mSentCount);

                Log.d(TAG, "send Data:" + sendData.toString() + "[" + mSentCount + "]");
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

                int len = mInputStream.read(mBuf);
                if (len > 0) {
                    mAnalytics.setRecvCount(++mReceivedCount);

                    long t = (new Date()).getTime();
                    JSONObject recvData = Utils.buildRecvPacket(mBuf, t);
                    long rtt = t - recvData.getLong("clientSentTime");
                    mAnalytics.updateRtt(rtt);

                    Log.d(TAG, "TCP recvData:[" + recvData.toString() + "]RTT[" + rtt + "]");

                    // Save it
                    mAnalytics.saveLog(recvData);
                }

                Log.d(TAG, "received data len: [" + len + "]recvCount[" + mReceivedCount + "]");

                return len;
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
