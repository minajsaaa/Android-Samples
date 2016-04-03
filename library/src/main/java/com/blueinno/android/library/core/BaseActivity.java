package com.blueinno.android.library.core;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.bumptech.glide.Glide;

public class BaseActivity extends AppCompatActivity implements IBaseView {

    protected Context mContext;
    protected String mScreen;

//  =============================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(getLayoutContentView());
        initialize();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home: {
//                onBackPressed();
                supportFinishAfterTransition();
            }
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        try {
            Glide.get(mContext).clearMemory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        try {
            Glide.get(mContext).trimMemory(level);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//  =============================================================================================

    @Override
    public int getLayoutContentView() {
        return 0;
    }

    @Override
    public void initialize() {
        createChildren();
        setProperties();
        configureListener();
    }

    @Override
    public void createChildren() {
    }

    @Override
    public void configureListener() {
    }

    @Override
    public void setProperties() {
    }

//  =============================================================================================

}
