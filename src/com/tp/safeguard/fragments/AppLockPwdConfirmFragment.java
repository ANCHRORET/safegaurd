package com.tp.safeguard.fragments;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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

public class AppLockPwdConfirmFragment extends Fragment {

	private LockPatternView mLpvLock;
	private Button mBtnRight;
	private Button mBtnLeft;
	private Handler mHandler = new Handler();
	private ClearPatternTask mClearPatternTask;

	private String firstPwd;
	private String mConfirmPwd;

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
		// view.findViewById(R.id.fbs_btn_left).setVisibility(View.GONE);
		view.findViewById(R.id.fbs_tv_tip).setVisibility(View.GONE);
		mBtnLeft = (Button) view.findViewById(R.id.fbs_btn_left);
		mBtnRight = (Button) view.findViewById(R.id.fbs_btn_right);
		mLpvLock = (LockPatternView) view.findViewById(R.id.fbs_lpv_lock);
	}

	private void initData() {
		mBtnRight.setText("设置完成");
		Bundle bundle = getArguments();
		firstPwd = bundle.getString(AppLockSetFragment.FIRSTPASSWORD);
	}

	private void initEvent() {
		mBtnRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (!firstPwd.equals(mConfirmPwd)) {
					Toast.makeText(getActivity(), "两次密码输入不一致,请重新输入",
							Toast.LENGTH_SHORT).show();
					return;
				}

				PreferenceUtils.putString(getActivity(),
						Constants.CYGJ_APPLOCKPWD, mConfirmPwd);
				startActivity(new Intent(getActivity(), AppLock.class));

				getActivity().finish();
			}
		});
		mBtnLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppLockSetFragment pFragment = (AppLockSetFragment) getParentFragment();
				pFragment.popFragment();
			}
		});

		mLpvLock.setOnPatternListener(new OnPatternListener() {

			@Override
			public void onPatternStart() {
				if (mClearPatternTask != null) {
					mHandler.removeCallbacks(mClearPatternTask);
				}
			}

			@Override
			public void onPatternDetected(List<Cell> pattern) {

				StringBuilder sb = new StringBuilder();
				for (Cell cell : pattern) {
					sb.append(cell.toPassword());
				}
				mConfirmPwd = sb.toString();
				if (!firstPwd.equals(mConfirmPwd)) {
					mLpvLock.setDisplayMode(DisplayMode.Wrong);
					mClearPatternTask = new ClearPatternTask();
					mHandler.postDelayed(mClearPatternTask, 1000);
				} else {
					mLpvLock.setDisplayMode(DisplayMode.Correct);
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
