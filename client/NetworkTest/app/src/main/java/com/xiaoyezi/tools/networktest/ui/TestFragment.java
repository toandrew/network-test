package com.xiaoyezi.tools.networktest.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoyezi.tools.networktest.R;
import com.xiaoyezi.tools.networktest.analytics.Analytics;
import com.xiaoyezi.tools.networktest.controllers.NetManager;
import com.xiaoyezi.tools.networktest.utils.Constants;

import java.util.Timer;
import java.util.TimerTask;

public class TestFragment extends Fragment {
    private static final String TAG = "TestFragment";

    private View mView;

    private NetManager mNetManager;

    private Button mSendButton;
    private Spinner mSpinner;

    private Constants.TRANSPORT_TYPE mPreferredTransportMode = Constants.TRANSPORT_TYPE.TYPE_TCP;

    private Timer mTimer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNetManager = new NetManager(getActivity());
        mNetManager.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mView = view;

        Log.e(TAG, "onViewCreated!!!!");
        SharedPreferences settings = getActivity().getPreferences(0);

        EditText editText = (EditText) mView.findViewById(R.id.editTextIP);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setText(settings.getString("host", "192.168.89.243"), TextView.BufferType.EDITABLE);
        editText = (EditText) mView.findViewById(R.id.editTextPort);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);

        // fixed port
        int type = settings.getInt("type", 0);
        String defaultPort = Constants.TCP_DEFAULT_PORT;
        if (type == 1) {
            defaultPort = Constants.UDP_DEFAULT_PORT;
        } else if (type == 2) {
            defaultPort = Constants.RUDP_DEFAULT_PORT;
        }
        editText.setText(defaultPort, TextView.BufferType.EDITABLE);

        editText = (EditText) mView.findViewById(R.id.editTextData);
        editText.setText(settings.getString("dataText", ""), TextView.BufferType.EDITABLE);

        ArrayAdapter dataTypeArrayAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.plants, android.R.layout.simple_spinner_item);
        dataTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner = (Spinner) mView.findViewById(R.id.dataType);
        mSpinner.setAdapter(dataTypeArrayAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                       long arg3) {
                Constants.TRANSPORT_TYPE type = Constants.TRANSPORT_TYPE.TYPE_NONE;

                switch (arg2) {
                    case 0:
                        type = Constants.TRANSPORT_TYPE.TYPE_TCP;
                        break;
                    case 1:
                        type = Constants.TRANSPORT_TYPE.TYPE_UDP;
                        break;
                    case 2:
                        type = Constants.TRANSPORT_TYPE.TYPE_RUDP;
                        break;
                }

                setPreferredTransportMode(type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                setPreferredTransportMode(Constants.TRANSPORT_TYPE.TYPE_TCP);
            }
        });
        mSpinner.setSelection(settings.getInt("type", 0));

        mSendButton = (Button) mView.findViewById(R.id.buttonSend);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canStart()) {
                    startLoop();
                } else {
                    stopLoop();
                }

                updateTransportState();
            }
        });

        setPreferredTransportMode(mNetManager.getCurrentTransportState());

        mTimer = new Timer();

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "xxxx!!!");
                        updateTransportState();
                    }
                });
            }
        }, 1000, 2000);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Get current values
        EditText editText = (EditText) mView.findViewById(R.id.editTextIP);
        String host = editText.getText().toString();
        editText = (EditText) mView.findViewById(R.id.editTextPort);
        String port = editText.getText().toString();
        editText = (EditText) mView.findViewById(R.id.editTextData);
        String dataText = editText.getText().toString();

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getActivity().getPreferences(0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("host", host);
        editor.putString("port", port);
        editor.putString("dataText", dataText);
        editor.putInt("type", mSpinner.getSelectedItemPosition());

        // Commit the edits!
        editor.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.e(TAG, "onDestroyView!!!!");

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy!!!!");

        mNetManager.close();
    }

    /**
     * Start loop work
     */
    private void startLoop() {
        Context context = getActivity().getApplicationContext();

        EditText editText = (EditText) mView.findViewById(R.id.editTextIP);
        String host = editText.getText().toString();
        if (!host.matches("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b")) {
            CharSequence text = "Error: Invalid IP Address";
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        editText = (EditText) mView.findViewById(R.id.editTextPort);
        String port = editText.getText().toString();
        if (!port.matches("^(6553[0-5]|655[0-2]\\d|65[0-4]\\d\\d|6[0-4]\\d{3}|[1-5]\\d{4}|[1-9]\\d{0,3}|0)$")) {
            CharSequence text = "Error: Invalid Port Number";
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        editText = (EditText) mView.findViewById(R.id.editTextData);
        String dataText = editText.getText().toString();
        if (dataText.isEmpty()) {
            dataText = getResources().getString(R.string.hint_text);
        }

        // ready to start loop
        mNetManager.startLoop(getPreferredTransportMode(), host, port, dataText);
    }

    /**
     * Stop loop work
     */
    private void stopLoop() {
        mNetManager.stopLoop();
    }

    /**
     * Check whether can trigger the loop action.
     *
     * @return
     */
    private boolean canStart() {
        Constants.TRANSPORT_TYPE type = mNetManager.getCurrentTransportState();
        return (type == Constants.TRANSPORT_TYPE.TYPE_NONE);
    }

    /**
     * Check whether we can trigger the stop loop action.
     *
     * @return
     */
    private boolean canStop() {
        Constants.TRANSPORT_TYPE type = mNetManager.getCurrentTransportState();
        return (type != Constants.TRANSPORT_TYPE.TYPE_NONE);
    }

    /**
     * Which mode should be preferred used: tcp, udp or rudp?
     *
     * @return
     */
    private Constants.TRANSPORT_TYPE getPreferredTransportMode() {
        return mPreferredTransportMode;
    }

    /**
     * Set preferred mode
     *
     * @param type
     */
    private void setPreferredTransportMode(Constants.TRANSPORT_TYPE type) {
        mPreferredTransportMode = type;

        // update port
        String defaultPort = Constants.TCP_DEFAULT_PORT;
        switch (type) {
            case TYPE_UDP:
                defaultPort = Constants.UDP_DEFAULT_PORT;
                break;
            case TYPE_RUDP:
                defaultPort = Constants.RUDP_DEFAULT_PORT;
                break;
        }

        EditText editText = (EditText) mView.findViewById(R.id.editTextPort);
        editText.setText(defaultPort, TextView.BufferType.EDITABLE);
    }

    /**
     * Update ui status.
     */
    private void updateTransportState() {
        if (canStart()) {
            mSendButton.setText(R.string.button_start);
            mSendButton.setTextColor(Color.BLACK);
        }

        if (canStop()) {
            String info = "(" + Analytics.getInstance().getRecvCount() + "/" + Analytics.getInstance().getSentCount() + ")";
            mSendButton.setText(getString(R.string.button_stop) + info);
            mSendButton.setTextColor(Color.RED);
        }
    }
}
