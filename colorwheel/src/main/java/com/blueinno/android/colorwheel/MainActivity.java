package com.blueinno.android.colorwheel;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.blueinno.android.library.BlueinnoActivity;
import com.blueinno.android.library.activity.DeviceListActivity;
import com.blueinno.android.library.constant.State;
import com.blueinno.android.library.event.bluetooth.BluetoothEvent;
import com.github.danielnilsson9.colorpickerview.view.ColorPanelView;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;
import com.squareup.otto.Subscribe;

public class MainActivity extends BlueinnoActivity implements ColorPickerView.OnColorChangedListener, View.OnClickListener {

    private ColorPickerView			mColorPickerView;
    private ColorPanelView			mOldColorPanelView;
    private ColorPanelView			mNewColorPanelView;

    private Button					mOkButton;

    //  =======================================================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_connect) {
            startActivity(new Intent(MainActivity.this, DeviceListActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //  ======================================================================================


    @Override
    public int getLayoutContentView() {
        return R.layout.activity_main;
    }

    @Override
    public void createChildren() {
        super.createChildren();

        getWindow().setFormat(PixelFormat.RGBA_8888);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int initialColor = prefs.getInt("color_3", 0xFF000000);

        mColorPickerView = (ColorPickerView) findViewById(R.id.colorpickerview__color_picker_view);
        mOldColorPanelView = (ColorPanelView) findViewById(R.id.colorpickerview__color_panel_old);
        mNewColorPanelView = (ColorPanelView) findViewById(R.id.colorpickerview__color_panel_new);

        mOkButton = (Button) findViewById(R.id.okButton);

        ((LinearLayout) mOldColorPanelView.getParent()).setPadding(mColorPickerView.getPaddingLeft(), 0, mColorPickerView.getPaddingRight(), 0);

        mColorPickerView.setOnColorChangedListener(this);
        mColorPickerView.setColor(initialColor, true);
        mOldColorPanelView.setColor(initialColor);

        mOkButton.setOnClickListener(this);
    }

    @Override
    public void setProperties() {
        super.setProperties();

        initializeProperties();
    }

    @Override
    public void configureListener() {
        super.configureListener();

    }

    //  =====================================================================================

    private void initializeProperties() {

    }

    @Override
    protected void update(byte[] data) {
        super.update(data);

        Log.e("rrobbie", "update : " + data);
    }

    //  =========================================================================================

    @Subscribe
    public void onBluetoothEvent(BluetoothEvent event) {
        if (event.type == State.CONNECTING) {
            connect();
        } else if (event.type == State.SCAN) {
            scan();
        } else if (event.type == State.CONNECTED) {
            bluetoothDevice = (BluetoothDevice) event.data;

        }
    }

    //  =======================================================================================

    @Override
    public void onColorChanged(int newColor) {
        mNewColorPanelView.setColor(mColorPickerView.getColor());
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.okButton:
                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
                edit.putInt("color_3", mColorPickerView.getColor());
                edit.commit();


                int color = mColorPickerView.getColor();
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = (color >> 0) & 0xFF;

                break;
        }

    }



}
