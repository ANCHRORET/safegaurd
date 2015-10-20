package com.tp.safeguard.activities;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tp.safeguard.R;
import com.tp.safeguard.bean.BlackBean;
import com.tp.safeguard.db.BlackDao;

public class SrljActivity extends Activity {

	protected static final int REQUESTCODE_UPDATE = 101;
	protected static final int REQUESTCODE_ADD = 102;
	private static final String TAG = "SrljActivity";
	private static final int SIZE = 20;// 一次查询的条数
	private ListView mLvBlack;
	private List<BlackBean> mDatas;
	private BlackDao mBlackDao;
	private BlackAdapter mBlackAdapter;
	private LinearLayout mLlLoading;
	private ImageView mIvEmpty;
	protected boolean isLoadMore = false;
	protected boolean isLoadFinish = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_srlj);

		initView();
		initData();
		initEvent();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUESTCODE_UPDATE) {
			Log.d(TAG, "更新了...");
			switch (resultCode) {
			case Activity.RESULT_OK:
				Log.d(TAG, "更新ok了...");
				int position = data.getIntExtra("position", -1);
				int type = data.getIntExtra("type", -1);
				BlackBean bean = mDatas.get(position);
				bean.type = type;

				mBlackAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		} else if (requestCode == REQUESTCODE_ADD) {
			Log.d(TAG, "添加了...");
			switch (resultCode) {
			case Activity.RESULT_OK:
				Log.d(TAG, "添加ok了...");
				String number = data.getStringExtra("number");
				int type = data.getIntExtra("type", -1);
				BlackBean bean = new BlackBean();
				bean.number = number;
				bean.type = type;
				mDatas.add(bean);
				mBlackAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
	}

	private void initView() {
		mLvBlack = (ListView) findViewById(R.id.srlj_lv_black);
		mLlLoading = (LinearLayout) findViewById(R.id.srlj_ll_load);
		mIvEmpty = (ImageView) findViewById(R.id.srlj_iv_empty);
	}

	private void initData() {
		mLlLoading.setVisibility(View.VISIBLE);
		mBlackDao = new BlackDao(this);
		new Thread() {
			@Override
			public void run() {
				mDatas = mBlackDao.queryPart(SIZE, 0);
				SystemClock.sleep(1500);

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mBlackAdapter = new BlackAdapter();
						mLvBlack.setAdapter(mBlackAdapter);
						mLlLoading.setVisibility(View.GONE);
						mLvBlack.setEmptyView(mIvEmpty);
					}
				});
			}
		}.start();
	}

	// 跳到添加黑名单页面
	public void addBlack(View v) {
		Intent intent = new Intent(this, BlackActivity.class);
		intent.setAction(BlackActivity.BLACK_ADD);
		startActivityForResult(intent, REQUESTCODE_ADD);
	}

	// 跳到更改黑名单页面
	private void initEvent() {

		// 点击监听器
		mLvBlack.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 跳到更改黑名单页面
				Intent intent = new Intent(SrljActivity.this,
						BlackActivity.class);
				BlackBean bean = mDatas.get(position);
				intent.setAction(BlackActivity.BLACK_UPDATE);
				intent.putExtra("number", bean.number);
				intent.putExtra("type", bean.type);
				intent.putExtra("position", position);
				startActivityForResult(intent, REQUESTCODE_UPDATE);
			}
		});

		// 滚动监听器
		mLvBlack.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 去获取更多的数据
				 selectMoreData();
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
//				selectMoreData();
			}
		});
	}

	private void selectMoreData() {
		if (mDatas != null) {
			int lastVisiblePosition = mLvBlack.getLastVisiblePosition();
			if (lastVisiblePosition == mDatas.size() - 1) {

				if (isLoadMore) {
					return;
				}

				if (isLoadFinish) {
					return;
				}
				mLlLoading.setVisibility(View.VISIBLE);
				isLoadMore = true;
				new Thread() {
					@Override
					public void run() {
						SystemClock.sleep(1500);
						final List<BlackBean> list = mBlackDao.queryPart(SIZE,
								mDatas.size());
						runOnUiThread(new Runnable() {
							@Override
							public void run() {

								if (list.size() < SIZE) {
									isLoadFinish = true;
								} else {
									isLoadFinish = false;
								}
								mDatas.addAll(list);
								mBlackAdapter.notifyDataSetChanged();
								mLlLoading.setVisibility(View.GONE);
								isLoadMore = false;
							}
						});
					}
				}.start();
			}
		}
	}

	class BlackAdapter extends BaseAdapter {

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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(SrljActivity.this,
						R.layout.lv_item_black, null);
				holder = new ViewHolder();
				convertView.setTag(holder);
				holder.mIvDelete = (ImageView) convertView
						.findViewById(R.id.sjfd_iv_delete);
				holder.mTvNumber = (TextView) convertView
						.findViewById(R.id.sjfd_tv_number);
				holder.mTvType = (TextView) convertView
						.findViewById(R.id.sjfd_tv_type);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final BlackBean bean = mDatas.get(position);
			holder.mIvDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 点击删除
					mBlackDao.delete(bean.number);
					mDatas.remove(position);
					// 实现删一个加一个,有可能数据多余20条,所以没必要加那么多,当前当删的能见到最后一个的时候就会加载20个
					// List<BlackBean> list = mBlackDao.queryPart(1,
					// mDatas.size());
					// mDatas.addAll(list);
					notifyDataSetChanged();
				}
			});

			switch (bean.type) {
			case BlackBean.BLACK_TYPE_CALL:
				holder.mTvType.setText("电话拦截");
				break;
			case BlackBean.BLACK_TYPE_SMS:
				holder.mTvType.setText("短信拦截");
				break;
			case BlackBean.BLACK_TYPE_ALL:
				holder.mTvType.setText("电话+短信拦截");
				break;
			default:
				holder.mTvType.setText("没有拦截");
				break;
			}

			holder.mTvNumber.setText(bean.number);
			return convertView;
		}
	}

	class ViewHolder {
		TextView mTvNumber;
		TextView mTvType;
		ImageView mIvDelete;
	}
}
