package com.tp.safeguard.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;

import com.tp.safeguard.R;

public abstract class BaseSetupActivity extends Activity {

	private GestureDetector mDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDetector = new GestureDetector(this, new SimpleOnGestureListener() {

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				// e1,开始点
				// e2,结束点
				float startX = e1.getX();
				float endX = e2.getX();

				float startY = e1.getY();
				float endY = e2.getY();

				float moveX = endX - startX;
				float moveY = endY - startY;
				if (Math.abs(moveY) > Math.abs(moveX)) {
					return false;
				}

				if (Math.abs(velocityX) < 80) {
					return false;
				}
				
				if (moveX < 0) {
					doNext();
				}else {
					doPre();
				}

				return super.onFling(e1, e2, velocityX, velocityY);
			}

		});

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	public void clickNext(View v) {
		doNext();
	}

	private void doNext() {
		if (performNext()) {
			return;
		}

		// 这个方法的位置也有要求
		overridePendingTransition(R.anim.next_enter_anim, R.anim.next_exit_anim);
		finish();
	}

	public void clickPre(View v) {
		doPre();
	}

	private void doPre() {
		if (performPre()) {
			return;
		}
		overridePendingTransition(R.anim.pre_enter_anim, R.anim.pre_exit_anim);
		finish();
	}

	/**
	 * 点击下一步时,要实现关于本页面的功能
	 * 
	 * @return 返回true代表点击下一步要做的处理在本方法里已经处理完了
	 */
	public abstract boolean performNext();

	/**
	 * 点击上一步时,要实现关于本页面的功能
	 * 
	 * @return 返回true代表点击上一步要做的处理在本方法里已经处理完了
	 */
	public abstract boolean performPre();

}
