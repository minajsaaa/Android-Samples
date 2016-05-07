package com.blueinno.android.smartlamp.fragment;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.blueinno.android.library.R;
import com.blueinno.android.library.constant.State;
import com.blueinno.android.library.core.BaseActivity;
import com.blueinno.android.library.core.BaseFragment;
import com.blueinno.android.library.event.bluetooth.BluetoothEvent;
import com.blueinno.android.library.event.provider.BusProvider;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

public class DeviceFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private ListView listView;
    private HashMap<String, BluetoothDevice> map = new HashMap<String, BluetoothDevice>();
    private ArrayAdapter adapter;

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
    public int getLayoutContentView() {
        return R.layout.activity_device_list;
    }

    //  =========================================================================================

    public void createChildren() {
        super.createChildren();

        listView = (ListView)mView.findViewById(R.id.listView);

        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, new ArrayList());
        listView.setAdapter(adapter);
        adapter.clear();
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BusProvider.getInstance().post(new BluetoothEvent(State.CONNECTING));
    }
}