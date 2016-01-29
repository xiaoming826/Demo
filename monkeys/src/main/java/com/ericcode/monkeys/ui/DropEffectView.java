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
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;

import com.ericcode.monkeys.Logger;

import java.util.Random;

/**
 * Created by zhoushengming on 16/1/19.
 */
public class DropEffectView extends View {

	// 持续 8s
	// 移动方向 各个方向
	// 匀速
	// 直线
	// 大小 随机
	private static final String TAG = DropEffectView.class.getSimpleName();
	private int MAX_MONKEY_COUNT = 40;
	// 图片
	private Bitmap bitmap_monkey = null;
	// 画笔
	private final Paint mPaint = new Paint();
	// 随即生成器
	private static final Random random = new Random();

	private Monkey[] monkeys;
	private Animator[] animators = new Animator[MAX_MONKEY_COUNT];
	// 屏幕的高度和宽度
	private int view_height;
	private int view_width;
	private float MAX_SPEED = 8f;

	private int MONKEY_MIN_WIDTH;
	private int MONKEY_MAX_WIDTH;
	private Callback callback;


	/**
	 * 构造器
	 */
	public DropEffectView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		DisplayMetrics dm = new DisplayMetrics();
		((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
		setView(dm.heightPixels, dm.widthPixels);
	}

	public DropEffectView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public Bitmap getMonkeyImage(float width, int imageId) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(getResources(), imageId, options);
		int imageWidth = options.outWidth;

		options.inSampleSize = (int) (imageWidth / width);
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeResource(getResources(), imageId, options);
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
		monkeys = new Monkey[MAX_MONKEY_COUNT];
		for (int i = 0; i < MAX_MONKEY_COUNT; i++) {
			int startx = (width / (column - 1)) * (i % column);
			int starty = (height / (row - 1)) * (i % row);    //(random.nextInt(row)+1);
			int endx = (width / (column - 1)) * (random.nextInt(column));
			int endy = view_height;
			float speed = getRandomDouble(MAX_SPEED / 1.2f, MAX_SPEED);
			float monkeyWidth = getRandomDouble(MONKEY_MIN_WIDTH, MONKEY_MAX_WIDTH);

			// 随机偏移量
			startx += getRandomInt(-view_width / column / 2, view_width / column / 2);
			starty += getRandomInt((height / (row - 1)), 0);
			starty -= MONKEY_MAX_WIDTH;
			endx += getRandomInt(-view_width / column / 2, view_width / column / 2);

			if (startx < 0) {
//				Logger.i(TAG, "addRandomMonkey: startx< 0");
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
//						Logger.i(TAG, String.format("addRandomMonkey:one: %d, two: %d; compare:%d", i, j, compare));
//						Logger.i(TAG, i + "  " + monkeys[i].toString());
//						Logger.i(TAG, i + "  " + monkeys[j].toString());
						if (monkey1.endPoint.x > view_width / 2) {
							monkey1.endPoint.x -= view_width / 2;
						} else {
							monkey1.endPoint.x += view_width / 2;
						}
//						Logger.i(TAG, i + " new " + monkeys[i].toString());
					}
				}
			}
		}
	}


	@Override
	public void onDraw(Canvas canvas) {
		if (monkeys == null) {
			Logger.w(TAG, "onDraw: monkeys is null");
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
		}

	}

	private long maxDuration = 0;
	private int slowMonkey = 0;

	private void startAnimation(final int i) {
		int startX = monkeys[i].startPoint.x;
		int startY = monkeys[i].startPoint.y;
		int endX = monkeys[i].endPoint.x;
		int endY = monkeys[i].endPoint.y;
		float speed = monkeys[i].speed;

		Point startPoint = new Point(startX, startY);
		Point endPoint = new Point(endX, endY);
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
		anim.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if (slowMonkey == i) {
					callback.onEnd();
				}
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}
		});
		anim.setInterpolator(new LinearInterpolator());
		long duration = (long) (speed *
				(Math.abs(startY) + endY));
		anim.setDuration(duration);
		if (maxDuration < duration) {
			maxDuration = duration;
			slowMonkey = i;
		}
		anim.start();
		animators[i] = anim;
//		Logger.i(TAG, i + "  Duration:" + anim.getDuration() + "   " + monkeys[i].toString());
	}

	public interface Callback {
		public void onEnd();
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
					", width=" + width +
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


	public void start(int imageId, Callback callback) {
		clearAnimation();
		MONKEY_MIN_WIDTH = 50 * view_width / 240;
		MONKEY_MAX_WIDTH = 80 * view_width / 240;
		bitmap_monkey = getMonkeyImage(MONKEY_MAX_WIDTH, imageId);
		addRandomMonkey();
		invalidate();
		this.callback = callback;
	}

	public void clearAnimation() {
		super.clearAnimation();
		if (animators == null) {
			return;
		}
		for (Animator animator : animators) {
			if (animator != null && animator.isRunning()) {
				animator.end();
			}
		}
		monkeys = null;
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
		int left = (int) (monkey.startPoint.x - monkey.width / 2);
		int top = monkey.startPoint.y;
		int right = left + (int) monkey.width;
		int bottom = (int) (top + (float) bitmap_monkey.getHeight() / (float) bitmap_monkey.getWidth() * monkey.width);
//		canvas.drawBitmap(bitmap_monkey, left, top, mPaint);
		Rect dstRect = new Rect(left, top, right, bottom);
		canvas.drawBitmap(bitmap_monkey, null, dstRect, mPaint);
//		Paint paint = new Paint();
//		paint.setColor(Color.BLACK);
//		canvas.drawText(i + "", left, top, paint);
	}

}