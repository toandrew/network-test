package com.xiaoyezi.tools.networktest.models;


import android.util.Log;

import com.xiaoyezi.enet.Event;
import com.xiaoyezi.enet.Host;
import com.xiaoyezi.enet.Packet;
import com.xiaoyezi.enet.Peer;

import java.net.InetSocketAddress;
import java.util.EnumSet;

/**
 * Created by jianmin on 16-7-1.
 */
public class RudpModel extends NetModel {
    private static final String TAG = "RudpModel";

    Host mHost;

    Peer mPeer;

    private boolean mIsConnected = false;

    public RudpModel(String host, String port) {
        super(host, port);
    }

    @Override
    public int init() {
        try {
            mHost = new Host(null, 1, 2, 0, 0);
            mPeer = mHost.connect(new InetSocketAddress(getHost(), Integer.parseInt(getPort())), 2, 0);
           Event event = mHost.service(5000);
            if (event.type() == Event.Type.Connect) {
                mIsConnected = true;
            }
            Log.d(TAG, "!!!event:" + event.type() + " mIsConnected:" + mIsConnected + " ChannelId:" + event.channelID());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        return 0;
    }

    @Override
    public int clean() {
        if (mHost == null) {
            Log.d(TAG, "clean: host is null?!");
            return 0;
        }

        try {
            mHost.clean();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public int sendData(String data) {
        try {
            Log.d(TAG, "sendData!!");
            Event event = mHost.service(5000);
            if (event == null) {
                return -1;
            }
            if (event.type() == Event.Type.Connect) {
                mIsConnected = true;
            }
            Log.d(TAG, "!!!event:" + event.type() + " mIsConnected:" + mIsConnected + " ChannelId:" + event.channelID());

            if (mIsConnected) {
                Log.d(TAG, "send data!!!");
                String helloWorld = "Hello World!!";
                mPeer.send(0, new Packet(helloWorld.getBytes(), EnumSet.of(Packet.Flag.RELIABLE)));
            }
//            Event event;
//            while ((event = mHost.service(5000)) != null) {
//                switch (event.type()) {
//                    case Connect:
//                        Log.d(TAG, "Connected!!!!");
//                        break;
//                    case Disconnect:
//                        Log.d(TAG, "Disconnect!!!!");
//                        break;
//                    case None:
//                        Log.d(TAG, "None!!!!");
//                        break;
//                    case Receive:
//                        Log.d(TAG, "Receive!!!!");
//                        break;
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int recvData() {
        Event event;
        try {
            while ((event = mHost.service(Integer.MAX_VALUE)) != null) {
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
                        return 0;
                }
            }
        }catch(Exception e) {
            e.printStackTrace();

            return -1;
        }

        return 0;
    }

    @Override
    public boolean isConnected() {
        return mHost != null && mPeer != null;
    }

    @Override
    public void saveLog(String data) {

    }

    @Override
    public void loadLog() {
    }
}
