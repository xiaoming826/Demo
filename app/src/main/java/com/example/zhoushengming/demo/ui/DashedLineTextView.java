package com.example.zhoushengming.demo.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zhoushengming on 15/12/17.
 */
public class DashedLineTextView extends View {
	public DashedLineTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public DashedLineTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DashedLineTextView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		Paint paint = new Paint();
//		paint.setStyle(Paint.Style.STROKE);
//		paint.setColor(Color.BLACK);
//		paint.setStrokeWidth(1);
//		PathEffect effects = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
//		paint.setAntiAlias(true);
//		paint.setPathEffect(effects);
//		int y = canvas.getHeight() - 2 > 0 ? canvas.getHeight() - 2 : 0;
//		canvas.drawLine(0, y, canvas.getWidth(), y, paint);
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.DKGRAY);
		Path path = new Path();
		path.moveTo(0, 10);
		path.lineTo(480,10);
		PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);
		paint.setPathEffect(effects);
		canvas.drawPath(path, paint);
	}
}
