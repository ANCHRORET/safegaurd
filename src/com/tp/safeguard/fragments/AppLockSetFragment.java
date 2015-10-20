package com.tp.safeguard.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tp.safeguard.R;
import com.tp.safeguard.activities.ApplockEntranceActivity;

public class AppLockSetFragment extends Fragment {

	private static final String PWDBOOT = "pwdboot";
	private static final String PWDSET = "pwdset";
	private static final String PWDCONFIRM = "pwdconfirm";
	public static final String FIRSTPASSWORD = "first password";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_pwd_setup, container,
				false);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		final FragmentManager cfm = getChildFragmentManager();
		cfm.addOnBackStackChangedListener(new OnBackStackChangedListener() {

			@Override
			public void onBackStackChanged() {
				int count = cfm.getBackStackEntryCount();
				if (count == 0) {
					return;
				}

				BackStackEntry entry = cfm.getBackStackEntryAt(count - 1);
				ApplockEntranceActivity activity = (ApplockEntranceActivity) getActivity();
				String name = entry.getName();
				if (PWDBOOT.equals(name)) {
					activity.setTitleText("密码设置向导");
				} else if (PWDSET.equals(name)) {
					activity.setTitleText("密码设置");

				} else if (PWDCONFIRM.equals(name)) {
					activity.setTitleText("密码确认");
				}
			}
		});

		loadPwdBootFragment();
	}

	/**
	 * 设置为,密码设置引导页面的Fragment
	 */
	private void loadPwdBootFragment() {
		FragmentManager childFM = getChildFragmentManager();
		FragmentTransaction ts = childFM.beginTransaction();
		Fragment fragment = new AppLockPwdBootFragment();
		ts.addToBackStack(PWDBOOT);
		ts.setCustomAnimations(0, R.anim.fragment_exit_anim,
				R.anim.fragment_pop_enter_anim, 0);
		ts.replace(R.id.fps_fl_container, fragment);
		ts.commit();
	}

	// private void setFragment(Fragment fragment) {
	// FragmentManager childFM = getChildFragmentManager();
	// FragmentTransaction ts = childFM.beginTransaction();
	// ts.replace(R.id.fps_fl_container, fragment);
	// ts.commit();
	// }

	/**
	 * 设置为,密码设置页面的Fragment
	 */
	public void loadPwdSetFragment() {
		FragmentManager childFM = getChildFragmentManager();
		FragmentTransaction ts = childFM.beginTransaction();
		Fragment fragment = new AppLockPwdSetFragment();
		ts.addToBackStack(PWDSET);
		ts.setCustomAnimations(R.anim.fragment_enter_anim,
				R.anim.fragment_exit_anim, R.anim.fragment_pop_enter_anim,
				R.anim.fragment_pop_exit_anim);
		ts.replace(R.id.fps_fl_container, fragment);
		ts.commit();
	}

	/**
	 * 设置为,密码确认页面的Fragment
	 */
	public void loadPwdConfirmFragment(String pwd) {
		FragmentManager childFM = getChildFragmentManager();
		FragmentTransaction ts = childFM.beginTransaction();
		ts.addToBackStack(PWDCONFIRM);
		ts.setCustomAnimations(R.anim.fragment_enter_anim,
				R.anim.fragment_exit_anim, R.anim.fragment_pop_enter_anim,
				R.anim.fragment_pop_exit_anim);
		Fragment fragment = new AppLockPwdConfirmFragment();
		Bundle bundle = new Bundle();
		bundle.putString(FIRSTPASSWORD, pwd);
		fragment.setArguments(bundle);
		ts.replace(R.id.fps_fl_container, fragment);
		ts.commit();
	}

	public void popFragment() {
		FragmentManager childFM = getChildFragmentManager();
		childFM.popBackStack();

	}

}
