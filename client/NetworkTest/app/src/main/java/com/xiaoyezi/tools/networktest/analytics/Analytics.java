package com.xiaoyezi.tools.networktest.analytics;

import com.xiaoyezi.tools.networktest.utils.Constants;
import com.xiaoyezi.tools.networktest.utils.Utils;

import org.json.JSONObject;

/**
 * Created by jianmin on 16-7-2.
 */
public class Analytics {
    private static Analytics mInstance = null;

    private int mSentCount = 0;
    private int mRecvCount = 0;

    // mLoss = （mSentCount - mRecvCount)/mSentCount
    private float mLoss = 0;

    // mRtt = (mClientRecvTime - mClientSentTime) - (mServerRespTime - mServerRecvTime)
    private double mMinRtt = Long.MAX_VALUE;
    private double mMaxRtt = 0;

    private double mAvgRtt = 0;
    private double mTotalRtt = 0;

    private double mCurrentRtt = 0;

    private Analytics() {
    }

    /**
     * Single
     *
     * @return
     */
    public static Analytics getInstance() {
        if (mInstance == null) {
            init();
        }

        return mInstance;
    }

    /**
     * Reset all
     */
    public void reset() {
        mSentCount = 0;
        mRecvCount = 0;

        // mLoss = （mSentCount - mRecvCount)/mSentCount
        mLoss = 0;

        mMinRtt = Long.MAX_VALUE;
        mMaxRtt = 0;
        mAvgRtt = 0;
        mTotalRtt = 0;
        mCurrentRtt = 0;
    }

    /**
     * Save sent package count
     *
     * @param count
     */
    public void setSentCount(int count) {
        mSentCount = count;
    }

    /**
     * Get sent package count
     *
     * @return
     */
    public int getSentCount() {
        return mSentCount;
    }

    /**
     * Set received package count
     *
     * @param count
     */
    public void setRecvCount(int count) {
        mRecvCount = count;
    }

    /**
     * Get received package count
     *
     * @return
     */
    public int getRecvCount() {
        return mRecvCount;
    }

    /**
     * Get current Loss rate
     *
     * @return
     */
    public float getLoss() {
        if (getSentCount() == 0) {
            return 0;
        }

        return (getSentCount() - getRecvCount()) / (float) getSentCount();
    }

    /**
     * Update RTT
     *
     * @param rtt
     */
    public void updateRtt(double rtt) {
        mMinRtt = Math.min(mMinRtt, rtt);
        mMaxRtt = Math.max(mMaxRtt, rtt);

        mTotalRtt += rtt;
        mAvgRtt = Math.max(0, mTotalRtt / getRecvCount());

        mCurrentRtt = rtt;
    }

    /**
     * Get min RTT
     *
     * @return
     */
    public double getMinRtt() {
        return mMinRtt;
    }

    /**
     * Get max RTT
     *
     * @return
     */
    public double getMaxRtt() {
        return mMaxRtt;
    }

    /**
     * Get Avg RTT
     *
     * @return
     */
    public double getAvgRtt() {
        return mAvgRtt;
    }

    /**
     * Get current package's RTT
     *
     * @return
     */
    public double getCurrentRtt() {
        return mCurrentRtt;
    }

    /**
     * Init
     */
    private synchronized static void init() {
        if (mInstance == null) {
            mInstance = new Analytics();
        }
    }

    /**
     * Save log
     *
     * @param log
     */
    public void saveLog(Constants.TRANSPORT_TYPE type, JSONObject log) {
        String sdCard = Utils.getSDPath();
        if (sdCard == null) {
            return;
        }

        String path = null;

        switch (type) {
            case TYPE_TCP:
                path = sdCard + "/" + Constants.TCP_DEFAULT_FILE;
                break;
            case TYPE_UDP:
                path = sdCard + "/" + Constants.UDP_DEFAULT_FILE;
                break;
            case TYPE_RUDP:
                path = sdCard + "/" + Constants.RUDP_DEFAULT_FILE;
                break;
        }

        try {
            Utils.writeFile(path, log.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
