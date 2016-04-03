package com.blueinno.android.alcoholsensor.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.blueinno.android.alcoholsensor.R;
import com.blueinno.android.alcoholsensor.manager.TextFileManager;
import com.blueinno.android.alcoholsensor.util.PreferenceUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TerminalFragment extends BaseFragment implements View.OnClickListener {

    private TextView top_text;
    private View mFragmentView;
    private TextView terminal;
    private Button logButton;

    private StringBuilder logBuilder;
    private boolean isLoging = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logBuilder = new StringBuilder();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.fragment_terminal, container, false);
        terminal = (TextView)mFragmentView.findViewById(R.id.terminalTextView);
        logButton = (Button) mFragmentView.findViewById(R.id.logButton);
        logButton.setOnClickListener(this);
        top_text = (TextView)mFragmentView.findViewById(R.id.top_text);
        return mFragmentView;
    }

    @Override
    public void update(byte[] data) {
        super.update(data);
        Float f = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        int value = f.intValue();
        updateText(String.valueOf(value));
    }
/*

    private void draw() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    if( getActivity() != null ) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateText( "" + 48.3 );
                            }
                        });

                        try {
                            Thread.sleep(600);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }
*/

    //  ========================================================================================

    public void updateText(String text){
        try {
            Float max = Float.parseFloat(text);

            String limit = PreferenceUtil.get(getActivity(), PreferenceUtil.PREFERENCE_MAX_Y_SCALE);

            if( limit == null )
                limit = "300";

            if (max > Float.valueOf(limit) ) {
                top_text.setTextColor(Color.rgb(204, 61, 61));
                top_text.setText("You should not drive");
            }else{
                top_text.setTextColor(Color.rgb(71, 200, 62));
                top_text.setText("You may drive");
            }
            terminal.setText(text);

            if( isLoging ) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = dateFormat.format(new Date());
                logBuilder.append("- time : " + time + " / - vlaue : " + text + "\n" );
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //  =======================================================================================

    @Override
    public void onClick(View v) {
        if (!isLoging) {
            logButton.setText("Stop Logging");
            isLoging = true;
        } else {
            logButton.setText("Start Logging");
            isLoging = false;
            TextFileManager manager = new TextFileManager(getActivity());
            manager.save(logBuilder.toString() + "\n");
            Toast.makeText(getActivity(), "unist_log file saved", Toast.LENGTH_SHORT).show();
        }
    }

    //  ========================================================================================


}
