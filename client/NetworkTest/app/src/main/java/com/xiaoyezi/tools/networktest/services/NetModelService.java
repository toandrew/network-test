package com.xiaoyezi.tools.networktest.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.xiaoyezi.tools.networktest.models.NetModel;
import com.xiaoyezi.tools.networktest.models.RudpModel;
import com.xiaoyezi.tools.networktest.models.TcpModel;
import com.xiaoyezi.tools.networktest.models.UdpModel;
import com.xiaoyezi.tools.networktest.utils.Constants;

import java.io.IOException;

/**
 * This service will be used to send data according to current requested transport type.
 * <p/>
 * Please note,
 * There's only one thread which will be used to contain all net models' action.
 */
public class NetModelService extends Service implements Runnable {
    private static final String TAG = "NetModelService";

    public static boolean sIsRunning = false;

    private Thread mThread = null;

    private Constants.TRANSPORT_TYPE mCurrentTransportType = Constants.TRANSPORT_TYPE.TYPE_NONE;

    private NetModel mCurrentNetModel;

    private String mData = "Hello World!!";

    public class NetBinder extends Binder {
        /**
         * Ready to start loop
         *
         * @param type
         * @param host
         * @param port
         * @param data
         */
        public void startLoop(Constants.TRANSPORT_TYPE type, String host, String port, String data) {
            if (mCurrentTransportType != type) {
                mCurrentTransportType = type;
                mData = data;

                onTransportTypeChanged(host, port);
            }
        }

        /**
         * Stop loop work
         */
        public void stopLoop() {
            Log.d(TAG, "stopLoop!");

            stopLoopWork();
        }

        /**
         * Get current transport type
         *
         * @return
         */
        public Constants.TRANSPORT_TYPE getTransportType() {
            return mCurrentTransportType;
        }
    }

    public NetBinder mBinder;

    public NetModelService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sIsRunning = true;

        mThread = new Thread(this, "VpnService");
        mThread.start();

        mBinder = new NetBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sIsRunning = true;

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mThread != null) {
            mThread.interrupt();
        }
    }

    @Override
    public void run() {
        try {
            while (sIsRunning) {
                // sleep to avoid busy looping

                if (shouldStartLoop()) {
                    startLoop();
                }

                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "Exception", e);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Fatal error", e);
        } finally {
            Log.e(TAG, "terminated.[" + sIsRunning + "]");
            stop();
        }
    }

    /**
     * Stop service
     */
    private synchronized void stop() {
        try {
            Log.d(TAG, "Closing NetModelService service..");
        } catch (Exception e) {
        }

        stopLoopWork();

        stopSelf();

        sIsRunning = false;
    }

    /**
     * Trigger some actions when transport type changed.
     *
     * @param host
     * @param port
     */
    private void onTransportTypeChanged(String host, String port) {
        if (mCurrentNetModel != null) {
            mCurrentNetModel.destroy();
            mCurrentNetModel = null;
        }

        switch (mCurrentTransportType) {
            case TYPE_NONE:
                Log.d(TAG, "onTransportTypeChanged: TYPE_NONE!");
                break;

            case TYPE_UDP:
                if (mCurrentNetModel == null) {
                    mCurrentNetModel = new UdpModel(host, port);
                }

                Log.d(TAG, "onTransportTypeChanged: TYPE_UDP!");
                break;

            case TYPE_TCP:
                if (mCurrentNetModel == null) {
                    mCurrentNetModel = new TcpModel(host, port);
                }

                Log.d(TAG, "onTransportTypeChanged: TYPE_TCP!");
                break;

            case TYPE_RUDP:
                if (mCurrentNetModel == null) {
                    mCurrentNetModel = new RudpModel(host, port);
                }

                Log.d(TAG, "onTransportTypeChanged: TYPE_RUDP!");
                break;
        }
    }

    /**
     * Whether we should begin send data
     *
     * @return
     */
    private boolean shouldStartLoop() {
        return mCurrentTransportType != Constants.TRANSPORT_TYPE.TYPE_NONE;
    }

    /**
     * Reset transport type to its init state which will cause the service sleep
     */
    private void clearSendState() {
        mCurrentTransportType = Constants.TRANSPORT_TYPE.TYPE_NONE;
    }

    /**
     * Let the model to send data
     */
    private void startLoop() throws IOException {
        if (mCurrentNetModel != null) {
            mCurrentNetModel.startLoop(mData);
        }
    }

    /**
     * Let the model to stop loop work
     */
    private void stopLoopWork() {
        if (mCurrentNetModel != null) {
            mCurrentNetModel.stopLoop();
            mCurrentNetModel = null;
        }

        Log.d(TAG, "stopLoopWork!!");

        clearSendState();
    }
}
