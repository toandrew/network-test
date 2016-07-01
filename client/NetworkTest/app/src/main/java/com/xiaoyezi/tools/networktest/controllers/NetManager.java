package com.xiaoyezi.tools.networktest.controllers;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.xiaoyezi.tools.networktest.services.NetModelService;
import com.xiaoyezi.tools.networktest.utils.Constants;

/**
 * Created by jianmin on 16-6-30.
 */
public class NetManager {
    private static final String TAG = "NetManager";

    private ServiceConnection mServiceConnection;

    private boolean mIsBind = false;

    Activity mActivity;

    NetModelService.NetBinder mBinder;

    public NetManager(Activity activity) {
        mActivity = activity;

        mServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "onServiceConnected!!!!!!");

                mBinder = (NetModelService.NetBinder) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
    }

    /**
     * Start service
     */
    public void start() {
        if (!mIsBind) {
            mActivity.bindService(new Intent(mActivity, NetModelService.class), mServiceConnection, Context.BIND_AUTO_CREATE);

            mIsBind = true;
        }
    }

    /**
     * Close service
     */
    public void close() {
        if (mIsBind) {
            mActivity.unbindService(mServiceConnection);

            mIsBind = false;
        }
    }

    /**
     * Get Current transport mode: tcp, udp or rudp
     *
     * @return
     */
    public Constants.TRANSPORT_TYPE getCurrentTransportState() {
        if (mBinder == null) {
            return Constants.TRANSPORT_TYPE.TYPE_NONE;
        }

        return mBinder.getTransportType();
    }

    /**
     * Begin loop
     *
     * @param type
     * @param host
     * @param port
     * @param data
     */
    public void startLoop(Constants.TRANSPORT_TYPE type, String host, String port, String data) {
        if (mBinder != null) {
            mBinder.startLoop(type, host, port, data);
        }
    }

    /**
     * Stop loop
     */
    public void stopLoop() {
        if (mBinder != null) {
            mBinder.stopLoop();
        }
    }
}
