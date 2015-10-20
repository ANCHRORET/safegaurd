package com.tp.safeguard.fragments;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.tp.safeguard.R;
import com.tp.safeguard.utils.PreferenceUtils;
import com.tp.safeguard.view.LockPatternView;
import com.tp.safeguard.view.LockPatternView.Cell;
import com.tp.safeguard.view.LockPatternView.DisplayMode;
import com.tp.safeguard.view.LockPatternView.OnPatternListener;

public class AppLockPwdBootFragment extends Fragment {

	private LockPatternView mLpvLock;
	private Button mBtnRight;
	private Button mBtnLeft;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_boot_setup, container,
				false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView(view);
		initData();
		initEvent();
	}

	private void initView(View view) {
		mBtnLeft = (Button) view.findViewById(R.id.fbs_btn_left);
		mBtnRight = (Button) view.findViewById(R.id.fbs_btn_right);
		mLpvLock = (LockPatternView) view.findViewById(R.id.fbs_lpv_lock);
	}

	private void initData() {
		mBtnLeft.setVisibility(View.GONE);
		List<Cell> pattern = LockPatternView.password2Cells("0143678");
		mLpvLock.setClickable(false);
		mLpvLock.setFocusable(false);
		mLpvLock.setPattern(DisplayMode.Animate, pattern);
		mLpvLock.disableInput();

	}

	private void initEvent() {
		mBtnRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppLockSetFragment pFragment = (AppLockSetFragment) getParentFragment();
				pFragment.loadPwdSetFragment();
			}
		});
	}
}
