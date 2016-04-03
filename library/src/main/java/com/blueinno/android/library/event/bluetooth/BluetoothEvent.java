package com.blueinno.android.library.event.bluetooth;

public class BluetoothEvent {

    public int type;
    public Object data;

    //  ========================================================================================

    public BluetoothEvent(int type) {
        this.type = type;
    }

    public BluetoothEvent(int type, Object data) {
        this.type = type;
        this.data = data;
    }

}
