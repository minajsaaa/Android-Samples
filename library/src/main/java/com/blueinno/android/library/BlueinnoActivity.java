package com.blueinno.android.library;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.blueinno.android.library.constant.State;
import com.blueinno.android.library.core.BaseActivity;
import com.blueinno.android.library.event.bluetooth.BluetoothEvent;
import com.blueinno.android.library.event.provider.BusProvider;
import com.blueinno.android.library.helper.BluetoothHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class BlueinnoActivity extends BaseActivity implements BluetoothAdapter.LeScanCallback {

    protected int state;
    protected boolean scanStarted;
    protected boolean scanning;

    protected BluetoothAdapter bluetoothAdapter;
    protected BluetoothDevice bluetoothDevice;
    protected BlueinnoService blueinnoService;

    protected BluetoothManager bluetoothManager;

    //  ==========================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onDestroy() {
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    //  ========================================================================================


    @Override
    public void createChildren() {
        super.createChildren();

        bluetoothManager =(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        Log.e("rrobbie", "create children : " + bluetoothAdapter );
    }

    @Override
    public void setProperties() {
        super.setProperties();

    }

    //  ======================================================================================

    protected void update(byte[] data) {
        float f = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        String temp = String.format("%.1f", f);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Log.e("rrobbie", "update : " + temp + " / " + dateFormat.format(calendar.getTime()));
/*
        Log.e("rrobbie", "update : " + temp + " / " + dateFormat.format(calendar.getTime()));
        int color = mColorPickerView.getColor();
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;*/
    }

    protected void send(byte[] data) {
        if( blueinnoService == null )
            return;

        try {
            blueinnoService.send(data);
        } catch (Exception e) {
            Log.w("rrobbie", "Lost connection to service", e);
            unbindService(blueinnoServiceConnection);
        }
    }

    //  ========================================================================================

    public void scan() {
        scanStarted = true;
        registerReceiver(scanModeReceiver, new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));
        registerReceiver(bluetoothStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        registerReceiver(blueinnoReceiver, BlueinnoService.getIntentFilter());
        updateState(bluetoothAdapter.isEnabled() ? State.DISCONNECTED : State.BLUETOOTH_OFF);
        bluetoothAdapter.startLeScan(new UUID[]{ BlueinnoService.UUID_SERVICE }, this);
    }

    public void connect() {
        Intent rfduinoIntent = new Intent(this, BlueinnoService.class);
        bindService(rfduinoIntent, blueinnoServiceConnection, BIND_AUTO_CREATE);
    }

    public void disConnect() {
        bluetoothAdapter.stopLeScan(this);

        if( blueinnoReceiver != null ) {
            unregisterReceiver(scanModeReceiver);
            unregisterReceiver(bluetoothStateReceiver);
            unregisterReceiver(blueinnoReceiver);
        }
    }

    //  ========================================================================================

    protected void upgradeState(int newState) {
        if (newState > state) {
            updateState(newState);
        }
    }

    protected void downgradeState(int newState) {
        if (newState < state) {
            updateState(newState);
        }
    }

    protected void updateState(int newState) {
        state = newState;
    }

    //  ========================================================================================

    private BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
            if (state == BluetoothAdapter.STATE_ON) {
                upgradeState(State.DISCONNECTED);
            } else if (state == BluetoothAdapter.STATE_OFF) {
                downgradeState(State.BLUETOOTH_OFF);
            }
            Log.e("rrobbie", "bluetoothStateReceiver");
        }
    };

    private BroadcastReceiver scanModeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            scanning = (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_NONE);
            scanStarted &= scanning;
            Log.e("rrobbie", "scanModeReceiver");
        }
    };

    private ServiceConnection blueinnoServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            blueinnoService = ((BlueinnoService.LocalBinder) service).getService();
            
            if (blueinnoService.initialize()) {
                if( bluetoothDevice != null ) {
                    if (blueinnoService.connect(bluetoothDevice.getAddress())) {
                        upgradeState(State.CONNECTING);
                        String message = bluetoothDevice.getName() + " connect success";
                        Toast.makeText(BlueinnoActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            blueinnoService = null;
            downgradeState(State.DISCONNECTED);
        }
    };

    private BroadcastReceiver blueinnoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BlueinnoService.ACTION_CONNECTED.equals(action)) {
                upgradeState(State.CONNECTED);
            } else if (BlueinnoService.ACTION_DISCONNECTED.equals(action)) {
                downgradeState(State.DISCONNECTED);
            } else if (BlueinnoService.ACTION_DATA_AVAILABLE.equals(action)) {
                update(intent.getByteArrayExtra(BlueinnoService.EXTRA_DATA));
            }
        }
    };

    //  ========================================================================================

    @Override
    public void onLeScan(BluetoothDevice device, final int rssi, final byte[] scanRecord) {
        bluetoothAdapter.stopLeScan(this);
        bluetoothDevice = device;

        Log.e("rrobbie", "onLeScan : " + bluetoothDevice  );

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("rrobbie", "device info : " + BluetoothHelper.getDeviceInfoText(bluetoothDevice, rssi, scanRecord) );
                BusProvider.getInstance().post(new BluetoothEvent(State.CONNECTED, bluetoothDevice));
            }
        });
    }
}
