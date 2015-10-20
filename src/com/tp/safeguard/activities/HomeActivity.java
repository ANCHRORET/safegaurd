package com.tp.safeguard.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tp.safeguard.R;
import com.tp.safeguard.utils.Constants;
import com.tp.safeguard.utils.PreferenceUtils;
import com.tp.safeguard.utils.ViewAction;

public class HomeActivity extends Activity implements OnItemClickListener {

	private String[] text_above = { "手机防盗", "骚扰拦截", "软件管家", "进程管理", "流量统计",
			"手机杀毒", "缓存清理", "常用工具" };// 子项目中上面显示的文字
	private String[] text_below = { "远程定位手机", "全面连接骚扰", "管理您的软件", "管理正在运行",
			"流量一目了然", "病毒无法藏身", "系统快如火箭", "常用工具大全" };// 子项目中下面显示的文字
	private int[] imageID = { R.drawable.sjfd, R.drawable.srlj,
			R.drawable.rjgj, R.drawable.jcgl, R.drawable.lltj, R.drawable.sjsd,
			R.drawable.hcql, R.drawable.cygj };// 子项目中显示的图片
	private GridView gv_item;

	List<Map<String, Object>> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		ImageView iv_heima = (ImageView) findViewById(R.id.home_iv_heima);
		ImageView iv_setting = (ImageView) findViewById(R.id.home_iv_setting);
		gv_item = (GridView) findViewById(R.id.home_gv_item);
		gv_item_init();
		gv_item.setOnItemClickListener(this);

