package com.blueinno.android.alcoholsensor.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blueinno.android.alcoholsensor.R;
import com.blueinno.android.alcoholsensor.util.PreferenceUtil;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GraphFragment extends BaseFragment {

    private View myFragmentView;
    private GraphView graph;

    private LineGraphSeries<DataPoint> series;
    private Viewport viewport;
    private int lastX = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_graph, container, false);
        initialize();
        return myFragmentView;
    }

    @Override
    public void update(final byte[] data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if( getActivity() != null ) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Float f = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                            addEntry( f );
                        }
                    });
                }
            }
        }).start();
    }

    public void setMinMax(int min, int max) {
        graph.removeAllSeries();
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
        viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMaxY(max);
        viewport.setMinY(min);

        Log.e("rrobbie", "set min max");
    }

    //  ===========================================================================================

    private void initialize() {
        createChildren();
    }

    private void createChildren() {
        graph = (GraphView) myFragmentView.findViewById(R.id.graphView);
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
        viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        graph.onDataChanged(false, false);
        graph.getViewport().setScrollable(true);

        String max = PreferenceUtil.get(getActivity(), PreferenceUtil.PREFERENCE_MAX_Y_SCALE);
        String min = PreferenceUtil.get(getActivity(), PreferenceUtil.PREFERENCE_MIN_Y_SCALE);

        if (max == null)
            max = "300";

        if( min == null )
            min = "0";

        viewport.setMaxY(Integer.valueOf(max));
        viewport.setMinY(Integer.valueOf(min));
    }

    private void draw() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    if( getActivity() != null ) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addEntry(100);
                            }
                        });

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    private void addEntry(double value) {
        series.appendData(new DataPoint(lastX++, value), false, lastX);
    }

}
