package com.blueinno.android.smartlamp.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.blueinno.android.library.event.provider.BusProvider;
import com.blueinno.android.library.util.PreferenceUtil;
import com.blueinno.android.smartlamp.R;
import com.blueinno.android.smartlamp.constant.SharedProperty;
import com.blueinno.android.smartlamp.event.NotificationEvent;

import java.util.Calendar;

public class TimePickerFragment extends AppCompatDialogFragment implements TimePickerDialog.OnTimeSetListener {

    //  ========================================================================================

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    //  ========================================================================================

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        try {
            int total = hourOfDay * 60 + minute;
            PreferenceUtil.put(getActivity(), SharedProperty.TIMER_VALUE, total);
            BusProvider.getInstance().post(new NotificationEvent(NotificationEvent.TIME_SETTING, total));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}