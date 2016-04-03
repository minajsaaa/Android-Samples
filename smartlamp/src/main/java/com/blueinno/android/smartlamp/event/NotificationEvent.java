package com.blueinno.android.smartlamp.event;

public class NotificationEvent {

    public static int TIME_SETTING = 0;

    //  =======================================================================================

    public int type;
    public Object data;

    //  ========================================================================================

    public NotificationEvent(int type) {
        this.type = type;
    }

    public NotificationEvent(int type, Object data) {
        this.type = type;
        this.data = data;
    }
}
