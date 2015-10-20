package com.tp.safeguard.view;

import com.tp.safeguard.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AlternativeButton extends LinearLayout {

	private boolean isLeftSelected = true;
	private TextView tvLeft;
	private TextView tvRight;
	private OnChangeStateListener mListener;

	public interface OnChangeStateListener {
		public abstract void OnChangeState(boolean isLeftSelected);
	}

	public AlternativeButton(Context context) {
		this(context, null);
	}

	public AlternativeButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		View.inflate(context, R.layout.view_btn_alternative, this);

		initView();
		initData();
		initEvent();

	}

	private void initView() {
		tvLeft = (TextView) findViewById(R.id.alter_tv_left_unlock);
		tvRight = (TextView) findViewById(R.id.alter_tv_right_lock);
	}

	private void initData() {
		tvLeft.setSelected(isLeftSelected);
		tvRight.setSelected(!isLeftSelected);
	}

	private void initEvent() {
		tvLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isLeftSelected) {
					changeState();
				}
			}
		});

		tvRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isLeftSelected) {
					changeState();
				}
			}
		});
	}

	public void setOnChangeStateListener(OnChangeStateListener listener) {
		this.mListener = listener;
	}

	private void changeState() {
		isLeftSelected = !isLeftSelected;
		tvLeft.setSelected(isLeftSelected);
		tvRight.setSelected(!isLeftSelected);
		if (mListener != null) {
			mListener.OnChangeState(isLeftSelected);
		}
	}

	public boolean isLeftSelected() {
		return isLeftSelected;
	}
}
