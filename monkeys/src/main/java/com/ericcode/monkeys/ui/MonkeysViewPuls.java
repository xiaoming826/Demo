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
	int MAX_SPEED = 30;
	private MyHandler myHandler;

	/**
	 * 构造器
	 *
	 *
	 */
	public MonkeysViewPuls(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		loadMonkeImage();
		myHandler = new MyHandler();
		DisplayMetrics dm = new DisplayMetrics();
		((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
		setView(dm.heightPixels, dm.widthPixels);
		addRandomSnow();
	}

	public MonkeysViewPuls(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * 加载图片到内存中
	 *
	 */
	public void loadMonkeImage() {
		Resources r = this.getContext().getResources();
		bitmap_monkey = ((BitmapDrawable) r.getDrawable(R.drawable.monkey))
				.getBitmap();
		bitmap_monkey = zoomImage(bitmap_monkey,0.5f);
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
	 *
	 */
	public void setView(int height, int width) {
		view_height = height;
		view_width = width;

	}

	/**
	 * 随机的生成猴子的位置
	 *
	 */
	public void addRandomSnow() {
		for(int i =0; i< MAX_MONKEY_COUNT;i++){
//			monkeys[i] = new Monkey(random.nextInt(view_width), 0,random.nextInt(MAX_SPEED));
			monkeys[i] = new Monkey(random.nextInt(view_width), getRandomInt(-view_height,0),getRandomInt(MAX_SPEED-20,MAX_SPEED));
			Log.i(TAG, "onDraw: monkeys: "+i+monkeys[i]);
		}
	}

	private int getRandomInt(int min,int max) {
		if (min>max){
			throw new RuntimeException("min > max");
		}
		return (int)(min+Math.random()*(max-min));
	}


	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (int i = 0; i < MAX_MONKEY_COUNT; i += 1) {
			//  下落的速度
			monkeys[i].coordinate.y += monkeys[i].speed ;
			// 飘动的效果

//			// 随机产生一个数字，让图片有水平移动的效果
			int tmp = MAX_SPEED/2 - random.nextInt(MAX_SPEED);
//			//为了动画的自然性，如果水平的速度大于图片的下落速度，那么水平的速度我们取下落的速度。
			monkeys[i].coordinate.x += monkeys[i].speed < tmp ? monkeys[i].speed : tmp;
			canvas.drawBitmap(bitmap_monkey, monkeys[i].coordinate.x,//((float) monkeys[i].coordinate.x)
					((float) monkeys[i].coordinate.y) , mPaint);
			if (monkeys[i].coordinate.x >= (view_width+bitmap_monkey.getWidth()) || monkeys[i].coordinate.y >= (view_height+bitmap_monkey.getHeight())) {
//				Log.i(TAG, "onDraw: monkeys: "+i+monkeys[i]);
				monkeys[i].coordinate.y = -bitmap_monkey.getHeight();
				monkeys[i].coordinate.x = random.nextInt(view_width);
			}
		}
		Message message = myHandler.obtainMessage();
		myHandler.sendMessageDelayed(message,100);
	}
	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			invalidate();
		}
	}


	class Monkey {
		Coordinate coordinate;
		int speed;

		public Monkey(int x, int y, int speed){
			coordinate = new Coordinate(x, y);
			this.speed = speed;
			if(this.speed == 0) {
				this.speed =1;
			}
		}

		@Override
		public String toString() {
			return "Monkey{" +
					"coordinate=" + coordinate +
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
			return "Coordinate: [" + x + "," + y + "]";
		}
	}
}
