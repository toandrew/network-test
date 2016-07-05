package com.xiaoyezi.tools.networktest.models;

import java.io.IOException;

/**
 * Created by jianmin on 16-7-1.
 */
public abstract class NetModel {
    private String mHost;
    private String mPort;

    public NetModel(String host, String port) {
        mHost = host;
        mPort = port;
    }

    /**
     * Host
     *
     * @return
     */
    public String getHost() {
        return mHost;
    }

    /**
     * Port
     *
     * @return
     */
    public String getPort() {
        return mPort;
    }

    /**
     * Do some init work
     *
     * @return
     */
    public int init() throws IOException {
        return 0;
    }

    /**
     * Do some clean work
     *
     * @return
     */
    public int clean() {
        return 0;
    }

    /**
     * Start loop work
     *
     * @param data
     * @return
     */
    public int sendData(String data) {
        return 0;
    }

    /**
     * Stop loop work
     *
     * @return
     */
    public int recvData() {
        return 0;
    }

    /**
     * Whether it's connected
     *
     * @return
     */
    public boolean isConnected() {
        return false;
    }

    /**
     * Save log
     */
    public void saveLog(String data) {
    }

    /**
     * Log log
     */
    public void loadLog() {

    }
}
