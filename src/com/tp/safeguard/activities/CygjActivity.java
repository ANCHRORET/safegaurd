package com.tp.safeguard.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.tp.safeguard.R;
import com.tp.safeguard.utils.Constants;
//import com.tp.safeguard.utils.PreferenceUtils;
import com.tp.safeguard.utils.SmsUtils;
import com.tp.safeguard.utils.SmsUtils.OnOperateProgressListener;
import com.tp.safeguard.view.SettingItemView;

public class CygjActivity extends Activity implements OnClickListener {

	// private static final String TAG = "CygjActivity";
	private SettingItemView mSivQueryAddress;
	private SettingItemView mSivQueryCommonNum;
	private SettingItemView mSivSmsBackup;
	private SettingItemView mSivSmsRestore;
	private SettingItemView mSivAppLock;
	private boolean mAppLock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cygj);

		initView();
//		initData();
		initEvent();
	}

	private void initView() {
		mSivQueryAddress = (SettingItemView) findViewById(R.id.cygj_siv_queryAddress);
		mSivQueryCommonNum = (SettingItemView) findViewById(R.id.cygj_siv_queryCommonNum);
		mSivSmsBackup = (SettingItemView) findViewById(R.id.cygj_siv_smsBackup);
		mSivSmsRestore = (SettingItemView) findViewById(R.id.cygj_siv_smsRestore);
		mSivAppLock = (SettingItemView) findViewById(R.id.cygj_siv_appLock);
	}

//	private void initData() {
//		mAppLock = PreferenceUtils.getBoolean(this, Constants.SYGJ_APPLOCK,
//				true);
//		mSivAppLock.setToggleOpened(mAppLock);
//	}

	private void initEvent() {
		mSivQueryAddress.setOnClickListener(this);
		mSivQueryCommonNum.setOnClickListener(this);
		mSivSmsBackup.setOnClickListener(this);
		mSivSmsRestore.setOnClickListener(this);
		mSivAppLock.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == mSivQueryAddress) {
			queryAddress();
		} else if (v == mSivQueryCommonNum) {
			queryCommonNum();
		} else if (v == mSivSmsBackup) {
			backupSms();
		} else if (v == mSivSmsRestore) {
			restoreSms();
		} else if (v == mSivAppLock) {
			clickAppLock();
		} else if (true) {
			// TODO:
		}
	}

	/**
	 * 进入程序锁页面
	 */
	private void clickAppLock() {
//		mAppLock = !mAppLock;
//		PreferenceUtils.putBoolean(this, Constants.SYGJ_APPLOCK, mAppLock);
//		mSivAppLock.setToggleOpened(mAppLock);
		// TODO 关闭程序锁的服务.
		startActivity(new Intent(this, ApplockEntranceActivity.class));
	}

	/**
	 * 短信还原
	 */
	private void restoreSms() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示").setIcon(R.drawable.ic_info)
				.setMessage("请确认是否还原,确认则会把所有你所备份的短信加到您的短信中")
				.setNegativeButton("取消", null)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						final ProgressDialog progressDialog = new ProgressDialog(
								CygjActivity.this);
						progressDialog
								.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
						progressDialog.setCanceledOnTouchOutside(false);
						progressDialog.setCancelable(false);
						progressDialog.show();
						SmsUtils.smsRestore(CygjActivity.this,
								new OnOperateProgressListener() {

									@Override
									public void onProcessUpdate(int progress) {
										progressDialog.setProgress(progress);

									}

									@Override
									public void onOperateFinish() {
										progressDialog.dismiss();

									}

									@Override
									public void onGetMax(int max) {
										progressDialog.setMax(max);

									}

									@Override
									public void onErrorOccur(Exception e) {
										progressDialog.dismiss();
										Toast.makeText(CygjActivity.this,
												"还原失败,请您确定是否备份了",
												Toast.LENGTH_SHORT).show();

									}
								});
					}
				}).create().show();
	}

	/**
	 * 短信备份,此方法向数据库重新添加了备份的条目,可能还需要优化,TODO
	 */
	private void backupSms() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示").setIcon(R.drawable.ic_info)
				.setMessage("请确认是否备份,确认则会清除原来已经存在的备份")
				.setNegativeButton("取消", null)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final ProgressDialog progressDialog = new ProgressDialog(
								CygjActivity.this);
						progressDialog
								.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
						progressDialog.setCanceledOnTouchOutside(false);
						progressDialog.setCancelable(false);
						progressDialog.show();
						SmsUtils.backupSms(CygjActivity.this,
								new OnOperateProgressListener() {

									@Override
									public void onProcessUpdate(int progress) {
										progressDialog.setProgress(progress);
									}

									@Override
									public void onGetMax(int max) {
										progressDialog.setMax(max);
									}

									@Override
									public void onErrorOccur(Exception e) {
										progressDialog.dismiss();
										Toast.makeText(CygjActivity.this,
												"备份失败...", Toast.LENGTH_SHORT)
												.show();
									}

									@Override
									public void onOperateFinish() {
										progressDialog.dismiss();
									}
								});
					}
				}).create().show();
	}

	/**
	 * 查询常用号码
	 */
	private void queryCommonNum() {
		Intent intent = new Intent(this, QueryCommonNumActivity.class);
		startActivity(intent);
	}

	/**
	 * 查询号码归属地
	 */
	private void queryAddress() {
		Intent intent = new Intent(this, QueryAddressActivity.class);
		startActivity(intent);
	}
}
