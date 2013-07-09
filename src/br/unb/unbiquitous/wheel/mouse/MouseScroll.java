package br.unb.unbiquitous.wheel.mouse;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import br.unb.unbiquitous.wheel.R;

public class MouseScroll extends View {

	public Drawable topArrowImage;
	public static Drawable scrollImage;
	public Drawable bottomArrowImage;
	private int x;
	private int y;
	private int scrollImageWidth;
	private int scrollImageHeight;
	private int middlePoint;


	public MouseScroll(Context context) {
		this(context, null);
	}

	public MouseScroll(Context context, AttributeSet attrs) {
		super(context, attrs);

		setBackgroundColor(Color.TRANSPARENT);

		scrollImage = context.getResources().getDrawable(
				R.drawable.circulo_vermelho);
		scrollImageWidth = scrollImage.getIntrinsicWidth();
		scrollImageHeight = scrollImage.getIntrinsicHeight();

		setFocusable(true);
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldw, int oldh) {
		super.onSizeChanged(width, height, oldw, oldh);

		x = width / 2 - (scrollImageWidth / 2);
		y = height / 2 - (scrollImageHeight / 2);

		middlePoint = y;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		scrollImage
				.setBounds(x, y, x + scrollImageWidth, y + scrollImageHeight);
		scrollImage.draw(canvas);

	}

	public boolean onTouchEvent(MotionEvent event) {
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			this.y = (int) y - (scrollImageHeight / 2);

			break;

		case MotionEvent.ACTION_MOVE:
			this.y = (int) y - (scrollImageHeight / 2);

			break;

		case MotionEvent.ACTION_UP:
			this.y = middlePoint;
			break;
		}

		invalidate();

		return true;
	}
}
