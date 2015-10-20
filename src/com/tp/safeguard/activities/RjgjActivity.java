package com.tp.safeguard.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tp.safeguard.R;
import com.tp.safeguard.bean.AppInfoBean;
import com.tp.safeguard.business.AppInfoProvider;
import com.tp.safeguard.view.ProgressStateView;

public class RjgjActivity extends Activity {

	public static final String TAG = "RjgjActivity";
	private ProgressStateView mPsvRaw;
	private ProgressStateView mPsvSd;
	private ListView mLvAppInfo;
	private List<AppInfoBean> mDatas;
	private List<AppInfoBean> mUserDatas;
	private List<AppInfoBean> mSystemDatas;
	private TextView mTvTitle;
	private boolean isUserDesc = true;
	private AppInfoAdapter mAdapter;
	private LinearLayout mLProgressLoading;
	private UnInstalledReceiver mReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rjgj);

		initView();
		initData();
		initEvent();
		mReceiver = new UnInstalledReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		registerReceiver(mReceiver, filter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	private void initView() {
		mPsvRaw = (ProgressStateView) findViewById(R.id.rjgj_psv_raw);
		mPsvSd = (ProgressStateView) findViewById(R.id.rjgj_psv_sd);
		mLvAppInfo = (ListView) findViewById(R.id.rjgj_lv_appInfo);
		mTvTitle = (TextView) findViewById(R.id.rjgj_tv_title);
		mLProgressLoading = (LinearLayout) findViewById(R.id.srlj_ll_load);
	}

	private void initData() {

		// 查询手机内存的使用程度
		File dataDir = Environment.getDataDirectory();
		long totalRaw = dataDir.getTotalSpace();
		long freeRaw = dataDir.getFreeSpace();
		long usedRaw = totalRaw - freeRaw;
		int dataProgress = (int) ((usedRaw) * 100f / totalRaw);
		mPsvRaw.setProgress(dataProgress);

		mPsvRaw.setTitle("内存:");
		mPsvRaw.setLeft(Formatter.formatFileSize(this, usedRaw) + "已用");
		mPsvRaw.setRight(Formatter.formatFileSize(this, freeRaw) + "可用");

		// 查询sd卡的使用程度
		File sdDir = Environment.getExternalStorageDirectory();
		long totalSd = sdDir.getTotalSpace();
		long freeSd = sdDir.getFreeSpace();
		long usedSd = totalSd - freeSd;
		int sdProgress = (int) ((usedSd) * 100f / totalSd);
		mPsvSd.setProgress(sdProgress);

		mPsvSd.setTitle("sd卡:");
		mPsvSd.setLeft(Formatter.formatFileSize(this, usedSd) + "已用");
		mPsvSd.setRight(Formatter.formatFileSize(this, freeSd) + "可用");

		// 查询手机软件的信息放入listView
		// 初始化mDatas
		mLProgressLoading.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {

			@Override
			public void run() {
				mSystemDatas = new ArrayList<AppInfoBean>();
				mUserDatas = new ArrayList<AppInfoBean>();
				mDatas = AppInfoProvider.getAllInstalled(RjgjActivity.this);
				for (AppInfoBean bean : mDatas) {
					if (bean.isSystemApp) {
						mSystemDatas.add(bean);
					} else {
						mUserDatas.add(bean);
					}
				}
				mDatas.clear();
				mDatas.addAll(mUserDatas);
				mDatas.addAll(mSystemDatas);

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mLProgressLoading.setVisibility(View.GONE);
						mAdapter = new AppInfoAdapter();
						mLvAppInfo.setAdapter(mAdapter);
						mTvTitle.setText("用户程序:(" + mUserDatas.size() + ")个");
					}
				});
			}
		}).start();

	}

	private void initEvent() {

		mLvAppInfo.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				AppInfoBean bean = (AppInfoBean) mAdapter.getItem(position);
				if (bean == null) {
					return;
				}
				// 显示一个PopupWindow
				showPopupWindow(view, bean);
			}
		});
		mLvAppInfo.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// 因为页面开始的时候,滚动事件就会发生一次
				if (mDatas == null) {
					return;
				}
				// 有firstVisibleItem,在这里写滑动事件
				if (firstVisibleItem > mUserDatas.size() && isUserDesc) {
					mTvTitle.setText("系统程序(" + mSystemDatas.size() + ")个");
					isUserDesc = false;
				} else if (firstVisibleItem <= mUserDatas.size() && !isUserDesc) {

					mTvTitle.setText("用户程序(" + mUserDatas.size() + ")个");
					isUserDesc = true;
				}
			}
		});
	}

	private void showPopupWindow(View view, final AppInfoBean bean) {
		View contentView = View.inflate(this, R.layout.popupwindow_appchoice,
				null);
		int width = LayoutParams.WRAP_CONTENT;
		int height = LayoutParams.WRAP_CONTENT;
		final PopupWindow popupWindow = new PopupWindow(contentView, width,
				height);
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		int yoff = -view.getHeight();
		int xoff = 80;

		popupWindow.setAnimationStyle(R.style.PopupAnimationStyle);

		popupWindow.showAsDropDown(view, xoff, yoff);
		contentView.findViewById(R.id.pop_tv_unInstall).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 卸载
						// <intent-filter>
						// <action android:name="android.intent.action.VIEW" />
						// <action android:name="android.intent.action.DELETE"
						// />
						// <category
						// android:name="android.intent.category.DEFAULT" />
						// <data android:scheme="package" />
						// </intent-filter>

						Intent intent = new Intent("android.intent.action.VIEW");
						intent.setData(Uri.parse("package:" + bean.packageName));
						startActivity(intent);
						popupWindow.dismiss();
					}
				});
		contentView.findViewById(R.id.pop_tv_open).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 打开
						PackageManager pm = getPackageManager();
						Intent intent = pm
								.getLaunchIntentForPackage(bean.packageName);
						startActivity(intent);
						popupWindow.dismiss();
					}
				});
		contentView.findViewById(R.id.pop_tv_share).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 分享
						// <action android:name="android.intent.action.VIEW" />
						// <action android:name="android.intent.action.SENDTO"
						// />
						// <category
						// android:name="android.intent.category.DEFAULT" />
						// <category
						// android:name="android.intent.category.BROWSABLE" />
						// <data android:scheme="sms" />
						// <data android:scheme="smsto" />

						Intent intent = new Intent(
								"android.intent.action.SENDTO");
						intent.setData(Uri.parse("smsto:"));
						intent.putExtra("sms_body", bean.appName + "非常好用");

						startActivity(intent);
						popupWindow.dismiss();
					}
				});
		contentView.findViewById(R.id.pop_tv_info).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 信息
						// <action
						// android:name="android.settings.APPLICATION_DETAILS_SETTINGS"
						// />
						// <category
						// android:name="android.intent.category.DEFAULT" />
						// <data android:scheme="package" />

						Intent intent = new Intent(
								"android.settings.APPLICATION_DETAILS_SETTINGS");
						intent.setData(Uri.parse("package:" + bean.packageName));
						startActivity(intent);
						popupWindow.dismiss();
					}
				});

	}

	private class AppInfoAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			int count = 0;
			if (mDatas != null) {
				if (mUserDatas != null) {
					count++;
				}
				if (mSystemDatas != null) {
					count++;
				}
				return mDatas.size() + count;
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (position > 0 && position < mUserDatas.size() + 1) {
				return mUserDatas.get(position - 1);
			}
			if (position > mUserDatas.size() + 1) {
				return mSystemDatas.get(position - mUserDatas.size() - 2);
			}

			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position == 0) {
				TextView view = new TextView(RjgjActivity.this);
				view.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
				view.setText("用户程序:(" + mUserDatas.size() + ")个");
				view.setPadding(4, 4, 4, 4);
				return view;
			}
			if (position == mUserDatas.size() + 1) {
				TextView view = new TextView(RjgjActivity.this);
				view.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
				view.setText("系统程序:(" + mSystemDatas.size() + ")个");
				view.setPadding(4, 4, 4, 4);
				return view;
			}

			ViewHolder holder = null;
			if (convertView == null || convertView instanceof TextView) {
				convertView = View.inflate(RjgjActivity.this,
						R.layout.lv_item_appinfo, null);
				holder = new ViewHolder();
				convertView.setTag(holder);
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.ai_iv_icon);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.ai_tv_appName);
				holder.tvLocation = (TextView) convertView
						.findViewById(R.id.ai_tv_location);
				holder.tvSize = (TextView) convertView
						.findViewById(R.id.ai_tv_size);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			AppInfoBean bean = (AppInfoBean) getItem(position);
			holder.ivIcon.setImageDrawable(bean.icon);
			holder.tvLocation.setText(bean.location);
			holder.tvName.setText(bean.appName);
			holder.tvSize.setText(Formatter.formatFileSize(RjgjActivity.this,
					bean.size));

			return convertView;
		}
	}

	private class ViewHolder {
		public ImageView ivIcon;
		public TextView tvName;
		public TextView tvLocation;
		public TextView tvSize;

	}

	private class UnInstalledReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String str = intent.getDataString();
			String packageName = str.replace("package:", "").trim();
			ListIterator<AppInfoBean> iterator = mDatas.listIterator();
			while (iterator.hasNext()) {
				AppInfoBean bean = (AppInfoBean) iterator.next();
				if (packageName.equals(bean.packageName)) {
					iterator.remove();
					break;
				}
			}
			ListIterator<AppInfoBean> userIterator = mUserDatas.listIterator();
			while (userIterator.hasNext()) {
				AppInfoBean bean = (AppInfoBean) userIterator.next();
				if (packageName.equals(bean.packageName)) {
					userIterator.remove();
					break;
				}
			}
			ListIterator<AppInfoBean> systemIterator = mSystemDatas
					.listIterator();
			while (systemIterator.hasNext()) {
				AppInfoBean bean = (AppInfoBean) systemIterator.next();
				if (packageName.equals(bean.packageName)) {
					systemIterator.remove();
					break;
				}
			}
			mAdapter.notifyDataSetChanged();

		}
	}
}
