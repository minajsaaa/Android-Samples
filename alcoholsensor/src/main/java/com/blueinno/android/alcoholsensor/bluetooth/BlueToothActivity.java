package com.blueinno.android.alcoholsensor.bluetooth;

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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.blueinno.android.alcoholsensor.constant.State;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class BlueToothActivity extends AppCompatActivity implements BluetoothAdapter.LeScanCallback {

    protected int state;
    protected boolean scanStarted;
    protected boolean scanning;

    protected BluetoothAdapter bluetoothAdapter;
    protected BluetoothDevice bluetoothDevice;
    protected RFduinoService rfduinoService;

    protected BluetoothManager bluetoothManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createChidren();
        setProperties();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //  ========================================================================================

    protected void createChidren() {
        bluetoothManager =(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    protected void setProperties() {
    }

    protected void update(byte[] data) {
        float f = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        String temp = String.format("%.1f", f);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Log.e("rrobbie", "update : " + temp + " / " + dateFormat.format(calendar.getTime()));
    }

    protected void updateUI() {
    }

    protected void updateScan(BluetoothDevice device) {
    }

    //  ========================================================================================

    public void scan() {
        scanStarted = true;
        registerReceiver(scanModeReceiver, new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));
        registerReceiver(bluetoothStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        registerReceiver(rfduinoReceiver, RFduinoService.getIntentFilter());
        updateState(bluetoothAdapter.isEnabled() ? State.DISCONNECTED : State.BLUETOOTH_OFF);
        bluetoothAdapter.startLeScan(new UUID[]{ RFduinoService.UUID_SERVICE }, this);
    }

    public void connect() {
        Intent rfduinoIntent = new Intent(this, RFduinoService.class);
        bindService(rfduinoIntent, rfduinoServiceConnection, BIND_AUTO_CREATE);
    }

    public void disConnect() {
        bluetoothAdapter.stopLeScan(this);

        if( rfduinoReceiver != null ) {
            unregisterReceiver(scanModeReceiver);
            unregisterReceiver(bluetoothStateReceiver);
            unregisterReceiver(rfduinoReceiver);
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
        updateUI();
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
            updateUI();
            Log.e("rrobbie", "scanModeReceiver");
        }
    };

    private ServiceConnection rfduinoServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            rfduinoService = ((RFduinoService.LocalBinder) service).getService();
            if (rfduinoService.initialize()) {
                if( bluetoothDevice != null ) {
                    if (rfduinoService.connect(bluetoothDevice.getAddress())) {
                        upgradeState(State.CONNECTING);
                        String message = bluetoothDevice.getName() + " Connected";
                        Toast.makeText(BlueToothActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            rfduinoService = null;
            downgradeState(State.DISCONNECTED);
        }
    };

    private BroadcastReceiver rfduinoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (RFduinoService.ACTION_CONNECTED.equals(action)) {
                upgradeState(State.CONNECTED);
            } else if (RFduinoService.ACTION_DISCONNECTED.equals(action)) {
                downgradeState(State.DISCONNECTED);
            } else if (RFduinoService.ACTION_DATA_AVAILABLE.equals(action)) {
                update(intent.getByteArrayExtra(RFduinoService.EXTRA_DATA));
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
                updateScan(bluetoothDevice);
            }
        });
    }
}
