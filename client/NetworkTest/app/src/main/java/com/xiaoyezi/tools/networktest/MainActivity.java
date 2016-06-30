package com.xiaoyezi.tools.networktest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private SendManager mSendManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Restore preferences

        SharedPreferences settings = getPreferences(0);

        EditText editText = (EditText) findViewById(R.id.editTextIP);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setText(settings.getString("host", ""), TextView.BufferType.EDITABLE);
        editText = (EditText) findViewById(R.id.editTextPort);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setText(settings.getString("port", ""), TextView.BufferType.EDITABLE);
        editText = (EditText) findViewById(R.id.editTextData);
        editText.setText(settings.getString("dataText", ""), TextView.BufferType.EDITABLE);

        ArrayAdapter dataTypeArrayAdapter = ArrayAdapter.createFromResource(this, R.array.plants, android.R.layout.simple_spinner_item);
        dataTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner dataTypeSpinner = (Spinner) findViewById(R.id.dataType);
        dataTypeSpinner.setAdapter(dataTypeArrayAdapter);
        dataTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                       long arg3) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        mSendManager = new SendManager();
        mSendManager.start();
    }

    @Override
    public void onPause() {
        super.onPause();

        // Get current values
        EditText editText = (EditText) findViewById(R.id.editTextIP);
        String host = editText.getText().toString();
        editText = (EditText) findViewById(R.id.editTextPort);
        String port = editText.getText().toString();
        editText = (EditText) findViewById(R.id.editTextData);
        String dataText = editText.getText().toString();

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getPreferences(0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("host", host);
        editor.putString("port", port);
        editor.putString("dataText", dataText);

        // Commit the edits!
        editor.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSendManager.close();
    }

    public void sendData(View view) {
        Context context = getApplicationContext();

        EditText editText = (EditText) findViewById(R.id.editTextIP);
        String host = editText.getText().toString();
        if (!host.matches("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b")) {
            CharSequence text = "Error: Invalid IP Address";
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        editText = (EditText) findViewById(R.id.editTextPort);
        String port = editText.getText().toString();
        if (!port.matches("^(6553[0-5]|655[0-2]\\d|65[0-4]\\d\\d|6[0-4]\\d{3}|[1-5]\\d{4}|[1-9]\\d{0,3}|0)$")) {
            CharSequence text = "Error: Invalid Port Number";
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        editText = (EditText) findViewById(R.id.editTextData);
        String dataText = editText.getText().toString();

        String uriString = "udp://" + host + ":" + port + "/";
        uriString += Uri.encode(dataText);

        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        startActivity(intent);
    }
}
