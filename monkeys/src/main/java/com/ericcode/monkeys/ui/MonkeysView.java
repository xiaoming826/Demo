package com.ericcode.monkeys.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.ericcode.monkeys.R;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by zhoushengming on 16/1/19.
 */
public class MonkeysView extends View {
	private static final String TAG = MonkeysView.class.getSimpleName();
	Bitmap monkey;
	int offsetHorizontal[] = new int[8];
	int offsetVertical[] = new int[16];
	float bitmapZoom = 0.5f;
	float shakeOffset;
	private int widthPixels;
	private int heightPixels;

	public MonkeysView(Context context) {
		this(context, null);
	}

	public MonkeysView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MonkeysView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		widthPixels = dm.widthPixels;
		heightPixels = dm.heightPixels;
		shakeOffset = widthPixels / offsetHorizontal.length * 0.8f;
		monkey = BitmapFactory.decodeResource(getResources(), R.drawable.monkey);
		monkey = zoomMonkey(monkey, bitmapZoom);
		for (int i = 0; i < offsetHorizontal.length; i++) {
			offsetHorizontal[i] = (widthPixels / offsetHorizontal.length) * i + new Random().nextInt(30) - 30;
		}
		for (int i = 0; i < offsetVertical.length; i++) {
			offsetVertical[i] = (heightPixels / offsetVertical.length) * i + new Random().nextInt(30) - 30;
		}
		Log.i(TAG, "MonkeysView: offsetHorizontal:" + Arrays.toString(offsetHorizontal));
	}

	private Bitmap zoomMonkey(Bitmap bitmap, float zoom) {
		Matrix matrix = new Matrix();
		matrix.postScale(zoom, zoom); //长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizeBmp;

	}

	int offsetTop = 0;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		offsetTop=100;
		Paint paint_line = new Paint();                       //绘制直线
		paint_line.setColor(Color.BLUE);

		for (int j = 0; j < offsetVertical.length; j++) {
			for (int i = 0; i < offsetHorizontal.length; i++) {
				canvas.drawBitmap(monkey,
						(int) (offsetHorizontal[i] + (Math.random() - 0.5) * shakeOffset),
						(int) (offsetVertical[j]+offsetTop + Math.random()),
						paint_line);
			}
		}
		offsetTop += 5;
		if (offsetTop>heightPixels){
			offsetTop=0;
		}
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
