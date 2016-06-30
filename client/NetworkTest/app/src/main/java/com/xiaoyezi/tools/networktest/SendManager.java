package com.xiaoyezi.tools.networktest;

/**
 * Created by jianmin on 16-6-30.
 */
public class SendManager {
    static {
        System.loadLibrary(Constants.ENET_LIB_NAME);
    }

    public SendManager() {

    }

    public void start() {

    }

    public void close(){

    }

    public static native int sendUdpData(String data);

    public static native int sendTcpData(String data);

    public static native int sendRudpData(String data);
}
