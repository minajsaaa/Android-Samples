package com.blueinno.android.smartlamp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;

import com.blueinno.android.library.BlueinnoActivity;
import com.blueinno.android.library.constant.State;
import com.blueinno.android.library.event.bluetooth.BluetoothEvent;
import com.blueinno.android.smartlamp.adapter.PagerAdapter;
import com.blueinno.android.smartlamp.component.NonViewPager;
import com.blueinno.android.smartlamp.event.NotificationEvent;
import com.blueinno.android.smartlamp.fragment.DeviceFragment;
import com.blueinno.android.smartlamp.fragment.IntroFragment;
import com.blueinno.android.smartlamp.fragment.MainFragment;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

public class MainActivity extends BlueinnoActivity {

    private NonViewPager viewPager;
    private PagerAdapter pagerAdapter;

    public static MainFragment mainFragment;

    //  ======================================================================================


    @Override
    public int getLayoutContentView() {
        return R.layout.activity_main;
    }

    @Override
    public void createChildren() {
        super.createChildren();

        IntroFragment introFragment = new IntroFragment();
        DeviceFragment deviceFragment = new DeviceFragment();
        mainFragment = new MainFragment();
        mainFragment.setup(bluetoothAdapter, bluetoothDevice);

        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(introFragment);
        fragments.add(deviceFragment);
        fragments.add(mainFragment);

        viewPager = (NonViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(fragments.size());
        pagerAdapter = new PagerAdapter(mContext, getSupportFragmentManager(), fragments);

    }

    @Override
    public void setProperties() {
        super.setProperties();

        viewPager.setAdapter(pagerAdapter);
    }

    @Override
    public void configureListener() {
        super.configureListener();

    }

    //  =====================================================================================

    public void setCurrentItem(int index) {
        viewPager.setCurrentItem(index);
    }

    public void sendData(byte[] value) {
        send(value);
    }

    //  =====================================================================================

    @Override
    protected void update(byte[] data) {
        super.update(data);

        try {
            mainFragment.update(data);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //  =========================================================================================

    @Subscribe
    public void onBluetoothEvent(BluetoothEvent event) {
        if( event.type == State.CONNECTING ) {
            connect();
            viewPager.setCurrentItem(2);
        } else if( event.type == State.SCAN ) {
            if(!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                scan();
                viewPager.setCurrentItem(1);
            }
        } else if( event.type == State.CONNECTED ) {
            bluetoothDevice = (BluetoothDevice)event.data;
        }
    }

    @Subscribe
    public void onNotificationEvent(NotificationEvent event) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == REQUEST_ENABLE_BT ) {
            if(resultCode == RESULT_OK) {
                scan();
                viewPager.setCurrentItem(1);
            } else {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
