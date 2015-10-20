package com.tp.safeguard.view;

import com.tp.safeguard.R;
import com.tp.safeguard.utils.Constants;
import com.tp.safeguard.utils.PreferenceUtils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

public class DisplayNumToast {

	private final int[] iconIDs = new int[] {
			R.drawable.as_shape_color_translucent,
			R.drawable.as_shape_color_orange, R.drawable.as_shape_color_blue,
			R.drawable.as_shape_color_gray };
	private WindowManager mWM;
	private Context mContext;
	private WindowManager.LayoutParams mParams;
	private View mView;

	public DisplayNumToast(Context context) {

		this.mContext = context;
		mParams = new WindowManager.LayoutParams();
		mWM = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		mParams.gravity = Gravity.CENTER;
		mParams = new WindowManager.LayoutParams();
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		// | WindowManager.LayoutParams.FLAG_TOUCHABLE
		mParams.format = PixelFormat.TRANSLUCENT;
		// mParams.windowAnimations =
		// com.android.internal.R.style.Animation_Toast;
		mParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		mParams.setTitle("Toast");
	}

	public void show(String address) {
		hide();
		mView = View.inflate(mContext, R.layout.dntoast_tv_address, null);

		int colorID = PreferenceUtils.getInt(mContext,
				Constants.SETTING_ADDRESSSTYLE, 0);
		mView.setBackgroundResource(iconIDs[colorID]);
		TextView textAddress = (TextView) mView.findViewById(R.id.tv_address);
		textAddress.setText(address);
		mView.setOnTouchListener(new OnMyViewTouchListener());
		if (mView.getParent() != null) {
			mWM.removeView(mView);
		}
		mWM.addView(mView, mParams);
	}

	public void hide() {
		if (mView != null) {
			if (mView.getParent() != null) {
				mWM.removeView(mView);
			}
			mView = null;
		}
	}

	private class OnMyViewTouchListener implements OnTouchListener {

		private float downX;
		private float downY;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// Log.d(TAG, "触到图标");
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downX = event.getRawX();
				downY = event.getRawY();
				break;
			case MotionEvent.ACTION_MOVE:
				float moveX = event.getRawX();
				float moveY = event.getRawY();

				float diffX = moveX - downX;
				float diffY = moveY - downY;

				mParams.x += (int) (diffX + 0.5);
				mParams.y += (int) (diffY + 0.5);

				mWM.updateViewLayout(mView, mParams);
				downX = moveX;
				downY = moveY;
				break;
			case MotionEvent.ACTION_UP:
				break;
			default:
				break;
			}
			return true;
		}
	}

}
