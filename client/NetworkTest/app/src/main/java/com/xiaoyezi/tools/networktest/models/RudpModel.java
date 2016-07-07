package com.xiaoyezi.tools.networktest.models;

import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.EnumSet;

import android.util.Log;

import com.xiaoyezi.enet.Event;
import com.xiaoyezi.enet.Host;
import com.xiaoyezi.enet.Packet;
import com.xiaoyezi.enet.Peer;
import com.xiaoyezi.tools.networktest.analytics.Analytics;
import com.xiaoyezi.tools.networktest.utils.Constants;
import com.xiaoyezi.tools.networktest.utils.Utils;

/**
 * Created by jianmin on 16-7-1.
 */
public class RudpModel extends NetModel {
    private static final String TAG = "RudpModel";

    Host mHost;

    Peer mPeer;

    private boolean mIsConnected = false;

    private Analytics mAnalytics = Analytics.getInstance();

    private int mReceivedCount = 0;
    private int mSentCount = 0;

    public RudpModel(String host, String port) {
        super(host, port);
    }

    @Override
    public int init() {
        try {
            if (mHost != null) {
                try {
                    reset();
                    mHost.clean();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            mHost = new Host(null, 1, 2, 0, 0);
            mPeer = mHost.connect(new InetSocketAddress(getHost(), Integer.parseInt(getPort())), 2, 0);
            Event event = mHost.service(5000);
            if (event != null && event.type() == Event.Type.Connect) {
                mIsConnected = true;
                Log.d(TAG, "mIsConnected:" + mIsConnected + " ChannelId:" + event.channelID());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        return 0;
    }

    @Override
    public int clean() {
        if (mHost == null) {
            return 0;
        }

        try {
            reset();
            mHost.clean();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        mIsConnected = false;

        mReceivedCount = 0;
        mSentCount = 0;

        mAnalytics.reset();

        return 0;
    }

    @Override
    public int sendData(String data) {
        try {
            Log.d(TAG, "sendData!![" + mIsConnected + "]");
            if (mIsConnected) {
                JSONObject sendData = Utils.buildSendPacket((new Date()).getTime(), data);
                mPeer.send(0, new Packet(sendData.toString().getBytes(), EnumSet.of(Packet.Flag.RELIABLE)));

                // change sent packet count
                mAnalytics.setSentCount(++mSentCount);
            }
        } catch (Exception e) {
            e.printStackTrace();

            return -1;
        }

        return 0;
    }

    @Override
    public int recvData() {
        Event event;
        try {
            while ((event = mHost.service(5000)) != null) {
                switch (event.type()) {
                    case Connect:
                        Log.d(TAG, "Connected!!!!");
                        return -1;
                    case Disconnect:
                        Log.d(TAG, "Disconnect!!!!");
                        return -1;
                    case None:
                        Log.d(TAG, "None!!!!");
                        return -1;
                    case Receive:
                        Log.d(TAG, "Receive!!!!");

                        long t = (new Date()).getTime();

                        Packet packet = event.packet();
                        ByteBuffer buf = packet.getBytes();

                        buf.clear();

                        byte[] content = new byte[buf.capacity()];
                        buf.get(content, 0, content.length);

                        JSONObject data = Utils.buildRecvPacket(content, t);
                        long rtt = t - data.getLong("clientSentTime");
                        mAnalytics.updateRtt(rtt);

                        Log.d(TAG, "Rudp recvData:[" + data.toString() + "]RTT[" + rtt + "]");

                        mAnalytics.setRecvCount(++mReceivedCount);

                        // Save it?
                        mAnalytics.saveLog(Constants.TRANSPORT_TYPE.TYPE_RUDP, data);
                        return 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        return 0;
    }

    @Override
    public boolean isConnected() {
        return mHost != null && mPeer != null && mIsConnected;
    }

    @Override
    public void saveLog(String data) {

    }

    @Override
    public void loadLog() {
    }

    /**
     * Reset peer.
     */
    private void reset() {
        if (mPeer == null) {
            return;
        }

        mPeer.disconnectNow(0);

        // no need?
//        Event event;
//        try {
//            while ((event = mHost.service(3000)) != null) {
//                switch (event.type()) {
//                    case Disconnect:
//                        Log.d(TAG, "Disconnect!!!!");
//                        return;
//                }
//            }
//        } catch(Exception e) {
//            e.printStackTrace();
//        }

        // Force the connection down.
        mPeer.reset();
    }
}
