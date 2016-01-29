package com.ericcode.monkeys;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.ericcode.monkeys.ui.DropEffectView;

public class MainActivity2 extends Activity {
	public static String TAG = "MainActivity2";

	private DropEffectView mDropEffectView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);
		mDropEffectView = (DropEffectView) findViewById(R.id
				.monkeysViewByValueAnimator);
	}

	public void click(View view) {
		DropEffectView.Callback callback = new DropEffectView.Callback() {
			@Override
			public void onEnd() {
				Logger.i(TAG, "end");
//				mDropEffectView.setVisibility(View.GONE);
			}
		};

//		showEggs(R.drawable.drop_effect_monkey, callback);
//		showEggs(R.drawable.drop_effect_heart, callback);
		showEggs(R.drawable.drop_effect_red, callback);
	}

	private void showEggs(int imageId, DropEffectView.Callback callback) {
		mDropEffectView.setVisibility(View.VISIBLE);
		mDropEffectView.start(imageId, callback);
	}
}
