package com.blueinno.android.library.activity;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.blueinno.android.library.R;
import com.blueinno.android.library.constant.State;
import com.blueinno.android.library.core.BaseActivity;
import com.blueinno.android.library.event.bluetooth.BluetoothEvent;
import com.blueinno.android.library.event.provider.BusProvider;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

public class DeviceListActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ListView listView;
    private HashMap<String, BluetoothDevice> map = new HashMap<String, BluetoothDevice>();
    private ArrayAdapter adapter;

    private Button scanButton;

    //  ========================================================================================

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
    public int getLayoutContentView() {
        return R.layout.activity_device_list;
    }

    //  =========================================================================================

    public void createChildren() {
        if( getSupportActionBar() != null ) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        listView = (ListView)findViewById(R.id.listView);

        adapter = new ArrayAdapter(DeviceListActivity.this, android.R.layout.simple_list_item_1, new ArrayList());
        listView.setAdapter(adapter);
        adapter.clear();

        BusProvider.getInstance().post(new BluetoothEvent(State.SCAN));

    }

    @Override
    public void configureListener() {
        super.configureListener();

        listView.setOnItemClickListener(this);
    }

    @Override
    public void setProperties() {
        super.setProperties();
    }

    //  =======================================================================================

    private void update(BluetoothDevice data) {
        if (data == null)
            return;

        if (!map.containsKey(data.getAddress())) {
            String item = data.getName() + "\n" + data.getAddress();
            map.put(data.getAddress(), data);
            adapter.add(item);
        }
    }

    //  =======================================================================================

    @Subscribe
    public void onBluetoothEvent(BluetoothEvent event) {
        if( event.type == State.CONNECTING ) {

        } else if( event.type == State.SCAN ) {

        } else if( event.type == State.CONNECTED ) {
            BluetoothDevice bluetoothDevice = (BluetoothDevice)event.data;
            update(bluetoothDevice);
        }
    }

    //  =======================================================================================

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BusProvider.getInstance().post(new BluetoothEvent(State.CONNECTING));
        finish();
    }
}