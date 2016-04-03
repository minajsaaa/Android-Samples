package com.blueinno.android.alcoholsensor.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;

import com.blueinno.android.alcoholsensor.R;
import com.blueinno.android.alcoholsensor.util.PreferenceUtil;

public class GraphSettingActivity extends AppCompatActivity {

    private EditText maxEditText;
    private EditText minEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        maxEditText = (EditText) findViewById(R.id.maxEditText);
        minEditText = (EditText) findViewById(R.id.minEditText);

        if( getSupportActionBar() != null )
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String max = PreferenceUtil.get(this, PreferenceUtil.PREFERENCE_MAX_Y_SCALE);
        String min = PreferenceUtil.get(this, PreferenceUtil.PREFERENCE_MIN_Y_SCALE);

        if( max == null )
            max = "300";

        if( min == null )
            min = "0";

        maxEditText.setText( max );
        minEditText.setText( min );
    }

    @Override
    public void onBackPressed() {
        PreferenceUtil.save(this, PreferenceUtil.PREFERENCE_MAX_Y_SCALE, maxEditText.getText().toString());
        PreferenceUtil.save(this, PreferenceUtil.PREFERENCE_MIN_Y_SCALE, minEditText.getText().toString());

        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}