		iv_setting.setOnClickListener(new OnClickListener() {// 如果不改变可能是因为资源文件没有刷新

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(HomeActivity.this,
								SettingActivity.class);
						startActivity(intent);
					}
				});
		iv_heima_set(iv_heima);
	}

	private void gv_item_init() {

		list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < imageID.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("text_below", text_below[i]);
			map.put("text_above", text_above[i]);
			map.put("imageID", imageID[i]);
			list.add(map);
		}

		// gv_item.setAdapter(new SimpleAdapter(this, list, R.layout.item,
		// new String[] { "text_above", "text_below", "imageID" },
		// new int[] { R.id.item_tv1, R.id.item_tv2, R.id.item_iv }));

		gv_item.setAdapter(new MyItemAdapter());

	}

	class MyItemAdapter extends BaseAdapter {

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
			if (convertView == null) {
				convertView = View.inflate(HomeActivity.this, R.layout.item,
						null);
			}
			TextView item_tv1 = (TextView) convertView
					.findViewById(R.id.item_tv1);
			TextView item_tv2 = (TextView) convertView
					.findViewById(R.id.item_tv2);
			ImageView item_iv = (ImageView) convertView
					.findViewById(R.id.item_iv);

			item_tv1.setText(list.get(position).get("text_above").toString());
			item_tv2.setText(list.get(position).get("text_below").toString());
			item_iv.setImageResource((Integer) list.get(position)
					.get("imageID"));

			return convertView;
		}
	}

	private void iv_heima_set(ImageView imageView) {
		// imageView.setRotationX(rotationX)
		ObjectAnimator animator = ObjectAnimator.ofFloat(imageView,
				"rotationY", new float[] { 0, 100, 160, 200, 260, 360 });
		animator.setRepeatCount(ObjectAnimator.INFINITE);
		animator.setRepeatMode(ObjectAnimator.RESTART);
		animator.setDuration(1500);
		animator.start();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		switch (position) {
		case 0:
			clicksjfd();
			break;
		case 1:
			clicksrlj();
			break;
		case 2:
			clickrjgj();
			break;
		case 3:
			clickjcgl();
			break;
		case 7:
			clickcygj();
			break;
		default:
			break;
		}

	}

	/**
	 * 点击了进程管理
	 */
	private void clickjcgl() {
		startActivity(new Intent(this, JcglActivity.class));
	}

	/**
	 * 点击软件管家后调用的方法
	 */
	private void clickrjgj() {
		startActivity(new Intent(this, RjgjActivity.class));

	}

	/**
	 * 点击手机防盗后调用的方法
	 */
	private void clicksjfd() {

		String sjfd_pwd = PreferenceUtils.getString(this, Constants.SJFD_PWD,
				"");
		if (TextUtils.isEmpty(sjfd_pwd)) {
			// 弹出密码设置对话框
			showSettingDialog();
		} else {
			// 设置密码校验对话框
			showEnterDialog();
		}
	}

	/**
	 * 点击骚扰拦截后调用的方法
	 */
	private void clicksrlj() {
		startActivity(new Intent(this, SrljActivity.class));
	}

	/**
	 * 点击常用工具大全后调用的方法
	 */
	private void clickcygj() {
		startActivity(new Intent(this, CygjActivity.class));
	}

	private void showEnterDialog() {
		// 显示校验密码对话框
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.dialog_enter_pwd, null);
		builder.setView(view);
		final AlertDialog dialog = builder.show();

		// 输入方式(软键盘)管理器
		final InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// 显示或隐藏软键盘
		im.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

		final EditText et_pwd = (EditText) view
				.findViewById(R.id.dialog_et_pwd);
		Button btn_yes = (Button) view.findViewById(R.id.dialog_btn_yes);
		Button btn_no = (Button) view.findViewById(R.id.dialog_btn_no);

		btn_yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 确定设置密码

				String pwd = et_pwd.getText().toString().trim();
				String localPwd = PreferenceUtils.getString(HomeActivity.this,
						Constants.SJFD_PWD, "");

				if (TextUtils.isEmpty(pwd) || !localPwd.equals(pwd)) {
					Toast.makeText(HomeActivity.this, "密码错误,请重新输入",
							Toast.LENGTH_SHORT).show();
					ViewAction.shakeView(et_pwd);
					et_pwd.setText("");
					et_pwd.requestFocus();
					return;
				}

				if (PreferenceUtils.getBoolean(HomeActivity.this,
						Constants.SJFD_SETUP, false)) {
					// 进入到设置完成的页面
					startActivity(new Intent(HomeActivity.this,
							SjfdSetupInfoActivity.class));

				} else {
					// 进入到设置向导界面 :
					startActivity(new Intent(HomeActivity.this,
							SjfdSetup1Activity.class));

				}
				// 不显示Dialog了,并且将软键盘取消
				dialog.dismiss();
				// 取消软键盘
				im.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
			}
		});
		btn_no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 取消对话框
				dialog.dismiss();
				// 取消软键盘
				im.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
			}
		});
	}

	private void showSettingDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.dialog_set_pwd, null);
		builder.setView(view);
		final AlertDialog dialog = builder.show();

		// 输入方式(软键盘)管理器
		final InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// 显示或隐藏软键盘
		im.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

		final EditText et_pwd = (EditText) view
				.findViewById(R.id.dialog_et_pwd);
		final EditText et_confirmpwd = (EditText) view
				.findViewById(R.id.dialog_et_confirmpwd);
		Button btn_yes = (Button) view.findViewById(R.id.dialog_btn_yes);
		Button btn_no = (Button) view.findViewById(R.id.dialog_btn_no);

		btn_yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 确定设置密码
				// 输入密码不能为空
				if (TextUtils.isEmpty(et_pwd.getText())) {
					ViewAction.shakeView(et_pwd);
					Toast.makeText(HomeActivity.this, "密码不能为空",
							Toast.LENGTH_SHORT).show();
					et_pwd.requestFocus();
					return;
				}
				// 两次输入密码需要一致
				if (!et_pwd.getText().toString()
						.equals(et_confirmpwd.getText().toString())) {

					Toast.makeText(HomeActivity.this, "两次密码输入不一致",
							Toast.LENGTH_SHORT).show();
					et_pwd.setText("");
					et_confirmpwd.setText("");
					ViewAction.shakeView(et_confirmpwd);
					return;
				}

				PreferenceUtils.putString(HomeActivity.this,
						Constants.SJFD_PWD, et_pwd.getText().toString().trim());

				// 进入到设置向导界面
				Intent intent = new Intent(HomeActivity.this,
						SjfdSetup1Activity.class);
				startActivity(intent);
				// 不显示Dialog了,并且将软键盘取消
				dialog.dismiss();
				// 取消软键盘
				im.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

			}
		});
		btn_no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 取消对话框
				dialog.dismiss();
				// 取消软键盘
				im.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

			}
		});

	}

}
