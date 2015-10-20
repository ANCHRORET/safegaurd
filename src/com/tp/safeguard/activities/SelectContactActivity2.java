package com.tp.safeguard.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tp.safeguard.R;
import com.tp.safeguard.bean.ContactBean;
import com.tp.safeguard.utils.ContactUtils;

public class SelectContactActivity2 extends Activity {

	private ListView lv_bean;
	private List<String> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);

		// 获得布局里的View组件
		lv_bean = (ListView) findViewById(R.id.contacts_lv_bean);
		// 初始化得到的组件
		// 获取数据
		list=new ArrayList<String>();
		list.add("aaa");
		list.add("bbb");
		list.add("ccc");
		lv_bean.setAdapter(new MyAdapter());
		// 处理view对应的事件
		lv_bean.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Toast.makeText(SelectContactActivity2.this, "click",
						Toast.LENGTH_LONG).show();
			}
		});
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 设置item的view

			if (convertView == null) {
				convertView = View.inflate(SelectContactActivity2.this,
						R.layout.lv_item_contact, null);

			}

			TextView tv_name = (TextView) convertView
					.findViewById(R.id.lv_iv_name);
			TextView tv_num = (TextView) convertView
					.findViewById(R.id.lv_iv_num);

			tv_name.setText("去死");
			tv_num.setText(list.get(position));
			return convertView;
		}
	}

}
