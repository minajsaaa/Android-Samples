package com.blueinno.android.alcoholsensor.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.blueinno.android.alcoholsensor.MainActivity;
import com.blueinno.android.alcoholsensor.R;

public class DeviceFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.fragment_devices, container, false);
        Button connectButton = (Button)myFragmentView.findViewById(R.id.connect_button);
        connectButton.setOnClickListener(this);

        return myFragmentView;
    }

    //  =======================================================================================

    @Override
    public void onClick(View v) {
        ((MainActivity)getActivity()).setCurrentItem(4);
    }

}
