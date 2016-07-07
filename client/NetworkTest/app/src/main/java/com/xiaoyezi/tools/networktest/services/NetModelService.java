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
import com.xiaoyezi.tools.networktest.utils.Utils;

import java.io.IOException;

/**
 * This service will be used to send data according to current requested transport type.
 *
 * Please note,
 * There's only one thread which will be used to contain all net models' action.
 */
public class NetModelService extends Service implements Runnable {
    private static final String TAG = "NetModelService";

    public static boolean sIsRunning = false;

    private Thread mSendThread = null;
    private Thread mThread = null;

    private Constants.TRANSPORT_TYPE mCurrentTransportType = Constants.TRANSPORT_TYPE.TYPE_NONE;

    private NetModel mCurrentNetModel;

    private String mData = "Hello World!";

    private byte[] mLock = new byte[0];

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
            Log.d(TAG, "startLoop");

            startLoopWork(type, host, port, data);
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

        mSendThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (sIsRunning) {
                        // wait for signal
                        Log.d(TAG, "mSendThread:wait!!!!");
                        synchronized (mLock) {
                            mLock.wait();
                        }

                        if (shouldStart()) {
                            sendData(mData);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mSendThread.start();

        mThread = new Thread(this, "RecvService");
        mThread.start();

        mBinder = new NetBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sIsRunning = true;

        Log.d(TAG, "onStartCommand!");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy!");
        synchronized (mLock) {
            mLock.notifyAll();
        }

        if (mThread != null) {
            mThread.interrupt();
        }
    }

    @Override
    public void run() {
        int ret = 0;

        try {
            while (sIsRunning) {
                if (shouldStart()) {
                    if (mCurrentNetModel != null && !mCurrentNetModel.isConnected()) {
                        ret = mCurrentNetModel.init();
                        if (ret < 0) {
                            Log.e(TAG, "Failed to init net model. wait for next around!");

                            try {
                                Thread.sleep(3000);
                            } catch(Exception e) {
                                e.printStackTrace();
                            }

                            continue;
                        }
                    }

                    Log.d(TAG, "Notify write!!!!");
                    synchronized (mLock) {
                        mLock.notify();
                    }

                    ret = recvData();
                    if (ret < 0) {
                        Log.d(TAG, "recvData error!!!");
                    }
                } else {
                    Thread.sleep(100);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Fatal error", e);
        } finally {
            Log.e(TAG, "terminated.[" + sIsRunning + "]");
            stop();
        }
    }

    /**
     * Start the network model's loop
     *
     * @param type
     * @param host
     * @param port
     * @param data
     */
    private void startLoopWork(Constants.TRANSPORT_TYPE type, String host, String port, String data) {
        initNetModel(type, host, port, data);
    }

    /**
     * Let the model to stop loop work
     */
    private void stopLoopWork() {
        if (mCurrentNetModel != null) {
            mCurrentNetModel.clean();
            mCurrentNetModel = null;
        }

        Log.d(TAG, "stopLoopWork!!");

        clearSendState();
    }

    /**
     * Stop service
     */
    private void stop() {
        stopLoopWork();

        sIsRunning = false;

        stopSelf();
    }

    /**
     * Init net model
     *
     * @param host
     * @param port
     */
    private void initNetModel(Constants.TRANSPORT_TYPE type, String host, String port, String data) {
        mCurrentTransportType = type;
        mData = data;

        if (mCurrentNetModel != null) {
            mCurrentNetModel.clean();
            mCurrentNetModel = null;
        }

        switch (mCurrentTransportType) {
            case TYPE_NONE:
                Log.d(TAG, "initNetModel: TYPE_NONE!");
                break;

            case TYPE_UDP:
                mCurrentNetModel = new UdpModel(host, port);

                Log.d(TAG, "initNetModel: TYPE_UDP!");

                Utils.deleteFile(Utils.getSDPath() +  '/' + Constants.UDP_DEFAULT_FILE);
                break;

            case TYPE_TCP:
                mCurrentNetModel = new TcpModel(host, port);

                Log.d(TAG, "initNetModel: TYPE_TCP!");

                Utils.deleteFile(Utils.getSDPath() + '/' + Constants.TCP_DEFAULT_FILE);
                break;

            case TYPE_RUDP:
                mCurrentNetModel = new RudpModel(host, port);

                Log.d(TAG, "initNetModel: TYPE_RUDP!");

                Utils.deleteFile(Utils.getSDPath() + '/' + Constants.RUDP_DEFAULT_FILE);
                break;
        }
    }

    /**
     * Whether we should begin send/recv data
     *
     * @return
     */
    private boolean shouldStart() {
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
    private int recvData() throws IOException {
        int ret = 0;

        if (mCurrentNetModel != null) {
            ret = mCurrentNetModel.recvData();
        }

        return ret;
    }

    /**
     * Send data
     *
     * @param data
     */
    private void sendData(String data) {
        if (mCurrentNetModel != null) {
            mCurrentNetModel.sendData(data);
        }
    }
}
