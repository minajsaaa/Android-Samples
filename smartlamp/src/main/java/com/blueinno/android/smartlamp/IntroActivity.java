package com.blueinno.android.smartlamp;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.blueinno.android.library.constant.State;
import com.blueinno.android.library.core.BaseActivity;
import com.blueinno.android.library.event.bluetooth.BluetoothEvent;
import com.blueinno.android.library.event.provider.BusProvider;

public class IntroActivity extends BaseActivity implements View.OnClickListener {

    private Button scanButton;

    @Override
    public int getLayoutContentView() {
        return R.layout.activity_intro;
    }

    @Override
    public void createChildren() {
        super.createChildren();

        scanButton = (Button) findViewById(R.id.scanButton);
        scanButton.setOnClickListener(this);
    }

    //  =======================================================================================

    @Override
    public void onClick(View v) {
        startActivity(new Intent(IntroActivity.this, DeviceListActivity.class));
        finish();
    }



}
