package com.xiaoyezi.tools.networktest.models;

import com.xiaoyezi.tools.networktest.utils.Utils;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jianmin on 16-7-1.
 */
public class TcpModel extends NetModel {
    Socket mSocket = null;
    DataOutputStream mOutputStream = null;
    DataInputStream mInputStream = null;

    private boolean mLoop = false;

    public TcpModel(String host, String port) {
        super(host, port);
    }

    @Override
    public int create() {
        return 0;
    }

    @Override
    public int destroy() {
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (Exception e) {
            }
        }

        return 0;
    }

    @Override
    public int startLoop(String data) {
        mLoop = true;

        try {
            mSocket = new Socket(getHost(), Integer.parseInt(getPort()));
            mOutputStream = new DataOutputStream(mSocket.getOutputStream());
            mInputStream = new DataInputStream(mSocket.getInputStream());

            do {
                // Send data
                Map<String, String> map = new HashMap<>();
                map.put("data", data);
                map.put("clientSendTime", "haha");
                JSONObject json = new JSONObject(map);
                mOutputStream.write(json.toString().getBytes());

                // Receive data
                String readData = mInputStream.readUTF();

                // Save it
                saveLog(readData);
            } while (mLoop);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {

        }
        return 0;
    }

    @Override
    public int stopLoop() {
        mLoop = false;

        destroy();

        return 0;
    }

    @Override
    public void saveLog(String data) {
        try {
            JSONObject payLoad = Utils.getPayload(data);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadLog() {
    }
}
