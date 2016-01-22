package com.ericcode.monkeys;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ericcode.monkeys.ui.MonkeysViewByValueAnimator;
import com.ericcode.monkeys.ui.MonkeysViewPuls;

public class MainActivity extends Activity {

	private MonkeysViewByValueAnimator monkeysViewByValueAnimator;
	private MonkeysViewPuls monkeysViewPuls;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//无title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		monkeysViewByValueAnimator = (MonkeysViewByValueAnimator) findViewById(R.id.monkeysViewByValueAnimator);
		monkeysViewPuls = (MonkeysViewPuls) findViewById(R.id.monkeysViewPuls);


		monkeysViewByValueAnimator.setVisibility(View.VISIBLE);
		monkeysViewPuls.setVisibility(View.GONE);
	}

	boolean showWho = true;
	public void click(View view) {
		if (showWho) {
			showWho=false;
			monkeysViewPuls.setVisibility(View.VISIBLE);
			monkeysViewByValueAnimator.setVisibility(View.GONE);
		}else {
			showWho=true;
			monkeysViewByValueAnimator.setVisibility(View.VISIBLE);
			monkeysViewPuls.setVisibility(View.GONE);
		}
	}
}
