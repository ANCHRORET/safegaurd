package com.tp.safeguard.activities;

import com.tp.safeguard.R;

import android.content.Intent;
import android.os.Bundle;

public class SjfdSetup1Activity extends BaseSetupActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup1_activity);
	}

	@Override
	public boolean performNext() {
		Intent intent = new Intent(this, SjfdSetup2Activity.class);
		startActivity(intent);
		return false;
	}

	@Override
	public boolean performPre() {
		return true;
	}
}
