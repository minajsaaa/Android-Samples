package com.blueinno.android.alcoholsensor.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blueinno.android.alcoholsensor.R;
import com.blueinno.android.alcoholsensor.activity.GraphSettingActivity;
import com.blueinno.android.alcoholsensor.activity.TerminalSettingActivity;
import com.blueinno.android.alcoholsensor.util.PreferenceUtil;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private TextView graphField;
    private TextView terminalField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        graphField = (TextView) view.findViewById(R.id.graphField);
        terminalField = (TextView) view.findViewById(R.id.terminalField);

        graphField.setOnClickListener(this);
        terminalField.setOnClickListener(this);
        return view;
    }

    //  ========================================================================================

    @Override
    public void onClick(View v) {
        Intent intent = null;
        if( v.getId() == R.id.graphField ) {
            intent = new Intent(getActivity(), GraphSettingActivity.class);
            getActivity().startActivityForResult(intent, PreferenceUtil.REQUEST_GRAPH);
        } else {
            intent = new Intent(getActivity(), TerminalSettingActivity.class);
            getActivity().startActivityForResult(intent, PreferenceUtil.REQUEST_TERMINAL);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    //  ========================================================================================

}
