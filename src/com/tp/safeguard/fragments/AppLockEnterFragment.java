package com.tp.safeguard.fragments;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.tp.safeguard.R;
import com.tp.safeguard.activities.AppLock;
import com.tp.safeguard.utils.Constants;
import com.tp.safeguard.utils.PreferenceUtils;
import com.tp.safeguard.view.LockPatternView;
import com.tp.safeguard.view.LockPatternView.Cell;
import com.tp.safeguard.view.LockPatternView.DisplayMode;
import com.tp.safeguard.view.LockPatternView.OnPatternListener;

public class AppLockEnterFragment extends Fragment {

	private LockPatternView mLpvLock;
	private Button mBtnRight;
	private Button mBtnLeft;

	private Handler mHandler;
	private Runnable mRunnable;

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
		mHandler = new Handler();
	}

	private void initView(View view) {
		// view.findViewById(R.id.fbs_btn_left).setVisibility(View.GONE);
		view.findViewById(R.id.fbs_tv_tip).setVisibility(View.GONE);
		mBtnLeft = (Button) view.findViewById(R.id.fbs_btn_left);
		mBtnRight = (Button) view.findViewById(R.id.fbs_btn_right);
		mLpvLock = (LockPatternView) view.findViewById(R.id.fbs_lpv_lock);

	}

	private void initData() {
		mBtnLeft.setVisibility(View.GONE);
		mBtnRight.setVisibility(View.GONE);
	}

	private void initEvent() {
		mLpvLock.setOnPatternListener(new OnPatternListener() {

			@Override
			public void onPatternStart() {
				if (mRunnable != null) {
					mHandler.removeCallbacks(mRunnable);
				}
			}

			@Override
			public void onPatternDetected(List<Cell> pattern) {
				String pwd = PreferenceUtils.getString(getActivity(),
						Constants.CYGJ_APPLOCKPWD, "");
				if (TextUtils.isEmpty(pwd)) {
					Toast.makeText(getActivity(), "密码意外丢失,请返回重新进入",
							Toast.LENGTH_SHORT).show();
					return;
				}
				StringBuilder pwdsb = new StringBuilder();
				for (Cell cell : pattern) {
					pwdsb.append(cell.toPassword());
				}
				// 密码正确,进入程序锁界面
				if (pwd.equals(pwdsb.toString())) {
					mLpvLock.setDisplayMode(DisplayMode.Correct);
					getActivity().startActivity(
							new Intent(getActivity(), AppLock.class));
					getActivity().finish();
				} else {
					mLpvLock.setDisplayMode(DisplayMode.Wrong);
					mRunnable = new ClearPatternTask();
					mHandler.postDelayed(mRunnable, 500);
				}
			}

			@Override
			public void onPatternCleared() {
			}

			@Override
			public void onPatternCellAdded(List<Cell> pattern) {
			}
		});
	}

	private class ClearPatternTask implements Runnable {

		@Override
		public void run() {
			mLpvLock.clearPattern();
		}
	}
}
