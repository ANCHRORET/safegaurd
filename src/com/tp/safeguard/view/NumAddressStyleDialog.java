package com.tp.safeguard.view;

import com.tp.safeguard.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class NumAddressStyleDialog extends Dialog {

	private ListView mLvAddressStyle;
	private ListAdapter mAdapter;
	private OnItemClickListener mOnItemClickListener;

	public NumAddressStyleDialog(Context context) {
		super(context, R.style.NADialogStyle);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_numaddress_style);

		Window window = getWindow();
		LayoutParams layoutParams = window.getAttributes();
		layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		window.setAttributes(layoutParams);

		initView();

		initData();
	}

	private void initView() {
		mLvAddressStyle = (ListView) findViewById(R.id.dialog_lv_numAddressStyle);
	}

	private void initData() {
		// 使用者如果不初始化这两个参数,就是设置空的适配器和监听器
		mLvAddressStyle.setAdapter(mAdapter);
		mLvAddressStyle.setOnItemClickListener(mOnItemClickListener);

	}

	/**
	 * 为Dialog里的listView设置适配器
	 * 
	 * @param adapter
	 */
	public void setAdapter(ListAdapter adapter) {
		this.mAdapter = adapter;
		// 根据源码,show在onCreate前执行,所以且有判断条件的执行,有可能不执行,所以为保险,两边都设置
		if (mLvAddressStyle != null) {
			mLvAddressStyle.setAdapter(adapter);

		}
	}

	/**
	 * 为Dialog里的listView设置监听器
	 * 
	 * @param adapter
	 */
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
		// 根据源码,show在onCreate前执行,所以且有判断条件的执行,有可能不执行,所以为保险,两边都设置
		if (mLvAddressStyle != null) {
			mLvAddressStyle.setOnItemClickListener(onItemClickListener);
		}
	}
}
