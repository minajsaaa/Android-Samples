package com.blueinno.android.smartlamp.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blueinno.android.library.constant.State;
import com.blueinno.android.library.core.BaseFragment;
import com.blueinno.android.library.event.bluetooth.BluetoothEvent;
import com.blueinno.android.library.event.provider.BusProvider;
import com.blueinno.android.library.util.PreferenceUtil;
import com.blueinno.android.smartlamp.MainActivity;
import com.blueinno.android.smartlamp.R;
import com.blueinno.android.smartlamp.constant.SharedProperty;
import com.blueinno.android.smartlamp.event.NotificationEvent;
import com.blueinno.android.smartlamp.task.ReservationTimerTask;
import com.blueinno.android.smartlamp.util.CommonUtil;
import com.squareup.otto.Subscribe;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Timer;

public class MainFragment extends BaseFragment
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {

    private RelativeLayout cardviewLight;
    private RelativeLayout cardviewTimer;

    private SwitchCompat lampSwitchCompat;
    private SwitchCompat timerSwitchCompat;
    private AppCompatSeekBar appCompatSeekBar;
    private TextView timerField;
    private Timer timer;

    protected BluetoothAdapter bluetoothAdapter;
    protected BluetoothDevice bluetoothDevice;

    //  ======================================================================================

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(this);
        int chosenColor = Color.rgb(100, 125, 0);
        byte[] color = new byte[]{(byte) Color.red(chosenColor), (byte) Color.green(chosenColor), (byte) Color.blue(chosenColor)};
        ((MainActivity)getActivity()).sendData( color );
        super.onDestroy();
    }

    //  ======================================================================================


    @Override
    public int getLayoutContentView() {
        return R.layout.content_main;
    }

    @Override
    public void createChildren() {
        super.createChildren();

        cardviewLight = (RelativeLayout) mView.findViewById(R.id.cardviewLight);
        cardviewTimer = (RelativeLayout) mView.findViewById(R.id.cardviewTimer);

        lampSwitchCompat = (SwitchCompat) mView.findViewById(R.id.lampSwitchCompat);
        timerSwitchCompat = (SwitchCompat) mView.findViewById(R.id.timerSwitchCompat);

        appCompatSeekBar = (AppCompatSeekBar) mView.findViewById(R.id.seekBar);

        timerField = (TextView) mView.findViewById(R.id.timerField);
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

        cardviewTimer.setOnClickListener(this);
        timerField.setOnClickListener(this);

        lampSwitchCompat.setOnCheckedChangeListener(this);
        timerSwitchCompat.setOnCheckedChangeListener(this);

        appCompatSeekBar.setOnSeekBarChangeListener(this);
    }

    //  =====================================================================================

    private void initializeProperties() {
        boolean lamp = true;
        boolean timerLamp = PreferenceUtil.getValue(mContext, SharedProperty.TIMER_LAMP, false);
        int light = PreferenceUtil.getValue(mContext, SharedProperty.LAMP_LIGHT, 3);
        int timerValue = PreferenceUtil.getValue(mContext, SharedProperty.TIMER_VALUE, -1);

        lampSwitchCompat.setChecked(lamp);
        timerSwitchCompat.setChecked(timerLamp);
        appCompatSeekBar.setProgress(light);

        if( timerValue > 0 ) {
            timerField.setText( CommonUtil.getTimeFormat(timerValue) );
            timer.schedule(new ReservationTimerTask(getActivity()), CommonUtil.getTimeSecond(timerValue));
        } else {
            timerField.setText("");
        }

        enableMode(lampSwitchCompat.isChecked());
    }

    //  ======================================================================================

    public void setup(BluetoothAdapter bluetoothAdapter, BluetoothDevice bluetoothDevice) {
        this.bluetoothAdapter = bluetoothAdapter;
        this.bluetoothDevice = bluetoothDevice;
    }

    //  ======================================================================================

    private void off() {
        enableMode(false);
    }

    private void on() {
        enableMode(true);
    }

    private void enableMode(boolean flag) {
        setTrackColor(lampSwitchCompat);
        cardviewLight.setEnabled(flag);
        appCompatSeekBar.setEnabled(flag);
        setTrackColor(timerSwitchCompat);

//        timerSwitchCompat.setEnabled(flag);
//        timerPickerEnableMode(flag);
    }

    private void timerPickerEnableMode(boolean flag) {
        int color = flag ? R.color.seekbar_progress_select : android.R.color.darker_gray;
        timerField.setTextColor(ContextCompat.getColor(mContext, color));
    }

    private void setTrackColor(SwitchCompat lampSwitchCompat) {
        int color = lampSwitchCompat.isChecked() ? R.color.seekbar_progress_select : R.color.seekbar_progress;
        lampSwitchCompat.getTrackDrawable().setColorFilter(ContextCompat.getColor(getActivity(), color), PorterDuff.Mode.SRC_IN);
    }

    private byte[] getValue() {
        int value;
        if( !lampSwitchCompat.isChecked() ) {
            value = 0;
        } else {
            value = appCompatSeekBar.getProgress() * 20;
        }

        int chosenColor = Color.rgb(100, 125, value);
        byte[] color = new byte[]{(byte) Color.red(chosenColor), (byte) Color.green(chosenColor), (byte) Color.blue(chosenColor)};
        return color;
    }

    public void update(byte[] data) {
        float f = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getFloat();

        if( f > 0 ) {
            on();
            int value = (int)f / 20;
            appCompatSeekBar.setProgress(value);
        } else {
            off();
        }
    }

    //  ======================================================================================

    @Override
    public void onClick(View v) {
        if( lampSwitchCompat.isChecked() ) {
            switch (v.getId()) {
                case R.id.timerField:
                case R.id.cardviewTimer:
                    AppCompatDialogFragment newFragment = new TimePickerFragment();
                    newFragment.show(getChildFragmentManager(), TimePickerFragment.class.getSimpleName());
                    break;
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.lampSwitchCompat:
                enableMode(isChecked);
                PreferenceUtil.put(mContext, SharedProperty.LAMP, isChecked);
                ((MainActivity)getActivity()).sendData( getValue() );
                break;

            case R.id.timerSwitchCompat:
                try {
                    timerPickerEnableMode(isChecked);
                    PreferenceUtil.put(mContext, SharedProperty.TIMER_LAMP, isChecked);

                    if( !isChecked ) {
                        timer.cancel();
                    } else {
                        int timerValue = PreferenceUtil.getValue(mContext, SharedProperty.TIMER_VALUE, -1);

                        if( timerValue > 0 )
                            if( timer != null ) {
                                timer.schedule(new ReservationTimerTask(getActivity()), CommonUtil.getTimeSecond(timerValue));
                            }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
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

        try {
            ((MainActivity)getActivity()).sendData(getValue());
            Toast.makeText(mContext, "전달값 : " + (seekBar.getProgress() * 20), Toast.LENGTH_SHORT ).show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //  =========================================================================================

    @Subscribe
    public void onBluetoothEvent(BluetoothEvent event) {
        if( event.type == State.CONNECTED ) {
            bluetoothDevice = (BluetoothDevice)event.data;
        }
    }

    @Subscribe
    public void onNotificationEvent(NotificationEvent event) {
        if( event.type == NotificationEvent.TIME_SETTING) {
            int timerValue = (Integer)event.data;
            String timeTemp = CommonUtil.getTimeFormat(timerValue);
            timer.schedule(new ReservationTimerTask(getActivity()), CommonUtil.getTimeSecond(timerValue));
            timerField.setText(CommonUtil.getTimeFormat(timerValue));
            String message = "[ " + timeTemp + " ]" + " 예약 설정 되었습니다.";
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }


}
