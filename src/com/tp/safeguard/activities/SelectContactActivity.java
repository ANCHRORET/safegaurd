package com.tp.safeguard.activities;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tp.safeguard.R;
import com.tp.safeguard.bean.ContactBean;
import com.tp.safeguard.utils.ContactUtils;

public class SelectContactActivity extends Activity {

	protected static final int RESULTCODE = 1001;
	private ListView lv_bean;
	private List<ContactBean> list;
	private ProgressBar mpb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);

		// 获得布局里的View组件
		initView();
		// 初始化得到的组件
		initData();
		// 处理view对应的事件
		initEvent();
	}

	private void initEvent() {
		// 为listView设置时间监听器
		lv_bean.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Toast.makeText(SelectContactActivity.this, "click",
						Toast.LENGTH_LONG).show();
				// 点击listView某一项后的操作
				Intent intent = new Intent();
				intent.putExtra("ContactNum", list.get(position).num);

				setResult(RESULTCODE, intent);
				finish();
			}
		});
	}

	private void initView() {
		lv_bean = (ListView) findViewById(R.id.contacts_lv_bean);
		mpb = (ProgressBar) findViewById(R.id.contacts_pb);
	}

	private void initData() {

		mpb.setVisibility(View.VISIBLE);
		new Thread() {
			@Override
			public void run() {
				// 开始获取数据
				list = ContactUtils.queryAll(SelectContactActivity.this);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mpb.setVisibility(View.GONE);// 这个为什么不能在子线程里操作
						lv_bean.setAdapter(new MyAdapter());
					}
				});
			}
		}.start();
		// 获取数据
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (list != null) {
				return list.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (list != null) {
				return list.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 设置item的view

			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(SelectContactActivity.this,
						R.layout.lv_item_contact, null);

				holder = new ViewHolder();
				holder.iv_icon = (ImageView) convertView
						.findViewById(R.id.lv_iv_icon);
				holder.tv_name = (TextView) convertView
						.findViewById(R.id.lv_iv_name);
				holder.tv_num = (TextView) convertView
						.findViewById(R.id.lv_iv_num);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			ContactBean bean = list.get(position);
			bean.icon = ContactUtils.getContactPhoto(
					SelectContactActivity.this, bean.contactID);
			if (bean.icon == null) {
				holder.iv_icon.setImageResource(R.drawable.ic_contact);
			} else {
				holder.iv_icon.setImageBitmap(bean.icon);
			}
			holder.tv_name.setText(bean.name);
			holder.tv_num.setText(bean.num);
			return convertView;
		}
	}

	private class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_num;
	}

}
