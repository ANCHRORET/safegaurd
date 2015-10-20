package com.tp.safeguard.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tp.safeguard.R;
import com.tp.safeguard.bean.AppLockBean;
import com.tp.safeguard.view.AlternativeButton;
import com.tp.safeguard.view.AlternativeButton.OnChangeStateListener;

public class AppLock extends Activity {

	private AlternativeButton mAbSelect;

	private List<AppLockBean> mDatas;
	private List<AppLockBean> mLockDatas;
	private List<AppLockBean> mUnLockDatas;

	private ListView mLvUnLock;
	private ListView mLvLock;

	private boolean isLeftSelected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_applock);

		initView();
		initData();
		initEvent();
	}

	private void initView() {
		mAbSelect = (AlternativeButton) findViewById(R.id.al_ab_select);
		mLvLock = (ListView) findViewById(R.id.al_lv_lock);
		mLvUnLock = (ListView) findViewById(R.id.al_lv_unlock);
	}

	private void initData() {
		mDatas = null;// TODO
		mUnLockDatas = new ArrayList<AppLockBean>();// TODO
		mLockDatas = new ArrayList<AppLockBean>();// TODO

		isLeftSelected = mAbSelect.isLeftSelected();

		updateDisplayContent();

		mLvLock.setAdapter(new ContentAdapter());

	}

	private void updateDisplayContent() {
		mLvLock.setVisibility(isLeftSelected ? View.GONE : View.VISIBLE);
		mLvUnLock.setVisibility(isLeftSelected ? View.VISIBLE : View.GONE);
	}

	private void initEvent() {
		mAbSelect.setOnChangeStateListener(new OnChangeStateListener() {

			@Override
			public void OnChangeState(boolean isLeftSelected) {
				if (isLeftSelected) {
					Toast.makeText(AppLock.this, "未加锁被选中了", Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(AppLock.this, "已上锁被选中了", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
	}

	private class ContentAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	class ViewHolder {
		ImageView ivAppIcon;
		ImageView ivLock;
		TextView tvAppName;
	}
}
