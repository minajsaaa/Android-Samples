package com.blueinno.android.smartlamp.task;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.blueinno.android.smartlamp.MainActivity;

import java.util.TimerTask;

public class ReservationTimerTask extends TimerTask {

    private Context mContext;

    public ReservationTimerTask(Context mContext) {
        this.mContext = mContext;
    }

    //  =======================================================================================

    @Override
    public void run() {
        Log.e("rrobbie", "reservation timer");
        int chosenColor = Color.rgb(100, 125, 0);
        byte[] color = new byte[]{(byte) Color.red(chosenColor), (byte) Color.green(chosenColor), (byte) Color.blue(chosenColor)};
        ((MainActivity)mContext).sendData(color);
    }
}
