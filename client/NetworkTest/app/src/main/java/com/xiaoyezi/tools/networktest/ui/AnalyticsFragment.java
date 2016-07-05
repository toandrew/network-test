package com.xiaoyezi.tools.networktest.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoyezi.tools.networktest.R;
import com.xiaoyezi.tools.networktest.analytics.Analytics;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.channels.SocketChannel;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jianmin on 16-7-3.
 */
public class AnalyticsFragment extends Fragment {
    private static final String TAG = "AnalyticsFragment";

    private TextView mIsAvailable;
    private TextView mWifi3g;
    private TextView mIsConnected;
    private TextView mIpAddr;
    private TextView mSocketIpAddr;
    private TextView mTxStat;
    private TextView mRxStat;
    private TextView mMinMaxAvg;
    private TextView mLoss;

    private SocketChannel mSocketToServer;

    private Timer mTimer;

    NumberFormat mNumberFormat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNumberFormat = NumberFormat.getNumberInstance();
        mNumberFormat.setMaximumFractionDigits(2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analytics, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mWifi3g = (TextView) view.findViewById(R.id.wifi3g);
        mIsAvailable = (TextView) view.findViewById(R.id.isAvailable);
        mIsConnected = (TextView) view.findViewById(R.id.isConnected);
        mIpAddr = (TextView) view.findViewById(R.id.ipAddr);
        mSocketIpAddr = (TextView) view.findViewById(R.id.socketIpAddr);
        mTxStat = (TextView) view.findViewById(R.id.txStat);
        mRxStat = (TextView) view.findViewById(R.id.rxStat);
        mMinMaxAvg = (TextView) view.findViewById(R.id.minMaxAvg);
        mLoss = (TextView) view.findViewById(R.id.loss);

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                });
            }
        }, 1000, 5000);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.e(TAG, "onDestroyView!!!!");

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Refresh ui view
     */
    private void refresh() {
        ConnectivityManager conMgr = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (null == netInfo) {
            mWifi3g.setText("No Active Network");
            mIsAvailable.setText("Unavailable");
            Toast.makeText(getActivity().getApplicationContext(), "No Active Network", Toast.LENGTH_SHORT).show();
            return;
        }

        // Display Network Type - 3G or WiFi
        String netType = netInfo.getTypeName();
        if (netType.equals("MOBILE") || netType.equals("mobile")) {
            netType = "3G";
        }
        mWifi3g.setText(netType);

        // Display Network Availability and Connectivity
        if (!netInfo.isAvailable()) {
            mIsAvailable.setText("Unavailable");
        } else if (!netInfo.isConnected()) {
            mIsAvailable.setText("NOT Connected");
        } else {
            mIsAvailable.setText("Available");
        }

        // Display TCP Connection Status
        if (null != mSocketToServer) {
            if (mSocketToServer.isConnected()) {
                mIsConnected.setText("TCP Connected");
            } else {
                mIsConnected.setText("TCP NOT Connected");
            }
        } else {
            mIsConnected.setText("Socket is NULL");
        }

        // Get Local IP Address and Display on the screen
        String ipAddress = getLocalIpAddress(netType);
        if (null != ipAddress) {
            mIpAddr.setText(ipAddress);
        } else {
            mIpAddr.setText("Invalid IP");
        }

        // Display TCP Socket's IP Address
        if (null == mSocketToServer || !mSocketToServer.isConnected()) {
            mSocketIpAddr.setText("0.0.0.0");
        } else {
            mSocketIpAddr.setText(mSocketToServer.socket().getLocalAddress().getHostAddress());
        }

        mTxStat.setText(Analytics.getInstance().getSentCount() + "");
        mRxStat.setText(Analytics.getInstance().getRecvCount() + "");

        float loss = 0;
        loss = Analytics.getInstance().getLoss();
        mLoss.setText(mNumberFormat.format(loss * 100) + "%");

        mMinMaxAvg.setText("MIN[" + (Analytics.getInstance().getMinRtt() >= Long.MAX_VALUE ? "0" : Analytics.getInstance().getMinRtt())
                + "ms]  MAX[" + Analytics.getInstance().getMaxRtt() + "ms] AVG[" + mNumberFormat.format(Analytics.getInstance().getAvgRtt()) + "ms] current[" + Analytics.getInstance().getCurrentRtt() + "ms]");
    }

    /**
     * Get local ip address
     *
     * @param netType
     * @return
     */
    private String getLocalIpAddress(String netType) {
        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();

            while (netInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = netInterfaces.nextElement();
                Enumeration<InetAddress> inetAddrs = netInterface.getInetAddresses();
                while (inetAddrs.hasMoreElements()) {
                    InetAddress inetAddr = inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {
                        String addr = inetAddr.getHostAddress();
                        if (netType.equals("WIFI") && !addr.startsWith("192")) {
                            continue;
                        }
                        return inetAddr.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
        }

        return null;
    }
}
