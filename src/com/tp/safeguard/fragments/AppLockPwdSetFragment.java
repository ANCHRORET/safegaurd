package com.tp.safeguard.fragments;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.tp.safeguard.R;
import com.tp.safeguard.view.LockPatternView;
import com.tp.safeguard.view.LockPatternView.Cell;
import com.tp.safeguard.view.LockPatternView.DisplayMode;
import com.tp.safeguard.view.LockPatternView.OnPatternListener;

public class AppLockPwdSetFragment extends Fragment {

	private LockPatternView mLpvLock;
	private Button mBtnRight;
	private Button mBtnLeft;
	private String mPwd;

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

		initEvent();
	}

	private void initView(View view) {
		// view.findViewById(R.id.fbs_btn_left).setVisibility(View.GONE);
		view.findViewById(R.id.fbs_tv_tip).setVisibility(View.GONE);
		mBtnLeft = (Button) view.findViewById(R.id.fbs_btn_left);
		mBtnRight = (Button) view.findViewById(R.id.fbs_btn_right);
		mLpvLock = (LockPatternView) view.findViewById(R.id.fbs_lpv_lock);

		if (!TextUtils.isEmpty(mPwd)) {
			mLpvLock.setPattern(DisplayMode.Correct,
					LockPatternView.password2Cells(mPwd));
		}
	}

	private void initEvent() {
		mBtnRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(mPwd)) {
					Toast.makeText(getActivity(), "请绘制手势密码", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				AppLockSetFragment pFragment = (AppLockSetFragment) getParentFragment();

				pFragment.loadPwdConfirmFragment(mPwd);
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
			}

			@Override
			public void onPatternDetected(List<Cell> pattern) {

				StringBuilder sb = new StringBuilder();
				for (Cell cell : pattern) {
					sb.append(cell.toPassword());
				}
				mPwd = sb.toString();
			}

			@Override
			public void onPatternCleared() {
			}

			@Override
			public void onPatternCellAdded(List<Cell> pattern) {
			}
		});
	}
}
