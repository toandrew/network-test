package com.xiaoyezi.tools.networktest.analytics;

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
    private double mMinRtt = 0;
    private double mMaxRtt = 0;

    private Analytics() {
    }

    public static Analytics getInstance() {
        if (mInstance == null) {
            init();
        }

        return mInstance;
    }

    public void reset() {
        mSentCount = 0;
        mRecvCount = 0;

        // mLoss = （mSentCount - mRecvCount)/mSentCount
        mLoss = 0;

        mMinRtt = 0;
        mMaxRtt = 0;
    }

    public void setSentCount(int count) {
        mSentCount = count;
    }

    public int getSentCount() {
        return mSentCount;
    }

    public void setRecvCount(int count) {
        mRecvCount = count;
    }

    public int getRecvCount() {
        return mRecvCount;
    }

    public float getLoss() {
        if (getSentCount() == 0) {
            return 0;
        }

        return (getSentCount() - getRecvCount())/ (float)getSentCount();
    }

    public void updateRtt(double rtt) {
        mMinRtt = Math.min(mMinRtt, rtt);
        mMaxRtt = Math.max(mMaxRtt, rtt);
    }
    public double getmMinRtt() {
        return mMinRtt;
    }

    public double getmMaxRtt() {
        return mMaxRtt;
    }

    private synchronized static void init() {
        if (mInstance == null) {
            mInstance = new Analytics();
        }
    }

    public void saveLog(byte[] buf) {
    }
}