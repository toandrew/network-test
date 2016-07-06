package com.xiaoyezi.tools.networktest.models;


import com.xiaoyezi.enet.Host;

/**
 * Created by jianmin on 16-7-1.
 */
public class RudpModel extends NetModel {
    Host mHost;

    public RudpModel(String host, String port) {
        super(host, port);
    }

    @Override
    public int init() {
        try {
            mHost = new Host(null, 1, 2, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        return 0;
    }

    @Override
    public int clean() {
        try {
            mHost.clean();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public int sendData(String data) {
        return 0;
    }

    @Override
    public int recvData() {
        return 0;
    }

    @Override
    public void saveLog(String data) {

    }

    @Override
    public void loadLog() {
    }
}
