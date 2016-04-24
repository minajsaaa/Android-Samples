package com.blueinno.android.smartlamp;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blueinno.android.library.BlueinnoActivity;
import com.blueinno.android.library.activity.DeviceListActivity;
import com.blueinno.android.library.constant.State;
import com.blueinno.android.library.event.bluetooth.BluetoothEvent;
import com.blueinno.android.library.util.PreferenceUtil;
import com.blueinno.android.smartlamp.constant.SharedProperty;
import com.blueinno.android.smartlamp.event.NotificationEvent;
import com.blueinno.android.smartlamp.fragment.TimePickerFragment;
import com.blueinno.android.smartlamp.task.ReservationTimerTask;
import com.blueinno.android.smartlamp.util.CommonUtil;
import com.squareup.otto.Subscribe;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;

public class MainActivity extends BlueinnoActivity
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {

    private CardView cardviewLamp;
    private CardView cardviewTimer;
    private CardView cardviewTimePicker;

    private SwitchCompat lampSwitchCompat;
    private SwitchCompat timerSwitchCompat;

    private AppCompatSeekBar appCompatSeekBar;

    private TextView timerField;
    private TextView lightField;

    private Timer timer;

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

        cardviewLamp = (CardView) findViewById(R.id.cardviewLamp);
        cardviewTimer = (CardView) findViewById(R.id.cardviewTimer);
        cardviewTimePicker = (CardView) findViewById(R.id.cardviewTimePicker);

        lampSwitchCompat = (SwitchCompat) findViewById(R.id.lampSwitchCompat);
        timerSwitchCompat = (SwitchCompat) findViewById(R.id.timerSwitchCompat);

        appCompatSeekBar = (AppCompatSeekBar) findViewById(R.id.seekBar);

        lightField = (TextView) findViewById(R.id.lightField);
        timerField = (TextView) findViewById(R.id.timerField);

        timer = new Timer();
    }

    @Override
    public void setProperties() {
        super.setProperties();

        initializeProperties();
    }

    @Override
    public void configureListener() {
        super.configureListener();

        cardviewLamp.setOnClickListener(this);
        cardviewTimer.setOnClickListener(this);
        cardviewTimePicker.setOnClickListener(this);

        lampSwitchCompat.setOnCheckedChangeListener(this);
        timerSwitchCompat.setOnCheckedChangeListener(this);

        appCompatSeekBar.setOnSeekBarChangeListener(this);
    }

    //  =====================================================================================

    private void initializeProperties() {
        boolean lamp = PreferenceUtil.getValue(mContext, SharedProperty.LAMP, false);
        boolean timerLamp = PreferenceUtil.getValue(mContext, SharedProperty.TIMER_LAMP, false);
        int light = PreferenceUtil.getValue(mContext, SharedProperty.LAMP_LIGHT, 3);
        int timerValue = PreferenceUtil.getValue(mContext, SharedProperty.TIMER_VALUE, -1);

        lampSwitchCompat.setChecked(lamp);
        timerSwitchCompat.setChecked(timerLamp);
        appCompatSeekBar.setProgress(light);
        lightField.setText(CommonUtil.getProgress(light));

        if( timerValue > 0 ) {
            timerField.setText( CommonUtil.getTimeFormat(timerValue) );
            timer.schedule(new ReservationTimerTask(), CommonUtil.getTimeSecond(timerValue));
        } else {
            timerField.setText("설정된 시간이 없습니다.");
        }

        enableMode(lampSwitchCompat.isChecked());
    }

    //  ======================================================================================

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Toast.makeText(mContext, "센서 데이터 전달", Toast.LENGTH_SHORT).show();
        }
    };

    private void off() {
        enableMode(false);
    }

    private void on() {
        enableMode(true);
    }

    private void enableMode(boolean flag) {
        timerSwitchCompat.setEnabled(flag);
        appCompatSeekBar.setEnabled(flag);
        timerPickerEnableMode(timerSwitchCompat.isChecked());
    }

    private void timerPickerEnableMode(boolean flag) {
        int color = flag ? android.R.color.holo_red_dark : android.R.color.darker_gray;
        cardviewTimePicker.setEnabled(flag);
        timerField.setTextColor(ContextCompat.getColor(mContext, color));
    }

    private byte[] getValue() {
        int value;
        if( !lampSwitchCompat.isChecked() ) {
            value = 0;
        } else {
            value = appCompatSeekBar.getProgress();
        }
        Toast.makeText(mContext, ("get value : " + value), Toast.LENGTH_SHORT ).show();
        return new byte[]{(byte) Color.red(100), (byte) Color.green(125), (byte) Color.blue(value)};
    }

    @Override
    protected void update(byte[] data) {
        super.update(data);

        float f = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        String temp = String.format("%.1f", f);
        Log.e("rrobbie", "update : " + temp);
        Toast.makeText(mContext, ("receive : " + temp) , Toast.LENGTH_SHORT ).show();
/*
        Log.e("rrobbie", "update : " + temp + " / " + dateFormat.format(calendar.getTime()));
        int color = mColorPickerView.getColor();
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;*/

    }

    //  ======================================================================================

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cardviewLamp:
                lampSwitchCompat.setChecked(!lampSwitchCompat.isChecked());
                Log.d("rrobbie", "check : " + lampSwitchCompat.isChecked() );
                send(getValue());
                break;

            case R.id.cardviewTimer:
                timerSwitchCompat.setChecked(!timerSwitchCompat.isChecked());
                cardviewTimePicker.setEnabled(timerSwitchCompat.isChecked());
                break;

            case R.id.cardviewTimePicker:
                AppCompatDialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(),"TimePicker");
                break;

        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.lampSwitchCompat:
                enableMode(isChecked);
                PreferenceUtil.put(mContext, SharedProperty.LAMP, isChecked);
                break;

            case R.id.timerSwitchCompat:
                timerPickerEnableMode(isChecked);
                PreferenceUtil.put(mContext, SharedProperty.TIMER_LAMP, isChecked);

                if( !isChecked ) {
                    timer.cancel();
                } else {
                    int timerValue = PreferenceUtil.getValue(mContext, SharedProperty.TIMER_VALUE, -1);

                    if( timerValue > 0 )
                        timer.schedule(new ReservationTimerTask(), CommonUtil.getTimeSecond(timerValue));
                }
                break;
        }
    }

    //  =========================================================================================

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)  {}
    @Override public void onStartTrackingTouch(SeekBar seekBar) {}
    @Override public void onStopTrackingTouch(SeekBar seekBar) {
        Log.d("rrobbie", "stop : " + appCompatSeekBar.getProgress() );
        PreferenceUtil.put(mContext, SharedProperty.LAMP_LIGHT, appCompatSeekBar.getProgress());
        lightField.setText(CommonUtil.getProgress(appCompatSeekBar.getProgress()));
        send(getValue());
    }

    //  =========================================================================================

    @Subscribe
    public void onBluetoothEvent(BluetoothEvent event) {
        if( event.type == State.CONNECTING ) {
            connect();
        } else if( event.type == State.SCAN ) {
            scan();
        } else if( event.type == State.CONNECTED ) {
            bluetoothDevice = (BluetoothDevice)event.data;
        }
    }

    @Subscribe
    public void onNotificationEvent(NotificationEvent event) {
        if( event.type == NotificationEvent.TIME_SETTING) {
            int timerValue = (Integer)event.data;
            timer.schedule(new ReservationTimerTask(), CommonUtil.getTimeSecond(timerValue));
/*
            send(getValue());
            Log.e("rrobbie", "notification : " + timerValue + " / ");*/
        }
    }


}
