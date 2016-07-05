package com.xiaoyezi.tools.networktest.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.xiaoyezi.tools.networktest.R;
import com.xiaoyezi.tools.networktest.utils.Constants;
import com.xiaoyezi.tools.networktest.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jianmin on 16-7-3.
 */
public class LogFragment extends Fragment {

    private ListView mListView;

    private TextView mNoLogsView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = (ListView) view.findViewById(R.id.file_logs);

        mNoLogsView = (TextView) view.findViewById(R.id.no_logs);

        updateListView();
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
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Update log files' list view.
     */
    private void updateListView() {
        ArrayList<Map<String, String>> listItems = new ArrayList<Map<String, String>>();
        String tcpLogPath = Utils.getLogPath(Constants.TRANSPORT_TYPE.TYPE_TCP);
        if (tcpLogPath != null && (new File(tcpLogPath)).exists()) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("filePath", tcpLogPath);
            map.put("fileDesc", "TCP log");
            map.put("fileSize", Utils.getFileSize(new File(tcpLogPath)));
            listItems.add(map);
        }

        String udpLogPath = Utils.getLogPath(Constants.TRANSPORT_TYPE.TYPE_UDP);
        if (udpLogPath != null && (new File(udpLogPath)).exists()) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("filePath", udpLogPath);
            map.put("fileDesc", "UDP log");
            map.put("fileSize", Utils.getFileSize(new File(udpLogPath)));
            listItems.add(map);
        }

        String rudpLogPath = Utils.getLogPath(Constants.TRANSPORT_TYPE.TYPE_RUDP);
        if (rudpLogPath != null && (new File(rudpLogPath)).exists()) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("filePath", rudpLogPath);
            map.put("fileDesc", "RUDP log");
            map.put("fileSize", Utils.getFileSize(new File(rudpLogPath)));
            listItems.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(getContext(), listItems,
                R.layout.log_item,
                new String[]{"filePath", "fileDesc", "fileSize"},
                new int[]{R.id.filePath, R.id.fileDesc, R.id.fileSize}
        );

        if (listItems.size() > 0) {
            mListView.setVisibility(View.VISIBLE);
            mNoLogsView.setVisibility(View.GONE);
            mListView.setAdapter(adapter);

            return;
        }

        mNoLogsView.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);
    }
}
