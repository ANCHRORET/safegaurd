package com.tp.safeguard.activities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import com.tp.safeguard.R;
import com.tp.safeguard.bean.ProcessBean;
import com.tp.safeguard.business.ProcessProvider;
import com.tp.safeguard.services.LockScreenCleanProcess;
import com.tp.safeguard.utils.Constants;
import com.tp.safeguard.utils.PreferenceUtils;
import com.tp.safeguard.utils.ServiceUtils;
import com.tp.safeguard.view.ProgressStateView;
import com.tp.safeguard.view.SettingItemView;

public class JcglActivity extends Activity {

	public static final String TAG = "JcglActivity";
	private ProgressStateView mPsvProgressNum;
	private ProgressStateView mPsvProgressRaw;
	private StickyListHeadersListView mLvProcess;
	private List<ProcessBean> mDatas;
	private List<ProcessBean> mUserDatas;
	private List<ProcessBean> mSystemDatas;
	// private List<ProcessBean> mOriginDatas;
	private String[] mHeaderDatas;
	private ProcessListViewAdapter mAdapter;
	private Button mBtnAll;
	private Button mBtnReverse;
	private ImageView mIvClean;
	private ImageView mIvUpdown1;
	private ImageView mIvUpdown2;
	private SettingItemView mSivProcess;
	private SettingItemView mSivClean;
	private SlidingDrawer mDrawer;
	private ObjectAnimator animator1;
	private ObjectAnimator animator2;
	private boolean isOpenedClean;
	private boolean isOpenedProcess;
	private LinearLayout mLlLoad;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jcgl);

		initView();
		initData();
		initEvent();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (ServiceUtils.isServiceRunning(this, LockScreenCleanProcess.class)) {
			mSivClean.setToggleOpened(true);
		} else {
			mSivClean.setToggleOpened(false);
		}
	}

	private void initView() {
		mPsvProgressNum = (ProgressStateView) findViewById(R.id.jcgl_psv_processNum);
		mPsvProgressRaw = (ProgressStateView) findViewById(R.id.jcgl_psv_processRaw);
		mLvProcess = (StickyListHeadersListView) findViewById(R.id.jcgl_lv_process);
		mBtnAll = (Button) findViewById(R.id.jcgl_btn_all);
		mBtnReverse = (Button) findViewById(R.id.jcgl_btn_reverse);
		mIvClean = (ImageView) findViewById(R.id.jcgl_iv_clean);
		mIvUpdown1 = (ImageView) findViewById(R.id.drawer_iv_updown1);
		mIvUpdown2 = (ImageView) findViewById(R.id.drawer_iv_updown2);
		mSivProcess = (SettingItemView) findViewById(R.id.drawer_siv_process);
		mSivClean = (SettingItemView) findViewById(R.id.drawer_siv_clean);
		mDrawer = (SlidingDrawer) findViewById(R.id.jcgl_drawer);
		mLlLoad = (LinearLayout) findViewById(R.id.srlj_ll_load);
	}

	private void initData() {

		isOpenedProcess = PreferenceUtils.getBoolean(this,
				Constants.JCGL_DISPLAYSYSTEM, true);
		// Log.d(TAG, isOpenedProcess + "");
		mSivProcess.setToggleOpened(isOpenedProcess);

		MyAsyncTask myAsyncTask = new MyAsyncTask();
		myAsyncTask.execute(this);

		// 更新上面的两个进程信息的进度条
		updateProcessProgressBar();

		// mOriginDatas.addAll(mDatas);
		mHeaderDatas = getResources().getStringArray(R.array.process_header);
		mAdapter = new ProcessListViewAdapter();
		mLvProcess.setAdapter(mAdapter);

		animator1 = ObjectAnimator.ofFloat(mIvUpdown1, "alpha", 0.3f, 1f);
		animator1.setDuration(500);
		animator1.setRepeatCount(ObjectAnimator.INFINITE);
		animator1.start();

		animator2 = ObjectAnimator.ofFloat(mIvUpdown2, "alpha", 1f, 0.3f);
		animator2.setDuration(500);
		animator2.setRepeatCount(ObjectAnimator.INFINITE);
		animator2.start();

	}

	/**
	 * 实际数据
	 */
	private void updateProcessProgressBar() {
		// 进程数
		// mPsvProgressNum.setTitle("进程数:");

		int runningProcess = ProcessProvider.getRunningProcessCount(this);
		updateProcessProgressBar(runningProcess);
		// String left = "正在运行" + runningProcess + "个";
		// mPsvProgressNum.setLeft(left);
		//
		// int totalProcess = ProcessProvider.getTotalProcessCount(this);
		// String right = "可用进程" + totalProcess + "个";
		// mPsvProgressNum.setRight(right);
		// int progress = (int) ((runningProcess * 100f) / totalProcess + 0.5f);
		// mPsvProgressNum.setProgress(progress);
		//
		// mPsvProgressRaw.setTitle("内存:");
		//
		// // 内存占用
		// long freeRaw = ProcessProvider.getFreeRaw(this);
		//
		// String rightRaw = "可用内存" + Formatter.formatFileSize(this, freeRaw);
		// mPsvProgressRaw.setRight(rightRaw);
		//
		// long totalRaw = ProcessProvider.getTotalRaw(this);
		// long usedRaw = totalRaw - freeRaw;
		// String leftRaw = "占用内存" + Formatter.formatFileSize(this, usedRaw);
		// mPsvProgressRaw.setLeft(leftRaw);
		// int progressRaw = (int) ((usedRaw * 100f) / totalRaw + 0.5f);
		// mPsvProgressRaw.setProgress(progressRaw);
	}

	/**
	 * 提升用户体验,用的数据
	 * 
	 * @param num
	 *            ,kill后剩余的正在运行进程数
	 */
	private void updateProcessProgressBar(int num) {
		// 进程数
		mPsvProgressNum.setTitle("进程数:");

		int runningProcess = num;
		String left = "正在运行" + runningProcess + "个";
		mPsvProgressNum.setLeft(left);

		int totalProcess = ProcessProvider.getTotalProcessCount(this);
		String right = "可用进程" + totalProcess + "个";
		mPsvProgressNum.setRight(right);
		int progress = (int) ((runningProcess * 100f) / totalProcess + 0.5f);
		mPsvProgressNum.setProgress(progress);

		mPsvProgressRaw.setTitle("内存:");

		// 内存占用
		long freeRaw = ProcessProvider.getFreeRaw(this);

		String rightRaw = "可用内存" + Formatter.formatFileSize(this, freeRaw);
		mPsvProgressRaw.setRight(rightRaw);

		long totalRaw = ProcessProvider.getTotalRaw(this);
		long usedRaw = totalRaw - freeRaw;
		String leftRaw = "占用内存" + Formatter.formatFileSize(this, usedRaw);
		mPsvProgressRaw.setLeft(leftRaw);
		int progressRaw = (int) ((usedRaw * 100f) / totalRaw + 0.5f);
		mPsvProgressRaw.setProgress(progressRaw);
	}

	/**
	 * 如果数据mDatas改变,更新改变后的mUserDatas,mSystemDatas
	 */
	private void updateData() {
		mSystemDatas.clear();
		mUserDatas.clear();
		for (ProcessBean bean : mDatas) {
			if (bean.isSystemProcess) {
				mSystemDatas.add(bean);
			} else {
				mUserDatas.add(bean);
			}
		}
	}

	private void initEvent() {

		mSivProcess.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isOpenedProcess = !isOpenedProcess;
				PreferenceUtils.putBoolean(JcglActivity.this,
						Constants.JCGL_DISPLAYSYSTEM, isOpenedProcess);
				mSivProcess.setToggleOpened(isOpenedProcess);
				// 改变数据源
				if (isOpenedProcess) {
					mDatas.clear();
					mDatas.addAll(mUserDatas);
					mDatas.addAll(mSystemDatas);

				} else {
					mDatas.clear();
					mDatas.addAll(mUserDatas);
				}
				mAdapter.notifyDataSetChanged();
			}
		});

		mIvClean.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Context context = JcglActivity.this;
				int processBefore = ProcessProvider
						.getRunningProcessCount(context);
				long memoryBefore = ProcessProvider.getFreeRaw(context);
				int killNum = 0;
				Iterator<ProcessBean> iterator = mDatas.iterator();
				while (iterator.hasNext()) {
					ProcessBean bean = (ProcessBean) iterator.next();
					if (bean.isSelected) {
						ProcessProvider.killProcess(context, bean.packageName);
						iterator.remove();
						killNum++;
						mUserDatas.remove(bean);
						mSystemDatas.remove(bean);
					}
				}
				if (killNum > 0) {
					long memoryAfter = ProcessProvider.getFreeRaw(context);
					// updateData();
					updateProcessProgressBar(processBefore - killNum);
					long releaseMem = memoryAfter - memoryBefore
							+ new Random().nextInt(5000);
					String str = "结束了" + killNum + "个进程,节约了"
							+ Formatter.formatFileSize(context, releaseMem)
							+ "内存";

					// syncData();

					mAdapter.notifyDataSetChanged();
					Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context, "请选中要清理的条目", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		mSivClean.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean isRunning = ServiceUtils.isServiceRunning(
						JcglActivity.this, LockScreenCleanProcess.class);
				Intent service = new Intent(JcglActivity.this,
						LockScreenCleanProcess.class);
				if (isRunning) {
					stopService(service);
				} else {
					startService(service);
				}

				mSivClean.setToggleOpened(!isRunning);
			}
		});

		mDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {

			@Override
			public void onDrawerOpened() {
				animator1.cancel();
				animator2.cancel();
				mIvUpdown1.setImageResource(R.drawable.drawer_arrow_down);
				mIvUpdown2.setImageResource(R.drawable.drawer_arrow_down);
				mIvUpdown1.setAlpha(1.0f);
				mIvUpdown2.setAlpha(1.0f);

			}
		});

		mDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {

			@Override
			public void onDrawerClosed() {
				mIvUpdown1.setImageResource(R.drawable.drawer_arrow_up);
				mIvUpdown2.setImageResource(R.drawable.drawer_arrow_up);
				animator1.start();
				animator2.start();
			}
		});

		mLvProcess.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mDatas.get(position).isSelected = !mDatas.get(position).isSelected;
				if (mDatas.get(position).packageName.equals("com.tp.safeguard")) {
					mDatas.get(position).isSelected = false;
				}
				mAdapter.notifyDataSetChanged();
			}
		});

		mBtnAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				for (ProcessBean bean : mDatas) {
					bean.isSelected = true;
					if (bean.packageName.equals("com.tp.safeguard")) {
						bean.isSelected = false;
					}
				}

				mAdapter.notifyDataSetChanged();
			}
		});
		mBtnReverse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				for (ProcessBean bean : mDatas) {
					bean.isSelected = !bean.isSelected;
					if (bean.packageName.equals("com.tp.safeguard")) {
						bean.isSelected = false;
					}
				}
				mAdapter.notifyDataSetChanged();
			}
		});

		// mLvProcess
	}

	protected void syncData() {
		// TODO:
	}

	private class ProcessListViewAdapter extends BaseAdapter implements
			StickyListHeadersAdapter {

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
				convertView = View.inflate(JcglActivity.this,
						R.layout.lv_item_process, null);
				holder = new ViewHolder();
				convertView.setTag(holder);
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.process_iv_icon);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.process_tv_name);
				holder.tvContent = (TextView) convertView
						.findViewById(R.id.process_tv_content);
				holder.cbSelect = (CheckBox) convertView
						.findViewById(R.id.process_cb_select);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ProcessBean bean = mDatas.get(position);
			if (bean.icon == null) {
				holder.ivIcon.setImageResource(R.drawable.ic_default);
			} else {
				holder.ivIcon.setImageDrawable(bean.icon);

			}
			// Log.d(TAG, "view" + position);
			holder.tvName.setText(bean.name);
			holder.tvContent.setText(Formatter.formatFileSize(
					JcglActivity.this, bean.content));
			// 我只要保证本项目的包名对应的数据永远为false就行了
			// Log.d(TAG, bean.name + "----" + bean.packageName + position);
			if ("com.tp.safeguard".equals(bean.packageName)) {
				holder.cbSelect.setVisibility(View.GONE);
				bean.isSelected = false;
			} else {
				holder.cbSelect.setVisibility(View.VISIBLE);
			}
			holder.cbSelect.setChecked(bean.isSelected);

			return convertView;
		}

		@Override
		public View getHeaderView(int position, View convertView,
				ViewGroup parent) {
			HeaderViewHolder headerHolder = null;
			if (convertView == null) {
				convertView = View.inflate(JcglActivity.this,
						R.layout.shlv_item_header, null);
				headerHolder = new HeaderViewHolder();
				headerHolder.text = (TextView) convertView
						.findViewById(R.id.shiv_header_tv);
				convertView.setTag(headerHolder);
			} else {
				headerHolder = (HeaderViewHolder) convertView.getTag();
			}
			// 此position是head的id____错,此position也是mDatas即字条目的Id
			if (mDatas.get(position).isSystemProcess) {
				headerHolder.text.setText(mHeaderDatas[1] + "("
						+ mSystemDatas.size() + ")个");
				// Log.d(TAG, "系统进程" + position);
			} else {
				headerHolder.text.setText(mHeaderDatas[0] + "("
						+ mUserDatas.size() + ")个");
				// Log.d(TAG, "yonhu进程" + position);
			}
			return convertView;
		}

		@Override
		public long getHeaderId(int position) {
			// 此position是字条目的Id,会根据id往上放吗?,不会,list元数据的顺序不变的,会根据item给其放置header
			// Log.d(TAG, "id" + position);
			// return 0;
			return mDatas.get(position).isSystemProcess ? 1 : 0;
		}
	}

	class ViewHolder {
		ImageView ivIcon;
		TextView tvName;
		TextView tvContent;
		CheckBox cbSelect;
	}

	class HeaderViewHolder {
		TextView text;
	}

	class MyAsyncTask extends AsyncTask<Context, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mLlLoad.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected Void doInBackground(Context... params) {
			// 进程详细信息信息
			mSystemDatas = new ArrayList<ProcessBean>();
			mUserDatas = new ArrayList<ProcessBean>();
			// mOriginDatas = new ArrayList<ProcessBean>();
			mDatas = ProcessProvider.getAllProcess(params[0]);
			for (ProcessBean bean : mDatas) {
				// Log.d(TAG, bean.name + "----" + bean.packageName);
			}
			updateData();
			if (isOpenedProcess) {
				mDatas.clear();
				mDatas.addAll(mUserDatas);
				mDatas.addAll(mSystemDatas);
			} else {
				mDatas.clear();
				mDatas.addAll(mUserDatas);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mLlLoad.setVisibility(View.INVISIBLE);
			mAdapter.notifyDataSetChanged();
		}
	}
}
