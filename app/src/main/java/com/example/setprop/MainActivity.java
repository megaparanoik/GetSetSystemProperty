package com.example.setprop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnUpdateInfo;
    Button btnUpdateFw;
    TextView FW;
    TextView HW;
    TextView EXT;
    TextView Status;
    String result = " ";

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


        FW.setText(getString(R.string.fw_ver) + "   Unknown");
        HW.setText(getString(R.string.hw_ver) + "   Unknown");
        EXT.setText(getString(R.string.extensions) + "   Unknown");
        Status.setText(getString(R.string.update_status) + " Halt");

        btnUpdateInfo.setOnClickListener(this);
        btnUpdateFw.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.id_update_button:
                FW.setText(getString(R.string.fw_ver) + "   " + getSystemProperty("hw.gps.fw"));
                HW.setText(getString(R.string.hw_ver) + "   " + getSystemProperty("hw.gps.hw"));

                int i = 1;
                String extstring = "";
                while(!getSystemProperty("hw.gps.ext" + i).isEmpty()) {
                    extstring += getSystemProperty("hw.gps.ext" + i) + ",";
                    i++;
                    if(i > 20)
                        break;
                }
                EXT.setText(getString(R.string.extensions) + "   " + extstring);

                break;

            case R.id.id_update_fw_button:
                    setSystemProperty("hw.gps.update", "start");

                new Thread(new Runnable() {
                    public void run() {
                        result = " ";
                        while( !result.equals("Complete") && !result.equals("Fail") ) {
                            result = getSystemProperty("hw.gps.update");
                            //Status.setText(result);
                            Status.post(new Runnable() {
                                @Override
                                public void run() {
                                    Status.setText(getText(R.string.update_status) + " " + result);
                                }
                            });

                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        return;
                    }
                }).start();


                break;
        }

    }

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

}
