package com.tp.safeguard.activities;

import com.tp.safeguard.R;
import com.tp.safeguard.utils.Constants;
import com.tp.safeguard.utils.PreferenceUtils;
import com.tp.safeguard.utils.ViewAction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SjfdSetup3Activity extends BaseSetupActivity {

	private static final int SETUP3_REQUESTCODE = 101;
	private EditText et_safeNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup3_activity);

		initView();

	}

	private void initView() {
		et_safeNum = (EditText) findViewById(R.id.setup3_et_safeNum);
		String safeNum = PreferenceUtils.getString(this,
				Constants.SJFD_SAFENUM, "");
		et_safeNum.setText(safeNum);
		et_safeNum.setSelection(safeNum.length());
	}

	public void selectSafeNum(View v) {
		// 选择安全号码
		Intent intent = new Intent(this, SelectContactActivity.class);
		startActivityForResult(intent, SETUP3_REQUESTCODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == SETUP3_REQUESTCODE) {
			if (data!=null) {
				String text = data.getStringExtra("ContactNum");
				et_safeNum.setText(text);
				et_safeNum.setSelection(text.length());
			}
		}

	}

	@Override
	public boolean performNext() {

		String localSaveNum = et_safeNum.getText().toString();
		if (TextUtils.isEmpty(localSaveNum)) {
			ViewAction.shakeView(et_safeNum);
			Toast.makeText(this, "安全号码不能为空", Toast.LENGTH_SHORT).show();
			return true;
		}

		PreferenceUtils.putString(this, Constants.SJFD_SAFENUM, localSaveNum);
		Intent intent = new Intent(this, SjfdSetup4Activity.class);
		startActivity(intent);
		return false;
	}

	@Override
	public boolean performPre() {
		Intent intent = new Intent(this, SjfdSetup2Activity.class);
		startActivity(intent);
		return false;
	}
}
