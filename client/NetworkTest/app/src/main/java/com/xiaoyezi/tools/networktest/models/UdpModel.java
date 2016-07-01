package com.xiaoyezi.tools.networktest.models;

import java.io.IOException;

/**
 * Created by jianmin on 16-7-1.
 */
public class UdpModel extends NetModel {
    public UdpModel(String host, String port) {
        super(host, port);
    }

    @Override
    public int create() {
        return 0;
    }

    @Override
    public int destroy() {
        return 0;
    }

    @Override
    public int startLoop(String data) {
        return 0;
    }

    @Override
    public int stopLoop() {
        return 0;
    }

    @Override
    public void saveLog(String data) {
    }

    @Override
    public void loadLog() {
    }
}
