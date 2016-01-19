package com.example.zhoushengming.alltextview;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

	private static final String TAG = "main";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		View root = getLayoutInflater().inflate(R.layout.activity_main, null);
		List<View> allChildViews = getAllChildViews(root);
		for (View v : allChildViews) {
			if (v instanceof TextView) {
				if (!(v instanceof Button)) {
					Log.i(TAG, "onCreate:textview's text size is :```` " + ((TextView) v).getTextSize());
				}
			}
		}
	}

	private List<View> getAllChildViews(View view) {
		List<View> allchildren = new ArrayList<View>();
		if (view instanceof ViewGroup) {
			ViewGroup vp = (ViewGroup) view;
			for (int i = 0; i < vp.getChildCount(); i++) {
				View viewchild = vp.getChildAt(i);
				allchildren.add(viewchild);
				allchildren.addAll(getAllChildViews(viewchild));
			}
		}
		return allchildren;
	}
}
