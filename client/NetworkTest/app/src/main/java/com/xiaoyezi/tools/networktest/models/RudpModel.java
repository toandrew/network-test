package com.xiaoyezi.tools.networktest.models;

import java.io.IOException;

/**
 * Created by jianmin on 16-7-1.
 */
public class RudpModel extends NetModel {
    public RudpModel(String host, String port) {
        super(host, port);
    }

    @Override
    public int init() {
        return 0;
    }

    @Override
    public int clean() {
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
