package com.ericcode.monkeys.ui;

import android.animation.Animator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.ericcode.monkeys.R;

import java.util.Random;

/**
 * Created by zhoushengming on 16/1/19.
 */
public class MonkeysViewByValueAnimator extends View {
	private static final String TAG = MonkeysViewByValueAnimator.class.getSimpleName();
	int MAX_MONKEY_COUNT = 60;
	// 图片
	Bitmap bitmap_monkey = null;
	// 画笔
	private final Paint mPaint = new Paint();
	// 随即生成器
	private static final Random random = new Random();
	// 位置
	private Monkey[] monkeys = new Monkey[MAX_MONKEY_COUNT];
	// 屏幕的高度和宽度
	int view_height = 0;
	int view_width = 0;
	float MAX_SPEED = 30f;
	private MyHandler myHandler;

	/**
	 * 构造器
	 */
	public MonkeysViewByValueAnimator(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		loadMonkeImage();
		myHandler = new MyHandler();
		DisplayMetrics dm = new DisplayMetrics();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
		setView(dm.heightPixels, dm.widthPixels);
		addRandomMonkey();
	}

	public MonkeysViewByValueAnimator(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * 加载图片到内存中
	 */
	public void loadMonkeImage() {
		Resources r = this.getContext().getResources();
		bitmap_monkey = ((BitmapDrawable) r.getDrawable(R.drawable.monkey))
				.getBitmap();
		bitmap_monkey = zoomImage(bitmap_monkey, 0.20f);
	}

	/**
	 * 缩放图片
	 *
	 * @param bitmap
	 * @param zoom
	 * @return
	 */
	private Bitmap zoomImage(Bitmap bitmap, float zoom) {
		Matrix matrix = new Matrix();
		matrix.postScale(zoom, zoom); //长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizeBmp;

	}

	/**
	 * 设置当前窗体的实际高度和宽度
	 */
	public void setView(int height, int width) {
		view_height = height;
		view_width = width;

	}

	/**
	 * 随机的生成猴子的位置
	 */
	public void addRandomMonkey() {
		for (int i = 0; i < MAX_MONKEY_COUNT; i++) {
			monkeys[i] = new Monkey(random.nextInt(view_width), getRandomInt(-view_height, 0),
					getRandomDouble(MAX_SPEED/1.4f, MAX_SPEED));
			Log.i(TAG, "addRandomMonkey: monkeys: " + i + monkeys[i]);
		}
	}

	private int getRandomInt(int min, int max) {
		if (min > max) {
			throw new RuntimeException("min > max");
		}
		return (int) (min + Math.random() * (max - min));
	}


	private float getRandomDouble(float min, float max) {
		if (min > max) {
			throw new RuntimeException("min > max");
		}
		float v = (float) (Math.random() * (max - min));
		float v1 = min + v;
		return (v1);
//		return (int)(min+Math.random()*(max-min));
	}

	@Override
	public void onDraw(Canvas canvas) {
		for (int i = 0; i < MAX_MONKEY_COUNT; i += 1) {
			if (monkeys[i].isMove==false) {
				monkeys[i].isMove=true;
				startAnimation(i);
				drawMonkey(canvas, i);
			} else {
				drawMonkey(canvas, i);
			}
			if (monkeys[i].point.x >= (view_width ) || monkeys[i].point.y >=
					(view_height + bitmap_monkey.getHeight())) {
				monkeys[i].point.y = -bitmap_monkey.getHeight();
				monkeys[i].point.x = random.nextInt(view_width);
				monkeys[i].isMove=false;
			}
		}

	}

	private void drawMonkey(Canvas canvas, int i) {
		canvas.drawBitmap(bitmap_monkey, monkeys[i].point.x,//((float) monkeys[i].point.x)
				monkeys[i].point.y, mPaint);
	}

	private void startAnimation(final int i) {
		Point startPoint = new Point(monkeys[i].point.x, monkeys[i].point.y);
		//monkeys[i].point.y
		Point endPoint = new Point(monkeys[i].point.x, getHeight() + bitmap_monkey
				.getHeight());
		ValueAnimator anim = ValueAnimator.ofObject(new PointEvaluator(), startPoint, endPoint);
		anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				monkeys[i].point = (Point) animation.getAnimatedValue();
				invalidate();
			}
		});
		anim.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				invalidate();
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}
		});
		anim.setDuration((long) (100 * monkeys[i].speed));
		anim.start();
	}

	class PointEvaluator implements TypeEvaluator {

		@Override
		public Object evaluate(float fraction, Object startValue, Object endValue) {
			Point startPoint = (Point) startValue;
			Point endPoint = (Point) endValue;
			int x = (int) (startPoint.getX() + fraction * (endPoint.getX() - startPoint.getX()));
			int y = (int) (startPoint.getY() + fraction * (endPoint.getY() - startPoint.getY()));
			Point point = new Point(x, y);
			return point;
		}

	}


	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			invalidate();
		}
	}


	class Monkey {
		Point point;
		float speed;
		boolean isMove=false;

		public Monkey(int x, int y, float speed) {
			point = new Point(x, y);
			this.speed = speed;
			if (this.speed == 0) {
				this.speed = 1;
			}
		}

		@Override
		public String toString() {
			return "Monkey{" +
					"point=" + point +
					", speed=" + speed +
					'}';
		}
	}

	class Point {
		public int x;
		public int y;

		public Point(int newX, int newY) {
			x = newX;
			y = newY;
		}

		public float getX() {
			return x;
		}

		public float getY() {
			return y;
		}


		@Override
		public String toString() {
			return "Point: [" + x + "," + y + "]";
		}
	}
}
