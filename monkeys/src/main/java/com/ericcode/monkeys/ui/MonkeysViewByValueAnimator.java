package com.ericcode.monkeys.ui;

import android.animation.Animator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.ericcode.monkeys.R;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhoushengming on 16/1/19.
 */
public class MonkeysViewByValueAnimator extends View {

	// 持续 8s
	// 移动方向 各个方向
	// 匀速
	// 直线
	// 大小 随机
	private static final String TAG = MonkeysViewByValueAnimator.class.getSimpleName();
	int MAX_MONKEY_COUNT = 50;
	// 图片
	Bitmap bitmap_monkey = null;
	// 画笔
	private final Paint mPaint = new Paint();
	// 随即生成器
	private static final Random random = new Random();

	private Monkey[] monkeys = new Monkey[MAX_MONKEY_COUNT];
	private Animator[] animators = new Animator[MAX_MONKEY_COUNT];
	// 屏幕的高度和宽度
	int view_height = 0;
	int view_width = 0;
	float MAX_SPEED = 10f;

	private final int MONKEY_MIN_WIDTH = 50;
	private final int MONKEY_MAX_WIDTH = 80;


	/**
	 * 构造器
	 */
	public MonkeysViewByValueAnimator(Context context, AttributeSet attrs, int defStyle) {
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
	}

	public MonkeysViewByValueAnimator(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

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
		final int height = -3 * view_height;
		final int width = view_width;
		for (int i = 0; i < MAX_MONKEY_COUNT; i++) {
			int startx = (width / (column - 1)) * (i % column);
			int starty = (height / (row - 1)) * (i % row);    //(random.nextInt(row)+1);
			int endx = (width / (column - 1)) * (random.nextInt(column));
			int endy = view_height + MONKEY_MAX_WIDTH;
			float speed = getRandomDouble(MAX_SPEED / 1.2f, MAX_SPEED);
			float monkeyWidth = getRandomDouble(MONKEY_MIN_WIDTH, MONKEY_MAX_WIDTH);

			// 随机偏移量
			startx += getRandomInt(-MONKEY_MAX_WIDTH / 2, 0);
			starty += getRandomInt((height / (row - 1)), 0);

			if (startx < 0) {
//				Log.i(TAG, "addRandomMonkey: startx< 0");
				if (Math.random() > 0.5) {
					startx = 0;
					endx = view_width;
				} else {
					startx = view_width;
					endx = 0;
				}
			}

			monkeys[i] = new Monkey(startx, starty, endx, endy, speed, monkeyWidth);

		}

		for (int i = 0; i < monkeys.length; i++) {
			for (int j = i + 1; j < monkeys.length; j++) {
				if (i != j) {
					Monkey monkey1 = monkeys[i];
					Monkey monkey2 = monkeys[j];
					int compare = monkey1.compareTo(monkey2);
					if (compare < MONKEY_MIN_WIDTH) {
//						Log.i(TAG, String.format("addRandomMonkey:one: %d, two: %d; compare:%d", i, j, compare));
//						Log.i(TAG, i + "  " + monkeys[i].toString());
//						Log.i(TAG, i + "  " + monkeys[j].toString());
						if (monkey1.endPoint.x > view_width / 2) {
							monkey1.endPoint.x -= view_width / 2;
						} else {
							monkey1.endPoint.x += view_width / 2;
						}
//						Log.i(TAG, i + " new " + monkeys[i].toString());
					}
				}
			}

		}
	}

	AtomicBoolean isShow = new AtomicBoolean(false);

	@Override
	public void onDraw(Canvas canvas) {
		if (!isShow.get()) {
			return;
		}
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

	long maxDuration = 0;
	int slowMonkey = 0;

	private void startAnimation(final int i) {
		int startX = monkeys[i].startPoint.x;
		int startY = monkeys[i].startPoint.y;
		int endX = monkeys[i].endPoint.x;
		int endY = monkeys[i].endPoint.y;
		float speed = monkeys[i].speed;

		Point startPoint = new Point(startX, startY);
		Point endPoint = new Point(endX, view_height + endY);
		final ValueAnimator anim = ValueAnimator.ofObject(new PointEvaluator(), startPoint, endPoint);
		anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			                       @Override
			                       public void onAnimationUpdate(ValueAnimator animation) {
				                       monkeys[i].startPoint = (Point) animation.getAnimatedValue();
				                       if (slowMonkey == i) {
					                       invalidate();
				                       }
			                       }
		                       }
		);
		anim.setInterpolator(new AccelerateDecelerateInterpolator());
		long duration = (long) (speed *
				(Math.abs(startY) + endY));
		anim.setDuration(duration);
		if (maxDuration < duration) {
			maxDuration = duration;
			slowMonkey = i;
		}
		anim.start();
		animators[i] = anim;
//		Log.i(TAG, i + "  Duration:" + anim.getDuration() + "   " + monkeys[i].toString());
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


	class Monkey implements Comparable<Monkey> {
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

		@Override
		public int compareTo(Monkey another) {
			int startPointCompare = this.startPoint.compareTo(another.startPoint);
			int endPointCompare = this.endPoint.compareTo(another.endPoint);
			float speedCompare = Math.abs(this.speed - another.speed);

			return startPointCompare > endPointCompare ? startPointCompare : endPointCompare;
		}
	}

	class Point implements Comparable<Point> {
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

		@Override
		public int compareTo(Point another) {
			int xDiffer = (int) Math.abs(this.getX() - another.getX());
			int yDiffer = (int) Math.abs(this.getY() - another.getY());
			return xDiffer > yDiffer ? xDiffer : yDiffer;
		}
	}


	public void startAnimation() {
		cleanAnimation();
		isShow.set(true);
		init();
		invalidate();
	}

	public void cleanAnimation() {
		for (Animator animator : animators) {
			if (animator != null) {
				animator.end();
			}
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
	}

	private void drawMonkey(Canvas canvas, int i) {
		Monkey monkey = monkeys[i];
		int left = monkey.startPoint.x - bitmap_monkey.getWidth() / 2;
		int top = monkey.startPoint.y - bitmap_monkey.getHeight() / 2;
//		canvas.drawBitmap(bitmap_monkey, left, top, mPaint);
		Rect srcRect = new Rect();
		Rect dstRect = new Rect(left, top, left + (int) monkey.width, top + (int) monkey.width);
		canvas.drawBitmap(bitmap_monkey, null, dstRect, mPaint);
//		Paint paint = new Paint();
//		paint.setColor(Color.BLACK);
//		canvas.drawText(i + "", left, top, paint);
	}

}
