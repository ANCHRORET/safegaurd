package com.tp.safeguard.activities;

import android.app.Activity;
import android.drm.DrmStore.Action;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tp.safeguard.R;
import com.tp.safeguard.db.NumAddressDao;
import com.tp.safeguard.utils.ViewAction;

public class QueryAddressActivity extends Activity {
	private EditText mEtNumber;
	private TextView mTvDesc;
	private static final String TAG = "QueryAddressActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_queryaddress);

		initView();
		initEvent();

	}

	private void initView() {
		mEtNumber = (EditText) findViewById(R.id.qa_et_number);
		mTvDesc = (TextView) findViewById(R.id.qa_tv_desc);
	}

	private void initEvent() {
		mEtNumber.addTextChangedListener(new MyTextWatcher());
	}

	// 点击了查询按钮调用的方法
	public void queryAddress(View v) {
		String number=mEtNumber.getText().toString();
		if (TextUtils.isEmpty(number)) {
			ViewAction.shakeView(mEtNumber);
			return;
		}
		queryNumAddress(mEtNumber.getText().toString());
	}

	/**
	 * 自定义文本编辑框的文字改变监听器类
	 */
	private class MyTextWatcher implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// Log.d(TAG, "beforeTextChanged");
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// Log.d(TAG, "onTextChanged");
		}

		@Override
		public void afterTextChanged(Editable s) {
			// Log.d(TAG, "afterTextChanged");
			queryNumAddress(s.toString());

		}
	}
	private void queryNumAddress(String number) {
		String numDesc = NumAddressDao.queryNumAddress(
				QueryAddressActivity.this, number);
		mTvDesc.setText(numDesc);
	}
}
