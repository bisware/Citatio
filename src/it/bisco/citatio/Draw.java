package it.bisco.citatio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class Draw extends View {
	private int x = 1;
	private int y = 1;
	private int r = 1;

	public Draw(Context context) {
		super(context);
	}

	public void setCords(int mx, int my, int mr) {
		x = mx;
		y = my;
		r = mr;
	}

	@SuppressLint("DrawAllocation")
	protected void onDraw(Canvas canvas) {

		System.out.println("Draw :)");

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(2);
		paint.setColor(Color.RED);

		canvas.drawCircle(x, y, r, paint);
		super.onDraw(canvas);

	}

	protected void DrawCircle(Canvas canvas, int x, int y, int r) {
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		paint.setColor(Color.GREEN);
		canvas.drawCircle(x, y, r, paint);
		super.onDraw(canvas);
	}
}
