package com.tp.safeguard.activities;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

import com.tp.safeguard.R;
import com.tp.safeguard.bean.CommonNumChildBean;
import com.tp.safeguard.bean.CommonNumGroupBean;
import com.tp.safeguard.db.CommonNumDao;

public class QueryCommonNumActivity extends Activity implements
		OnChildClickListener, OnGroupClickListener {

	private ExpandableListView mElvCommonNum;
	private List<CommonNumGroupBean> mDatas;
	private int mCurrentExpendGroupPosition = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_querycommonnum);

		initView();
		initData();
		initEvent();
	}

	private void initView() {
		mElvCommonNum = (ExpandableListView) findViewById(R.id.qcn_elv_content);
	}

	private void initData() {

		// Random random = new Random();
		// mDatas = new ArrayList<CommonNumGroupBean>();
		// for (int i = 0; i < random.nextInt(20); i++) {
		// CommonNumGroupBean groupBean = new CommonNumGroupBean();
		// groupBean.title = "groupBean " + i;
		// groupBean.list = new ArrayList<CommonNumChildBean>();
		// mDatas.add(groupBean);
		// for (int j = 0; j < random.nextInt(30); j++) {
		// CommonNumChildBean childBean = new CommonNumChildBean();
		// childBean.name = "groupBean " + i + " name " + j;
		// childBean.number = "groupBean " + i + " number " + j;
		// groupBean.list.add(childBean);
		// }
		// }
		// 初始化mDatas
		mDatas = CommonNumDao.queryAll(this);
		mElvCommonNum.setAdapter(new CommonNumAdapter());
	}

	private void initEvent() {
		mElvCommonNum.setOnGroupClickListener(this);
		mElvCommonNum.setOnChildClickListener(this);
	}

	private class CommonNumAdapter extends BaseExpandableListAdapter {

		@Override
		public int getGroupCount() {
			if (mDatas != null) {
				return mDatas.size();
			}
			return 0;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			if (mDatas != null) {
				List<CommonNumChildBean> list = mDatas.get(groupPosition).list;
				if (list != null) {
					return list.size();
				}
			}
			return 0;
		}

		@Override
		public Object getGroup(int groupPosition) {
			if (mDatas != null) {
				return mDatas.get(groupPosition);
			}
			return null;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			if (mDatas != null) {
				List<CommonNumChildBean> list = mDatas.get(groupPosition).list;
				if (list != null) {
					return list.get(childPosition);
				}
			}
			return null;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return groupPosition + childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			GroupHolder groupHolder = null;
			if (convertView == null) {
				convertView = View.inflate(QueryCommonNumActivity.this,
						R.layout.elv_groupitem_numbertype, null);
				groupHolder = new GroupHolder();
				convertView.setTag(groupHolder);
				groupHolder.tvTitle = (TextView) convertView
						.findViewById(R.id.numberType_tv_type);
			} else {
				groupHolder = (GroupHolder) convertView.getTag();
			}
			CommonNumGroupBean groupBean = mDatas.get(groupPosition);
			groupHolder.tvTitle.setText(groupBean.title);

			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ChildHolder childHolder = null;
			if (convertView == null) {
				convertView = View.inflate(QueryCommonNumActivity.this,
						R.layout.elv_childitem_content, null);
				childHolder = new ChildHolder();
				convertView.setTag(childHolder);
				childHolder.tvChildName = (TextView) convertView
						.findViewById(R.id.child_tv_name);
				childHolder.tvChildNumber = (TextView) convertView
						.findViewById(R.id.child_tv_number);
			} else {
				childHolder = (ChildHolder) convertView.getTag();
			}
			CommonNumChildBean childBean = mDatas.get(groupPosition).list
					.get(childPosition);
			childHolder.tvChildName.setText(childBean.name);
			childHolder.tvChildNumber.setText(childBean.number);
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	private class GroupHolder {
		public TextView tvTitle;
	}

	private class ChildHolder {
		TextView tvChildName;
		TextView tvChildNumber;
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v,
			int groupPosition, long id) {
		if (mCurrentExpendGroupPosition == -1) {
			mCurrentExpendGroupPosition = groupPosition;
			mElvCommonNum.expandGroup(groupPosition);
			// 第一次打开的不用了
			// mElvCommonNum.setSelectedGroup(groupPosition);
		} else {
			if (mCurrentExpendGroupPosition == groupPosition) {
				mCurrentExpendGroupPosition = -1;
				mElvCommonNum.collapseGroup(groupPosition);
			} else {
				mElvCommonNum.collapseGroup(mCurrentExpendGroupPosition);
				mElvCommonNum.expandGroup(groupPosition);
				mCurrentExpendGroupPosition = groupPosition;
				mElvCommonNum.setSelectedGroup(groupPosition);
			}
		}
		return true;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		String number = mDatas.get(groupPosition).list.get(childPosition).number;
		Intent intent = new Intent(Intent.ACTION_DIAL);
		intent.setData(Uri.parse("tel:" + number));
		startActivity(intent);
		return true;
	}

}