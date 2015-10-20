package com.tp.safeguard.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tp.safeguard.R;
import com.tp.safeguard.bean.NumAddressStyleBean;
import com.tp.safeguard.services.CallSmsSafeService;
import com.tp.safeguard.services.DisplayNumAddress;
import com.tp.safeguard.utils.Constants;
import com.tp.safeguard.utils.PreferenceUtils;
import com.tp.safeguard.utils.ServiceUtils;
import com.tp.safeguard.view.NumAddressStyleDialog;
import com.tp.safeguard.view.SettingItemView;

public class SettingActivity extends Activity implements OnClickListener {

	private SettingItemView mAutoUpdate, mAutoRefuse, mDisplayAddress,
			mDisplayStyle;
	private List<NumAddressStyleBean> mDatas;
	private final int[] iconIDs = new int[] {
			R.drawable.as_shape_color_translucent,
			R.drawable.as_shape_color_orange, R.drawable.as_shape_color_blue,
			R.drawable.as_shape_color_gray };
	private final String[] descs = { "半透明", "活力橙", "卫士蓝", "金属灰" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		// 初始化所用的view
		initView();
		// 初始化数据
		initData();
		// 为view设置事件
		initEvent();

	}

	@Override
	protected void onStart() {
		super.onStart();
		// Class.forName("com.tp.safeguard.serivce.CallSmsSafeService")
		// 为什么上面的不行 TODO:
		mAutoRefuse.setToggleOpened(ServiceUtils.isServiceRunning(this,
				CallSmsSafeService.class));

		// 显示归属地的服务是否运行了
		mDisplayAddress.setToggleOpened(ServiceUtils.isServiceRunning(this,
				DisplayNumAddress.class));
	}

	private void initView() {
		mAutoUpdate = (SettingItemView) findViewById(R.id.set_autoUpdate);

		mAutoRefuse = (SettingItemView) findViewById(R.id.set_autoRefuse);

		mDisplayAddress = (SettingItemView) findViewById(R.id.set_displayAddress);

		mDisplayStyle = (SettingItemView) findViewById(R.id.set_displayStyle);

	}

	private void initData() {
		mAutoUpdate.setToggleOpened(PreferenceUtils.getBoolean(this,
				Constants.AUTOUPDATE, true));

		mDatas = new ArrayList<NumAddressStyleBean>();
		for (int i = 0; i < iconIDs.length; i++) {
			NumAddressStyleBean bean = new NumAddressStyleBean();
			bean.iconID = iconIDs[i];
			bean.desc = descs[i];
			mDatas.add(bean);
		}
	}

	private void initEvent() {
		mAutoUpdate.setOnClickListener(this);
		mAutoRefuse.setOnClickListener(this);
		mDisplayAddress.setOnClickListener(this);
		mDisplayStyle.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		SettingItemView siv = (SettingItemView) v;
		switch (v.getId()) {
		case R.id.set_autoUpdate:
			PreferenceUtils.putBoolean(this, Constants.AUTOUPDATE,
					!siv.isToggleOpened());
			siv.toggle();
			break;
		case R.id.set_autoRefuse:
			clickCallSmsSafe();
			break;
		case R.id.set_displayAddress:
			clickDisplayAddress();
			break;
		case R.id.set_displayStyle:
			clickNumAddressStyle();
		default:
			break;
		}

	}

	// 归属地显示的样式
	private void clickNumAddressStyle() {
		final NumAddressStyleDialog dialog = new NumAddressStyleDialog(this);
		final NumAddressListAdaper adaper = new NumAddressListAdaper();
		dialog.setAdapter(adaper);
		dialog.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// PreferenceUtils.getInt(SettingActivity.this,
				// Constants.SETTING_ADDRESSSTYLE, -1);
				// view.findViewById(R.id.as_iv_selected).setVisibility(
				// View.INVISIBLE);//TODO 怎么将之前的设为不可见,能不能全部设为不可见
				PreferenceUtils.putInt(SettingActivity.this,
						Constants.SETTING_ADDRESSSTYLE, position);
				// 哈哈,可以的
				adaper.notifyDataSetChanged();
				view.findViewById(R.id.as_iv_selected).setVisibility(
						View.VISIBLE);
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	// 点击了,骚扰拦截
	private void clickCallSmsSafe() {
		boolean isRunning = ServiceUtils.isServiceRunning(this,
				CallSmsSafeService.class);
		Intent intent = new Intent(this, CallSmsSafeService.class);
		if (isRunning) {
			stopService(intent);
			mAutoRefuse.setToggleOpened(false);
		} else {
			startService(intent);
			mAutoRefuse.setToggleOpened(true);
		}
	}

	// 点击了,显示归属地
	private void clickDisplayAddress() {
		boolean isRunning = ServiceUtils.isServiceRunning(this,
				DisplayNumAddress.class);
		Intent intent = new Intent(this, DisplayNumAddress.class);
		if (isRunning) {
			stopService(intent);
			mDisplayAddress.setToggleOpened(false);
		} else {
			startService(intent);
			mDisplayAddress.setToggleOpened(true);
		}
	}

	private class NumAddressListAdaper extends BaseAdapter {

		@Override
		public int getCount() {
			if (mDatas != null) {
				return mDatas.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (mDatas != null) {
				return mDatas.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(SettingActivity.this,
						R.layout.lv_item_addressstyle, null);
				holder = new ViewHolder();
				convertView.setTag(holder);
				holder.ivColor = (ImageView) convertView
						.findViewById(R.id.as_iv_color);
				holder.tvDesc = (TextView) convertView
						.findViewById(R.id.as_tv_desc);
				holder.ivSelected = (ImageView) convertView
						.findViewById(R.id.as_iv_selected);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			NumAddressStyleBean bean = mDatas.get(position);
			holder.ivColor.setImageResource(bean.iconID);
			holder.tvDesc.setText(bean.desc);
			int localPosition = PreferenceUtils.getInt(SettingActivity.this,
					Constants.SETTING_ADDRESSSTYLE, -1);
			if (localPosition == -1 && position == 0) {
				holder.ivSelected.setVisibility(View.VISIBLE);
			} else {
				holder.ivSelected
						.setVisibility(position == localPosition ? View.VISIBLE
								: View.INVISIBLE);
			}

			return convertView;
		}
	}

	private class ViewHolder {
		ImageView ivColor;
		TextView tvDesc;
		ImageView ivSelected;
	}
}
