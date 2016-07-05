package com.xiaoyezi.tools.networktest.models;

import android.util.Log;

import com.xiaoyezi.tools.networktest.analytics.Analytics;
import com.xiaoyezi.tools.networktest.utils.Constants;
import com.xiaoyezi.tools.networktest.utils.Utils;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Date;

/**
 * Created by jianmin on 16-7-1.
 */
public class UdpModel extends NetModel {
    private static final String TAG = "UdpModel";

    private byte[] mBuf = new byte[1024];

    private DatagramSocket mSocket = null;

    private Analytics mAnalytics = Analytics.getInstance();

    private int mReceivedCount = 0;

    private int mSentCount = 0;

    public UdpModel(String host, String port) {
        super(host, port);
    }

    @Override
    public int init() {
        try {
            mSocket = new DatagramSocket();
            mSocket.setSoTimeout(5000); // recv timeout!
        } catch (Exception e) {
            e.printStackTrace();

            return -1;
        }

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
        DatagramPacket dp = null;

        try {
            byte[] buf = Utils.buildSendPacket((new Date()).getTime(), data).toString().getBytes();

            dp = new DatagramPacket(buf, buf.length, InetAddress.getByName(getHost()), Integer.parseInt(getPort()));
            mSocket.send(dp);
            Log.d(TAG, "send !!!!");

            // change sent packet count
            mAnalytics.setSentCount(++mSentCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int recvData() {
        DatagramPacket dp = new DatagramPacket(mBuf, mBuf.length);

        try {
            // TODO: NEED CHANGE IT LATER FOR DATA HEADER.
            mSocket.receive(dp);
            if (dp.getLength() > 0) {
                mAnalytics.setRecvCount(++mReceivedCount);

                long t = (new Date()).getTime();
                JSONObject data = Utils.buildRecvPacket(dp.getData(), t);
                long rtt = t - data.getLong("clientSentTime");
                mAnalytics.updateRtt(rtt);

                Log.d(TAG, "TCP recvData:[" + data.toString() + "]RTT[" + rtt + "]");

                // Save it?
                mAnalytics.saveLog(Constants.TRANSPORT_TYPE.TYPE_UDP, data);
            }
        }catch (Exception e) {
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
    }

    @Override
    public void loadLog() {
    }
}
