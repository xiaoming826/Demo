package com.ericcode.monkeys;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ericcode.monkeys.ui.MonkeysViewByValueAnimator;

public class MainActivity2 extends Activity {

	private MonkeysViewByValueAnimator monkeysViewByValueAnimator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);
		monkeysViewByValueAnimator = (MonkeysViewByValueAnimator) findViewById(R.id
				.monkeysViewByValueAnimator);
	}

	public void click(View view) {
		monkeysViewByValueAnimator.startAnimation();

	}

}
