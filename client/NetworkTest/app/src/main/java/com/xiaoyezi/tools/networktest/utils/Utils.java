package com.xiaoyezi.tools.networktest.utils;

import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;

/**
 * Created by jianmin on 16-7-1.
 */
public class Utils {
    private static final String TAG = "Utils";

    private static double packetIndex = 0;

    public static JSONObject getPayload(String data) throws JSONException {
        JSONObject payLoad = new JSONObject(data);
        return payLoad;
    }

    public static JSONObject buildSendPacket(long clientSentTime, String data) {
        Utils.packetIndex++;

        JSONObject packet = new JSONObject();

        try {
            packet.put("packetId", Utils.packetIndex);
            packet.put("clientSentTime", clientSentTime);
            packet.put("data", data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return packet;
    }

    public static JSONObject buildRecvPacket(byte[] recvBuf, long clientRecvTime) {
        JSONObject packet = null;

        try {
            String recv = new String(recvBuf, "UTF8");
            Log.d(TAG, "buildRecvPacket:" + recv);
            packet = new JSONObject(recv);
            packet.put("clientRecvTime", clientRecvTime);
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }

        Log.d(TAG, "recv packet:" + packet.toString());

        return packet;
    }

    public static int fullyRead(InputStream in, byte[] buf, int len) throws IOException {
        if (len == 0 || buf == null || buf.length == 0) {
            return 0;
        }

        int count = len;
        int pos = 0;
        while (count > 0) {
            int readCount = in.read(buf, pos, count);
            count -= readCount;
            pos += readCount;
        }

        return len;
    }

    public static void deleteFile(String path) {
        File file = new File(path);
        if (file.isFile() && file.exists()) {
            file.delete();
        }
    }

    public static void writeFile(String filePath, String sets)
            throws IOException {
        FileWriter fw = new FileWriter(filePath, true);
        PrintWriter out = new PrintWriter(fw);
        out.write(sets);
        out.println();
        fw.close();
        out.close();
    }

    public static String readFile(String path) {
        File file = new File(path);
        BufferedReader reader = null;
        String laststr = "";
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                // do sth
                // ...

                laststr = laststr + tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return laststr;
    }

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }

        return sdDir.toString();
    }

    public static String getLogPath(Constants.TRANSPORT_TYPE type) {
        String sdPath = getSDPath();

        if (sdPath == null) {
            return null;
        }

        switch (type) {
            case TYPE_TCP:
                return sdPath + "/" + Constants.TCP_DEFAULT_FILE;
            case TYPE_UDP:
                return sdPath + "/" + Constants.UDP_DEFAULT_FILE;
            case TYPE_RUDP:
                return sdPath + "/" + Constants.RUDP_DEFAULT_FILE;
        }

        return null;
    }

    private static String formetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    public static String getFileSize(File file) {
        long size = 0;

        try {
            if (file.exists()) {
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                size = fis.available();
            } else {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return formetFileSize(size);
    }
}
