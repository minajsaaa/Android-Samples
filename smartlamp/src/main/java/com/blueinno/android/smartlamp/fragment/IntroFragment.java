package com.blueinno.android.smartlamp.fragment;

import android.view.View;
import android.widget.Button;

import com.blueinno.android.library.constant.State;
import com.blueinno.android.library.core.BaseFragment;
import com.blueinno.android.library.event.bluetooth.BluetoothEvent;
import com.blueinno.android.library.event.provider.BusProvider;
import com.blueinno.android.smartlamp.MainActivity;
import com.blueinno.android.smartlamp.R;

public class IntroFragment extends BaseFragment implements View.OnClickListener {

    private Button scanButton;

    @Override
    public int getLayoutContentView() {
        return R.layout.activity_intro;
    }

    @Override
    public void createChildren() {
        super.createChildren();

        scanButton = (Button) mView.findViewById(R.id.scanButton);
        scanButton.setOnClickListener(this);
    }

    //  =======================================================================================

    @Override
    public void onClick(View v) {
        BusProvider.getInstance().post(new BluetoothEvent(State.SCAN));
    }



}
