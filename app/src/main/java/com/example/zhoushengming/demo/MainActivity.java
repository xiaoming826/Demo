package com.example.zhoushengming.demo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import com.example.zhoushengming.demo.ui.PercentView;

public class MainActivity extends ActionBarActivity {

    private PercentView percentView;
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        percentView = (PercentView) findViewById(R.id.myCircleView);

    }

    public void click(View view) {
        Log.d(TAG, "click: ");
        percentView.setSweepValue(percentView.getSweepValue() > 50 ? 0 : 100)
                .setTextSize(40);

    }
}
