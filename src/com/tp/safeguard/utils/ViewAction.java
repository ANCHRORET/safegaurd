package com.tp.safeguard.utils;

import android.animation.ObjectAnimator;
import android.view.View;

public class ViewAction {

	/**
	 * 将view抖动一下下
	 * @param view
	 */
	public static void shakeView(View view) {
		ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX",
				new float[] { 0f, 3f, 0f, -3f });
		animator.setRepeatCount(2);
		animator.setRepeatMode(ObjectAnimator.RESTART);
		animator.setDuration(100);
		animator.start();
	}

}
