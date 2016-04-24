package com.blueinno.android.smartlamp;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blueinno.android.library.core.BaseActivity;

import java.util.Timer;

/**
 * Created by Rrobbie on 2016-04-24.
 */
public class IntroActivity extends BaseActivity implements View.OnClickListener {

    private Button scanButton;

    @Override
    public int getLayoutContentView() {
        return R.layout.activity_intro;
    }

    @Override
    public void createChildren() {
        super.createChildren();

        scanButton = (Button) findViewById(R.id.scanButton);
        scanButton.setOnClickListener(this);
    }

    //  =======================================================================================

    @Override
    public void onClick(View v) {

    }



}
