package com.example.setprop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Timer;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnUpdateInfo;
    Button btnUpdateFw;
    TextView FW;
    TextView HW;
    TextView EXT;
    TextView Status;
    String result = " ";
    Date timer;
    boolean lock_update;
    String TAG = "GetSetProp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnUpdateInfo = findViewById(R.id.id_update_button);
        btnUpdateFw = findViewById(R.id.id_update_fw_button);
        FW = findViewById(R.id.idFWVer);
        HW = findViewById(R.id.idHWVer);
        EXT = findViewById(R.id.idExt);
        Status = findViewById(R.id.idStatus);

        timer = new Date();
        lock_update = false;

        FW.setText(getString(R.string.fw_ver) + "Unknown");
        HW.setText(getString(R.string.hw_ver) + "Unknown");
        EXT.setText(getString(R.string.extensions) + "Unknown");
        Status.setText(getString(R.string.update_status) + "Halt");

        btnUpdateInfo.setOnClickListener(this);
        btnUpdateFw.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.id_update_button:
                FW.setText(getString(R.string.fw_ver) + SystemPropertiesProxy.get("hw.gps.fw", "Unknown"));
                HW.setText(getString(R.string.hw_ver) + SystemPropertiesProxy.get("hw.gps.hw", "Unknown"));

                int i = 1;
                String extstring = "";

                while ( !SystemPropertiesProxy.get(String.format("hw.gps.ext%d", i)).equals("") ) {
                    extstring += SystemPropertiesProxy.get(String.format("hw.gps.ext%d", i)) + ",";
                    i++;
                    if (i > 20) {
                        break;
                    }
                }
                EXT.setText(getString(R.string.extensions) + extstring);

                break;

            case R.id.id_update_fw_button:
                if (!lock_update){
                    lock_update = true;

                    SystemPropertiesProxy.set("hw.gps.update", "start");

                    new Thread(new Runnable() {
                        public void run() {
                            result = " ";

                            timer.setTime(0);

                            while (!result.equals("Complete") && !result.equals("Fail")) {
                                result = SystemPropertiesProxy.get("hw.gps.update", "Unknown");
                                //Status.setText(result);
                                Status.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Status.setText(getText(R.string.update_status) + result);
                                    }
                                });

                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                timer.setSeconds(timer.getSeconds() + 1);

                                btnUpdateFw.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        btnUpdateFw.setText(String.format("00:%02d:%02d", timer.getMinutes(), timer.getSeconds()));
                                    }
                                });
                            }

                            lock_update = false;

                            btnUpdateFw.post(new Runnable() {
                                @Override
                                public void run() {
                                    btnUpdateFw.setText(getString(R.string.update_fw_btn));
                                }
                            });

                            return;
                        }
                    }).start();
                }
                break;
        }

    }
/*
    private String getSystemProperty(String propertyName) {
        String propertyValue = "[UNKNOWN]";

        try {
            Process getPropProcess = Runtime.getRuntime().exec("getprop " + propertyName);

            BufferedReader osRes = new BufferedReader(new InputStreamReader(getPropProcess.getInputStream()));

            propertyValue = osRes.readLine();

            osRes.close();
        } catch (Exception e) {
            // Do nothing - can't get property value
        }

        return propertyValue;
    }

    private String setSystemProperty(String propertyName, String propertyValue) {
        String retValue = "";
        try {
            Process getPropProcess = Runtime.getRuntime().exec("setprop " + propertyName + " " + propertyValue);

            BufferedReader osRes = new BufferedReader(new InputStreamReader(getPropProcess.getInputStream()));
            retValue = osRes.readLine();

            osRes.close();
        } catch (Exception e) {
            // Do nothing - can't get property value
        }

        return retValue;
    }
*/
}
