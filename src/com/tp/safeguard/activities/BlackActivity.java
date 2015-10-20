package com.tp.safeguard.activities;

import com.tp.safeguard.R;
import com.tp.safeguard.bean.BlackBean;
import com.tp.safeguard.db.BlackDao;
import com.tp.safeguard.utils.ViewAction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class BlackActivity extends Activity {

	public static final String BLACK_ADD = "black_add";
	public static final String BLACK_UPDATE = "black_update";
	private static final String TAG = "BlackActivity";
	private RadioGroup mRgType;
	private EditText mEtNumber;
	private BlackDao mBlackDao;
	private Button mBtnUpdate;
	private TextView mTvTitleBar;
	private int position;
	private String action;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_black_edit);
		initView();
		mBlackDao = new BlackDao(this);
	}

	private void initView() {
		mRgType = (RadioGroup) findViewById(R.id.black_rg_type);
		mEtNumber = (EditText) findViewById(R.id.black_et_number);
		mBtnUpdate = (Button) findViewById(R.id.black_btn_update);
		mTvTitleBar = (TextView) findViewById(R.id.black_tv_titleBar);
		Intent intent = getIntent();
		action = intent.getAction();
		if (action.equals(BLACK_UPDATE)) {
			String number = intent.getStringExtra("number");
			int type = intent.getIntExtra("type", -1);
			position = intent.getIntExtra("position", -1);
			mEtNumber.setText(number);
			mEtNumber.setEnabled(false);
			mTvTitleBar.setText("更新联系人");
			mBtnUpdate.setText("更新");
			int id = getIDByType(type);
			mRgType.check(id);
		}
	}

	public void blackOk(View v) {
		//校验type
		int checkedID = mRgType.getCheckedRadioButtonId();
		if (checkedID==-1) {
			Toast.makeText(this, "拦截类型不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		//校验number
		String number = mEtNumber.getText().toString().trim();
		if (TextUtils.isEmpty(number)) {
			ViewAction.shakeView(mEtNumber);
			mEtNumber.requestFocus();
			Toast.makeText(this, "电话号码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		
		int type = getTypeByID(checkedID);
		int findType = mBlackDao.findType(number);
		
		if (action.equals(BLACK_ADD)) {
			if (findType!=-1) {
				Toast.makeText(this, "此号码已经存在,无需添加", Toast.LENGTH_SHORT).show();
				ViewAction.shakeView(mEtNumber);
				mEtNumber.setSelection(number.length());
				return;	
			}
			
			Log.d(TAG, "添加:"+mBlackDao.insert(number, type));
			// 设置返回数据给调用者
			Intent data=new Intent();
			data.putExtra("type", type);
			data.putExtra("number", number);
			setResult(Activity.RESULT_OK, data);
		} else if (action.equals(BLACK_UPDATE)) {
			
			//如果数据被意外删除
			if (findType==-1) {
				Toast.makeText(this, "此号码已经不存在,请返回主页面,再次打开", Toast.LENGTH_SHORT).show();
				finish();
				return;
			}
			Log.d(TAG, "更新:"+mBlackDao.update(number, type));
			Intent data=new Intent();
			data.putExtra("type", type);
			data.putExtra("position", position);
			setResult(Activity.RESULT_OK, data);
		}
		finish();
	}

	public void blackCancel(View v) {
		finish();
	}
	
	/**
	 * 通过type的类型,获得RadioButton的Id
	 */
	private int getIDByType(int type) {
		int id = -1;
		switch (type) {
		case BlackBean.BLACK_TYPE_CALL:
			id = R.id.black_rb_call;
			break;
		case BlackBean.BLACK_TYPE_SMS:
			id = R.id.black_rb_sms;
			break;
		case BlackBean.BLACK_TYPE_ALL:
			id = R.id.black_rb_all;
			break;
		default:
			break;
		}
		return id;
	}
	
	/**
	 * 通过RadioButton的Id获得拦截类型
	 * @param checkedID
	 * @return
	 */
	private int getTypeByID(int checkedID) {
		int type = -1;
		switch (checkedID) {
		case R.id.black_rb_call:
			type = BlackBean.BLACK_TYPE_CALL;
			break;
		case R.id.black_rb_sms:
			type = BlackBean.BLACK_TYPE_SMS;
			break;
		case R.id.black_rb_all:
			type = BlackBean.BLACK_TYPE_ALL;
			break;
		default:
			break;
		}
		return type;
	}
}
