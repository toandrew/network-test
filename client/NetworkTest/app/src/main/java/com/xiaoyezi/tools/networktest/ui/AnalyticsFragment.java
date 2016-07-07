package com.xiaoyezi.tools.networktest.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
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

    private LineChart mChart;
    protected Typeface mTfLight;

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
        }, 1000, 100);

        initRealtimeLineChart(view);
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

        if (mChart != null) {
            mChart.clear();
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
        if (getActivity() == null || getActivity().getApplicationContext() == null) {
            return;
        }

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

        // update chart
        if (Analytics.getInstance().getCurrentRtt() > 0) {
            addEntry();
        }
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

    /**
     * Init chart
     *
     * @param view
     */
    private void initRealtimeLineChart(View view) {
        mChart = (LineChart) view.findViewById(R.id.chart1);
        //mChart.setOnChartValueSelectedListener(this);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.BLACK);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        l.setTypeface(mTfLight);
        l.setTextColor(Color.RED);

        XAxis xl = mChart.getXAxis();
        xl.setTypeface(mTfLight);
        xl.setTextColor(Color.BLUE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(Color.RED);
        leftAxis.setAxisMaxValue(150f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    /**
     * Add data
     */
    private void addEntry() {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            //data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 40) + 30f), 0);
            data.addEntry(new Entry(set.getEntryCount(), (float)Analytics.getInstance().getCurrentRtt()), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(120);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());

            // this automatically refreshes the chart (calls invalidate())
            // mChart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    /**
     * Create data set
     *
     * @return
     */
    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "RTT(ms)");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.RED);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }
}
