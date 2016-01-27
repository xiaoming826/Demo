package com.ericcode.monkeys.ui;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;

import com.ericcode.monkeys.R;

import java.util.Random;

/**
 * Created by zhoushengming on 16/1/19.
 */
public class MonkeysViewByValueAnimator2 extends View {

	// 持续 8s
	// 移动方向 各个方向
	// 匀速
	// 直线
	// 大小 随机
	private static final String TAG = MonkeysViewByValueAnimator2.class.getSimpleName();
	int MAX_MONKEY_COUNT = 40;
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
	float MAX_SPEED = 8f;

	private final int MONKEY_MIN_WIDTH = 40;
	private final int MONKEY_MAX_WIDTH = 60;

	/**
	 * 构造器
	 */
	public MonkeysViewByValueAnimator2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
//		loadMonkeImage();
		init();
	}

	private void init() {
		bitmap_monkey = getMonkeyImage(MONKEY_MAX_WIDTH);
		DisplayMetrics dm = new DisplayMetrics();
		((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
		setView(dm.heightPixels, dm.widthPixels);
		addRandomMonkey();
		for (int i = 0; i < monkeys.length; i++) {
			Log.i(TAG, i + "  " + monkeys[i].toString());
		}
	}

	public MonkeysViewByValueAnimator2(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

//	/**
//	 * 加载图片到内存中
//	 */
//	public void loadMonkeImage() {
//		Resources r = this.getContext().getResources();
//		bitmap_monkey = ((BitmapDrawable) r.getDrawable(R.drawable.monkey))
//				.getBitmap();
//		float zoom = bitmap_monkey_height / bitmap_monkey.getHeight();
//		bitmap_monkey = zoomImage(bitmap_monkey, zoom);
//	}


	public Bitmap getMonkeyImage(float width) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(getResources(), R.drawable.monkey, options);
		int imageWidth = options.outWidth;

		options.inSampleSize = (int) (imageWidth / width);
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeResource(getResources(), R.drawable.monkey, options);
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
		final int column = 3;
		final int row = 8;
		final int height = -4 * view_height;
		final int width = view_width;
		for (int i = 0; i < MAX_MONKEY_COUNT; i++) {
			int startx = (width / (column - 1)) * (i % column);
			int starty = (height / (row - 1)) * (i % row);    //(random.nextInt(row)+1);
			int endx = getRandomInt(-MONKEY_MAX_WIDTH, view_width);
			int endy = view_height + MONKEY_MAX_WIDTH;
			float speed = getRandomDouble(MAX_SPEED / 1.4f, MAX_SPEED);
			float monkeyWidth = getRandomDouble(MONKEY_MIN_WIDTH, MONKEY_MAX_WIDTH);

			// 随机偏移量
			startx += getRandomInt(-MONKEY_MAX_WIDTH / 2, MONKEY_MAX_WIDTH / 2);
			starty += getRandomInt(-MONKEY_MAX_WIDTH * 2, 0);


			monkeys[i] = new Monkey(startx, starty, endx, endy, speed, monkeyWidth);
		}
	}

	@Override
	public void onDraw(Canvas canvas) {
		for (int i = 0; i < MAX_MONKEY_COUNT; i += 1) {
			if (monkeys[i].isMove == false) {
				monkeys[i].isMove = true;
				startAnimation(i);
				drawMonkey(canvas, i);
			} else {
				drawMonkey(canvas, i);
			}
//			if (monkeys[i].startPoint.y >=
//					(view_height + bitmap_monkey.getHeight())) {
//				monkeys[i].startPoint.y = -bitmap_monkey.getHeight();
//				monkeys[i].startPoint.x = getRandomInt(-bitmap_monkey.getWidth() / 2, view_width - bitmap_monkey
// .getWidth()
//						/ 2);
//				monkeys[i].isMove = false;
//				invalidate();
//			}
		}

	}


	private void startAnimation(final int i) {
		int startX = monkeys[i].startPoint.x;
		int startY = monkeys[i].startPoint.y;
		int endX = monkeys[i].endPoint.x;
		int endY = monkeys[i].endPoint.y;

		Point startPoint = new Point(startX, startY);
		Point endPoint = new Point(endX, view_height + endY);
		ValueAnimator anim = ValueAnimator.ofObject(new PointEvaluator(), startPoint, endPoint);
		anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			                       @Override
			                       public void onAnimationUpdate(ValueAnimator animation) {
				                       monkeys[i].startPoint = (Point) animation.getAnimatedValue();
				                       invalidate();
			                       }
		                       }

		);
		anim.setInterpolator(new LinearInterpolator());
		anim.setDuration((long) (10 *
				(Math.abs(startY)+endY)));
//		anim.setDuration((long) (1000 * monkeys[i].speed));
		anim.start();
		Log.i(TAG, i + "  Duration:" + anim.getDuration() + "   " + monkeys[i].toString());
	}

	class PointEvaluator implements TypeEvaluator {

		@Override
		public Object evaluate(float fraction, Object startValue, Object endValue) {
			Point startPoint = (Point) startValue;
			Point endPoint = (Point) endValue;
			int x = (int) (startPoint.getX() + fraction * (endPoint.getX() - startPoint.getX()));
			int y = (int) (startPoint.getY() + fraction * (endPoint.getY() - startPoint.getY()));
			Point point = new Point(x, y);
//			Log.i(TAG, "evaluate: startPoint:" + startPoint);
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
		Point startPoint;
		Point endPoint;
		float speed;
		boolean isMove = false;
		float width;

		public Monkey(int startx, int starty, int endx, int endy, float speed, float width) {
			this.startPoint = new Point(startx, starty);
			this.endPoint = new Point(endx, endy);
			this.speed = speed;
			this.width = width;
		}

		@Override
		public String toString() {
			return "Monkey{" +
					"startPoint=" + startPoint +
					", endPoint=" + endPoint +
					", speed=" + speed +
					", isMove=" + isMove +
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


	public void startAnimation() {
		addRandomMonkey();
		invalidate();
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
	}

	private void drawMonkey(Canvas canvas, int i) {
		Monkey monkey = monkeys[i];
		int left = monkey.startPoint.x - bitmap_monkey.getWidth() / 2;
		int top = monkey.startPoint.y - bitmap_monkey.getHeight() / 2;
		if (top<0) {
			return;
		}
//		canvas.drawBitmap(bitmap_monkey, left, top, mPaint);
		Rect srcRect = new Rect();
		Rect dstRect = new Rect(left, top, left + (int) monkey.width, top + (int) monkey.width);
		canvas.drawBitmap(bitmap_monkey, null, dstRect, mPaint);
	}

}
