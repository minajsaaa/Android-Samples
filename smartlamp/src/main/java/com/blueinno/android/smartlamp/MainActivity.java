package com.blueinno.android.smartlamp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
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
import java.util.Timer;

public class MainActivity extends BlueinnoActivity
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {

    private RelativeLayout cardviewLamp;
    private RelativeLayout cardviewTimer;
    private CheckBox lampSwitchCompat;
    private CheckBox timerSwitchCompat;
    private AppCompatSeekBar appCompatSeekBar;
    private TextView timerField;
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

    @Override
    protected void onDestroy() {
        byte[] min = new byte[]{(byte) Color.red(100), (byte) Color.green(125), (byte) Color.blue(0)};
        send(min);
        super.onDestroy();
    }

    //  ======================================================================================


    @Override
    public int getLayoutContentView() {
        return R.layout.activity_main;
    }

    @Override
    public void createChildren() {
        super.createChildren();

        startActivity(new Intent(MainActivity.this, IntroActivity.class));

        cardviewLamp = (RelativeLayout) findViewById(R.id.cardviewLamp);
        cardviewTimer = (RelativeLayout) findViewById(R.id.cardviewTimer);

        lampSwitchCompat = (CheckBox) findViewById(R.id.lampSwitchCompat);
        timerSwitchCompat = (CheckBox) findViewById(R.id.timerSwitchCompat);

        appCompatSeekBar = (AppCompatSeekBar) findViewById(R.id.seekBar);

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
        timerField.setOnClickListener(this);

        lampSwitchCompat.setOnCheckedChangeListener(this);
        timerSwitchCompat.setOnCheckedChangeListener(this);

        appCompatSeekBar.setOnSeekBarChangeListener(this);
    }

    //  =====================================================================================

    private void initializeProperties() {
        boolean lamp = true;    //PreferenceUtil.getValue(mContext, SharedProperty.LAMP, false);
        boolean timerLamp = PreferenceUtil.getValue(mContext, SharedProperty.TIMER_LAMP, false);
        int light = PreferenceUtil.getValue(mContext, SharedProperty.LAMP_LIGHT, 3);
        int timerValue = PreferenceUtil.getValue(mContext, SharedProperty.TIMER_VALUE, -1);

        lampSwitchCompat.setChecked(lamp);
        timerSwitchCompat.setChecked(timerLamp);
        appCompatSeekBar.setProgress(light);
//        lightField.setText(CommonUtil.getProgress(light));

        if( timerValue > 0 ) {
            timerField.setText( CommonUtil.getTimeFormat(timerValue) );
            timer.schedule(new ReservationTimerTask(), CommonUtil.getTimeSecond(timerValue));
        } else {
            timerField.setText("");
        }

        enableMode(lampSwitchCompat.isChecked());
    }

    //  ======================================================================================

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
//            Toast.makeText(mContext, "센서 데이터 전달", Toast.LENGTH_SHORT).show();
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
        int color = flag ? R.color.seekbar_progress_select : android.R.color.darker_gray;
        timerField.setTextColor(ContextCompat.getColor(mContext, color));
    }

    private byte[] getValue() {
        int value;
        if( !lampSwitchCompat.isChecked() ) {
            value = 0;
        } else {
            value = appCompatSeekBar.getProgress() * 20;
        }

        Log.e("rrobbie", "getValue : " + value );

        return new byte[]{(byte) Color.red(100), (byte) Color.green(125), (byte) Color.blue(value)};
    }

    @Override
    protected void update(byte[] data) {
        super.update(data);

        final ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        try {
            byte r = buffer.get();
            byte g = buffer.get();
            int temp = buffer.getInt();
            Toast.makeText(mContext, ("받은값 정수 변환 : " + temp) , Toast.LENGTH_SHORT ).show();

            if( temp > 0 ) {
                on();
                int value = temp / 20;
                appCompatSeekBar.setProgress(value);
            } else {
                off();
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
/*
        float f = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        String temp = String.format("%.1f", f);
        Log.e("rrobbie", "update : " + temp);
*/

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
                send(getValue());
                break;

            case R.id.cardviewTimer:
//                timerSwitchCompat.setChecked(!timerSwitchCompat.isChecked());
                AppCompatDialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), TimePickerFragment.class.getSimpleName());
                break;

            case R.id.timerField:
                AppCompatDialogFragment newFragment1 = new TimePickerFragment();
                newFragment1.show(getSupportFragmentManager(), TimePickerFragment.class.getSimpleName());
                break;

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.lampSwitchCompat:
                enableMode(isChecked);
                PreferenceUtil.put(mContext, SharedProperty.LAMP, isChecked);
                send(getValue());
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
        PreferenceUtil.put(mContext, SharedProperty.LAMP_LIGHT, appCompatSeekBar.getProgress());
//        lightField.setText(CommonUtil.getProgress(appCompatSeekBar.getProgress()));
        try {
            send(getValue());
            Toast.makeText(mContext, "전달값 : " + (seekBar.getProgress() * 20), Toast.LENGTH_SHORT ).show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //  =========================================================================================

    @Subscribe
    public void onBluetoothEvent(BluetoothEvent event) {
        if( event.type == State.CONNECTING ) {
            connect();
        } else if( event.type == State.SCAN ) {
            if(!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

//                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

                Log.e("rrobbie", "inti");

            } else {
                scan();
            }
        } else if( event.type == State.CONNECTED ) {
            bluetoothDevice = (BluetoothDevice)event.data;
        }
    }

    @Subscribe
    public void onNotificationEvent(NotificationEvent event) {
        if( event.type == NotificationEvent.TIME_SETTING) {
            int timerValue = (Integer)event.data;
            String timeTemp = CommonUtil.getTimeFormat(timerValue);
            timer.schedule(new ReservationTimerTask(), CommonUtil.getTimeSecond(timerValue));
            timerField.setText(CommonUtil.getTimeFormat(timerValue));
            String message = "[ " + timeTemp + " ]" + " 예약 설정 되었습니다.";
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
/*
            send(getValue());
*/
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("rrobbie", "result : " + (requestCode == REQUEST_ENABLE_BT) + " / " + (resultCode == RESULT_OK) );
        if( requestCode == REQUEST_ENABLE_BT ) {

            Log.e("rrobbie", "result : " + (resultCode == RESULT_OK));

            if(resultCode == RESULT_OK) {
                scan();
            } else {
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
