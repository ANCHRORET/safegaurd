package com.tp.safeguard.view;

import com.tp.safeguard.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressStateView extends LinearLayout {

	private TextView mTvTitle;
	private TextView mTvLeft;
	private TextView mTvRight;
	private ProgressBar mPbProgress;

	public ProgressStateView(Context context) {
		this(context, null);
	}

	public ProgressStateView(Context context, AttributeSet attrs) {
		super(context, attrs);

		View.inflate(context, R.layout.view_progress_state, this);
		mTvTitle = (TextView) findViewById(R.id.view_ps_tv_title);
		mTvLeft = (TextView) findViewById(R.id.view_ps_tv_left);
		mTvRight = (TextView) findViewById(R.id.view_ps_tv_right);
		mPbProgress = (ProgressBar) findViewById(R.id.view_ps_pb_progress);
	}

	/**
	 * 标明是哪一类内存使用的比例
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		mTvTitle.setText(title);
	}

	/**
	 * 使用比例条的左边文字
	 * 
	 * @param left
	 */
	public void setLeft(String left) {
		mTvLeft.setText(left);
	}

	/**
	 * 使用比例条的右边文字
	 * 
	 * @param left
	 */
	public void setRight(String right) {
		mTvRight.setText(right);
	}

	/**
	 * 使用的百分比,取值为0-100
	 * 
	 * @param progress
	 */
	public void setProgress(int progress) {
		mPbProgress.setProgress(progress);
	}
}
