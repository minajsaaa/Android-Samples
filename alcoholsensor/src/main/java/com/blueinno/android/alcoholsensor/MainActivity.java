package com.blueinno.android.alcoholsensor;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.blueinno.android.alcoholsensor.adapter.PagerAdapter;
import com.blueinno.android.alcoholsensor.bluetooth.BlueToothActivity;
import com.blueinno.android.alcoholsensor.component.NonViewPager;
import com.blueinno.android.alcoholsensor.fragment.DeviceFragment;
import com.blueinno.android.alcoholsensor.fragment.DeviceListFragment;
import com.blueinno.android.alcoholsensor.fragment.GraphFragment;
import com.blueinno.android.alcoholsensor.fragment.NavigationDrawerFragment;
import com.blueinno.android.alcoholsensor.fragment.SettingsFragment;
import com.blueinno.android.alcoholsensor.fragment.TerminalFragment;
import com.blueinno.android.alcoholsensor.util.PreferenceUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends BlueToothActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private DrawerLayout mDrawerLayout;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;
    private NonViewPager mViewPager;
    private PagerAdapter mPagerAdapter;

    private DeviceListFragment deviceListFragment;
    private DeviceFragment deviceFragment;
    private GraphFragment graphFragment;
    private SettingsFragment settingsFragment;
    private TerminalFragment terminalFragment;

    @Override
    protected void createChidren() {
        super.createChidren();
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mViewPager = (NonViewPager) findViewById(R.id.viewPager);
    }

    protected void setProperties() {
        super.setProperties();

        mToolbar.setTitle(getString(R.string.app_name));
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, mDrawerLayout, mToolbar);

        deviceListFragment = new DeviceListFragment();
        deviceFragment = new DeviceFragment();
        graphFragment = new GraphFragment();
        settingsFragment = new SettingsFragment();
        terminalFragment = new TerminalFragment();

        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add( deviceFragment );
        fragments.add(settingsFragment);
        fragments.add(graphFragment);
        fragments.add(terminalFragment);
        fragments.add( deviceListFragment );

        mPagerAdapter = new PagerAdapter(this, getSupportFragmentManager(), fragments);
        mViewPager.setOffscreenPageLimit(fragments.size());
        mViewPager.setAdapter(mPagerAdapter);
    }

    @Override
    public void onBackPressed() {
        if( mViewPager.getCurrentItem() != 0 ) {
            setCurrentItem(0);
            return;
        }
        super.onBackPressed();
    }

    //  =======================================================================================

    public void setCurrentItem(int position) {
        mViewPager.setCurrentItem(position);


    }

    //  =======================================================================================


    @Override
    public void connect() {
        super.connect();
        setCurrentItem(3);
    }

    @Override
    protected void update(byte[] data) {
        float f = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        String temp = String.format("%.1f", f);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        terminalFragment.update(data);
        graphFragment.update(data);
    }

    @Override
    protected void updateUI() {
    }

    @Override
    protected void updateScan(BluetoothDevice device) {
        super.updateScan(device);

        deviceListFragment.setUp(device);
    }

    //  =========================================================================================

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if( mViewPager != null ) {
            setCurrentItem(position);
        }
    }

    //  =========================================================================================

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PreferenceUtil.REQUEST_GRAPH) {
                String min = PreferenceUtil.get(this, PreferenceUtil.PREFERENCE_MIN_Y_SCALE);
                String max = PreferenceUtil.get(this, PreferenceUtil.PREFERENCE_MAX_Y_SCALE);

                if( min != null && max != null ) {
                    graphFragment.setMinMax( Integer.valueOf(min), Integer.valueOf(max) );
                } else {
                    Toast.makeText(this, "min or max is invalid value", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == PreferenceUtil.REQUEST_TERMINAL) {

            }
        }
    }
}
