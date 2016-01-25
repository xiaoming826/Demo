package com.ericcode.monkeys;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.ericcode.monkeys.ui.MonkeysViewPuls;

public class MainActivity extends Activity {

//	private MonkeysViewByValueAnimator monkeysViewByValueAnimator;
	private MonkeysViewPuls monkeysViewPuls;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		RelativeLayout rootView = (RelativeLayout) findViewById(R.id.rlRoot);

		monkeysViewPuls=new MonkeysViewPuls(getBaseContext());
		rootView.addView(monkeysViewPuls);

		monkeysViewPuls.startAnimatorAtTime(1000);
	}

	public void click(View view) {
		switch (view.getId()) {
//			case R.id.startAnimator:
//				monkeysViewPuls.startAnimator();
//				break;
//			case R.id.startAnimator1000ms:
//				monkeysViewPuls.startAnimatorAtTime(1000);
//				break;
			case R.id.startAnimator50:
				monkeysViewPuls.startAnimatorAtMonkeys(50);
				break;
		}


	}

//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		setContentView(R.layout.activity_main);
//
//		monkeysViewPuls = (MonkeysViewPuls) findViewById(R.id.monkeysViewPuls);
//
//
//		monkeysViewPuls.setVisibility(View.VISIBLE);
//	}

//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		setContentView(R.layout.activity_main);
//
//		monkeysViewByValueAnimator = (MonkeysViewByValueAnimator) findViewById(R.id.monkeysViewByValueAnimator);
//		monkeysViewPuls = (MonkeysViewPuls) findViewById(R.id.monkeysViewPuls);
//
//
//		monkeysViewByValueAnimator.setVisibility(View.GONE);
//		monkeysViewPuls.setVisibility(View.VISIBLE);
//	}
//
//	boolean showWho = true;
//	public void click(View view) {
//		if (showWho) {
//			showWho=false;
//			monkeysViewPuls.setVisibility(View.VISIBLE);
//			monkeysViewByValueAnimator.setVisibility(View.GONE);
//		}else {
//			showWho=true;
//			monkeysViewByValueAnimator.setVisibility(View.VISIBLE);
//			monkeysViewPuls.setVisibility(View.GONE);
//		}
//	}
}
