package com.tp.safeguard.view;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeView extends TextView {

	public MarqueeView(Context context) {
		this(context, null);
	}

	public MarqueeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//设置单行显示
		setSingleLine();

		//设置显示为走马灯效果
		setEllipsize(TruncateAt.MARQUEE);
		
		//设置可以获得焦点
		setFocusable(true);
		
		//设置获得焦点
		setFocusableInTouchMode(true);
		
		//设置走马灯运行的次数
		setMarqueeRepeatLimit(-1);
	}
	
	
	@Override
	public boolean isFocused() {
		//欺骗调用者,此view是获得了焦点的,如果有多个此view的实例,都可以表现为获得焦点
		return true;
	}
	
	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
		//欺骗调用者,此view的焦点在本页面一直没有被抢走,就算焦点被EditView等控件抢走了
		super.onFocusChanged(true, direction, previouslyFocusedRect);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		//欺骗调用者,即使窗口改变了,焦点依然还在
		super.onWindowFocusChanged(true);
	}
	
}
