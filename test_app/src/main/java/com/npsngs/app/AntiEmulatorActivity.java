package com.npsngs.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.forthe.tool.AntiEmulator;
import com.forthe.tool.DeviceInfo;


public class AntiEmulatorActivity extends Activity {
    private TextView tv_title, tv_info;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antiemulator);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_info = (TextView) findViewById(R.id.tv_info);

        AntiEmulator antiEmulator = new AntiEmulator();
        boolean isOnEmulator = antiEmulator.isOnEmulator(this);
        tv_title.setText(isOnEmulator?"Emulator":"Device");
        tv_info.setText(antiEmulator.getLastRateRecord()+"\n\n"+ DeviceInfo.showBuild(this));


    }
}
