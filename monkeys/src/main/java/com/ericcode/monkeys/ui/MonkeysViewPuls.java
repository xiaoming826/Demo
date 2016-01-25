package com.ericcode.monkeys.ui;

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
public class MonkeysViewPuls extends View {
	private static final String TAG = MonkeysViewPuls.class.getSimpleName();
	private int monkeysCountInScreen = 40;
	// 图片
	private Bitmap bitmap_monkey = null;
	// 画笔
	private final Paint mPaint = new Paint();
	// 随即生成器
	private static final Random random = new Random();
	// 位置
	private Monkey[] monkeys = new Monkey[monkeysCountInScreen];
	// 屏幕的高度和宽度
	private int view_height = 0;
	private int view_width = 0;
	private final float imgZoom = 0.10f;
	private float MAX_SPEED = 8f;
	private MyHandler myHandler;
	private int monkeysShowedCount;
	private int monkeysShowCount;
	private long stopTime;

	public MonkeysViewPuls(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		loadMonkeImage(R.drawable.monkey,imgZoom);
		myHandler = new MyHandler();
		DisplayMetrics dm = new DisplayMetrics();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
		setViewSize(dm.heightPixels, dm.widthPixels);
		addRandomMonkey();
	}

	public MonkeysViewPuls(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MonkeysViewPuls(Context context) {
		this(context, null, 0);
	}

	private void init(){
		setViewSize(view_height, view_width);
		monkeys = new Monkey[monkeysCountInScreen];
		addRandomMonkey();
	}

	public void stopAnimator() {
		myHandler.setIsStop(true);
	}

	public void startAnimator() {
		myHandler.setIsStop(false);
		myHandler.sendMessage(myHandler.obtainMessage());
	}

	public void startAnimatorAtTime(int millisecond) {
		myHandler.setIsStop(false);
		init();
		this.stopTime = System.currentTimeMillis()+millisecond;
		myHandler.sendMessage(myHandler.obtainMessage());
	}

	public void startAnimatorAtMonkeys(int monkeys) {
		myHandler.setIsStop(false);
		this.monkeysShowCount=monkeys;
		if (monkeysCountInScreen>monkeys) {
			setMonkeysCountInScreen(monkeys);
		}
		init();
		myHandler.sendMessage(myHandler.obtainMessage());
	}

	/**
	 * 加载图片到内存中
	 */
	public void loadMonkeImage(int rId,float zoom) {
		Resources r = this.getContext().getResources();
		bitmap_monkey = ((BitmapDrawable) r.getDrawable(rId))
				.getBitmap();
		bitmap_monkey = zoomImage(bitmap_monkey, zoom);
	}

	/**
	 * 缩放图片
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
	public void setViewSize(int height, int width) {
		view_height = height;
		view_width = width;
	}


	public void setMonkeysCountInScreen(int count) {
		this.monkeysCountInScreen =count;
		init();
	}

	/**
	 * 随机的生成猴子的位置
	 */
	public void addRandomMonkey() {
		for (int i = 0; i < monkeysCountInScreen; i++) {
//			monkeys[i] = new Monkey(random.nextInt(view_width), 0,random.nextInt(MAX_SPEED));
			monkeys[i] = new Monkey(random.nextInt(view_width), getRandomInt(-view_height,-bitmap_monkey.getHeight()),
					getRandomDouble(MAX_SPEED/1.4f, MAX_SPEED));
			Log.i(TAG, "onDraw: monkeys: " + i + monkeys[i]);
		}
		monkeysShowedCount = monkeysCountInScreen;
	}

	private int getRandomInt(int min, int max) {
		if (min > max) {
			throw new RuntimeException("min > max");
		}
		double v = Math.random() * (max - min);
		double v1 = min + v;
		return (int) (v1);
	}

	private double getRandomDouble(float min, float max) {
		if (min > max) {
			throw new RuntimeException("min > max");
		}
		double v = Math.random() * (max - min);
		double v1 = min + v;
		return (v1);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int minMonkeyY=view_height;
		for (Monkey monkey : monkeys) {
			if (monkey.coordinate.y<minMonkeyY) {
				minMonkeyY=monkey.coordinate.y;
			}
		}
		Log.i(TAG, "onDraw: minMonkeyY:"+minMonkeyY);
		if (minMonkeyY >= view_height) {
			myHandler.setIsStop(true);
		}
		for (int i = 0; i < monkeysCountInScreen; i += 1) {
			//  下落的速度
			monkeys[i].coordinate.y += monkeys[i].speed;
			// 飘动的效果
//			if (Math.random()<0.2) {
//	//			// 随机产生一个数字，让图片有水平移动的效果
//				int tmp = (int) (MAX_SPEED/2 - random.nextInt((int)MAX_SPEED)-0.5);
//	//			//为了动画的自然性，如果水平的速度大于图片的下落速度，那么水平的速度我们取下落的速度。
//				monkeys[i].point.x += monkeys[i].speed < tmp ? monkeys[i].speed : tmp;
//			}
			canvas.drawBitmap(bitmap_monkey, monkeys[i].coordinate.x,//((float) monkeys[i].point.x)
					((float) monkeys[i].coordinate.y), mPaint);
			if (monkeys[i].coordinate.x >= (view_width + bitmap_monkey.getWidth()) || monkeys[i].coordinate.y >=
					(view_height + bitmap_monkey.getHeight())) {
				if (monkeysShowCount!=0){
					if (monkeysShowedCount<monkeysShowCount) {
						monkeys[i].coordinate.y = -bitmap_monkey.getHeight();
						monkeys[i].coordinate.x = random.nextInt(view_width);
						monkeysShowedCount++;
					}
				}
				if (stopTime!=0) {
					if (System.currentTimeMillis()<stopTime) {
						monkeys[i].coordinate.y = -bitmap_monkey.getHeight();
						monkeys[i].coordinate.x = random.nextInt(view_width);
						monkeysShowedCount++;
					}
				}
			}
		}

	}

	class MyHandler extends Handler {
		private boolean isStop=false;
		@Override
		public void handleMessage(Message msg) {
			if (isStop) {
				Log.i(TAG, "handleMessage: stop");
				return;
			}
			invalidate();
			Message message = myHandler.obtainMessage();
			myHandler.sendMessageDelayed(message, 30);
		}

		public void setIsStop(boolean isStop) {
			this.isStop = isStop;
		}
	}

	class Monkey {
		Coordinate coordinate;
		double speed;

		public Monkey(int x, int y, double speed) {
			coordinate = new Coordinate(x, y);
			this.speed = speed;
			if (this.speed == 0) {
				this.speed = 1;
			}
		}

		@Override
		public String toString() {
			return "Monkey{" +
					"point=" + coordinate +
					", speed=" + speed +
					'}';
		}
	}

	class Coordinate {
		public int x;
		public int y;

		public Coordinate(int newX, int newY) {
			x = newX;
			y = newY;
		}

		@Override
		public String toString() {
			return "Point: [" + x + "," + y + "]";
		}
	}
}
