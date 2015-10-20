package com.tp.safeguard.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tp.safeguard.R;

/**
 * 这是一个组合起来的相对布局管理器
 * 
 * @author Zhanglei
 * 
 */
public class SettingItemView extends RelativeLayout {

	private static final int BACKGROUND_START = 0;
	private static final int BACKGROUND_MIDDLE = 1;
	private static final int BACKGROUND_END = 2;
	public TextView mTVTitle;
	public ImageView mIVToggle;
	private boolean isOpened = true;

	public boolean isToggleOpened() {
		return isOpened;
	}

	public void toggle() {
		// 如果开关是打开的，就关闭，如果是关闭的就打开
		isOpened = !isOpened;
		mIVToggle.setImageResource(isOpened ? R.drawable.on : R.drawable.off);
	};

	public void setToggleOpened(boolean isOpened) {
		this.isOpened = isOpened;
//		Log.i("SettingActivity", isOpened+"siv");
		mIVToggle.setImageResource(isOpened ? R.drawable.on : R.drawable.off);
	}

	public SettingItemView(Context context) {
		this(context, null);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);

		View.inflate(context, R.layout.view_setting_item, this);

		// 初始化组件
		mTVTitle = (TextView) findViewById(R.id.view_tv_title);
		mIVToggle = (ImageView) findViewById(R.id.view_iv_toggle);

		// 读取属性值
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.SettingItemView);

		String title = ta.getString(R.styleable.SettingItemView_sivTitle);
		int background = ta.getInt(R.styleable.SettingItemView_sivBackground,
				BACKGROUND_START);
		boolean toggle = ta.getBoolean(R.styleable.SettingItemView_sivToggle,
				true);
		ta.recycle();

		mTVTitle.setText(title);
		mIVToggle.setVisibility(toggle ? View.VISIBLE : View.GONE);
		switch (background) {
		case BACKGROUND_START:
			setBackgroundResource(R.drawable.bg_start_selector);
			break;
		case BACKGROUND_MIDDLE:
			setBackgroundResource(R.drawable.bg_middle_selector);
			break;
		case BACKGROUND_END:
			setBackgroundResource(R.drawable.bg_end_selector);
			break;
		default:
			setBackgroundResource(R.drawable.bg_start_selector);
			break;
		}
	}
}
